#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import os
import pkgutil
import random
import string
from typing import Dict

try:
    from IPython.display import display_html
except ImportError:
    display_html = None

from ._frontend_ctx import FrontendContext
from .. import _kbridge as kbr
from .._global_settings import get_js_cdn_url
from .._global_settings import JS_BASE_URL, JS_NAME
from .._version import __version__

# Data-attributes used to store extra information about the meaning of 'script' elements
_ATT_SCRIPT_KIND = 'data-lets-plot-script'
_SCRIPT_KIND_LIB_LOADING = 'library'
_SCRIPT_KIND_PLOT = 'plot'


class JupyterNotebookContext(FrontendContext):

    def __init__(self, offline: bool) -> None:
        super().__init__()
        self.connected = not offline

    def configure(self, verbose: bool):
        if self.connected:
            # noinspection PyTypeChecker
            display_html(self._configure_connected_script(verbose), raw=True)
        else:
            # noinspection PyTypeChecker
            display_html(self._configure_embedded_script(verbose), raw=True)

    def as_str(self, plot_spec: Dict) -> str:
        return kbr._generate_dynamic_display_html(plot_spec)

    @staticmethod
    def _configure_connected_script(verbose: bool) -> str:
        url = get_js_cdn_url()
        output_id = JupyterNotebookContext._rand_string()
        success_message = """
            var div = document.createElement("div");
            div.style.color = 'darkblue';
            div.textContent = 'Lets-Plot JS successfully loaded.';
            document.getElementById("{id}").appendChild(div);
        """.format(id=output_id) if verbose else ""

        return """
            <div id="{id}"></div>
            <script type="text/javascript" {data_attr}="{script_kind}">
                if(!window.letsPlotCallQueue) {{
                    window.letsPlotCallQueue = [];
                }}; 
                window.letsPlotCall = function(f) {{
                    window.letsPlotCallQueue.push(f);
                }};
                (function() {{
                    var script = document.createElement("script");
                    script.type = "text/javascript";
                    script.src = "{url}";
                    script.onload = function() {{
                        window.letsPlotCall = function(f) {{f();}};
                        window.letsPlotCallQueue.forEach(function(f) {{f();}});
                        window.letsPlotCallQueue = [];
                        {success_message}
                    }};
                    script.onerror = function(event) {{
                        window.letsPlotCall = function(f) {{}};    // noop
                        window.letsPlotCallQueue = [];
                        var div = document.createElement("div");
                        div.style.color = 'darkred';
                        div.textContent = 'Error loading Lets-Plot JS';
                        document.getElementById("{id}").appendChild(div);
                    }};
                    var e = document.getElementById("{id}");
                    e.appendChild(script);
                }})()
            </script>
            """.format(
            data_attr=_ATT_SCRIPT_KIND,
            script_kind=_SCRIPT_KIND_LIB_LOADING,
            id=output_id,
            url=url,
            success_message=success_message)

    @staticmethod
    def _configure_embedded_script(verbose: bool) -> str:
        js_name = "lets-plot-latest.js"
        path = os.path.join("package_data", js_name)
        js_code = pkgutil.get_data("lets_plot", path).decode("utf-8")
        success_message = '<div style="color:darkblue;">Lets-Plot JS is embedded.</div>' if verbose else ""

        return """
            <script type="text/javascript" {data_attr}="{script_kind}">
                window.letsPlotCall = function(f) {{f();}};
                console.log('Embedding: {js_name}');
                {js_code}
            </script>
            {success_message}
            """.format(
            data_attr=_ATT_SCRIPT_KIND,
            script_kind=_SCRIPT_KIND_LIB_LOADING,
            js_code=js_code,
            js_name=js_name,
            success_message=success_message)

    @staticmethod
    def _rand_string(size=6) -> str:
        alphabet = string.ascii_letters + string.digits
        # noinspection PyShadowingBuiltins
        return ''.join([random.choice(alphabet) for _ in range(size)])
