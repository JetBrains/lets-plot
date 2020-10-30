#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from ._global_settings import has_global_value, get_global_val
from ._global_settings import MAPTILES_KIND, MAPTILES_URL, MAPTILES_THEME, MAPTILES_ATTRIBUTION, MAPTILES_MIN_ZOOM, \
    MAPTILES_MAX_ZOOM, TILES_VECTOR_LETS_PLOT, TILES_RASTER_ZXY, _DATALORE_TILES_ATTRIBUTION
from ._global_settings import GEOCODING_PROVIDER_URL

__all__ = ['maptiles_zxy', 'maptiles_lets_plot']


def maptiles_lets_plot(url: str = None, theme: str = None) -> dict:
    """
    Makes vector tiles config.
    Can be used individually in geom_livemap() or in every livemap via LetsPlot.set(...)

    Parameters
    ----------
    url : [string, None]
        Address of the tile server. Can be ommited if URL is already set in global settings.

    theme : ['color', 'light', 'dark', None]
        Tiles theme.

    Returns
    -------
        Tile provider settings
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
        MAPTILES_ATTRIBUTION: _DATALORE_TILES_ATTRIBUTION
    }


def maptiles_zxy(url: str, attribution: str = None, min_zoom: int = None, max_zoom: int = None, **other_args) -> dict:
    """
    Makes raster tiles config.
    Can be used individually in geom_livemap() or in every livemap via LetsPlot.set(...)

    Parameters
    ----------
    url : string
        Template for a standard raster ZXY tile provider with {z}, {x} and {y} wildcards, e.g. 'http://my.tile.com/{z}/{x}/{y}.png'

    attribution : string
        An attribution or a copyright notice to display on the map as required by the tile license.
        Supports HTML links <a href="http://www.example.com">Example</a>

    min_zoom : int
        Minimal zoom limit

    max_zoom : int
        Maximal zoom limit

    other_args : **string
        Any key-value pairs that can be substituted into the url template

        maptiles_zxy(
            url = 'http://maps.example.com/{z}/{x}/{y}.png?access_key={key}'
            key = 'MY_ACCESS_KEY'
        )

    Returns
    -------
        Tile provider settings
    """
    assert isinstance(url, str), "'url' argument is not str: {}".format(type(url))
    assert isinstance(attribution, (str, type(None))), "'attribution' argument is not str: {}".format(type(url))

    for k, v in other_args.items():
        assert k not in ["x", "y", "z"], "other_args can't contain keys x, y and z"
        url = url.replace("{" + k + "}", v)

    return {
        MAPTILES_KIND: TILES_RASTER_ZXY,
        MAPTILES_URL: url,
        MAPTILES_ATTRIBUTION: attribution,
        MAPTILES_MIN_ZOOM: min_zoom,
        MAPTILES_MAX_ZOOM: max_zoom
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
