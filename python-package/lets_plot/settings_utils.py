#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from ._global_settings import GEOCODING_PROVIDER_URL, MAPTILES_SOLID_FILL_COLOR, TILES_CHESSBOARD, \
    _DATALORE_TILES_SERVICE
from ._global_settings import MAPTILES_KIND, MAPTILES_URL, MAPTILES_THEME, MAPTILES_ATTRIBUTION, MAPTILES_MIN_ZOOM, \
    MAPTILES_MAX_ZOOM, TILES_VECTOR_LETS_PLOT, TILES_RASTER_ZXY, TILES_SOLID, _DATALORE_TILES_ATTRIBUTION
from ._global_settings import has_global_value, get_global_val, _DATALORE_TILES_MIN_ZOOM, _DATALORE_TILES_MAX_ZOOM

__all__ = ['maptiles_zxy', 'maptiles_lets_plot', 'maptiles_solid']


def maptiles_lets_plot(url: str = None, theme: str = None) -> dict:
    """
    Make vector tiles config. Can be used individually in `geom_livemap()`
    or in every livemap via `LetsPlot.set()`.

    Parameters
    ----------
    url : str
        Address of the tile server. Can be ommited if URL is already set in global settings.

    theme : {'color', 'light', 'dark'}
        Tiles theme.

    Returns
    -------
    dict
        Tile provider settings.

    Notes
    -----
    If you're using Safari, and you're having trouble loading tiles, try disabling the NSURLSession Websocket feature.
    Go to `Develop -> Experimental Features -> NSURLSession Websocket` to turn it off.

    Also, you could use raster tiles from `lets_plot.tilesets`, e.g.
    `ggplot() + geom_livemap(tiles=tilesets.OPEN_TOPO_MAP)`

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 3

        from lets_plot import *
        LetsPlot.setup_html()
        tiles = maptiles_lets_plot(url='wss://tiles.datalore.jetbrains.com', theme='light')
        ggplot() + geom_livemap(tiles=tiles)

    """
    assert isinstance(url, (str, type(None))), "'url' argument is not str: {}".format(type(url))
    assert isinstance(theme, (str, type(None))), "'theme' argument is not str: {}".format(type(theme))

    if url is None:
        global_maptiles_kind = get_global_val(MAPTILES_KIND) if has_global_value(MAPTILES_KIND) else None
        global_maptiles_url = get_global_val(MAPTILES_URL) if has_global_value(MAPTILES_URL) else None

        # try to read url from global settings
        if global_maptiles_kind == TILES_VECTOR_LETS_PLOT:
            if global_maptiles_url is None:
                # global URL is somehow broken - use default URL
                url = _DATALORE_TILES_SERVICE
            else:
                url = global_maptiles_url
        else:
            # User input:
            # LetsPlot.set(maptiles_zxy(...))
            # LetsPlot.set(maptiles_lets_plot(...))
            # In this case global_maptiles_url will contain not-applicable raster tile URL.
            # Use hardcoded lets_plot tiles URL.
            url = _DATALORE_TILES_SERVICE

    if url is None:
        raise ValueError('lets_plot tiles service URL is not defined')

    return {
        MAPTILES_KIND: TILES_VECTOR_LETS_PLOT,
        MAPTILES_URL: url,
        MAPTILES_THEME: theme,
        MAPTILES_ATTRIBUTION: _DATALORE_TILES_ATTRIBUTION,
        MAPTILES_MIN_ZOOM: _DATALORE_TILES_MIN_ZOOM,
        MAPTILES_MAX_ZOOM: _DATALORE_TILES_MAX_ZOOM,
    }


