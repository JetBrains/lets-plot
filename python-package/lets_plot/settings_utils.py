#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

MAPTILES_KIND = 'maptiles_kind'
MAPTILES_URL = 'maptiles_url'
MAPTILES_THEME = 'maptiles_theme'
MAPTILES_ATTRIBUTION = 'maptiles_attribution'
MAPTILES_MIN_ZOOM = 'maptiles_min_zoom'
MAPTILES_MAX_ZOOM = 'maptiles_max_zoom'

GEOCODING_PROVIDER_URL = 'geocoding_url'


_VECTOR_LETS_PLOT = 'vector_lets_plot'
_RASTER_ZXY = 'raster_zxy'

__all__ = ['maptiles_zxy']


def maptiles_lets_plot(url: str, theme: str = None) -> dict:
    """
    :param url: str
        Address of the tile server

    :param theme: ['color', 'light', 'dark', None]
        Tiles theme

    :return:
        Tile provider settings
    """
    assert isinstance(url, (str, type(None))), "'url' argument is not str: {}".format(type(url))
    assert isinstance(theme, (str, type(None))), "'theme' argument is not str: {}".format(type(theme))

    return {
        MAPTILES_KIND: _VECTOR_LETS_PLOT,
        MAPTILES_URL: url,
        MAPTILES_THEME: theme,
        MAPTILES_ATTRIBUTION: 'Map data <a href="https://www.openstreetmap.org/copyright">\u00a9 OpenStreetMap</a> contributors'
    }


def maptiles_zxy(url: str, attribution: str = None, min_zoom: int = None, max_zoom: int = None, **other_args) -> dict:
    """
    :param url:
        Template for a standard raster ZXY tile provider with {z}, {x} and {y} wildcards, e.g. 'http://my.tile.com/{z}/{x}/{y}.png'
    :param attribution:
        An attribution or a copyright notice to display on the map as required by the tile license.
        Supports HTML links <a href="http://www.example.com">Example</a>
    :param min_zoom:
        Minimal zoom limit
    :param max_zoom:
        Maximal zoom limit
    :param other_args
        Any key-value pairs that can be substituted into the url template

        maptiles_zxy(
            url = 'http://{sub}.example.com/{z}/{x}/{y}.png'
            sub = 'maps'
        )
    :return:
        Tile provider settings
    """
    assert isinstance(url, (str, type(None))), "'url' argument is not str: {}".format(type(url))
    assert isinstance(attribution, (str, type(None))), "'attribution' argument is not str: {}".format(type(url))

    for k, v in other_args.items():
        assert k not in ["x", "y", "z"], "other_args can't contain keys x, y and z"
        url = url.replace("{" + k + "}", v)

    return {
        MAPTILES_KIND: _RASTER_ZXY,
        MAPTILES_URL: url,
        MAPTILES_ATTRIBUTION: attribution,
        MAPTILES_MIN_ZOOM: min_zoom,
        MAPTILES_MAX_ZOOM: max_zoom
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
