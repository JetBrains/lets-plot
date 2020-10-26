#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from ._frontend_ctx import FrontendContext
from ._intellij_python_json_ctx import IntellijPythonJsonContext


def _create_json_frontend_context() -> FrontendContext:
    """
    Configures Lets-Plot JSON output.

    Such context requires a Lets-Plot JSON interpreter plugged in to the frontend env (like PyCharm)
    """

    if _is_Intellij_Python_Lets_Plot_Plugin():
        return IntellijPythonJsonContext()

    # ToDo: GenericJsonFrontendContext
    raise RuntimeError("Couldn't detect Intellij Python environment")


def _is_Intellij_Python_Lets_Plot_Plugin() -> bool:
    try:
        # An empty marker module defined by Intellij Lets-Plot plugin
        import lets_plot_intellij_python_plugin
        # # See intellij.python.helpers module in IDEA
        # from datalore.display import display
        return True
    except ImportError:
        return False
