#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
from collections import namedtuple, Iterable
from typing import Union, List, Optional, Dict

from pandas import Series

from .geocodes import _to_level_kind, request_types, Geocodes, _raise_exception, _ensure_is_list
from .gis.geocoding_service import GeocodingService
from .gis.geometry import GeoRect, GeoPoint
from .gis.request import RequestBuilder, GeocodingRequest, RequestKind, MapRegion, AmbiguityResolver, \
    RegionQuery, LevelKind, IgnoringStrategyKind, PayloadKind, ReverseGeocodingRequest
from .gis.response import Response, SuccessResponse
from .type_assertion import assert_list_type

NAMESAKE_MAX_COUNT = 10

ShapelyPointType = 'shapely.geometry.Point'
ShapelyPolygonType = 'shapely.geometry.Polygon'

QuerySpec = namedtuple('QuerySpec', 'name, county, state, country')
WhereSpec = namedtuple('WhereSpec', 'scope, ambiguity_resolver')

parent_types = Optional[Union[str, Geocodes, 'Geocoder', MapRegion, List]] # list of same types
scope_types = Optional[Union[str, Geocodes, 'Geocoder', ShapelyPolygonType]]


def _to_scope(location: scope_types) -> Optional[Union[List[MapRegion], MapRegion]]:
    if location is None:
        return None

    def _make_region(obj: Union[str, Geocodes]) -> Optional[MapRegion]:
        if isinstance(obj, Geocodes):
            return MapRegion.scope(obj.unique_ids())

        if isinstance(obj, str):
            return MapRegion.with_name(obj)

        raise ValueError('Unsupported scope type. Expected Geocoder, str or list, but was `{}`'.format(type(obj)))

    if isinstance(location, list):
        return [_make_region(obj) for obj in location]

    return _make_region(location)


class LazyShapely:
    @staticmethod
    def is_point(p) -> bool:
        if not LazyShapely._is_shapely_available():
            return False

        from shapely.geometry import Point
        return isinstance(p, Point)

    @staticmethod
    def is_polygon(p):
        if not LazyShapely._is_shapely_available():
            return False

        from shapely.geometry import Polygon
        return isinstance(p, Polygon)

    @staticmethod
    def _is_shapely_available():
        try:
            import shapely
            return True
        except:
            return False


def _make_ambiguity_resolver(ignoring_strategy: Optional[IgnoringStrategyKind] = None,
                             scope: Optional[ShapelyPolygonType] = None,
                             closest_object: Optional[Union[Geocodes, ShapelyPointType]] = None):
    if LazyShapely.is_polygon(scope):
        rect = GeoRect(min_lon=scope.bounds[0], min_lat=scope.bounds[1], max_lon=scope.bounds[2], max_lat=scope.bounds[3])
    elif scope is None:
        rect = None
    else:
        assert scope is not None # else for empty scope - existing scope should be already handled
        raise ValueError('Wrong type of parameter `scope` - expected `shapely.geometry.Polygon`, but was `{}`'.format(type(scope).__name__))

    return AmbiguityResolver(
        ignoring_strategy=ignoring_strategy,
        closest_coord=_to_geo_point(closest_object),
        box=rect
    )


def _to_geo_point(closest_place: Optional[Union[Geocodes, ShapelyPointType]]) -> Optional[GeoPoint]:
    if closest_place is None:
        return None

    if isinstance(closest_place, Geocoder):
        closest_place = closest_place._geocode()

    if isinstance(closest_place, Geocodes):
        closest_place_id = closest_place.as_list()[0].unique_ids()
        assert len(closest_place_id) == 1

        request = RequestBuilder() \
            .set_request_kind(RequestKind.explicit) \
            .set_requested_payload([PayloadKind.centroids]) \
            .set_ids(closest_place_id) \
            .build()

        response: Response = GeocodingService().do_request(request)
        if isinstance(response, SuccessResponse):
            assert len(response.features) == 1
            centroid = response.features[0].centroid
            return GeoPoint(lon=centroid.lon, lat=centroid.lat)
        else:
            raise ValueError("Unexpected geocoding response for id " + str(closest_place_id[0]))

    if LazyShapely.is_point(closest_place):
        return GeoPoint(lon=closest_place.x, lat=closest_place.y)

    raise ValueError('Not supported type: {}'.format(type(closest_place)))


def _get_or_none(list, index):
    if index >= len(list):
        return None
    return list[index]


