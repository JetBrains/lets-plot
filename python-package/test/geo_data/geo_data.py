#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import json
from typing import List, Union, Callable, Any

from lets_plot.geo_data import DF_ID, DF_REQUEST, DF_FOUND_NAME, DF_PARENT_COUNTY, DF_PARENT_STATE, DF_PARENT_COUNTRY
from lets_plot.geo_data.gis.geometry import Ring, Polygon, Multipolygon
from lets_plot.geo_data.gis.json_response import ResponseField, GeometryKind
from lets_plot.geo_data.gis.request import RegionQuery
from lets_plot.geo_data.gis.response import Answer, GeocodedFeature, FeatureBuilder, LevelKind, Status, GeoRect, GeoPoint, \
    Response, SuccessResponse, AmbiguousResponse, ErrorResponse, ResponseBuilder
from lets_plot.geo_data.geocodes import Geocodes

from pandas import DataFrame
from shapely.geometry import Point

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


def assert_row(
        df,
        index: int = 0,
        names: Union[str, List] = IGNORED,
        found_name: Union[str, List] = IGNORED,
        id: Union[str, List] = IGNORED,
        county: Union[str, List] = IGNORED,
        state: Union[str, List] = IGNORED,
        country: Union[str, List] = IGNORED,
        lon=None,
        lat=None
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


    assert_str(DF_ID, id)
    assert_str(DF_REQUEST, names)
    assert_str(DF_FOUND_NAME, found_name)
    assert_str(DF_PARENT_COUNTY, county)
    assert_str(DF_PARENT_STATE, state)
    assert_str(DF_PARENT_COUNTRY, country)
    if lon is not None:
        assert Point(df.geometry[index]).x == lon, 'lon {} != {}'.format(lon, Point(df.geometry[index]).x)

    if lat is not None:
        assert Point(df.geometry[index]).y == lat, 'lat {} != {}'.format(lat, Point(df.geometry[index]).y)


def assert_found_names(df: DataFrame, names: List[str]):
    assert names == df[DF_FOUND_NAME].tolist()

def make_geocode_region(request: str, name: str, geo_object_id: str, highlights: List[str], level_kind: LevelKind = LevelKind.city) -> Geocodes:
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


def get_data_meta(plotSpec, layerIdx: int) -> dict:
    return plotSpec.as_dict()['layers'][layerIdx]['data_meta']


def get_map_data_meta(plotSpec, layerIdx: int) -> dict:
    return plotSpec.as_dict()['layers'][layerIdx]['map_data_meta']



def assert_names(df: DataFrame, index: int, name=FOUND_NAME, found_name=FOUND_NAME):
    assert name == df[DF_REQUEST][index]
    assert found_name == df[DF_FOUND_NAME][index]


def assert_limit(limit: DataFrame, index: int, name=FOUND_NAME, found_name=FOUND_NAME):
    assert_names(limit, index, name, found_name)

    min_lon = limit['lonmin'][index]
    min_lat = limit['latmin'][index]
    max_lon = limit['lonmax'][index]
    max_lat = limit['latmax'][index]
    assert GEO_RECT_MIN_LON == min_lon
    assert GEO_RECT_MIN_LAT == min_lat
    assert GEO_RECT_MAX_LON == max_lon
    assert GEO_RECT_MAX_LAT == max_lat


def feature_to_answer(feature: GeocodedFeature) -> Answer:
    return Answer([feature])

def features_to_answers(features: List[GeocodedFeature]) -> List[Answer]:
    return [Answer([feature]) for feature in features]

def features_to_queries(features: List[GeocodedFeature]) -> List[RegionQuery]:
    return [RegionQuery(feature.name) for feature in features]