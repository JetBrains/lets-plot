#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec, FeatureSpecArray
from .util import as_boolean

#
# Scales
#

__all__ = ['scale_shape',
           'scale_manual', 'scale_color_manual', 'scale_fill_manual', 'scale_size_manual',
           'scale_shape_manual', 'scale_linetype_manual', 'scale_alpha_manual',
           'scale_continuous', 'scale_fill_continuous', 'scale_color_continuous',
           'scale_gradient', 'scale_fill_gradient', 'scale_color_gradient',
           'scale_gradient2', 'scale_fill_gradient2', 'scale_color_gradient2',
           'scale_gradientn', 'scale_fill_gradientn', 'scale_color_gradientn',
           'scale_hue', 'scale_fill_hue', 'scale_color_hue',
           'scale_discrete', 'scale_fill_discrete', 'scale_color_discrete',
           'scale_grey', 'scale_fill_grey', 'scale_color_grey',
           'scale_brewer', 'scale_fill_brewer', 'scale_color_brewer',
           'scale_viridis', 'scale_fill_viridis', 'scale_color_viridis',
           'scale_alpha', 'scale_size', 'scale_size_area', 'scale_linewidth', 'scale_stroke'
           ]


def scale_shape(solid=True, name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None, format=None):
    """
    Scale for shapes.

    Parameters
    ----------
    solid : bool, default=True
        Are the shapes solid (default) True, or hollow (False).
    name : str
        The name of the scale - used as the axis label or the legend title.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        A result returned by `guide_legend()` function or 'none' to hide the guide.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Scale for shapes. A continuous variable cannot be mapped to shape.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        x = np.arange(10)
        c = np.where(x < 5, 'a', 'b')
        ggplot({'x': x, 'y': x, 'c': c}, aes('x', 'y')) + \\
            geom_point(aes(shape='c'), size=5) + \\
            scale_shape(solid=False, name='shapes')

    """
    solid = as_boolean(solid, default=True)
    return _scale('shape',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=None,
                  format=format,
                  #
                  solid=solid)


#
# Manual Scales
#

def scale_manual(aesthetic, values, *,
                 name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None, format=None):
    """
    Create your own discrete scale for the specified aesthetics.

    Parameters
    ----------
    aesthetic : str or list
        The name(s) of the aesthetic(s) that this scale works with.
    values : list of str or dict
        A set of aesthetic values to map data values to.
        If this is a list, the values will be matched in order (usually alphabetical) with the limits of the scale.
        If a dictionary, then the values will be matched based on the names.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        Guide to use for this scale. It can either be a string ('colorbar', 'legend')
        or a call to a guide function (`guide_colorbar()`, `guide_legend()`)
        specifying additional arguments. 'none' will hide the guide.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec` or `FeatureSpecArray`
        Scales specification.

    Notes
    -----
    Create your own scales for the specified aesthetics.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6-7

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(9))
        ggplot({'x': x, 'y': x}, aes('x', 'y')) + \\
            geom_point(aes(color='x', fill='x'), shape=21, size=5) + \\
            scale_manual(aesthetic=['color', 'fill'], values=['red', 'green', 'blue'], name='color', \\
                         breaks=[2, 4, 7], labels=['red', 'green', 'blue'])

    """

    # 'values' - dict of limits or breaks as keys and values as values
    if isinstance(values, dict):
        if breaks is None and limits is None:
            breaks = list(values.keys())
            values = list(values.values())
        else:
            base_order = breaks if limits is None else limits
            if isinstance(base_order, dict):
                base_order = list(base_order.values())
            new_values = [values[break_value] for break_value in base_order if break_value in values]
            if new_values:
                no_match_values = list(set(values.values()) - set(new_values))  # doesn't preserve order
                values = new_values + no_match_values
            else:
                values = None

    return _scale(aesthetic,
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=None,
                  format=format,
                  #
                  values=values)


def scale_color_manual(values, name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None,
                       format=None):
    """
    Create your own discrete scale for `color` aesthetic.

    Parameters
    ----------
    values : list of str or dict
        A set of aesthetic values to map data values to.
        If this is a list, the values will be matched in order (usually alphabetical) with the limits of the scale.
        If a dictionary, then the values will be matched based on the names.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        Guide to use for this scale. It can either be a string ('colorbar', 'legend')
        or a call to a guide function (`guide_colorbar()`, `guide_legend()`)
        specifying additional arguments. 'none' will hide the guide.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Create your own color scale. Values are strings, encoding colors.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6-7

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(9))
        ggplot({'x': x, 'y': x}, aes('x', 'y')) + \\
            geom_point(aes(color='x'), shape=1, size=5) + \\
            scale_color_manual(values=['red', 'green', 'blue'], name='color', \\
                               breaks=[2, 4, 7], labels=['red', 'green', 'blue'])

    """
    return scale_manual('color',
                        values=values,
                        name=name,
                        breaks=breaks,
                        labels=labels,
                        limits=limits,
                        na_value=na_value,
                        guide=guide,
                        format=format)


def scale_fill_manual(values, name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None, format=None):
    """
    Create your own discrete scale for `fill` aesthetic.

    Parameters
    ----------
    values : list of str or dict
        A set of aesthetic values to map data values to.
        If this is a list, the values will be matched in order (usually alphabetical) with the limits of the scale.
        If a dictionary, then the values will be matched based on the names.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        Guide to use for this scale. It can either be a string ('colorbar', 'legend')
        or a call to a guide function (`guide_colorbar()`, `guide_legend()`)
        specifying additional arguments. 'none' will hide the guide.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Create your own color scale for `fill` aesthetic. Values are strings, encoding filling colors.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6-7

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(9))
        ggplot({'x': x, 'y': x}, aes('x', 'y')) + \\
            geom_point(aes(fill='x'), shape=21, size=5, color='black') + \\
            scale_fill_manual(values=['green', 'yellow', 'red'], name='color', \\
                              breaks=[2, 4, 7], labels=['green', 'yellow', 'red'])

    """
    return scale_manual('fill',
                        values=values,
                        name=name,
                        breaks=breaks,
                        labels=labels,
                        limits=limits,
                        na_value=na_value,
                        guide=guide,
                        format=format)


