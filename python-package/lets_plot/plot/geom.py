#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec, LayerSpec
from .util import as_annotated_data, as_annotated_map_data, is_geo_data_frame, is_geocoder, auto_join_geocoder, \
    geo_data_frame_to_wgs84, as_map_join

#
# Geoms, short for geometric objects, describe the type of plot ggplot will produce.
#
__all__ = ['geom_point', 'geom_path', 'geom_line',
           'geom_smooth', 'geom_bar',
           'geom_histogram', 'geom_bin2d',
           'geom_tile', 'geom_raster',
           'geom_errorbar', 'geom_crossbar', 'geom_linerange', 'geom_pointrange',
           'geom_contour',
           'geom_contourf', 'geom_polygon', 'geom_map',
           'geom_abline', 'geom_hline', 'geom_vline',
           'geom_boxplot',
           'geom_ribbon', 'geom_area', 'geom_density',
           'geom_density2d', 'geom_density2df', 'geom_jitter',
           'geom_freqpoly', 'geom_step', 'geom_rect', 'geom_segment',
           'geom_text']


def geom_point(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
               map=None, map_join=None,
               **other_args):
    """
    Draw points defined by an x and y coordinate, as for a scatter plot.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `GeoDataFrame` (supported shapes `Point` and `MultiPoint`), optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    map : `GeoDataFrame` (supported shapes `Point` and `MultiPoint`) or `Geocoder` (implicitly invoke `centroids()`), optional
        Data containing coordinates of points.
    map_join : str or list, optional
        Keys used to join map coordinates with data.
        If the parameter type is list then it should be a pair of values,
        either of str type or a list of strings.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    The point geometry is used to create scatterplots.
    The scatterplot is useful for displaying the relationship between
    two continuous variables, although it can also be used with one continuous
    and one categorical variable, or two categorical variables.

    `geom_point()` understands the following aesthetics mappings:
        - x : x-axis value.
        - y : y-axis value.
        - alpha : transparency level of the point. Understands numbers between 0 and 1.
        - color (colour) : color of the geometry. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - fill : color to paint shape's inner points. Is applied only to the points of shapes having inner points.
        - shape : shape of the point.
        - size : size of the point.

    Note
    ----
    The conventions for the values of `map_join` parameter are as follows.

    When `map` is a `GeoDataFrame`:
        - map_join='state':
          same as [['state'], ['state']].
        - map_join=[['city', 'state']]:
          same as [['city', 'state'], ['city', 'state']].
        - map_join=[['City_Name', 'State_Name'], ['city', 'state']]:
          explicitly set keys for both data and map.

    If `map` is a `Geocoder`:
        - map_join='State_Name':
          same as [['State_Name'], ['state']].
        - map_join=['City_Name', 'State_Name']:
          same as [['City_Name', 'State_Name'], ['city', 'state']].
        - map_join=[['City_Name', 'State_Name'], ['city', 'state']]:
          explicitly set keys for both data and map.

    Generated keys follow this order: 'city', 'county', 'state', 'country'.
    Parents that were not provided will be omitted.
    Data columns should follow the same order or result of join operation will be incorrect.

    Examples
    --------
    .. jupyter-execute::
        :emphasize-lines: 6

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> x = np.linspace(-2 * np.pi, 2 * np.pi, 100)
        >>> y = np.sin(x)
        >>> ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + geom_point()

    |

    .. jupyter-execute::
        :emphasize-lines: 9-10

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> np.random.seed(42)
        >>> n = 100
        >>> x = np.random.uniform(-1, 1, size=n)
        >>> y = 25 * x ** 2 + np.random.normal(size=n)
        >>> ggplot({'x': x, 'y': y}) + \\
        >>>     geom_point(aes(x='x', y='y', fill='y'), \\
        >>>                shape=21, size=5, color='white')

    |

    .. jupyter-execute::
        :emphasize-lines: 8-11

        >>> from lets_plot import *
        >>> from lets_plot.geo_data import *
        >>> LetsPlot.setup_html()
        >>> data = {'city': ['New York', 'Los Angeles', 'Chicago'], \\
        >>>         'pop_2020': [18_804_000, 12_447_000, 8_865_000]}
        >>> centroids = geocode_cities(data['city']).get_centroids()
        >>> ggplot() + geom_livemap() + \\
        >>>     geom_point(aes(size='pop_2020'), \\
        >>>                data=data, map=centroids, map_join='city', \\
        >>>                tooltips=layer_tooltips().line('@city')
        >>>                                         .line('population|@pop_2020')) + \\
        >>>     ggsize(600, 450)

    """

    if is_geocoder(map):
        map_join = auto_join_geocoder(map_join, map)
        map = map.get_centroids()

    return _geom('point',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 map=map, map_join=map_join,
                 **other_args)


def geom_path(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
              map=None, map_join=None,
              **other_args):
    """
    Connects observations in the order, how they appear in the data.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `GeoDataFrame` (supported shapes `LineString` and `MultiLineString`), optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    map : `GeoDataFrame` (supported shapes `LineString` and `MultiLineString`), optional
        Data containing coordinates of lines.
    map_join : str or list, optional
        Pair of names used to join map coordinates with data.
        str is allowed only when used with `Geocoder` object -
        map key 'request' will be automatically added.
        First value in pair - column in `data`.
        Second value in pair - column in `map`.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_path()` connects the observations in the order in which they appear in the data.
    `geom_path()` lets you explore how two variables are related over time.

    `geom_path()` understands the following aesthetics mappings:
        - x : x-axis value.
        - y : y-axis value.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash.
        - size : line width.

    Examples
    --------
    .. jupyter-execute::
        :emphasize-lines: 8

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> np.random.seed(42)
        >>> T = 100
        >>> x = np.cumsum(np.random.normal(size=T))
        >>> y = np.cumsum(np.random.normal(size=T))
        >>> ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + geom_path()

    |

    .. jupyter-execute::
        :emphasize-lines: 12-13

        >>> import numpy as np
        >>> import pandas as pd
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> np.random.seed(42)
        >>> t = np.arange(100)
        >>> x1 = np.cumsum(np.random.normal(size=t.size))
        >>> x2 = np.cumsum(np.random.normal(size=t.size))
        >>> df = pd.DataFrame({'t': t, 'x1': x1, 'x2': x2})
        >>> df = pd.melt(df, id_vars=['t'], value_vars=['x1', 'x2'])
        >>> ggplot(df, aes(x='t', y='value', group='variable')) + \\
        >>>     geom_path(aes(color='variable'), size=1, alpha=0.5) + \\
        >>>     geom_path(stat='smooth', color='red', linetype='longdash')

    """
    if is_geocoder(map):
        raise ValueError("Geocoding doesn't provide geometries supported by geom_path")
    return _geom('path',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 map=map, map_join=map_join,
                 **other_args)


