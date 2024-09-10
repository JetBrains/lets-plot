#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

__all__ = ['guide_legend', 'guide_colorbar', 'guides', 'layer_key']


def guide_legend(title=None, *, nrow=None, ncol=None, byrow=None, override_aes=None):
    """
    Legend guide.

    Parameters
    ----------
    title : str
        Title of guide.
    nrow : int
        Number of rows in legend's guide.
    ncol : int
        Number of columns in legend's guide.
    byrow : bool, default=True
        Type of output: by row, or by column.
    override_aes : dict
        Dictionary that maps aesthetic parameters to new values, overriding the default legend appearance.
        Each value can be a constant applied to all keys or a list that changes particular keys.


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
            scale_shape(guide=guide_legend(nrow=3, override_aes={'color': 'red'}))

    """
    return _guide('legend', **locals())


def guide_colorbar(title=None, *, barwidth=None, barheight=None, nbin=None):
    """
    Continuous color bar guide.

    Parameters
    ----------
    title : str
        Title of guide.
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
        Key-value pairs where the key can be:
        
        - An aesthetic name
        - 'manual' - a key referring to the default custom legend
        - A group name referring to a custom legend where the group is defined via the `layer_key()` function

        The value can be either:

        - A string ('colorbar', 'legend')
        - A call to a guide function (`guide_colorbar()`, `guide_legend()`) specifying additional arguments
        - 'none' to hide the guide

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

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 10
        np.random.seed(42)
        x = list(range(n))
        y = np.random.uniform(size=n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + \\
            geom_point(color='red', manual_key="point") + \\
            geom_line(color='blue', manual_key="line") + \\
            guides(manual=guide_legend('Zones', ncol=2))

    """
    return FeatureSpec('guides', name=None, **kwargs)


def layer_key(label, group=None, *, index=None, **kwargs):
    """
    Configure custom legend.

    Parameters
    ----------
    label : str
        Text for the element in the custom legend.
    group : str, default='manual'
        Group name by which elements are combined into a legend group.
    index : int
        Position of the element in the custom legend.
    kwargs :
        A list of aesthetic parameters to use in the custom legend.

    Returns
    -------
    `FeatureSpec`
        Custom legend specification.

    Notes
    -----
    The group name specified with the `group` parameter can be used in the `labs()` and `guides()` functions
    to further customize the display of this group (e.g. change its name).
    In particular, items in the 'manual' group will be displayed without a title unless you change it manually.

    ----

    If you set the same group and label for a legend element in different layers, they will merge into one complex legend element.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 10
        np.random.seed(42)
        x = list(range(n))
        y = np.random.uniform(size=n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + \\
            geom_point(color='red', manual_key=layer_key("point", shape=21)) + \\
            geom_line(color='blue', linetype=2, manual_key=layer_key("line", linetype=1))

    """
    return FeatureSpec('layer_key', name=None, label=label, group=group, index=index, **kwargs)