def scale_size_manual(values, name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None, format=None):
    """
    Create your own discrete scale for `size` aesthetic.

    Parameters
    ----------
    values : list of str or dict
        A set of aesthetic values to map data values to.
        If this is a list, the values will be matched in order (usually alphabetical) with the limits of the scale.
        If a dictionary, then the values will be matched based on the names.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        A result returned by `guide_legend()` function or 'none' to hide the guide.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Create your own discrete scale for `size` aesthetic. Values are numbers, defining sizes.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        x = np.arange(10)
        c = np.where(x < 5, 'a', 'b')
        ggplot({'x': x, 'y': x, 'c': c}, aes('x', 'y')) + \\
            geom_point(aes(size='c'), shape=1) + \\
            scale_size_manual(name='size', values=[5, 8])

    """
    return scale_manual('size',
                        values=values,
                        name=name,
                        breaks=breaks,
                        labels=labels,
                        limits=limits,
                        na_value=na_value,
                        guide=guide,
                        format=format)


def scale_shape_manual(values, name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None,
                       format=None):
    """
    Create your own discrete scale for `shape` aesthetic.

    Parameters
    ----------
    values : list of str or dict
        A set of aesthetic values to map data values to.
        If this is a list, the values will be matched in order (usually alphabetical) with the limits of the scale.
        If a dictionary, then the values will be matched based on the names.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        A result returned by `guide_legend()` function or 'none' to hide the guide.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Create your own discrete scale for `size` aesthetic. Values are numbers, encoding shapes.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        x = np.arange(10)
        c = np.where(x < 5, 'a', 'b')
        ggplot({'x': x, 'y': x, 'c': c}, aes('x', 'y')) + \\
            geom_point(aes(shape='c'), size=5) + \\
            scale_shape_manual(values=[12, 13], name='shapes', labels=['12', '13'])

    """
    return scale_manual('shape',
                        values=values,
                        name=name,
                        breaks=breaks,
                        labels=labels,
                        limits=limits,
                        na_value=na_value,
                        guide=guide,
                        format=format)


def scale_linetype_manual(values, name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None,
                          format=None):
    """
    Create your own discrete scale for line type aesthetic.

    Parameters
    ----------
    values : list of str or dict
        A set of aesthetic values to map data values to.
        If this is a list, the values will be matched in order (usually alphabetical) with the limits of the scale.
        If a dictionary, then the values will be matched based on the names.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        A result returned by `guide_legend()` function or 'none' to hide the guide.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Create your own discrete scale for line type aesthetic.
    Values are strings or numbers, encoding linetypes.
    Available codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash',
    5 = 'longdash', 6 = 'twodash'.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5-6

        from lets_plot import *
        LetsPlot.setup_html()
        x = [-.3, -.1, .1, .3]
        ggplot() + geom_hline(aes(yintercept=x, linetype=x), size=1) + \\
            scale_linetype_manual(values=[3, 4, 5, 6], breaks=[-0.3, -0.1, 0.1, 0.3],
                                  labels=['dotted', 'dotdash', 'longdash', 'twodash'])

    """
    return scale_manual('linetype',
                        values=values,
                        name=name,
                        breaks=breaks,
                        labels=labels,
                        limits=limits,
                        na_value=na_value,
                        guide=guide,
                        format=format)


def scale_alpha_manual(values, name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None,
                       format=None):
    """
    Create your own discrete scale for `alpha` (transparency) aesthetic.

    Parameters
    ----------
    values : list of str or dict
        A set of aesthetic values to map data values to.
        If this is a list, the values will be matched in order (usually alphabetical) with the limits of the scale.
        If a dictionary, then the values will be matched based on the names.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        A result returned by `guide_legend()` function or 'none' to hide the guide.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Create your own discrete scale for `alpha` (transparency) aesthetic.
    Accept values between 0 and 1.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(10))
        ggplot({'x': x, 'y': x}, aes('x', 'y')) + \\
            geom_point(aes(alpha='x'), shape=21, size=5) + \\
            scale_alpha_manual(values=[.2, .5, .9])

    """
    return scale_manual('alpha',
                        values=values,
                        name=name,
                        breaks=breaks,
                        labels=labels,
                        limits=limits,
                        na_value=na_value,
                        guide=guide,
                        format=format)


