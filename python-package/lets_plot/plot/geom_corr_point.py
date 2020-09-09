#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

"""Correlation matrix implementation via geom_point"""

from .geom import geom_point
from .scale import scale_color_continuous, scale_size, scale_y_discrete_reversed
from .scale_identity import scale_size_identity
from .coord import coord_fixed
from .theme_ import theme, element_blank
from .tooltip import layer_tooltips

__all__ = ['geom_corr_point']


def geom_corr_point(data=None, show_legend=None, **other_args):
    return geom_point(mapping=None, data=data, stat='corr', show_legend=show_legend, animation=None, size_unit='x',
                      tooltips=layer_tooltips().format({'$color': '.1f'}).line('Corr|$color').line('$x and $y'),
                      **other_args) + \
           scale_color_continuous(name='Correlation', low='dark_blue', high='red', limits=[-1.0, 1.0]) + \
           scale_size(name='Abs. corr') + \
           theme(axis_title=element_blank(), legend_title=element_blank()) + \
           coord_fixed() + \
           scale_size_identity() + \
           scale_y_discrete_reversed()
