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
    Export plot to a file.
    Supported formats: PNG, SVG, PDF, HTML.

    The exported file is created in directory ${user.dir}/lets-plot-images
    if not specified otherwise (see the ``path`` parameter).

    Parameters
    ----------
    plot : ``PlotSpec``
        Plot specification to export.
    filename : str
        Name of the file. It must end with a file extension corresponding
        to one of the supported formats: SVG, HTML (or HTM), PNG, PDF (requires the pillow library).
    path : str
        Path to a directory to save image files in.
        By default, it is ${user.dir}/lets-plot-images.
    iframe : bool, default=True
        Whether to wrap HTML page into a iFrame.
        Only applicable when exporting to HTML.
        Some browsers may not display some UTF-8 characters correctly when setting iframe=True
    scale : float, default=2.0
        Scaling factor for raster output.
        Only applicable when exporting to PNG or PDF.
    w : float, default=None
        Width of the output image in units.
        Only applicable when exporting to SVG, PNG or PDF.
    h : float, default=None
        Height of the output image in units.
        Only applicable when exporting to SVG, PNG or PDF.
    unit : {'in', 'cm', 'mm', 'px'}, default='in'
        Unit of the output image. One of: 'in', 'cm', 'mm' or 'px'.
        Only applicable when exporting to SVG, PNG or PDF.
    dpi : int, default=300
        Resolution in dots per inch.
        Only applicable when exporting to PNG or PDF.
        The default value depends on the unit:

        - for 'px' it is 96 (output image will have the same pixel size as ``w`` and ``h`` values)
        - for physical units ('in', 'cm', 'mm') it is 300.

    Returns
    -------
    str
        Absolute pathname of the created file.

    Notes
    -----
    Output format is inferred from the filename extension.

    For PNG and PDF formats:

    - If ``w``, ``h``, ``unit``, and ``dpi`` are all specified:

      - The plot's pixel size (default or set by `ggsize() <https://lets-plot.org/python/pages/api/lets_plot.ggsize.html>`__) is ignored.
      - The output size is calculated using the specified ``w``, ``h``, ``unit``, and ``dpi``.

        - The plot is resized to fit the specified ``w`` x ``h`` area, which may affect the layout, tick labels, and other elements.

    - If only ``dpi`` is specified:

      - The plot's pixel size (default or set by `ggsize() <https://lets-plot.org/python/pages/api/lets_plot.ggsize.html>`__) is converted to inches using the standard display PPI of 96.
      - The output size is then calculated based on the specified DPI.

        - The plot maintains its aspect ratio, preserving layout, tick labels, and other visual elements.
        - Useful for printing - the plot will appear nearly the same size as on screen.

    - If ``w``, ``h`` are not specified:

      - The ``scale`` parameter is used to determine the output size.

        - The plot maintains its aspect ratio, preserving layout, tick labels, and other visual elements.
        - Useful for generating high-resolution images suitable for publication.

    For SVG format:

    - If ``w``, ``h`` and ``unit`` are specified:

      - The plot's pixel size (default or set by `ggsize() <https://lets-plot.org/python/pages/api/lets_plot.ggsize.html>`__) is ignored.
      - The output size is calculated using the specified ``w``, ``h``, and ``unit``.


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
        plot = ggplot() + geom_point(x=0, y=0)
        ggsave(plot, 'plot.png', w=4, h=3)

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
        return _to_svg(plot, pathname, w=w, h=h, unit=unit)
    elif ext in ['html', 'htm']:
        return _to_html(plot, pathname, iframe=iframe)
    elif ext in ['png', 'pdf']:
        return _export_as_raster(plot, pathname, scale, export_format=ext, w=w, h=h, unit=unit, dpi=dpi)
    else:
        raise ValueError(
            "Unsupported file extension: '{}'\nPlease use one of: 'png', 'svg', 'pdf', 'html', 'htm'".format(ext)
        )
