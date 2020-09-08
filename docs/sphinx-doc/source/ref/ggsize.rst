ggsize()
---------

    Specifies overall size of plot

.. py:function:: ggsize(width, height)

    :argument width: (number): Width of plot in px.
    :argument height: (number):  Height of plot in px.


Examples
==========

.. jupyter-execute::

    import numpy as np
    import pandas as pd
    from lets_plot import *

    LetsPlot.setup_html()

    x = np.arange(100)
    y = np.random.normal(size=100)
    dat = pd.DataFrame({'x':x, 'y':y})
    p = ggplot(dat) + geom_line(aes('x','y')) + ggsize(600, 120)
    p
