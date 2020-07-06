#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from typing import Union
from unittest import mock

import pytest
from pandas import DataFrame

from lets_plot._type_utils import _standardize_value
from lets_plot.geo_data import regions, regions_builder
from lets_plot.geo_data.gis.geocoding_service import GeocodingService
from lets_plot.geo_data.gis.request import MapRegion, RegionQuery, GeocodingRequest, PayloadKind, ExplicitRequest, \
    AmbiguityResolver
from lets_plot.geo_data.gis.response import LevelKind, FeatureBuilder, GeoPoint
from lets_plot.geo_data.livemap_helper import _prepare_location, RegionKind, _prepare_parent, \
    LOCATION_LIST_ERROR_MESSAGE, LOCATION_DATAFRAME_ERROR_MESSAGE
from lets_plot.geo_data.regions import _to_scope, _to_resolution, _ensure_is_list, Regions, DF_REQUEST, DF_ID, \
    DF_FOUND_NAME
from .geo_data import make_geocode_region, make_region, make_success_response

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

REGION_QUERY_LA = RegionQuery('LA', PARENT_WITH_NAME, AmbiguityResolver())
REGION_QUERY_NY = RegionQuery('NY', PARENT_WITH_NAME, AmbiguityResolver())

NAMESAKES_EXAMPLE_LIMIT = 10


def make_expected_map_region(region_kind: RegionKind, values):
    return {
        'type': region_kind.value,
        'data': values
    }


@mock.patch.object(GeocodingService, 'do_request')
def test_regions(mock_geocoding):
    try:
        regions(level=LEVEL, request=FILTER_LIST, within=REGION_NAME).to_data_frame()
    except Exception:
        pass  # response doesn't contain proper feature with ids - ignore

    mock_geocoding.assert_called_with(
        GeocodingRequest(requested_payload=[],
                         resolution=None,
                         region_queries=[REGION_QUERY_LA, REGION_QUERY_NY],
                         level=LEVEL_KIND,
                         namesake_example_limit=NAMESAKES_EXAMPLE_LIMIT
                         )
    )


@mock.patch.object(GeocodingService, 'do_request')
def test_regions_with_highlights(mock_geocoding):
    try:
        regions_builder(level=LEVEL, request=FILTER_LIST, within=REGION_NAME, highlights=True).build()
    except Exception:
        pass  # response doesn't contain proper feature with ids - ignore

    mock_geocoding.assert_called_with(
        GeocodingRequest(requested_payload=[PayloadKind.highlights],
                         resolution=None,
                         region_queries=[REGION_QUERY_LA, REGION_QUERY_NY],
                         level=LEVEL_KIND,
                         namesake_example_limit=NAMESAKES_EXAMPLE_LIMIT
                         )
    )


FOO = FeatureBuilder().set_query('foo').set_name('fooname').set_id('fooid').build_geocoded()
BAR = FeatureBuilder().set_query('foo').set_name('barname').set_id('barid').build_geocoded()

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
    (Regions(
        [
            FOO,
            BAR
        ]),
     MapRegion.with_ids([FOO.id, BAR.id])
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
         Regions([FOO]),
         Regions([BAR])
     ],

     [
         MapRegion.with_ids([FOO.id]),
         MapRegion.with_ids([BAR.id])
     ]
    ),

    # mix of strings and regions
    ([
         'foo',
         Regions([BAR]),
     ],
     [
         MapRegion.with_name(FOO.query),
         MapRegion.with_ids([BAR.id])
     ]
    )
])
def test_to_parent_with_name(location, expected):
    actual = _to_scope(location)
    assert expected == actual


def test_to_parent_with_id():
    assert MapRegion.with_ids(REGION_LIST) == _to_scope(make_geocode_region(REQUEST, REGION_NAME, REGION_ID, REGION_HIGHLIGHTS))


@mock.patch.object(GeocodingService, 'do_request')
def test_request_remove_duplicated_ids(mock_request):
    try:
        Regions(
            [
                make_region(REQUEST, REGION_NAME, REGION_ID, REGION_HIGHLIGHTS),
                make_region(REQUEST, REGION_NAME, REGION_ID, REGION_HIGHLIGHTS)
            ]
        ).centroids()
    except ValueError:
        pass  # response doesn't contain proper feature with ids - ignore

    mock_request.assert_called_with(
        ExplicitRequest(
            requested_payload=[PayloadKind.centroids],
            ids=[REGION_ID]
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
            resolution=_to_resolution(RESOLUTION)
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
    mock_request.return_value = make_success_response() \
        .set_geocoded_features(
        [
            FeatureBuilder().set_id('2').set_query('New York').set_name('New York').set_centroid(GeoPoint(0, 0)).build_geocoded(),
            FeatureBuilder().set_id('3').set_query('Las Vegas').set_name('Las Vegas').set_centroid(GeoPoint(0, 0)).build_geocoded(),
            FeatureBuilder().set_id('1').set_query('Los Angeles').set_name('Los Angeles').set_centroid(GeoPoint(0, 0)).build_geocoded()
        ]
    ).build()

    df = Regions(
        [
            make_region('Los Angeles', 'Los Angeles', '1', []),
            make_region('New York', 'New York', '2', []),
            make_region('Las Vegas', 'Las Vegas', '3', []),
            make_region('Los Angeles', 'Los Angeles', '1', []),
        ]
    ).centroids()

    assert ['Los Angeles', 'New York', 'Las Vegas', 'Los Angeles'] == df[DF_FOUND_NAME].tolist()


@pytest.mark.parametrize('arg,expected_resolution', [
    (1, 1),
    (3, 3),
    (6, 6),
    (9, 9),
    (12, 12),
    (15, 15),
    (None, None),
])
def test_to_resolution(arg: Union[str, int], expected_resolution: int):
    assert expected_resolution == _to_resolution(arg)


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
    assert [DF_REQUEST, DF_ID, DF_FOUND_NAME] == list(regions_df.columns.values)


def test_regions_to_dict():
    regions = make_geocode_region(REQUEST, REGION_NAME, REGION_ID, [])
    regions_dict = _standardize_value(regions)
    assert REQUEST == regions_dict[DF_REQUEST][0]
    assert REGION_ID == regions_dict[DF_ID][0]
    assert REGION_NAME == regions_dict[DF_FOUND_NAME][0]