#
# Gradient (continuous) Color Scales
#
def scale_continuous(aesthetic, *,
                     low=None, high=None, name=None, breaks=None, labels=None,
                     limits=None, na_value=None, guide=None, trans=None, format=None):
    """
    Define smooth color gradient between two colors for the specified aesthetics.

    Parameters
    ----------
    aesthetic : str or list
        The name(s) of the aesthetic(s) that this scale works with.
    low : str
        Color for low end of gradient.
    high : str
        Color for high end of gradient.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec` or `FeatureSpecArray`
        Scales specification.

    Notes
    -----
    Define smooth gradient between two colors (defined by low and high) for the specified aesthetics.

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
            scale_continuous(aesthetic=['color', 'fill'], low='#1a9641', high='#d7191c') + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return _scale(aesthetic,
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=trans,
                  format=format,
                  #
                  low=low, high=high,
                  scale_mapper_kind='color_gradient')


def scale_gradient(aesthetic, *,
                   low=None, high=None, name=None, breaks=None, labels=None,
                   limits=None, na_value=None, guide=None, trans=None, format=None):
    """
    Define smooth color gradient between two colors for the specified aesthetics.

    Parameters
    ----------
    aesthetic : str or list
        The name(s) of the aesthetic(s) that this scale works with.
    low : str
        Color for low end of gradient.
    high : str
        Color for high end of gradient.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec` or `FeatureSpecArray`
        Scales specification.

    Notes
    -----
    Define smooth gradient between two colors (defined by low and high) for the specified aesthetics.

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
            scale_gradient(aesthetic=['color', 'fill'], low='#1a9641', high='#d7191c') + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """

    return scale_continuous(aesthetic,
                            low=low, high=high,
                            name=name,
                            breaks=breaks,
                            labels=labels,
                            limits=limits,
                            na_value=na_value,
                            guide=guide,
                            trans=trans,
                            format=format)


def scale_fill_gradient(low=None, high=None, name=None, breaks=None, labels=None,
                        limits=None, na_value=None, guide=None, trans=None, format=None):
    """
    Define smooth color gradient between two colors for `fill` aesthetic.

    Parameters
    ----------
    low : str
        Color for low end of gradient.
    high : str
        Color for high end of gradient.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Define smooth gradient between two colors (defined by low and high) for filling color.

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
            scale_fill_gradient(low='#1a9641', high='#d7191c') + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return scale_gradient('fill',
                          low=low, high=high,
                          name=name,
                          breaks=breaks,
                          labels=labels,
                          limits=limits,
                          na_value=na_value,
                          guide=guide,
                          trans=trans,
                          format=format)


def scale_fill_continuous(low=None, high=None, name=None, breaks=None, labels=None,
                          limits=None, na_value=None, guide=None, trans=None, format=None):
    """
    Define smooth color gradient between two colors for `fill` aesthetic.

    Parameters
    ----------
    low : str
        Color for low end of gradient.
    high : str
        Color for high end of gradient.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Define smooth gradient between two colors (defined by low and high) for filling color.

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
            scale_fill_continuous(low='#1a9641', high='#d7191c') + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return scale_continuous('fill',
                            low=low, high=high,
                            name=name,
                            breaks=breaks,
                            labels=labels,
                            limits=limits,
                            na_value=na_value,
                            guide=guide,
                            trans=trans,
                            format=format)


def scale_color_gradient(low=None, high=None, name=None, breaks=None, labels=None, limits=None,
                         na_value=None, guide=None, trans=None, format=None):
    """
    Define smooth color gradient between two colors for `color` aesthetic.

    Parameters
    ----------
    low : str
        Color for low end of gradient.
    high : str
        Color for high end of gradient.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Define smooth gradient between two colors (defined by low and high) for `color` aesthetic.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(50))
        ggplot({'x': x}, aes(x='x')) + \\
            geom_tile(aes(color='x'), fill='white', size=3) + \\
            scale_color_gradient(low='#1a9641', high='#d7191c', guide='legend') + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return scale_gradient('color',
                          low=low, high=high,
                          name=name,
                          breaks=breaks,
                          labels=labels,
                          limits=limits,
                          na_value=na_value,
                          guide=guide,
                          trans=trans,
                          format=format)


def scale_color_continuous(low=None, high=None, name=None, breaks=None, labels=None, limits=None,
                           na_value=None, guide=None, trans=None, format=None):
    """
    Define smooth color gradient between two colors for `color` aesthetic.

    Parameters
    ----------
    low : str
        Color for low end of gradient.
    high : str
        Color for high end of gradient.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(10))
        ggplot({'x': x, 'y': x}, aes('x', 'y')) + \\
            geom_point(aes(color='x'), shape=1, size=5) + \\
            scale_color_continuous(low='#1a9641', high='#d7191c')

    """
    return scale_continuous('color',
                            low=low, high=high,
                            name=name,
                            breaks=breaks,
                            labels=labels,
                            limits=limits,
                            na_value=na_value,
                            guide=guide,
                            trans=trans,
                            format=format)


def scale_gradient2(aesthetic, *,
                    low=None, mid=None, high=None, midpoint=0, name=None, breaks=None, labels=None, limits=None,
                    na_value=None, guide=None, trans=None, format=None):
    """
    Define diverging color gradient for the specified aesthetics.

    Parameters
    ----------
    aesthetic : str or list
        The name(s) of the aesthetic(s) that this scale works with.
    low : str
        Color for low end of gradient.
    mid : str
        Color for mid-point.
    high : str
        Color for high end of gradient.
    midpoint : float, default=0.0
        The midpoint (in data value) of the diverging scale.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec` or `FeatureSpecArray`
        Scales specification.

    Notes
    -----
    Define diverging color gradient for the specified aesthetics. Default mid point is set to white color.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(-25, 26))
        ggplot({'x': x}, aes(x='x')) + \\
            geom_tile(aes(color='x', fill='x')) + \\
            scale_gradient2(aesthetic=['color', 'fill'], low='#2b83ba', mid='#ffffbf', high='#d7191c') + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return _scale(aesthetic,
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=trans,
                  format=format,
                  #
                  low=low, mid=mid, high=high,
                  midpoint=midpoint,
                  scale_mapper_kind='color_gradient2')


def scale_fill_gradient2(low=None, mid=None, high=None, midpoint=0, name=None, breaks=None, labels=None, limits=None,
                         na_value=None, guide=None, trans=None, format=None):
    """
    Define diverging color gradient for `fill` aesthetic.

    Parameters
    ----------
    low : str
        Color for low end of gradient.
    mid : str
        Color for mid-point.
    high : str
        Color for high end of gradient.
    midpoint : float, default=0.0
        The midpoint (in data value) of the diverging scale.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Define diverging color gradient for filling color. Default mid point is set to white color.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(-25, 26))
        ggplot({'x': x}, aes(x='x')) + \\
            geom_tile(aes(fill='x')) + \\
            scale_fill_gradient2(low='#2b83ba', mid='#ffffbf', high='#d7191c') + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return scale_gradient2('fill',
                           low=low, mid=mid, high=high,
                           midpoint=midpoint,
                           name=name,
                           breaks=breaks,
                           labels=labels,
                           limits=limits,
                           na_value=na_value,
                           guide=guide,
                           trans=trans,
                           format=format)


def scale_color_gradient2(low=None, mid=None, high=None, midpoint=0, name=None, breaks=None, labels=None, limits=None,
                          na_value=None, guide=None, trans=None, format=None):
    """
    Define diverging color gradient for `color` aesthetic.

    Parameters
    ----------
    low : str
        Color for low end of gradient.
    mid : str
        Color for mid-point.
    high : str
        Color for high end of gradient.
    midpoint : float, default=0.0
        The midpoint (in data value) of the diverging scale.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Define diverging color gradient for `color` aesthetic. Default mid point is set to white color.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(-25, 26))
        ggplot({'x': x}, aes(x='x')) + \\
            geom_tile(aes(color='x'), fill='white', size=3) + \\
            scale_color_gradient2(low='#2b83ba', mid='#ffffbf', high='#d7191c') + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return scale_gradient2('color',
                           low=low, mid=mid, high=high,
                           midpoint=midpoint,
                           name=name,
                           breaks=breaks,
                           labels=labels,
                           limits=limits,
                           na_value=na_value,
                           guide=guide,
                           trans=trans,
                           format=format)


