#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
import lets_plot.geo_data as geodata
from .geo_data import assert_error, assert_row, assert_request_and_found_name_are_equal


def test_us48_in_names_with_level():
    us48 = geodata.geocode_states('us-48').get_geocodes()
    assert 49 == len(us48)
    assert_request_and_found_name_are_equal(us48)


def test_us48_in_names_without_level():
    us48 = geodata.geocode(names='us-48').get_geocodes()
    assert 49 == len(us48)
    assert_request_and_found_name_are_equal(us48)


def test_us48_with_extra_names():
    us48 = geodata.geocode(names=['texas', 'us-48', 'nevada']).get_geocodes()
    assert 51 == len(us48)
    assert_request_and_found_name_are_equal(us48, range(1, 49))
    assert_row(us48, index=0, names='texas', found_name='Texas')
    assert_row(us48, index=50, names='nevada', found_name='Nevada')


def test_us48_with_extra_and_missing_names():
    us48 = geodata.geocode(names=['texas', 'blahblahblah', 'us-48', 'nevada'])\
        .drop_not_found()\
        .get_geocodes()

    # still 51 - drop missing completley
    assert 51 == len(us48)
    assert_request_and_found_name_are_equal(us48, range(1, 49))
    assert_row(us48, index=0, names='texas', found_name='Texas')
    assert_row(us48, index=50, names='nevada', found_name='Nevada')


def test_scope_us_48_with_level():
    # Oslo is a city in Marshall County, Minnesota, United States
    # Also Oslo is a capital of Norway
    name = 'oslo'
    oslo_in_norway = geodata.geocode_cities(name).where(name, scope='norway').get_geocodes()
    oslo_in_usa = geodata.geocode_cities(name).where(name, scope='us-48').get_geocodes()

    assert oslo_in_norway.id[0] != oslo_in_usa.id[0]


def test_where_scope_us_48_without_level():
    name = 'oslo'
    oslo_in_norway = geodata.geocode(names=name).where(name, scope='norway').get_geocodes()
    oslo_in_usa = geodata.geocode(names=name).where(name, scope='us-48').get_geocodes()

    assert oslo_in_norway.id[0] != oslo_in_usa.id[0]


def test_where_scope_us_48_with_level():
    name = 'oslo'
    oslo_in_norway = geodata.geocode_cities(names=name).scope('norway').get_geocodes()
    oslo_in_usa = geodata.geocode_cities(names=name).scope('us-48').get_geocodes()

    assert oslo_in_norway.id[0] != oslo_in_usa.id[0]


def test_scope_us_48_without_level():
    name = 'oslo'
    oslo_in_norway = geodata.geocode(names=name).scope('norway').get_geocodes()
    oslo_in_usa = geodata.geocode(names=name).scope('us-48').get_geocodes()

    assert oslo_in_norway.id[0] != oslo_in_usa.id[0]


def test_parent_states_us48():
    boston = geodata.geocode_cities('boston').states('us-48').get_geocodes()

    assert_row(boston, names='boston', found_name='Boston')


def test_error_us48_in_request_not_available():
    assert_error(
        "us-48 can't be used in requests with parents.",
        lambda: geodata.geocode_states('us-48').countries('usa').get_geocodes()
    )


