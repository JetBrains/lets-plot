#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
import pandas
import pytest
from geopandas import GeoDataFrame
from pandas import DataFrame
from shapely.geometry import Point, Polygon, LinearRing, MultiPolygon

from lets_plot._kbridge import _standardize_plot_spec
from lets_plot.geo_data import DF_COLUMN_CITY, DF_COLUMN_STATE, DF_COLUMN_COUNTY
from lets_plot.geo_data.geocoder import Geocoder
from lets_plot.plot import ggplot, geom_polygon, geom_point, geom_map, geom_rect, geom_text, geom_path, geom_livemap
from lets_plot.plot import util
from geo_data_test_util import get_map_data_meta, assert_error


def geo_data_frame(geometry, columns=[]):
    data = {key: None for key in columns}
    data['coord'] = geometry
    return GeoDataFrame(
        data=data,
        geometry='coord'
    )


def get_map(plot_spec) -> dict:
    return _standardize_plot_spec(plot_spec.as_dict())['layers'][0]['map']


def get_map_join(plot_spec) -> dict:
    return _standardize_plot_spec(plot_spec.as_dict())['layers'][0]['map_join']


def get_data(plot_spec) -> dict:
    return _standardize_plot_spec(plot_spec.as_dict())['layers'][0]['data']


def assert_map_data_meta(plot_spec):
    expected_map_data_meta = {'geodataframe': {'geometry': 'coord'}}
    assert expected_map_data_meta == get_map_data_meta(plot_spec, 0)


def test_geom_path_raises_an_error():
    assert_error(
        "Geocoding doesn't provide geometries for geom_path",
        lambda: ggplot() + geom_path(map=mock_geocoder())
    )


def test_geom_point_fetches_centroids():
    geocoder = mock_geocoder()
    plot_spec = ggplot() + geom_point(map=geocoder)

    assert_map_data_meta(plot_spec)
    assert geocoder.get_test_point_dict() == get_map(plot_spec)


def test_geom_polygon_fetches_boundaries():
    geocoder = mock_geocoder()
    plot_spec = ggplot() + geom_polygon(map=geocoder)

    assert_map_data_meta(plot_spec)
    assert geocoder.get_test_polygon_dict() == get_map(plot_spec)


def test_geom_map_fetches_boundaries():
    geocoder = mock_geocoder()
    plot_spec = ggplot() + geom_map(map=geocoder)

    assert_map_data_meta(plot_spec)
    assert geocoder.get_test_polygon_dict() == get_map(plot_spec)


def test_geom_rect_fetches_limits():
    geocoder = mock_geocoder()
    plot_spec = ggplot() + geom_rect(map=geocoder)

    assert_map_data_meta(plot_spec)
    assert geocoder.get_test_polygon_dict() == get_map(plot_spec)


def test_geom_text_fetches_centroids():
    geocoder = mock_geocoder()
    plot_spec = ggplot() + geom_text(map=geocoder)

    assert_map_data_meta(plot_spec)
    assert geocoder.get_test_point_dict() == get_map(plot_spec)


def test_data_should_call_to_dataframe():
    geocoder = mock_geocoder()
    plot_spec = ggplot() + geom_map(data=geocoder)

    geocoder.assert_get_geocodes_invocation()
    assert geocoder.get_test_geocodes() == get_layer_spec(plot_spec, 'data')


def get_layer_spec(plot_spec, spec_name):
    return _standardize_plot_spec(plot_spec.as_dict())['layers'][0].get(spec_name)


