#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from typing import Optional
from lets_plot.geo_data.new_api import RegionsBuilder2
from lets_plot.geo_data.gis.request import Request, GeocodingRequest, MapRegion, AmbiguityResolver

from .request_assertion import GeocodingRequestAssertion, RegionQueryMatcher, ValueMatcher, eq, empty, eq_map_region

def no_parents(request: ValueMatcher[Optional[str]],
               scope: ValueMatcher[Optional[MapRegion]] = empty(),
               ambiguity_resolver: ValueMatcher[AmbiguityResolver] = eq(AmbiguityResolver.empty())
               ):
    return RegionQueryMatcher(request=request, scope=scope, ambiguity_resolver=ambiguity_resolver,
                              country=empty(), state=empty(), county=empty())

def that_matches():
    return RegionQueryMatcher()

def assert_that(request):
    return GeocodingRequestAssertion(request)

def test_simple():
    request = RegionsBuilder2('city', 'foo')._build_request()
    assert_that(request)\
            .has_query(0, no_parents(request=eq('foo')))

def test_no_parents_where_should_override_scope():
    request = RegionsBuilder2('city', 'foo')\
        .where('foo', scope='bar')\
        ._build_request()

    assert_that(request) \
        .has_query(0, no_parents(request=eq('foo'), scope=eq_map_region('bar')))

def test_where_with_duplicated_names_and_parents_should_work():
    request = RegionsBuilder2('city', request=['foo', 'foo'])\
        .counties(['bar', 'baz'])\
        .where(name='foo', county='baz', scope='spam')\
        ._build_request()

    assert_that(request) \
        .has_query(0, that_matches()
                   .request(eq('foo'))
                   .county(eq_map_region('bar'))
                   .scope(empty())
                   )\
        .has_query(1, that_matches()
                   .request(eq('foo'))
                   .county(eq_map_region('baz'))
                   .scope(eq_map_region('spam'))
                   )
