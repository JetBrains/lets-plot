import enum
from abc import abstractmethod
from typing import List, Dict, Optional, Union

from pandas import DataFrame, Series

from .gis.geocoding_service import GeocodingService
from .gis.request import PayloadKind, RequestBuilder, RequestKind, MapRegion, RegionQuery
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


def contains_values(column):
    return any(v is not None for v in column)

def select_not_empty_name(feature: GeocodedFeature) -> str:
    return feature.name if feature.query is None or feature.query == '' else feature.query

def select_parents(queries: List[RegionQuery] = None) -> Dict:
    if queries is None:
        return {}

    data = {}

    counties = [query.county for query in queries]
    if contains_values(counties):
        data[DF_PARENT_COUNTY] = counties

    states = [query.state for query in queries]
    if contains_values(states):
        data[DF_PARENT_STATE] = states

    countries = [query.country for query in queries]
    if contains_values(countries):
        data[DF_PARENT_COUNTRY] = countries

    return data


class PlacesDataFrameBuilder:
    def __init__(self):
        self._request: List[str] = []
        self._found_name: List[str] = []
        self._county: List[str] = []
        self._state: List[str] = []
        self._country: List[str] = []

    def append_row(self, request: str, found_name: str, parents: Dict, parent_row: int):
        self._request.append(request)
        self._found_name.append(found_name)

        self._county.append(parents[DF_PARENT_COUNTY][parent_row] if DF_PARENT_COUNTY in parents else None)
        self._state.append(parents[DF_PARENT_STATE][parent_row] if DF_PARENT_STATE in parents else None)
        self._country.append(parents[DF_PARENT_COUNTRY][parent_row] if DF_PARENT_COUNTRY in parents else None)


    def build_dict(self):
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
    def to_data_frame(self, features: List[GeocodedFeature], queries: List[RegionQuery] = None) -> DataFrame:
        raise ValueError('Not implemented')


class Regions(CanToDataFrame):
    def __init__(self, level_kind: LevelKind, features: List[GeocodedFeature], highlights: bool = False, queries: List[RegionQuery] = None):
        try:
            import geopandas
        except:
            raise ValueError('Module \'geopandas\'is required for using regions') from None

        self._level_kind: LevelKind = level_kind
        self._geocoded_features: List[GeocodedFeature] = features
        self._highlights: bool = highlights
        self._queries: List[RegionQuery] = queries

    def __repr__(self):
        return self.to_data_frame().to_string()

    def __len__(self):
        return len(self._geocoded_features)

    def as_list(self) -> List['Regions']:
        return [Regions(self._level_kind, [feature], self._highlights) for feature in self._geocoded_features]

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
        parents = select_parents(self._queries)
        places = PlacesDataFrameBuilder()

        data = {}
        data[DF_ID] = [feature.id for feature in self._geocoded_features]

        for i in range(len(self._geocoded_features)):
            feature = self._geocoded_features[i]
            places.append_row(select_not_empty_name(feature), feature.name, parents, i)

        data = {**data, **places.build_dict()}

        if self._highlights:
            data[DF_HIGHLIGHTS] = [feature.highlights for feature in self._geocoded_features]

        return DataFrame(data)


    def _execute(self, request_builder: RequestBuilder, df_converter):
        response = GeocodingService().do_request(request_builder.build())

        if not isinstance(response, SuccessResponse):
            _raise_exception(response)

        self._join_payload(response.features)

        return df_converter.to_data_frame(self._geocoded_features, self._queries)

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
        return MapRegion.with_single_id(place.unique_ids())

    raise ValueError('Unsupported parent type: ' + str(type(place)))


def _to_scope(location: scope_types) -> Optional[Union[List[MapRegion], MapRegion]]:
    if location is None:
        return None

    def _make_region(obj: Union[str, Regions]) -> Optional[MapRegion]:
        if isinstance(obj, Regions):
            return MapRegion.with_ids(obj.unique_ids())

        if isinstance(obj, str):
            return MapRegion.with_name(obj)

        raise ValueError('Invalid region: ' + str(obj))

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