def geom_line(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
              **other_args):
    """
    Connect points in the order of the variable on the x axis.
    In case points need to be connected in the order in which they appear in the data,
    use `geom_path()`.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_line()` connects the observations in the order of the variable on the x axis.
    `geom_line()` can be used to plot time series.

    `geom_line()` understands the following aesthetics mappings:
        - x : x-axis value.
        - y : y-axis value.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash.
        - size : line width.

    Examples
    --------
    .. jupyter-execute::
        :emphasize-lines: 6

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> x = np.linspace(-4 * np.pi, 4 * np.pi, 100)
        >>> y = np.sin(x)
        >>> ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + geom_line()

    |

    .. jupyter-execute::
        :emphasize-lines: 12-13

        >>> import numpy as np
        >>> import pandas as pd
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> np.random.seed(42)
        >>> t = np.arange(100)
        >>> x1 = np.cumsum(np.random.normal(size=t.size))
        >>> x2 = np.cumsum(np.random.normal(size=t.size))
        >>> df = pd.DataFrame({'t': t, 'x1': x1, 'x2': x2})
        >>> df = pd.melt(df, id_vars=['t'], value_vars=['x1', 'x2'])
        >>> ggplot(df, aes(x='t', y='value', group='variable')) + \\
        >>>     geom_line(aes(color='variable'), size=1, alpha=0.5) + \\
        >>>     geom_line(stat='smooth', color='red', linetype='longdash')

    """
    return _geom('line',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 **other_args)


def geom_smooth(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                method=None,
                n=None,
                se=None,
                level=None,
                span=None,
                deg=None,
                seed=None,
                max_n=None,
                **other_args):
    """
    Add a smoothed conditional mean.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='smooth'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    method : str, default='lm'
        Smoothing method: 'lm' (Linear Model) or 'loess' (Locally Estimated Scatterplot Smoothing).
    n : int, optional
        Number of points to evaluate smoother at.
    se : bool, default=True
        Display confidence interval around smooth.
    level : float, default=0.95
        Level of confidence interval to use.
    span : float, default=0.5
        Only for 'loess' method. The fraction of source points closest
        to the current point is taken into account for computing a least-squares regression.
        A sensible value is usually 0.25 to 0.5.
    deg : int, default=1
        Degree of polynomial for linear regression model.
    seed : int, optional
        Random seed for 'loess' sampling.
    max_n : int, default=1000
        Maximum number of data-points for 'loess' method.
        If this quantity exceeded random sampling is applied to data.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_smooth()` aids the eye in seeing patterns in the presence of overplotting.

    Computed variables:
      - y : predicted (smoothed) value.
      - ymin : lower pointwise confidence interval around the mean.
      - ymax : upper pointwise confidence interval around the mean.
      - se : standard error.

    `geom_smooth()` understands the following aesthetics mappings:
        - x : x-axis value.
        - y : y-axis value.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - linetype : type of the line of conditional mean line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash.
        - size : lines width. Defines line width for conditional mean and confidence bounds lines.

    Examples
    --------
    .. jupyter-execute::
        :emphasize-lines: 9

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> np.random.seed(42)
        >>> n = 50
        >>> x = np.arange(n)
        >>> y = x + np.random.normal(scale=10, size=n)
        >>> ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
        >>>     geom_point() + geom_smooth()

    |

    .. jupyter-execute::
        :emphasize-lines: 9

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> np.random.seed(42)
        >>> n = 100
        >>> x = np.linspace(-2, 2, n)
        >>> y = x ** 2 + np.random.normal(size=n)
        >>> ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
        >>>     geom_point() + geom_smooth(color='red', deg=2, se=False)

    |

    .. jupyter-execute::
        :emphasize-lines: 14-15

        >>> import numpy as np
        >>> import pandas as pd
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> np.random.seed(42)
        >>> t = np.linspace(0, 1, 100)
        >>> mean = 1 + np.zeros(2)
        >>> cov = np.eye(2)
        >>> x, y = np.random.multivariate_normal(mean, cov, t.size).T
        >>> df = pd.DataFrame({'t': t, 'x': x, 'y': y})
        >>> df = df.melt(id_vars=['t'], value_vars=['x', 'y'])
        >>> ggplot(df, aes(x='t', y='value', group='variable')) + \\
        >>>     geom_point(aes(color='variable'), size=3, alpha=.5) + \\
        >>>     geom_smooth(aes(color='variable'), size=1, \\
        >>>                 method='loess', span=.3, level=.7, seed=42)

    """
    return _geom('smooth',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 method=method,
                 n=n,
                 se=se,
                 level=level,
                 span=span,
                 deg=deg,
                 seed=seed,
                 max_n=max_n,
                 **other_args)


def geom_bar(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
             **other_args):
    """
    Display a bar chart which makes the height of the bar proportional to the
    number of observed variable values, mapped to x axis.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='count'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_bar()` makes the height of the bar proportional to the number
    of observed variable values, mapped to x axis. Is intended to use for discrete data.
    If used for continuous data with stat='bin' produces histogram for binned data.
    `geom_bar()` handles no group aesthetics.

    `geom_bar()` understands the following aesthetics mappings:
        - x : x-axis value (this values will produce cases or bins for bars).
        - y : y-axis value (this value will be used to multiply the case's or bin's counts).
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - fill : color of geometry filling.
        - size : lines width. Defines bar line width.

    Examples
    --------
    .. jupyter-execute::
        :emphasize-lines: 6

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> np.random.seed(42)
        >>> data = {'x': np.random.randint(10, size=100)}
        >>> ggplot(data, aes(x='x')) + geom_bar()

    |

    .. jupyter-execute::
        :emphasize-lines: 9-10

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> np.random.seed(42)
        >>> n = 10
        >>> x = np.arange(n)
        >>> y = 1 + np.random.randint(5, size=n)
        >>> ggplot() + \\
        >>>     geom_bar(aes(x='x', y='y', fill='x'), data={'x': x, 'y': y}, \\
        >>>              stat='identity', show_legend=False) + \\
        >>>     scale_fill_discrete()

    |

    .. jupyter-execute::
        :emphasize-lines: 9-12

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> np.random.seed(42)
        >>> n = 5000
        >>> x = np.random.normal(size=n)
        >>> c = np.random.choice(list('abcde'), size=n)
        >>> ggplot({'x': x, 'class': c}, aes(x='x')) + \\
        >>>     geom_bar(aes(group='class', fill='class', color='class'), \\
        >>>              stat='bin', sampling=sampling_pick(n=500), alpha=.3, \\
        >>>              tooltips=layer_tooltips().line('@|@class')
        >>>                                       .line('count|@..count..'))

    """
    return _geom('bar',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 **other_args)


def geom_histogram(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                   bins=None,
                   binwidth=None,
                   center=None,
                   boundary=None,
                   **other_args):
    """
    Displays a 1d distribution by dividing variable mapped to x axis into bins
    and counting the number of observations in each bin.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='bin'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, default='stack'
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    bins : int, default=30
        Number of bins. Overridden by `binwidth`.
    binwidth : float, optional
        The width of the bins. The default is to use bin widths that cover
        the range of the data. You should always override this value,
        exploring multiple widths to find the best to illustrate the stories in your data.
    center : float, optional
        Specifies x-value to align bin centers to.
    boundary : float, optional
        Specifies x-value to align bin boundary (i.e. point berween bins) to.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_histogram()` displays a 1d distribution by dividing variable
    mapped to x axis into bins and counting the number of observations in each bin.

    `geom_histogram()` understands the following aesthetics mappings:
        - x : x-axis value (this values will produce cases or bins for bars).
        - y : y-axis value, default: '..count..'. Alternatively: '..density..'.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - fill : color of geometry filling.
        - size : lines width.
        - weight : used by 'bin' stat to compute weighted sum instead of simple count.

    Examples
    --------
    .. jupyter-execute::
        :emphasize-lines: 6

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> np.random.seed(42)
        >>> data = {'x': np.random.normal(size=1000)}
        >>> ggplot(data, aes(x='x')) + geom_histogram()

    |

    .. jupyter-execute::
        :emphasize-lines: 7

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> np.random.seed(42)
        >>> data = {'x': np.random.gamma(2.0, size=1000)}
        >>> ggplot(data, aes(x='x')) + \\
        >>>     geom_histogram(aes(color='x', fill='x'), bins=50)

    |

    .. jupyter-execute::
        :emphasize-lines: 8-10

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> np.random.seed(42)
        >>> x = np.random.normal(scale=3, size=1000)
        >>> y = 2 * (np.round(x) % 2) - 1
        >>> ggplot({'x': x, 'y': y}) + \\
        >>>     geom_histogram(aes(x='x', weight='y'), \\
        >>>                    center=0, binwidth=1, \\
        >>>                    color='black', fill='gray', size=1)

    """
    return _geom('histogram',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 bins=bins,
                 binwidth=binwidth,
                 center=center,
                 boundary=boundary,
                 **other_args)


