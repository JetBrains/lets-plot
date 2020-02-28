#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec, LayerSpec
from .util import as_annotated_data, as_annotated_map_data, is_geo_data_frame, geo_data_frame_to_lon_lat, as_pair

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


def geom_point(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, animation=None,
               map_join=None, **other_args):
    """
    Points, as for a scatter plot.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary, pandas DataFrame or GeoDataFrame (supported shapes Point and MultiPoint), optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer.
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    animation : type of the animation, optional
        Codes and names: 0 = "none" (default), 1 = "ripple".
    map : dictionary, pandas DataFrame or GeoDataFrame (supported shapes Point and MultiPoint)
        Data containing coordinates of points.
        Can be used with aesthetic parameter 'map_id' for joining data and map coordinates.
        Dictionary and DataFrame object must contain keys/columns:
        1. 'x' or 'lon' or 'long'
        2. 'y' or 'lat'
    map_join : str, pair, optional
        Pair of names used to join map coordinates with data.
        str or first value in pair - column in data
        second value in pair - column in map
    other_args :
        Other arguments passed on to the layer. These are often aesthetics settings used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    The point geometry is used to create scatterplots. The scatterplot is useful for displaying the relationship
    between two continuous variables, although it can also be used with one continuous and one categorical variable,
    or two categorical variables.

    geom_point understands the following aesthetics mappings:
    - map_id : name used to join data with map coordinates
    - x : x-axis value
    - y : y-axis value
    - alpha : transparency level of the point
        Understands numbers between 0 and 1.
    - color (colour) : color of the geometry
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color to paint shape's inner points
        Is applied only to the points of shapes having inner points.
    - shape : shape of the point
    - size : size of the point

    Examples
    --------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> x = np.random.uniform(-1, 1, size=100)
    >>> y = np.random.normal(size=100)
    >>> dat = pd.DataFrame({'x': x, 'y': 25 * x ** 2 + y})
    >>> dat['class'] = ['0' if dat['x'][i] < 0 else '1' for i in range(100)]
    >>> p = ggplot(dat) + geom_point(aes(x='x', y='y', color='y', shape='class', fill='x', size='y'))
    >>> p += geom_point(shape=21, color='red', fill='green', size=5, stat='smooth')
    """
    return _geom('point', mapping, data, stat, position, show_legend, sampling=sampling, animation=animation,
                 map_join=map_join, **other_args)


def geom_path(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, animation=None,
              **other_args):
    """
    Connects observations in the order, how they appear in the data.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary, pandas DataFrame or GeoDataFrame (supported shapes LineString and MultiLineString), optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    animation : type of the animation, optional
        Codes and names: 0 = "none" (default), 1 = "dash", 2 = "plane", 3 = "circle".
    map : dictionary, pandas DataFrame or GeoDataFrame (supported shapes LineString and MultiLineString)
        Can be used with aesthetic parameter 'map_id' for joining data and map coordinates.

        Dictionary and DataFrame object must contain keys/columns:
        1. 'x' or 'lon' or 'long'
        2. 'y' or 'lat'
    map_join : str, pair, optional
        Pair of names used to join map coordinates with data.
        str or first value in pair - column in data
        second value in pair - column in map
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    geom_path connects the observations in the order in which they appear in the data.
    geom_path lets you explore how two variables are related over time.

    geom_path understands the following aesthetics mappings:
    - map_id : name used to join data with map coordinates
    - x : x-axis value
    - y : y-axis value
    - alpha : transparency level of a point
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - linetype : type of the line
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"
    - size : line width
    - speed : animation speed
        The number of pixels covered by animation object per second. Default value is 10.
    - flow : animation flow
        The number of animation objects passing a reference point per second. Default value is 0.1.

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from lets_plot import *
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
    >>> # plot brownian motion path
    >>> ggplot(dat) + geom_path()
    >>> # transform data via melt function
    >>> # to produce two trajectories
    >>> dat = pd.DataFrame(dat)
    >>> dat = pd.melt(dat, id_vars=['t'], value_vars=['W1', 'W2'])
    >>> p = ggplot(dat, aes(x='t', y='value', group='variable'))
    >>> p += geom_path(aes(color='variable', linetype='variable'), size=1, alpha=0.5)
    >>> p += geom_path(stat='smooth', color='red', linetype="dashed")
    """
    return _geom('path', mapping, data, stat, position, show_legend, sampling=sampling, animation=animation,
                 **other_args)


def geom_line(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, **other_args):
    """
    Connect points in order of the variable on the x-axis

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    geom_line connects the observations in the order of the variable on the x axis. geom_line can be used to plot
    time series.

    geom_line understands the following aesthetics mappings:
    - x : x-axis value
    - y : y-axis value
    - alpha : transparency level of a point
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - linetype : type of the line
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"
    - size : line width

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from lets_plot import *
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
    >>> p = ggplot(dat, aes(x='t', y='value', group='variable'))
    >>> p += geom_line(aes(color='variable', linetype='variable'), size=1, alpha=0.5)
    >>> p += geom_line(stat='smooth', color='red', linetype="dashed")
    """
    return _geom('line', mapping, data, stat, position, show_legend, sampling=sampling, **other_args)


