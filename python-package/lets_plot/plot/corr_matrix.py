#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

"""Correlation matrix implementation module"""

from .geom import geom_point, geom_text
from .scale import scale_color_continuous, scale_y_discrete_reversed
from .scale_identity import scale_size_identity
from .coord import coord_fixed
from .theme_ import theme, element_blank
from .tooltip import layer_tooltips

__all__ = ['geom_corr_point', 'geom_corr_text']


def geom_corr_point(data=None, show_legend=None, type=None, fill_diagonal=None,
                    format=None, low_color=None, high_color=None, **other):
    """
    Correlation matrix implementation via geom_point

    Parameters
    ----------
    data : dictionary, pandas DataFrame or GeoDataFrame (supported shapes Point and MultiPoint), optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.

    show_legend: Boolean parameter for switching legend visibility on / off.

    type: Type of correlation matrix - "upper", "lower" or "full" . Default = "full"

    fill_diagonal: Boolean. When true main diagonal in correlation matrix is filled. Default = True

    format: string, tooltip format for correlation value. Default = ".2f"

    low_color: Color for low end of gradient. Default = 'dark blue'

    high_color: Color for high end of gradient. Default = 'red'

    """

    type = type if type else 'full'
    format = format if format else '.2f'
    low_color = low_color if low_color else 'dark_blue'
    high_color = high_color if high_color else 'red'

    return geom_point(data=data, stat='corr', show_legend=show_legend, size_unit='x',
                      tooltips=layer_tooltips().format({'$color': format}).line('Corr|$color').line('$x and $y'),
                      type=type, fill_diagonal=fill_diagonal, **other) + \
           scale_color_continuous(name='Correlation', low=low_color, high=high_color, limits=[-1.0, 1.0]) + \
           theme(axis_title=element_blank(), legend_title=element_blank()) + \
           coord_fixed() + \
           scale_size_identity() + \
           scale_y_discrete_reversed()


def geom_corr_text(data=None, show_legend=None, type=None, fill_diagonal=None,
                   format=None, low_color=None, high_color=None, **other):

    """
    Correlation matrix implementation via geom_text

    Parameters
    ----------
    data : dictionary, pandas DataFrame or GeoDataFrame (supported shapes Point and MultiPoint), optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.

    show_legend: Boolean parameter for switching legend visibility on / off.

    type: Type of correlation matrix - "upper", "lower" or "full" . Default = "full"

    fill_diagonal: Boolean. When true main diagonal in correlation matrix is filled. Default = True

    format: string, tooltip format for correlation value. Default = ".2f"

    low_color: Color for low end of gradient. Default = 'dark blue'

    high_color: Color for high end of gradient. Default = 'red'

    """


    type = type if type else 'full'
    format = format if format else '.2f'
    low_color = low_color if low_color else 'dark_blue'
    high_color = high_color if high_color else 'red'

    return geom_text(data=data, stat='corr', show_legend=show_legend,
                     type=type, fill_diagonal=fill_diagonal, label_format=format, **other) + \
           scale_color_continuous(name='Correlation', low=low_color, high=high_color, limits=[-1.0, 1.0]) + \
           coord_fixed() + \
           theme(axis_title=element_blank(), legend_title=element_blank()) + \
           scale_y_discrete_reversed()
