#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
from collections import namedtuple
from typing import Union, List, Optional, Dict

from .gis.geocoding_service import GeocodingService
from .gis.request import RequestBuilder, GeocodingRequest, RequestKind, MapRegion, AmbiguityResolver, \
    RegionQuery, LevelKind, IgnoringStrategyKind, PayloadKind
from .gis.response import Response, SuccessResponse
from .regions import _make_parent_region
from .regions import _to_level_kind, request_types, parent_types, scope_types, Regions, _raise_exception, \
    _ensure_is_list
from .regions_builder import NAMESAKE_MAX_COUNT, ShapelyPointType, ShapelyPolygonType, _make_ambiguity_resolver

__all__ = [
    'regions2', 'regions_builder2', 'city_regions_builder', 'county_regions_builder', 'state_regions_builder',
    'country_regions_builder'
]

QuerySpec = namedtuple('QuerySpec', 'name, county, state, country')
WhereSpec = namedtuple('WithinSpec', 'scope, ambiguity_resolver')


def _get_or_none(list, index):
    if index >= len(list):
        return None
    return list[index]


def _ensure_is_parent_list(obj):
    if obj is None:
        return None

    if isinstance(obj, Regions):
        return obj.as_list()

    if isinstance(obj, list):
        return obj

    return [obj]


def _make_parents(values: parent_types) -> List[Optional[MapRegion]]:
    values = _ensure_is_parent_list(values)

    if values is None:
        return []

    return list(map(lambda v: _make_parent_region(v) if values is not None else None, values))


class RegionsBuilder2:
    def __init__(self,
                 level: Optional[Union[str, LevelKind]] = None,
                 request: request_types = None
                 ):

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

    def scope(self, scope: scope_types) -> 'RegionsBuilder2':
        self._scope = _prepare_new_scope(scope)
        return self

    def highlights(self, v: bool) -> 'RegionsBuilder2':
        self._highlights = v
        return self

    def countries(self, countries: parent_types) -> 'RegionsBuilder2':
        self._countries = _make_parents(countries)
        return self

    def states(self, states: parent_types) -> 'RegionsBuilder2':
        self._states = _make_parents(states)
        return self

    def counties(self, counties: parent_types) -> 'RegionsBuilder2':
        self._counties = _make_parents(counties)
        return self

    def drop_not_found(self) -> 'RegionsBuilder2':
        self._default_ambiguity_resolver = AmbiguityResolver(IgnoringStrategyKind.skip_missing)
        return self

    def drop_not_matched(self) -> 'RegionsBuilder2':
        self._default_ambiguity_resolver = AmbiguityResolver(IgnoringStrategyKind.skip_all)
        return self

    def allow_ambiguous(self) -> 'RegionsBuilder2':
        self._default_ambiguity_resolver = AmbiguityResolver(IgnoringStrategyKind.take_namesakes)
        self._allow_ambiguous = True
        return self

    def where(self, name: str,
              county: Optional[parent_types] = None,
              state: Optional[parent_types] = None,
              country: Optional[parent_types] = None,
              scope: scope_types = None,
              within: ShapelyPolygonType = None,
              near: Optional[Union[Regions, ShapelyPointType]] = None
              ):
        """
        If name is not exist - error will be generated.
        If name is exist in the RegionsBuilder - specify extra parameters for geocoding.


        Parameters
        ----------
        name : string
            Name in RegionsBuilder that needs better qualificationfrom request Data can be filtered by full names at any level (only exact matching).
        county : [string | None]
            When RegionsBuilder built with parents this field is used to identify a row for the name
        state : [string | None]
            When RegionsBuilder built with parents this field is used to identify a row for the name
        country : [string | None]
            When RegionsBuilder built with parents this field is used to identify a row for the name
        scope : [string | Regions | None]
            Resolve ambiguity by setting scope as parent. If parent country is set then error will be shown.
             If type is string - scope will be geocoded and used as parent.
             If type is Regions  - scope will be used as parent.
        within : [shapely.Polygon | None]
            Resolve ambihuity by limiting area in which centroid be located.
        near: [Regions | shapely.geometry.Point | None]
            Resolve ambiguity by taking object closest to a 'near' object.

        Returns
        -------
            RegionsBuilder object
        """
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
            def to_scope(regions):
                if len(regions) == 0:
                    return None
                elif len(regions) == 1:
                    return regions[0]
                else:
                    raise ValueError('Too many parent objects. Expcted single object instead of {}'.format(len(regions)))

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

    def _build_regions(self, response: Response, queries: List[RegionQuery]) -> Regions:
        if not isinstance(response, SuccessResponse):
            _raise_exception(response)

        return Regions(response.level, response.answers, queries, self._highlights)

    def build(self) -> Regions:
        request: GeocodingRequest = self._build_request()

        response: Response = GeocodingService().do_request(request)

        return self._build_regions(response, request.region_queries)

    def __eq__(self, o):
        return isinstance(o, RegionsBuilder2) \
               and self._overridings == o._overridings

    def __ne__(self, o):
        return not self == o


