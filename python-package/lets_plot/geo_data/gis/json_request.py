import enum
from typing import Dict, List, Optional, Tuple

from .fluent_dict import FluentDict, FluentList
from .geometry import GeoPoint
from .request import RegionQuery, MapRegion, MapRegionKind, PayloadKind, RegionQueryBuilder, IgnoringStrategyKind, \
    MapRegionBuilder
from .request import Request, GeocodingRequest, ExplicitRequest, RequestBuilder, RequestKind, ReverseGeocodingRequest
from .response import LevelKind, GeoRect

PROTOCOL_VERSION = 3


class Field(enum.Enum):
    version = 'version'
    mode = 'mode'
    requested_payload = 'feature_options'
    resolution = 'resolution'
    view_box = 'view_box'
    fetched_ids = 'fetched_ids'
    option_kind = 'kind'
    geo_object_list = 'ids'
    region_queries = 'region_queries'
    region_query_names = 'region_query_names'
    region_query_countries = 'region_query_countries'
    region_query_states = 'region_query_states'
    region_query_counties = 'region_query_counties'
    region_query_parent = 'region_query_parent'
    scope = 'scope'
    level = 'level'
    map_region_kind = 'kind'
    map_region_values = 'values'
    match = 'match'
    namesake_example_limit = 'namesake_example_limit'
    allow_ambiguous = 'allow_ambiguous'
    ambiguity_resolver = 'ambiguity_resolver'
    ambiguity_ignoring_strategy = 'ambiguity_resolver_ignoring_strategy'
    ambiguity_closest_coord = 'ambiguity_resolver_closest_coord'
    ambiguity_box = 'ambiguity_resolver_box'

    reverse_level = "level"
    reverse_coordinates = "reverse_coordinates"
    reverse_parent = "reverse_parent"

    coord_lon = 'lon'
    coord_lat = 'lat'

    min_lon = 'min_lon'
    min_lat = 'min_lat'
    max_lon = 'max_lon'
    max_lat = 'max_lat'


class RequestFormatter:
    @staticmethod
    def format(request: Request) -> FluentDict:
        if isinstance(request, GeocodingRequest):
            return RequestFormatter._format_geocoding_request(request)
        elif isinstance(request, ExplicitRequest):
            return RequestFormatter._format_explicit_request(request)
        elif isinstance(request, ReverseGeocodingRequest):
            return RequestFormatter._format_reverse_geocoding_request(request)
        else:
            raise ValueError('Unknown request kind: ' + str(request))

    @staticmethod
    def _format_geocoding_request(request: 'GeocodingRequest') -> FluentDict:
        return RequestFormatter \
            ._common(RequestKind.geocoding, request) \
            .put(Field.region_queries, RequestFormatter._format_region_queries(request.region_queries)) \
            .put(Field.scope, RequestFormatter._format_scope(request.scope)) \
            .put(Field.level, request.level) \
            .put(Field.namesake_example_limit, request.namesake_example_limit) \
            .put(Field.allow_ambiguous, request.allow_ambiguous)

    @staticmethod
    def _format_explicit_request(request: 'ExplicitRequest') -> FluentDict:
        return RequestFormatter \
            ._common(RequestKind.explicit, request) \
            .put(Field.geo_object_list, request.geo_object_list)

    @staticmethod
    def _format_reverse_geocoding_request(request: 'ReverseGeocodingRequest'):
        return RequestFormatter \
            ._common(RequestKind.reverse, request) \
            .put(Field.reverse_coordinates, [RequestFormatter._format_coord(coord) for coord in request.coordinates]) \
            .put(Field.reverse_level, request.level) \
            .put(Field.reverse_parent, RequestFormatter._format_map_region(request.scope))

    @staticmethod
    def _common(request_kind: RequestKind, request: Request) -> FluentDict:
        return FluentDict() \
            .put(Field.version, PROTOCOL_VERSION) \
            .put(Field.mode, request_kind.value) \
            .put(Field.requested_payload, request.requested_payload) \
            .put(Field.resolution, request.resolution) \
            .put(Field.view_box, None) \
            .put(Field.fetched_ids, None)

    @staticmethod
    def _format_region_queries(region_queires: List[RegionQuery]) -> List[Dict]:
        result = []
        for query in region_queires:
            result.append(
                FluentDict()
                    .put(Field.region_query_names, [] if query.request is None else [query.request])
                    .put(Field.region_query_countries, RequestFormatter._format_map_region(query.country))
                    .put(Field.region_query_states, RequestFormatter._format_map_region(query.state))
                    .put(Field.region_query_counties, RequestFormatter._format_map_region(query.county))
                    .put(Field.ambiguity_resolver, None if query.ambiguity_resolver is None else FluentDict()
                         .put(Field.ambiguity_ignoring_strategy, query.ambiguity_resolver.ignoring_strategy)
                         .put(Field.ambiguity_box, RequestFormatter._format_box(query.ambiguity_resolver.box))
                         .put(Field.ambiguity_closest_coord, RequestFormatter._format_coord(query.ambiguity_resolver.closest_coord))) \
                    .put(Field.region_query_parent, RequestFormatter._format_map_region(query.scope))
                    .to_dict()
            )
        return result

    @staticmethod
    def _format_scope(scope: List[MapRegion]) -> List[Dict]:
        return [RequestFormatter._format_map_region(s) for s in scope]

    @staticmethod
    def _format_map_region(parent: Optional[MapRegion]) -> Optional[Dict]:
        if parent is None:
            return None

        # special case - place is just a geocoded object with id and extra information, used by client
        # server doesn't need this extra information
        if parent.kind.value == 'place':
            return FluentDict() \
                .put(Field.map_region_kind, MapRegionKind.id.value) \
                .put(Field.map_region_values, parent.values) \
                .to_dict()

        return FluentDict() \
            .put(Field.map_region_kind, parent.kind.value) \
            .put(Field.map_region_values, parent.values) \
            .to_dict()

    @staticmethod
    def _format_coord(closest_coord: GeoPoint) -> Optional[Tuple[float, float]]:
        if closest_coord is None:
            return None

        return closest_coord.lon, closest_coord.lat

    @staticmethod
    def _format_box(rect: GeoRect) -> Optional[Dict]:
        if rect is None:
            return None

        return FluentDict() \
            .put(Field.min_lon, rect.start_lon) \
            .put(Field.min_lat, rect.min_lat) \
            .put(Field.max_lon, rect.end_lon) \
            .put(Field.max_lat, rect.max_lat) \
            .to_dict()


