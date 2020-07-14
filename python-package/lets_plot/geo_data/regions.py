from abc import abstractmethod
from typing import List, Dict, Optional, Union

from pandas import DataFrame, Series

from .gis.geocoding_service import GeocodingService
from .gis.request import PayloadKind, RequestBuilder, RequestKind, MapRegion
from .gis.response import GeocodedFeature, Namesake, AmbiguousFeature, LevelKind
from .gis.response import SuccessResponse, Response, AmbiguousResponse, ErrorResponse
from .type_assertion import assert_type
from .._type_utils import CanToDataFrame

NO_OBJECTS_FOUND_EXCEPTION_TEXT = 'No objects were found.'
MULTIPLE_OBJECTS_FOUND_EXCEPTION_TEXT = "Multiple objects were found. Use all_result=True to see them."

DF_REQUEST = 'request'
DF_ID = 'id'
DF_FOUND_NAME = 'found name'
DF_HIGHLIGHTS = 'highlights'
DF_GROUP = 'group'


class DataFrameProvider():
    def __init__(self):
        self._request: List[str] = []
        self._found_name: List[str] = []

    @abstractmethod
    def to_data_frame(self, features: List[GeocodedFeature]) -> DataFrame:
        raise ValueError('Not implemented')

    def _extend_names(self, query: str, name: str, size: int):
        query = self._select_not_empty_string(query, name)
        self._request.extend([query] * size)
        self._found_name.extend([name] * size)

    def _get_request(self, feature: GeocodedFeature) -> str:
        return feature.query

    def _get_found_name(self, feature: GeocodedFeature) -> str:
        return feature.name

    def _select_not_empty_string(self, first: str, second: str) -> str:
        if first is None or first == '':
            return second
        return first


class Regions(CanToDataFrame):
    @staticmethod
    def _select_not_empty_name(feature: GeocodedFeature) -> str:
        return feature.name if feature.query is None or feature.query == '' else feature.query

    def __init__(self, features: List[GeocodedFeature], highlights: bool = False):
        self._geocoded_features: List[GeocodedFeature] = features
        self._highlights: bool = highlights

    def __repr__(self):
        return self.to_data_frame().to_string()

    def as_list(self) -> List['Regions']:
        return [Regions([feature], self._highlights) for feature in self._geocoded_features]

    def unique_ids(self) -> List[str]:
        seen = set()
        seen_add = seen.add
        return [feature.id for feature in self._geocoded_features if not (feature.id in seen or seen_add(feature.id))]

    def boundaries(self, resolution=None):
        from lets_plot.geo_data.to_geo_data_frame import BoundariesGeoDataFrame
        return self._execute(
            self._request_builder(PayloadKind.boundaries)
                .set_resolution(_to_resolution(resolution)),
            BoundariesGeoDataFrame()
        )

    def limits(self):
        from lets_plot.geo_data.to_geo_data_frame import LimitsGeoDataFrame
        return self._execute(
            self._request_builder(PayloadKind.limits),
            LimitsGeoDataFrame()
        )

    def centroids(self):
        from lets_plot.geo_data.to_geo_data_frame import CentroidsGeoDataFrame
        return self._execute(
            self._request_builder(PayloadKind.centroids),
            CentroidsGeoDataFrame()
        )

    # implements abstract in CanToDataFrame
    def to_data_frame(self) -> DataFrame:
        keyMappers: Dict = {
            DF_REQUEST: lambda feature: self._select_not_empty_name(feature),
            DF_ID: lambda feature: feature.id,
            DF_FOUND_NAME: lambda feature: feature.name,
            DF_HIGHLIGHTS: lambda feature: feature.highlights
        }

        keyList: List[str] = [DF_REQUEST, DF_ID, DF_FOUND_NAME]

        if self._highlights:
            keyList.append(DF_HIGHLIGHTS)

        data: Dict = {}
        for key in keyList:
            data[key] = [keyMappers[key](feature) for feature in self._geocoded_features]

        return DataFrame(data, columns=keyList)

    def __len__(self):
        return len(self._geocoded_features)

    def _execute(self, request_builder: RequestBuilder, df_converter):
        response = GeocodingService().do_request(request_builder.build())

        if not isinstance(response, SuccessResponse):
            _raise_exception(response)

        self._join_payload(response.features)

        return df_converter.to_data_frame(self._geocoded_features)

    def _request_builder(self, payload_kind: PayloadKind) -> RequestBuilder:
        assert_type(payload_kind, PayloadKind)

        return RequestBuilder() \
            .set_request_kind(RequestKind.explicit) \
            .set_ids(self.unique_ids()) \
            .set_requested_payload([payload_kind])

    def _join_payload(self, payloads: List[GeocodedFeature]):
        for payload in payloads:
            for feature in self._get_features(payload.id):

                if payload.limit is not None:
                    feature.limit = payload.limit

                if payload.boundary is not None:
                    feature.boundary = payload.boundary

                if payload.centroid is not None:
                    feature.centroid = payload.centroid

                if payload.position is not None:
                    feature.position = payload.position

    def _get_features(self, feature_id: str) -> List[GeocodedFeature]:
        return [feature for feature in self._geocoded_features if feature.id == feature_id]


