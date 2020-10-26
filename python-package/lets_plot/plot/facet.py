#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

# Facets display subsets of the dataset in different panels.


__all__ = ['facet_grid']


def facet_grid(x=None, y=None):
    """
    Lay out panels in a grid.

    Parameters
    ----------
    x : string, optional
        Feature, which defines columns of the facet grid to be displayed.
    y : string, optional
        Feature, which defines rows of the facet grid to be displayed.

    Returns
    -------
        facet grid specification

    Note
    -----
    Lay out panels in a grid.

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
    return _facet('grid', x=x, y=y)


def _facet(name, **kwargs):
    return FeatureSpec('facet', name=name, **kwargs)
