#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

"""Correlation matrix implementation module"""
try:
    import numpy
except ImportError:
    numpy = None

try:
    import pandas
except ImportError:
    pandas = None

from lets_plot._type_utils import is_number
from pandas.api.types import is_numeric_dtype
from lets_plot.plot.plot import ggsize
from lets_plot.plot.core import PlotSpec
from lets_plot.plot.geom import geom_point, geom_text
from lets_plot.plot.scale import scale_y_discrete_reversed, scale_color_gradient2, scale_color_brewer
from lets_plot.plot.scale_identity import scale_size_identity
from lets_plot.plot.coord import coord_fixed
from lets_plot.plot.theme_ import theme, element_blank
from lets_plot.plot.tooltip import layer_tooltips

__all__ = ['corr_plot_builder', 'corr_plot_scatter', 'corr_plot_tiles',
           'corr_plot_tileslab', 'corr_plot_scatterlab']


class corr_plot_builder:
    """
    This class is intended to build correlation matrix plots.
    """

    def __init__(self, data, show_legend=None, format=None, flip=None):
        """
        Parameters
        ----------
        data :  dictionary or pandas DataFrame
            Correlation will be calculated for each variable pair.
        show_legend : Boolean
            If True legend is shown. Default - True.
        format : string
            The format to apply to the field. The format contains a number format (1.f) or a string template ({.1f}).
            Default - '.2f'.
        flip : Boolean
            If True the y axis is flipped.
        """

        self._data = data
        self._show_legend = show_legend
        self._format = format if format else '.2f'
        self._reverse_y = flip if flip else False
        self._text_color = None
        self._tiles_layer = None
        self._points_layer = None
        self._labels_layer = None
        self._color_scale = scale_color_gradient2(name='Correlation',
                                                  low='red', mid='light_gray', high='blue',
                                                  breaks=[-1.0, -0.5, 0.0, 0.5, 1.0],
                                                  limits=[-1.0, 1.0])

    def points(self, type=None, fill_diagonal=None, format=None, **other_args):
        """
        Method defines correlation matrix layer drawn by points to the plot.

        Parameters
        ----------
        type : string
            Type of matrix. Possible values - "upper", "lower", "full". Default - "full".
        fill_diagonal : Boolean
            If True the main diagonal is filled with values. Default - True.
        format : string
            The format to apply to the field. The format contains a number format (1.f) or a string template ({.1f}).
            Default - '.2f'.

        Returns
        -------
            self
        """

        self._points_layer = geom_point(stat='corr', show_legend=self._show_legend, size_unit='x',
                                        tooltips=self._tooltip_spec(format),
                                        type=self._get_type(type), fill_diagonal=fill_diagonal,
                                        **other_args)

        return self

    def labels(self, type=None, fill_diagonal=None, format=None, map_size=False, **other_args):
        """
        Method defines correlation matrix layer drawn with geom_text to the plot.

        Parameters
        ----------
        type : string
            Type of matrix. Possible values - "upper", "lower", "full". Default - "full".
        fill_diagonal : Boolean
            If True the main diagonal is filled with values. Default - True.
        format : string
            The format to apply to the field. The format contains a number format (1.f) or a string template ({.1f}).
            Default - '.2f'.
        map_size - Boolean
            If True, then absolute value of correlation is mapped to text size. Default - False.

        Returns
        -------
            self
        """
        other_args['label_format'] = self._get_format(format)

        if 'size' not in other_args:
            other_args['size_unit'] = 'x'

            if not map_size:
                other_args['size'] = 1

        if 'color' not in other_args:
            other_args['color'] = self._text_color

        self._labels_layer = geom_text(stat='corr', show_legend=self._show_legend,
                                       tooltips=self._tooltip_spec(format),
                                       type=self._get_type(type), fill_diagonal=fill_diagonal,
                                       na_value='', **other_args)

        return self

    def tiles(self, type=None, fill_diagonal=None, format=None, **other_args):
        """
        Method defines correlation matrix layer drawn as square tiles to the plot.

        Parameters
        ----------
        type : string
            Type of matrix. Possible values - "upper", "lower", "full". Default - "full".
        fill_diagonal : Boolean
            If True the main diagonal is filled with values. Default - True.
        format : string
            The format to apply to the field. The format contains a number format (1.f) or a string template ({.1f}).
            Default - '.2f'.

        Returns
        -------
            self
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

        Returns
        -------
            self
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

    def palette_gradient(self, low, mid, high):
        """
        Set scale_color_gradient2 for corr plot.

        Parameters
        ----------
        low : string
            Color for low end of gradient (correlation -1).
        mid : string
            Color for mid point (correlation 0).
        high : string
            Color for high end of gradient (correlation 1).

        Returns
        -------
            self
        """
        self._color_scale = scale_color_gradient2(name='Correlation',
                                                  low=low, mid=mid, high=high,
                                                  breaks=[-1.0, -0.5, 0.0, 0.5, 1.0],
                                                  limits=[-1.0, 1.0])

        return self

    def patette_BrBG(self):
        """
        Set scale_color_brewer with BrBG palette for corr plot.

        Returns
        -------
            self
        """
        return self._set_diverging_palette('BrBG')

    def patette_PiYG(self):
        """
        Set scale_color_brewer with PiYG palette for corr plot.

        Returns
        -------
            self
        """
        return self._set_diverging_palette('PiYG')

    def patette_PRGn(self):
        """
        Set scale_color_brewer with PRGn palette for corr plot.

        Returns
        -------
            self
        """
        return self._set_diverging_palette('PRGn')

    def patette_PuOr(self):
        """
        Set scale_color_brewer with PuOr palette for corr plot.

        Returns
        -------
            self
        """
        return self._set_diverging_palette('PuOr')

    def patette_RdBu(self):
        """
        Set scale_color_brewer with RdBu palette for corr plot.

        Returns
        -------
            self
        """
        return self._set_diverging_palette('RdBu')

    def patette_RdGy(self):
        """
        Set scale_color_brewer with RdGy palette for corr plot.

        Returns
        -------
            self
        """
        return self._set_diverging_palette('RdGy')

    def patette_RdYlBu(self):
        """
        Set scale_color_brewer with RdYlBu palette for corr plot.

        Returns
        -------
            self
        """
        return self._set_diverging_palette('RdYlBu')

    def patette_RdYlGn(self):
        """
        Set scale_color_brewer with RdYlGn palette for corr plot.

        Returns
        -------
            self
        """
        return self._set_diverging_palette('RdYlGn')

    def patette_Spectral(self):
        """
        Set scale_color_brewer with Spectral palette for corr plot.

        Returns
        -------
            self
        """
        return self._set_diverging_palette('Spectral')

    def _get_numeric_columns_count(self):
        res = 0

        for k in self._data:
            values = self._data[k]

            if numpy and isinstance(values, numpy.ndarray):
                res += 1
            elif pandas and isinstance(values, pandas.Series) and is_numeric_dtype(values):
                res += 1
            elif isinstance(values, list) and all(values, lambda v: v is None or is_number(v)):
                res += 1

        return res


    def _add_common_params(self, plot):
        _COLUMN_WIDTH = 60
        _MIN_PLOT_WIDTH = 400
        _MAX_PLOT_WIDTH = 900
        _PLOT_PROPORTION = 3.0 / 4.0

        plot += theme(axis_title=element_blank(),
                      legend_title=element_blank(),
                      axis_line_x=element_blank(),
                      axis_line_y=element_blank())

        plot += coord_fixed()
        plot += scale_size_identity(name="", na_value=0)
        plot += self._color_scale

        if self._reverse_y:
            plot += scale_y_discrete_reversed()

        columns_count = self._get_numeric_columns_count()
        width = min(_MAX_PLOT_WIDTH, max(_MIN_PLOT_WIDTH, columns_count * _COLUMN_WIDTH))
        height = width * _PLOT_PROPORTION

        plot += ggsize(width, height)

        return plot

    def _get_format(self, format):
        return format if format else self._format

    def _tooltip_spec(self, format):
        return layer_tooltips(). \
            format(field='var@..corr..', format=self._get_format(format)). \
            line('${var@..corr..}')

    def _get_type(self, type):
        def _reverse_type(type):
            if type == 'upper':
                return 'lower'
            elif type == 'lower':
                return 'upper'

            return type

        res = type if type else "full"

        if self._reverse_y:
            res = _reverse_type(res)

        return res

    def _set_diverging_palette(self, palette):
        self._color_scale = scale_color_brewer(name='Correlation',
                                               type='div',
                                               palette=palette,
                                               breaks=[-1.0, -0.5, 0.0, 0.5, 1.0],
                                               limits=[-1.0, 1.0])

        return self


def corr_plot_scatter(data, format=None, palette=None):
    """
    Draws correlation matrix as scatterplot.

    Parameters
    ----------
    data : dictionary or pandas DataFrame.
        Correlation will be calculated for each variable pair. Required.
    format : string
        The format to apply to the field. The format contains a number format (1.f) or a string template ({.1f}).
        Default - '.2f'.
    palette : string
        Palette name, one of: "BrBG", "PiYG", "PRGn", "PuOr", "RdBu", "RdGy", "RdYlBu", "RdYlGn", "Spectral".

    Returns
    -------
        PlotSpec for correlation matrix.
    """

    plot_builder = corr_plot_builder(data=data, format=format, flip=True)
    plot_builder.points()

    if palette:
        plot_builder._set_diverging_palette(palette)

    return plot_builder.build()


def corr_plot_tiles(data, format=None, palette=None):
    """
    Draws correlation matrix as tiles.

    Parameters
    ----------
    data : dictionary or pandas DataFrame.
        Correlation will be calculated for each variable pair. Required.
    format : string
        The format to apply to the field. The format contains a number format (1.f) or a string template ({.1f}).
        Default - '.2f'.
    palette : string
        Palette name, one of: "BrBG", "PiYG", "PRGn", "PuOr", "RdBu", "RdGy", "RdYlBu", "RdYlGn", "Spectral".

    Returns
    -------
        PlotSpec for correlation matrix.
    """
    plot_builder = corr_plot_builder(data=data, format=format, flip=True)
    plot_builder.tiles()

    if palette:
        plot_builder._set_diverging_palette(palette)

    return plot_builder.build()


def corr_plot_tileslab(data, format=None, palette=None):
    """
    Draws correlation matrix as tiles with labels.

    Parameters
    ----------
    data : dictionary or pandas DataFrame.
        Correlation will be calculated for each variable pair. Required.
    format : string
        The format to apply to the field. The format contains a number format (1.f) or a string template ({.1f}).
        Default - '.2f'.
    palette : string
        Palette name, one of: "BrBG", "PiYG", "PRGn", "PuOr", "RdBu", "RdGy", "RdYlBu", "RdYlGn", "Spectral".

    Returns
    -------
        PlotSpec for correlation matrix.
    """
    plot_builder = corr_plot_builder(data=data, format=format, flip=True)
    plot_builder.tiles()
    plot_builder.labels()

    if palette:
        plot_builder._set_diverging_palette(palette)

    return plot_builder.build()


def corr_plot_scatterlab(data, format=None, palette=None):
    """
    Draws correlation matrix as mix of scattrplot and labels.

    Parameters
    ----------
    data : dictionary or pandas DataFrame.
        Correlation will be calculated for each variable pair. Required.
    format : string
        The format to apply to the field. The format contains a number format (1.f) or a string template ({.1f}).
        Default - '.2f'.
    palette : string
        Palette name, one of: "BrBG", "PiYG", "PRGn", "PuOr", "RdBu", "RdGy", "RdYlBu", "RdYlGn", "Spectral".

    Returns
    -------
        PlotSpec for correlation matrix.
    """
    plot_builder = corr_plot_builder(data=data, format=format, flip=True)
    plot_builder.points(type='lower')
    plot_builder.labels(type='upper', fill_diagonal=False, map_size=False)

    if palette:
        plot_builder._set_diverging_palette(palette)

    return plot_builder.build()
