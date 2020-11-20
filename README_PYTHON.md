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
- [Example Notebooks](#examples)
- [GGBunch](#ggbunch)
- [Data Sampling](#sampling)
- [Export to File](#export)
- [Cloud Notebooks](#cloud_based)
- [Geospatial](#geospatial)
    - [GeoPandas Support](#geopandas)
    - [Interactive Maps](#livemap)
    - [Geocoding API](#geocoding)
- [Interesting Demos](#interesting)
- [Offline Mode](#offline)
- [Scientific Mode in IntelliJ IDEA / PyCharm](#pycharm)
- [What is new in 1.5.3](#new)
- [Change Log](#change_log)
- [License](#license)

<a id="overview"></a>
### Overview

The `Lets-Plot for Python` library includes a native backend and a Python API, which was mostly based on the [`ggplot2`](https://ggplot2.tidyverse.org/) package well-known to data scientists who use R.

R `ggplot2` has extensive documentation and a multitude of examples and therefore is an excellent resource for those who want to learn the grammar of graphics. 

Note that the Python API being very similar yet is different in detail from R. Although we have not implemented the entire ggplot2 API in our Python package, we have added a few [new features](#nonstandard) to our Python API.

You can try the Lets-Plot library in [Datalore](https://datalore.jetbrains.com). 
Lets-Plot is available in Datalore out-of-the-box (i.e. you can ignore the [Installation](#inst) chapter below). 

The advantage of [Datalore](https://datalore.jetbrains.com) as a learning tool in comparison to Jupyter is that it is equipped with very friendly Python editor which comes with auto-completion, intentions, and other useful coding assistance features.

Begin with the [quickstart in Datalore](https://view.datalore.io/notebook/Zzg9EVS6i16ELQo3arzWsP) notebook to learn more about Datalore notebooks. 

Watch the [Datalore Getting Started Tutorial](https://youtu.be/MjvFQxqNSe0) video for a quick introduction to Datalore.   


<a id="inst"></a>
### Installation

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
### Quick start with Jupyter

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
<a href="https://colab.research.google.com/drive/1uYYZcG0g0kP4lJdPkpWB8aBS96ioDii2?usp=sharing" title="View at Colab"> 
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_colab.svg" width="20" height="20">
</a>
<br>
<br>

<a id="examples"></a>
### Example Notebooks

See [Example Notebooks](https://github.com/JetBrains/lets-plot/blob/master/docs/examples.md).

  
<a id="ggbunch"></a>
### GGBunch
GGBunch allows to show a collection of plots on one figure. Each plot in the collection can have arbitrary location and size. There is no automatic layout inside the bunch.

* [ggbunch.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/ggbunch.ipynb) 
* [geom_smooth.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geom_smooth.ipynb)
* [scatter_matrix.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/scatter_matrix.ipynb) 

  
<a id="sampling"></a>
### Data Sampling 

Sampling is a special technique of data transformation, which helps dealing with large datasets and overplotting.

Learn more: [Data Sampling](https://github.com/JetBrains/lets-plot/blob/master/docs/sampling.md). 
  

<a id="export"></a>
### Export to File

The `ggsave()` function is an easy way to export plot to a file in SVG or HTML formats.
 
Note: The `ggsave()` function currently do not save images of interactive maps to SVG.
 
Example notebook: [export_SVG_HTML](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/export_SVG_HTML.ipynb)
  

<a id="cloud_based"></a>
### Cloud Notebooks

Examples:

* [Datalore](https://view.datalore.io/notebook/Zzg9EVS6i16ELQo3arzWsP)
* [Kaggle](https://www.kaggle.com/alshan/lets-plot-quickstart)
* [Colab](https://colab.research.google.com/drive/1o9rFQbkGqvvixYLTogrzIjFPp1ti2cH-)
  
<a id="geospatial"></a>
### Geospatial  
  
<a id="geopandas"></a>
#### GeoPandas Support 
  
GeoPandas `GeoDataFrame` is supported by the following geometry layers: `geom_polygon`, `geom_map`, `geom_point`, `geom_text`, `geom_rect`.

Learn more: [GeoPandas Support](https://github.com/JetBrains/lets-plot/blob/master/docs/geopandas.md).
 
<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/kotlin_island.png" alt="Couldn't load kotlin_island.png" width="473" height="327"><br><br>

<a id="livemap"></a>
#### Interactive Maps 
Interactive maps allow zooming and panning around geospatial data that can be added to the base-map layer 
using regular ggplot geoms.

Learn more: [Interactive Maps](https://github.com/JetBrains/lets-plot/blob/master/docs/interactive_maps.md). 

<a id="geocoding"></a>
#### Geocoding API
Geocoding is the process of converting names of places into geographic coordinates.  

Learn more: [Geocoding API](https://github.com/JetBrains/lets-plot/blob/master/docs/geocoding.md). 

<a id="interesting"></a>
### Interesting Demos

A set of [interesting notebooks](https://github.com/denisvstepanov/lets-plot-examples/blob/master/README.md) using `Lets-Plot` library for visualization.    
<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/klein_bottle.png" alt="Couldn't load klein_bottle.png" width="498" height="386">
<br>
 
  
<a id="offline"></a>
### Offline Mode

In classic Jupyter notebook the `LetsPlot.setup_html()` statement by default pre-loads `Lets-Plot` JS library from CDN. 
Alternatively, option `offline=True` will force `Lets-Plot` adding the full Lets-Plot JS bundle to the notebook. 
In this case, plots in the notebook will be working without an Internet connection.
```python
from lets_plot import *

LetsPlot.setup_html(offline=True)
```
 
  
<a id="pycharm"></a>
### Scientific mode in IntelliJ IDEA / PyCharm

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
### What is new in 1.5.3

- Tooltip Customization

    New API for customization of tooltip contents and its position (see [Tooltip Customization](https://github.com/JetBrains/lets-plot/blob/master/docs/tooltips.md)).

- Attribution when Configuring 3-rd Party Map-tiles

    New arguments in the `maptiles_zxy()` function allowing configuring attributions
when using 3-rd party map-tiles as a base-map layer.

    See [The Gallery of Base-maps](https://www.kaggle.com/alshan/the-gallery-of-basemaps).     
 
- Formatting labels in `geom_text()`

    New parameter, 'label_format' to define a formatting pattern.

    See demo: [label_format.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/label_format.ipynb)   
 
- Export to File

    The `ggsave()` function is an easy way to export plot to a file in SVG or HTML formats.
 
    Note: The `ggsave()` function currently do not save images of interactive maps to SVG.
 
    Example: [export_SVG_HTML](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/export_SVG_HTML.ipynb)

- Fixed 'HUE' Scale and Other Fixes

    See [CHANGELOG.md](https://github.com/JetBrains/lets-plot/blob/master/CHANGELOG.md) for details.

<a id="change_log"></a>
### Change Log

See [CHANGELOG.md](https://github.com/JetBrains/lets-plot/blob/master/CHANGELOG.md).


<a id="license"></a>
### License

Code and documentation released under the [MIT license](https://github.com/JetBrains/lets-plot/blob/master/LICENSE).
Copyright © 2019-2020, JetBrains s.r.o.
    



