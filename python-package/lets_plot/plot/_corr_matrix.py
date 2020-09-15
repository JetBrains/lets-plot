#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

"""Correlation matrix implementation module"""

from .core import PlotSpec
from .geom import geom_point, geom_text, geom_tile
from .scale import scale_y_discrete_reversed, scale_color_gradient2
from .scale_identity import scale_size_identity
from .coord import coord_fixed
from .theme_ import theme, element_blank
from .tooltip import layer_tooltips

__all__ = ['corr_matrix']


def to_type(tp):
    return tp if tp else 'full'


def to_format(fmt):
    return fmt if fmt else '.2f'


def to_method(method):
    return method if method else 'pearson'


def add_common_params(plot):
    plot += theme(axis_title=element_blank(), legend_title=element_blank())
    plot += coord_fixed()
    plot += scale_y_discrete_reversed()
    plot += scale_size_identity(name="", na_value=0)
    plot += scale_color_gradient2(name='Correlation',
                                  low='blue', mid='light_gray', high='red',
                                  limits=[-1.0, 1.0])

    return plot


class corr_matrix():
    def __init__(self, data, show_legend=None, format=None, method=None):
        self.data = data
        self.show_legend = show_legend
        self.format = to_format(format)
        self.method = to_method(method)
        self.layers = []

    def get_format(self, format):
        return format if format else self.format

    def tooltip_spec(self, format):
        return layer_tooltips().format({'$color': self.get_format(format)}).line('Corr|$color')

    def points(self, type=None, fill_diagonal=None, tooltip_format=None, shape=None):
        points = geom_point(stat='corr', show_legend=self.show_legend, size_unit='x',
                            tooltips=self.tooltip_spec(tooltip_format),
                            type=to_type(type), fill_diagonal=fill_diagonal,
                            method=self.method, shape=shape)

        self.layers.append(points)

        return self

    def text(self, type=None, fill_diagonal=None, text_format=None, text_size=None):
        if text_size:
            kwargs = {'size': text_size}
        else:
            kwargs = {'size_unit': 'x'}

        text = geom_text(stat='corr', show_legend=self.show_legend,
                         label_format=self.get_format(text_format),
                         type=to_type(type), fill_diagonal=fill_diagonal,
                         method=self.method, na_value='', **kwargs)

        self.layers.append(text)

        return self

    def tiles(self, type=None, fill_diagonal=None, tooltip_format=None):
        tiles = geom_tile(stat='corr', show_legend=self.show_legend, size_unit='x',
                          tooltips=self.tooltip_spec(tooltip_format),
                          type=to_type(type), fill_diagonal=fill_diagonal,
                          method=self.method)

        self.layers.append(tiles)

        return self

    def build(self):
        plot = PlotSpec(self.data, mapping=None, scales=[], layers=self.layers)
        return add_common_params(plot)
