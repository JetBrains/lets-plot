#
#  Copyright (c) 2023. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from ..plot.plot import ggplot
from ..plot.core import DummySpec, aes
from ..plot.geom import geom_smooth
from ..plot.label import xlab, ylab
from ._plot2d_common import _get_bin_params_2d, _get_geom2d_layer, _get_marginal_layers

__all__ = ['joint_plot']

_GEOM_DEF = 'point'

_REG_LINE_METHOD = 'lm'
_REG_LINE_COLOR = "magenta"
_REG_LINE_LINETYPE = 'dashed'


def _get_marginal_def(geom_kind, color_by=None):
    if geom_kind in ['density2d', 'density2df'] or color_by is not None:
        return "dens:tr"
    else:
        return "hist:tr"


def _is_reg_line_needed(reg_line, geom_kind):
    if reg_line is not None:
        return reg_line
    if geom_kind == 'point':
        return True
    else:
        return False


def joint_plot(data, x, y, *,
               geom=None,
               bins=None, binwidth=None,
               color=None, size=None, alpha=None,
               color_by=None,
               show_legend=None,
               reg_line=None,
               se=None,
               marginal=None):
    """
    Produce a joint plot that contains bivariate and univariate graphs at the same time.

    Parameters
    ----------
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed.
    x, y : str
        Names of a variables.
    geom : {'point', 'tile', 'density2d', 'density2df'}, default='point'
        The geometric object to use to display the data.
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
        Color of the geometry.
    size : float
        Size of the geometry.
    alpha : float
        Transparency level of the geometry. Accept values between 0 and 1.
    color_by : str
        Name of grouping variable.
    show_legend : bool, default=True
        False - do not show legend for the main layer.
    reg_line : bool
        True - show the line of linear regression.
    se : bool, default=True
        Display confidence interval around regression line.
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
        from lets_plot.bistro.joint import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        data = {
            'x': np.random.normal(size=n),
            'y': np.random.normal(size=n)
        }
        joint_plot(data, 'x', 'y')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11-13

        import numpy as np
        from lets_plot import *
        from lets_plot.bistro.joint import *
        LetsPlot.setup_html()
        n = 500
        np.random.seed(42)
        data = {
            'x': np.random.normal(size=n),
            'y': np.random.normal(size=n)
        }
        joint_plot(data, 'x', 'y', geom='tile', \\
                   binwidth=[.5, .5], color="black", \\
                   marginal="hist:tr,box:bl") + \\
            theme_minimal()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12

        import numpy as np
        from lets_plot import *
        from lets_plot.bistro.joint import *
        LetsPlot.setup_html()
        n = 500
        np.random.seed(42)
        data = {
            'x': np.concatenate((np.random.normal(loc=-1, size=n), np.random.normal(loc=2, size=n))),
            'y': np.concatenate((np.random.normal(loc=-.5, size=n), np.random.normal(loc=1.5, size=n))),
            'g': ["A"] * n + ["B"] * n
        }
        joint_plot(data, 'x', 'y', geom='density2df', color_by='g', alpha=.75)

    """
    # prepare parameters
    geom_kind = geom or _GEOM_DEF
    binwidth2d, bins2d = _get_bin_params_2d(data[x], data[y], binwidth, bins)
    # prepare mapping
    mapping_dict = {'x': x, 'y': y}
    if color_by is not None:
        mapping_dict['color'] = color_by
        mapping_dict['fill'] = color_by
    # prepare layers
    layers = DummySpec()
    # main layer
    main_layer = _get_geom2d_layer(geom_kind, binwidth2d, bins2d, color, color_by, size, alpha, show_legend)
    if main_layer is not None:
        layers += main_layer
    # smooth layer
    if _is_reg_line_needed(reg_line, geom_kind):
        layers += geom_smooth(
            aes(group=color_by),
            method=_REG_LINE_METHOD, se=se,
            color=_REG_LINE_COLOR, linetype=_REG_LINE_LINETYPE
        )
    # marginal layers
    if len(data[x]) == 0:
        marginal = 'none'
    defined_marginal = marginal or _get_marginal_def(geom_kind, color_by)
    if defined_marginal != 'none':
        layers += _get_marginal_layers(defined_marginal, binwidth2d, bins2d, color, color_by, show_legend)

    return ggplot(data, aes(**mapping_dict)) + layers + xlab(x) + ylab(y)
