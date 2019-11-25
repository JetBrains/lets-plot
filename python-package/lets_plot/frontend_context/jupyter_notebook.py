#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import os
import pkgutil
import random
import string
from typing import Dict

# noinspection PyPackageRequirements
from IPython.display import display_html

from .frontend_context import FrontendContext
from .._global_settings import _get_global_str, _has_global_value, _is_production
from .._version import __version__


class JupyterNotebookContext(FrontendContext):

    def __init__(self, connected: bool) -> None:
        super().__init__()
        self.connected = connected

    def as_str(self, plot_spec: Dict) -> str:
        # noinspection PyUnresolvedReferences
        import lets_plot_kotlin_bridge
        return lets_plot_kotlin_bridge.generate_html(plot_spec)

    def _undef_modules_script(self) -> str:
        pass

    def configure(self, verbose: bool):
        if self.connected:
            # noinspection PyTypeChecker
            display_html(self._configure_connected_script(verbose), raw=True)
        else:
            # noinspection PyTypeChecker
            display_html(self._configure_embedded_script(verbose), raw=True)

    @staticmethod
    def _configure_connected_script(verbose: bool) -> str:
        base_url = _get_global_str("js_base_url")
        if _has_global_value('js_name'):
            name = _get_global_str('js_name')
        else:
            suffix = ".min.js" if _is_production() else ".js"
            name = "lets-plot-{version}{suffix}".format(version=__version__, suffix=suffix)

        url = "{base_url}/{name}".format(base_url=base_url, name=name)
        output_id = JupyterNotebookContext._rand_string()
        success_message = """
            var div = document.createElement("div");
            div.style.color = 'darkblue';
            div.textContent = 'Lets-Plot JS successfully loaded.';
            document.getElementById("{id}").appendChild(div);
        """.format(id=output_id) if verbose else ""

        return """
            <div id="{id}"></div>
            <script type="text/javascript">
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
            """.format(id=output_id, url=url, success_message=success_message)

    @staticmethod
    def _configure_embedded_script(verbose: bool) -> str:
        js_name = "lets-plot-latest.min.js"
        path = os.path.join("package_data", js_name)
        js_code = pkgutil.get_data("lets_plot", path).decode("utf-8")
        success_message = '<div style="color:darkblue;">Lets-Plot JS is embedded.</div>' if verbose else ""

        return """
            <script type="text/javascript">
                window.letsPlotCall = function(f) {{f();}};
                console.log('Embedding: {js_name}');
                
                {js_code}
            </script>
            {success_message}
            """.format(js_code=js_code, js_name=js_name, success_message=success_message)

    @staticmethod
    def _rand_string(size=6) -> str:
        alphabet = string.ascii_letters + string.digits
        # noinspection PyShadowingBuiltins
        return ''.join([random.choice(alphabet) for _ in range(size)])