def geom_bin2d(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
               bins=None,
               binwidth=None,
               drop=None,
               **other_args):
    """
    Displays a 1d distribution by dividing variable mapped to x axis into bins
    and counting the number of observations in each bin.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='bin2d'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, default='stack'
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    bins : list of int, default=[30, 30]
        Number of bins in both directions, vertical and horizontal. Overridden by `binwidth`.
    binwidth : list of float, optional
        The width of the bins in both directions, vertical and horizontal.
        Overrides `bins`. The default is to use bin widths that cover the entire range of the data.
    drop : bool, default=True
        Specifies whether to remove all bins with 0 counts.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_bin2d()` applies rectangular grid to the plane then counts observation
    in each cell of the grid (bin). Uses `geom_tile()` to display counts as a tile fill-color.

    `geom_bin2d()` understands the following aesthetics mappings:
        - x : x-axis value.
        - y : y-axis value.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - fill : color of geometry filling, default: '..count..'. Alternatively: '..density..'.
        - size : lines width.
        - weight : used by 'bin' stat to compute weighted sum instead of simple count.

    Examples
    --------
    .. jupyter-execute::
        :emphasize-lines: 8

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> np.random.seed(42)
        >>> mean = np.zeros(2)
        >>> cov = np.eye(2)
        >>> x, y = np.random.multivariate_normal(mean, cov, 1000).T
        >>> ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + geom_bin2d()

    |

    .. jupyter-execute::
        :emphasize-lines: 9-14

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> np.random.seed(42)
        >>> n = 5000
        >>> x = np.random.uniform(-2, 2, size=n)
        >>> y = np.random.normal(scale=.5, size=n)
        >>> ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
        >>>     geom_bin2d(aes(fill='..density..'), binwidth=[.25, .24], \\
        >>>                tooltips=layer_tooltips().format('@x', '.2f')
        >>>                        .format('@y', '.2f').line('(@x, @y)')
        >>>                        .line('count|@..count..')
        >>>                        .format('@..density..', '.3f')
        >>>                        .line('density|@..density..')) + \\
        >>>     scale_fill_gradient(low='black', high='red')

    |

    .. jupyter-execute::
        :emphasize-lines: 10-11

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> np.random.seed(42)
        >>> mean = np.zeros(2)
        >>> cov = [[1, .5],
        >>>        [.5, 1]]
        >>> x, y = np.random.multivariate_normal(mean, cov, 500).T
        >>> ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
        >>>     geom_bin2d(aes(alpha='..count..'), bins=[20, 20], \\
        >>>                color='white', fill='darkgreen') + \\
        >>>     geom_point(size=1.5, shape=21, color='white', \\
        >>>                fill='darkgreen') + \\
        >>>     ggsize(600, 450)

    """
    return _geom('bin2d',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 bins=bins,
                 binwidth=binwidth,
                 drop=drop,
                 **other_args)


def geom_tile(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
              **other_args):
    """
    Display rectangles with x, y values mapped to the center of the tile.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    Understands the following aesthetics mappings:
        - x : x-axis coordinates of the center of rectangles.
        - y : y-axis coordinates of the center of rectangles.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - fill : color of geometry filling.
        - size : lines width.
        - width : width of a tile.
        - height : height of a tile.
        - linetype : type of the line of tile's border. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Examples
    --------
    .. jupyter-execute::
        :emphasize-lines: 12

        >>> import numpy as np
        >>> from scipy.stats import multivariate_normal
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> n = 100
        >>> a, b = -1, 0
        >>> x = np.linspace(-3, 3, n)
        >>> y = np.linspace(-3, 3, n)
        >>> X, Y = np.meshgrid(x, y)
        >>> Z = np.exp(-5 * np.abs(Y ** 2 - X ** 3 - a * X - b))
        >>> data = {'x': X.flatten(), 'y': Y.flatten(), 'z': Z.flatten()}
        >>> ggplot(data, aes(x='x', y='y', color='z', fill='z')) + geom_tile()

    |

    .. jupyter-execute::
        :emphasize-lines: 17

        >>> import numpy as np
        >>> from scipy.stats import multivariate_normal
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> np.random.seed(42)
        >>> n = 25
        >>> x = np.linspace(-1, 1, n)
        >>> y = np.linspace(-1, 1, n)
        >>> X, Y = np.meshgrid(x, y)
        >>> mean = np.zeros(2)
        >>> cov =[[1, -.5],
        >>>       [-.5, 1]]
        >>> rv = multivariate_normal(mean, cov)
        >>> Z = rv.pdf(np.dstack((X, Y)))
        >>> data = {'x': X.flatten(), 'y': Y.flatten(), 'z': Z.flatten()}
        >>> ggplot(data, aes(x='x', y='y')) + \\
        >>>     geom_tile(aes(fill='z'), width=.8, height=.8, color='black') + \\
        >>>     scale_fill_gradient(low='yellow', high='darkgreen')

    """
    return _geom('tile',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 **other_args)


def geom_raster(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                **other_args):
    """
    Display rectangles with x, y values mapped to the center of the tile.
    This is a high performance special function for same-sized tiles.
    Much faster than `geom_tile()` but doesn't support width/height and color.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    Understands the following aesthetics mappings:
        - x : x-axis coordinates of the center of rectangles.
        - y : y-axis coordinates of the center of rectangles.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - fill : color of geometry filling.

    Examples
    --------
    .. jupyter-execute::

        >>> import numpy as np
        >>> from lets_plot import *
        >>> from scipy.stats import multivariate_normal
        >>> LetsPlot.setup_html()
        >>> delta = 0.5
        >>> center_x = 6
        >>> center_y = 6
        >>> x = np.arange(-5.0, 5.0, delta)
        >>> y = np.arange(-5.0, 5.0, delta)
        >>> X, Y = np.meshgrid(x, y)
        >>> mu = np.array([1, 0])
        >>> sigma = np.diag([1, 4])
        >>> mu1 = np.array([0, 0])
        >>> sigma1 = np.diag([4, 1])
        >>> Z = multivariate_normal.pdf(np.dstack((X, Y)), mean=mu, cov=sigma)
        >>> Z = Z - multivariate_normal.pdf(np.dstack((X, Y)), mean=mu1, cov=sigma1)
        >>> x = X.reshape(-1) + center_x
        >>> y = Y.reshape(-1) + center_y
        >>> z = Z.reshape(-1)
        >>> dat = dict(x=x, y=y, z=z)
        >>> plot = ggplot(dat, aes('x', 'y')) +  geom_raster(aes(fill='z'))
        >>> plot

    """
    return _geom('raster',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 **other_args)


