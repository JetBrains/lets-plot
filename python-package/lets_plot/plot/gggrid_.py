#
# Copyright (c) 2023. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .subplots import SupPlotsLayoutSpec
from .subplots import SupPlotsSpec

__all__ = ['gggrid']


def gggrid(plots: list, ncol: int = None, *,
           widths: list = None,
           heights: list = None,
           hspace: float = None,
           vspace: float = None,
           fit: bool = None,
           align: bool = None
           ):
    """
    Combine several plots on one figure, organized in a regular grid.

    Parameters
    ----------
    plots : list
        A list where each element is a plot specificatio, a subplots specification, or `None`.
        Use value `None` to fill-in empty cells in grid.
    ncol : int
        Number of columns in grid.
        If not specified, shows plots horizontally, in one row.
    widths : list
        A numeric list.
        Relative width of each column of grid, left to right.
    heights : list
        A numeric list.
        Relative height of each row of grid, top-down.
    hspace : float
        Default: 4px
        Cell horizontal spacing.
    vspace : float
        Default: 4px
        Cell vertical spacing.
    fit: bool
        Default: True
        Whether to stretch each plot to match the aspect ratio of its cell (`fit=True`),
        or to preserve the original aspect ratio of plots (`fit=False`).
    align: bool
        Default: False
        If `True`, align inner areas (i.e. "geom" bounds) of plots.
        Howether, cells containing other (sub)grids are not participating in the plot "inner areas" layouting.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10, 13

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 100
        x = np.arange(n)
        y = np.random.normal(size=n)
        w, h = 200, 150
        p = ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + ggsize(w, h)
        plot_list=[
            gggrid([p+geom_point(), p+geom_histogram(bins=3)]),
            p+geom_line()
        ]
        gggrid(plot_list, ncol=1) + ggsize(400, 300)

    """

    if not len(plots):
        raise ValueError("Supplots list is empty.")

    if ncol is None:
        ncol = len(plots)
        nrow = 1
    else:
        extended_list = plots + [None] * (ncol - 1)
        nrow = len(extended_list) // ncol
        length = ncol * nrow
        plots = extended_list[0:length]

    layout = SupPlotsLayoutSpec(
        name="grid",
        ncol=ncol,
        nrow=nrow,
        widths=widths,
        heights=heights,
        hspace=hspace,
        vspace=vspace,
        fit=fit,
        align=align
    )

    return SupPlotsSpec(
        figures=plots,
        layout=layout
    )
