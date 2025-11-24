#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from typing import Dict

try:
    from IPython.display import display_html
except ImportError:
    display_html = None

from ._frontend_ctx import FrontendContext
from ._dynamic_configure_html import generate_dynamic_configure_html
from .. import _kbridge as kbr


class JupyterNotebookContext(FrontendContext):

    def __init__(self, offline: bool, *,
                 width_mode: str = 'min',
                 height_mode: str = 'scaled',
                 width: float = None,
                 height: float = None,
                 responsive: bool = False,
                 force_immediate_render: bool = False,
                 height100pct: bool = False
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
        html = generate_dynamic_configure_html(offline=not self.connected, verbose=verbose)
        # noinspection PyTypeChecker
        display_html(html, raw=True)

    def as_str(self, plot_spec: Dict) -> str:
        # Old implementation (deprecated):
        # return kbr._generate_dynamic_display_html(plot_spec)

        # Build sizing_options
        # Default to notebookCell sizing (MIN width, SCALED height) if not specified
        # if self.width_mode is not None and self.height_mode is not None:
        #     # Use dev options
        #     sizing_options = {
        #         'width_mode': self.width_mode,
        #         'height_mode': self.height_mode
        #     }
        # else:
        #     # Default to notebookCell sizing
        #     sizing_options = {
        #         'width_mode': 'min',
        #         'height_mode': 'scaled'
        #     }

        sizing_options = {
            'width_mode': self.width_mode,
            'height_mode': self.height_mode
        }

        # Add width and height if specified
        if self.width is not None:
            sizing_options['width'] = self.width
        if self.height is not None:
            sizing_options['height'] = self.height

        return kbr._generate_display_html_for_raw_spec(
            plot_spec,
            sizing_options,
            dynamic_script_loading=True,  # True for Jupyter Notebook, JupyterLab
            force_immediate_render=self.force_immediate_render,
            responsive=self.responsive,
            height100pct=self.height100pct
        )
