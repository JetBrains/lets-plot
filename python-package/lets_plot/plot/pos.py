#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

#
# Position Adjustments
#
__all__ = ['position_dodge', 'position_jitter', 'position_nudge', 'position_jitterdodge']


def position_dodge(width=None):
    """
    Adjust position by dodging overlaps to the side

    Parameters
    ----------
    width:
        Dodging width, when different to the width of the individual elements.
        This is useful when you want to align narrow geoms with wider geoms.

    Returns
    -------
        geom object position specification
    Notes
    -----
        Adjust position by dodging overlaps to the side.
     Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from scipy.stats import multivariate_normal
    >>> N = 100
    >>> M = 3
    >>> mean = np.zeros(M)
    >>> cov = np.eye(M)
    >>> X = multivariate_normal.rvs(mean, cov, N)
    >>> X = X.astype(int) # comment this line to make variables continuous back
    >>> dat = pd.DataFrame(X)
    >>> dat = pd.melt(dat)
    >>> ggplot(dat, aes(x='value', group='variable', fill='variable')) \
    >>>     + geom_bar(stat='bin', position=position_dodge(width=5.0), width=10, alpha=0.8)
    """
    return _pos('dodge', width=width)


def position_jitter(width=None, height=None):
    """
    Adjust position by assigning random noise to points. Better for discrete values

    Parameters
    ----------
    width:
        Jittering width
    height:
        Jittering height

    Returns
    -------
        geom object position specification
    Notes
    -----
        Adjust position by dodging overlaps to the side.
    Examples
    ---------
    >>> import numpy as np
    >>> from random import randint
    >>> N = 100
    >>> x = np.array([['a', 'b', 'c'] for i in range(N)]).flatten()
    >>> y = np.array([randint(0, 2) for i in range(3 * N)])
    >>> ggplot(mapping=aes(x, y)) + geom_point(position=position_jitter(width=.2, height=.2))
    """
    return _pos('jitter', width=width, height=height)


def position_nudge(x=None, y=None):
    """
    Adjust position by nudging a given offset

    Parameters
    ----------
    x:
        Nudging width
    y:
        Nudging height

    Returns
    -------
        geom object position specification
    Notes
    -----
        Adjust position by dodging overlaps to the side.
     Examples
    ---------
    >>> x = [1, 2, 3]
    >>> y = [1, 2, 3]
    >>> ggplot(mapping=aes(x, y)) + geom_point() + geom_point(position=position_nudge(y=-0.2), color='orange')
    """
    return _pos('nudge', x=x, y=y)


def position_jitterdodge(dodge_width=None, jitter_width=None, jitter_height=None):
    """
    This is primarily used for aligning points generated through geom_point() with dodged boxplots
    (e.g., a geom_boxplot() with a fill aesthetic supplied).

    Parameters
    ----------
    dodge_width:
        Bin width
    jitter_width:
        jittering width
    jitter_height:
        jittering height

    Returns
    -------
        geom object position specification
    Notes
    -----
        Adjust position by dodging overlaps to the side.
     Examples
    ---------
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> mpg_url = 'https://vincentarelbundock.github.io/Rdatasets/csv/ggplot2/mpg.csv'
    >>> mpg = pd.read_csv(mpg_url)
    >>> p = ggplot(mpg, aes('cyl', 'hwy',group='drv',fill='drv'))
    >>> p += scale_color_discrete() + scale_fill_discrete()
    >>> p + geom_boxplot(outlier_size=0) + geom_point(position='jitterdodge', shape=21, color='black')
    """
    return _pos('jitterdodge', dodge_width=dodge_width, jitter_width=jitter_width, jitter_height=jitter_height)


def _pos(name, **other):
    args = locals().copy()
    args.pop('other')
    return FeatureSpec('pos', **args, **other)
