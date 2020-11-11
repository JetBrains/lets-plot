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
from lets_plot.plot.plot import ggsize
from lets_plot.plot.geom import geom_point, geom_text
from lets_plot.plot.scale import scale_y_discrete_reversed, scale_color_gradient2, scale_color_brewer
from lets_plot.plot.scale_identity import scale_size_identity
from lets_plot.plot.coord import coord_fixed
from lets_plot.plot.theme_ import theme, element_blank
from lets_plot.plot.tooltip import layer_tooltips
from lets_plot.plot.plot import ggplot

__all__ = ['corr_plot_builder', 'corr_plot_scatter', 'corr_plot_tiles',
           'corr_plot_tileslab', 'corr_plot_scatterlab']


class corr_plot_builder:
    """
    This class is intended to build correlation matrix plots.
    """

    _LEGEND_NAME = 'Corr'
    _BREAKS = [-1.0, -0.5, 0.0, 0.5, 1.0]
    _LIMITS = [-1.0, 1.0]

    def __init__(self, data, show_legend=None, flip=True):
        """
        Parameters
        ----------
        data :  dictionary or pandas DataFrame
            Correlation will be calculated for each variable pair.
        show_legend : Boolean
            If True legend is shown. Default - True.
        flip : Boolean
            If True the y axis is flipped.
        """

        self._data = data
        self._show_legend = show_legend
        self._format = '.2f'
        self._reverse_y = flip if flip else False
        self._color_scale = scale_color_gradient2(name=corr_plot_builder._LEGEND_NAME,
                                                  low='red', mid='light_gray', high='blue',
                                                  breaks=corr_plot_builder._BREAKS,
                                                  limits=corr_plot_builder._LIMITS)
        self._points_params = None
        self._tiles_params = None
        self._labels_params = None

    def points(self, type=None, fill_diagonal=None):
        """
        Method defines correlation matrix layer drawn by points to the plot.

        Parameters
        ----------
        type : string
            Type of matrix. Possible values - "upper", "lower", "full". Default - "full".
        fill_diagonal : Boolean
            If True the main diagonal is filled with values. Default - True.

        Returns
        -------
            self
        """
        self._points_params = {}

        if type:
            self._points_params['type'] = self._get_type(type)

        if fill_diagonal:
            self._points_params['fill_diagonal'] = fill_diagonal

        return self

    def labels(self, type=None, fill_diagonal=None, map_size=False, color=None):
        """
        Method defines correlation matrix layer drawn with geom_text to the plot.

        Parameters
        ----------
        type : string
            Type of matrix. Possible values - "upper", "lower", "full". Default - "full".
        fill_diagonal : Boolean
            If True the main diagonal is filled with values. Default - True.
        map_size : Boolean
            If True, then absolute value of correlation is mapped to text size. Default - False.
        color: string
            Set text color.
        Returns
        -------
            self
        """

        self._labels_params = {}

        if type:
            self._labels_params['type'] = self._get_type(type)

        if fill_diagonal:
            self._labels_params['fill_diagonal'] = fill_diagonal

        if not map_size:
            self._labels_params['size'] = 1

        if color:
            self._labels_params['color'] = color

        return self

    def tiles(self, type=None, fill_diagonal=None):
        """
        Method defines correlation matrix layer drawn as square tiles to the plot.

        Parameters
        ----------
        type : string
            Type of matrix. Possible values - "upper", "lower", "full". Default - "full".
        fill_diagonal : Boolean
            If True the main diagonal is filled with values. Default - True.

        Returns
        -------
            self
        """

        self._tiles_params = {}

        if type:
            self._tiles_params['type'] = self._get_type(type)

        if fill_diagonal:
            self._tiles_params['fill_diagonal'] = fill_diagonal

        return self

    def build(self):
        """
        This method create PlotSpec and returns it.

        Returns
        -------
            self
        """

        plot = ggplot(self._data)

        if self._tiles_params is not None:
            plot += geom_point(stat='corr', show_legend=self._show_legend, size_unit='x',
                               tooltips=self._tooltip_spec(), size=1.0, shape=15,
                               **self._tiles_params)

        if self._points_params is not None:
            plot += geom_point(stat='corr', show_legend=self._show_legend, size_unit='x',
                               tooltips=self._tooltip_spec(),
                               **self._points_params)

        if self._labels_params is not None:

            if self._tiles_params is not None and 'color' not in self._labels_params:
                self._labels_params['color'] = 'white'

            plot += geom_text(stat='corr', show_legend=self._show_legend,
                              tooltips=self._tooltip_spec(),
                              na_value='', label_format=self._format,
                              size_unit='x', **self._labels_params)

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
        self._color_scale = scale_color_gradient2(name=corr_plot_builder._LEGEND_NAME,
                                                  low=low, mid=mid, high=high,
                                                  breaks=corr_plot_builder._BREAKS,
                                                  limits=corr_plot_builder._LIMITS)

        return self

    def patette_BrBG(self):
        """
        Set scale_color_brewer with BrBG palette for corr plot.

        Returns
        -------
            self
        """
        return self._set_brewer_palette('BrBG')

    def patette_PiYG(self):
        """
        Set scale_color_brewer with PiYG palette for corr plot.

        Returns
        -------
            self
        """
        return self._set_brewer_palette('PiYG')

    def patette_PRGn(self):
        """
        Set scale_color_brewer with PRGn palette for corr plot.

        Returns
        -------
            self
        """
        return self._set_brewer_palette('PRGn')

    def patette_PuOr(self):
        """
        Set scale_color_brewer with PuOr palette for corr plot.

        Returns
        -------
            self
        """
        return self._set_brewer_palette('PuOr')

    def patette_RdBu(self):
        """
        Set scale_color_brewer with RdBu palette for corr plot.

        Returns
        -------
            self
        """
        return self._set_brewer_palette('RdBu')

    def patette_RdGy(self):
        """
        Set scale_color_brewer with RdGy palette for corr plot.

        Returns
        -------
            self
        """
        return self._set_brewer_palette('RdGy')

    def patette_RdYlBu(self):
        """
        Set scale_color_brewer with RdYlBu palette for corr plot.

        Returns
        -------
            self
        """
        return self._set_brewer_palette('RdYlBu')

    def patette_RdYlGn(self):
        """
        Set scale_color_brewer with RdYlGn palette for corr plot.

        Returns
        -------
            self
        """
        return self._set_brewer_palette('RdYlGn')

    def patette_Spectral(self):
        """
        Set scale_color_brewer with Spectral palette for corr plot.

        Returns
        -------
            self
        """
        return self._set_brewer_palette('Spectral')

    def _get_numeric_columns_count(self):

        def is_numeric_values(values):
            if (numpy and isinstance(values, numpy.ndarray)) or (pandas and isinstance(values, pandas.Series)):
                return is_numeric_values(values.tolist())

            if isinstance(values, list) and all(map(lambda v: v is None or is_number(v), values)):
                return True

            return False

        res = 0

        for k in self._data:
            values = self._data[k]

            if is_numeric_values(values):
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
        height = width

        if self._show_legend:
            height *= _PLOT_PROPORTION

        plot += ggsize(width, height)

        return plot

    def _tooltip_spec(self):
        return layer_tooltips(). \
            format(field='@..corr..', format=self._format). \
            line('@..corr..')

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

    def _set_brewer_palette(self, palette):
        self._color_scale = scale_color_brewer(name=corr_plot_builder._LEGEND_NAME,
                                               palette=palette,
                                               breaks=corr_plot_builder._BREAKS,
                                               limits=corr_plot_builder._LIMITS)

        return self


