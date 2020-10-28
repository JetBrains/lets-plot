#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
from typing import TypeVar, Generic, Optional

from lets_plot.geo_data.gis.request import Request, GeocodingRequest, RegionQuery, MapRegion, AmbiguityResolver

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
        assert self.expected == value, '{} != {}'.format(self.expected, value)

class eq_map_region(eq):
    def __init__(self, v):
        self.expected = MapRegion.with_name(v)


class empty(ValueMatcher[T]):
    def check(self, value):
        assert value is None, '{} is not None'.format(value)

class RegionQueryMatcher:
    def __init__(self,
                 request: ValueMatcher[Optional[str]] = any(),
                 scope: ValueMatcher[Optional[MapRegion]] = any(),
                 ambiguity_resolver: ValueMatcher[AmbiguityResolver] = any(),
                 country: ValueMatcher[Optional[MapRegion]] = any(),
                 state: ValueMatcher[Optional[MapRegion]] = any(),
                 county: ValueMatcher[Optional[MapRegion]] = any()
                 ):
        self._request: ValueMatcher[Optional[str]] = request
        self._scope: ValueMatcher[Optional[MapRegion]] = scope
        self._ambiguity_resolver: ValueMatcher[AmbiguityResolver] = ambiguity_resolver
        self._country: ValueMatcher[Optional[MapRegion]] = country
        self._state: ValueMatcher[Optional[MapRegion]] = state
        self._county: ValueMatcher[Optional[MapRegion]] = county

    def request(self, request: ValueMatcher[Optional[str]]) -> 'RegionQueryMatcher':
        self._request = request
        return self

    def scope(self, scope: ValueMatcher[Optional[MapRegion]]) -> 'RegionQueryMatcher':
        self._scope = scope
        return self

    def ambiguity_resolver(self, ambiguity_resolver: ValueMatcher[AmbiguityResolver]) -> 'RegionQueryMatcher':
        self._ambiguity_resolver = ambiguity_resolver
        return self

    def country(self, country: ValueMatcher[Optional[MapRegion]]) -> 'RegionQueryMatcher':
        self._country: ValueMatcher[Optional[MapRegion]]
        return self

    def state(self, state: ValueMatcher[Optional[MapRegion]]) -> 'RegionQueryMatcher':
        self._state = state
        return self

    def county(self, county: ValueMatcher[Optional[MapRegion]]) -> 'RegionQueryMatcher':
        self._county = county
        return self

    def check(self, q: RegionQuery):
        self._request.check(q.request)
        self._scope.check(q.scope)
        self._ambiguity_resolver.check(q.ambiguity_resolver)
        self._country.check(q.country)
        self._state.check(q.state)
        self._county.check(q.county)

class GeocodingRequestAssertion:
    def __init__(self, request: Request):
        assert isinstance(request, GeocodingRequest)
        self._request: GeocodingRequest = request

    def has_query(self, i: int, query_matcher: RegionQueryMatcher) -> 'GeocodingRequestAssertion':
        query_matcher.check(self._request.region_queries[i])
        return self