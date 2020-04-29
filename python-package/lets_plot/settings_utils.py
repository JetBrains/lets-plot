#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

ENV_TILES_PROVIDER_KIND = 'tiles_kind'
ENV_TILES_PROVIDER_URL = 'tiles_url'
ENV_TILES_PROVIDER_THEME = 'tiles_theme'
ENV_GEOCODING_PROVIDER_URL = 'geocoding_url'


_VECTOR_LETS_PLOT = 'vector_lets_plot'
_RASTER_ZXY = 'raster_zxy'

__all__ = ['tiles_provider_lets_plot', 'tiles_provider_zxy', 'geocoding_service']


def tiles_provider_lets_plot(url: str, theme: str = None) -> dict:
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
        ENV_TILES_PROVIDER_KIND: _VECTOR_LETS_PLOT,
        ENV_TILES_PROVIDER_URL: url,
        ENV_TILES_PROVIDER_THEME: theme
    }


def tiles_provider_zxy(url: str) -> dict:
    """
    :param url: str
        Address of the tile server
    :return:
        Tile provider settings
    """
    assert isinstance(url, (str, type(None))), "'url' argument is not str: {}".format(type(url))

    return {
        ENV_TILES_PROVIDER_KIND: _RASTER_ZXY,
        ENV_TILES_PROVIDER_URL: url
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
        ENV_GEOCODING_PROVIDER_URL: url
    }
