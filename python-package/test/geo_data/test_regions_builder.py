#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from collections import namedtuple
from typing import Optional, List, Union
from unittest import mock

import shapely
from shapely.geometry import Point

from lets_plot.geo_data import regions_builder, GeocodingService
from lets_plot.geo_data.gis.request import RegionQuery, MapRegion, MapRegionKind, IgnoringStrategyKind, \
    AmbiguityResolver
from lets_plot.geo_data.gis.response import FeatureBuilder, LevelKind, GeoPoint, GeoRect
from lets_plot.geo_data.regions import Regions
from lets_plot.geo_data.regions_builder import RegionsBuilder
from .geo_data import make_success_response

Query = namedtuple('Query', 'name, region_id, region, feature')
ShapelyPoint = Point


def make_query(name: str, region_id: str) -> Query:
    region_feataure = FeatureBuilder().set_query(name).set_name(name).set_id(region_id).build_geocoded()
    return Query(name, region_id, MapRegion.scope([region_id]), region_feataure)


FOO = make_query('foo', 'foo_region')
BAR = make_query('bar', 'bar_region')
BAZ = make_query('baz', 'baz_region')
FOO_NAMESAKE = make_query(FOO.name, 'foo_namesake_region')


def feature(q: Query) -> FeatureBuilder:
    return FeatureBuilder().set_id(q.region_id).set_query(q.name).set_name(q.name)


def test_ctor():
    actual = \
        regions_builder(request=names(FOO), within=single_region(FOO)) \
            ._get_queries()
    expected = [query(FOO.name, FOO.region)]
    assert expected == actual


def test_single_chaining_with_addition():
    actual = \
        regions_builder(request=names(FOO)) \
            .where(names(BAR), single_region(BAR)) \
            ._get_queries()

    expected = [
        query(FOO.name),
        query(BAR.name, BAR.region)
    ]

    assert expected == actual


def test_list_chaining_with_addition():
    actual = \
        regions_builder(request=names(FOO, BAR)) \
            .where(names(BAZ), single_region(BAZ)) \
            ._get_queries()

    expected = [
        query(FOO.name),
        query(BAR.name),
        query(BAZ.name, BAZ.region)
    ]

    assert expected == actual


def test_single_chaining_with_overriding():
    actual = \
        regions_builder(request=names(FOO, BAR)) \
            .where(names(BAR), single_region(BAR)) \
            ._get_queries()

    expected = [
        query(FOO.name),
        query(BAR.name, BAR.region)
    ]

    assert expected == actual


def test_list_chaining_with_overriding():
    queries = \
        regions_builder(request=names(FOO, BAR, BAZ)) \
            .where(names(BAR), single_region(BAR)) \
            .where(names(BAZ), single_region(BAZ)) \
            ._get_queries()

    expected = [
        query(FOO.name),
        query(BAR.name, BAR.region),
        query(BAZ.name, BAZ.region)
    ]

    assert expected == queries


def test_override_twice():
    actual = \
        regions_builder(request=names(FOO)) \
            .where(names(FOO), single_region(FOO)) \
            .where(names(FOO), 'foofoo_region') \
            ._get_queries()

    expected = [
        query(FOO.name, 'foofoo_region')
    ]

    assert expected == actual


def test_with_regions():
    actual = \
        regions_builder(
            request=names(FOO),
            within=Regions(LevelKind.city, [FOO.feature])) \
            ._get_queries()

    expected = [
        query(FOO.name, FOO.region)
    ]

    assert expected == actual


def test_countries_alike():
    assert [query()] == RegionsBuilder(level=LevelKind.country)._get_queries()


def test_us48_alike():
    actual = RegionsBuilder(level=LevelKind.state, scope='us-48')._get_queries()
    expected = [query(request=None, scope=MapRegion.with_name('us-48'))]
    assert expected == actual


def test_list_with_duplications():
    assert [query('foo'), query('foo')] == regions_builder(request=names(FOO, FOO))._get_queries()


def test_list_duplication_with_overriding():
    actual = \
        regions_builder(request=names(FOO, FOO)) \
            .where(names(FOO)) \
            ._get_queries()

    expected = [
        query(FOO.name), query(FOO.name)
    ]

    assert expected == actual


