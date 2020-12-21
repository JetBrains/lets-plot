#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
from collections import namedtuple
from typing import Union, List, Optional, Dict
from pandas import Series

from .type_assertion import assert_list_type
from .gis.geocoding_service import GeocodingService
from .gis.geometry import GeoRect, GeoPoint
from .gis.request import RequestBuilder, GeocodingRequest, RequestKind, MapRegion, AmbiguityResolver, \
    RegionQuery, LevelKind, IgnoringStrategyKind, PayloadKind, ReverseGeocodingRequest
from .gis.response import Response, SuccessResponse
from .geocodes import _to_level_kind, request_types, Geocodes, _raise_exception, \
    _ensure_is_list

__all__ = [
    'geocode',
    'regions_builder2',
    'geocode_cities',
    'geocode_counties',
    'geocode_states',
    'geocode_countries',
    'reverse_geocode'
]

NAMESAKE_MAX_COUNT = 10

ShapelyPointType = 'shapely.geometry.Point'
ShapelyPolygonType = 'shapely.geometry.Polygon'

QuerySpec = namedtuple('QuerySpec', 'name, county, state, country')
WhereSpec = namedtuple('WithinSpec', 'scope, ambiguity_resolver')

parent_types = Optional[Union[str, Geocodes, 'Geocoder', MapRegion, List]] # list of same types
scope_types = Optional[Union[str, List[str], Geocodes, 'Geocoder', List[Geocodes]]]


def _to_scope(location: scope_types) -> Optional[Union[List[MapRegion], MapRegion]]:
    if location is None:
        return None

    def _make_region(obj: Union[str, Geocodes]) -> Optional[MapRegion]:
        if isinstance(obj, Geocodes):
            return MapRegion.scope(obj.unique_ids())

        if isinstance(obj, str):
            return MapRegion.with_name(obj)

        raise ValueError('Unsupported scope type. Expected Geocoder, str or list, but was `{}`'.format(type(obj)))

    if isinstance(location, list):
        return [_make_region(obj) for obj in location]

    return _make_region(location)


class LazyShapely:
    @staticmethod
    def is_point(p) -> bool:
        if not LazyShapely._is_shapely_available():
            return False

        from shapely.geometry import Point
        return isinstance(p, Point)

    @staticmethod
    def is_polygon(p):
        if not LazyShapely._is_shapely_available():
            return False

        from shapely.geometry import Polygon
        return isinstance(p, Polygon)

    @staticmethod
    def _is_shapely_available():
        try:
            import shapely
            return True
        except:
            return False


def _make_ambiguity_resolver(ignoring_strategy: Optional[IgnoringStrategyKind] = None,
                             within: ShapelyPolygonType = None,
                             near: Optional[Union[Geocodes, ShapelyPointType]] = None):
    box = None
    if LazyShapely.is_polygon(within):
        box = GeoRect(min_lon=within.bounds[0], min_lat=within.bounds[1], max_lon=within.bounds[2], max_lat=within.bounds[3])

    near = _to_near_coord(near)

    return AmbiguityResolver(
        ignoring_strategy=ignoring_strategy,
        closest_coord=near,
        box=box
    )


def _to_near_coord(near: Optional[Union[Geocodes, ShapelyPointType]]) -> Optional[GeoPoint]:
    if near is None:
        return None

    if isinstance(near, Geocoder):
        near = near._get_geocodes_obj()

    if isinstance(near, Geocodes):
        near_id = near.as_list()[0].unique_ids()
        assert len(near_id) == 1

        request = RequestBuilder() \
            .set_request_kind(RequestKind.explicit) \
            .set_requested_payload([PayloadKind.centroids]) \
            .set_ids(near_id) \
            .build()

        response: Response = GeocodingService().do_request(request)
        if isinstance(response, SuccessResponse):
            assert len(response.features) == 1
            centroid = response.features[0].centroid
            return GeoPoint(lon=centroid.lon, lat=centroid.lat)
        else:
            raise ValueError("Unexpected geocoding response for id " + str(near_id[0]))

    if LazyShapely.is_point(near):
        return GeoPoint(lon=near.x, lat=near.y)

    raise ValueError('Not supported type: {}'.format(type(near)))


def _get_or_none(list, index):
    if index >= len(list):
        return None
    return list[index]


def _ensure_is_parent_list(obj):
    if obj is None:
        return None

    if isinstance(obj, Geocoder):
        obj = obj._get_geocodes_obj()

    if isinstance(obj, Geocodes):
        return obj.as_list()

    if isinstance(obj, list):
        return obj

    return [obj]


