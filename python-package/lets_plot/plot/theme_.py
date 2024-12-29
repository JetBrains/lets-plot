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
    'element_text',
    'margin',
    'element_geom',
]


def theme(*,
          exponent_format=None,

          line=None,
          rect=None,
          text=None,
          title=None,
          # ToDo: aspect.ratio
          axis=None,
          axis_ontop=None, axis_ontop_x=None, axis_ontop_y=None,
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

          legend_background=None,
          legend_text=None, legend_title=None,
          legend_position=None, legend_justification=None, legend_direction=None,
          legend_margin=None,
          legend_spacing=None, legend_spacing_x=None,legend_spacing_y=None,
          legend_key=None,
          legend_key_size=None, legend_key_width=None, legend_key_height=None,
          legend_key_spacing=None, legend_key_spacing_x=None, legend_key_spacing_y=None,
          legend_box=None, legend_box_just=None, legend_box_spacing=None,
          # ToDo: other legend options...

          panel_background=None,
          panel_border=None,
          panel_border_ontop=None,
          # ToDo: other panel options...

          panel_grid=None,
          panel_grid_ontop=None,
          panel_grid_ontop_x=None,
          panel_grid_ontop_y=None,
          panel_grid_major=None,
          panel_grid_minor=None,
          panel_grid_major_x=None,
          panel_grid_minor_x=None,
          panel_grid_major_y=None,
          panel_grid_minor_y=None,
          panel_inset=None,

          plot_background=None,
          plot_title=None,
          plot_subtitle=None,
          plot_caption=None,
          plot_message=None,
          plot_margin=None,
          plot_inset=None,

          plot_title_position=None,
          plot_caption_position=None,

          strip_background=None, strip_background_x=None, strip_background_y=None,
          strip_text=None, strip_text_x=None, strip_text_y=None,
          # ToDo: strip.placement

          axis_tooltip=None, axis_tooltip_x=None, axis_tooltip_y=None,
          axis_tooltip_text=None, axis_tooltip_text_x=None, axis_tooltip_text_y=None,

          tooltip=None,
          tooltip_text=None, tooltip_title_text=None,

          label_text=None,

          geom=None
          ):
    """
    Use `theme()` to modify individual components of a theme,
    allowing you to control all non-data components of the plot.

    Parameters
    ----------
    exponent_format : {'e', 'pow', 'pow_full'} or tuple, default='e'

        Controls the appearance of numbers formatted with 'e' or 'g' types.

        Value is either a string - style, or a tuple: (style, lower_exp_bound, upper_exp_bound)
        where style can be:
        
        - 'e' : e-notation (e.g., 1e+6)
        - 'pow' : superscript powers of 10 in shortened form (e.g., 10^6)
        - 'pow_full' : superscript powers of 10 with coefficient (e.g., 1×10^6)

        For 'g' type formatting, scientific notation is applied when the number's exponent
        is less than or equal to the lower_exp_bound (-7 by default) or greater than or equal
        to the upper_exp_bound (6 by default, but can be affected by `precision` in format specifier).

         see `Formatting <https://lets-plot.org/python/pages/formats.html>`__.

        Superscript is not supported when exporting to PNG/PDF.
    line : str or dict
        All line elements.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_line()` to specify line parameters.
    rect : str or dict
        All rectangular elements.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_rect()` to specify rectangular element parameters.
    text : str or dict
        All text elements.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_text()` to specify text parameters.
    title : str or dict
        All title elements: plot, axes, legends.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_text()` to specify title text parameters, inherited from `text`.
    axis : str or dict
        All axis elements: lines, ticks, texts, titles.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_line()` to specify axes parameters.
    axis_ontop, axis_ontop_x, axis_ontop_y : bool, default=True
        Option to place axis (lines, tickmarks and labels) over the data layers.
    axis_title, axis_title_x, axis_title_y : str or dict
        Labels of axes.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_text()` to specify axes label parameters.
        `axis_title_*` inherits from `axis_title` which inherits from `text`.
    axis_text, axis_text_x, axis_text_y : str or dict
        Tick labels along axes.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_text()` to specify all axes tick label parameters.
        `axis_text_*` inherits from `axis_text` which inherits from `text`.
    axis_ticks, axis_ticks_x, axis_ticks_y : str or dict
        Tick marks along axes.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_line()` to specify all tick mark parameters.
        `axis_ticks_*` inherits from `axis_ticks` which inherits from `line`.
    axis_ticks_length, axis_ticks_length_x, axis_ticks_length_y : float
        Length of tick marks in px.
    axis_line, axis_line_x, axis_line_y : str or dict
        Lines along axes.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_line()` to specify line parameters along all axes.
        `axis_line_*` inherits from `axis_line` which inherits from `line`.
    legend_background : str or dict
        Background of legend.
        Set 'blank' or result of `element_blank()` to draw nothing.
        Set `element_rect()` to specify legend background parameters, inherited from `rect`.
    legend_text : str or dict
        Legend item labels.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_text()` to specify legend item label parameters, inherited from `text`.
    legend_title : str or dict
        Title of legend.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_text()` to specify legend title parameters, inherited from `title`.
    legend_position : {'none', 'left', 'right', 'bottom', 'top'} or list
        The position of legends. To remove the plot legend, use the 'none' value.
        If parameter is a list, then it should be a two-element numeric vector,
        specifying the position inside the plotting area,
        each value of float type between 0 and 1.
    legend_justification : str or list
        Anchor point for positioning legend. If parameter is a list, then
        it should be a two-element numeric vector. The pair [0, 0] corresponds to the
        bottom left corner, the pair [1, 1] corresponds to the top right.
        For string parameter the only possible value is 'center'.
    legend_direction : {'horizontal', 'vertical'}
        Layout of items in legends.
    legend_margin : number or list of numbers
        Margin around each legend.
        The margin may be specified using a number or a list of numbers:

        - a number or list of one number - the same margin it applied to all four sides;
        - a list of two numbers - the first margin applies to the top and bottom, the second - to the left and right;
        - a list of three numbers - the first margin applies to the top, the second - to the right and left, the third - to the bottom;
        - a list of four numbers - the margins are applied to the top, right, bottom and left in that order.

        It is acceptable to use None for any side; in this case, the default value for the legend margin side will be used.
    legend_spacing : float
        Spacing between legends.
    legend_spacing_x : float
         Spacing between legends in the horizontal direction, inherited from `legend_spacing`.
    legend_spacing_y : float
        Spacing between legends in the vertical direction, inherited from `legend_spacing`.
    legend_key : str or dict
        Background underneath legend keys.
        Set 'blank' or result of `element_blank()` to draw nothing.
        Set `element_rect()` to specify legend key background parameters, inherited from `rect`.
    legend_key_size : float
        Size of legend keys.
    legend_key_width : float
        Key background width, inherited from `legend_key_size`.
    legend_key_height : float
        Key background height, inherited from `legend_key_size`.
    legend_key_spacing : float
        Spacing between legend keys.
    legend_key_spacing_x : float
        Spacing between legend keys in the horizontal direction, inherited from `legend_key_spacing`.
    legend_key_spacing_y : float
        Spacing between legend keys in the vertical direction, inherited from `legend_key_spacing`.
    legend_box : {'horizontal', 'vertical'}
        Arrangement of multiple legends.
    legend_box_just : {'left', 'right', 'bottom', 'top', 'center'}
        Justification of each legend within the overall bounding box, when there are multiple legends.
    legend_box_spacing : float
        Spacing between plotting area and legend box.
    panel_background : str or dict
        Background of plotting area.
        Set 'blank' or result of `element_blank()` to draw nothing.
        Set `element_rect()` to specify plotting area background parameters, inherited from `rect`.
    panel_border : str or dict
        Border around plotting area.
        Set 'blank' or result of `element_blank()` to draw nothing.
        Set `element_rect()` to specify border parameters, inherited from `rect`.
    panel_border_ontop : bool, default=True
        Option to place border around plotting area over the data layers.
    panel_grid, panel_grid_major, panel_grid_minor, panel_grid_major_x, panel_grid_major_y, panel_grid_minor_x, panel_grid_minor_y : str or dict
        Grid lines. Specify major grid lines or minor grid lines separately if needed.
        Set 'blank' or result of `element_blank()` to draw nothing.
        Set `element_line()` to specify grid line parameters.
        `panel_grid_*_*` inherits from `panel_grid_*` which inherits from `panel_grid`,
        which in turn inherits from `line`.
    panel_inset : number or list of numbers
        Inset for a panel. The inset behaves like a padding for `coord_polar(transofrm_bkgr=False)` otherwise it behaves like a margin around the panel.
        The inset may be specified using a number or a list of numbers:

        - a number or list of one number - the same inset it applied to all four sides;
        - a list of two numbers - the first inset applies to the top and bottom, the second - to the left and right;
        - a list of three numbers - the first inset applies to the top, the second - to the right and left, the third - to the bottom;
        - a list of four numbers - the insets are applied to the top, right, bottom and left in that order.

        It is acceptable to use None for any side; in this case, the default value for the plot inset side will be used.
    panel_grid_ontop, panel_grid_ontop_x, panel_grid_ontop_y : bool, default=False
        Option to place major grid lines and minor grid lines over the data layers.
    plot_background : str or dict
        Background of the entire plot.
        Set 'blank' or result of `element_blank()` to draw nothing.
        Set `element_rect()` to specify plot background parameters, inherited from `rect`.
    plot_title : str or dict
        Plot title.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_text()` to specify plot title parameters, inherited from `title`.
    plot_subtitle : str or dict
        Plot subtitle.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_text()` to specify plot subtitle parameters, inherited from `plot_title` or `title`.
    plot_caption : str or dict
        Plot caption.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_text()` to specify plot caption parameters, inherited from `title`.
    plot_message : str or dict
        Plot message (e.g. sampling messages).
        Set 'blank' or result of `element_blank()` to show nothing.
        Set `element_text()` to show sampling messages (`element_text()` options don't affect a message text).
    plot_margin : number or list of numbers
        Margin around entire plot.
        The margin may be specified using a number or a list of numbers:

        - a number or list of one number - the same margin it applied to all four sides;
        - a list of two numbers - the first margin applies to the top and bottom, the second - to the left and right;
        - a list of three numbers - the first margin applies to the top, the second - to the right and left, the third - to the bottom;
        - a list of four numbers - the margins are applied to the top, right, bottom and left in that order.

        It is acceptable to use None for any side; in this case, the default value for the plot margin side will be used.
    plot_inset : number or list of numbers
        Inset for a plotting area, including the axes with their labels, but without titles.
        The inset may be specified using a number or a list of numbers:

        - a number or list of one number - the same inset it applied to all four sides;
        - a list of two numbers - the first inset applies to the top and bottom, the second - to the left and right;
        - a list of three numbers - the first inset applies to the top, the second - to the right and left, the third - to the bottom;
        - a list of four numbers - the insets are applied to the top, right, bottom and left in that order.

        It is acceptable to use None for any side; in this case, the default value for the plot inset side will be used.
    plot_title_position : {'panel', 'plot'}, default='panel'
        Alignment of the plot title/subtitle.
        A value of 'panel' means that title and subtitle are aligned to the plot panels.
        A value of 'plot' means that title and subtitle are aligned to the entire plot (excluding margins).
    plot_caption_position : {'panel', 'plot'}, default='panel'
        Alignment of the plot caption.
        A value of 'panel' means that caption is aligned to the plot panels.
        A value of 'plot' means that caption is aligned to the entire plot (excluding margins).
    strip_background : str or dict
        Background of facet labels.
        Set 'blank' or result of `element_blank()` to draw nothing.
        Set `element_rect()` to specify facet label background parameters, inherited from `rect`.
    strip_background_x : str or dict
        Horizontal facet background.
        Set 'blank' or result of `element_blank()` to draw nothing.
        Set `element_rect()` to specify facet label background parameters, inherited from `strip_background`.
    strip_background_y : str or dict
        Vertical facet background.
        Set 'blank' or result of `element_blank()` to draw nothing.
        Set `element_rect()` to specify facet label background parameters, inherited from `strip_background`.
    strip_text : str or dict
        Facet labels.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_text()` to specify facet label parameters, inherited from `text`.
    strip_text_x : str or dict
        Horizontal facet labels.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_text()` to specify facet label parameters, inherited from `strip_text`.
    strip_text_y : str or dict
        Vertical facet labels.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_text()` to specify facet label parameters, inherited from `strip_text`.
    axis_tooltip, axis_tooltip_x, axis_tooltip_y : str or dict
        Axes tooltips.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_rect()` to specify axes tooltip parameters.
        `axis_tooltip_*` inherits from `axis_tooltip` which inherits from `rect`.
    axis_tooltip_text, axis_tooltip_text_x, axis_tooltip_text_y : str or dict
        Text in axes tooltips.
        Set 'blank' or result of `element_blank()` to draw nothing and assign no space.
        Set `element_text()` to specify axes text tooltip parameters.
        `axis_tooltip_text_*` inherits from `axis_tooltip_text` which inherits from `tooltip_text`.
    tooltip : str or dict
        General tooltip.
        Set 'blank' or result of `element_blank()` to hide the tooltip (also hides side tooltips).
        Set `element_rect()` to specify tooltip rectangular parameters, inherited from `rect`.
    tooltip_text : str or dict
        Text in general tooltip.
        Set `element_text()` to specify tooltip text parameters.
    tooltip_title_text : str or dict
        Tooltip title text.
        Set `element_text()` to specify tooltip title parameters, inherited from `tooltip_text`. Bold by default.
    label_text : str or dict
        Annotation text.
        Annotations are currently supported for pie and bar charts.
        Set `element_text()` to specify annotation text parameters: font family and face, text size, text color.
    geom : dict
        Geometry colors.
        Set `element_geom()` to specify new values for the named colors.

    Returns
    -------
    `FeatureSpec`
        Theme specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11-16

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.normal(size=n)
        c = np.random.choice(['a', 'b', 'c'], size=n)
        ggplot({'x': x, 'class': c}, aes('x')) + \\
            geom_density(aes(color='class'), size=2) + \\
            ggtitle('Density of classes') + \\
            theme(axis_line=element_line(size=4), \\
                  axis_ticks_length=10, \\
                  axis_title_y='blank', \\
                  legend_position=[1, 1], legend_justification=[1, 1], \\
                  panel_background=element_rect(color='black', fill='#eeeeee', size=2), \\
                  panel_grid=element_line(color='black', size=1))

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 14-19

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        np.random.seed(42)
        p = np.random.uniform(size=7)
        x = np.random.choice(range(p.size), p=p/p.sum(), size=n)
        c = np.random.choice(['a', 'b', 'c'], p=[.5, .3, .2], size=n)
        ggplot({'x': x, 'class': c}) + \\
            geom_bar(aes('x', fill='x')) + \\
            scale_y_continuous(breaks=list(range(0, 151, 25))) + \\
            scale_fill_discrete() + \\
            facet_grid(y='class') + \\
            theme(axis_line_x='blank', \\
                  axis_ticks=element_line(color='white'), \\
                  panel_grid_major_x='blank', \\
                  strip_background=element_rect(color='black', fill='white'), \\
                  axis_tooltip=element_rect(color='black', fill='white'), \\
                  legend_position='top')

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
    Specify how non-data components of the plot are drawn.
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
        linetype=None,
        blank=False,
) -> dict:
    """
    Specify how non-data components of the plot are drawn.
    This theme element draws borders and backgrounds.

    Parameters
    ----------
    fill : str
        Fill color.
    color : str
        Border color.
    size : int
        Border size.
    linetype : int or str or list
        Type of the line. Accepts the following values:

        - Codes or names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.
        - A string of an even number (up to eight) of hexadecimal digits, specifying the lengths in consecutive positions.
        - A list defines the pattern of dashes and gaps, either with an offset: [offset, [dash, gap, ...]], or without an offset: [dash, gap, ...].

        For more info see `Line Types <https://lets-plot.org/python/pages/aesthetics.html#line-types>`__.
    blank : bool, default=False
        If True - draws nothing, and assigns no space.

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
            theme(panel_background=element_rect(fill='#f7fcf5'))

    """
    return locals()


def element_line(
        color=None,
        size=None,
        linetype=None,
        # ToDo: lineend, arrow
        blank=False,
) -> dict:
    """
    Specify how non-data components of the plot are drawn.
    This theme element draws lines.

    Parameters
    ----------
    color : str
        Line color.
    size : int
        Line size.
    linetype : int or str or list
        Type of the line. Accepts the following values:

        - Codes or names: 0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.
        - A string of an even number (up to eight) of hexadecimal digits, specifying the lengths in consecutive positions.
        - A list defines the pattern of dashes and gaps, either with an offset: [offset, [dash, gap, ...]], or without an offset: [dash, gap, ...].

        For more info see `Line Types <https://lets-plot.org/python/pages/aesthetics.html#line-types>`__.
    blank : bool, default=False
        If True - draws nothing, and assigns no space.

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
            theme(panel_grid=element_line(size=3, linetype='dashed'))

    """
    return locals()


def element_text(
        color=None,
        family=None,
        face=None,
        size=None,
        angle=None,
        # ToDo: lineheight
        hjust=None,
        vjust=None,
        margin=None,
        blank=False,
) -> dict:
    """
    Specify how non-data components of the plot are drawn.
    This theme element draws texts.

    Parameters
    ----------
    color : str
        Text color.
    family : str
        Font family.
    face : str
        Font face ("plain", "italic", "bold", "bold_italic").
    size : int
        Text size in px.
    angle : float
        Angle to rotate the text (in degrees).
    hjust : float
        Horizontal justification (in [0, 1]).
        0 - left-justified;
        1 - right-justified;
        0.5 - center-justified.
        Can be used with values out of range, but behaviour is not specified.
    vjust : float
        Vertical justification (in [0, 1]).
        0 - bottom-justified;
        1 - top-justified;
        0.5 - middle-justified.
        Can be used with values out of range, but behaviour is not specified.
    margin : number or list of numbers
        Margins around the text.

        The margin may be specified using a number or a list of numbers:
        - a number or list of one number - the same margin it applied to all four sides;
        - a list of two numbers - the first margin applies to the top and bottom, the second - to the left and right;
        - a list of three numbers -  the first margin applies to the top, the second - to the right and left,
        the third - to the bottom;
        - a list of four numbers - the margins are applied to the top, right, bottom and left in that order.

        It is acceptable to use None for any side; in this case, the default side value for this element will be used.
    blank : bool, default=False
        If True - draws nothing, and assigns no space.

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
            theme(axis_text=element_text(color='#cb181d', face='bold_italic', margin=[5, 10]))

    """
    return locals()


def margin(t=None, r=None, b=None, l=None):
    """
    Function `margin()` is deprecated.
    Please, use a number or list of numbers to specify margins (see description of the parameter used).

    """
    print("WARN: The margin() is deprecated and will be removed in future releases.\n"
          "      Please, use a number or list of numbers to specify margins (see description of the parameter used).")

    return [t, r, b, l]


def element_geom(
        pen=None,
        brush=None,
        paper=None,
        # ToDo: fatten
) -> dict:
    """
    Specify new values for the named colors.

    Parameters
    ----------
    pen : str
        Color to use by name "pen".
    brush : str
        Color to use by name "brush".
    paper : str
        Color to use by name "paper".

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
        ggplot(data, aes(x='x')) + geom_histogram(color='pen', fill='paper') + \\
            theme(geom=element_geom(pen='dark_blue', paper='light_blue'))

    """
    return locals()