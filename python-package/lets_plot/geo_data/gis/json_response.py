import json
from enum import Enum
from functools import partial
from typing import Dict, List, Optional, Union

from .fluent_dict import FluentDict
from .geometry import Ring
from .response import Multipolygon, GeoPoint, GeoRect, Boundary, Polygon
from .response import Response, ResponseBuilder, SuccessResponse, AmbiguousResponse
from .response import Status, LevelKind, Answer, GeocodedFeature, AmbiguousFeature, Namesake, NamesakeParent, \
    FeatureBuilder


class ResponseField(Enum):
    status = 'status'
    message = 'message'
    data = 'data'
    answers = 'answers'
    features = 'features'
    geocoded_data = 'good_features'
    incorrect_data = 'bad_features'
    level = 'level'
    position = 'position'
    query = 'query'
    name = 'name'
    highlights = 'highlights'
    limit = 'limit'
    centroid = 'centroid'
    boundary = 'boundary'
    boundary_type = 'type'
    geo_object_id = 'id'
    boundary_lon = 'lon'
    boundary_lat = 'lat'
    boundary_coordinates = 'coordinates'
    centroid_lon = 'lon'
    centroid_lat = 'lat'
    min_lon = 'min_lon'
    min_lat = 'min_lat'
    max_lon = 'max_lon'
    max_lat = 'max_lat'
    total_namesake_count = 'total_namesake_count'
    namesake_examples = 'namesake_examples'
    namesake_name = 'name'
    namesake_parents = 'parents'
    namesake_parent_name = 'name'
    namesake_parent_level = 'level'


class GeometryKind(Enum):
    point = 'Point'
    polygon = 'Polygon'
    multipolygon = 'MultiPolygon'


class ResponseParser:

    @staticmethod
    def parse(response_json: Dict) -> Response:
        response = ResponseBuilder()
        response_dict = FluentDict(response_json) \
            .visit_enum(ResponseField.status, Status, response.set_status) \
            .visit_str(ResponseField.message, response.set_message)

        if response.status == Status.error:
            return response.build()

        data_dict = FluentDict(response_dict.get(ResponseField.data)) \
            .visit_enum_existing(ResponseField.level, LevelKind, response.set_level)

        if response.status == Status.success:
            data_dict.visit(ResponseField.answers, partial(ResponseParser._parse_answers, response=response))
        elif response.status == Status.ambiguous:
            data_dict.visit(ResponseField.features, partial(ResponseParser._parse_ambiguous_features, response=response))
        else:
            raise ValueError('Unknown response kind')

        return response.build()

    @staticmethod
    def _parse_answers(answers_json: List[Dict], response: ResponseBuilder):
        answers: List[Answer] = []
        for answer_json in answers_json:
            features_json = answer_json.get(ResponseField.features.value, [])
            geocoded_features: List[GeocodedFeature] = []
            for feature_json in features_json:
                feature = FeatureBuilder()

                FluentDict(feature_json) \
                    .visit_str(ResponseField.geo_object_id, feature.set_id) \
                    .visit_str(ResponseField.name, feature.set_name) \
                    .visit_str_list_optional(ResponseField.highlights, feature.set_highlights) \
                    .visit_str_existing(ResponseField.boundary, lambda json: feature.set_boundary(GeoJson().parse_geometry(json))) \
                    .visit_object_optional(ResponseField.centroid, lambda json: feature.set_centroid(ResponseParser._parse_point(json))) \
                    .visit_object_optional(ResponseField.limit, lambda json: feature.set_limit(ResponseParser._parse_rect(json))) \
                    .visit_object_optional(ResponseField.position, lambda json: feature.set_position(ResponseParser._parse_rect(json)))

                geocoded_features.append(feature.build_geocoded())
            answers.append(Answer(geocoded_features))

        response.set_answers(answers)

    @staticmethod
    def _parse_ambiguous_features(features_json: List[Dict], response: ResponseBuilder):
        ambiguous_features: List[AmbiguousFeature] = []
        for feature_json in features_json:
            feature = FeatureBuilder()
            FluentDict(feature_json) \
                .visit_str(ResponseField.query, feature.set_query) \
                .visit_int(ResponseField.total_namesake_count, feature.set_total_namesake_count) \
                .visit_objects(ResponseField.namesake_examples, lambda json: feature.add_namesake(ResponseParser._parse_namesake(json)))

            ambiguous_features.append(feature.build_ambiguous())

        response.set_ambiguous_features(ambiguous_features)

    @staticmethod
    def _parse_point(centroid_dict: FluentDict) -> GeoPoint:
        return GeoPoint(
            centroid_dict.get(ResponseField.centroid_lon),
            centroid_dict.get(ResponseField.centroid_lat)
        )

    @staticmethod
    def _parse_rect(rect_dict: FluentDict) -> GeoRect:
        return GeoRect(
            rect_dict.get(ResponseField.min_lon),
            rect_dict.get(ResponseField.min_lat),
            rect_dict.get(ResponseField.max_lon),
            rect_dict.get(ResponseField.max_lat),
        )

    @staticmethod
    def _parse_namesake(namesake_dict: FluentDict):
        return Namesake(
            namesake_dict.get(ResponseField.namesake_name),
            namesake_dict.get_objects(ResponseField.namesake_parents)
                .map(
                lambda parent: NamesakeParent(
                    parent.get(ResponseField.namesake_parent_name),
                    parent.get_enum(ResponseField.namesake_parent_level, LevelKind)))
                .list()
        )


