import enum
from abc import abstractmethod
from collections.abc import Iterable
from typing import List, Optional, Union, Dict

from pandas import DataFrame, Series

from lets_plot.geo_data_internals.constants import DF_COLUMN_HIGHLIGHTS, DF_COLUMN_COUNTRY, DF_COLUMN_STATE, \
    DF_COLUMN_COUNTY, DF_COLUMN_CITY, DF_COLUMN_ID, DF_COLUMN_FOUND_NAME, DF_COLUMN_POSITION, DF_COLUMN_LIMIT, \
    DF_COLUMN_CENTROID
from .gis.geocoding_service import GeocodingService
from .gis.request import PayloadKind, RequestBuilder, RequestKind, MapRegion, RegionQuery
from .gis.response import Answer, GeocodedFeature, Namesake, AmbiguousFeature, LevelKind
from .gis.response import SuccessResponse, Response, AmbiguousResponse, ErrorResponse
from .type_assertion import assert_type, assert_list_type

NO_OBJECTS_FOUND_EXCEPTION_TEXT = 'No objects were found.'
MULTIPLE_OBJECTS_FOUND_EXCEPTION_TEXT = "Multiple objects were found. Use all_result=True to see them."


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


class PlacesDataFrameBuilder:
    def __init__(self, level_kind: LevelKind):
        self.level_kind: LevelKind = level_kind
        self._request: List[str] = []
        self._found_name: List[str] = []
        self._county: List[Optional[str]] = []
        self._state: List[Optional[str]] = []
        self._country: List[Optional[str]] = []

    def append_row(self, query: RegionQuery, feature: GeocodedFeature):
        self._request.append(_select_request_string(query.request, feature.name))
        self._found_name.append(feature.name)

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

        request_column = _level_to_column_name(self.level_kind)

        data[request_column] = self._request
        data[DF_COLUMN_FOUND_NAME] = self._found_name

        if contains_values(self._county):
            data[DF_COLUMN_COUNTY] = self._county

        if contains_values(self._state):
            data[DF_COLUMN_STATE] = self._state

        if contains_values(self._country):
            data[DF_COLUMN_COUNTRY] = self._country

        return data

    @abstractmethod
    def to_data_frame(self, answers: List[Answer], queries: List[RegionQuery], level_kind: LevelKind) -> DataFrame:
        raise ValueError('Not implemented')


class Geocodes:
    def __init__(self, level_kind: LevelKind, answers: List[Answer], queries: List[RegionQuery], highlights: bool = False):
        assert_list_type(answers, Answer)
        assert_list_type(queries, RegionQuery)

        if len(answers) == 0:
            assert len(queries) == 1 and queries[0].request is None  # select all
        else:
            assert len(queries) == len(answers)  # regular request - should have same size

        try:
            import geopandas
        except ImportError:
            raise ValueError('Module \'geopandas\'is required for geocoding') from None

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
        for answer, query in _zip_answers(self._answers, self._queries):
            for feature in answer.features:
                regions.append(
                    MapRegion.place(feature.id, _select_request_string(query.request, feature.name), self._level_kind))
        return regions

    def as_list(self) -> List['Geocodes']:
        if len(self._queries) == 0:
            return [Geocodes(self._level_kind, [answer], [RegionQuery(request=None)], self._highlights) for answer in
                    self._answers]

        assert len(self._queries) == len(self._answers)
        return [Geocodes(self._level_kind, [answer], [query], self._highlights) for query, answer in
                zip(self._queries, self._answers)]

    def unique_ids(self) -> List[str]:
        seen = set()
        seen_add = seen.add
        return [feature.id for feature in self._geocoded_features if not (feature.id in seen or seen_add(feature.id))]

    def boundaries(self, resolution: Optional[Union[int, str, Resolution]] = None, inc_res: int = 0):
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

        inc_res: int
            Increase auto-detected resolution.

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
            int_resolution = min(Resolution.city_high.value, autodetected_resolution + inc_res)
        elif isinstance(resolution, int):
            int_resolution = resolution
        elif isinstance(resolution, Resolution):
            int_resolution = resolution.value
        elif isinstance(resolution, str):
            int_resolution = _parse_resolution(resolution).value
        else:
            raise ValueError('Invalid resolution: ' + type(resolution).__name__)

        if int_resolution < Resolution.world_low.value or int_resolution > Resolution.city_high.value:
            raise ValueError(
                "Resolution is out of range. Expected to be from ({}) to ({}), but was ({})."
                    .format(Resolution.world_low.value, Resolution.city_high.value, int_resolution)
            )

        return self._execute(
            self._request_builder(PayloadKind.boundaries)
                .set_resolution(int_resolution),
            BoundariesGeoDataFrame()
        )

    def limits(self) -> 'GeoDataFrame':
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

    def to_data_frame(self) -> DataFrame:
        places = PlacesDataFrameBuilder(self._level_kind)

        # for us-48 queries doesnt' count
        for query, answer in _zip_answers(self._queries, self._answers):
            for feature in answer.features:
                places.append_row(query, feature)

        def geo_rect_to_list(geo_rect: 'GeoRect') -> List:
            return [geo_rect.start_lon, geo_rect.min_lat, geo_rect.end_lon, geo_rect.max_lat]

        data = {
            DF_COLUMN_ID: [feature.id for feature in self._geocoded_features],
            **places.build_dict(),
            DF_COLUMN_CENTROID: [[feature.centroid.lon, feature.centroid.lat] for feature in self._geocoded_features],
            DF_COLUMN_POSITION: [geo_rect_to_list(feature.position) for feature in self._geocoded_features],
            DF_COLUMN_LIMIT: [geo_rect_to_list(feature.limit) for feature in self._geocoded_features]
        }

        if self._highlights:
            data[DF_COLUMN_HIGHLIGHTS] = [feature.highlights for feature in self._geocoded_features]

        return DataFrame(data)

    def _execute(self, request_builder: RequestBuilder, df_converter):
        response = GeocodingService().do_request(request_builder.build())

        if not isinstance(response, SuccessResponse):
            _raise_exception(response)

        features = []

        for a in response.answers:
            features.extend(a.features)

        self._join_payload(features)

        return df_converter.to_data_frame(self._answers, self._queries, self._level_kind)

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


