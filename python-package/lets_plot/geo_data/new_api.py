#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
from typing import Any, Union, List, Optional, Dict
from collections import namedtuple

import numpy as np
from pandas import Series

from .gis.geocoding_service import GeocodingService
from .gis.geometry import GeoPoint
from .gis.request import RequestBuilder, Request, GeocodingRequest, RequestKind, MapRegion, MapRegionKind, AmbiguityResolver, RegionQuery, \
    LevelKind, IgnoringStrategyKind
from .gis.response import Response, SuccessResponse
from .regions import Regions, _raise_exception, _to_level_kind, _to_scope, _make_parent_region
from .regions_builder import RegionsBuilder, NAMESAKE_MAX_COUNT
from .type_assertion import assert_list_type
from .regions import _to_level_kind, request_types, parent_types, scope_types, Regions, _raise_exception, \
    _ensure_is_list

__all__ = [
    'regions_builder2'
]

QuerySpec = namedtuple('QuerySpec', 'name, county, state, country')
WithinSpec = namedtuple('WithinSpec', 'scope, polygon')

def _get_or_none(list, index):
    if index >= len(list):
        return None
    return list[index]


def _ensure_is_parent_list(obj):
    if obj is None:
        return None

    if isinstance(obj, str):
        return [obj]
    if isinstance(obj, Regions):
        return obj.as_list()

    if isinstance(obj, list):
        return obj

    return [obj]




class RegionsBuilder2:
    def __init__(self,
                 level: Optional[Union[str, LevelKind]] = None,
                 request: request_types = None,
                 scope: scope_types = None,
                 highlights: bool = False,
                 allow_ambiguous=False
                 ):

        self._level: Optional[LevelKind] = _to_level_kind(level)
        self._default_ambiguity_resolver: AmbiguityResolver = AmbiguityResolver.empty()  # TODO rename to geohint
        self._highlights: bool = highlights
        self._allow_ambiguous = allow_ambiguous
        self._countries: List[Optional[MapRegion]] = []
        self._states: List[Optional[MapRegion]] = []
        self._counties: List[Optional[MapRegion]] = []
        self._overridings: Dict[QuerySpec, WhereSpec] = {}  # query to scope

        requests: Optional[List[str]] = _ensure_is_list(request)

        self._names: List[Optional[str]] = list(map(lambda name: name if requests is not None else None, requests))
        self._scope: List[Optional[MapRegion]] = _prepare_new_scope(scope)

    def countries(self, countries) -> 'RegionsBuilder2':
        self._countries = self._make_parents(countries, 'countries')
        return self

    def states(self, states) -> 'RegionsBuilder2':
        self._states = self._make_parents(states, 'states')
        return self

    def counties(self, counties) -> 'RegionsBuilder2':
        self._counties = self._make_parents(counties, 'counties')
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
              county: Optional[parent_types]=None,
              state: Optional[parent_types]=None,
              country: Optional[parent_types]=None,
              scope: scope_types=None):
        new_scope: List[MapRegion] = _prepare_new_scope(scope)
        if len(new_scope) != 1:
            raise ValueError('Scope for where should have length of 1, but was {}'.format(len(new_scope)))

        county_region = _make_parent_region(county)
        state_region = _make_parent_region(state)
        country_region = _make_parent_region(country)

        self._overridings[QuerySpec(name, county_region, state_region, country_region)] = WithinSpec(new_scope[0], AmbiguityResolver.empty())
        return self

    def _build_request(self) -> GeocodingRequest:
        queries = []
        for i in range(len(self._names)):
            name = self._names[i]
            country = _get_or_none(self._countries, i)
            state = _get_or_none(self._states, i)
            county = _get_or_none(self._counties, i)

            scope, ambiguity_resolver = self._overridings.get(QuerySpec(name, county, state, country),
                                                              WithinSpec(None, self._default_ambiguity_resolver))

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

    def build(self) -> Regions:
        request: GeocodingRequest = self._build_request()

        response: Response = GeocodingService().do_request(request)

        if not isinstance(response, SuccessResponse):
            _raise_exception(response)

        return Regions(response.level, response.answers, request.region_queries, self._highlights)

    def _make_parents(self, values, kind) -> List[Optional[MapRegion]]:
        values = _ensure_is_parent_list(values)

        if values is None:
            return []

        if values is not None and len(values) != len(self._names):
            raise ValueError('{} count({}) != names count({})'.format(kind, len(values), len(self._names)))

        return list(map(lambda v: _make_parent_region(v) if values is not None else None, values))

    def __eq__(self, o):
        return isinstance(o, RegionsBuilder2) \
               and self._overridings == o._overridings

    def __ne__(self, o):
        return not self == o


def _prepare_new_scope(scope: Optional[Union[str, Regions, MapRegion, List]]) -> List[MapRegion]:
    def obj_to_map_region(obj) -> List[MapRegion]:
        if isinstance(obj, str):
            return [MapRegion.with_name(obj)]
        if isinstance(obj, Regions):
            return obj.to_map_regions()
        if isinstance(obj, MapRegion):
            if scope.kind == MapRegionKind.id:
                return [MapRegion.scope([id]) for id in obj.values]  # flat_map ids
            else:
                assert len(obj.values) == 1  # only id have more than one value
                return [obj]

    if scope is None:
        return []

    flatten_scope = []
    if isinstance(scope, (list, tuple)):
        for obj in scope:
            flatten_scope.extend(obj_to_map_region(obj))
    else:
        flatten_scope.extend(obj_to_map_region(scope))

    return flatten_scope


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
    >>> r = regions_builder(level='city', request=['moscow', 'york']).where('york', regions_state('New York')).build()
    """

    new_scope = _prepare_new_scope(scope)

    return RegionsBuilder2(level, names, scope, highlights, allow_ambiguous=False)\
        .countries(countries) \
        .states(states) \
        .counties(counties)

    return RegionsBuilder(level, names, scope, highlights,
                          allow_ambiguous=False,
                          countries=countries,
                          states=states,
                          counties=counties,
                          new_api=True,
                          new_scope=new_scope)


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