def geom_errorbar(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                  **other_args):
    """
    Display error bars defined by the upper and lower values.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_errorbar()` represents a vertical interval, defined by `x`, `ymin`, `ymax`.

    `geom_errorbar()` understands the following aesthetics mappings:
        - x : x-axis coordinates.
        - ymin : lower bound for error bar.
        - ymax : upper bound for error bar.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - size : lines width. Defines bar line width.
        - width : width of a bar.
        - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Examples
    --------
    .. jupyter-execute::

        >>> import numpy as np
        >>> from lets_plot import *
        >>> from scipy.stats import multivariate_normal
        >>> LetsPlot.setup_html()
        >>> N = 10
        >>> M = 10
        >>> m = np.random.random(M) * 5.0
        >>> cov = np.eye(M)
        >>> W = np.random.multivariate_normal(m, cov, N)
        >>> se = W.std(axis=1)
        >>> mean = W.mean(axis=1)
        >>> ymin = mean - se
        >>> ymax = mean + se
        >>> x = np.arange(0, N, 1)
        >>> dat = dict(x=x, ymin=ymin, ymax=ymax)
        >>> ggplot(dat, aes(x='x')) + geom_errorbar(aes(ymin='ymin', ymax='ymax'))

    """
    return _geom('errorbar',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 **other_args)


def geom_crossbar(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                  fatten=None,
                  **other_args):
    """
    Display bars with horizontal median line.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    fatten : float, default=2.5
        A multiplicative factor applied to size of the middle bar.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_crossbar()` represents a vertical interval, defined by `x`, `ymin`, `ymax`.
    The mean is represented by horizontal line.

    `geom_crossbar()` understands the following aesthetics mappings:
        - x : x-axis coordinates.
        - ymin : lower bound for error bar.
        - middle : position of median bar.
        - ymax : upper bound for error bar.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - fill : color of geometry filling.
        - size : lines width.
        - width : width of a bar.
        - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Examples
    --------
    .. jupyter-execute::

        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> data = dict(
        >>>     supp = ['OJ', 'OJ', 'OJ', 'VC', 'VC', 'VC'],
        >>>     dose = [0.5, 1.0, 2.0, 0.5, 1.0, 2.0],
        >>>     length = [13.23, 22.70, 26.06, 7.98, 16.77, 26.14],
        >>>     len_min = [11.83, 21.2, 24.50, 4.24, 15.26, 23.35],
        >>>     len_max = [15.63, 24.9, 27.11, 10.72, 19.28, 28.93]
        >>> )
        >>>
        >>> p = ggplot(data, aes(x='dose', color='supp'))
        >>> p += geom_crossbar(aes(ymin='len_min', ymax='len_max', middle='length'), fatten=5)
        >>> p

    """
    return _geom('crossbar',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 fatten=fatten,
                 **other_args)


def geom_pointrange(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None,
                    tooltips=None,
                    fatten=None,
                    **other_args):
    """
    Add a vertical line defined by upper and lower value with midpoint at y location.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    fatten : float, default=5.0
        A multiplicative factor applied to size of the middle bar.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_pointrange()` represents a vertical interval, defined by `x`, `ymin`, `ymax`.
    The mid-point is defined by `y`.

    `geom_pointrange()` understands the following aesthetics mappings:
        - x : x-axis coordinates.
        - y : position of mid-point.
        - ymin : lower bound for error bar.
        - ymax : upper bound for error bar.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - fill : color of geometry filling.
        - size : lines width, size of mid-point.
        - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.
        - shape : shape of the mid-point.

    Examples
    --------
    .. jupyter-execute::

        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> data = dict(
        >>>     supp = ['OJ', 'OJ', 'OJ', 'VC', 'VC', 'VC'],
        >>>     dose = [0.5, 1.0, 2.0, 0.5, 1.0, 2.0],
        >>>     length = [13.23, 22.70, 26.06, 7.98, 16.77, 26.14],
        >>>     len_min = [11.83, 21.2, 24.50, 4.24, 15.26, 23.35],
        >>>     len_max = [15.63, 24.9, 27.11, 10.72, 19.28, 28.93]
        >>> )
        >>>
        >>> p = ggplot(data, aes(x='dose', color='supp'))
        >>> p += geom_pointrange(aes(ymin='len_min', ymax='len_max', y='length'), fatten=5)
        >>> p

    """
    return _geom('pointrange',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 fatten=fatten,
                 **other_args)


def geom_linerange(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                   **other_args):
    """
    Display a line range defined by an upper and lower value.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_linerange()` represents a vertical interval, defined by `x`, `ymin`, `ymax`.

    `geom_linerange()` understands the following aesthetics mappings:
        - x : x-axis coordinates.
        - ymin : lower bound for line range.
        - ymax : upper bound for line range.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - size : lines width.
        - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Examples
    --------
    .. jupyter-execute::

        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> data = dict(
        >>>     supp = ['OJ', 'OJ', 'OJ', 'VC', 'VC', 'VC'],
        >>>     dose = [0.5, 1.0, 2.0, 0.5, 1.0, 2.0],
        >>>     length = [13.23, 22.70, 26.06, 7.98, 16.77, 26.14],
        >>>     len_min = [11.83, 21.2, 24.50, 4.24, 15.26, 23.35],
        >>>     len_max = [15.63, 24.9, 27.11, 10.72, 19.28, 28.93]
        >>> )
        >>>
        >>> p = ggplot(data, aes(x='dose', color='supp'))
        >>> p += geom_linerange(aes(ymin='len_min', ymax='len_max'))
        >>> p

    """
    return _geom('linerange',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 **other_args)


def geom_contour(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                 bins=None,
                 binwidth=None,
                 **other_args):
    """
    Display contours of a 3d surface in 2d.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='contour'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    bins : int, optional
        Number of levels.
    binwidth: float, optional
        Distance between levels.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_contour()` displays contours of a 3d surface in 2d.

    Computed variables:
        - level : height of a contour.

    `geom_contour()` understands the following aesthetics mappings:
        - x : x-axis coordinates of the center of rectangles, forming a tessellation.
        - y : y-axis coordinates of the center of rectangles, forming a tessellation.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - size : lines width.
        - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Examples
    --------
    .. jupyter-execute::

        >>> import numpy as np
        >>> import pandas as pd
        >>> from lets_plot import *
        >>> from scipy.stats import multivariate_normal
        >>> LetsPlot.setup_html()
        >>> delta = 0.5
        >>> center_x = 6
        >>> center_y = 6
        >>> x = np.arange(-5.0, 5.0, delta)
        >>> y = np.arange(-5.0, 5.0, delta)
        >>> X, Y = np.meshgrid(x, y)
        >>> mu = np.array([1, 0])
        >>> sigma = np.diag([1, 4])
        >>> mu1 = np.array([0, 0])
        >>> sigma1 = np.diag([4, 1])
        >>> Z = multivariate_normal.pdf(np.dstack((X, Y)), mean=mu, cov=sigma)
        >>> Z = Z - multivariate_normal.pdf(np.dstack((X, Y)), mean=mu1, cov=sigma1)
        >>> x = X.reshape(-1) + center_x
        >>> y = Y.reshape(-1) + center_y
        >>> z = Z.reshape(-1)
        >>> dat = dict(x=x, y=y, z=z)
        >>> p = ggplot(dat, aes('x', 'y')) + geom_tile(aes(fill='z')) + geom_contour(aes(z='z', color='..level..')) + scale_color_gradient(low='dark_green', high='yellow')
        >>> p

    """
    return _geom('contour',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 bins=bins, binwidth=binwidth,
                 **other_args)


