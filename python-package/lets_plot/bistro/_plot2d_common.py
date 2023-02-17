#
#  Copyright (c) 2023. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from ..plot.core import aes
from ..plot.geom import *
from ..plot.marginal_layer import ggmarginal

_BINS_DEF = 30
_COLOR_DEF = "#118ed8"

_MARGINAL_ALPHA = .1


def _get_bin_params_2d(xs, ys, binwidth, bins):
    if isinstance(bins, int):
        bins = [bins, bins]
    if isinstance(binwidth, int) or isinstance(binwidth, float):
        binwidth = [binwidth, binwidth]
    if binwidth is not None or bins is not None or len(xs) == 0:
        return binwidth, bins
    binwidth_x = (max(xs) - min(xs)) / _BINS_DEF
    binwidth_y = (max(ys) - min(ys)) / _BINS_DEF
    binwidth_max = max(binwidth_x, binwidth_y)

    return [binwidth_max, binwidth_max], bins


def _get_geom2d_layer(geom_kind, binwidth2d, bins2d, color, color_by, size, alpha, show_legend):
    if geom_kind == 'point':
        return geom_point(color=color, size=size, alpha=alpha, show_legend=show_legend)
    if geom_kind == 'tile':
        return geom_bin2d(
            aes(fill=('..count..' if color_by is None else color_by)),
            bins=bins2d, binwidth=binwidth2d,
            color=color, size=size, alpha=alpha,
            show_legend=show_legend
        )
    if geom_kind == 'density2d':
        return geom_density2d(
            aes(color=('..group..' if color_by is None else color_by)),
            color=color, size=size, alpha=alpha,
            show_legend=show_legend
        )
    if geom_kind == 'density2df':
        return geom_density2df(
            aes(fill=('..group..' if color_by is None else color_by)),
            color=color, size=size, alpha=alpha,
            show_legend=show_legend
        )
    if geom_kind == 'none':
        return None
    raise Exception("Unknown geom '{0}'".format(geom_kind))


def _get_marginal_layers(marginal, binwidth2d, bins2d, color, color_by, show_legend):
    marginal_color = None if color_by is not None else (color or _COLOR_DEF)

    def bin_param_to_1d(param2d, side):
        if param2d is None:
            return None
        else:
            if side in ['t', 'b']:
                return param2d[0]
            else:
                return param2d[1]

    def _get_marginal_layer(geom_kind, side, size):
        if geom_kind in ['dens', 'density']:
            layer = geom_area(stat='density', position='identity', color=marginal_color, fill=marginal_color,
                              alpha=_MARGINAL_ALPHA, show_legend=show_legend)
        elif geom_kind in ['hist', 'histogram']:
            binwidth = bin_param_to_1d(binwidth2d, side)
            bins = bin_param_to_1d(bins2d, side)
            layer = geom_histogram(bins=bins, binwidth=binwidth,
                                   color=marginal_color, fill=marginal_color, alpha=_MARGINAL_ALPHA,
                                   show_legend=show_legend)
        elif geom_kind in ['box', 'boxplot']:
            layer = geom_boxplot(color=marginal_color, fill=marginal_color, alpha=_MARGINAL_ALPHA, show_legend=show_legend)
        else:
            raise Exception("Unknown geom '{0}'".format(geom_kind))

        return ggmarginal(side, size=size, layer=layer)

    marginals = []
    for layer_description in filter(bool, marginal.split(",")):
        params = layer_description.strip().split(":")
        geom_kind, sides = params[0].strip(), params[1].strip()
        size = float(params[2].strip()) if len(params) > 2 else None
        for side in sides:
            marginals.append(_get_marginal_layer(geom_kind, side, size))

    return marginals
