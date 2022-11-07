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

## What is new in 2.5.1

Mostly a maintenance release.

Nevertheless, few new features and improvements were added as well, among them:
  - New rendering options in `geom_text(), geom_label()`
  - `geom_imshow()` is now supporting `cmap` and `extent` parameters (also, `norm, vmin` and `vmax` were fixed)

You will find more details about fixes and improvements in the [CHANGELOG.md](https://github.com/JetBrains/lets-plot/blob/master/CHANGELOG.md#251---2022-11-03).

## What is new in 2.5.0

- ### Plot Theme

  - #### `theme_bw()`

    See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/theme_bw.ipynb).
                         
  - #### Theme Flavors
    
    Theme flavor offers an easy way to change the colors of all elements in a theme to match a specific color scheme.

    In this release, we have added the following flavors: 
    - _darcula_
    - _solarized_light_
    - _solarized_dark_
    - _high_contrast_light_
    - _high_contrast_dark_
               
  <br>
  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-22c/images/theme_flavors.png" alt="f-22c/images/theme_flavors.png" width="1000" height="133">

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/theme_flavors.ipynb).

  - #### New parameters in `element_text()`
    - `size, family`
      ([example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/font_size_and_family.ipynb))
    - `hjust, vjust` for plot title, subtitle, caption, legend and axis titles
      ([example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/hjust_vjust.ipynb))
    - `margin` for plot title, subtitle, caption, axis titles and tick labels
      ([example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/text_margins.ipynb))

- ### New Plot Types

    `geom_label()`.

    See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/geom_label.ipynb).

- ### Color Scales

    Viridis color scales: `scale_color_viridis()`, `scale_fill_viridis()`.

    Supported colormaps:
    - _magma_
    - _inferno_
    - _plasma_
    - _viridis_
    - _cividis_
    - _turbo_
    - _twilight_    

  <br>
  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-22c/images/viridis_plasma.png" alt="f-22c/images/viridis_plasma.png" width="439" height="132">

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/colors_viridis.ipynb).


## Change Log

See [CHANGELOG.md](https://github.com/JetBrains/lets-plot/blob/master/CHANGELOG.md) for other changes and fixes.


## License

Code and documentation released under the [MIT license](https://github.com/JetBrains/lets-plot/blob/master/LICENSE).
Copyright © 2019-2022, JetBrains s.r.o.