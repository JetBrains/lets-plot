import os
import pkgutil
from typing import Dict

from IPython.display import display_html

from .frontend_context import FrontendContext

_libs = [
    'kotlin',
    'kotlin-logging',
    'datalore-plot-base-portable',
    'datalore-plot-base',
    'mapper-core',
    'visualization-base-svg',
    'visualization-base-svg-mapper',
    'visualization-base-canvas',
    'visualization-plot-common-portable',
    'visualization-plot-common',
    'visualization-plot-base-portable',
    'visualization-plot-base',
    'visualization-plot-builder-portable',
    'visualization-plot-builder',
    'visualization-plot-config-portable',
    'visualization-plot-config',
]


class JupyterNotebookContext(FrontendContext):

    def __init__(self, connected: bool = False) -> None:
        super().__init__()
        self.connected = connected

    def as_str(self, plot_spec: Dict) -> str:
        # from datalore.plot import libdatalore_plot_python_extension
        # return libdatalore_plot_python_extension.generate_html(plot_spec)
        import datalore_plot_kotlin_bridge
        return datalore_plot_kotlin_bridge.generate_html(plot_spec)

    def configure(self):
        display_html(self._undef_modules_script(), raw=True)
        if self.connected:
            display_html(self._configure_connected_script(), raw=True)
        else:
            display_html(self._configure_embedded_script(), raw=True)

    def _configure_connected_script(self) -> str:
        # ToDo: CDN
        base_url = "http://0.0.0.0:8080"
        lib_paths = ",\n".join(["'{v}':'{base_url}/{v}'".format(v=v, base_url=base_url) for v in _libs])
        code = """\
                requirejs.config({{
                    paths: {{
                        {paths}        
                    }}
                }});
        """.format(paths=lib_paths)

        return self._wrap_in_script_element(code)

    def _configure_embedded_script(self) -> str:
        js_code_blocks = []
        path = os.path.join("package_data", "datalore-plot.js")
        lib_js = """
            console.log('Embedding: datalore-plot.js');
            
            {js_code}
        """.format(js_code=pkgutil.get_data("datalore", path).decode("utf-8"))
        return self._wrap_in_script_element(lib_js)
        # for lib in _libs:
        #     path = os.path.join("package_data", lib + ".js")
        #     lib_js = pkgutil.get_data("datalore", path).decode("utf-8")
        #
        #     # !!!
        #     # kotlin JS generates anonymous modules (modules that call define() with no string ID)
        #     # anonymous module can't be loaded inside script tag.
        #     # See https://requirejs.org/docs/errors.html#mismatch
        #     # Some discussion see:
        #     # https://discuss.kotlinlang.org/t/include-packages-from-bower/6300
        #     # https://stackoverflow.com/questions/15371918/mismatched-anonymous-define-module
        #     # Therefore we have to patch each anonymous by passing the module ID to `define()`
        #     lib_js_patched = re.sub(r'define\(\[', "define('{module}', [".format(module=lib), lib_js)
        #
        #     module_code_block = """\
        #             console.log('Embedding: {module}.js');
        #             // *** {module}.js ***
        #             {js_code}
        #         """.format(module=lib, js_code=lib_js_patched)
        #
        #     js_code_blocks.append(module_code_block)
        #
        # return self._wrap_in_script_element("".join(js_code_blocks))

    def _undef_modules_script(self) -> str:
        code = "".join(["requirejs.undef('{v}');\n".format(v=v) for v in _libs])
        return self._wrap_in_script_element(code)

    def _wrap_in_script_element(self, script: str) -> str:
        return """\
                <script type="text/javascript">
                    {script}
                </script>
            """.format(script=script)