def geom_smooth(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, **other_args):
    """
    Add a smoothed conditional mean.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    span : number, optional
        Amount of smoothing. A sensible value is usually 0.25 to 0.5.
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification

    Notes
    -----
    geom_smooth aids the eye in seeing patterns in the presence of overplotting.
    Computed variables:
    - y : predicted (smoothed) value
    - ymin : lower pointwise confidence interval around the mean
    - ymax : upper pointwise confidence interval around the mean
    - se : standard error
    geom_smooth understands the following aesthetics mappings:
    - x : x-axis value
    - y : y-axis value
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - linetype : type of the line of conditional mean line
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"
    - size : lines width
        Defines line width for conditional mean and confidence bounds lines.

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from scipy.stats import multivariate_normal
    >>> from lets_plot import *
    >>> N = 100
    >>> t = np.linspace(1, N, N)
    >>> a = 1
    >>> b = 0
    >>> M = 2
    >>> A = np.random.standard_normal(M) / N + a
    >>> B = np.random.standard_normal(M) / N + b
    >>> mean = np.zeros(M)
    >>> cov = np.eye(M)
    >>> Z = multivariate_normal.rvs(mean, cov, N)
    >>> X = np.outer(B, t / N)
    >>> X = X + Z.T
    >>> X = X + (np.full((N, M), 1) * A).T
    >>> dat = pd.DataFrame(X.T)
    >>> dat = pd.melt(dat)
    >>> dat["t"] = np.tile(t / N, M)
    >>> ggplot(dat, aes(x='t', y='value', group='variable')) + geom_point(aes(color='variable')) + geom_smooth(color='red')
    """
    return _geom('smooth', mapping, data, stat, position, show_legend, sampling=sampling, **other_args)


def geom_bar(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, **other_args):
    """
    Bar chart which makes the height of the bar proportional to the number of observed variable values, mapped to x axis.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    geom_bar makes the height of the bar proportional to the number of observed variable values, mapped to x axis.
    Is intended to use for discrete data. If used for continuous data with stat='bin' produces histogram for binned
    data. geom_bar handles no group aesthetics.

    geom_bar understands the following aesthetics mappings:
    - x : x-axis value (this values will produce cases or bins for bars)
    - y : y-axis value (this value will be used to multiply the case's or bin's counts)
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color of geometry filling
    - size : lines width
        Defines bar line width

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from scipy.stats import multivariate_normal
    >>> from lets_plot import *
    >>> N = 100
    >>> M = 3
    >>> mean = np.zeros(M)
    >>> mean = np.arange(M) * 5
    >>> cov = np.eye(M)
    >>> X = multivariate_normal.rvs(mean, cov, N)
    >>> X = X.astype(int) # comment this line to make variables continuous back
    >>> dat = pd.DataFrame(X)
    >>> dat = pd.melt(dat)
    >>> ggplot(dat, aes(x='value')) + geom_bar(stat='bin', color='red', fill='green', size=3)
    """
    return _geom('bar', mapping, data, stat, position, show_legend, sampling=sampling, **other_args)


def geom_histogram(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None,
                   bins=None,
                   binwidth=None,
                   center=None,
                   boundary=None,
                   **other_args):
    """
    Displays a 1d distribution by dividing variable mapped to x axis into bins and counting the number of observations
    in each bin.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional, default: "bin"
        The statistical transformation to use on the data for this layer.
    position : string, optional, default: "stack"
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    bins :
        Number of bins.  Overridden by binwidth. Defaults to 30
    binwidth :
        The width of the bins. The default is to use bin widths that cover the range of the data. You should always
        override this value, exploring multiple widths to find the best to illustrate the stories in your data.
    center : number, optional
        Specifies x-value to align bin centers to.
    boundary : number, optional
        Specifies x-value to align bin boundary (i.e. point berween bins) to.
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    geom_histogram displays a 1d distribution by dividing variable mapped to x axis into bins and counting the number
    of observations in each bin.

    geom_histogram understands the following aesthetics mappings:
    - x : x-axis value (this values will produce cases or bins for bars)
    - y : y-axis value, default: "..count..".
        Alternatively: '..density..'
    - weight : used by "bin" stat to compute weighted sum instead of simple count.
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color of geometry filling
    - size : lines width

    Examples
    ---------
    >>> import numpy as np
    >>> from lets_plot import *
    >>>
    >>> np.random.seed(123)
    >>> data = dict(
    >>>     x = np.random.normal(0, 1, 100)
    >>> )
    >>> ggplot(data) + geom_histogram(aes(x='x'))
    """
    return _geom('histogram', mapping, data, stat, position, show_legend, sampling=sampling,
                 bins=bins,
                 binwidth=binwidth,
                 center=center,
                 boundary=boundary,
                 **other_args)


def geom_bin2d(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None,
               bins=None,
               binwidth=None,
               drop=None,
               **other_args):
    """
    Displays a 1d distribution by dividing variable mapped to x axis into bins and counting the number of observations
    in each bin.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, default: "bin"
        The statistical transformation to use on the data for this layer.
    position : string, default: "stack"
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    bins : list of 2 numbers, default: [30,30]
        Number of bins in both directions, vertical and horizontal.  Overridden by binwidth.
    binwidth : list of 2 numbers, optional
        The width of the bins in both directions, vertical and horizontal. Overrides `bins`.
        The default is to use bin widths that cover the entire range of the data.
    drop : bool, optional, default: True
        Specifies whether to remove all bins with 0 counts.

Aesthetics
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    geom_bin2d applies rectangular grid to the plane then counts observation in each cell of the grid (bin).
    Uses geom_tile to display counts as a tile fill-color.

    geom_histogram understands the following aesthetics mappings:
    - x : x-axis value
    - y : y-axis value
    - weight : used by "bin" stat to compute weighted sum instead of simple count.
    - alpha : number in [0..1]
        Transparency level of a layer
    - color : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color of geometry filling, default: "..count..".
        Alternatively: '..density..'
    - size : lines width

    Examples
    ---------
    >>> import numpy as np
    >>> from lets_plot import *
    >>>
    >>> cov=[[1, 0],
    >>>      [0, 1]]
    >>> x, y = np.random.multivariate_normal(mean=[0,0], cov=cov, size=400).T
    >>>
    >>> data = dict(
    >>>     x = x,
    >>>     y = y
    >>> )
    >>> ggplot(data) + geom_bin2d(aes(x='x', y='y'), binwidth=[0.5,0.5])
    """
    return _geom('bin2d', mapping, data, stat, position, show_legend, sampling=sampling,
                 bins=bins,
                 binwidth=binwidth,
                 drop=drop,
                 **other_args)


