# Lets-Plot

[![official JetBrains project](http://jb.gg/badges/official-flat-square.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![License MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://raw.githubusercontent.com/JetBrains/lets-plot-kotlin/master/LICENSE)
[![Latest Release](https://img.shields.io/github/v/release/JetBrains/lets-plot)](https://github.com/JetBrains/lets-plot/releases/latest)


**Lets-Plot** is a multiplatform plotting library built on the principles of the Grammar of Graphics. 

The library' design is heavily influenced by Leland Wilkinson work [The Grammar of Graphics](https://www.goodreads.com/book/show/2549408.The_Grammar_of_Graphics) describing the deep features that underlie all statistical graphics.

> This grammar [...] is made up of a set of independent components that can be composed in many different ways. This makes [it] very powerful because you are not limited to a set of pre-specified graphics, but you can create new graphics that are precisely tailored for your problem.
> - Hadley Wickham, "[ggplot2: Elegant Graphics for Data Analysis](https://ggplot2-book.org/index.html)"


## Grammar of Graphics for Python [![Latest Release](https://badge.fury.io/py/lets-plot.svg)](https://pypi.org/project/lets-plot)

A bridge between R (ggplot2) and Python data visualization. \
To learn more see the documentation site at **[lets-plot.org](https://lets-plot.org)**.          


## Grammar of Graphics for Kotlin [![Latest Release](https://img.shields.io/github/v/release/JetBrains/lets-plot-kotlin)](https://github.com/JetBrains/lets-plot-kotlin/releases/latest)

### Notebooks
Create plots in [Kotlin Notebook](https://plugins.jetbrains.com/plugin/16340-kotlin-notebook),
[Datalore](https://datalore.jetbrains.com/report/static/HZqq77cegYd.E7get_WnChZ/aTA9lQnPkRwdCzT6uy95GZ), [Jupyter with Kotlin Kernel](https://github.com/Kotlin/kotlin-jupyter#readme) \
or any other notebook that supports `Kotlin Kernel`. \
To learn more see the **[Lets-Plot Kotlin API](https://github.com/JetBrains/lets-plot-kotlin)** project at GitHub.

### Compose Multiplatform
Embed Lets-Plot charts in [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) applications. \
To learn more see the **[Lets-Plot Skia Frontend](https://github.com/JetBrains/lets-plot-skia)** project at GitHub.

### JVM and Kotlin/JS
Embed Lets-Plot charts in JVM (Swing, JavaFX) and Kotlin/JS applications. <br>
To learn more see the **[Lets-Plot Kotlin API](https://github.com/JetBrains/lets-plot-kotlin)** project at GitHub.

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

## What is new in 4.7.0

- #### Time Series Plotting
  - Support for Python `time` and `date` objects.
  - Support for timezone-aware `datetime` objects and Pandas/Polars `Series`.

  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-25b/images/time_date_datetime.png" alt="f-25b/images/time_date_datetime.png" width="400" height="237">
  
  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25b/time_date_datetime.ipynb).

- #### Native support for PNG and PDF exports
  Exporting to PNG and PDF formats now uses the `ImageMagick` library bundled with Lets-Plot Python wheels and available out-of-the-box. <br>
  This replaces the previous dependency on the `CairoSVG` library and comes with improved support for LaTeX labels rasterization. <br>

- #### `geom_sina()` Geometry

  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-25b/images/geom_sina.png" alt="f-25b/images/geom_sina.png" width="400" height="276">

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25b/geom_sina.ipynb).

- #### `geom_text_repel()` and `geom_label_repel()` Geometries

  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-25b/images/geom_repel.png" alt="f-25b/images/geom_repel.png" width="400" height="232">

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25b/ggrepel.ipynb).

- #### `waterfall_plot()` Chart

  - Annotations support via `relative_labels` and `absolute_labels` parameters. <br>
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-25b/images/waterfall_plot_annotations.png" alt="f-25b/images/waterfall_plot_annotations.png" width="400" height="253">

    See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25b/waterfall_plot_annotations.ipynb).
                                   
  - Support for combining waterfall bars with other geometry layers. <br>
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-25b/images/waterfall_plot_layers.png" alt="f-25b/images/waterfall_plot_layers.png" width="400" height="227">

    See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25b/waterfall_plot_layers.ipynb).

- #### Continuous Data on Discrete Scales

  Continuous data when used with discrete positional scales is no longer transformed to discrete data. <br>
  Instead, it remains continuous, allowing for precise positioning of continuous elements relative to discrete ones. <br>
  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-25b/images/combo_discrete_continuous.png" alt="f-25b/images/combo_discrete_continuous.png" width="400" height="151">

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25b/numeric_data_on_discrete_scale.ipynb).

> [!TIP]
> New way of handling continuous data on discrete scales could potentially break existing plots.
> If you want to restore a broken plot to its original form, you can use the [`as_discrete()`](https://lets-plot.org/python/pages/api/lets_plot.mapping.as_discrete.html) function to annotate continuous data as discrete.


- #### Plot Layout
  The default plot layout has been improved to better accommodate axis labels and titles. <br>
  Also, new `theme()` options `axis_text_spacing`, `axis_text_spacing_x`, and `axis_text_spacing_y` control spacing between axis ticks and labels. <br>
  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-25b/images/plot_layout_diagram.png" alt="f-25b/images/plot_layout_diagram.png" width="400" height="175">

  See the [plot layout diagram](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25b/plot_layout_scheme.ipynb) showing various layout options and their effects on plot appearance.


- #### And More

  See [CHANGELOG.md](https://github.com/JetBrains/lets-plot/blob/master/CHANGELOG.md) for a full list of changes.


## Recent Updates in the [Gallery](https://lets-plot.org/python/pages/gallery.html)

  <a href="https://nbviewer.org/github/JetBrains/lets-plot-docs/blob/master/source/examples/demo/magnifier_inset.ipynb">
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-25a/images/magnifier_inset.png" alt="f-25a/images/magnifier_inset.png" width="128" height="128">
  </a>
  <a href="https://nbviewer.org/github/JetBrains/lets-plot-docs/blob/master/source/examples/demo/ggbunch_indonesia.ipynb">
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-25a/images/ggbunch_indonesia.png" alt="f-25a/images/ggbunch_indonesia.png" width="128" height="128">
  </a>
  <a href="https://nbviewer.org/github/JetBrains/lets-plot-docs/blob/master/source/examples/demo/theme_legend_scheme.ipynb">
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-24g/images/theme_legend_scheme.png" alt="f-24g/images/theme_legend_scheme.png" width="128" height="128">
  </a>
  <a href="https://nbviewer.org/github/JetBrains/lets-plot-docs/blob/master/source/examples/demo/interact_pan_zoom.ipynb">
    <img src="https://github.com/JetBrains/lets-plot-docs/blob/4b9571b8af759574fa2db313a102069d8f8c7238/source/_static/images/changelog/4.5.0/interact_pan_zoom.png?raw=true" alt="images/changelog/4.5.0/interact_pan_zoom.png" width="128" height="128">
  </a>
  <a href="https://nbviewer.org/github/JetBrains/lets-plot-docs/blob/master/source/examples/demo/lp_verse.ipynb">
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-24g/images/lp_verse.png" alt="f-24g/images/lp_verse.png" width="128" height="128">
  </a>
  <a href="https://nbviewer.org/github/JetBrains/lets-plot-docs/blob/master/source/examples/demo/us_unemployment.ipynb">
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-24e/images/us_unemployment.png" alt="f-24e/images/us_unemployment.png" width="128" height="128">
  </a>
  <a href="https://nbviewer.org/github/JetBrains/lets-plot-docs/blob/master/source/examples/demo/venn_diagram.ipynb">
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-24b/images/gal_venn_diagram.png" alt="f-24b/images/gal_venn_diagram.png" width="128" height="128">
  </a>
  <a href="https://nbviewer.org/github/JetBrains/lets-plot-docs/blob/master/source/examples/cookbook/geom_spoke.ipynb">
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-24b/images/gal_spoke.png" alt="f-24b/images/gal_spoke.png" width="128" height="128">
  </a>
  <a href="https://www.kaggle.com/code/alshan/indonesia-volcanoes-on-map">
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-24b/images/gal_indonesia_volcanoes_on_map.png" alt="f-24b/images/gal_indonesia_volcanoes_on_map.png" width="128" height="128">
  </a>
  <a href="https://www.kaggle.com/code/alshan/japanese-volcanoes-on-map">
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-24b/images/gal_japanese_volcanoes_on_map.png" alt="f-24b/images/gal_japanese_volcanoes_on_map.png" width="128" height="128">
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
