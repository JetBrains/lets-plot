#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

MAPTILES_KIND = 'maptiles_kind'
MAPTILES_URL = 'maptiles_url'
MAPTILES_THEME = 'maptiles_theme'
GEOCODING_PROVIDER_URL = 'geocoding_url'


_VECTOR_LETS_PLOT = 'vector_lets_plot'
_RASTER_ZXY = 'raster_zxy'

__all__ = ['maptiles_zxy']


def maptiles_lets_plot(url: str, theme: str = None) -> dict:
    """
    :param url: str
        Address of the tile server

    :param theme: ['color', 'light', 'dark', None]
        Ttiles theme

    :return:
        Tile provider settings
    """
    assert isinstance(url, (str, type(None))), "'url' argument is not str: {}".format(type(url))
    assert isinstance(theme, (str, type(None))), "'theme' argument is not str: {}".format(type(theme))

    return {
        MAPTILES_KIND: _VECTOR_LETS_PLOT,
        MAPTILES_URL: url,
        MAPTILES_THEME: theme
    }


def maptiles_zxy(url: str) -> dict:
    """
    :param url:
        Template for a standard raster ZXY tile provider with {z}, {x} and {y} wildcards, e.g. 'http://my.tile.com/{z}/{x}/{y}.png'
    :return:
        Tile provider settings
    """
    assert isinstance(url, (str, type(None))), "'url' argument is not str: {}".format(type(url))

    return {
        MAPTILES_KIND: _RASTER_ZXY,
        MAPTILES_URL: url
    }

def geocoding_service(url: str):
    """
    :param url:
        Address of the geocoding server
    :return:
        Geocoding service settings
    """
    assert isinstance(url, (str, type(None))), "'url' argument is not str: {}".format(type(url))

    return {
        GEOCODING_PROVIDER_URL: url
    }
