#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

__all__ = [
    'theme',
    'element_blank',
    "element_line",
    'element_rect',
    'element_text'
]


def theme(*,
          line=None,
          rect=None,
          text=None,
          title=None,
          # ToDo: aspect.ratio
          axis=None,
          axis_title=None, axis_title_x=None, axis_title_y=None,
          # ToDo: axis.title.x.top, axis.title.x.bottom
          # ToDo: axis.title.y.left, axis.title.y.right
          axis_text=None, axis_text_x=None, axis_text_y=None,
          # ToDo: axis.text.x.top, axis.text.x.bottom
          # ToDo: axis.text.x.left, axis.text.x.right
          axis_ticks=None, axis_ticks_x=None, axis_ticks_y=None,
          # ToDo: axis.ticks.x.top, axis.ticks.x.bottom
          # ToDo: axis.ticks.x.left, axis.ticks.x.right
          axis_ticks_length=None, axis_ticks_length_x=None, axis_ticks_length_y=None,
          axis_line=None, axis_line_x=None, axis_line_y=None,
          # ToDo: axis.line.x.top, axis.line.x.bottom
          # ToDo: axis.line.x.left, axis.line.x.right

          legend_text=None, legend_title=None,
          legend_position=None, legend_justification=None, legend_direction=None,
          # ToDo: legend.background, etc...

          panel_background=None,
          # ToDo: panel.border, etc...

          panel_grid=None,
          panel_grid_major=None,
          panel_grid_minor=None,
          panel_grid_major_x=None,
          panel_grid_minor_x=None,
          panel_grid_major_y=None,
          panel_grid_minor_y=None,

          plot_title=None,
          # ToDo: plot_subtitle=None,
          # ToDo: plot_caption=None,
          # ToDo: plot.background, etc...

          strip_background=None,  # ToDo: x/y
          strip_text=None,  # ToDo: x/y
          # ToDo: strip.placement

          axis_tooltip=None, axis_tooltip_x=None, axis_tooltip_y=None
          ):
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

    filtered = _filter_none(locals())
    return FeatureSpec('theme', name=None, **filtered)


def _filter_none(original: dict) -> dict:
    def _filter_val(value):
        if isinstance(value, dict):
            return _filter_none(value)
        else:
            return value

    return {k: _filter_val(v) for k, v in original.items() if v is not None}


def element_blank() -> dict:
    """
    Specifies how non-data components of the plot are drawn.
    This theme element draws nothing, and assigns no space.

    Returns
    -------
    `dict`
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
    return dict(blank=True)


def element_rect(
        fill=None,
        color=None,
        size=None,
        # ToDo: linetype
        blank=False,
) -> dict:
    return locals()


def element_line(
        color=None,
        size=None,
        # ToDo: linetype, lineend, arrow
        blank=False,
) -> dict:
    return locals()


def element_text(
        color=None,
        # ToDo: family, face
        # ToDo: font_size = None,
        # ToDo: hjust, vjust, angle, lineheight, margin
        blank=False,
) -> dict:
    return locals()
