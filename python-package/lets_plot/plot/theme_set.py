#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

__all__ = [
    'theme_grey',
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
    'flavor_high_contrast_dark'
]


def theme_grey():
    """
    Grey background and white gridlines.

    Returns
    -------
    `FeatureSpec`
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


def theme_light():
    """
    Light grey lines of various widths on white backgrounds.

    Returns
    -------
    `FeatureSpec`
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
    Black axes and no gridlines.

    Returns
    -------
    `FeatureSpec`
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
    A minimalistic theme without axes lines.

    Returns
    -------
    `FeatureSpec`
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
    Default theme similar to `theme_minimal()` but with x axis line and only major grid lines.

    Returns
    -------
    `FeatureSpec`
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
    A completely empty theme.

    Returns
    -------
    `FeatureSpec`
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
    Grey lines on white backgrounds with black plot border.

    Returns
    -------
    `FeatureSpec`
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
    A completely empty theme.

    Returns
    -------
    `FeatureSpec`
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
    blank_elems = {'line': 'blank', 'rect': 'blank', 'axis': 'blank'}
    return theme_classic() + FeatureSpec('theme', name=None, **blank_elems)


def flavor_darcula():
    """
    Darcula color scheme.

    Returns
    -------
    `FeatureSpec`
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
    Solarized light color scheme.

    Returns
    -------
    `FeatureSpec`
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
    Solarized dark color scheme.

    Returns
    -------
    `FeatureSpec`
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
    High contrast light color scheme.

    Returns
    -------
    `FeatureSpec`
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
    High contrast dark color scheme.

    Returns
    -------
    `FeatureSpec`
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