def geom_contourf(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                  bins=None,
                  binwidth=None,
                  **other_args):
    """
    Fill contours of a 3d surface in 2d.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='contourf'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    bins : int, optional
        Number of levels.
    binwidth: float, optional
        Distance between levels.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_contourf()` fills contours of a 3d surface in 2d.

    Computed variables:
        - level : height of a contour.

    `geom_contourf()` understands the following aesthetics mappings:
        - x : x-axis coordinates of the center of rectangles, forming a tessellation.
        - y : y-axis coordinates of the center of rectangles, forming a tessellation.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - fill : color of a geometry areas. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.

    Examples
    --------
    .. jupyter-execute::

        >>> import numpy as np
        >>> import pandas as pd
        >>> from lets_plot import *
        >>> from scipy.stats import multivariate_normal
        >>> LetsPlot.setup_html()
        >>> delta = 0.5
        >>> center_x = 6
        >>> center_y = 6
        >>> x = np.arange(-5.0, 5.0, delta)
        >>> y = np.arange(-5.0, 5.0, delta)
        >>> X, Y = np.meshgrid(x, y)
        >>> mu = np.array([1, 0])
        >>> sigma = np.diag([1, 4])
        >>> mu1 = np.array([0, 0])
        >>> sigma1 = np.diag([4, 1])
        >>> Z = multivariate_normal.pdf(np.dstack((X, Y)), mean=mu, cov=sigma)
        >>> Z = Z - multivariate_normal.pdf(np.dstack((X, Y)), mean=mu1, cov=sigma1)
        >>> x = X.reshape(-1) + center_x
        >>> y = Y.reshape(-1) + center_y
        >>> z = Z.reshape(-1)
        >>> dat = dict(x=x, y=y, z=z)
        >>> p = ggplot(dat, aes('x', 'y', z='z')) + geom_contourf(aes(fill='..level..'))
        >>> p

    """
    return _geom('contourf',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 bins=bins,
                 binwidth=binwidth,
                 **other_args)


def geom_polygon(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                 map=None, map_join=None,
                 **other_args):
    """
    Display a filled closed path defined by the vertex coordinates of individual polygons.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `GeoDataFrame` (supported shapes `Polygon` and `MultiPolygon`), optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    map : `GeoDataFrame` (supported shapes `Polygon` and `MultiPolygon`) or `Geocoder` (implicitly invoke `boundaries()`), optional
        Data contains coordinates of polygon vertices on map.
    map_join : str or list, optional
        Keys used to join map coordinates with data.
        If the parameter type is list then it should be a pair of values,
        either of str type or a list of strings.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_polygon()` draws polygons, which are filled paths.
    Each vertex of the polygon requires a separate row in the data.

    `geom_polygon()` understands the following aesthetics mappings:
        - x : x-axis coordinates of the vertices of the polygon.
        - y : y-axis coordinates of the vertices of the polygon.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - size : lines width. Defines line width.
        - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Note
    ----
    The conventions for the values of `map_join` parameter are as follows.

    When `map` is a `GeoDataFrame`:
        - map_join='state':
          same as [['state'], ['state']].
        - map_join=[['city', 'state']]:
          same as [['city', 'state'], ['city', 'state']].
        - map_join=[['City_Name', 'State_Name'], ['city', 'state']]:
          explicitly set keys for both data and map.

    If `map` is a `Geocoder`:
        - map_join='State_Name':
          same as [['State_Name'], ['state']].
        - map_join=['City_Name', 'State_Name']:
          same as [['City_Name', 'State_Name'], ['city', 'state']].
        - map_join=[['City_Name', 'State_Name'], ['city', 'state']]:
          explicitly set keys for both data and map.

    Generated keys follow this order: 'city', 'county', 'state', 'country'.
    Parents that were not provided will be omitted.
    Data columns should follow the same order or result of join operation will be incorrect.

    Examples
    --------
    .. jupyter-execute::

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> id = ["A", "B", "C", "D", "E", "F"]
        >>> val = np.random.uniform(3, 3.5, 6)
        >>> x = np.random.uniform(1, 3, 18)
        >>> y = np.random.uniform(0, 3, 18)
        >>> id3 = [v for v in id for _ in range(3)]
        >>> val3 = [v for v in val for _ in range(3)]
        >>> dat = dict(id=id3, val=val3, x=x, y=y)
        >>> ggplot(dat, aes('x', 'y')) + geom_polygon(aes(fill='id'), alpha=0.5)

    """

    if is_geocoder(map):
        map_join = auto_join_geocoder(map_join, map)
        map = map.get_boundaries()

    return _geom('polygon',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 map=map, map_join=map_join,
                 **other_args)


def geom_map(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
             map=None, map_join=None,
             **other_args):
    """
    Display polygons from a reference map.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `GeoDataFrame` (supported shapes `Polygon` and `MultiPolygon`), optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    map : `GeoDataFrame` (supported shapes `Polygon` and `MultiPolygon`) or `Geocoder` (implicitly invoke `boundaries()`), optional
        Data containing region boundaries (coordinates of polygon vertices on map).
    map_join : str or list, optional
        Keys used to join map coordinates with data.
        If the parameter type is list then it should be a pair of values,
        either of str type or a list of strings.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_map()` draws polygons which boundaries are specified by `map` parameter.
    Aesthetics of ploygons (`fill` etc.) are computed basing on input data and mapping
    (see `data` and `mapping` arguments).

    `geom_map()` understands the following aesthetics:
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - fill : color of a geometry internals. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - size : lines width. Defines line width.
        - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Note
    ----
    The conventions for the values of `map_join` parameter are as follows.

    When `map` is a `GeoDataFrame`:
        - map_join='state':
          same as [['state'], ['state']].
        - map_join=[['city', 'state']]:
          same as [['city', 'state'], ['city', 'state']].
        - map_join=[['City_Name', 'State_Name'], ['city', 'state']]:
          explicitly set keys for both data and map.

    If `map` is a `Geocoder`:
        - map_join='State_Name':
          same as [['State_Name'], ['state']].
        - map_join=['City_Name', 'State_Name']:
          same as [['City_Name', 'State_Name'], ['city', 'state']].
        - map_join=[['City_Name', 'State_Name'], ['city', 'state']]:
          explicitly set keys for both data and map.

    Generated keys follow this order: 'city', 'county', 'state', 'country'.
    Parents that were not provided will be omitted.
    Data columns should follow the same order or result of join operation will be incorrect.

    Examples
    --------
    .. jupyter-execute::

        >>> import pandas as pd
        >>> import numpy as np
        >>> from lets_plot import *
        >>> import lets_plot.geo_data as gd
        >>> LetsPlot.setup_html()
        >>> boundaries = gd.geocode_states(['Texas', 'Iowa', 'Arizona']).scope('US-48').get_boundaries()
        >>> regions = np.unique(boundaries['found name'])
        >>> num_of_regions = len(regions)
        >>> df = pd.DataFrame(regions, columns=['state'])
        >>> df['value'] = np.random.rand(num_of_regions)
        >>> ggplot(df) + ggtitle('Randomly colored states') + geom_map(aes(fill='value'), map=boundaries, map_join=('state', 'found name'), color='white')

    """

    if is_geocoder(map):
        map_join = auto_join_geocoder(map_join, map)
        map = map.get_boundaries()

    return _geom('map',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 map=map, map_join=map_join,
                 **other_args)


