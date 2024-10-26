#
# Copyright (c) 2024. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

__all__ = ['ggtb']


def ggtb() -> FeatureSpec:
    """
    Add a toolbar to a chart.

    This function adds a toolbar containing three tool-buttons (pan, rubber-band zoom,
    and center-point zoom) to a chart. Each tool uses mouse-drag for its
    specific functionality. Additionally, the mouse wheel can be used for zooming
    in and out, regardless of the selected tool.

    The toolbar includes:

    - Pan: Drag to move the plot.
    - Rubber-band zoom: Drag to define a rectangular area to zoom into.
    - Center-point zoom: Drag up or down to zoom in or out from a center point.
    - Reset button: Click to reset the plot and tools to their original state.

    Double-clicking anywhere on the plot resets it to its original coordinates,
    regardless of whether a tool is selected or not.

    Limitations:

    - The toolbar does not work with interactive maps.
    - The toolbar is not compatible with GGBunch.
    - The toolbar cannot be used with plots using a polar coordinate system.

    Returns
    -------
    `FeatureSpec`
        Toolbar feature specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        x = np.linspace(-2 * np.pi, 2 * np.pi, 100)
        y = np.sin(x)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            geom_point() + \\
            ggtb()

    """
    return FeatureSpec(kind='ggtoolbar', name=None)
