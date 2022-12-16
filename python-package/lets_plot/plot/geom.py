#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from lets_plot.geo_data_internals.utils import is_geocoder

from .core import FeatureSpec, LayerSpec
from .util import as_annotated_data, is_geo_data_frame, geo_data_frame_to_crs, get_geo_data_frame_meta

#
# Geoms, short for geometric objects, describe the type of plot ggplot will produce.
#
__all__ = ['geom_point', 'geom_path', 'geom_line',
           'geom_smooth', 'geom_bar',
           'geom_histogram', 'geom_dotplot', 'geom_bin2d',
           'geom_tile', 'geom_raster',
           'geom_errorbar', 'geom_crossbar', 'geom_linerange', 'geom_pointrange',
           'geom_contour',
           'geom_contourf', 'geom_polygon', 'geom_map',
           'geom_abline', 'geom_hline', 'geom_vline',
           'geom_boxplot', 'geom_violin', 'geom_ydotplot',
           'geom_area_ridges',
           'geom_ribbon', 'geom_area', 'geom_density',
           'geom_density2d', 'geom_density2df', 'geom_jitter',
           'geom_qq', 'geom_qq2', 'geom_qq_line', 'geom_qq2_line',
           'geom_freqpoly', 'geom_step', 'geom_rect', 'geom_segment',
           'geom_text', 'geom_label', 'geom_pie']


def geom_point(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
               map=None, map_join=None, use_crs=None,
               **other_args):
    """
    Draw points defined by an x and y coordinate, as for a scatter plot.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame` or `GeoDataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    map : `GeoDataFrame` or `Geocoder`
        Data containing coordinates of points.
    map_join : str or list
        Keys used to join map coordinates with data.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    use_crs : str, optional, default="EPSG:4326" (aka WGS84)
        EPSG code of the coordinate reference system (CRS) or the keyword "provided".
        If an EPSG code is given, then all the coordinates in GeoDataFrame (see the `map` parameter)
        will be projected to this CRS.
        Specify "provided" to disable any further re-projection and to keep the GeoDataFrame’s original CRS.
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
    The point geometry is used to create scatterplots.
    The scatterplot is useful for displaying the relationship between
    two continuous variables, although it can also be used with one continuous
    and one categorical variable, or two categorical variables.

    `geom_point()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of the point. Accepts values between 0 and 1.
    - color (colour) : color of the geometry. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color to paint shape's inner points. Is applied only to the points of shapes having inner points.
    - shape : shape of the point, an integer from 0 to 25.
    - size : size of the point.

    |

    The `data` and `map` parameters of `GeoDataFrame` type support shapes `Point` and `MultiPoint`.

    The `map` parameter of `Geocoder` type implicitly invoke `centroids()` function.

    |

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
    """
    return _geom('point',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 map=map, map_join=map_join, use_crs=use_crs,
                 **other_args)