def _make_parents(values: parent_types) -> List[Optional[MapRegion]]:
    values = _ensure_is_parent_list(values)

    if values is None:
        return []

    return list(map(lambda v: _make_parent_region(v) if values is not None else None, values))


def _make_parent_region(place: parent_types) -> Optional[MapRegion]:
    if place is None:
        return None

    if isinstance(place, Geocoder):
        place = place._get_geocodes_obj()

    if isinstance(place, str):
        return MapRegion.with_name(place)

    if isinstance(place, Geocodes):
        assert len(place.to_map_regions()) == 1, 'Region object used as parent should contain only single record'
        return place.to_map_regions()[0]

    raise ValueError('Unsupported parent type: ' + str(type(place)))


class Geocoder:
    def _get_geocodes(self) -> Geocodes:
        raise ValueError('Abstract method')

    def get_limits(self) -> 'GeoDataFrame':
        return self._get_geocodes().limits()

    def get_centroids(self) -> 'GeoDataFrame':
        return self._get_geocodes().centroids()

    def get_boundaries(self, resolution=None) -> 'GeoDataFrame':
        return self._get_geocodes().boundaries(resolution)

    def get_geocodes(self) -> 'DataFrame':
        return self._get_geocodes().to_data_frame()

    def _get_geocodes_obj(self) -> Geocodes:
        return self._get_geocodes()


def _to_coords(lon: Optional[Union[float, Series, List[float]]], lat: Optional[Union[float, Series, List[float]]]) -> List[GeoPoint]:
    if type(lon) != type(lat):
        raise ValueError('lon and lat have different types')

    if isinstance(lon, float):
        return [GeoPoint(lon, lat)]

    if isinstance(lon, Series):
        lon = lon.tolist()
        lat = lat.tolist()

    if isinstance(lon, list):
        assert_list_type(lon, float)
        assert_list_type(lat, float)
        return [GeoPoint(lo, la) for lo, la in zip(lon, lat)]


class ReverseGeocoder(Geocoder):
    def __init__(self, lon, lat, level: Optional[Union[str, LevelKind]], within=None):
        self._geocodes: Optional[Geocodes] = None
        self._request: ReverseGeocodingRequest = RequestBuilder() \
            .set_request_kind(RequestKind.reverse) \
            .set_reverse_coordinates(_to_coords(lon, lat)) \
            .set_level(_to_level_kind(level)) \
            .set_reverse_scope(_to_scope(within)) \
            .build()

    def _get_geocodes(self) -> Geocodes:
        if self._geocodes is None:
            self._geocodes = self._build()

        return self._geocodes

    def _build(self):
        response: Response = GeocodingService().do_request(self._request)

        if not isinstance(response, SuccessResponse):
            _raise_exception(response)

        return Geocodes(
            response.level,
            response.answers,
            [
                RegionQuery(request='[{}, {}]'.format(pt.lon, pt.lat)) for pt in self._request.coordinates
            ],
            False
        )



