#
# Copyright (c) 2025. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from typing import Dict

from ._dynamic_configure_html import generate_dynamic_configure_html
from ._frontend_ctx import FrontendContext
from .. import _kbridge as kbr


# noinspection PyPackageRequirements

class IsolatedWebviewPanelContext(FrontendContext):

    def __init__(self, offline: bool, *,
                 width_mode: str = 'fit',
                 height_mode: str = 'fit',
                 width: float = None,
                 height: float = None,
                 responsive: bool = True,
                 force_immediate_render: bool = False,
                 height100pct: bool = True  # Set height to 100% the panel height
                 ) -> None:
        super().__init__()
        self.connected = not offline
        self.width_mode = width_mode
        self.height_mode = height_mode
        self.width = width
        self.height = height
        self.responsive = responsive
        self.force_immediate_render = force_immediate_render
        self.height100pct = height100pct

    def configure(self, verbose: bool):
        # Nothing here because everything is inside the WebView panel.
        pass

    def as_str(self, plot_spec: Dict) -> str:
        # Build sizing_options
        # Default to notebookCell sizing (MIN width, SCALED height) if not specified
        if self.width_mode is not None and self.height_mode is not None:
            # Use dev options
            sizing_options = {
                'width_mode': self.width_mode,
                'height_mode': self.height_mode
            }
        else:
            # Default to notebookCell sizing
            sizing_options = {
                'width_mode': 'min',
                'height_mode': 'scaled'
            }

        # Add width and height if specified
        if self.width is not None:
            sizing_options['width'] = self.width
        if self.height is not None:
            sizing_options['height'] = self.height

        # Generate dynamic configure HTML (verbose=False)
        dynamic_configure_html = generate_dynamic_configure_html(
            offline=not self.connected,
            verbose=False
        )

        # Generate dynamic display HTML
        dynamic_display_html = kbr._generate_display_html_for_raw_spec(
            plot_spec,
            sizing_options,
            dynamic_script_loading=True,  # True for WebView panel
            force_immediate_render=self.force_immediate_render,
            responsive=self.responsive,
            height100pct=self.height100pct
        )

        # Wrap both in a fixed position container
        return f"""<div style="position: fixed; width: 100%; height: 100%;">
{dynamic_configure_html}
{dynamic_display_html}
</div>"""