def _prepare_new_scope(scope: Optional[Union[str, Regions, MapRegion, List]]) -> List[MapRegion]:
    """
    Return list of MapRegions. Every MapRegion object contains only one name or id.
    """
    if scope is None:
        return []

    if isinstance(scope, str):
        return [MapRegion.with_name(scope)]

    if isinstance(scope, Regions):
        return scope.to_map_regions()

    if isinstance(scope, (list, tuple)):
        if all(map(lambda v: isinstance(v, str), scope)):
            return [MapRegion.with_name(name) for name in scope]
        if all(map(lambda v: isinstance(v, Regions), scope)):
            return [map_region for region in scope for map_region in region.to_map_regions()]
        else:
            raise ValueError('Iterable scope can contain str or Regions.')


def regions_builder2(level=None, names=None, countries=None, states=None, counties=None, scope=None,
                     highlights=False) -> RegionsBuilder2:
    """
    Create a RegionBuilder class by level and request. Allows to refine ambiguous request with
    where method. build() method creates Regions object or shows details for ambiguous result.

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
        Parent countries. Should have same size as names. Can contain strings or Regions objects.
    states : [array | None]
        Parent states. Should have same size as names. Can contain strings or Regions objects.
    counties : [array | None]
        Parent counties. Should have same size as names. Can contain strings or Regions objects.
    scope : [array | string | Regions | None]
        Limits area of geocoding. Applyed to a highest admin level of parents that are set or to names, if no parents given.
        If all parents are set (including countries) then the scope parameter is ignored.
        If scope is an array then geocoding will try to search objects in all scopes.

    Returns
    -------
    RegionsBuilder object :

    Note
    -----
    regions_builder() allows to refine ambiguous request with where() method. Call build() method to create Regions object

    Examples
    ---------
    >>> from lets_plot.geo_data import *
    >>> r = regions_builder2(level='city', names=['moscow', 'york']).where('york', regions_state('New York')).build()
    """
    return RegionsBuilder2(level, names) \
        .scope(scope) \
        .highlights(highlights) \
        .countries(countries) \
        .states(states) \
        .counties(counties)


def regions2(level=None, names=None, countries=None, states=None, counties=None, scope=None) -> Regions:
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
        Parent countries. Should have same size as names. Can contain strings or Regions objects.
    states : [array | None]
        Parent states. Should have same size as names. Can contain strings or Regions objects.
    counties : [array | None]
        Parent counties. Should have same size as names. Can contain strings or Regions objects.
    scope : [array | string | Regions | None]
        Limits area of geocoding. Applyed to a highest admin level of parents that are set or to names, if no parents given.
        If all parents are set (including countries) then the scope parameter is ignored.
        If scope is an array then geocoding will try to search objects in all scopes.
    """
    return regions_builder2(level, names, countries, states, counties, scope).build()


def city_regions_builder(names=None) -> RegionsBuilder2:
    """
    Create a RegionBuilder object for cities. Allows to refine ambiguous request with
    where method. build() method creates Regions object or shows details for ambiguous result.

    regions_builder(level, request, within)

    Parameters
    ----------
    names : [array | string | None]
        Names of objects to be geocoded.

    Returns
    -------
    RegionsBuilder object :

    Note
    -----
    regions_builder() allows to refine ambiguous request with where() method. Call build() method to create Regions object

    Examples
    ---------
    >>> from lets_plot.geo_data import *
    >>> r = city_regions_builder(names=['moscow', 'york']).where('york', regions_state('New York')).build()
    """
    return RegionsBuilder2('city', names)


def county_regions_builder(names=None) -> RegionsBuilder2:
    """
    Create a RegionBuilder object for counties. Allows to refine ambiguous request with
    where method. build() method creates Regions object or shows details for ambiguous result.

    regions_builder(level, request, within)

    Parameters
    ----------
    names : [array | string | None]
        Names of objects to be geocoded.

    Returns
    -------
    RegionsBuilder object :

    Note
    -----
    regions_builder() allows to refine ambiguous request with where() method. Call build() method to create Regions object

    Examples
    ---------
    >>> from lets_plot.geo_data import *
    >>> r = county_regions_builder(names='suffolk').build()
    """
    return RegionsBuilder2('county', names)


def state_regions_builder(names=None) -> RegionsBuilder2:
    """
    Create a RegionBuilder object for states. Allows to refine ambiguous request with
    where method. build() method creates Regions object or shows details for ambiguous result.

    regions_builder(level, request, within)

    Parameters
    ----------
    names : [array | string | None]
        Names of objects to be geocoded.

    Returns
    -------
    RegionsBuilder object :

    Note
    -----
    regions_builder() allows to refine ambiguous request with where() method. Call build() method to create Regions object

    Examples
    ---------
    >>> from lets_plot.geo_data import *
    >>> r = state_regions_builder(names='texas').build()
    """
    return RegionsBuilder2('state', names)


def country_regions_builder(names=None) -> RegionsBuilder2:
    """
    Create a RegionBuilder object for countries. Allows to refine ambiguous request with
    where method. build() method creates Regions object or shows details for ambiguous result.

    regions_builder(level, request, within)

    Parameters
    ----------
    names : [array | string | None]
        Names of objects to be geocoded.

    Returns
    -------
    RegionsBuilder object :

    Note
    -----
    regions_builder() allows to refine ambiguous request with where() method. Call build() method to create Regions object

    Examples
    ---------
    >>> from lets_plot.geo_data import *
    >>> r = country_regions_builder(names='USA').build()
    """
    return RegionsBuilder2('country', names)
