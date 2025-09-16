#
# Copyright (c) 2024. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

__all__ = ['ggtb']


def ggtb(zoom_basis=None, zoom_limits=None) -> FeatureSpec:
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
    - The toolbar cannot be used with plots using a polar coordinate system.

    Parameters
    ----------
    zoom_basis: String, default="max" {'x', 'y', 'min', 'max'}
        Control how zooming of the map widget affects the scale of geometry objects on plot.
        'x' - size changes only when zooming in/out along x-axis;
        'y' - size changes only when zooming in/out along y-axis;
        'min' - size changes when zooming in/out along any axis, but the change is determined by the axis
               with the minimum zoom factor;
        'max' - size changes when zooming in/out along any axis, but the change is determined by the axis
               with the maximum zoom factor.
    zoom_limits : int, default=0
        Control how zooming-in of the map widget increases size of geometry objects on plot
        when the size is set by means of mapping between the data and the ``size`` aesthetic.
        0 - size never increases;
        -1 - size will be increasing without limits;
        n - the number of times the size of objects will increase (relative to the initial state of the plot).
        Farther zooming will no longer affect the size.

    Returns
    -------
    ``FeatureSpec``
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
    return FeatureSpec(kind='ggtoolbar', name=None, zoom_basis=zoom_basis, zoom_limits=zoom_limits)
