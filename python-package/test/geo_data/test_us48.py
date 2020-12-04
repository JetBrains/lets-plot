#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
import lets_plot.geo_data as geodata
from lets_plot.geo_data import DF_ID
from .geo_data import assert_error


def test_us48_in_request_with_level():
    geodata.state_regions_builder('us-48').build()


def test_us48_in_request_without_level():
    geodata.regions_builder2(names='us-48').build()


def test_within_us_48_with_level():
    name = 'oslo'
    non_us48 = geodata.regions_builder('city', request=name, within='norway').build().to_data_frame()
    us48 = geodata.regions_builder('city', request=name, within='us-48').build().to_data_frame()

    assert non_us48[DF_ID][0] != us48[DF_ID][0]


def test_within_us_48_without_level():
    name = 'oslo'
    non_us48 = geodata.regions_builder(request=name, within='norway').build().to_data_frame()
    us48 = geodata.regions_builder(request=name, within='us-48').build().to_data_frame()

    assert non_us48[DF_ID][0] != us48[DF_ID][0]


def test_scope_us_48_with_level():
    name = 'oslo'
    non_us48 = geodata.city_regions_builder(names=name).scope('norway').build().to_data_frame()
    us48 = geodata.city_regions_builder(names=name).scope('us-48').build().to_data_frame()

    assert non_us48[DF_ID][0] != us48[DF_ID][0]


def test_scope_us_48_without_level():
    name = 'oslo'
    non_us48 = geodata.regions_builder2(names=name).scope('norway').build().to_data_frame()
    us48 = geodata.regions_builder2(names=name).scope('us-48').build().to_data_frame()

    assert non_us48[DF_ID][0] != us48[DF_ID][0]


def test_parent_states_us48():
    geodata.city_regions_builder('boston').states('us-48').build()


def test_error_us48_in_request_not_available():
    assert_error(
        "us-48 can't be used in requests with parents.",
        lambda: geodata.state_regions_builder('us-48').countries('usa').build()
    )