def scale_gradientn(aesthetic, *,
                    colors=None, name=None, breaks=None, labels=None, limits=None,
                    na_value=None, guide=None, trans=None, format=None):
    """
    Define smooth color gradient between multiple colors for the specified aesthetics.

    Parameters
    ----------
    aesthetic : str or list
        The name(s) of the aesthetic(s) that this scale works with.
    colors : list
        Gradient colors list.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec` or `FeatureSpecArray`
        Scales specification.

    Notes
    -----
    Define smooth color gradient between multiple colors for the specified aesthetics.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(-25, 26))
        colors = ["#e41a1c", "#e41a1c", "#e41a1c", "#4daf4a", "#377eb8"]
        ggplot({'x': x}, aes(x='x')) + \\
            geom_tile(aes(color='x', fill='x'),size=3) + \\
            scale_gradientn(aesthetic=['color', 'fill'], colors=colors) + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return _scale(aesthetic,
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=trans,
                  format=format,
                  #
                  colors=colors,
                  scale_mapper_kind='color_gradientn')


def scale_color_gradientn(colors=None, name=None, breaks=None, labels=None, limits=None,
                          na_value=None, guide=None, trans=None, format=None):
    """
    Define smooth color gradient between multiple colors for `color` aesthetic.

    Parameters
    ----------
    colors : list
        Gradient colors list.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Define smooth color gradient between multiple colors for `color` aesthetic.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(-25, 26))
        colors = ["#e41a1c", "#e41a1c", "#e41a1c", "#4daf4a", "#377eb8"]
        ggplot({'x': x}, aes(x='x')) + \\
            geom_tile(aes(color='x'), fill='white', size=3) + \\
            scale_color_gradientn(colors=colors) + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return scale_gradientn('color',
                           colors=colors,
                           name=name,
                           breaks=breaks,
                           labels=labels,
                           limits=limits,
                           na_value=na_value,
                           guide=guide,
                           trans=trans,
                           format=format)


