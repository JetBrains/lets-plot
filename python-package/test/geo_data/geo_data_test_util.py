#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import json
from typing import List, Union, Callable, Any

import shapely
from geopandas import GeoDataFrame
from shapely.geometry import Point

from lets_plot.geo_data import DF_COLUMN_ID, DF_COLUMN_FOUND_NAME, DF_COLUMN_COUNTY, DF_COLUMN_STATE, DF_COLUMN_COUNTRY
from lets_plot.geo_data.geocodes import Geocodes
from lets_plot.geo_data.gis.geometry import Ring, Polygon, Multipolygon
from lets_plot.geo_data.gis.json_response import ResponseField, GeometryKind
from lets_plot.geo_data.gis.request import RegionQuery
from lets_plot.geo_data.gis.response import Answer, GeocodedFeature, FeatureBuilder, LevelKind, Status, GeoRect, \
    GeoPoint, \
    Response, SuccessResponse, AmbiguousResponse, ErrorResponse, ResponseBuilder

GEOJSON_TYPE = ResponseField.boundary_type.value
GEOJSON_COORDINATES = ResponseField.boundary_coordinates.value

LEVEL: LevelKind = LevelKind.county
ID: str = 'iddd'
NAME: str = 'rrr'
FOUND_NAME: str = 'a'
MESSAGE = 'msg'
ERROR_MESSAGE = 'error msg'

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

COLUMN_NAME_CITY = 'city'


def run_intergration_tests() -> bool:
    import os
    if 'RUN_GEOCODING_INTEGRATION_TEST' in os.environ.keys():
        return os.environ.get('RUN_GEOCODING_INTEGRATION_TEST').lower() == 'true'
    return False


NO_COLUMN = '<no column>'
IGNORED = '__value_ignored__'


def assert_error(message: str, action: Callable[[], Any]):
    assert isinstance(message, str)
    try:
        action()
        assert False, 'No error, but expected: {}'.format(message)
    except Exception as e:
        assert message == str(e), "'{}' != '{}'".format(message, str(e))


def assert_request_and_found_name_are_equal(df, r=None):
    if r is None:
        r = range(len(df))

    assert df[get_request_column_name(df)].tolist()[r.start:r.stop] == df[DF_COLUMN_FOUND_NAME].tolist()[r.start:r.stop]


def get_request_column_name(df) -> str:
    if COLUMN_NAME_CITY in df.columns:
        return COLUMN_NAME_CITY
    elif DF_COLUMN_COUNTY in df.columns:
        return DF_COLUMN_COUNTY
    elif DF_COLUMN_STATE in df.columns:
        return DF_COLUMN_STATE
    elif DF_COLUMN_COUNTRY in df.columns:
        return DF_COLUMN_COUNTRY
    else:
        raise ValueError('Magic state - no expected columns')


def assert_row(
        df,
        index: int = 0,
        names: Union[str, List] = IGNORED,
        found_name: Union[str, List] = IGNORED,
        id: Union[str, List] = IGNORED,
        city: Union[str, List] = IGNORED,
        county: Union[str, List] = IGNORED,
        state: Union[str, List] = IGNORED,
        country: Union[str, List] = IGNORED,
        lon=None,
        lat=None,
        lon_min=None,
        lon_max=None,
        lat_min=None,
        lat_max=None,
        boundary=None
):
    def assert_str(column, expected):
        if expected == IGNORED:
            return

        if expected == NO_COLUMN:
            assert column not in df.columns.tolist()
            return

        if isinstance(expected, str):
            assert expected == df[column][index], '{} != {}'.format(expected, df[column][index])
            return

        if isinstance(expected, list):
            actual = df[column][index:index + len(expected)].tolist()
            assert actual == expected, '{} != {}'.format(expected, actual)
            return

        raise ValueError('Not support type of expected: {}'.format(str(type(expected))))

    assert_str(get_request_column_name(df), names)
    assert_str(DF_COLUMN_ID, id)
    assert_str(DF_COLUMN_FOUND_NAME, found_name)
    assert_str(COLUMN_NAME_CITY, city)
    assert_str(DF_COLUMN_COUNTY, county)
    assert_str(DF_COLUMN_STATE, state)
    assert_str(DF_COLUMN_COUNTRY, country)
    if lon is not None:
        assert Point(df.geometry[index]).x == lon, 'lon {} != {}'.format(lon, Point(df.geometry[index]).x)

    if lat is not None:
        assert Point(df.geometry[index]).y == lat, 'lat {} != {}'.format(lat, Point(df.geometry[index]).y)

    if any([v is not None for v in [lon_min, lon_max, lat_min, lat_max]]):
        if isinstance(df, GeoDataFrame):
            bounds = df.geometry[index].bounds

            if lon_min is not None:
                assert lon_min == bounds[0]

            if lat_min is not None:
                assert lat_min == bounds[1]

            if lon_max is not None:
                assert lon_max == bounds[2]

            if lat_max is not None:
                assert lat_max == bounds[3]
        else:
            assert GEO_RECT_MIN_LON == df.lonmin[index]
            assert GEO_RECT_MIN_LAT == df.latmin[index]
            assert GEO_RECT_MAX_LON == df.lonmax[index]
            assert GEO_RECT_MAX_LAT == df.latmax[index]

    if boundary is not None:
        def assert_geo_multipolygon(geo_multipolygon, multipolygon: GJMultipolygon):
            for i, geo_polygon in enumerate(geo_multipolygon.geoms):
                assert_geo_polygon(geo_polygon, multipolygon[i])

        def assert_geo_polygon(geo_polygon, polygon: GJPolygon):
            assert_geo_ring(geo_polygon.exterior.coords, polygon[0])

            for i, interior in enumerate(geo_polygon.interiors):
                assert_geo_ring(interior.coords, polygon[1 + i])

        def assert_geo_ring(geo_ring, ring: GJRing):
            for i, point in enumerate(ring):
                assert point[0] == geo_ring[i][0]  # lon
                assert point[1] == geo_ring[i][1]  # lat

        geometry = df.geometry[index]
        if isinstance(geometry, shapely.geometry.Polygon):
            assert_geo_polygon(geometry, boundary)

        if isinstance(geometry, shapely.geometry.MultiPolygon):
            assert_geo_multipolygon(geometry, boundary)


def make_geocode_region(request: str, name: str, geo_object_id: str, highlights: List[str],
                        level_kind: LevelKind = LevelKind.city) -> Geocodes:
    return Geocodes(
        level_kind=level_kind,
        queries=[RegionQuery(request=request)],
        answers=[make_answer(name, geo_object_id, highlights)]
    )


def make_answer(name: str, geo_object_id: str, highlights: List[str]) -> Answer:
    return Answer([FeatureBuilder() \
                  .set_name(name) \
                  .set_id(geo_object_id) \
                  .set_highlights(highlights) \
                  .set_centroid(GeoPoint(0, 0)) \
                  .set_position(GeoRect(0, 0, 0, 0)) \
                  .set_limit(GeoRect(0, 0, 0, 0)) \
                  .build_geocoded()
                   ]
                  )


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


def make_single_point_boundary(lon: float = 0, lat: float = 0) -> GeoPoint:
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


def feature_to_answer(feature: GeocodedFeature) -> Answer:
    return Answer([feature])


def features_to_answers(features: List[GeocodedFeature]) -> List[Answer]:
    return [Answer([feature]) for feature in features]


def features_to_queries(features: List[GeocodedFeature]) -> List[RegionQuery]:
    return [RegionQuery(feature.name) for feature in features]
