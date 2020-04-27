#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

_TILE_PROVIDER_SETTINGS = 'tile_provider_settings'

__all__ = ['vector_livemap_tiles', 'raster_zxy_tiles']


def vector_livemap_tiles(url: str, theme: str = None) -> dict:
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
        _TILE_PROVIDER_SETTINGS: {
            'kind': 'vector_livemap',
            'url': url,
            'theme': theme
        }
    }


def raster_zxy_tiles(url: str) -> dict:
    """
    :param url:
            Template for a standard raster ZXY tile provider with {z}, {x} and {y} wildcards, e.g. 'http://my.tile.com/{z}/{x}/{y}.png'
    :return:
        Tile provider settings
    """
    assert isinstance(url, (str, type(None))), "'url' argument is not str: {}".format(type(url))

    return {
        _TILE_PROVIDER_SETTINGS: {
            'kind': 'raster_zxy',
            'url': url
        }
    }
