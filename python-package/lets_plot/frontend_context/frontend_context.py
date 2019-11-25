#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from typing import Dict, Any

from .._global_settings import _get_global_bool
from ..plot.core import PlotSpec
from ..plot.plot import GGBunch
from ..type_utils.type_utils import is_dict_or_dataframe, standardize_dict

__all__ = ['load_lets_plot_js']


class FrontendContext:
    def configure(self, verbose: bool):
        pass

    def as_str(self, plot_spec: Dict) -> str:
        pass


_frontend_contexts: Dict[str, FrontendContext] = {}


def load_lets_plot_js(embed: bool = None):
    """
    Loads Lets-Plot javascript library into current frontend context.

    Parameters
    ----------
    embed : bool, optional
        True - embed JS which is bundled with Lets-Plot PyPI package. This is useful for off-line notebooks.
        False - load JS from CDN.
        default - load JS from CDN.
    """
    if not (isinstance(embed, bool) or embed is None):
        raise ValueError("'embed' argument is not boolean: {}".format(type(embed)))

    offline = embed if embed is not None else _get_global_bool('offline')
    _setup_html_context(offline, verbose=True)


def _setup_html_context(offline: bool, verbose: bool):
    # only Jupyter notebooks as yet
    from .jupyter_notebook import JupyterNotebookContext
    connected = not offline
    ctx = JupyterNotebookContext(connected)
    ctx.configure(verbose)
    _frontend_contexts['html'] = ctx


def _standardize_plot_spec(plot_spec: Dict) -> Dict:
    """
    :param plot_spec: dict or pandas.DataFrame
    """
    if not is_dict_or_dataframe(plot_spec):
        raise ValueError("dict or pandas.Dataframe expected but was {}".format(type(plot_spec)))

    return standardize_dict(plot_spec)


def _as_html(plot_spec: Dict) -> str:
    """
    :param plot_spec: dict or pandas.DataFrame
    """
    plot_spec = _standardize_plot_spec(plot_spec)

    if 'html' not in _frontend_contexts:
        # Lazy context setup
        _setup_html_context(_get_global_bool('offline'), verbose=False)

    return _frontend_contexts['html'].as_str(plot_spec)


def _display_plot(plot_spec: Any):
    """
    Draw plot or `bunch` of plots in the current frontend context
    :param plot_spec: PlotSpec or GGBunch object
    """
    if not (isinstance(plot_spec, PlotSpec) or isinstance(plot_spec, GGBunch)):
        raise ValueError("PlotSpec or GGBunch expected but was: {}".format(type(plot_spec)))

    try:
        from IPython.display import display_html

        plot_spec_dict = _standardize_plot_spec(plot_spec.as_dict())
        display_html(_as_html(plot_spec_dict), raw=True)
        return
    except ImportError:
        pass

    print(plot_spec.as_dict())
