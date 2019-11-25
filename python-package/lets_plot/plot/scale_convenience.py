#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import DummySpec
from .scale import scale_x_continuous, scale_y_continuous, scale_x_discrete, scale_y_discrete

#
# Scale convenience functions to set the axis limits
#
__all__ = ['lims', 'xlim', 'ylim']


def lims(x, y):
    if x is None:
        x = []
    if y is None:
        y = []
    return xlim(*list(x)) + ylim(*list(y))


def xlim(*args):
    return _limits("x", *args)


def ylim(*args):
    return _limits("y", *args)


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
