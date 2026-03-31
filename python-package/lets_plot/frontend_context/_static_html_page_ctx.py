#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from typing import Dict

from ._frontend_ctx import FrontendContext
from .. import _kbridge as kbr


# noinspection PyPackageRequirements


class StaticHtmlPageContext(FrontendContext):

    def __init__(self, offline: bool, *,
                 width_mode: str = 'min',
                 height_mode: str = 'scaled',
                 width: float = None,
                 height: float = None,
                 responsive: bool = False,
                 force_immediate_render: bool = False,
                 height100pct: bool = False,
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
        # Nothing here because the complete HTML page is created per each cell output.
        if not self.connected:
            print("WARN: Embedding Lets-Plot JS library for offline usage is not supported.")

    def as_str(self, plot_spec: Dict) -> str:
        # Old implementation (uses static HTML page generator):
        # return kbr._generate_static_html_page(plot_spec, iframe=False)

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

        return kbr._generate_static_html_page_for_raw_spec(
            plot_spec,
            sizing_options,
            dynamic_script_loading=False,  # False for static HTML pages
            force_immediate_render=self.force_immediate_render,
            responsive=self.responsive,
            height100pct=self.height100pct
        )
