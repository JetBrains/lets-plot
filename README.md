# Lets-Plot

<a href="https://opensource.org/licenses/MIT">
<img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="Couldn't load MIT license svg"/>
</a>

###
**Lets-Plot** is an open-source plotting library for statistical data. It is implemented using the [Kotlin programming language](https://kotlinlang.org/).

The design of Lets-Plot library is heavily influenced by Leland Wilkinson work [The Grammar of Graphics](https://www.goodreads.com/book/show/2549408.The_Grammar_of_Graphics) describing the deep features that underlie all statistical graphics.

> This grammar [...] is made up of a set of independent components that can be composed in many different ways. This makes [it] very powerful because you are not limited to a set of pre-specified graphics, but you can create new graphics that are precisely tailored for your problem.
> - Hadley Wickham, "ggplot2: [Elegant Graphics for Data Analysis](https://www.goodreads.com/book/show/6829192-ggplot2)"

Due to the unique multi-platform nature of Kotlin programming language, you can obtain the plotting functionality that is packaged as a JavaScript library, JVM library, and a native Python extension.

## Python Extension 
<a href="https://pypi.org/project/lets-plot/"/>
<img src="https://badge.fury.io/py/lets-plot.svg"/>
<br>

[Learn](README_PYTHON.md) how to create plots using the `Lets-Plot` python package.

<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/quickstart.png" alt="Couldn't load quickstart.png" width="505" height="260"/>

## JVM artifacts and Kotlin API

[ ![Download](https://api.bintray.com/packages/jetbrains/lets-plot-maven/lets-plot-jars/images/download.svg)](https://bintray.com/jetbrains/lets-plot-maven/lets-plot-jars/_latestVersion)

[Lets-Plot Kotlin API](https://github.com/JetBrains/lets-plot-kotlin) adds Grammar-of-Graphics plotting capabilities to Kotlin kernels for Jupyter notebooks.

Besides Jupyter notebooks, **Lets-Plot Kotlin** API enables embedding plots into a JVM-based application.

To learn more about creating plots in a JVM environment see [README_DEV.md](https://github.com/JetBrains/lets-plot-kotlin/blob/master/README_DEV.md) in
[Lets-Plot Kotlin](https://github.com/JetBrains/lets-plot-kotlin) project on GitHub.


## License

Code and documentation released under the [MIT license](https://github.com/JetBrains/lets-plot/blob/master/LICENSE).
Copyright © 2019-2020, JetBrains s.r.o.
