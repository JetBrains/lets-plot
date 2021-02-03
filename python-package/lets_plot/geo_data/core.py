from typing import Any

import numpy as np

from .geocoder import Geocoder, ReverseGeocoder, NamesGeocoder

__all__ = [
    'distance',
    'regions_builder',
    'regions',
    'regions_country',
    'regions_state',
    'regions_county',
    'regions_city',
    'regions_xy',

    'geocode',
    'geocode_cities',
    'geocode_counties',
    'geocode_states',
    'geocode_countries',
    'reverse_geocode'
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


def regions_xy(lon, lat, level, within=None) -> Geocoder:
    raise ValueError('Function `regions_xy(...)` is deprecated. Use new function `reverse_geocode(...)`.\n See https://github.com/JetBrains/lets-plot/blob/master/docs/geocoding.md for details.')


def regions_builder(level=None, request=None, within=None, highlights=False):
    """
    Create a RegionBuilder class by level and request. Allows to refine ambiguous request with
    where method. build() method creates Geocoder object or shows details for ambiguous result.

    regions_builder(level, request, within)

    Parameters
    ----------
    level : ['country' | 'state' | 'county' | 'city' | None]
        The level of administrative division. Default is a 'state'.
    request : [array | string | None]
        Data can be filtered by full names at any level (only exact matching).
        For 'state' level:
        -'US-48' returns continental part of United States (48 states) in a compact form.
    within : [array | string | Geocoder | None]
        Data can be filtered by within name.
        If within is array then request and within will be merged positionally (size should be equal).
        If within is Geocoder then request will be searched in any of these regions.
        'US-48' includes continental part of United States (48 states).

    Returns
    -------
    Geocoder object :

    Note
    -----
    regions_builder() allows to refine ambiguous request with where() method. Call build() method to create Geocoder object

    Examples
    ---------
    .. jupyter-execute::

        >>> from lets_plot.geo_data import *
        >>> r = regions_builder(level='city', request=['moscow', 'york']).where('york', regions_state('New York')).build()
        >>> r
    """
    raise ValueError('Function `regions_builder(...)` is deprecated. Use new function `geocode(...)`.\n See https://github.com/JetBrains/lets-plot/blob/master/docs/geocoding.md for details.')
    #return Geocoder(level, request, within, highlights)


def regions(level=None, request=None, within=None):
    """
    Create a Geocoder class by level and request.

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
    within : [array | string | Geocoder| None]
        Data can be filtered by within name.
        If within is array then request and within will be merged positionally (size should be equal).
        If within is Geocoder then request will be searched in any of these regions.
        'US-48' includes continental part of United States (48 states).

    Returns
    -------
    Geocoder object :

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
    raise ValueError('Function `regions(...)` is deprecated. Use new function `geocode(...)`.\n See https://github.com/JetBrains/lets-plot/blob/master/docs/geocoding.md for details.')
    #return Geocoder(level=level, request=request, scope=within).build()


def regions_country(request=None):
    """
    Create a Geocoder class for country level by request.

    regions_country(request)

    Parameters
    ----------
    request : [array | string | None]
        Data can be filtered by full names at any level (only exact matching).

    Returns
    -------
    Geocoder object :

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
    raise ValueError('Function `regions_country(...)` is deprecated. Use new function `geocode_countries(...)`.\n See https://github.com/JetBrains/lets-plot/blob/master/docs/geocoding.md for details.')
    #return regions('country', request, None)


def regions_state(request=None, within=None):
    """
    Create a Geocoder class for state level by request.

    regions_state(request, within)

    Parameters
    ----------
    request : [array | string | None]
        Data can be filtered by full names at any level (only exact matching).
        For 'state' level:
        -'US-48' returns continental part of United States (48 states) in a compact form.
    within : [array | string | Geocoder| None]
        Data can be filtered by within name.
        If within is array then filter and within will be merged positionally (size should be equal).
        If within is Geocoder then request will be searched in any of these regions.
        'US-48' includes continental part of United States (48 states).

    Returns
    -------
    Geocoder object :

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
    raise ValueError('Function `regions_state(...)` is deprecated. Use new function `geocode_states(...)`.\n See https://github.com/JetBrains/lets-plot/blob/master/docs/geocoding.md for details.')
    #return regions('state', request, within)


def regions_county(request=None, within=None):
    """
    Create a Geocoder class for county level by request.

    regions_county(request, within)

    Parameters
    ----------
    request : [array | string | None]
        Data can be filtered by full names at any level (only exact matching).
    within : [array | string | Geocoder| None]
        Data can be filtered by within name.
        If within is array then request and within will be merged positionally (size should be equal).
        If within is Geocoder then request will be searched in any of these regions.
        'US-48' includes continental part of United States (48 states).

    Returns
    -------
    Geocoder object :

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
    raise ValueError('Function `regions_county(...)` is deprecated. Use new function `geocode_counties(...)`.\n See https://github.com/JetBrains/lets-plot/blob/master/docs/geocoding.md for details.')
    #return regions('county', request, within)


def regions_city(request=None, within=None):
    """
    Create a Geocoder class for city level by request.

    regions_city(request, within)

    Parameters
    ----------
    request : [array | string | None]
        Data can be filtered by full names at any level (only exact matching).
    within : [array | string | Geocoder| None]
        Data can be filtered by within name.
        If within is array then request and within will be merged positionally (size should be equal).
        If within is Geocoder then request will be searched in any of these regions.
        'US-48' includes continental part of United States (48 states).

    Returns
    -------
    Geocoder object :

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
    raise ValueError('Function `regions_city(...)` is deprecated. Use new function `geocode_cities(...)`.\n See https://github.com/JetBrains/lets-plot/blob/master/docs/geocoding.md for details.')
    #return regions('city', request, within)



def geocode(level=None, names=None, countries=None, states=None, counties=None, scope=None) -> NamesGeocoder:
    """
    Create a Geocoder. Allows to refine ambiguous request with where method, scope that limits area of geocoding
    or with parents.

    Parameters
    ----------
    level : ['country' | 'state' | 'county' | 'city' | None]
        The level of administrative division. Autodetection by default.
    names : [array | string | None]
        Names of objects to be geocoded.
        For 'state' level:
        -'US-48' returns continental part of United States (48 states) in a compact form.
    countries : [array | None]
        Parent countries. Should have same size as names. Can contain strings or Geocoder objects.
    states : [array | None]
        Parent states. Should have same size as names. Can contain strings or Geocoder objects.
    counties : [array | None]
        Parent counties. Should have same size as names. Can contain strings or Geocoder objects.
    scope : [string | Geocoder | None]
        Limits area of geocoding. If parent country is set then error will be generated.
        If type is a string - geoobject should have geocoded scope in parents.
        If type is a Geocoder  - geoobject should have geocoded scope in parents. Scope should contain only one entry.
    """
    return NamesGeocoder(level, names) \
        .scope(scope) \
        .countries(countries) \
        .states(states) \
        .counties(counties)


def geocode_cities(names=None) -> NamesGeocoder:
    """
    Create a Geocoder object for cities. Allows to refine ambiguous request with
    where method, with a scope that limits area of geocoding or with parents.

    geocode_cities(names)

    Parameters
    ----------
    names : [array | string | None]
        Names of objects to be geocoded.

    Returns
    -------
    Geocoder object :

    Note
    -----
    Geocoder allows to refine ambiguous request with where() method.

    Examples
    ---------
    >>> from lets_plot.geo_data import *
    >>> r = geocode_cities(['moscow', 'york']).where('york', scope=geocode_states('New York')).get_geocodes()
    """
    return NamesGeocoder('city', names)


def geocode_counties(names=None) -> NamesGeocoder:
    """
    Create a Geocoder object for counties. Allows to refine ambiguous request with
    where method, with a scope that limits area of geocoding or with parents.

    geocode_counties(names)

    Parameters
    ----------
    names : [array | string | None]
        Names of objects to be geocoded.

    Returns
    -------
    Geocoder object :

    Note
    -----
    Geocoder allows to refine ambiguous request with where() method.

    Examples
    ---------
    >>> from lets_plot.geo_data import *
    >>> r = geocode_counties('barnstable').get_geocodes()
    """
    return NamesGeocoder('county', names)


def geocode_states(names=None) -> NamesGeocoder:
    """
    Create a Geocoder object for states. Allows to refine ambiguous request with
    where method, with a scope that limits area of geocoding or with parents.

    geocode_states(names)

    Parameters
    ----------
    names : [array | string | None]
        Names of objects to be geocoded.

    Returns
    -------
    Geocoder object :

    Note
    -----
    Geocoder allows to refine ambiguous request with where() method.

    Examples
    ---------
    >>> from lets_plot.geo_data import *
    >>> r = geocode_states('texas').get_geocodes()
    """
    return NamesGeocoder('state', names)


def geocode_countries(names=None) -> NamesGeocoder:
    """
    Create a Geocoder object for countries. Allows to refine ambiguous request with
    where method.

    geocode_countries(names)

    Parameters
    ----------
    names : [array | string | None]
        Names of objects to be geocoded.

    Returns
    -------
    Geocoder object :

    Note
    -----
    Geocoder allows to refine ambiguous request with where() method.

    Examples
    ---------
    >>> from lets_plot.geo_data import *
    >>> r = geocode_countries('USA').get_geocodes()
    """
    return NamesGeocoder('country', names)

def reverse_geocode(lon, lat, level=None, scope=None) -> ReverseGeocoder:
    return ReverseGeocoder(lon, lat, level, scope)

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
