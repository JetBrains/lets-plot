geom_raster()
--------------

    Rectangles with x, y values mapped to center.
    Much faster than geom_tile but doesn't support width/height and color.

.. py:function:: geom_raster(mapping=None, data=None, stat=None, position=None, show_legend=None, **other_args)

    :argument mapping: set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    :argument data: optional.
    :type data: dictionary or pandas DataFrame
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    :argument stat: optional.
    :type stat: string
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    :argument position: optional.
    :type position: string
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    :argument other_args:
        Other arguments passed on to the layer. These are often aesthetics settings used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.

    :return: **geom object specification**

.. note::

    Understands the following aesthetics mappings:

     - `x` : x-axis coordinates of the center of rectangles.
     - `y` : y-axis coordinates of the center of rectangles.
     - `alpha` : transparency level of a layer
     - `fill` : color of geometry filling


Examples
=========
.. jupyter-execute::

    import numpy as np
    from lets_plot import *

    LetsPlot.setup_html()

    delta = 0.5
    x = np.arange(-5.0, 5.0, delta)
    y = np.arange(-5.0, 5.0, delta)

    X, Y = np.meshgrid(x, y)
    Z = np.random.normal(0, 1, X.shape)

    x = X.reshape(-1)
    y = Y.reshape(-1)
    z = Z.reshape(-1)

    dat = dict(x=x, y=y, z=z)

    ggplot(dat, aes('x', 'y')) + geom_raster(aes(fill='z')) + geom_contour(aes(z='z'))