@pytest.mark.parametrize('map_join,map_columns,expected', [
    (
            'City_Name',
            [DF_COLUMN_CITY],
            [['City_Name'], [DF_COLUMN_CITY]]
    ),
    (  # Pair of string - simple keys for both data and map
            ['City_Name', DF_COLUMN_CITY],
            [DF_COLUMN_CITY],
            [['City_Name'], [DF_COLUMN_CITY]]
    ),
    (  # Pair of lists - simple keys for both data and map
            [['City_Name'], [DF_COLUMN_CITY]],
            [DF_COLUMN_CITY],
            [['City_Name'], [DF_COLUMN_CITY]]
    ),
    (  # List of list - data multikey. Deduce map multikey
            [['City_Name', 'State_Name']],
            [DF_COLUMN_CITY, DF_COLUMN_STATE],
            [['City_Name', 'State_Name'], [DF_COLUMN_CITY, DF_COLUMN_STATE]]
    ),
    (  # not all names were used for join
            [['City_Name', 'State_Name'], [DF_COLUMN_CITY, DF_COLUMN_STATE]],
            [DF_COLUMN_CITY, 'county', DF_COLUMN_STATE],
            [['City_Name', 'State_Name'], [DF_COLUMN_CITY, DF_COLUMN_STATE]]
    ),
    (
            None,
            [DF_COLUMN_CITY, DF_COLUMN_STATE],
            None
    ),
    (
            [['City_Name', 'State_Name']],
            [DF_COLUMN_CITY],
            "Data key columns count exceeds map key columns count: 2 > 1"
    ),
    (
            ['City_Name', 'County_Name', 'State_Name'],
            [DF_COLUMN_CITY, DF_COLUMN_COUNTY, DF_COLUMN_STATE],
            "map_join of type list[str] expected to have 1 or 2 items, but was 3"
    ),
    (
            'City_Name',
            [DF_COLUMN_CITY, DF_COLUMN_STATE],
            [['City_Name'], [DF_COLUMN_CITY]]
    ),
    (
            # HACK
            # Fake columns that never can be returned in real Geocoder to emulate generic GeoDataFrame
            'City_Name',
            ['absolutely_not_geocoding_gdf_city', 'absolutely_not_geocoding_gdf_state'],
            "Can't deduce joining keys.\n"
            "Define both data and map key columns in map_join explicitly: map_join=[['data_column'], ['map_column']]."
    ),
    (
            # HACK
            # Fake columns that never can be returned in real Geocoder to emulate generic GeoDataFrame
            [['City_Name', 'State_Name'], ['absolutely_not_geocoding_gdf_city', 'absolutely_not_geocoding_gdf_state']],
            ['absolutely_not_geocoding_gdf_city', 'absolutely_not_geocoding_gdf_state'],
            [['City_Name', 'State_Name'], ['absolutely_not_geocoding_gdf_city', 'absolutely_not_geocoding_gdf_state']]
    ),
])
def test_map_join_with_geodata(map_join, map_columns, expected):
    class MockGeocoder(Geocoder):
        def get_centroids(self) -> 'GeoDataFrame':
            return geo_data_frame([Point(-5, 17)], map_columns)

        def get_geocodes(self) -> pandas.DataFrame:
            return pandas.DataFrame(columns=map_columns)

    def run_test(map_param):
        if not isinstance(expected, str):
            plot_spec = ggplot() + geom_point(map_join=map_join, map=map_param)
            assert get_layer_spec(plot_spec, 'map_join') == expected
        else:
            assert_error(
                expected,
                lambda: ggplot() + geom_point(map_join=map_join, map=map_param)
            )

    run_test(MockGeocoder())  # with geocoder
    run_test(MockGeocoder().get_centroids())  # same with GeoDataFrame


def mock_geocoder() -> 'MockGeocoder':
    point_gdf = geo_data_frame([Point(-5, 17)])
    point_dict = {'coord': ['{"type": "Point", "coordinates": [-5.0, 17.0]}']}

    polygon_gdf = geo_data_frame([MultiPolygon([
        Polygon(LinearRing([(11, 12), (13, 14), (15, 13), (7, 4)])),
        Polygon(LinearRing([(10, 2), (13, 10), (12, 3)]))
    ])]
    )

    polygon_dict = {
        'coord': [
            '{'
            '"type": "MultiPolygon", '
            '"coordinates": ['
            '[[[11.0, 12.0], [13.0, 14.0], [15.0, 13.0], [7.0, 4.0], [11.0, 12.0]]], '
            '[[[10.0, 2.0], [13.0, 10.0], [12.0, 3.0], [10.0, 2.0]]]]'
            '}'
        ]
    }

    class MockGeocoder(Geocoder):
        def __init__(self):
            self._get_geocodes_invoked = False
            self._limits_fetched = False
            self._centroids_fetched = False
            self._boundaries_fetched = False

        def get_test_point_dict(self):
            return point_dict

        def get_test_polygon_dict(self):
            return polygon_dict

        def get_test_geocodes(self) -> dict:
            return {'request': ['foo'], 'found name': ['FOO']}

        def get_geocodes(self):
            self._get_geocodes_invoked = True
            return DataFrame(self.get_test_geocodes())

        def get_limits(self) -> GeoDataFrame:
            self._limits_fetched = True
            return polygon_gdf

        def get_centroids(self) -> GeoDataFrame:
            self._centroids_fetched = True
            return point_gdf

        def get_boundaries(self, resolution=None) -> GeoDataFrame:
            self._boundaries_fetched = True
            return polygon_gdf

        def assert_get_geocodes_invocation(self):
            assert self._get_geocodes_invoked, 'to_data_frame() invocation expected, but not happened'

    return MockGeocoder()
