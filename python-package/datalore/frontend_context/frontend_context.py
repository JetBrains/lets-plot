import os
import pkgutil
import re
from typing import Dict

try:
    from IPython.display import display, display_html
except ImportError:
    pass

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


class FrontendContext:
    def configure(self):
        pass

    def as_str(self, plot_spec: Dict) -> str:
        pass


_default_frontend_contexts: Dict[str, FrontendContext] = {}


def as_html(plot_spec: Dict) -> str:
    if 'html' not in _default_frontend_contexts:
        # ctx = JupyterNotebookContext(connected=True)
        ctx = JupyterNotebookContext(connected=False)
        ctx.configure()
        _default_frontend_contexts['html'] = ctx

    return _default_frontend_contexts['html'].as_str(plot_spec)


class JupyterNotebookContext(FrontendContext):

    def __init__(self, connected: bool = False) -> None:
        super().__init__()
        self.connected = connected

    def as_str(self, plot_spec: Dict) -> str:
        import datalore_plot_kotlin_bridge
        return datalore_plot_kotlin_bridge.generate_html()
        # return '<p>***plot***</p>'

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
        for lib in _libs:
            path = os.path.join("package_data", lib + ".js")
            lib_js = pkgutil.get_data("datalore", path).decode("utf-8")

            # !!!
            # kotlin JS generates anonymous modules (modules that call define() with no string ID)
            # anonymous module can't be loaded inside script tag.
            # See https://requirejs.org/docs/errors.html#mismatch
            # Some discussion see:
            # https://discuss.kotlinlang.org/t/include-packages-from-bower/6300
            # https://stackoverflow.com/questions/15371918/mismatched-anonymous-define-module
            # Therefore we have to patch each anonymous by passing the module ID to `define()`
            lib_js_patched = re.sub(r'define\(\[', "define('{module}', [".format(module=lib), lib_js)

            module_code_block = """\
                    console.log('Embedding: {module}.js');
                    // *** {module}.js ***
                    {js_code}
                """.format(module=lib, js_code=lib_js_patched)

            js_code_blocks.append(module_code_block)

        return self._wrap_in_script_element("".join(js_code_blocks))

    def _undef_modules_script(self) -> str:
        code = "".join(["requirejs.undef('{v}');\n".format(v=v) for v in _libs])
        return self._wrap_in_script_element(code)

    def _wrap_in_script_element(self, script: str) -> str:
        return """\
                <script type="text/javascript">
                    {script}
                </script>
            """.format(script=script)
