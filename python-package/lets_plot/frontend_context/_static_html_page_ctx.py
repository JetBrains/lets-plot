#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from typing import Dict

from ._frontend_ctx import FrontendContext
from .. import _kbridge as kbr
from .._version import __version__


# noinspection PyPackageRequirements


class StaticHtmlPageContext(FrontendContext):

    def __init__(self, offline: bool) -> None:
        super().__init__()
        self.connected = not offline

    def configure(self, verbose: bool):
        # Nothing here because the entire html page is created per each cell output.
        if not self.connected:
            print("WARN: Embedding Lets-Plot JS library for offline usage is not supported.")

    def as_str(self, plot_spec: Dict) -> str:
        return kbr._generate_static_html_page(plot_spec, iframe=False)
