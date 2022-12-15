# Lets-Plot  [![official JetBrains project](http://jb.gg/badges/official-flat-square.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)

<a href="https://raw.githubusercontent.com/JetBrains/lets-plot/master/LICENSE">
  <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="Couldn't load MIT license svg"/>
</a>

##

**Lets-Plot** is an open-source plotting library for statistical data. 

The design of Lets-Plot library is heavily influenced by Leland Wilkinson work [The Grammar of Graphics](https://www.goodreads.com/book/show/2549408.The_Grammar_of_Graphics) describing the deep features that underlie all statistical graphics.

> This grammar [...] is made up of a set of independent components that can be composed in many different ways. This makes [it] very powerful because you are not limited to a set of pre-specified graphics, but you can create new graphics that are precisely tailored for your problem.
> - Hadley Wickham, "[ggplot2: Elegant Graphics for Data Analysis](https://ggplot2-book.org/index.html)"

We provide ggplot2-like plotting API for Python and Kotlin users. 


## Lets-Plot for Python

<a href="https://pypi.org/project/lets-plot/">
  <img src="https://badge.fury.io/py/lets-plot.svg"/>
</a>

A bridge between R (ggplot2) and Python data visualization.

Learn more about *Lets-Plot for Python* installation and usage at the documentation website: https://lets-plot.org.          


## Lets-Plot for Kotlin

<a href="https://github.com/JetBrains/lets-plot-kotlin/releases/latest">
  <img src="https://img.shields.io/github/v/release/JetBrains/lets-plot-kotlin"/>
</a>

*Lets-Plot for Kotlin* adds plotting capabilities to scientific notebooks built on the Jupyter [Kotlin Kernel](https://github.com/Kotlin/kotlin-jupyter).

You can use this API to embed charts into Kotlin/JVM and Kotlin/JS applications as well.

*Lets-Plot for Kotlin* at GitHub: https://github.com/JetBrains/lets-plot-kotlin.

## "Lets-Plot in SciView" plugin

[![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/v/14379-lets-plot-in-sciview.svg)](http://plugins.jetbrains.com/plugin/14379-lets-plot-in-sciview)
[![JetBrains plugins](https://img.shields.io/jetbrains/plugin/d/14379-lets-plot-in-sciview.svg)](http://plugins.jetbrains.com/plugin/14379-lets-plot-in-sciview)

*Scientific mode* in PyCharm and in IntelliJ IDEA provides support for interactive scientific computing and data visualization.

[*Lets-Plot in SciView*](https://plugins.jetbrains.com/plugin/14379-lets-plot-in-sciview) plugin adds 
support for interactive plotting to IntelliJ-based IDEs with the *Scientific mode* enabled.
 
>
> **Note:** The *Scientific mode* is NOT available in communinty editions of JetBrains IDEs. 
>

Also read:

- [Scientific mode in PyCharm](https://www.jetbrains.com/help/pycharm/matplotlib-support.html)
- [Scientific mode in IntelliJ IDEA](https://www.jetbrains.com/help/idea/matplotlib-support.html)

## What is new in 3.0.0

- ### Breaking Changes

  - `Python 3.6` is no longer supported as it is in the ["end-of-life"](https://devguide.python.org/versions/) release cycle stage.
  
  - `geom_livemap()` is now a pure basemap layer. The following options are no longer supported:
    `symbol`, `data`, `mapping`, `map`, `map_join`, `ontop`, `stat`, `position`, `show_legend`, `sampling`, `tooltips`, `geodesic`.
 
>    To draw **point** and **pie** markers on map, please, use the `geom_point()` and `geom_pie()` geometry layers.
>
>    See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/titanic.ipynb).

>    In place of the former `geodetic` parameter in `geom_livemap` please use the new parameter `flat` in **path** and **segment**
>    geometry layers. 
>
>    See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/param_flat.ipynb).

- ### New Features

  - #### `residual_plot()`
    <br>
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-22e/images/residual-light.png" alt="f-22e/images/residual-light.png" width="200" height="133">
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-22e/images/residual-dark.png" alt="f-22e/images/residual-dark.png" width="200" height="133">

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/residual_plot.ipynb).

  - #### `geom_area_ridges()`
    <br>
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-22e/images/ridges-dark.png" alt="f-22e/images/ridges-dark.png" width="400" height="130">

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/ridgeline_plot.ipynb).
      
  - #### `geom_pie()`
    <br>
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-22e/images/pie.png" alt="f-22e/images/pie.png" width="379" height="106">

    See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/geom_pie.ipynb).

  - #### Annotation Labels on Pie-Chart
    <br>
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-22e/images/pie-labels-explode.png" alt="f-22e/images/pie-labels-explode.png" width="195" height="133">
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-22e/images/pie-labels-titanic.png" alt="f-22e/images/pie-labels-titanic.png" width="366" height="133">

    See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/annotations_for_pie.ipynb).

  - #### Spatial Pies
    <br>
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-22e/images/spatial_pies_titanic.png" alt="f-22e/images/spatial_pies_titanic.png" width="293" height="133">

    See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/titanic.ipynb).

  - #### New Parameters in `geom_imshow()`:
    <br>
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-22e/images/imshow-alpha-jp.png" alt="f-22e/images/imshow-alpha-jp.png" width="180" height="172">

    - Transparency of `NaN` values in grayscale images: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/image_nan_values.ipynb).

    - `alpha` parameter: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/image_alpha_param.ipynb).


## Change Log

See [CHANGELOG.md](https://github.com/JetBrains/lets-plot/blob/master/CHANGELOG.md) for other changes and fixes.


## License

Code and documentation released under the [MIT license](https://github.com/JetBrains/lets-plot/blob/master/LICENSE).
Copyright © 2019-2022, JetBrains s.r.o.
