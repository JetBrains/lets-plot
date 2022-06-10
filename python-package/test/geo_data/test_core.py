#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from unittest import mock

import pytest
from pandas import DataFrame

from lets_plot.geo_data import geocode
from lets_plot.geo_data.geocoder import _to_scope
from lets_plot.geo_data.geocodes import _ensure_is_list, Geocodes, DF_COLUMN_ID, DF_COLUMN_FOUND_NAME, DF_COLUMN_CENTROID, DF_COLUMN_POSITION, DF_COLUMN_LIMIT
from lets_plot.geo_data.gis.geocoding_service import GeocodingService
from lets_plot.geo_data.gis.request import MapRegion, RegionQuery, GeocodingRequest, PayloadKind, ExplicitRequest, \
    AmbiguityResolver
from lets_plot.geo_data.gis.response import LevelKind, FeatureBuilder, GeoPoint, Answer
from lets_plot.geo_data.livemap_helper import _prepare_location, RegionKind, _prepare_parent, \
    LOCATION_LIST_ERROR_MESSAGE, LOCATION_DATAFRAME_ERROR_MESSAGE
from geo_data_test_util import make_geocode_region, make_success_response, features_to_answers, features_to_queries, \
    COLUMN_NAME_CITY

DATAFRAME_COLUMN_NAME = 'name'
DATAFRAME_NAME_LIST = ['usa', 'russia']
DATA_FRAME = DataFrame({DATAFRAME_COLUMN_NAME: DATAFRAME_NAME_LIST, 'rating': [1, 1]})
POINT_DATA_FRAME = DataFrame({DATAFRAME_COLUMN_NAME: DATAFRAME_NAME_LIST, 'lon': [1, 2], 'lat': [3, 4]})
RECT_DATA_FRAME = DataFrame({DATAFRAME_COLUMN_NAME: DATAFRAME_NAME_LIST, 'lonmin': [1, 2], 'latmin': [3, 4], 'lonmax': [5, 6], 'latmax': [7, 8]})

LEVEL_KIND: LevelKind = LevelKind.county
LEVEL: str = LEVEL_KIND.value
FILTER = 'Texas'
FILTER_LIST = ['LA', 'NY']
REGION_NAME = 'USA'
REGION_ID = '8898'
REGION_LIST = [REGION_ID]
RESOLUTION = 12
REQUEST = 'united states'
REGION_HIGHLIGHTS = ['united states', 'united states of america']

PARENT_WITH_NAME = MapRegion.with_name(REGION_NAME)

REGION_QUERY_LA = RegionQuery('LA', None, AmbiguityResolver())
REGION_QUERY_NY = RegionQuery('NY', None, AmbiguityResolver())

NAMESAKES_EXAMPLE_LIMIT = 10


def feature_id(answer: Answer) -> str:
    assert len(answer.features) == 1
    return answer.features[0].id


def make_expected_map_region(region_kind: RegionKind, values):
    return {
        'type': region_kind.value,
        'data': values
    }


@mock.patch.object(GeocodingService, 'do_request')
def test_regions(mock_geocoding):
    try:
        geocode(level=LEVEL, names=FILTER_LIST).scope(REGION_NAME).get_geocodes()
    except Exception:
        pass  # response doesn't contain proper feature with ids - ignore

    mock_geocoding.assert_called_with(
        GeocodingRequest(requested_payload=[PayloadKind.limits, PayloadKind.poisitions, PayloadKind.centroids],
                         resolution=None,
                         region_queries=[REGION_QUERY_LA, REGION_QUERY_NY],
                         level=LEVEL_KIND,
                         scope=[MapRegion.with_name(REGION_NAME)],
                         namesake_example_limit=NAMESAKES_EXAMPLE_LIMIT,
                         allow_ambiguous=False
                         )
    )


@mock.patch.object(GeocodingService, 'do_request')
def test_regions_with_highlights(mock_geocoding):
    try:
        geocode(level=LEVEL, names=FILTER_LIST).scope(REGION_NAME).highlights(True).get_geocodes()
    except Exception:
        pass  # response doesn't contain proper feature with ids - ignore

    mock_geocoding.assert_called_with(
        GeocodingRequest(requested_payload=[PayloadKind.limits, PayloadKind.poisitions, PayloadKind.centroids, PayloadKind.highlights],
                         resolution=None,
                         region_queries=[REGION_QUERY_LA, REGION_QUERY_NY],
                         scope=MapRegion.with_name(REGION_NAME),
                         level=LEVEL_KIND,
                         namesake_example_limit=NAMESAKES_EXAMPLE_LIMIT,
                         allow_ambiguous=False
                         )
    )

