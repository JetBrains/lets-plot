import enum
from numbers import Number
from typing import Optional, List, Tuple, Union

from .geometry import GeoRect, GeoPoint
from ..type_assertion import assert_type, assert_list_type, assert_optional_type

MISSING_SCOPE_OR_REQUEST_EXCEPTION_TEXT = 'Missing required argument: scope or request.'
MISSING_LEVEL_OR_REQUEST_EXCEPTION_TEXT = 'Missing required argument: level or request.'
MISSING_LEVEL_AND_SCOPE_OR_REQUEST_EXCEPTION_TEXT = 'Missing required argument. You must enter level and scope or request.'

GeoId = str


class PayloadKind(enum.Enum):
    highlights = 'highlights'
    centroids = 'centroid'
    boundaries = 'boundary'
    limits = 'limit'
    poisitions = 'position'


class RequestKind(enum.Enum):
    explicit = 'by_id'
    geocoding = 'by_geocoding'
    reverse = 'reverse'


class IgnoringStrategyKind(enum.Enum):
    skip_all = 'skip_all'
    skip_missing = 'skip_missing'
    skip_ambiguous = 'skip_ambiguous'
    take_namesakes = 'take_namesakes'


class LevelKind(enum.Enum):
    country = 'country'
    state = 'state'
    county = 'county'
    city = 'city'


MODE_BY_GEOCODING = 'by_geocoding'
MODE_BY_ID = 'by_id'


class MapRegionKind(enum.Enum):
    id = True
    name = False
    place = 'place'


class MapRegion:
    '''
    Represents three different entities:
    scope - ids of already geocoded objects. The only kind of MapRegion allowed to store multiply objects
    place - already geocoded single place. In addition to id it holds administrative level and requeted name.
            Used mostly as parent object for geocoding other objects.
    with_name - single name, not yet geocoded.
    '''
    @staticmethod
    def name_or_none(place: Optional['MapRegion']):
        if place is None:
            return None

        if place.kind == MapRegionKind.place:
            return place.request()

        if place.kind == MapRegionKind.name:
            return place.name()

        raise ValueError('MapRegion with kind \'{}\' doesn\'t have a name'.format(place.kind))


    @staticmethod
    def place(id: str, request: Optional[str], level_kind: LevelKind):
        assert_type(id, str)
        assert_optional_type(request, str)
        assert_type(level_kind, LevelKind)
        return MapRegion(MapRegionKind.place, [id], request, level_kind)

    @staticmethod
    def scope(parent_ids: List[str]):
        assert_list_type(parent_ids, str)
        return MapRegion(MapRegionKind.id, parent_ids)

    @staticmethod
    def with_name(name: str):
        assert_type(name, str)
        return MapRegion(MapRegionKind.name, [name])

    def __init__(self, kind: MapRegionKind, values: List[str], request: Optional[str] = None, level_kind: Optional[LevelKind] = None):
        assert_type(kind, MapRegionKind)
        assert_list_type(values, str)
        assert_optional_type(request, str)
        assert_optional_type(level_kind, LevelKind)

        self.kind: MapRegionKind = kind
        self.values: Tuple[str] = tuple(values, )
        self._request:Optional[str] = request
        self._level_kind: Optional[LevelKind] = level_kind
        self._hash = hash((self.values, self.kind))

    def request(self) -> Optional[str]:
        assert self.kind == MapRegionKind.place, 'Invalid MapRegion kind. Expected \'place\', but was ' + str(self.kind)
        assert_optional_type(self._request, str)
        return self._request

    def name(self) -> str:
        assert self.kind == MapRegionKind.name, 'Invalid MapRegion kind. Expected \'name\', but was ' + str(self.kind)
        assert_type(self.values[0], str)
        return self.values[0]

    def level_kind(self) -> Optional[LevelKind]:
        assert self.kind == MapRegionKind.place, 'Invalid MapRegion kind: only place contains level_kind'
        return self._level_kind

    def __eq__(self, other: 'MapRegion'):
        return isinstance(other, MapRegion) \
               and self.kind == other.kind \
               and self.values == other.values \
               and self._request == other._request \
               and self._level_kind == other._level_kind

    def __ne__(self, o: object) -> bool:
        return not self == o

    def __str__(self):
        if self.kind == MapRegionKind.place:
            return '{} {} {}'.format(str(self.values), self._request, self._level_kind)

        if self.kind == MapRegionKind.name:
            return self.values[0]

        if self.kind == MapRegionKind.id:
            return ",".join(self.values)

        return str(self.values)

    def __hash__(self):
        return self._hash


