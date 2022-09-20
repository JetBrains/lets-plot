#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

"""Correlation matrix implementation module"""
from typing import Any

from lets_plot.plot.util import is_data_frame

try:
    import numpy
except ImportError:
    numpy = None

try:
    import pandas
except ImportError:
    pandas = None

from lets_plot.plot.core import PlotSpec

__all__ = ['corr_plot']


def _is_corr_matrix(data: Any):
    if is_data_frame(data):
        if data.shape[0] != data.shape[1]:
            return False

        if not (all(col_type == 'float64' for col_type in data.dtypes)):
            return False

        for column in data:
            import math
            if not all(math.isnan(v) or (1.0 >= v >= -1.0) for v in data[column]):
                return False

        return True

    elif isinstance(data, dict):
        m = len(data.keys())
        for column in data.values():
            if not isinstance(column, (list, tuple)):
                return False

            if len(column) != m:
                return False

            import math
            for v in column:
                if not isinstance(v, float):
                    return False

                if math.isnan(v):
                    return True

                if 1.0 >= v >= -1.0:
                    return True

                return False

            return True
    else:
        return False


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

    def _duplicate(self):
        dup = corr_plot(
            data=self._data,
            show_legend=self._show_legend,
            flip=self._reverse_y,
            threshold=self.threshold
        )

        dup._color_scale = self._color_scale
        dup._fill_scale = self._fill_scale
        dup._points_params = self._points_params
        dup._tiles_params = self._tiles_params
        dup._labels_params = self._labels_params
        dup._labels_map_size = self._labels_map_size
        dup._palette = self._palette
        dup._low = self._low
        dup._mid = self._mid
        dup._high = self._high

        return dup

    def __init__(self, data, show_legend=True, flip=True, threshold=None):
        """
        Parameters
        ----------
        data : dict or `DataFrame`
            Correlation matrix or data (correlation will be calculated for each variable pair).
            data will be recognized as correlation matrix if it has a square shape and all values are
            in range -1.0..+1.0 or NaN.
        show_legend : bool, default=True
            If True legend is shown.
        flip : bool, default=True
            If True the y axis is flipped.
        threshold : float, default=0.0
            Minimal correlation abs value to be included in result.
            Accepts values between 0 and 1.

        """

        self._data = data
        self._show_legend = show_legend
        self._reverse_y = flip if flip else False
        self.threshold = threshold
        self._color_scale = None
        self._fill_scale = None
        self._points_params = None
        self._tiles_params = None
        self._labels_params = None
        self._labels_map_size = None
        self._palette = None
        self._low = None
        self._mid = None
        self._high = None

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
        return self._duplicate()._set_points(type, diag)

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
        return self._duplicate()._set_labels(type, diag, map_size, color)

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
        return self._duplicate()._set_tiles(type, diag)

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
        return self._duplicate()._set_gradient_palette(low, mid, high)

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
        return self._duplicate()._set_brewer_palette('PiYG')

    def palette_PRGn(self):
        """
        Set `scale_color_brewer()` with PRGn palette for corr plot.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """
        return self._duplicate()._set_brewer_palette('PRGn')

    def palette_PuOr(self):
        """
        Set `scale_color_brewer()` with PuOr palette for corr plot.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """
        return self._duplicate()._set_brewer_palette('PuOr')

    def palette_RdBu(self):
        """
        Set `scale_color_brewer()` with RdBu palette for corr plot.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """
        return self._duplicate()._set_brewer_palette('RdBu')

    def palette_RdGy(self):
        """
        Set `scale_color_brewer()` with RdGy palette for corr plot.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """
        return self._duplicate()._set_brewer_palette('RdGy')

    def palette_RdYlBu(self):
        """
        Set `scale_color_brewer()` with RdYlBu palette for corr plot.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """
        return self._duplicate()._set_brewer_palette('RdYlBu')

    def palette_RdYlGn(self):
        """
        Set `scale_color_brewer()` with RdYlGn palette for corr plot.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """
        return self._duplicate()._set_brewer_palette('RdYlGn')

    def palette_Spectral(self):
        """
        Set `scale_color_brewer()` with Spectral palette for corr plot.

        Returns
        -------
        `corr_plot`
            Correlation plot specification.
        """
        return self._duplicate()._set_brewer_palette('Spectral')

    def _set_points(self, type=None, diag=None):
        self._points_params = {'type': type, 'diag': diag, 'threshold': self.threshold}
        return self

    def _set_labels(self, type=None, diag=None, map_size=None, color=None):
        self._labels_params = {'type': type, 'diag': diag, 'color': color, 'threshold': self.threshold}
        self._labels_map_size = map_size
        return self

    def _set_tiles(self, type=None, diag=None):
        self._tiles_params = {'type': type, 'diag': diag, 'threshold': self.threshold}
        return self

    def _set_gradient_palette(self, low, mid, high):
        self._palette = 'gradient'
        self._low = low
        self._mid = mid
        self._high = high
        return self

    def _set_brewer_palette(self, palette):
        self._palette = palette
        self._low = None
        self._mid = None
        self._high = None
        return self

    def build(self) -> PlotSpec:
        """
        This method creates PlotSpec object.

        Returns
        -------
        `PlotSpec`
            Plot specification.
        """

        if self._points_params is not None:
            point_params = {
                'type': self._points_params['type'],
                'diag': self._points_params['diag']
            }
        else:
            point_params = None

        if self._labels_params is not None:
            label_params = {
                'type': self._labels_params['type'],
                'diag': self._labels_params['diag'],
                'color': self._labels_params['color'],
                'map_size': self._labels_map_size
            }
        else:
            label_params = None

        if self._tiles_params is not None:
            tile_params = {
                'type': self._tiles_params['type'],
                'diag': self._tiles_params['diag']
            }
        else:
            tile_params = None

        data = self._data
        if _is_corr_matrix(data):
            coefficients = True
        else:
            if is_data_frame(data):
                data = data.corr()
                coefficients = True
            else:
                coefficients = False

        return PlotSpec(data=data, mapping=None, scales=[], layers=[], bistro={
            'name': 'corr',
            'coefficients': coefficients,
            'show_legend': self._show_legend,
            'flip': self._reverse_y,
            'threshold': self.threshold,
            'palette': self._palette,
            'low': self._low,
            'mid': self._mid,
            'high': self._high,
            'point_params': point_params,
            'tile_params': tile_params,
            'label_params': label_params
        })
