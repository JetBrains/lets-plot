#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
__all__ = ['stat_corr']

from .coord import coord_cartesian, coord_fixed
from .core import FeatureSpec
from .geom import _geom
from .scale import scale_x_discrete, scale_y_discrete, scale_color_continuous, scale_fill_continuous
from .scale_identity import scale_size_identity
from .tooltip import layer_tooltips

_DEF_FORMAT = '.2f'

def stat_corr(mapping=None, *, data=None, geom=None, position=None, show_legend=None, sampling=None, tooltips=None,
              type='full',
              diag=None,
              flip=True,
              threshold=None,
              **other_args):
    """
    Function `stat_corr(...)` is deprecated. Please use `corr_plot()` instead.
    See https://lets-plot.org/pages/charts.html#correlation-plot-icon-correlation-plot for details.

    Computes correlations between numeric variables in the `data` and draws a correlation matrix.
    By default uses the `tile` geometry.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `GeoDataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    geom : str
        The name of geometry used to draw correlation matrix.
        For example: 'tile' or 'point' or 'text'.
    position : str or `FeatureSpec`
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    type : {'upper', 'lower', 'full'}, default='full'
        Type of matrix.
    diag : bool
        Determines whether to fill the main diagonal with values.
        Default - True if 'full' matrix, else - False.
    flip : bool, default=True
        If True the y axis is flipped.
    threshold : float, default=0.0
        Minimal correlation abs value to be included in result.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Notes
    -----
    The correlation statistic computes the following variables that can be used in the aesthetic mapping:

    - ..x.. : X coordinates.
    - ..y.. : Y coordinates.
    - ..corr.. : correlation (in range -1..1).
    - ..corr_abs.. : absolute value of correlation (in range 0..1).

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {var: np.random.poisson(size=10) for var in 'abcdef'}
        ggplot(data) + stat_corr() + coord_fixed()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7-9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {var: np.random.uniform(size=10) for var in 'abcd'}
        ggplot(data) + \\
            stat_corr(type='upper', diag=True, color='white') + \\
            stat_corr(aes(size='..corr_abs..'), geom='text', \\
                      type='upper', diag=True, color='black') + \\
            scale_fill_brewer(type='div', palette='RdYlBu', limits=[-1, 1]) + \\
            coord_fixed()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7-10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {var: np.random.normal(size=10) for var in 'abcdef'}
        ggplot(data) + \\
            stat_corr(aes(size='..corr_abs..'), geom='point', \\
                      type='upper', flip=False, threshold=.4) + \\
            stat_corr(geom='text', type='lower', \\
                      flip=False, threshold=.4) + \\
            scale_color_gradient2(low='#d73027', mid='#ffffbf', \\
                                  high='#1a9850', limits=[-1, 1]) + \\
            coord_fixed()

    """

    geom = geom if geom else "tile"
    other_args['label_format'] = other_args.get('label_format', _DEF_FORMAT)

    avoid_na_color = scale_color_continuous(na_value='rgba(0,0,0,0)')
    scale_xy_expand = None
    if geom == 'tile':
        scale_xy_expand = [0, 0.1]  # Smaller 'additive' expand for tiles (normally: 0.6)
        avoid_na_color += scale_fill_continuous(na_value='rgba(0,0,0,0)')

    coord = coord_cartesian()
    if geom in ['point', 'text']:
        other_args['size_unit'] = other_args.get('size_unit', 'x')
        fixed_size = True
        if mapping is not None and isinstance(mapping, FeatureSpec):
            if 'size' in mapping.props():
                fixed_size = False

        if fixed_size:
            other_args['size'] = other_args.get('size', .75)

        if geom == 'point' and not fixed_size:
            coord = coord_fixed()

    if flip:
        if type == 'upper':
            type = 'lower'
        elif type == 'lower':
            type = 'upper'

    if type in ['lower', 'upper']:
        diag = False if diag is None else diag

    sampling = 'none' if sampling is None else sampling

    tooltips = (layer_tooltips()
                .format(field='@..corr..', format=other_args.get('label_format', _DEF_FORMAT))
                .line('@..corr..')) if tooltips is None else tooltips

    return (_geom(geom,
                  mapping=mapping,
                  data=data,
                  stat='corr',
                  position=position,
                  show_legend=show_legend,
                  sampling=sampling,
                  tooltips=tooltips,
                  type=type,
                  diag=diag,
                  threshold=threshold,
                  na_text='',
                  **other_args) +
            scale_size_identity(na_value=0) +
            coord +
            avoid_na_color +
            scale_x_discrete(expand=scale_xy_expand) +
            scale_y_discrete(expand=scale_xy_expand, reverse=flip))