class NamesGeocoder(Geocoder):
    def __init__(self,
                 level: Optional[Union[str, LevelKind]] = None,
                 request: request_types = None
                 ):
        self._geocodes: Optional[Geocodes] = None
        self._scope: List[Optional[MapRegion]] = []
        self._level: Optional[LevelKind] = _to_level_kind(level)
        self._default_ambiguity_resolver: AmbiguityResolver = AmbiguityResolver.empty()  # TODO rename to geohint
        self._highlights: bool = False
        self._allow_ambiguous = False
        self._countries: List[Optional[MapRegion]] = []
        self._states: List[Optional[MapRegion]] = []
        self._counties: List[Optional[MapRegion]] = []
        self._overridings: Dict[QuerySpec, WhereSpec] = {}  # query to scope

        requests: Optional[List[str]] = _ensure_is_list(request)
        if requests is not None:
            self._names: List[Optional[str]] = list(map(lambda name: name if requests is not None else None, requests))
        else:
            self._names = []

    def scope(self, scope: scope_types) -> 'NamesGeocoder':
        self._reset_geocodes()
        self._scope = _prepare_new_scope(scope)
        return self

    def highlights(self, v: bool) -> 'NamesGeocoder':
        self._highlights = v
        return self

    def countries(self, countries: parent_types) -> 'NamesGeocoder':
        self._reset_geocodes()
        self._countries = _make_parents(countries)
        return self

    def states(self, states: parent_types) -> 'NamesGeocoder':
        self._reset_geocodes()
        self._states = _make_parents(states)
        return self

    def counties(self, counties: parent_types) -> 'NamesGeocoder':
        self._reset_geocodes()
        self._counties = _make_parents(counties)
        return self

    def drop_not_found(self) -> 'NamesGeocoder':
        self._reset_geocodes()
        self._default_ambiguity_resolver = AmbiguityResolver(IgnoringStrategyKind.skip_missing)
        return self

    def drop_not_matched(self) -> 'NamesGeocoder':
        self._reset_geocodes()
        self._default_ambiguity_resolver = AmbiguityResolver(IgnoringStrategyKind.skip_all)
        return self

    def allow_ambiguous(self) -> 'NamesGeocoder':
        self._reset_geocodes()
        self._default_ambiguity_resolver = AmbiguityResolver(IgnoringStrategyKind.take_namesakes)
        self._allow_ambiguous = True
        return self

    def where(self, name: str,
              county: Optional[parent_types] = None,
              state: Optional[parent_types] = None,
              country: Optional[parent_types] = None,
              scope: scope_types = None,
              within: ShapelyPolygonType = None,
              near: Optional[Union[Geocodes, ShapelyPointType]] = None
              ) -> 'NamesGeocoder':
        """
        If name is not exist - error will be generated.
        If name is exist in the Geocoder - specify extra parameters for geocoding.


        Parameters
        ----------
        name : string
            Name in Geocoder that needs better qualificationfrom request Data can be filtered by full names at any level (only exact matching).
        county : [string | None]
            When Geocoder built with parents this field is used to identify a row for the name
        state : [string | None]
            When Geocoder built with parents this field is used to identify a row for the name
        country : [string | None]
            When Geocoder built with parents this field is used to identify a row for the name
        scope : [string | Geocoder | None]
            Resolve ambiguity by setting scope as parent. If parent country is set then error will be shown.
             If type is string - scope will be geocoded and used as parent.
             If type is Geocoder  - scope will be used as parent.
        within : [shapely.Polygon | None]
            Resolve ambihuity by limiting area in which centroid be located.
        near: [Geocoder | shapely.geometry.Point | None]
            Resolve ambiguity by taking object closest to a 'near' object.

        Returns
        -------
            Geocoder object
        """
        self._reset_geocodes()
        query_spec = QuerySpec(
            name,
            _make_parent_region(county),
            _make_parent_region(state),
            _make_parent_region(country)
        )

        def query_exist(query):
            for i in range(len(self._names)):
                if query.name == self._names[i] and \
                        query.country == _get_or_none(self._countries, i) and \
                        query.state == _get_or_none(self._states, i) and \
                        query.county == _get_or_none(self._counties, i):
                    return True
            return False

        if not query_exist(query_spec):
            parents: List[str] = []
            if query_spec.county is not None:
                parents.append('county={}'.format(str(query_spec.county)))

            if query_spec.state is not None:
                parents.append('state={}'.format(str(query_spec.state)))

            if query_spec.country is not None:
                parents.append('country={}'.format(str(query_spec.country)))

            parents_str = ", ".join(parents)
            if len(parents_str) == 0:
                raise ValueError("{} is not found in names".format(name))
            else:
                raise ValueError("{}({}) is not found in names".format(name, parents_str))

        if scope is None:
            new_scope = None
        else:
            new_scopes: List[MapRegion] = _prepare_new_scope(scope)
            if len(new_scopes) != 1:
                raise ValueError(
                    'Invalid request: where functions scope should have length of 1, but was {}'.format(len(new_scopes)))
            new_scope = new_scopes[0]

        ambiguity_resolver = _make_ambiguity_resolver(within=within, near=near)

        self._overridings[query_spec] = WhereSpec(new_scope, ambiguity_resolver)
        return self


    def _build_request(self) -> GeocodingRequest:
        if len(self._names) == 0:
            def to_scope(parents):
                if len(parents) == 0:
                    return None
                elif len(parents) == 1:
                    return parents[0]
                else:
                    raise ValueError('Too many parent objects. Expcted single object instead of {}'.format(len(parents)))

            # all countries/states etc. We need one dummy query
            queries = [
                RegionQuery(
                    request=None,
                    country=to_scope(self._countries),
                    state=to_scope(self._states),
                    county=to_scope(self._counties)
                )
            ]
        else:
            def _validate_parents_size(parents: List, parents_level: str):
                if len(parents) > 0 and len(parents) != len(self._names):
                    raise ValueError('Invalid request: {} count({}) != names count({})'
                                     .format(parents_level, len(parents), len(self._names)))

            _validate_parents_size(self._countries, 'countries')
            _validate_parents_size(self._states, 'states')
            _validate_parents_size(self._counties, 'counties')

            if len(self._scope) > 0 and (len(self._countries) + len(self._states) + len(self._counties)) > 0:
                raise ValueError("Invalid request: parents and scope can't be used simultaneously")

            queries = []
            for i in range(len(self._names)):
                name = self._names[i]
                country = _get_or_none(self._countries, i)
                state = _get_or_none(self._states, i)
                county = _get_or_none(self._counties, i)

                scope, ambiguity_resolver = self._overridings.get(
                    QuerySpec(name, county, state, country),
                    WhereSpec(None, self._default_ambiguity_resolver)
                )

                query = RegionQuery(
                    request=name,
                    country=country,
                    state=state,
                    county=county,
                    scope=scope,
                    ambiguity_resolver=ambiguity_resolver
                )

                queries.append(query)

        request = RequestBuilder() \
            .set_request_kind(RequestKind.geocoding) \
            .set_requested_payload([PayloadKind.highlights] if self._highlights else []) \
            .set_queries(queries) \
            .set_scope(self._scope) \
            .set_level(self._level) \
            .set_namesake_limit(NAMESAKE_MAX_COUNT) \
            .set_allow_ambiguous(self._allow_ambiguous) \
            .build()

        return request

    def _build_regions(self, response: Response, queries: List[RegionQuery]) -> Geocodes:
        if not isinstance(response, SuccessResponse):
            _raise_exception(response)

        return Geocodes(response.level, response.answers, queries, self._highlights)

    def _build(self) -> Geocodes:
        request: GeocodingRequest = self._build_request()

        response: Response = GeocodingService().do_request(request)

        return self._build_regions(response, request.region_queries)

    def _get_geocodes(self) -> Geocodes:
        if self._geocodes is None:
            self._geocodes = self._build()

        return self._geocodes

    def _reset_geocodes(self):
        self._geocodes = None

    def __eq__(self, o):
        return isinstance(o, NamesGeocoder) \
               and self._overridings == o._overridings

    def __ne__(self, o):
        return not self == o


