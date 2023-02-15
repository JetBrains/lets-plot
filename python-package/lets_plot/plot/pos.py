#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

#
# Position Adjustments
#
__all__ = ['position_dodge', 'position_jitter', 'position_nudge', 'position_jitterdodge',
           'position_stack', 'position_gstack', 'position_fill', 'position_gfill']


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


def position_jitter(width=None, height=None):
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
                       position=position_jitter(width=.2, height=.2))

    """
    return _pos('jitter', width=width, height=height)


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


def position_jitterdodge(dodge_width=None, jitter_width=None, jitter_height=None):
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
                       position=position_jitterdodge())

    """
    return _pos('jitterdodge', dodge_width=dodge_width, jitter_width=jitter_width, jitter_height=jitter_height)


def position_stack(vjust=None):
    """
    Adjust position by stacking overlapping objects on top of each other.

    Parameters
    ----------
    vjust : float
        Vertical adjustment for geoms that have a position (like points or lines),
        not a dimension (like bars or areas).
        Set to 0 to align with the bottom, 0.5 for the middle, and 1 for the top.

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
        :emphasize-lines: 10

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'x': [1, 1, 2, 2],
            'y': [1, 3, 2, 1],
            'grp': ["a", "b", "a", "b"]
        }
        ggplot(data, aes('x', 'y', group='grp')) + \\
            geom_bar(aes(fill='grp'), stat='identity') + \\
            geom_text(aes(label='y'), position=position_stack(0.5))

    """
    return _pos('stack', vjust=vjust)


def position_gstack(vjust=None):
    """
    Adjust position by stacking overlapping groups of objects on top of each other.
    Preferred for density-like geometries.

    Parameters
    ----------
    vjust : float
        Vertical adjustment for geoms that have a position (like points or lines),
        not a dimension (like bars or areas).
        Set to 0 to align with the bottom, 0.5 for the middle, and 1 for the top.

    Returns
    -------
    `FeatureSpec`
        Geom object position specification.

    Notes
    -----
    Adjust position by stacking overlapping groups of objects on top of each other.

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
            geom_point(position=position_gstack(), size=10)

    """
    return _pos('gstack', vjust=vjust)


def position_fill(vjust=None):
    """
    Adjust position by stacking overlapping objects on top of each other
    and standardise each stack to have constant height.
                                        
    Parameters
    ----------
    vjust : float
        Vertical adjustment for geoms that have a position (like points or lines),
        not a dimension (like bars or areas).
        Set to 0 to align with the bottom, 0.5 for the middle, and 1 for the top.

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
            'x': [1, 1, 2, 2],
            'y' : [1, 3, 2, 1],
            'grp': ["a", "b", "a", "b"]
        }
        ggplot(data, aes('x', 'y', group='grp')) + \\
            geom_bar(aes(fill='grp'), stat='identity', position='fill') + \\
            geom_text(aes(label='y'), position=position_fill(0.5))

    """
    return _pos('fill', vjust=vjust)


def position_gfill(vjust=None):
    """
    Adjust position by stacking overlapping groups of objects on top of each other
    and standardise each stack to have constant height.

    Parameters
    ----------
    vjust : float
        Vertical adjustment for geoms that have a position (like points or lines),
        not a dimension (like bars or areas).
        Set to 0 to align with the bottom, 0.5 for the middle, and 1 for the top.

    Returns
    -------
    `FeatureSpec`
        Geom object position specification.

    Notes
    -----
    Adjust position by stacking overlapping groups of objects on top of each other
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
            geom_point(position=position_gfill(), size=10)

    """
    return _pos('gfill', vjust=vjust)


def _pos(name, **other):
    args = locals().copy()
    args.pop('other')
    return FeatureSpec('pos', **args, **other)
