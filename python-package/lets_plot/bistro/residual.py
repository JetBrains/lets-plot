#  Copyright (c) 2022. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import pkg_resources
installed_packages_names = [pkg.key for pkg in pkg_resources.working_set]
for pkg_name in ['numpy', 'statsmodels', 'scipy']:
    if pkg_name not in installed_packages_names:
        raise Exception("To use this module you need to install '{0}' package".format(pkg_name))

import numpy as np

from lets_plot.plot.core import PlotSpec, aes
from lets_plot.plot.geom import *
from lets_plot.plot.label import ylab
from lets_plot.plot.marginal_layer import ggmarginal
from lets_plot.plot.theme_ import *

__all__ = ['residual_plot']


METHOD_DEF = "lm"
METHOD_LM_DEG_DEF = 1
METHOD_LOESS_SPAN_DEF = .5
METHOD_LOESS_SEED_DEF = 42
GEOM_DEF = "point"
BINS_DEF = 30
MARGINAL_DEF = "dens:r"
COLOR_DEF = "#118ed8"


def _extract_data_series(data, x, y):
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
    def _transform(X):
        assert len(X.shape) > 1 and X.shape[1] == 1
        return np.concatenate([np.power(X, d) for d in range(deg + 1)], axis=1).astype(float)

    return _transform

def _get_predictor(xs_train, ys_train, method, deg, span, seed, max_n):
    import statsmodels.api as sm
    from scipy.interpolate import interp1d

    if method == 'lm':
        X_train = xs_train.reshape(-1, 1)
        transform = _poly_transform(deg)
        model = sm.OLS(ys_train, transform(X_train)).fit()
        return lambda xs: model.predict(transform(xs.reshape(-1, 1)))
    if method in ['loess', 'lowess']:
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
    if method == 'none':
        return lambda xs: np.array([0] * xs.size)
    else:
        raise Exception("Unknown method '{0}'".format(method))

def _get_binwidth(xs, ys, binwidth, bins):
    if binwidth != None or bins != None:
        return binwidth
    binwidth_x = (xs.max() - xs.min()) / BINS_DEF
    binwidth_y = (ys.max() - ys.min()) / BINS_DEF
    binwidth_max = max(binwidth_x, binwidth_y)

    return [binwidth_max, binwidth_max]

def _parse_marginal(marginal, color, bins2d, binwidth2d):
    def _parse_marginal_layer(geom_name, side, size):
        layer = None
        if geom_name in ["dens", "density"]:
            layer = geom_density(color=color)
        elif geom_name in ["hist", "histogram"]:
            bins = None if bins2d is None else (bins2d[0] if side in ["t", "b"] else bins2d[1])
            binwidth = None if binwidth2d is None else (binwidth2d[0] if side in ["t", "b"] else binwidth2d[1])
            layer = geom_histogram(color=color or COLOR_DEF, alpha=0, bins=bins, binwidth=binwidth)
        elif geom_name in ["box", "boxplot"]:
            layer = geom_boxplot(color=color)
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
                  span=METHOD_LOESS_SPAN_DEF, seed=METHOD_LOESS_SEED_DEF, max_n=None,
                  geom=GEOM_DEF,
                  bins=None, binwidth=None,
                  color=None, size=None, alpha=None,
                  color_by=None,
                  show_legend=None,
                  hline=True, marginal=MARGINAL_DEF):
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
    if color_by != None:
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
    elif geom == 'blank':
        pass
    else:
        raise Exception("Unknown geom '{0}'".format(geom))
    # hline layer
    if hline:
        layers.append(geom_hline(yintercept=0, color="magenta", linetype='dashed'))
    # marginal layers
    if marginal != 'none':
        layers += _parse_marginal(marginal, color, bins, binwidth)
    # theme layer
    theme_layer = theme(axis="blank",
                        axis_text_x=element_text(),
                        axis_title_x=element_text(),
                        axis_line_y=element_line(),
                        axis_ticks_y=element_line(),
                        axis_text_y=element_text(),
                        axis_title_y=element_text())

    return PlotSpec(data=residual_data, mapping=aes(**mapping_dict), scales=scales, layers=layers) + theme_layer