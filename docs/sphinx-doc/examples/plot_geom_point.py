# -*- coding: utf-8 -*-
"""
Geometry geom_point()
=====================

An example with the geom_point() geometry.
"""

# sphinx_gallery_thumbnail_path = '_static/gallery/thumbnails/plot_geom_point.png'

import numpy as np

from lets_plot import *; LetsPlot.setup_html()

#%%

N = 100
np.random.seed(42)
x = np.random.uniform(-5, 5, size=N)
y = x**2 + np.random.normal(size=N)
data = dict(x=x, y=y)

#%%

ggplot(data, aes(x='x', y='y')) + geom_point()