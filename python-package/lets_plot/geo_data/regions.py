import enum
from abc import abstractmethod
from typing import List, Dict, Optional, Union

from pandas import DataFrame, Series

from .gis.geocoding_service import GeocodingService
from .gis.request import PayloadKind, RequestBuilder, RequestKind, MapRegion, RegionQuery
from .gis.response import Answer, GeocodedFeature, Namesake, AmbiguousFeature, LevelKind
from .gis.response import SuccessResponse, Response, AmbiguousResponse, ErrorResponse
from .type_assertion import assert_type, assert_list_type
from .._type_utils import CanToDataFrame

NO_OBJECTS_FOUND_EXCEPTION_TEXT = 'No objects were found.'
MULTIPLE_OBJECTS_FOUND_EXCEPTION_TEXT = "Multiple objects were found. Use all_result=True to see them."

DF_REQUEST = 'request'
DF_ID = 'id'
DF_FOUND_NAME = 'found name'
DF_HIGHLIGHTS = 'highlights'
DF_GROUP = 'group'
DF_PARENT_COUNTRY = 'country'
DF_PARENT_STATE = 'state'
DF_PARENT_COUNTY = 'county'


class Resolution(enum.Enum):
    city_high = 15
    city_medium = 14
    city_low = 13
    county_high = 12
    county_medium = 11
    county_low = 10
    state_high = 9
    state_medium = 8
    state_low = 7
    country_high = 6
    country_medium = 5
    country_low = 4
    world_high = 3
    world_medium = 2
    world_low = 1


def select_request(query: RegionQuery, answer: Answer, feature: GeocodedFeature) -> str:
    # exploding answers (features count > 1) don't have exact request (like us-48, it can't be a proper
    # request for 48 features/states) and so feature name should be used as request.
    return query.request if len(answer.features) <= 1 else feature.name


def zip_answers(queries: List, answers: List):
    if len(queries) > 0:
        return zip(queries, answers)
    else:
        return zip([None] * len(answers), answers)


class PlacesDataFrameBuilder:
    def __init__(self):
        self._request: List[str] = []
        self._found_name: List[str] = []
        self._county: List[Optional[str]] = []
        self._state: List[Optional[str]] = []
        self._country: List[Optional[str]] = []

    def append_row(self, request: str, found_name: str, query: Optional[RegionQuery]):
        self._request.append(request)
        self._found_name.append(found_name)

        if query is None:
            self._county.append(MapRegion.name_or_none(None))
            self._state.append(MapRegion.name_or_none(None))
            self._country.append(MapRegion.name_or_none(None))
        else:
            self._county.append(MapRegion.name_or_none(query.county))
            self._state.append(MapRegion.name_or_none(query.state))
            self._country.append(MapRegion.name_or_none(query.country))

    def build_dict(self):
        def contains_values(column):
            return any(v is not None for v in column)

        data = {}
        data[DF_REQUEST] = self._request
        data[DF_FOUND_NAME] = self._found_name

        if contains_values(self._county):
            data[DF_PARENT_COUNTY] = self._county

        if contains_values(self._state):
            data[DF_PARENT_STATE] = self._state

        if contains_values(self._country):
            data[DF_PARENT_COUNTRY] = self._country

        return data

    @abstractmethod
    def to_data_frame(self, answers: List[Answer], queries: List[RegionQuery] = []) -> DataFrame:
        raise ValueError('Not implemented')


