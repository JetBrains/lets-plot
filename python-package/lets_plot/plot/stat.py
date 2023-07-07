#  Copyright (c) 2023. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
from .geom import _geom

#
# Stats - functions, drawing attention to the statistical transformation rather than the visual appearance.
#
__all__ = ['stat_summary', 'stat_summary_bin']


def stat_summary(mapping=None, *, data=None, geom='pointrange',
                 position=None, show_legend=None, sampling=None, tooltips=None,
                 orientation=None,
                 fun=None, fun_min=None, fun_max=None,
                 quantiles=None,
                 color_by=None, fill_by=None,
                 **other_args):
    """
    Visualise the aggregated values of a single continuous variable grouped along the x axis.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame`
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
        A name of a function that get a vector of values and should return a single number.
        Values 'lq', 'mq', 'uq' corresponds to lower, middle and upper quantiles,
        which are determined by the probabilities passed in the `quantiles` parameter.
    fun_min : {'count', 'sum', 'mean', 'median', 'min', 'max', 'lq', 'mq', 'uq'}, default='min'
        A name of a function that get a vector of values and should return a single number.
        Values 'lq', 'mq', 'uq' corresponds to lower, middle and upper quantiles,
        which are determined by the probabilities passed in the `quantiles` parameter.
    fun_max : {'count', 'sum', 'mean', 'median', 'min', 'max', 'lq', 'mq', 'uq'}, default='max'
        A name of a function that get a vector of values and should return a single number.
        Values 'lq', 'mq', 'uq' corresponds to lower, middle and upper quantiles,
        which are determined by the probabilities passed in the `quantiles` parameter.
    quantiles : list of float, default=[0.25, 0.5, 0.75]
        The list of probabilities defining the quantile functions 'lq', 'mq' and 'uq'.
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

    - ..ymin.. : smallest observation.
    - ..ymax.. : largest observation.

    `stat_summary()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - y : mean.
    - ymin : lower bound.
    - ymax : upper bound.

    In addition, you can use any aesthetics, available for the geometry defined by the `geom` parameter.
    They can be mapped to the following variables, which will be immediately computed:

    - ..count.. : number of observations.
    - ..sum.. : sum of observations.
    - ..mean.. : mean of observations.
    - ..median.. : median of observations.
    - ..ymin.. : smallest observation.
    - ..ymax.. : largest observation.
    - ..lq.. : lower quantile defined by first element of the `quantiles` parameter.
    - ..mq.. : middle quantile defined by first element of the `quantiles` parameter.
    - ..uq.. : upper quantile defined by first element of the `quantiles` parameter.

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
            stat_summary(fun_min='lq', fun_max='uq', quantiles=[.1, .5, .9], \\
                         position=position_nudge(x=.1), color="blue")

    """
    return _geom(geom,
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


def stat_summary_bin(mapping=None, *, data=None, geom='pointrange',
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        A name of a function that get a vector of values and should return a single number.
        Values 'lq', 'mq', 'uq' corresponds to lower, middle and upper quantiles,
        which are determined by the probabilities passed in the `quantiles` parameter.
    fun_min : {'count', 'sum', 'mean', 'median', 'min', 'max', 'lq', 'mq', 'uq'}, default='min'
        A name of a function that get a vector of values and should return a single number.
        Values 'lq', 'mq', 'uq' corresponds to lower, middle and upper quantiles,
        which are determined by the probabilities passed in the `quantiles` parameter.
    fun_max : {'count', 'sum', 'mean', 'median', 'min', 'max', 'lq', 'mq', 'uq'}, default='max'
        A name of a function that get a vector of values and should return a single number.
        Values 'lq', 'mq', 'uq' corresponds to lower, middle and upper quantiles,
        which are determined by the probabilities passed in the `quantiles` parameter.
    quantiles : list of float, default=[0.25, 0.5, 0.75]
        The list of probabilities defining the quantile functions 'lq', 'mq' and 'uq'.
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

    - ..ymin.. : smallest observation.
    - ..ymax.. : largest observation.

    `stat_summary_bin()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - y : mean.
    - ymin : lower bound.
    - ymax : upper bound.

    In addition, you can use any aesthetics, available for the geometry defined by the `geom` parameter.
    They can be mapped to the following variables, which will be immediately computed:

    - ..count.. : number of observations.
    - ..sum.. : sum of observations.
    - ..mean.. : mean of observations.
    - ..median.. : median of observations.
    - ..ymin.. : smallest observation.
    - ..ymax.. : largest observation.
    - ..lq.. : lower quantile defined by first element of the `quantiles` parameter.
    - ..mq.. : middle quantile defined by first element of the `quantiles` parameter.
    - ..uq.. : upper quantile defined by first element of the `quantiles` parameter.

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
            stat_summary_bin(fun_min='lq', fun_max='uq', geom='crossbar', \\
                             bins=11, width=1, quantiles=[.05, .5, .95], boundary=0) + \\
            geom_point()

    """
    return _geom(geom,
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
