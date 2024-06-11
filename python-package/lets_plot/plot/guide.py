#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

__all__ = ['guide_legend', 'guide_colorbar', 'guides']


def guide_legend(title=None, *, nrow=None, ncol=None, byrow=None):
    """
    Legend guide.

    Parameters
    ----------
    title : str
        Title of guide.
        By default, the name of the scale object or the name specified in `labs()` is used for the title.
    nrow : int
        Number of rows in legend's guide.
    ncol : int
        Number of columns in legend's guide.
    byrow : bool, default=True
        Type of output: by row, or by column.

    Returns
    -------
    `FeatureSpec`
        Legend guide specification.

    Notes
    -----
    Legend type guide shows key (i.e., geoms) mapped onto values.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.uniform(size=n)
        y = np.random.uniform(size=n)
        c = np.random.choice(list('abcdefgh'), size=n)
        ggplot({'x': x, 'y': y, 'c': c}, aes('x', 'y')) + \\
            geom_point(aes(shape='c'), size=4, alpha=.7) + \\
            scale_shape(guide=guide_legend(nrow=3))

    """
    return _guide('legend', **locals())


def guide_colorbar(title=None, *, barwidth=None, barheight=None, nbin=None):
    """
    Continuous color bar guide.

    Parameters
    ----------
    title : str
        Title of guide.
        By default, the name of the scale object or the name specified in `labs()` is used for the title.
    barwidth : float
        Color bar width in px.
    barheight : float
        Color bar height in px.
    nbin : int
        Number of bins in color bar.

    Returns
    -------
    `FeatureSpec`
        Color guide specification.

    Notes
    -----
    Color bar guide shows continuous color scales mapped onto values.
    Color bar is available with scale_fill and scale_color.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 50
        np.random.seed(42)
        x = np.random.uniform(size=n)
        y = np.random.uniform(size=n)
        v = np.random.normal(size=n)
        ggplot({'x': x, 'y': y, 'v': v}, aes('x', 'y')) + \\
            geom_point(aes(fill='v'), size=4, shape=21, color='black') + \\
            scale_fill_gradient2(low='red', mid='yellow', high='blue', \\
                                 guide=guide_colorbar(nbin=8, barwidth=10))

    """
    return _guide('colorbar', **locals())


def _guide(name, **kwargs):
    if 'title' in kwargs and isinstance(kwargs['title'], int):
        raise ValueError("Use keyword arguments for all other than 'title' parameters.")
    return FeatureSpec('guide', name=name, **kwargs)


def guides(**kwargs):
    """
    Set guides for each scale.

    Parameters
    ----------
    kwargs
        Name-guide pairs where name should be an aesthetic.
        The guide can either be a string ('colorbar', 'legend'),
        or a call to a guide function (`guide_colorbar()`, `guide_legend()`)
        specifying additional arguments, or 'none' to hide the guide.

    Returns
    -------
    `FeatureSpec`
        Guides specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 13-14

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 25
        np.random.seed(42)
        x = np.random.uniform(size=n)
        y = np.random.uniform(size=n)
        c = np.random.choice(list('abcdefgh'), size=n)
        v = np.random.normal(size=n)
        ggplot({'x': x, 'y': y, 'c': c, 'v': v}, aes('x', 'y')) + \\
            geom_point(aes(shape='c', color='v'), size=4) + \\
            scale_color_gradient2(low='red', mid='yellow', high='blue') + \\
            guides(shape=guide_legend(ncol=2), \\
                   color=guide_colorbar(nbin=8, barwidth=20))

    """
    return FeatureSpec('guides', name=None, **kwargs)
