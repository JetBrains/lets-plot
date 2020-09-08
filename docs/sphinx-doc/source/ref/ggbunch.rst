GGBunch()
---------

    Creates a new object - a collection of plots created with the `ggplot()` function.
    Use `add_plot()` method to add plot to the `bunch`. Set arbitrary location and size for plots inside the grid.
    To display the final `bunch` object, use `show()` method.

.. py:function:: add_plot()

    Add a plot to the bunch. Create the `ggplot()` object, specify plot parameters and add the resulting object to the grid.

.. py:function:: bunch.add_plot(self, plot_spec: PlotSpec, x, y, width=None, height=None)

    :argument plot_spec: (`ggplot()` object): Plot specifications set with `ggplot()` function.
    :argument x: (number) x-coordinate of plot origin in px.
    :argument y: (number) y-coordinate of plot origin in px.
    :argument width: (number) Width of plot in px.
    :argument height: (number) Height of plot in px.

.. py:function:: show()

    Display plots within the `bunch` object in a grid.

.. py:function:: bunch.show()

Examples
=========

.. jupyter-execute::

    import numpy as np
    from lets_plot import *

    LetsPlot.setup_html()

    cov=[[1, 0],
         [0, 1]]
    x, y = np.random.multivariate_normal(mean=[0,0], cov=cov, size=400).T

    data = dict(
        x = x,
        y = y
    )

    p = ggplot(data) + ggsize(600,200)

    scatter = p + geom_point(aes('x', 'y'), color='black', alpha=.4)
    scatter

.. jupyter-execute::

    histogram = p + geom_histogram(aes('x', y = '..count..'), fill='rgb(131,161,189)')
    histogram

