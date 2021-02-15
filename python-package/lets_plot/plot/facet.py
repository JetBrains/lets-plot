#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

# Facets display subsets of the dataset in different panels.


__all__ = ['facet_grid', 'facet_wrap']


def facet_grid(x=None, y=None, x_order=1, y_order=1, x_format=None, y_format=None):
    """
    Splits data by one or two faceting varibles.
    For each data subset creates a plot panel and lays out panels as grid.
    The grid columns are defined by X faceting variable and rows are defined by Y faceting variable.

    Parameters
    ----------
    x : string
        Variable name which defines columns of the facet grid.
    y : string
        Variable name which defines rows of the facet grid.
    x_order : int
        Specifies ordering direction of colums.
        1 - ascending, -1 - descending
    y_order : int
        Specifies ordering direction of rows.
        1 - ascending, -1 - descending
    x_format : string
        Specifies the format pattern for displaying faceting values in columns.
    y_format : string
        Specifies the format pattern for displaying faceting values in rows.

    Returns
    -------
        facet grid specification

    Note
    -----
    Format pattern in the x_format/y_format parameters can be just a number format (like "d") or
    a string template where number format is surrounded by curly braces: "{d} cylinders".

    Examples:
        '.2f' -> '12.45'
        'Score: {.2f}' -> 'Score: 12.45'
        'Score: {}' -> 'Score: 12.454789'

    For more info see the formatting reference: https://github.com/JetBrains/lets-plot/blob/master/docs/formats.md


    Examples
    ---------
    .. jupyter-execute::

        >>> import numpy as np
        >>> import pandas as pd
        >>> from scipy.stats import multivariate_normal
        >>> from scipy.stats import norm
        >>> from lets_plot import *
        >>> LetsPlot.setup_html()
        >>> mean = norm(loc=0, scale=5).rvs(size=3)
        >>> X = multivariate_normal(mean=mean, cov=0.1).rvs(1000)
        >>> df=pd.melt(pd.DataFrame(X))
        >>> ggplot(df) + geom_histogram(aes('value')) + facet_grid(y='variable')
    """
    return _facet('grid',
                  x=x, y=y,
                  x_order=x_order, y_order=y_order,
                  x_format=x_format, y_format=y_format)


def facet_wrap(facets, ncol=None, nrow=None, order=None, format=None, dir="h"):
    """
    Splits data by one or more faceting varibles.
    For each data subset creates a plot panel and lays out panels according to the `ncol`, `nrow` and `dir` settings.

    Parameters
    ----------
    facets : [string | array]
        One or more faceting variable names.
    ncol : int
        Number of columns
    nrow : int
        Number of rows
    order : [int | array]
        Specifies ordering direction panels.
        1 - ascending, -1 - descending, None - default (ascending).
        The `order` values are positionally matched to variables in `facets`.
    format : [string | array]
        Specifies the format pattern for displaying faceting values.
        The `format` values are positionally matched to variables in `facets`.
    dir : string
        Direction: either "h" for horizontal, the default, or "v", for vertical.

    Returns
    -------
        facet wrap specification

    Note
    -----
    Format patterns in the `format` parameter can be just a number format (like "d") or
    a string template where number format is surrounded by curly braces: "{d} cylinders".

    Examples:
        '.2f' -> '12.45'
        'Score: {.2f}' -> 'Score: 12.45'
        'Score: {}' -> 'Score: 12.454789'

    For more info see the formatting reference: https://github.com/JetBrains/lets-plot/blob/master/docs/formats.md


    Examples
    ---------
    TODO
    """
    return _facet('wrap',
                  facets=facets,
                  ncol=ncol, nrow=nrow,
                  order=order,
                  format=format,
                  dir=dir)


def _facet(name, **kwargs):
    return FeatureSpec('facet', name=name, **kwargs)
