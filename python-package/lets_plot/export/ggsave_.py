#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import os
from os.path import join
from typing import Union

from .simple import export_svg, export_html, export_png
from ..plot.core import PlotSpec
from ..plot.plot import GGBunch

__all__ = ['ggsave']

_DEF_EXPORT_DIR = "lets-plot-images"


def ggsave(plot: Union[PlotSpec, GGBunch], filename: str, *, path: str = None, iframe: bool = True,
           scale: float = 2.0) -> str:
    """
    Export plot or `bunch` to a file.
    Supported formats: PNG, SVG, HTML.

    The exported file is created in directory ${user.dir}/lets-plot-images
    if not specified otherwise (see the `path` parameter).

    Parameters
    ----------
    plot : `PlotSpec` or `GGBunch`
        Plot specification to export.
    filename : str
        The name of file. It must end with a file extension corresponding
        to one of the supported formats: SVG, HTML (or HTM), PNG (requires CairoSVG library).
    path : str
        Path to a directory to save image files in.
        By default it is ${user.dir}/lets-plot-images.
    iframe : bool, default=True
        Whether to wrap HTML page into a iFrame.
        Only applicable when exporting to HTML.
    scale : float, default=2.0
        Scaling factor for raster output.
        Only applicable when exporting to PNG.

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

    """

    if not (isinstance(plot, PlotSpec) or isinstance(plot, GGBunch)):
        raise ValueError("PlotSpec or GGBunch expected but was: {}".format(type(plot)))

    filename = filename.strip()
    name, ext = os.path.splitext(filename)

    if not name:
        raise ValueError("Malformed filename: '{}'.".format(filename))
    if not ext:
        raise ValueError("Missing file extension: '{}'.".format(filename))

    if not path:
        path = join(os.getcwd(), _DEF_EXPORT_DIR)

    if not os.path.exists(path):
        os.makedirs(path)

    pathname = join(path, filename)

    ext = ext[1:].lower()
    if ext == 'svg':
        return export_svg(plot, pathname)
    elif ext in ['html', 'htm']:
        return export_html(plot, pathname, iframe=iframe)
    elif ext == 'png':
        return export_png(plot, pathname, scale)
    else:
        raise ValueError(
            "Unsupported file extension: '{}'\nPlease use one of: 'png', 'svg', 'html', 'htm'".format(ext)
        )
