#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from typing import List

from pandas import DataFrame

from lets_plot.geo_data.gis.response import SuccessResponse, FeatureBuilder
from lets_plot.geo_data.to_data_frame import LimitsDataFrame, CentroidsDataFrame, BoundariesDataFrame, \
    PositionsDataFrame, DF_REQUEST, DF_FOUND_NAME, \
    DF_LON, DF_LAT
from .geo_data import make_limit_rect, assert_success_response, NAME, make_centroid_point, make_point_boundary, point, \
    make_position_rect, \
    FOUND_NAME, GEO_RECT_MIN_LON, GEO_RECT_MIN_LAT, GEO_RECT_MAX_LON, GEO_RECT_MAX_LAT, CENTROID_LON, CENTROID_LAT, \
    GJPoint, lon, lat, make_success_response, ID

NAMED_FEATURE_BUILDER = FeatureBuilder() \
    .set_query(NAME) \
    .set_id(ID) \
    .set_name(FOUND_NAME)


def test_limit_response():
    response: SuccessResponse = make_success_response() \
        .set_geocoded_features(
        [
            NAMED_FEATURE_BUILDER \
                .set_limit(make_limit_rect()) \
                .build_geocoded()
        ]
    ).build()
    assert_success_response(response)

    data_frame = LimitsDataFrame().to_data_frame(response.features)
    assert_limit(data_frame, 0, name=NAME)


def test_centroid_response():
    response: SuccessResponse = make_success_response() \
        .set_geocoded_features(
        [
            NAMED_FEATURE_BUILDER \
                .set_centroid(make_centroid_point()) \
                .build_geocoded()
        ]
    ).build()
    assert_success_response(response)

    data_frame = CentroidsDataFrame().to_data_frame(response.features)
    assert_centroid(data_frame, 0, name=NAME)


def test_boundaries_response():
    response: SuccessResponse = make_success_response() \
        .set_geocoded_features(
        [
            NAMED_FEATURE_BUILDER \
                .set_boundary(make_point_boundary(-5, 13)) \
                .build_geocoded()
        ]
    ).build()

    assert_success_response(response)

    data_frame = BoundariesDataFrame().to_data_frame(response.features)
    assert_boundary(data_frame, 0, points=[point(-5, 13)])


def test_multiresponse():
    response: SuccessResponse = make_success_response() \
        .set_geocoded_features(
        [
            NAMED_FEATURE_BUILDER \
                .set_boundary(make_point_boundary(-5, 13)) \
                .set_centroid(make_centroid_point()) \
                .set_limit(make_limit_rect()) \
                .set_position(make_position_rect()) \
                .build_geocoded()
        ]
    ).build()

    assert_centroid(CentroidsDataFrame().to_data_frame(response.features), 0)
    assert_limit(LimitsDataFrame().to_data_frame(response.features), 0)
    assert_limit(PositionsDataFrame().to_data_frame(response.features), 0)
    assert_boundary(BoundariesDataFrame().to_data_frame(response.features), index=0, points=[point(-5, 13)])


def assert_names(df: DataFrame, index: int, name=NAME, found_name=FOUND_NAME):
    assert name == df[DF_REQUEST][index]
    assert found_name == df[DF_FOUND_NAME][index]


def assert_limit(limit: DataFrame, index: int, name=NAME, found_name=FOUND_NAME):
    assert_names(limit, index, name, found_name)

    min_lon = limit['lonmin'][index]
    min_lat = limit['latmin'][index]
    max_lon = limit['lonmax'][index]
    max_lat = limit['latmax'][index]
    assert GEO_RECT_MIN_LON == min_lon
    assert GEO_RECT_MIN_LAT == min_lat
    assert GEO_RECT_MAX_LON == max_lon
    assert GEO_RECT_MAX_LAT == max_lat


def assert_centroid(centroid: DataFrame, index: int, name=NAME, found_name=FOUND_NAME, lon=CENTROID_LON, lat=CENTROID_LAT):
    assert_names(centroid, index, name, found_name)
    assert_point(centroid, index, lon, lat)


def assert_boundary(boundary: DataFrame, index: int, points: List[GJPoint], name=NAME, found_name=FOUND_NAME):
    assert_names(boundary, index, name, found_name)
    for i, point in enumerate(points):
        assert_point(boundary, index + i, lon(point), lat(point))


def assert_point(df: DataFrame, index: int, lon: float, lat: float):
    assert lon == df[DF_LON][index]
    assert lat == df[DF_LAT][index]