def maptiles_zxy(url: str, attribution: str = None, min_zoom: int = None, max_zoom: int = None, subdomains: str = None,
                 **other_args) -> dict:
    """
    Make raster tiles config. Can be used individually in `geom_livemap()`
    or in every livemap via `LetsPlot.set()`.

    Parameters
    ----------
    url : str
        Template for a standard raster ZXY tile provider with {z}, {x}, {y} and {s} placeholders,
        e.g. ``"https://{s}.tile.com/{z}/{x}/{y}.png"``. Where {z} means zoom, {x} and {y} means
        tile coordinate, {s} means subdomains.
    attribution : str
        An attribution or a copyright notice to display on the map as required by the tile license.
        Supports HTML links: ``'<a href="http://www.example.com">Example</a>'``.
    min_zoom : int
        Minimal zoom limit, an integer from 1 to 15. Should be less than or equal to `max_zoom`.
    max_zoom : int
        Maximal zoom limit, an integer from 1 to 15. Should be greater than or equal to `min_zoom`.
    subdomains : str
        Each character of this list is interpreted as standalone tile servers, so an interactive map
        can request tiles from any of these servers independently for better load balance. If url
        contains {s} placeholder and subdomains parameter is not set default string 'abc' will be used.
    other_args
        Any key-value pairs that can be substituted into the URL template, e.g.
        ``maptiles_zxy(url='http://maps.example.com/{z}/{x}/{y}.png?access_key={key}', key='MY_ACCESS_KEY')``.

    Returns
    -------
    dict
        Tile provider settings.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 3-7

        from lets_plot import *
        LetsPlot.setup_html()
        tiles = maptiles_zxy(
            url="https://gibs.earthdata.nasa.gov/wmts/epsg3857/best/VIIRS_CityLights_2012/default/GoogleMapsCompatible_Level8/{z}/{y}/{x}.jpg",
            attribution='<a href="https://earthdata.nasa.gov/eosdis/science-system-description/eosdis-components/gibs">© NASA Global Imagery Browse Services (GIBS)</a>',
            max_zoom=8
        )
        ggplot() + geom_livemap(tiles=tiles)

    """
    assert isinstance(url, str), "'url' argument is not str: {}".format(type(url))
    assert isinstance(attribution, (str, type(None))), "'attribution' argument is not str: {}".format(type(url))
    if subdomains is not None and "{s}" not in url:
        raise ValueError("Subdomains are set but {s} placeholder is not found in url: " + subdomains)

    for k, v in other_args.items():
        assert k not in ["x", "y", "z", "s"], "other_args can't contain keys x, y, z and s"
        url = url.replace("{" + k + "}", v)

    if subdomains is not None and "{s}" in url:
        url = url.replace("{s}", '[' + subdomains + ']')
    elif subdomains is None and "{s}" in url:
        url = url.replace("{s}", '[abc]')

    return {
        MAPTILES_KIND: TILES_RASTER_ZXY,
        MAPTILES_URL: url,
        MAPTILES_ATTRIBUTION: attribution,
        MAPTILES_MIN_ZOOM: min_zoom,
        MAPTILES_MAX_ZOOM: max_zoom
    }


def maptiles_solid(color: str):
    """
    Make solid color tiles config. Can be used individually in `geom_livemap()`
    or in every livemap via `LetsPlot.set()`.

    Parameters
    ----------
    color : str
        Color in HEX format.

    Returns
    -------
    dict
        Tile provider settings.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5

        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        nyc = geocode_cities('New York').get_boundaries()
        tiles = maptiles_solid(color='#d3d3d3')
        ggplot() + geom_livemap(tiles=tiles) + geom_map(data=nyc)

    """
    return {
        MAPTILES_KIND: TILES_SOLID,
        MAPTILES_SOLID_FILL_COLOR: color
    }


def maptiles_chessboard():
    """
    Make solid color tiles with chessboard pattern. Can be used individually in `geom_livemap()`
    or in every livemap via `LetsPlot.set()`.

    Returns
    -------
    dict
        Tile provider settings.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 3

        from lets_plot.settings_utils import maptiles_chessboard
        LetsPlot.setup_html()
        ggplot() + geom_livemap(tiles=maptiles_chessboard())

    """
    return {
        MAPTILES_KIND: TILES_CHESSBOARD
    }


def geocoding_service(url: str):
    """
    Make geocoding service config.
    Can be applied via LetsPlot.set(...)

    Parameters
    ----------
    url : string
        Address of the geocoding server

    Returns
    -------
        Geocoding service settings
    """
    assert isinstance(url, str), "'url' argument is not str: {}".format(type(url))

    return {
        GEOCODING_PROVIDER_URL: url
    }
