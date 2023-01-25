#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import io
from os.path import abspath
from typing import Union

from ..plot.core import PlotSpec
from ..plot.plot import GGBunch


def export_svg(plot: Union[PlotSpec, GGBunch], filename: str) -> str:
    """
    Export plot or `bunch` to a file in SVG format.
    
    Parameters
    ----------
    plot: `PlotSpec` or `GGBunch` object
            Plot specification to export.
    filename: str
            Filename to save SVG under.

    Returns
    -------
    str
        Absolute pathname of created SVG file.

    """
    if not (isinstance(plot, PlotSpec) or isinstance(plot, GGBunch)):
        raise ValueError("PlotSpec or GGBunch expected but was: {}".format(type(plot)))

    from .. import _kbridge as kbr

    svg = kbr._generate_svg(plot.as_dict())
    with io.open(filename, mode="w", encoding="utf-8") as f:
        f.write(svg)

    return abspath(filename)


def export_html(plot: Union[PlotSpec, GGBunch], filename: str, iframe: bool = False) -> str:
    """
    Export plot or `bunch` to a file in HTML format.

    Parameters
    ----------
    plot: `PlotSpec` or `GGBunch` object
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
    if not (isinstance(plot, PlotSpec) or isinstance(plot, GGBunch)):
        raise ValueError("PlotSpec or GGBunch expected but was: {}".format(type(plot)))

    from .. import _kbridge as kbr

    html_page = kbr._generate_static_html_page(plot.as_dict(), iframe)
    with io.open(filename, mode="w", encoding="utf-8") as f:
        f.write(html_page)

    return abspath(filename)


def export_png(plot: Union[PlotSpec, GGBunch], filename: str) -> str:
    """
    Export plot or `bunch` to a file in PNG format (requires `cairosvg`).

    Parameters
    ----------
    plot: `PlotSpec` or `GGBunch` object
            Plot specification to export.
    filename: str
            Filename to save PNG under.

    Returns
    -------
    str
        Absolute pathname of created PNG file.

    """
    if not (isinstance(plot, PlotSpec) or isinstance(plot, GGBunch)):
        raise ValueError("PlotSpec or GGBunch expected but was: {}".format(type(plot)))

    import cairosvg
    from .. import _kbridge as kbr

    svg = kbr._generate_svg(plot.as_dict())

    cairosvg.svg2png(bytestring=svg, write_to=filename)
    return abspath(filename)
