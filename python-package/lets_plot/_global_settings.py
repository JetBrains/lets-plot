#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import os
from typing import Any

from ._version import __version__

# Supported environment variables.
# 'bool' variables accept values:
#       True: 'true', '1', 't', 'y', 'yes'
#       False: 'false', '0', 'f', 'n', 'no'
#
# Production mode env variables:
ENV_HTML_ISOLATED_FRAME = 'LETS_PLOT_HTML_ISOLATED_FRAME'  # bool
ENV_OFFLINE = 'LETS_PLOT_OFFLINE'  # bool
ENV_NO_JS = 'LETS_PLOT_NO_JS'  # bool
ENV_MAX_WIDTH = 'LETS_PLOT_MAX_WIDTH'
ENV_MAX_HEIGHT = 'LETS_PLOT_MAX_HEIGHT'
ENV_MAPTILES_KIND = 'LETS_PLOT_MAPTILES_KIND'
ENV_MAPTILES_URL = 'LETS_PLOT_MAPTILES_URL'
ENV_MAPTILES_THEME = 'LETS_PLOT_MAPTILES_THEME'
ENV_GEOCODING_URL = 'LETS_PLOT_GEOCODING_URL'

# Dev mode env variables have 'LETS_PLOT_DEV_' prefix instead of 'LETS_PLOT_'.
ENV_DEV_HTML_ISOLATED_FRAME = 'LETS_PLOT_DEV_HTML_ISOLATED_FRAME'  # bool
ENV_DEV_OFFLINE = 'LETS_PLOT_DEV_OFFLINE'  # bool
ENV_DEV_NO_JS = 'LETS_PLOT_DEV_NO_JS'  # bool
ENV_DEV_MAX_WIDTH = 'LETS_PLOT_DEV_MAX_WIDTH'
ENV_DEV_MAX_HEIGHT = 'LETS_PLOT_DEV_MAX_HEIGHT'
ENV_DEV_MAPTILES_KIND = 'LETS_PLOT_DEV_MAPTILES_KIND'
ENV_DEV_MAPTILES_URL = 'LETS_PLOT_DEV_MAPTILES_URL'
ENV_DEV_MAPTILES_THEME = 'LETS_PLOT_DEV_MAPTILES_THEME'
ENV_DEV_GEOCODING_URL = 'LETS_PLOT_DEV_GEOCODING_URL'

# Options

HTML_ISOLATED_FRAME = 'html_isolated_frame'
OFFLINE = 'offline'
NO_JS = 'no_js'
JS_BASE_URL = 'js_base_url'
JS_PATH_TO_FILE = 'js_path_to_file'
JS_NAME = 'js_name'
JS_URL_MANUAL = 'js_url_manual'
MAX_WIDTH = 'max_width'
MAX_HEIGHT = 'max_height'

MAPTILES_KIND = 'maptiles_kind'
MAPTILES_URL = 'maptiles_url'
MAPTILES_THEME = 'maptiles_theme'
MAPTILES_ATTRIBUTION = 'maptiles_attribution'
MAPTILES_MIN_ZOOM = 'maptiles_min_zoom'
MAPTILES_MAX_ZOOM = 'maptiles_max_zoom'
MAPTILES_SOLID_FILL_COLOR = 'maptiles_fill_color'
TILES_VECTOR_LETS_PLOT = 'vector_lets_plot'
TILES_RASTER_ZXY = 'raster_zxy'
TILES_SOLID = 'solid'
TILES_CHESSBOARD = 'chessboard'
GEOCODING_PROVIDER_URL = 'geocoding_url'
GEOCODING_ROUTE = '/map_data/geocoding'
FRAGMENTS_ENABLED = 'fragments_enabled'

_DATALORE_TILES_SERVICE = 'wss://tiles.datalore.jetbrains.com'
_DATALORE_TILES_ATTRIBUTION = '<a href="https://lets-plot.org">\u00a9 Lets-Plot</a>, map data: <a href="https://www.openstreetmap.org/copyright">\u00a9 OpenStreetMap contributors</a>.'
_DATALORE_TILES_THEME = 'color'
_DATALORE_TILES_MIN_ZOOM = 1
_DATALORE_TILES_MAX_ZOOM = 15
_DATALORE_GEOCODING_SERVICE = 'https://geo2.datalore.jetbrains.com'


def _init_value(actual_name: str, def_val: Any) -> Any:
    env_val = _get_env_val(actual_name)
    return env_val if env_val else def_val


def _get_env_val(actual_name: str) -> Any:
    env_name = "LETS_PLOT_{}".format(actual_name.upper())
    return os.environ.get(env_name)


