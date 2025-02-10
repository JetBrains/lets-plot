#  Copyright (c) 2025. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from numbers import Number
from typing import List, Tuple

from ._global_theme import _get_global_theme
from .subplots import SupPlotsLayoutSpec
from .subplots import SupPlotsSpec
from .subplots_util import _strip_theme_if_global

__all__ = ['ggbunch']


def ggbunch(plots: List,
            regions: List[Tuple[float, float, float, float, float, float]]
            ) -> SupPlotsSpec:
    """
    Combine several plots into a single figure with custom layout.

    Parameters
    ----------
    plots : List
        A list where each element is one of:

        - a plot specification
        - a subplots specification
        - None

    regions : List[Tuple]
        Layout parameters for each plot. Each region is specified as
        (x, y, width, height, dx, dy) where:

        - x, y: Position of the plot's top-left corner in relative coordinates ([0,0] is top-left corner, [1,1] is bottom-right corner of the container).
        - width, height: Size of the plot relative to container dimensions (1 equal to the full container width/height).
        - dx, dy: Pixel offsets to move the region (defaults to 0).

    Returns
    -------
    `SupPlotsSpec`
        A specification describing the combined figure with all plots and their layout.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10-14

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {'x': np.random.gamma(2.0, size=100)}
        p1 = ggplot(data, aes(x='x')) + \\
            geom_histogram(aes(color='x', fill='x'))
        p2 = ggplot(data, aes(x='x')) + \\
            geom_density() + theme_bw() + theme(axis='blank', panel_grid='blank')
        ggbunch(
            [p1, p2],
            [(0, 0, 1, 1),
             (0.5, 0.1, 0.3, 0.3)]
        ) + ggsize(400, 300)

    """

    if not len(plots):
        raise ValueError("Supplots list is empty.")

    # Validate provided regions
    for i, region in enumerate(regions):
        if len(region) not in (4, 6):
            raise ValueError(f"Region {i} must have 4 or 6 values, got {len(region)}")
        if not all(isinstance(x, Number) for x in region):
            raise ValueError(f"Region {i} contains non-numeric values: {region}")

        # Validate size is positive
        if any(x <= 0 for x in region[2:4]):
            raise ValueError(f"Region {i} sizes must be positive: {region}")

    # Convert regions tuples to lists
    regions_list = [list(r) for r in regions]
    layout = SupPlotsLayoutSpec(
        name="free",
        regions=regions_list
    )

    figures = [_strip_theme_if_global(fig) for fig in plots]

    figure_spec = SupPlotsSpec(figures=figures, layout=layout)

    # Apply global theme if defined
    global_theme_options = _get_global_theme()
    if global_theme_options is not None:
        figure_spec += global_theme_options

    return figure_spec
