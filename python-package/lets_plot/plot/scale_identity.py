#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .scale import _scale

#
# Identity Scales
#

__all__ = ['scale_color_identity',
           'scale_fill_identity',
           'scale_shape_identity',
           'scale_linetype_identity',
           'scale_alpha_identity',
           'scale_size_identity'
           ]


def scale_color_identity(name=None, breaks=None, labels=None, limits=None, na_value=None, guide='none', format=None):
    """
    Use this scale when your data has already been scaled.
    I.e. it already represents aesthetic values that ggplot2 can handle directly.
    This will not produce a legend unless you also supply the breaks and labels.

    Parameters
    ----------
    name : str
        The name of the scale - used as the axis label or the legend title.
    breaks : list of float
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
    guide, default='none'
        Guide to use for this scale. Defaults to 'none'.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Input data expected: list of strings containing:

    - names of colors (e.g. 'green'),
    - hex codes of colors (e.g. 'x00ff00'),
    - css colors (e.g. 'rgb(0, 255, 0)').

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 50
        np.random.seed(42)
        c = np.random.choice(['#e41a1c', '#377eb8', '#4daf4a'], size=n)
        v = np.random.normal(size=n)
        ggplot({'c': c, 'v': v}, aes(x='c', y='v')) + \\
            geom_boxplot(aes(color='c'), size=2) + \\
            scale_color_identity()

    """
    return _scale('color',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=None,
                  format=format,
                  #
                  scale_mapper_kind='identity')


def scale_fill_identity(name=None, breaks=None, labels=None, limits=None, na_value=None, guide='none', format=None):
    """
    Use this scale when your data has already been scaled.
    I.e. it already represents aesthetic values that ggplot2 can handle directly.
    This will not produce a legend unless you also supply the breaks and labels.

    Parameters
    ----------
    name : str
        The name of the scale - used as the axis label or the legend title.
    breaks : list of float
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
    guide, default='none'
        Guide to use for this scale. Defaults to 'none'.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Input data expected: list of strings containing:

    - names of colors (e.g. 'green'),
    - hex codes of colors (e.g. 'x00ff00'),
    - css colors (e.g. 'rgb(0, 255, 0)').

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8-10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        colors = {'red': '#e41a1c', 'green': '#4daf4a', 'blue': '#377eb8'}
        c = np.random.choice(list(colors.values()), size=20)
        ggplot({'c': c}, aes(x='c')) + geom_bar(aes(fill='c')) + \\
            scale_fill_identity(guide=guide_legend(), name='color', \\
                                breaks=list(colors.values()), \\
                                labels=list(colors.keys()))

    """
    return _scale('fill',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=None,
                  format=format,
                  #
                  scale_mapper_kind='identity')


def scale_shape_identity(name=None, breaks=None, labels=None, limits=None, na_value=None, guide='none', format=None):
    """
    Use this scale when your data has already been scaled.
    I.e. it already represents aesthetic values that ggplot2 can handle directly.
    This will not produce a legend unless you also supply the breaks and labels.

    Parameters
    ----------
    name : str
        The name of the scale - used as the axis label or the legend title.
    breaks : list of float
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
    guide, default='none'
        Guide to use for this scale. Defaults to 'none'.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Input data expected: numeric codes of shapes.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        from lets_plot import *
        LetsPlot.setup_html()
        n, m = 26, 6
        x = [i % m for i in range(n)]
        y = [int(i / m) for i in range(n)]
        s = list(range(n))
        ggplot({'x': x, 'y': y, 's': s}, aes('x', 'y')) + \\
            geom_point(aes(shape='s'), size=10, show_legend=False, \\
                       color='#2166ac', fill='#fddbc7', \\
                       tooltips=layer_tooltips().line('shape #@s')) + \\
            scale_shape_identity()

    """
    return _scale('shape',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=None,
                  format=format,
                  #
                  solid=None,
                  scale_mapper_kind='identity', discrete=True)


def scale_linetype_identity(name=None, breaks=None, labels=None, limits=None, na_value=None, guide='none', format=None):
    """
    Use this scale when your data has already been scaled.
    I.e. it already represents aesthetic values that ggplot2 can handle directly.
    This will not produce a legend unless you also supply the breaks and labels.

    Parameters
    ----------
    name : str
        The name of the scale - used as the axis label or the legend title.
    breaks : list of float
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
    guide, default='none'
        Guide to use for this scale. Defaults to 'none'.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Input data expected: numeric codes or names of line types (e.g. 'dotdash').
    The codes are: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash',
    5 = 'longdash', 6 = 'twodash'.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 13

        from lets_plot import *
        LetsPlot.setup_html()
        n = 7
        data = {
            'x': [0] * n,
            'xend': [1] * n,
            'y': list(range(n)),
            'yend': list(range(n)),
        }
        ggplot(data) + \\
            geom_segment(aes(x='x', xend='xend', y='y', \\
                             yend='yend', linetype='y')) + \\
            scale_linetype_identity()

    """
    return _scale('linetype',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=None,
                  format=format,
                  #
                  scale_mapper_kind='identity', discrete=True)


def scale_alpha_identity(name=None, breaks=None, labels=None, limits=None, na_value=None, guide='none', format=None):
    """
    Use this scale when your data has already been scaled.
    I.e. it already represents aesthetic values that ggplot2 can handle directly.
    This will not produce a legend unless you also supply the breaks and labels.

    Parameters
    ----------
    name : str
        The name of the scale - used as the axis label or the legend title.
    breaks : list of float
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
    guide, default='none'
        Guide to use for this scale. Defaults to 'none'.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Input data expected: numeric values in range [0, 1].

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        a = np.random.uniform(0, .5, size=n)
        ggplot({'x': x, 'y': y, 'a': a}, aes('x', 'y')) + \\
            geom_point(aes(alpha='a'), shape=21, size=10) + \\
            scale_alpha_identity(limits=[.2, .5], breaks=[.2, .3, .4, .5])

    """
    return _scale('alpha',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=None,
                  trans=None,
                  format=format,
                  #
                  scale_mapper_kind='identity')


def scale_size_identity(name=None, breaks=None, labels=None, limits=None, na_value=None, guide='none', format=None):
    """
    Use this scale when your data has already been scaled.
    I.e. it already represents aesthetic values that ggplot2 can handle directly.
    This will not produce a legend unless you also supply the breaks and labels.

    Parameters
    ----------
    name : str
        The name of the scale - used as the axis label or the legend title.
    breaks : list of float
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
    guide, default='none'
        Guide to use for this scale. Defaults to 'none'.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Input data expected: positive numeric values.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        w = np.random.choice([8, 10, 12], size=n)
        ggplot({'x': x, 'y': y, 'w': w}, aes('x', 'y')) + \\
            geom_point(aes(size='w'), shape=21, alpha=.2) + \\
            scale_size_identity()

    """
    return _scale('size',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=None,
                  format=format,
                  #
                  scale_mapper_kind='identity')
