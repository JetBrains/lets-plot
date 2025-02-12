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


def _generate_static_configure_html() -> str:
    """
    Generate static HTML configuration.

    Returns
    -------
    str
        HTML string containing the static configuration with the script URL from global settings.
    """
    scriptUrl = get_js_cdn_url()
    return lets_plot_kotlin_bridge.get_static_configure_html(scriptUrl)


def _generate_display_html_for_raw_spec(
    plot_spec: Dict,
    sizing_options: Dict,
    *,
    dynamic_script_loading: bool = False,
    force_immediate_render: bool = False,
    responsive: bool = False
) -> str:
    """
    Generate HTML for displaying a plot from raw specification with customizable options.

    Parameters
    ----------
    plot_spec : Dict
        Dict containing the plot specification.
    sizing_options : Dict
        Dict containing sizing policy options (width_mode, height_mode, width, height).
    dynamic_script_loading : bool, default=False
        If True, loads JS library dynamically; if False, expects static loading.
    force_immediate_render : bool, default=False
        If True, forces immediate plot rendering.
    responsive : bool, default=False
        If True, makes the plot responsive to container size changes.

    Returns
    -------
    str
        HTML string containing the plot with specified options.

    Notes
    -----
    The sizing_options dict supports the following keys:
    - width_mode : str
        One of: 'fit', 'min', 'scaled', 'fixed'
    - height_mode : str
        One of: 'fit', 'min', 'scaled', 'fixed'
    - width : number, optional
        The width value (used with 'fixed' mode).
    - height : number, optional
        The height value (used with 'fixed' mode).

    The modes determine how the plot dimensions are computed:
    - 'fit': uses the container dimension
    - 'min': uses the smaller of plot's own dimension or container dimension
    - 'scaled': computes dimension to preserve plot's aspect ratio
    - 'fixed': uses plot's own dimension (non-responsive)
    """
    plot_spec = _standardize_plot_spec(plot_spec)
    sizing_options = standardize_dict(sizing_options)
    return lets_plot_kotlin_bridge.get_display_html_for_raw_spec(
        plot_spec,
        sizing_options,
        dynamic_script_loading,
        force_immediate_render,
        responsive
    )