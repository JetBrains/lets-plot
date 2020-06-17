#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import json
from typing import List, Union

from lets_plot.geo_data.gis.geometry import Ring, Polygon, Multipolygon
from lets_plot.geo_data.gis.json_response import ResponseField, GeometryKind
from lets_plot.geo_data.gis.response import GeocodedFeature, FeatureBuilder, LevelKind, Status, GeoRect, GeoPoint, \
    Response, \
    SuccessResponse, AmbiguousResponse, ErrorResponse, ResponseBuilder
from lets_plot.geo_data.regions import Regions

GEOJSON_TYPE = ResponseField.boundary_type.value
GEOJSON_COORDINATES = ResponseField.boundary_coordinates.value

GEO_OBJECT_ID: str = '777'
LEVEL: LevelKind = LevelKind.county
TEXAS: str = 'Texas'
REQUEST: str = 'request'
ID: str = 'iddd'
NAME: str = 'rrr'
OTHER_NAME: str = 'otherrr name'
FOUND_NAME: str = 'a'
MESSAGE = 'msg'
ERROR_MESSAGE = 'error msg'

SUCCESS = Status.success.value
AMBIGUOUS = Status.ambiguous.value
ERROR = Status.error.value

GJPoint = List[float]
GJRing = List[GJPoint]
GJPolygon = List[GJRing]
GJMultipolygon = List[GJPolygon]

CENTROID_LAT = 7
CENTROID_LON = 5

GEO_RECT_MIN_LON: float = 5
GEO_RECT_MIN_LAT: float = 1
GEO_RECT_MAX_LON: float = 9
GEO_RECT_MAX_LAT: float = 7


def make_geocode_region(request: str, name: str, geo_object_id: str, highlights: List[str]) -> Regions:
    return Regions([make_region(request, name, geo_object_id, highlights)])


def make_region(request: str, name: str, geo_object_id: str, highlights: List[str]) -> GeocodedFeature:
    return FeatureBuilder() \
        .set_query(request) \
        .set_name(name) \
        .set_id(geo_object_id) \
        .set_highlights(highlights) \
        .build_geocoded()


def make_centroid_point() -> GeoPoint:
    return GeoPoint(CENTROID_LON, CENTROID_LAT)


def make_limit_rect() -> GeoRect:
    return GeoRect(GEO_RECT_MIN_LON, GEO_RECT_MIN_LAT, GEO_RECT_MAX_LON, GEO_RECT_MAX_LAT)


def make_position_rect() -> GeoRect:
    return GeoRect(GEO_RECT_MIN_LON, GEO_RECT_MIN_LAT, GEO_RECT_MAX_LON, GEO_RECT_MAX_LAT)


def make_point_boundary(lon: float, lat: float) -> Multipolygon:
    return Multipolygon(
        [
            Polygon(
                [
                    Ring(
                        [
                            GeoPoint(lon, lat)
                        ]
                    )
                ]
            )
        ]
    )


def make_single_point_boundary(lon: float, lat: float) -> GeoPoint:
    return GeoPoint(lon, lat)


def make_ring_bondary(ring: GJRing) -> Ring:
    return Ring([GeoPoint(lon(p), lat(p)) for p in ring])


def make_polygon_boundary(coordinates: GJPolygon) -> Polygon:
    return Polygon([make_ring_bondary(ring) for ring in coordinates])


def make_multipolygon_boundary(coordinates: GJMultipolygon) -> Multipolygon:
    return Multipolygon([make_polygon_boundary(poly) for poly in coordinates])


def format_polygon(coordinates: GJPolygon) -> str:
    return json.dumps({
        GEOJSON_TYPE: GeometryKind.polygon.value,
        GEOJSON_COORDINATES: coordinates
    })


def format_multipolygon(coordinates: GJMultipolygon) -> str:
    return json.dumps({
        GEOJSON_TYPE: GeometryKind.multipolygon.value,
        GEOJSON_COORDINATES: coordinates
    })


def make_success_response() -> ResponseBuilder:
    return ResponseBuilder() \
        .set_status(Status.success) \
        .set_message(MESSAGE) \
        .set_level(LEVEL)


def make_ambiguous_response() -> ResponseBuilder:
    return ResponseBuilder() \
        .set_status(Status.ambiguous) \
        .set_message(MESSAGE) \
        .set_level(LEVEL)


def make_error_response() -> ResponseBuilder:
    return ResponseBuilder() \
        .set_status(Status.error) \
        .set_message(MESSAGE)


def assert_success_response(response: Union[Response]):
    assert isinstance(response, SuccessResponse)
    assert MESSAGE == response.message
    assert LEVEL == response.level


def assert_ambiguous_response(response: Union[Response]):
    assert isinstance(response, AmbiguousResponse)
    assert LEVEL == response.level
    assert MESSAGE == response.message


def assert_error_response(response: Union[Response]):
    assert isinstance(response, ErrorResponse)
    assert ERROR_MESSAGE == response.message


def point(lon: float, lat: float) -> GJPoint:
    return [lon, lat]


def ring(*points: GJPoint) -> GJRing:
    return [p for p in points]


def polygon(*rings: GJRing) -> GJPolygon:
    polygon: GJPolygon = []
    for r in rings:
        polygon.append(r)
    return polygon


def multipolygon(*polygons: GJPolygon) -> GJMultipolygon:
    multipolygon: GJMultipolygon = []
    for p in polygons:
        multipolygon.append(p)
    return multipolygon


def lon(p: GJPoint) -> float:
    return p[0]


def lat(p: GJPoint) -> float:
    return p[1]


def get_data_meta(plotSpec, layerIdx: int) -> dict:
    return plotSpec.as_dict()['layers'][layerIdx]['data_meta']


def get_map_data_meta(plotSpec, layerIdx: int) -> dict:
    return plotSpec.as_dict()['layers'][layerIdx]['map_data_meta']