def _ensure_is_parent_list(obj):
    if obj is None:
        return None

    if isinstance(obj, Geocoder):
        obj = obj._geocode()

    if isinstance(obj, Geocodes):
        return obj.as_list()

    if isinstance(obj, Iterable) and not isinstance(obj, str):
        return [v for v in obj]

    return [obj]


def _make_parents(values: parent_types) -> List[Optional[MapRegion]]:
    values = _ensure_is_parent_list(values)

    if values is None:
        return []

    return list(map(lambda v: _make_parent_region(v) if values is not None else None, values))


def _make_parent_region(place: parent_types) -> Optional[MapRegion]:
    if place is None:
        return None

    if isinstance(place, Geocoder):
        place = place._geocode()

    if isinstance(place, str):
        return MapRegion.with_name(place)

    if isinstance(place, Geocodes):
        assert len(place.to_map_regions()) == 1, 'Region object used as parent should contain only single record'
        return place.to_map_regions()[0]

    raise ValueError('Unsupported parent type: ' + str(type(place)))


class Geocoder:
    def __init__(self):
        self._inc_res = 0

    def get_limits(self) -> 'GeoDataFrame':
        """
        Return bboxes (Polygon geometry) for given regions in form of GeoDataFrame. For regions intersecting
        anti-meridian bbox will be divided into two parts and stored as two rows.

        Examples
        ---------
        .. jupyter-execute::

            >>> from lets_plot.geo_data import *
            >>> limits = geocode_countries(['germany', 'russia']).get_limits()
            >>> limits
        """
        return self._geocode().limits()

    def get_centroids(self) -> 'GeoDataFrame':
        """
        Return centroids (Point geometry) for given regions in form of GeoDataFrame.

        Examples
        ---------
        .. jupyter-execute::

            >>> from lets_plot.geo_data import *
            >>> centroids = geocode_countries(['germany', 'russia']).get_centroids()
            >>> centroids
        """
        return self._geocode().centroids()

    def get_boundaries(self, resolution=None) -> 'GeoDataFrame':
        """
        Return boundaries for given regions in form of GeoDataFrame.

        Parameters
        ----------
        resolution: [int | str | None]
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

            The resolution choice depends on kind of displayed area. The number of objects also makes sense:
			one state looks good on a 'state' scale while 50 states is a 'country' view.

            It is allowed to use any resolution for all regions. For example 'city' scale can be used for state
			to get more detailed boundary when zoom in or 'world' for a small preview.

            None:
                Auto-detection by level_kind used for geocoding and number of objects. In this case
                performance is preferred over quality. The pixelated geometries can be obtained.
                Use explicit resolution or inc_res() function for better quality.

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
            >>> boundaries = geocode_countries(['germany', 'russia']).get_boundaries()
            >>> boundaries
        """
        return self._geocode().boundaries(resolution, self._inc_res)

    def get_geocodes(self) -> 'DataFrame':
        """
        Return DataFrame with metadata.

        Examples
        ---------
        .. jupyter-execute::

            >>> from lets_plot.geo_data import *
            >>> geocodes = geocode_countries(['germany', 'russia']).get_geocodes()
            >>> geocodes
        """
        return self._geocode().to_data_frame()

    def inc_res(self, delta=2):
        """
        Increase auto-detected resolution for boundaries.

        Parameters
        ----------
        delta: int
            Value that will be added to auto-detected resolution. Default value is 2.

        Examples
        ---------
        .. jupyter-execute::

            >>> from lets_plot.geo_data import *
            >>> boundaries = geocode_countries(['germany', 'russia']).inc_res().get_boundaries()
            >>> boundaries
        """
        self._inc_res = delta
        return self

    def _geocode(self) -> Geocodes:
        raise ValueError('Abstract method')


def _to_coords(lon: Optional[Union[float, Series, List[float]]], lat: Optional[Union[float, Series, List[float]]]) -> List[GeoPoint]:
    if type(lon) != type(lat):
        raise ValueError('lon and lat have different types')

    if isinstance(lon, float):
        return [GeoPoint(lon, lat)]

    if isinstance(lon, Series):
        lon = lon.tolist()
        lat = lat.tolist()

    if isinstance(lon, list):
        assert_list_type(lon, float)
        assert_list_type(lat, float)
        return [GeoPoint(lo, la) for lo, la in zip(lon, lat)]


