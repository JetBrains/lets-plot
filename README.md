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


## What is new in 2.4.0

- ### Python versions

  Added Python 3.10 wheels as well as new Apple Silicon wheel for Python 3.9.

- ### New Plot Types

  - #### Quantile-Quantile (Q-Q) plot.
    - geometries:
      - `geom_qq()`
      - `geom_qq_line()`
      - `geom_qq2()`
      - `geom_qq2_line()`
    - quick Q-Q : the `qq_plot()` function in the `bistro` module.

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/qq_plots.ipynb).

  - #### Marginal plots.
  
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-22b/images/marginal_layers.png" alt="f-22b/images/marginal_layers.png" width="360" height="276">

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/marginal_layers.ipynb).

- ### Plot Theme

  - `face` parameter in `element_text()`.

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/set_font_faces.ipynb).

  - `panel_border` parameter in `theme()`.

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/panel_border.ipynb).

  - New options for configuring tooltip appearance.
  
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-22b/images/tooltip_theme.png" alt="f-22b/images/tooltip_theme.png" width="150" height="114">

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/tooltips_theme.ipynb).
 
- ### Color Scales

  `scale_color_gradientn()` and `scale_fill_gradientn()` functions.
  
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/scale_%28color_fill%29_gradientn.ipynb).



## Change Log

See [CHANGELOG.md](https://github.com/JetBrains/lets-plot/blob/master/CHANGELOG.md) for other changes and fixes.


## License

Code and documentation released under the [MIT license](https://github.com/JetBrains/lets-plot/blob/master/LICENSE).
Copyright © 2019-2022, JetBrains s.r.o.