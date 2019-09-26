from typing import Dict


class FrontendContext:
    def configure(self):
        pass

    def as_str(self, plot_spec: Dict) -> str:
        pass


_default_frontend_contexts: Dict[str, FrontendContext] = {}


def as_html(plot_spec: Dict) -> str:
    # ToDo: convert all types (pandas, numpy) to built-in python types

    if 'html' not in _default_frontend_contexts:
        connected = False
        from .jupyter_notebook import JupyterNotebookContext
        ctx = JupyterNotebookContext(connected)
        ctx.configure()
        _default_frontend_contexts['html'] = ctx

    return _default_frontend_contexts['html'].as_str(plot_spec)


def display_plot(plot_spec):
    """
    Draw plot or `bunch` of plots in the current frontend context
    :param plot_spec: PlotSpec or GGBunch object
    """
    try:
        from datalore.display import display
        display(plot_spec)
        return
    except ImportError:
        pass

    try:
        from IPython.display import display_html
        display_html(as_html(plot_spec.as_dict()), raw=True)
        return
    except ImportError:
        pass

    print(plot_spec.as_dict())
