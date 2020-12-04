#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from typing import Optional, Union, List, Callable
from unittest import mock

import shapely

from lets_plot.geo_data import GeocodingService, SuccessResponse, Answer, GeocodedFeature
from lets_plot.geo_data.gis.geometry import GeoRect, GeoPoint
from lets_plot.geo_data.gis.request import MapRegion, AmbiguityResolver, GeocodingRequest, LevelKind, RegionQuery
from lets_plot.geo_data.new_api import RegionsBuilder2
from lets_plot.geo_data.regions import Regions
from .geo_data import make_answer
from .request_assertion import GeocodingRequestAssertion, QueryMatcher, ScopeMatcher, ValueMatcher, eq, empty, \
    eq_map_region_with_name, eq_map_region_with_id


def test_simple():
    request = RegionsBuilder2(request='foo')\
        ._build_request()

    assert_that(request) \
        .has_query(no_parents(request=eq('foo')))


def test_no_parents_where_should_override_scope():
    # without parents should update scope for matching name

    request = RegionsBuilder2(request='foo') \
        .where('foo', scope='bar') \
        ._build_request()

    assert_that(request) \
        .has_query(no_parents(request=eq('foo'), scope=eq_map_region_with_name('bar')))


def test_when_regions_in_parent_should_take_region_id():
    builder = RegionsBuilder2(request='foo') \
        .states(make_simple_region('bar'))

    assert_that(builder) \
        .has_query(QueryMatcher()
                   .with_name('foo')\
                   .state(eq_map_region_with_id('bar_id'))
                   )


def test_parents_can_contain_nulls():
    builder = RegionsBuilder2(request=['foo', 'bar'])\
        .states([None, 'baz'])

    assert_that(builder) \
        .has_query(QueryMatcher()
                   .with_name('foo') \
                   .state(empty())
                   ) \
        .has_query(QueryMatcher()
                   .with_name('bar') \
                   .state(eq_map_region_with_name('baz'))
                   )


def test_where_with_given_parents_and_duplicated_names():
    # should update scope only for matching name and parents - query with index 1

    request = RegionsBuilder2(request=['foo', 'foo']) \
        .states(['bar', 'baz']) \
        .where(name='foo', state='baz', scope='spam') \
        ._build_request()

    assert_that(request) \
        .has_query(QueryMatcher()
                   .with_name('foo')
                   .state(eq_map_region_with_name('bar'))
                   .scope(empty())
                   ) \
        .has_query(QueryMatcher()
                   .with_name('foo')
                   .state(eq_map_region_with_name('baz'))
                   .scope(eq_map_region_with_name('spam'))
                   )


def test_where_with_given_country_should_be_used():
    # should update scope only for matching name and parents - query with index 1

    request = RegionsBuilder2(request=['foo', 'foo']) \
        .countries(['bar', 'baz']) \
        .where(name='foo', country='baz', scope='spam') \
        ._build_request()

    assert_that(request) \
        .has_query(QueryMatcher()
                   .with_name('foo')
                   .country(eq_map_region_with_name('bar'))
                   .scope(empty())
                   ) \
        .has_query(QueryMatcher()
                   .with_name('foo')
                   .country(eq_map_region_with_name('baz'))
                   .scope(eq_map_region_with_name('spam'))
                   )


def test_where_within_box():
    request = RegionsBuilder2(request=['foo']) \
        .states(['bar']) \
        .where(name='foo', state='bar', within=shapely.geometry.box(1, 2, 3, 4)) \
        ._build_request()

    assert_that(request) \
        .has_query(QueryMatcher()
                   .with_name('foo')
                   .state(eq_map_region_with_name('bar'))
                   .ambiguity_resolver(eq(AmbiguityResolver(box=GeoRect(1, 2, 3, 4))))
                   )


def test_where_near_point():
    request = RegionsBuilder2(request=['foo']) \
        .states(['bar']) \
        .where(name='foo', state='bar', near=shapely.geometry.Point(1, 2)) \
        ._build_request()

    assert_that(request) \
        .has_query(QueryMatcher()
                   .with_name('foo')
                   .state(eq_map_region_with_name('bar'))
                   .ambiguity_resolver(eq(AmbiguityResolver(closest_coord=GeoPoint(1, 2))))
                   )


@mock.patch.object(GeocodingService, 'do_request', lambda self, reqest: SuccessResponse(
    message='',
    level=LevelKind.city,
    answers=[
        Answer(
            features=[
                GeocodedFeature(
                    id='foo_id',
                    name='foo',
                    highlights=None,
                    boundary=None,
                    centroid=GeoPoint(1, 2),
                    limit=None,
                    position=None
                )
            ])
    ]
))
def test_where_near_region():
    request = RegionsBuilder2(request=['foo']) \
        .states(['bar']) \
        .where(name='foo', state='bar', near=make_simple_region('foo', 'foo_id')) \
        ._build_request()


    assert_that(request) \
        .has_query(QueryMatcher()
                   .with_name('foo')
                   .state(eq_map_region_with_name('bar'))
                   .ambiguity_resolver(eq(AmbiguityResolver(closest_coord=GeoPoint(1, 2))))
                   )