_settings = {
    OFFLINE: _init_value(OFFLINE, False),  # default: download from CDN
    NO_JS: _init_value(NO_JS, False),
    # JS_BASE_URL: 'https://dl.bintray.com/jetbrains/lets-plot',
    # JS_BASE_URL: "https://cdnjs.cloudflare.com/ajax/libs/lets-plot",
    JS_BASE_URL: "https://cdn.jsdelivr.net/gh/JetBrains/lets-plot@v{version}".format(version=__version__),
    JS_PATH_TO_FILE: "js-package/distr",
    JS_NAME: '',  # default: lets-plot.min.js
    GEOCODING_PROVIDER_URL: _init_value(GEOCODING_PROVIDER_URL, _DATALORE_GEOCODING_SERVICE),
    MAPTILES_KIND: _init_value(MAPTILES_KIND, TILES_VECTOR_LETS_PLOT),
    MAPTILES_URL: _init_value(MAPTILES_URL, _DATALORE_TILES_SERVICE),
    MAPTILES_ATTRIBUTION: _init_value(MAPTILES_ATTRIBUTION, _DATALORE_TILES_ATTRIBUTION),
    MAPTILES_THEME: _init_value(MAPTILES_THEME, _DATALORE_TILES_THEME),
    MAPTILES_MIN_ZOOM: _init_value(MAPTILES_MIN_ZOOM, _DATALORE_TILES_MIN_ZOOM),
    MAPTILES_MAX_ZOOM: _init_value(MAPTILES_MAX_ZOOM, _DATALORE_TILES_MAX_ZOOM),

    'dev_' + OFFLINE: _init_value('dev_' + OFFLINE, True),  # default: embed js into the notebook
    'dev_' + NO_JS: _init_value('dev_' + NO_JS, False),
    # We don't publish "dev" version, it must be served on localhost:
    # $ cd lets-plot
    # ./gradlew js-package:jsBrowserDevelopmentWebpack
    # $ python -m http.server 8080
    'dev_' + JS_BASE_URL: "http://127.0.0.1:8080",
    'dev_' + JS_PATH_TO_FILE: "js-package/build/kotlin-webpack/js/developmentExecutable",
    'dev_' + JS_NAME: '',  # default: lets-plot.js
    'dev_' + GEOCODING_PROVIDER_URL: _init_value('dev_' + GEOCODING_PROVIDER_URL, _DATALORE_GEOCODING_SERVICE),
    'dev_' + MAPTILES_KIND: _init_value('dev_' + MAPTILES_KIND, TILES_VECTOR_LETS_PLOT),
    'dev_' + MAPTILES_URL: _init_value('dev_' + MAPTILES_URL, _DATALORE_TILES_SERVICE),
    'dev_' + MAPTILES_ATTRIBUTION: _init_value('dev_' + MAPTILES_ATTRIBUTION, _DATALORE_TILES_ATTRIBUTION),
    'dev_' + MAPTILES_THEME: _init_value('dev_' + MAPTILES_THEME, _DATALORE_TILES_THEME),
    'dev_' + MAPTILES_MIN_ZOOM: _init_value('dev_' + MAPTILES_MIN_ZOOM, _DATALORE_TILES_MIN_ZOOM),
    'dev_' + MAPTILES_MAX_ZOOM: _init_value('dev_' + MAPTILES_MAX_ZOOM, _DATALORE_TILES_MAX_ZOOM),
}


def _to_actual_name(name: str) -> str:
    if name.startswith("dev_"):
        return name

    return name if is_production() else 'dev_' + name


def _get_global_val_intern(actual_name: str) -> Any:
    # `settings` dict has precedence over environment variables.
    env_val = _get_env_val(actual_name)
    return _settings.get(actual_name, env_val)


def is_production() -> bool:
    return 'dev' not in __version__


def get_js_cdn_url() -> str:
    if has_global_value(JS_URL_MANUAL):
        return get_global_str(JS_URL_MANUAL)

    base_url = get_global_str(JS_BASE_URL)

    js_path_to_file = get_global_str(JS_PATH_TO_FILE)

    if has_global_value(JS_NAME):
        name = get_global_str(JS_NAME)
    else:
        name = "lets-plot.min.js" if is_production() else "lets-plot.js".format(version=__version__)
        # name = "lets-plot-{version}.{suffix}".format(version=__version__, suffix=suffix)
        # name = "lets-plot.{suffix}".format(suffix=suffix)

    # url = "{base_url}/{name}".format(base_url=base_url, name=name)
    url = "{base_url}/{js_path_to_file}/{name}".format(base_url=base_url, js_path_to_file=js_path_to_file, name=name)
    return url


def has_global_value(name: str) -> bool:
    val = _get_global_val_intern(_to_actual_name(name))

    if isinstance(val, bool):
        return True
    if isinstance(val, str) and not val.strip():
        return False
    return bool(val)


def get_global_val(name: str) -> Any:
    if not has_global_value(name):
        raise ValueError("Not defined '{}'".format(_to_actual_name(name)))

    return _get_global_val_intern(_to_actual_name(name))


def get_global_str(name: str) -> str:
    val = get_global_val(name)
    if not isinstance(val, str):
        raise ValueError("Not string value: ['{}'] : {}".format(_to_actual_name(name), type(val)))
    return val


def get_global_bool(name: str) -> bool:
    val = get_global_val(name)
    if isinstance(val, bool):
        return val

    if isinstance(val, str):
        if val.lower() in ['true', '1', 't', 'y', 'yes']:
            return True
        elif val.lower() in ['false', '0', 'f', 'n', 'no']:
            return False
        else:
            raise ValueError("Can't convert str to boolean : ['{}'] : {}".format(_to_actual_name(name), val))

    raise ValueError("Not boolean value: ['{}'] : {}".format(_to_actual_name(name), type(val)))
