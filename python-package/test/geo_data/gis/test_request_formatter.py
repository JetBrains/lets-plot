#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from typing import Optional

from geo_data.gis.assertions import assert_region_query

from lets_plot.geo_data.gis.geometry import GeoRect, GeoPoint
from lets_plot.geo_data.gis.json_request import RequestFormatter, RequestParser
from lets_plot.geo_data.gis.request import RequestBuilder, ExplicitRequest, RequestKind, GeocodingRequest, RegionQuery, \
    MapRegion, \
    PayloadKind, AmbiguityResolver, IgnoringStrategyKind, ReverseGeocodingRequest
from lets_plot.geo_data.gis.response import LevelKind


def _query(
        request: Optional[str],
        scope: Optional[MapRegion] = None,
        ambiguity_resolver: AmbiguityResolver = AmbiguityResolver.empty()):
    return RegionQuery(request, scope, ambiguity_resolver)


def test_explicit_request():
    request: ExplicitRequest = RequestBuilder() \
        .set_request_kind(RequestKind.explicit) \
        .set_ids(['1', '2', '3']) \
        .build()

    json = RequestFormatter().format(request).to_dict()

    parsed_request = RequestParser().parse(json)

    assert isinstance(parsed_request, ExplicitRequest)
    assert request.requested_payload == parsed_request.requested_payload
    assert request.resolution == parsed_request.resolution

    assert request.geo_object_list == parsed_request.geo_object_list


def test_geocoding_request():
    foo = _query(request='foo')
    bar = _query(
        request='bar',
        scope=MapRegion.with_name('baz'),
        ambiguity_resolver=AmbiguityResolver(ignoring_strategy=IgnoringStrategyKind.skip_all)
    )

    request: GeocodingRequest = RequestBuilder() \
        .set_request_kind(RequestKind.geocoding) \
        .set_requested_payload([PayloadKind.boundaries, PayloadKind.centroids]) \
        .set_resolution(123) \
        .set_level(LevelKind.city) \
        .set_namesake_limit(10) \
        .set_queries([foo, bar]) \
        .build()

    json = RequestFormatter().format(request).to_dict()

    parsed_request = RequestParser().parse(json)

    assert isinstance(parsed_request, GeocodingRequest)
    assert request.requested_payload == parsed_request.requested_payload
    assert request.resolution == parsed_request.resolution

    assert request.level == parsed_request.level
    assert request.namesake_example_limit == parsed_request.namesake_example_limit
    assert 2 == len(parsed_request.region_queries)
    assert_region_query(foo, parsed_request.region_queries[0])
    assert_region_query(bar, parsed_request.region_queries[1])


def test_ambiguity_resolver():
    foo = _query(request='foo', ambiguity_resolver=AmbiguityResolver(closest_coord=GeoPoint(1, 2)))

    request: GeocodingRequest = RequestBuilder() \
        .set_request_kind(RequestKind.geocoding) \
        .set_namesake_limit(10) \
        .set_queries([foo]) \
        .build()

    json = RequestFormatter().format(request).to_dict()

    parsed_request = RequestParser().parse(json)

    assert isinstance(parsed_request, GeocodingRequest)
    assert 1 == len(parsed_request.region_queries)
    assert_region_query(foo, parsed_request.region_queries[0])


def test_ambiguity_resolver_with_box():
    foo = _query(request='foo', ambiguity_resolver=AmbiguityResolver(box=GeoRect(0, 1, 2, 3)))

    request: GeocodingRequest = RequestBuilder() \
        .set_request_kind(RequestKind.geocoding) \
        .set_namesake_limit(10) \
        .set_queries([foo]) \
        .build()

    json = RequestFormatter().format(request).to_dict()

    parsed_request = RequestParser().parse(json)

    assert isinstance(parsed_request, GeocodingRequest)
    assert 1 == len(parsed_request.region_queries)
    assert_region_query(foo, parsed_request.region_queries[0])


def test_reverse_request():
    request: ReverseGeocodingRequest = RequestBuilder() \
        .set_request_kind(RequestKind.reverse) \
        .set_reverse_coordinates([GeoPoint(1.0, 2.0)]) \
        .set_level(LevelKind.city) \
        .set_reverse_scope(MapRegion.with_name('foo_region')) \
        .set_resolution(123) \
        .set_requested_payload([PayloadKind.boundaries, PayloadKind.centroids]) \
        .build()

    json = RequestFormatter().format(request).to_dict()

    parsed_request: ReverseGeocodingRequest = RequestParser().parse(json)

    assert isinstance(parsed_request, ReverseGeocodingRequest)
    assert request.requested_payload == parsed_request.requested_payload
    assert request.resolution == parsed_request.resolution

    assert request.coordinates == parsed_request.coordinates
    assert request.level == parsed_request.level
    assert request.scope == parsed_request.scope
