#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import os
from os.path import join
from typing import Union

from .simple import export_svg, export_html
from ..plot.core import PlotSpec
from ..plot.plot import GGBunch

__all__ = ['ggsave']

_DEF_EXPORT_DIR = "lets-plot-images"


def ggsave(plot: Union[PlotSpec, GGBunch], filename: str, *, path: str = None, iframe: bool = True) -> str:
    """
    Exports plot or "bunch" to a file.
    Supported formats: SVG, HTML.

    The exported file is created in directory ${user.dir}/lets-plot-images
    if not specified otherwise (see the `path` parameter).

    Parameters
    ----------
    plot: Plot or GGBunch.
            Plot specification to export.
    filename: str
            The name of file. It mast end with a file extention corresponding
            to one of the supported formats: svg, html (or htm)
    path: str
            Path to a directory to save image files in.
            By default it is `${user.dir}/lets-plot-images`
    iframe: bool
            Whether to wrap HTML page into a iFrame. Default value is True.
            Only applicable when exporting to HTML.

    Returns
    -------
    str
        Absolute pathname of created HTML file.

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
    else:
        raise ValueError(
            "Unsupported file extension: '{}'\nPlease use one of: 'svg', 'html', 'htm'".format(ext)
        )