class Regions(CanToDataFrame):
    def __init__(self, level_kind: LevelKind, answers: List[Answer], queries: List[RegionQuery],
                 highlights: bool = False):
        assert_list_type(answers, Answer)
        assert_list_type(queries, RegionQuery)
        assert len(answers) == len(queries)

        try:
            import geopandas
        except:
            raise ValueError('Module \'geopandas\'is required for using regions') from None

        self._level_kind: LevelKind = level_kind
        self._answers: List[Answer] = answers

        features = []
        for answer in answers:
            features.extend(answer.features)

        self._geocoded_features: List[GeocodedFeature] = features
        self._highlights: bool = highlights
        self._queries: List[RegionQuery] = queries

    def __repr__(self):
        return self.to_data_frame().to_string()

    def __len__(self):
        return len(self._geocoded_features)

    def to_map_regions(self) -> List[MapRegion]:
        regions: List[MapRegion] = []
        for answer, query in zip_answers(self._answers, self._queries):
            for feature in answer.features:
                regions.append(MapRegion.place(feature.id, select_request(query, answer, feature), self._level_kind))
        return regions

    def as_list(self) -> List['Regions']:
        if len(self._queries) == 0:
            return [Regions(self._level_kind, [answer], [RegionQuery(request=None)], self._highlights) for answer in
                    self._answers]

        assert len(self._queries) == len(self._answers)
        return [Regions(self._level_kind, [answer], [query], self._highlights) for query, answer in
                zip(self._queries, self._answers)]

    def unique_ids(self) -> List[str]:
        seen = set()
        seen_add = seen.add
        return [feature.id for feature in self._geocoded_features if not (feature.id in seen or seen_add(feature.id))]

    def boundaries(self, resolution: Optional[Union[int, str, Resolution]] = None):
        """
        Return boundaries for given regions in form of GeoDataFrame.

        Parameters
        ----------
        resolution: [str | int | None]
            Boundaries resolution.

            int: [1-15]
                15 - maximum quality, 1 - maximum performance:
                 - 1-3 for world scale view
                 - 4-6 for country scale view
                 - 7-9 for state scale view
                 - 10-12 for county scale view
                 - 13-15 for city scale view

            str: ['world', 'country', 'state', 'county', 'city']
                'city' - maximum quality, 'world'  - maximum performance.
                Corresponding numeric resolutions:
                 - 'world' - 2
                 - 'country' - 5
                 - 'state' - 8
                 - 'county' - 11
                 - 'city' - 14

            Kind of area expected to be displayed. Resolution depends on a number of objects - single state is a 'state'
            scale view, while 50 states is a 'country' scale view.

            It is allowed to use any kind of resolution for any regions, i.e. 'city' for state to see more detailed
            boundary (when need to show zoomed part), or 'world' (when used for small preview).

            None:
                Autodetection. Uses level_kind that was used for geocoding this regions object and number of objects in it.
                Prefers performance over qulity. It's expected to get pixelated geometries with autodetection.
                Use explicit resolution for better quality.

                Resolution for countries:
                    If n < 3 => 3
                    else => 1

                Resolution for states:
                    If n < 3 => 7
                    If n < 10 => 4
                    else => 2

                Resolution for counties:
                    If n < 5 => 10
                    If n < 20 => 8
                    else => 3

                Resolution for cities:
                    If n < 5 => 13
                    If n < 50 => 4
                    else => 3

        Examples
        --------
        .. jupyter-execute::

            >>> from lets_plot.geo_data import *
            >>> rb = regions_country(['germany', 'russia']).boundaries()
            >>> rb
        """
        from lets_plot.geo_data.to_geo_data_frame import BoundariesGeoDataFrame

        if resolution is None:
            autodetected_resolution = _autodetect_resolution(self._level_kind, len(self._geocoded_features))
            int_resolution = _coerce_resolution(autodetected_resolution.value)
        elif isinstance(resolution, int):
            int_resolution = _coerce_resolution(resolution)
        elif isinstance(resolution, Resolution):
            int_resolution = _coerce_resolution(resolution.value)
        elif isinstance(resolution, str):
            int_resolution = _coerce_resolution(_parse_resolution(resolution).value)
        else:
            raise ValueError('Invalid resolution: ' + type(resolution).__name__)

        return self._execute(
            self._request_builder(PayloadKind.boundaries)
                .set_resolution(int_resolution),
            BoundariesGeoDataFrame()
        )

    def limits(self):
        """
        Return bboxes (Polygon geometry) for given regions in form of GeoDataFrame. For regions intersecting
        anti-meridian bbox will be divided into two and stored as two rows.

        Examples
        ---------
        .. jupyter-execute::

            >>> from lets_plot.geo_data import *
            >>> rl = regions_country(['germany', 'russia']).limits()
            >>> rl
        """
        from lets_plot.geo_data.to_geo_data_frame import LimitsGeoDataFrame
        return self._execute(
            self._request_builder(PayloadKind.limits),
            LimitsGeoDataFrame()
        )

    def centroids(self):
        """
        Return centroids (Point geometry) for given regions in form of GeoDataFrame.

        Examples
        ---------
        .. jupyter-execute::

            >>> from lets_plot.geo_data import *
            >>> rc = regions_country(['germany', 'russia']).centroids()
            >>> rc
        """
        from lets_plot.geo_data.to_geo_data_frame import CentroidsGeoDataFrame
        return self._execute(
            self._request_builder(PayloadKind.centroids),
            CentroidsGeoDataFrame()
        )

    # implements abstract in CanToDataFrame
    def to_data_frame(self) -> DataFrame:
        places = PlacesDataFrameBuilder()

        data = {}
        data[DF_ID] = [feature.id for feature in self._geocoded_features]

        # for us-48 queries doesnt' count
        for query, answer in zip_answers(self._queries, self._answers):
            for feature in answer.features:
                places.append_row(select_request(query, answer, feature), feature.name, query)

        data = {**data, **places.build_dict()}

        if self._highlights:
            data[DF_HIGHLIGHTS] = [feature.highlights for feature in self._geocoded_features]

        return DataFrame(data)

    def _execute(self, request_builder: RequestBuilder, df_converter):
        response = GeocodingService().do_request(request_builder.build())

        if not isinstance(response, SuccessResponse):
            _raise_exception(response)

        features = []

        for a in response.answers:
            features.extend(a.features)

        self._join_payload(features)

        return df_converter.to_data_frame(self._answers, self._queries)

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
parent_types = Optional[Union[str, Regions]]


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
        return response.message

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


