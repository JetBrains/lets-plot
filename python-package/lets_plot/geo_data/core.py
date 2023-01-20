from typing import Any

import numpy as np

from .geocoder import Geocoder, ReverseGeocoder, NamesGeocoder

__all__ = [
    'distance',

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


def geocode(level=None, names=None, countries=None, states=None, counties=None, scope=None) -> NamesGeocoder:
    """
    Create a `NamesGeocoder`. Allow to refine ambiguous request with `where()` method,
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
    Create a `NamesGeocoder` object for cities. Allow to refine ambiguous request with
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
    Create a `NamesGeocoder` object for counties. Allow to refine ambiguous request with
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
    Create a `NamesGeocoder` object for states. Allow to refine ambiguous request with
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
    Create a `NamesGeocoder` object for countries. Allow to refine ambiguous request with
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
    Convert a location as described by geographic coordinates to a `ReverseGeocoder` object.

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
    Calculate the distance between two points. Return result in kilometers or miles.

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
