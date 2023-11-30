#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .scale import _scale
from .util import as_boolean

#
# Position Scales
#

__all__ = ['scale_x_discrete', 'scale_y_discrete',
           'scale_x_discrete_reversed', 'scale_y_discrete_reversed',
           'scale_x_continuous', 'scale_y_continuous',
           'scale_x_log10', 'scale_y_log10',
           'scale_x_log2', 'scale_y_log2',
           'scale_x_reverse', 'scale_y_reverse',
           'scale_x_datetime', 'scale_y_datetime',
           'scale_x_time', 'scale_y_time',
           ]


#
# Continuous Scales
#

def scale_x_continuous(name=None, *,
                       breaks=None, labels=None,
                       limits=None,
                       expand=None,
                       na_value=None,
                       trans=None,
                       format=None,
                       position=None
                       ):
    """
    Continuous position scale x.

    Parameters
    ----------
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
    expand : list
        A numeric vector of length two giving multiplicative and additive expansion constants.
        The vector size == 1 => only multiplicative expand (and additive expand by default).
        Defaults: multiplicative = 0.05, additive = 0.
    na_value
        Missing values will be replaced with this value.
    trans : {'identity', 'log10', 'log2', 'symlog', 'sqrt', 'reverse'}
        Name of built-in transformation.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see https://lets-plot.org/pages/formats.html.
    position : str
        The position of the axis:

        - 'left', 'right' or 'both' for y-axis;
        - 'top', 'bottom' or 'both' for x-axis.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7-8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        x = np.random.randint(-10, 10, size=100)
        ggplot({'x': x}, aes(x='x')) + geom_bar(stat='bin', bins=8) + \\
            scale_x_continuous(name='observations', breaks=[-9, -3, 3, 9], \\
                               limits=[-8, 11], expand=[.2], format='.1f')

    """
    return _scale('x',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=expand,
                  na_value=na_value,
                  trans=trans,
                  guide=None,
                  format=format,
                  position=position,
                  )


def scale_y_continuous(name=None, *,
                       breaks=None, labels=None,
                       limits=None,
                       expand=None,
                       na_value=None,
                       trans=None,
                       format=None,
                       position=None
                       ):
    """
    Continuous position scale y.

    Parameters
    ----------
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
    expand : list
        A numeric vector of length two giving multiplicative and additive expansion constants.
        The vector size == 1 => only multiplicative expand (and additive expand by default).
        Defaults: multiplicative = 0.05, additive = 0.
    na_value
        Missing values will be replaced with this value.
    trans : {'identity', 'log10', 'log2', 'symlog', 'sqrt', 'reverse'}
        Name of built-in transformation.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see https://lets-plot.org/pages/formats.html.
    position : str
        The position of the axis:

        - 'left', 'right' or 'both' for y-axis;
        - 'top', 'bottom' or 'both' for x-axis.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7-8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        x = np.random.randint(-10, 10, size=1000)
        ggplot({'x': x}, aes(x='x')) + geom_bar(stat='bin', bins=4) + \\
            scale_y_continuous(name='hundreds', breaks=[100, 200, 300, 400], \\
                               labels=['one', 'two', 'three', 'four'])

    """
    return _scale('y',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=expand,
                  na_value=na_value,
                  trans=trans,
                  guide=None,
                  format=format,
                  position=position,
                  )


def scale_x_log10(name=None, *,
                  breaks=None, labels=None,
                  limits=None,
                  expand=None,
                  na_value=None,
                  format=None,
                  position=None
                  ):
    """
    Continuous position scale x where trans='log10'.

    Parameters
    ----------
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
    expand : list
        A numeric vector of length two giving multiplicative and additive expansion constants.
        The vector size == 1 => only multiplicative expand (and additive expand by default).
        Defaults: multiplicative = 0.05, additive = 0.
    na_value
        Missing values will be replaced with this value.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see https://lets-plot.org/pages/formats.html.
    position : str
        The position of the axis:

        - 'left', 'right' or 'both' for y-axis;
        - 'top', 'bottom' or 'both' for x-axis.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        x = np.power(10, np.random.randint(9, size=100))
        ggplot({'x': x}, aes(x='x')) + geom_bar() + scale_x_log10()

    """
    return scale_x_continuous(name,
                              breaks=breaks,
                              labels=labels,
                              limits=limits,
                              expand=expand,
                              na_value=na_value,
                              trans='log10',
                              format=format,
                              position=position,
                              )


