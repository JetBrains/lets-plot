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
    The Cartesian coordinate system is the most familiar, and common, type of coordinate system.
    Setting limits on the coordinate system will zoom the plot (like you're looking at it with a magnifying glass),
    and will not change the underlying data like setting limits on a scale will.

    Parameters
    ----------
    xlim : list of numbers (2 elements)
           Limits for the x axes.
           1st element defines lower limit, 2nd element defines upper limit.
           `None` means no lower / upper bound - depending on the index in list.
    ylim : list of numbers (2 elements)
           Limits for the y axes.
           1st element defines lower limit, 2nd element defines upper limit.
           `None` means no lower / upper bound - depending on the index in list.
    """

    return _coord('cartesian', xlim=xlim, ylim=ylim)


def coord_fixed(ratio=1., xlim=None, ylim=None):
    """
    A fixed scale coordinate system forces a specified ratio between the physical representation of data units on the axes.

    Parameters
    ----------
    xlim : list of numbers (2 elements)
           Limits for the x axes.
           1st element defines lower limit, 2nd element defines upper limit.
           `None` means no lower / upper bound - depending on the index in list.
    ylim : list of numbers (2 elements)
           Limits for the y axes.
           1st element defines lower limit, 2nd element defines upper limit.
           `None` means no lower / upper bound - depending on the index in list.
    ratio : number
            The ratio represents the number of units on the y-axis equivalent to one unit on the x-axis.
            ratio = 1, ensures that one unit on the x-axis is the same length as one unit on the y-axis.
            Ratios higher than one make units on the y axis longer than units on the x-axis, and vice versa.
    """

    return _coord('fixed', ratio=ratio, xlim=xlim, ylim=ylim)


def coord_map(xlim=None, ylim=None):
    """
    Projects a portion of the earth, which is approximately spherical,
    onto a flat 2D plane.
    Map projections do not, in general, preserve straight lines, so this requires considerable computation.

    Parameters
    ----------
    xlim : list of numbers (2 elements)
           Limits for the x axes.
           1st element defines lower limit, 2nd element defines upper limit.
           `None` means no lower / upper bound - depending on the index in list.
    ylim : list of numbers (2 elements)
           Limits for the y axes.
           1st element defines lower limit, 2nd element defines upper limit.
           `None` means no lower / upper bound - depending on the index in list.
    """

    return _coord('map', xlim=xlim, ylim=ylim)


def _coord(name, **other):
    return FeatureSpec('coord', name=name, **other)
