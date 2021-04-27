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
        <td>3.6, 3.7, 3.8, 3.9</td>
    </tr>
</table>


- [Overview](#overview)
- [Installation](#installation)
- [Quick start with Jupyter](#quick-start-with-jupyter)
- [Example Notebooks](#example-notebooks)
- [GGBunch](#ggbunch)
- [Data Sampling](#data-sampling)
- [Export to File](#export-to-file)
- [Formatting](#formatting)
- [The 'bistro' Package](#the-bistro-package)
    - [Correlation Plot](#correlation-plot)
    - [Image Matrix](#image-matrix)
- [Geospatial](#geospatial)
    - [GeoPandas Support](#geopandas-support)
    - [Interactive Maps](#interactive-maps)
    - [Geocoding](#geocoding)
- ['No Javascript' Mode](#no-javascript-mode)
- [Offline Mode](#offline-mode)
- [Interesting Demos](#interesting-demos)
- [Scientific Mode in IntelliJ IDEA / PyCharm](#scientific-mode-in-intellij-idea-pycharm)
- [What is new in 2.0.0](#what-is-new-in-2-0-0)
- [Change Log](#change-log)
- [License](#license)

<a id="overview"></a>
### Overview

The `Lets-Plot for Python` library includes a native backend and a Python API, which was mostly based on the [ggplot2](https://ggplot2.tidyverse.org) package well-known to data scientists who use R.

R ggplot2 has extensive documentation and a multitude of examples and therefore is an excellent resource for those who want to learn the grammar of graphics. 

Note that the Python API being very similar yet is different in detail from R. Although we have not implemented the entire ggplot2 API in our Python package, we have added a few new features to our Python API.

You can try the Lets-Plot library in [Datalore](https://datalore.jetbrains.com). 
Lets-Plot is available in Datalore out-of-the-box (i.e. you can ignore the [Installation](#installation) chapter below). 

The advantage of [Datalore](https://datalore.jetbrains.com) as a learning tool in comparison to Jupyter is that it is equipped with very friendly Python editor which comes with auto-completion, intentions, and other useful coding assistance features.

Begin with the [quickstart in Datalore](https://view.datalore.io/notebook/Zzg9EVS6i16ELQo3arzWsP) notebook to learn more about Datalore notebooks. 

Watch the [Datalore Getting Started Tutorial](https://youtu.be/MjvFQxqNSe0) video for a quick introduction to Datalore.   


<a id="installation"></a>
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

<a id="quick-start-with-jupyter"></a>
### Quick start with Jupyter

You can use Lets-Plot in Jupyter notebook or other notebook of your choice, like Datalore, Kaggle or Colab.
 
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
<span>&nbsp;&nbsp;</span>
<a href="https://deepnote.com/project/673ea421-638e-469d-8d04-5cc4c6e0258f#%2Fnotebook.ipynb" title="View at Deepnote"> 
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_deepnote.svg" width="20" height="20">
</a>
<br>
<br>

<a id="example-notebooks"></a>
### Example Notebooks

See [Example Notebooks](https://jetbrains.github.io/lets-plot-docs/pages/features/examples.html).

  
<a id="ggbunch"></a>
### GGBunch
GGBunch allows to show a collection of plots on one figure. Each plot in the collection can have arbitrary location and size. There is no automatic layout inside the bunch.

* [ggbunch.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/ggbunch.ipynb) 
* [geom_smooth.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geom_smooth.ipynb)
* [scatter_matrix.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/scatter_matrix.ipynb) 

  
<a id="data-sampling"></a>
### Data Sampling

Sampling is a special technique of data transformation, which helps dealing with large datasets and overplotting.

Learn more: [Data Sampling](https://jetbrains.github.io/lets-plot-docs/pages/features/sampling.html). 
  

<a id="export-to-file"></a>
### Export to File

The `ggsave()` function is an easy way to export plot to a file in SVG or HTML formats.
 
Note: The `ggsave()` function currently do not save images of interactive maps to SVG.
 
Example notebook: [export_SVG_HTML](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/export_SVG_HTML.ipynb)

<a id="formatting"></a>
### Formatting

Lets-Plot supports formatting of values of numeric and date-time types.

Complementary to the value formatting, a *string template* is also supported.

For example:  
```
value: 67719.94988293362
+
string template: "Mean income: £{.2s}"
=
the formatting result: "Mean income: £67k"
```
An empty placeholder {} is also allowed. In this case a default string representation will be shown. This is also applicable to categorical values.   

To learn more about format strings see: [Formatting](https://jetbrains.github.io/lets-plot-docs/pages/features/formats.html).

In Lets-Plot you can use formatting for:
- tooltip text, see: [Tooltip Customization](https://jetbrains.github.io/lets-plot-docs/pages/features/tooltips.html).
- labels on X/Y axis. See: [Formatting demo](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/formatting_axes_etc.ipynb).
- the `geom_text()` labels. See: [Label format demo](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/label_format.ipynb).
- facetting values in `facet_grid()`, `facet_wrap()` functions. See: [Facets demo](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/facets.ipynb).


<a id="the-bistro-package"></a>
### The 'bistro' Package

The 'bistro' package is a collection of higher level API functions, each allows 
to create a certain kind of plot with a single function call instead of combining a plethora of plot features manually.

<a id="correlation-plot"></a>
#### Correlation Plot

`from lets_plot.bistro.corr`

The `corr_plot()` function creates a fluent builder object offering a set of methods for 
configuring of beautiful correlation plots. A call to the terminal `build()` method in the end 
will create a resulting plot object.    

Example: [correlation_plot.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/correlation_plot.ipynb)
  
<a id="image-matrix"></a>
#### Image Matrix

`from lets_plot.bistro.im`

The `image_matrix()` function arranges a set of images in a grid.
 
Example: [image_matrix.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/image_matrix.ipynb)

The `image_matrix()` function uses `geom_image` under the hood, so you might want to check out these demos as well:
* [image_101.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/image_101.ipynb)
* [image_fisher_boat.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/image_fisher_boat.ipynb) 
 
<a id="geospatial"></a>
### Geospatial
  
<a id="geopandas-support"></a>
#### GeoPandas Support
  
GeoPandas `GeoDataFrame` is supported by the following geometry layers: `geom_polygon`, `geom_map`, `geom_point`, `geom_text`, `geom_rect`.

Learn more: [GeoPandas Support](https://jetbrains.github.io/lets-plot-docs/pages/features/geopandas_support.html).

<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/kotlin_island.png" alt="Couldn't load kotlin_island.png" width="473" height="327">
<br><br>

<a id="interactive-maps"></a>
#### Interactive Maps
Interactive maps allow zooming and panning around geospatial data that can be added to the base-map layer 
using regular ggplot geoms.

Learn more: [Interactive Maps](https://jetbrains.github.io/lets-plot-docs/pages/features/interactive_maps.html). 

<a id="geocoding"></a>
#### Geocoding
Geocoding is the process of converting names of places into geographic coordinates.  

The Lets-Plot has built-in geocoding capabilities covering the folloing administrative levels:
- countries
- states (US and non-US equivalents)
- counties (and equivalents)
- cities (and towns)

Learn more: [Geocoding](https://jetbrains.github.io/lets-plot-docs/pages/features/geocoding_api.html). 

<a id="no-javascript-mode"></a>
### 'No Javascript' Mode

In the 'no javascript' mode Lets-Plot genetares plots as bare-bones SVG images.

This mode is halpfull when there is a requirement to render notebooks in an 'ipnb' renderer which does not suppopt javascript 
(like GitHub's built-in renderer).    

Activate 'no javascript' mode using the `LetsPlot.setup_html()` method call:
```python
from lets_plot import *

LetsPlot.setup_html(no_js=True)
```

Alternativaly, you can set up the environment variable:
```
LETS_PLOT_NO_JS = true   (other accepted values are: 1, t, y, yes)
``` 

Note: interactive maps do not support the 'no javascript' mode.

<a id="offline-mode"></a>
### Offline Mode

In classic Jupyter notebook the `LetsPlot.setup_html()` statement by default pre-loads `Lets-Plot` JS library from CDN. 
Alternatively, option `offline=True` will force `Lets-Plot` adding the full Lets-Plot JS bundle to the notebook. 
In this case, plots in the notebook will be working without an Internet connection.
```python
from lets_plot import *

LetsPlot.setup_html(offline=True)
```
 
Note: internet connection is still required for interactive maps and geocoding API.
  
<a id="interesting-demos"></a>
### Interesting Demos

A set of [interesting notebooks](https://github.com/denisvstepanov/lets-plot-examples/blob/master/README.md) using `Lets-Plot` library for visualization.

<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/klein_bottle.png" alt="Couldn't load klein_bottle.png" width="498" height="386">
<br>
 
<a id="scientific-mode-in-intellij-idea-pycharm"></a>
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

<a id="what-is-new-in-2-0-0"></a>
### What is new in 2.0.0
     
- Python 3.9 support
- Faceted plots:
  - new `facet_wrap()` function.
  - ordering of faceting values.
  - formatting of faceting values.

  See: [Facets demo](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/facets.ipynb)


- new `format` parameter on scales: formatting tick labels on X/Y axis.

  Example:
    ```python
    scale_x_datetime(format="%b %Y")
    scale_x_continuous(format='is {.2f}')
    ```
  Demo: [Formatting demo](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/formatting_axes_etc.ipynb)

  See also: [Formatting](https://jetbrains.github.io/lets-plot-docs/pages/features/formats.html)


- Tooltips:
  - new `color` option: overrides the default tooltip color:

    ```python
    geom_xxx(tooltips=layer_tooltips().color('red'))
    ```

    Learn more: [Tooltip Customization](https://jetbrains.github.io/lets-plot-docs/pages/features/tooltips.html).
  - *crosshair* cursor when tooltip is in a fixed position specified by the `anchor` option.


- Brand new Geocoding API.

  Note: This is a **breaking change!** Hence we bumped the Lets-Plot version to 2.0.0. 

  In the Lets-Plot v2.0.0 the peviouse Geocoding API is no longer working.

  The old version of geocoding backend remains on-line for a couple of release cycles
  to continue support of prior Lets-Plot versions.

  To learn more about new Geocoding API see: [Geocoding](https://jetbrains.github.io/lets-plot-docs/pages/features/geocoding_api.html).


See [CHANGELOG.md](https://github.com/JetBrains/lets-plot/blob/master/CHANGELOG.md) for other changes and fixes.

<a id="change-log"></a>
### Change Log

See [CHANGELOG.md](https://github.com/JetBrains/lets-plot/blob/master/CHANGELOG.md)


<a id="license"></a>
### License

Code and documentation released under the [MIT license](https://github.com/JetBrains/lets-plot/blob/master/LICENSE).
Copyright © 2019-2021, JetBrains s.r.o.
    



