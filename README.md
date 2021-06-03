# Lets-Plot  [![official JetBrains project](http://jb.gg/badges/official-flat-square.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)

<a href="https://opensource.org/licenses/MIT">
<img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="Couldn't load MIT license svg"/>
</a>

###
**Lets-Plot** is an open-source plotting library for statistical data. It is implemented using the [Kotlin programming language](https://kotlinlang.org/).

The design of Lets-Plot library is heavily influenced by Leland Wilkinson work [The Grammar of Graphics](https://www.goodreads.com/book/show/2549408.The_Grammar_of_Graphics) describing the deep features that underlie all statistical graphics.

> This grammar [...] is made up of a set of independent components that can be composed in many different ways. This makes [it] very powerful because you are not limited to a set of pre-specified graphics, but you can create new graphics that are precisely tailored for your problem.
> - Hadley Wickham, "ggplot2: [Elegant Graphics for Data Analysis](https://www.goodreads.com/book/show/6829192-ggplot2)"

Due to the unique multi-platform nature of Kotlin programming language, you can obtain the plotting functionality that is packaged as a JavaScript library, JVM library, and a native Python extension.

## Lets-Plot for Python 
<a href="https://pypi.org/project/lets-plot/">
<img src="https://badge.fury.io/py/lets-plot.svg"/></a>
<br>
<br>


The `Lets-Plot for Python` package offers a ggplot-like API for data visualization in [Jupyter](https://jupyter-notebook.readthedocs.io/en/stable/) notebooks 
as well as in other notebooks like [Datalore](https://view.datalore.io/notebook/Zzg9EVS6i16ELQo3arzWsP), 
[Kaggle](https://www.kaggle.com/alshan/lets-plot-quickstart) or [Colab](https://colab.research.google.com/drive/1o9rFQbkGqvvixYLTogrzIjFPp1ti2cH-).
  
You can even create plots using Python editor in PyCharm or IntelliJ IDEA (Scientific mode, the [Lets-Plot in SciView plugin](#pycharm_plugin) is required).   
  
Read the Lets-Plot for Python [documentation](https://jetbrains.github.io/lets-plot-docs/) to learn more about the package installation and usage.

<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/quickstart.png" alt="Couldn't load quickstart.png" width="505" height="260"/>

## JVM artifacts and Kotlin API

[![Maven Central](https://img.shields.io/maven-central/v/org.jetbrains.lets-plot/lets-plot-common?color=blue&label=Maven%20Central)](https://search.maven.org/search?q=lets-plot)

[Lets-Plot Kotlin API](https://github.com/JetBrains/lets-plot-kotlin) adds Grammar-of-Graphics plotting capabilities to Kotlin kernels for Jupyter notebooks.

Apart from Jupyter notebooks, **Lets-Plot Kotlin** API enables embedding plots into a JVM-based application.

To learn more about creating plots in a JVM environment see [README_DEV.md](https://github.com/JetBrains/lets-plot-kotlin/blob/master/README_DEV.md) 
in the [Lets-Plot Kotlin](https://github.com/JetBrains/lets-plot-kotlin) project on GitHub.

<a id="pycharm_plugin"></a>
## "Lets-Plot in SciView" plugin for IntelliJ IDEA and PyCharm

[![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/v/14379-lets-plot-in-sciview.svg)](http://plugins.jetbrains.com/plugin/14379-lets-plot-in-sciview)
[![JetBrains plugins](https://img.shields.io/jetbrains/plugin/d/14379-lets-plot-in-sciview.svg)](http://plugins.jetbrains.com/plugin/14379-lets-plot-in-sciview)

The plugin adds support for interactive plots in IntelliJ-based IDEs with the enabled _Scientific mode_.

The _Scientific mode_ in PyCharm and in IntelliJ IDEA Python plugin provides support for interactive scientific computing and data visualization.

To learn more about _Scientific mode_ check these help pages:
* [Scientific mode in PyCharm](https://www.jetbrains.com/help/pycharm/matplotlib-support.html)
* [Scientific mode in IntelliJ IDEA](https://www.jetbrains.com/help/idea/matplotlib-support.html)

To learn more about the plugin check: [Lets-Plot in SciView plugin homepage](https://plugins.jetbrains.com/plugin/14379-lets-plot-in-sciview). 

<div>
<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/pycharm_quickstart.png" alt="Couldn't load pycharm_quickstart.png" width="537" height="188"/>
<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/pycharm_logo.png" alt="Couldn't load pycharm_logo.png" width="50" height="50"/>
</div>
<div>
<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/pycharm_map_fr_low_65.gif" alt="Couldn't load pycharm_map_fr_low_65.png" width="537" height="188"/>
</div>


## License

Code and documentation released under the [MIT license](https://github.com/JetBrains/lets-plot/blob/master/LICENSE).
Copyright © 2019-2021, JetBrains s.r.o.