def geom_path(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
              map=None, map_join=None, use_crs=None,
              flat=None,
              **other_args):
    """
    Connects observations in the order, how they appear in the data.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame` or `GeoDataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    map : `GeoDataFrame`
        Data containing coordinates of lines.
    map_join : str or list
        Keys used to join map coordinates with data.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    use_crs : str, optional, default="EPSG:4326" (aka WGS84)
        EPSG code of the coordinate reference system (CRS) or the keyword "provided".
        If an EPSG code is given, then all the coordinates in GeoDataFrame (see the `map` parameter)
        will be projected to this CRS.
        Specify "provided" to disable any further re-projection and to keep the GeoDataFrame’s original CRS.
    flat : Boolean, default=False.
        True - keeps a line flat, False - allows projection to curve a line.
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
    `geom_path()` connects the observations in the order in which they appear in the data.
    `geom_path()` lets you explore how two variables are related over time.

    `geom_path()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash.
    - size : line width.

    |

    The `data` and `map` parameters of `GeoDataFrame` type support shapes `LineString` and `MultiLineString`.

    |

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
                 map=map, map_join=map_join, use_crs=use_crs,
                 flat=flat,
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
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

    Notes
    -----
    `geom_line()` connects the observations in the order of the variable on the x axis.
    `geom_line()` can be used to plot time series.

    `geom_line()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
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
                orientation=None,
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    orientation : str, default='x'
        Specifies the axis that the layer' stat and geom should run along.
        Possible values: 'x', 'y'.
    method : str, default='lm'
        Smoothing method: 'lm' (Linear Model) or 'loess' (Locally Estimated Scatterplot Smoothing).
        If value of `deg` parameter is greater than 1 then linear model becomes polynomial of the given degree.
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

    Notes
    -----
    `geom_smooth()` aids the eye in seeing patterns in the presence of overplotting.

    Computed variables:

    - ..y.. : predicted (smoothed) value.
    - ..ymin.. : lower pointwise confidence interval around the mean.
    - ..ymax.. : upper pointwise confidence interval around the mean.
    - ..se.. : standard error.

    `geom_smooth()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
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
                 orientation=orientation,
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
             orientation=None,
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    orientation : str, default='x'
        Specifies the axis that the layer' stat and geom should run along.
        Possible values: 'x', 'y'.
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
    `geom_bar()` makes the height of the bar proportional to the number
    of observed variable values, mapped to x axis. Is intended to use for discrete data.
    If used for continuous data with stat='bin' produces histogram for binned data.
    `geom_bar()` handles no group aesthetics.

    Computed variables:

    - ..count.. : number of points with same x-axis coordinate.
    - ..sum.. : total number of points with same x-axis coordinate.
    - ..prop.. : groupwise proportion.
    - ..proppct.. : groupwise proportion in percent.

    `geom_bar()` understands the following aesthetics mappings:

    - x : x-axis value (this values will produce cases or bins for bars).
    - y : y-axis value (this value will be used to multiply the case's or bin's counts).
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color of geometry filling.
    - size : lines width. Defines bar line width.
    - weight : used by 'count' stat to compute weighted sum instead of simple count.

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
                 orientation=orientation,
                 **other_args)


def geom_histogram(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                   orientation=None,
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    orientation : str, default='x'
        Specifies the axis that the layer' stat and geom should run along.
        Possible values: 'x', 'y'.
    bins : int, default=30
        Number of bins. Overridden by `binwidth`.
    binwidth : float
        The width of the bins. The default is to use bin widths that cover
        the range of the data. You should always override this value,
        exploring multiple widths to find the best to illustrate the stories in your data.
    center : float
        Specifies x-value to align bin centers to.
    boundary : float
        Specifies x-value to align bin boundary (i.e. point between bins) to.
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
    `geom_histogram()` displays a 1d distribution by dividing variable
    mapped to x-axis into bins and counting the number of observations in each bin.

    Computed variables:

    - ..count.. : number of points with x-axis coordinate in the same bin.
    - ..binwidth.. : width of each bin.

    `geom_histogram()` understands the following aesthetics mappings:

    - x : x-axis value (this values will produce cases or bins for bars).
    - y : y-axis value, default: '..count..'. Alternatively: '..density..'.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
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
                 orientation=orientation,
                 bins=bins,
                 binwidth=binwidth,
                 center=center,
                 boundary=boundary,
                 **other_args)


def geom_dotplot(mapping=None, *, data=None, stat=None, show_legend=None, sampling=None, tooltips=None,
                 binwidth=None,
                 bins=None,
                 method=None,
                 stackdir=None,
                 stackratio=None,
                 dotsize=None,
                 stackgroups=None,
                 center=None,
                 boundary=None,
                 **other_args):
    """
    Dotplot represents individual observations in a batch of data with circular dots.
    The diameter of a dot corresponds to the maximum width or bin width, depending on the binning algorithm.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='dotplot'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'dotplot' (depends on `method` parameter).
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`
        Result of the call to the `sampling_xxx()` function.
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    binwidth : float
        When method is 'dotdensity', this specifies maximum bin width.
        When method is 'histodot', this specifies bin width.
    bins : int, default=30
        When method is 'histodot', this specifies number of bins. Overridden by `binwidth`.
    method : {'dotdensity', 'histodot'}, default='dotdensity'
        Use 'dotdensity' for dot-density binning,
        or 'histodot' for fixed bin widths (like in geom_histogram).
    stackdir : {'up', 'down', 'center', 'centerwhole'}, default='up'
        Which direction to stack the dots.
    stackratio : float, default=1.0
        How close to stack the dots.
        Use smaller values for closer, overlapping dots.
    dotsize : float, default=1.0
        The diameter of the dots relative to binwidth.
    stackgroups : bool, default=False
        Stacks dots across groups when method='histodot'.
    center : float
        When method is 'histodot', this specifies x-value to align bin centers to.
    boundary : float
        When method is 'histodot', this specifies x-value to align bin boundary
        (i.e. point between bins) to.
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
    With 'dotdensity' binning, the bin positions are determined by the data and binwidth, which is the maximum width of each bin.
    With 'histodot' binning, the bins have fixed positions and fixed widths, much like a histogram.

    Computed variables:

    - ..count.. : number of points with x-axis coordinate in the same bin.
    - ..binwidth.. : max width of each bin if method is 'dotdensity'; width of each bin if method is 'histodot'.

    `geom_dotplot()` understands the following aesthetics mappings:

    - x : x-axis value.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color of geometry filling.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {'x': np.random.normal(size=100)}
        ggplot(data, aes(x='x')) + geom_dotplot()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7-8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {'x': np.random.gamma(2.0, size=100)}
        ggplot(data, aes(x='x')) + \\
            geom_dotplot(aes(color='x', fill='x'), \\
                         binwidth=.2, method='histodot', boundary=0.0)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7-8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {'x': np.random.normal(size=100)}
        ggplot(data, aes(x='x')) + \\
            geom_dotplot(binwidth=.2, stackdir='centerwhole', \\
                         stackratio=1.2, color='black', fill='gray')

    """
    return _geom('dotplot',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=None,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 binwidth=binwidth,
                 bins=bins,
                 method=method,
                 stackdir=stackdir,
                 stackratio=stackratio,
                 dotsize=dotsize,
                 stackgroups=stackgroups,
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
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

    Notes
    -----
    `geom_bin2d()` applies rectangular grid to the plane then counts observation
    in each cell of the grid (bin). Uses `geom_tile()` to display counts as a tile fill-color.

    Computed variables:

    - ..count.. : number of points with coordinates in the same bin.

    `geom_bin2d()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
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

    Notes
    -----
    Understands the following aesthetics mappings:

    - x : x-axis coordinates of the center of rectangles.
    - y : y-axis coordinates of the center of rectangles.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color of geometry filling.
    - size : lines width.
    - width : width of a tile. Typically ranges between 0 and 1. Values that are greater than 1 lead to overlapping of the tiles.
    - height : height of a tile. Typically ranges between 0 and 1. Values that are greater than 1 lead to overlapping of the tiles.
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
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
    Understands the following aesthetics mappings:

    - x : x-axis coordinates of the center of rectangles.
    - y : y-axis coordinates of the center of rectangles.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
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

    Notes
    -----
    `geom_errorbar()` represents a vertical interval, defined by `x`, `ymin`, `ymax`.

    `geom_errorbar()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - ymin : lower bound for error bar.
    - ymax : upper bound for error bar.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : lines width. Defines bar line width.
    - width : width of a bar. Typically ranges between 0 and 1. Values that are greater than 1 lead to overlapping of the bars.
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
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

    Notes
    -----
    `geom_crossbar()` represents a vertical interval, defined by `x`, `ymin`, `ymax`.
    The mean is represented by horizontal line.

    `geom_crossbar()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - ymin : lower bound for error bar.
    - middle : position of median bar.
    - ymax : upper bound for error bar.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color of geometry filling.
    - size : lines width.
    - width : width of a bar. Typically ranges between 0 and 1. Values that are greater than 1 lead to overlapping of the bars.
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    fatten : float, default=5.0
        A multiplicative factor applied to size of the middle point.
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
    `geom_pointrange()` represents a vertical interval, defined by `x`, `ymin`, `ymax`.
    The mid-point is defined by `y`.

    `geom_pointrange()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - y : position of mid-point.
    - ymin : lower bound for error bar.
    - ymax : upper bound for error bar.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color of geometry filling.
    - size : lines width, size of mid-point.
    - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.
    - shape : shape of the mid-point, an integer from 0 to 25.

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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
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

    Notes
    -----
    `geom_linerange()` represents a vertical interval, defined by `x`, `ymin`, `ymax`.

    `geom_linerange()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - ymin : lower bound for line range.
    - ymax : upper bound for line range.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
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

    Notes
    -----
    `geom_contour()` displays contours of a 3d surface in 2d.

    Computed variables:

    - ..level.. : height of a contour.

    `geom_contour()` understands the following aesthetics mappings:

    - x : x-axis coordinates of the center of rectangles, forming a tessellation.
    - y : y-axis coordinates of the center of rectangles, forming a tessellation.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
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

    Notes
    -----
    `geom_contourf()` fills contours of a 3d surface in 2d.

    Computed variables:

    - ..level.. : height of a contour.

    `geom_contourf()` understands the following aesthetics mappings:

    - x : x-axis coordinates of the center of rectangles, forming a tessellation.
    - y : y-axis coordinates of the center of rectangles, forming a tessellation.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
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
                 map=None, map_join=None, use_crs=None,
                 **other_args):
    """
    Display a filled closed path defined by the vertex coordinates of individual polygons.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame` or `GeoDataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    map : `GeoDataFrame` or `Geocoder`
        Data contains coordinates of polygon vertices on map.
    map_join : str or list
        Keys used to join map coordinates with data.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    use_crs : str, optional, default="EPSG:4326" (aka WGS84)
        EPSG code of the coordinate reference system (CRS) or the keyword "provided".
        If an EPSG code is given, then all the coordinates in GeoDataFrame (see the `map` parameter)
        will be projected to this CRS.
        Specify "provided" to disable any further re-projection and to keep the GeoDataFrame’s original CRS.
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
    `geom_polygon()` draws polygons, which are filled paths.
    Each vertex of the polygon requires a separate row in the data.

    `geom_polygon()` understands the following aesthetics mappings:

    - x : x-axis coordinates of the vertices of the polygon.
    - y : y-axis coordinates of the vertices of the polygon.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : lines width. Defines line width.
    - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    |

    The `data` and `map` parameters of `GeoDataFrame` type support shapes `Polygon` and `MultiPolygon`.

    The `map` parameter of `Geocoder` type implicitly invoke `boundaries()` function.

    |

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
            geom_polygon(aes(color='city', fill='city'), data=data, map=boundaries, \\
                         map_join='city', alpha=.2, \\
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
                 map=map, map_join=map_join, use_crs=use_crs,
                 **other_args)


def geom_map(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
             map=None, map_join=None, use_crs=None,
             **other_args):
    """
    Display polygons from a reference map.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame` or `GeoDataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    map : `GeoDataFrame` or `Geocoder`
        Data containing region boundaries (coordinates of polygon vertices on map).
    map_join : str or list
        Keys used to join map coordinates with data.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    use_crs : str, optional, default="EPSG:4326" (aka WGS84)
        EPSG code of the coordinate reference system (CRS) or the keyword "provided".
        If an EPSG code is given, then all the coordinates in GeoDataFrame (see the `map` parameter)
        will be projected to this CRS.
        Specify "provided" to disable any further re-projection and to keep the GeoDataFrame’s original CRS.
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
    `geom_map()` draws polygons which boundaries are specified by `map` parameter.
    Aesthetics of ploygons (`fill` etc.) are computed basing on input data and mapping
    (see `data` and `mapping` arguments).

    `geom_map()` understands the following aesthetics:

    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color of a geometry internals. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : lines width. Defines line width.
    - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    |

    The `data` and `map` parameters of `GeoDataFrame` type support shapes `Polygon` and `MultiPolygon`.

    The `map` parameter of `Geocoder` type implicitly invoke `boundaries()` function.

    |

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
            geom_map(aes(color='city', fill='city'), data=data, map=boundaries, \\
                     map_join='city', size=.5, alpha=.3, \\
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
                 map=map, map_join=map_join, use_crs=use_crs,
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
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

    Notes
    -----
    `geom_abline()` understands the following aesthetics mappings:

    - slope : line slope.
    - intercept : line y-intercept.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
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

    Notes
    -----
    `geom_hline()` understands the following aesthetics mappings:

    - yintercept : line y-intercept.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
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
                       data=bounds_df, size=.7, linetype='longdash') + \\
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
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

    Notes
    -----
    `geom_hline()` understands the following aesthetics mappings:

    - xintercept : line x-intercept.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
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
                       data=bounds_df, size=.7, linetype='longdash') + \\
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
                 orientation=None,
                 fatten=None,
                 outlier_color=None, outlier_fill=None, outlier_shape=None, outlier_size=None,
                 varwidth=None,
                 whisker_width=None,
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    orientation : str, default='x'
        Specifies the axis that the layer' stat and geom should run along.
        Possible values: 'x', 'y'.
    fatten : float, default=1.0
        A multiplicative factor applied to size of the middle bar.
    outlier_color : str
        Default color aesthetic for outliers.
    outlier_fill : str
        Default fill aesthetic for outliers.
    outlier_shape : int
        Default shape aesthetic for outliers, an integer from 0 to 25.
    outlier_size : float
        Default size aesthetic for outliers.
    varwidth : bool, default=False
        If False make a standard box plot.
        If True, boxes are drawn with widths proportional to the square-roots
        of the number of observations in the groups.
    whisker_width : float, default=0.5
        A multiplicative factor applied to the box width to draw horizontal segments on whiskers.
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
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color of geometry filling.
    - size : lines width.
    - linetype : type of the line of border. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.
    - width : width of boxplot. Typically ranges between 0 and 1. Values that are greater than 1 lead to overlapping of the boxes.

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
                 orientation=orientation,
                 fatten=fatten,
                 outlier_color=outlier_color,
                 outlier_fill=outlier_fill,
                 outlier_shape=outlier_shape,
                 outlier_size=outlier_size,
                 varwidth=varwidth,
                 whisker_width=whisker_width,
                 **other_args)