class RequestParser:
    @staticmethod
    def parse(request_json: Dict) -> Request:
        request = RequestBuilder()
        request_dict = FluentDict(request_json) \
            .visit_enum(Field.mode, RequestKind, request.set_request_kind) \
            .visit_enums(Field.requested_payload, PayloadKind, request.set_requested_payload) \
            .visit_int_optional(Field.resolution, request.set_resolution)

        if request.request_kind == RequestKind.explicit:
            request_dict.visit_str_list(Field.geo_object_list, request.set_ids)
        elif request.request_kind == RequestKind.geocoding:
            request_dict \
                .visit_enum_existing(Field.level, LevelKind, request.set_level) \
                .visit_int(Field.namesake_example_limit, request.set_namesake_limit) \
                .visit_bool(Field.allow_ambiguous, request.set_allow_ambiguous) \
                .visit_list(Field.region_queries, lambda regions: request.set_queries(regions.map(RequestParser._parse_region_query).list()))
        elif request.request_kind == RequestKind.reverse:
            request_dict \
                .visit_enum_existing(Field.reverse_level, LevelKind, request.set_level) \
                .visit_list(Field.reverse_coordinates, lambda coords: request.set_reverse_coordinates(RequestParser._parse_coordinates(coords))) \
                .visit_object_optional(Field.reverse_parent,
                                       lambda parent: request.set_reverse_scope(RequestParser._parse_map_region(parent)))
        else:
            raise ValueError('Unknown request kind: ' + str(request))

        return request.build()

    @staticmethod
    def _parse_region_query(region_query: dict) -> RegionQuery:
        region_q = FluentDict(region_query)
        assert len(region_q.get_list(Field.region_query_names)) in [0, 1], 'Multirequests are not supported'

        builder = RegionQueryBuilder()

        FluentDict(region_query) \
            .visit_str_list(Field.region_query_names, lambda names: builder.set_request(names[0] if len(names) == 1 else None)) \
            .visit_object_optional(Field.ambiguity_resolver, lambda resolver: resolver
                                   .visit_list_optional(Field.ambiguity_closest_coord,
                                                        lambda coord: builder.set_closest_coord(RequestParser._parse_coord(coord)))
                                   .visit_object_optional(Field.ambiguity_box,
                                                          lambda jsonBox: builder.set_box(RequestParser._parse_geo_rect(jsonBox)))
                                   .visit_enum_existing(Field.ambiguity_ignoring_strategy, IgnoringStrategyKind, builder.set_ignoring_strategy)) \
            .visit_object_optional(Field.region_query_parent, lambda parent: builder.set_scope(RequestParser._parse_map_region(parent)))

        return builder.build()

    @staticmethod
    def _parse_map_region(json: FluentDict) -> MapRegion:
        builder = MapRegionBuilder()
        json \
            .visit_str_list(Field.map_region_values, builder.set_parent_values) \
            .visit_bool(Field.map_region_kind, builder.set_parent_kind)

        return builder.build()

    @staticmethod
    def _parse_coord(jsonCoord: FluentList) -> GeoPoint:
        return GeoPoint(jsonCoord.list()[0], jsonCoord.list()[1])

    @staticmethod
    def _parse_geo_rect(jsonBox: FluentDict) -> GeoRect:
        return GeoRect(
            start_lon=jsonBox.get_float(Field.min_lon),
            min_lat=jsonBox.get_float(Field.min_lat),
            end_lon=jsonBox.get_float(Field.max_lon),
            max_lat=jsonBox.get_float(Field.max_lat),
        )

    @staticmethod
    def _parse_coordinates(coordinates: FluentList) -> List[GeoPoint]:
        return [GeoPoint(coord[0], coord[1]) for coord in coordinates.list()]
