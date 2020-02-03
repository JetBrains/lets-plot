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
    return _coord('cartesian', xlim=xlim, ylim=ylim)


def coord_fixed(ratio=1., xlim=None, ylim=None):
    return _coord('fixed', ratio=ratio, xlim=xlim, ylim=ylim)


def coord_map(xlim=None, ylim=None):
    return _coord('map', xlim=xlim, ylim=ylim)


def _coord(name, **other):
    return FeatureSpec('coord', name=name, **other)
