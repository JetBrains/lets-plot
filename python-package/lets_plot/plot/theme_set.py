#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

__all__ = [
    'theme_grey',
    'theme_gray',
    'theme_light',
    'theme_classic',
    'theme_minimal',
    'theme_minimal2',
    'theme_none',
    'theme_bw',
    'theme_void',
    'flavor_darcula',
    'flavor_solarized_light',
    'flavor_solarized_dark',
    'flavor_high_contrast_light',
    'flavor_high_contrast_dark',
    'flavor_standard'
]


def theme_grey():
    """
    Set the grey background with white gridlines.

    Returns
    -------
    ``FeatureSpec``
        Theme specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {'x': np.random.normal(size=1000)}
        ggplot(data, aes(x='x')) + geom_histogram() + \\
            theme_grey()

    """
    return FeatureSpec('theme', name="grey")


def theme_gray():
    """
    Set the gray background with white gridlines. It is an alias for `theme_grey() <https://lets-plot.org/python/pages/api/lets_plot.theme_grey.html>`__
    """
    return FeatureSpec('theme', name="gray")


def theme_light():
    """
    Set the light grey lines of various widths on the white background.

    Returns
    -------
    ``FeatureSpec``
        Theme specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {'x': np.random.normal(size=1000)}
        ggplot(data, aes(x='x')) + geom_histogram() + \\
            theme_light()

    """
    return FeatureSpec('theme', name="light")


def theme_classic():
    """
    Set the dark grey axes and no gridlines on the white background.

    Returns
    -------
    ``FeatureSpec``
        Theme specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {'x': np.random.normal(size=1000)}
        ggplot(data, aes(x='x')) + geom_histogram() + \\
            theme_classic()

    """
    return FeatureSpec('theme', name="classic")


def theme_minimal():
    """
    Set a minimalistic theme without axes lines.

    Returns
    -------
    ``FeatureSpec``
        Theme specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {'x': np.random.normal(size=1000)}
        ggplot(data, aes(x='x')) + geom_histogram() + \\
            theme_minimal()

    """
    return FeatureSpec('theme', name="minimal")


def theme_minimal2():
    """
    Set the default theme similar to `theme_minimal() <https://lets-plot.org/python/pages/api/lets_plot.theme_minimal.html>`__
    adding an x-axis line and only major gridlines.

    Returns
    -------
    ``FeatureSpec``
        Theme specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {'x': np.random.normal(size=1000)}
        ggplot(data, aes(x='x')) + geom_histogram() + \\
            theme_minimal2()

    """
    return FeatureSpec('theme', name="minimal2")


def theme_none():
    """
    Set a basic blue-accented scheme with the light blue background.

    Returns
    -------
    ``FeatureSpec``
        Theme specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {'x': np.random.normal(size=1000)}
        ggplot(data, aes(x='x')) + geom_histogram() + \\
            theme_none()

    """
    return FeatureSpec('theme', name="none")


def theme_bw():
    """
    Set a dark grey plot border and grey gridlines on the white background.

    Returns
    -------
    ``FeatureSpec``
        Theme specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {'x': np.random.normal(size=1000)}
        ggplot(data, aes(x='x')) + geom_histogram() + \\
            theme_bw()

    """
    return FeatureSpec('theme', name="bw")


def theme_void():
    """
    Set a completely blank (or "void") background theme by removing all
    non-data elements: no borders, axes, or gridlines.

    Returns
    -------
    ``FeatureSpec``
        Theme specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {'x': np.random.normal(size=1000)}
        ggplot(data, aes(x='x')) + geom_histogram() + \\
            theme_void()

    """
    blank_elems = {'line': 'blank', 'axis': 'blank'}
    return theme_classic() + FeatureSpec('theme', name=None, **blank_elems)


def flavor_darcula():
    """
    Set the Darcula color scheme.

    Returns
    -------
    ``FeatureSpec``
        Theme specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        data = {'name': ['pen', 'brush', 'paper'],
                'slice': [1, 3, 3]}
        ggplot(data) + \\
            geom_pie(aes(fill='name', slice='slice'),
                     stat='identity', color='pen',
                     tooltips='none', labels=layer_labels().line('@name')) + \\
            scale_fill_manual(['pen', 'brush', 'paper']) + \\
            flavor_darcula()

    """
    return FeatureSpec('theme', name=None, flavor="darcula")


def flavor_solarized_light():
    """
    Set the Solarized Light color scheme.

    Returns
    -------
    ``FeatureSpec``
        Theme specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        data = {'name': ['pen', 'brush', 'paper'],
                'slice': [1, 3, 3]}
        ggplot(data) + \\
            geom_pie(aes(fill='name', slice='slice'),
                     stat='identity', color='pen',
                     tooltips='none', labels=layer_labels().line('@name')) + \\
            scale_fill_manual(['pen', 'brush', 'paper']) + \\
            flavor_solarized_light()

    """
    return FeatureSpec('theme', name=None, flavor="solarized_light")


def flavor_solarized_dark():
    """
    Set the Solarized Dark color scheme.

    Returns
    -------
    ``FeatureSpec``
        Theme specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        data = {'name': ['pen', 'brush', 'paper'],
                'slice': [1, 3, 3]}
        ggplot(data) + \\
            geom_pie(aes(fill='name', slice='slice'),
                     stat='identity', color='pen',
                     tooltips='none', labels=layer_labels().line('@name')) + \\
            scale_fill_manual(['pen', 'brush', 'paper']) + \\
            flavor_solarized_dark()

    """
    return FeatureSpec('theme', name=None, flavor="solarized_dark")


def flavor_high_contrast_light():
    """
    Set a high-contrast light color scheme.

    Returns
    -------
    ``FeatureSpec``
        Theme specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        data = {'name': ['pen', 'brush', 'paper'],
                'slice': [1, 3, 3]}
        ggplot(data) + \\
            geom_pie(aes(fill='name', slice='slice'),
                     stat='identity', color='pen',
                     tooltips='none', labels=layer_labels().line('@name')) + \\
            scale_fill_manual(['pen', 'brush', 'paper']) + \\
            flavor_high_contrast_light()

    """
    return FeatureSpec('theme', name=None, flavor="high_contrast_light")


def flavor_high_contrast_dark():
    """
    Set a high-contrast dark color scheme.

    Returns
    -------
    ``FeatureSpec``
        Theme specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        data = {'name': ['pen', 'brush', 'paper'],
                'slice': [1, 3, 3]}
        ggplot(data) + \\
            geom_pie(aes(fill='name', slice='slice'),
                     stat='identity', color='pen',
                     tooltips='none', labels=layer_labels().line('@name')) + \\
            scale_fill_manual(['pen', 'brush', 'paper']) + \\
            flavor_high_contrast_dark()

    """
    return FeatureSpec('theme', name=None, flavor="high_contrast_dark")


def flavor_standard():
    """
    Set the theme’s default color scheme.
    Use to override other flavors or make defaults explicit.

    Returns
    -------
    ``FeatureSpec``
        Theme specification.

    """
    return FeatureSpec('theme', name=None, flavor="standard")