def geom_tile(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, **other_args):
    """
    Rectangles with x, y values mapped to center

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    Understands the following aesthetics mappings:
    - x : x-axis coordinates of the center of rectangles.
    - y : y-axis coordinates of the center of rectangles.
    - width : width of a tile.
    - height : height of a tile.
    - alpha : transparency level of a layer
    - color (colour) : color of a geometry lines
    - fill : color of geometry filling
    - size : lines width
    - linetype : type of the line of tile's border
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"

    Examples
    ---------
    >>> import numpy as np
    >>> import matplotlib.mlab as mlab
    >>> from lets_plot import *
    >>> delta = 0.5
    >>> x = np.arange(-5.0, 5.0, delta)
    >>> y = np.arange(-5.0, 5.0, delta)
    >>> X, Y = np.meshgrid(x, y)
    >>> Z = mlab.bivariate_normal(X, Y, 1, 2, 1, 0)
    >>> Z = Z - mlab.bivariate_normal(X, Y, 2, 1)
    >>> x = X.reshape(-1)
    >>> y = Y.reshape(-1)
    >>> z = Z.reshape(-1)
    >>> dat = dict(x=x, y=y, z=z)
    >>> p = ggplot(dat, aes('x', 'y')) + geom_tile(aes(fill='z')) + geom_contour(aes(z='z'))
    """
    return _geom('tile', mapping, data, stat, position, show_legend, sampling=sampling, **other_args)


def geom_raster(mapping=None, data=None, stat=None, position=None, show_legend=None, **other_args):
    """
    Rectangles with x, y values mapped to center.
    Much faster than geom_tile but doesn't support width/height and color.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    Understands the following aesthetics mappings:
    - x : x-axis coordinates of the center of rectangles.
    - y : y-axis coordinates of the center of rectangles.
    - alpha : transparency level of a layer
    - fill : color of geometry filling

    Examples
    ---------
    >>> import numpy as np
    >>> import matplotlib.mlab as mlab
    >>> from lets_plot import *
    >>> delta = 0.5
    >>> x = np.arange(-5.0, 5.0, delta)
    >>> y = np.arange(-5.0, 5.0, delta)
    >>> X, Y = np.meshgrid(x, y)
    >>> Z = mlab.bivariate_normal(X, Y, 1, 2, 1, 0)
    >>> Z = Z - mlab.bivariate_normal(X, Y, 2, 1)
    >>> x = X.reshape(-1)
    >>> y = Y.reshape(-1)
    >>> z = Z.reshape(-1)
    >>> dat = dict(x=x, y=y, z=z)
    >>> p = ggplot(dat, aes('x', 'y')) + geom_raster(aes(fill='z')) + geom_contour(aes(z='z'))
    """
    return _geom('raster', mapping, data, stat, position, show_legend, **other_args)