def scale_fill_gradientn(colors=None, name=None, breaks=None, labels=None, limits=None,
                         na_value=None, guide=None, trans=None, format=None):
    """
    Define smooth color gradient between multiple colors for `fill` aesthetic.

    Parameters
    ----------
    colors : list
        Gradient colors list.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Define smooth color gradient between multiple colors for `fill` aesthetic.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(-25, 26))
        colors = ["#e41a1c", "#e41a1c", "#e41a1c", "#4daf4a", "#377eb8"]
        ggplot({'x': x}, aes(x='x')) + \\
            geom_tile(aes(fill='x'), size=3) + \\
            scale_fill_gradientn(colors=colors) + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return scale_gradientn('fill',
                           colors=colors,
                           name=name,
                           breaks=breaks,
                           labels=labels,
                           limits=limits,
                           na_value=na_value,
                           guide=guide,
                           trans=trans,
                           format=format)


def scale_hue(aesthetic, *,
              h=None, c=None, l=None, h_start=None, direction=None, name=None, breaks=None, labels=None,
              limits=None, na_value=None, guide=None, trans=None, format=None):
    """
    Qualitative color scale with evenly spaced hues for the specified aesthetics.

    Parameters
    ----------
    aesthetic : str or list
        The name(s) of the aesthetic(s) that this scale works with.
    h : list
        Range of hues (two numerics), in [0, 360].
    c : int
        Chroma (intensity of color), maximum value varies depending on.
    l : int
        Luminance (lightness), in [0, 100].
    direction : {1, -1}, default=1
        Direction to travel around the color wheel, 1=clockwise, -1=counter-clockwise.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec` or `FeatureSpecArray`
        Scales specification.

    Notes
    -----
    Define qualitative color scale with evenly spaced hues for the specified aesthetics.

    Examples
    --------
      .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(50))
        ggplot({'x': x}, aes(x='x')) + \\
            geom_tile(aes(color='x', fill='x'), fill='white', size=3) + \\
            scale_hue(aesthetic=['color', 'fill'], c=20, l=90) + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return _scale(aesthetic=aesthetic,
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=trans,
                  format=format,
                  #
                  h=h, c=c, l=l, h_start=h_start,
                  direction=direction,
                  scale_mapper_kind='color_hue')


def scale_fill_hue(h=None, c=None, l=None, h_start=None, direction=None, name=None, breaks=None, labels=None,
                   limits=None, na_value=None, guide=None, trans=None, format=None):
    """
    Qualitative color scale with evenly spaced hues for `fill` aesthetic.

    Parameters
    ----------
    h : list, default=[15, 375]
        Range of hues (two numerics), in [0, 360].
    c : int, default=100
        Chroma (intensity of color), maximum value varies depending on.
    l : int, default=65
        Luminance (lightness), in [0, 100].
    h_start : int, default=0
        Hue starting point.
    direction : {1, -1}, default=1
        Direction to travel around the color wheel, 1=clockwise, -1=counter-clockwise.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Define qualitative color scale with evenly spaced hues for filling color aesthetic.

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
            scale_fill_hue(c=85) + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return scale_hue('fill',
                     h=h, c=c, l=l,
                     h_start=h_start,
                     direction=direction,
                     name=name,
                     breaks=breaks,
                     labels=labels,
                     limits=limits,
                     na_value=na_value,
                     guide=guide,
                     trans=trans,
                     format=format)


def scale_color_hue(h=None, c=None, l=None, h_start=None, direction=None, name=None, breaks=None, labels=None,
                    limits=None, na_value=None, guide=None, trans=None, format=None):
    """
    Qualitative color scale with evenly spaced hues for `color` aesthetic.

    Parameters
    ----------
    h : list
        Range of hues (two numerics), in [0, 360].
    c : int
        Chroma (intensity of color), maximum value varies depending on.
    l : int
        Luminance (lightness), in [0, 100].
    direction : {1, -1}, default=1
        Direction to travel around the color wheel, 1=clockwise, -1=counter-clockwise.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Define qualitative color scale with evenly spaced hues for `color` aesthetic.

    Examples
    --------
      .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(50))
        ggplot({'x': x}, aes(x='x')) + \\
            geom_tile(aes(color='x'), fill='white', size=3) + \\
            scale_color_hue(c=20, l=90) + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return scale_hue('color',
                     h=h, c=c, l=l,
                     h_start=h_start,
                     direction=direction,
                     name=name,
                     breaks=breaks,
                     labels=labels,
                     limits=limits,
                     na_value=na_value,
                     guide=guide,
                     trans=trans,
                     format=format)


def scale_discrete(aesthetic, *,
                   direction=None,
                   name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None, format=None):
    """
    Qualitative colors.
    Defaults to the Brewer 'Set2' palette (or 'Set3' if the categories count > 8).

    Parameters
    ----------
    aesthetic : str or list
        The name(s) of the aesthetic(s) that this scale works with.
    direction : {1, -1}, default=1
        Set the order of colors in the scale. If 1, colors are as output by brewer palette.
        If -1, the order of colors is reversed.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        A vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        Guide to use for this scale. It can either be a string ('colorbar', 'legend')
        or a call to a guide function (`guide_colorbar()`, `guide_legend()`)
        specifying additional arguments. 'none' will hide the guide.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec` or `FeatureSpecArray`
        Scales specification.

    Notes
    -----
    Define qualitative color scale with evenly spaced hues for the specified aesthetics.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(100)
        n = 50
        x = np.random.rand(n)
        y = np.random.rand(n)
        z = np.random.rand(n)
        ggplot() + geom_point(aes(x, y, color=z, fill=z), shape=21, size=4) + \\
            scale_discrete(aesthetic=['color', 'fill'], guide='none')

    """
    return _scale(aesthetic=aesthetic,
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  format=format,
                  #
                  direction=direction,
                  discrete=True)


def scale_fill_discrete(direction=None,
                        name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None, format=None):
    """
    Qualitative colors.
    Defaults to the Brewer 'Set2' palette (or 'Set3' if the categories count > 8).

    Parameters
    ----------
    direction : {1, -1}, default=1
        Set the order of colors in the scale. If 1, colors are as output by brewer palette.
        If -1, the order of colors is reversed.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        A vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        Guide to use for this scale. It can either be a string ('colorbar', 'legend')
        or a call to a guide function (`guide_colorbar()`, `guide_legend()`)
        specifying additional arguments. 'none' will hide the guide.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Define qualitative color scale with evenly spaced hues for filling color aesthetic.

    Examples
    --------
      .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(100)
        n = 50
        x = np.random.rand(n)
        y = np.random.rand(n)
        z = np.random.rand(n)
        ggplot() + geom_point(aes(x, y, fill=z), shape=21, size=4, color='gray') + \\
            scale_fill_discrete(guide='none')

    """
    return scale_discrete('fill',
                          direction=direction,
                          name=name,
                          breaks=breaks,
                          labels=labels,
                          limits=limits,
                          na_value=na_value,
                          guide=guide,
                          format=format)


def scale_color_discrete(direction=None,
                         name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None, format=None):
    """
    Qualitative colors.
    Defaults to the Brewer 'Set2' palette (or 'Set3' if the categories count > 8).

    Parameters
    ----------
    direction : {1, -1}, default=1
        Set the order of colors in the scale. If 1, colors are as output by brewer palette.
        If -1, the order of colors is reversed.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        A vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        Guide to use for this scale. It can either be a string ('colorbar', 'legend')
        or a call to a guide function (`guide_colorbar()`, `guide_legend()`)
        specifying additional arguments. 'none' will hide the guide.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Define qualitative color scale with evenly spaced hues for `color` aesthetic.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(100)
        n = 50
        x = np.random.rand(n)
        y = np.random.rand(n)
        z = np.random.rand(n)
        ggplot() + geom_point(aes(x, y, color=z), size=4) + \\
            scale_color_discrete(guide='none')

    """
    return scale_discrete('color',
                          direction=direction,
                          name=name,
                          breaks=breaks,
                          labels=labels,
                          limits=limits,
                          na_value=na_value,
                          guide=guide,
                          format=format)


def scale_grey(aesthetic, *,
               start=None, end=None, name=None, breaks=None, labels=None, limits=None,
               na_value=None, guide=None, trans=None, format=None):
    """
    Sequential grey color scale for the specified aesthetics.

    Parameters
    ----------
    aesthetic : str or list
        The name(s) of the aesthetic(s) that this scale works with.
    start : float
        Gray value at low end of palette in range [0, 1].
    end : float
        Gray value at high end of palette in range [0, 1].
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec` or `FeatureSpecArray`
        Scales specification.

    Notes
    -----
    Define sequential grey color scale for the specified aesthetics.

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
            scale_grey(aesthetic=['color', 'fill'], start=.9, end=.1) + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    start, end = _greyscale_check_parameters(start, end)

    return _scale(aesthetic=aesthetic,
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=trans,
                  format=format,
                  #
                  start=start, end=end,
                  scale_mapper_kind='color_grey')


def scale_fill_grey(start=None, end=None, name=None, breaks=None, labels=None, limits=None,
                    na_value=None, guide=None, trans=None, format=None):
    """
    Sequential grey color scale for `fill` aesthetic.

    Parameters
    ----------
    start : float
        Gray value at low end of palette in range [0, 1].
    end : float
        Gray value at high end of palette in range [0, 1].
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Define sequential grey color scale for filling color aesthetic.

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
            scale_fill_grey(start=.9, end=.1) + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return scale_grey('fill',
                      start=start, end=end,
                      name=name,
                      breaks=breaks,
                      labels=labels,
                      limits=limits,
                      na_value=na_value,
                      guide=guide,
                      trans=trans,
                      format=format)


def scale_color_grey(start=None, end=None, name=None, breaks=None, labels=None, limits=None,
                     na_value=None, guide=None, trans=None, format=None):
    """
    Sequential grey color scale for `color` aesthetic.

    Parameters
    ----------
    start : float
        Gray value at low end of palette in range [0, 1].
    end : float
        Gray value at high end of palette in range [0, 1].
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Define sequential grey color scale for `color` aesthetic.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(50))
        ggplot({'x': x}, aes(x='x')) + \\
            geom_tile(aes(color='x'), fill='white', size=3) + \\
            scale_color_grey(start=.7, end=.2) + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return scale_grey('color',
                      start=start, end=end,
                      name=name,
                      breaks=breaks,
                      labels=labels,
                      limits=limits,
                      na_value=na_value,
                      guide=guide,
                      trans=trans,
                      format=format)


def _greyscale_check_parameters(start=None, end=None):
    # Up to v.1.4.2 start/end values were in range [0,100]
    # Since v.1.4.3 start/end values are in range [0,1]
    if start != None and not (0 <= start <= 1):
        start = start / 100
        print("WARN: Value of 'start' has been scaled down to range: [0,1] : {}".format(start))

    if end != None and not (0 <= end <= 1):
        end = end / 100
        print("WARN: Value of 'end' has been scaled down to range: [0,1] : {}".format(end))

    if start != None and not (0 <= start <= 1):
        raise ValueError("Value of 'start' must be in range: [0,1] : {}".format(start))

    if end != None and not (0 <= end <= 1):
        raise ValueError("Value of 'end' must be in range: [0,1] : {}".format(end))

    return (start, end)


def scale_brewer(aesthetic, *,
                 type=None, palette=None, direction=None, name=None, breaks=None, labels=None, limits=None,
                 na_value=None, guide=None, trans=None, format=None):
    """
    Sequential, diverging and qualitative color scales from colorbrewer2.org for the specified aesthetics.
    Color schemes provided are particularly suited to display discrete values (levels of factors) on a map.

    Parameters
    ----------
    aesthetic : str or list
        The name(s) of the aesthetic(s) that this scale works with.
    type : {'seq', 'div', 'qual'}
        One of seq (sequential), div (diverging) or qual (qualitative) types of scales.
    palette : str or int
        If a string, will use that named palette. If a number, will index
        into the list of palettes of appropriate type.
    direction : {1, -1}, default=1
        Set the order of colors in the scale. If 1, colors are as output by brewer palette.
        If -1, the order of colors is reversed.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec` or `FeatureSpecArray`
        Scales specification.

    Notes
    -----
    Define sequential, diverging and qualitative color scales from colorbrewer2.org for the specified aesthetics.
    ColorBrewer provides sequential, diverging and qualitative color schemes which are particularly suited and
    tested to display discrete values (levels of a factor) on a map. It allows to smoothly interpolate 6 colors
    from any palette to a continuous scale (6 colors per palette gives nice gradients; more results in more saturated
    colors which do not look as good).

    However, the original color schemes (particularly the qualitative ones) were not intended for this and the
    perceptual result is left to the appreciation of the user. See colorbrewer2.org for more information.

    Palettes:

    - Diverging : BrBG, PiYG, PRGn, PuOr, RdBu, RdGy, RdYlBu, RdYlGn, Spectral.
    - Qualitative : Accent, Dark2, Paired, Pastel1, Pastel2, Set1, Set2, Set3.
    - Sequential : Blues, BuGn, BuPu, GnBu, Greens, Greys, Oranges, OrRd, PuBu, PuBuGn, PuRd, Purples, RdPu, Reds, YlGn, YlGnBu, YlOrBr, YlOrRd.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(9))
        ggplot({'x': x}, aes(x='x')) + \\
            geom_tile(aes(color='x', fill='x')) + \\
            scale_brewer(aesthetic=['color', 'fill'], palette='YlGnBu') + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return _scale(aesthetic=aesthetic,
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=trans,
                  format=format,
                  #
                  type=type,
                  palette=palette,
                  direction=direction,
                  scale_mapper_kind='color_brewer')


