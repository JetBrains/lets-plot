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

from typing import Dict

from lets_plot._type_utils import is_number
from lets_plot.plot.coord import coord_fixed, coord_cartesian
from lets_plot.plot.core import PlotSpec
from lets_plot.plot.core import aes
from lets_plot.plot.geom import geom_point, geom_text, geom_tile
from lets_plot.plot.plot import ggplot
from lets_plot.plot.plot import ggsize
from lets_plot.plot.scale import scale_color_gradient2, scale_color_brewer, \
    scale_fill_gradient2, scale_fill_brewer, scale_x_discrete, scale_y_discrete
from lets_plot.plot.scale_identity import scale_size_identity
from lets_plot.plot.theme_ import theme, element_blank
from lets_plot.plot.tooltip import layer_tooltips

__all__ = ['corr_plot']


class corr_plot:
    """
    This class is intended to build correlation matrix plot.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        from lets_plot.bistro.corr import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {var: np.random.poisson(size=10) for var in 'abcdef'}
        corr_plot(data).tiles().build()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7-9

        import numpy as np
        from lets_plot import *
        from lets_plot.bistro.corr import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {var: np.random.uniform(size=10) for var in 'abcd'}
        corr_plot(data).tiles(type='upper', diag=True)\\
            .labels(type='upper', diag=True, map_size=True, color='black')\\
            .palette_RdBu().build()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7-9

        import numpy as np
        from lets_plot import *
        from lets_plot.bistro.corr import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {var: np.random.normal(size=10) for var in 'abcdef'}
        corr_plot(data, flip=False, threshold=.4).points().labels()\\
            .palette_gradient(low='#d73027', mid='#ffffbf', high='#1a9850')\\
            .build()

    """

    _LEGEND_NAME = 'Corr'
    _BREAKS = [-1.0, -0.5, 0.0, 0.5, 1.0]
    _LABELS = ['-1', '-0.5', '0', '0.5', '1']
    _LIMITS = [-1.0, 1.0]
    _DEF_LOW_COLOR = '#B3412C'
    _DEF_MID_COLOR = '#EDEDED'
    _DEF_HIGH_COLOR = '#326C81'

    def __init__(self, data, show_legend=True, flip=True, threshold=None):
        """
        Parameters
        ----------
        data : dict or `DataFrame`
            Correlation will be calculated for each variable pair.
        show_legend : bool, default=True
            If True legend is shown.
        flip : bool, default=True
            If True the y axis is flipped.
        threshold : float, default=0.0
            Minimal correlation abs value to be included in result.
            Must be in interval [0.0, 1.0].

        """

        self._data = data
        self._show_legend = show_legend
        self._format = '.2f'
        self._reverse_y = flip if flip else False
        self.threshold = threshold
        self._color_scale = None
        self._fill_scale = None
        self._points_params = None
        self._tiles_params = None
        self._labels_params = None
        self._labels_map_size = None
        self.palette_gradient(low=corr_plot._DEF_LOW_COLOR,
                              mid=corr_plot._DEF_MID_COLOR,
                              high=corr_plot._DEF_HIGH_COLOR)

    def points(self, type=None, diag=None):
        """
        Method defines correlation matrix layer drawn by points to the plot.

        Parameters
        ----------
        type : {'upper', 'lower', 'full'}
            Type of matrix. Default - contextual.
        diag : bool
            Determines whether to fill the main diagonal with values or not.
            Default - contextual.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """
        self._points_params = {'type': type, 'diag': diag, 'threshold': self.threshold}
        return self

    def labels(self, type=None, diag=None, map_size=None, color=None):
        """
        Method defines correlation matrix layer drawn with geom_text to the plot.

        Parameters
        ----------
        type : {'upper', 'lower', 'full'}
            Type of matrix. Default - contextual.
        diag : bool
            Determines whether to fill the main diagonal with values or not.
            Default - contextual.
        map_size : bool
            If True, then absolute value of correlation is mapped to text size.
            If False - the text size is constant. Default - contextual.
        color : str
            Set text color. Default - contextual.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """

        self._labels_params = {'type': type, 'diag': diag, 'color': color, 'threshold': self.threshold}
        self._labels_map_size = map_size
        return self

    def tiles(self, type=None, diag=None):
        """
        Method defines correlation matrix layer drawn as square tiles to the plot.

        Parameters
        ----------
        type : {'upper', 'lower', 'full'}
            Type of matrix. Default - contextual.
        diag : bool
            Determines whether to fill the main diagonal with values or not.
            Default - contextual.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """

        self._tiles_params = {'type': type, 'diag': diag, 'threshold': self.threshold}
        return self

    def build(self) -> PlotSpec:
        """
        This method creates PlotSpec object.

        Returns
        -------
        `PlotSpec`
            Plot specification.
        """

        tiles_params = self._tiles_params.copy() if self._tiles_params is not None else None
        points_params = self._points_params.copy() if self._points_params is not None else None
        labels_params = self._labels_params.copy() if self._labels_params is not None else None

        plot = _BuildUtil.create_plot(data=self._data,
                                      tiles_params=tiles_params,
                                      points_params=points_params,
                                      labels_params=labels_params,
                                      reverse_y=self._reverse_y,
                                      show_legend=self._show_legend,
                                      labels_map_size=self._labels_map_size,
                                      corr_value_format=self._format,
                                      threshold=self.threshold)
        return _BuildUtil.add_common_params(plot=plot,
                                            has_tiles=tiles_params is not None,
                                            color_scale=self._color_scale,
                                            fill_scale=self._fill_scale,
                                            reverse_y=self._reverse_y,
                                            show_legend=self._show_legend,
                                            numeric_columns_count=self._get_numeric_columns_count())

    def palette_gradient(self, low, mid, high):
        """
        Set `scale_color_gradient2()` and `scale_fill_gradient()` for corr plot.

        Parameters
        ----------
        low : str
            Color for low end of gradient (correlation -1).
        mid : str
            Color for mid point (correlation 0).
        high : str
            Color for high end of gradient (correlation 1).

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """
        self._color_scale = scale_color_gradient2(name=corr_plot._LEGEND_NAME,
                                                  low=low, mid=mid, high=high,
                                                  breaks=corr_plot._BREAKS,
                                                  limits=corr_plot._LIMITS,
                                                  na_value='rgba(0,0,0,0)')

        self._fill_scale = scale_fill_gradient2(name=corr_plot._LEGEND_NAME,
                                                low=low, mid=mid, high=high,
                                                breaks=corr_plot._BREAKS,
                                                limits=corr_plot._LIMITS,
                                                na_value='rgba(0,0,0,0)')

        return self

    def palette_BrBG(self):
        """
        Set `scale_color_brewer()` with BrBG palette for corr plot.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """
        return self._set_brewer_palette('BrBG')

    def palette_PiYG(self):
        """
        Set `scale_color_brewer()` with PiYG palette for corr plot.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """
        return self._set_brewer_palette('PiYG')

    def palette_PRGn(self):
        """
        Set `scale_color_brewer()` with PRGn palette for corr plot.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """
        return self._set_brewer_palette('PRGn')

    def palette_PuOr(self):
        """
        Set `scale_color_brewer()` with PuOr palette for corr plot.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """
        return self._set_brewer_palette('PuOr')

    def palette_RdBu(self):
        """
        Set `scale_color_brewer()` with RdBu palette for corr plot.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """
        return self._set_brewer_palette('RdBu')

    def palette_RdGy(self):
        """
        Set `scale_color_brewer()` with RdGy palette for corr plot.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """
        return self._set_brewer_palette('RdGy')

    def palette_RdYlBu(self):
        """
        Set `scale_color_brewer()` with RdYlBu palette for corr plot.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """
        return self._set_brewer_palette('RdYlBu')

    def palette_RdYlGn(self):
        """
        Set `scale_color_brewer()` with RdYlGn palette for corr plot.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """
        return self._set_brewer_palette('RdYlGn')

    def palette_Spectral(self):
        """
        Set `scale_color_brewer()` with Spectral palette for corr plot.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
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

    def _set_brewer_palette(self, palette):
        self._color_scale = scale_color_brewer(name=corr_plot._LEGEND_NAME,
                                               palette=palette,
                                               breaks=corr_plot._BREAKS,
                                               labels=corr_plot._LABELS,
                                               limits=corr_plot._LIMITS,
                                               na_value='rgba(0,0,0,0)')

        self._fill_scale = scale_fill_brewer(name=corr_plot._LEGEND_NAME,
                                             palette=palette,
                                             breaks=corr_plot._BREAKS,
                                             labels=corr_plot._LABELS,
                                             limits=corr_plot._LIMITS,
                                             na_value='rgba(0,0,0,0)')

        return self


class _BuildUtil:
    @classmethod
    def create_plot(cls, *,
                    data,
                    tiles_params: Dict,
                    points_params: Dict,
                    labels_params: Dict,
                    reverse_y: bool,
                    show_legend: bool,
                    labels_map_size: bool,
                    corr_value_format: str,
                    threshold
                    ) -> PlotSpec:

        # Adjust options
        labels_map_size = _OpUtil.adjust_type_color_size(tiles_params, points_params, labels_params, labels_map_size)
        _OpUtil.adjust_diag(tiles_params, points_params, labels_params, threshold)
        _OpUtil.flip_type(tiles_params, points_params, labels_params, reverse_y)

        tooltips = (layer_tooltips()
                    .format(field='@..corr..', format=corr_value_format)
                    .line('@..corr..'))

        plot = ggplot(data)

        if tiles_params is not None:
            plot += geom_tile(stat='corr',
                              show_legend=show_legend,
                              size=0.0, width=1.002, height=1.002,
                              tooltips=tooltips,
                              sampling='none',
                              **tiles_params)
            plot += coord_cartesian()
        else:
            plot += coord_fixed()

        if points_params is not None:
            plot += geom_point(stat='corr',
                               show_legend=show_legend,
                               size_unit='x',
                               mapping=aes(size='..corr_abs..'),
                               tooltips=tooltips,
                               sampling='none',
                               **points_params)

        if labels_params is not None:
            m = None
            if labels_map_size:
                m = aes(size='..corr_abs..')
            else:
                labels_params['size'] = 1

            plot += geom_text(stat='corr',
                              show_legend=show_legend,
                              mapping=m,
                              na_text='',
                              label_format=corr_value_format,
                              tooltips=tooltips,
                              size_unit='x',
                              sampling='none',
                              **labels_params)

        return plot

    @classmethod
    def add_common_params(cls, *,
                          plot: PlotSpec,
                          has_tiles: bool,
                          color_scale,
                          fill_scale,
                          reverse_y,
                          show_legend,
                          numeric_columns_count) -> PlotSpec:
        _COLUMN_WIDTH = 60
        _MIN_PLOT_WIDTH = 400
        _MAX_PLOT_WIDTH = 900
        _PLOT_PROPORTION = 3.0 / 4.0

        scale_xy_expand = None
        if has_tiles:
            scale_xy_expand = [0, 0.1]  # Smaller 'additive' expand for tiles (normally: 0.6)

        plot += theme(axis_title=element_blank(),
                      legend_title=element_blank(),
                      axis_line_x=element_blank(),
                      axis_line_y=element_blank())

        plot += scale_size_identity(name="", na_value=0)

        plot += color_scale
        plot += fill_scale

        plot += scale_x_discrete(expand=scale_xy_expand)
        plot += scale_y_discrete(expand=scale_xy_expand, reverse=reverse_y)

        columns_count = numeric_columns_count
        width = min(_MAX_PLOT_WIDTH, max(_MIN_PLOT_WIDTH, columns_count * _COLUMN_WIDTH))
        height = width

        if show_legend:
            height *= _PLOT_PROPORTION

        plot += ggsize(width, height)

        return plot


class _OpUtil:
    @classmethod
    def flip(cls, type):
        if type == 'upper':
            return 'lower'
        elif type == 'lower':
            return 'upper'
        return type

    @classmethod
    def overlap(cls, type0, type1) -> bool:
        if type0 is None or type1 is None:
            return False
        if type0 == 'full' or type1 == 'full':
            return True

        return type0 == type1

    @classmethod
    def adjust_type_color_size(cls, tiles_params: dict, points_params: dict, labels_params: dict,
                               labels_map_size: bool) -> bool:
        """
        Returns
        -------
            bool - Updated 'labels_map_size' value.

        """
        has_tiles = tiles_params is not None
        has_points = points_params is not None
        has_labels = labels_params is not None

        tiles_type = tiles_params.get('type') if has_tiles else None
        points_type = points_params.get('type') if has_points else None
        labels_type = labels_params.get('type') if has_labels else None

        if has_tiles and has_points:
            # avoid showing tiles and points in the same cells
            if (tiles_type is None and points_type is None):
                tiles_type = "lower"
                points_type = "upper"
            elif tiles_type is None:
                if points_type == 'lower':
                    tiles_type = "upper"
                elif points_type in ['upper', 'full']:
                    tiles_type = "lower"
            elif points_type is None:
                points_type = cls.flip(tiles_type)

        if has_labels and labels_type is None and labels_params.get('color') is None:
            # avoid labels without 'color' showing on top of tiles or points.
            if has_points:
                if points_type is None and labels_type is None:
                    labels_type = "lower"
                    points_type = "upper"
                elif points_type is None:
                    points_type = cls.flip(labels_type)
                else:
                    labels_type = cls.flip(points_type)
            if has_tiles:
                if tiles_type is None and labels_type is None:
                    tiles_type = "lower"
                    labels_type = "upper"
                elif tiles_type is None:
                    tiles_type = cls.flip(labels_type)
                else:
                    labels_type = cls.flip(tiles_type)

        # Set labels color if labels are over points or tiles.
        if has_labels and labels_params.get('color') is None:
            if has_tiles:
                if cls.overlap(labels_type if labels_type is not None else 'full',
                               tiles_type if tiles_type is not None else 'full'):
                    labels_params['color'] = "white"
            if has_points:
                if cls.overlap(labels_type if labels_type is not None else 'full',
                               points_type if points_type is not None else 'full'):
                    labels_params['color'] = "white"

        # Map labels size if labels are over points.
        if has_points and has_labels and labels_map_size is None:
            if cls.overlap(labels_type if labels_type is not None else 'full',
                           points_type if points_type is not None else 'full'):
                labels_map_size = True

        # Update tiles and points parameters.
        if has_tiles:
            tiles_params['type'] = tiles_type

        if has_points:
            points_params['type'] = points_type

        if has_labels:
            labels_params['type'] = labels_type

        return labels_map_size

    @classmethod
    def adjust_diag(cls, tiles_params: dict, points_params: dict, labels_params: dict, threshold) -> None:
        # Prefer not to fill diagonal when the type is 'lower' or 'upper'.
        def _adjust(diag: bool, type):
            if diag is None:
                if type in ['lower', 'upper']:
                    diag = False
            return diag

        tiles_diag = None
        points_diag = None
        labels_diag = None
        if tiles_params is not None:
            tiles_diag = _adjust(tiles_params.get('diag'), tiles_params.get('type'))
        if points_params is not None:
            points_diag = _adjust(points_params.get('diag'), points_params.get('type'))
        if labels_params is not None:
            labels_diag = _adjust(labels_params.get('diag'), labels_params.get('type'))

        if threshold is not None and threshold > 0.0:
            # For all layers 'diag' must be the same.
            if tiles_diag or points_diag or labels_diag:
                tiles_diag = True
                points_diag = True
                labels_diag = True
            else:
                tiles_diag = False
                points_diag = False
                labels_diag = False

        if tiles_params is not None:
            tiles_params['diag'] = tiles_diag
        if points_params is not None:
            points_params['diag'] = points_diag
        if labels_params is not None:
            labels_params['diag'] = labels_diag

    @classmethod
    def flip_type(cls, tiles_params: dict, points_params: dict, labels_params: dict, flip: bool) -> None:
        if flip:
            if tiles_params is not None:
                tiles_params['type'] = cls.flip(tiles_params.get('type'))
            if points_params is not None:
                points_params['type'] = cls.flip(points_params.get('type'))
            if labels_params is not None:
                labels_params['type'] = cls.flip(labels_params.get('type'))