def _raise_exception(response: Response):
    msg = _format_error_message(response)
    raise ValueError(msg)


def _format_error_message(response: Response) -> str:
    if isinstance(response, AmbiguousResponse):
        not_found_names: Dict = {}
        multiple_objects: List[AmbiguousFeature] = []

        for ambiguous_feature in response.features:
            if ambiguous_feature.total_namesake_count == 0:
                not_found_names[ambiguous_feature.query] = None

            if ambiguous_feature.total_namesake_count > 0:
                multiple_objects.append(ambiguous_feature)

        if len(not_found_names) > 0:
            display_limit = 10
            msg_text = 'No objects were found for '
            if len(not_found_names) > display_limit:
                msg_text += ', '.join(list(not_found_names.keys())[:display_limit])
                msg_text += ' and ({}) more'.format(len(not_found_names) - display_limit)
            else:
                msg_text += ', '.join(list(not_found_names.keys()))

            return msg_text + '.\n'

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


def _ensure_is_list(obj) -> Optional[List[str]]:
    if obj is None:
        return None

    if isinstance(obj, Iterable) and not isinstance(obj, str):
        return [v for v in obj]

    return [obj]


def _autodetect_resolution(level: LevelKind, count: int) -> int:
    if level == LevelKind.country:
        if count < 3:
            return Resolution.world_high.value
        else:
            return Resolution.world_low.value

    if level == LevelKind.state:
        if count < 3:
            return Resolution.state_low.value
        if count < 10:
            return Resolution.country_low.value
        else:
            return Resolution.world_medium.value

    if level == LevelKind.county:
        if count < 5:
            return Resolution.county_low.value
        elif count < 20:
            return Resolution.state_medium.value
        else:
            return Resolution.world_high.value

    if level == LevelKind.city:
        if count < 5:
            return Resolution.city_low.value
        elif count < 50:
            return Resolution.country_low.value
        else:
            return Resolution.world_high.value


def _select_request_string(request: Optional[str], name: str) -> str:
    if request is None:
        return name

    if len(request) == 0:
        return name

    if 'us-48' == request.lower():
        return name

    return request


def _level_to_column_name(level_kind: LevelKind):
    if level_kind == LevelKind.city:
        return DF_COLUMN_CITY
    elif level_kind == LevelKind.county:
        return DF_COLUMN_COUNTY
    elif level_kind == LevelKind.state:
        return DF_COLUMN_STATE
    elif level_kind == LevelKind.country:
        return DF_COLUMN_COUNTRY
    else:
        raise ValueError('Unknown level kind: {}'.format(level_kind))


def _zip_answers(queries: List, answers: List):
    if len(queries) > 0:
        return zip(queries, answers)
    else:
        return zip([None] * len(answers), answers)
