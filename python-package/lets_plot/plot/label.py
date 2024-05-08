#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec, FeatureSpecArray
from .scale import _scale
from .guide import guides, guide_legend

#
# Plot title
# Scale names: axis labels / legend titles
#
__all__ = ['ggtitle',
           'labs',
           'xlab', 'ylab']


def ggtitle(label, subtitle=None):
    """
    Add title to the plot.

    Parameters
    ----------
    label : str
        The text for the plot title.
    subtitle : str
        The text for the plot subtitle.

    Returns
    -------
    `FeatureSpec`
        Plot title specification.

    Notes
    -----
    Split a long title/subtitle into two lines or more using `\\\\n` as a text separator.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5
        
        from lets_plot import *
        LetsPlot.setup_html()
        data = {'x': list(range(10)), 'y': list(range(10))}
        ggplot(data, aes('x', 'y')) + geom_point(aes(size='y')) + \\
            ggtitle('New Plot Title')

    """
    return labs(title=label, subtitle=subtitle)


def xlab(label):
    """
    Add label to the x axis.

    Parameters
    ----------
    label : str
        The text for the x axis label.

    Returns
    -------
    `FeatureSpec`
        Axis label specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5
        
        from lets_plot import *
        LetsPlot.setup_html()
        data = {'x': list(range(10)), 'y': list(range(10))}
        ggplot(data, aes('x', 'y')) + geom_point(aes(size='y')) + \\
            xlab('x axis label')

    """
    return labs(x=label)


def ylab(label):
    """
    Add label to the y axis.

    Parameters
    ----------
    label : str
        The text for the y axis label.

    Returns
    -------
    `FeatureSpec`
        Axis label specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5
        
        from lets_plot import *
        LetsPlot.setup_html()
        data = {'x': list(range(10)), 'y': list(range(10))}
        ggplot(data, aes('x', 'y')) + geom_point(aes(size='y')) + \\
            ylab('y axis label')

    """
    return labs(y=label)


def labs(title=None, subtitle=None, caption=None, custom_legend=None, **labels):
    """
    Change plot title and axis label.

    Parameters
    ----------
    title : str
        The text for the plot title.
    subtitle : str
        The text for the plot subtitle.
    caption : str
        The text for the plot caption.
    custom_legend : str or dict
        The text for the custom legend title.
    labels
        Name-value pairs where name should be an aesthetic and value should be a string, e.g. `color="New Color label"`.

    Returns
    -------
    `FeatureSpec` or `FeatureSpecArray`
        Labels specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5-6
        
        from lets_plot import *
        LetsPlot.setup_html()
        data = {'x': list(range(10)), 'y': list(range(10))}
        ggplot(data, aes('x', 'y')) + geom_point(aes(size='y')) + \\
            labs(title='New plot title', subtitle='The plot subtitle', caption='The plot caption', \\
                 x='New x axis label', y='New y axis label', size='New legend title')

    """
    specs = []

    # handle ggtitle
    if title is not None or subtitle is not None:
        specs.append(FeatureSpec('ggtitle', name=None, text=title, subtitle=subtitle))

    # plot caption
    if caption is not None:
        specs.append(FeatureSpec('caption', name=None, text=caption))

    # custom legend titles
    if custom_legend is not None:
        guides_args = {}
        if isinstance(custom_legend, dict):
            guides_args = {key: guide_legend(title=value) for key, value in custom_legend.items()}
        else:
            guides_args['custom_legend'] = guide_legend(title=custom_legend)
        specs.append(guides(**guides_args))

    # scales
    for aes, label in labels.items():
        specs.append(_scale(aesthetic=aes, name=label))

    if len(specs) == 1:
        return specs[0]
    return FeatureSpecArray(*specs)
