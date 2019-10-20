import os
import pkgutil
from typing import Dict

from IPython.display import display_html

from .frontend_context import FrontendContext


class JupyterNotebookContext(FrontendContext):

    def __init__(self, connected: bool = False) -> None:
        super().__init__()
        self.connected = connected

    def as_str(self, plot_spec: Dict) -> str:
        import datalore_plot_kotlin_bridge
        return datalore_plot_kotlin_bridge.generate_html(plot_spec)

    def configure(self):
        if self.connected:
            display_html(self._configure_connected_script(), raw=True)
        else:
            display_html(self._configure_embedded_script(), raw=True)

    def _configure_connected_script(self) -> str:
        # ToDo: CDN
        base_url = "http://0.0.0.0:8080"
        url = "{base_url}/datalore-plot-latest.min.js".format(base_url=base_url)
        return """\
                <script type="text/javascript" src="{script_src}"/>
            """.format(script_src=url)

    def _configure_embedded_script(self) -> str:
        js_name = "datalore-plot-latest.min.js"
        path = os.path.join("package_data", js_name)
        js_code = pkgutil.get_data("dlrplot", path).decode("utf-8")
        lib_js = """
                console.log('Embedding: {js_name}');
                
                {js_code}
            """.format(js_code=js_code, js_name=js_name)
        return self._wrap_in_script_element(lib_js)

    def _undef_modules_script(self) -> str:
        pass

    def _wrap_in_script_element(self, script: str) -> str:
        return """\
                    <script type="text/javascript">
                        {script}
                    </script>
                """.format(script=script)
