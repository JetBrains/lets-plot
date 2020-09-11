#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

"""Correlation matrix implementation module"""

from . import ggplot
from .geom import geom_point, geom_text
from .scale import scale_y_discrete_reversed, scale_color_gradient2
from .scale_identity import scale_size_identity
from .coord import coord_fixed
from .theme_ import theme, element_blank
from .tooltip import layer_tooltips

# __all__ = ['corr_matrix_point', 'corr_matrix_text', 'corr_matrix']
__all__ = ['corr_matrix_point', 'corr_matrix_text']


def corr_matrix_point(data, show_legend=None, type=None, fill_diagonal=None, format=None, **other):
    type = type if type else 'full'
    format = format if format else '.2f'

    res = ggplot(data=data)
    res = res + geom_point(stat='corr', show_legend=show_legend, size_unit='x',
                           tooltips=layer_tooltips().format({'$color': format}).line('Corr|$color'),
                           type=type, fill_diagonal=fill_diagonal, **other)
    res = res + theme(axis_title=element_blank(), legend_title=element_blank())
    res = res + coord_fixed()
    res = res + scale_size_identity()
    res = res + scale_y_discrete_reversed()
    res = res + scale_color_gradient2(name='Correlation',
                                      low='blue', mid='light_gray', high='red',
                                      limits=[-1.0, 1.0])

    return res


def corr_matrix_text(data, show_legend=None, type=None, fill_diagonal=None, format=None, **other):
    type = type if type else 'full'
    format = format if format else '.2f'

    res = ggplot(data=data)
    res = res + geom_text(stat='corr', show_legend=show_legend, label_format=format,
                          tooltips=layer_tooltips().format({'$color': format}).line('Corr|$color'),
                          type=type, fill_diagonal=fill_diagonal, **other)
    res = res + theme(axis_title=element_blank(), legend_title=element_blank())
    res = res + coord_fixed()
    res = res + scale_y_discrete_reversed()
    res = res + scale_color_gradient2(name='Correlation',
                                      low='blue', mid='light_gray', high='red',
                                      limits=[-1.0, 1.0])

    return res