def geom_abline(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                slope=None,
                intercept=None,
                **other_args):
    """
    Add a straight line with specified slope and intercept to the plot.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    slope : float, optional
        The line slope.
    intercept : float, optional
        The value of y at the point where the line crosses the y axis.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_abline()` understands the following aesthetics mappings:
        - slope : line slope.
        - intercept : line y-intercept.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - size : lines width. Defines line width.
        - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Examples
    --------
    .. jupyter-execute::

        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> ggplot() + geom_abline(intercept=1, slope=3, color='red', linetype='dashed', size=3)

    """
    return _geom('abline',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 slope=slope,
                 intercept=intercept,
                 **other_args)


def geom_hline(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
               yintercept=None,
               **other_args):
    """
    Add a straight horizontal line to the plot.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    yintercept : float, optional
        The value of y at the point where the line crosses the y axis.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_hline()` understands the following aesthetics mappings:
        - yintercept : line y-intercept.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - size : lines width. Defines line width.
        - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Examples
    --------
    .. jupyter-execute::

        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> ggplot() + geom_hline(yintercept=0.2, color='dark_blue', linetype='longdash', size=2)

    """
    return _geom('hline',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 yintercept=yintercept,
                 **other_args)


def geom_vline(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
               xintercept=None,
               **other_args):
    """
    Add a straight vertical line to the plot.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    xintercept : float, optional
        The value of x at the point where the line crosses the x axis.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_hline()` understands the following aesthetics mappings:
        - xintercept : line x-intercept.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - size : lines width. Defines line width.
        - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Examples
    --------
    .. jupyter-execute::

        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> ggplot() + geom_vline(xintercept=0.2, color='dark_green', linetype='dotdash', size=2)

    """
    return _geom('vline',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 xintercept=xintercept,
                 **other_args)


def geom_boxplot(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                 fatten=None,
                 outlier_color=None, outlier_fill=None, outlier_shape=None, outlier_size=None,
                 varwidth=None,
                 **other_args):
    """
    Display the distribution of data based on a five number summary
    ("minimum", first quartile (Q1), median, third quartile (Q3), and "maximum"),
    and "outlying" points individually.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='boxplot'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    fatten : float, default=1.0
        A multiplicative factor applied to size of the middle bar.
    outlier_color : str, optional
        Default color aesthetic for outliers.
    outlier_fill : str, optional
        Default fill aesthetic for outliers.
    outlier_shape : int, optional
        Default shape aesthetic for outliers.
    outlier_size : float, optional
        Default size aesthetic for outliers.
    varwidth : bool, default=False
        If False make a standard box plot.
        If True, boxes are drawn with widths proportional to the square-roots
        of the number of observations in the groups.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_boxplot()` understands the following aesthetics mappings:
        - lower : lower hinge, 25% quantile.
        - middle : median, 50% quantile.
        - upper : upper hinge, 75% quantile.
        - ymin : lower whisker = smallest observation greater than or equal to lower hinge - 1.5 * IQR.
        - ymax : upper whisker = largest observation less than or equal to upper hinge + 1.5 * IQR.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - fill : color of geometry filling.
        - size : lines width.
        - linetype : type of the line of border. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.
        - width : width of boxplot [0..1].

    Examples
    --------
    .. jupyter-execute::

        >>> from pandas import DataFrame
        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> np.random.seed(123)
        >>> data = DataFrame(dict(
        >>>     cond=np.repeat(['A','B'], 200),
        >>>     rating=np.concatenate((np.random.normal(0, 1, 200), np.random.normal(.8, 1, 200)))
        >>> ))
        >>> p = ggplot(data, aes(x='cond', y='rating')) + ggsize(300, 200)
        >>> p += geom_boxplot(outlier_color='red', outlier_shape=8, outlier_size=5)
        >>> p

    """
    return _geom('boxplot',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 fatten=fatten,
                 outlier_color=outlier_color,
                 outlier_fill=outlier_fill,
                 outlier_shape=outlier_shape,
                 outlier_size=outlier_size,
                 varwidth=varwidth,
                 **other_args)


def geom_ribbon(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                **other_args):
    """
    Display a y interval defined by `ymin` and `ymax`.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_ribbon()` draws a ribbon bounded by `ymin` and `ymax`.

    `geom_ribbon()` understands the following aesthetics mappings:
        - x : x-axis coordinates.
        - ymin : y-axis coordinates of the lower bound.
        - ymax : y-axis coordinates of the upper bound.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - fill : color of geometry filling.
        - size : lines width. Defines line width.
        - linetype : type of the line of border. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Examples
    --------
    .. jupyter-execute::

        >>> import numpy as np
        >>> import pandas as pd
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> id = ["A", "A", "A", "B", "B", "B"]
        >>> x = [1, 2, 4, 1, 3, 4]
        >>> ymin = [-1, 0, 0, 3, 3, 4]
        >>> ymax = [0, 1, 1, 4, 5, 5]
        >>> dat = dict(id=id, x=x, ymin=ymin, ymax=ymax)
        >>> ggplot(dat) + geom_ribbon(aes(x='x', ymin='ymin', ymax='ymax', group='id', fill='id'), color='black', alpha=0.5)

    """
    return _geom('ribbon',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 **other_args)


def geom_area(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
              **other_args):
    """
    Display the development of quantitative values over an interval.
    This is the continuous analog of geom_bar.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_area()` draws an area bounded by the data and x axis.

    `geom_area()` understands the following aesthetics mappings:
        - x : x-axis coordinates.
        - y : y-axis coordinates.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - fill : color of geometry filling.
        - size : lines width. Defines line width.
        - linetype : type of the line of border. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Examples
    --------
    .. jupyter-execute::

        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> x = [1,3,4,1,3,4]
        >>> y = [1,1,2,3,4,2]
        >>> g = [1,1,1,2,2,2]
        >>> dat = dict(x=x, y=y, g=g)
        >>> ggplot(dat,aes('x','y', group='g')) + geom_area(aes(fill='g', color='g'), alpha=.2) + scale_fill_discrete() + scale_color_discrete()

    """
    return _geom('area',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 **other_args)


def geom_density(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                 kernel=None,
                 adjust=None,
                 bw=None,
                 n=None,
                 fs_max=None,
                 **other_args):
    """
    Displays kernel density estimate, which is a smoothed version of the histogram.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='density'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    kernel : str, default='gaussian'
        The kernel we use to calculate the density function.
        Choose among 'gaussian', 'cosine', 'optcosine', 'rectangular' (or 'uniform'),
        'triangular', 'biweight' (or 'quartic'), 'epanechikov' (or 'parabolic').
    bw : str or float, optional
        The method (or exact value) of bandwidth.
        Either a string (choose among 'nrd0' and 'nrd'), or a float.
    adjust : float, optional
        Adjust the value of bandwidth my multiplying it. Changes how smooth the frequency curve is.
    n : int, default=512
        The number of sampled points for plotting the function.
    fs_max : int, default=500
        Maximum size of data to use density computation with 'full scan'.
        For bigger data, less accurate but more efficient density computation is applied.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    Density stat computes variables:
        - ..density.. : density estimate (mapped by default).
        - ..count.. : density * number of points.
        - ..scaled.. : density estimate, scaled to maximum of 1.

    `geom_density()` understands the following aesthetics mappings:
        - x : x-axis coordinates.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - fill : color of geometry filling.
        - size : lines width. Defines line width.
        - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.
        - weight : used by 'density' stat to compute weighted density.

    Examples
    --------
    .. jupyter-execute::

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> x = np.random.normal(0,1,1000)
        >>> dat = dict(x=x)
        >>> ggplot(dat,aes('x')) + geom_density()

    """
    return _geom('density',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 kernel=kernel, adjust=adjust, bw=bw, n=n, fs_max=fs_max,
                 **other_args)


