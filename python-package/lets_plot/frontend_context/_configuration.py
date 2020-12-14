#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from typing import Dict, Any

from ._frontend_ctx import FrontendContext
from ._html_contexts import _create_html_frontend_context, _use_isolated_frame
from ._json_contexts import _create_json_frontend_context, _is_Intellij_Python_Lets_Plot_Plugin
from ._mime_types import TEXT_HTML, LETS_PLOT_JSON
from ._static_svg_ctx import StaticSvgImageContext
from .._version import __version__
from ..plot.core import PlotSpec
from ..plot.plot import GGBunch

__all__ = []

_frontend_contexts: Dict[str, FrontendContext] = {}

_default_mimetype = TEXT_HTML
if _is_Intellij_Python_Lets_Plot_Plugin():
    _default_mimetype = LETS_PLOT_JSON
    _frontend_contexts[LETS_PLOT_JSON] = _create_json_frontend_context()


def _setup_html_context(*,
                        isolated_frame: bool = None,
                        offline: bool,
                        no_js: bool,
                        show_status: bool) -> None:
    """
    Configures Lets-Plot HTML output.

    Parameters
    ----------
    isolated_frame : bool
        True - generate HTLM which can be used in `iframe` or in a standalone HTML document
        False - pre-load Lets-Plot JS library. Notebook cell output will only consist of HTML for the plot rendering.
        Default: None - auto-detect.
    offline : bool
        True - full Lets-Plot JS bundle will be added to the notebook. Use this option if you would like
        to work with notebook without the Internet connection.
        False - load Lets-Plot JS library from CDN.
    no_js : bool
        True - do not generate HTML+JS as an output - just static SVG image.
    show_status : bool
        Whether to show status of loading of the Lets-Plot JS library.
        Only applicable when the Lets-Plot JS library is preloaded.

    """
    global _default_mimetype
    if _default_mimetype == LETS_PLOT_JSON:
        # Plots will be rendered by Lets-Plot IntelliJ plugin.
        # No other contexts are needed.
        if show_status:
            print(
                'Lets-Plot v{}: output mimetype {} configured by default. No need for HTML output.'.format(__version__,
                                                                                                           LETS_PLOT_JSON))
        return

    if no_js:
        ctx = StaticSvgImageContext()
    else:
        ctx = _create_html_frontend_context(isolated_frame, offline=offline)

    ctx.configure(verbose=show_status)
    _frontend_contexts[TEXT_HTML] = ctx


def _display_plot(plot_spec: Any):
    """
    Draw plot or `bunch` of plots in the current frontend context
    :param plot_spec: PlotSpec or GGBunch object
    """
    if not (isinstance(plot_spec, PlotSpec) or isinstance(plot_spec, GGBunch)):
        raise ValueError("PlotSpec or GGBunch expected but was: {}".format(type(plot_spec)))

    if _default_mimetype == TEXT_HTML:
        plot_html = _as_html(plot_spec.as_dict())
        try:
            from IPython.display import display_html
            display_html(plot_html, raw=True)
            return
        except ImportError:
            pass

        # ToDo: show HTML in brawser window.
        return

    if _default_mimetype == LETS_PLOT_JSON:
        _frontend_contexts[LETS_PLOT_JSON].show(plot_spec.as_dict())
        return

    # fallback to plain text.
    print(plot_spec.as_dict())


def _as_html(plot_spec: Dict) -> str:
    """
    Creates plot HTML using 'html' frontend context.

    :param plot_spec: dict
    """
    if TEXT_HTML not in _frontend_contexts:
        if _use_isolated_frame():
            # 'Isolated' HTML context can be setup lazily.
            _setup_html_context(isolated_frame=True,
                                offline=False,
                                no_js=False,
                                show_status=False)
        else:
            return """\
                <div style="color:darkred;">
                    Lets-plot `html` is not configured.<br> 
                    Try to use `LetsPlot.setup_html()` before first occurrence of plot.
                </div>    
                """

    return _frontend_contexts[TEXT_HTML].as_str(plot_spec)
