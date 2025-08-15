#
#  Copyright (c) 2022. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
try:
    import numpy as np
except ImportError:
    np = None

try:
    import pandas as pd
except ImportError:
    pd = None

try:
    import polars as pl
except ImportError:
    pl = None

from ._plot2d_common import _get_bin_params_2d, _get_geom2d_layer, _get_marginal_layers
from ..plot.core import DummySpec, aes
from ..plot.geom import geom_hline
from ..plot.label import ylab
from ..plot.plot import ggplot
from ..plot.theme_ import *

__all__ = ['residual_plot']

_METHOD_DEF = 'lm'
_METHOD_LM_DEG_DEF = 1
_METHOD_LOESS_SPAN_DEF = .5
_GEOM_DEF = 'point'
_MARGINAL_DEF = "dens:r"
_HLINE_DEF = True

_HLINE_COLOR = "magenta"
_HLINE_LINETYPE = 'dashed'
_RESIDUAL_COL = "..residual.."


def _extract_data_series(df, x, y):
    xs = np.array(df[x])
    ys = np.array(df[y])
    if xs.size != ys.size:
        raise Exception("All data series in dataset must have equal size "
                        "{x_col} : {x_len} {y_col} : {y_len}".format(
            x_col=x,
            y_col=y,
            x_len=xs.size,
            y_len=ys.size
        ))
    if xs.size == 1:
        raise Exception("Data should have at least two points.")

    return xs, ys


def _poly_transform(deg):
    def _transform(X):
        assert len(X.shape) > 1 and X.shape[1] == 1
        return np.concatenate([np.power(X, d) for d in range(deg + 1)], axis=1).astype(float)

    return _transform


def _get_lm_predictor(xs_train, ys_train, deg):
    import statsmodels.api as sm

    X_train = xs_train.reshape(-1, 1)
    transform = _poly_transform(deg)
    model = sm.OLS(ys_train, transform(X_train)).fit()

    return lambda xs: model.predict(transform(xs.reshape(-1, 1)))


def _get_loess_predictor(xs_train, ys_train, span, seed, max_n):
    import statsmodels.api as sm
    from scipy.interpolate import interp1d

    if max_n is not None:
        np.random.seed(seed)
        indices = np.random.choice(range(xs_train.size), size=max_n, replace=False)
        xs_train = xs_train[indices]
        ys_train = ys_train[indices]
    lowess = sm.nonparametric.lowess(ys_train, xs_train, frac=span)
    lowess_x = list(zip(*lowess))[0]
    lowess_y = list(zip(*lowess))[1]
    model = interp1d(lowess_x, lowess_y, bounds_error=False)

    return lambda xs: np.array([model(x) for x in xs])


def _get_predictor(xs_train, ys_train, method, deg, span, seed, max_n):
    if method == 'lm':
        return _get_lm_predictor(xs_train, ys_train, deg)
    if method in ['loess', 'lowess']:
        return _get_loess_predictor(xs_train, ys_train, span, seed, max_n)
    if method == 'none':
        return lambda xs: np.array([0] * xs.size)
    else:
        raise Exception("Unknown method '{0}'".format(method))


def _get_stat_data(data, x, y, group_by, method, deg, span, seed, max_n):
    def _get_group_stat_data(group_df):
        xs, ys = _extract_data_series(group_df, x, y)
        if len(xs) == 0:
            return group_df.assign(**{_RESIDUAL_COL: []}), xs, ys
        predictor = _get_predictor(xs, ys, method, deg, span, seed, max_n)
        return group_df.assign(**{_RESIDUAL_COL: ys - predictor(xs)}), xs, ys

    if isinstance(data, dict):
        df = pd.DataFrame(data)
    elif isinstance(data, pd.DataFrame):
        df = data.copy()
    elif pl is not None and isinstance(data, pl.DataFrame):
        df = pd.DataFrame(data.to_dict(as_series=False))
    else:
        raise Exception("Unsupported type of data: {0}".format(data))
    df = df[(df[x].notna()) & df[y].notna()]
    if group_by is None:
        return _get_group_stat_data(df)
    else:
        df_list, xs_list, ys_list = zip(*[
            _get_group_stat_data(df[df[group_by] == group_value])
            for group_value in df[group_by].unique()
        ])
        return pd.concat(df_list), np.concatenate(xs_list), np.concatenate(ys_list)


