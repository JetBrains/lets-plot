#
# Copyright (c) 2025. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import os
import pkgutil
import random
import string

from .._global_settings import get_js_cdn_url

# Data-attributes used to store extra information about the meaning of 'script' elements
_ATT_SCRIPT_KIND = 'data-lets-plot-script'
_SCRIPT_KIND_LIB_LOADING = 'library'


def generate_dynamic_configure_html(offline: bool, verbose: bool) -> str:
    """
    Generate HTML for dynamic loading of lets-plot.js library.

    Parameters
    ----------
    offline : bool
        If True, embeds the JS library directly. If False, loads from CDN.
    verbose : bool
        If True, shows success/error messages to the user.

    Returns
    -------
    str
        HTML string that loads the lets-plot.js library and sets up the dynamic loading mechanism.
    """
    if offline:
        return _configure_embedded_script(verbose)
    else:
        return _configure_connected_script(verbose)


def _configure_connected_script(verbose: bool) -> str:
    """Generate HTML that loads lets-plot.js from CDN."""
    url = get_js_cdn_url()
    output_id = _rand_string()
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


def _configure_embedded_script(verbose: bool) -> str:
    """Generate HTML that embeds lets-plot.js directly."""
    js_name = "lets-plot.min.js"
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


def _rand_string(size=6) -> str:
    """Generate a random string for unique element IDs."""
    alphabet = string.ascii_letters + string.digits
    # noinspection PyShadowingBuiltins
    return ''.join([random.choice(alphabet) for _ in range(size)])
