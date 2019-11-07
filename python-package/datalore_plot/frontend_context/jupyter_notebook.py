#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import os
import pkgutil
import random
import string
from typing import Dict

from IPython.display import display_html

from .frontend_context import FrontendContext
from .._global_settings import _get_global_str, _has_global_value, _is_production
from .._version import __version__


class JupyterNotebookContext(FrontendContext):

    def __init__(self, connected: bool) -> None:
        super().__init__()
        self.connected = connected

    def as_str(self, plot_spec: Dict) -> str:
        import datalore_plot_kotlin_bridge
        return datalore_plot_kotlin_bridge.generate_html(plot_spec)

    def configure(self, verbose: bool):
        if self.connected:
            # noinspection PyTypeChecker
            display_html(self._configure_connected_script(), raw=True)
        else:
            # noinspection PyTypeChecker
            display_html(self._configure_embedded_script(), raw=True)

        if verbose:
            # noinspection PyTypeChecker
            display_html(self._check_js_loaded_script(), raw=True)

    # noinspection PyMethodMayBeStatic
    def _configure_connected_script(self) -> str:
        base_url = _get_global_str("js_base_url")
        if _has_global_value('js_name'):
            name = _get_global_str('js_name')
        else:
            suffix = ".min.js" if _is_production() else ".js"
            name = "datalore-plot-{version}{suffix}".format(version=__version__, suffix=suffix)

        url = "{base_url}/{name}".format(base_url=base_url, name=name)
        return """\
                <script type="text/javascript" src="{url}"/>
            """.format(url=url)

    def _configure_embedded_script(self) -> str:
        js_name = "datalore-plot-latest.min.js"
        path = os.path.join("package_data", js_name)
        js_code = pkgutil.get_data("datalore_plot", path).decode("utf-8")
        lib_js = """
                console.log('Embedding: {js_name}');
                
                {js_code}
            """.format(js_code=js_code, js_name=js_name)
        return self._wrap_in_script_element(lib_js)

    def _undef_modules_script(self) -> str:
        pass

    @staticmethod
    def _wrap_in_script_element(script: str) -> str:
        return """\
                    <script type="text/javascript">
                        {script}
                    </script>
                """.format(script=script)

    def _check_js_loaded_script(self) -> str:
        alphabet = string.ascii_letters + string.digits
        # noinspection PyShadowingBuiltins
        id = ''.join([random.choice(alphabet) for _ in range(6)])
        return """\
            <div id="{id}"></div>
            <script type="text/javascript">
                var e = document.getElementById("{id}");
                if(typeof DatalorePlot === 'undefined') {{
                    e.style.color = 'darkred';
                    e.textContent = 'Datalore Plot JS{embedded} was not loaded!';
                }} else {{
                    e.style.color = 'darkblue';
                    e.textContent = 'Datalore Plot JS{embedded} successfully loaded.';
                }}
            </script>
                """.format(id=id, embedded="" if self.connected else " (embedded)")
