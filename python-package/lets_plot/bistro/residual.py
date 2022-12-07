#  Copyright (c) 2022. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

try:
    import numpy as np
except ImportError:
    np = None

from lets_plot.plot.core import PlotSpec, aes
from lets_plot.plot.geom import *
from lets_plot.plot.label import ylab
from lets_plot.plot.marginal_layer import ggmarginal
from lets_plot.plot.theme_ import *

__all__ = ['residual_plot']


METHOD_DEF = "lm"
METHOD_LM_DEG_DEF = 1
METHOD_LOESS_SPAN_DEF = .5
GEOM_DEF = "point"
BINS_DEF = 30
MARGINAL_DEF = "dens:r"
COLOR_DEF = "#118ed8"
HLINE_DEF = True


def _extract_data_series(data, x, y):
    if np is None:
        raise ValueError("Module 'numpy' is required")

    xs = np.array(data[x])
    ys = np.array(data[y])
    if xs.size != ys.size:
        raise Exception("All data series in data frame must have equal size "
                        "{x_col} : {x_len} {y_col} : {y_len}".format(
            x_col=x,
            y_col=y,
            x_len=xs.size,
            y_len=ys.size
        ))
    if xs.size < 2:
        raise Exception("Data should have at least two points.")

    return xs, ys

def _poly_transform(deg):
    if np is None:
        raise ValueError("Module 'numpy' is required")

    def _transform(X):
        assert len(X.shape) > 1 and X.shape[1] == 1
        return np.concatenate([np.power(X, d) for d in range(deg + 1)], axis=1).astype(float)

    return _transform

def _get_lm_predictor(xs_train, ys_train, deg):
    try:
        import statsmodels.api as sm
    except ImportError:
        raise ValueError("Module 'statsmodels' is required for 'lm' method")

    X_train = xs_train.reshape(-1, 1)
    transform = _poly_transform(deg)
    model = sm.OLS(ys_train, transform(X_train)).fit()

    return lambda xs: model.predict(transform(xs.reshape(-1, 1)))

def _get_loess_predictor(xs_train, ys_train, span, seed, max_n):
    if np is None:
        raise ValueError("Module 'numpy' is required")

    try:
        import statsmodels.api as sm
    except ImportError:
        raise ValueError("Module 'statsmodels' is required for 'loess' method")

    try:
        from scipy.interpolate import interp1d
    except ImportError:
        raise ValueError("Module 'scipy' is required for 'loess' method")

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
    if np is None:
        raise ValueError("Module 'numpy' is required")

    if method == 'lm':
        return _get_lm_predictor(xs_train, ys_train, deg)
    if method in ['loess', 'lowess']:
        return _get_loess_predictor(xs_train, ys_train, span, seed, max_n)
    if method == 'none':
        return lambda xs: np.array([0] * xs.size)
    else:
        raise Exception("Unknown method '{0}'".format(method))

def _get_binwidth(xs, ys, binwidth, bins):
    if binwidth is not None or bins is not None:
        return binwidth
    binwidth_x = (xs.max() - xs.min()) / BINS_DEF
    binwidth_y = (ys.max() - ys.min()) / BINS_DEF
    binwidth_max = max(binwidth_x, binwidth_y)

    return [binwidth_max, binwidth_max]

def _parse_marginal(marginal, color, color_by, show_legend, bins2d, binwidth2d):
    def _parse_marginal_layer(geom_name, side, size):
        layer = None
        if geom_name in ["dens", "density"]:
            layer = geom_density(color=color, show_legend=show_legend)
        elif geom_name in ["hist", "histogram"]:
            bins = None if bins2d is None else (bins2d[0] if side in ["t", "b"] else bins2d[1])
            binwidth = None if binwidth2d is None else (binwidth2d[0] if side in ["t", "b"] else binwidth2d[1])
            marginal_color = None if color_by is not None else (color or COLOR_DEF)
            layer = geom_histogram(color=marginal_color, alpha=0, bins=bins, binwidth=binwidth, show_legend=show_legend)
        elif geom_name in ["box", "boxplot"]:
            layer = geom_boxplot(color=color, show_legend=show_legend)
        else:
            raise Exception("Unknown geom '{0}'".format(self.geom))

        return ggmarginal(side, size=size, layer=layer)

    marginals = []
    for layer_description in filter(bool, marginal.split(",")):
        params = layer_description.strip().split(":")
        geom_name, sides = params[0], params[1]
        size = float(params[2]) if len(params) > 2 else None
        for side in sides:
            marginals.append(_parse_marginal_layer(geom_name, side, size))

    return marginals


