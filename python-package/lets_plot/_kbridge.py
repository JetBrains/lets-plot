#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

# noinspection PyUnresolvedReferences
from typing import Dict

import lets_plot_kotlin_bridge

from ._global_settings import get_js_cdn_url
from ._type_utils import standardize_dict


def _generate_dynamic_display_html(plot_spec: Dict) -> str:
    plot_spec = _standardize_plot_spec(plot_spec)
    return lets_plot_kotlin_bridge.generate_html(plot_spec)


def _generate_svg(plot_spec: Dict, use_css_pixelated_image_rendering: bool = True) -> str:
    plot_spec = _standardize_plot_spec(plot_spec)
    return lets_plot_kotlin_bridge.export_svg(plot_spec, use_css_pixelated_image_rendering)

def _save_image(bytestring: Dict, write_to: str, dpi: int = 0, output_width: int = 0, output_height: int = 0, scale: float = 0.0):
    plot_spec = _standardize_plot_spec(bytestring)
    lets_plot_kotlin_bridge.save_image(plot_spec, write_to, dpi, output_width, output_height, scale)


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
    Generate HTML for displaying a plot from 'raw' specification (not processed by plot backend)
    with customizable options.

    Parameters
    ----------
    plot_spec : Dict
        Dict containing the plot specification.
    sizing_options : Dict
        Dict containing sizing policy options (width_mode, height_mode, width, height).
    dynamic_script_loading : bool, default=False
        Controls how the generated JS code interacts with the lets-plot JS library.
        If True, assumes the library will be loaded dynamically.
        If False, assumes the library is already present in the page header (static loading).
    force_immediate_render : bool, default=False
        Controls the timing of plot rendering.
        If True, forces immediate plot rendering.
        If False, waits for ResizeObserver(JS) event and renders the plot after the plot
        container is properly layouted in DOM.
    responsive : bool, default=False
        If True, makes the plot responsive to container size changes.

    Returns
    -------
    str
        HTML string containing the plot with specified options.

    Notes
    -----
    The sizing_options dict supports the following structure:
    {
        'width_mode': str,     # 'fixed', 'min', 'fit', 'scaled' (case-insensitive)
        'height_mode': str,    # 'fixed', 'min', 'fit', 'scaled' (case-insensitive)
        'width': number,       # optional
        'height': number       # optional
    }

    Sizing modes determine how the plot dimensions are calculated:

    1. FIXED mode:
       - Uses the explicitly provided width/height values
       - Falls back to the default figure size if no values provided
       - Not responsive to container size

    2. MIN mode:
       Applies the smallest dimension among:
       - The default figure size
       - The specified width/height (if provided)
       - The container size (if available)

    3. FIT mode:
       Uses either:
       - The specified width/height if provided
       - Otherwise uses container size if available
       - Falls back to default figure size if neither is available

    4. SCALED mode:
       - Always preserves the figure's aspect ratio
       - Typical usage: one dimension (usually width) uses FIXED/MIN/FIT mode
         and SCALED height adjusts to maintain aspect ratio
       - Special case: when both width and height are SCALED:
         * Requires container size to be available
         * Fits figure within container while preserving aspect ratio
         * Neither dimension is predetermined

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
