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

## What is new in 3.1.0

- ### `gggrid()`
  <br>
  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-23a/images/plot_grid.png" alt="f-23a/images/plot_grid.png" width="400" height="200">

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/plot_grid.ipynb).


- ### `joint_plot()`
  <br>
  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-23a/images/joint_plot.png" alt="f-23a/images/joint_plot.png" width="400" height="267">

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/joint_plot.ipynb).


- ### Configuring Axis Position
  <br>
  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-23a/images/axis_position.png" alt="f-23a/images/axis_position.png" width="300" height="200">

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/axis_position.ipynb).

              
- ### Showing Quantiles on Density Plots
  <br>
  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-23a/images/density_quantiles.png" alt="f-23a/images/density_quantiles.png" width="400" height="150">

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/quantile_parameters.ipynb).

              
- ### Additional "color" aesthetics: `paint_a, paint_b, paint_c`.
  <br>
  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-23a/images/additional_color_aes.png" alt="f-23a/images/additional_color_aes.png" width="400" height="300">

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/multiple_color_scales.ipynb).
  
  Also added a set of related "color scale" functions with the "aesthetic" parameter for configuring of additional color scales.

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/scale_functions.ipynb) demo.


- ### Other New Features and Improvements 

  - Export to PNG file in `ggsave()` (requires the [CairoSVG](https://pypi.org/project/CairoSVG) library), see [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/export_to_png.ipynb).
  - `angle` parameter in `element_text()` in `theme()`, see [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/axis_text_angle.ipynb).  
  - `geodesic` parameter in `geom_segment()` and `geom_path()`, see [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/param_geodesic.ipynb).  


## Change Log

See [CHANGELOG.md](https://github.com/JetBrains/lets-plot/blob/master/CHANGELOG.md) for other changes and fixes.


## License

Code and documentation released under the [MIT license](https://github.com/JetBrains/lets-plot/blob/master/LICENSE).
Copyright © 2019-2023, JetBrains s.r.o.
