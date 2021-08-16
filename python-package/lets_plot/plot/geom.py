#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec, LayerSpec
from .util import as_annotated_data, as_annotated_map_data, is_geo_data_frame, auto_join_geo_names, \
    geo_data_frame_to_wgs84, normalize_map_join, get_geo_data_frame_meta
from lets_plot.geo_data_internals.utils import is_geocoder

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `GeoDataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    map : `GeoDataFrame` or `Geocoder`
        Data containing coordinates of points.
    map_join : str or list
        Keys used to join map coordinates with data.
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
    The `data` and `map` parameters of `GeoDataFrame` type support shapes `Point` and `MultiPoint`.

    The `map` parameter of `Geocoder` type implicitly invoke `centroids()` function.

    Note
    ----
    The conventions for the values of `map_join` parameter are as follows.

    - Joining data and `GeoDataFrame` object

      Data has a column named 'State_name' and `GeoDataFrame` has a matching column named 'state':

      - map_join=['State_Name', 'state']
      - map_join=[['State_Name'], ['state']]

    - Joining data and `Geocoder` object

      Data has a column named 'State_name'. The matching key in `Geocoder` is always 'state' (providing it is a state-level geocoder) and can be omitted:

      - map_join='State_Name'
      - map_join=['State_Name']

    - Joining data by composite key

      Joining by composite key works like in examples above, but instead of using a string for a simple key you need to use an array of strings for a composite key. The names in the composite key must be in the same order as in the US street addresses convention: 'city', 'county', 'state', 'country'. For example, the data has columns 'State_name' and 'County_name'. Joining with a 2-keys county level `Geocoder` object (the `Geocoder` keys 'county' and 'state' are omitted in this case):

      - map_join=['County_name', 'State_Name']

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        x = np.linspace(-2 * np.pi, 2 * np.pi, 100)
        y = np.sin(x)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + geom_point()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 100
        x = np.random.uniform(-1, 1, size=n)
        y = 25 * x ** 2 + np.random.normal(size=n)
        ggplot({'x': x, 'y': y}) + \\
            geom_point(aes(x='x', y='y', fill='y'), \\
                       shape=21, size=5, color='white')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8-11

        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        data = {'city': ['New York', 'Los Angeles', 'Chicago'], \\
                'est_pop_2019': [8_336_817, 3_979_576, 2_693_976]}
        centroids = geocode_cities(data['city']).get_centroids()
        ggplot() + geom_livemap() + \\
            geom_point(aes(size='est_pop_2019'), \\
                       data=data, map=centroids, map_join='city', \\
                       tooltips=layer_tooltips().line('@city')
                                                .line('population|@est_pop_2019')) + \\
            ggsize(600, 450)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7-8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {var: np.random.normal(size=10) for var in 'abcdef'}
        ggplot(data) + \\
            geom_point(aes(size='..corr_abs..', fill='..corr..'), \\
                       stat='corr', shape=21, color='black') + \\
            scale_size(range=(.1, .6), guide='none') + \\
            scale_fill_gradient2(low='#2166ac', mid='#f7f7f7', high='#b2182b')

    """
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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `GeoDataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    map : `GeoDataFrame`
        Data containing coordinates of lines.
    map_join : str or list
        Keys used to join map coordinates with data.
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
    `geom_path()` connects the observations in the order in which they appear in the data.
    `geom_path()` lets you explore how two variables are related over time.

    `geom_path()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Understands numbers between 0 and 1.
    - color (colour) : color of a geometry. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash.
    - size : line width.

    Note
    ----
    The `data` and `map` parameters of `GeoDataFrame` type support shapes `LineString` and `MultiLineString`.

    Note
    ----
    The conventions for the values of `map_join` parameter are as follows.

    - Joining data and `GeoDataFrame` object

      Data has a column named 'State_name' and `GeoDataFrame` has a matching column named 'state':

      - map_join=['State_Name', 'state']
      - map_join=[['State_Name'], ['state']]

    - Joining data and `Geocoder` object

      Data has a column named 'State_name'. The matching key in `Geocoder` is always 'state' (providing it is a state-level geocoder) and can be omitted:

      - map_join='State_Name'
      - map_join=['State_Name']

    - Joining data by composite key

      Joining by composite key works like in examples above, but instead of using a string for a simple key you need to use an array of strings for a composite key. The names in the composite key must be in the same order as in the US street addresses convention: 'city', 'county', 'state', 'country'. For example, the data has columns 'State_name' and 'County_name'. Joining with a 2-keys county level `Geocoder` object (the `Geocoder` keys 'county' and 'state' are omitted in this case):

      - map_join=['County_name', 'State_Name']

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        t = np.linspace(0, 2 * np.pi, n)
        data = {'x': t * np.sin(t), 'y': t * np.cos(t)}
        ggplot(data, aes(x='x', y='y')) + geom_path()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        T = 50
        np.random.seed(42)
        x = np.cumsum(np.random.normal(size=2*T))
        y = np.cumsum(np.random.normal(size=2*T))
        c = [0] * T + [1] * T
        data = {'x': x, 'y': y, 'c': c}
        ggplot(data, aes(x='x', y='y', group='c')) + \\
            geom_path(aes(color='c'), size=2, alpha=.5) + \\
            scale_color_discrete()

    """
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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
        :linenos:
        :emphasize-lines: 6

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        x = np.linspace(-4 * np.pi, 4 * np.pi, 100)
        y = np.sin(x)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + geom_line()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12-13

        import numpy as np
        import pandas as pd
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        t = np.arange(100)
        x1 = np.cumsum(np.random.normal(size=t.size))
        x2 = np.cumsum(np.random.normal(size=t.size))
        df = pd.DataFrame({'t': t, 'x1': x1, 'x2': x2})
        df = pd.melt(df, id_vars=['t'], value_vars=['x1', 'x2'])
        ggplot(df, aes(x='t', y='value', group='variable')) + \\
            geom_line(aes(color='variable'), size=1, alpha=0.5) + \\
            geom_line(stat='smooth', color='red', linetype='longdash')

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='smooth'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    method : str, default='lm'
        Smoothing method: 'lm' (Linear Model) or 'loess' (Locally Estimated Scatterplot Smoothing).
    n : int
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
    seed : int
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

    - ..y.. : predicted (smoothed) value.
    - ..ymin.. : lower pointwise confidence interval around the mean.
    - ..ymax.. : upper pointwise confidence interval around the mean.
    - ..se.. : standard error.

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
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 50
        x = np.arange(n)
        y = x + np.random.normal(scale=10, size=n)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            geom_point() + geom_smooth()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 100
        x = np.linspace(-2, 2, n)
        y = x ** 2 + np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            geom_point() + geom_smooth(color='red', deg=2, se=False)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 14-15

        import numpy as np
        import pandas as pd
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        t = np.linspace(0, 1, 100)
        mean = 1 + np.zeros(2)
        cov = np.eye(2)
        x, y = np.random.multivariate_normal(mean, cov, t.size).T
        df = pd.DataFrame({'t': t, 'x': x, 'y': y})
        df = df.melt(id_vars=['t'], value_vars=['x', 'y'])
        ggplot(df, aes(x='t', y='value', group='variable')) + \\
            geom_point(aes(color='variable'), size=3, alpha=.5) + \\
            geom_smooth(aes(color='variable'), size=1, \\
                        method='loess', span=.3, level=.7, seed=42)

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='count'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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

    Computed variables:

    - ..count.. : number of points with same x-axis coordinate.

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
        :linenos:
        :emphasize-lines: 6

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {'x': np.random.randint(10, size=100)}
        ggplot(data, aes(x='x')) + geom_bar()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 10
        x = np.arange(n)
        y = 1 + np.random.randint(5, size=n)
        ggplot() + \\
            geom_bar(aes(x='x', y='y', fill='x'), data={'x': x, 'y': y}, \\
                     stat='identity', show_legend=False) + \\
            scale_fill_discrete()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-12

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 5000
        x = np.random.normal(size=n)
        c = np.random.choice(list('abcde'), size=n)
        ggplot({'x': x, 'class': c}, aes(x='x')) + \\
            geom_bar(aes(group='class', fill='class', color='class'), \\
                     stat='bin', sampling=sampling_pick(n=500), alpha=.3, \\
                     tooltips=layer_tooltips().line('@|@class')
                                              .line('count|@..count..'))

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
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
    sampling : `FeatureSpec`
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    bins : int, default=30
        Number of bins. Overridden by `binwidth`.
    binwidth : float
        The width of the bins. The default is to use bin widths that cover
        the range of the data. You should always override this value,
        exploring multiple widths to find the best to illustrate the stories in your data.
    center : float
        Specifies x-value to align bin centers to.
    boundary : float
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

    Computed variables:

    - ..count.. : number of points with x-axis coordinate in the same bin.

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
        :linenos:
        :emphasize-lines: 6

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {'x': np.random.normal(size=1000)}
        ggplot(data, aes(x='x')) + geom_histogram()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {'x': np.random.gamma(2.0, size=1000)}
        ggplot(data, aes(x='x')) + \\
            geom_histogram(aes(color='x', fill='x'), bins=50)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8-10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        x = np.random.normal(scale=3, size=1000)
        y = 2 * (np.round(x) % 2) - 1
        ggplot({'x': x, 'y': y}) + \\
            geom_histogram(aes(x='x', weight='y'), \\
                           center=0, binwidth=1, \\
                           color='black', fill='gray', size=1)

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='bin2d'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, default='stack'
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
    bins : list of int, default=[30, 30]
        Number of bins in both directions, vertical and horizontal. Overridden by `binwidth`.
    binwidth : list of float
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

    Computed variables:

    - ..count.. : number of points with coordinates in the same bin.

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
        :linenos:
        :emphasize-lines: 8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        mean = np.zeros(2)
        cov = np.eye(2)
        x, y = np.random.multivariate_normal(mean, cov, 1000).T
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + geom_bin2d()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-14

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 5000
        x = np.random.uniform(-2, 2, size=n)
        y = np.random.normal(scale=.5, size=n)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            geom_bin2d(aes(fill='..density..'), binwidth=[.25, .24], \\
                       tooltips=layer_tooltips().format('@x', '.2f')
                               .format('@y', '.2f').line('(@x, @y)')
                               .line('count|@..count..')
                               .format('@..density..', '.3f')
                               .line('density|@..density..')) + \\
            scale_fill_gradient(low='black', high='red')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10-11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        mean = np.zeros(2)
        cov = [[1, .5],
               [.5, 1]]
        x, y = np.random.multivariate_normal(mean, cov, 500).T
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            geom_bin2d(aes(alpha='..count..'), bins=[20, 20], \\
                       color='white', fill='darkgreen') + \\
            geom_point(size=1.5, shape=21, color='white', \\
                       fill='darkgreen') + \\
            ggsize(600, 450)

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
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
        :linenos:
        :emphasize-lines: 12

        import numpy as np
        from scipy.stats import multivariate_normal
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        a, b = -1, 0
        x = np.linspace(-3, 3, n)
        y = np.linspace(-3, 3, n)
        X, Y = np.meshgrid(x, y)
        Z = np.exp(-5 * np.abs(Y ** 2 - X ** 3 - a * X - b))
        data = {'x': X.flatten(), 'y': Y.flatten(), 'z': Z.flatten()}
        ggplot(data, aes(x='x', y='y', color='z', fill='z')) + geom_tile()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 17

        import numpy as np
        from scipy.stats import multivariate_normal
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 25
        x = np.linspace(-1, 1, n)
        y = np.linspace(-1, 1, n)
        X, Y = np.meshgrid(x, y)
        mean = np.zeros(2)
        cov = [[1, -.5],
               [-.5, 1]]
        rv = multivariate_normal(mean, cov)
        Z = rv.pdf(np.dstack((X, Y)))
        data = {'x': X.flatten(), 'y': Y.flatten(), 'z': Z.flatten()}
        ggplot(data, aes(x='x', y='y')) + \\
            geom_tile(aes(fill='z'), width=.8, height=.8, color='black') + \\
            scale_fill_gradient(low='yellow', high='darkgreen')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {var: np.random.uniform(size=10) for var in 'abcd'}
        ggplot(data) + \\
            geom_tile(aes(fill='..corr..'), stat='corr', tooltips='none', color='white') + \\
            geom_text(aes(label='..corr..'), stat='corr', color='white') + \\
            scale_fill_brewer(type='div', palette='RdBu', breaks=[-1, -.5, 0, .5, 1])

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


def geom_raster(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None,
                **other_args):
    """
    Display rectangles with x, y values mapped to the center of the tile.
    This is a high performance special function for same-sized tiles.
    Much faster than `geom_tile()` but doesn't support width/height and color.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
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
        :linenos:
        :emphasize-lines: 17

        import numpy as np
        from scipy.stats import multivariate_normal
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 25
        x = np.linspace(-1, 1, n)
        y = np.linspace(-1, 1, n)
        X, Y = np.meshgrid(x, y)
        mean = np.zeros(2)
        cov = [[1, -.5],
               [-.5, 1]]
        rv = multivariate_normal(mean, cov)
        Z = rv.pdf(np.dstack((X, Y)))
        data = {'x': X.flatten(), 'y': Y.flatten(), 'z': Z.flatten()}
        ggplot(data) + \\
            geom_raster(aes(x='x', y='y', fill='z')) + \\
            scale_fill_gradient(low='#54278f', high='#f2f0f7')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {var: np.random.uniform(size=10) for var in 'abcd'}
        ggplot(data) + \\
            geom_raster(aes(fill='..corr..'), stat='corr', tooltips='none') + \\
            geom_text(aes(label='..corr..'), stat='corr', color='white') + \\
            scale_fill_brewer(type='div', palette='RdBu', breaks=[-1, -.5, 0, .5, 1])

    """
    return _geom('raster',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 **other_args)


def geom_errorbar(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                  **other_args):
    """
    Display error bars defined by the upper and lower values.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
        :linenos:
        :emphasize-lines: 9

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'x': ['a', 'b', 'c', 'd'],
            'ymin': [5, 7, 3, 5],
            'ymax': [8, 11, 6, 9],
        }
        ggplot(data, aes(x='x')) + \\
            geom_errorbar(aes(ymin='ymin', ymax='ymax'))

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 13-14

        import numpy as np
        import pandas as pd
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 1000
        x = np.random.randint(10, size=n)
        y = np.sqrt(x) + np.random.normal(scale=.3, size=n)
        df = pd.DataFrame({'x': x, 'y': y})
        err_df = df.groupby('x').agg({'y': ['min', 'max']}).reset_index()
        err_df.columns = ['x', 'ymin', 'ymax']
        ggplot() + \\
            geom_errorbar(aes(x='x', ymin='ymin', ymax='ymax'), \\
                          data=err_df, width=.5, color='red') + \\
            geom_jitter(aes(x='x', y='y'), data=df, width=.2, size=1) + \\
            scale_x_continuous(breaks=list(range(10)))

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
        :linenos:
        :emphasize-lines: 10

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'x': ['a', 'b', 'c', 'd'],
            'ymin': [5, 7, 3, 5],
            'middle': [6.5, 9, 4.5, 7],
            'ymax': [8, 11, 6, 9],
        }
        ggplot(data, aes(x='x')) + \\
            geom_crossbar(aes(ymin='ymin', middle='middle', ymax='ymax'))

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 14-15

        import numpy as np
        import pandas as pd
        from lets_plot import *
        LetsPlot.setup_html()
        n = 800
        cat_list = {c: np.random.uniform(3) for c in 'abcdefgh'}
        np.random.seed(42)
        x = np.random.choice(list(cat_list.keys()), n)
        y = np.array([cat_list[c] for c in x]) + np.random.normal(size=n)
        df = pd.DataFrame({'x': x, 'y': y})
        err_df = df.groupby('x').agg({'y': ['min', 'median', 'max']}).reset_index()
        err_df.columns = ['x', 'ymin', 'ymedian', 'ymax']
        ggplot() + \\
            geom_crossbar(aes(x='x', ymin='ymin', middle='ymedian', ymax='ymax', fill='x'), \\
                          data=err_df, width=.6, fatten=5) + \\
            geom_jitter(aes(x='x', y='y'), data=df, width=.3, shape=1, color='black', alpha=.5)

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
        :linenos:
        :emphasize-lines: 10

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'x': ['a', 'b', 'c', 'd'],
            'ymin': [5, 7, 3, 5],
            'y': [6.5, 9, 4.5, 7],
            'ymax': [8, 11, 6, 9],
        }
        ggplot(data, aes(x='x', y='y')) + \\
            geom_pointrange(aes(ymin='ymin', ymax='ymax'))

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 14-16

        import numpy as np
        import pandas as pd
        from lets_plot import *
        LetsPlot.setup_html()
        n = 800
        cat_list = {c: np.random.uniform(3) for c in 'abcdefgh'}
        np.random.seed(42)
        x = np.random.choice(list(cat_list.keys()), n)
        y = np.array([cat_list[c] for c in x]) + np.random.normal(size=n)
        df = pd.DataFrame({'x': x, 'y': y})
        err_df = df.groupby('x').agg({'y': ['min', 'mean', 'max']}).reset_index()
        err_df.columns = ['x', 'ymin', 'ymean', 'ymax']
        ggplot(err_df, aes(x='x', y='ymean')) + \\
            geom_pointrange(aes(ymin='ymin', ymax='ymax', fill='x'), \\
                            show_legend=False, fatten=10, shape=4, \\
                            color='red', size=1)

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
        :linenos:
        :emphasize-lines: 9

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'x': ['a', 'b', 'c', 'd'],
            'ymin': [5, 7, 3, 5],
            'ymax': [8, 11, 6, 9],
        }
        ggplot(data, aes(x='x')) + \\
            geom_linerange(aes(ymin='ymin', ymax='ymax'))

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 14-15

        import numpy as np
        import pandas as pd
        from lets_plot import *
        LetsPlot.setup_html()
        n = 800
        cat_list = {c: np.random.uniform(3) for c in 'abcdefgh'}
        np.random.seed(42)
        x = np.random.choice(list(cat_list.keys()), n)
        y = np.array([cat_list[c] for c in x]) + np.random.normal(size=n)
        df = pd.DataFrame({'x': x, 'y': y})
        err_df = df.groupby('x').agg({'y': ['min', 'max']}).reset_index()
        err_df.columns = ['x', 'ymin', 'ymax']
        ggplot() + \\
            geom_linerange(aes(x='x', ymin='ymin', ymax='ymax', fill='x'), \\
                            data=err_df, show_legend=False, color='black', size=1) + \\
            geom_point(aes(x='x', y='y'), data=df, size=4, alpha=.1, color='black', \\
                       tooltips=layer_tooltips().line('@y'))

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='contour'
        The statistical transformation to use on the data for this layer, as a string.
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
    bins : int
        Number of levels.
    binwidth: float
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

    - ..level.. : height of a contour.

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
        :linenos:
        :emphasize-lines: 16

        import numpy as np
        from scipy.stats import multivariate_normal
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 25
        x = np.linspace(-1, 1, n)
        y = np.linspace(-1, 1, n)
        X, Y = np.meshgrid(x, y)
        mean = np.zeros(2)
        cov = [[1, .5],
               [.5, 1]]
        rv = multivariate_normal(mean, cov)
        Z = rv.pdf(np.dstack((X, Y)))
        data = {'x': X.flatten(), 'y': Y.flatten(), 'z': Z.flatten()}
        ggplot(data, aes(x='x', y='y', z='z')) + geom_contour()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        a, b = -1, 0
        x = np.linspace(-3, 3, n)
        y = np.linspace(-3, 3, n)
        X, Y = np.meshgrid(x, y)
        Z = np.exp(-5 * np.abs(Y ** 2 - X ** 3 - a * X - b))
        data = {'x': X.flatten(), 'y': Y.flatten(), 'z': Z.flatten()}
        ggplot(data, aes(x='x', y='y', z='z')) + \\
            geom_contour(aes(color='..level..'), bins=3, size=1) + \\
            scale_color_gradient(low='#dadaeb', high='#3f007d')

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='contourf'
        The statistical transformation to use on the data for this layer, as a string.
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
    bins : int
        Number of levels.
    binwidth: float
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

    - ..level.. : height of a contour.

    `geom_contourf()` understands the following aesthetics mappings:

    - x : x-axis coordinates of the center of rectangles, forming a tessellation.
    - y : y-axis coordinates of the center of rectangles, forming a tessellation.
    - alpha : transparency level of a layer. Understands numbers between 0 and 1.
    - fill : color of a geometry areas. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 16

        import numpy as np
        from scipy.stats import multivariate_normal
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 25
        x = np.linspace(-1, 1, n)
        y = np.linspace(-1, 1, n)
        X, Y = np.meshgrid(x, y)
        mean = np.zeros(2)
        cov = [[1, .5],
               [.5, 1]]
        rv = multivariate_normal(mean, cov)
        Z = rv.pdf(np.dstack((X, Y)))
        data = {'x': X.flatten(), 'y': Y.flatten(), 'z': Z.flatten()}
        ggplot(data, aes(x='x', y='y', z='z')) + geom_contourf()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        a, b = -1, 0
        x = np.linspace(-3, 3, n)
        y = np.linspace(-3, 3, n)
        X, Y = np.meshgrid(x, y)
        Z = np.exp(-5 * np.abs(Y ** 2 - X ** 3 - a * X - b))
        data = {'x': X.flatten(), 'y': Y.flatten(), 'z': Z.flatten()}
        ggplot(data, aes(x='x', y='y', z='z')) + \\
            geom_contourf(aes(fill='..level..'), bins=3, size=0) + \\
            scale_fill_gradient(low='#dadaeb', high='#3f007d')

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `GeoDataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
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
    map : `GeoDataFrame` or `Geocoder`
        Data contains coordinates of polygon vertices on map.
    map_join : str or list
        Keys used to join map coordinates with data.
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
    The `data` and `map` parameters of `GeoDataFrame` type support shapes `Polygon` and `MultiPolygon`.

    The `map` parameter of `Geocoder` type implicitly invoke `boundaries()` function.

    Note
    ----
    The conventions for the values of `map_join` parameter are as follows.

    - Joining data and `GeoDataFrame` object

      Data has a column named 'State_name' and `GeoDataFrame` has a matching column named 'state':

      - map_join=['State_Name', 'state']
      - map_join=[['State_Name'], ['state']]

    - Joining data and `Geocoder` object

      Data has a column named 'State_name'. The matching key in `Geocoder` is always 'state' (providing it is a state-level geocoder) and can be omitted:

      - map_join='State_Name'
      - map_join=['State_Name']

    - Joining data by composite key

      Joining by composite key works like in examples above, but instead of using a string for a simple key you need to use an array of strings for a composite key. The names in the composite key must be in the same order as in the US street addresses convention: 'city', 'county', 'state', 'country'. For example, the data has columns 'State_name' and 'County_name'. Joining with a 2-keys county level `Geocoder` object (the `Geocoder` keys 'county' and 'state' are omitted in this case):

      - map_join=['County_name', 'State_Name']

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 7
        t = np.linspace(0, 2 * np.pi, 2 * n + 1)
        r = np.concatenate((np.tile([1, .5], n), [1]))
        data = {'x': r * np.cos(t), 'y': r * np.sin(t)}
        ggplot(data, aes(x='x', y='y')) + \\
            geom_polygon() + \\
            coord_fixed()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 16-17

        import numpy as np
        import pandas as pd
        from scipy.spatial import Voronoi
        from lets_plot import *
        LetsPlot.setup_html()
        n = 30
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        df = pd.DataFrame({'x': x, 'y': y})
        v = Voronoi(list(zip(x, y)))
        v_df = pd.DataFrame([(i, *v.vertices[v_id]) for i, r in enumerate(v.regions) \\
                                                    for v_id in r if any(r) and not -1 in r],
                            columns=['id', 'x', 'y'])
        ggplot() + \\
            geom_polygon(aes(x='x', y='y', group='id', fill='id'), \\
                         data=v_df, show_legend=False, color='white') + \\
            geom_point(aes(x='x', y='y'), data=df, shape=21, color='black', fill='white') + \\
            scale_fill_discrete() + \\
            coord_fixed()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-12

        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        data = {'city': ['New York', 'Philadelphia'], \\
                'est_pop_2019': [8_336_817, 1_584_064]}
        boundaries = geocode_cities(data['city']).get_boundaries(resolution=15)
        ggplot() + \\
        geom_livemap() + \\
            geom_polygon(data=data, map=boundaries, map_join='city', \\
                         color='black', fill='black', alpha=.1, \\
                         tooltips=layer_tooltips().line('@city')\\
                                                  .line('population|@est_pop_2019'))

    """
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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `GeoDataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    map : `GeoDataFrame` or `Geocoder`
        Data containing region boundaries (coordinates of polygon vertices on map).
    map_join : str or list
        Keys used to join map coordinates with data.
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
    The `data` and `map` parameters of `GeoDataFrame` type support shapes `Polygon` and `MultiPolygon`.

    The `map` parameter of `Geocoder` type implicitly invoke `boundaries()` function.

    Note
    ----
    The conventions for the values of `map_join` parameter are as follows.

    - Joining data and `GeoDataFrame` object

      Data has a column named 'State_name' and `GeoDataFrame` has a matching column named 'state':

      - map_join=['State_Name', 'state']
      - map_join=[['State_Name'], ['state']]

    - Joining data and `Geocoder` object

      Data has a column named 'State_name'. The matching key in `Geocoder` is always 'state' (providing it is a state-level geocoder) and can be omitted:

      - map_join='State_Name'
      - map_join=['State_Name']

    - Joining data by composite key

      Joining by composite key works like in examples above, but instead of using a string for a simple key you need to use an array of strings for a composite key. The names in the composite key must be in the same order as in the US street addresses convention: 'city', 'county', 'state', 'country'. For example, the data has columns 'State_name' and 'County_name'. Joining with a 2-keys county level `Geocoder` object (the `Geocoder` keys 'county' and 'state' are omitted in this case):

      - map_join=['County_name', 'State_Name']

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5

        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        country = geocode_countries('Italy').get_boundaries(resolution=6)
        ggplot() + geom_map(data=country)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8-11

        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        data = {'city': ['New York', 'Philadelphia'], \\
                'est_pop_2019': [8_336_817, 1_584_064]}
        boundaries = geocode_cities(data['city']).get_boundaries()
        ggplot() + \\
            geom_map(data=data, map=boundaries, map_join='city', \\
                     color='#006d2c', fill='#edf8e9', size=.5, \\
                     tooltips=layer_tooltips().line('@city')\\
                                              .line('population|@est_pop_2019'))

    """
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


