#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

#
# Position Adjustments
#
__all__ = ['position_dodge', 'position_dodgev', 'position_jitter', 'position_nudge', 'position_jitterdodge',
           'position_stack', 'position_fill']


def position_dodge(width=None):
    """
    Adjust position by dodging overlaps to the side.

    Parameters
    ----------
    width : float
        Dodging width, when different to the width of the individual elements.
        This is useful when you want to align narrow geoms with wider geoms.
        The value of width is relative and typically ranges between 0 and 1.
        Values that are greater than 1 lead to overlapping of the objects.

    Returns
    -------
    `FeatureSpec`
        Geom object position specification.

    Notes
    -----
    Adjust position by dodging overlaps to the side.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.randint(5, size=n)
        c = np.random.choice(['a', 'b', 'c'], size=n)
        ggplot({'x': x, 'c': c}, aes(x='x')) + \\
            geom_bar(aes(fill='c'), width=.4, \\
                     position=position_dodge(width=.6))

    """
    return _pos('dodge', width=width)


def position_dodgev(height=None):
    """
    Adjust position by dodging overlaps to the side.

    Parameters
    ----------
    height : float
        Dodging height, when different to the height of the individual elements.
        This is useful when you want to align narrow geoms with taller geoms.
        The value of height is relative and typically ranges between 0 and 1.
        Values that are greater than 1 lead to overlapping of the objects.

    Returns
    -------
    `FeatureSpec`
        Geom object position specification.

    Notes
    -----
    Adjust position by dodging overlaps to the side.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'xmin': [0.2, 4.6, 1.6, 3.5],
            'xmax': [1.5, 5.3, 3.0, 4.4],
            'y': ['a', 'a', 'b', 'b'],
            'c': ['gr1', 'gr2', 'gr1', 'gr2']
        }
        ggplot(data, aes(y='y', color='c')) + \\
            geom_errorbar(aes(xmin='xmin', xmax='xmax'), height=0.1, size=2, \\
                          position=position_dodgev(height=0.2))

    """
    return _pos('dodgev', height=height)


def position_jitter(width=None, height=None, seed=None):
    """
    Adjust position by assigning random noise to points. Better for discrete values.

    Parameters
    ----------
    width : float
        Jittering width.
        The value of width is relative and typically ranges between 0 and 0.5.
        Values that are greater than 0.5 lead to overlapping of the points.
    height : float
        Jittering height.
        The value of height is relative and typically ranges between 0 and 0.5.
        Values that are greater than 0.5 lead to overlapping of the points.
    seed : int
        A random seed to make the jitter reproducible.
        If None (the default value), the seed is initialised with a random value.

    Returns
    -------
    `FeatureSpec`
        Geom object position specification.

    Notes
    -----
    Adjust position by dodging overlaps to the side.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.randint(4, size=n)
        y = np.random.randint(3, size=n)
        c = np.char.add(x.astype(str), y.astype(str))
        ggplot({'x': x, 'y': y, 'c': c}, aes('x', 'y')) + \\
            geom_point(aes(fill='c'), show_legend=False, \\
                       size=8, alpha=.5, shape=21, color='black', \\
                       position=position_jitter(width=.2, height=.2, seed=42))

    """
    return _pos('jitter', width=width, height=height, seed=seed)


def position_nudge(x=None, y=None):
    """
    Adjust position by nudging a given offset.

    Parameters
    ----------
    x : float
        Nudging width.
    y : float
        Nudging height.

    Returns
    -------
    `FeatureSpec`
        Geom object position specification.

    Notes
    -----
    Adjust position by dodging overlaps to the side.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 5
        np.random.seed(42)
        x = np.random.uniform(size=n)
        y = np.random.uniform(size=n)
        t = np.random.choice(list('abcdefghijk'), size=n)
        ggplot({'x': x, 'y': y, 't': t}, aes('x', 'y')) + \\
            geom_point(size=5, shape=21, color='black', fill='red') + \\
            geom_text(aes(label='t'), position=position_nudge(y=.05))

    """
    return _pos('nudge', x=x, y=y)


