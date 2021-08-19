import enum
from typing import List, Optional, Dict, Union

from .geometry import GeometryBase, GeoPoint, GeoRect, Polygon, Multipolygon
from .request import LevelKind
from ..type_assertion import assert_list_type, assert_optional_type, assert_type, assert_optional_list_type


class Status(enum.Enum):
    success = 'success'
    ambiguous = 'ambiguous'
    error = 'error'


class NamesakeParent:
    def __init__(self, name: str, level: LevelKind):
        assert_type(name, str)
        assert_type(level, LevelKind)

        self.name: str = name
        self.level: LevelKind = level


class Namesake:
    def __init__(self, name: str, parents: List[NamesakeParent]):
        assert_type(name, str)
        assert_list_type(parents, NamesakeParent)

        self.name: str = name
        self.parents: List[NamesakeParent] = parents


class Boundary(GeometryBase):
    def __init__(self, geometry: Union[Multipolygon, Polygon, GeoPoint]):
        self.geometry: Union[Multipolygon, Polygon, GeoPoint] = geometry


class GeocodedFeature:
    def __init__(self,
                 id: str, name: str,
                 highlights: Optional[List[str]] = None,
                 boundary: Optional[Boundary] = None,
                 centroid: Optional[GeoPoint] = None,
                 limit: Optional[GeoRect] = None,
                 position: Optional[GeoRect] = None):
        assert_type(id, str)
        assert_type(name, str)
        assert_optional_list_type(highlights, str)
        assert_optional_type(boundary, Boundary)
        assert_optional_type(centroid, GeoPoint)
        assert_optional_type(limit, GeoRect)
        assert_optional_type(position, GeoRect)

        self.id: str = id
        self.name: str = name
        self.highlights: Optional[List[str]] = highlights
        self.boundary: Optional[Boundary] = boundary
        self.centroid: Optional[GeoPoint] = centroid
        self.limit: Optional[GeoRect] = limit
        self.position: Optional[GeoRect] = position


class AmbiguousFeature:
    def __init__(self, query: str, total_namesake_count: int, namesake_examples: List[Namesake]):
        assert_type(query, str)
        assert_type(total_namesake_count, int)
        assert_list_type(namesake_examples, Namesake)

        self.query: str = query
        self.total_namesake_count: int = total_namesake_count
        self.namesake_examples: List[Namesake] = namesake_examples


class Response:
    def __init__(self, message: str):
        assert_type(message, str)
        self.message: str = message


class Answer:
    def __init__(self, features: List[GeocodedFeature]):
        assert_list_type(features, GeocodedFeature)
        self.features: List[GeocodedFeature] = features


class SuccessResponse(Response):
    def __init__(self, message: str, level: LevelKind, answers: List[Answer]):
        super().__init__(message)

        assert_type(message, str)
        assert_optional_type(level, LevelKind)
        assert_list_type(answers, Answer)

        self.level: LevelKind = level
        self.answers: List[Answer] = answers

        features = []
        for answer in answers:
            features.extend(answer.features)

        self.features: List[GeocodedFeature] = features


class AmbiguousResponse(Response):
    def __init__(self, message: str, level: LevelKind, features: List[AmbiguousFeature]):
        super().__init__(message)

        assert_type(message, str)
        assert_optional_type(level, LevelKind)
        assert_list_type(features, AmbiguousFeature)

        self.level: LevelKind = level
        self.features: List[AmbiguousFeature] = features


class ErrorResponse(Response):
    def __init__(self, message: str):
        super().__init__(message)


class FeatureBuilder:
    def __init__(self):
        self.query: Optional[str] = None
        self.id: Optional[str] = None
        self.name: Optional[str] = None
        self.highlights: Optional[List[str]] = None
        self.boundary: Optional[Boundary] = None
        self.centroid: Optional[GeoPoint] = None
        self.limit: Optional[GeoRect] = None
        self.position: Optional[GeoRect] = None
        self.total_namesake_count: Optional[int] = None
        self.namesake_examples: List[Namesake] = []

    def set_query(self, v: Optional[str]) -> 'FeatureBuilder':
        assert_optional_type(v, str)
        self.query = v
        return self

    def set_id(self, v: str) -> 'FeatureBuilder':
        assert_type(v, str)
        self.id = v
        return self

    def set_name(self, v: str) -> 'FeatureBuilder':
        assert_type(v, str)
        self.name = v
        return self

    def set_highlights(self, v: List[str]) -> 'FeatureBuilder':
        assert_list_type(v, str)
        self.highlights = v
        return self

    def set_boundary(self, v: Union[Multipolygon, Polygon, GeoPoint]) -> 'FeatureBuilder':
        assert_type(v, (Multipolygon, Polygon, GeoPoint))
        self.boundary = Boundary(v)
        return self

    def set_centroid(self, v: GeoPoint) -> 'FeatureBuilder':
        assert_type(v, GeoPoint)
        self.centroid = v
        return self

    def set_limit(self, v: GeoRect) -> 'FeatureBuilder':
        assert_type(v, GeoRect)
        self.limit = v
        return self

    def set_position(self, v: GeoRect) -> 'FeatureBuilder':
        assert_type(v, GeoRect)
        self.position = v
        return self

    def set_total_namesake_count(self, v: int) -> 'FeatureBuilder':
        assert_type(v, int)
        self.total_namesake_count = v
        return self

    def set_namesake_examples(self, v: List[Namesake]) -> 'FeatureBuilder':
        assert_list_type(v, Namesake)
        self.namesake_examples = v
        return self

    def add_namesake(self, namesake: Namesake) -> 'FeatureBuilder':
        assert_type(namesake, Namesake)
        self.namesake_examples.append(namesake)
        return self

    def build_ambiguous(self) -> AmbiguousFeature:
        return AmbiguousFeature(self.query, self.total_namesake_count, self.namesake_examples)

    def build_geocoded(self) -> GeocodedFeature:
        return GeocodedFeature(self.id, self.name, self.highlights, self.boundary, self.centroid, self.limit,
                               self.position)


class ResponseBuilder:
    def __init__(self):
        self.status: Status = None
        self.level: LevelKind = None
        self.message: str = None
        self.answers: List[Answer] = None
        self.ambiguous_features: List[AmbiguousFeature] = None
        self.data: Dict = None

    def set_status(self, v: Status) -> 'ResponseBuilder':
        assert_type(v, Status)
        self.status = v
        return self

    def set_level(self, v: LevelKind) -> 'ResponseBuilder':
        assert_type(v, LevelKind)
        self.level = v
        return self

    def set_message(self, v: str) -> 'ResponseBuilder':
        assert_type(v, str)
        self.message = v
        return self

    def set_ambiguous_features(self, v: List[AmbiguousFeature]) -> 'ResponseBuilder':
        assert_list_type(v, AmbiguousFeature)
        self.ambiguous_features = v
        return self

    def set_answers(self, v: List[Answer]) -> 'ResponseBuilder':
        assert_list_type(v, Answer)
        self.answers = v
        return self

    def set_geocoded_features(self, v: List[GeocodedFeature]):
        '''
        Exactly matching non-exploding features, i.e. one feature per answer
        '''
        assert_list_type(v, GeocodedFeature)
        self.answers = [Answer([f]) for f in v]
        return self

    def build(self) -> Response:
        if self.status == Status.error:
            return ErrorResponse(self.message)
        elif self.status == Status.success:
            return SuccessResponse(self.message, self.level, self.answers)
        elif self.status == Status.ambiguous:
            return AmbiguousResponse(self.message, self.level, self.ambiguous_features)
        else:
            raise ValueError('Unknown status: ' + str(self.status))