def scale_fill_brewer(type=None, palette=None, direction=None, name=None, breaks=None, labels=None, limits=None,
                      na_value=None, guide=None, trans=None, format=None):
    """
    Sequential, diverging and qualitative color scales from colorbrewer2.org for `fill` aesthetic.
    Color schemes provided are particularly suited to display discrete values (levels of factors) on a map.

    Parameters
    ----------
    type : {'seq', 'div', 'qual'}
        One of seq (sequential), div (diverging) or qual (qualitative) types of scales.
    palette : str or int
        If a string, will use that named palette. If a number, will index
        into the list of palettes of appropriate type.
    direction : {1, -1}, default=1
        Set the order of colors in the scale. If 1, colors are as output by brewer palette.
        If -1, the order of colors is reversed.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Define sequential, diverging and qualitative color scales from colorbrewer2.org for filling color aesthetic.
    ColorBrewer provides sequential, diverging and qualitative color schemes which are particularly suited and
    tested to display discrete values (levels of a factor) on a map. It allows to smoothly interpolate 6 colors
    from any palette to a continuous scale (6 colors per palette gives nice gradients; more results in more saturated
    colors which do not look as good).

    However, the original color schemes (particularly the qualitative ones) were not intended for this and the
    perceptual result is left to the appreciation of the user. See colorbrewer2.org for more information.

    Palettes:

    - Diverging : BrBG, PiYG, PRGn, PuOr, RdBu, RdGy, RdYlBu, RdYlGn, Spectral.
    - Qualitative : Accent, Dark2, Paired, Pastel1, Pastel2, Set1, Set2, Set3.
    - Sequential : Blues, BuGn, BuPu, GnBu, Greens, Greys, Oranges, OrRd, PuBu, PuBuGn, PuRd, Purples, RdPu, Reds, YlGn, YlGnBu, YlOrBr, YlOrRd.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(9))
        ggplot({'x': x}, aes(x='x')) + \\
            geom_tile(aes(fill='x')) + \\
            scale_fill_brewer(palette='YlGnBu') + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return scale_brewer('fill',
                        type=type,
                        palette=palette,
                        direction=direction,
                        name=name,
                        breaks=breaks,
                        labels=labels,
                        limits=limits,
                        na_value=na_value,
                        guide=guide,
                        trans=trans,
                        format=format)


def scale_color_brewer(type=None, palette=None, direction=None, name=None, breaks=None, labels=None, limits=None,
                       na_value=None, guide=None, trans=None, format=None):
    """
    Sequential, diverging and qualitative color scales from colorbrewer2.org for `color` aesthetic.
    Color schemes provided are particularly suited to display discrete values (levels of factors) on a map.

    Parameters
    ----------
    type : {'seq', 'div', 'qual'}
        One of seq (sequential), div (diverging) or qual (qualitative) types of scales.
    palette : str or int
        If a string, will use that named palette. If a number, will index
        into the list of palettes of appropriate type.
    direction : {1, -1}, default=1
        Set the order of colors in the scale. If 1, colors are as output by brewer palette.
        If -1, the order of colors is reversed.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Define sequential, diverging and qualitative color scales from colorbrewer2.org for `color` aesthetic.
    ColorBrewer provides sequential, diverging and qualitative color schemes which are particularly suited and
    tested to display discrete values (levels of a factor) on a map. It allows to smoothly interpolate 6 colors
    from any palette to a continuous scale (6 colors per palette gives nice gradients; more results in more saturated
    colors which do not look as good).

    However, the original color schemes (particularly the qualitative ones) were not intended for this and
    the perceptual result is left to the appreciation of the user. See colorbrewer2.org for more information.

    Palettes:

    - Diverging : BrBG, PiYG, PRGn, PuOr, RdBu, RdGy, RdYlBu, RdYlGn, Spectral.
    - Qualitative : Accent, Dark2, Paired, Pastel1, Pastel2, Set1, Set2, Set3.
    - Sequential : Blues, BuGn, BuPu, GnBu, Greens, Greys, Oranges, OrRd, PuBu, PuBuGn, PuRd, Purples, RdPu, Reds, YlGn, YlGnBu, YlOrBr, YlOrRd.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(10))
        ggplot({'x': x, 'y': x}, aes('x', 'y')) + \\
            geom_point(aes(color='x'), shape=13, size=5) + \\
            scale_color_brewer(palette='Dark2', direction=-1)

    """
    return scale_brewer('color',
                        type=type,
                        palette=palette,
                        direction=direction,
                        name=name,
                        breaks=breaks,
                        labels=labels,
                        limits=limits,
                        na_value=na_value,
                        guide=guide,
                        trans=trans,
                        format=format)


def scale_viridis(aesthetic, *,
                  alpha=None, begin=None, end=None, direction=None, option=None,
                  name=None, breaks=None, labels=None, limits=None,
                  na_value=None, guide=None, trans=None, format=None):
    """
    The `viridis` color maps are designed to be perceptually-uniform,
    both in regular form and also when converted to black-and-white.

    The `viridis` color scales are suitable for viewers with common forms of colour blindness.
    See also https://bids.github.io/colormap/.


    Parameters
    ----------
    aesthetic : str or list
        The name(s) of the aesthetic(s) that this scale works with.
    alpha : float, default=1.0
        Alpha transparency channel. (0 means transparent and 1 means opaque).
    begin : float, default=0.0
        Correspond to a color hue to start at. Accept values between 0 and 1. Should be less than `end`.
    end : float, default=1.0
        Correspond to a color hue to end with. Accept values between 0 and 1. Should be greater than `begin`.
    direction : {1, -1}, default=1
        Set the order of colors in the scale.
        If -1, the order of colors is reversed.
    option : str, default="D" (or "viridis")
        The colormap to use:
            - "magma" (or "A"),
            - "inferno" (or "B")
            - "plasma" (or "C")
            - "viridis" (or "D")
            - "cividis" (or "E")
            - "turbo"
            - "twilight"

    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec` or `FeatureSpecArray`
        Scales specification.

    Notes
    -----
    Can be used for both, continuous and discrete data.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(9))
        ggplot({'x': x}, aes(x='x')) + \\
            geom_tile(aes(color='x', fill='x')) + \\
            scale_viridis(aesthetic=['color', 'fill'], option='twilight') + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """

    return _scale(aesthetic=aesthetic,
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=trans,
                  format=format,
                  #
                  alpha=alpha,
                  begin=begin,
                  end=end,
                  direction=direction,
                  option=option,
                  scale_mapper_kind='color_cmap')


def scale_fill_viridis(alpha=None, begin=None, end=None, direction=None, option=None,
                       name=None, breaks=None, labels=None, limits=None,
                       na_value=None, guide=None, trans=None, format=None):
    """
    The `viridis` color maps are designed to be perceptually-uniform,
    both in regular form and also when converted to black-and-white.

    The `viridis` color scales are suitable for viewers with common forms of colour blindness.
    See also https://bids.github.io/colormap/.


    Parameters
    ----------
    alpha : float, default=1.0
        Alpha transparency channel. (0 means transparent and 1 means opaque).
    begin : float, default=0.0
        Correspond to a color hue to start at. Accept values between 0 and 1. Should be less than `end`.
    end : float, default=1.0
        Correspond to a color hue to end with. Accept values between 0 and 1. Should be greater than `begin`.
    direction : {1, -1}, default=1
        Set the order of colors in the scale.
        If -1, the order of colors is reversed.
    option : str, default="D" (or "viridis")
        The colormap to use:
            - "magma" (or "A"),
            - "inferno" (or "B")
            - "plasma" (or "C")
            - "viridis" (or "D")
            - "cividis" (or "E")
            - "turbo"
            - "twilight"

    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Can be used for both, continuous and discrete data.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(9))
        ggplot({'x': x}, aes(x='x')) + \\
            geom_tile(aes(fill='x')) + \\
            scale_fill_viridis(option='twilight') + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return scale_viridis('fill',
                         alpha=alpha,
                         begin=begin, end=end,
                         direction=direction,
                         option=option,
                         name=name,
                         breaks=breaks,
                         labels=labels,
                         limits=limits,
                         na_value=na_value,
                         guide=guide,
                         trans=trans,
                         format=format)


def scale_color_viridis(alpha=None, begin=None, end=None, direction=None, option=None,
                        name=None, breaks=None, labels=None, limits=None,
                        na_value=None, guide=None, trans=None, format=None):
    """
    The `viridis` color maps are designed to be perceptually-uniform,
    both in regular form and also when converted to black-and-white.

    The `viridis` color scales are suitable for viewers with common forms of colour blindness.
    See also https://bids.github.io/colormap/.


    Parameters
    ----------
    alpha : float, default=1.0
        Alpha transparency channel. (0 means transparent and 1 means opaque).
    begin : float, default=0.0
        Correspond to a color hue to start at. Accept values between 0 and 1. Should be less than `end`.
    end : float, default=1.0
        Correspond to a color hue to end with. Accept values between 0 and 1. Should be greater than `begin`.
    direction : {1, -1}, default=1
        Set the order of colors in the scale.
        If -1, the order of colors is reversed.
    option : str, default="D" (or "viridis")
        The colormap to use:
            - "magma" (or "A"),
            - "inferno" (or "B")
            - "plasma" (or "C")
            - "viridis" (or "D")
            - "cividis" (or "E")
            - "turbo"
            - "twilight"

    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale
        and the default order of their display in guides.
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
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    Can be used for both, continuous and discrete data.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(10))
        ggplot({'x': x, 'y': x}, aes('x', 'y')) + \\
            geom_point(aes(color='x'), shape=13, size=5) + \\
            scale_color_viridis(option='cividis', direction=-1)

    """
    return scale_viridis('color',
                         alpha=alpha,
                         begin=begin, end=end,
                         direction=direction,
                         option=option,
                         name=name,
                         breaks=breaks,
                         labels=labels,
                         limits=limits,
                         na_value=na_value,
                         guide=guide,
                         trans=trans,
                         format=format)

#
# Range Scale (alpha and size)
#

def scale_alpha(range=None, name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None, trans=None,
                format=None):
    """
    Scale for alpha.

    Parameters
    ----------
    range : list
        The range of the mapped aesthetics result.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        A vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        A result returned by `guide_legend()` function or 'none' to hide the guide.
    trans : {'identity', 'log10', 'log2', 'symlog', 'sqrt', 'reverse'}
        Name of built-in transformation.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(100)
        x = np.random.normal(0, 1, 1000)
        y = np.random.normal(0, 1, 1000)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + \\
            geom_point(aes(alpha='..density..'), stat='density2d', contour=False, n=30) + \\
            scale_alpha(range=[.01, .99])

    """
    return _scale('alpha',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=trans,
                  format=format,
                  #
                  range=range)


def scale_size(range=None, name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None, trans=None,
               format=None):
    """
    Scale for size.

    Parameters
    ----------
    range : list
        The range of the mapped aesthetics result.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        A vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        A result returned by `guide_legend()` function or 'none' to hide the guide.
    trans : {'identity', 'log10', 'log2', 'symlog', 'sqrt', 'reverse'}
        Name of built-in transformation.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(100)
        n = 50
        x = np.random.rand(n)
        y = np.random.rand(n)
        area = np.power(np.random.randint(30, size=n), 2)
        ggplot() + geom_point(aes(x, y, size=area), alpha=0.7) + \\
            scale_size(range=[3, 13])

    """
    return _scale('size',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=trans,
                  format=format,
                  #
                  range=range)


def scale_size_area(max_size=None, name=None, breaks=None, labels=None, limits=None,
                    na_value=None, guide=None, trans=None, format=None):
    """
    Continuous scale for size that maps 0 to 0.

    Parameters
    ----------
    max_size : float
        The max size that is mapped to.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        A vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        A result returned by `guide_legend()` function or 'none' to hide the guide.
    trans : {'identity', 'log10', 'log2', 'symlog', 'sqrt', 'reverse'}
        Name of built-in transformation.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Notes
    -----
    This method maps 0 data to 0 size. Useful in some stats such as count.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(100)
        n = 50
        x = np.random.rand(n)
        y = np.random.rand(n)
        area = np.power(np.random.uniform(30, size=n), 2)
        ggplot() + geom_point(aes(x, y, size=area), alpha=0.7) + \\
            scale_size_area(max_size=15)

    """
    return _scale('size',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=trans,
                  format=format,
                  #
                  max_size=max_size,
                  scale_mapper_kind='size_area')


def scale_linewidth(range=None, name=None, breaks=None, labels=None, limits=None,
                    na_value=None, guide=None, trans=None, format=None):
    """
    Scale for linewidth.

    Parameters
    ----------
    range : list
        The range of the mapped aesthetics result.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        A vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        A result returned by `guide_legend()` function or 'none' to hide the guide.
    trans : {'identity', 'log10', 'log2', 'symlog', 'sqrt', 'reverse'}
        Name of built-in transformation.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'x': [0, 1, 2],
            'y': [1, 2, 1],
            'w': ['a', 'b', 'c'],
        }
        ggplot(data, aes('x', 'y')) + geom_lollipop(aes(linewidth='w')) + \\
            scale_linewidth(range=[.5, 2])

    """
    return _scale('linewidth',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=trans,
                  format=format,
                  #
                  range=range)


def scale_stroke(range=None, name=None, breaks=None, labels=None, limits=None,
                 na_value=None, guide=None, trans=None, format=None):
    """
    Scale for stroke.

    Parameters
    ----------
    range : list
        The range of the mapped aesthetics result.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        A vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        A result returned by `guide_legend()` function or 'none' to hide the guide.
    trans : {'identity', 'log10', 'log2', 'symlog', 'sqrt', 'reverse'}
        Name of built-in transformation.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:
        '.2f' -> '12.45'
        'Num {}' -> 'Num 12.456789'
        'TTL: {.2f}$' -> 'TTL: 12.45$'
        For more info see https://lets-plot.org/pages/formats.html.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'x': [0, 1, 2],
            'y': [1, 2, 1],
            's': ['a', 'b', 'c'],
        }
        ggplot(data, aes('x', 'y')) + geom_lollipop(aes(stroke='s')) + \\
            scale_stroke(range=[.5, 2])

    """
    return _scale('stroke',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  trans=trans,
                  format=format,
                  #
                  range=range)


def _scale(aesthetic, *,
           name=None,
           breaks=None, labels=None,
           limits=None,
           expand=None,
           na_value=None,
           trans=None,
           guide=None,
           format=None,
           position=None,
           **other):
    """
    Create a scale (discrete or continuous)

    Parameters
    ----------
    aesthetic : str or list
        The name(s) of the aesthetic(s) that this scale works with.
    name : str
        The name of the scale - used as the axis label or the legend title
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        A numeric vector of length two providing limits of the scale.
    expand : list
        A numeric vector of length two giving multiplicative and additive expansion constants.
    na_value
        Value to use for missing values
    trans : str
        Name of built-in transformation.
    guide
        Type of legend. Use 'colorbar' for continuous color bar, or 'legend' for discrete values.
    format : str
        A string of the format for labels on the scale. Supported types are number and date/time.
    position : str
        For position scales,
        The position of the axis:
         - 'left', 'right' or 'both' for y-axis;
         - 'top', 'bottom' or 'both' for x-axis.

    Returns
    -------
    `FeatureSpec` or `FeatureSpecArray`
        Scales specification.

    """

    # flatten the 'other' sub-dictionary
    args = locals().copy()
    args.pop('other')

    # 'breaks' - dict of labels as keys and breaks as values
    if isinstance(breaks, dict):
        if labels is None:
            args['labels'] = list(breaks.keys())
        breaks = list(breaks.values())
        args['breaks'] = breaks

    # 'labels' - dict of breaks as keys and labels as values
    if isinstance(labels, dict):
        if breaks is None:
            args['breaks'] = list(labels.keys())
            args['labels'] = list(labels.values())
        else:
            new_labels = []
            new_breaks = []
            for break_value in breaks:
                if break_value in labels:
                    new_labels.append(labels[break_value])
                    new_breaks.append(break_value)

            breaks_without_label = [item for item in breaks if item not in new_breaks]  # keeps order
            args['breaks'] = new_breaks + breaks_without_label
            args['labels'] = new_labels

    specs = []
    if isinstance(aesthetic, list):
        args.pop('aesthetic')
        for aes in aesthetic:
            specs.append(FeatureSpec('scale', aesthetic=aes, **args, **other))
    else:
        specs.append(FeatureSpec('scale', **args, **other))

    if len(specs) == 1:
        return specs[0]

    return FeatureSpecArray(*specs)
