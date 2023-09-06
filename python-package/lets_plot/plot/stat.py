#  Copyright (c) 2023. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
from .geom import _geom

#
# Stats - functions, drawing attention to the statistical transformation rather than the visual appearance.
#
__all__ = ['stat_summary', 'stat_summary_bin', 'stat_ecdf', 'stat_sum']


def stat_summary(mapping=None, *, data=None, geom=None,
                 position=None, show_legend=None, sampling=None, tooltips=None,
                 orientation=None,
                 fun=None, fun_min=None, fun_max=None,
                 quantiles=None,
                 color_by=None, fill_by=None,
                 **other_args):
    """
    Display the aggregated values of a single continuous variable grouped along the x axis.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    geom : str, default='pointrange'
        The geometry to display the summary stat for this layer, as a string.
    position : str or `FeatureSpec`, default='identity'
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`
        Result of the call to the `sampling_xxx()` function.
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specify appearance, style and content.
    orientation : str, default='x'
        Specify the axis that the layer's stat and geom should run along.
        Possible values: 'x', 'y'.
    fun : {'count', 'sum', 'mean', 'median', 'min', 'max', 'lq', 'mq', 'uq'}, default='mean'
        Name of function computing stat variable '..y..'.
        Names 'lq', 'mq', 'uq' corresponds to lower, middle and upper quantiles, default=[0.25, 0.5, 0.75].
    fun_min : {'count', 'sum', 'mean', 'median', 'min', 'max', 'lq', 'mq', 'uq'}, default='min'
        Name of function computing stat variable '..ymin..'.
        Names 'lq', 'mq', 'uq' corresponds to lower, middle and upper quantiles, default=[0.25, 0.5, 0.75].
    fun_max : {'count', 'sum', 'mean', 'median', 'min', 'max', 'lq', 'mq', 'uq'}, default='max'
        Name of function computing stat variable '..ymax..'.
        Names 'lq', 'mq', 'uq' corresponds to lower, middle and upper quantiles, default=[0.25, 0.5, 0.75].
    quantiles : list of float, default=[0.25, 0.5, 0.75]
        A list of probabilities defining the quantile functions 'lq', 'mq' and 'uq'.
        Must contain exactly 3 values between 0 and 1.
    color_by : {'fill', 'color', 'paint_a', 'paint_b', 'paint_c'}, default='color'
        Define the color aesthetic for the geometry.
    fill_by : {'fill', 'color', 'paint_a', 'paint_b', 'paint_c'}, default='fill'
        Define the fill aesthetic for the geometry.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Notes
    -----
    Computed variables:

    - ..y.. : result of calculating of `fun`.
    - ..ymin.. : result of calculating of `fun_min`.
    - ..ymax.. : result of calculating of `fun_max`.
    - ..count.. : number of observations.
    - ..sum.. : sum of observations.
    - ..mean.. : mean of observations.
    - ..median.. : median of observations.
    - ..lq.. : lower quantile defined by first element of the `quantiles` parameter.
    - ..mq.. : middle quantile defined by first element of the `quantiles` parameter.
    - ..uq.. : upper quantile defined by first element of the `quantiles` parameter.

    |

    Variables ..count.., ..sum.., ..mean.., ..median.., ..lq.., ..mq.., ..uq.. would not be computed without mappings.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.choice(['a', 'b', 'c'], size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            stat_summary()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.choice(['a', 'b', 'b', 'c'], size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y', fill='x')) + \\
            stat_summary(aes(lower='..lq..', middle='..mq..', upper='..uq..'), \\
                         geom='boxplot', fatten=5)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.choice(['a', 'b', 'c'], size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            stat_summary(position=position_nudge(x=-.1), color="red") + \\
            stat_summary(fun='mq', fun_min='lq', fun_max='uq', quantiles=[.1, .5, .9], \\
                         position=position_nudge(x=.1), color="blue")

    """
    summary_geom = geom if geom is not None else 'pointrange'
    return _geom(summary_geom,
                 mapping=mapping,
                 data=data,
                 stat='summary',
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 orientation=orientation,
                 fun=fun, fun_min=fun_min, fun_max=fun_max,
                 quantiles=quantiles,
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def stat_summary_bin(mapping=None, *, data=None, geom=None,
                     position=None, show_legend=None, sampling=None, tooltips=None,
                     orientation=None,
                     fun=None, fun_min=None, fun_max=None,
                     quantiles=None,
                     bins=None, binwidth=None,
                     center=None, boundary=None,
                     color_by=None, fill_by=None,
                     **other_args):
    """
    Display a distribution by dividing variable mapped to x axis into bins
    and applying aggregation functions to each bin.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    geom : str, default='pointrange'
        The geometry to display the summary stat for this layer, as a string.
    position : str or `FeatureSpec`, default='identity'
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`
        Result of the call to the `sampling_xxx()` function.
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specify appearance, style and content.
    orientation : str, default='x'
        Specify the axis that the layer's stat and geom should run along.
        Possible values: 'x', 'y'.
    fun : {'count', 'sum', 'mean', 'median', 'min', 'max', 'lq', 'mq', 'uq'}, default='mean'
        Name of function computing stat variable '..y..'.
        Names 'lq', 'mq', 'uq' corresponds to lower, middle and upper quantiles, default=[0.25, 0.5, 0.75].
    fun_min : {'count', 'sum', 'mean', 'median', 'min', 'max', 'lq', 'mq', 'uq'}, default='min'
        Name of function computing stat variable '..ymin..'.
        Names 'lq', 'mq', 'uq' corresponds to lower, middle and upper quantiles, default=[0.25, 0.5, 0.75].
    fun_max : {'count', 'sum', 'mean', 'median', 'min', 'max', 'lq', 'mq', 'uq'}, default='max'
        Name of function computing stat variable '..ymax..'.
        Names 'lq', 'mq', 'uq' corresponds to lower, middle and upper quantiles, default=[0.25, 0.5, 0.75].
    quantiles : list of float, default=[0.25, 0.5, 0.75]
        A list of probabilities defining the quantile functions 'lq', 'mq' and 'uq'.
        Must contain exactly 3 values between 0 and 1.
    bins : int, default=30
        Number of bins. Overridden by `binwidth`.
    binwidth : float
        The width of the bins. The default is to use bin widths that cover
        the range of the data. You should always override this value,
        exploring multiple widths to find the best to illustrate the stories in your data.
    center : float
        Specify x-value to align bin centers to.
    boundary : float
        Specify x-value to align bin boundary (i.e. point between bins) to.
    color_by : {'fill', 'color', 'paint_a', 'paint_b', 'paint_c'}, default='color'
        Define the color aesthetic for the geometry.
    fill_by : {'fill', 'color', 'paint_a', 'paint_b', 'paint_c'}, default='fill'
        Define the fill aesthetic for the geometry.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Notes
    -----
    Computed variables:

    - ..y.. : result of calculating of `fun`.
    - ..ymin.. : result of calculating of `fun_min`.
    - ..ymax.. : result of calculating of `fun_max`.
    - ..count.. : number of observations.
    - ..sum.. : sum of observations.
    - ..mean.. : mean of observations.
    - ..median.. : median of observations.
    - ..lq.. : lower quantile defined by first element of the `quantiles` parameter.
    - ..mq.. : middle quantile defined by first element of the `quantiles` parameter.
    - ..uq.. : upper quantile defined by first element of the `quantiles` parameter.

    |

    Variables ..count.., ..sum.., ..mean.., ..median.., ..lq.., ..mq.., ..uq.. would not be computed without mappings.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.uniform(size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            stat_summary_bin()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10-11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.uniform(size=n)
        y = np.random.normal(size=n)
        g = np.random.choice(["A", "B"], size=n)
        ggplot({'x': x, 'y': y, 'g': g}, aes(x='x', y='y', fill='g')) + \\
            stat_summary_bin(aes(lower='..lq..', middle='..mq..', upper='..uq..'), \\
                             geom='boxplot', bins=6, fatten=5, position='dodge')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.uniform(size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            stat_summary_bin(fun='mq', fun_min='lq', fun_max='uq', geom='crossbar', \\
                             bins=11, width=1, quantiles=[.05, .5, .95], boundary=0) + \\
            geom_point()

    """
    summary_bin_geom = geom if geom is not None else 'pointrange'
    return _geom(summary_bin_geom,
                 mapping=mapping,
                 data=data,
                 stat='summarybin',
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 orientation=orientation,
                 fun=fun, fun_min=fun_min, fun_max=fun_max,
                 quantiles=quantiles,
                 bins=bins, binwidth=binwidth,
                 center=center, boundary=boundary,
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def stat_ecdf(mapping=None, *, data=None, geom=None,
              position=None, show_legend=None, sampling=None, tooltips=None,
              orientation=None,
              n=None, pad=None,
              color_by=None,
              **other_args):
    """
    Display the empirical cumulative distribution function.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    geom : str, default='step'
        The geometry to display the ecdf stat for this layer, as a string.
    position : str or `FeatureSpec`, default='identity'
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`
        Result of the call to the `sampling_xxx()` function.
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specify appearance, style and content.
    orientation : str, default='x'
        Specify the axis that the layer's stat and geom should run along.
        Possible values: 'x', 'y'.
    n : int
        If None, do not interpolate.
        If not None, this is the number of points to interpolate with.
    pad : bool, default=True
        If geometry is `'step'` and `pad=True`, then the points at the ends:
        (-inf, 0) and (inf, 1) are added to the ecdf.
    color_by : {'fill', 'color', 'paint_a', 'paint_b', 'paint_c'}, default='color'
        Define the color aesthetic for the geometry.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Notes
    -----
    `stat_ecdf()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - y : y-axis coordinates.

    In addition, you can use any aesthetics, available for the geometry defined by the `geom` parameter.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.normal(size=n)
        ggplot({'x': x}, aes(x='x')) + stat_ecdf()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 14-15

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.concatenate([
            np.random.normal(size=n),
            np.random.uniform(size=n),
            np.random.poisson(size=n),
        ])
        g = ["A"] * n + ["B"] * n + ["C"] * n
        p = ggplot({'x': x, 'g': g}, aes(x='x', color='g'))
        gggrid([
            p + stat_ecdf() + ggtitle("pad=True (default)"),
            p + stat_ecdf(pad=False) + ggtitle("pad=False")
        ])

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8-9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 500
        np.random.seed(42)
        x = np.random.normal(size=n)
        ggplot() + \\
            stat_ecdf(aes(x=x), geom='point', n=20, \\
                      shape=21, color="#f03b20", fill="#ffeda0")

    """
    ecdf_geom = geom if geom is not None else 'step'
    ecdf_pad = pad if pad is not None else True
    return _geom(ecdf_geom,
                 mapping=mapping,
                 data=data,
                 stat='ecdf',
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 orientation=orientation,
                 n=n,
                 pad=ecdf_pad,
                 color_by=color_by,
                 **other_args)


def stat_sum(mapping=None, *, data=None, geom=None,
             position=None, show_legend=None, sampling=None, tooltips=None,
             **other_args):
    """
    Sum unique values

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    geom : str, default='point'
        The geometry to display the sum stat for this layer, as a string.
    position : str or `FeatureSpec`, default='identity'
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`
        Result of the call to the `sampling_xxx()` function.
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specify appearance, style and content.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Notes
    -----
    `stat_sum()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - y : y-axis coordinates.

    In addition, you can use any aesthetics, available for the geometry defined by the `geom` parameter.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        from lets_plot.mapping import as_discrete
        LetsPlot.setup_html()
        n = 50
        np.random.seed(42)
        x = [round(it) for it in np.random.normal(0, 1.5, size=n)]
        y = [round(it) for it in np.random.normal(0, 1.5, size=n)]
        ggplot({'x': x, 'y': y}, aes(x=as_discrete('x', order=1), y=as_discrete('y', order=1))) + stat_sum()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 14-15

        import numpy as np
        from lets_plot import *
        from lets_plot.mapping import as_discrete
        LetsPlot.setup_html()
        n = 50
        np.random.seed(42)
        x = [round(it) for it in np.random.normal(0, 1.5, size=n)]
        y = [round(it) for it in np.random.normal(0, 1.5, size=n)]
        ggplot({'x': x, 'y': y}, aes(x=as_discrete('x', order=1), y=as_discrete('y', order=1))) + stat_sum(aes(size='..prop..', group='x'))
"""
    sum_geom = 'point' if geom is None else geom
    return _geom(sum_geom,
                 mapping=mapping,
                 data=data,
                 stat='sum',
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 **other_args)
