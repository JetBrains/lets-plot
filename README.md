# Datalore Plot

## Quickstart

`pip install datalore-plot`

```python
import numpy as np
from datalore_plot import *

np.random.seed(123)
X = np.concatenate((np.random.normal(0, 0.5, 100), np.random.normal(5, 1.5, 100)))
Y = np.concatenate((np.random.normal(4, 1.5, 100), np.random.normal(6, 2.0, 100)))
data = dict(x=X,y=Y)

ggplot(data, aes('x','y')) \
+ geom_point(color='black', alpha=0.5, size=10) \
+ geom_density2d(aes(color='..level..'),size=3) \
+ scale_color_hue()
````