def scale_y_log10(name=None, *,
                  breaks=None, labels=None,
                  limits=None,
                  expand=None,
                  na_value=None,
                  format=None,
                  position=None
                  ):
    """
    Continuous position scales y where trans='log10'.

    Parameters
    ----------
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
    expand : list
        A numeric vector of length two giving multiplicative and additive expansion constants.
        The vector size == 1 => only multiplicative expand (and additive expand by default).
        Defaults: multiplicative = 0.05, additive = 0.
    na_value
        Missing values will be replaced with this value.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see https://lets-plot.org/pages/formats.html.
    position : str
        The position of the axis:

        - 'left', 'right' or 'both' for y-axis;
        - 'top', 'bottom' or 'both' for x-axis.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        x = np.random.poisson(size=100)
        ggplot({'x': x}, aes(x='x')) + geom_histogram() + scale_y_log10()

    """
    return scale_y_continuous(name,
                              breaks=breaks,
                              labels=labels,
                              limits=limits,
                              expand=expand,
                              na_value=na_value,
                              trans='log10',
                              format=format,
                              position=position,
                              )


def scale_x_log2(name=None, *,
                 breaks=None, labels=None,
                 limits=None,
                 expand=None,
                 na_value=None,
                 format=None,
                 position=None
                 ):
    """
    Continuous position scale x where trans='log2'.

    Parameters
    ----------
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
    expand : list
        A numeric vector of length two giving multiplicative and additive expansion constants.
        The vector size == 1 => only multiplicative expand (and additive expand by default).
        Defaults: multiplicative = 0.05, additive = 0.
    na_value
        Missing values will be replaced with this value.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see https://lets-plot.org/pages/formats.html.
    position : str
        The position of the axis:

        - 'left', 'right' or 'both' for y-axis;
        - 'top', 'bottom' or 'both' for x-axis.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        x = np.power(2, np.random.randint(9, size=100))
        ggplot({'x': x}, aes(x='x')) + geom_bar() + scale_x_log2()

    """
    return scale_x_continuous(name,
                              breaks=breaks,
                              labels=labels,
                              limits=limits,
                              expand=expand,
                              na_value=na_value,
                              trans='log2',
                              format=format,
                              position=position,
                              )


def scale_y_log2(name=None, *,
                 breaks=None, labels=None,
                 limits=None,
                 expand=None,
                 na_value=None,
                 format=None,
                 position=None
                 ):
    """
    Continuous position scales y where trans='log2'.

    Parameters
    ----------
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
    expand : list
        A numeric vector of length two giving multiplicative and additive expansion constants.
        The vector size == 1 => only multiplicative expand (and additive expand by default).
        Defaults: multiplicative = 0.05, additive = 0.
    na_value
        Missing values will be replaced with this value.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see https://lets-plot.org/pages/formats.html.
    position : str
        The position of the axis:

        - 'left', 'right' or 'both' for y-axis;
        - 'top', 'bottom' or 'both' for x-axis.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        x = np.random.poisson(size=100)
        ggplot({'x': x}, aes(x='x')) + geom_histogram() + scale_y_log2()

    """
    return scale_y_continuous(name,
                              breaks=breaks,
                              labels=labels,
                              limits=limits,
                              expand=expand,
                              na_value=na_value,
                              trans='log2',
                              format=format,
                              position=position,
                              )


def scale_x_reverse(name=None, *,
                    breaks=None, labels=None,
                    limits=None,
                    expand=None,
                    na_value=None,
                    format=None,
                    position=None
                    ):
    """
    Continuous position scale x where trans='reverse'.

    Parameters
    ----------
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
    expand : list
        A numeric vector of length two giving multiplicative and additive expansion constants.
        The vector size == 1 => only multiplicative expand (and additive expand by default).
        Defaults: multiplicative = 0.05, additive = 0.
    na_value
        Missing values will be replaced with this value.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see https://lets-plot.org/pages/formats.html.
    position : str
        The position of the axis:

        - 'left', 'right' or 'both' for y-axis;
        - 'top', 'bottom' or 'both' for x-axis.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(10))
        ggplot({'x': x, 'y': x}, aes('x', 'y')) + \\
            geom_point() + scale_x_reverse()

    """

    return scale_x_continuous(name,
                              breaks=breaks,
                              labels=labels,
                              limits=limits,
                              expand=expand,
                              na_value=na_value,
                              trans='reverse',
                              format=format,
                              position=position,
                              )


