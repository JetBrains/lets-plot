#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import io
from os.path import abspath
from typing import Union

from ..plot.core import PlotSpec
from ..plot.plot import GGBunch
from ..plot.subplots import SupPlotsSpec


def export_svg(plot: Union[PlotSpec, SupPlotsSpec, GGBunch], filename: str) -> str:
    """
    Export plot or `bunch` to a file in SVG format.
    
    Parameters
    ----------
    plot: `PlotSpec`, `SupPlotsSpec` or `GGBunch` object
            Plot specification to export.
    filename: str
            Filename to save SVG under.

    Returns
    -------
    str
        Absolute pathname of created SVG file.

    """
    if not (isinstance(plot, PlotSpec) or isinstance(plot, SupPlotsSpec) or isinstance(plot, GGBunch)):
        raise ValueError("PlotSpec, SupPlotsSpec or GGBunch expected but was: {}".format(type(plot)))

    from .. import _kbridge as kbr

    svg = kbr._generate_svg(plot.as_dict())
    with io.open(filename, mode="w", encoding="utf-8") as f:
        f.write(svg)

    return abspath(filename)


def export_html(plot: Union[PlotSpec, SupPlotsSpec, GGBunch], filename: str, iframe: bool = False) -> str:
    """
    Export plot or `bunch` to a file in HTML format.

    Parameters
    ----------
    plot: `PlotSpec`, `SupPlotsSpec` or `GGBunch` object
            Plot specification to export.
    filename: str
            Filename to save HTML page under.
    iframe: bool
            Whether to wrap HTML page into a iFrame. Default value is False.

    Returns
    -------
    str
        Absolute pathname of created HTML file.

    """
    if not (isinstance(plot, PlotSpec) or isinstance(plot, SupPlotsSpec) or isinstance(plot, GGBunch)):
        raise ValueError("PlotSpec, SupPlotsSpec or GGBunch expected but was: {}".format(type(plot)))

    from .. import _kbridge as kbr

    html_page = kbr._generate_static_html_page(plot.as_dict(), iframe)
    with io.open(filename, mode="w", encoding="utf-8") as f:
        f.write(html_page)

    return abspath(filename)


def export_png(plot: Union[PlotSpec, SupPlotsSpec, GGBunch], filename: str, scale: float = 2.0) -> str:
    """
    Export plot or `bunch` to a file in PNG format.

    Parameters
    ----------
    plot: `PlotSpec`, `SupPlotsSpec` or `GGBunch` object
            Plot specification to export.
    filename: str
            Filename to save PNG under.
    scale : float, default=2.0
        Scaling factor for raster output.

    Returns
    -------
    str
        Absolute pathname of created PNG file.

    Notes
    -----

    Export to PNG file uses the CairoSVG library.
    CairoSVG is free and distributed under the LGPL-3.0 license.
    For more details visit: https://cairosvg.org/documentation/

    """
    if not (isinstance(plot, PlotSpec) or isinstance(plot, SupPlotsSpec) or isinstance(plot, GGBunch)):
        raise ValueError("PlotSpec, SupPlotsSpec or GGBunch expected but was: {}".format(type(plot)))

    try:
        import cairosvg


    except ImportError:
        import sys
        print("\n"
              "To export Lets-Plot figure to a PNG file please install CairoSVG library to your Python environment.\n"
              "CairoSVG is free and distributed under the LGPL-3.0 license.\n"
              "For more details visit: https://cairosvg.org/documentation/\n", file=sys.stderr)
        return None

    from .. import _kbridge
    # Use SVG image-rendering style as Cairo doesn't support CSS image-rendering style,
    svg = _kbridge._generate_svg(plot.as_dict(), use_css_pixelated_image_rendering=False)

    cairosvg.svg2png(bytestring=svg, write_to=filename, scale=scale)

    return abspath(filename)


def export_pdf(plot: Union[PlotSpec, SupPlotsSpec, GGBunch], filename: str, scale: float = 2.0) -> str:
    """
    Export plot or `bunch` to a file in PDF format.

    Parameters
    ----------
    plot: `PlotSpec`, `SupPlotsSpec` or `GGBunch` object
            Plot specification to export.
    filename: str
            Filename to save PDF under.
    scale : float, default=2.0
        Scaling factor for raster output.

    Returns
    -------
    str
        Absolute pathname of created PDF file.

    Notes
    -----

    Export to PDF file uses the CairoSVG library.
    CairoSVG is free and distributed under the LGPL-3.0 license.
    For more details visit: https://cairosvg.org/documentation/

    """
    if not (isinstance(plot, PlotSpec) or isinstance(plot, SupPlotsSpec) or isinstance(plot, GGBunch)):
        raise ValueError("PlotSpec, SupPlotsSpec or GGBunch expected but was: {}".format(type(plot)))

    try:
        import cairosvg


    except ImportError:
        import sys
        print("\n"
              "To export Lets-Plot figure to a PDF file please install CairoSVG library to your Python environment.\n"
              "CairoSVG is free and distributed under the LGPL-3.0 license.\n"
              "For more details visit: https://cairosvg.org/documentation/\n", file=sys.stderr)
        return None

    from .. import _kbridge
    # Use SVG image-rendering style as Cairo doesn't support CSS image-rendering style,
    svg = _kbridge._generate_svg(plot.as_dict(), use_css_pixelated_image_rendering=False)

    cairosvg.svg2pdf(bytestring=svg, write_to=filename, scale=scale)

    return abspath(filename)