def geom_violin(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                orientation=None,
                show_half=None,
                draw_quantiles=None,
                scale=None, trim=None, tails_cutoff=None, kernel=None, bw=None, adjust=None, n=None, fs_max=None,
                **other_args):
    """
    A violin plot is a mirrored density plot with an additional grouping as for a boxplot.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='ydensity'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`
        Result of the call to the `sampling_xxx()` function.
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    orientation : str, default='x'
        Specifies the axis that the layer' stat and geom should run along.
        Possible values: 'x', 'y'.
    show_half : float, default=0
        If -1 then it's drawing only half of each violin.
        If 1 then it's drawing other half.
        If 0 then violins looking as usual.
    draw_quantiles : list of float
        Draw horizontal lines at the given quantiles of the density estimate.
    scale : {'area', 'count', 'width'}, default='area'
        If 'area', all violins have the same area.
        If 'count', areas are scaled proportionally to the number of observations.
        If 'width', all violins have the same maximum width.
    trim : bool, default=True
        Trim the tails of the violins to the range of the data.
    tails_cutoff : float, default=3.0
        Extend domain of each violin on `tails_cutoff * bw` if `trim=False`.
    kernel : str, default='gaussian'
        The kernel we use to calculate the density function.
        Choose among 'gaussian', 'cosine', 'optcosine', 'rectangular' (or 'uniform'),
        'triangular', 'biweight' (or 'quartic'), 'epanechikov' (or 'parabolic').
    bw : str or float
        The method (or exact value) of bandwidth.
        Either a string (choose among 'nrd0' and 'nrd'), or a float.
    adjust : float
        Adjust the value of bandwidth by multiplying it. Changes how smooth the frequency curve is.
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

    Notes
    -----
    Computed variables:

    - ..violinwidth.. : density scaled for the violin plot, according to area, counts or to a constant maximum width (mapped by default).
    - ..density.. : density estimate.
    - ..count.. : density * number of points.
    - ..scaled.. : density estimate, scaled to maximum of 1.

    `geom_violin()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - y : y-axis coordinates.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color of geometry filling.
    - size : lines width.
    - linetype : type of the line of border. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.
    - weight : used by 'ydensity' stat to compute weighted density.

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
            geom_violin()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.choice(['a', 'b', 'b', 'c'], size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + \\
            geom_violin(scale='count', draw_quantiles=[.25, .5, .75])

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 3
        np.random.seed(42)
        x = ['a'] * n + ['b'] * n + ['c'] * n
        y = 3 * list(range(n))
        vw = np.random.uniform(size=3*n)
        ggplot({'x': x, 'y': y, 'vw': vw}, aes('x', 'y')) + \\
            geom_violin(aes(violinwidth='vw', fill='x'), stat='identity')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10-11

        import numpy as np
        import pandas as pd
        from lets_plot import *
        LetsPlot.setup_html()
        n, m = 100, 5
        np.random.seed(42)
        df = pd.DataFrame({'x%s' % i: np.random.normal(size=n) \\
                           for i in range(1, m + 1)})
        ggplot(df.melt(), aes('variable', 'value')) + \\
            geom_violin(aes(color='variable', fill='variable'), \\
                        size=2, alpha=.5, scale='width') + \\
            geom_boxplot(aes(fill='variable'), width=.2)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10-13

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.choice(["a", "b", "c", "d"], size=n)
        y1 = np.random.normal(size=n)
        y2 = np.random.normal(size=n)
        ggplot({'x': x, 'y1': y1, 'y2': y2}) + \\
            geom_violin(aes('x', 'y1'), show_half=-1, \\
                        trim=False, fill='#ffffb2') + \\
            geom_violin(aes('x', 'y2'), show_half=1, \\
                        trim=False, fill='#74c476')

    """
    return _geom('violin',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 orientation=orientation,
                 show_half=show_half,
                 draw_quantiles=draw_quantiles,
                 scale=scale, trim=trim, tails_cutoff=tails_cutoff, kernel=kernel, bw=bw, adjust=adjust, n=n, fs_max=fs_max,
                 **other_args)


