#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import os
from os.path import join
from typing import Union, Optional

from ..plot.core import PlotSpec
from ..plot.core import _to_svg, _to_html, _export_as_raster
from ..plot.plot import GGBunch
from ..plot.subplots import SupPlotsSpec

__all__ = ['ggsave']

_DEF_EXPORT_DIR = "lets-plot-images"


def ggsave(plot: Union[PlotSpec, SupPlotsSpec, GGBunch], filename: str, *, path: str = None, iframe: bool = True,
           scale: float = None, w: Optional[float] = None, h: Optional[float] = None, unit: Optional[str] = None,
           dpi: Optional[int] = None) -> str:
    """
    Export plot or `bunch` to a file.
    Supported formats: PNG, SVG, PDF, HTML.

    The exported file is created in directory ${user.dir}/lets-plot-images
    if not specified otherwise (see the `path` parameter).

    Parameters
    ----------
    plot : `PlotSpec` or `GGBunch`
        Plot specification to export.
    filename : str
        The name of file. It must end with a file extension corresponding
        to one of the supported formats: SVG, HTML (or HTM), PNG (requires CairoSVG library), PDF.
    path : str
        Path to a directory to save image files in.
        By default it is ${user.dir}/lets-plot-images.
    iframe : bool, default=True
        Whether to wrap HTML page into a iFrame.
        Only applicable when exporting to HTML.
        Some browsers may not display some UTF-8 characters correctly when setting iframe=True
    scale : float, default=2.0
        Scaling factor for raster output.
        Only applicable when exporting to PNG or PDF.
    w : float, default=None
        Width of the output image in units.
        Only applicable when exporting to PNG or PDF.
    h : float, default=None
        Height of the output image in units.
        Only applicable when exporting to PNG or PDF.
    unit : {'in', 'cm', 'mm'}, default=None
        Unit of the output image. One of: 'in', 'cm', 'mm'.
        Only applicable when exporting to PNG or PDF.
    dpi : int, default=None
        Resolution in dots per inch.
        Only applicable when exporting to PNG or PDF.

    Returns
    -------
    str
        Absolute pathname of created file.

    Examples
    --------
    .. code-block::
        :linenos:
        :emphasize-lines: 4

        from lets_plot import *
        LetsPlot.setup_html()
        plot = ggplot() + geom_point(x=0, y=0)
        ggsave(plot, 'plot.html', path='.', iframe=False)

    |

    .. code-block::
        :linenos:
        :emphasize-lines: 4

        from lets_plot import *
        LetsPlot.setup_html()
        plot = ggplot() + geom_point(x=0, y=0) + ggsize(800, 400)
        ggsave(plot, 'plot.png', w=8, h=4, unit='in', dpi=300)

    """

    if not (isinstance(plot, PlotSpec) or isinstance(plot, SupPlotsSpec) or isinstance(plot, GGBunch)):
        raise ValueError("PlotSpec, SupPlotsSpec or GGBunch expected but was: {}".format(type(plot)))

    filename = filename.strip()
    name, ext = os.path.splitext(filename)

    if not name:
        raise ValueError("Malformed filename: '{}'.".format(filename))
    if not ext:
        raise ValueError("Missing file extension: '{}'.".format(filename))

    if not path:
        path = join(os.getcwd(), _DEF_EXPORT_DIR)

    pathname = join(path, filename)

    ext = ext[1:].lower()
    if ext == 'svg':
        return _to_svg(plot, pathname)
    elif ext in ['html', 'htm']:
        return _to_html(plot, pathname, iframe=iframe)
    elif ext in ['png', 'pdf']:
        return _export_as_raster(plot, pathname, scale, export_format=ext, w=w, h=h, unit=unit, dpi=dpi)
    else:
        raise ValueError(
            "Unsupported file extension: '{}'\nPlease use one of: 'png', 'svg', 'pdf', 'html', 'htm'".format(ext)
        )
