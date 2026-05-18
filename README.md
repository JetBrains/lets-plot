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

## What is new in 4.10.0

- #### `ggdeck()`

  The new `ggdeck()` function overlays multiple independent plots in a shared plotting area.
  Typically, all plots share one axis — enabling dual-axis charts and multivariate comparisons.<br><br>
    - **Dual Axis:**
   
      <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-26b/images/ggdeck_dual_axis.png" alt="f-26b/images/ggdeck_dual_axis.png" width="550" height="295">

      See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26b/ggdeck_dual_axis.html).<br><br>

    - **Multivariate Comparison:**

      <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-26b/images/ggdeck_plot_overlay.png" alt="f-26b/images/ggdeck_plot_overlay.png" width="600" height="283">

      See [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26b/ggdeck_plot_overlay.html).
              
                 

- #### Alpha Channel in Color Strings

  - Named colors accept an opacity suffix after a slash: `"steelblue/0.35"`.
  - Hex colors accept an alpha channel: `#RRGGBBAA` or short form `#RGBA`.

  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-26b/images/color_alpha_componnet.png" alt="f-26b/images/color_alpha_componnet.png" width="400" height="214">

  See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26b/color_alpha.html).


- #### Text Angle in Facet Strip Labels

  Facet strip labels can now be rotated via the `angle` parameter of `element_text()`, applied to `strip_text`, `strip_text_x`, or `strip_text_y`.

  Thanks to a contribution by [tentrillion](https://github.com/tentrillion).

  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/f-26b/images/facet_strip_text_angle.png" alt="f-26b/images/facet_strip_text_angle.png" width="400" height="225">

  See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26b/strip_text_angle.html).



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