def geom_ydotplot(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                  binwidth=None,
                  bins=None,
                  method=None,
                  stackdir=None,
                  stackratio=None,
                  dotsize=None,
                  stackgroups=None,
                  center=None,
                  boundary=None,
                  **other_args):
    """
    Dotplot represents individual observations in a batch of data with circular dots.
    The diameter of a dot corresponds to the maximum width or bin width, depending on the binning algorithm.
    `geom_ydotplot()` is an obvious blend of `geom_violin()` and `geom_dotplot()`.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='ydotplot'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'ydotplot' (depends on `method` parameter).
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`
        Result of the call to the `sampling_xxx()` function.
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    binwidth : float
        When method is 'dotdensity', this specifies maximum bin width.
        When method is 'histodot', this specifies bin width.
    bins : int, default=30
        When method is 'histodot', this specifies number of bins. Overridden by `binwidth`.
    method : {'dotdensity', 'histodot'}, default='dotdensity'
        Use 'dotdensity' for dot-density binning,
        or 'histodot' for fixed bin widths (like in geom_histogram).
    stackdir : {'left', 'right', 'center', 'centerwhole'}, default='center'
        Which direction to stack the dots.
    stackratio : float, default=1.0
        How close to stack the dots.
        Use smaller values for closer, overlapping dots.
    dotsize : float, default=1.0
        The diameter of the dots relative to binwidth.
    stackgroups : bool, default=False
        Separates overlapping stack groups when stackgroups=False.
        Overlaps stack groups when method='dotdensity' and stackgroups=True.
        Stacks dots across groups when method='histodot' and stackgroups=True.
    center : float
        When method is 'histodot', this specifies x-value to align bin centers to.
    boundary : float
        When method is 'histodot', this specifies x-value to align bin boundary
        (i.e. point between bins) to.
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
    With 'dotdensity' binning, the bin positions are determined by the data and binwidth, which is the maximum width of each bin.
    With 'histodot' binning, the bins have fixed positions and fixed widths, much like a histogram.

    Computed variables:

    - ..count.. : number of points with y-axis coordinate in the same bin.
    - ..binwidth.. : max width of each bin if method is 'dotdensity'; width of each bin if method is 'histodot'.

    `geom_ydotplot()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color of geometry filling.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 100
        x = np.random.choice(['A', 'B'], size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + geom_ydotplot()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 100
        x = np.random.choice(['A', 'B'], size=n)
        y = np.random.gamma(0.75, size=n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + \\
            geom_ydotplot(aes(fill='x'), color='black', \\
                          binwidth=.15, method='histodot', \\
                          boundary=0.0, dotsize=.75)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-14

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 100
        x = np.random.choice(['A', 'B', 'C'], size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + \\
            geom_ydotplot(color='black', fill='#d7191c', \\
                          method='dotdensity', binwidth=.2,
                          stackdir='left', stackratio=.8) + \\
            geom_ydotplot(color='black', fill='#2c7bb6', \\
                          method='histodot', binwidth=.2, \\
                          stackdir='right', stackratio=.8)

    """
    return _geom('ydotplot',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 binwidth=binwidth,
                 bins=bins,
                 method=method,
                 stackdir=stackdir,
                 stackratio=stackratio,
                 dotsize=dotsize,
                 stackgroups=stackgroups,
                 center=center,
                 boundary=boundary,
                 **other_args)