def _prepare_new_scope(scope: Optional[Union[str, Geocoder, Geocodes, MapRegion, List]]) -> List[MapRegion]:
    """
    Return list of MapRegions. Every MapRegion object contains only one name or id.
    """
    if scope is None:
        return []

    if isinstance(scope, Geocoder):
        scope = scope._get_geocodes_obj()

    if isinstance(scope, str):
        return [MapRegion.with_name(scope)]

    if isinstance(scope, Geocodes):
        return scope.to_map_regions()

    if isinstance(scope, (list, tuple)):
        if all(map(lambda v: isinstance(v, str), scope)):
            return [MapRegion.with_name(name) for name in scope]
        if all(map(lambda v: isinstance(v, Geocodes), scope)):
            return [map_region for region in scope for map_region in region.to_map_regions()]
        else:
            raise ValueError('Iterable scope can contain str or Geocoder.')


def regions_builder2(level=None, names=None, countries=None, states=None, counties=None, scope=None,
                     highlights=False) -> NamesGeocoder:
    """
    Create a RegionBuilder class by level and request. Allows to refine ambiguous request with
    where method. build() method creates Geocoder object or shows details for ambiguous result.

    regions_builder(level, request, within)

    Parameters
    ----------
    level : ['country' | 'state' | 'county' | 'city' | None]
        The level of administrative division. Default is a 'state'.
    names : [array | string | None]
        Names of objects to be geocoded.
        For 'state' level:
        -'US-48' returns continental part of United States (48 states) in a compact form.
    countries : [array | None]
        Parent countries. Should have same size as names. Can contain strings or Geocoder objects.
    states : [array | None]
        Parent states. Should have same size as names. Can contain strings or Geocoder objects.
    counties : [array | None]
        Parent counties. Should have same size as names. Can contain strings or Geocoder objects.
    scope : [array | string | Geocoder | None]
        Limits area of geocoding. Applyed to a highest admin level of parents that are set or to names, if no parents given.
        If all parents are set (including countries) then the scope parameter is ignored.
        If scope is an array then geocoding will try to search objects in all scopes.

    Returns
    -------
    Geocoder object :

    Note
    -----
    regions_builder() allows to refine ambiguous request with where() method. Call build() method to create Geocoder object

    Examples
    ---------
    >>> from lets_plot.geo_data import *
    >>> r = regions_builder2(level='city', names=['moscow', 'york']).where('york', regions_state('New York')).build()
    """
    return NamesGeocoder(level, names) \
        .scope(scope) \
        .highlights(highlights) \
        .countries(countries) \
        .states(states) \
        .counties(counties)