def test_list_duplication_with_overriding_duplication():
    actual = \
        regions_builder(request=names(FOO, FOO)) \
            .where(names(FOO, FOO)) \
            ._get_queries()

    expected = [
        query(FOO.name), query(FOO.name)
    ]

    assert expected == actual


def test_simple_positional():
    actual = \
        regions_builder(
            request=names(FOO, BAR),
            within=regions_list(FOO, BAR)
        )._get_queries()

    expected = [
        query(FOO.name, FOO.region),
        query(BAR.name, BAR.region)
    ]

    assert expected == actual


def test_positional_ctor_with_duplicated_queries_and_different_regions():
    actual = \
        regions_builder(
            request=names(FOO, FOO_NAMESAKE),
            within=regions_list(FOO, FOO_NAMESAKE),
        )._get_queries()

    expected = [
        query(FOO.name, FOO.region),
        query(FOO_NAMESAKE.name, FOO_NAMESAKE.region)
    ]

    assert expected == actual


def test_positional_with_duplicated_queries_and_regions():
    actual = regions_builder(
        request=names(FOO, FOO),
        within=regions_list(FOO, FOO)
    )._get_queries()

    expected = [
        query(FOO.name, FOO.region),
        query(FOO.name, FOO.region)
    ]

    assert expected == actual


def test_positional_where_full_replace():
    actual = \
        regions_builder(
            request=names(FOO, BAR)
        ) \
            .where(names(FOO, BAR), regions_list(FOO, BAR)) \
            ._get_queries()

    expected = [
        query(FOO.name, FOO.region),
        query(BAR.name, BAR.region)
    ]

    assert expected == actual


def test_positional_where_partial_replace():
    actual = \
        regions_builder(
            request=names(FOO, BAR, BAZ)
        ) \
            .where(names(FOO, BAZ), regions_list(FOO, BAZ)) \
            ._get_queries()

    expected = [
        query(FOO.name, FOO.region),
        query(BAR.name),
        query(BAZ.name, BAZ.region)
    ]

    assert expected == actual


def test_positional_multi_where_replace():
    actual = \
        regions_builder(
            request=names(FOO, BAR, BAZ)
        ) \
            .where(names(BAR), regions_list(BAR)) \
            .where(names(BAZ, FOO), regions_list(BAZ, FOO)) \
            ._get_queries()

    expected = [
        query(FOO.name, FOO.region),
        query(BAR.name, BAR.region),
        query(BAZ.name, BAZ.region)
    ]

    assert expected == actual


def test_order_with_positional_where():
    actual = \
        regions_builder(
            request=names(FOO, BAR, BAZ)
        ) \
            .where(names(BAR), regions_list(BAR)) \
            .where(names(BAZ, FOO), regions_list(BAZ, FOO)) \
            ._get_queries()

    expected = [
        query(FOO.name, FOO.region),
        query(BAR.name, BAR.region),
        query(BAZ.name, BAZ.region)
    ]

    assert expected == actual


def test_order_with_where():
    actual = \
        regions_builder(
            request=names(FOO, BAR, BAZ)
        ) \
            .where(names(BAR), regions_list(BAR)) \
            .where(names(BAZ, FOO), single_region(BAZ, FOO)) \
            ._get_queries()

    expected = [
        query(FOO.name, map_region([BAZ, FOO])),
        query(BAR.name, BAR.region),
        query(BAZ.name, map_region([BAZ, FOO]))
    ]

    assert expected == actual


def test_only_default_ignoring_strategy():
    actual = \
        regions_builder(request=names(FOO, BAR)) \
            .allow_ambiguous() \
            ._get_queries()

    expected = [
        query(FOO.name, ignoring_strategy=IgnoringStrategyKind.take_namesakes),
        query(BAR.name, ignoring_strategy=IgnoringStrategyKind.take_namesakes)
    ]

    assert expected == actual


def test_empty_where_with_default_ignoring_strategy():
    actual = \
        regions_builder(request=names(FOO)) \
            .allow_ambiguous() \
            .where(names(BAR)) \
            ._get_queries()

    expected = [
        query(FOO.name, ignoring_strategy=IgnoringStrategyKind.take_namesakes),
        query(BAR.name, ignoring_strategy=IgnoringStrategyKind.take_namesakes),
    ]

    assert expected == actual


