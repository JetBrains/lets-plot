#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec, FeatureSpecArray
from .scale import _scale

#
# Plot title
# Scale names: axis labels / legend titles
#
__all__ = ['ggtitle',
           'labs',
           'xlab', 'ylab']


def ggtitle(label):
    """
    Add title to the plot.

    Parameters
    ----------
    label: string
        The text for the plot title.

    Returns
    --------
    `FeatureSpec`
        Plot title specification.

    Note
    ----
    Changes plot title.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7
        
        from lets_plot import *
        LetsPlot.setup_html()
        data = {}
        data['x'] = [x for x in range(10)]
        data['y'] = [y for y in range(10)]
        ggplot(data=data) + geom_point(aes(x='x', y='y', size='y')) + \\
            ggtitle(label='New plot title')
    """
    return labs(title=label)


def xlab(label):
    """
    Add label to the x axis.

    Parameters
    ----------
    label: string
        The text for the x axis label.

    Returns
    -------
    `FeatureSpec`
        Axis label specification.

    Note
    ----
    Change x axis label.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7
        
        from lets_plot import *
        LetsPlot.setup_html()
        data = {}
        data['x'] = [x for x in range(10)]
        data['y'] = [y for y in range(10)]
        ggplot(data=data) + geom_point(aes(x='x', y='y', size='y')) + \\
            xlab('x axis label')
    """
    return labs(x=label)


def ylab(label):
    """
    Add label to the y axis.

    Parameters
    ----------
    label: string
        The text for the y axis label.

    Returns
    -------
    `FeatureSpec`
        Axis label specification.

    Note
    ----
    Change y axis label.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7
        
        from lets_plot import *
        LetsPlot.setup_html()
        data = {}
        data['x'] = [x for x in range(10)]
        data['y'] = [y for y in range(10)]
        ggplot(data=data) + geom_point(aes(x='x', y='y', size='y')) + \\
            ylab('y axis label')
    """
    return labs(y=label)


def labs(**kwargs):
    """
    Change plot title, axis labels and legend titles.

    Parameters
    ----------
    kwargs: string
        A list of new name-value pairs where name should be an aesthetic,
        e.g. title='Plot title' or aesthetic='Scale label'.

    Returns
    -------
    `FeatureSpec`
        Axis label specification.

    Note
    ----
    Change plot title, axis labels and legend titles.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7-8
        
        from lets_plot import *
        LetsPlot.setup_html()
        data = {}
        data['x'] = [x for x in range(10)]
        data['y'] = [y for y in range(10)]
        ggplot(data=data) + geom_point(aes(x='x', y='y', size='y')) + \\
            labs(title='New plot title', x='New x axis label', \\
                 y='New y axis label', size = 'New legend title')
    """
    specs = []
    for k, v in kwargs.items():
        if k == 'title':
            specs.append(FeatureSpec('ggtitle', name=None, text=v))
        else:
            specs.append(_scale(aesthetic=k, name=v))

    if len(specs) == 1:
        return specs[0]
    return FeatureSpecArray(*specs)
