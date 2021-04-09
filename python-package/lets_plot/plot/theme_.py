#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

__all__ = ['theme', 'element_blank']


def theme(*,
          axis_title=None, axis_title_x=None, axis_title_y=None,
          axis_text=None, axis_text_x=None, axis_text_y=None,
          axis_ticks=None, axis_ticks_x=None, axis_ticks_y=None,
          axis_line=None, axis_line_x=None, axis_line_y=None,
          legend_position=None, legend_justification=None, legend_direction=None,
          axis_tooltip=None, axis_tooltip_x=None, axis_tooltip_y=None,
          **kwargs):
    """
    Use `theme()` to modify individual components of a theme,
    allowing you to control the appearance of all non-data components of the plot.

    Parameters
    ----------
    axis_title : str or `FeatureSpec`
        Label of axes. Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
    axis_title_x : str or `FeatureSpec`
        x axis label. Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
    axis_title_y : str or `FeatureSpec`
        y axis label. Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
    axis_text : str or `FeatureSpec`
        Tick labels along axes. Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
    axis_text_x : str or `FeatureSpec`
        x axis tick labels. Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
    axis_text_y : str or `FeatureSpec`
        y axis tick labels. Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
    axis_ticks : str or `FeatureSpec`
        Tick marks along axes. Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
    axis_ticks_x : str or `FeatureSpec`
        x axis tick marks. Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
    axis_ticks_y : str or `FeatureSpec`
        y axis tick marks. Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
    axis_line : str or `FeatureSpec`
        Lines along axes. Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
    axis_line_x : str or `FeatureSpec`
        Line along x axis. Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
    axis_line_y : str or `FeatureSpec`
        Line along y axis. Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
    legend_position : {'none', 'left', 'right', 'bottom', 'top'} or list
        The position of legends. To remove the plot legend, use the 'none' value.
        If parameter is a list, then it should be a two-element numeric vector,
        each value of float type between 0 and 1.
    legend_justification : str or list
        Anchor point for positioning legend. If parameter is a list, then
        it should be a two-element numeric vector. The pair [0, 0] corresponds to the
        bottom left corner, the pair [1, 1] corresponds to the top right.
        For string parameter the only possible value is 'center'.
    legend_direction : {'horizontal', 'vertical'}
        Layout of items in legends.
    axis_tooltip : str or `FeatureSpec`
        Axes tooltips. Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
    axis_tooltip_x : str or `FeatureSpec`
        x axis tooltips. Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
    axis_tooltip_y : str or `FeatureSpec`
        y axis tooltips. Set 'blank' or result of `element_blank()` to draw nothing and assign no space.

    Returns
    -------
    `FeatureSpec`
        Theme specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11-13

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        cats = ['a', 'b', 'c', 'd', 'e']
        np.random.seed(42)
        p = np.random.uniform(size=len(cats))
        x = np.random.choice(cats, p=p/p.sum(), size=1000)
        ggplot({'x': x}, aes(x='x')) + \\
            geom_bar(aes(fill='x')) + \\
            scale_fill_discrete(name='cat') + \\
            theme(axis_title_x='blank', axis_text_x='blank', \\
                  axis_ticks_x='blank', axis_line='blank', \\
                  legend_position='bottom')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12-13

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.normal(size=n)
        y = np.random.normal(size=n)
        v = np.random.uniform(size=n)
        ggplot({'x': x, 'y': y, 'v': v}, aes('x', 'y')) + \\
            geom_point(aes(color='v')) + \\
            scale_color_gradient(name='value') + \\
            theme(axis_tooltip=element_blank(), legend_direction='horizontal', \\
                  legend_position=[1, 1], legend_justification=[1, 1])

    """
    return FeatureSpec('theme', name=None,
                       axis_title=axis_title,
                       axis_title_x=axis_title_x,
                       axis_title_y=axis_title_y,
                       axis_text=axis_text,
                       axis_text_x=axis_text_x,
                       axis_text_y=axis_text_y,
                       axis_ticks=axis_ticks,
                       axis_ticks_x=axis_ticks_x,
                       axis_ticks_y=axis_ticks_y,
                       axis_line=axis_line,
                       axis_line_x=axis_line_x,
                       axis_line_y=axis_line_y,
                       legend_position=legend_position,
                       legend_justification=legend_justification,
                       legend_direction=legend_direction,
                       axis_tooltip=axis_tooltip,
                       axis_tooltip_x=axis_tooltip_x,
                       axis_tooltip_y=axis_tooltip_y,
                       **kwargs)


def element_blank():
    """
    Specifies how non-data components of the plot are drawn.
    This theme element draws nothing, and assigns no space.

    Returns
    -------
    `FeatureSpec`
        Theme element specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        data = {'x': np.random.normal(size=1000)}
        ggplot(data, aes(x='x')) + geom_histogram() + \\
            theme(axis_title_x=element_blank(), axis_ticks=element_blank())

    """
    return FeatureSpec('theme_element', name='blank')
