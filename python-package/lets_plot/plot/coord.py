#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

#
# Coordinate Systems
#
__all__ = ['coord_cartesian',
           'coord_fixed',
           'coord_map'
           ]


def coord_cartesian(xlim=None, ylim=None):
    """
    The Cartesian coordinate system is the most familiar and common type of coordinate system.
    Setting limits on the coordinate system will zoom the plot like you're looking at it with a magnifying glass.
    It does not change the underlying data as setting limits on a scale does.

    Parameters
    ----------
    xlim : list of numbers (2 elements)
        Limits for the x axis.
        1st element defines lower limit, 2nd element defines upper limit.
        None means no lower / upper bound - depending on the index in list.
    ylim : list of numbers (2 elements)
        Limits for the y axis.
        1st element defines lower limit, 2nd element defines upper limit.
        None means no lower / upper bound - depending on the index in list.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        ggplot() + geom_map(map=geocode_states(['US-48']).get_boundaries(4), \\
                            fill='black', color='white') + \\
            coord_map(xlim=[-130, -60], ylim=[None, 60])
    """

    return _coord('cartesian', xlim=xlim, ylim=ylim)


def coord_fixed(ratio=1., xlim=None, ylim=None):
    """
    A fixed scale coordinate system forces a specified ratio between the physical representation of data units on the axes.

    Parameters
    ----------
    xlim : list of numbers (2 elements)
        Limits for the x axis.
        1st element defines lower limit, 2nd element defines upper limit.
        None means no lower / upper bound - depending on the index in list.
    ylim : list of numbers (2 elements)
        Limits for the y axis.
        1st element defines lower limit, 2nd element defines upper limit.
        None means no lower / upper bound - depending on the index in list.
    ratio : number
        The ratio represents the number of units on the y-axis equivalent to one unit on the x-axis.
        ratio = 1, ensures that one unit on the x-axis is the same length as one unit on the y-axis.
        Ratios higher than one make units on the y-axis longer than units on the x-axis, and vice versa.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        ggplot() + geom_map(map=geocode_countries(['Italy']).get_boundaries(5)) + \\
            coord_fixed(xlim=[None, 30], ylim=[35, 50], ratio=0.7)
    """

    return _coord('fixed', ratio=ratio, xlim=xlim, ylim=ylim)


def coord_map(xlim=None, ylim=None):
    """
    Projects a portion of the earth, which is approximately spherical,
    onto a flat 2D plane.
    Map projections generally do not preserve straight lines, so this requires considerable computation.

    Parameters
    ----------
    xlim : list of numbers (2 elements)
        Limits for the x axis.
        1st element defines lower limit, 2nd element defines upper limit.
        None means no lower / upper bound - depending on the index in list.
    ylim : list of numbers (2 elements)
        Limits for the y axis.
        1st element defines lower limit, 2nd element defines upper limit.
        None means no lower / upper bound - depending on the index in list.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        ggplot() + geom_map(map=geocode_states(['Texas']).get_boundaries()) + \\
            coord_map(xlim=(-100, None), ylim=(20, 40))
    """

    return _coord('map', xlim=xlim, ylim=ylim)


def _coord(name, **other):
    return FeatureSpec('coord', name=name, **other)
