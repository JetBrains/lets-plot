from typing import Any, Union, List, Optional

import numpy as np
from pandas import Series

from .gis.geocoding_service import GeocodingService
from .gis.geometry import GeoPoint
from .gis.request import RequestBuilder, RequestKind
from .gis.response import Response, SuccessResponse
from .regions import Regions, _raise_exception, _to_level_kind, _to_scope
from .regions_builder import RegionsBuilder
from .type_assertion import assert_list_type

__all__ = [
    'distance',
    'regions_builder',
    'regions',
    'regions_country',
    'regions_state',
    'regions_county',
    'regions_city',
    'regions_xy',
]

UNITS_DICT = {
    'mi': 3959,
    'km': 6371
}

GEOFUNC_TYPES = {
    'centroids': 'centroids',
    'boundaries': 'boundaries',
    'limits': 'limits',
    'region': 'regions'
}


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


def regions_xy(lon, lat, level, within=None):
    request = RequestBuilder() \
        .set_request_kind(RequestKind.reverse) \
        .set_reverse_coordinates(_to_coords(lon, lat)) \
        .set_level(_to_level_kind(level)) \
        .set_reverse_scope(_to_scope(within)) \
        .build()

    response: Response = GeocodingService().do_request(request)

    if not isinstance(response, SuccessResponse):
        _raise_exception(response)

    return Regions(response.level, response.features, False)


def regions_builder(level=None, request=None, within=None, highlights=False) -> RegionsBuilder:
    """
    Create a RegionBuilder class by level and request. Allows to refine ambiguous request with
    where method. build() method creates Regions object or shows details for ambiguous result.

    regions_builder(level, request, within)

    Parameters
    ----------
    level : ['country' | 'state' | 'county' | 'city' | None]
        The level of administrative division. Default is a 'state'.
    request : [array | string | None]
        Data can be filtered by full names at any level (only exact matching).
        For 'state' level:
        -'US-48' returns continental part of United States (48 states) in a compact form.
    within : [array | string | Regions | None]
        Data can be filtered by within name.
        If within is array then request and within will be merged positionally (size should be equal).
        If within is Regions then request will be searched in any of these regions.
        'US-48' includes continental part of United States (48 states).

    Returns
    -------
    RegionsBuilder object :

    Note
    -----
    regions_builder() allows to refine ambiguous request with where() method. Call build() method to create Regions object

    Examples
    ---------
    .. jupyter-execute::

        >>> from lets_plot.geo_data import *
        >>> r = regions_builder(level='city', request=['moscow', 'york']).where('york', regions_state('New York')).build()
        >>> r
    """
    return RegionsBuilder(level, request, within, highlights)


def regions(level=None, request=None, within=None) -> Regions:
    """
    Create a Regions class by level and request.

    regions(level, request, within)

    Parameters
    ----------
    level : ['country' | 'state' | 'county' | 'city' | None]
        The level of administrative division. None is for autodetection, falls back to a 'state' in case of ambiguity.
    request : [array | string | None]
        Data can be filtered by full names at any level (only exact matching).
        None with explicit level returns all corresponding regions, like all countries i.e. regions(level='country').
        For 'state' level:
        -'US-48' returns continental part of United States (48 states) in a compact form.
    within : [array | string | Regions| None]
        Data can be filtered by within name.
        If within is array then request and within will be merged positionally (size should be equal).
        If within is Regions then request will be searched in any of these regions.
        'US-48' includes continental part of United States (48 states).

    Returns
    -------
    Regions object :

    Note
    -----
    regions() is used to get name and object id by level and request.
    If the given names are not found exception will be thrown.

    Examples
    ---------
    .. jupyter-execute::

        >>> from lets_plot.geo_data import *
        >>> r = regions(level='country', request=['Germany', 'USA'])
        >>> r
    """
    return RegionsBuilder(level=level, request=request, scope=within).build()


def regions_country(request=None):
    """
    Create a Regions class for country level by request.

    regions_country(request)

    Parameters
    ----------
    request : [array | string | None]
        Data can be filtered by full names at any level (only exact matching).

    Returns
    -------
    Regions object :

    Note
    -----
    regions_country() is used to get name and object id by request.
    If the given names are not found exception will be thrown.
    See also regions().

    Examples
    ---------
    .. jupyter-execute::

        >>> from lets_plot.geo_data import *
        >>> r_country = regions_country(request=['Germany', 'USA'])
        >>> r_country
    """
    return regions('country', request, None)


