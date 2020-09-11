#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

"""Correlation matrix implementation module"""

from . import ggplot
from .geom import geom_point, geom_text
from .scale import scale_y_discrete_reversed, scale_color_gradient2, scale_size
from .scale_identity import scale_size_identity
from .coord import coord_fixed
from .theme_ import theme, element_blank
from .tooltip import layer_tooltips

__all__ = ['corr_matrix_point', 'corr_matrix_text', 'corr_matrix']


def common_corr_spec(data):
    res = ggplot(data=data)
    res += theme(axis_title=element_blank(), legend_title=element_blank())
    res += coord_fixed()
    res += scale_y_discrete_reversed()
    res += scale_color_gradient2(name='Correlation',
                                 low='blue', mid='light_gray', high='red',
                                 limits=[-1.0, 1.0])

    return res


def corr_tooltips_spec(format):
    return layer_tooltips().format({'$color': format}).line('Corr|$color')


def corr_matrix_point(data, show_legend=None, type=None, fill_diagonal=None, format=None, **other):
    type = type if type else 'full'
    format = format if format else '.2f'

    res = common_corr_spec(data)
    res += geom_point(stat='corr', show_legend=show_legend, size_unit='x',
                      tooltips=corr_tooltips_spec(format),
                      type=type, fill_diagonal=fill_diagonal, **other)
    res += scale_size_identity()

    return res


def corr_matrix_text(data, show_legend=None, type=None, fill_diagonal=None, format=None, **other):
    type = type if type else 'full'
    format = format if format else '.2f'

    res = common_corr_spec(data)
    res += geom_text(stat='corr', show_legend=show_legend, label_format=format,
                     tooltips=corr_tooltips_spec(format),
                     type=type, fill_diagonal=fill_diagonal, **other)

    return res


def corr_matrix(data, show_legend=None,
                draw=None, type=None, fill_diagonal=None,
                format=None, text_size=None, **other):
    """
    Returns correlation matrix plot.

    data : dictionary or pandas DataFrame, required.


    """
    # draw - "points", "text", "mixed"
    draw = draw if draw else "points"
    type = type if type else 'full'
    format = format if format else '.2f'

    res = common_corr_spec(data)

    if draw == "points":
        res += geom_point(stat='corr', show_legend=show_legend, size_unit='x',
                          tooltips=corr_tooltips_spec(format),
                          type=type, fill_diagonal=fill_diagonal, **other)

        res += scale_size_identity(name="")
    elif draw == "text":
        res += geom_text(stat='corr', show_legend=show_legend, label_format=format,
                         tooltips=corr_tooltips_spec(format),
                         type=type, fill_diagonal=fill_diagonal, size=text_size, **other)
        res += scale_size(name="")

    elif draw == "mixed":
        res += geom_point(stat='corr', show_legend=show_legend,
                          tooltips=corr_tooltips_spec(format),
                          type="upper", fill_diagonal=True, **other)

        res += geom_text(stat='corr', show_legend=show_legend, type="lower", fill_diagonal=False,
                         label_format=format, size=text_size, **other)

        res += scale_size(name="")

    return res
