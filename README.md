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

*Lets-Plot for Kotlin* adds plotting capabilities to scientific notebooks built on the Jupyter [Kotlin Kermel](https://github.com/Kotlin/kotlin-jupyter).

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


## What is new in 2.3.0

- ### Geometries

  - `geom_violin()`

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/geom_violin.ipynb).

  - `geom_dotplot()`

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/geom_dotplot.ipynb).

  - `geom_ydotplot()`

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/geom_ydotplot.ipynb).

- ### Labels and Legends

  - Plot **subtitle** and **caption** are now supported.
  
    You can use parameter `subtitle`in `ggtitle()` and `labs()` to add a subtitle below the plot' title, and 
    parameter `caption` in `labs()` to add a caption below plot.
  
  - Multi-line labels.

    The 'newline' character (`\n`) now works as `line break` in plot title, subtitle and caption, in legend's title and in tooltips.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/title_subtitle_caption.ipynb).

- ### Tooltips

<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-22a/images/tooltip.png" alt="f-22a/images/tooltip.png" width="362" height="310">

  - Improved appearance
  - Automatic word wrap makes long text values look better
  - Tooltip title 
  
    You can use new method `title()` in the [Tooltip castomization API](https://lets-plot.org/pages/tooltips.html) to add a title to tooltip.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/tooltip_title.ipynb).
                                         
- ### Maps

  Our interactive map widget now supports automatic size adjustment for markers on map (i.e. the radius of points and the width of lines) when zooming.
  You can control this behavior using new parameters `data_size_zoomin, const_size_zoomin` in `geom_livemap()`.

  Also note new "reset" tool-button.                        

<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-22a/images/map_airports_zoomin.gif" alt="f-22a/images/map_airports_zoomin.gif">


- ### Facets

  "Free" scales are now supported on faceted plots. 

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/facets_free_scales.ipynb).


## Change Log

See [CHANGELOG.md](https://github.com/JetBrains/lets-plot/blob/master/CHANGELOG.md) for other changes and fixes.


## License

Code and documentation released under the [MIT license](https://github.com/JetBrains/lets-plot/blob/master/LICENSE).
Copyright © 2019-2022, JetBrains s.r.o.