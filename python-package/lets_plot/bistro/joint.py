#
#  Copyright (c) 2023. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from ..plot.core import PlotSpec, aes
from ..plot.geom import geom_smooth
from ..plot.label import xlab, ylab
from ._plot2d_common import *

__all__ = ['joint_plot']

_GEOM_DEF = 'point'
_REG_LINE_DEF = True

_REG_LINE_METHOD = 'lm'
_REG_LINE_SE = False
_REG_LINE_COLOR = "magenta"
_REG_LINE_LINETYPE = 'dashed'


def _get_marginal_def(geom_kind, color_by=None):
    if geom_kind == 'density2df':
        return "area:tr"
    if geom_kind == 'density2d' or color_by is not None:
        return "dens:tr"
    return "hist:tr"


def joint_plot(data, x, y, *,
               geom=_GEOM_DEF,
               bins=None, binwidth=None,
               color=None, size=None, alpha=None,
               color_by=None,
               show_legend=None,
               reg_line=_REG_LINE_DEF,
               marginal=None):
    # prepare parameters
    binwidth2d, bins2d = _get_bin_params_2d(data[x], data[y], binwidth, bins)
    # prepare mapping
    mapping_dict = {'x': x, 'y': y}
    if color_by is not None:
        mapping_dict['color'] = color_by
    # prepare layers
    layers = []
    # main layer
    main_layer = _get_geom2d_layer(geom, binwidth2d, bins2d, color, color_by, size, alpha, show_legend)
    if main_layer is not None:
        layers.append(main_layer)
    # smooth layer
    if reg_line:
        layers.append(geom_smooth(
            aes(group=color_by),
            method=_REG_LINE_METHOD, se=_REG_LINE_SE,
            color=_REG_LINE_COLOR, linetype=_REG_LINE_LINETYPE
        ))
    # marginal layers
    if len(data[x]) == 0:
        marginal = 'none'
    defined_marginal = marginal or _get_marginal_def(geom, color_by)
    if defined_marginal != 'none':
        layers += _get_marginal_layers(defined_marginal, binwidth2d, bins2d, color, color_by, show_legend)

    return PlotSpec(data=data, mapping=aes(**mapping_dict), scales=[xlab(x), ylab(y)], layers=layers)