@mock.patch.object(GeocodingService, 'do_request')
def test_near_with_default_ignoring_strategy(mock_request):
    mock_request.return_value = make_success_response() \
        .set_geocoded_features(
        [
            feature(BAZ).set_centroid(GeoPoint(1., 2.)).build_geocoded()
        ]
    ).build()

    actual = \
        regions_builder(request=names(FOO, BAR)) \
            .allow_ambiguous() \
            .where(names(BAR), near=single_region(BAZ)) \
            ._get_queries()

    expected = [
        query(FOO.name, ignoring_strategy=IgnoringStrategyKind.take_namesakes),
        query(BAR.name, near=GeoPoint(1., 2.), ignoring_strategy=None)
    ]

    assert expected == actual


@mock.patch.object(GeocodingService, 'do_request')
def test_near_to_region(mock_request):
    mock_request.return_value = make_success_response() \
        .set_geocoded_features(
        [
            feature(BAZ).set_centroid(GeoPoint(1, 2)).build_geocoded()
        ]
    ).build()

    actual = \
        regions_builder(request=names(FOO, BAR)) \
            .where(names(BAR), near=single_region(BAZ)) \
            ._get_queries()

    expected = [
        query(FOO.name),
        query(BAR.name, near=GeoPoint(1, 2))
    ]

    assert expected == actual


def test_near_shapely_point():
    actual = \
        regions_builder(request=names(FOO, BAR)) \
            .where(names(BAR), near=ShapelyPoint(1., 2.)) \
            ._get_queries()

    expected = [
        query(FOO.name),
        query(BAR.name, near=GeoPoint(1., 2.))
    ]

    assert expected == actual


def test_within_shapely_box():
    actual = \
        regions_builder(request=names(FOO, BAR)) \
            .where(names(BAR), within=shapely.geometry.box(0, 1, 2, 3)) \
            ._get_queries()

    expected = [
        query(FOO.name),
        query(BAR.name, box=GeoRect(min_lon=0, min_lat=1, max_lon=2, max_lat=3))
    ]

    assert expected == actual


def test_empty():
    actual = \
        regions_builder()._get_queries()

    expected = [
        query()
    ]

    assert expected == actual


def test_positional_empty():
    actual = \
        regions_builder(request=[])._get_queries()

    expected = [
        query()
    ]

    assert expected == actual


def test_positional_wrong_size():
    try:
        regions_builder(request=names(FOO, BAR))._get_queries()
    except ValueError as e:
        assert 'Length of filter and region is not equal' == str(e)


def test_controversy_positional_where_with_duplicated_queries_and_different_regions():
    # We have to add duplicated objects with properly set regions. It's imposible to fix it later

    actual = \
        regions_builder(
            request=names(FOO, FOO_NAMESAKE)
        ).where(
            request=names(FOO, FOO_NAMESAKE),
            within=regions_list(FOO, FOO_NAMESAKE)
        )._get_queries()

    expected = [
        query(FOO.name, FOO_NAMESAKE.region),
        query(FOO_NAMESAKE.name, FOO_NAMESAKE.region)
    ]

    assert expected == actual


def query(
        request: Optional[str] = None,
        scope: Optional[Union[str, MapRegion]] = None,
        ignoring_strategy: Optional[IgnoringStrategyKind] = None,
        near: Optional[Union[str, GeoPoint]] = None,
        box: Optional[GeoRect] = None) -> RegionQuery:
    if isinstance(scope, MapRegion):
        pass
    elif isinstance(scope, str):
        scope = MapRegion.with_name(scope)
    else:
        scope = None

    return RegionQuery(request, scope, AmbiguityResolver(ignoring_strategy, near, box))


def map_region(queries: List[Query]):
    return MapRegion(MapRegionKind.id, [query.region_id for query in queries])


def names(*queries: Query) -> List[str]:
    return [query.name for query in queries]


def single_region(*queries: Query) -> Regions:
    return Regions(LevelKind.city, [query.feature for query in queries])


def regions_list(*queries: Query) -> List[Regions]:
    return [
        Regions(LevelKind.city, [query.feature]) for query in queries
    ]
