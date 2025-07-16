#  Copyright (c) 2024. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from lets_plot.plot.core import PlotSpec, LayerSpec, FeatureSpecArray, aes
from lets_plot.plot.util import as_annotated_data

__all__ = ['waterfall_plot']


def waterfall_plot(data, x, y, *,
                   measure=None, group=None,
                   color=None, fill=None, size=None, alpha=None, linetype=None,
                   width=None,
                   show_legend=None, relative_tooltips=None, absolute_tooltips=None,
                   sorted_value=None, threshold=None, max_values=None,
                   base=None,
                   calc_total=None, total_title=None,
                   hline=None, hline_ontop=None,
                   connector=None,
                   relative_labels=None, absolute_labels=None,
                   label=None, label_format=None,
                   background_layers=None) -> PlotSpec:
    """
    A waterfall plot shows the cumulative effect of sequentially introduced positive or negative values.

    Parameters
    ----------
    data : dict or Pandas or Polars ``DataFrame``
        The data to be displayed.
    x : str
        Name of a variable.
    y : str
        Name of a numeric variable.
    measure : str
        Kind of a calculation.
        It takes the name of a data column.
        The values in the column could be:

        'absolute' - the value is shown as is;
        'relative' - the value is shown as a difference from the previous value;
        'total' - the value is shown as a cumulative sum of all previous values.

    group : str
        Grouping variable. Each group calculates its own statistics.
    color : str
        Color of the box boundary lines.
        For more info see `Color and Fill <https://lets-plot.org/python/pages/aesthetics.html#color-and-fill>`__.
        Use 'flow_type' to color lines by the direction of the flow.
        Flow type names: "Absolute", "Increase", "Decrease" and "Total".
        You could use these names to change the default colors with the
        `scale_color_manual() <https://lets-plot.org/python/pages/api/lets_plot.scale_color_manual.html>`__ function.
    fill : str
        Fill color of the boxes.
        For more info see `Color and Fill <https://lets-plot.org/python/pages/aesthetics.html#color-and-fill>`__.
        Use 'flow_type' to color boxes by the direction of the flow.
        Flow type names: "Absolute", "Increase", "Decrease" and "Total".
        You could use these names to change the default colors with the
        `scale_fill_manual() <https://lets-plot.org/python/pages/api/lets_plot.scale_fill_manual.html>`__ function.
    size : float, default=0.0
        Line width of the box boundary lines.
    alpha : float
        Transparency level of the boxes. Accept values between 0 and 1.
    linetype : int or str or list
        Type of the box boundary lines.
        Accept codes or names (0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'),
        a hex string (up to 8 digits for dash-gap lengths),
        or a list pattern [offset, [dash, gap, ...]] / [dash, gap, ...].
        For more info see `Line Types <https://lets-plot.org/python/pages/aesthetics.html#line-types>`__.
    width : float, default=0.9
        Width of the boxes. Typically range between 0 and 1.
        Values that are greater than 1 lead to overlapping of the boxes.
    show_legend : bool, default=False
        True - show the legend.
    relative_tooltips : ``layer_tooltips`` or str
        Tooltips for boxes with relative values.
        Result of the call to the `layer_tooltips() <https://lets-plot.org/python/pages/api/lets_plot.layer_tooltips.html>`__ function.
        Specify appearance, style and content.
        When 'none', tooltips are not shown.
        When 'detailed', a more detailed (compared to the default) version of the tooltips is shown.
    absolute_tooltips : ``layer_tooltips`` or str
        Tooltips for boxes with absolute values.
        Result of the call to the `layer_tooltips() <https://lets-plot.org/python/pages/api/lets_plot.layer_tooltips.html>`__ function.
        Specify appearance, style and content.
        When 'none', tooltips are not shown.
        When 'detailed', a more detailed (compared to the default) version of the tooltips is shown.
    sorted_value : bool, default=False
        Sorts categories by absolute value of the changes.
    threshold : float
        Groups all categories under a certain threshold value into "Other" category.
    max_values : int
        Groups all categories with the smallest changes, except the first ``max_values``, into "Other" category.
    base : float, default=0.0
        Values with measure 'absolute' or 'total' are relative to this value.
    calc_total : bool, default=True
        Setting the ``calc_total`` to True will put the final cumulative sum into a new separate box.
        Taken into account only if the 'measure' column isn't provided.
    total_title : str
        The header of the last box with the final cumulative sum, if 'measure' column isn't provided.
        Also used as a title in the legend for columns of type 'total'.
    hline : str or dict
        Horizontal line passing through 0.
        Set 'blank' or result of `element_blank() <https://lets-plot.org/python/pages/api/lets_plot.element_blank.html>`__ to draw nothing.
        Set `element_line() <https://lets-plot.org/python/pages/api/lets_plot.element_line.html>`__ to specify parameters.
    hline_ontop : bool, default=True
        Option to place horizontal line over the other layers.
    connector : str or dict
        Line between neighbouring boxes connecting the end of the previous box and the beginning of the next box.
        Set 'blank' or result of `element_blank() <https://lets-plot.org/python/pages/api/lets_plot.element_blank.html>`__ to draw nothing.
        Set `element_line() <https://lets-plot.org/python/pages/api/lets_plot.element_line.html>`__ to specify parameters.
    relative_labels : dict
        Result of the call to the `layer_labels() <https://lets-plot.org/python/pages/api/lets_plot.layer_labels.html>`__ function.
        Specify content and formatting of annotation labels on relative change bars.
        If specified, overrides ``label_format`` for relative bars.
    absolute_labels : dict
        Result of the call to the `layer_labels() <https://lets-plot.org/python/pages/api/lets_plot.layer_labels.html>`__ function.
        Specify content and formatting of annotation labels on absolute value bars.
        If specified, overrides ``label_format`` for absolute bars.
    label : str or dict
        Style configuration for labels on bars. Applied to default labels or to
        relative/absolute labels when ``relative_labels`` or ``absolute_labels`` are specified.
        Set 'blank' or result of `element_blank() <https://lets-plot.org/python/pages/api/lets_plot.element_blank.html>`__ to draw nothing.
        Set `element_text() <https://lets-plot.org/python/pages/api/lets_plot.element_text.html>`__ to specify style parameters.
        Use ``element_text(color='inherit')`` to make labels inherit the color of bar borders.
    label_format : str
        Format string used to transform label values to text. Applied to default labels or to
        relative/absolute labels when ``relative_labels`` or ``absolute_labels`` are specified.
        Can be overridden by formatting specified in ``relative_labels`` or ``absolute_labels``.
        Examples:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see `Formatting <https://lets-plot.org/python/pages/formats.html>`__.
    background_layers : LayerSpec or FeatureSpecArray
        Background layers to be added to the plot.

    Returns
    -------
    ``PlotSpec``
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
        :emphasize-lines: 21-25

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
        band_data = {
            'xmin': [-0.5, 2.5],
            'xmax': [2.5, 5.5],
            'name': ['Q1', 'Q2']
        }
        text_data = {
            'x': [0, 3],
            'y': [2.7, 2.7],
            'name': ['Q1', 'Q2']
        }
        waterfall_plot(data, 'x', 'y', label_format='.2f',
                       background_layers=geom_band(
                           aes(xmin='xmin', xmax='xmax', fill='name', color='name'),
                           data=band_data, alpha=0.2
                       )) + \\
            geom_text(aes(x='x', y='y', label='name'), data=text_data, size=10) + \\
            ggtitle("Waterfall with background layers")

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
                       label=element_text(color='inherit'), \\
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
        :emphasize-lines: 17-18

        from lets_plot import *
        from lets_plot.bistro.waterfall import *
        LetsPlot.setup_html()
        data = {
            'company': ["Badgersoft"] * 7 + ["AIlien Co."] * 7,
            'accounts': ["initial", "revenue", "costs", "Q1", "revenue", "costs", "Q2"] * 2,
            'values': [200, 200, -100, None, 250, -100, None,
                       150, 50, -100, None, 100, -100, None],
            'measure': ['absolute', 'relative', 'relative', 'total', 'relative', 'relative', 'total'] * 2,
        }
        colors = {
            "Absolute": "darkseagreen",
            "Increase": "palegoldenrod",
            "Decrease": "paleturquoise",
            "Total": "palegreen",
        }
        waterfall_plot(data, 'accounts', 'values', measure='measure', group='company',
                       size=.75, label=element_text(color="black")) + \\
            scale_fill_manual(values=colors) + \\
            facet_wrap(facets='company', scales='free_x')

    """
    data, mapping, data_meta = as_annotated_data(data, aes(x=x, y=y))

    if background_layers is None:
        layers = []
    elif isinstance(background_layers, LayerSpec):
        layers = [background_layers]
    elif isinstance(background_layers, FeatureSpecArray):
        for sublayer in background_layers.elements():
            if not isinstance(sublayer, LayerSpec):
                raise TypeError("Invalid 'layer' type: {}".format(type(sublayer)))
        layers = background_layers.elements()
    else:
        raise TypeError("Invalid 'layer' type: {}".format(type(background_layers)))

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
        'base': base,
        'calc_total': calc_total,
        'total_title': total_title,
        'hline': hline,
        'hline_ontop': hline_ontop,
        'connector': connector,
        'relative_labels': relative_labels,
        'absolute_labels': absolute_labels,
        'label': label,
        'label_format': label_format,
        'background_layers': [layer.as_dict() for layer in layers]
    }, **data_meta)
