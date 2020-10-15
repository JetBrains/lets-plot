#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from typing import List

from geo_data.geo_data import assert_names
from geopandas import GeoDataFrame
from pandas import DataFrame

from lets_plot.geo_data.gis.response import SuccessResponse, FeatureBuilder, GeocodedFeature, Answer
from lets_plot.geo_data.to_geo_data_frame import LimitsGeoDataFrame, CentroidsGeoDataFrame, BoundariesGeoDataFrame
from .geo_data import GJMultipolygon, GJPolygon, GJRing, lon, lat, NAME, \
    FOUND_NAME, CENTROID_LON, CENTROID_LAT, GEO_RECT_MIN_LON, GEO_RECT_MIN_LAT, GEO_RECT_MAX_LON, GEO_RECT_MAX_LAT, \
    assert_success_response, \
    make_success_response, make_limit_rect, make_centroid_point, polygon, ring, point, make_polygon_boundary, \
    multipolygon, \
    make_multipolygon_boundary, make_single_point_boundary, ID

NAMED_FEATURE_BUILDER = FeatureBuilder() \
    .set_query(NAME) \
    .set_id(ID) \
    .set_name(FOUND_NAME)


def test_requestless_boundaries():
    gdf = BoundariesGeoDataFrame().to_data_frame([
        answer(
            FeatureBuilder()
                .set_id(ID)
                .set_name(FOUND_NAME)
                .set_boundary(make_single_point_boundary()) # dummy geometry to not fail on None property
                .build_geocoded()
        )
    ])
    assert_names(gdf, 0, FOUND_NAME, FOUND_NAME)


def test_requestless_centroids():
    gdf = CentroidsGeoDataFrame().to_data_frame([
        answer(
            FeatureBuilder()
                .set_id(ID)
                .set_name(FOUND_NAME)
                .set_centroid(make_centroid_point())
                .build_geocoded()
        )
    ])
    assert_names(gdf, 0, FOUND_NAME, FOUND_NAME)


def test_requestless_limits():
    gdf = LimitsGeoDataFrame().to_data_frame([
        answer(
            FeatureBuilder()
                .set_id(ID)
                .set_name(FOUND_NAME)
                .set_limit(make_limit_rect())
                .build_geocoded()
        )
    ])
    assert_names(gdf, 0, FOUND_NAME, FOUND_NAME)


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

    data_frame: DataFrame = LimitsGeoDataFrame().to_data_frame(answers(response.features))
    assert_geo_limit(data_frame, 0, name=NAME)


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

    data_frame: DataFrame = CentroidsGeoDataFrame().to_data_frame(answers(response.features))
    assert_geo_centroid(data_frame, 0, name=NAME)


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
    boundary: DataFrame = BoundariesGeoDataFrame().to_data_frame(answers(response.features))
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
    boundary: DataFrame = BoundariesGeoDataFrame().to_data_frame(answers(response.features))
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
    boundary: DataFrame = BoundariesGeoDataFrame().to_data_frame(answers(response.features))
    assert_geo_multiboundary(boundary, index=0, multipolygon=[[r0], [r1, r2]])
    assert_geo_multiboundary(boundary, index=1, multipolygon=[[r3]])


def assert_geo_limit(limit: DataFrame, index: int, name=NAME, found_name=FOUND_NAME):
    assert isinstance(limit, GeoDataFrame)
    assert_names(limit, index, name, found_name)

    bounds = limit.geometry[index].bounds
    assert GEO_RECT_MIN_LON == bounds[0]
    assert GEO_RECT_MIN_LAT == bounds[1]
    assert GEO_RECT_MAX_LON == bounds[2]
    assert GEO_RECT_MAX_LAT == bounds[3]


def assert_geo_centroid(centroid: DataFrame, index: int, name=NAME, found_name=FOUND_NAME, lon=CENTROID_LON, lat=CENTROID_LAT):
    assert isinstance(centroid, GeoDataFrame)
    assert_names(centroid, index, name, found_name)
    assert lon == centroid.geometry[index].x
    assert lat == centroid.geometry[index].y


def assert_geo_boundary(boundary: DataFrame, index: int, polygon: GJPolygon, name=NAME, found_name=FOUND_NAME):
    assert isinstance(boundary, GeoDataFrame)
    assert_names(boundary, index, name, found_name)
    assert_geo_polygon(boundary.geometry[index], polygon)


def assert_geo_multiboundary(boundary: DataFrame, index: int, multipolygon: GJMultipolygon, name=NAME, found_name=FOUND_NAME):
    assert isinstance(boundary, GeoDataFrame)
    assert_names(boundary, index, name, found_name)
    assert_geo_multipolygon(boundary.geometry[index], multipolygon)


def assert_geo_multipolygon(geo_multipolygon, multipolygon: GJMultipolygon):
    for i, geo_polygon in enumerate(geo_multipolygon.geoms):
        assert_geo_polygon(geo_polygon, multipolygon[i])


def assert_geo_polygon(geo_polygon, polygon: GJPolygon):
    assert_geo_ring(geo_polygon.exterior.coords, polygon[0])

    for i, interior in enumerate(geo_polygon.interiors):
        assert_geo_ring(interior.coords, polygon[1 + i])


def assert_geo_ring(geo_ring, ring: GJRing):
    for i, point in enumerate(ring):
        assert lon(point) == geo_ring[i][0]
        assert lat(point) == geo_ring[i][1]

def answer(feature: GeocodedFeature) -> Answer:
    return Answer(feature.query, [feature])

def answers(features: List[GeocodedFeature]) -> List[Answer]:
    return [Answer(feature.query, [feature]) for feature in features]