FOO_FEATURE = FeatureBuilder().set_name('fooname').set_id('fooid').build_geocoded()
BAR_FEATURE = FeatureBuilder().set_name('barname').set_id('barid').build_geocoded()

FOO = Answer([FeatureBuilder().set_name('fooname').set_id('fooid').build_geocoded()])
BAR = Answer([FeatureBuilder().set_name('barname').set_id('barid').build_geocoded()])

@pytest.mark.parametrize('location,expected', [
    # none
    (None,
     None
     ),

    # single string
    ('name',
     MapRegion.with_name('name')
     ),

    # single region
    (Geocodes(
        level_kind=LEVEL_KIND,
        queries=features_to_queries([FOO_FEATURE, BAR_FEATURE]),
        answers=features_to_answers([FOO_FEATURE, BAR_FEATURE])
    ),
     MapRegion.scope([feature_id(FOO), feature_id(BAR)])
    ),

    # list of strings
    ([
         'foo', 'bar'
     ],
     [
         MapRegion.with_name('foo'),
         MapRegion.with_name('bar')
     ]
    ),

    # list of regions
    ([
         Geocodes(
             level_kind=LEVEL_KIND,
             queries=features_to_queries([FOO_FEATURE]),
             answers=features_to_answers([FOO_FEATURE])
         ),
         Geocodes(
             level_kind=LEVEL_KIND,
             queries=features_to_queries([BAR_FEATURE]),
             answers=features_to_answers([BAR_FEATURE]),
         )
     ],

     [
         MapRegion.scope([feature_id(FOO)]),
         MapRegion.scope([feature_id(BAR)])
     ]
    ),

    # mix of strings and regions
    ([
         FOO_FEATURE.name,
         Geocodes(
             level_kind=LEVEL_KIND,
             queries=features_to_queries([BAR_FEATURE]),
             answers=features_to_answers([BAR_FEATURE])
         )
     ],
     [
         MapRegion.with_name(FOO_FEATURE.name),
         MapRegion.scope([feature_id(BAR)])
     ]
    )
])
def test_to_parent_with_name(location, expected):
    actual = _to_scope(location)
    assert expected == actual


def test_to_parent_with_id():
    assert MapRegion.scope(REGION_LIST) == _to_scope(make_geocode_region(REQUEST, REGION_NAME, REGION_ID, REGION_HIGHLIGHTS))


@mock.patch.object(GeocodingService, 'do_request')
def test_request_remove_duplicated_ids(mock_request):
    try:
        Geocodes(
            level_kind=LEVEL_KIND,
            queries=features_to_queries([FOO_FEATURE, FOO_FEATURE]),
            answers=features_to_answers([FOO_FEATURE, FOO_FEATURE])
        ).centroids()
    except ValueError:
        pass  # response doesn't contain proper feature with ids - ignore

    mock_request.assert_called_with(
        ExplicitRequest(
            requested_payload=[PayloadKind.centroids],
            ids=[FOO_FEATURE.id]
        )
    )


@mock.patch.object(GeocodingService, 'do_request')
def test_geocode_centroid(mock_request):
    try:
        make_geocode_region(REQUEST, REGION_NAME, REGION_ID, REGION_HIGHLIGHTS).centroids()
    except ValueError:
        pass  # response doesn't contain proper feature with ids - ignore

    mock_request.assert_called_with(
        ExplicitRequest(
            requested_payload=[PayloadKind.centroids],
            ids=REGION_LIST
        )
    )


@mock.patch.object(GeocodingService, 'do_request')
def test_geocode_boundary(mock_request):
    try:
        make_geocode_region(REQUEST, REGION_NAME, REGION_ID, REGION_HIGHLIGHTS).boundaries(RESOLUTION)
    except ValueError:
        pass  # response doesn't contain proper feature with ids - ignore

    mock_request.assert_called_with(
        ExplicitRequest(
            requested_payload=[PayloadKind.boundaries],
            ids=REGION_LIST,
            resolution=RESOLUTION
        )
    )


