#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from geo_data_test_util import features_to_answers, assert_row, FOUND_NAME, GEO_RECT_MIN_LON, GEO_RECT_MAX_LON, \
    GEO_RECT_MIN_LAT, GEO_RECT_MAX_LAT
from pandas import DataFrame

from lets_plot.geo_data.gis.request import RegionQuery, LevelKind
from lets_plot.geo_data.gis.response import SuccessResponse, FeatureBuilder
from lets_plot.geo_data.to_geo_data_frame import LimitsGeoDataFrame, CentroidsGeoDataFrame, BoundariesGeoDataFrame
from geo_data_test_util import CENTROID_LON, CENTROID_LAT, GEO_RECT_MIN_LON, GEO_RECT_MIN_LAT, GEO_RECT_MAX_LON, GEO_RECT_MAX_LAT
from geo_data_test_util import GJMultipolygon, GJPolygon
from geo_data_test_util import ID, NAME, FOUND_NAME
from geo_data_test_util import assert_success_response, assert_row, make_success_response
from geo_data_test_util import feature_to_answer, features_to_answers, features_to_queries
from geo_data_test_util import make_limit_rect, make_centroid_point, polygon, ring, point, make_polygon_boundary, multipolygon, \
    make_multipolygon_boundary, make_single_point_boundary

NAMED_FEATURE_BUILDER = FeatureBuilder() \
    .set_query(NAME) \
    .set_id(ID) \
    .set_name(FOUND_NAME)


def test_requestless_boundaries():
    gdf = BoundariesGeoDataFrame().to_data_frame(
        answers=[
            feature_to_answer(
                FeatureBuilder()
                    .set_id(ID)
                    .set_name(FOUND_NAME)
                    .set_boundary(make_single_point_boundary()) # dummy geometry to not fail on None property
                    .build_geocoded()
            )
        ],
        queries=[RegionQuery(request=FOUND_NAME)],
        level_kind=LevelKind.city
    )
    assert_row(gdf, names=FOUND_NAME, found_name=FOUND_NAME)


def test_requestless_centroids():
    gdf = CentroidsGeoDataFrame().to_data_frame(
        answers=[
            feature_to_answer(
                FeatureBuilder()
                    .set_id(ID)
                    .set_name(FOUND_NAME)
                    .set_centroid(make_centroid_point())
                    .build_geocoded()
            )
        ],
        queries=[RegionQuery(request=FOUND_NAME)],
        level_kind=LevelKind.city
    )
    assert_row(gdf, names=FOUND_NAME, found_name=FOUND_NAME)


def test_requestless_limits():
    gdf = LimitsGeoDataFrame().to_data_frame(
        answers=[
            feature_to_answer(
                FeatureBuilder()
                    .set_id(ID)
                    .set_name(FOUND_NAME)
                    .set_limit(make_limit_rect())
                    .build_geocoded()
            )
        ],
        queries=[RegionQuery(request=FOUND_NAME)],
        level_kind=LevelKind.city
    )
    assert_row(gdf, names=FOUND_NAME, found_name=FOUND_NAME)


def test_geo_limit_response():
    response: SuccessResponse = make_success_response() \
        .set_geocoded_features(
        [
            NAMED_FEATURE_BUILDER \
                .set_limit(make_limit_rect()) \
                .build_geocoded()
        ]
    ).build()
    assert_success_response(response)

    data_frame: DataFrame = LimitsGeoDataFrame().to_data_frame(
        answers=features_to_answers(response.features),
        queries=features_to_queries(response.features),
        level_kind=LevelKind.city
    )
    assert_row(
        df=data_frame,
        names=FOUND_NAME,
        found_name=FOUND_NAME,
        lon_min=GEO_RECT_MIN_LON,
        lon_max=GEO_RECT_MAX_LON,
        lat_min=GEO_RECT_MIN_LAT,
        lat_max=GEO_RECT_MAX_LAT
    )


def test_geo_centroid_response():
    response: SuccessResponse = make_success_response() \
        .set_geocoded_features(
        [
            NAMED_FEATURE_BUILDER \
                .set_centroid(make_centroid_point()) \
                .build_geocoded()
        ]
    ).build()
    assert_success_response(response)

    data_frame: DataFrame = CentroidsGeoDataFrame().to_data_frame(
        answers=features_to_answers(response.features),
        queries=features_to_queries(response.features),
        level_kind=LevelKind.city
    )
    assert_geo_centroid(data_frame, 0)


