#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
from geopandas import GeoDataFrame
from shapely.geometry import Point, MultiPoint, LineString, MultiLineString, Polygon, LinearRing, MultiPolygon

from lets_plot.geo_data.geocoder import Geocoder
from lets_plot.plot import ggplot, geom_polygon, geom_point, geom_map, geom_rect, geom_text, geom_path, geom_livemap
from .geo_data import get_map_data_meta, assert_error


def geo_data_frame(geometry):
    return GeoDataFrame(
        data={'coord': geometry},
        geometry='coord'
    )


def assert_map_data_meta(plotSpec):
    expected_map_data_meta = {'geodataframe': {'geometry': 'coord'}}
    assert expected_map_data_meta == get_map_data_meta(plotSpec, 0)


def test_geom_point_fetches_centroids():
    geocoder = mock_geocoder()
    plotSpec = ggplot() + geom_point(map=geocoder)

    assert_map_data_meta(plotSpec)
    geocoder.assert_centroids_fetch()


def test_geom_path_raises_an_error():
    assert_error(
        "Geocoding doesn't provide geometries supported by geom_path",
        lambda: ggplot() + geom_path(map=mock_geocoder())
    )


def test_geom_polygon_fetches_boundaries():
    geocoder = mock_geocoder()
    plotSpec = ggplot() + geom_polygon(map=geocoder)

    assert_map_data_meta(plotSpec)
    geocoder.assert_boundaries_fetch()


def test_geom_map_fetches_boundaries():
    geocoder = mock_geocoder()
    plotSpec = ggplot() + geom_map(map=geocoder)

    assert_map_data_meta(plotSpec)
    geocoder.assert_boundaries_fetch()


def test_geom_rect_fetches_limits():
    geocoder = mock_geocoder()
    plotSpec = ggplot() + geom_rect(map=geocoder)

    assert_map_data_meta(plotSpec)
    geocoder.assert_limits_fetch()


def test_geom_text_fetches_centroids():
    geocoder = mock_geocoder()
    plotSpec = ggplot() + geom_text(map=geocoder)

    assert_map_data_meta(plotSpec)
    geocoder.assert_centroids_fetch()


def test_geom_livemap_fetches_centroids():
    geocoder = mock_geocoder()
    plotSpec = ggplot() + geom_livemap(map=geocoder)

    assert_map_data_meta(plotSpec)
    geocoder.assert_centroids_fetch()



def mock_geocoder() -> 'MockGeocoder':
    POINT = geo_data_frame([Point(-5, 17)])

    MULTIPOLYGON = geo_data_frame(MultiPolygon([
        Polygon(LinearRing([(11, 12), (13, 14), (15, 13), (7, 4)])),
        Polygon(LinearRing([(10, 2), (13, 10), (12, 3)]))
    ])
    )

    class MockGeocoder(Geocoder):
        def __init__(self):
            self._limits_fetched = False
            self._centroids_fetched = False
            self._boundaries_fetched = False

        def get_limits(self) -> GeoDataFrame:
            self._limits_fetched = True
            return MULTIPOLYGON

        def get_centroids(self) -> GeoDataFrame:
            self._centroids_fetched = True
            return POINT

        def get_boundaries(self, resolution=None) -> GeoDataFrame:
            self._boundaries_fetched = True
            return MULTIPOLYGON

        def assert_limits_fetch(self):
            assert self._limits_fetched, 'get_limits() invocation expected, but not happened'

        def assert_centroids_fetch(self):
            assert self._centroids_fetched, 'get_centroids() invocation expected, but not happened'

        def assert_boundaries_fetch(self):
            assert self._boundaries_fetched, 'get_boundaries() invocation expected, but not happened'

    return MockGeocoder()
