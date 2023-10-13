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
           'coord_map',
           'coord_flip',
           ]


def coord_cartesian(xlim=None, ylim=None, flip=False):
    """
    The Cartesian coordinate system is the most familiar and common type of coordinate system.
    Setting limits on the coordinate system will zoom the plot like you're looking at it with a magnifying glass.
    It does not change the underlying data as setting limits on a scale does.

    Parameters
    ----------
    xlim : list
        Limits (2 elements) for the x axis.
        1st element defines lower limit, 2nd element defines upper limit.
        None means no lower / upper bound - depending on the index in list.
    ylim : list
        Limits (2 elements) for the y axis.
        1st element defines lower limit, 2nd element defines upper limit.
        None means no lower / upper bound - depending on the index in list.
    flip : bool
        Flip the coordinate system axis so that horizontal axis becomes vertical and vice versa.

    Returns
    -------
    `FeatureSpec`
        Coordinate system specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        from lets_plot import *
        LetsPlot.setup_html()
        data = {'x': [0, 8, 12, 17, 20, 26],
                'y': [0, 8, 12, 17, 20, 26],
                'g': ['a', 'a', 'b', 'b', 'c', 'c']}
        ggplot(data) + geom_line(aes(x='x', y='y', group='g')) + \\
            coord_cartesian(xlim=(4, 23), ylim=(3, 22))

    """

    return _coord('cartesian', xlim=xlim, ylim=ylim, flip=flip)


def coord_fixed(ratio=1., xlim=None, ylim=None, flip=False):
    """
    A fixed scale coordinate system forces a specified ratio between the physical representations of data units on the axes.

    Parameters
    ----------
    ratio : float
        The ratio represents the number of units on the y-axis equivalent to one unit on the x-axis.
        ratio = 1, ensures that one unit on the x-axis is the same length as one unit on the y-axis.
        Ratios higher than one make units on the y-axis longer than units on the x-axis, and vice versa.
    xlim : list
        Limits (2 elements) for the x axis.
        1st element defines lower limit, 2nd element defines upper limit.
        None means no lower / upper bound - depending on the index in list.
    ylim : list
        Limits (2 elements) for the y axis.
        1st element defines lower limit, 2nd element defines upper limit.
        None means no lower / upper bound - depending on the index in list.
    flip : bool
        Flip the coordinate system axis so that horizontal axis becomes vertical and vice versa.

    Returns
    -------
    `FeatureSpec`
        Coordinate system specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 30
        np.random.seed(42)
        x = np.random.uniform(-1, 1, size=n)
        y = 25 * x ** 2 + np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            geom_point() + coord_fixed(ratio=.2, ylim=(7, 20))

    """

    return _coord('fixed', ratio=ratio, xlim=xlim, ylim=ylim, flip=flip)


def coord_map(xlim=None, ylim=None, flip=False):
    """
    Project a portion of the earth, which is approximately spherical,
    onto a flat 2D plane.
    Map projections generally do not preserve straight lines, so this requires considerable computation.

    Parameters
    ----------
    xlim : list
        Limits (2 elements) for the x axis.
        1st element defines lower limit, 2nd element defines upper limit.
        None means no lower / upper bound - depending on the index in list.
    ylim : list
        Limits (2 elements) for the y axis.
        1st element defines lower limit, 2nd element defines upper limit.
        None means no lower / upper bound - depending on the index in list.
    flip : bool
        Flip the coordinate system axis so that horizontal axis becomes vertical and vice versa.

    Returns
    -------
    `FeatureSpec`
        Coordinate system specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        us = geocode_states('US-48').get_boundaries(4)
        ggplot() + geom_map(map=us, fill='gray', color='white') + \\
            coord_map(xlim=(-130, -100))

    """

    return _coord('map', xlim=xlim, ylim=ylim, flip=flip)


def coord_flip(xlim=None, ylim=None):
    """
    Flip axis of default coordinate system so that horizontal axis becomes vertical and vice versa.

    Parameters
    ----------
    xlim : list
        Limits (2 elements) for the x axis.
        1st element defines lower limit, 2nd element defines upper limit.
        None means no lower / upper bound - depending on the index in list.
    ylim : list
        Limits (2 elements) for the y axis.
        1st element defines lower limit, 2nd element defines upper limit.
        None means no lower / upper bound - depending on the index in list.

    Returns
    -------
    `FeatureSpec`
        Coordinate system specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 10
        x = np.arange(n)
        y = 1 + np.random.randint(5, size=n)
        ggplot() + \\
            geom_bar(aes(x='x', y='y', fill='x'), data={'x': x, 'y': y}, \\
                     stat='identity', show_legend=False) + \\
            scale_fill_discrete() + \\
            coord_flip()

    """

    return _coord('flip', xlim=xlim, ylim=ylim, flip=True)


def coord_polar(theta=None, start=None, direction=None):
    """
    Polar coordinate system. It is used for pie charts and polar plots.

    Parameters
    ----------
    theta : {'x', 'y'}, default='x'
        Aesthetic that is used to map angle.
    start : float, default=0
        Offset relative to the starting angle (which is 12 o'clock), in radians.
    direction : {1, -1}, default=1
        Specify angle direction. 1 for clockwise, -1 for counterclockwise.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 13

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 20
        data = {
            'v': 1 + np.random.randint(5, size=n)
        }
        ggplot(data) + \\
            geom_bar(aes(fill=as_discrete('v')), size=0, show_legend=False) + \\
            scale_x_continuous(expand=[0, 0]) + \\
            scale_y_continuous(expand=[0, 0]) + \\
            coord_polar(theta='y')

    """
    return _coord('polar', theta=theta, start=start, direction=direction)


def _coord(name, **other):
    return FeatureSpec('coord', name=name, **other)