def residual_plot(data=None, x=None, y=None, *,
                  method=_METHOD_DEF,
                  deg=_METHOD_LM_DEG_DEF,
                  span=_METHOD_LOESS_SPAN_DEF, seed=None, max_n=None,
                  geom=_GEOM_DEF,
                  bins=None, binwidth=None,
                  color=None, size=None, alpha=None,
                  color_by=None,
                  show_legend=None,
                  hline=_HLINE_DEF, marginal=_MARGINAL_DEF):
    """
    Produce a residual plot that shows the difference between the observed response and the fitted response values.

    To use ``residual_plot()``, the `numpy` and `pandas` libraries are required.
    Also, `statsmodels` and `scipy` are required for 'lm' and 'loess' methods.

    Parameters
    ----------
    data : dict or Pandas or Polars ``DataFrame``
        The data to be displayed.
    x : str
        Name of independent variable.
    y : str
        Name of dependent variable that will be fitted.
    method : {'lm', 'loess', 'lowess', 'none'}, default='lm'
        Fitting method: 'lm' (Linear Model) or 'loess'/'lowess' (Locally Estimated Scatterplot Smoothing).
        If value of ``deg`` parameter is greater than 1 then linear model becomes polynomial of the given degree.
        If method is 'none' then data lives as is.
    deg : int, default=1
        Degree of polynomial for linear regression model.
    span : float, default=0.5
        Only for 'loess' method. The fraction of source points closest to the current point is taken into account
        for computing a least-squares regression. A sensible value is usually 0.25 to 0.5.
    seed : int
        Random seed for 'loess' sampling.
    max_n : int
        Maximum number of data-points for 'loess' method. If this quantity exceeded random sampling is applied to data.
    geom : {'point', 'tile', 'hex', 'density2d', 'density2df', 'none'}, default='point'
        The geometric object to use to display the data. No object will be used if ``geom='none'``.
    bins : int or list of int
        Number of bins in both directions, vertical and horizontal. Overridden by ``binwidth``.
        If only one value given - interpret it as list of two equal values.
        Applicable simultaneously for 'tile'/'hex' geom and 'histogram' marginal.
    binwidth : float or list of float
        The width of the bins in both directions, vertical and horizontal.
        Overrides ``bins``. The default is to use bin widths that cover the entire range of the data.
        If only one value given - interpret it as list of two equal values.
        Applicable simultaneously for 'tile'/'hex' geom and 'histogram' marginal.
    color : str
        Color of the geometry.
        For more info see `Color and Fill <https://lets-plot.org/python/pages/aesthetics.html#color-and-fill>`__.
    size : float
        Size of the geometry.
    alpha : float
        Transparency level of the geometry. Accept values between 0 and 1.
    color_by : str
        Name of grouping variable.
    show_legend : bool, default=True
        False - do not show legend for the main layer.
    hline : bool, default=True
        False - do not show horizontal line passing through 0.
    marginal : str, default='dens:r'
        Description of marginal layers packed to string value.
        Different marginals are separated by the ',' char.
        Parameters of a marginal are separated by the ':' char.
        First parameter of a marginal is a geometry name.
        Possible values: 'dens'/'density', 'hist'/'histogram', 'box'/'boxplot'.
        Second parameter is a string specifying which sides of the plot the marginal layer will appear on.
        Possible values: 't' (top), 'b' (bottom), 'l' (left), 'r' (right).
        Third parameter (optional) is size of marginal.
        To suppress marginals use ``marginal='none'``.
        Examples:
        "hist:tr:0.3",
        "dens:tr,hist:bl",
        "box:tr:.05, hist:bl, dens:bl".

    Returns
    -------
    ``PlotSpec``
        Plot object specification.

    Notes
    -----
    When using 'lm' and 'loess' methods,
    this function requires the `statsmodels` and `scipy` libraries to be installed.

    ----

    To hide axis tooltips, set 'blank' or the result of `element_blank() <https://lets-plot.org/python/pages/api/lets_plot.element_blank.html>`__
    to the ``axis_tooltip``, ``axis_tooltip_x`` or ``axis_tooltip_y`` parameter of the `theme() <https://lets-plot.org/python/pages/api/lets_plot.theme.html>`__.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        import numpy as np
        from lets_plot import *
        from lets_plot.bistro.residual import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        data = {
            'x': np.random.uniform(size=n),
            'y': np.random.normal(size=n)
        }
        residual_plot(data, 'x', 'y')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-11

        import numpy as np
        from lets_plot import *
        from lets_plot.bistro.residual import *
        LetsPlot.setup_html()
        n, m = 1000, 5
        np.random.seed(42)
        x = np.random.uniform(low=-m, high=m, size=n)
        y = x**2 + np.random.normal(size=n)
        residual_plot({'x': x, 'y': y}, 'x', 'y', \\
                      deg=2, geom='tile', binwidth=[1, .5], \\
                      hline=False, marginal="hist:tr")

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10-13

        import numpy as np
        from lets_plot import *
        from lets_plot.bistro.residual import *
        LetsPlot.setup_html()
        n = 200
        np.random.seed(42)
        x = np.random.uniform(size=n)
        y = x * np.random.normal(size=n)
        g = np.random.choice(['A', 'B'], size=n)
        residual_plot({'x': x, 'y': y, 'g': g}, 'x', 'y', \\
                      method='none', bins=[30, 15], \\
                      size=5, alpha=.5, color_by='g', show_legend=False, \\
                      marginal="hist:t:.2, hist:r, dens:tr, box:bl:.05")

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12

        import numpy as np
        from lets_plot import *
        from lets_plot.bistro.residual import *
        LetsPlot.setup_html()
        n = 100
        color, fill = "#bd0026", "#ffffb2"
        np.random.seed(42)
        data = {
            'x': np.random.uniform(size=n),
            'y': np.random.normal(size=n)
        }
        residual_plot(data, 'x', 'y', geom='none', hline=False, marginal='none') + \\
            geom_hline(yintercept=0, size=1, color=color) + \\
            geom_point(shape=21, size=3, color=color, fill=fill) + \\
            ggmarginal('r', layer=geom_area(stat='density', color=color, fill=fill))

    """
    # requirements
    if np is None:
        raise ValueError("Module 'numpy' is required for residual plot")
    if pd is None:
        raise ValueError("Module 'pandas' is required for residual plot")
    # prepare data
    stat_data, xs, ys = _get_stat_data(data, x, y, color_by, method, deg, span, seed, max_n)
    # prepare parameters
    binwidth2d, bins2d = _get_bin_params_2d(xs, ys, binwidth, bins)
    # prepare mapping
    mapping_dict = {'x': x, 'y': _RESIDUAL_COL}
    if color_by is not None:
        mapping_dict['color'] = color_by
        mapping_dict['fill'] = color_by
    # prepare scales
    if method == 'none':
        scales = ylab(y)
    else:
        scales = ylab("{0} residual".format(y))
    # prepare layers
    layers = DummySpec()
    # main layer
    main_layer = _get_geom2d_layer(geom, binwidth2d, bins2d, color, color_by, size, alpha, show_legend)
    if main_layer is not None:
        layers += main_layer
    # hline layer
    if hline:
        layers += geom_hline(yintercept=0, color=_HLINE_COLOR, linetype=_HLINE_LINETYPE)
    # marginal layers
    if marginal != 'none':
        layers += _get_marginal_layers(marginal, binwidth2d, bins2d, color, color_by, show_legend)
    # theme layer
    theme_layer = theme(axis="blank",
                        axis_text_x=element_text(),
                        axis_title_x=element_text(),
                        axis_line_y=element_line(),
                        axis_ticks_y=element_line(),
                        axis_text_y=element_text(),
                        axis_title_y=element_text())

    return ggplot(stat_data, aes(**mapping_dict)) + layers + scales + theme_layer
