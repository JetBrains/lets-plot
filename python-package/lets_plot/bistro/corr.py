#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

"""Correlation matrix implementation module"""

from lets_plot.plot.core import PlotSpec
from lets_plot.plot.geom import geom_point, geom_text
from lets_plot.plot.scale import scale_y_discrete_reversed, scale_color_gradient2
from lets_plot.plot.scale_identity import scale_size_identity
from lets_plot.plot.coord import coord_fixed
from lets_plot.plot.theme_ import theme, element_blank
from lets_plot.plot.tooltip import layer_tooltips

__all__ = ['corr_plot_builder', 'corr_plot']


def _reverse_type(type):
    if type == 'upper':
        return 'lower'
    elif type == 'lower':
        return 'upper'

    return type


class corr_plot_builder:
    """
    This class is intended to build correlation matrix plots.
    """

    def __init__(self, data, show_legend=None, format=None, flip=None):
        """
        :param data:  dictionary or pandas DataFrame  required. Correlation will bw calculated for each variable pair.
        :param show_legend: Boolean. If True legend is shown. Default - True.
        :param format: format for tooltips and labels. Default '.2f'.
        :param flip: Boolean. If True th y axis is flipped.
        """

        self._data = data
        self._show_legend = show_legend
        self._format = format if format else '.2f'
        self._reverse_y = flip if flip else False
        self._text_color = None
        self._tiles_layer = None
        self._points_layer = None
        self._labels_layer = None

    def _add_common_params(self, plot):
        plot += theme(axis_title=element_blank(),
                      legend_title=element_blank(),
                      axis_line_x=element_blank(),
                      axis_line_y=element_blank())

        plot += coord_fixed()
        plot += scale_size_identity(name="", na_value=0)
        plot += scale_color_gradient2(name='Correlation',
                                      low='red', mid='light_gray', high='blue',
                                      breaks=[-1.0, -0.5, 0.0, 0.5, 1.0],
                                      limits=[-1.0, 1.0])

        if self._reverse_y:
            plot += scale_y_discrete_reversed()

        return plot

    def _get_format(self, format):
        return format if format else self._format

    def _tooltip_spec(self, format):
        return layer_tooltips(). \
            format(field='var@..corr..', format=self._get_format(format)). \
            line('${var@..corr..}')

    def _get_type(self, type):
        res = type if type else "full"

        if self._reverse_y:
            res = _reverse_type(res)

        return res

    def points(self, type=None, fill_diagonal=None, format=None, **other_args):

        """
        Method adds correlation matrix layer drawn by points to the plot.
        :param type: Type of matrix. Possible values - "upper", "lower", "full". Default - "full"
        :param fill_diagonal: Boolean parameter, if True the main diagonal is filled with values. Default - True
        :param format: Text format for tooltips. Default - '.2f'
        :param other_args: - other args, passed to geom_point.
        :return: self
        """

        self._points_layer = geom_point(stat='corr', show_legend=self._show_legend, size_unit='x',
                                        tooltips=self._tooltip_spec(format),
                                        type=self._get_type(type), fill_diagonal=fill_diagonal,
                                        **other_args)

        return self

    def labels(self, type=None, fill_diagonal=None, format=None, map_size=False, **other_args):

        """
        Method adds correlation matrix layer drawn with geom_text to the plot.
        :param type: Type of matrix. Possible values - "upper", "lower", "full". Default - "full"
        :param fill_diagonal: Boolean parameter, if True the main diagonal is filled with values. Default - True
        :param format: Text format for tooltips and labels. Default - '.2f'
        :param map_size - Boolean, if True, then absolute value of correlation is mapped to text size.
        :param other_args: - other args, passed to geom_text.
        :return: self
        """
        other_args['label_format'] = self._get_format(format)

        if 'size' not in other_args:
            if not map_size:
                other_args['size_unit'] = 'x'
                other_args['size'] = 1
            else:
                other_args['size_unit'] = 'x'

        if 'color' not in other_args:
            other_args['color'] = self._text_color

        self._labels_layer = geom_text(stat='corr', show_legend=self._show_legend,
                                       tooltips=self._tooltip_spec(format),
                                       type=self._get_type(type), fill_diagonal=fill_diagonal,
                                       na_value='', **other_args)

        return self

    def tiles(self, type=None, fill_diagonal=None, format=None, **other_args):

        """
        Method adds correlation matrix layer drawn as square tiles to the plot.
        :param type: Type of matrix. Possible values - "upper", "lower", "full". Default - "full"
        :param fill_diagonal: Boolean parameter, if True the main diagonal is filled with values. Default - True
        :param format: Text format for tooltips and labels. Default - '.2f'
        :param other_args: - other args, passed to geom_text.
        :return: self
        """

        self._text_color = 'white'

        self._tiles_layer = geom_point(stat='corr', show_legend=self._show_legend, size_unit='x',
                                       tooltips=self._tooltip_spec(format),
                                       type=self._get_type(type), fill_diagonal=fill_diagonal,
                                       size=1.0, shape=15, **other_args)

        return self

    def build(self):

        """
        This method create PlotSpec and returns it.
        :return: PlotSpec for correlation matrix.
        """

        layers = []

        if self._tiles_layer:
            layers.append(self._tiles_layer)

        if self._points_layer:
            layers.append(self._points_layer)

        if self._labels_layer:
            layers.append(self._labels_layer)

        plot = PlotSpec(self._data, mapping=None, scales=[], layers=layers)

        return self._add_common_params(plot)


def corr_plot(data, draw_as='points', format=None):
    """
    :param data: dictionary or pandas DataFrame  required. Correlation will bw calculated for each variable pair.
    :param draw_as: Specifies how correlation matrix is drawn. Can be 'points', 'tiles' or 'labels'. Default - 'points'
    :param format: Format specification for tooltips and labels.
    :return: PlotSpec for correlation matrix
    """

    plot_builder = corr_plot_builder(data=data, format=format, flip=True)

    if draw_as == 'points':
        plot_builder.points()
    elif draw_as == 'tiles':
        plot_builder.tiles()
        plot_builder.labels()
    elif draw_as == 'labels':
        plot_builder.labels()

    return plot_builder.build()
