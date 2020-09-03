# Lets-Plot for Python [![official JetBrains project](http://jb.gg/badges/official-flat-square.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)

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
        <td>Linux, MacOS, Windows</td>
    </tr>
    <tr>
        <td>Python versions</td>
        <td>3.6, 3.7, 3.8</td>
    </tr>
</table>


- [Overview](#overview)
- [Installation](#inst)
- [Quick start with Jupyter](#start)
- [Examples](#examples)
    - [Quickstart and more](#quickstart)
    - [GeoPandas support](#geopandas)
    - [Interactive Maps](#livemap)
    - [Nonstandard plotting functions](#nonstandard)
    - [GGBanch](#ggbunch)
    - [Data sampling](#sampling)
    - [Cloud-based notebooks](#cloud_based)
    - [Interesting demos](#interesting)

- [SVG/HTML export to file](#export)
- [Offline mode](#offline)
- [Scientific mode in IntelliJ IDEA / PyCharm](#pycharm)
- [What is new in 1.5.0](#new)
- [Change log](#change_log)
- [License](#license)

<a id="overview"></a>
## Overview

The `Lets-Plot for Python` library includes a native backend and a Python API, which was mostly based on the [`ggplot2`](https://ggplot2.tidyverse.org/) package well-known to data scientists who use R.

R `ggplot2` has extensive documentation and a multitude of examples and therefore is an excellent resource for those who want to learn the grammar of graphics. 

Note that the Python API being very similar yet is different in detail from R. Although we have not implemented the entire ggplot2 API in our Python package, we have added a few [new features](#nonstandard) to our Python API.

You can try the Lets-Plot library in [Datalore](https://datalore.jetbrains.com). 
Lets-Plot is available in Datalore out-of-the-box (i.e. you can ignore the [Installation](#inst) chapter below). 

The advantage of [Datalore](https://datalore.jetbrains.com) as a learning tool in comparison to Jupyter is that it is equipped with very friendly Python editor which comes with auto-completion, intentions, and other useful coding assistance features.

Begin with the [quickstart in Datalore](https://view.datalore.io/notebook/Zzg9EVS6i16ELQo3arzWsP) notebook to learn more about Datalore notebooks. 

Watch the [Datalore Getting Started Tutorial](https://youtu.be/MjvFQxqNSe0) video for a quick introduction to Datalore.   


<a id="inst"></a>
## Installation

#### 1. For Linux and Mac users:
To install the Lets-Plot library, run the following command:
```shell script
pip install lets-plot
```
#### 2. For Windows users:
Install Anaconda3 (or Miniconda3), then install MinGW toolchain to Conda:
```shell script
conda install m2w64-toolchain
```
Install the Lets-Plot library:
```shell script
pip install lets-plot
```

<a id="start"></a>
## Quick start with Jupyter

To evaluate the plotting capabilities of Lets-Plot, add the following code to a Jupyter notebook:
```python
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
```

<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/quickstart.png" alt="Couldn't load quickstart.png" width="505" height="260">
<br>
<a href="https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/quickstart.ipynb"> 
    <img src="https://raw.githubusercontent.com/jupyter/design/master/logos/Badges/nbviewer_badge.png" width="109" height="20" align="left">
</a>
<span>&nbsp;&nbsp;</span>
<a href="https://view.datalore.io/notebook/Zzg9EVS6i16ELQo3arzWsP" title="View in Datalore"> 
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_datalore.svg" width="20" height="20">
</a>
<span>&nbsp;&nbsp;</span>
<a href="https://www.kaggle.com/alshan/lets-plot-quickstart" title="View at Kaggle"> 
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_kaggle.svg" width="20" height="20">
</a>
<span>&nbsp;&nbsp;</span>
<a href="https://colab.research.google.com/drive/1o9rFQbkGqvvixYLTogrzIjFPp1ti2cH-?usp=sharing" title="View at Colab"> 
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_colab.svg" width="20" height="20">
</a>
<br>
<br>

<a id="examples"></a>
## Example Notebooks

Try the following examples to study more features of the `Lets-Plot` library.

<a id="quickstart"></a>
### Quickstart and more

* Quickstart in Jupyter: [quickstart.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/quickstart.ipynb)

* Histogram, density plot, box plot and facets:
[distributions.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/distributions.ipynb) 

* Error-bars, crossbar, linerange, pointrange, points, lines, bars, dodge position:
[error_bars.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/error_bars.ipynb)
 
* Points, point shapes, linear regression, jitter position:
[scatter_plot.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/scatter_plot.ipynb)
 
* Smoothing: linear, [LOESS](https://en.wikipedia.org/wiki/Local_regression):
[geom_smooth.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geom_smooth.ipynb) 
 
* `as_discrete()` function:
[geom_smooth.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geom_smooth.ipynb) 
 
* Points, density2d, polygons, density2df, bin2d:
[density_2d.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/density_2d.ipynb)
 
* Tiles, contours, polygons, contourf:
[contours.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/contours.ipynb)
 
* Raster geom, Image geom:
[image_fisher_boat.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/image_fisher_boat.ipynb) 
 
* Various presentation options:
[legend_and_axis.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/legend_and_axis.ipynb)
  
  
<a id="geopandas"></a>
### GeoDataFrame support ([Shapely](https://pypi.org/project/Shapely/) and [GeoPandas](https://geopandas.org)). 
  
GeoPandas `GeoDataFrame` is supported by the following geometry layers: `geom_polygon`, `geom_map`, `geom_point`, `geom_text`, `geom_rect`.

* Map building basics with *Lets-Plot* and *GeoPandas*: 
[geopandas_naturalearth.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geopandas_naturalearth.ipynb)

* An **inset map** of Kotlin island: 
[geopandas_kotlin_isl.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geopandas_kotlin_isl.ipynb)

<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/kotlin_island.png" alt="Couldn't load kotlin_island.png" width="473" height="327"><br><br>

<a name="Interactive Maps" id="livemap"></a>
### Interactive Maps. 
  
The interactive map allows zooming in and out and panning around geospatial data that can be added to the base-map layer 
using regular ggplot geoms.

The basemap layer is created by the `geom livemap` geom which in addition can also work as scatter plot - similar to `geom_point`.  

[Learn more](https://github.com/JetBrains/lets-plot/blob/master/docs/interactive_maps.md) about interactive maps support in Lets-Plot. 

  
<a id="nonstandard"></a>
### Nonstandard plotting functions  
  
The following features of `Lets-Plot` are not available or have different implementation in other `Grammar of Graphics` libraries.

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

<a id="ggbunch"></a>
### GGBunch

GGBunch allows to show a collection of plots on one figure. Each plot in the collection can have arbitrary location and size. There is no automatic layout inside the bunch.

Examples:

* [ggbunch.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/ggbunch.ipynb)
* [scatter_matrix.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/scatter_matrix.ipynb)


<a id="sampling"></a>
### Data sampling 

Sampling is a special technique of data transformation, which helps dealing with large datasets and overplotting.

[Learn more](https://github.com/JetBrains/lets-plot/blob/master/docs/sampling.md) about sampling in Lets-Plot. 
  

<a id="cloud_based"></a>
### Cloud-based notebooks

Examples:

* [Datalore](https://view.datalore.io/notebook/Zzg9EVS6i16ELQo3arzWsP)
* [Kaggle](https://www.kaggle.com/alshan/lets-plot-quickstart)
* [Colab](https://colab.research.google.com/drive/1o9rFQbkGqvvixYLTogrzIjFPp1ti2cH-)
  

<a id="interesting"></a>
### Interesting demos

A set of [interesting notebooks](https://github.com/denisvstepanov/lets-plot-examples/blob/master/README.md) using `Lets-Plot` library for visualization.    
<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/klein_bottle.png" alt="Couldn't load klein_bottle.png" width="498" height="386">
<br>
  

<a id="export"></a>
## SVG/HTML export to file

`export_svg` function takes plot specification and filename as parameters and saves SVG representation of the plot to
 a file in the current working directory.
```python
from lets_plot import *
p = ggplot()...
               
# export SVG to file
from lets_plot.export.simple import export_svg

export_svg(p, "p.svg")
```
 
Note: The `simple.export_svg()` function do not save images of an `interactive map`.
 
`export_html` function takes plot specification and filename as parameters and saves dynamic HTML to a file in the current 
working directory.
When viewing this content the internet connection is required.

`export_html` has one more option - `iframe`. If `iframe=True` then `Lets-PLot` will wrap output HTML into `iframe`.

```python
from lets_plot import *
p = ggplot()...
               
# export HTML to file
from lets_plot.export.simple import export_html

export_html(p, "p.htm")
```
Example notebook: [export_SVG_HTML](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/export_SVG_HTML.ipynb)
 
  
<a id="offline"></a>
## Offline mode

In classic Jupyter notebook the `LetsPlot.setup_html()` statement by default pre-loads `Lets-Plot` JS library from CDN. 
Alternatively, option `offline=True` will force `Lets-Plot` adding the full Lets-Plot JS bundle to the notebook. 
In this case, plots in the notebook will be working without an Internet connection.
```python
from lets_plot import *

LetsPlot.setup_html(offline=True)
```
 
  
<a id="pycharm"></a>
## Scientific mode in IntelliJ IDEA / PyCharm

[![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/v/14379-lets-plot-in-sciview.svg)](http://plugins.jetbrains.com/plugin/14379-lets-plot-in-sciview)
[![JetBrains plugins](https://img.shields.io/jetbrains/plugin/d/14379-lets-plot-in-sciview.svg)](http://plugins.jetbrains.com/plugin/14379-lets-plot-in-sciview)

Plugin "Lets-Plot in SciView" is available at the JetBrains Plugin Repository.

The plugin adds support for interactive plots in IntelliJ-based IDEs with the enabled 
[Scientific mode](https://www.jetbrains.com/help/pycharm/matplotlib-support.html).

To learn more about the plugin check: [Lets-Plot in SciView plugin homepage](https://plugins.jetbrains.com/plugin/14379-lets-plot-in-sciview).

<div>
<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/pycharm_quickstart.png" alt="Couldn't load pycharm_quickstart.png" width="537" height="188"/>
<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/pycharm_logo.png" alt="Couldn't load pycharm_logo.png" width="50" height="50"/>
</div>

<a id="new"></a>
## What is new in 1.5.0

### Geocoding API

Geocoding is the process of converting names of places into geographic coordinates.

*Lets-Plot* now offers geocoding API covering the following administrative levels:
- country
- state
- county
- city

*Lets-Plot* geocoding API allows a user to execute a single and batch geocoding queries, and handle possible 
names ambiguity.

Relatively simple geocoding queries are executed using the `regions_xxx()` functions family. For example:
```python
from lets_plot.geo_data import *
regions_country(['usa', 'canada'])
```
returns the `Regions` object containing internal IDs for Canada and the US:
```
  request       id                found name
0     usa   297677  United States of America
1  canada  2856251                    Canada 
```
More complex geocoding queries can be created with the help of the `regions_builder()` function that
returns the `RegionsBuilder` object and allows chaining its various methods in order to specify 
how to handle geocoding ambiguities.

For example:
```python
regions_builder(request='warwick', level='city')  \
    .allow_ambiguous()  \
    .build()
```    
This sample returns the `Regions` object containing IDs of all cities matching "warwick":
```
    request        id                   found name
0   warwick    785807                      Warwick
1   warwick    363189                      Warwick
2   warwick    352173                      Warwick
3   warwick  15994531                      Warwick
4   warwick    368499                      Warwick
5   warwick    239553                      Warwick
6   warwick    352897                      Warwick
7   warwick   3679247                      Warwick
8   warwick   8144841                      Warwick
9   warwick    382429                 West Warwick
10  warwick   7042961             Warwick Township
11  warwick   6098747             Warwick Township
12  warwick  15994533  Sainte-Élizabeth-de-Warwick
``` 
```python
boston_us = regions(request='boston', within='us')
regions_builder(request='warwick', level='city') \
    .where('warwick', near=boston_us) \
    .build()
```    
This example returns the `Regions` object containing the ID of one particular "warwick" near Boston (US):
```
   request      id found name
0  warwick  785807    Warwick
```
Once the `Regions` object is available, it can be passed to any *Lets-Plot* geom 
supporting the `map` parameter.

If necessary, the `Regions` object can be transformed into a regular pandas `DataFrame` using `to_data_frame()` method
or to a geopandas `GeoDataFrame` using one of `centroids()`, `boundaries()`, or `limits()` methods.

All coordinates are in the EPSG:4326 coordinate reference system (CRS). 

Note what executing geocoding queries requires an internet connection.

Examples:

<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/map_titanic.png" alt="Couldn't load map_titanic.png" width="547" height="197">
<br>
<a href="https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/map_titanic.ipynb"> 
    <img src="https://raw.githubusercontent.com/jupyter/design/master/logos/Badges/nbviewer_badge.png" width="109" height="20" align="left">
</a>
<span>&nbsp;&nbsp;</span>
<a href="https://view.datalore.io/notebook/1h4h0HMctRKJLY64PBe63a" title="View in Datalore"> 
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_datalore.svg" width="20" height="20">
</a>
<span>&nbsp;&nbsp;</span>
<a href="https://www.kaggle.com/alshan/lets-plot-titanic" title="View at Kaggle"> 
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_kaggle.svg" width="20" height="20">
</a>

 

<a id="change_log"></a>
## Change Log

See [Lets-Plot at Github](https://github.com/JetBrains/lets-plot/blob/master/CHANGELOG.md).


<a id="license"></a>
## License

Code and documentation released under the [MIT license](https://github.com/JetBrains/lets-plot/blob/master/LICENSE).
Copyright © 2019-2020, JetBrains s.r.o.
    