def position_jitterdodge(dodge_width=None, jitter_width=None, jitter_height=None, seed=None):
    """
    This is primarily used for aligning points generated through `geom_point()`
    with dodged boxplots (e.g., a `geom_boxplot()` with a fill aesthetic supplied).

    Parameters
    ----------
    dodge_width : float
        Bin width.
        The value of `dodge_width` is relative and typically ranges between 0 and 1.
        Values that are greater than 1 lead to overlapping of the boxes.
    jitter_width : float
        Jittering width.
        The value of `jitter_width` is relative and typically ranges between 0 and 0.5.
        Values that are greater than 0.5 lead to overlapping of the points.
    jitter_height : float
        Jittering height.
        The value of `jitter_height` is relative and typically ranges between 0 and 0.5.
        Values that are greater than 0.5 lead to overlapping of the points.
    seed : int
        A random seed to make the jitter reproducible.
        If None (the default value), the seed is initialised with a random value.

    Returns
    -------
    `FeatureSpec`
        Geom object position specification.

    Notes
    -----
    Adjust position by dodging overlaps to the side.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 13

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 50
        np.random.seed(42)
        x = np.random.uniform(size=n)
        c = np.random.choice(['a', 'b', 'c'], size=n)
        ggplot({'x': x, 'c': c}) + \\
            geom_crossbar(aes(x='c', y='x', color='c'), \\
                          stat='boxplot') + \\
            geom_point(aes(x='c', y='x', color='c'), \\
                       size=4, shape=21, fill='white',
                       position=position_jitterdodge(seed=42))

    """
    return _pos('jitterdodge', dodge_width=dodge_width, jitter_width=jitter_width, jitter_height=jitter_height,
                seed=seed)


def position_stack(vjust=None, mode=None):
    """
    Adjust position by stacking overlapping objects on top of each other.
    Preferred for density-like geometries.

    Parameters
    ----------
    vjust : float
        Vertical adjustment for geoms that have a position (like points or lines),
        not a dimension (like bars or areas).
        Set to 0 to align with the bottom, 0.5 for the middle, and 1 for the top.
    mode : {'groups', 'all'}, default='groups'
        If 'groups', objects inside one group are positioned as in `position='identity'`,
        but each group is shifted to sum of heights of previous groups
        (where height of a group is a maximum of it's y values).
        If 'all', each object will be shifted.

    Returns
    -------
    `FeatureSpec`
        Geom object position specification.

    Notes
    -----
    Adjust position by stacking overlapping objects on top of each other.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'x': [1, 1, 1, 2, 2, 2],
            'y': [1, 2, 3, 1, 2, 3],
            'g': ["a", "b", "b", "a", "a", "b"],
        }
        ggplot(data, aes('x', 'y', color='g')) + \\
            geom_point(position=position_stack(), size=10)

    """
    return _pos('stack', vjust=vjust, mode=mode)


def position_fill(vjust=None, mode=None):
    """
    Adjust position by stacking overlapping objects on top of each other
    and standardise each stack to have constant height.

    Parameters
    ----------
    vjust : float
        Vertical adjustment for geoms that have a position (like points or lines),
        not a dimension (like bars or areas).
        Set to 0 to align with the bottom, 0.5 for the middle, and 1 for the top.
    mode : {'groups', 'all'}, default='groups'
        If 'groups', objects inside one group are positioned as in `position='identity'`,
        but each group is shifted to sum of heights of previous groups
        (where height of a group is a maximum of it's y values).
        If 'all', each object will be shifted.

    Returns
    -------
    `FeatureSpec`
        Geom object position specification.

    Notes
    -----
    Adjust position by stacking overlapping objects on top of each other
    and standardise each stack to have constant height.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'x': [1, 1, 1, 1, 1, 2, 2, 2],
            'y': [1, 2, 3, 4, 5, 1, 2, 3],
            'g': ["a", "a", "b", "b", "b", "a", "a", "b"],
        }
        ggplot(data, aes('x', 'y', color='g')) + \\
            geom_point(position=position_fill(), size=10)

    """
    return _pos('fill', vjust=vjust, mode=mode)


def _pos(name, **other):
    args = locals().copy()
    args.pop('other')
    return FeatureSpec('pos', **args, **other)
