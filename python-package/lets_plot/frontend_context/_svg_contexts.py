#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from ._frontend_ctx import FrontendContext
from ._static_svg_ctx import StaticSvgImageContext


def _create_image_svg_frontend_context() -> FrontendContext:
    """
    Configures Lets-Plot SVG output.
    I.e. the "No JS" mode.
    """
    return StaticSvgImageContext()