class ReverseGeocoder(Geocoder):
    def __init__(self, lon, lat, level: Optional[Union[str, LevelKind]], scope=None):
        Geocoder.__init__(self)

        self._geocodes: Optional[Geocodes] = None
        self._request: ReverseGeocodingRequest = RequestBuilder() \
            .set_request_kind(RequestKind.reverse) \
            .set_reverse_coordinates(_to_coords(lon, lat)) \
            .set_level(_to_level_kind(level)) \
            .set_reverse_scope(_to_scope(scope)) \
            .build()

    def _geocode(self) -> Geocodes:
        if self._geocodes is None:
            response: Response = GeocodingService().do_request(self._request)
            if not isinstance(response, SuccessResponse):
                _raise_exception(response)
            self._geocodes = Geocodes(
                response.level,
                response.answers,
                [RegionQuery(request='[{}, {}]'.format(pt.lon, pt.lat)) for pt in self._request.coordinates],
                highlights=False
            )

        return self._geocodes


class NamesGeocoder(Geocoder):
    def __init__(
            self,
            level: Optional[Union[str, LevelKind]] = None,
            request: request_types = None
    ):
        Geocoder.__init__(self)

        self._geocodes: Optional[Geocodes] = None
        self._scope: List[Optional[MapRegion]] = []
        self._level: Optional[LevelKind] = _to_level_kind(level)
        self._default_ambiguity_resolver: AmbiguityResolver = AmbiguityResolver.empty()  # TODO rename to geohint
        self._highlights: bool = False
        self._allow_ambiguous = False
        self._countries: List[Optional[MapRegion]] = []
        self._states: List[Optional[MapRegion]] = []
        self._counties: List[Optional[MapRegion]] = []
        self._overridings: Dict[QuerySpec, WhereSpec] = {}  # query to scope

        requests: Optional[List[str]] = _ensure_is_list(request)
        if requests is not None:
            self._names: List[Optional[str]] = list(map(lambda name: name if requests is not None else None, requests))
        else:
            self._names = []

    def scope(self, scope) -> 'NamesGeocoder':
        """
        Limit area of interest to resolve an ambiguity.

        Parameters
        ----------
        scope: [str | Geocoder | shapely.geometry.Polygon]
            Area of interest.

            str:
            Name of geo-object.

            Geocoder:
            It must contain only one object.

            shapely.geometry.Polygon:
            Rectangular area (bounding box of the polygon).
            Coordinates are expected to be in WGS84 reference system.


        Examples
        ---------
        .. jupyter-execute::

            >>> from lets_plot.geo_data import *
            >>> centroids = geocode_states('florida').scope('uruguay').get_centroids()
            >>> centroids
        """
        self._reset_geocodes()
        self._scope = _prepare_new_scope(scope)
        return self

    def highlights(self, v: bool):
        """
        Add matched string to geocodes DataFrame. Doesn't affect GeoDataFrames.

        Parameters
        ----------
        v: bool
            If True geocodes DataFrame will contain column 'highlights' with string that matched the name.


        Examples
        ---------
        .. jupyter-execute::

            >>> from lets_plot.geo_data import *
            >>> codes = geocode(names=['OH']).allow_ambiguous().highlights(True).get_geocodes()
            >>> codes
        """
        self._highlights = v
        return self

    def countries(self, countries):
        """
        Set parents for COUNTRY level to resolve an ambiguity or to join geometry with data via multi-key.

        Parameters
        ----------
        countries: [str | Geocoder | array]
            Parents for COUNTRY level.

            str:
            Name of country.

            Geocoder:
            Geocoder with countries. It must contain the same number of values as the number of names of Geocoder.

            array:
            List of country names. It must be the same size as the number of names of Geocoder.


        Examples
        ---------
        .. jupyter-execute::

            >>> from lets_plot.geo_data import *
            >>> centroids = geocode_cities('boston').countries('usa').get_centroids()
            >>> centroids
        """
        self._reset_geocodes()
        self._countries = _make_parents(countries)
        return self

    def states(self, states) -> 'NamesGeocoder':
        """
        Set parents for STATE level to resolve an ambiguity or to join geometry with data via multi-key.

        Parameters
        ----------
        states: [str | Geocoder | array]
            Parents for STATE level.

            str:
            Name of state.

            Geocoder:
            Geocoder with states. It must contain the same number of values as the number of names of Geocoder.

            array:
            List of state names. It must be the same size as the number of names of Geocoder.


        Examples
        ---------
        .. jupyter-execute::

            >>> from lets_plot.geo_data import *
            >>> centroids = geocode_cities('boston').states('Massachusetts').get_centroids()
            >>> centroids
        """
        self._reset_geocodes()
        self._states = _make_parents(states)
        return self

    def counties(self, counties: parent_types) -> 'NamesGeocoder':
        """
        Set parents for COUNTY level to resolve an ambiguity or to join geometry with data via multi-key.

        Parameters
        ----------
        counties: [str | Geocoder | array]
            Parents for COUNTY level.

            str:
            Name of county.

            Geocoder:
            Geocoder with counties. It must contain the same number of values as the number of names of Geocoder.

            array:
            List of county names. It must be the same size as the number of names of Geocoder.


        Examples
        ---------
        .. jupyter-execute::

            >>> from lets_plot.geo_data import *
            >>> centroids = geocode_cities('boston').counties('Suffolk County').states('Massachusetts').get_centroids()
            >>> centroids
        """
        self._reset_geocodes()
        self._counties = _make_parents(counties)
        return self

    def ignore_not_found(self) -> 'NamesGeocoder':
        """
		Remove not found objects from the result.

        Examples
        ---------
        .. jupyter-execute::

            >>> from lets_plot.geo_data import *
            >>> centroids = geocode_states(['foo', 'florida']).ignore_not_found().get_centroids()
            >>> centroids
        """
        self._reset_geocodes()
        self._default_ambiguity_resolver = AmbiguityResolver(IgnoringStrategyKind.skip_missing)
        return self

    def ignore_all_errors(self) -> 'NamesGeocoder':
        """
        Remove objects that have multiple matches from the result.

        Examples
        ---------
        .. jupyter-execute::

            >>> from lets_plot.geo_data import *
            >>> centroids = geocode_cities(['worcester']).ignore_all_errors().get_centroids()
            >>> centroids
        """
        self._reset_geocodes()
        self._default_ambiguity_resolver = AmbiguityResolver(IgnoringStrategyKind.skip_all)
        return self

    def allow_ambiguous(self) -> 'NamesGeocoder':
        """
        For objects that have multiple matches add all of them to the result.

        Examples
        ---------
        .. jupyter-execute::

            >>> from lets_plot.geo_data import *
            >>> centroids = geocode_cities(['worcester']).allow_ambiguous().get_centroids()
            >>> centroids
        """
        self._reset_geocodes()
        self._default_ambiguity_resolver = AmbiguityResolver(IgnoringStrategyKind.take_namesakes)
        self._allow_ambiguous = True
        return self

    def where(self, name: str,
              county: Optional[parent_types] = None,
              state: Optional[parent_types] = None,
              country: Optional[parent_types] = None,
              scope: scope_types = None,
              closest_to: Optional[Union[Geocodes, ShapelyPointType]] = None
              ) -> 'NamesGeocoder':
        """
        Allows to resolve ambiguity by setting up extra parameters. Combination of name, county, state, country
        identifies a row with an ambiguity.
        If row with given names does not exist error will be generated.

        Parameters
        ----------
        name : string
            Name in Geocoder that needs better qualification.
        county : [string | None]
            If Geocoder has parent counties this field must be present to identify a row for the name
        state : [string | None]
            If Geocoder has parent states this field must be present to identify a row for the name
        country : [string | None]
            If Geocoder has parent countries this field must be present to identify a row for the name
        scope : [string | Geocoder | shapely.Polygon | None]
            Limits area of geocoding. If parent country is set then error will be generated.
            If type is a string - geoobject should have geocoded scope in parents.
            If type is a Geocoder  - geoobject should have geocoded scope in parents. Scope should contain only one entry.
            If type is a shapely.Polygon - geoobject centroid should fall into bbox of the polygon.
        closest_to: [Geocoder | shapely.geometry.Point | None]
            Resolve ambiguity by taking closest geoobject.

        Returns
        -------
            Geocoder object
        """
        self._reset_geocodes()
        query_spec = QuerySpec(
            name,
            _make_parent_region(county),
            _make_parent_region(state),
            _make_parent_region(country)
        )

        def query_exist(query):
            for i in range(len(self._names)):
                if query.name == self._names[i] and \
                        query.country == _get_or_none(self._countries, i) and \
                        query.state == _get_or_none(self._states, i) and \
                        query.county == _get_or_none(self._counties, i):
                    return True
            return False

        if not query_exist(query_spec):
            parents: List[str] = []
            if query_spec.county is not None:
                parents.append('county={}'.format(str(query_spec.county)))

            if query_spec.state is not None:
                parents.append('state={}'.format(str(query_spec.state)))

            if query_spec.country is not None:
                parents.append('country={}'.format(str(query_spec.country)))

            parents_str = ", ".join(parents)
            if len(parents_str) == 0:
                raise ValueError("{} is not found in names".format(name))
            else:
                raise ValueError("{}({}) is not found in names".format(name, parents_str))

        if scope is None:
            new_scope = None
            ambiguity_resolver = _make_ambiguity_resolver(scope=None, closest_object=closest_to)
        else:
            if LazyShapely.is_polygon(scope):
                new_scope = None
                ambiguity_resolver = _make_ambiguity_resolver(scope=scope, closest_object=closest_to)
            else:
                new_scope = _prepare_new_scope(scope)[0]
                ambiguity_resolver = _make_ambiguity_resolver(scope=None, closest_object=closest_to)

        self._overridings[query_spec] = WhereSpec(new_scope, ambiguity_resolver)
        return self


    def _build_request(self) -> GeocodingRequest:
        if len(self._names) == 0:
            def to_scope(parents):
                if len(parents) == 0:
                    return None
                elif len(parents) == 1:
                    return parents[0]
                else:
                    raise ValueError('Too many parent objects. Expcted single object instead of {}'.format(len(parents)))

            # all countries/states etc. We need one dummy query
            queries = [
                RegionQuery(
                    request=None,
                    country=to_scope(self._countries),
                    state=to_scope(self._states),
                    county=to_scope(self._counties)
                )
            ]
        else:
            def assert_parents_size(parents: List, parents_level: str):
                if len(parents) == 0:
                    return

                if len(parents) != len(self._names):
                    raise ValueError('Invalid request: {} count({}) != names count({})'.format(parents_level, len(parents), len(self._names)))

            if len(self._countries) > 0 and len(self._scope) > 0:
                raise ValueError("Invalid request: countries and scope can't be used simultaneously")

            assert_parents_size(self._countries, 'countries')
            assert_parents_size(self._states, 'states')
            assert_parents_size(self._counties, 'counties')

            queries = []
            for i in range(len(self._names)):
                name = self._names[i]
                country = _get_or_none(self._countries, i)
                state = _get_or_none(self._states, i)
                county = _get_or_none(self._counties, i)

                scope, ambiguity_resolver = self._overridings.get(
                    QuerySpec(name, county, state, country),
                    WhereSpec(None, self._default_ambiguity_resolver)
                )

                query = RegionQuery(
                    request=name,
                    country=country,
                    state=state,
                    county=county,
                    scope=scope,
                    ambiguity_resolver=ambiguity_resolver
                )

                queries.append(query)

        request = RequestBuilder() \
            .set_request_kind(RequestKind.geocoding) \
            .set_requested_payload([PayloadKind.highlights] if self._highlights else []) \
            .set_queries(queries) \
            .set_scope(self._scope) \
            .set_level(self._level) \
            .set_namesake_limit(NAMESAKE_MAX_COUNT) \
            .set_allow_ambiguous(self._allow_ambiguous) \
            .build()

        return request

    def _geocode(self) -> Geocodes:
        if self._geocodes is None:
            request: GeocodingRequest = self._build_request()
            response: Response = GeocodingService().do_request(request)
            if not isinstance(response, SuccessResponse):
                _raise_exception(response)
            self._geocodes = Geocodes(response.level, response.answers, request.region_queries, self._highlights)

        return self._geocodes

    def _reset_geocodes(self):
        self._geocodes = None

    def __eq__(self, o):
        return isinstance(o, NamesGeocoder) \
               and self._overridings == o._overridings

    def __ne__(self, o):
        return not self == o


def _prepare_new_scope(scope: Optional[Union[str, Geocoder, Geocodes, MapRegion]]) -> List[MapRegion]:
    """
    Return list of MapRegions. Every MapRegion object contains only one name or id.
    """
    if scope is None:
        return []

    def assert_scope_length_(l):
        if l != 1:
            raise ValueError("'scope' has {} entries, but expected to have exactly 1".format(l))

    if isinstance(scope, MapRegion):
        assert_scope_length_(len(scope.values))
        return [scope]

    if isinstance(scope, str):
        return [MapRegion.with_name(scope)]

    if isinstance(scope, Geocoder):
        scope = scope._geocode()

    if isinstance(scope, Geocodes):
        map_regions = scope.to_map_regions()
        assert_scope_length_(len(map_regions))
        return map_regions

    raise ValueError("Unsupported 'scope' type. Expected 'str' or 'Geocoder' but was '{}'".format(type(scope).__name__))

