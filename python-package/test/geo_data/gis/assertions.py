#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from lets_plot.geo_data.gis.request import RegionQuery, MapRegion
from lets_plot.geo_data.gis.response import GeocodedFeature, GeoPoint, GeoRect, Namesake, NamesakeParent, AmbiguousFeature, Boundary


def assert_geocoded(expected: GeocodedFeature, actual: GeocodedFeature):
    assert expected.query == actual.query
    assert expected.id == actual.id
    assert expected.name == actual.name
    assert_point(expected.centroid, actual.centroid)
    assert_rect(expected.limit, actual.limit)
    assert_rect(expected.position, actual.position)
    assert_geometry(expected.boundary, actual.boundary)


def assert_point(expected: GeoPoint, actual: GeoPoint):
    if not _check_is_none(expected, actual):
        assert expected.lon == actual.lon
        assert expected.lat == actual.lat


def assert_rect(expected: GeoRect, actual: GeoRect):
    if not _check_is_none(expected, actual):
        assert expected.min_lon == actual.min_lon
        assert expected.min_lat == actual.min_lat
        assert expected.max_lon == actual.max_lon
        assert expected.max_lat == actual.max_lat


def assert_geometry(expected: Boundary, actual: Boundary):
    if not _check_is_none(expected, actual):
        assert expected.geometry == actual.geometry


def assert_ambiguous(expected: AmbiguousFeature, actual: AmbiguousFeature):
    assert expected.query == actual.query
    assert expected.total_namesake_count == actual.total_namesake_count
    for exp_namesake, act_namesake in zip(expected.namesake_examples, actual.namesake_examples):
        assert_namesake(exp_namesake, act_namesake)


def assert_parent(expected: NamesakeParent, actual: NamesakeParent):
    assert expected.name == actual.name
    assert expected.level == actual.level


def assert_namesake(expected: Namesake, actual: Namesake):
    assert expected.name == actual.name
    for exp_parent, act_parent in zip(expected.parents, actual.parents):
        assert_parent(exp_parent, act_parent)


def assert_region_query(expected: RegionQuery, actual: RegionQuery):
    assert expected.request == actual.request
    assert expected.ambiguity_resolver == actual.ambiguity_resolver
    assert_map_region(expected.scope, actual.scope)


def assert_map_region(expected: MapRegion, actual: MapRegion):
    if not _check_is_none(expected, actual):
        assert expected.kind == actual.kind
        assert expected.values == actual.values


def _check_is_none(expected, actual) -> bool:
    both_same = (expected is None and actual is None) or (expected is not None and actual is not None)
    assert both_same
    return expected is None