def geom_density2d(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                   kernel=None,
                   adjust=None,
                   bw=None,
                   n=None,
                   bins=None,
                   binwidth=None,
                   **other_args):
    """
    Display density function contour.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='density2d'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    kernel : str, default='gaussian'
        The kernel we use to calculate the density function.
        Choose among 'gaussian', 'cosine', 'optcosine', 'rectangular' (or 'uniform'),
        'triangular', 'biweight' (or 'quartic'), 'epanechikov' (or 'parabolic').
    bw : str or list of float, optional
        The method (or exact value) of bandwidth.
        Either a string (choose among 'nrd0' and 'nrd'), or a float array of length 2.
    adjust : float, optional
        Adjust the value of bandwidth my multiplying it. Changes how smooth the frequency curve is.
    n : list of int, optional
        The number of sampled points for plotting the function
        (on x and y direction correspondingly).
    bins : int, optional
        Number of levels.
    binwidth : float, optional
        Distance between levels.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_density2d()` draws density function.

    `geom_density2d()` understands the following aesthetics mappings:
        - x : x-axis coordinates.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - size : lines width. Defines line width.
        - linetype : type of the line of border. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Examples
    --------
    .. jupyter-execute::

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> x = np.random.normal(0,1,1000)
        >>> y = np.random.normal(0,1,1000)
        >>> dat = dict(x=x, y=y)
        >>> ggplot(dat,aes('x', 'y')) + geom_density2d()

    """
    return _geom('density2d',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 kernel=kernel, adjust=adjust, bw=bw, n=n, bins=bins, binwidth=binwidth,
                 **other_args)


def geom_density2df(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None,
                    tooltips=None,
                    kernel=None,
                    adjust=None,
                    bw=None,
                    n=None,
                    bins=None,
                    binwidth=None,
                    **other_args):
    """
    Fill density function contour.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='density2df'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    kernel : str, default='gaussian'
        The kernel we use to calculate the density function.
        Choose among 'gaussian', 'cosine', 'optcosine', 'rectangular' (or 'uniform'),
        'triangular', 'biweight' (or 'quartic'), 'epanechikov' (or 'parabolic').
    bw : str or list of float, optional
        The method (or exact value) of bandwidth.
        Either a string (choose among 'nrd0' and 'nrd'), or a float array of length 2.
    adjust : float, optional
        Adjust the value of bandwidth my multiplying it. Changes how smooth the frequency curve is.
    n : list of int, optional
        The number of sampled points for plotting the function
        (on x and y direction correspondingly).
    bins : int, optional
        Number of levels.
    binwidth : float, optional
        Distance between levels.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_density2df()` fills density contours.

    `geom_density2df()` understands the following aesthetics mappings:
        - x : x-axis coordinates.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - fill : color of geometry filling.

    Examples
    --------
    .. jupyter-execute::

        >>> import numpy as np
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> x = np.random.normal(0,1,1000)
        >>> y = np.random.normal(0,1,1000)
        >>> dat = dict(x=x, y=y)
        >>> ggplot(dat,aes('x', 'y')) + geom_density2df(aes(fill='..level..'))

    """
    return _geom('density2df',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 kernel=kernel,
                 adjust=adjust,
                 bw=bw, n=n,
                 bins=bins,
                 binwidth=binwidth,
                 **other_args)


def geom_jitter(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                width=None,
                height=None,
                **other_args):
    """
    Display jittered points, especially for discrete plots or dense plots.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    width : float, default=0.4
        Width for jitter.
    height : float, default=0.4
        Height for jitter.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    The jitter geometry is used to create jittered points.
    The scatterplot is useful for displaying the relationship between two discrete variables.

    `geom_jitter()` understands the following aesthetics mappings:
        - x : x-axis value.
        - y : y-axis value.
        - alpha : transparency level of a point. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - fill : color to paint shape's inner points. Is applied only to the points of shapes having inner points.
        - shape : shape of the point.
        - size : size of the point.

    Examples
    --------
    .. jupyter-execute::

        >>> import numpy as np
        >>> import pandas as pd
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> x = np.random.randint(3, size=100)
        >>> y = np.random.normal(size=100)
        >>> dat = pd.DataFrame({'x': x, 'y': y})
        >>> p = ggplot(dat) + geom_jitter(aes(x='x', y='y', color='x'), height=0)
        >>> p

    """
    return _geom('jitter',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 width=width, height=height, **other_args)


def geom_freqpoly(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                  **other_args):
    """
    Display a line chart which makes the y value proportional to the number
    of observed variable values, mapped to x axis.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='bin'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_freqpoly()` connects the top points in `geom_bar()`.

    `geom_freqpoly()` understands the following aesthetics mappings:
        - x : x-axis value.
        - y : y-axis value.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - size : lines width.
        - linetype : type of the line of border. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Examples
    --------
    .. jupyter-execute::

        >>> import numpy as np
        >>> import pandas as pd
        >>> from scipy.stats import multivariate_normal
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> N = 100
        >>> M = 3
        >>> mean = np.zeros(M)
        >>> mean = np.arange(M) * 5
        >>> cov = np.eye(M)
        >>> X = multivariate_normal.rvs(mean, cov, N)
        >>> X = X.astype(int) # comment this line to make variables continuous back
        >>> dat = pd.DataFrame(X)
        >>> dat = pd.melt(dat)
        >>> ggplot(dat, aes(x='value')) + geom_freqpoly(size=2)

    """
    return _geom('freqpoly',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 **other_args)


def geom_step(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
              direction=None,
              **other_args):
    """
    Connect observations in the order in which they appear in the data by stairs.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    direction : string, default='hv'
        'hv' or 'HV' stands for horizontal then vertical;
        'vh' or 'VH' stands for vertical then horizontal.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_step()` draws steps between the observations in the order of X.

    `geom_step()` understands the following aesthetics mappings:
        - x : x-axis value.
        - y : y-axis value.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - size : line width.
        - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Examples
    --------
    .. jupyter-execute::

        >>> import numpy as np
        >>> import pandas as pd
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> T = 1
        >>> N = 1000
        >>> t = np.linspace(0, T, N)
        >>> dt = T / N
        >>> # brownian motions
        >>> W1 = np.random.standard_normal(size=N)
        >>> Wt1 = np.cumsum(W1) * np.sqrt(dt)
        >>> W2 = np.random.standard_normal(size=N)
        >>> Wt2 = np.cumsum(W2) * np.sqrt(dt)
        >>> dat = {}
        >>> dat['W1'] = Wt1
        >>> dat['W2'] = Wt2
        >>> dat['t'] = t
        >>> # transform data via melt function
        >>> # to produce two trajectories
        >>> dat = pd.DataFrame(dat)
        >>> dat = pd.melt(dat, id_vars=['t'], value_vars=['W1', 'W2'])
        >>> ggplot(dat, aes(x='t', y='value', group='variable')) + geom_step(aes(color='variable'), size=1, alpha=0.7)

    """
    return _geom('step',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 direction=direction,
                 **other_args)


