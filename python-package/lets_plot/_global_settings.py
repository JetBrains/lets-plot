#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from typing import Dict, Any

from ._version import __version__

_settings = {
    'offline': False,  # default: download from CDN
    'js_base_url': 'https://dl.bintray.com/jetbrains/lets-plot',
    'js_name': '',  # default: lets-plot-<version>.min.js

    'dev_offline': True,  # default: embed js into the notebook
    'dev_js_base_url': "http://0.0.0.0:8080",
    'dev_js_name': ''  # default: lets-plot-<version>.js
}


class LetsPlotSettings:
    @classmethod
    def apply(cls, settings: Dict):
        _settings.update(settings)

    # @classmethod
    # def init_frontend(cls, settings=None):
    #     if settings is None:
    #         settings = {}
    #
    #     LetsPlotSettings.apply(settings)
    #     # ToDo: inject js to the notebook
    #     raise NotImplementedError


def _is_production() -> bool:
    return 'dev' not in __version__


def _has_global_value(name: str) -> bool:
    actual_name = name if _is_production() else 'dev_' + name
    val = _settings[actual_name]
    if isinstance(val, bool):
        return True
    if isinstance(val, str) and not val.strip():
        return False
    return val


def _to_actual_name(name: str) -> str:
    if name.startswith("dev_"):
        return name

    return name if _is_production() else 'dev_' + name


def _get_global_val(name: str) -> Any:
    actual_name = _to_actual_name(name)
    if not _has_global_value(name):
        raise ValueError("Not defined '{}'".format(actual_name))

    return _settings[actual_name]


def _get_global_str(name: str) -> str:
    val = _get_global_val(name)
    if not isinstance(val, str):
        raise ValueError("Not string value: ['{}'] : {}".format(_to_actual_name(name), type(val)))
    return val


def _get_global_bool(name: str) -> bool:
    val = _get_global_val(name)
    if not isinstance(val, bool):
        raise ValueError("Not boolean value: ['{}'] : {}".format(_to_actual_name(name), type(val)))
    return val