@mock.patch.object(GeocodingService, 'do_request')
def test_geocode_limit(mock_request):
    try:
        make_geocode_region(REQUEST, REGION_NAME, REGION_ID, REGION_HIGHLIGHTS).limits()
    except ValueError:
        pass  # response doesn't contain proper feature with ids - ignore

    mock_request.assert_called_with(
        ExplicitRequest(
            requested_payload=[PayloadKind.limits],
            ids=REGION_LIST
        )
    )


@mock.patch.object(GeocodingService, 'do_request')
def test_reorder_for_centroids_should_happen(mock_request):
    new_york = FeatureBuilder().set_id('2').set_query('New York').set_name('New York').set_centroid(GeoPoint(0, 0)).build_geocoded()
    las_vegas = FeatureBuilder().set_id('3').set_query('Las Vegas').set_name('Las Vegas').set_centroid(GeoPoint(0, 0)).build_geocoded()

    los_angeles = FeatureBuilder().set_id('1').set_query('Los Angeles').set_name('Los Angeles').set_centroid(
        GeoPoint(0, 0)).build_geocoded()
    mock_request.return_value = make_success_response().set_geocoded_features([new_york, las_vegas, los_angeles]).build()

    df = Geocodes(
        level_kind=LevelKind.city,
        queries=features_to_queries([los_angeles, new_york, las_vegas, los_angeles]),
        answers=features_to_answers([los_angeles, new_york, las_vegas, los_angeles])
    ).centroids()

    assert ['Los Angeles', 'New York', 'Las Vegas', 'Los Angeles'] == df[DF_COLUMN_FOUND_NAME].tolist()


@pytest.mark.parametrize('location,expected_type,expected_data', [
    (REGION_NAME, RegionKind.region_name, REGION_NAME),
    (make_geocode_region(REQUEST, REGION_NAME, REGION_ID, REGION_HIGHLIGHTS), RegionKind.region_ids, [REGION_ID]),
])
def test_prepare_parent(location, expected_type, expected_data):
    assert make_expected_map_region(expected_type, expected_data) == _prepare_parent(location)


@pytest.mark.parametrize('location,expected_type,expected_data', [
    (REGION_NAME, RegionKind.region_name, REGION_NAME),
    ([1.0, 2.0], RegionKind.coordinates, [1.0, 2.0]),
    ([1.0, 2.0, 3.0, 4.0, 5.0, 6.0], RegionKind.coordinates, [1.0, 2.0, 3.0, 4.0, 5.0, 6.0]),
    (POINT_DATA_FRAME, RegionKind.data_frame, POINT_DATA_FRAME),
    (RECT_DATA_FRAME, RegionKind.data_frame, RECT_DATA_FRAME),
    (make_geocode_region(REQUEST, REGION_NAME, REGION_ID, REGION_HIGHLIGHTS), RegionKind.region_ids, [REGION_ID]),
])
def test_prepare_location(location, expected_type, expected_data):
    assert make_expected_map_region(expected_type, expected_data) == _prepare_location(location)


@pytest.mark.parametrize('location,message', [
    ([], LOCATION_LIST_ERROR_MESSAGE),
    ([1.0], LOCATION_LIST_ERROR_MESSAGE),
    ([1.0, 2.0, 3.0], LOCATION_LIST_ERROR_MESSAGE),
    (DATA_FRAME, LOCATION_DATAFRAME_ERROR_MESSAGE),
])
def test_prepare_location_that_fail(location, message):
    with pytest.raises(ValueError) as exception:
        _prepare_location(location)

    assert message == exception.value.args[0]


@pytest.mark.parametrize('arg,expected_result', [
    (None, None),
    (['string'], ['string']),
    ('string', ['string']),
    (DATA_FRAME[DATAFRAME_COLUMN_NAME], DATAFRAME_NAME_LIST)
])
def test_ensure_is_list(arg, expected_result):
    assert expected_result == _ensure_is_list(arg)


def test_regions_to_data_frame_should_skip_highlights():
    regions = make_geocode_region(REQUEST, REGION_NAME, REGION_ID, REGION_HIGHLIGHTS)
    regions_df = regions.to_data_frame()
    assert [DF_COLUMN_ID, COLUMN_NAME_CITY, DF_COLUMN_FOUND_NAME, DF_COLUMN_CENTROID, DF_COLUMN_POSITION, DF_COLUMN_LIMIT] == list(regions_df.columns.values)

