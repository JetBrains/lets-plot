#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import DummySpec
from .scale_position import scale_x_continuous, scale_y_continuous, scale_x_discrete, scale_y_discrete

#
# Scale convenience functions to set the axis limits
#
__all__ = ['lims', 'xlim', 'ylim']


def lims(x, y):
    """
    This is a shortcut for supplying the `limits` parameter to the x and y axes.
    Observations outside the range will be dropped.

    Parameters
    ----------
    x : list of float
        Limits (2 elements) for the x axis.
        1st element defines lower limit, 2nd element defines upper limit.
        None value in list means no bounds.
    y : list of float
        Limits (2 elements) for the y axis.
        1st element defines lower limit, 2nd element defines upper limit.
        None value in list means no bounds.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + \\
            geom_point() + lims([-2, 2], [-2, 2])

    """
    if x is None:
        x = []
    if y is None:
        y = []
    return xlim(*list(x)) + ylim(*list(y))


def xlim(*limits):
    """
    This is a shortcut for supplying the `limits` parameter to the x axis.
    Observations outside the range will be dropped.

    Parameters
    ----------
    limits
        Limits (2 parameters) for the x axis.
        1st parameter defines lower limit, 2nd parameter defines upper limit.
        None value means no bounds.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + \\
            geom_point() + xlim(-1, 1)

    """
    return _limits("x", *limits)


def ylim(*limits):
    """
    This is a shortcut for supplying the `limits` parameter to the y axis.
    Observations outside the range will be dropped.
    
    Parameters
    ----------
    limits
        Limits (2 parameters) for the y axis.
        1st parameter defines lower limit, 2nd parameter defines upper limit.
        None value means no bounds.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + \\
            geom_point() + ylim(-1, 1)

    """
    return _limits("y", *limits)


def _limits(aesthetic, *args):
    if len(args) == 0:
        return DummySpec()
    elif any(isinstance(v, (str, bytes)) for v in args):
        return _discrete_scale_limits(aesthetic=aesthetic, limits=list(args))
    else:
        return _continuous_scale_limits(aesthetic=aesthetic, limits=list(args))


def _discrete_scale_limits(aesthetic, limits):
    if aesthetic == "x":
        return scale_x_discrete(limits=limits)
    elif aesthetic == "y":
        return scale_y_discrete(limits=limits)
    raise ValueError("Unexpected aesthetic value '{}'".format(aesthetic))


def _continuous_scale_limits(aesthetic, limits):
    if aesthetic == "x":
        return scale_x_continuous(limits=limits)
    elif aesthetic == "y":
        return scale_y_continuous(limits=limits)
    raise ValueError("Unexpected aesthetic value '{}'".format(aesthetic))
