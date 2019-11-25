#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

__all__ = ['guide_legend', 'guide_colorbar']


def guide_legend(nrow=None, ncol=None, byrow=None):
    """
    Legend guide.

    Parameters
    ----------
    nrow : int, optional
        Number of rows in legend's guide
    ncol : int, optional
        Number of columns in legend's guide
    byrow : boolean, optional
        Type of output: by row (default), or by column
     Returns
    -------
        legend guide specification
    Notes
    -----
    Legend type guide shows key (i.e., geoms) mapped onto values.

     Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from sklearn.datasets import make_blobs
    >>> X,y = make_blobs(n_samples=1000)
    >>> dat = {'x': X.T[0], 'y': X.T[1], 'variable': y}
    >>> dat = pd.DataFrame(dat)
    >>> colors = {0:'red', 1: 'blue', 2: 'green'}
    >>> dat['color'] = [colors[variable] for variable in dat['variable']]
    >>> ggplot(dat, aes(x='x', y='y')) \
    >>>         + geom_point(aes(color='color'))\
    >>>         + scale_color_manual(list(colors.values()),guide=guide_legend(ncol=3))\
    >>>         + theme(legend_position=[ 0.5,0.5])
    """
    return _guide('legend', **locals())


def guide_colorbar(barwidth=None, barheight=None, nbin=None):
    """
    Continuous color bar guide.

    Parameters
    ----------
    barwidth : value, optional
        Color bar width
    barheight : value, optional
        Color bar height
    nbin : int, optional
        Number of bins in color bar
     Returns
    -------
        color guide specification
    Notes
    -----
    Color bar guide shows continuous color scales mapped onto values.
    Color bar is available with scale_fill and scale_color.

     Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from sklearn.datasets import make_blobs
    >>> X,y = make_blobs(n_samples=1000)
    >>> dat = {'x': X.T[0], 'y': X.T[1], 'variable': y}
    >>> dat = pd.DataFrame(dat)
    >>> colors = {0:'red', 1: 'blue', 2: 'green'}
    >>> dat['color'] = [colors[variable] for variable in dat['variable']]
    >>> ggplot(dat, aes(x='x', y='y')) \
    >>>     + geom_point(aes(color='y'))\
    >>>     + scale_color_gradient(guide=guide_colorbar(nbin=10,barheight= 8, barwidth=300))\
    >>>     + theme(legend_position='top')
    """
    return _guide('colorbar', **locals())


def _guide(name, **kwargs):
    return FeatureSpec('guide', name=name, **kwargs)
