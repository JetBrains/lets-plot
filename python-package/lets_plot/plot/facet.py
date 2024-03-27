#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

# Facets display subsets of the dataset in different panels.


__all__ = ['facet_grid', 'facet_wrap']


def facet_grid(x=None, y=None, *, scales=None, x_order=1, y_order=1,
               x_format=None, y_format=None,
               x_labwidth=None, y_labwidth=None):
    """
    Split data by one or two faceting variables.
    For each data subset creates a plot panel and lays out panels as grid.
    The grid columns are defined by X faceting variable and rows are defined by Y faceting variable.

    Parameters
    ----------
    x : str
        Variable name which defines columns of the facet grid.
    y : str
        Variable name which defines rows of the facet grid.
    scales : str
        Specify whether scales are shared across all facets.
        'fixed' - shared (the default), 'free' - vary across both rows and columns,
        'free_x' or 'free_y' - vary across rows or columns respectively.
    x_order : int, default=1
        Specify ordering direction of columns. 1 - ascending, -1 - descending, 0 - no ordering.
    y_order : int, default=1
        Specify ordering direction of rows. 1 - ascending, -1 - descending, 0 - no ordering.
    x_format : str
        Specify the format pattern for displaying faceting values in columns.
    y_format : str
        Specify the format pattern for displaying faceting values in rows.
    x_labwidth : int, default=None
        The maximum label length (in characters) before a line breaking is applied.
        If the original facet label already contains `\\\\n` as a text separator, the line breaking is not applied.
    y_labwidth : int, default=None
        The maximum label length (in characters) before a line breaking is applied.
        If the original facet label already contains `\\\\n` as a text separator, the line breaking is not applied.

    Returns
    -------
    `FeatureSpec`
        Facet grid specification.

    Notes
    -----
    Format pattern in the `x_format` / `y_format` parameters can be
    just a number format (like 'd') or a string template where number format
    is surrounded by curly braces: "{d} cylinders".

    For example:

    - '.2f' -> '12.45',
    - 'Score: {.2f}' -> 'Score: 12.45',
    - 'Score: {}' -> 'Score: 12.454789'.

    For more info see https://lets-plot.org/python/pages/formats.html.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.normal(size=n)
        group = np.random.choice(['a', 'b'], size=n)
        ggplot({'x': x, 'group': group}, aes(x='x')) + \\
            geom_histogram() + facet_grid(x='group')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        x = np.random.normal(size=n)
        p = [1/6, 1/3, 1/2]
        y = np.random.choice(p, size=n, p=p)
        ggplot({'x': x, 'y': y}, aes(x='x')) + \\
            geom_histogram() + \\
            facet_grid(y='y', y_order=-1, y_format='.2f')

    """
    return _facet('grid',
                  x=x, y=y,
                  scales=scales,
                  x_order=x_order, y_order=y_order,
                  x_format=x_format, y_format=y_format,
                  x_labwidth=x_labwidth, y_labwidth=y_labwidth)


def facet_wrap(facets, ncol=None, nrow=None, *, scales=None, order=1, format=None, dir="h", labwidth=None):
    """
    Split data by one or more faceting variables.
    For each data subset creates a plot panel and lays out panels
    according to the `ncol`, `nrow` and `dir` settings.

    Parameters
    ----------
    facets : str or list
        One or more faceting variable names.
    ncol : int
        Number of columns.
    nrow : int
        Number of rows.
    scales : str
        Specify whether scales are shared across all facets.
        'fixed' - shared (the default), 'free' - vary across both rows and columns,
        'free_x' or 'free_y' - vary across rows or columns respectively.
    order : int or list, default=1
        Specify ordering direction panels. 1 - ascending, -1 - descending, 0 - no ordering.
        When a list is given, then values in the list are positionally matched to variables in `facets`.
    format : str or list
        Specify the format pattern for displaying faceting values.
        The `format` values are positionally matched to variables in `facets`.
    dir : {'h', 'v'}, default='h'
        Direction: either 'h' for horizontal, or 'v' for vertical.
    labwidth : int or list
        The maximum label length (in characters) before a line breaking is applied.
        If the original facet label already contains `\\\\n` as a text separator, the line breaking is not applied.

    Returns
    -------
    `FeatureSpec`
        Facet wrap specification.

    Notes
    -----
    Format patterns in the `format` parameter can be just a number format (like 'd') or
    a string template where number format is surrounded by curly braces: "{d} cylinders".

    For example:

    - '.2f' -> '12.45',
    - 'Score: {.2f}' -> 'Score: 12.45',
    - 'Score: {}' -> 'Score: 12.454789'.

    For more info see https://lets-plot.org/python/pages/formats.html.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.normal(size=n)
        group = np.random.choice(['a', 'b'], size=n)
        ggplot({'x': x, 'group': group}, aes(x='x')) + \\
            geom_histogram() + facet_wrap(facets='group')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        x = np.random.normal(size=n)
        p = [1/6, 1/3, 1/2]
        y = np.random.choice(p, size=n, p=p)
        ggplot({'x': x, 'y': y}, aes(x='x')) + \\
            geom_histogram() + \\
            facet_wrap(facets='y', order=-1, ncol=2, dir='v', format='.2f')

    """
    return _facet('wrap',
                  facets=facets,
                  ncol=ncol, nrow=nrow,
                  scales=scales,
                  order=order,
                  format=format,
                  dir=dir,
                  labwidth=labwidth)


def _facet(name, **kwargs):
    return FeatureSpec('facet', name=name, **kwargs)