def _parse_resolution(resolution: str) -> Resolution:
    if isinstance(resolution, str):
        if resolution == 'city':
            return Resolution.city_medium

        if resolution == 'county':
            return Resolution.county_medium

        if resolution == 'state':
            return Resolution.state_medium

        if resolution == 'country':
            return Resolution.country_medium

        if resolution == 'world':
            return Resolution.world_medium

        return Resolution[resolution]

    raise ValueError('Invalid resolution type: ' + type(resolution).__name__)


def _make_parent_region(place: parent_types) -> Optional[MapRegion]:
    if place is None:
        return None

    if isinstance(place, str):
        return MapRegion.with_name(place)

    if isinstance(place, Regions):
        assert len(place.to_map_regions()) == 1, 'Region object used as parent should contain only single record'
        return place.to_map_regions()[0]

    raise ValueError('Unsupported parent type: ' + str(type(place)))


def _to_scope(location: scope_types) -> Optional[Union[List[MapRegion], MapRegion]]:
    if location is None:
        return None

    def _make_region(obj: Union[str, Regions]) -> Optional[MapRegion]:
        if isinstance(obj, Regions):
            return MapRegion.scope(obj.unique_ids())

        if isinstance(obj, str):
            return MapRegion.with_name(obj)

        raise ValueError('Invalid region: ' + str(obj))

    if isinstance(location, list):
        return [_make_region(obj) for obj in location]

    return _make_region(location)


def _ensure_is_list(obj) -> Optional[List[str]]:
    if obj is None:
        return None

    if isinstance(obj, list):
        return obj

    if isinstance(obj, Series):
        return obj.tolist()

    return [obj]


def _coerce_resolution(res: int) -> int:
    if isinstance(res, int):
        if 1 <= res <= 15:
            return res
        else:
            raise ValueError("Invalid resolution value: " + str(res))

    raise ValueError("Unsupported resolution type: " + type(res).__name__)


def _autodetect_resolution(level: LevelKind, count: int) -> Resolution:
    if level == LevelKind.country:
        if count < 3:
            return Resolution.world_high
        else:
            return Resolution.world_low

    if level == LevelKind.state:
        if count < 3:
            return Resolution.state_low
        if count < 10:
            return Resolution.country_low
        else:
            return Resolution.world_medium

    if level == LevelKind.county:
        if count < 5:
            return Resolution.county_low
        elif count < 20:
            return Resolution.state_medium
        else:
            return Resolution.world_high

    if level == LevelKind.city:
        if count < 5:
            return Resolution.city_low
        elif count < 50:
            return Resolution.country_low
        else:
            return Resolution.world_high