class AmbiguityResolver:

    @staticmethod
    def empty() -> 'AmbiguityResolver':
        return AmbiguityResolver()

    def __init__(self,
                 ignoring_strategy: Optional[IgnoringStrategyKind] = None,
                 closest_coord: Optional[GeoPoint] = None,
                 box: Optional[GeoRect] = None):
        assert_optional_type(ignoring_strategy, IgnoringStrategyKind)
        assert_optional_type(closest_coord, GeoPoint)
        assert_optional_type(box, GeoRect)

        self.ignoring_strategy: IgnoringStrategyKind = ignoring_strategy
        self.closest_coord: Optional[GeoPoint] = closest_coord
        self.box: Optional[GeoRect] = box

    def __eq__(self, o):
        return isinstance(o, AmbiguityResolver) \
               and self.ignoring_strategy == o.ignoring_strategy \
               and self.closest_coord == o.closest_coord \
               and self.box == o.box

    def __ne__(self, o):
        return not self == o


class RegionQuery:
    def __init__(self,
                 request: Optional[str],
                 scope: Optional[MapRegion] = None,
                 ambiguity_resolver: AmbiguityResolver = AmbiguityResolver.empty(),
                 country: Optional[MapRegion] = None,
                 state: Optional[MapRegion] = None,
                 county: Optional[MapRegion] = None
                 ):
        assert_optional_type(request, str)
        assert_optional_type(scope, MapRegion)
        assert_type(ambiguity_resolver, AmbiguityResolver)
        assert_optional_type(county, MapRegion)
        assert_optional_type(state, MapRegion)
        assert_optional_type(country, MapRegion)

        self.request: Optional[str] = request
        self.scope: Optional[MapRegion] = scope
        self.ambiguity_resolver: AmbiguityResolver = ambiguity_resolver
        self.country: Optional[MapRegion] = country
        self.state: Optional[MapRegion] = state
        self.county: Optional[MapRegion] = county

    def __eq__(self, o: object) -> bool:
        return isinstance(o, RegionQuery) \
               and self.request == o.request \
               and self.scope == o.scope \
               and self.ambiguity_resolver == o.ambiguity_resolver \
               and self.country == o.country \
               and self.state == o.state \
               and self.county == o.county

    def __ne__(self, o: object) -> bool:
        return not self == o

    def __str__(self):
        return str(self.request) + ' in ' + str(self.scope)


class Request:
    def __init__(self, requested_payload: List[PayloadKind], resolution: Optional[Number]):
        assert_list_type(requested_payload, PayloadKind)
        assert_optional_type(resolution, Number)

        assert requested_payload is not None

        self.requested_payload: List[PayloadKind] = requested_payload
        self.resolution: Optional[int] = resolution

    def __eq__(self, o: object) -> bool:
        return isinstance(o, Request) \
               and self.requested_payload == o.requested_payload \
               and self.resolution == o.resolution

    def __ne__(self, o: object) -> bool:
        return not self == o


