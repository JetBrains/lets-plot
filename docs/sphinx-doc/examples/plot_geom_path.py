# -*- coding: utf-8 -*-
"""
Geometry geom_path()
=======================

An example with the geom_path() geometry.
"""

# sphinx_gallery_thumbnail_path = '_static/gallery/thumbnails/plot_geom_path.png'

import numpy as np

from lets_plot import *; LetsPlot.setup_html()

#%%

N = 100
a, l = .9, 1
phi = np.linspace(0, 2 * np.pi, N)
rho = l - a * np.sin(phi)
x = rho * np.cos(phi)
y = rho * np.sin(phi)
data = dict(x=x, y=y)

#%%

ggplot(data, aes(x='x', y='y')) + \
    geom_path(size=1, color='red') + \
    coord_fixed()