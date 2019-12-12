# Lets-Plot for Python

<table>
    <tr>
        <td>Latest Release</td>
        <td>
            <a href="https://pypi.org/project/lets-plot/"/>
            <img src="https://badge.fury.io/py/lets-plot.svg"/>
        </td>
    </tr>
    <tr>
        <td>License</td>
        <td>
            <a href="https://opensource.org/licenses/MIT"/>
            <img src="https://img.shields.io/badge/License-MIT-yellow.svg"/>
        </td>
    </tr>
    <tr>
        <td>OS</td>
        <td>Linux, MacOS</td>
    </tr>
    <tr>
        <td>Python versions</td>
        <td>3.7, 3.8</td>
    </tr>
</table>

### Implementation Overview

The Lets-Plot python extension includes native backend and a Python API, which was mostly based on the [`ggplot2`](https://ggplot2.tidyverse.org/) package well-known to data scientists who use R.

R `ggplot2` has extensive documentation and a multitude of examples and therefore is an excellent resource for those who want to learn the grammar of graphics. 

Note that the Python API being very similar yet is different in detail from R. Although we have not implemented the entire ggplot2 API in our Python package, we have added a few [new features](https://github.com/JetBrains/lets-plot/blob/master/README_PYTHON.md#new_to_experienced_users) to our Python API.

You can try the Lets-Plot library in [Datalore](https://blog.jetbrains.com/blog/2018/10/17/datalore-1-0-intelligent-web-application-for-data-analysis/). Lets-Plot is available in Datalore out-of-the-box and is almost identical to the one we ship as PyPI package. This is because Lets-Plot is an offshoot of the Datalore project from which it was extracted to a separate plotting library.

One important difference is that the python package in Datalore is named **datalore.plot** and the package you install from PyPI has name **lets_plot**.

The advantage of [Datalore](https://blog.jetbrains.com/blog/2018/10/17/datalore-1-0-intelligent-web-application-for-data-analysis/) as a learning tool in comparison to Jupyter is that it is equipped with very friendly Python editor which comes with auto-completion, intentions, and other useful coding assistance features.


### Installation

To install the Lets-Plot library, run the following command:
```shell script
pip install lets-plot
```

### Quickstart in Jupyter

To evaluate the plotting capabilities of Lets-Plot, add the following code to a Jupyter notebook:
```python
import numpy as np
from lets_plot import *

np.random.seed(12)
data = dict(
    cond=np.repeat(['A','B'], 200),
    rating=np.concatenate((np.random.normal(0, 1, 200), np.random.normal(1, 1.5, 200)))
)

ggplot(data, aes(x='rating', fill='cond')) + ggsize(500, 250) \
+ geom_density(color='dark_green', alpha=.7) + scale_fill_brewer(type='seq') \
+ theme(axis_line_y='blank')
```

<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/quickstart.png" alt="Couldn't load quickstart.png" width="505" height="260"/>


### Examples

Try the following examples to study more features of the Lets-Plot library.

Quickstart in Jupyter: [quickstart.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/quickstart.ipynb)

Histogram, density plot, box plot and facets:
[distributions.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/distributions.ipynb) 

Error-bars, points, lines, bars, dodge position:
[error_bars.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/error_bars.ipynb)
 
Points, point shapes, linear regression, jitter position:
[scatter_plot.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/scatter_plot.ipynb)
 
Points, density2d, polygons, density2df:
[density_2d.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/density_2d.ipynb)
 
Tiles, contours, polygons, contourf:
[contours.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/contours.ipynb)
 
Various presentation options:
[legend_and_axis.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/legend_and_axis.ipynb)
 
 
[![Binder](https://mybinder.org/badge_logo.svg)](https://mybinder.org/v2/gh/JetBrains/lets-plot/9622ee1410133dc4513fb3a27c72057932e99a74?filepath=docs%2Fexamples%2Fjupyter-notebooks)
 
### New to experienced users

These features of `Lets-Plot` are not present or have different implementation in other `Grammar of Graphics` libraries.

#### Plotting functions

* `ggsize()` - sets size of the plot. Used in many examples starting from `quickstart`
* `geom_density2df()` - fills space between equal density lines on 2D density plot. Similar to `geom_density2d` but supports `fill` aesthetic.

    Example: [density_2d.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/density_2d.ipynb) 

* `geom_contourf()` - fills space between lines of equal level of bivariate function. Similar to `geom_contour` but supports `fill` aesthetic.

    Example: [contours.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/contours.ipynb) 

* `geom_image()` - shows image encoded as numpy 3-dimensional ndarray.

    Example: [TBD]
    
* `gg_image_matrix()` - a utility helping to combine several images into one graphical object.     

    Example: [TBD]

#### GGBanch

GGBunch allows to show a collection of plots on one figure. Each plot in the collection can have arbitrary location and size. Where is no automatic layot inside the bunch.

Examples: 
* [ggbunch.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks-dev/ggbunch.ipynb)
* [scatter_matrix.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks-dev/scatter_matrix.ipynb)


#### Data sampling 

Sampling is a special technique of data transformation, which helps dealing with large datasets and overplotting.

[Learn more](https://github.com/JetBrains/lets-plot/blob/master/docs/sampling.md) about sampling in Lets-Plot. 


### Change Log

See [Lets-Plot at Github](https://github.com/JetBrains/lets-plot/blob/master/CHANGELOG.md).


### License

Code and documentation released under the [MIT license](https://github.com/JetBrains/lets-plot/blob/master/LICENSE).
Copyright 2019, JetBrains s.r.o.
    



