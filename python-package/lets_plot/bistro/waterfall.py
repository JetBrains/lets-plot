#  Copyright (c) 2024. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from lets_plot.plot.core import PlotSpec

__all__ = ['waterfall_plot']


def waterfall_plot(data, x, y, *,
                   measure=None, group=None,
                   color=None, fill=None, size=None, alpha=None, linetype=None,
                   width=None,
                   show_legend=None, relative_tooltips=None, absolute_tooltips=None,
                   sorted_value=None, threshold=None, max_values=None,
                   calc_total=None, total_title=None,
                   hline=None, hline_ontop=None,
                   connector=None,
                   label=None, label_format=None) -> PlotSpec:
    """
    A waterfall plot shows the cumulative effect of sequentially introduced positive or negative values.

    Parameters
    ----------
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed.
    x : str
        Name of a variable. All values should be distinct.
    y : str
        Name of a numeric variable.
    measure : str
        Kind of a calculation.
        Values in 'measure' column could be:

        'absolute' - the value is shown as is;
        'relative' - the value is shown as a difference from the previous value;
        'total' - the value is shown as a cumulative sum of all previous values.

    group : str
        Grouping variable. Each group calculates its own statistics.
    color : str
        Color of the box boundary lines.
        Use 'flow_type' to color lines by the direction of the flow.
    fill : str
        Fill color of the boxes.
        Use 'flow_type' to color boxes by the direction of the flow.
    size : float, default=0.0
        Line width of the box boundary lines.
    alpha : float
        Transparency level of the boxes. Accept values between 0 and 1.
    linetype : int or str
        Type of the box boundary lines.
        Codes and names: 0 = 'blank', 1 = 'solid', 2 = 'dashed',
        3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'.
    width : float, default=0.9
        Width of the boxes. Typically range between 0 and 1.
        Values that are greater than 1 lead to overlapping of the boxes.
    show_legend : bool, default=False
        True - show the legend.
    relative_tooltips : `layer_tooltips` or str
        Tooltips for boxes with relative values.
        Result of the call to the `layer_tooltips()` function.
        Specify appearance, style and content.
        When 'none', tooltips are not shown.
        When 'detailed', a more detailed (compared to the default) version of the tooltips is shown.
    absolute_tooltips : `layer_tooltips` or str
        Tooltips for boxes with absolute values.
        Result of the call to the `layer_tooltips()` function.
        Specify appearance, style and content.
        When 'none', tooltips are not shown.
        When 'detailed', a more detailed (compared to the default) version of the tooltips is shown.
    sorted_value : bool, default=False
        Sorts categories by absolute value of the changes.
    threshold : float
        Groups all categories under a certain threshold value into "Other" category.
    max_values : int
        Groups all categories with the smallest changes, except the first `max_values`, into "Other" category.
    calc_total : bool, default=True
        Setting the `calc_total` to True will put the final cumulative sum into a new separate box.
        Taken into account only if the 'measure' column isn't provided.
    total_title : str
        The header of the last box with the final cumulative sum, if 'measure' column isn't provided.
        Also used as a title in the legend for columns of type 'total'.
    hline : str or dict
        Horizontal line passing through 0.
        Set 'blank' or result of `element_blank()` to draw nothing.
        Set `element_line()` to specify parameters.
    hline_ontop : bool, default=True
        Option to place horizontal line over the other layers.
    connector : str or dict
        Line between neighbouring boxes connecting the end of the previous box and the beginning of the next box.
        Set 'blank' or result of `element_blank()` to draw nothing.
        Set `element_line()` to specify parameters.
    label : str or dict
        Label on the box. Shows change value.
        Set 'blank' or result of `element_blank()` to draw nothing.
        Set `element_text()` to specify parameters.
        Use 'flow_type' for `color` parameter of the `element_text()` to color labels by the direction of the flow.
    label_format : str
        Format used to transform label mapping values to a string.
        Examples:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see https://lets-plot.org/python/pages/formats.html.

    Returns
    -------
    `PlotSpec`
        Plot object specification.

    Notes
    -----
    Computed variables:

    - ..x.. : category id.
    - ..xlabel.. : category name.
    - ..ymin.. : lower value of the change.
    - ..ymax.. : upper value of the change.
    - ..measure.. : kind of a calculation: absolute, relative or total.
    - ..flow_type.. : direction of the flow: increasing, decreasing, or the result (total).
    - ..initial.. : initial value of the change.
    - ..value.. : current cumsum (result of the change) or absolute value (depending on the 'measure' column).
    - ..dy.. : value of the change.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        import numpy as np
        from lets_plot import *
        from lets_plot.bistro.waterfall import *
        LetsPlot.setup_html()
        categories = list("ABCDEF")
        np.random.seed(42)
        data = {
            'x': categories,
            'y': np.random.normal(size=len(categories))
        }
        waterfall_plot(data, 'x', 'y')

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12-18

        import numpy as np
        from lets_plot import *
        from lets_plot.bistro.waterfall import *
        LetsPlot.setup_html()
        n, m = 10, 5
        categories = list(range(n))
        np.random.seed(42)
        data = {
            'x': categories,
            'y': np.random.randint(2 * m + 1, size=len(categories)) - m
        }
        waterfall_plot(data, 'x', 'y', \\
                       threshold=2, \\
                       width=.7, size=1, fill="white", color='flow_type', \\
                       hline=element_line(linetype='solid'), hline_ontop=False, \\
                       connector=element_line(linetype='dotted'), \\
                       label=element_text(color='flow_type'), \\
                       total_title="Result", show_legend=True)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11-20

        import numpy as np
        from lets_plot import *
        from lets_plot.bistro.waterfall import *
        LetsPlot.setup_html()
        categories = list("ABCDEFGHIJKLMNOP")
        np.random.seed(42)
        data = {
            'x': categories,
            'y': np.random.uniform(-1, 1, size=len(categories))
        }
        waterfall_plot(data, 'x', 'y', sorted_value=True, max_values=5, calc_total=False, \\
                       relative_tooltips=layer_tooltips().title("Category: @..xlabel..")
                                                         .format("@..initial..", ".2~f")
                                                         .format("@..value..", ".2~f")
                                                         .format("@..dy..", ".2~f")
                                                         .line("@{..flow_type..}d from @..initial.. to @..value..")
                                                         .line("Difference: @..dy..")
                                                         .disable_splitting(), \\
                       size=1, alpha=.5, \\
                       label=element_text(color="black"), label_format=".4f")

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        from lets_plot import *
        from lets_plot.bistro.waterfall import *
        LetsPlot.setup_html()
        data = {
            'company': ["Badgersoft"] * 7 + ["AIlien Co."] * 7,
            'accounts': ["initial", "revenue", "costs", "Q1", "revenue", "costs", "Q2"] * 2,
            'values': [200, 200, -100, None, 250, -100, None, \\
                       150, 50, -100, None, 100, -100, None],
            'measure': ['absolute', 'relative', 'relative', 'total', 'relative', 'relative', 'total'] * 2,
        }
        waterfall_plot(data, 'accounts', 'values', measure='measure', group='company') + \\
            facet_wrap(facets='company', scales='free_x')

    """
    return PlotSpec(data=data, mapping=None, scales=[], layers=[], bistro={
        'name': 'waterfall',
        'x': x,
        'y': y,
        'measure': measure,
        'group': group,
        'color': color,
        'fill': fill,
        'size': size,
        'alpha': alpha,
        'linetype': linetype,
        'width': width,
        'show_legend': show_legend,
        'relative_tooltips': relative_tooltips,
        'absolute_tooltips': absolute_tooltips,
        'sorted_value': sorted_value,
        'threshold': threshold,
        'max_values': max_values,
        'calc_total': calc_total,
        'total_title': total_title,
        'hline': hline,
        'hline_ontop': hline_ontop,
        'connector': connector,
        'label': label,
        'label_format': label_format,
    })