#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

"""Correlation matrix implementation module"""

from .core import PlotSpec
from .geom import geom_point, geom_text
from .scale import scale_y_discrete_reversed, scale_color_gradient2
from .scale_identity import scale_size_identity
from .coord import coord_fixed
from .theme_ import theme, element_blank
from .tooltip import layer_tooltips

__all__ = ['corr_plot']


def to_type(tp):
    return tp if tp else 'full'


def to_format(fmt):
    return fmt if fmt else '.2f'


def to_method(method):
    return method if method else 'pearson'


def add_common_params(plot, reverse_y):
    plot += theme(axis_title=element_blank(), legend_title=element_blank())
    plot += coord_fixed()
    plot += scale_size_identity(name="", na_value=0)
    plot += scale_color_gradient2(name='Correlation',
                                  low='blue', mid='light_gray', high='red',
                                  breaks=[-1.0, -0.5, 0.0, 0.5, 1.0],
                                  limits=[-1.0, 1.0])

    if reverse_y:
        plot += scale_y_discrete_reversed()

    return plot


def reverse_type(type):
    if type == 'upper':
        return 'lower'
    elif type == 'lower':
        return 'upper'

    return type


class corr_plot:

    def __init__(self, data, show_legend=None, format=None, reverse_y=None):
        self.data = data
        self.show_legend = show_legend
        self.format = to_format(format)
        self.reverse_y = reverse_y if reverse_y else False
        self.layers = []
        self.text_color = None

    def get_format(self, format):
        return format if format else self.format

    def tooltip_spec(self, format):
        return layer_tooltips().format({'$var@..corr..': self.get_format(format)}).line('$var@..corr..')

    def get_type(self, type):
        res = type if type else "full"

        if self.reverse_y:
            res = reverse_type(res)

        return res

    def points(self, type=None, fill_diagonal=None, format=None, **other_args):

        points = geom_point(stat='corr', show_legend=self.show_legend, size_unit='x',
                            tooltips=self.tooltip_spec(format),
                            type=to_type(type), fill_diagonal=fill_diagonal,
                            **other_args)

        self.layers.append(points)

        return self

    def labels(self, type=None, fill_diagonal=None, format=None, fit_size=True, **other_args):

        other_args['label_format'] = self.get_format(format)

        if fit_size:
            other_args['size_unit'] = 'x'
            other_args['size'] = 1
        elif 'size' not in other_args:
            other_args['size_unit'] = 'x'

        if 'color' not in other_args:
            other_args['color'] = self.text_color

        text = geom_text(stat='corr', show_legend=self.show_legend,
                         type=to_type(type), fill_diagonal=fill_diagonal,
                         na_value='', **other_args)

        self.layers.append(text)

        return self

    def tiles(self, type=None, fill_diagonal=None, format=None, **other_args):

        self.text_color = 'white'

        tiles = geom_point(stat='corr', show_legend=self.show_legend, size_unit='x',
                           tooltips=self.tooltip_spec(format),
                           type=self.get_type(type), fill_diagonal=fill_diagonal,
                           size=1.0, shape=15, **other_args)

        self.layers.append(tiles)

        return self

    def build(self):
        plot = PlotSpec(self.data, mapping=None, scales=[], layers=self.layers)
        return add_common_params(plot, self.reverse_y)