def geocode(level=None, names=None, countries=None, states=None, counties=None, scope=None) -> NamesGeocoder:
    """
    Returns regions object.

    Parameters
    ----------
    level : ['country' | 'state' | 'county' | 'city' | None]
        The level of administrative division. Autodetection by default.
    names : [array | string | None]
        Names of objects to be geocoded.
        For 'state' level:
        -'US-48' returns continental part of United States (48 states) in a compact form.
    countries : [array | None]
        Parent countries. Should have same size as names. Can contain strings or Geocoder objects.
    states : [array | None]
        Parent states. Should have same size as names. Can contain strings or Geocoder objects.
    counties : [array | None]
        Parent counties. Should have same size as names. Can contain strings or Geocoder objects.
    scope : [array | string | Geocoder | None]
        Limits area of geocoding. Applyed to a highest admin level of parents that are set or to names, if no parents given.
        If all parents are set (including countries) then the scope parameter is ignored.
        If scope is an array then geocoding will try to search objects in all scopes.
    """
    return regions_builder2(level, names, countries, states, counties, scope)


def geocode_cities(names=None) -> NamesGeocoder:
    """
    Create a RegionBuilder object for cities. Allows to refine ambiguous request with
    where method. build() method creates Geocoder object or shows details for ambiguous result.

    regions_builder(level, request, within)

    Parameters
    ----------
    names : [array | string | None]
        Names of objects to be geocoded.

    Returns
    -------
    Geocoder object :

    Note
    -----
    regions_builder() allows to refine ambiguous request with where() method. Call build() method to create Geocoder object

    Examples
    ---------
    >>> from lets_plot.geo_data import *
    >>> r = geocode_cities(names=['moscow', 'york']).where('york', regions_state('New York')).get_geocodes()
    """
    return NamesGeocoder('city', names)


def geocode_counties(names=None) -> NamesGeocoder:
    """
    Create a RegionBuilder object for counties. Allows to refine ambiguous request with
    where method. build() method creates Geocoder object or shows details for ambiguous result.

    regions_builder(level, request, within)

    Parameters
    ----------
    names : [array | string | None]
        Names of objects to be geocoded.

    Returns
    -------
    Geocoder object :

    Note
    -----
    regions_builder() allows to refine ambiguous request with where() method. Call build() method to create Geocoder object

    Examples
    ---------
    >>> from lets_plot.geo_data import *
    >>> r = geocode_counties(names='suffolk').get_geocodes()
    """
    return NamesGeocoder('county', names)


def geocode_states(names=None) -> NamesGeocoder:
    """
    Create a RegionBuilder object for states. Allows to refine ambiguous request with
    where method. build() method creates Geocoder object or shows details for ambiguous result.

    regions_builder(level, request, within)

    Parameters
    ----------
    names : [array | string | None]
        Names of objects to be geocoded.

    Returns
    -------
    Geocoder object :

    Note
    -----
    regions_builder() allows to refine ambiguous request with where() method. Call build() method to create Geocoder object

    Examples
    ---------
    >>> from lets_plot.geo_data import *
    >>> r = geocode_states(names='texas').get_geocodes()
    """
    return NamesGeocoder('state', names)


def geocode_countries(names=None) -> NamesGeocoder:
    """
    Create a RegionBuilder object for countries. Allows to refine ambiguous request with
    where method. build() method creates Geocoder object or shows details for ambiguous result.

    regions_builder(level, request, within)

    Parameters
    ----------
    names : [array | string | None]
        Names of objects to be geocoded.

    Returns
    -------
    Geocoder object :

    Note
    -----
    regions_builder() allows to refine ambiguous request with where() method. Call build() method to create Geocoder object

    Examples
    ---------
    >>> from lets_plot.geo_data import *
    >>> r = geocode_countries(names='USA').get_geocodes()
    """
    return NamesGeocoder('country', names)

def reverse_geocode(lon, lat, level=None, scope=None) -> ReverseGeocoder:
    return ReverseGeocoder(lon, lat, level, scope)