def test_geo_boundaries_point_response():
    response: SuccessResponse = make_success_response() \
        .set_geocoded_features(
        [
            NAMED_FEATURE_BUILDER \
                .set_boundary(make_single_point_boundary(-5, 13)) \
                .build_geocoded(),

            NAMED_FEATURE_BUILDER \
                .set_boundary(make_single_point_boundary(7, -3)) \
                .build_geocoded(),
        ]
    ).build()

    assert_success_response(response)
    boundary: DataFrame = BoundariesGeoDataFrame().to_data_frame(
        queries=features_to_queries(response.features),
        answers=features_to_answers(response.features),
        level_kind=LevelKind.city
    )
    assert_geo_centroid(boundary, index=0, lon=-5, lat=13)
    assert_geo_centroid(boundary, index=1, lon=7, lat=-3)


def test_geo_boundaries_polygon_response():
    response: SuccessResponse = make_success_response() \
        .set_geocoded_features(
        [
            NAMED_FEATURE_BUILDER \
                .set_boundary(
                make_polygon_boundary(
                    polygon(
                        ring(
                            point(1, 2), point(3, 4), point(5, 6)
                        )
                    )
                )
            ).build_geocoded(),

            NAMED_FEATURE_BUILDER \
                .set_boundary(
                make_polygon_boundary(
                    polygon(
                        ring(
                            point(11, 11), point(11, 14), point(14, 14), point(14, 11), point(11, 11)
                        ),
                        ring(
                            point(12, 12), point(12, 13), point(13, 13), point(13, 12), point(12, 12)
                        )
                    )
                )
            ).build_geocoded()
        ]
    ).build()

    assert_success_response(response)
    boundary: DataFrame = BoundariesGeoDataFrame().to_data_frame(
        queries=features_to_queries(response.features),
        answers=features_to_answers(response.features),
        level_kind=LevelKind.city
    )
    assert_geo_boundary(boundary, index=0, polygon=[[point(1, 2), point(3, 4), point(5, 6)]])
    assert_geo_boundary(boundary, index=1, polygon=[[point(11, 11), point(11, 14), point(14, 14), point(14, 11), point(11, 11)],
                                                    [point(12, 12), point(12, 13), point(13, 13), point(13, 12), point(12, 12)]])


def test_geo_boundaries_multipolygon_response():
    r0 = [point(1, 2), point(3, 4), point(5, 6), point(1, 2)]
    r1 = [point(7, 8), point(9, 10), point(11, 12), point(7, 8)]
    r2 = [point(13, 14), point(15, 16), point(17, 18), point(13, 14)]
    r3 = [point(19, 20), point(21, 22), point(23, 24), point(19, 20)]
    response: SuccessResponse = make_success_response() \
        .set_geocoded_features(
        [
            NAMED_FEATURE_BUILDER \
                .set_boundary(
                make_multipolygon_boundary(
                    multipolygon(
                        polygon(
                            ring(*r0)
                        ),
                        polygon(
                            ring(*r1),
                            ring(*r2)
                        )
                    ),
                )
            ).build_geocoded(),
            NAMED_FEATURE_BUILDER \
                .set_boundary(
                make_multipolygon_boundary(
                    multipolygon(
                        polygon(ring(*r3))
                    )
                )
            ).build_geocoded()
        ]
    ).build()

    assert_success_response(response)
    boundary: DataFrame = BoundariesGeoDataFrame().to_data_frame(
        queries=features_to_queries(response.features),
        answers=features_to_answers(response.features),
        level_kind=LevelKind.city
    )
    assert_geo_multiboundary(boundary, index=0, multipolygon=[[r0], [r1, r2]])
    assert_geo_multiboundary(boundary, index=1, multipolygon=[[r3]])


def assert_geo_limit(limit: DataFrame, index: int, name=FOUND_NAME, found_name=FOUND_NAME):
    assert_row(
        df=limit,
        index=index,
        names=name,
        found_name=found_name,
        lon_min=GEO_RECT_MIN_LON,
        lon_max=GEO_RECT_MAX_LON,
        lat_min=GEO_RECT_MIN_LAT,
        lat_max=GEO_RECT_MAX_LAT
    )


def assert_geo_centroid(centroid: DataFrame, index: int, name=FOUND_NAME, found_name=FOUND_NAME, lon=CENTROID_LON, lat=CENTROID_LAT):
    assert_row(df=centroid, index=index, names=name, found_name=found_name, lon=lon, lat=lat)


def assert_geo_boundary(boundary: DataFrame, index: int, polygon: GJPolygon, name=FOUND_NAME, found_name=FOUND_NAME):
    assert_row(df=boundary, index=index, names=name, found_name=found_name, boundary=polygon)


def assert_geo_multiboundary(boundary: DataFrame, index: int, multipolygon: GJMultipolygon, name=FOUND_NAME, found_name=FOUND_NAME):
    assert_row(df=boundary, index=index, names=name, found_name=found_name, boundary=multipolygon)

