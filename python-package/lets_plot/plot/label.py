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
    Add title to the plot

    Parameters
    ----------
    label: string
        The text for the plot title.

    Returns
    --------
        Plot title specification.

    Note
    -----
        Changes plot title.

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
    >>> ggplot(dat, aes(x='value', group='variable', fill='variable')) +
    ... geom_bar(stat='bin', position=position_dodge(width=5.0), width=10, alpha=0.8) +
    ... ggtitle('Plot title') + xlab('x axis label') + ylab('y axis label')
    """
    return labs(title=label)


def xlab(label):
    """
    Add label to the x axis

    Parameters
    ----------
    label: string
        The text for the x axis label

    Returns
    --------
        Axis label specification.

    Note
    -----
        Changes axis label.

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
    >>> ggplot(dat, aes(x='value', group='variable', fill='variable')) +
    ... geom_bar(stat='bin', position=position_dodge(width=5.0), width=10, alpha=0.8) +
    ... ggtitle('Plot title') + xlab('x axis label') + ylab('y axis label')
    """
    return labs(x=label)


def ylab(label):
    """
    Add label to the y axis

    Parameters
    ----------
    label: string
        The text for the y axis label

    Returns
    --------
        Axis label specification.

    Note
    -----
        Changes axis label.

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
    >>> ggplot(dat, aes(x='value', group='variable', fill='variable')) +
    ... geom_bar(stat='bin', position=position_dodge(width=5.0), width=10, alpha=0.8) +
    ... ggtitle('Plot title') + xlab('x axis label') + ylab('y axis label')
    """
    return labs(y=label)


def labs(**kwargs):
    """
    Change plot title, axis labels and legend titles.

    Parameters
    ----------
    kwargs:
        A list of new names in the form aesthetic='new name',
        e.g. title='Plot title' or aes-name='Scale label'

    Returns
    --------
        Axis label specification.

    Note
    -----
        Change axis labels and legend titles.

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
    >>> ggplot(dat, aes(x='value', group='variable', fill='variable')) +
    ... geom_bar(stat='bin', position=position_dodge(width=5.0), width=10, alpha=0.8) +
    ... labs(title='New plot title', x='New x axis label', y='New y axis label')
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
