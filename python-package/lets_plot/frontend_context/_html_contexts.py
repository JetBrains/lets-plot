#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import os

from ._frontend_ctx import FrontendContext
from ._jupyter_notebook_ctx import JupyterNotebookContext
from ._static_html_page_ctx import StaticHtmlPageContext
from ._webbr_html_page_ctx import WebBrHtmlPageContext
from .._global_settings import has_global_value, get_global_bool, HTML_ISOLATED_FRAME


def _create_html_frontend_context(isolated_frame: bool = None, offline: bool = None) -> FrontendContext:
    """
    Configures Lets-Plot HTML output.

    Parameters
    ----------
    isolated_frame : bool, optional, default None - auto-detect
        If True, generate HTLM which can be used in `iframe` or in a standalone HTML document
        If False, pre-load Lets-Plot JS library. Notebook cell output will only consist of HTML for the plot rendering.

    offline : bool, optional, default None - evaluated to 'connected' mode in production environment.
        If True, full Lets-Plot JS bundle will be added to the notebook. Use this option if you would like
        to work with notebook without the Internet connection.
        If False, load Lets-Plot JS library from CDN.
    """
    if isolated_frame is None:
        isolated_frame = _use_isolated_frame()

    if isolated_frame:
        return StaticHtmlPageContext(offline)
    else:
        return JupyterNotebookContext(offline)


def _create_wb_html_frontend_context(exec: str, new: bool) -> FrontendContext:
    """
    Configures Lets-Plot HTML output for showing in web browser.

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

    return _detect_isolated_frame()


def _detect_isolated_frame() -> bool:
    if not _is_IPython_display():
        return True  # isolated HTML page to show somehow

    # Most online notebook platforms are showing cell output in iframe and require
    # a self-contained HTML which includes both:
    # - the script loading JS library and
    # - the script that uses this JS lib to create plot.

    # Try to detect the platform.
    try:
        import google.colab
        return True  # Colab -> iframe
    except ImportError:
        pass

    if os.path.exists("/kaggle/input"):
        return False  # Kaggle -> no iframe

    if "AZURE_NOTEBOOKS_HOST" in os.environ:
        return True  # Azure Notebook -> iframe

    if "DEEPNOTE_PROJECT_ID" in os.environ:
        return True  # Deepnote Notebook -> iframe

    if "databricks" in str(os.environ):
        # Databricks notebook -> iframe
        # As proposed: https://github.com/JetBrains/lets-plot/issues/602
        return True

    if "NEXTJOURNAL" in str(os.environ):
        return True  # NextJournal notebook -> iframe

    if os.getenv("PLOTLY_RENDERER") == "colab":
        # good enouth - something colab-like
        return True  # Colab -> iframe

    # ToDo: other platforms: vscode, nteract, cocalc

    try:
        shell = get_ipython().__class__.__name__
        if shell == 'ZMQInteractiveShell':
            return False  # Jupyter notebook or qtconsole  -> load JS librarty once per notebook
        elif shell == 'TerminalInteractiveShell':
            return True  # Terminal running IPython  -> an isolated HTML page to show somehow
        else:
            return True  # Other type (?)
    except NameError:
        return True  # some other env (even standard Python interpreter) ->  an isolated HTML page to show somehow


def _is_IPython_display() -> bool:
    try:
        from IPython.display import display_html
        return True
    except ImportError:
        return False
