#  Copyright (c) 2024. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

#
#

from typing import List

from .scale import _is_color_scale
from .scale import scale_gradientn

try:
    import matplotlib
except ImportError:
    matplotlib = None

__all__ = ['scale_cmapmpl',
           'scale_fill_cmapmpl',
           'scale_color_cmapmpl'
           ]


def _cmapmpl_to_hex(cmap) -> List[str]:
    """
    Convert matplotlib colormap to list of hex colors.

    Parameters
    ----------
    cmap: str or matplotlib colormap object

    Returns
    -------
    List of hex color strings.
    """
    if matplotlib is None:
        raise ImportError('matplotlib is not available. Please install it first.')

    if isinstance(cmap, str):
        cmap = matplotlib.colormaps[cmap]
        if cmap is None:
            raise ValueError(f"Unknown colormap: '{cmap}'")

    if isinstance(cmap, matplotlib.colors.ListedColormap):
        # colors from a discrete colormap (like "Dark2")
        colors = cmap.colors
    else:
        # colors from a continuous colormap (like "plasma")
        # colors = cmap(range(64))
        n = 256
        values = [i / (n - 1) for i in range(n)]
        # colors = cmap(range(64))
        colors = [cmap(value) for value in values]

    return [matplotlib.colors.to_hex(c) for c in colors]


def scale_cmapmpl(aesthetic, *,
                  cmap,
                  name=None, breaks=None, labels=None, lablim=None,
                  limits=None, na_value=None, guide=None, trans=None, format=None):
    """
    Create a scale for color-related aesthetic using matplotlib colormap.

    Parameters
    ----------
    aesthetic : str or list
        The name(s) of the aesthetic(s) that this scale works with.
        Valid values are: 'color', 'fill', 'paint_a', 'paint_b', 'paint_c'.
    cmap : str or matplotlib colormap object
        The name of colormap or the colormap object to use.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    lablim : int, default=None
        The maximum label length (in characters) before trimming is applied.
    limits : list
        A numeric vector of length two providing limits of the scale.
    na_value
        Missing values will be replaced with this value.
    guide
        Guide to use for this scale. It can either be a string ('colorbar', 'legend')
        or a call to a guide function (`guide_colorbar()`, `guide_legend()`)
        specifying additional arguments. 'none' will hide the guide.
    trans : {'identity', 'log10', 'log2', 'symlog', 'sqrt', 'reverse'}
        Name of built-in transformation.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see https://lets-plot.org/python/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        The scale specification.

    Notes
    -----
    This function requires matplotlib to be installed.

    Examples
    --------

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(50))
        ggplot({'x': x}, aes(x='x')) + \\
            geom_tile(aes(color='x', fill='x')) + \\
            scale_cmapmpl(aesthetic=['color', 'fill'], cmap='plasma', breaks=[5, 25, 45]) + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    if not _is_color_scale(aesthetic):
        raise ValueError(
            f"Invalid aesthetic: {aesthetic}. Expected one of: 'color', 'fill', 'paint_a', 'paint_b', 'paint_c'")

    return scale_gradientn(aesthetic,
                           colors=_cmapmpl_to_hex(cmap),
                           name=name,
                           breaks=breaks,
                           labels=labels,
                           lablim=lablim,
                           limits=limits,
                           na_value=na_value,
                           guide=guide,
                           trans=trans,
                           format=format
                           )


def scale_fill_cmapmpl(cmap, *,
                       name=None, breaks=None, labels=None, lablim=None,
                       limits=None, na_value=None, guide=None, trans=None, format=None):
    """
    Create a scale for the 'fill' aesthetic using matplotlib colormap.

    Parameters
    ----------
    cmap : str or matplotlib colormap object
        The name of colormap or the colormap object to use.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    lablim : int, default=None
        The maximum label length (in characters) before trimming is applied.
    limits : list
        A numeric vector of length two providing limits of the scale.
    na_value
        Missing values will be replaced with this value.
    guide
        Guide to use for this scale. It can either be a string ('colorbar', 'legend')
        or a call to a guide function (`guide_colorbar()`, `guide_legend()`)
        specifying additional arguments. 'none' will hide the guide.
    trans : {'identity', 'log10', 'log2', 'symlog', 'sqrt', 'reverse'}
        Name of built-in transformation.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see https://lets-plot.org/python/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        The scale specification.

    Notes
    -----
    This function requires matplotlib to be installed.

    Examples
    --------

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(50))
        ggplot({'x': x}, aes(x='x')) + \\
            geom_tile(aes(fill='x')) + \\
            scale_fill_cmapmpl('plasma', breaks=[5, 25, 45]) + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """

    return scale_cmapmpl('fill',
                         cmap=cmap,
                         name=name,
                         breaks=breaks,
                         labels=labels,
                         lablim=lablim,
                         limits=limits,
                         na_value=na_value,
                         guide=guide,
                         trans=trans,
                         format=format)


def scale_color_cmapmpl(cmap, *,
                        name=None, breaks=None, labels=None, lablim=None, limits=None,
                        na_value=None, guide=None, trans=None, format=None):
    """
    Create a scale for the 'color' aesthetic using matplotlib colormap.

    Parameters
    ----------
    cmap : str or matplotlib colormap object
        The name of colormap or the colormap object to use.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    lablim : int, default=None
        The maximum label length (in characters) before trimming is applied.
    limits : list
        A numeric vector of length two providing limits of the scale.
    na_value
        Missing values will be replaced with this value.
    guide
        Guide to use for this scale. It can either be a string ('colorbar', 'legend')
        or a call to a guide function (`guide_colorbar()`, `guide_legend()`)
        specifying additional arguments. 'none' will hide the guide.
    trans : {'identity', 'log10', 'log2', 'symlog', 'sqrt', 'reverse'}
        Name of built-in transformation.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see https://lets-plot.org/python/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        The scale specification.

    Notes
    -----
    This function requires matplotlib to be installed.

    Examples
    --------

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(50))
        ggplot({'x': x}, aes(x='x')) + \\
            geom_tile(aes(color='x'), size=2) + \\
            scale_color_cmapmpl('plasma', breaks=[5, 25, 45]) + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """

    return scale_cmapmpl('color',
                         cmap=cmap,
                         name=name,
                         breaks=breaks,
                         labels=labels,
                         lablim=lablim,
                         limits=limits,
                         na_value=na_value,
                         guide=guide,
                         trans=trans,
                         format=format)
