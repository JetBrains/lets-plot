#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import os

from ._frontend_ctx import FrontendContext
from ._isolated_webview_panel_ctx import IsolatedWebviewPanelContext
from ._jupyter_notebook_ctx import JupyterNotebookContext
from ._static_html_page_ctx import StaticHtmlPageContext
from ._webbr_html_page_ctx import WebBrHtmlPageContext
from .._global_settings import has_global_value, get_global_bool, HTML_ISOLATED_FRAME


def _create_html_frontend_context(
        isolated_frame: bool = None,
        offline: bool = None,
        dev_options: dict = None) -> FrontendContext:
    """
    Configures Lets-Plot HTML output.
    See the docstring in `setup_html()` for details on parameters.
    """

    if dev_options is None:
        dev_options = {}
    else:
        dev_options = dev_options.copy()

    # Extract and remove isolated_webview_panel from dev_options
    isolated_webview_panel = dev_options.pop('isolated_webview_panel', None)

    if isolated_webview_panel is None:
        isolated_webview_panel = _is_positron_console()

    if isolated_webview_panel:
        return IsolatedWebviewPanelContext(offline, **dev_options)

    if isolated_frame is None:
        isolated_frame = _use_isolated_frame()

    if isolated_frame:
        return StaticHtmlPageContext(offline, **dev_options)
    else:
        return JupyterNotebookContext(offline, **dev_options)


def _create_wb_html_frontend_context(exec: str, new: bool) -> FrontendContext:
    """
    Configures Lets-Plot HTML output for showing in a web browser.

    Parameters
    ----------
    exec : str, optional
        The name of the web browser to use.
        If not specified, the default browser will be used.
    new : bool, default=False
        If True, the URL is opened in a new window of the web browser.
        If False, the URL is opened in the already opened web browser window.
    """
    return WebBrHtmlPageContext(exec, new)


def _use_isolated_frame() -> bool:
    # check environment
    if has_global_value(HTML_ISOLATED_FRAME):
        return get_global_bool(HTML_ISOLATED_FRAME)

    # return _detect_isolated_frame()
    if not _is_IPython_display():
        return True  # fallback to a complete HTML page in the output

    # Some notebooks behave like a single HTML page where the JS library is loaded once per notebook.
    if (_is_jupyter_classic() or
            _is_kaggle() or
            _is_positron_notebook()):
        return False  # no iframes in the output cell

    # Most online notebook platforms are showing cell output in iframe and require
    # a complete HTML page in the output which includes both:
    # - the script loading JS library and
    # - the script that uses this JS lib to create a plot.

    if (_is_google_colab() or
            _is_azure_notebook() or
            _is_deepnote() or
            _is_databricks() or
            _is_nextjournal()
    ):
        return True  # complete HTML page in the output

    # if os.getenv("PLOTLY_RENDERER") == "colab":
    #     # good enough - something colab-like
    #     return True  # Colab -> iframe

    # try:
    #     shell = get_ipython().__class__.__name__
    #     if shell == 'ZMQInteractiveShell':
    #         return False  # Jupyter notebook or qtconsole  -> load JS librarty once per notebook
    #     elif shell == 'TerminalInteractiveShell':
    #         return True  # Terminal running IPython  -> an isolated HTML page to show somehow
    #     else:
    #         return True  # Other type (?)
    # except NameError:
    #     return True  # some other env (even standard Python interpreter) ->  an isolated HTML page to show somehow

    # Fallback to a complete HTML page in the output
    return True


# def _detect_isolated_frame() -> bool:
#     if not _is_IPython_display():
#         return True  # fallback to a complete HTML page in the output
#
#     # Some notebooks behave like a single HTML page where the JS library is loaded once per notebook.
#     if (_is_jupyter_classic() or
#             _is_kaggle() or
#             _is_positron_notebook()):
#         return False  # no iframes in the output cell
#
#     # Most online notebook platforms are showing cell output in iframe and require
#     # a complete HTML page in the output which includes both:
#     # - the script loading JS library and
#     # - the script that uses this JS lib to create a plot.
#
#     if (_is_google_colab() or
#             _is_azure_notebook() or
#             _is_deepnote() or
#             _is_databricks() or
#             _is_nextjournal()
#     ):
#         return True  # complete HTML page in the output
#
#     # if os.getenv("PLOTLY_RENDERER") == "colab":
#     #     # good enough - something colab-like
#     #     return True  # Colab -> iframe
#
#     # try:
#     #     shell = get_ipython().__class__.__name__
#     #     if shell == 'ZMQInteractiveShell':
#     #         return False  # Jupyter notebook or qtconsole  -> load JS librarty once per notebook
#     #     elif shell == 'TerminalInteractiveShell':
#     #         return True  # Terminal running IPython  -> an isolated HTML page to show somehow
#     #     else:
#     #         return True  # Other type (?)
#     # except NameError:
#     #     return True  # some other env (even standard Python interpreter) ->  an isolated HTML page to show somehow
#
#     # Fallback to a complete HTML page in the output
#     return True


def _is_IPython_display() -> bool:
    try:
        from IPython.display import display_html
        return True
    except ImportError:
        return False


def _is_jupyter_classic() -> bool:
    # This also detects JupyterLab, which uses the same ZMQInteractiveShell
    # and also qtconsole (allegedly)
    try:
        from IPython import get_ipython
    except ImportError:
        return False
    shell = get_ipython()
    try:
        return shell is not None and shell.__class__.__name__ == "ZMQInteractiveShell"
    except AttributeError:
        return False


def _is_google_colab() -> bool:
    try:
        import google.colab
        return True
    except ImportError:
        return False


def _is_kaggle() -> bool:
    return os.path.exists("/kaggle/input")


def _is_azure_notebook() -> bool:
    return "AZURE_NOTEBOOKS_HOST" in os.environ


def _is_deepnote() -> bool:
    return "DEEPNOTE_PROJECT_ID" in os.environ


def _is_databricks() -> bool:
    # As proposed: https://github.com/JetBrains/lets-plot/issues/602
    return "databricks" in str(os.environ)


def _is_nextjournal() -> bool:
    return "NEXTJOURNAL" in str(os.environ)


def _is_positron_console():
    try:
        from IPython import get_ipython
    except ImportError:
        return False
    shell = get_ipython()
    try:
        return shell is not None and shell.session_mode == "console"
    except AttributeError:
        return False


def _is_positron_notebook():
    try:
        from IPython import get_ipython
    except ImportError:
        return False
    shell = get_ipython()
    try:
        return shell is not None and shell.session_mode == "notebook"
    except AttributeError:
        return False