class ResponseFormatter:

    @staticmethod
    def format(response: Response) -> Dict:
        if isinstance(response, SuccessResponse):
            return FluentDict() \
                .put(ResponseField.status, Status.success.value) \
                .put(ResponseField.message, response.message) \
                .put(ResponseField.data, FluentDict()
                     .put(ResponseField.level, response.level.value)
                     .put(ResponseField.answers, list(map(ResponseFormatter._format_answer, response.answers)))) \
                .to_dict()
        elif isinstance(response, AmbiguousResponse):
            return FluentDict() \
                .put(ResponseField.status, Status.ambiguous.value) \
                .put(ResponseField.message, response.message) \
                .put(ResponseField.data, FluentDict()
                     .put(ResponseField.level, response.level.value)
                     .put(ResponseField.features, list(map(ResponseFormatter._format_ambiguous_feature, response.features)))) \
                .to_dict()

    @staticmethod
    def _format_answer(answer: Answer) -> Dict:
        features = []
        for feature in answer.features:
            features.append(
                FluentDict() \
                .put(ResponseField.geo_object_id, feature.id) \
                .put(ResponseField.name, feature.name) \
                .put(ResponseField.boundary, ResponseFormatter._format_boundary(feature.boundary)) \
                .put(ResponseField.centroid, ResponseFormatter._format_centroid(feature.centroid)) \
                .put(ResponseField.limit, ResponseFormatter._format_rect(feature.limit)) \
                .put(ResponseField.position, ResponseFormatter._format_rect(feature.position)) \
                .to_dict()
            )

        return FluentDict() \
            .put(ResponseField.features, features) \
            .to_dict()


    @staticmethod
    def _format_centroid(point: Optional[GeoPoint]) -> Optional[Dict]:
        if point is None:
            return None

        return FluentDict() \
            .put(ResponseField.centroid_lon, point.lon) \
            .put(ResponseField.centroid_lat, point.lat) \
            .to_dict()

    @staticmethod
    def _format_rect(rect: Optional[GeoRect]) -> Optional[Dict]:
        if rect is None:
            return None

        return FluentDict() \
            .put(ResponseField.min_lon, rect.start_lon) \
            .put(ResponseField.min_lat, rect.min_lat) \
            .put(ResponseField.max_lon, rect.end_lon) \
            .put(ResponseField.max_lat, rect.max_lat) \
            .to_dict()

    @staticmethod
    def _format_boundary(boundary: Optional[Boundary]) -> Optional[str]:
        if boundary is None:
            return None

        return GeoJson.format_geometry(boundary)

    @staticmethod
    def _format_ambiguous_feature(feaure: AmbiguousFeature) -> Dict:
        return FluentDict() \
            .put(ResponseField.query, feaure.query) \
            .put(ResponseField.total_namesake_count, feaure.total_namesake_count) \
            .put(ResponseField.namesake_examples, list(map(ResponseFormatter._format_namesake, feaure.namesake_examples))) \
            .to_dict()

    @staticmethod
    def _format_namesake(namesake: Namesake) -> Dict:
        return FluentDict() \
            .put(ResponseField.namesake_name, namesake.name) \
            .put(ResponseField.namesake_parents, list(map(ResponseFormatter._format_namesake_parent, namesake.parents))) \
            .to_dict()

    @staticmethod
    def _format_namesake_parent(parent: NamesakeParent) -> Dict:
        return FluentDict() \
            .put(ResponseField.namesake_parent_name, parent.name) \
            .put(ResponseField.namesake_parent_level, parent.level) \
            .to_dict()