def geom_abline(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None,
                slope=None,
                intercept=None,
                **other_args):
    """
    Add a straight line with specified slope and intercept to the plot.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    slope : float
        The line slope.
    intercept : float
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
        :linenos:
        :emphasize-lines: 3

        from lets_plot import *
        LetsPlot.setup_html()
        ggplot() + geom_abline(slope=0)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 17-18

        import numpy as np
        import pandas as pd
        from lets_plot import *
        LetsPlot.setup_html()
        n, m = 10, 3
        np.random.seed(42)
        ids = np.arange(m).astype(str)
        x = np.linspace(0, 1, n)
        y = x + np.random.uniform(size=(m, n))
        df = pd.DataFrame({'id': np.repeat(ids, n),
                           'x': np.tile(x, m),
                           'y': y.reshape(m * n)})
        slope = np.corrcoef(y, x)[0, :-1] * y.std(axis=1) / x.std()
        intercept = y.mean(axis=1) - slope * x.mean()
        reg_df = pd.DataFrame({'id': ids, 'slope': slope, 'intercept': intercept})
        ggplot() + \\
            geom_abline(aes(slope='slope', intercept='intercept', color='id'), \\
                        data=reg_df, size=1, linetype='dashed') + \\
            geom_point(aes(x='x', y='y', color='id', fill='id'), \\
                       data=df, size=4, shape=21, alpha=.5)

    """
    return _geom('abline',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
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
    yintercept : float
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
        :linenos:
        :emphasize-lines: 3

        from lets_plot import *
        LetsPlot.setup_html()
        ggplot() + geom_hline(yintercept=0)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 15-16

        import numpy as np
        import pandas as pd
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        classes = ['a', 'b', 'c']
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        c = np.random.choice(classes, size=n)
        df = pd.DataFrame({'x': x, 'y': y, 'c': c})
        bounds_df = pd.DataFrame([(cl, df[df.c == cl].y.max()) for cl in classes], \\
                                 columns=['c', 'ymax'])
        ggplot() + \\
            geom_hline(aes(yintercept='ymax', color='c'), \\
                       data=bounds_df, size=.2, linetype='longdash') + \\
            geom_point(aes(x='x', y='y', color='c'), data=df)

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
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
    xintercept : float
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
        :linenos:
        :emphasize-lines: 3

        from lets_plot import *
        LetsPlot.setup_html()
        ggplot() + geom_vline(xintercept=0)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 15-16

        import numpy as np
        import pandas as pd
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        classes = ['a', 'b', 'c']
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        c = np.random.choice(classes, size=n)
        df = pd.DataFrame({'x': x, 'y': y, 'c': c})
        bounds_df = pd.DataFrame([(cl, df[df.c == cl].x.max()) for cl in classes], \\
                                 columns=['c', 'xmax'])
        ggplot() + \\
            geom_vline(aes(xintercept='xmax', color='c'), \\
                       data=bounds_df, size=.2, linetype='longdash') + \\
            geom_point(aes(x='x', y='y', color='c'), data=df)

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='boxplot'
        The statistical transformation to use on the data for this layer, as a string.
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
    fatten : float, default=1.0
        A multiplicative factor applied to size of the middle bar.
    outlier_color : str
        Default color aesthetic for outliers.
    outlier_fill : str
        Default fill aesthetic for outliers.
    outlier_shape : int
        Default shape aesthetic for outliers.
    outlier_size : float
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
    Computed variables:

    - ..lower.. : lower hinge, 25% quantile.
    - ..middle.. : median, 50% quantile.
    - ..upper.. : upper hinge, 75% quantile.
    - ..ymin.. : lower whisker = smallest observation greater than or equal to lower hinge - 1.5 * IQR.
    - ..ymax.. : upper whisker = largest observation less than or equal to upper hinge + 1.5 * IQR.

    `geom_boxplot()` understands the following aesthetics mappings:

    - lower : lower hinge.
    - middle : median.
    - upper : upper hinge.
    - ymin : lower whisker.
    - ymax : upper whisker.
    - alpha : transparency level of a layer. Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color of geometry filling.
    - size : lines width.
    - linetype : type of the line of border. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.
    - width : width of boxplot [0..1].

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
            geom_boxplot()

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
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            geom_boxplot(fatten=5, varwidth=True, \\
                         outlier_shape=8, outlier_size=5)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 16-17

        import numpy as np
        import pandas as pd
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.choice(['a', 'b', 'c'], size=n)
        y = np.random.normal(size=n)
        df = pd.DataFrame({'x': x, 'y': y})
        agg_df = df.groupby('x').agg({'y': [
            'min', lambda s: np.quantile(s, 1/3),
            'median', lambda s: np.quantile(s, 2/3), 'max'
        ]}).reset_index()
        agg_df.columns = ['x', 'y0', 'y33', 'y50', 'y66', 'y100']
        ggplot(agg_df, aes(x='x')) + \\
            geom_boxplot(aes(ymin='y0', lower='y33', middle='y50', \\
                             upper='y66', ymax='y100'), stat='identity')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10-13

        import numpy as np
        import pandas as pd
        from lets_plot import *
        LetsPlot.setup_html()
        n, m = 100, 5
        np.random.seed(42)
        df = pd.DataFrame({'x%s' % i: np.random.normal(size=n) \\
                           for i in range(1, m + 1)})
        ggplot(df.melt()) + \\
            geom_boxplot(aes(x='variable', y='value', color='variable', \\
                             fill='variable'), \\
                         outlier_shape=21, outlier_size=4, size=2, \\
                         alpha=.5, width=.5, show_legend=False)

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
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
        :linenos:
        :emphasize-lines: 10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 10
        np.random.seed(42)
        x = np.arange(n)
        ymin = np.random.randint(-5, 0, size=n)
        ymax = np.random.randint(1, 6, size=n)
        ggplot({'x': x, 'ymin': ymin, 'ymax': ymax}, aes(x='x')) + \\
            geom_ribbon(aes(ymin='ymin', ymax='ymax'))

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11-15

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 30
        tmean = 20
        np.random.seed(42)
        day = np.arange(1, n + 1)
        tmin = tmean - (1 + np.abs(np.random.normal(size=n)))
        tmax = tmean + (1 + np.abs(np.random.normal(size=n)))
        ggplot({'day': day, 'tmin': tmin, 'tmax': tmax}) + \\
            geom_ribbon(aes(x='day', ymin='tmin', ymax='tmax'), \\
                        color='#bd0026', fill='#fd8d3c', size=2, \\
                        tooltips=layer_tooltips().line('@|@day')\\
                            .format('tmin', '.1f').line('min temp|@tmin')\\
                            .format('tmax', '.1f').line('max temp|@tmax'))

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 20
        np.random.seed(42)
        x = np.arange(n)
        y = np.cumsum(np.abs(np.random.uniform(size=n)))
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            geom_area()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 15-18

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 30
        np.random.seed(42)
        day = np.arange(1, n + 1)
        tmin = -1 - np.abs(np.random.normal(size=n))
        tmax = 1 + np.abs(np.random.normal(size=n))
        tooltips = layer_tooltips().line('@|@day')\\
                                   .format('tmin', '.1f')\\
                                   .line('min temp|@tmin')\\
                                   .format('tmax', '.1f')\\
                                   .line('max temp|@tmax')
        ggplot({'day': day, 'tmin': tmin, 'tmax': tmax}) + \\
            geom_area(aes(x='day', y='tmin'), color='#0571b0', \\
                      fill='#92c5de', tooltips=tooltips) + \\
            geom_area(aes(x='day', y='tmax'), color='#ca0020', \\
                      fill='#f4a582', tooltips=tooltips)

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='density'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    kernel : str, default='gaussian'
        The kernel we use to calculate the density function.
        Choose among 'gaussian', 'cosine', 'optcosine', 'rectangular' (or 'uniform'),
        'triangular', 'biweight' (or 'quartic'), 'epanechikov' (or 'parabolic').
    bw : str or float
        The method (or exact value) of bandwidth.
        Either a string (choose among 'nrd0' and 'nrd'), or a float.
    adjust : float
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
    Computed variables:

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
        :linenos:
        :emphasize-lines: 6

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        x = np.random.normal(size=1000)
        ggplot({'x': x}, aes(x='x')) + geom_density()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-15

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 300
        np.random.seed(42)
        x = np.random.normal(size=n)
        c = np.random.choice(['a', 'b', 'c'], size=n)
        ggplot({'x': x, 'c': c}, aes(x='x')) + \\
            geom_density(aes(group='c', color='c', fill='c'), alpha=.2, \\
                         tooltips=layer_tooltips().format('..density..', '.3f')\\
                                                  .line('density|@..density..')\\
                                                  .format('..count..', '.1f')\\
                                                  .line('count|@..count..')\\
                                                  .format('..scaled..', '.2f')\\
                                                  .line('scaled|@..scaled..'))

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        x = np.random.normal(size=1000)
        p = ggplot({'x': x}, aes(x='x'))
        bunch = GGBunch()
        for i, bw in enumerate([.1, .2, .4]):
            for j, n in enumerate([16, 64, 256]):
                bunch.add_plot(p + geom_density(kernel='epanechikov', bw=bw, n=n) + \\
                                   ggtitle('bw={0}, n={1}'.format(bw, n)),
                               j * 300, i * 200, 300, 200)
        bunch.show()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10-11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        x = np.random.normal(size=1000)
        y = np.sign(x)
        p = ggplot({'x': x, 'y': y}, aes(x='x'))
        bunch = GGBunch()
        for i, adjust in [(i, .5 * (1 + i)) for i in range(3)]:
            bunch.add_plot(p + geom_density(aes(weight='y'), kernel='cosine', \\
                                            adjust=adjust) + \\
                               ggtitle('adjust={0}'.format(adjust)),
                           i * 300, 0, 300, 200)
        bunch.show()

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='density2d'
        The statistical transformation to use on the data for this layer, as a string.
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
    kernel : str, default='gaussian'
        The kernel we use to calculate the density function.
        Choose among 'gaussian', 'cosine', 'optcosine', 'rectangular' (or 'uniform'),
        'triangular', 'biweight' (or 'quartic'), 'epanechikov' (or 'parabolic').
    bw : str or list of float
        The method (or exact value) of bandwidth.
        Either a string (choose among 'nrd0' and 'nrd'), or a float array of length 2.
    adjust : float
        Adjust the value of bandwidth my multiplying it. Changes how smooth the frequency curve is.
    n : list of int
        The number of sampled points for plotting the function
        (on x and y direction correspondingly).
    bins : int
        Number of levels.
    binwidth : float
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

    Computed variables:

    - ..group.. : number of density estimate contour line.

    `geom_density2d()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - y : y-axis coordinates.
    - alpha : transparency level of a layer. Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : lines width. Defines line width.
    - linetype : type of the line of border. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Note
    ----
    'density2d' statistical transformation combined with parameter value `contour=False`
    could be used to draw heatmaps (see the example below).

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + \\
            geom_density2d()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + \\
            geom_density2d(aes(color='..group..'), size=1, show_legend=False) + \\
            scale_color_brewer(type='seq', palette='GnBu', direction=-1)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        p = ggplot({'x': x, 'y': y}, aes('x', 'y'))
        bunch = GGBunch()
        for i, bw in enumerate([.2, .4]):
            for j, n in enumerate([16, 256]):
                bunch.add_plot(p + geom_density2d(kernel='epanechikov', bw=bw, n=n) + \\
                                   ggtitle('bw={0}, n={1}'.format(bw, n)),
                               j * 400, i * 400, 400, 400)
        bunch.show()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12-13

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        p = ggplot({'x': x, 'y': y}, aes('x', 'y'))
        bunch = GGBunch()
        for i, adjust in enumerate([1.5, 2.5]):
            for j, bins in enumerate([5, 15]):
                bunch.add_plot(p + geom_density2d(kernel='cosine', \\
                                                  adjust=adjust, bins=bins) + \\
                                   ggtitle('adjust={0}, bins={1}'.format(adjust, bins)),
                               j * 400, i * 400, 400, 400)
        bunch.show()

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + \\
            geom_raster(aes(fill='..density..'), \\
                       stat='density2d', contour=False, n=50) + \\
            scale_fill_gradient(low='#49006a', high='#fff7f3')

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='density2df'
        The statistical transformation to use on the data for this layer, as a string.
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
    kernel : str, default='gaussian'
        The kernel we use to calculate the density function.
        Choose among 'gaussian', 'cosine', 'optcosine', 'rectangular' (or 'uniform'),
        'triangular', 'biweight' (or 'quartic'), 'epanechikov' (or 'parabolic').
    bw : str or list of float
        The method (or exact value) of bandwidth.
        Either a string (choose among 'nrd0' and 'nrd'), or a float array of length 2.
    adjust : float
        Adjust the value of bandwidth my multiplying it. Changes how smooth the frequency curve is.
    n : list of int
        The number of sampled points for plotting the function
        (on x and y direction correspondingly).
    bins : int
        Number of levels.
    binwidth : float
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

    Computed variables:

    - ..group.. : number of density estimate contour line.

    `geom_density2df()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - alpha : transparency level of a layer. Understands numbers between 0 and 1.
    - fill : color of geometry filling.

    Note
    ----
    'density2df' statistical transformation combined with parameter value `contour=False`
    could be used to draw heatmaps (see the example below).

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            geom_density2df()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            geom_density2df(aes(fill='..group..'), show_legend=False) + \\
            scale_fill_brewer(type='seq', palette='GnBu', direction=-1)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12-13

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        p = ggplot({'x': x, 'y': y}, aes(x='x', y='y'))
        bunch = GGBunch()
        for i, bw in enumerate([.2, .4]):
            for j, n in enumerate([16, 256]):
                bunch.add_plot(p + geom_density2df(kernel='epanechikov', bw=bw, n=n, \\
                                                   size=.5, color='white') + \\
                                   ggtitle('bw={0}, n={1}'.format(bw, n)),
                               j * 400, i * 400, 400, 400)
        bunch.show()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12-14

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        p = ggplot({'x': x, 'y': y}, aes(x='x', y='y'))
        bunch = GGBunch()
        for i, adjust in enumerate([1.5, 2.5]):
            for j, bins in enumerate([5, 15]):
                bunch.add_plot(p + geom_density2df(kernel='cosine', \\
                                                   size=.5, color='white', \\
                                                   adjust=adjust, bins=bins) + \\
                                   ggtitle('adjust={0}, bins={1}'.format(adjust, bins)),
                               j * 400, i * 400, 400, 400)
        bunch.show()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + \\
            geom_tile(aes(fill='..density..'), color='black', \\
                       stat='density2df', contour=False, n=50) + \\
            scale_fill_gradient(low='#49006a', high='#fff7f3')

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    width : float, default=0.4
        Amount of horizontal variation. The jitter is added in both directions, so the total spread is twice the specified parameter.
    height : float, default=0.4
        Amount of vertical variation. The jitter is added in both directions, so the total spread is twice the specified parameter.
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
        :linenos:
        :emphasize-lines: 10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        x = np.random.randint(-5, 6, size=n)
        y = np.random.randint(10, size=n)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            geom_point(color='red', shape=3, size=10) + \\
            geom_jitter()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 6000
        np.random.seed(42)
        x = np.random.choice(list('abcde'), size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            geom_jitter(aes(color='x', size='y'), \\
                        sampling=sampling_random(n=600, seed=60), \\
                        show_legend=False, width=.25) + \\
            scale_color_grey(start=.75, end=0) + \\
            scale_size(range=[1, 3])

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='bin'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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

    Computed variables:

    - ..count.. : number of points with x-axis coordinate in the same bin.

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
        :linenos:
        :emphasize-lines: 6

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {'x': np.random.normal(size=1000)}
        ggplot(data, aes(x='x')) + geom_freqpoly()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        x = np.random.gamma(2.0, size=n)
        c = np.random.choice(['a', 'b', 'c'], size=n)
        ggplot({'x': x, 'c': c}, aes(x='x')) + \\
            geom_freqpoly(aes(color='c'), size=1) + \\
            geom_point(aes(color='c'), stat='bin', \\
                       shape=21, fill='white', size=3) + \\
            facet_grid(x='c')

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


def geom_step(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None,
              direction=None,
              **other_args):
    """
    Connect observations in the order in which they appear in the data by stairs.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    direction : {'hv', 'vh'}, default='hv'
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
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 20
        np.random.seed(42)
        x = np.arange(n)
        y = np.random.randint(5, size=n)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            geom_step() + \\
            coord_fixed()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10

        import numpy as np
        import pandas as pd
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        t = np.arange(n)
        x = np.cumsum(np.random.normal(size=n).astype(int))
        ggplot({'t': t, 'x': x}, aes(x='t', y='x')) + \\
            geom_step(direction='vh', color='#f03b20', size=1)

    """
    return _geom('step',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 direction=direction,
                 **other_args)


def geom_rect(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
              map=None, map_join=None,
              **other_args):
    """
    Display an axis-aligned rectangle defined by two corners.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `GeoDataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
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
    map : `GeoDataFrame` or `Geocoder`
        Bounding boxes of geometries will be drawn.
    map_join : str or list
        Keys used to join map coordinates with data.
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
    The `data` and `map` parameters of `GeoDataFrame` type support shapes `MultiPoint`, `Line`, `MultiLine`, `Polygon` and `MultiPolygon`.

    The `map` parameter of `Geocoder` type implicitly invoke `limits()` function.

    Note
    ----
    The conventions for the values of `map_join` parameter are as follows.

    - Joining data and `GeoDataFrame` object

      Data has a column named 'State_name' and `GeoDataFrame` has a matching column named 'state':

      - map_join=['State_Name', 'state']
      - map_join=[['State_Name'], ['state']]

    - Joining data and `Geocoder` object

      Data has a column named 'State_name'. The matching key in `Geocoder` is always 'state' (providing it is a state-level geocoder) and can be omitted:

      - map_join='State_Name'
      - map_join=['State_Name']

    - Joining data by composite key

      Joining by composite key works like in examples above, but instead of using a string for a simple key you need to use an array of strings for a composite key. The names in the composite key must be in the same order as in the US street addresses convention: 'city', 'county', 'state', 'country'. For example, the data has columns 'State_name' and 'County_name'. Joining with a 2-keys county level `Geocoder` object (the `Geocoder` keys 'county' and 'state' are omitted in this case):

      - map_join=['County_name', 'State_Name']

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 3

        from lets_plot import *
        LetsPlot.setup_html()
        ggplot() + geom_rect(xmin=-1, xmax=1, ymin=-1, ymax=1)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 16-18

        import numpy as np
        import pandas as pd
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        centers = {'a': (-2, -2), 'b': (3, 0), 'c': (0, 3)}
        np.random.seed(42)
        c = np.random.choice(list(centers.keys()), size=n)
        x = np.array([centers[k][0] for k in c]) + np.random.normal(size=n)
        y = np.array([centers[k][1] for k in c]) + np.random.normal(size=n)
        df = pd.DataFrame({'x': x, 'y': y, 'c': c}).sort_values(by='c')
        agg_df = df.groupby('c').agg({'x': ['min', 'max'], \\
                                      'y': ['min', 'max']}).reset_index()
        agg_df.columns = ['c', 'xmin', 'xmax', 'ymin', 'ymax']
        ggplot() + \\
            geom_rect(aes(xmin='xmin', xmax='xmax', ymin='ymin', \\
                          ymax='ymax', color='c', fill='c'), \\
                      data=agg_df, alpha=.2) + \\
            geom_point(aes(x='x', y='y', color='c'), data=df)

    """
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


def geom_segment(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                 arrow=None, **other_args):
    """
    Draw a straight line segment between two points.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    arrow : `FeatureSpec`
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
        :linenos:
        :emphasize-lines: 3

        from lets_plot import *
        LetsPlot.setup_html()
        ggplot() + geom_segment(x=0, y=0, xend=1, yend=1)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11-12

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        T = 25
        np.random.seed(42)
        t = [0, *np.random.normal(size=T)]
        x = np.cumsum(np.cos(t))
        y = np.cumsum(np.sin(t))
        data = {'x': x[:-1], 'y': y[:-1], 'xend': x[1:], 'yend': y[1:]}
        ggplot(data, aes(x='x', y='y')) + \\
            geom_segment(aes(xend='xend', yend='yend', color='xend'), \\
                         arrow=arrow(type='closed', angle=10)) + \\
            scale_color_gradient(low='#2c7bb6', high='#d7191c') + \\
            coord_fixed()

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
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `GeoDataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    map : `GeoDataFrame` or `Geocoder`
        Data containing coordinates of points.
    map_join : str or list
        Keys used to join map coordinates with data.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    label_format : str
        Format used to transform label mapping values to a string.
        Examples:
        '.2f' -> '12.45',
        'Num {}' -> 'Num 12.456789',
        'TTL: {.2f}$' -> 'TTL: 12.45$'.
        For more info see the `formatting reference <https://jetbrains.github.io/lets-plot-docs/pages/features/formats.html>`_.
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
    The `data` and `map` parameters of `GeoDataFrame` type support shapes `Point` and `MultiPoint`.

    The `map` parameter of `Geocoder` type implicitly invoke `centroids()` function.

    Note
    ----
    The conventions for the values of `map_join` parameter are as follows.

    - Joining data and `GeoDataFrame` object

      Data has a column named 'State_name' and `GeoDataFrame` has a matching column named 'state':

      - map_join=['State_Name', 'state']
      - map_join=[['State_Name'], ['state']]

    - Joining data and `Geocoder` object

      Data has a column named 'State_name'. The matching key in `Geocoder` is always 'state' (providing it is a state-level geocoder) and can be omitted:

      - map_join='State_Name'
      - map_join=['State_Name']

    - Joining data by composite key

      Joining by composite key works like in examples above, but instead of using a string for a simple key you need to use an array of strings for a composite key. The names in the composite key must be in the same order as in the US street addresses convention: 'city', 'county', 'state', 'country'. For example, the data has columns 'State_name' and 'County_name'. Joining with a 2-keys county level `Geocoder` object (the `Geocoder` keys 'county' and 'state' are omitted in this case):

      - map_join=['County_name', 'State_Name']

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 3

        from lets_plot import *
        LetsPlot.setup_html()
        ggplot() + geom_text(x=0, y=0, label='Lorem ipsum')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10-11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 10
        np.random.seed(42)
        x = np.arange(n)
        y = np.random.normal(loc=10, scale=2, size=n)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            geom_bar(stat='identity', fill='#2b8cbe', tooltips='none') + \\
            geom_text(aes(label='y'), position=position_nudge(y=1), \\
                      label_format='.1f', angle=45, color='#2b8cbe')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 13-15

        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        cities = ['New York', 'Los Angeles', 'Chicago']
        states = ['NY', 'CA', 'IL']
        titles = ['{0} ({1})'.format(city, state) \\
                  for city, state in zip(cities, states)]
        data = {'city': cities, 'state': states, 'title': titles}
        centroids = geocode_cities(data['city']).get_centroids()
        ggplot(data) + \\
            geom_point(map=centroids, map_join='city', \\
                       shape=21, color='black', fill='#f03b20') + \\
            geom_text(aes(label='title'), map=centroids, \\
                      map_join='city', size=8, vjust=1, \\
                      family='Optima', fontface='bold')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {var: np.random.uniform(size=10) for var in 'abcd'}
        ggplot(data) + \\
            geom_text(aes(label='..corr..', color='..corr..', size='..corr_abs..'), stat='corr') + \\
            scale_size(range=(.4, 1), guide='none') + \\
            scale_color_brewer(type='div', palette='RdYlGn', breaks=[-1, -.5, 0, .5, 1])

    """
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

    def process_map_parameters():
        map_join = kwargs.get('map_join', None)
        map = kwargs.get('map', None)

        if map_join is None and map is None:
            return

        map_join = normalize_map_join(map_join)

        if is_geocoder(map):
            if name in ['point', 'text', 'livemap']:
                map = map.get_centroids()
            elif name in ['map', 'polygon']:
                map = map.get_boundaries()
            elif name in ['rect']:
                map = map.get_limits()
            else:
                raise ValueError("Geocoding doesn't provide geometries for geom_{}".format(name))

        if is_geo_data_frame(map):
            map_join = auto_join_geo_names(map_join, map)
            map = geo_data_frame_to_wgs84(map)

        if map_join is not None:
            kwargs['map_join'] = map_join

        if map is not None:
            kwargs['map'] = map

    process_map_parameters()

    map_data_meta = as_annotated_map_data(kwargs.get('map', None))

    # TODO: check why map shouldn't be a GeoDataFrame
    if is_geo_data_frame(data) and not is_geo_data_frame(kwargs.get('map', None)):
        data = geo_data_frame_to_wgs84(data)
        data_meta['data_meta'].update(get_geo_data_frame_meta(data))

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