def scale_y_reverse(name=None, *,
                    breaks=None, labels=None,
                    limits=None,
                    expand=None,
                    na_value=None,
                    format=None,
                    position=None
                    ):
    """
    Continuous position scale y where trans='reverse'.

    Parameters
    ----------
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
    expand : list
        A numeric vector of length two giving multiplicative and additive expansion constants.
        The vector size == 1 => only multiplicative expand (and additive expand by default).
        Defaults: multiplicative = 0.05, additive = 0.
    na_value
        Missing values will be replaced with this value.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see https://lets-plot.org/pages/formats.html.
    position : str
        The position of the axis:

        - 'left', 'right' or 'both' for y-axis;
        - 'top', 'bottom' or 'both' for x-axis.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5

        from lets_plot import *
        LetsPlot.setup_html()
        x = list(range(10))
        ggplot({'x': x, 'y': x}, aes('x', 'y')) + \\
            geom_point() + scale_y_reverse(limits=[2, 6])

    """

    return scale_y_continuous(name,
                              breaks=breaks,
                              labels=labels,
                              limits=limits,
                              expand=expand,
                              na_value=na_value,
                              trans='reverse',
                              format=format,
                              position=position,
                              )


#
# Discrete Scales
#

def scale_x_discrete(name=None, *,
                     breaks=None, labels=None,
                     limits=None,
                     expand=None,
                     na_value=None,
                     reverse=None,
                     format=None,
                     position=None
                     ):
    """
    Discrete position scale x.

    Parameters
    ----------
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        A vector specifying the data range for the scale and the default order of their display in guides.
    expand : list
        A numeric vector of length two giving multiplicative and additive expansion constants.
        The vector size == 1 => only multiplicative expand (and additive expand by default).
        Defaults: multiplicative = 0, additive = 0.2.
    na_value
        Missing values will be replaced with this value.
    reverse : bool
        When True the scale is reversed.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see https://lets-plot.org/pages/formats.html.
    position : str
        The position of the axis:

        - 'left', 'right' or 'both' for y-axis;
        - 'top', 'bottom' or 'both' for x-axis.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(43)
        scores = {'rating': np.random.randint(3, 6, size=10)}
        ggplot(scores, aes(x='rating')) + geom_bar() + \\
            scale_x_discrete(name='rating', format='.1f')

    """

    reverse = as_boolean(reverse, default=False)
    return _scale('x',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=expand,
                  na_value=na_value,
                  trans=None,
                  guide=None,
                  format=format,
                  position=position,
                  #
                  discrete=True,
                  reverse=reverse
                  )


def scale_x_discrete_reversed(name=None, *,
                              breaks=None, labels=None,
                              limits=None,
                              expand=None,
                              na_value=None,
                              format=None,
                              position=None
                              ):
    """
    Reversed discrete position scale x.

    Parameters
    ----------
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        A vector specifying the data range for the scale and the default order of their display in guides.
    expand : list
        A numeric vector of length two giving multiplicative and additive expansion constants.
        The vector size == 1 => only multiplicative expand (and additive expand by default).
        Defaults: multiplicative = 0, additive = 0.2.
    na_value
        Missing values will be replaced with this value.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see https://lets-plot.org/pages/formats.html.
    position : str
        The position of the axis:

        - 'left', 'right' or 'both' for y-axis;
        - 'top', 'bottom' or 'both' for x-axis.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'time': ['Lunch', 'Dinner', 'Night'],
            'bill': [15.5, 18.13, 30],
        }
        ggplot(data, aes('time', 'bill')) + geom_bar(stat='identity') + \\
            scale_x_discrete_reversed()

    """

    return scale_x_discrete(name,
                            breaks=breaks,
                            labels=labels,
                            limits=limits,
                            expand=expand,
                            na_value=na_value,
                            format=format,
                            position=position,
                            #
                            reverse=True,
                            )


