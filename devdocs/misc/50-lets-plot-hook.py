import json
import subprocess
import uuid
from IPython import get_ipython
from IPython.core.magic import register_line_magic
from IPython.display import display
from lets_plot._type_utils import standardize_dict
from lets_plot.plot.core import FeatureSpec

ENABLE_COPY_SPEC_BUTTON_KEY = "_enable_spec_copy_button"

ipython = get_ipython()


def lets_plot_repr_hook(plot):
    plot_html = plot._repr_html_()

    if not ipython.db.get(ENABLE_COPY_SPEC_BUTTON_KEY, True):
        return plot_html  # Return only the plot, no button

    plot_dict = standardize_dict(plot.as_dict())
    plot_json = json.dumps(plot_dict, indent=2)
    plot_id = f"obj-{uuid.uuid4().hex}"  # Unique ID

    js_script = f"""
    <script>
        function copyToClipboard(plotId, button) {{
            navigator.clipboard.writeText(document.getElementById(plotId).textContent).then(() => {{
                button.textContent = 'Copied!';
                button.style.opacity = '0.5';
                setTimeout(() => {{
                    button.textContent = 'Copy Spec';
                    button.style.opacity = '1';
                }}, 1000);
            }});
        }}
    </script>
    """

    plot_and_button_html = f"""
    <div style="border:1px solid #ccc; padding:5px; margin-bottom:5px; width: 100%; 
                display: flex; align-items: flex-start; justify-content: space-between;
                overflow-x: hidden; box-sizing: border-box;">
        <div style="flex-grow: 1; max-width: calc(100% - 130px); overflow: hidden;">
            {plot_html}
        </div>
        <button onclick="copyToClipboard('{plot_id}', this)"
                style="cursor: pointer; width: 100px; padding: 5px 10px; font-size: 14px; 
                       background: #f5f5f5; border: 1px solid #ccc; border-radius: 5px; 
                       user-select: text; flex-shrink: 0; transition: opacity 0.3s ease;
                       white-space: nowrap; text-align: center;">
            Copy Spec
        </button>
    </div>
    <pre id="{plot_id}" style="display:none;">{plot_json}</pre>
    {js_script}
    """

    return plot_and_button_html


@register_line_magic
def toggle_copy_spec_button(line):
    new_state = line.strip().lower() != "off"
    ipython.db[ENABLE_COPY_SPEC_BUTTON_KEY] = new_state
    print(f"Copy button is now {'ENABLED' if new_state else 'DISABLED'}.")


html_formatter = ipython.display_formatter.formatters['text/html']
html_formatter.for_type(FeatureSpec, lets_plot_repr_hook)
