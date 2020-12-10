#
# Copyright (c) 2020. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from typing import Dict

from ._frontend_ctx import FrontendContext
from .. import _kbridge as kbr
from .._version import __version__


class StaticSvgImageContext(FrontendContext):

    def configure(self, verbose: bool):
        if verbose:
            message = '<div style="color:darkblue;">Lets-Plot v{}: static SVG output configured.</div>'.format(
                __version__)
            try:
                from IPython.display import display_html
                display_html(message, raw=True)
            except ImportError:
                pass
                print(message)

    def as_str(self, plot_spec: Dict) -> str:
        return kbr._generate_svg(plot_spec)