def scale_y_discrete(name=None, *,
                     breaks=None, labels=None,
                     limits=None,
                     expand=None,
                     na_value=None,
                     reverse=None,
                     format=None,
                     position=None,
                     ):
    """
    Discrete position scale y.

    Parameters
    ----------
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        A vector specifying the data range for the scale and the default order of their display in guides.
    expand : list
        A numeric vector of length two giving multiplicative and additive expansion constants.
        The vector size == 1 => only multiplicative expand (and additive expand by default).
        Defaults: multiplicative = 0, additive = 0.2.
    na_value
        Missing values will be replaced with this value.
    reverse : bool
        When True the scale is reversed.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see https://lets-plot.org/pages/formats.html.
    position : str
        The position of the axis:

        - 'left', 'right' or 'both' for y-axis;
        - 'top', 'bottom' or 'both' for x-axis.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'time': ['Breakfast', 'Lunch', 'Dinner', 'Night'],
            'bill': [3.25, 15.5, 18.3, 30],
        }
        ggplot(data, aes('bill', 'time')) + geom_point(size=5) + \\
            scale_y_discrete(limits=['Lunch', 'Dinner', 'Night'])

    """
    reverse = as_boolean(reverse, default=False)
    return _scale('y',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=expand,
                  na_value=na_value,
                  trans=None,
                  guide=None,
                  format=format,
                  position=position,
                  #
                  discrete=True,
                  reverse=reverse
                  )


def scale_y_discrete_reversed(name=None, *,
                              breaks=None, labels=None,
                              limits=None,
                              expand=None,
                              na_value=None,
                              format=None,
                              position=None
                              ):
    """
    Reversed discrete position scale y.

    Parameters
    ----------
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        A vector specifying the data range for the scale and the default order of their display in guides.
    expand : list of two numbers
        A numeric vector of length two giving multiplicative and additive expansion constants.
        The vector size == 1 => only multiplicative expand (and additive expand by default).
        Defaults: multiplicative = 0, additive = 0.2.
    na_value
        Missing values will be replaced with this value.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see https://lets-plot.org/pages/formats.html.
    position : str
        The position of the axis:

        - 'left', 'right' or 'both' for y-axis;
        - 'top', 'bottom' or 'both' for x-axis.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'time': ['Breakfast', 'Lunch', 'Dinner', 'Night'],
            'bill': [3.25, 15.5, 18.3, 30],
        }
        ggplot(data, aes('bill', 'time')) + geom_line() + \\
            scale_y_discrete_reversed()

    """

    return scale_y_discrete(name,
                            breaks=breaks,
                            labels=labels,
                            limits=limits,
                            expand=expand,
                            na_value=na_value,
                            format=format,
                            position=position,
                            #
                            reverse=True,
                            )


#
# Date-time
#


def scale_x_datetime(name=None, *,
                     breaks=None,
                     labels=None,
                     limits=None,
                     expand=None,
                     na_value=None,
                     format=None,
                     position=None
                     ):
    """
    Position scale x for date/time data.

    Parameters
    ----------
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        A vector of length two providing limits of the scale.
    expand : list
        A numeric vector of length two giving multiplicative and additive expansion constants.
        The vector size == 1 => only multiplicative expand (and additive expand by default).
        Defaults: multiplicative = 0.05, additive = 0.
    na_value
        Missing values will be replaced with this value.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:

        - '%d.%m.%y' -> '06.08.19'
        - '%B %Y' -> 'August 2019'
        - '%a, %e %b %Y %H:%M:%S' -> 'Tue, 6 Aug 2019 04:46:35'

        For more info see https://lets-plot.org/pages/formats.html.
    position : str
        The position of the axis:

        - 'left', 'right' or 'both' for y-axis;
        - 'top', 'bottom' or 'both' for x-axis.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12

        import datetime as dt
        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 31
        np.random.seed(42)
        d = [dt.datetime(2021, 1, 1) + dt.timedelta(days=d)
             for d in range(n)]
        t = np.random.normal(loc=-5, scale=6, size=n)
        ggplot({'d': d, 't': t}, aes('d', 't')) + \\
            geom_histogram(aes(fill='t'), stat='identity', color='black') + \\
            scale_x_datetime() + \\
            scale_fill_gradient2(low='#2c7bb6', high='#d7191c')

    """
    return _scale('x',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=expand,
                  na_value=na_value,
                  trans=None,
                  guide=None,
                  format=format,
                  position=position,
                  #
                  datetime=True)


