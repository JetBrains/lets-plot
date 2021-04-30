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
    raise ValueError('Function `regions_xy(...)` is deprecated. Use new function `reverse_geocode(...)`.\n See https://jetbrains.github.io/lets-plot-docs/pages/features/geocoding.html for details.')


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
        :raises: ValueError

        >>> from lets_plot.geo_data import *
        >>> r = regions_builder(level='city', request=['moscow', 'york']).where('york', regions_state('New York')).build()
        >>> r
    """
    raise ValueError('Function `regions_builder(...)` is deprecated. Use new function `geocode(...)`.\n See https://jetbrains.github.io/lets-plot-docs/pages/features/geocoding.html for details.')
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
        :raises: ValueError

        >>> from lets_plot.geo_data import *
        >>> r = regions(level='country', request=['Germany', 'USA'])
        >>> r
    """
    raise ValueError('Function `regions(...)` is deprecated. Use new function `geocode(...)`.\n See https://jetbrains.github.io/lets-plot-docs/pages/features/geocoding.html for details.')
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
        :raises: ValueError

        >>> from lets_plot.geo_data import *
        >>> r_country = regions_country(request=['Germany', 'USA'])
        >>> r_country
    """
    raise ValueError('Function `regions_country(...)` is deprecated. Use new function `geocode_countries(...)`.\n See https://jetbrains.github.io/lets-plot-docs/pages/features/geocoding.html for details.')
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
        :raises: ValueError

        >>> from lets_plot.geo_data import *
        >>> r_state = regions_state(request=['Texas', 'Iowa'], within='USA')
        >>> r_state
    """
    raise ValueError('Function `regions_state(...)` is deprecated. Use new function `geocode_states(...)`.\n See https://jetbrains.github.io/lets-plot-docs/pages/features/geocoding.html for details.')
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
        :raises: ValueError

        >>> from lets_plot.geo_data import *
        >>> r_county = regions_county(request=['Calhoun County', 'Howard County'], within='Texas')
        >>> r_county
    """
    raise ValueError('Function `regions_county(...)` is deprecated. Use new function `geocode_counties(...)`.\n See https://jetbrains.github.io/lets-plot-docs/pages/features/geocoding.html for details.')
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
        :raises: ValueError

        >>> from lets_plot.geo_data import *
        >>> r_city = regions_city(request=['New York', 'Los Angeles'])
        >>> r_city
    """
    raise ValueError('Function `regions_city(...)` is deprecated. Use new function `geocode_cities(...)`.\n See https://jetbrains.github.io/lets-plot-docs/pages/features/geocoding.html for details.')
    #return regions('city', request, within)



def geocode(level=None, names=None, countries=None, states=None, counties=None, scope=None) -> NamesGeocoder:
    """
    Create a `Geocoder`. Allows to refine ambiguous request with `where()` method,
    scope that limits area of geocoding or with parents.

    Parameters
    ----------
    level : {'country', 'state', 'county', 'city'}
        The level of administrative division. Autodetection by default.
    names : list or str
        Names of objects to be geocoded.
        For 'state' level: 'US-48' returns continental part of United States (48 states)
        in a compact form.
    countries : list
        Parent countries. Should have same size as names. Can contain strings or `Geocoder` objects.
    states : list
        Parent states. Should have same size as names. Can contain strings or `Geocoder` objects.
    counties : list
        Parent counties. Should have same size as names. Can contain strings or `Geocoder` objects.
    scope : str or `Geocoder`
        Limits area of geocoding. If parent country is set then error will be generated.
        If type is a string - geoobject should have geocoded scope in parents.
        If type is a `Geocoder` - geoobject should have geocoded scope in parents.
        Scope should contain only one entry.

    Returns
    -------
    `NamesGeocoder`
        Geocoder object specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5

        from IPython.display import display
        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        states = geocode('state').scope('Italy').get_boundaries(6)
        display(states.head())
        ggplot() + geom_map(data=states)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5, 8

        from IPython.display import display
        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        states = geocode(level='state', scope='US').get_geocodes()
        display(states.head())
        names = ['York'] * len(states.state)
        cities = geocode(names=names, states=states.state).ignore_not_found().get_centroids()
        display(cities.head())
        ggplot() + \\
            geom_livemap() + \\
            geom_point(data=cities, tooltips=layer_tooltips().line('@{found name}'))

    """
    return NamesGeocoder(level, names) \
        .scope(scope) \
        .countries(countries) \
        .states(states) \
        .counties(counties)