class GeocodingRequest(Request):

    @staticmethod
    def _check_required_parameters(region_queries: List[RegionQuery],
                                   level: Optional[LevelKind]) -> None:

        if len(region_queries) == 0 and not level:
            raise ValueError(MISSING_LEVEL_AND_SCOPE_OR_REQUEST_EXCEPTION_TEXT)

        for query in region_queries:
            if not query.request and not level and not query.scope:
                raise ValueError(MISSING_LEVEL_AND_SCOPE_OR_REQUEST_EXCEPTION_TEXT)

            if not query.request and not level and query.scope:
                raise ValueError(MISSING_LEVEL_OR_REQUEST_EXCEPTION_TEXT)

            if not query.request and not level and not query.scope:
                raise ValueError(MISSING_SCOPE_OR_REQUEST_EXCEPTION_TEXT)

    def __init__(self,
                 requested_payload: List[PayloadKind],
                 resolution: Optional[int],
                 region_queries: List[RegionQuery],
                 scope: List[MapRegion],
                 level: Optional[LevelKind],
                 namesake_example_limit: int,
                 allow_ambiguous: bool
                 ):
        super().__init__(requested_payload, resolution)
        assert_list_type(requested_payload, PayloadKind)
        assert_optional_type(resolution, int)
        assert_list_type(region_queries, RegionQuery)
        assert_optional_type(level, LevelKind)
        assert_type(namesake_example_limit, int)
        assert_type(allow_ambiguous, bool)

        self._check_required_parameters(region_queries, level)

        assert region_queries is not None
        assert namesake_example_limit is not None

        self.region_queries: List[RegionQuery] = region_queries
        self.scope: List[MapRegion] = scope
        self.level: Optional[LevelKind] = level
        self.namesake_example_limit: int = namesake_example_limit
        self.allow_ambiguous: bool = allow_ambiguous

    def __eq__(self, o: object) -> bool:
        return isinstance(o, GeocodingRequest) \
               and super().__eq__(o) \
               and self.region_queries == o.region_queries \
               and self.level == o.level \
               and self.namesake_example_limit == o.namesake_example_limit \
               and self.allow_ambiguous == o.allow_ambiguous

    def __ne__(self, o: object) -> bool:
        return not self == o


class ExplicitRequest(Request):
    def __init__(self,
                 requested_payload: List[PayloadKind],
                 ids: List[GeoId],
                 resolution: Optional[int] = None
                 ):
        super().__init__(requested_payload, resolution)

        assert_list_type(requested_payload, PayloadKind)
        assert_list_type(ids, GeoId)
        assert_optional_type(resolution, int)

        assert ids is not None
        self.geo_object_list: List[GeoId] = ids

    def __eq__(self, o: object) -> bool:
        return isinstance(o, ExplicitRequest) \
               and super().__eq__(o) \
               and self.geo_object_list == o.geo_object_list

    def __ne__(self, o: object) -> bool:
        return not self == o


class ReverseGeocodingRequest(Request):
    def __init__(self,
                 coordinates: List[GeoPoint],
                 level: LevelKind,
                 scope: Optional[MapRegion],
                 requested_payload: List[PayloadKind],
                 resolution: Optional[int] = None
                 ):
        super().__init__(requested_payload, resolution)
        assert_list_type(coordinates, GeoPoint)
        assert_type(level, LevelKind)
        assert_optional_type(scope, MapRegion)

        self.coordinates: List[GeoPoint] = coordinates
        self.level: LevelKind = level
        self.scope: Optional[MapRegion] = scope

    def __eq__(self, o: object) -> bool:
        return isinstance(o, ReverseGeocodingRequest) \
               and super().__eq__(o) \
               and self.coordinates == o.coordinates \
               and self.level == o.level \
               and self.scope == o.scope

    def __ne__(self, o: object) -> bool:
        return not self == o


