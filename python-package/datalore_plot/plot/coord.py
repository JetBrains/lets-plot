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


def coord_cartesian():
    return _coord('cartesian')


def coord_fixed(ratio=1.):
    return _coord('fixed', ratio=ratio)


def coord_map():
    return _coord('map')


def _coord(name, **other):
    return FeatureSpec('coord', name=name, **other)
