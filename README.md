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

To learn more: [lets-plot.org](https://lets-plot.org).          


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

## What is new in 4.0.0

The major version was bumped to 4 due to a major package refactoring that the project has undergone.\
This refactoring doesn't affect the Python API, however, as a result of package names changed,\
Lets-Plot v4.0.0 is partially incompatible with Lets-Plot Kotlin API versions 4.4.1 and earlier.
    
### A Number of Geometry Defaults Changed

  - The default qualitative color palette is now [Color Brewer "Set1"](https://colorbrewer2.org/#type=qualitative&scheme=Set1&n=9) (was ["Set2"](https://colorbrewer2.org/#type=qualitative&scheme=Set2&n=8)).
  - Slightly bigger default size of points and width of lines.
  - Flavor-aware default colors for points, lines etc.
  <br>
  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-23c/images/geom_defaults.png" alt="f-23c/images/geom_defaults.png" width="504" height="150">
  <br>
  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-23c/images/flavor_geom_colors.png" alt="f-23c/images/flavor_geom_colors.png" width="1024" height="120">

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_theme_colors.ipynb).

### Support for Variadic Line Width and/or Color in `geom_line()` and `geom_path()`
  <br>
  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-23c/images/variadic_width.png" alt="f-23c/images/variadic_width.png" width="455" height="150">

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/aes_size_color_variadic_lines.ipynb).
       
### Parameter `"size_unit"` in `geom_pie()`
  A way to specify size of the pie in units relative to the plot size.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_pie_size_unit.ipynb).

### Stroke and Spacers in `geom_pie()`
  <br>
  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-23c/images/pie_stroke.png" alt="f-23c/images/pie_stroke.png" width="162" height="150">

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_pie_stroke_and_spacers.ipynb).


### New `theme_void()`, Geometries and Statistics

- `theme_void()`: [example](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/theme_void.ipynb).
- `geom_function()`: [example](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_function.ipynb).
- `stat_ecdf()`: [example](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/stat_ecdf.ipynb).
- `stat_summary()`: [example](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/stat_summary.ipynb).
- `stat_summary_bin()`: [example](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/stat_summary_bin.ipynb).
- `"sum"` statistic: [example](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/stat_sum.ipynb).
- `"boxplot_outlier"` statistic: [example](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/stat_boxplot_outlier.ipynb).


## Change Log

See [CHANGELOG.md](https://github.com/JetBrains/lets-plot/blob/master/CHANGELOG.md) for other changes and fixes.


## Code of Conduct

This project and the corresponding community are governed by the
[JetBrains Open Source and Community Code of Conduct](https://confluence.jetbrains.com/display/ALL/JetBrains+Open+Source+and+Community+Code+of+Conduct).
Please make sure you read it.


## License

Code and documentation released under the [MIT license](https://github.com/JetBrains/lets-plot/blob/master/LICENSE).
Copyright © 2019-2023, JetBrains s.r.o.
