#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec
from .util import as_boolean

#
# Scales
#

__all__ = ['scale_shape',
           'scale_color_manual', 'scale_fill_manual', 'scale_size_manual',
           'scale_shape_manual', 'scale_linetype_manual', 'scale_alpha_manual',
           'scale_fill_gradient', 'scale_fill_continuous', 'scale_color_gradient', 'scale_color_continuous',
           'scale_fill_gradient2', 'scale_color_gradient2',
           'scale_color_gradientn', 'scale_fill_gradientn',
           'scale_fill_hue', 'scale_fill_discrete', 'scale_color_hue', 'scale_color_discrete',
           'scale_fill_grey', 'scale_color_grey',
           'scale_fill_brewer', 'scale_color_brewer',
           'scale_fill_viridis', 'scale_color_viridis',
           'scale_alpha', 'scale_size', 'scale_size_area'
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
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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

def scale_color_manual(values, name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None,
                       format=None):
    """
    Create your own discrete scale for color aesthetic.

    Parameters
    ----------
    values : list of str
        A set of aesthetic values to map data values to.
        If this is a named vector, then the values will be matched based on the names.
        If unnamed, values will be matched in order (usually alphabetical)
        with the limits of the scale.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    return _scale('color',
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


def scale_fill_manual(values, name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None, format=None):
    """
    Create your own discrete scale for fill aesthetic.

    Parameters
    ----------
    values : list of str
        A set of aesthetic values to map data values to.
        If this is a named vector, then the values will be matched based on the names.
        If unnamed, values will be matched in order (usually alphabetical)
        with the limits of the scale.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    Create your own color scale for fill aesthetic. Values are strings, encoding filling colors.

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
    return _scale('fill',
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


def scale_size_manual(values, name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None, format=None):
    """
    Create your own discrete scale for size aesthetic.

    Parameters
    ----------
    values : list of str
        A set of aesthetic values to map data values to.
        If this is a named vector, then the values will be matched based on the names.
        If unnamed, values will be matched in order (usually alphabetical)
        with the limits of the scale.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    Create your own discrete scale for size aesthetic. Values are numbers, defining sizes.

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
    return _scale('size',
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


def scale_shape_manual(values, name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None,
                       format=None):
    """
    Create your own discrete scale for shape aesthetic.

    Parameters
    ----------
    values : list of str
        A set of aesthetic values to map data values to.
        If this is a named vector, then the values will be matched based on the names.
        If unnamed, values will be matched in order (usually alphabetical)
        with the limits of the scale.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    Create your own discrete scale for size aesthetic. Values are numbers, encoding shapes.

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
                  values=values)


def scale_linetype_manual(values, name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None,
                          format=None):
    """
    Create your own discrete scale for line type aesthetic.

    Parameters
    ----------
    values : list of str
        A set of aesthetic values to map data values to.
        If this is a named vector, then the values will be matched based on the names.
        If unnamed, values will be matched in order (usually alphabetical)
        with the limits of the scale.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    return _scale('linetype',
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


def scale_alpha_manual(values, name=None, breaks=None, labels=None, limits=None, na_value=None, guide=None,
                       format=None):
    """
    Create your own discrete scale for alpha (transparency) aesthetic.

    Parameters
    ----------
    values : list of str
        A set of aesthetic values to map data values to.
        If this is a named vector, then the values will be matched based on the names.
        If unnamed, values will be matched in order (usually alphabetical)
        with the limits of the scale.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    Create your own discrete scale for alpha (transparency) aesthetic.
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
    return _scale('alpha',
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


#
# Gradient (continuous) Color Scales
#

def scale_fill_gradient(low=None, high=None, name=None, breaks=None, labels=None,
                        limits=None, na_value=None, guide=None, trans=None, format=None):
    """
    Define smooth color gradient between two colors for fill aesthetic.

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
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
    return scale_fill_continuous(low, high, name, breaks, labels, limits, na_value, guide, trans, format)


def scale_fill_continuous(low=None, high=None, name=None, breaks=None, labels=None,
                          limits=None, na_value=None, guide=None, trans=None, format=None):
    """
    Define smooth color gradient between two colors for fill aesthetic.

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
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
    limits : list
        A numeric vector of length two providing limits of the scale.
    na_value
        Missing values will be replaced with this value.
    guide
        Guide to use for this scale. It can either be a string ('colorbar', 'legend')
        or a call to a guide function (`guide_colorbar()`, `guide_legend()`)
        specifying additional arguments. 'none' will hide the guide.
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
    return _scale('fill',
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


def scale_color_gradient(low=None, high=None, name=None, breaks=None, labels=None, limits=None,
                         na_value=None, guide=None, trans=None, format=None):
    """
    Define smooth color gradient between two colors for color aesthetic.

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
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
    Define smooth gradient between two colors (defined by low and high) for color aesthetic.

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
    return scale_color_continuous(low, high, name, breaks, labels, limits, na_value, guide, trans, format)


def scale_color_continuous(low=None, high=None, name=None, breaks=None, labels=None, limits=None,
                           na_value=None, guide=None, trans=None, format=None):
    """
    Define smooth color gradient between two colors for color aesthetic.

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
    breaks : list
        A numeric vector of positions (of ticks).
    labels : list of str
        A vector of labels (on ticks).
    limits : list
        A numeric vector of length two providing limits of the scale.
    na_value
        Missing values will be replaced with this value.
    guide
        Guide to use for this scale. It can either be a string ('colorbar', 'legend')
        or a call to a guide function (`guide_colorbar()`, `guide_legend()`)
        specifying additional arguments. 'none' will hide the guide.
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
    return _scale('color',
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


def scale_fill_gradient2(low=None, mid=None, high=None, midpoint=0, name=None, breaks=None, labels=None, limits=None,
                         na_value=None, guide=None, trans=None, format=None):
    """
    Define diverging color gradient for fill aesthetic.

    Parameters
    ----------
    low : str
        Color for low end of gradient.
    mid : str
        Color for mid-point.
    high : str
        Color for high end of gradient.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
    return _scale('fill',
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
                  midpoint=midpoint, scale_mapper_kind='color_gradient2')


def scale_color_gradient2(low=None, mid=None, high=None, midpoint=0, name=None, breaks=None, labels=None, limits=None,
                          na_value=None, guide=None, trans=None, format=None):
    """
    Define diverging color gradient for color aesthetic.

    Parameters
    ----------
    low : str
        Color for low end of gradient.
    mid : str
        Color for mid-point.
    high : str
        Color for high end of gradient.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
    Define diverging color gradient for color aesthetic. Default mid point is set to white color.

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
    return _scale('color',
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
                  midpoint=midpoint, scale_mapper_kind='color_gradient2')


def scale_color_gradientn(colors=None, name=None, breaks=None, labels=None, limits=None,
                          na_value=None, guide=None, trans=None, format=None):
    """
    Define smooth color gradient between multiple colors for color aesthetic.

    Parameters
    ----------
    colors : list
        Gradient colors list.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
    Define smooth color gradient between multiple colors for color aesthetic.

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
    return _scale('color',
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


def scale_fill_gradientn(colors=None, name=None, breaks=None, labels=None, limits=None,
                         na_value=None, guide=None, trans=None, format=None):
    """
    Define smooth color gradient between multiple colors for fill aesthetic.

    Parameters
    ----------
    colors : list
        Gradient colors list.
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
    Define smooth color gradient between multiple colors for fill aesthetic.

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
    return _scale('fill',
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


def scale_fill_hue(h=None, c=None, l=None, h_start=None, direction=None, name=None, breaks=None, labels=None,
                   limits=None, na_value=None, guide=None, trans=None, format=None):
    """
    Qualitative color scale with evenly spaced hues for fill aesthetic.

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
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
            scale_fill_hue(c=50, l=80, h=[0, 50]) + \\
            coord_cartesian() + \\
            ggsize(600, 200)

    """
    return _scale('fill',
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
                  direction=direction, scale_mapper_kind='color_hue')


def scale_color_hue(h=None, c=None, l=None, h_start=None, direction=None, name=None, breaks=None, labels=None,
                    limits=None, na_value=None, guide=None, trans=None, format=None):
    """
    Qualitative color scale with evenly spaced hues for color aesthetic.

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
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
    Define qualitative color scale with evenly spaced hues for color aesthetic.

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
    return _scale('color',
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
                  direction=direction, scale_mapper_kind='color_hue')


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
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    return _scale('fill',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  format=format,
                  #
                  direction=direction, discrete=True)


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
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    Define qualitative color scale with evenly spaced hues for color aesthetic.

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
    return _scale('color',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=None,
                  na_value=na_value,
                  guide=guide,
                  format=format,
                  #
                  direction=direction, discrete=True)


def scale_fill_grey(start=None, end=None, direction=None, name=None, breaks=None, labels=None, limits=None,
                    na_value=None, guide=None, trans=None, format=None):
    """
    Sequential grey color scale for fill aesthetic.
    The palette is computed using HSV (hue, saturation, value) color model.

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
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
    start, end = _greyscale_check_parameters(start, end)

    return _scale('fill',
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
                  direction=direction,
                  scale_mapper_kind='color_grey')


def scale_color_grey(start=None, end=None, direction=None, name=None, breaks=None, labels=None, limits=None,
                     na_value=None, guide=None, trans=None, format=None):
    """
    Sequential grey color scale for color aesthetic.
    The palette is computed using HSV (hue, saturation, value) color model.

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
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
    Define sequential grey color scale for color aesthetic.

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
    start, end = _greyscale_check_parameters(start, end)

    return _scale('color',
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
                  direction=direction,
                  scale_mapper_kind='color_grey')


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


def scale_fill_brewer(type=None, palette=None, direction=None, name=None, breaks=None, labels=None, limits=None,
                      na_value=None, guide=None, trans=None, format=None):
    """
    Sequential, diverging and qualitative color scales from colorbrewer2.org for fill aesthetic.
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
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
    return _scale('fill',
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
                  type=type, palette=palette,
                  direction=direction,
                  scale_mapper_kind='color_brewer')


def scale_color_brewer(type=None, palette=None, direction=None, name=None, breaks=None, labels=None, limits=None,
                       na_value=None, guide=None, trans=None, format=None):
    """
    Sequential, diverging and qualitative color scales from colorbrewer2.org for color aesthetic.
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
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
    Define sequential, diverging and qualitative color scales from colorbrewer2.org for color aesthetic.
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
    return _scale('color',
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
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
    return _scale('fill',
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
    breaks : list
        A vector specifying values to display as ticks on axis.
    labels : list of str
        A vector of labels (on ticks).
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
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
    return _scale('color',
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
    breaks : list
        A numeric vector of positions (of ticks).
    labels : list of str
        A vector of labels (on ticks).
    limits : list
        A vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        A result returned by `guide_legend()` function or 'none' to hide the guide.
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
    breaks : list
        A numeric vector of positions (of ticks).
    labels : list of str
        A vector of labels (on ticks).
    limits : list
        A vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        A result returned by `guide_legend()` function or 'none' to hide the guide.
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
    breaks : list
        A numeric vector of positions (of ticks).
    labels : list
        A vector of labels (on ticks).
    limits : list
        A vector specifying the data range for the scale
        and the default order of their display in guides.
    na_value
        Missing values will be replaced with this value.
    guide
        A result returned by `guide_legend()` function or 'none' to hide the guide.
    trans : {'identity', 'log10', 'sqrt', 'reverse'}
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
    aesthetic : str
        The name of the aesthetic that this scale works with
    name : str
        The name of the scale - used as the axis label or the legend title
    breaks : list
        A numeric vector of positions (of ticks)
    labels : list
        A vector of labels (on ticks)
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
    `FeatureSpec`
        Scale specification.

    """

    # flatten the 'other' sub-dictionary
    args = locals().copy()
    args.pop('other')
    return FeatureSpec('scale', **args, **other)
