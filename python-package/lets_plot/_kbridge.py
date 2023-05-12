#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

# noinspection PyUnresolvedReferences
from typing import Dict

import lets_plot_kotlin_bridge

from ._type_utils import standardize_dict
from ._global_settings import get_js_cdn_url


def _generate_dynamic_display_html(plot_spec: Dict) -> str:
    plot_spec = _standardize_plot_spec(plot_spec)
    return lets_plot_kotlin_bridge.generate_html(plot_spec)


def _generate_svg(plot_spec: Dict, use_css_pixelated_image_rendering: bool = True) -> str:
    plot_spec = _standardize_plot_spec(plot_spec)
    return lets_plot_kotlin_bridge.export_svg(plot_spec, use_css_pixelated_image_rendering)


def _generate_static_html_page(plot_spec: Dict, iframe: bool) -> str:
    plot_spec = _standardize_plot_spec(plot_spec)
    scriptUrl = get_js_cdn_url()
    return lets_plot_kotlin_bridge.export_html(plot_spec, scriptUrl, iframe)


def _standardize_plot_spec(plot_spec: Dict) -> Dict:
    """
    :param plot_spec: dict
    """
    if not isinstance(plot_spec, dict):
        raise ValueError("dict expected but was {}".format(type(plot_spec)))

    return standardize_dict(plot_spec)
