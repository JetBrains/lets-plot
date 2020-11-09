# -*- coding: utf-8 -*-
"""
Geometry geom_boxplot()
=======================

An example with the geom_boxplot() geometry.
"""

# sphinx_gallery_thumbnail_path = '_static/gallery/thumbnails/plot_geom_boxplot.png'

import numpy as np

from lets_plot import *; LetsPlot.setup_html()

#%%

N, M = 100, 5
np.random.seed(42)
y = np.random.normal(size=N)
c = np.random.randint(M, size=N)
data = dict(y=y, c=c)

#%%

ggplot(data, aes(x='c', y='y')) + \
    geom_boxplot(aes(fill='c', color='c'), alpha=.5) + \
    scale_color_discrete() + scale_fill_discrete()