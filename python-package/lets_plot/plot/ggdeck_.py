#
# Copyright (c) 2026. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

from ._global_theme import _get_global_theme
from .subplots import SupPlotsLayoutSpec
from .subplots import SupPlotsSpec
from .subplots_util import _strip_theme_if_global

__all__ = ['ggdeck']


def ggdeck(plots: list, *,
           scale_share: str = None,
           ) -> SupPlotsSpec:
    """
    Overlay several plots on one figure, with aligned drawing areas.

    Parameters
    ----------
    plots : list
        A list of plot specifications to overlay.
        The first plot is the bottom layer, subsequent plots are drawn on top.
    scale_share : str, default='x'
        Controls sharing of scale limits between overlaid plots.

        - 'x' - share X-axis limits (the default; useful for secondary Y-axis).
        - 'y' - share Y-axis limits.
        - 'all' - share both X and Y limits.
        - 'none' - do not share limits.

    Returns
    -------
    ``SupPlotsSpec``
        The deck specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10-11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 50
        x = np.arange(n)
        data = {'x': x, 'y1': np.cumsum(np.random.normal(size=n)), 'y2': np.random.uniform(0, 10, size=n)}
        p1 = ggplot(data, aes('x', 'y1')) + geom_line()
        p2 = ggplot(data, aes('x', 'y2')) + geom_point(color='red')
        ggdeck([p1, p2]) + ggsize(400, 300)

    """

    if not len(plots):
        raise ValueError("Plots list is empty.")

    layout = SupPlotsLayoutSpec(
        name="deck",
        scale_share=scale_share,
    )

    figures = [_strip_theme_if_global(fig) for fig in plots]

    figure_spec = SupPlotsSpec(figures=figures, layout=layout)

    global_theme_options = _get_global_theme()
    if global_theme_options is not None:
        figure_spec += global_theme_options

    return figure_spec
