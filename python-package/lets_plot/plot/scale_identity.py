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


def scale_color_identity(name=None, breaks=None, labels=None, limits=None, na_value=None, guide='none'):
    """
    Use this scale when your data has already been scaled.
    I.e. it already represents aesthetic values that ggplot2 can handle directly.
    This will not produce a legend unless you also supply the breaks and labels.

    Parameters
    ----------
    name : string
        The name of the scale - used as the axis label or the legend title
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    guide :
        Guide to use for this scale. Defaults to "none".

    Returns
    -------
        scale specification

    Note
    -----
        Input data expected: list of strings containing
            a) names of colors (i.e. 'green')
            b) hex codes of colors (i.e 'x00ff00')
            c) css colors (i.e 'rgb(0,255,0)')

    Examples
    ---------
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> x = [0, 1, 2]
    >>> y = x
    >>> c = ['red', 'green', 'blue']
    >>> dat = pd.DataFrame({'x': x, 'y': y, 'c': c})
    >>> ggplot(dat, aes('x', 'y', color='c')) + geom_point(size=15) + scale_color_identity()
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
                  #
                  scale_mapper_kind='identity')


def scale_fill_identity(name=None, breaks=None, labels=None, limits=None, na_value=None, guide='none'):
    """
    Use this scale when your data has already been scaled.
    I.e. it already represents aesthetic values that ggplot2 can handle directly.
    This will not produce a legend unless you also supply the breaks and labels.

    Parameters
    ----------
    name : string
        The name of the scale - used as the axis label or the legend title
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    guide :
        Guide to use for this scale. Defaults to "none".

    Returns
    -------
        scale specification

    Note
    -----
        Input data expected: list of strings containing
            a) names of colors (i.e. 'green')
            b) hex codes of colors (i.e 'x00ff00')
            c) css colors (i.e 'rgb(0,255,0)')

    Examples
    ---------
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> x = [0, 1, 2]
    >>> y = x
    >>> c = ['red', 'green', 'blue']
    >>> dat = pd.DataFrame({'x': x, 'y': y, 'c': c})
    >>> ggplot(dat, aes('x', 'y', fill='c')) + geom_tile() + scale_fill_identity()
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
                  #
                  scale_mapper_kind='identity')


def scale_shape_identity(name=None, breaks=None, labels=None, limits=None, na_value=None, guide='none'):
    """
    Use this scale when your data has already been scaled.
    I.e. it already represents aesthetic values that ggplot2 can handle directly.
    This will not produce a legend unless you also supply the breaks and labels.

    Parameters
    ----------
    name : string
        The name of the scale - used as the axis label or the legend title
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    guide :
        Guide to use for this scale. Defaults to "none".

    Returns
    -------
        scale specification

    Note
    -----
        Input data expected: numetic codes of shapes.

    Examples
    ---------
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> x = range(7)
    >>> y = x
    >>> s = range(7)
    >>> dat = pd.DataFrame({'x': x, 'y': y, 's': s})
    >>> ggplot(dat, aes('x', 'y', shape='s')) + geom_point(size=5) + scale_shape_identity()
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
                  #
                  solid=None,
                  scale_mapper_kind='identity', discrete=True)


def scale_linetype_identity(name=None, breaks=None, labels=None, limits=None, na_value=None, guide='none'):
    """
    Use this scale when your data has already been scaled.
    I.e. it already represents aesthetic values that ggplot2 can handle directly.
    This will not produce a legend unless you also supply the breaks and labels.

    Parameters
    ----------
    name : string
        The name of the scale - used as the axis label or the legend title
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    guide :
        Guide to use for this scale. Defaults to "none".

    Returns
    -------
        scale specification

    Note
    -----
        Input data expected: numetic codes or names of line types (i.e 'dotdash').
        The codes are: 0 = blank, 1 = solid, 2 = dashed, 3 = dotted, 4 = dotdash, 5 = longdash, 6 = twodash

    Examples
    ---------
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> x = range(6)
    >>> xend = [v + 2 for v in x]
    >>> y = x
    >>> l = range(1,7)
    >>> dat = pd.DataFrame({'x': x, 'y': y, 'xend':xend, 'l': l})
    >>> ggplot(dat, aes('x', 'y', xend='xend', yend='y', linetype='l')) +
    ... geom_segment(size=2) + scale_linetype_identity()
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
                  #
                  scale_mapper_kind='identity', discrete=True)


def scale_alpha_identity(name=None, breaks=None, labels=None, limits=None, na_value=None, guide='none'):
    """
    Use this scale when your data has already been scaled.
    I.e. it already represents aesthetic values that ggplot2 can handle directly.
    This will not produce a legend unless you also supply the breaks and labels.

    Parameters
    ----------
    name : string
        The name of the scale - used as the axis label or the legend title
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    guide :
        Guide to use for this scale. Defaults to "none".

    Returns
    -------
        scale specification

    Note
    -----
        Input data expected: numetic values in range [0..1]

    Examples
    ---------
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> x = [0,1,2]
    >>> y = x
    >>> a = [.3, .5, 0.8]
    >>> dat = pd.DataFrame({'x': x, 'y': y, 'a': a})
    >>> ggplot(dat, aes('x', 'y', alpha='a')) + geom_point(size=15) + scale_alpha_identity()
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
                  #
                  scale_mapper_kind='identity')


def scale_size_identity(name=None, breaks=None, labels=None, limits=None, na_value=None, guide='none'):
    """
    Use this scale when your data has already been scaled.
    I.e. it already represents aesthetic values that ggplot2 can handle directly.
    This will not produce a legend unless you also supply the breaks and labels.

    Parameters
    ----------
    name : string
        The name of the scale - used as the axis label or the legend title
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    guide :
        Guide to use for this scale. Defaults to "none".

    Returns
    -------
        scale specification

    Note
    -----
        Input data expected: positive numetic values

    Examples
    ---------
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> x = [0,1,2]
    >>> y = x
    >>> s = [3, 9, 18]
    >>> dat = pd.DataFrame({'x': x, 'y': y, 's': s})
    >>> ggplot(dat, aes('x', 'y', size='s')) + geom_point() + scale_size_identity()
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
                  #
                  scale_mapper_kind='identity')
