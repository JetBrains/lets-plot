# Datalore Plot

<table>
    <tr>
        <td>Latest Release</td>
        <td>
            <a href="https://pypi.org/project/datalore-plot/"/>
            <img src="https://badge.fury.io/py/datalore-plot.svg"/>
        </td>
    </tr>
    <tr>
        <td>License</td>
        <td>
            <a href="https://opensource.org/licenses/MIT"/>
            <img src="https://img.shields.io/badge/License-MIT-yellow.svg"/>
        </td>
    </tr>
</table>


## Quickstart

```shell script
pip install datalore-plot`
```

```python
import numpy as np
from datalore_plot import *

np.random.seed(12)
data = dict(
    cond=np.repeat(['A','B'], 200),
    rating=np.concatenate((np.random.normal(0, 1, 200), np.random.normal(1, 1.5, 200)))
)

ggplot(data, aes(x='rating', fill='cond')) + ggsize(500, 250) \
+ geom_density(color='dark_green', alpha=.7) + scale_fill_brewer(type='seq') \
+ theme(axis_line_y='blank')
````

![](docs/examples/images/quickstart.png =505x256)