def geocode_cities(names=None) -> NamesGeocoder:
    """
    Create a `Geocoder` object for cities. Allows to refine ambiguous request with
    `where()` method, with a scope that limits area of geocoding or with parents.

    Parameters
    ----------
    names : str or list
        Names of objects to be geocoded.

    Returns
    -------
    `NamesGeocoder`
        Geocoder object specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5

        from IPython.display import display
        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        cities = geocode_cities(['York', 'Jersey'])\\
                 .where(name='Jersey', scope='New Jersey').get_boundaries()
        display(cities)
        ggplot() + geom_map(aes(fill='found name'), data=cities, color='white')

    """
    return NamesGeocoder('city', names)


def geocode_counties(names=None) -> NamesGeocoder:
    """
    Create a `Geocoder` object for counties. Allows to refine ambiguous request with
    `where()` method, with a scope that limits area of geocoding or with parents.

    Parameters
    ----------
    names : str or list
        Names of objects to be geocoded.

    Returns
    -------
    `NamesGeocoder`
        Geocoder object specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5

        from IPython.display import display
        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        counties = geocode_counties().scope('NY').get_boundaries(9)
        display(counties.head())
        ggplot() + geom_map(data=counties) + ggtitle('New York State Counties')

    """
    return NamesGeocoder('county', names)


def geocode_states(names=None) -> NamesGeocoder:
    """
    Create a `Geocoder` object for states. Allows to refine ambiguous request with
    `where()` method, with a scope that limits area of geocoding or with parents.

    Parameters
    ----------
    names : str or list
        Names of objects to be geocoded.

    Returns
    -------
    `NamesGeocoder`
        Geocoder object specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5

        from IPython.display import display
        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        states = geocode_states().scope('UK').get_boundaries()
        display(states)
        ggplot() + \\
            geom_map(aes(fill='found name'), data=states, color='white') + \\
            ggtitle('UK States')

    """
    return NamesGeocoder('state', names)


def geocode_countries(names=None) -> NamesGeocoder:
    """
    Create a `Geocoder` object for countries. Allows to refine ambiguous request with
    `where()` method.

    Parameters
    ----------
    names : str or list
        Names of objects to be geocoded.

    Returns
    -------
    `NamesGeocoder`
        Geocoder object specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5

        from IPython.display import display
        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        countries = geocode_countries(['Germany', 'Poland']).inc_res().get_boundaries()
        display(countries)
        ggplot() + geom_map(aes(fill='found name'), data=countries, color='white')

    """
    return NamesGeocoder('country', names)

def reverse_geocode(lon, lat, level=None, scope=None) -> ReverseGeocoder:
    """
    Convert a location as described by geographic coordinates to a `Geocoder` object.

    Parameters
    ----------
    lon : float
        Longitude coordinate of the geoobject.
    lat : float
        Latitude coordinate of the geoobject.
    level : {'country', 'state', 'county', 'city'}
        The level of administrative division.
    scope : str or `Geocoder`
        Specify this for resolving conflicts for disputed territories.

    Returns
    -------
    `ReverseGeocoder`
        Geocoder object specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5

        from IPython.display import display
        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        city = reverse_geocode(-73.87, 40.68, level='city').get_boundaries()
        display(city)
        ggplot() + geom_map(data=city) + ggtitle(city.iloc[0]['found name'])

    """
    return ReverseGeocoder(lon, lat, level, scope)

def distance(lon0, lat0, lon1, lat1, units='km'):
    """
    Calculate the distance between two points. Returns result in kilometers or miles.

    Parameters
    ----------
    lon0 : float
        Longitude coordinate of the first point.
    lat0 : float
        Latitude coordinate of the first point.
    lon1 : float
        Longitude coordinate of the second point.
    lat1 : float
        Latitude coordinate of the second point.
    units : {'mi', 'km'}, default='km'
        The units in which the result will be obtained.
        There are shorthands for values: 'mi' (miles), 'km' (kilometers).

    Returns
    -------
    float
        Distance between the points.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 3

        from lets_plot.geo_data import *
        cities = geocode_cities(['New York', 'Chicago']).get_centroids().geometry
        distance(cities[0].x, cities[0].y, cities[1].x, cities[1].y, units='mi')

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