def corr_plot_scatter(data, palette=None):
    """
    Draws correlation matrix as scatterplot.

    Parameters
    ----------
    data : dictionary or pandas DataFrame.
        Correlation will be calculated for each variable pair. Required.
    palette : string
        Palette name, one of: "BrBG", "PiYG", "PRGn", "PuOr", "RdBu", "RdGy", "RdYlBu", "RdYlGn", "Spectral".

    Returns
    -------
        PlotSpec for correlation matrix.
    """

    plot_builder = corr_plot_builder(data=data)
    plot_builder.points()

    if palette:
        plot_builder._set_brewer_palette(palette)

    return plot_builder.build()


def corr_plot_tiles(data, palette=None):
    """
    Draws correlation matrix as tiles.

    Parameters
    ----------
    data : dictionary or pandas DataFrame.
        Correlation will be calculated for each variable pair. Required.
    palette : string
        Palette name, one of: "BrBG", "PiYG", "PRGn", "PuOr", "RdBu", "RdGy", "RdYlBu", "RdYlGn", "Spectral".

    Returns
    -------
        PlotSpec for correlation matrix.
    """
    plot_builder = corr_plot_builder(data=data)
    plot_builder.tiles()

    if palette:
        plot_builder._set_brewer_palette(palette)

    return plot_builder.build()


def corr_plot_tileslab(data, palette=None):
    """
    Draws correlation matrix as tiles with labels.

    Parameters
    ----------
    data : dictionary or pandas DataFrame.
        Correlation will be calculated for each variable pair. Required.
    palette : string
        Palette name, one of: "BrBG", "PiYG", "PRGn", "PuOr", "RdBu", "RdGy", "RdYlBu", "RdYlGn", "Spectral".

    Returns
    -------
        PlotSpec for correlation matrix.
    """
    plot_builder = corr_plot_builder(data=data)
    plot_builder.tiles()
    plot_builder.labels()

    if palette:
        plot_builder._set_brewer_palette(palette)

    return plot_builder.build()


def corr_plot_scatterlab(data, palette=None):
    """
    Draws correlation matrix as mix of scattrplot and labels.

    Parameters
    ----------
    data : dictionary or pandas DataFrame.
        Correlation will be calculated for each variable pair. Required.
    palette : string
        Palette name, one of: "BrBG", "PiYG", "PRGn", "PuOr", "RdBu", "RdGy", "RdYlBu", "RdYlGn", "Spectral".

    Returns
    -------
        PlotSpec for correlation matrix.
    """
    plot_builder = corr_plot_builder(data=data)
    plot_builder.points(type='lower')
    plot_builder.labels(type='upper', fill_diagonal=False, map_size=False)

    if palette:
        plot_builder._set_brewer_palette(palette)

    return plot_builder.build()
