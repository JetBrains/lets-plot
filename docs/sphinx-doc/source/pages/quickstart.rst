.. _quickstart:


Quickstart with Jupyter
#######################

To evaluate the plotting capabilities of Lets-Plot, add the following code to a Jupyter notebook:

.. jupyter-execute::

    import numpy as np
    from lets_plot import *
    LetsPlot.setup_html()

    np.random.seed(12)
    data = dict(
        cond=np.repeat(['A','B'], 200),
        rating=np.concatenate((np.random.normal(0, 1, 200), np.random.normal(1, 1.5, 200)))
    )

    ggplot(data, aes(x='rating', fill='cond')) + ggsize(500, 250) \
    + geom_density(color='dark_green', alpha=.7) + scale_fill_brewer(type='seq') \
    + theme(axis_line_y='blank')