class GeoJson:

    def __init__(self):
        self.lon_list: List[float] = []
        self.lat_list: List[float] = []

    @staticmethod
    def parse_geometry(geometry_line: str) -> Union[Multipolygon, Polygon, GeoPoint]:
        geoJson = GeoJson()
        return geoJson._do_parse(geometry_line)

    @staticmethod
    def format_geometry(boundary: Boundary) -> str:
        if isinstance(boundary.geometry, GeoPoint):
            return json.dumps({
                ResponseField.boundary_type.value: GeometryKind.point.value,
                ResponseField.boundary_coordinates.value: [boundary.geometry.lon, boundary.geometry.lat]
            })

        if isinstance(boundary.geometry, Polygon):
            return json.dumps({
                ResponseField.boundary_type.value: GeometryKind.polygon.value,
                ResponseField.boundary_coordinates.value: GeoJson._format_polygon(boundary.geometry)
            })

        if isinstance(boundary.geometry, Multipolygon):
            return json.dumps({
                ResponseField.boundary_type.value: GeometryKind.polygon.value,
                ResponseField.boundary_coordinates.value: [GeoJson._format_polygon(poly) for poly in boundary.geometry.polygons]
            })

    @staticmethod
    def _format_polygon(polygon: Polygon):
        poly = []
        for ring in polygon.rings:
            poly.append([[p.lon, p.lat] for p in ring.points])
        return poly

    def _do_parse(self, geometry_line: str) -> Union[Multipolygon, Polygon, GeoPoint]:
        geometry_data: dict = json.loads(geometry_line)
        geometry_type: GeometryKind = GeometryKind(geometry_data[ResponseField.boundary_type.value])

        if geometry_type == GeometryKind.point:
            return self._parse_point(geometry_data)

        if geometry_type == GeometryKind.polygon:
            return self._parse_polygon(geometry_data[ResponseField.boundary_coordinates.value])

        if geometry_type == GeometryKind.multipolygon:
            return self._parse_multipolygon(geometry_data[ResponseField.boundary_coordinates.value])

        raise ValueError('Invalid geometry type')

    def _parse_multipolygon(self, geometry_data: List[List[List[List[float]]]]) -> Multipolygon:
        return Multipolygon([self._parse_polygon(p) for p in geometry_data])

    def _parse_polygon(self, geometry_data: List[List[List[float]]]) -> Polygon:
        rings: List[Ring] = []
        for ring in geometry_data:
            rings.append(Ring([GeoPoint(p[0], p[1]) for p in ring]))
        return Polygon(rings)

    def _parse_point(self, geometry_data: dict) -> GeoPoint:
        return GeoPoint(
            lon=geometry_data[ResponseField.boundary_lon.value],
            lat=geometry_data[ResponseField.boundary_lat.value]
        )

    def _add_point(self, lon: float, lat: float) -> None:
        self.lon_list.append(lon)
        self.lat_list.append(lat)
