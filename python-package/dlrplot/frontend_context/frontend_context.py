from typing import Dict

from ..plot.core import PlotSpec
from ..type_utils.type_utils import is_dict_or_dataframe, standardize_dict


class FrontendContext:
    def configure(self):
        pass

    def as_str(self, plot_spec: Dict) -> str:
        pass


_default_frontend_contexts: Dict[str, FrontendContext] = {}


def standardize_plot_spec(plot_spec: Dict) -> Dict:
    """
    :param plot_spec: dict or pandas.DataFrame
    """
    if not is_dict_or_dataframe(plot_spec):
        raise ValueError("dict or pandas.Dataframe expected but was {}".format(type(plot_spec)))

    return standardize_dict(plot_spec)


def as_html(plot_spec: Dict) -> str:
    """
    :param plot_spec: dict or pandas.DataFrame
    """
    plot_spec = standardize_plot_spec(plot_spec)

    if 'html' not in _default_frontend_contexts:
        connected = False
        from .jupyter_notebook import JupyterNotebookContext
        ctx = JupyterNotebookContext(connected)
        ctx.configure()
        _default_frontend_contexts['html'] = ctx

    return _default_frontend_contexts['html'].as_str(plot_spec)


def display_plot(plot_spec: PlotSpec):
    """
    Draw plot or `bunch` of plots in the current frontend context
    :param plot_spec: PlotSpec or GGBunch object
    """
    try:
        from dlrplot.display import display
        display(plot_spec)
        return
    except ImportError:
        pass

    try:
        from IPython.display import display_html

        plot_spec_dict = standardize_plot_spec(plot_spec.as_dict())
        display_html(as_html(plot_spec_dict), raw=True)
        return
    except ImportError:
        pass

    print(plot_spec.as_dict())
