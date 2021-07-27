#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from ._global_settings import GEOCODING_PROVIDER_URL, MAPTILES_SOLID_FILL_COLOR, TILES_CHESSBOARD
from ._global_settings import MAPTILES_KIND, MAPTILES_URL, MAPTILES_THEME, MAPTILES_ATTRIBUTION, MAPTILES_MIN_ZOOM, \
    MAPTILES_MAX_ZOOM, TILES_VECTOR_LETS_PLOT, TILES_RASTER_ZXY, TILES_SOLID, _DATALORE_TILES_ATTRIBUTION
from ._global_settings import has_global_value, get_global_val, _DATALORE_TILES_MIN_ZOOM, _DATALORE_TILES_MAX_ZOOM

__all__ = ['maptiles_zxy', 'maptiles_lets_plot', 'maptiles_solid']


def maptiles_lets_plot(url: str = None, theme: str = None) -> dict:
    """
    Makes vector tiles config. Can be used individually in `geom_livemap()`
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
        # try to read url from global settings
        if has_global_value(MAPTILES_KIND) and get_global_val(MAPTILES_KIND) == TILES_VECTOR_LETS_PLOT:
            url = get_global_val(MAPTILES_URL) if has_global_value(MAPTILES_URL) else None

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
    Makes raster tiles config. Can be used individually in `geom_livemap()`
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
        Minimal zoom limit.
    max_zoom : int
        Maximal zoom limit.
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
        :emphasize-lines: 10-11

        from lets_plot import *
        LetsPlot.setup_html()
        attribution = '''
        Map tiles by
        <a href="http://stamen.com">Stamen Design</a>, under
        <a href="http://creativecommons.org/licenses/by/3.0">CC BY 3.0</a>.
        Data by <a href="http://openstreetmap.org">OpenStreetMap</a>, under
        <a href="http://www.openstreetmap.org/copyright">ODbL</a>
        '''
        tiles = maptiles_zxy(url='http://c.tile.stamen.com/terrain/{z}/{x}/{y}@2x.png',
                             attribution=attribution)
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
        url = url.replace("{s}", '[abs]')

    return {
        MAPTILES_KIND: TILES_RASTER_ZXY,
        MAPTILES_URL: url,
        MAPTILES_ATTRIBUTION: attribution,
        MAPTILES_MIN_ZOOM: min_zoom,
        MAPTILES_MAX_ZOOM: max_zoom
    }


def maptiles_solid(color: str):
    """
    Makes solid color tiles config. Can be used individually in `geom_livemap()`
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
        :emphasize-lines: 10-11

        from lets_plot import *
        LetsPlot.setup_html()
        tiles = maptiles_solid(color='#d3d3d3')
        ggplot() + geom_livemap(tiles=tiles)

    """
    return {
        MAPTILES_KIND: TILES_SOLID,
        MAPTILES_SOLID_FILL_COLOR: color
    }


def maptiles_chessboard():
    """
    Makes solid color tiles with chessboard pattern. Can be used individually in `geom_livemap()`
    or in every livemap via `LetsPlot.set()`.

    Returns
    -------
    dict
        Tile provider settings.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10-11

        from lets_plot.settings_utils import maptiles_chessboard
        LetsPlot.setup_html()
        ggplot() + geom_livemap(tiles=maptiles_chessboard())

    """
    return {
        MAPTILES_KIND: TILES_CHESSBOARD
    }


def geocoding_service(url: str):
    """
    Makes geocoding service config.
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
