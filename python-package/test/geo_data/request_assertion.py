#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
from typing import TypeVar, Generic, Optional, List, Union

from lets_plot.geo_data.gis.geometry import GeoRect, GeoPoint

from lets_plot.geo_data.geocodes import _ensure_is_list
from lets_plot.geo_data.gis.request import Request, GeocodingRequest, RegionQuery, MapRegion, AmbiguityResolver, \
    PayloadKind, MapRegionKind, IgnoringStrategyKind, LevelKind

T = TypeVar('T')


class ValueMatcher(Generic[T]):
    def check(self, value):
        raise ValueError('abstract')


class any(ValueMatcher[T]):
    def check(self, value):
        return


class eq(ValueMatcher[T]):
    def __init__(self, v):
        self.expected = v

    def check(self, value):
        assert type(self.expected) == type(value), "{} != {}".format(type(self.expected), type(value))
        assert self.expected == value, '{} != {}'.format(self.expected, value)


class eq_map_region_with_name(eq[MapRegion]):
    def __init__(self, name: str):
        self.expected = MapRegion.with_name(name)


class eq_map_region_with_id(ValueMatcher[MapRegion]):
    """
    Checks only id
    """

    def __init__(self, ids: Union[str, List[str]]):
        ids = _ensure_is_list(ids)
        self.expected = MapRegion.scope(ids)

    def check(self, value):
        assert value.kind == MapRegionKind.id or value.kind == MapRegionKind.place
        assert self.expected.values == value.values


class empty(ValueMatcher[T]):
    def check(self, value):
        assert value is None, '{} is not None'.format(value)


class item_exists(ValueMatcher[T]):
    def __init__(self, value):
        self._expected = value

    def check(self, value):
        exists = False
        for v in value:
            if v == self._expected:
                exists = True
                break

        assert exists, 'Item {} not found in list'.format(self._expected)


class ScopeMatcher:
    '''
    Scope can't be mixed with names and ids.
    Scope with name should have length exactly 1.
    Scope with ids should have length exactly 1.

    '''

    def __init__(self):
        self._names: Optional[List[str]] = None
        self._ids: Optional[List[str]] = None

    def with_names(self, names: List[str]) -> 'ScopeMatcher':
        self._names = names
        return self

    def with_ids(self, ids: List[str]) -> 'ScopeMatcher':
        self._ids = ids
        return self

    def empty(self) -> 'ScopeMatcher':
        self._names = None
        self._ids = None
        return self

    def check(self, scope: List[MapRegion]):
        if self._names is None and self._ids is None:
            assert len(scope) == 0
        elif self._names is not None:
            assert len(self._names) == len(scope)
            for expected_name, region in zip(self._names, scope):
                assert expected_name == MapRegion.name_or_none(region)
        elif self._ids is not None:
            for expected_id, region in zip(self._ids, scope):
                assert len(region.values) == 1
                assert expected_id == region.values[0]
        else:
            raise ValueError('Invalid matcher state')


class AmbiguityResolverMatcher(ValueMatcher[AmbiguityResolver]):
    def __init__(self):
        self._ignoring_strategy: ValueMatcher[IgnoringStrategyKind] = any()
        self._box: ValueMatcher[GeoRect] = any()
        self._near: ValueMatcher[GeoPoint] = any()

    def check(self, value):
        self._ignoring_strategy.check(value.ignoring_strategy)
        self._box.check(value.box)
        self._near.check(value.closest_coord)


class QueryMatcher:
    def __init__(self,
                 name: ValueMatcher[Optional[str]] = any(),
                 scope: ValueMatcher[Optional[MapRegion]] = any(),
                 ambiguity_resolver: ValueMatcher[AmbiguityResolver] = any(),
                 country: ValueMatcher[Optional[MapRegion]] = any(),
                 state: ValueMatcher[Optional[MapRegion]] = any(),
                 county: ValueMatcher[Optional[MapRegion]] = any()
                 ):
        self._name: ValueMatcher[Optional[str]] = name
        self._scope: ValueMatcher[Optional[MapRegion]] = scope
        self._ambiguity_resolver: ValueMatcher[AmbiguityResolver] = ambiguity_resolver
        self._country: ValueMatcher[Optional[MapRegion]] = country
        self._state: ValueMatcher[Optional[MapRegion]] = state
        self._county: ValueMatcher[Optional[MapRegion]] = county

    def with_name(self, name: Optional[str]) -> 'QueryMatcher':
        self._name = eq(name)
        return self

    def scope(self, scope: ValueMatcher[Optional[MapRegion]]) -> 'QueryMatcher':
        self._scope = scope
        return self

    def ambiguity_resolver(self, ambiguity_resolver: ValueMatcher[AmbiguityResolver]) -> 'QueryMatcher':
        self._ambiguity_resolver = ambiguity_resolver
        return self

    def country(self, country: ValueMatcher[Optional[MapRegion]]) -> 'QueryMatcher':
        self._country = country
        return self

    def state(self, state: ValueMatcher[Optional[MapRegion]]) -> 'QueryMatcher':
        self._state = state
        return self

    def county(self, county: ValueMatcher[Optional[MapRegion]]) -> 'QueryMatcher':
        self._county = county
        return self

    def check(self, q: RegionQuery):
        self._name.check(q.request)
        self._scope.check(q.scope)
        self._ambiguity_resolver.check(q.ambiguity_resolver)
        self._country.check(q.country)
        self._state.check(q.state)
        self._county.check(q.county)


class GeocodingRequestAssertion:
    def __init__(self, request: Request):
        self._i = 0
        assert isinstance(request, GeocodingRequest)
        self._request: GeocodingRequest = request

    def allows_ambiguous(self) -> 'GeocodingRequestAssertion':
        assert self._request.allow_ambiguous
        return self

    def has_query(self, query_matcher: QueryMatcher, i: Optional[int] = None) -> 'GeocodingRequestAssertion':
        if i is None:
            i = self._i
            self._i += 1

        query_matcher.check(self._request.region_queries[i])
        return self

    def has_scope(self, scope_matcher: ScopeMatcher) -> 'GeocodingRequestAssertion':
        scope_matcher.check(self._request.scope)
        return self

    def has_level(self, level_matcher: ValueMatcher[LevelKind]) -> 'GeocodingRequestAssertion':
        level_matcher.check(self._request.level)
        return self

    def fetches(self, payload: PayloadKind):
        item_exists(payload).check(self._request.requested_payload)
