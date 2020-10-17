#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import pytest
from pandas import DataFrame

from lets_plot.geo_data.gis.request import RegionQuery
from lets_plot.geo_data.gis.response import Answer, GeoRect, FeatureBuilder
from lets_plot.geo_data import DF_FOUND_NAME, DF_ID, DF_REQUEST
from lets_plot.geo_data.to_geo_data_frame import LimitsGeoDataFrame

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
    return LimitsGeoDataFrame().to_data_frame(
        answers=[
            Answer(NAME, [
                FeatureBuilder() \
                   .set_id(ID) \
                   .set_query(NAME) \
                   .set_name(FOUND_NAME) \
                   .set_limit(r) \
                   .build_geocoded()
            ]
            )
        ],
        queries=[
            RegionQuery(request=NAME)
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
    # assert lon_min == df[DF_LONMIN][row]
    # assert lon_max == df[DF_LONMAX][row]
    # assert DEFAULT_LAT_MIN == df[DF_LATMIN][row]
    # assert DEFAULT_LAT_MAX == df[DF_LATMAX][row]

    bounds = df.geometry[row].bounds
    assert lon_min == bounds[0]
    assert DEFAULT_LAT_MIN == bounds[1]
    assert lon_max == bounds[2]
    assert DEFAULT_LAT_MAX == bounds[3]