def geom_area_ridges(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                     trim=None, tails_cutoff=None, kernel=None, adjust=None, bw=None, n=None, fs_max=None,
                     min_height=None, scale=None, quantiles=None, quantile_lines=None,
                     **other_args):
    """
    Plots the sum of the `y` and `height` aesthetics versus `x`. Heights of the ridges are relatively scaled.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='densityridges'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'densityridges' (computes and draws kernel density estimate for each ridge).
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
    trim : bool, default=false
        Trim the tails of the ridges to the range of the data.
    tails_cutoff : float
        Extend domain of each ridge on `tails_cutoff * bw` if `trim=False`.
        `tails_cutoff=None` (default) extends domain to maximum (domain overall ridges).
    kernel : str, default='gaussian'
        The kernel we use to calculate the density function.
        Choose among 'gaussian', 'cosine', 'optcosine', 'rectangular' (or 'uniform'),
        'triangular', 'biweight' (or 'quartic'), 'epanechikov' (or 'parabolic').
    bw : str or float
        The method (or exact value) of bandwidth.
        Either a string (choose among 'nrd0' and 'nrd'), or a float.
    adjust : float
        Adjust the value of bandwidth by multiplying it. Changes how smooth the frequency curve is.
    n : int, default=512
        The number of sampled points for plotting the function.
    fs_max : int, default=500
        Maximum size of data to use density computation with 'full scan'.
        For bigger data, less accurate but more efficient density computation is applied.
    min_height : float, default=0.0
        A height cutoff on the drawn ridges.
        All values that fall below this cutoff will be removed.
    scale : float, default=1.0
        A multiplicative factor applied to height aesthetic.
        If `scale = 1.0`, the heights of a ridges are automatically scaled
        such that the ridge with `height = 1.0` just touches the one above.
    quantiles : list of float, default=[0.25, 0.5, 0.75]
        Draw horizontal lines at the given quantiles of the density estimate.
    quantile_lines : bool, default=false
        Show the quantile lines.
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

    - ..height.. : density scaled for the ridges, according to area, counts or to a constant maximum height.
    - ..density.. : density estimate.
    - ..count.. : density * number of points.
    - ..scaled.. : density estimate, scaled to maximum of 1.

    `geom_area_ridges()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - y : y-axis coordinates.
    - height : height of the ridge. Assumed to be between 0 and 1, though this is not required.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color of geometry filling.
    - size : lines width.
    - linetype : type of the line of border. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n, m = 10, 3
        np.random.seed(42)
        x = np.random.normal(size=n*m)
        y = np.repeat(np.arange(m), n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + \\
            geom_area_ridges()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-11

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            "x": [1, 2, 3, 4, 5, 6],
            "y": ['a', 'a', 'a', 'a', 'a', 'a'],
            "h": [1, -2, 3, -4, 5, 4],
        }
        ggplot(data) + \\
            geom_area_ridges(aes("x", "y", height="h"), \\
                             stat='identity', min_height=-2, \\
                             color="#756bb1", fill="#bcbddc")

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n, m = 50, 3
        np.random.seed(42)
        x = np.random.normal(size=n*m)
        y = np.repeat(['a', 'b', 'c'], n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + \\
            geom_area_ridges(aes(fill='..quantile..'), \\
                             quantiles=[.05, .25, .5, .75, .95], quantile_lines=True, \\
                             scale=1.5, kernel='triangular', color='black')

    """
    return _geom('area_ridges',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 trim=trim,
                 tails_cutoff=tails_cutoff,
                 kernel=kernel,
                 adjust=adjust,
                 bw=bw,
                 n=n,
                 fs_max=fs_max,
                 min_height=min_height,
                 scale=scale,
                 quantiles=quantiles,
                 quantile_lines=quantile_lines,
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
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

    Notes
    -----
    `geom_ribbon()` draws a ribbon bounded by `ymin` and `ymax`.

    `geom_ribbon()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - ymin : y-axis coordinates of the lower bound.
    - ymax : y-axis coordinates of the upper bound.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
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

    Notes
    -----
    `geom_area()` draws an area bounded by the data and x axis.

    `geom_area()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - y : y-axis coordinates.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
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
                 orientation=None,
                 trim=None,
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    orientation : str, default='x'
        Specifies the axis that the layer' stat and geom should run along.
        Possible values: 'x', 'y'.
    trim : bool, default=False
        If False, each density is computed on the full range of the data.
        If True, each density is computed over the range of that group.
    kernel : str, default='gaussian'
        The kernel we use to calculate the density function.
        Choose among 'gaussian', 'cosine', 'optcosine', 'rectangular' (or 'uniform'),
        'triangular', 'biweight' (or 'quartic'), 'epanechikov' (or 'parabolic').
    bw : str or float
        The method (or exact value) of bandwidth.
        Either a string (choose among 'nrd0' and 'nrd'), or a float.
    adjust : float
        Adjust the value of bandwidth by multiplying it. Changes how smooth the frequency curve is.
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

    Notes
    -----
    Computed variables:

    - ..density.. : density estimate (mapped by default).
    - ..count.. : density * number of points.
    - ..scaled.. : density estimate, scaled to maximum of 1.

    `geom_density()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
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
                 orientation=orientation,
                 trim=trim, kernel=kernel, adjust=adjust, bw=bw, n=n, fs_max=fs_max,
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
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
        Adjust the value of bandwidth by multiplying it. Changes how smooth the frequency curve is.
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

    Notes
    -----
    `geom_density2d()` draws density function.

    Computed variables:

    - ..group.. : number of density estimate contour line.

    `geom_density2d()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - y : y-axis coordinates.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : lines width. Defines line width.
    - linetype : type of the line of border. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    |

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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
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
        Adjust the value of bandwidth by multiplying it. Changes how smooth the frequency curve is.
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

    Notes
    -----
    `geom_density2df()` fills density contours.

    Computed variables:

    - ..group.. : number of density estimate contour line.

    `geom_density2df()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - fill : color of geometry filling.

    |

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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    width : float, default=0.4
        Amount of horizontal variation. The jitter is added in both directions, so the total spread is twice the specified parameter.
        Typically ranges between 0 and 0.5. Values that are greater than 0.5 lead to overlapping of the points.
    height : float, default=0.4
        Amount of vertical variation. The jitter is added in both directions, so the total spread is twice the specified parameter.
        Typically ranges between 0 and 0.5. Values that are greater than 0.5 lead to overlapping of the points.
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
    The jitter geometry is used to create jittered points.
    The scatterplot is useful for displaying the relationship between two discrete variables.

    `geom_jitter()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a point. Accepts values between 0 and 1.
    - color (colour) : color of a geometry. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color to paint shape's inner points. Is applied only to the points of shapes having inner points.
    - shape : shape of the point, an integer from 0 to 25.
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


def geom_qq(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
            distribution=None,
            dparams=None,
            **other_args):
    """
    Display quantile-quantile plot.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='qq'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'qq' (compare two probability distributions),
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    distribution : {'norm', 'uniform', 't', 'gamma', 'exp', 'chi2'}, default='norm'
        Distribution function to use.
    dparams : list
        Additional parameters (of float type) passed on to distribution function.
        If `distribution` is `'norm'` then `dparams` is a pair [mean, std] (=[0.0, 1.0] by default).
        If `distribution` is `'uniform'` then `dparams` is a pair [a, b] (=[0.0, 1.0] by default).
        If `distribution` is `'t'` then `dparams` is an integer number [d] (=[1] by default).
        If `distribution` is `'gamma'` then `dparams` is a pair [alpha, beta] (=[1.0, 1.0] by default).
        If `distribution` is `'exp'` then `dparams` is a float number [lambda] (=[1.0] by default).
        If `distribution` is `'chi2'` then `dparams` is an integer number [k] (=[1] by default).
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
    The Q-Q plot is used for comparing two probability distributions
    (sample and theoretical) by plotting their quantiles against each other.

    If the two distributions being compared are similar, the points in the Q-Q plot
    will approximately lie on the straight line.

    Computed variables:

    - ..theoretical.. : theoretical quantiles.
    - ..sample.. : sample quantiles.

    `geom_qq()` understands the following aesthetics mappings:

    - sample : y-axis value.
    - alpha : transparency level of a point. Accepts values between 0 and 1.
    - color (colour) : color of a geometry. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color to paint shape's inner points. Is applied only to the points of shapes having inner points.
    - shape : shape of the point, an integer from 0 to 25.
    - size : size of the point.

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
        sample = np.random.normal(0, 1, n)
        ggplot({'sample': sample}, aes(sample='sample')) + geom_qq()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        sample = np.random.exponential(1, n)
        ggplot({'sample': sample}, aes(sample='sample')) + \\
            geom_qq(distribution='exp')

    """
    return _geom('qq',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 distribution=distribution,
                 dparams=dparams,
                 **other_args)


def geom_qq2(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None, **other_args):
    """
    Display quantile-quantile plot.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='qq2'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'qq2' (compare two probability distributions),
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
        To prevent any sampling for this layer pass value "none" (string "none").
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

    Notes
    -----
    The Q-Q plot is used for comparing two probability distributions
    by plotting their quantiles against each other. A point (`x`, `y`)
    on the plot corresponds to one of the quantiles of the first distribution
    (`x`-coordinate) plotted against the same quantile of the second distribution
    (`y`-coordinate).

    If the two distributions being compared are similar, the points in the Q-Q plot
    will approximately lie on the straight line.

    `geom_qq2()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a point. Accepts values between 0 and 1.
    - color (colour) : color of a geometry. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color to paint shape's inner points. Is applied only to the points of shapes having inner points.
    - shape : shape of the point, an integer from 0 to 25.
    - size : size of the point.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 500
        np.random.seed(42)
        x = np.random.normal(0, 1, n)
        y = np.random.normal(1, 2, n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + geom_qq2()

    """
    return _geom('qq2',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 **other_args)


def geom_qq_line(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                 distribution=None,
                 dparams=None,
                 quantiles=None,
                 **other_args):
    """
    Display quantile-quantile fitting line.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='qq_line'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'qq_line' (compare two probability distributions),
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    distribution : {'norm', 'uniform', 't', 'gamma', 'exp', 'chi2'}, default='norm'
        Distribution function to use.
    dparams : list
        Additional parameters (of float type) passed on to distribution function.
        If `distribution` is `'norm'` then `dparams` is a pair [mean, std] (=[0.0, 1.0] by default).
        If `distribution` is `'uniform'` then `dparams` is a pair [a, b] (=[0.0, 1.0] by default).
        If `distribution` is `'t'` then `dparams` is an integer number [d] (=[1] by default).
        If `distribution` is `'gamma'` then `dparams` is a pair [alpha, beta] (=[1.0, 1.0] by default).
        If `distribution` is `'exp'` then `dparams` is a float number [lambda] (=[1.0] by default).
        If `distribution` is `'chi2'` then `dparams` is an integer number [k] (=[1] by default).
    quantiles : list, default=[0.25, 0.75]
        Pair of quantiles to use when fitting the Q-Q line.
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
    The Q-Q line plot is used for comparing two probability distributions
    (sample and theoretical) by plotting line passed through the pair of corresponding quantiles.

    Computed variables:

    - ..theoretical.. : theoretical quantiles.
    - ..sample.. : sample quantiles.

    `geom_qq_line()` understands the following aesthetics mappings:

    - sample : y-axis value.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash.
    - size : line width.

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
        sample = np.random.normal(0, 1, n)
        ggplot({'sample': sample}, aes(sample='sample')) + geom_qq() + geom_qq_line()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        sample = np.random.exponential(1, n)
        ggplot({'sample': sample}, aes(sample='sample')) + \\
            geom_qq(distribution='exp') + \\
            geom_qq_line(distribution='exp', quantiles=[0, 1])

    """
    return _geom('qq_line',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 distribution=distribution,
                 dparams=dparams,
                 quantiles=quantiles,
                 **other_args)


def geom_qq2_line(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                  quantiles=None,
                  **other_args):
    """
    Display quantile-quantile fitting line.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='qq2_line'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'qq2_line' (compare two probability distributions),
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    quantiles : list, default=[0.25, 0.75]
        Pair of quantiles to use when fitting the Q-Q line.
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
    The Q-Q line plot is used for comparing two probability distributions
    by plotting line passed through the pair of corresponding quantiles.

    `geom_qq2_line()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash.
    - size : line width.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 500
        np.random.seed(42)
        x = np.random.normal(0, 1, n)
        y = np.random.normal(1, 2, n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + geom_qq2() + geom_qq2_line()

    """
    return _geom('qq2_line',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 quantiles=quantiles,
                 **other_args)


def geom_freqpoly(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                  orientation=None,
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    orientation : str, default='x'
        Specifies the axis that the layer' stat and geom should run along.
        Possible values: 'x', 'y'.
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
    `geom_freqpoly()` connects the top points in `geom_bar()`.

    Computed variables:

    - ..count.. : number of points with x-axis coordinate in the same bin.

    `geom_freqpoly()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
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
                 orientation=orientation,
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
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
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

    Notes
    -----
    `geom_step()` draws steps between the observations in the order of X.

    `geom_step()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
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
              map=None, map_join=None, use_crs=None,
              **other_args):
    """
    Display an axis-aligned rectangle defined by two corners.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame` or `GeoDataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    map : `GeoDataFrame` or `Geocoder`
        Bounding boxes of geometries will be drawn.
    map_join : str or list
        Keys used to join map coordinates with data.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    use_crs : str, optional, default="EPSG:4326" (aka WGS84)
        EPSG code of the coordinate reference system (CRS) or the keyword "provided".
        If an EPSG code is given, then all the coordinates in GeoDataFrame (see the `map` parameter)
        will be projected to this CRS.
        Specify "provided" to disable any further re-projection and to keep the GeoDataFrame’s original CRS.
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
    `geom_rect()` draws rectangles.

    `geom_rect()` understands the following aesthetics mappings:

    - xmin : x-axis value.
    - xmax : x-axis value.
    - ymin : y-axis value.
    - ymax : y-axis value.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color of geometry filling.
    - size : lines width. Defines line width.
    - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    |

    The `data` and `map` parameters of `GeoDataFrame` type support shapes `MultiPoint`, `Line`, `MultiLine`, `Polygon` and `MultiPolygon`.

    The `map` parameter of `Geocoder` type implicitly invoke `limits()` function.

    |

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
                 map=map, map_join=map_join, use_crs=use_crs,
                 **other_args)


def geom_segment(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                 arrow=None, flat=None, **other_args):
    """
    Draw a straight line segment between two points.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    arrow : `FeatureSpec`
        Specification for arrow head, as created by `arrow()` function.
    flat : Boolean, default=False.
        True - keeps a line flat, False - allows projection to curve a line.
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
    `geom_segment()` draws segments.

    `geom_segment()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - xend : x-axis value.
    - yend : y-axis value.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
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
                 flat=flat,
                 **other_args)


def geom_text(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
              map=None, map_join=None, use_crs=None,
              label_format=None,
              na_text=None,
              nudge_x=None, nudge_y=None,
              **other_args):
    """
    Add a text directly to the plot.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame` or `GeoDataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    map : `GeoDataFrame` or `Geocoder`
        Data containing coordinates of points.
    map_join : str or list
        Keys used to join map coordinates with data.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    use_crs : str, optional, default="EPSG:4326" (aka WGS84)
        EPSG code of the coordinate reference system (CRS) or the keyword "provided".
        If an EPSG code is given, then all the coordinates in GeoDataFrame (see the `map` parameter)
        will be projected to this CRS.
        Specify "provided" to disable any further re-projection and to keep the GeoDataFrame’s original CRS.
    label_format : str
        Format used to transform label mapping values to a string.
        Examples:
        '.2f' -> '12.45',
        'Num {}' -> 'Num 12.456789',
        'TTL: {.2f}$' -> 'TTL: 12.45$'.
        For more info see https://lets-plot.org/pages/formats.html.
    na_text : str, default='n/a'
        Text to show for missing values.
    nudge_x : float
        Horizontal adjustment to nudge labels by.
    nudge_y : float
        Vertical adjustment to nudge labels by.
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
    Adds text directly to the plot.

    `geom_text()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : font size.
    - label : text to add to plot.
    - family : font family. Possible values: 'sans', 'serif', 'mono', any other like: "Times New Roman". The default is 'sans'.
    - fontface : font style and weight. Possible values: 'plain', 'bold', 'italic', 'bold italic'. The default is 'plain'.
    - hjust : horizontal text alignment. Possible values: 'left', 'middle', 'right' or number between 0 ('left') and 1 ('right').
        There are two special alignments: 'inward' (aligns text towards the plot center) and 'outward' (away from the plot center).
    - vjust : vertical text alignment. Possible values: 'bottom', 'center', 'top' or number between 0 ('bottom') and 1 ('top').
        There are two special alignments: 'inward' (aligns text towards the plot center) and 'outward' (away from the plot center).
    - angle : text rotation angle in degrees.
    - lineheight : line height multiplier applied to the font size in the case of multi-line text.

    |

    The `data` and `map` parameters of `GeoDataFrame` type support shapes `Point` and `MultiPoint`.

    The `map` parameter of `Geocoder` type implicitly invoke `centroids()` function.

    |

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
            geom_point(aes(fill='city'), map=centroids, \\
                       map_join='city', shape=21, color='black') + \\
            geom_text(aes(label='title'), map=centroids, \\
                      map_join='city', size=8, vjust=1, \\
                      family='Optima', fontface='bold')

    """
    return _geom('text',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 map=map, map_join=map_join, use_crs=use_crs,
                 label_format=label_format,
                 na_text=na_text,
                 nudge_x=nudge_x, nudge_y=nudge_y,
                 **other_args)


def geom_label(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
               map=None, map_join=None, use_crs=None,
               label_format=None,
               na_text=None,
               nudge_x=None, nudge_y=None,
               label_padding=None, label_r=None, label_size=None,
               **other_args):
    """
    Add a text directly to the plot with a rectangle behind the text.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame` or `GeoDataFrame`
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
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    map : `GeoDataFrame` or `Geocoder`
        Data containing coordinates of points.
    map_join : str or list
        Keys used to join map coordinates with data.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    use_crs : str, optional, default="EPSG:4326" (aka WGS84)
        EPSG code of the coordinate reference system (CRS) or the keyword "provided".
        If an EPSG code is given, then all the coordinates in GeoDataFrame (see the `map` parameter)
        will be projected to this CRS.
        Specify "provided" to disable any further re-projection and to keep the GeoDataFrame’s original CRS.
    label_format : str
        Format used to transform label mapping values to a string.
        Examples:
        '.2f' -> '12.45',
        'Num {}' -> 'Num 12.456789',
        'TTL: {.2f}$' -> 'TTL: 12.45$'.
        For more info see https://lets-plot.org/pages/formats.html.
    nudge_x : float
        Horizontal adjustment to nudge labels by.
    nudge_y : float
        Vertical adjustment to nudge labels by.
    na_text : str, default='n/a'
        Text to show for missing values.
    label_padding : float
        Amount of padding around label. Defaults to 0.25 of font size.
    label_r : float
        Radius of rounded corners. Defaults to 0.15 of label height.
    label_size : float, default = 1.0
        Size of label border.
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
    Adds a text directly to the plot and draws a rectangle behind the text, making it easier to read.

    `geom_label()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accepts values between 0 and 1.
    - color (colour) : color of a geometry. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill: background color of the label.
    - size : font size.
    - label : text to add to plot.
    - family : font family. Possible values: 'sans', 'serif', 'mono', any other like: "Times New Roman". The default is 'sans'.
    - fontface : font style and weight. Possible values: 'plain', 'bold', 'italic', 'bold italic'. The default is 'plain'.
    - hjust : horizontal alignment. Possible values: 'left', 'middle', 'right' or number between 0 ('left') and 1 ('right').
        There are two special alignments: 'inward' (aligns label towards the plot center) and 'outward' (away from the plot center).
    - vjust : vertical alignment. Possible values: 'bottom', 'center', 'top' or number between 0 ('bottom') and 1 ('top').
        There are two special alignments: 'inward' (aligns label towards the plot center) and 'outward' (away from the plot center).
    - angle : rotation angle in degrees.
    - lineheight : line height multiplier applied to the font size in the case of multi-line text.

    |

    The `data` and `map` parameters of `GeoDataFrame` type support shapes `Point` and `MultiPoint`.

    The `map` parameter of `Geocoder` type implicitly invoke `centroids()` function.

    |

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
        ggplot() + geom_label(x=0, y=0, label='Lorem ipsum', size=14)

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
            geom_label(aes(label='y'), position=position_nudge(y=1), \\
                      label_format='.1f', angle=15, fill='#2b8cbe', color='white')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 13-14

        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        cities = ['New York', 'Los Angeles']
        states = ['NY', 'CA']
        titles = ['{0} ({1})'.format(city, state) \\
                  for city, state in zip(cities, states)]
        data = {'city': cities, 'state': states, 'title': titles}
        centroids = geocode_cities(data['city']).get_centroids()
        ggplot(data) + \\
            geom_livemap() + \\
            geom_point(map=centroids, map_join='city') + \\
            geom_label(aes(label='title'), map=centroids, \\
                       map_join='city', size=7, hjust=0, vjust=0) + \\
            ggsize(500, 400)


    """
    return _geom('label',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 map=map, map_join=map_join, use_crs=use_crs,
                 label_format=label_format,
                 na_text=na_text,
                 nudge_x=nudge_x, nudge_y=nudge_y,
                 label_padding=label_padding,
                 label_r=label_r,
                 label_size=label_size,
                 **other_args)


def geom_pie(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None, labels=None,
             map=None, map_join=None, use_crs=None,
             hole=None, fill_by=None, stroke=None, stroke_color=None,
             **other_args):
    """
    Draw pie chart.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame` or `GeoDataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='count2d'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count2d' (counts number of points with same x,y coordinate).
    position : str or `FeatureSpec`
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`
        Result of the call to the `sampling_xxx()` function.
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    labels : `layer_labels`
        Result of the call to the `layer_labels()` function.
        Specifies style and content of the annotations.
    map : `GeoDataFrame` or `Geocoder`
        Data containing coordinates of points.
    map_join : str or list
        Keys used to join map coordinates with data.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    use_crs : str, optional, default="EPSG:4326" (aka WGS84)
        EPSG code of the coordinate reference system (CRS) or the keyword "provided".
        If an EPSG code is given, then all the coordinates in GeoDataFrame (see the `map` parameter)
        will be projected to this CRS.
        Specify "provided" to disable any further re-projection and to keep the GeoDataFrame’s original CRS.
    hole : float, default=0.0
        A multiplicative factor applied to the pie diameter to draw donut-like chart.
    fill_by : string, default='fill'
        Defines the source aesthetic for geometry filling.
    stroke : float, default=0.0
        Width of slice borders.
    stroke_color : str, default='white'.
        Color of slice borders.
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

    - ..count.. : number of points with same (x,y) coordinate.
    - ..sum.. : total number of points with same (x,y) coordinate.
    - ..prop.. : groupwise proportion.
    - ..proppct.. : groupwise proportion in percent.

    `geom_pie()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - slice : values associated to pie sectors.
    - explode : values to explode slices away from their center point, detaching it from the main pie.
    - size : pie diameter.
    - fill : color of geometry filling (by default).
    - color (colour) : color of geometry filling if `fill_by='color'`.
    - alpha : transparency level of the pie. Accepts values between 0 and 1.
    - weight : used by 'count2d' stat to compute weighted sum instead of simple count.

    |

    The `data` and `map` parameters of `GeoDataFrame` type support shapes `Point` and `MultiPoint`.

    The `map` parameter of `Geocoder` type implicitly invoke `centroids()` function.

    |

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

    |

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 4

        from lets_plot import *
        LetsPlot.setup_html()
        data = {'name': ['a', 'b', 'c', 'd', 'b'], 'value': [40, 90, 10, 50, 20]}
        ggplot(data) + geom_pie(aes(slice='value', fill='name'), stat='identity')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5-9

        from lets_plot import *
        from lets_plot.mapping import *
        LetsPlot.setup_html()
        data = {'name': ['a', 'b', 'c', 'd', 'b'], 'value': [40, 90, 10, 50, 20]}
        ggplot(data) + geom_pie(aes(fill=as_discrete('name', order_by='..count..'), weight='value'), \\
                                size=15, hole=0.2, stroke=1.0, \\
                                tooltips=layer_tooltips().format('@{..prop..}', '.0%') \\
                                                         .line('count|@{..count..} (@{..prop..})') \\
                                                         .line('total|@{..sum..}'))

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5-7

        from lets_plot import *
        from lets_plot.mapping import *
        LetsPlot.setup_html()
        data = {'name': ['a', 'b', 'c', 'd', 'b'], 'value': [40, 90, 10, 50, 20]}
        ggplot(data) + geom_pie(aes(fill=as_discrete('name', order_by='..count..'), weight='value'), \\
                                size=15, hole=0.2, stroke=1.0, \\
                                labels=layer_labels(['..proppct..']).format('..proppct..', '{.1f}%'))

    |
    """

    return _geom('pie',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 labels=labels,
                 map=map, map_join=map_join, use_crs=use_crs,
                 hole=hole, fill_by=fill_by, stroke=stroke, stroke_color=stroke_color,
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

    # GDF in a map parameter has higher priority for defining a geo_data_meta
    if is_geo_data_frame(data) and not is_geo_data_frame(kwargs.get('map')):
        data = geo_data_frame_to_crs(data, kwargs.get('use_crs'))
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
                     **kwargs)
