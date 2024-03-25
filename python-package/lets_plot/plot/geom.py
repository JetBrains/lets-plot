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
           'geom_freqpoly', 'geom_step', 'geom_rect',
           'geom_segment', 'geom_curve', 'geom_spoke',
           'geom_text', 'geom_label', 'geom_pie', 'geom_lollipop',
           'geom_count']


def geom_point(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
               map=None, map_join=None, use_crs=None,
               color_by=None, fill_by=None,
               **other_args):
    """
    Draw points defined by an x and y coordinate, as for a scatter plot.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame` or `GeoDataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate),
        'sum' (counts the number of points at each location - might help to workaround overplotting).
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
    map : `GeoDataFrame` or `Geocoder`
        Data containing coordinates of points.
    map_join : str or list
        Keys used to join map coordinates with data.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    use_crs : str, optional, default="EPSG:4326" (aka WGS84)
        EPSG code of the coordinate reference system (CRS) or the keyword "provided".
        If an EPSG code is given, then all the coordinates in `GeoDataFrame` (see the `map` parameter)
        will be projected to this CRS.
        Specify "provided" to disable any further re-projection and to keep the `GeoDataFrame’s` original CRS.
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
    The point geometry is used to create scatterplots.
    The scatterplot is useful for displaying the relationship between
    two continuous variables, although it can also be used with one continuous
    and one categorical variable, or two categorical variables.

    `geom_point()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of the point. Accept values between 0 and 1.
    - color (colour) : color of the geometry. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. Is applied only to the points of shapes having inner area. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - shape : shape of the point, an integer from 0 to 25.
    - size : size of the point.
    - stroke : width of the shape border. Applied only to the shapes having border.

    |

    The `data` and `map` parameters of `GeoDataFrame` type support shapes `Point` and `MultiPoint`.

    The `map` parameter of `Geocoder` type implicitly invokes `centroids()` function.

    |

    The conventions for the values of `map_join` parameter are as follows:

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
        data = {"city": ["New York", "Los Angeles", "Chicago"], \\
                "est_pop_2019": [8_336_817, 3_979_576, 2_693_976]}
        centroids = geocode_cities(data["city"]).get_centroids()
        ggplot() + geom_livemap() + \\
            geom_point(aes(size="est_pop_2019"), color="red", show_legend=False, \\
                       data=data, map=centroids, map_join="city", \\
                       tooltips=layer_tooltips().title("@city")
                                                .line("population|@est_pop_2019"))

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
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_path(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
              map=None, map_join=None, use_crs=None,
              flat=None, geodesic=None,
              color_by=None,
              **other_args):
    """
    Connect observations in the order, how they appear in the data.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame` or `GeoDataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    map : `GeoDataFrame`
        Data containing coordinates of lines.
    map_join : str or list
        Keys used to join map coordinates with data.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    use_crs : str, optional, default="EPSG:4326" (aka WGS84)
        EPSG code of the coordinate reference system (CRS) or the keyword "provided".
        If an EPSG code is given, then all the coordinates in `GeoDataFrame` (see the `map` parameter)
        will be projected to this CRS.
        Specify "provided" to disable any further re-projection and to keep the `GeoDataFrame’s` original CRS.
    flat : bool, default=False.
        True - keep a line straight (corresponding to a loxodrome in case of Mercator projection).
        False - allow a line to be reprojected, so it can become a curve.
    geodesic : bool, default=False
        Draw geodesic. Coordinates expected to be in WGS84. Works only with `geom_livemap()`.
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
    `geom_path()` connects the observations in the order in which they appear in the data.
    `geom_path()` lets you explore how two variables are related over time.

    `geom_path()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.
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

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 15

        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        pushkin_1829_journey = {
            "city": ["Moscow", "Oryol", "Novocherkassk", "Stavropol", \\
                     "Georgiyevsk", "Vladikavkaz", "Tiflis", "Kars", "Erzurum"],
            "latitude": [55.751244, 52.929697, 47.414101, 45.0428, \\
                         44.1497667, 43.03667, 41.716667, 40.60199, 39.90861],
            "longitude": [37.618423, 36.098689, 40.110401, 41.9734, \\
                          43.4577689, 44.66778, 44.783333, 43.09495, 41.27694],
        }
        ggplot(pushkin_1829_journey, aes("longitude", "latitude")) + \\
            geom_livemap(const_size_zoomin=0) + \\
            geom_point(size=3, color="#fc4e2a", tooltips=layer_tooltips().line("@city")) + \\
            geom_path(color="#fc4e2a")

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
                 flat=flat, geodesic=geodesic,
                 color_by=color_by,
                 **other_args)


def geom_line(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
              color_by=None,
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
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    `geom_line()` connects the observations in the order of the variable on the x axis.
    `geom_line()` can be used to plot time series.

    `geom_line()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
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
                 color_by=color_by,
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
                color_by=None, fill_by=None,
                **other_args):
    """
    Add a smoothed conditional mean.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='smooth'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    `geom_smooth()` aids the eye in seeing patterns in the presence of overplotting.

    Computed variables:

    - ..y.. : predicted (smoothed) value.
    - ..ymin.. : lower pointwise confidence interval around the mean.
    - ..ymax.. : upper pointwise confidence interval around the mean.
    - ..se.. : standard error.

    `geom_smooth()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color for the confidence interval around the line. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - linetype : type of the line of conditional mean line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash.
    - size : line width. Define line width for conditional mean and confidence bounds lines.

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
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_bar(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None,
             tooltips=None, labels=None,
             orientation=None,
             color_by=None, fill_by=None,
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
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='count'
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
        Specify appearance, style and content.
    labels : `layer_labels`
        Result of the call to the `layer_labels()` function.
        Specify style and content of the annotations.
    orientation : str, default='x'
        Specify the axis that the layer's stat and geom should run along.
        Possible values: 'x', 'y'.
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
    `geom_bar()` makes the height of the bar proportional to the number
    of observed variable values, mapped to x axis. Is intended to use for discrete data.
    If used for continuous data with stat='bin' produces histogram for binned data.
    `geom_bar()` handles no group aesthetics.

    Computed variables:

    - ..count.. : number of points with same x-axis coordinate.
    - ..sum.. : total number of points with same x-axis coordinate.
    - ..prop.. : groupwise proportion.
    - ..proppct.. : groupwise proportion in percent.
    - ..sumprop.. : proportion of points with same x-axis coordinate among all points in the dataset.
    - ..sumpct.. : proportion of points with same x-axis coordinate among all points in the dataset in percent.

    `geom_bar()` understands the following aesthetics mappings:

    - x : x-axis value (this value will produce cases or bins for bars).
    - y : y-axis value (this value will be used to multiply the case's or bin's counts).
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : line width. Define bar line width.
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
                 labels=labels,
                 orientation=orientation,
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_histogram(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None,
                   tooltips=None, labels=None,
                   orientation=None,
                   bins=None,
                   binwidth=None,
                   center=None,
                   boundary=None,
                   color_by=None, fill_by=None,
                   **other_args):
    """
    Display a 1d distribution by dividing variable mapped to x axis into bins
    and counting the number of observations in each bin.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
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
        Specify appearance, style and content.
    labels : `layer_labels`
        Result of the call to the `layer_labels()` function.
        Specify style and content of the annotations.
    orientation : str, default='x'
        Specify the axis that the layer's stat and geom should run along.
        Possible values: 'x', 'y'.
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
    `geom_histogram()` displays a 1d distribution by dividing variable
    mapped to x-axis into bins and counting the number of observations in each bin.

    Computed variables:

    - ..count.. : number of points with x-axis coordinate in the same bin.
    - ..binwidth.. : width of each bin.

    `geom_histogram()` understands the following aesthetics mappings:

    - x : x-axis value (this value will produce cases or bins for bars).
    - y : y-axis value, default: '..count..'. Alternatively: '..density..'.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : line width.
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
                 labels=labels,
                 orientation=orientation,
                 bins=bins,
                 binwidth=binwidth,
                 center=center,
                 boundary=boundary,
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_dotplot(mapping=None, *, data=None, show_legend=None, sampling=None, tooltips=None,
                 binwidth=None,
                 bins=None,
                 method=None,
                 stackdir=None,
                 stackratio=None,
                 dotsize=None,
                 stackgroups=None,
                 center=None,
                 boundary=None,
                 color_by=None, fill_by=None,
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
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`
        Result of the call to the `sampling_xxx()` function.
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specify appearance, style and content.
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
        Stack dots across groups when method='histodot'.
    center : float
        When method is 'histodot', this specifies x-value to align bin centers to.
    boundary : float
        When method is 'histodot', this specifies x-value to align bin boundary
        (i.e. point between bins) to.
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
    With 'dotdensity' binning, the bin positions are determined by the data and binwidth, which is the maximum width of each bin.
    With 'histodot' binning, the bins have fixed positions and fixed widths, much like a histogram.

    Computed variables:

    - ..count.. : number of points with x-axis coordinate in the same bin.
    - ..binwidth.. : max width of each bin if method is 'dotdensity'; width of each bin if method is 'histodot'.

    `geom_dotplot()` understands the following aesthetics mappings:

    - x : x-axis value.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - stroke : width of the dot border.

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
    if 'stat' in other_args:
        print("WARN: using 'stat' parameter for dotplot was deprecated.")
        other_args.pop('stat')

    return _geom('dotplot',
                 mapping=mapping,
                 data=data,
                 stat=None,
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
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_bin2d(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
               bins=None,
               binwidth=None,
               drop=None,
               color_by=None, fill_by=None,
               **other_args):
    """
    Divides the plane into a grid and color the bins by the count of cases in them.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='bin2d'
        The statistical transformation to use on the data for this layer, as a string.
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
    bins : list of int, default=[30, 30]
        Number of bins in both directions, vertical and horizontal. Overridden by `binwidth`.
    binwidth : list of float
        The width of the bins in both directions, vertical and horizontal.
        Override `bins`. The default is to use bin widths that cover the entire range of the data.
    drop : bool, default=True
        Specify whether to remove all bins with 0 counts.
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
    `geom_bin2d()` applies rectangular grid to the plane then counts observation
    in each cell of the grid (bin). Uses `geom_tile()` to display counts as a tile fill-color.

    Computed variables:

    - ..count.. : number of points with coordinates in the same bin.

    `geom_bin2d()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : line width, default=0 (i.e. tiles outline initially is not visible).
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
                       fill='darkgreen') + \\
            geom_point(size=1.5, shape=21, color='white', \\
                       fill='darkgreen') + \\
            ggsize(600, 450)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7-8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        x, y = np.random.multivariate_normal(mean=[-98, 39], cov=[[100, 0], [0, 10]], size=100).T
        ggplot() + geom_livemap() + \\
            geom_bin2d(aes(x, y, fill='..density..'), \\
                       bins=[10, 5], alpha=.5, show_legend=False)

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
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_tile(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
              color_by=None, fill_by=None,
              **other_args):
    """
    Display rectangles with x, y values mapped to the center of the tile.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
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
    `geom_tile()` understands the following aesthetics mappings:

    - x : x-axis coordinates of the center of rectangles.
    - y : y-axis coordinates of the center of rectangles.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : line width, default=0 (i.e. tiles outline initially is not visible).
    - width : width of a tile. Typically range between 0 and 1. Values that are greater than 1 lead to overlapping of the tiles.
    - height : height of a tile. Typically range between 0 and 1. Values that are greater than 1 lead to overlapping of the tiles.
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
        ggplot(data, aes(x='x', y='y', color='z', fill='z')) + geom_tile(size=.5)

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
            geom_tile(aes(fill='z'), width=.8, height=.8, color='black', size=.5) + \\
            scale_fill_gradient(low='yellow', high='darkgreen')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 15

        import numpy as np
        import geopandas as gpd
        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        nlon, nlat = 30, 20
        geometry = geocode_countries("Kazakhstan").get_boundaries().iloc[0].geometry
        bbox = geometry.bounds
        lonspace = np.linspace(bbox[0], bbox[2], nlon)
        latspace = np.linspace(bbox[1], bbox[3], nlat)
        longrid, latgrid = np.meshgrid(lonspace, latspace)
        lon, lat = longrid.flatten(), latgrid.flatten()
        within = gpd.points_from_xy(lon, lat).within(geometry)
        ggplot() + geom_livemap() + \\
            geom_tile(aes(x=lon, y=lat, fill=within), alpha=.5, show_legend=False)

    """
    return _geom('tile',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_raster(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, fill_by=None,
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
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, default='identity'
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`
        Result of the call to the `sampling_xxx()` function.
        To prevent any sampling for this layer pass value "none" (string "none").
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
    `geom_raster()` understands the following aesthetics mappings:

    - x : x-axis coordinates of the center of rectangles.
    - y : y-axis coordinates of the center of rectangles.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").

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
                 fill_by=fill_by,
                 **other_args)


def geom_errorbar(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                  color_by=None,
                  **other_args):
    """
    Display error bars defined by the upper and lower values.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    `geom_errorbar()` represents a vertical interval, defined by `x`, `ymin`, `ymax`,
    or a horizontal interval, defined by `y`, `xmin`, `xmax`.

    `geom_errorbar()` understands the following aesthetics mappings:

    - x or y: x-axis or y-axis coordinates for vertical or horizontal error bar, respectively.
    - ymin or xmin: lower bound for vertical or horizontal error bar, respectively.
    - ymax or xmax: upper bound for vertical or horizontal error bar, respectively.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : line width. Define bar line width.
    - width or height : size of the whiskers of vertical or horizontal bar, respectively. Typically range between 0 and 1. Values that are greater than 1 lead to overlapping of the bars.
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

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'xmin': [0.2, 4.6, 1.6, 3.5],
            'xmax': [1.5, 5.3, 3.0, 4.4],
            'y': ['a', 'a', 'b', 'b'],
            'c': ['gr1', 'gr2', 'gr1', 'gr2']
        }
        ggplot(data) + \\
            geom_errorbar(aes(y='y', xmin='xmin', xmax='xmax', color='c'), height=0.1, size=2)

    """
    return _geom('errorbar',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 color_by=color_by,
                 **other_args)


def geom_crossbar(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                  fatten=None,
                  color_by=None, fill_by=None,
                  **other_args):
    """
    Display bars with horizontal median line.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, default='dodge'
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
    fatten : float, default=2.5
        A multiplicative factor applied to size of the middle bar.
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
    `geom_crossbar()` represents a vertical interval, defined by `x`, `ymin`, `ymax`,
    or a horizontal interval, defined by `y`, `xmin`, `xmax`.
    The mean is represented by horizontal (vertical) line.

    `geom_crossbar()` understands the following aesthetics mappings:

    - x or y: x-axis or y-axis coordinates for vertical or horizontal bar, respectively.
    - y or x : position of median bar for vertical or horizontal bar, respectively.
    - ymin or xmin: lower bound for vertical or horizontal bar, respectively.
    - ymax or xmax: upper bound for vertical or horizontal bar, respectively.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : lines width.
    - width : width of a bar. Typically range between 0 and 1. Values that are greater than 1 lead to overlapping of the bars.
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
            'y': [6.5, 9, 4.5, 7],
            'ymax': [8, 11, 6, 9],
        }
        ggplot(data, aes(x='x')) + \\
            geom_crossbar(aes(ymin='ymin', y='y', ymax='ymax'))

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
            geom_crossbar(aes(x='x', ymin='ymin', y='ymedian', ymax='ymax', fill='x'), \\
                          data=err_df, width=.6, fatten=5) + \\
            geom_jitter(aes(x='x', y='y'), data=df, width=.3, shape=1, color='black', alpha=.5)

    """
    if mapping is not None and 'middle' in mapping.props():
        print("WARN: using 'middle' aesthetic parameter for crossbar was deprecated.\n"
              "      Please, use `y` aesthetic instead.")
        mapping.props()['y'] = mapping.props().pop('middle')

    return _geom('crossbar',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 fatten=fatten,
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_pointrange(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None,
                    tooltips=None,
                    fatten=None,
                    color_by=None, fill_by=None,
                    **other_args):
    """
    Add a vertical line defined by upper and lower value with midpoint at y location.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    fatten : float, default=5.0
        A multiplicative factor applied to size of the middle point.
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
    `geom_pointrange()` represents a vertical interval, defined by `x`, `ymin`, `ymax`,
    or a horizontal interval, defined by `y`, `xmin`, `xmax`.
    The mid-point is defined by `y` or `x`, respectively.

    `geom_pointrange()` understands the following aesthetics mappings:

    - x or y: x-axis or y-axis coordinates for vertical or horizontal interval, respectively.
    - y or x : position of mid-point for vertical or horizontal interval, respectively.
    - ymin or xmin: lower bound for vertical or horizontal interval, respectively.
    - ymax or xmax: upper bound for vertical or horizontal interval, respectively.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : size of mid-point.
    - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.
    - shape : shape of the mid-point, an integer from 0 to 25.
    - stroke : width of the shape border. Applied only to the shapes having border.
    - linewidth : line width.

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
            geom_pointrange(aes(ymin='ymin', ymax='ymax'), \\
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
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_linerange(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                   color_by=None,
                   **other_args):
    """
    Display a line range defined by an upper and lower value.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    `geom_linerange()` represents a vertical interval, defined by `x`, `ymin`, `ymax`,
    or a horizontal interval, defined by `y`, `xmin`, `xmax`.

    `geom_linerange()` understands the following aesthetics mappings:

    - x or y: x-axis or y-axis coordinates for vertical or horizontal line range, respectively.
    - ymin or xmin: lower bound for vertical or horizontal line range, respectively.
    - ymax or xmax: upper bound for vertical or horizontal line range, respectively.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : line width.
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
                 color_by=color_by,
                 **other_args)


def geom_contour(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                 bins=None,
                 binwidth=None,
                 color_by=None,
                 **other_args):
    """
    Display contours of a 3d surface in 2d.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='contour'
        The statistical transformation to use on the data for this layer, as a string.
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
    bins : int
        Number of levels.
    binwidth : float
        Distance between levels.
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
    `geom_contour()` displays contours of a 3d surface in 2d.

    Computed variables:

    - ..level.. : height of a contour.

    `geom_contour()` understands the following aesthetics mappings:

    - x : x-axis coordinates of the center of rectangles, forming a tessellation.
    - y : y-axis coordinates of the center of rectangles, forming a tessellation.
    - z : value at point (x, y).
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
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

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        data = {'x': 10 * np.random.normal(size=n) - 100, \\
                'y': 3 * np.random.normal(size=n) + 40}
        ggplot(data, aes('x', 'y')) + geom_livemap() + \\
            geom_contour(stat='density2d')

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
                 color_by=color_by,
                 **other_args)


def geom_contourf(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                  bins=None,
                  binwidth=None,
                  color_by=None, fill_by=None,
                  **other_args):
    """
    Fill contours of a 3d surface in 2d.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='contourf'
        The statistical transformation to use on the data for this layer, as a string.
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
    bins : int
        Number of levels.
    binwidth : float
        Distance between levels.
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
    `geom_contourf()` fills contours of a 3d surface in 2d.

    Computed variables:

    - ..level.. : height of a contour.

    `geom_contourf()` understands the following aesthetics mappings:

    - x : x-axis coordinates of the center of rectangles, forming a tessellation.
    - y : y-axis coordinates of the center of rectangles, forming a tessellation.
    - z : value at point (x, y).
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").

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

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        data = {'x': 10 * np.random.normal(size=n) - 100, \\
                'y': 3 * np.random.normal(size=n) + 40}
        ggplot(data, aes('x', 'y')) + geom_livemap() + \\
            geom_contourf(aes(fill='..group..'), stat='density2df', \\
                          alpha=.5, show_legend=False)

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
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_polygon(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                 map=None, map_join=None, use_crs=None,
                 color_by=None, fill_by=None,
                 **other_args):
    """
    Display a filled closed path defined by the vertex coordinates of individual polygons.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame` or `GeoDataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
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
    map : `GeoDataFrame` or `Geocoder`
        Data contains coordinates of polygon vertices on map.
    map_join : str or list
        Keys used to join map coordinates with data.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    use_crs : str, optional, default="EPSG:4326" (aka WGS84)
        EPSG code of the coordinate reference system (CRS) or the keyword "provided".
        If an EPSG code is given, then all the coordinates in `GeoDataFrame` (see the `map` parameter)
        will be projected to this CRS.
        Specify "provided" to disable any further re-projection and to keep the `GeoDataFrame’s` original CRS.
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
    `geom_polygon()` draws polygons, which are filled paths.
    Each vertex of the polygon requires a separate row in the data.

    `geom_polygon()` understands the following aesthetics mappings:

    - x : x-axis coordinates of the vertices of the polygon.
    - y : y-axis coordinates of the vertices of the polygon.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : line width.
    - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    |

    The `data` and `map` parameters of `GeoDataFrame` type support shapes `Polygon` and `MultiPolygon`.

    The `map` parameter of `Geocoder` type implicitly invokes `boundaries()` function.

    |

    The conventions for the values of `map_join` parameter are as follows:

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
        :emphasize-lines: 8-11

        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        data = {"city": ["New York", "Philadelphia"], \\
                "est_pop_2019": [8_336_817, 1_584_064]}
        boundaries = geocode_cities(data["city"]).inc_res().get_boundaries()
        ggplot() + geom_livemap() + \\
            geom_polygon(aes(color="city", fill="city"), data=data, map=boundaries, \\
                         map_join="city", alpha=.2, \\
                         tooltips=layer_tooltips().title('@city')\\
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
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_map(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
             map=None, map_join=None, use_crs=None,
             color_by=None, fill_by=None,
             **other_args):
    """
    Display polygons from a reference map.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame` or `GeoDataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    map : `GeoDataFrame` or `Geocoder`
        Data containing region boundaries (coordinates of polygon vertices on map).
    map_join : str or list
        Keys used to join map coordinates with data.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    use_crs : str, optional, default="EPSG:4326" (aka WGS84)
        EPSG code of the coordinate reference system (CRS) or the keyword "provided".
        If an EPSG code is given, then all the coordinates in `GeoDataFrame` (see the `map` parameter)
        will be projected to this CRS.
        Specify "provided" to disable any further re-projection and to keep the `GeoDataFrame’s` original CRS.
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
    `geom_map()` draws polygons which boundaries are specified by `map` parameter.
    Aesthetics of ploygons (`fill` etc.) are computed basing on input data and mapping
    (see `data` and `mapping` arguments).

    `geom_map()` understands the following aesthetics:

    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : line width.
    - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    |

    The `data` and `map` parameters of `GeoDataFrame` type support shapes `Polygon` and `MultiPolygon`.

    The `map` parameter of `Geocoder` type implicitly invokes `boundaries()` function.

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

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8-11

        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        data = {"city": ["New York", "Philadelphia"], \\
                "est_pop_2019": [8_336_817, 1_584_064]}
        boundaries = geocode_cities(data["city"]).inc_res().get_boundaries()
        ggplot() + geom_livemap() + \\
            geom_map(aes(color="city", fill="city"), data=data, map=boundaries, \\
                     map_join="city", alpha=.2, \\
                     tooltips=layer_tooltips().title('@city')\\
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
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_abline(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None,
                slope=None,
                intercept=None,
                color_by=None,
                **other_args):
    """
    Add a straight line with specified slope and intercept to the plot.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, default='identity'
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
    Unlike most other geoms, this geom does not affect the x and y scales.

    `geom_abline()` understands the following aesthetics mappings:

    - slope : line slope.
    - intercept : line y-intercept.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : lines width.
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
                 color_by=color_by,
                 **other_args)


def geom_hline(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
               yintercept=None,
               color_by=None,
               **other_args):
    """
    Add a straight horizontal line to the plot.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
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
    yintercept : float
        The value of y at the point where the line crosses the y axis.
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
    `geom_hline()` understands the following aesthetics mappings:

    - yintercept : line y-intercept.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : line width.
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

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 4

        from lets_plot import *
        LetsPlot.setup_html()
        ggplot() + geom_livemap(location=[0, 0], zoom=1) + \\
            geom_hline(yintercept=0, size=1, color="red") + \\
            ggtitle("Equator")

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
                 color_by=color_by,
                 **other_args)


def geom_vline(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
               xintercept=None,
               color_by=None,
               **other_args):
    """
    Add a straight vertical line to the plot.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
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
    xintercept : float
        The value of x at the point where the line crosses the x axis.
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
    `geom_hline()` understands the following aesthetics mappings:

    - xintercept : line x-intercept.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : lines width.
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

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 4

        from lets_plot import *
        LetsPlot.setup_html()
        ggplot() + geom_livemap(location=[0, 0], zoom=1) + \\
            geom_vline(xintercept=0, size=1, color="red") + \\
            ggtitle("Prime meridian")

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
                 color_by=color_by,
                 **other_args)


def geom_boxplot(mapping=None, *, data=None, stat=None, position=None, show_legend=None, tooltips=None,
                 orientation=None,
                 fatten=None,
                 outlier_alpha=None, outlier_color=None, outlier_fill=None,
                 outlier_shape=None, outlier_size=None, outlier_stroke=None,
                 varwidth=None,
                 whisker_width=None,
                 color_by=None, fill_by=None,
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
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='boxplot'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, default='dodge'
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specify appearance, style and content.
    orientation : str, default='x'
        Specify the axis that the layer's stat and geom should run along.
        Possible values: 'x', 'y'.
    fatten : float, default=2.0
        A multiplicative factor applied to size of the middle bar.
    outlier_alpha : float
        Default transparency aesthetic for outliers.
    outlier_color : str
        Default color aesthetic for outliers.
    outlier_fill : str
        Default fill aesthetic for outliers.
    outlier_shape : int
        Default shape aesthetic for outliers, an integer from 0 to 25.
    outlier_size : float
        Default size aesthetic for outliers.
    outlier_stroke : float
        Default width of the border for outliers.
    varwidth : bool, default=False
        If False, make a standard box plot.
        If True, boxes are drawn with widths proportional to the square-roots
        of the number of observations in the groups.
    whisker_width : float, default=0.5
        A multiplicative factor applied to the box width to draw horizontal segments on whiskers.
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

    - ..lower.. : lower hinge, 25% quantile.
    - ..middle.. : median, 50% quantile.
    - ..upper.. : upper hinge, 75% quantile.
    - ..ymin.. : lower whisker = smallest observation greater than or equal to lower hinge - 1.5 * IQR.
    - ..ymax.. : upper whisker = largest observation less than or equal to upper hinge + 1.5 * IQR.

    `geom_boxplot()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - lower : lower hinge.
    - middle : median.
    - upper : upper hinge.
    - ymin : lower whisker.
    - ymax : upper whisker.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
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
                         outlier_shape=8, outlier_size=2)

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
                         outlier_shape=21, outlier_size=1.5, size=2, \\
                         alpha=.5, width=.5, show_legend=False)

    """
    boxplot_layer = _geom('boxplot',
                          mapping=mapping,
                          data=data,
                          stat=stat,
                          position=position,
                          show_legend=show_legend,
                          sampling=None,
                          tooltips=tooltips,
                          orientation=orientation,
                          fatten=fatten,
                          varwidth=varwidth,
                          whisker_width=whisker_width,
                          color_by=color_by, fill_by=fill_by,
                          **other_args)
    if stat is None or stat == 'boxplot':
        outlier_param = lambda name, value: value if value is not None else other_args.get(name)
        outlier_fatten = 4
        size = outlier_param('size', outlier_size)
        if size is not None:
            size *= outlier_fatten
        boxplot_layer += _geom('point',
                               mapping=mapping,
                               data=data,
                               stat='boxplot_outlier',
                               position=position,
                               show_legend=False,
                               sampling=None,
                               orientation=orientation,
                               alpha=outlier_alpha,
                               color=outlier_param('color', outlier_color),
                               fill=outlier_param('fill', outlier_fill),
                               shape=outlier_param('shape', outlier_shape),
                               size=size,
                               stroke=outlier_param('stroke', outlier_stroke),
                               color_by=color_by, fill_by=fill_by)
    return boxplot_layer


def geom_violin(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                orientation=None,
                show_half=None,
                quantiles=None, quantile_lines=None,
                scale=None, trim=None, tails_cutoff=None, kernel=None, bw=None, adjust=None, n=None, fs_max=None,
                color_by=None, fill_by=None,
                **other_args):
    """
    A violin plot is a mirrored density plot with an additional grouping as for a boxplot.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='ydensity'
        The statistical transformation to use on the data for this layer, as a string.
    position : str or `FeatureSpec`, default='dodge'
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
    show_half : float, default=0
        If -1, only half of each violin is drawn.
        If 1, another half is drawn.
        If 0, violins look as usual.
    quantiles : list of float, default=[0.25, 0.5, 0.75]
        Draw horizontal lines at the given quantiles of the density estimate.
    quantile_lines : bool, default=False
        Show the quantile lines.
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
        Adjust the value of bandwidth by multiplying it. Change how smooth the frequency curve is.
    n : int, default=512
        The number of sampled points for plotting the function.
    fs_max : int, default=500
        Maximum size of data to use density computation with 'full scan'.
        For bigger data, less accurate but more efficient density computation is applied.
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

    - ..violinwidth.. : density scaled for the violin plot, according to area, counts or to a constant maximum width (mapped by default).
    - ..density.. : density estimate.
    - ..count.. : density * number of points.
    - ..scaled.. : density estimate, scaled to maximum of 1.
    - ..quantile.. : quantile estimate.

    `geom_violin()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - y : y-axis coordinates.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : lines width.
    - linetype : type of the line of border. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.
    - weight : used by 'ydensity' stat to compute weighted density.
    - quantile : quantile values to draw quantile lines and fill quantiles of the geometry by color.

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
        :emphasize-lines: 9-10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.choice(['a', 'b', 'b', 'c'], size=n)
        y = np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + \\
            geom_violin(aes(fill='..quantile..'), scale='count', \\
                        quantiles=[.02, .25, .5, .75, .98], quantile_lines=True)

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
    if 'draw_quantiles' in other_args:
        raise ValueError("The parameter 'draw_quantiles' is no longer supported.\n"
                         "Use parameters 'quantile_lines', 'quantiles' as needed.")

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
                 quantiles=quantiles,
                 quantile_lines=quantile_lines,
                 scale=scale, trim=trim, tails_cutoff=tails_cutoff, kernel=kernel, bw=bw, adjust=adjust, n=n, fs_max=fs_max,
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_ydotplot(mapping=None, *, data=None, show_legend=None, sampling=None, tooltips=None,
                  binwidth=None,
                  bins=None,
                  method=None,
                  stackdir=None,
                  stackratio=None,
                  dotsize=None,
                  stackgroups=None,
                  center=None,
                  boundary=None,
                  color_by=None, fill_by=None,
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
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`
        Result of the call to the `sampling_xxx()` function.
        To prevent any sampling for this layer pass value "none" (string "none").
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specify appearance, style and content.
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
        Separate overlapping stack groups when stackgroups=False.
        Overlap stack groups when method='dotdensity' and stackgroups=True.
        Stack dots across groups when method='histodot' and stackgroups=True.
    center : float
        When method is 'histodot', this specifies x-value to align bin centers to.
    boundary : float
        When method is 'histodot', this specifies x-value to align bin boundary
        (i.e. point between bins) to.
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
    With 'dotdensity' binning, the bin positions are determined by the data and binwidth, which is the maximum width of each bin.
    With 'histodot' binning, the bins have fixed positions and fixed widths, much like a histogram.

    Computed variables:

    - ..count.. : number of points with y-axis coordinate in the same bin.
    - ..binwidth.. : max width of each bin if method is 'dotdensity'; width of each bin if method is 'histodot'.

    `geom_ydotplot()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - stroke : width of the dot border.

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
    if 'stat' in other_args:
        print("WARN: using 'stat' parameter for dotplot was deprecated.")
        other_args.pop('stat')

    return _geom('ydotplot',
                 mapping=mapping,
                 data=data,
                 stat=None,
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
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_area_ridges(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                     trim=None, tails_cutoff=None, kernel=None, adjust=None, bw=None, n=None, fs_max=None,
                     min_height=None, scale=None, quantiles=None, quantile_lines=None,
                     color_by=None, fill_by=None,
                     **other_args):
    """
    Plot the sum of the `y` and `height` aesthetics versus `x`. Heights of the ridges are relatively scaled.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='densityridges'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'densityridges' (computes and draws kernel density estimate for each ridge).
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
    trim : bool, default=False
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
        Adjust the value of bandwidth by multiplying it. Change how smooth the frequency curve is.
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
        Draw vertical lines at the given quantiles of the density estimate.
    quantile_lines : bool, default=False
        Show the quantile lines.
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

    - ..height.. : density scaled for the ridges, according to area, counts or to a constant maximum height.
    - ..density.. : density estimate.
    - ..count.. : density * number of points.
    - ..scaled.. : density estimate, scaled to maximum of 1.
    - ..quantile.. : quantile estimate.

    `geom_area_ridges()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - y : y-axis coordinates.
    - height : height of the ridge. Assumed to be between 0 and 1, though this is not required.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : lines width.
    - linetype : type of the line of border. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.
    - weight : used by 'densityridges' stat to compute weighted density.
    - quantile : quantile values to draw quantile lines and fill quantiles of the geometry by color.

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
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_ribbon(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                color_by=None, fill_by=None,
                **other_args):
    """
    Display a y interval defined by `ymin` and `ymax`.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
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
    `geom_ribbon()` draws a ribbon bounded by `ymin` and `ymax`, or a vertical ribbon, bounded by `xmin`, `xmax`.

    `geom_ribbon()` understands the following aesthetics mappings:

    - x or y: x-axis or y-axis coordinates for horizontal or vertical ribbon, respectively.
    - ymin or xmin: y-axis or x-axis coordinates of the lower bound for horizontal or vertical ribbon, respectively.
    - ymax or xmax: y-axis or x-axis coordinates of the upper bound for horizontal or vertical ribbon, respectively.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : lines width.
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
                        tooltips=layer_tooltips()\\
                            .format('^ymin', '.1f').line('min temp|^ymin')\\
                            .format('^ymax', '.1f').line('max temp|^ymax'))

    """
    return _geom('ribbon',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_area(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
              flat=None, color_by=None, fill_by=None,
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
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, default='gstack'
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
    flat : bool, default=False.
        True - keep a line straight (corresponding to a loxodrome in case of Mercator projection).
        False - allow a line to be reprojected, so it can become a curve.
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
    `geom_area()` draws an area bounded by the data and x axis.

    `geom_area()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - y : y-axis coordinates.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
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
                 flat=flat,
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_density(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                 orientation=None,
                 trim=None,
                 kernel=None,
                 adjust=None,
                 bw=None,
                 n=None,
                 fs_max=None,
                 quantiles=None,
                 quantile_lines=None,
                 color_by=None, fill_by=None,
                 **other_args):
    """
    Display kernel density estimate, which is a smoothed version of the histogram.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='density'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    quantiles : list of float, default=[0.25, 0.5, 0.75]
        Draw horizontal lines at the given quantiles of the density estimate.
    quantile_lines : bool, default=False
        Show the quantile lines.
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

    - ..density.. : density estimate (mapped by default).
    - ..count.. : density * number of points.
    - ..scaled.. : density estimate, scaled to maximum of 1.
    - ..quantile.. : quantile estimate.

    `geom_density()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : lines width.
    - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.
    - weight : used by 'density' stat to compute weighted density.
    - quantile : quantile values to draw quantile lines and fill quantiles of the geometry by color.

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
        :emphasize-lines: 7-8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 200
        np.random.seed(42)
        ggplot({'x': np.random.normal(size=n)}) + \\
            geom_density(aes(x='x', fill='..quantile..'), color='black', size=1, \\
                         quantiles=[0, .02, .1, .5, .9, .98, 1], quantile_lines=True)

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
                 quantiles=quantiles, quantile_lines=quantile_lines,
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_density2d(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                   kernel=None,
                   adjust=None,
                   bw=None,
                   n=None,
                   bins=None,
                   binwidth=None,
                   color_by=None,
                   **other_args):
    """
    Display density function contour.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='density2d'
        The statistical transformation to use on the data for this layer, as a string.
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
    kernel : str, default='gaussian'
        The kernel we use to calculate the density function.
        Choose among 'gaussian', 'cosine', 'optcosine', 'rectangular' (or 'uniform'),
        'triangular', 'biweight' (or 'quartic'), 'epanechikov' (or 'parabolic').
    bw : str or list of float
        The method (or exact value) of bandwidth.
        Either a string (choose among 'nrd0' and 'nrd'), or a float array of length 2.
    adjust : float
        Adjust the value of bandwidth by multiplying it. Change how smooth the frequency curve is.
    n : list of int
        The number of sampled points for plotting the function
        (on x and y direction correspondingly).
    bins : int
        Number of levels.
    binwidth : float
        Distance between levels.
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
    `geom_density2d()` draws density function.

    Computed variables:

    - ..group.. : number of density estimate contour line.
    - ..level.. : calculated value of the density estimate for given contour line.

    `geom_density2d()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - y : y-axis coordinates.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : lines width.
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

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        data = {'x': 10 * np.random.normal(size=n) - 100, \\
                'y': 3 * np.random.normal(size=n) + 40}
        ggplot(data, aes('x', 'y')) + geom_livemap() + \\
            geom_density2d()

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
                 color_by=color_by,
                 **other_args)


def geom_density2df(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None,
                    tooltips=None,
                    kernel=None,
                    adjust=None,
                    bw=None,
                    n=None,
                    bins=None,
                    binwidth=None,
                    color_by=None, fill_by=None,
                    **other_args):
    """
    Fill density function contour.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='density2df'
        The statistical transformation to use on the data for this layer, as a string.
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
    kernel : str, default='gaussian'
        The kernel we use to calculate the density function.
        Choose among 'gaussian', 'cosine', 'optcosine', 'rectangular' (or 'uniform'),
        'triangular', 'biweight' (or 'quartic'), 'epanechikov' (or 'parabolic').
    bw : str or list of float
        The method (or exact value) of bandwidth.
        Either a string (choose among 'nrd0' and 'nrd'), or a float array of length 2.
    adjust : float
        Adjust the value of bandwidth by multiplying it. Change how smooth the frequency curve is.
    n : list of int
        The number of sampled points for plotting the function
        (on x and y direction correspondingly).
    bins : int
        Number of levels.
    binwidth : float
        Distance between levels.
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
    `geom_density2df()` fills density contours.

    Computed variables:

    - ..group.. : number of density estimate contour band.
    - ..level.. : calculated value of the density estimate for given contour band.

    `geom_density2df()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").

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

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        data = {'x': 10 * np.random.normal(size=n) - 100, \\
                'y': 3 * np.random.normal(size=n) + 40}
        ggplot(data, aes('x', 'y')) + geom_livemap() + \\
            geom_density2df(aes(fill='..group..'), \\
                            alpha=.5, show_legend=False)

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
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_jitter(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                width=None, height=None,
                color_by=None, fill_by=None,
                seed=None,
                **other_args):
    """
    Display jittered points, especially for discrete plots or dense plots.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    position : str or `FeatureSpec`, default='jitter'
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
    width : float, default=0.4
        Amount of horizontal variation. The jitter is added in both directions, so the total spread is twice the specified parameter.
        Typically ranges between 0 and 0.5. Values that are greater than 0.5 lead to overlapping of the points.
    height : float, default=0.4
        Amount of vertical variation. The jitter is added in both directions, so the total spread is twice the specified parameter.
        Typically ranges between 0 and 0.5. Values that are greater than 0.5 lead to overlapping of the points.
    color_by : {'fill', 'color', 'paint_a', 'paint_b', 'paint_c'}, default='color'
        Define the color aesthetic for the geometry.
    fill_by : {'fill', 'color', 'paint_a', 'paint_b', 'paint_c'}, default='fill'
        Define the fill aesthetic for the geometry.
    seed : int
        A random seed to make the jitter reproducible.
        If None (the default value), the seed is initialised with a random value.
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
    - alpha : transparency level of a point. Accept values between 0 and 1.
    - color (colour) : color of the geometry. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. Is applied only to the points of shapes having inner area. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - shape : shape of the point, an integer from 0 to 25.
    - size : size of the point.
    - stroke : width of the shape border. Applied only to the shapes having border.

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
                        seed=37, show_legend=False, width=.25) + \\
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
                 width=width, height=height,
                 color_by=color_by, fill_by=fill_by,
                 seed=seed,
                 **other_args)


def geom_qq(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
            distribution=None,
            dparams=None,
            color_by=None, fill_by=None,
            **other_args):
    """
    Display quantile-quantile plot.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
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
    The Q-Q plot is used for comparing two probability distributions
    (sample and theoretical) by plotting their quantiles against each other.

    If the two distributions being compared are similar, the points in the Q-Q plot
    will approximately lie on the straight line.

    Computed variables:

    - ..theoretical.. : theoretical quantiles.
    - ..sample.. : sample quantiles.

    `geom_qq()` understands the following aesthetics mappings:

    - sample : y-axis value.
    - alpha : transparency level of a point. Accept values between 0 and 1.
    - color (colour) : color of the geometry. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. Is applied only to the points of shapes having inner area. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - shape : shape of the point, an integer from 0 to 25.
    - size : size of the point.
    - stroke : width of the shape border. Applied only to the shapes having border.

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
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_qq2(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
             color_by=None, fill_by=None,
             **other_args):
    """
    Display quantile-quantile plot.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
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
    - alpha : transparency level of a point. Accept values between 0 and 1.
    - color (colour) : color of the geometry. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. Is applied only to the points of shapes having inner area. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - shape : shape of the point, an integer from 0 to 25.
    - size : size of the point.
    - stroke : width of the shape border. Applied only to the shapes having border.

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
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_qq_line(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                 distribution=None,
                 dparams=None,
                 quantiles=None,
                 color_by=None,
                 **other_args):
    """
    Display quantile-quantile fitting line.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
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
    The Q-Q line plot is used for comparing two probability distributions
    (sample and theoretical) by plotting line passed through the pair of corresponding quantiles.

    Computed variables:

    - ..theoretical.. : theoretical quantiles.
    - ..sample.. : sample quantiles.

    `geom_qq_line()` understands the following aesthetics mappings:

    - sample : y-axis value.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
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
                 color_by=color_by,
                 **other_args)


def geom_qq2_line(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                  quantiles=None,
                  color_by=None,
                  **other_args):
    """
    Display quantile-quantile fitting line.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
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
    quantiles : list, default=[0.25, 0.75]
        Pair of quantiles to use when fitting the Q-Q line.
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
    The Q-Q line plot is used for comparing two probability distributions
    by plotting line passed through the pair of corresponding quantiles.

    `geom_qq2_line()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
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
                 color_by=color_by,
                 **other_args)


def geom_freqpoly(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                  orientation=None,
                  color_by=None,
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
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='bin'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    `geom_freqpoly()` connects the top points in `geom_bar()`.

    Computed variables:

    - ..count.. : number of points with x-axis coordinate in the same bin.

    `geom_freqpoly()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
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
                 color_by=color_by,
                 **other_args)


def geom_step(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
              direction=None,
              color_by=None,
              **other_args):
    """
    Connect observations in the order in which they appear in the data by stairs.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    direction : {'hv', 'vh'}, default='hv'
        'hv' or 'HV' stands for horizontal then vertical;
        'vh' or 'VH' stands for vertical then horizontal.
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
    `geom_step()` draws steps between the observations in the order of X.

    `geom_step()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
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
                 tooltips=tooltips,
                 direction=direction,
                 color_by=color_by,
                 **other_args)


def geom_rect(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
              map=None, map_join=None, use_crs=None,
              color_by=None, fill_by=None,
              **other_args):
    """
    Display an axis-aligned rectangle defined by two corners.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame` or `GeoDataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
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
    map : `GeoDataFrame` or `Geocoder`
        Bounding boxes of geometries will be drawn.
    map_join : str or list
        Keys used to join map coordinates with data.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    use_crs : str, optional, default="EPSG:4326" (aka WGS84)
        EPSG code of the coordinate reference system (CRS) or the keyword "provided".
        If an EPSG code is given, then all the coordinates in `GeoDataFrame` (see the `map` parameter)
        will be projected to this CRS.
        Specify "provided" to disable any further re-projection and to keep the `GeoDataFrame’s` original CRS.
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
    `geom_rect()` draws rectangles.

    `geom_rect()` understands the following aesthetics mappings:

    - xmin : x-axis value.
    - xmax : x-axis value.
    - ymin : y-axis value.
    - ymax : y-axis value.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : lines width.
    - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    |

    The `data` and `map` parameters of `GeoDataFrame` type support shapes `MultiPoint`, `Line`, `MultiLine`, `Polygon` and `MultiPolygon`.

    The `map` parameter of `Geocoder` type implicitly invokes `limits()` function.

    |

    The conventions for the values of `map_join` parameter are as follows:

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

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        geometry = geocode_countries("Kazakhstan").get_boundaries().iloc[0].geometry
        bbox = geometry.bounds
        ggplot() + geom_livemap() + \\
            geom_rect(xmin=bbox[0], ymin=bbox[1], xmax=bbox[2], ymax=bbox[3], alpha=.5)

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
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_segment(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                 arrow=None, flat=None, geodesic=None, spacer=None, color_by=None, **other_args):
    """
    Draw a straight line segment between two points.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    arrow : `FeatureSpec`
        Specification for arrow head, as created by `arrow()` function.
    flat : bool, default=False.
        True - keep a line straight (corresponding to a loxodrome in case of Mercator projection).
        False - allow a line to be reprojected, so it can become a curve.
    geodesic : bool, default=False
        Draw geodesic. Coordinates expected to be in WGS84. Works only with `geom_livemap()`.
    spacer : float, default=0.0
        Space to shorten a segment by moving the start/end.
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
    `geom_segment()` draws segments.

    `geom_segment()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - xend : x-axis value.
    - yend : y-axis value.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : line width.
    - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.
    - size_start : offset from the segment start coordinate (usually equal to the size of the point object from which the segment starts to avoid overlapping with it).
    - size_end : offset from the segment end coordinate (usually equal to the size of the point object from which the segment ends to avoid overlapping with it).
    - stroke_start : offset from the segment start coordinate (usually equal to the stroke of the point object from which the segment starts to avoid overlapping with it).
    - stroke_end : offset from the segment end coordinate (usually equal to the stroke of the point object from which the segment ends to avoid overlapping with it).

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

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 18-20

        from lets_plot import *
        LetsPlot.setup_html()
        pushkin_1829_journey = {
            "dep_city": ["Moscow", "Oryol", "Novocherkassk", "Stavropol", \\
                         "Georgiyevsk", "Vladikavkaz", "Tiflis", "Kars"],
            "arr_city": ["Oryol", "Novocherkassk", "Stavropol", "Georgiyevsk", \\
                         "Vladikavkaz", "Tiflis", "Kars", "Erzurum"],
            "dep_lon": [37.618423, 36.098689, 40.110401, 41.9734, \\
                        43.4577689, 44.66778, 44.783333, 43.09495],
            "arr_lon": [36.098689, 40.110401, 41.9734, 43.4577689, \\
                        44.66778, 44.783333, 43.09495, 41.27694],
            "dep_lat": [55.751244, 52.929697, 47.414101, 45.0428, \\
                        44.1497667, 43.03667, 41.716667, 40.60199],
            "arr_lat": [52.929697, 47.414101, 45.0428, 44.1497667, \\
                        43.03667, 41.716667, 40.60199, 39.90861],
        }
        ggplot(pushkin_1829_journey) + geom_livemap(const_size_zoomin=0) + \\
            geom_segment(aes(x="dep_lon", y="dep_lat", xend="arr_lon", yend="arr_lat"), \\
                         size=1, color="#fc4e2a", arrow=arrow(), \\
                         tooltips=layer_tooltips().line("from @dep_city to @arr_city"))

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
                 geodesic=geodesic,
                 spacer=spacer,
                 color_by=color_by,
                 **other_args)


def geom_curve(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
               arrow=None,
               curvature=None, angle=None, ncp=None,
               spacer=None,
               color_by=None, **other_args):
    """
    Draw a curved line.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    arrow : `FeatureSpec`
        Specification for arrow head, as created by `arrow()` function.
    curvature : float, default=0.5
        The amount of curvature.
        Negative values produce left-hand curves, positive values produce right-hand curves,
        and zero produces a straight line.
    angle : float, default=90
        Angle in degrees, giving an amount to skew the control points of the curve.
        Values less than 90 skew the curve towards the start point
        and values greater than 90 skew the curve towards the end point.
    ncp : int, default=5
        The number of control points used to draw the curve. More control points creates a smoother curve.
    spacer : float, default=0.0
        Space to shorten a curve by moving the start/end.
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
    `geom_curve()` draws a curved lines.

    `geom_curve()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - xend : x-axis value.
    - yend : y-axis value.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry lines. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : line width.
    - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.
    - size_start : offset from the start coordinate (usually equal to the size of the point object from which the curve starts to avoid overlapping with it).
    - size_end : offset from the end coordinate (usually equal to the size of the point object from which the curve ends to avoid overlapping with it).
    - stroke_start : offset from the start coordinate (usually equal to the stroke of the point object from which the curve starts to avoid overlapping with it).
    - stroke_end : offset from the end coordinate (usually equal to the stroke of the point object from which the curve ends to avoid overlapping with it).

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 3

        from lets_plot import *
        LetsPlot.setup_html()
        ggplot() + geom_curve(x=0, y=0, xend=1, yend=0, curvature=0.7, angle=30)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6-8

        from lets_plot import *
        LetsPlot.setup_html()
        data = {'x': [-1, -1, 1, 1], 'y': [-1, 1, 1, -1]}
        ggplot(data, aes(x='x', y='y')) + \\
            geom_point(size=14) + \\
            geom_curve(xend=0, yend=0, size_start=14, \\
                       curvature = -0.5, \\
                       arrow = arrow(ends='first')) + \\
            coord_fixed()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 18-19

        from lets_plot import *
        LetsPlot.setup_html()
        city = ["Moscow", "Oryol", "Novocherkassk", "Stavropol", \\
               "Georgiyevsk", "Vladikavkaz", "Tiflis", "Kars", "Erzurum"]
        latitude = [55.751244, 52.929697, 47.414101, 45.0428, \\
                   44.1497667, 43.03667, 41.716667, 40.60199, 39.90861]
        longitude = [37.618423, 36.098689, 40.110401, 41.9734, \\
                    43.4577689, 44.66778, 44.783333, 43.09495, 41.27694]
        pushkin_1829_cities = {"city": city, "latitude": latitude, "longitude": longitude}
        pushkin_1829_path = {
            "from_lon": longitude[:-1],
            "from_lat": latitude[:-1],
            "to_lon": longitude[1:],
            "to_lat": latitude[1:]
        }
        ggplot() + \\
            geom_livemap(const_size_zoomin=0) + \\
            geom_curve(aes("from_lon", "from_lat", xend="to_lon", yend="to_lat"), data=pushkin_1829_path, \\
                       size_end=3, color="#fc4e2a", curvature=-0.2, arrow=arrow(type='closed', length=5)) + \\
            geom_point(aes("longitude", "latitude"), data=pushkin_1829_cities, \\
                       size=3, color="#fc4e2a", tooltips=layer_tooltips().line("@city"))

    """
    return _geom('curve',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 arrow=arrow,
                 curvature=curvature, angle=angle, ncp=ncp,
                 spacer=spacer,
                 color_by=color_by,
                 **other_args)


def geom_spoke(mapping=None, *, data=None, position=None, show_legend=None, sampling=None, tooltips=None,
               arrow=None, pivot=None,
               color_by=None, **other_args):
    """
    Draw a straight line segment with given length and angle from the starting point.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
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
    arrow : `FeatureSpec`
        Specification for arrow head, as created by `arrow()` function.
    pivot : {'tail', 'middle', 'mid', 'tip'}, default='tail'
        The part of the segment that is anchored to the plane. The segment rotates about this point.
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
    `geom_spoke()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - angle : slope's angle in radians.
    - radius : segment length.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the line. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : line width.
    - linetype : type of the line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 3

        from lets_plot import *
        LetsPlot.setup_html()
        ggplot() + geom_spoke(x=0, y=0, angle=0, radius=1)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 17

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 21
        a, b = -1, 1
        d = (b - a) / (n - 1)
        space = np.linspace(a, b, n)
        X, Y = np.meshgrid(space, space)
        Z = X**2 + Y**2
        dY, dX = np.gradient(Z, d)
        R = np.sqrt(dX**2 + dY**2)
        nR = R / R.max() * d
        A = np.arctan2(dY, dX)
        data = dict(x=X.reshape(-1), y=Y.reshape(-1), z=Z.reshape(-1), r=nR.reshape(-1), a=A.reshape(-1))
        ggplot(data, aes('x', 'y', color='z')) + \\
            geom_point(size=1.5) + \\
            geom_spoke(aes(angle='a', radius='r')) + \\
            scale_color_gradient(low='#2c7bb6', high='#d7191c') + \\
            coord_fixed()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 13

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 10
        a, b = 10, 30
        d = (b - a) / (n - 1)
        space = np.linspace(a, b, n)
        X, Y = np.meshgrid(space, space)
        A = np.arctan2(*np.gradient(X * Y, d))
        data = dict(x=X.reshape(-1), y=Y.reshape(-1), a=A.reshape(-1))
        ggplot(data, aes('x', 'y')) + \\
            geom_livemap() + \\
            geom_spoke(aes(angle='a', radius='a'))

    """
    return _geom('spoke',
                 mapping=mapping,
                 data=data,
                 stat=None,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 arrow=arrow,
                 pivot=pivot,
                 color_by=color_by,
                 **other_args)


def geom_text(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
              map=None, map_join=None, use_crs=None,
              label_format=None,
              na_text=None,
              nudge_x=None, nudge_y=None,
              color_by=None,
              **other_args):
    """
    Add a text directly to the plot.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame` or `GeoDataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    map : `GeoDataFrame` or `Geocoder`
        Data containing coordinates of points.
    map_join : str or list
        Keys used to join map coordinates with data.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    use_crs : str, optional, default="EPSG:4326" (aka WGS84)
        EPSG code of the coordinate reference system (CRS) or the keyword "provided".
        If an EPSG code is given, then all the coordinates in `GeoDataFrame` (see the `map` parameter)
        will be projected to this CRS.
        Specify "provided" to disable any further re-projection and to keep the `GeoDataFrame’s` original CRS.
    label_format : str
        Format used to transform label mapping values to a string.
        Examples:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see https://lets-plot.org/python/pages/formats.html.
    na_text : str, default='n/a'
        Text to show for missing values.
    nudge_x : float
        Horizontal adjustment to nudge labels by.
    nudge_y : float
        Vertical adjustment to nudge labels by.
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
    Adds text directly to the plot.

    `geom_text()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - size : font size.
    - label : text to add to plot.
    - family : font family. Possible values: 'sans', 'serif', 'mono', any other like: "Times New Roman". The default is 'sans'.
    - fontface : font style and weight. Possible values: 'plain', 'bold', 'italic', 'bold italic'. The default is 'plain'.
    - hjust : horizontal text alignment. Possible values: 'left', 'middle', 'right' or number between 0 ('left') and 1 ('right'). There are two special alignments: 'inward' (aligns text towards the plot center) and 'outward' (away from the plot center).
    - vjust : vertical text alignment. Possible values: 'bottom', 'center', 'top' or number between 0 ('bottom') and 1 ('top'). There are two special alignments: 'inward' (aligns text towards the plot center) and 'outward' (away from the plot center).
    - angle : text rotation angle in degrees.
    - lineheight : line height multiplier applied to the font size in the case of multi-line text.

    |

    The `data` and `map` parameters of `GeoDataFrame` type support shapes `Point` and `MultiPoint`.

    The `map` parameter of `Geocoder` type implicitly invokes `centroids()` function.

    |

    The conventions for the values of `map_join` parameter are as follows:

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
        :emphasize-lines: 11-12

        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        cities = ["New York", "Los Angeles"]
        states = ["NY", "CA"]
        titles = ['{0} ({1})'.format(city, state) \\
                  for city, state in zip(cities, states)]
        data = {"city": cities, "state": states, "title": titles}
        centroids = geocode_cities(data["city"]).get_centroids()
        ggplot(data) + geom_livemap(tiles=maptiles_lets_plot(theme='dark')) + \\
            geom_text(aes(label="title"), map=centroids, map_join="city", \\
                      size=7, color="yellow", fontface='bold')

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
                 color_by=color_by,
                 **other_args)


