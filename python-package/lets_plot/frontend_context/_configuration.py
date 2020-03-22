#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from typing import Dict, Any

from ._frontend_ctx import FrontendContext
from ._html_contexts import _create_html_frontend_context, _use_isolated_frame
from .._global_settings import get_global_bool
from ..plot.core import PlotSpec
from ..plot.plot import GGBunch

__all__ = [
    'load_lets_plot_js'  # deprecated
]

_frontend_contexts: Dict[str, FrontendContext] = {}
_default_mimetype = "text/html"  # Just HTML as yet


def _setup_html_context(isolated_frame: bool = None, offline: bool = None) -> None:
    """

    :param isolated_frame:
    :param embed:
    :return:
    """
    embed = offline if offline is not None else get_global_bool('offline')
    ctx = _create_html_frontend_context(isolated_frame, embed)
    ctx.configure(verbose=True)
    _frontend_contexts['html'] = ctx


def load_lets_plot_js(embed: bool = None):
    """
    Deprecated since v.1.3: instead use LetsPlot.setup_html()

    Loads Lets-Plot javascript library into current frontend context.

    Parameters
    ----------
    embed : bool, optional
        True - embed JS which is bundled with Lets-Plot PyPI package. This is useful for off-line notebooks.
        False - load JS from CDN.
        default - load JS from CDN.
    """
    try:
        from IPython.display import display_html
        display_html("""\
            <div style="color:darkred;">
                Method `load_lets_plot_js()` is deprecated since v.1.3 and will be removed soon.<br> 
                Try to use `LetsPlot.setup_html()` instead.
            </div>    
        """, raw=True)
    except ImportError:
        pass

    _setup_html_context(None, embed)


def _display_plot(plot_spec: Any):
    """
    Draw plot or `bunch` of plots in the current frontend context
    :param plot_spec: PlotSpec or GGBunch object
    """
    if not (isinstance(plot_spec, PlotSpec) or isinstance(plot_spec, GGBunch)):
        raise ValueError("PlotSpec or GGBunch expected but was: {}".format(type(plot_spec)))

    if _default_mimetype == "text/html":
        plot_html = _as_html(plot_spec.as_dict())
        try:
            from IPython.display import display_html
            display_html(plot_html, raw=True)
            return
        except ImportError:
            pass

        # ToDo: show HTML is brawser window
        return

    # fallback plain text
    print(plot_spec.as_dict())


def _as_html(plot_spec: Dict) -> str:
    """
    Creates plot HTML using 'html' frontend context.

    :param plot_spec: dict
    """
    if 'html' not in _frontend_contexts:
        if _use_isolated_frame():
            # 'Isolated' HTML context can be setup lazily.
            _setup_html_context(isolated_frame=True, offline=False)
        else:
            return """\
                <div style="color:darkred;">
                    Lets-plot `html` is not configured.<br> 
                    Try to use `LetsPlot.setup_html()` before first occurrence of plot.
                </div>    
                """

    return _frontend_contexts['html'].as_str(plot_spec)
