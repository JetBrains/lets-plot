#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

ENV_TILES_PROVIDER_KIND = 'tiles_kind'
ENV_TILES_PROVIDER_URL = 'tiles_url'
ENV_TILES_PROVIDER_THEME = 'tiles_theme'


_VECTOR_LETS_PLOT = 'vector_lets_plot'
_RASTER_ZXY = 'raster_zxy'

__all__ = ['tiles_provider_lets_plot', 'tiles_provider_zxy']


def tiles_provider_lets_plot(url: str, theme: str = None) -> dict:
    """
    :param url: str
        If kind is 'datalore': address of the tile server

    :param theme: ['color', 'light', 'dark', None]
        If kind is 'datalore': tiles theme

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
    :param url:
            Template for a standard raster ZXY tile provider with {z}, {x} and {y} wildcards, e.g. 'http://my.tile.com/{z}/{x}/{y}.png'
    :return:
        Tile provider settings
    """
    assert isinstance(url, (str, type(None))), "'url' argument is not str: {}".format(type(url))

    return {
        ENV_TILES_PROVIDER_KIND: _RASTER_ZXY,
        ENV_TILES_PROVIDER_URL: url
    }