def scale_y_datetime(name=None, *,
                     breaks=None, labels=None,
                     limits=None,
                     expand=None,
                     na_value=None,
                     format=None,
                     position=None
                     ):
    """
    Position scale y for date/time data.

    Parameters
    ----------
    name : str
        The name of the scale - used as the axis label or the legend title.
        If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list or dict
        A list of data values specifying the positions of ticks, or a dictionary which maps the tick labels to the breaks values.
    labels : list of str or dict
        A list of labels on ticks, or a dictionary which maps the breaks values to the tick labels.
    limits : list
        A vector of length two providing limits of the scale.
    expand : list of two numbers
        A numeric vector of length two giving multiplicative and additive expansion constants.
        The vector size == 1 => only multiplicative expand (and additive expand by default).
        Defaults: multiplicative = 0.05, additive = 0.
    na_value
        Missing values will be replaced with this value.
    format : str
        Define the format for labels on the scale. The syntax resembles Python's:

        - '%d.%m.%y' -> '06.08.19'
        - '%B %Y' -> 'August 2019'
        - '%a, %e %b %Y %H:%M:%S' -> 'Tue, 6 Aug 2019 04:46:35'

        For more info see https://lets-plot.org/pages/formats.html.
    position : str
        The position of the axis:

        - 'left', 'right' or 'both' for y-axis;
        - 'top', 'bottom' or 'both' for x-axis.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 13

        import datetime as dt
        from lets_plot import *
        LetsPlot.setup_html()
        n = 12
        rcount = lambda m: 1 if m < 2 else rcount(m - 1) + rcount(m - 2)
        data = {
            'date': [dt.datetime(2020, m, 1) for m in range(1, n + 1)],
            'rabbits count': [rcount(m) for m in range(1, n + 1)],
        }
        ggplot(data) + \\
            geom_segment(aes(x=[0] * n, y='date', xend='rabbits count', yend='date'), size=3, \\
                         tooltips=layer_tooltips().line('@|@{rabbits count}')) + \\
            scale_y_datetime(format='%b') + \\
            xlab('rabbits count')

    """
    return _scale('y',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=expand,
                  na_value=na_value,
                  trans=None,
                  guide=None,
                  format=format,
                  position=position,
                  #
                  datetime=True)


def scale_x_time(name=None, *,
                 breaks=None, labels=None,
                 limits=None,
                 expand=None,
                 na_value=None,
                 # format=None,
                 position=None
                 ):
    """
    Position scale x for data representing "time delta" values expressed in milliseconds.

    Parameters
    ----------
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
    expand : list
        A numeric vector of length two giving multiplicative and additive expansion constants.
        The vector size == 1 => only multiplicative expand (and additive expand by default).
        Defaults: multiplicative = 0.05, additive = 0.
    na_value
        Missing values will be replaced with this value.
    position : str
        The position of the axis:

        - 'left', 'right' or 'both' for y-axis;
        - 'top', 'bottom' or 'both' for x-axis.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 13

        import datetime as dt
        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 31
        np.random.seed(42)
        data = {
            'time': [dt.timedelta(days=v).total_seconds() * 1000 for v in range(n)],
            'value': np.random.normal(loc=-5, scale=6, size=n)
        }
        ggplot(data) + \\
            geom_line(aes('time', 'value')) + \\
            scale_x_time()

    """
    return _scale('x',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=expand,
                  na_value=na_value,
                  trans=None,
                  guide=None,
                  # format=format,
                  position=position,
                  #
                  time=True)


def scale_y_time(name=None, *,
                 breaks=None, labels=None,
                 limits=None,
                 expand=None,
                 na_value=None,
                 # format=None,
                 position=None
                 ):
    """
    Position scale y for data representing "time delta" values expressed in milliseconds.

    Parameters
    ----------
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
    expand : list
        A numeric vector of length two giving multiplicative and additive expansion constants.
        The vector size == 1 => only multiplicative expand (and additive expand by default).
        Defaults: multiplicative = 0.05, additive = 0.
    na_value
        Missing values will be replaced with this value.
    position : str
        The position of the axis:

        - 'left', 'right' or 'both' for y-axis;
        - 'top', 'bottom' or 'both' for x-axis.

    Returns
    -------
    `FeatureSpec`
        Scale specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 13

        import datetime as dt
        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 31
        np.random.seed(42)
        data = {
            'time': [dt.timedelta(days=v).total_seconds() * 1000 for v in range(n)],
            'value': np.random.normal(loc=-5, scale=6, size=n)
        }
        ggplot(data) + \\
            geom_line(aes('value', 'time')) + \\
            scale_y_time()

    """
    return _scale('y',
                  name=name,
                  breaks=breaks,
                  labels=labels,
                  limits=limits,
                  expand=expand,
                  na_value=na_value,
                  trans=None,
                  guide=None,
                  # format=format,
                  position=position,
                  #
                  time=True)
