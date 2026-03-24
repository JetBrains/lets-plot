# Lets-Plot

[![official JetBrains project](http://jb.gg/badges/official-flat-square.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![License MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://raw.githubusercontent.com/JetBrains/lets-plot-kotlin/master/LICENSE)
[![Latest Release](https://img.shields.io/github/v/release/JetBrains/lets-plot)](https://github.com/JetBrains/lets-plot/releases/latest)


**Lets-Plot** is a multiplatform plotting library built on the principles of the Grammar of Graphics. 

The library design is heavily influenced by Leland Wilkinson's work [The Grammar of Graphics](https://www.goodreads.com/book/show/2549408.The_Grammar_of_Graphics) describing the deep features that underlie all statistical graphics.

> This grammar [...] is made up of a set of independent components that can be composed in many different ways. This makes [it] very powerful because you are not limited to a set of pre-specified graphics, but you can create new graphics that are precisely tailored for your problem.
> - Hadley Wickham, "[ggplot2: Elegant Graphics for Data Analysis](https://ggplot2-book.org/index.html)"


## Grammar of Graphics for Python [![Latest Release](https://badge.fury.io/py/lets-plot.svg)](https://pypi.org/project/lets-plot)

A bridge between R (ggplot2) and Python data visualization. \
To learn more, see the documentation site at **[lets-plot.org/python](https://lets-plot.org/python)**.          


## Grammar of Graphics for Kotlin [![Latest Release](https://img.shields.io/github/v/release/JetBrains/lets-plot-kotlin)](https://github.com/JetBrains/lets-plot-kotlin/releases/latest)

### Notebooks
Create plots in [Kotlin Notebook](https://plugins.jetbrains.com/plugin/16340-kotlin-notebook),
[Datalore](https://datalore.jetbrains.com/report/static/HZqq77cegYd.E7get_WnChZ/aTA9lQnPkRwdCzT6uy95GZ), [Jupyter with Kotlin Kernel](https://github.com/Kotlin/kotlin-jupyter#readme) \
or any other notebook that supports `Kotlin Kernel`. \
To learn more, see the **[Lets-Plot Kotlin API](https://github.com/JetBrains/lets-plot-kotlin)** project at GitHub.

### Compose Multiplatform
Embed Lets-Plot charts in [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) applications. \
To learn more, see the **[Lets-Plot Compose Frontend](https://github.com/JetBrains/lets-plot-compose)** project at GitHub.

### JVM and Kotlin/JS
Embed Lets-Plot charts in JVM (Swing, JavaFX) and Kotlin/JS applications. <br>
To learn more, see the **[Lets-Plot Kotlin API](https://github.com/JetBrains/lets-plot-kotlin)** project at GitHub.
        
### Documentation

Kotlin API documentation site: [lets-plot.org/kotlin](https://lets-plot.org/kotlin).

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

## What is new in 4.9.0

- #### Statistical Summaries Directly on `geom_smooth()` Plot Layer

  The `geom_smooth()` layer now includes a `labels` parameter designed to display statistical summaries of the fitted model directly on the plot. \
  This parameter accepts a `smooth_labels()` object, which provides access to model-specific variables like $R^2$ and the regression equation.
  
  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-26a/images/smooth_summary.png" alt="f-26a/images/smooth_summary.png" width="400" height="265">

  See [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/smooth_summary.html).
                         
- #### Plot Tags
  Plot tags are short labels attached to a plot.

  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-26a/images/plot_tags.png" alt="f-26a/images/plot_tags.png" width="600" height="185">

  See [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/plot_tags.html).

- #### New `geom_bracket()` and `geom_bracket_dodge()` Geometries
  New geometries designed primarily for significance bars (*p-values*) annotations in categorical plots.

  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-26a/images/geom_bracket.png" alt="f-26a/images/geom_bracket.png" width="400" height="261">

  See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/geom_bracket.html).

- #### Custom Color Palettes in `geom_imshow()`
  The `cmap` parameter now allows you to specify a list of hex color codes for visualizing grayscale images. \
  Also, the new `cguide` parameter lets you customize the colorbar for grayscale images.

  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-26a/images/image_custom_cmap.png" alt="f-26a/images/image_custom_cmap.png" width="400" height="248">

  See [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/image_custom_cmap.html).

- #### New `palette()` Method in Color Scales
  Generates a list of hex color codes that can be used with `scale_color_manual()` to maintain consistent colors across multiple plots.

  See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/scale_color_palette.html).

- #### New `overflow` parameter in `scale_color_brewer()`, `scale_fill_brewer()`
  Controls how colors are generated when more colors are needed than the palette provides. \
  Options: `'interpolate'` (`'i'`), `'cycle'` (`'c'`), `'generate'` (`'g'`).

  See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/scale_brewer_overflow.html).

- #### New `break_width` Parameter in Positional Scales
  Specifies a fixed distance between axis breaks.

  See examples:
  - [datetime scale](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/scale_break_width_datetime.html)
  - [time (duration) scale](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/scale_break_width_duration.html)
  - [log10 scale](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/scale_break_width_log10.html)

- #### Axis Minor Ticks Customization
  The `axis_minor_ticks` and `axis_minor_ticks_length` parameters in `theme()`.

  See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/axis_minor_ticks.html).

- #### Pan/Zoom in `gggrid()` with Shared Axes
  Pan/Zoom now propagates across subplots with shared axes (`sharex`/`sharey`).

  See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/gggrid_scale_share_zoom.html).


- #### And More

  See [CHANGELOG.md](https://github.com/JetBrains/lets-plot/blob/master/CHANGELOG.md) for a full list of changes.


## Recent Updates in the [Gallery](https://lets-plot.org/python/pages/gallery.html)

  <a href="https://lets-plot.org/examples/demo/cities_density.html">
    <img src="https://github.com/JetBrains/lets-plot-docs/blob/bcc63703214b5b02a8a374668d8bba7a451a9152/source/_static/images/changelog/4.8.0/square-cities_density.png?raw=true" alt="images/changelog/4.8.0/square-cities_density.png" width="128" height="128">
  </a>
  <a href="https://lets-plot.org/examples/demo/raincloud.html">
    <img src="https://github.com/JetBrains/lets-plot-docs/blob/41d87786905efdd5995f66e6a2734255548f00dc/source/_static/images/changelog/4.7.0/square-raincloud.png?raw=true" alt="images/changelog/4.7.0/square-raincloud.png" width="128" height="128">
  </a>
  <a href="https://lets-plot.org/examples/demo/europe_capitals.html">
    <img src="https://github.com/JetBrains/lets-plot-docs/blob/41d87786905efdd5995f66e6a2734255548f00dc/source/_static/images/changelog/4.7.0/square-europe_capitals.png?raw=true" alt="images/changelog/4.7.0/square-europe_capitals.png" width="128" height="128">
  </a>
  <a href="https://lets-plot.org/examples/demo/trading_chart.html">
    <img src="https://github.com/JetBrains/lets-plot-docs/blob/41d87786905efdd5995f66e6a2734255548f00dc/source/_static/images/changelog/4.7.0/square-trading_chart.png?raw=true" alt="images/changelog/4.7.0/square-trading_chart.png" width="128" height="128">
  </a>
  <a href="https://lets-plot.org/examples/demo/magnifier_inset.html">
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-25a/images/magnifier_inset.png" alt="f-25a/images/magnifier_inset.png" width="128" height="128">
  </a>
  <a href="https://lets-plot.org/examples/demo/ggbunch_indonesia.html">
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-25a/images/ggbunch_indonesia.png" alt="f-25a/images/ggbunch_indonesia.png" width="128" height="128">
  </a>
  <a href="https://lets-plot.org/examples/demo/lets_plot_in_2024.html">
    <img src="https://github.com/JetBrains/lets-plot-docs/blob/41d87786905efdd5995f66e6a2734255548f00dc/source/_static/images/changelog/4.7.0/square-lets_plot_in_2024.png?raw=true" alt="images/changelog/4.7.0/square-lets_plot_in_2024.png" width="128" height="128">
  </a>
  <a href="https://lets-plot.org/examples/demo/plot_layout_scheme.html">
    <img src="https://github.com/JetBrains/lets-plot-docs/blob/41d87786905efdd5995f66e6a2734255548f00dc/source/_static/images/changelog/4.7.0/square-plot_layout_scheme.png?raw=true" alt="images/changelog/4.7.0/square-plot_layout_scheme.png" width="128" height="128">
  </a>
  <a href="https://lets-plot.org/examples/demo/theme_legend_scheme.html">
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-24g/images/theme_legend_scheme.png" alt="f-24g/images/theme_legend_scheme.png" width="128" height="128">
  </a>


## Change Log

[CHANGELOG.md](https://github.com/JetBrains/lets-plot/blob/master/CHANGELOG.md)


## Code of Conduct

This project and the corresponding community are governed by the
[JetBrains Open Source and Community Code of Conduct](https://confluence.jetbrains.com/display/ALL/JetBrains+Open+Source+and+Community+Code+of+Conduct).
Please make sure you read it.


## License

Code and documentation released under the [MIT license](https://github.com/JetBrains/lets-plot/blob/master/LICENSE).
Copyright © 2019-2025, JetBrains s.r.o.