def geom_rect(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
              map=None, map_join=None,
              **other_args):
    """
    Display an axis-aligned rectangle defined by two corners.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `GeoDataFrame` (supported shapes `MultiPoint`, `Line`, `MultiLine`, `Polygon` and `MultiPolygon`), optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    map : `GeoDataFrame` (supported shapes `MultiPoint`, `Line`, `MultiLine`, `Polygon` and `MultiPolygon`) or `Geocoder` (implicitly invoke `limits()`), optional
        Bounding boxes of geometries will be drawn.
    map_join : str or list, optional
        Keys used to join map coordinates with data.
        If the parameter type is list then it should be a pair of values,
        either of str type or a list of strings.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_rect()` draws rectangles.

    `geom_rect()` understands the following aesthetics mappings:
        - xmin : x-axis value.
        - xmax : x-axis value.
        - ymin : y-axis value.
        - ymax : y-axis value.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - fill : color of geometry filling.
        - size : lines width. Defines line width.
        - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Note
    ----
    The conventions for the values of `map_join` parameter are as follows.

    When `map` is a `GeoDataFrame`:
        - map_join='state':
          same as [['state'], ['state']].
        - map_join=[['city', 'state']]:
          same as [['city', 'state'], ['city', 'state']].
        - map_join=[['City_Name', 'State_Name'], ['city', 'state']]:
          explicitly set keys for both data and map.

    If `map` is a `Geocoder`:
        - map_join='State_Name':
          same as [['State_Name'], ['state']].
        - map_join=['City_Name', 'State_Name']:
          same as [['City_Name', 'State_Name'], ['city', 'state']].
        - map_join=[['City_Name', 'State_Name'], ['city', 'state']]:
          explicitly set keys for both data and map.

    Generated keys follow this order: 'city', 'county', 'state', 'country'.
    Parents that were not provided will be omitted.
    Data columns should follow the same order or result of join operation will be incorrect.

    Examples
    --------
    .. jupyter-execute::

        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> ggplot() + geom_rect(aes(xmin=[3], xmax=[4], ymin=[6], ymax=[10]), alpha=0.5, color='black', size=1)

    """

    if is_geocoder(map):
        map_join = auto_join_geocoder(map_join, map)
        map = map.get_limits()

    return _geom('rect',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 map=map, map_join=map_join,
                 **other_args)


def geom_segment(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, arrow=None,
                 tooltips=None, **other_args):
    """
    Draw a straight line segment between two points.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    arrow : `FeatureSpec`, optional
        Specification for arrow head, as created by `arrow()` function.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    `geom_segment()` draws segments.

    `geom_segment()` understands the following aesthetics mappings:
        - x : x-axis value.
        - y : y-axis value.
        - xend : x-axis value.
        - yend : y-axis value.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - size : line width.
        - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Examples
    --------
    .. jupyter-execute::

        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> ggplot() + geom_segment(aes(x=[3], y=[6], xend=[4], yend=[10]))

    """
    return _geom('segment',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 arrow=arrow,
                 **other_args)


def geom_text(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
              map=None, map_join=None,
              label_format=None,
              na_text=None,
              **other_args):
    """
    Add a text directly to the plot.

    Parameters
    ----------
    mapping : `FeatureSpec`, optional
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, optional
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`, optional
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`, optional
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    map : `GeoDataFrame` (supported shapes `Point` and `MultiPoint`) or `Geocoder` (implicitly invoke `centroids()`), optional
        Data containing coordinates of points.
    map_join : str or list, optional
        Keys used to join map coordinates with data.
        If the parameter type is list then it should be a pair of values,
        either of str type or a list of strings.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    label_format : str, optional
        Format used to transform label mapping values to a string.
        Examples:
        '.2f' -> '12.45',
        'Num {}' -> 'Num 12.456789',
        'TTL: {.2f}$' -> 'TTL: 12.45$'.
    na_text : str, default='n/a'
        Text to show for missing values.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Note
    ----
    Adds text directly to the plot.

    `geom_text()` understands the following aesthetics mappings:
        - x : x-axis value.
        - y : y-axis value.
        - alpha : transparency level of a layer. Understands numbers between 0 and 1.
        - color (colour) : color of a geometry. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
        - size : font size.
        - label : text to add to plot.
        - family : tont family. Possible values: 'sans', 'serif', 'mono', any other like: "Times New Roman". The default is 'sans'.
        - fontface : font style and weight. Possible values: 'plain', 'bold', 'italic', 'bold italic'. The default is 'plain'.
        - hjust : horizontal text alignment. Possible values: 'left', 'middle', 'right' or number between 0 ('right') and 1 ('left').
        - vjust : vertical text alignment. Possible values: 'bottom', 'center', 'top' or number between 0 ('bottom') and 1 ('top').
        - angle : text rotation angle in degrees.

    Note
    ----
    The conventions for the values of `map_join` parameter are as follows.

    When `map` is a `GeoDataFrame`:
        - map_join='state':
          same as [['state'], ['state']].
        - map_join=[['city', 'state']]:
          same as [['city', 'state'], ['city', 'state']].
        - map_join=[['City_Name', 'State_Name'], ['city', 'state']]:
          explicitly set keys for both data and map.

    If `map` is a `Geocoder`:
        - map_join='State_Name':
          same as [['State_Name'], ['state']].
        - map_join=['City_Name', 'State_Name']:
          same as [['City_Name', 'State_Name'], ['city', 'state']].
        - map_join=[['City_Name', 'State_Name'], ['city', 'state']]:
          explicitly set keys for both data and map.

    Generated keys follow this order: 'city', 'county', 'state', 'country'.
    Parents that were not provided will be omitted.
    Data columns should follow the same order or result of join operation will be incorrect.

    Examples
    --------
    .. jupyter-execute::

        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> ggplot() + geom_text(aes(x=[1], y=[1], label=['Text'], angle=[30], family=['mono']), size = 10)

    """

    if is_geocoder(map):
        map_join = auto_join_geocoder(map_join, map)
        map = map.get_centroids()

    return _geom('text',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 map=map, map_join=map_join,
                 label_format=label_format,
                 na_text=na_text,
                 **other_args)


def _geom(name, *,
          mapping=None,
          data=None,
          stat=None,
          position=None,
          show_legend=None,
          sampling=None,
          tooltips=None,
          **kwargs):
    if mapping:
        if not (isinstance(mapping, FeatureSpec) and mapping.kind == 'mapping'):
            raise ValueError("Unexpected value for argument 'mapping'. Hint: try to use function aes()")

    data, mapping, data_meta = as_annotated_data(data, mapping)

    if is_geocoder(data):
        data = data.get_geocodes()

    if is_geo_data_frame(data):
        data = geo_data_frame_to_wgs84(data)

    if is_geo_data_frame(kwargs.get('map', None)):
        kwargs['map'] = geo_data_frame_to_wgs84(kwargs['map'])

    map_data_meta = as_annotated_map_data(kwargs.get('map', None))

    kwargs['map_join'] = as_map_join(
        kwargs.get('map_join', None)
    )

    return LayerSpec(geom=name,
                     stat=stat,
                     data=data,
                     mapping=mapping,
                     position=position,
                     show_legend=show_legend,
                     sampling=sampling,
                     tooltips=tooltips,
                     **data_meta,
                     **map_data_meta,
                     **kwargs)