def geom_errorbar(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, **other_args):
    """
    Error bars

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    geom_errorbar represents a vertical interval, defined by x, ymin, ymax.
    geom_errorbar understands the following aesthetics mappings:
    - x : x-axis coordinates
    - ymin : lower bound for error bar.
    - ymax : upper bound for error bar.
    - width : width of a bar.
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : lines width
        Defines bar line width
    - linetype : type of the line of tile's border
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"

    Examples
    ---------
    >>> import numpy as np
    >>> from lets_plot import *
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
    return _geom('errorbar', mapping, data, stat, position, show_legend, sampling=sampling, **other_args)


def geom_crossbar(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, fatten=None,
                  **other_args):
    """
    Bar with horizontal median line

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer.
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    fatten : number, default: 2.5
        A multiplicative factor applied to size of the middle bar
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    geom_crossbar represents a vertical interval, defined by x, ymin, ymax. The mean is represented by horizontal line.

    geom_crossbar understands the following aesthetics mappings:
    - x : x-axis coordinates
    - ymin : lower bound for error bar.
    - ymax : upper bound for error bar.
    - middle : position of median bar.
    - width : width of a bar.
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color of geometry filling.
    - size : lines width.
    - linetype : type of the line of tile's border
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"

    Examples
    ---------
    >>> from lets_plot import *
    >>>
    >>> data = dict(
    >>>     supp = ['OJ', 'OJ', 'OJ', 'VC', 'VC', 'VC'],
    >>>     dose = [0.5, 1.0, 2.0, 0.5, 1.0, 2.0],
    >>>     length = [13.23, 22.70, 26.06, 7.98, 16.77, 26.14],
    >>>     len_min = [11.83, 21.2, 24.50, 4.24, 15.26, 23.35],
    >>>     len_max = [15.63, 24.9, 27.11, 10.72, 19.28, 28.93]
    >>> )
    >>>
    >>> p = ggplot(data, aes(x='dose', color='supp'))
    >>> p + geom_crossbar(aes(ymin='len_min', ymax='len_max', middle='length'), fatten=5)
    """
    return _geom('crossbar', mapping, data, stat, position, show_legend, sampling=sampling, fatten=fatten, **other_args)


def geom_pointrange(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, fatten=None,
                    **other_args):
    """
    Vertical line defined by upper and lower value with mid-point at Y-location.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer.
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    fatten : number, default: 5.0
        A multiplicative factor applied to size of the middle bar
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    geom_pointrange represents a vertical interval, defined by x, ymin, ymax. The mid-point is defined by y.

    geom_pointrange understands the following aesthetics mappings:
    - x : x-axis coordinates
    - y : position of mid-point.
    - ymin : lower bound for error bar.
    - ymax : upper bound for error bar.
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines.
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color of geometry filling.
    - size : lines width, size of mid-point.
    - linetype : type of the line of tile's border
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"
    - shape : shape of the mid-point

    Examples
    ---------
    >>> from lets_plot import *
    >>>
    >>> data = dict(
    >>>     supp = ['OJ', 'OJ', 'OJ', 'VC', 'VC', 'VC'],
    >>>     dose = [0.5, 1.0, 2.0, 0.5, 1.0, 2.0],
    >>>     length = [13.23, 22.70, 26.06, 7.98, 16.77, 26.14],
    >>>     len_min = [11.83, 21.2, 24.50, 4.24, 15.26, 23.35],
    >>>     len_max = [15.63, 24.9, 27.11, 10.72, 19.28, 28.93]
    >>> )
    >>>
    >>> p = ggplot(data, aes(x='dose', color='supp'))
    >>> p + geom_crossbar(aes(ymin='len_min', ymax='len_max', y='length'), fatten=5)
    """
    return _geom('pointrange', mapping, data, stat, position, show_legend, sampling=sampling, fatten=fatten,
                 **other_args)


def geom_linerange(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None,
                   **other_args):
    """
    Line range, defined by an upper and lower value

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer.
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    geom_linerange represents a vertical interval, defined by x, ymin, ymax.
    geom_linerange understands the following aesthetics mappings:
    - x : x-axis coordinates
    - ymin : lower bound for line range.
    - ymax : upper bound for line range.
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : lines width
    - linetype : type of the line of tile's border
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"

    Examples
    ---------
    >>> from lets_plot import *
    >>>
    >>> data = dict(
    >>>     supp = ['OJ', 'OJ', 'OJ', 'VC', 'VC', 'VC'],
    >>>     dose = [0.5, 1.0, 2.0, 0.5, 1.0, 2.0],
    >>>     length = [13.23, 22.70, 26.06, 7.98, 16.77, 26.14],
    >>>     len_min = [11.83, 21.2, 24.50, 4.24, 15.26, 23.35],
    >>>     len_max = [15.63, 24.9, 27.11, 10.72, 19.28, 28.93]
    >>> )
    >>>
    >>> p = ggplot(data, aes(x='dose', color='supp'))
    >>> p + geom_linerange(aes(ymin='len_min', ymax='len_max'))
    """
    return _geom('linerange', mapping, data, stat, position, show_legend, sampling=sampling, **other_args)


def geom_contour(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, bins=None,
                 binwidth=None, **other_args):
    """
    Display contours of a 3d surface in 2d.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    bins : int, optional
        Number of levels.
    binwidth: double, optional
        Distance between levels.
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    geom_contour() displays contours of a 3d surface in 2d.
    Computed variables:
        level : height of a contour
    geom_contour understands the following aesthetics mappings:
    - x : x-axis coordinates of the center of rectangles, forming a tessellation.
    - y : y-axis coordinates of the center of rectangles, forming a tessellation.
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : lines width
        Defines bar line width
    - linetype : type of the line of tile's border
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> import matplotlib.mlab as mlab
    >>> from lets_plot import *
    >>> delta = 0.5
    >>> x = np.arange(-5.0, 5.0, delta)
    >>> y = np.arange(-5.0, 5.0, delta)
    >>> X, Y = np.meshgrid(x, y)
    >>> Z = mlab.bivariate_normal(X, Y, 1, 2, 1, 0)
    >>> Z = Z - mlab.bivariate_normal(X, Y, 2, 1)
    >>> x = X.reshape(-1)
    >>> y = Y.reshape(-1)
    >>> z = Z.reshape(-1)
    >>> dat = dict(x=x, y=y, z=z)
    >>> p = ggplot(dat, aes('x', 'y')) + geom_tile(aes(fill='z')) + geom_contour(aes(z='z', color='..level..'))
    """
    return _geom('contour', mapping, data, stat, position, show_legend, sampling=sampling, bins=bins, binwidth=binwidth,
                 **other_args)


def geom_contourf(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, bins=None,
                  binwidth=None, **other_args):
    """
    Fill contours of a 3d surface in 2d.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    bins : int, optional
        Number of levels.
    binwidth: double, optional
        Distance between levels.
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    geom_contourf() fills contours of a 3d surface in 2d.
    Computed variables:
        level : height of a contour
    geom_contour understands the following aesthetics mappings:
    - x : x-axis coordinates of the center of rectangles, forming a tessellation.
    - y : y-axis coordinates of the center of rectangles, forming a tessellation.
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - fill : color of a geometry areas
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> import matplotlib.mlab as mlab
    >>> delta = 0.5
    >>> x = np.arange(-5.0, 5.0, delta)
    >>> y = np.arange(-5.0, 5.0, delta)
    >>> X, Y = np.meshgrid(x, y)
    >>> Z = mlab.bivariate_normal(X, Y, 1, 2, 1, 0)
    >>> Z = Z - mlab.bivariate_normal(X, Y, 2, 1)
    >>> x = X.reshape(-1)
    >>> y = Y.reshape(-1)
    >>> z = Z.reshape(-1)
    >>> dat = dict(x=x, y=y, z=z)
    >>> p = ggplot(dat, aes('x', 'y', z='z')) + geom_contour() + geom_contourf(aes(fill='..level..'))
    """
    return _geom('contourf', mapping, data, stat, position, show_legend, sampling=sampling, bins=bins,
                 binwidth=binwidth, **other_args)


def geom_polygon(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, **other_args):
    """
    Display a polygon (filled path).

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary, pandas DataFrame or GeoDataFrame (supported shapes Polygon and MultiPolygon), optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    map : dictionary, pandas DataFrame or GeoDataFrame (supported shapes Polygon and MultiPolygon)
        Data (Dictionary, DataFrame or GeoDataFrame object) contains coordinates of polygon vertices on map.
        Can be used with aesthetic parameter 'map_id' for joining data and map coordinates.
        Dictionary and DataFrame object must contain keys/columns:
        1. 'x' or 'lon' or 'long'
        2. 'y' or 'lat'
    map_join : str, pair, optional
        Pair of names used to join map coordinates with data.
        str or first value in pair - column in data
        second value in pair - column in map
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
        geom_polygon draws polygons, which are filled paths. Each vertex of the polygon requires a separate row in the
         data.
     geom_polygon understands the following aesthetics mappings:
    - map_id : name used to join data with map coordinates
    - x : x-axis coordinates of the vertices of the polygon.
    - y : y-axis coordinates of the vertices of the polygon.
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : lines width
        Defines line width
    - linetype : type of the line of tile's border
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> id = ["A", "B", "C", "D", "E", "F"]
    >>> val = np.random.uniform(3, 3.5, 6)
    >>> x = np.random.uniform(1, 3, 24)
    >>> y = np.random.uniform(0, 3, 24)
    >>> id3 = [v for v in id for _ in range(3)]
    >>> val3 = [v for v in val for _ in range(3)]
    >>> dat = dict(id=id3, val=val3, x=x, y=y)
    >>> ggplot(dat, aes('x', 'y')) + geom_polygon(aes(group='id'), alpha=0.5)
    """
    return _geom('polygon', mapping, data, stat, position, show_legend, sampling=sampling, **other_args)


def geom_map(mapping=None, data=None, stat=None, show_legend=None, sampling=None, **other_args):
    """
    Display polygons from a reference map.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary, pandas DataFrame or GeoDataFrame (supported shapes Polygon and MultiPolygon), optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    map : dictionary, pandas DataFrame or GeoDataFrame (supported shapes Polygon and MultiPolygon)
        Data containing region boundaries (coordinates of polygon vertices on map).
        It must contain columns:
         1. 'x' or 'lon' or 'long'
         2. 'y' or 'lat'
    map_join : str, pair, optional
        Pair of names used to join map coordinates with data.
        str or first value in pair - column in data
        second value in pair - column in map
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
        geom_map draws polygons which boundaries are specified by 'map' parameter.
        Aesthetics of ploygons (fill etc.) are computed basing on input data and mapping
        (see 'data' and 'mapping' arguments, 'map_id' aesthetic).

     geom_map understands the following aesthetics:
    - map_id : name used to join data with map coordinates (region boundaries).
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill  : color of a geometry internals
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : lines width
        Defines bar line width
    - linetype : type of the line of tile's border
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"

    Examples
    --------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> import lets_plot.geo_data as gd
    >>> boundaries = gd.regions_state(request=['Texas', 'Iowa', 'Arizona'], within='US-48').boundaries()
    >>> regions = np.unique(boundaries['name'])
    >>> num_of_regions = len(regions)
    >>> df = pd.DataFrame(regions, columns=['state'])
    >>> df['value'] = np.random.rand(num_of_regions)
    >>> ggplot(df) + ggtitle('Randomly colored states')\
             + geom_map(aes(map_id='state', fill='value'),map=boundaries, color='white')
    """

    mapKey = 'map'

    if mapKey in other_args.keys():
        map = other_args.get(mapKey)

        # map data: do not allow aes(..) here
        if isinstance(map, FeatureSpec):
            raise ValueError("Argument 'map' can't accept object '{}'".format(map.kind))

    elif not is_geo_data_frame(data):
        raise TypeError("geom_map() missing 1 required keyword-only argument: 'map'")

    return _geom('map', mapping, data, stat, None, show_legend, sampling=sampling, **other_args)


def geom_abline(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, slope=None,
                intercept=None, **other_args):
    """
    Add straight lines to a plot specified by slope and intercept.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    slope :
        The line slope.
    intercept:
        The value of y at the point where the line crosses the y axis.
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
     geom_abline understands the following aesthetics mappings:
    - slope : line slope
    - intercept : line y-intercept
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : lines width
        Defines bar line width
    - linetype : type of the line of tile's border
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"

    Examples
    --------
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> ggplot() + geom_abline(intercept=1, slope=3, color='red', linetype='dashed', size=3, alpha=0.5)
    """
    return _geom('abline', mapping, data, stat, position, show_legend, sampling=sampling, slope=slope,
                 intercept=intercept, **other_args)


def geom_hline(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, yintercept=None,
               **other_args):
    """
    Add straight horizontal lines to a plot

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    yintercept:
        The value of y at the point where the line crosses the y axis.
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
     geom_hline understands the following aesthetics mappings:
    - yintercept : line y-intercept
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : lines width
        Defines bar line width
    - linetype : type of the line of tile's border
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"

    Examples
    --------
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> ggplot() + geom_hline(yintercept=1, color='red', linetype='dashed', size=3, alpha=0.5)
    """
    return _geom('hline', mapping, data, stat, position, show_legend, sampling=sampling, yintercept=yintercept,
                 **other_args)


def geom_vline(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, xintercept=None,
               **other_args):
    """
    Add straight vertical lines to a plot

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    xintercept:
        The value of x at the point where the line crosses the x axis.
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
     geom_hline understands the following aesthetics mappings:
    - xintercept : line x-intercept
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : lines width
        Defines bar line width
    - linetype : type of the line of tile's border
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"

    Examples
    --------
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> ggplot() + geom_vline(xintercept=1, color='red', linetype='dashed', size=3, alpha=0.5)
    """
    return _geom('vline', mapping, data, stat, position, show_legend, sampling=sampling, xintercept=xintercept,
                 **other_args)


def geom_boxplot(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None,
                 fatten=None,
                 outlier_color=None, outlier_fill=None, outlier_shape=None, outlier_size=None,
                 varwidth=None,
                 **other_args):
    """

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged) should be used to define boxplot from your own computations via lower,
        upper, ymin, ymax, middle aesthetics mappings (see below), "count" (counts number of points with same x-axis
        coordinate), "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs
        smoothing - linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    fatten : number, default: 1.0
        A multiplicative factor applied to size of the middle bar
    outlier_color, outlier_fill, outlier_shape, outlier_size:
        Default aesthetics for outliers.
    varwidth:
        if FALSE (default) make a standard box plot.
        If TRUE, boxes are drawn with widths proportional to the square-roots of the number of
        observations in the groups.

    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
     geom_boxplot understands the following aesthetics mappings:
    - lower : lower hinge, 25% quantile
    - middle : median, 50% quantile
    - upper : upper hinge, 75% quantile
    - ymin : lower whisker = smallest observation greater than or equal to lower hinge - 1.5 * IQR
    - ymax : upper whisker = largest observation less than or equal to upper hinge + 1.5 * IQR
    - width : width of boxplot [0..1]
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines
    - fill : color of geometry filling
    - size : lines width
    - linetype : type of the line of border
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"

    Examples
    --------
    >>> from pandas import DataFrame
    >>> import numpy as np
    >>> from lets_plot import *
    >>>
    >>> np.random.seed(123)
    >>> data = DataFrame(dict(
    >>>     cond=np.repeat(['A','B'], 200),
    >>>     rating=np.concatenate((np.random.normal(0, 1, 200), np.random.normal(.8, 1, 200)))
    >>> ))
    >>> p = ggplot(data, aes(x='cond', y='rating')) + ggsize(300, 200)
    >>> p + geom_boxplot(outlier_color='red', outlier_shape=8, outlier_size=5)
    """
    return _geom('boxplot', mapping, data, stat, position, show_legend, sampling=sampling, fatten=fatten,
                 outlier_color=outlier_color, outlier_fill=outlier_fill, outlier_shape=outlier_shape,
                 outlier_size=outlier_size, varwidth=varwidth,
                 **other_args)


def geom_ribbon(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, **other_args):
    """
    Display a ribbon.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
        geom_ribbon draws a ribbon bounded by ymin and ymax.
     geom_ribbon understands the following aesthetics mappings:
    - x : x-axis coordinates.
    - ymin : y-axis coordinates of the lower bound.
    - ymax : y-axis coordinates of the upper bound.
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : lines width
        Defines line width
    - linetype : type of the line of tile's border
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"
    - fill : color of geometry filling

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> id = ["A", "A", "A", "B", "B", "B"]
    >>> x = [1, 2, 4, 1, 3, 4]
    >>> ymin = [-1, 0, 0, 3, 3, 4]
    >>> ymax = [0, 1, 1, 4, 5, 5]
    >>> dat = dict(id=id, x=x, ymin=ymin, ymax = ymax)
    >>> ggplot(dat, aes('x', 'y', group='id')) + geom_ribbon(aes(fill='id'), alpha=0.5)
    """
    return _geom('ribbon', mapping, data, stat, position, show_legend, sampling=sampling, **other_args)


def geom_area(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, **other_args):
    """
    Display an area.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
        geom_area draws an area bounded by the data and x axis.
     geom_area understands the following aesthetics mappings:
    - x : x-axis coordinates.
    - y : y-axis coordinates.
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : lines width
        Defines line width
    - linetype : type of the line of tile's border
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"
    - fill : color of geometry filling

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> x = [1,3,4,1,3,4]
    >>> y = [1,1,2,3,4,2]
    >>> g = [1,1,1,2,2,2]
    >>> dat = dict(x=x, y=y, g=g)
    >>> ggplot(dat,aes('x','y', group='g')) + geom_area(aes(fill='g', color='g'), alpha=.2)\
         + scale_fill_discrete() + scale_color_discrete()
    """
    return _geom('area', mapping, data, stat, position, show_legend, sampling=sampling, **other_args)


def geom_density(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, kernel=None,
                 adjust=None, bw=None, n=None,
                 **other_args):
    """
    Display density function.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    kernel : string, optional
        The kernel we use to calculate the density function. Choose among "gaussian", "cosine", "optcosine",
        "rectangular" (or "uniform"), "triangular", "biweight" (or "quartic"), "epanechikov" (or "parabolic")
    bw: string or double, optional
        The method (or exact value) of bandwidth. Either a string (choose among "nrd0" and "nrd"), or a double.
    adjust: double, optional
        Adjust the value of bandwidth my multiplying it. Changes how smooth the frequency curve is.
    n: int, optional
        The number of sampled points for plotting the function
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
        geom_density draws density function.
     geom_density understands the following aesthetics mappings:
    - x : x-axis coordinates.
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : lines width
        Defines line width
    - linetype : type of the line of tile's border
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"
    - fill : color of geometry filling

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> x = np.random.normal(0,1,1000)
    >>> dat = dict(x=x)
    >>> ggplot(dat,aes('x')) + geom_density()
    """
    return _geom('density', mapping, data, stat, position, show_legend, sampling=sampling,
                 kernel=kernel, adjust=adjust, bw=bw, n=n, **other_args)


def geom_density2d(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None,
                   kernel=None, adjust=None, bw=None, n=None,
                   bins=None, binwidth=None, **other_args):
    """
    Display density function contour.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    kernel : string, optional
        The kernel we use to calculate the density function. Choose among "gaussian", "cosine", "optcosine",
        "rectangular" (or "uniform"), "triangular", "biweight" (or "quartic"), "epanechikov" (or "parabolic")
    bw: string or double array, optional
        The method (or exact value) of bandwidth. Either a string (choose among "nrd0" and "nrd"), or a double array of length 2.
    adjust: double, optional
        Adjust the value of bandwidth my multiplying it. Changes how smooth the frequency curve is.
    n: int array, optional
        The number of sampled points for plotting the function (on x and y direction correspondingly)
    bins : int, optional
        Number of levels.
    binwidth: double, optional
        Distance between levels.
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
        geom_density2d draws density function.
     geom_density understands the following aesthetics mappings:
    - x : x-axis coordinates.
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : lines width
        Defines line width
    - linetype : type of the line of tile's border
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> x = np.random.normal(0,1,1000)
    >>> y = np.random.normal(0,1,1000)
    >>> dat = dict(x=x, y=y)
    >>> ggplot(dat,aes('x', 'y')) + geom_density2d()
    """
    return _geom('density2d', mapping, data, stat, position, show_legend, sampling=sampling,
                 kernel=kernel, adjust=adjust, bw=bw, n=n, bins=bins, binwidth=binwidth, **other_args)


def geom_density2df(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, kernel=None,
                    adjust=None, bw=None, n=None,
                    bins=None, binwidth=None, **other_args):
    """
    Fill density function contour.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    kernel : string, optional
        The kernel we use to calculate the density function. Choose among "gaussian", "cosine", "optcosine",
        "rectangular" (or "uniform"), "triangular", "biweight" (or "quartic"), "epanechikov" (or "parabolic")
    bw: string or double array, optional
        The method (or exact value) of bandwidth. Either a string (choose among "nrd0" and "nrd"), or a double array of length 2.
    adjust: double, optional
        Adjust the value of bandwidth my multiplying it. Changes how smooth the frequency curve is.
    n: int array, optional
        The number of sampled points for plotting the function (on x and y direction correspondingly)
    bins : int, optional
        Number of levels.
    binwidth: double, optional
        Distance between levels.
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
        geom_density2df fills density contours.
     geom_density understands the following aesthetics mappings:
    - x : x-axis coordinates.
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - fill : color of geometry filling

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> x = np.random.normal(0,1,1000)
    >>> y = np.random.normal(0,1,1000)
    >>> dat = dict(x=x, y=y)
    >>> ggplot(dat,aes('x', 'y')) + geom_density2df(aes(fill='..level..'))
    """
    return _geom('density2df', mapping, data, stat, position, show_legend, sampling=sampling,
                 kernel=kernel, adjust=adjust, bw=bw, n=n, bins=bins, binwidth=binwidth, **other_args)


def geom_jitter(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, width=None,
                height=None, **other_args):
    """
    Jittered Points, especially for discrete plots or dense plots.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    width : double, optional
        width for jitter, default=0.4
    height : double, optional
        height for jitter, default=0.4
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    The jitter geometry is used to create jittered points. The scatterplot is useful for displaying the relationship
    between two discrete variables.

    geom_jitter understands the following aesthetics mappings:
    - x : x-axis value
    - y : y-axis value
    - alpha : transparency level of a point
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color to paint shape's inner points
        Is applied only to the points of shapes having inner points.
    - shape : shape of the point
    - size : size of the point

    Examples
    --------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> x = np.random.randint(3, size=100)
    >>> y = np.random.normal(size=100)
    >>> dat = pd.DataFrame({'x': x, 'y': y})
    >>> p = ggplot(mapping=aes(x='x', y='y')) + geom_jitter(aes(color='x'), height=0)
    """
    return _geom('jitter', mapping, data, stat, position, show_legend, sampling=sampling, width=width, height=height,
                 **other_args)


def geom_freqpoly(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, **other_args):
    """
    Line chart which makes the y value proportional to the number of observed variable values, mapped to x axis.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    geom_freqpoly connects the top points in geom_bar

    geom_freqpoly understands the following aesthetics mappings:
    - x : x-axis value
    - y : y-axis value
    - alpha : transparency level of a point
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - linetype : type of the line
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"
    - size : line width

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from scipy.stats import multivariate_normal
    >>> N = 100
    >>> M = 3
    >>> mean = np.zeros(M)
    >>> mean = np.arange(M) * 5
    >>> cov = np.eye(M)
    >>> X = multivariate_normal.rvs(mean, cov, N)
    >>> X = X.astype(int) # comment this line to make variables continuous back
    >>> dat = pd.DataFrame(X)
    >>> dat = pd.melt(dat)
    >>> ggplot(dat, aes(x='value')) + geom_freqpoly(size=3)
    """
    return _geom('freqpoly', mapping, data, stat, position, show_legend, sampling=sampling, **other_args)


def geom_step(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, direction=None,
              **other_args):
    """
    Connects observations in the order, how they appear in the data.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    direction: string, optional
        "hv" or "HV" stands for horizontal then vertical (default); "vh" or "VH" stands for vertical then horizontal
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    geom_step draws steps between the observations in the order of X

    geom_step understands the following aesthetics mappings:
    - x : x-axis value
    - y : y-axis value
    - alpha : transparency level of a point
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - linetype : type of the line
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"
    - size : line width

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from lets_plot import *
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
    >>> ggplot(dat, aes(x='t', y='value', group='variable')) + geom_step(aes(color='variable', linetype='variable'), size=1, alpha=0.5)
    """
    return _geom('step', mapping, data, stat, position, show_legend, sampling=sampling, direction=direction,
                 **other_args)


def geom_rect(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, **other_args):
    """
    Draws rectangles

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary, pandas DataFrame or GeoDataFrame (supported shapes MultiPoint, Line, MultiLine, Polygon and MultiPolygon), optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    map : dictionary, pandas DataFrame or GeoDataFrame (shapes MultiPoint, Line, MultiLine, Polygon and MultiPolygon)
        Bounding boxes of geometries will be drawn.
        Data must contain columns:
         1. 'x' or 'lon' or 'long'
         2. 'y' or 'lat'
    map_join : str, pair, optional
        Pair of names used to join map coordinates with data.
        str or first value in pair - column in data
        second value in pair - column in map
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    geom_rect draws rectangles

    geom_rect understands the following aesthetics mappings:
    - map_id : name used to join data with map coordinates
    - xmin : x-axis value
    - xmax : x-axis value
    - ymin : y-axis value
    - ymax : y-axis value
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : lines width
        Defines line width
    - linetype : type of the line of tile's border
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"
    - fill : color of geometry filling

    Examples
    ---------
    >>> from lets_plot import *
    >>> ggplot() + geom_rect(aes(xmin=[3], xmax=[4], ymin=[6], ymax=[10]))

    """
    return _geom('rect', mapping, data, stat, position, show_legend, sampling=sampling, **other_args)


def geom_segment(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, arrow=None,
                 animation=None, **other_args):
    """
    Draws segments

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    arrow : optional
        Specification for arrow head, as created by arrow() function.
    animation : type of the animation, optional
        Codes and names: 0 = "none" (default), 1 = "dash", 2 = "plane", 3 = "circle".
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    geom_segment draws segments

    geom_segment understands the following aesthetics mappings:
    - x : x-axis value
    - y : y-axis value
    - xend : x-axis value
    - yend : y-axis value
    - alpha : transparency level of a point
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - linetype : type of the line
        Codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"
    - size : line width
    - speed : animation speed
        The number of pixels covered by animation object per second. Default value is 10.
    - flow : animation flow
        The number of animation objects passing a reference point per second. Default value is 0.1.

    Examples
    ---------
    >>> from lets_plot import *
    >>> ggplot() + geom_segment(aes(x=[3], y=[6], xend=[4], yend=[10]))
    """
    return _geom('segment', mapping, data, stat, position, show_legend, sampling=sampling, arrow=arrow,
                 animation=animation, **other_args)


def geom_text(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, **other_args):
    """
    Adds text directly to the plot.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    map : dictionary, pandas DataFrame or GeoDataFrame (supported shapes Point and MultiPoint)
        Data must contain columns:
         1. 'x' or 'lon' or 'long'
         2. 'y' or 'lat'
    map_join : str, pair, optional
        Pair of names used to join map coordinates with data.
        str or first value in pair - column in data
        second value in pair - column in map
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
    -----
    Adds text directly to the plot.

    geom_text understands the following aesthetics mappings:
    - map_id : name used to join data with map coordinates
    - x : x-axis value
    - y : y-axis value
    - label : text to add to plot
    - alpha : transparency level of a point
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : font size
    - family : ['sans' | 'serif' | 'mono' | any other like: "Times New Roman"]
        Font family. The default is 'sans'
    - fontface : ['plain' | 'bold' | 'italic' | 'bold italic']
        Font style and weight . The default is 'plain'
    - hjust : ['left', 'middle', 'right'] or number between 0 ('right') and 1 ('left')
        Horizontal text alignment
    - vjust : ['bottom', 'center', 'top'] or number between 0 ('bottom') and 1 ('top')
        Vertical text alignment
    - angle : Text rotation angle in degrees

    Examples
    ---------
    >>> from lets_plot import *
    >>> ggplot() + geom_text(aes(x=[1], y=[1], label=['Text'], angle=[30], family=['mono']), size = 10)
    """
    return _geom('text', mapping, data, stat, position, show_legend, sampling=sampling, **other_args)


def _geom(name, mapping=None, data=None, stat=None, position=None, show_legend=None, **kwargs):
    """
    :param mapping: Set of aesthetic mappings created by aes.
    :param data: The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    :param stat: The statistical transformation to use on the data for this layer, as a string.
    :param position: Position adjustment, either as a string, or the result of a call
        to a position adjustment function.
    ..
    :param kwargs:  passed on to layer. (These are often aesthetics, used to set an aesthetic to a fixed value,
        like color = "red" or size = 3. Also other cases - see doc)
                        They may also be parameters to the paired geom/stat

    todo: other parameters:
    na.rm = FALSE, show.legend = NA, inherit.aes = TRUE, ...
    """
    # aes(...)
    if mapping:
        if not (isinstance(mapping, FeatureSpec) and mapping.kind == 'mapping'):
            raise ValueError("Unexpected value for argument 'mapping'. Hint: try to use function aes()")

    data, mapping, data_meta = as_annotated_data(data, mapping)

    if is_geo_data_frame(data):
        data = geo_data_frame_to_lon_lat(data)

    if is_geo_data_frame(kwargs.get('map', None)):
        kwargs['map'] = geo_data_frame_to_lon_lat(kwargs['map'])

    map_data_meta = as_annotated_map_data(kwargs.get('map', None))

    map_join = kwargs.get('map_join', None)
    if map_join is not None:
        pair = as_pair(map_join)
        if pair is not None:
            kwargs['map_join'] = pair
        else:
            raise ValueError("Unexpected 'map_join' format. Should be str, [str] or [str, str]")

    return LayerSpec(geom=name, stat=stat, data=data, mapping=mapping, position=position, show_legend=show_legend,
                     **data_meta, **map_data_meta, **kwargs)