def test_global_scope():
    # scope should be applied to whole request, not to queries

    builder: RegionsBuilder2 = RegionsBuilder2(request='foo')

    # single str scope
    assert_that(builder.scope('bar')) \
        .has_scope(ScopeMatcher().with_names(['bar'])) \
        .has_query(QueryMatcher().with_name('foo').scope(empty()))

    # two strings scope - should flatten to two MapRegions with single name in each
    assert_that(builder.scope(['bar', 'baz'])) \
        .has_scope(ScopeMatcher().with_names(['bar', 'baz'])) \
        .has_query(QueryMatcher().with_name('foo').scope(empty()))

    # single regions scope
    assert_that(builder.scope(make_simple_region('bar', 'bar_id'))) \
        .has_scope(ScopeMatcher().with_ids(['bar_id'])) \
        .has_query(QueryMatcher().with_name('foo').scope(empty()))

    # single region with entries scope
    assert_that(builder.scope([make_simple_region(['bar', 'baz'], ['bar_id', 'baz_id'])])) \
        .has_scope(ScopeMatcher().with_ids(['bar_id', 'baz_id'])) \
        .has_query(QueryMatcher().with_name('foo').scope(empty()))

    # two regions scope - flatten ids
    assert_that(builder.scope([make_simple_region('bar', 'bar_id'), make_simple_region('baz', 'baz_id')])) \
        .has_scope(ScopeMatcher().with_ids(['bar_id', 'baz_id'])) \
        .has_query(QueryMatcher().with_name('foo').scope(empty()))


def test_request_without_name():
    assert_that(RegionsBuilder2(level='county').states('New York')) \
        .has_level(eq(LevelKind.county)) \
        .has_query(QueryMatcher()
                   .with_name(None)
                   .state(eq_map_region_with_name('New York'))
                   )


def test_request_countries():
    assert_that(RegionsBuilder2(level='country')) \
        .has_level(eq(LevelKind.country)) \
        .has_query(QueryMatcher()
                   .with_name(None)
                   .state(eq_map_region_with_name('New York'))
                   )


def test_error_when_country_and_scope_set_should_show_error():
    # scope can't work with given country parent.
    check_validation_error(
        "Invalid request: parents and scope can't be used simultaneously",
        lambda: RegionsBuilder2(request='foo').countries('bar').scope('baz')
    )


def test_error_when_names_and_parents_have_different_size():
    check_validation_error(
        'Invalid request: countries count(2) != names count(1)',
        lambda: RegionsBuilder2(request='foo').countries(['bar', 'baz'])
    )

    check_validation_error(
        'Invalid request: states count(2) != names count(1)',
        lambda: RegionsBuilder2(request='foo').states(['bar', 'baz'])
    )

    check_validation_error(
        'Invalid request: counties count(2) != names count(1)',
        lambda: RegionsBuilder2(request='foo').counties(['bar', 'baz'])
    )


def test_error_where_scope_len_is_invalid():
    check_validation_error(
        'Invalid request: where functions scope should have length of 1, but was 2',
        lambda: RegionsBuilder2(request='foo').where('foo', scope=['bar', 'baz'])
    )


def test_error_for_where_with_unknown_name():
    check_validation_error(
        "bar is not found in names",
        lambda: RegionsBuilder2(request='foo').where('bar', scope='baz')
    )


def test_error_for_where_with_unknown_name_and_parents():
    check_validation_error(
        "bar(country=baz) is not found in names",
        lambda: RegionsBuilder2(request='foo').where('bar', country='baz', scope='spam')
    )


def make_simple_region(requests: Union[str, List[str]], geo_object_ids: Union[str, List[str]] = None, level_kind: LevelKind = LevelKind.county) -> Regions:
    requests = requests if isinstance(requests, (list, tuple)) else [requests]
    geo_object_ids = geo_object_ids if geo_object_ids is not None else [request + '_id' for request in requests]
    geo_object_ids = geo_object_ids if isinstance(geo_object_ids, (list, tuple)) else [geo_object_ids]

    queries = []
    answers = []
    for request, id in zip(requests, geo_object_ids):
        queries.append(RegionQuery(request=request))
        answers.append(make_answer(request, id, []))

    return Regions(level_kind, answers, queries)


def no_parents(request: ValueMatcher[Optional[str]],
               scope: ValueMatcher[Optional[MapRegion]] = empty(),
               ambiguity_resolver: ValueMatcher[AmbiguityResolver] = eq(AmbiguityResolver.empty())
               ) -> QueryMatcher:
    return QueryMatcher(name=request, scope=scope, ambiguity_resolver=ambiguity_resolver,
                        country=empty(), state=empty(), county=empty())


def assert_that(request: Union[RegionsBuilder2, GeocodingRequest]) -> GeocodingRequestAssertion:
    if isinstance(request, RegionsBuilder2):
        return GeocodingRequestAssertion(request._build_request())
    elif isinstance(request, GeocodingRequest):
        return GeocodingRequestAssertion(request)
    else:
        raise ValueError('Expected types are [RegionsBuilder2, GeocodingRequest], but was {}', str(type(request)))


def check_validation_error(message: str, get_builder: Callable[[], RegionsBuilder2]):
    assert isinstance(message, str)
    try:
        get_builder()._build_request()
        assert False, 'Validation error expected'
    except Exception as e:
        assert message == str(e)
