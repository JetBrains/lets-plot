geom_point()
-------------

    Points, as for a scatter plot.

.. py:function:: geom_point(mapping=None, data=None, stat=None, position=None, show_legend=None, sampling=None, animation=None, map_join=None, **other_args)

    :argument mapping: set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    :argument data: dictionary, pandas DataFrame or GeoDataFrame (supported shapes Point and MultiPoint), optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    :type data: dictionary
    :argument stat: optional. The statistical transformation to use on the data for this layer.
    :type stat: string
    :argument position: optional. Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    :type position: string
    :argument animation: type of the animation, optional.
         Codes and names: 0 = "none" (default), 1 = "ripple".
    :argument map: pandas DataFrame or GeoDataFrame (supported shapes Point and MultiPoint)
        Data containing coordinates of points.
        Can be used with aesthetic parameter 'map_id' for joining data and map coordinates.
        Dictionary and DataFrame object must contain keys/columns:
         1. 'x' or 'lon' or 'long'
         2. 'y' or 'lat'
    :type map: dictionary
    :argument map_join: optional
        Pair of names used to join map coordinates with data.
        str or first value in pair - column in data
        second value in pair - column in map
    :type map_join: str or pair
    :argument other_args:
        Other arguments passed on to the layer. These are often aesthetics settings used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.


    :return: **geom object specification**

.. note::

    The point geometry is used to create scatterplots. The scatterplot is useful for displaying the relationship
    between two continuous variables, although it can also be used with one continuous and one categorical variable,
    or two categorical variables.

    geom_point understands the following aesthetics mappings:

     - `map_id` : name used to join data with map coordinates
     - `x` : x-axis value
     - `y` : y-axis value
     - `alpha` : transparency level of the point. Understands numbers between 0 and 1.
     - `color` (colour) : color of the geometry. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
     - `fill` : color to paint shape's inner points. Is applied only to the points of shapes having inner points.
     - `shape` : shape of the point
     - `size` : size of the point


Examples
=========
.. jupyter-execute::

    import numpy as np
    import pandas as pd
    from lets_plot import *

    LetsPlot.setup_html()

    x = np.random.uniform(-1, 1, size=100)
    y = np.random.normal(size=100)

    dat = pd.DataFrame({'x': x, 'y': 25 * x ** 2 + y})
    dat['class'] = ['0' if dat['x'][i] < 0 else '1' for i in range(100)]

    p = ggplot(dat) + geom_point(aes(x='x', y='y', color='y', shape='class', fill='x', size='y'))
    p += geom_point(shape=21, color='red', fill='green', size=5, stat='smooth')
    p