def residual_plot(data=None, x=None, y=None, *,
                  method=METHOD_DEF,
                  deg=METHOD_LM_DEG_DEF,
                  span=METHOD_LOESS_SPAN_DEF, seed=None, max_n=None,
                  geom=GEOM_DEF,
                  bins=None, binwidth=None,
                  color=None, size=None, alpha=None,
                  color_by=None,
                  show_legend=None,
                  hline=HLINE_DEF, marginal=MARGINAL_DEF):
    """
    Produces a residual plot that shows the difference between the observed response and the fitted response values.

    Parameters
    ----------
    data : dict or `DataFrame`
        The data to be displayed.
    x : str
        Name of independent variable.
    x : str
        Name of dependent variable that will be fitted.
    method : {'lm', 'loess', 'lowess', 'none'}, default='lm'
        Fitting method: 'lm' (Linear Model) or 'loess'/'lowess' (Locally Estimated Scatterplot Smoothing).
        If value of `deg` parameter is greater than 1 then linear model becomes polynomial of the given degree.
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
    geom : {'point', 'tile', 'none'}, default='point'
        The geometric object to use to display the data. No object will be used if `geom='none'`.
    bins : int or list of int
        Number of bins in both directions, vertical and horizontal. Overridden by `binwidth`.
        If only one value given - interpret it as list of two equal values.
        Applicable simultaneously for 'tile' geom and 'histogram' marginal.
    binwidth : float or list of float
        The width of the bins in both directions, vertical and horizontal.
        Overrides `bins`. The default is to use bin widths that cover the entire range of the data.
        If only one value given - interpret it as list of two equal values.
        Applicable simultaneously for 'tile' geom and 'histogram' marginal.
    color : str
        Color of a geometry.
    size : float
        Size of a geometry.
    alpha : float
        Transparency level of a geometry. Accepts values between 0 and 1.
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
        To suppress marginals use `marginal='none'`.
        Examples:
        "hist:tr:0.3",
        "dens:tr,hist:bl",
        "box:tr:.05, hist:bl, dens:bl".

    Returns
    -------
    `PlotSpec`
        Plot object specification.

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
        :emphasize-lines: 11

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
            geom_point(shape=21, color=color, fill=fill) + \\
            ggmarginal('r', layer=geom_area(stat='density', color=color, fill=fill))

    """
    # prepare residuals
    residual_data = data.copy()
    xs, ys = _extract_data_series(residual_data, x, y)
    residual_col = "..residual.."
    predictor = _get_predictor(xs, ys, method, deg, span, seed, max_n)
    residual_data[residual_col] = ys - predictor(xs)
    # prepare parameters
    if isinstance(bins, int):
        bins = [bins, bins]
    if isinstance(binwidth, int) or isinstance(binwidth, float):
        binwidth = [binwidth, binwidth]
    binwidth = _get_binwidth(xs, ys, binwidth, bins)
    # prepare mapping
    mapping_dict = {'x': x, 'y': residual_col}
    if color_by is not None:
        mapping_dict['color'] = color_by
    # prepare scales
    scales = []
    if method == 'none':
        scales.append(ylab(y))
    else:
        scales.append(ylab("{0} residual".format(y)))
    # prepare layers
    layers = []
    # main layer
    if geom == 'point':
        layers.append(geom_point(color=color, size=size, alpha=alpha, show_legend=show_legend))
    elif geom == 'tile':
        layers.append(geom_bin2d(
            bins=bins, binwidth=binwidth,
            color=color, size=size, alpha=alpha,
            show_legend=show_legend
        ))
    elif geom == 'none':
        pass
    else:
        raise Exception("Unknown geom '{0}'".format(geom))
    # hline layer
    if hline:
        layers.append(geom_hline(yintercept=0, color="magenta", linetype='dashed'))
    # marginal layers
    if marginal != 'none':
        layers += _parse_marginal(marginal, color, color_by, show_legend, bins, binwidth)
    # theme layer
    theme_layer = theme(axis="blank",
                        axis_text_x=element_text(),
                        axis_title_x=element_text(),
                        axis_line_y=element_line(),
                        axis_ticks_y=element_line(),
                        axis_text_y=element_text(),
                        axis_title_y=element_text())

    return PlotSpec(data=residual_data, mapping=aes(**mapping_dict), scales=scales, layers=layers) + theme_layer