def regions_state(request=None, within=None):
    """
    Create a Regions class for state level by request.

    regions_state(request, within)

    Parameters
    ----------
    request : [array | string | None]
        Data can be filtered by full names at any level (only exact matching).
        For 'state' level:
        -'US-48' returns continental part of United States (48 states) in a compact form.
    within : [array | string | Regions| None]
        Data can be filtered by within name.
        If within is array then filter and within will be merged positionally (size should be equal).
        If within is Regions then request will be searched in any of these regions.
        'US-48' includes continental part of United States (48 states).

    Returns
    -------
    Regions object :

    Note
    -----
    regions_state() is used to get name and object id by request.
    If the given names are not found exception will be thrown.
    See also regions().

    Examples
    ---------
    .. jupyter-execute::

        >>> from lets_plot.geo_data import *
        >>> r_state = regions_state(request=['Texas', 'Iowa'], within='USA')
        >>> r_state
    """
    return regions('state', request, within)


def regions_county(request=None, within=None):
    """
    Create a Regions class for county level by request.

    regions_county(request, within)

    Parameters
    ----------
    request : [array | string | None]
        Data can be filtered by full names at any level (only exact matching).
    within : [array | string | Regions| None]
        Data can be filtered by within name.
        If within is array then request and within will be merged positionally (size should be equal).
        If within is Regions then request will be searched in any of these regions.
        'US-48' includes continental part of United States (48 states).

    Returns
    -------
    Regions object :

    Note
    -----
    regions_county() is used to get name and object id by request.
    If the given names are not found exception will be thrown.
    See also regions().

    Examples
    ---------
    .. jupyter-execute::

        >>> from lets_plot.geo_data import *
        >>> r_county = regions_county(request=['Calhoun County', 'Howard County'], within='Texas')
        >>> r_county
    """
    return regions('county', request, within)


def regions_city(request=None, within=None):
    """
    Create a Regions class for city level by request.

    regions_city(request, within)

    Parameters
    ----------
    request : [array | string | None]
        Data can be filtered by full names at any level (only exact matching).
    within : [array | string | Regions| None]
        Data can be filtered by within name.
        If within is array then request and within will be merged positionally (size should be equal).
        If within is Regions then request will be searched in any of these regions.
        'US-48' includes continental part of United States (48 states).

    Returns
    -------
    Regions object :

    Note
    -----
    regions_city() is used to get name and object id by request.
    If the given names are not found exception will be thrown.
    See also regions().

    Examples
    ---------
    .. jupyter-execute::

        >>> from lets_plot.geo_data import *
        >>> r_city = regions_city(request=['New York', 'Los Angeles'])
        >>> r_city
    """
    return regions('city', request, within)


def distance(lon0, lat0, lon1, lat1, units='km'):
    """
    Calculate the distance between two points. Returns result in kilometers or miles.

    distance(lon0, lat0, lon1, lat1, units)

    Parameters
    ----------
    lon0: number
        Longitude coordinate of the first point.
    lat0: number
        Latitude coordinate of the first point.
    lon1: number
        Longitude coordinate of the second point.
    lat1: number
        Latitude coordinate of the second point.

    units: [string | None]
        The units in which the result will be obtained.
        There are shorthands for values: 'mi'(miles), 'km'(kilometers).
        Default is kilometers.

    Returns
    -------
    object : float

    Note
    -----
    distance() calculates the distance between two points.

    Examples
    ---------
    .. jupyter-execute::

        >>> from lets_plot.geo_data import *
        >>> dist = distance(-99.25061, 31.25044, -105.50083, 39.00027)
        >>> dist
    """
    return _calc_distance(lon0, lat0, lon1, lat1, units)


def _calc_distance(lon0, lat0, lon1, lat1, u):
    r = _prepare_units(u)

    lon0, lat0, lon1, lat1 = map(np.radians, [lon0, lat0, lon1, lat1])

    dlon = lon1 - lon0
    dlat = lat1 - lat0

    a = np.sin(dlat / 2.0) ** 2 + np.cos(lat0) * np.cos(lat1) * np.sin(dlon / 2.0) ** 2

    c = 2 * np.arcsin(np.sqrt(a))
    return c * r


def _prepare_units(units: Any) -> float:
    try:
        return UNITS_DICT[units]
    except KeyError:
        raise ValueError('Wrong units: {}. The units can take the following values: '
                         'mi (miles), km (kilometers).'.format(units))
