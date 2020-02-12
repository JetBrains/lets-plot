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

<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/quickstart.png" alt="Couldn't load quickstart.png" width="505" height="260">
<br>
<a href="https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/quickstart.ipynb"> 
    <img src="https://raw.githubusercontent.com/jupyter/design/master/logos/Badges/nbviewer_badge.png" width="109" height="20" align="left">
</a>
<br>
<br>


### What is new in v.1.2.0

* [Shapely](https://pypi.org/project/Shapely/) and [GeoPandas](https://geopandas.org) support.

    GeoPandas `GeoDataFrame` is supported by the following geometry layers: `geom_polygon`, `geom_map`, `geom_point`, `geom_text`, `geom_rect`.

    Examples: 

    - Map building basics with *Lets-Plot* and *GeoPandas*: [geopandas_naturalearth.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geopandas_naturalearth.ipynb)

    - Inserting a map of Kotlin island: [geopandas_kotlin_isl.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geopandas_kotlin_isl.ipynb)

<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/kotlin_island.png" alt="Couldn't load kotlin_island.png" width="630" height="449"><br><br>

* Plot cropping using the `xlim, ylim` coordinate system parameters.

    See [geopandas_naturalearth.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geopandas_naturalearth.ipynb) 
    and [geopandas_kotlin_isl.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geopandas_kotlin_isl.ipynb).

* [LOESS](https://en.wikipedia.org/wiki/Local_regression) smoothing method in geom_smooth: 
[geom_smooth.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geom_smooth.ipynb)

* New geometry layers.

    `geom_crossbar, geom_linerange, geom_pointrange`: 
[error_bars.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/error_bars.ipynb)

    `geom_bin2d`:
[density_2d.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/density_2d)



### Example Notebooks

Try the following examples to study more features of the `Lets-Plot` library.

* Quickstart in Jupyter: [quickstart.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/quickstart.ipynb)

* Histogram, density plot, box plot and facets:
[distributions.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/distributions.ipynb) 

* Error-bars, points, lines, bars, dodge position:
[error_bars.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/error_bars.ipynb)
 
* Points, point shapes, linear regression, jitter position:
[scatter_plot.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/scatter_plot.ipynb)
 
* Points, density2d, polygons, density2df:
[density_2d.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/density_2d.ipynb)
 
* Tiles, contours, polygons, contourf:
[contours.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/contours.ipynb)
 
* Various presentation options:
[legend_and_axis.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/legend_and_axis.ipynb)
  
  
The following features of `Lets-Plot` are not present or have different implementation in other `Grammar of Graphics` libraries.

##### Plotting functions

* `ggsize()` - sets the size of the plot. Used in many examples starting from `quickstart`.
* `geom_density2df()` - fills space between equal density lines on a 2D density plot. Similar to `geom_density2d` but supports the `fill` aesthetic.

    Example: [density_2d.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/density_2d.ipynb) 

* `geom_contourf()` - fills space between the lines of equal level of the bivariate function. Similar to `geom_contour` but supports the `fill` aesthetic.

    Example: [contours.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/contours.ipynb) 

* `geom_image()` - displays an image specified by a ndarray with shape (n,m) or (n,m,3) or (n,m,4).

    Example: [image_101.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/image_101.ipynb)
    
    Example: [image_fisher_boat.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/image_fisher_boat.ipynb)
    
* `gg_image_matrix()` - a utility helping to combine several images into one graphical object.     

    Example: [image_matrix.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/image_matrix.ipynb)

##### GGBanch

GGBunch allows to show a collection of plots on one figure. Each plot in the collection can have arbitrary location and size. There is no automatic layout inside the bunch.

Examples: 
* [ggbunch.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/ggbunch.ipynb)
* [scatter_matrix.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/scatter_matrix.ipynb)


##### Data sampling 

Sampling is a special technique of data transformation, which helps dealing with large datasets and overplotting.

[Learn more](https://github.com/JetBrains/lets-plot/blob/master/docs/sampling.md) about sampling in Lets-Plot. 
  
  

### Artistic demos

A set of [interesting notebooks](https://github.com/denisvstepanov/lets-plot-examples/blob/master/README.md) with `Lets-Plot` for visualization.    
  

### Change Log

See [Lets-Plot at Github](https://github.com/JetBrains/lets-plot/blob/master/CHANGELOG.md).


### License

Code and documentation released under the [MIT license](https://github.com/JetBrains/lets-plot/blob/master/LICENSE).
Copyright 2019, JetBrains s.r.o.
    



