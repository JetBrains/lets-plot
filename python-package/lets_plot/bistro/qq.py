#  Copyright (c) 2022. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from lets_plot.plot.core import PlotSpec

__all__ = ['qq_plot']


def qq_plot(data=None, sample=None, *, x=None, y=None,
            distribution=None, dparams=None, quantiles=None,
            group=None,
            show_legend=None,
            color=None, fill=None, alpha=None, size=None, shape=None,
            line_color=None, line_size=None, linetype=None) -> PlotSpec:
    """
    Produce a Q-Q plot (quantile-quantile plot).

    Supply the `sample` parameter to compare distribution of observations with a theoretical distribution
    ('normal' or as otherwise specified by the `distribution` parameter).

    Alternatively, supply `x` and `y` parameters to compare the distribution of `x` with the distribution of `y`.

    Parameters
    ----------
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed.
    sample : str
        Name of variable specifying a vector of observations used for computing of "sample quantiles".
        Use this parameter to produce a "sample vs. theoretical" Q-Q plot.
    x, y : str
        Names of variables specifying two vectors of observations used for computing of
        x and y "sample quantiles".
        Use these two parameters to produce a "sample X vs. sample Y" Q-Q plot.
    distribution : {'norm', 'uniform', 't', 'gamma', 'exp', 'chi2'}, default='norm'
        Distribution function to use. Could be specified if `sample` is.
    dparams : list
        Additional parameters (of float type) passed on to distribution function.
        Could be specified if `sample` is.
        If `distribution` is `'norm'` then `dparams` is a pair [mean, std] (=[0.0, 1.0] by default).
        If `distribution` is `'uniform'` then `dparams` is a pair [a, b] (=[0.0, 1.0] by default).
        If `distribution` is `'t'` then `dparams` is an integer number [d] (=[1] by default).
        If `distribution` is `'gamma'` then `dparams` is a pair [alpha, beta] (=[1.0, 1.0] by default).
        If `distribution` is `'exp'` then `dparams` is a float number [lambda] (=[1.0] by default).
        If `distribution` is `'chi2'` then `dparams` is an integer number [k] (=[1] by default).
    quantiles : list, default=[0.25, 0.75]
        Pair of quantiles to use when fitting the Q-Q line.
    group : str
        Grouping parameter.
        If it is specified and color-parameters isn't then different groups will has different colors.
    show_legend : bool, default=True
        False - do not show legend.
    color : str
        Color of a points.
    fill : str
        Color to paint shape's inner points. Is applied only to the points of shapes having inner points.
    alpha : float, default=0.5
        Transparency level of points. Accept values between 0 and 1.
    size : float, default=3.0
        Size of the points.
    shape : int
        Shape of the points, an integer from 0 to 25.
        For more info see https://lets-plot.org/python/pages/aesthetics.html#point-shapes.
    line_color : str, default='#FF0000'
        Color of the fitting line.
    line_size : float, default=0.75
        Width of the fitting line.
    linetype : int or str
        Type of the fitting line.
        Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed',
        3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.
        For more info see https://lets-plot.org/python/pages/aesthetics.html#line-types.

    Returns
    -------
    `PlotSpec`
        Plot object specification.

    Notes
    -----
    The Q-Q plot is used for comparing two probability distributions
    (sample and theoretical or two sample) by plotting their quantiles against each other.

    If the two distributions being compared are similar, the points in the Q-Q plot
    will approximately lie on the straight line.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8

        import numpy as np
        from lets_plot.bistro.qq import qq_plot
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.normal(0, 1, n)
        qq_plot(data={'x': x}, sample='x')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8-10

        import numpy as np
        from lets_plot.bistro.qq import qq_plot
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.exponential(1, n)
        qq_plot({'x': x}, 'x', \\
                distribution='exp', quantiles=[0, .9], \\
                color='black', line_size=.25)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12-13

        import numpy as np
        from lets_plot.bistro.qq import qq_plot
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        data = {
            'x': np.random.normal(0, 1, n),
            'y': np.random.normal(1, 2, n),
            'g': np.random.choice(['a', 'b'], n),
        }
        qq_plot(data, x='x', y='y', group='g', \\
                shape=21, alpha=.2, size=5, linetype=5)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11-12

        import numpy as np
        from lets_plot.bistro.qq import qq_plot
        from lets_plot import *
        LetsPlot.setup_html()
        n = 150
        np.random.seed(42)
        data = {
            'x': np.random.normal(0, 5, n),
            'g': np.random.choice(['a', 'b', 'c'], n),
        }
        qq_plot(data, 'x', dparams=[0, 5], group='g', \\
                line_color='black', line_size=.5) + \\
            scale_color_brewer(type='qual', palette='Set1') + \\
            facet_grid(x='g') + \\
            coord_fixed() + \\
            xlab("Norm distribution quantiles") + \\
            ggtitle("Interaction of the qq_plot() with other layers") + \\
            theme_classic()

    """
    return PlotSpec(data=data, mapping=None, scales=[], layers=[], bistro={
        'name': 'qqplot',
        'sample': sample,
        'x': x,
        'y': y,
        'distribution': distribution,
        'dparams': dparams,
        'quantiles': quantiles,
        'group': group,
        'show_legend': show_legend,
        'color': color,
        'fill': fill,
        'alpha': alpha,
        'size': size,
        'shape': shape,
        'line_color': line_color,
        'line_size': line_size,
        'linetype': linetype,
    })