class RequestBuilder:
    def __init__(self):
        self.request_kind: Optional[RequestKind] = None
        self.requested_payload: List[PayloadKind] = []
        self.resolution: Optional[int] = None
        self.ids: List[str] = []
        self.region_queries: List[RegionQuery] = []
        self.scope: List[MapRegion] = []
        self.level: Optional[LevelKind] = None
        self.namesake_limit: int = 10
        self.allow_ambiguous: bool = False

        # reverse
        self.reverse_coordinates: Optional[List[GeoPoint]] = None
        self.reverse_scope: Optional[MapRegion] = None

    def set_reverse_coordinates(self, coordinates: List[GeoPoint]) -> 'RequestBuilder':
        assert_list_type(coordinates, GeoPoint)
        self.reverse_coordinates = coordinates
        return self

    def set_reverse_scope(self, region: Optional[MapRegion]) -> 'RequestBuilder':
        assert_optional_type(region, MapRegion)
        self.reverse_scope = region
        return self

    def set_request_kind(self, v: RequestKind) -> 'RequestBuilder':
        assert_type(v, RequestKind)
        self.request_kind = v
        return self

    def set_requested_payload(self, v: List[PayloadKind]) -> 'RequestBuilder':
        assert_list_type(v, PayloadKind)
        self.requested_payload = v
        return self

    def set_resolution(self, v: Optional[int]) -> 'RequestBuilder':
        assert_optional_type(v, int)
        self.resolution = v
        return self

    def set_ids(self, v: List[str]) -> 'RequestBuilder':
        assert_list_type(v, str)
        self.ids = v
        return self

    def set_queries(self, v: List[RegionQuery]) -> 'RequestBuilder':
        assert_list_type(v, RegionQuery)
        self.region_queries = v
        return self

    def set_scope(self, v: List[MapRegion]) -> 'RequestBuilder':
        assert_list_type(v, MapRegion)
        self.scope = v
        return self

    def set_level(self, v: LevelKind) -> 'RequestBuilder':
        assert_optional_type(v, LevelKind)
        self.level = v
        return self

    def set_namesake_limit(self, v: int) -> 'RequestBuilder':
        assert_optional_type(v, int)
        self.namesake_limit = v
        return self

    def set_allow_ambiguous(self, v: bool) -> 'RequestBuilder':
        assert_optional_type(v, bool)
        self.allow_ambiguous = v
        return self

    def build(self) -> Union[ExplicitRequest, GeocodingRequest, ReverseGeocodingRequest]:
        if self.request_kind == RequestKind.explicit:
            return ExplicitRequest(self.requested_payload, self.ids, self.resolution)

        elif self.request_kind == RequestKind.geocoding:
            return GeocodingRequest(self.requested_payload, self.resolution, self.region_queries, self.scope,
                                    self.level, self.namesake_limit, self.allow_ambiguous)

        elif self.request_kind == RequestKind.reverse:
            assert self.reverse_coordinates is not None
            assert self.level is not None
            return ReverseGeocodingRequest(self.reverse_coordinates, self.level, self.reverse_scope,
                                           self.requested_payload, self.resolution)

        else:
            raise ValueError('Unknown mode: ' + str(self.request_kind))


class MapRegionBuilder:
    def __init__(self):
        self.parent_kind: Optional[bool] = None
        self.parent_values: List[str] = []

    def set_parent_kind(self, kind: bool) -> 'MapRegionBuilder':
        self.parent_kind = kind
        return self

    def set_parent_values(self, values: List[str]) -> 'MapRegionBuilder':
        self.parent_values = values
        return self

    def build(self) -> Optional[MapRegion]:
        if self.parent_kind is not None:
            return MapRegion(MapRegionKind(self.parent_kind), self.parent_values)
        else:
            return None


class RegionQueryBuilder:
    def __init__(self):
        self.request: Optional[str] = None
        self.scope: Optional[MapRegion] = None
        self.ignoring_strategy: Optional[IgnoringStrategyKind] = None
        self.closest_coord: Optional[GeoPoint] = None
        self.box: Optional[GeoRect] = None

    def set_request(self, request: Optional[str]) -> 'RegionQueryBuilder':
        assert_optional_type(request, str)
        self.request = request
        return self

    def set_scope(self, parent: Optional[MapRegion]) -> 'RegionQueryBuilder':
        assert_optional_type(parent, MapRegion)
        self.scope = parent
        return self

    def set_ignoring_strategy(self, ignoring_strategy: IgnoringStrategyKind):
        assert_type(ignoring_strategy, IgnoringStrategyKind)
        self.ignoring_strategy = ignoring_strategy
        return self

    def set_closest_coord(self, closest_coord: Optional[GeoPoint]):
        assert_optional_type(closest_coord, GeoPoint)
        self.closest_coord = closest_coord
        return self

    def set_box(self, box: Optional[GeoRect]):
        assert_optional_type(box, GeoRect)
        self.box = box
        return self

    def build(self) -> RegionQuery:
        return RegionQuery(self.request, self.scope, self._build_ambiguity_resolver())

    def _build_ambiguity_resolver(self) -> AmbiguityResolver:
        if self.ignoring_strategy is not None \
                or self.closest_coord is not None \
                or self.box is not None:
            return AmbiguityResolver(self.ignoring_strategy, self.closest_coord, self.box)
        else:
            return AmbiguityResolver.empty()
