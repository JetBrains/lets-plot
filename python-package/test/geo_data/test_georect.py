#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import pytest
from pandas import DataFrame

from lets_plot.geo_data.gis.response import GeoRect, FeatureBuilder
from lets_plot.geo_data.to_data_frame import DF_REQUEST, DF_FOUND_NAME, DF_LONMIN, DF_LONMAX, DF_LATMIN, DF_LATMAX, \
    LimitsDataFrame
from .geo_data import NAME, FOUND_NAME, ID

DEFAULT_LAT_MIN = 20
DEFAULT_LAT_MAX = 30


@pytest.mark.parametrize(
    'lon_min, lon_max',
    [
        (10, 20),
        (0, 0),
        (-180, 180),
        (-180, -180),
        (-180, 0),
    ])
def test_if_not_cross_antimeridian_should_return_one_rect(lon_min, lon_max):
    r = make_rect(lon_min=lon_min, lon_max=lon_max)
    assert_whole_rect(r, lon_min=lon_min, lon_max=lon_max)


@pytest.mark.parametrize(
    'lon_min, lon_max',
    [
        (20, 10),
        (180, -180),
        (0, -180),
    ])
def test_if_cross_antimeridian_should_return_two_rects(lon_min, lon_max):
    r = make_rect(lon_min=lon_min, lon_max=lon_max)
    assert_split_rect(r, lon_min=lon_min, lon_max=lon_max)


def make_rect(lon_min: float, lon_max: float) -> GeoRect:
    return GeoRect(lon_min, DEFAULT_LAT_MIN, lon_max, DEFAULT_LAT_MAX)


def data_frame(r: GeoRect):
    return LimitsDataFrame().to_data_frame(
        [
            FeatureBuilder() \
                .set_id(ID) \
                .set_query(NAME) \
                .set_name(FOUND_NAME) \
                .set_limit(r) \
                .build_geocoded()
        ]
    )


def assert_whole_rect(r: GeoRect, lon_min: float, lon_max: float):
    assert_row(data_frame(r), 0, lon_min=lon_min, lon_max=lon_max)


def assert_split_rect(r: GeoRect, lon_min: float, lon_max: float):
    assert_row(data_frame(r), 0, lon_min=lon_min, lon_max=180.)
    assert_row(data_frame(r), 1, lon_min=-180., lon_max=lon_max)


def assert_row(df: 'DataFrame', row: int, lon_min: float, lon_max: float):
    assert NAME == df[DF_REQUEST][row]
    assert FOUND_NAME == df[DF_FOUND_NAME][row]
    assert lon_min == df[DF_LONMIN][row]
    assert lon_max == df[DF_LONMAX][row]
    assert DEFAULT_LAT_MIN == df[DF_LATMIN][row]
    assert DEFAULT_LAT_MAX == df[DF_LATMAX][row]