request_types = Optional[Union[str, List[str], Series]]
scope_types = Optional[Union[str, List[str], Regions, List[Regions]]]


def _raise_exception(response: Response):
    msg = _format_error_message(response)
    raise ValueError(msg)


def _format_error_message(response: Response) -> str:
    if isinstance(response, AmbiguousResponse):
        not_found_names: List[str] = []
        multiple_objects: List[AmbiguousFeature] = []

        for ambiguous_feature in response.features:
            if ambiguous_feature.total_namesake_count == 0:
                not_found_names.append(ambiguous_feature.query)

            if ambiguous_feature.total_namesake_count > 0:
                multiple_objects.append(ambiguous_feature)

        if len(not_found_names) > 0:
            return 'No objects were found for {}.\n'.format(', '.join(not_found_names))

        if len(multiple_objects) > 0:
            message = ''
            for multiple_object in multiple_objects:
                message += _create_multiple_error_message(
                    multiple_object.query,
                    multiple_object.namesake_examples,
                    multiple_object.total_namesake_count
                ) + '\n'

            return message

        return 'Invalid bad feature'

    if isinstance(response, ErrorResponse):
        return 'Error: ' + response.message

    return 'Unsupported error response status: ' + str(response.__class__)


def _create_multiple_error_message(request: str, namesakes: List[Namesake], total_namesake_count: int):
    lines = []
    for namesake in namesakes:
        line = '- ' + namesake.name
        if len(namesake.parents) > 0:
            line += ' (' + ', '.join([o.name for o in namesake.parents]) + ')'
        lines.append(line)

    text = 'Multiple objects (' + str(total_namesake_count) + ') were found for ' + request
    if not lines:
        text += '.'
    else:
        text += ':\n' + '\n'.join(lines)
    return text


def _to_level_kind(level_kind: Optional[Union[str, LevelKind]]) -> Optional[LevelKind]:
    if level_kind is None:
        return None

    if isinstance(level_kind, LevelKind):
        return level_kind

    if isinstance(level_kind, str):
        return LevelKind(level_kind)

    raise ValueError('Invalid level kind')


def _to_scope(location: scope_types) -> Optional[Union[List[MapRegion], MapRegion]]:
    if location is None:
        return None

    def _make_region(obj: Union[str, Regions]) -> Optional[MapRegion]:
        if isinstance(obj, Regions):
            return MapRegion.with_ids(obj.unique_ids())

        if isinstance(obj, str):
            return MapRegion.with_name(obj)

        raise ValueError('Invalid region: ' + obj)

    if isinstance(location, list):
        return [_make_region(obj) for obj in location]

    return _make_region(location)


def _ensure_is_list(obj: request_types) -> Optional[List[str]]:
    if obj is None:
        return None

    if isinstance(obj, list):
        return obj

    if isinstance(obj, str):
        return [obj]

    if isinstance(obj, Series):
        return obj.tolist()

    raise ValueError("Wrong type")


def _to_resolution(res: Optional[int]) -> Optional[int]:
    if res is None:
        return None

    if isinstance(res, int):
        if 1 <= res <= 15:
            return res

    raise ValueError("Invalid resolution value: " + str(res))