def geom_label(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
               map=None, map_join=None, use_crs=None,
               label_format=None,
               na_text=None,
               nudge_x=None, nudge_y=None,
               label_padding=None, label_r=None, label_size=None,
               alpha_stroke=None,
               color_by=None, fill_by=None,
               **other_args):
    """
    Add a text directly to the plot with a rectangle behind the text.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame` or `GeoDataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    map : `GeoDataFrame` or `Geocoder`
        Data containing coordinates of points.
    map_join : str or list
        Keys used to join map coordinates with data.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    use_crs : str, optional, default="EPSG:4326" (aka WGS84)
        EPSG code of the coordinate reference system (CRS) or the keyword "provided".
        If an EPSG code is given, then all the coordinates in `GeoDataFrame` (see the `map` parameter)
        will be projected to this CRS.
        Specify "provided" to disable any further re-projection and to keep the `GeoDataFrame’s` original CRS.
    label_format : str
        Format used to transform label mapping values to a string.
        Examples:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see https://lets-plot.org/python/pages/formats.html.
    nudge_x : float
        Horizontal adjustment to nudge labels by.
    nudge_y : float
        Vertical adjustment to nudge labels by.
    na_text : str, default='n/a'
        Text to show for missing values.
    label_padding : float
        Amount of padding around label. Default is 0.25 of font size.
    label_r : float
        Radius of rounded corners. Default is 0.15 of label height.
    label_size : float, default = 1.0
        Size of label border.
    alpha_stroke : bool, default=False
        Enable the applying of 'alpha' to 'color' (label text and border).
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
    `geom_label()` adds a text directly to the plot and draws a rectangle behind it, making it easier to read.

    `geom_label()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill: background color of the label.
    - size : font size.
    - label : text to add to plot.
    - family : font family. Possible values: 'sans', 'serif', 'mono', any other like: "Times New Roman". The default is 'sans'.
    - fontface : font style and weight. Possible values: 'plain', 'bold', 'italic', 'bold italic'. The default is 'plain'.
    - hjust : horizontal alignment. Possible values: 'left', 'middle', 'right' or number between 0 ('left') and 1 ('right'). There are two special alignments: 'inward' (aligns label towards the plot center) and 'outward' (away from the plot center).
    - vjust : vertical alignment. Possible values: 'bottom', 'center', 'top' or number between 0 ('bottom') and 1 ('top'). There are two special alignments: 'inward' (aligns label towards the plot center) and 'outward' (away from the plot center).
    - angle : rotation angle in degrees.
    - lineheight : line height multiplier applied to the font size in the case of multi-line text.

    |

    The `data` and `map` parameters of `GeoDataFrame` type support shapes `Point` and `MultiPoint`.

    The `map` parameter of `Geocoder` type implicitly invokes `centroids()` function.

    |

    The conventions for the values of `map_join` parameter are as follows:

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
        :emphasize-lines: 12-13

        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        cities = ["New York", "Los Angeles"]
        states = ["NY", "CA"]
        titles = ['{0} ({1})'.format(city, state) \\
                  for city, state in zip(cities, states)]
        data = {"city": cities, "state": states, "title": titles}
        centroids = geocode_cities(data["city"]).get_centroids()
        ggplot(data) + geom_livemap() + \\
            geom_point(map=centroids, map_join="city") + \\
            geom_label(aes(label="title"), map=centroids, \\
                       map_join="city", size=7, hjust=0, vjust=0)

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
                 alpha_stroke=alpha_stroke,
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_pie(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None, labels=None,
             map=None, map_join=None, use_crs=None,
             hole=None,
             stroke_side=None,
             spacer_width=None, spacer_color=None,
             size_unit=None,
             color_by=None, fill_by=None,
             **other_args):
    """
    Draw pie chart.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame` or `GeoDataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='count2d'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count2d' (counts number of points with same x,y coordinate).
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
    labels : `layer_labels`
        Result of the call to the `layer_labels()` function.
        Specify style and content of the annotations.
    map : `GeoDataFrame` or `Geocoder`
        Data containing coordinates of points.
    map_join : str or list
        Keys used to join map coordinates with data.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    use_crs : str, optional, default="EPSG:4326" (aka WGS84)
        EPSG code of the coordinate reference system (CRS) or the keyword "provided".
        If an EPSG code is given, then all the coordinates in `GeoDataFrame` (see the `map` parameter)
        will be projected to this CRS.
        Specify "provided" to disable any further re-projection and to keep the `GeoDataFrame’s` original CRS.
    hole : float, default=0.0
        A multiplicative factor applied to the pie diameter to draw donut-like chart.
        Accept values between 0 and 1.
    stroke_side : {'outer', 'inner', 'both'}, default='both'
        Define which arcs of pie sector should have a stroke.
    spacer_width : float, default=0.75
        Line width between sectors.
        Spacers are not applied to exploded sectors and to sides of adjacent sectors.
    spacer_color : str
        Color for spacers between sectors. By default, the "paper" color is used.
    size_unit : {'x', 'y'}
        Relate the size of the pie chart to the length of the unit step along one of the axes.
        If None, no fitting is performed.
    color_by : {'fill', 'color', 'paint_a', 'paint_b', 'paint_c'}, default='color'
        Define the color aesthetic for the geometry.
    fill_by : {'fill', 'color', 'paint_a', 'paint_b', 'paint_c'}, default='fill'
        Define the source aesthetic for geometry filling.
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
    - ..sumprop.. : proportion of points with same (x,y) coordinate among all points in the dataset.
    - ..sumpct.. : proportion of points with same (x,y) coordinate among all points in the dataset in percent.

    `geom_pie()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - slice : values associated to pie sectors.
    - explode : values to explode slices away from their center point, detaching it from the main pie.
    - size : pie diameter.
    - fill : fill color. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - alpha : transparency level of the pie. Accept values between 0 and 1.
    - weight : used by 'count2d' stat to compute weighted sum instead of simple count.
    - stroke : width of inner and outer arcs of pie sector.
    - color : color of inner and outer arcs of pie sector.

    |

    The `data` and `map` parameters of `GeoDataFrame` type support shapes `Point` and `MultiPoint`.

    The `map` parameter of `Geocoder` type implicitly invokes `centroids()` function.

    |

    The conventions for the values of `map_join` parameter are as follows:

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
        :emphasize-lines: 4

        from lets_plot import *
        LetsPlot.setup_html()
        data = {'name': ['a', 'b', 'c', 'd', 'b'], 'value': [40, 90, 10, 50, 20]}
        ggplot(data) + geom_pie(aes(fill='name', weight='value'), size=.5, size_unit='x')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 4-6

        from lets_plot import *
        LetsPlot.setup_html()
        data = {'name': ['a', 'b', 'c', 'd', 'b'], 'value': [40, 90, 10, 50, 20], 'explode': [0, 0, 0.2, 0, 0]}
        ggplot(data) + geom_pie(aes(fill='name', weight='value', explode='explode'), \\
                                size=15, hole=0.2, color='black', stroke=2, stroke_side='both', \\
                                spacer_color='black', spacer_width=2)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5-9

        from lets_plot import *
        from lets_plot.mapping import *
        LetsPlot.setup_html()
        data = {'name': ['a', 'b', 'c', 'd', 'b'], 'value': [40, 90, 10, 50, 20]}
        ggplot(data) + geom_pie(aes(fill=as_discrete('name', order_by='..count..'), weight='value'), \\
                                size=15, hole=0.2, \\
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
                                size=15, hole=0.2, \\
                                labels=layer_labels(['..proppct..']).format('..proppct..', '{.1f}%'))

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-10

        from lets_plot import *
        from lets_plot.geo_data import *
        LetsPlot.setup_html()
        data = {"city": ["New York", "New York", "Philadelphia", "Philadelphia"], \\
                "est_pop_2020": [4_381_593, 3_997_959, 832_685, 748_846], \\
                "sex": ["female", "male", "female", "male"]}
        centroids = geocode_cities(data["city"]).get_centroids()
        ggplot() + geom_livemap() + \\
            geom_pie(aes(slice="est_pop_2020", fill="sex", size="est_pop_2020"), \\
                     stat='identity', data=data, map=centroids, map_join="city") + \\
            scale_size(guide='none')

    """
    if 'stroke_color' in other_args:
        print("WARN: The parameter 'stroke_color' for pie is no longer supported.")
        other_args.pop('stroke_color')

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
                 hole=hole,
                 stroke_side=stroke_side,
                 spacer_width=spacer_width,
                 spacer_color=spacer_color,
                 size_unit=size_unit,
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_lollipop(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
                  orientation=None,
                  dir=None, fatten=None, slope=None, intercept=None,
                  color_by=None, fill_by=None,
                  **other_args):
    """
    Draw lollipop chart.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
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
    orientation : {'x', 'y'}, default='x'
        Specify the axis that the baseline should run along.
        Possible values: 'x', 'y'.
    dir : {'v', 'h', 's'}, default='v'
        Direction of the lollipop stick.
        'v' for vertical, 'h' for horizontal, 's' for orthogonal to the baseline.
    fatten : float, default=2.5
        A multiplicative factor applied to size of the point.
    slope : float
        The baseline slope.
    intercept : float
        The value of y at the point where the baseline crosses the y axis.
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
    `geom_lollipop()` understands the following aesthetics mappings:

    - x : x-axis value.
    - y : y-axis value.
    - alpha : transparency level of the point. Accept values between 0 and 1.
    - color (colour) : color of the geometry. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. Is applied only to the points of shapes having inner area. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - shape : shape of the point, an integer from 0 to 25.
    - size : size of the point.
    - stroke : width of the shape border. Applied only to the shapes having border.
    - linewidth : stick width.
    - linetype : type of the stick line. Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.

    |

    When `slope=0`, the baseline cannot be parallel to the lollipop sticks.
    So, in this case, if `dir='h'`, the baseline will becomes vertical, as for infinity slope.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'x': [-3, -2, -1, 0, 1, 2, 3],
            'y': [2, 3, -2, 3, -1, 0, 4],
        }
        ggplot(data, aes('x', 'y')) + geom_lollipop()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8-9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {'v': np.random.randint(5, size=20)}
        ggplot(data, aes(y='v')) + \\
            geom_vline(xintercept=10) + \\
            geom_lollipop(stat='count', orientation='y', intercept=10, \\
                          fatten=5, linewidth=2)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10-11

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'x': [-3, -2, -1, 0, 1, 2, 3],
            'y': [-1, -3, -2, 3, -1, 2, -1],
            'g': ['a', 'a', 'b', 'b', 'b', 'a', 'a'],
        }
        ggplot(data, aes('x', 'y')) + \\
            geom_abline(slope=1, size=1.5, color="black") + \\
            geom_lollipop(aes(fill='g'), slope=1, shape=22, \\
                          size=5, stroke=2, color="black") + \\
            coord_fixed()

    """
    return _geom('lollipop',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 orientation=orientation,
                 dir=dir, fatten=fatten, slope=slope, intercept=intercept,
                 color_by=color_by, fill_by=fill_by,
                 **other_args)


def geom_count(mapping=None, *, data=None, stat=None, position=None, show_legend=None, sampling=None, tooltips=None,
               color_by=None, fill_by=None,
               **other_args):
    """
    Sum unique values.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : str, default='sum'
        The statistical transformation to use on the data for this layer, as a string.
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

    - ..n.. : number of points with same x-axis and y-axis coordinates.
    - ..prop.. : proportion of points with same x-axis and y-axis coordinates.
    - ..proppct.. : proportion of points with same x-axis and y-axis coordinates in percent.

    `geom_count()` understands the following aesthetics mappings:

    - x : x-axis coordinates.
    - y : y-axis coordinates.
    - alpha : transparency level of the point. Accept values between 0 and 1.
    - color (colour) : color of the geometry. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - fill : fill color. Is applied only to the points of shapes having inner area. String in the following formats: RGB/RGBA (e.g. "rgb(0, 0, 255)"); HEX (e.g. "#0000FF"); color name (e.g. "red"); role name ("pen", "paper" or "brush").
    - shape : shape of the point, an integer from 0 to 25.
    - size : size of the point.
    - stroke : width of the shape border. Applied only to the shapes having border.


    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10

        import numpy as np
        from lets_plot import *
        from lets_plot.mapping import as_discrete
        LetsPlot.setup_html()
        n = 50
        np.random.seed(42)
        x = [round(it) for it in np.random.normal(0, 1.5, size=n)]
        y = [round(it) for it in np.random.normal(0, 1.5, size=n)]
        ggplot({'x': x, 'y': y}, aes(x=as_discrete('x', order=1), y=as_discrete('y', order=1))) + \\
            geom_count()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10

        import numpy as np
        from lets_plot import *
        from lets_plot.mapping import as_discrete
        LetsPlot.setup_html()
        n = 50
        np.random.seed(42)
        x = [round(it) for it in np.random.normal(0, 1.5, size=n)]
        y = [round(it) for it in np.random.normal(0, 1.5, size=n)]
        ggplot({'x': x, 'y': y}, aes(x=as_discrete('x', order=1), y=as_discrete('y', order=1))) + \\
            geom_count(aes(size='..prop..', group='x'))

"""
    stat = 'sum' if stat is None else stat
    return _geom('point',
                 mapping=mapping,
                 data=data,
                 stat=stat,
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 color_by=color_by, fill_by=fill_by,
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
