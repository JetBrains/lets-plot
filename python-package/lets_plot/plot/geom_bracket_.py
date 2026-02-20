#  Copyright (c) 2026. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
try:
    import pandas as pd
except ImportError:
    pd = None

try:
    import polars as pl
except ImportError:
    pl = None

from .core import aes
from .geom import _geom

__all__ = ['geom_bracket']

_DEF_DODGE_WIDTH = .95

_bracket_min_col, _bracket_max_col = "..bracket_min..", "..bracket_max.."


def _compute_dodged_position(group_id, subgroup_id, n_subgroups, width):
    median = (n_subgroups - 1) / 2
    offset = (subgroup_id - median) * width
    scaler = 1.0 / n_subgroups
    return group_id + offset * scaler


def _resolve_primary_axis(orientation, mapping_dict, other_args):
    axis = orientation
    if axis is None and \
        ("x" in mapping_dict or "x" in other_args.keys()) and \
        "ymin" not in mapping_dict and "ymax" not in mapping_dict and \
        "ymin" not in other_args.keys() and "ymax" not in other_args.keys():
        axis = "x"
    if axis is not None and axis not in mapping_dict and axis not in other_args.keys():
        raise ValueError(f"Expected {axis} in mapping or in the list of arguments of the function")
    return axis


def _get_primary_axis_values(axis, mapping_dict, other_args, data):
    if axis in other_args.keys():
        return [other_args[axis]]
    # axis in mapping_dict
    x_aes = mapping_dict[axis]
    if isinstance(x_aes, str) and data is not None:
        return data[x_aes]
    elif isinstance(x_aes, str) and data is None:
        raise ValueError(f"Cannot resolve '{axis}' from column name '{x_aes}' because data is None.")
    elif hasattr(x_aes, '__iter__'):
        return x_aes
    else:
        raise TypeError(f"Invalid mapping for '{axis}': "
                        f"expected a column name (str) or a sequence of values, got {type(x_aes).__name__}: {x_aes!r}.")


def _resolve_subgroup_values(subgroup, data, group_values):
    if subgroup is None:
        raise ValueError("Subgroups must be provided (either as column names or as explicit values).")
    elif isinstance(subgroup, str) and data is not None:
        subgroups = data[subgroup]
        if len(subgroups) != len(group_values):
            raise ValueError(f"Subgroup values must have the same length as group values: "
                             f"expected {len(group_values)}, got {len(subgroups)}.")
        return subgroups
    elif isinstance(subgroup, str) and data is None:
        raise ValueError(f"Cannot resolve subgroup from column name '{subgroup}' because data is None. "
                         "Provide data or pass explicit subgroup values.")
    elif hasattr(subgroup, '__iter__'):
        if len(subgroup) != len(group_values):
            raise ValueError(f"Subgroup values must have the same length as group values: "
                             f"expected {len(group_values)}, got {len(subgroup)}.")
        return subgroup
    else:
        return [subgroup] * len(group_values)


def _attach_bracket_columns(data, axis_min_positions, axis_max_positions):
    if data is None:
        return {_bracket_min_col: axis_min_positions, _bracket_max_col: axis_max_positions}
    else:
        if isinstance(data, dict):
            return {**data, **{_bracket_min_col: axis_min_positions, _bracket_max_col: axis_max_positions}}
        elif pd is not None and isinstance(data, pd.DataFrame):
            return data.assign(**{_bracket_min_col: axis_min_positions, _bracket_max_col: axis_max_positions})
        elif pl is not None and isinstance(data, pl.DataFrame):
            return data.with_columns([pl.Series(_bracket_min_col, axis_min_positions), pl.Series(_bracket_max_col, axis_max_positions)])
        else:
            raise TypeError(f"Unsupported data type: {type(data).__name__}. "
                            f"Expected dict, pandas.DataFrame, or polars.DataFrame.")


def _resolve_category_order(values, ordered_categories):
    # dict.fromkeys() takes unique values and preserves an order
    distinct_values = list(dict.fromkeys(values).keys())
    if ordered_categories is None:
        return distinct_values
    else:
        if len(set(ordered_categories)) != len(ordered_categories):
            raise ValueError(f"Values in the {ordered_categories} should be distinct.")
        if next((v for v in distinct_values if v not in ordered_categories)) is not None:
            raise ValueError(f"Values in the {ordered_categories} should be exhaustive.")
        if len(distinct_values) != len(ordered_categories):
            raise ValueError(f"Too many values in the {ordered_categories}.")
        return ordered_categories


def _build_bracket_data(axis, mapping_dict, data,
                        subgroup1, subgroup2,
                        dodge_width,
                        group_order, subgroup_order,
                        other_args):
    group_values = _get_primary_axis_values(axis, mapping_dict, other_args, data)
    group_to_index = {g: i for i, g in enumerate(_resolve_category_order(group_values, group_order))}
    subgroups1 = _resolve_subgroup_values(subgroup1, data, group_values)
    subgroups2 = _resolve_subgroup_values(subgroup2, data, group_values)
    subgroup_to_index = {s: i for i, s in enumerate(_resolve_category_order(list(subgroups1) + list(subgroups2), subgroup_order))}
    n_subgroups = len(subgroup_to_index.keys())
    dodge_width = _DEF_DODGE_WIDTH if dodge_width is None else dodge_width
    axis_min_positions = [_compute_dodged_position(group_to_index[group], subgroup_to_index[subgroup], n_subgroups, dodge_width)
                          for (group, subgroup) in zip(group_values, subgroups1)]
    axis_max_positions = [_compute_dodged_position(group_to_index[group], subgroup_to_index[subgroup], n_subgroups, dodge_width)
                          for (group, subgroup) in zip(group_values, subgroups2)]
    return _attach_bracket_columns(data, axis_min_positions, axis_max_positions)


def geom_bracket(mapping=None, *, data=None,
                 subgroup1=None, subgroup2=None,
                 position=None, show_legend=None,
                 manual_key=None,
                 sampling=None,
                 orientation=None,
                 label_format=None, na_text=None,
                 nudge_x=None, nudge_y=None, nudge_unit=None,
                 size_unit=None,
                 bracket_shorten=None, tip_length_unit=None,
                 dodge_width=None,
                 group_order=None, subgroup_order=None,
                 color_by=None,
                 **other_args):
    """
    Annotate a plot with labeled brackets to highlight relationships or groupings between categories or ranges.

    Parameters
    ----------
    mapping : ``FeatureSpec``
        Set of aesthetic mappings created by `aes() <https://lets-plot.org/python/pages/api/lets_plot.aes.html>`__ function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or Pandas or Polars ``DataFrame``
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    position : str or ``FeatureSpec``, default='identity'
        Position adjustment.
        Either a position adjustment name: 'dodge', 'jitter', 'nudge', 'jitterdodge', 'fill',
        'stack' or 'identity', or the result of calling a position adjustment function (e.g., `position_dodge() <https://lets-plot.org/python/pages/api/lets_plot.position_dodge.html>`__ etc.).
    show_legend : bool, default=False
        True - show legend for this layer.
    manual_key : str or ``layer_key``
        The key to show in the manual legend.
        Specify text for the legend label or advanced settings using the `layer_key() <https://lets-plot.org/python/pages/api/lets_plot.layer_key.html>`__ function.
    sampling : ``FeatureSpec``
        Result of the call to the ``sampling_xxx()`` function.
        To prevent any sampling for this layer pass value "none" (string "none").
    orientation : str, default='x'
        Specify the axis that the geom should run along.
        Possible values: 'x', 'y'.
    label_format : str
        Format used to transform text label mapping values to a string.
        Examples:

        - '.2f' -> '12.45'
        - 'Num {}' -> 'Num 12.456789'
        - 'TTL: {.2f}$' -> 'TTL: 12.45$'

        For more info see `Formatting <https://lets-plot.org/python/pages/formats.html>`__.
    na_text : str, default='n/a'
        Text to show for missing values.
    nudge_x : float
        Horizontal adjustment to nudge geometry by.
    nudge_y : float
        Vertical adjustment to nudge geometry by.
    size_unit : {'x', 'y', 'min', 'max'}
        Relate the size of the text to the length of the unit step along one of the axes.
        'x' uses the unit step along the x-axis, 'y' uses the unit step along the y-axis.
        'min' uses the smaller of the unit steps along the x- and y-axes.
        'max' uses the larger of the unit steps along the x- and y-axes.
        If None, no fitting is performed.
    bracket_shorten : float, default=1
        Symmetrically shorten the bracket by shifting both ends toward the center.
        Expect values between 0 and 1.
    tip_length_unit : {'res', 'identity', 'size', 'px'}, default='size'
        Unit for ``tip_length_start`` and ``tip_length_end`` aesthetics.
        Possible values:

        - 'res': the unit equals the smallest distance between data points along the corresponding axis;
        - 'identity': a unit of 1 corresponds to a difference of 1 in data space;
        - 'size': a unit of 1 corresponds to the diameter of a point with ``size=1``;
        - 'px': the unit is measured in screen pixels.

    nudge_unit : {'identity', 'size', 'px'}, default='identity'
        Units for x and y nudging.
        Possible values:

        - 'identity': a unit of 1 corresponds to a difference of 1 in data space;
        - 'size': a unit of 1 corresponds to the diameter of a point with ``size=1``;
        - 'px': the unit is measured in screen pixels.

    color_by : {'fill', 'color', 'paint_a', 'paint_b', 'paint_c'}, default='color'
        Define the color aesthetic for the geometry.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    ``LayerSpec``
        Geom object specification.

    Notes
    -----
    ``geom_bracket()`` understands the following aesthetics mappings:

    - xmin or ymin: left or lower end of the bracket for horizontal or vertical brackets, respectively.
    - xmax or ymax: right or upper end of the bracket for horizontal or vertical brackets, respectively.
    - y or x : y-axis or x-axis coordinates for horizontal or vertical brackets, respectively.
    - alpha : transparency level of a layer. Accept values between 0 and 1.
    - color (colour) : color of the geometry. For more info see `Color and Fill <https://lets-plot.org/python/pages/aesthetics.html#color-and-fill>`__.
    - size : font size.
    - label : text to add.
    - family : font family. For more info see `Text <https://lets-plot.org/python/pages/aesthetics.html#text>`__.
    - fontface : font style and weight. For more info see `Text <https://lets-plot.org/python/pages/aesthetics.html#text>`__.
    - hjust : horizontal text alignment relative to the x-coordinate. Possible values: 0 or 'left' - left-aligned (text starts at x), 0.5 or 'middle' (default) - text is centered on x, 1 or 'right' - right-aligned (text ends at x). There are two special alignments: 'inward' (aligns text towards the plot center) and 'outward' (away from the plot center).
    - vjust : vertical text alignment relative to the y-coordinate. Accept either a numeric value or one of the following strings: 'bottom', 'center', or 'top'. The numeric values 0, 0.5 (default), and 1 correspond to 'bottom' (bottom of text at y), 'center' (middle of text at y), and 'top' (top of text at y), respectively. There are two special alignments: 'inward' (aligns text towards the plot center) and 'outward' (away from the plot center).
    - angle : text rotation angle in degrees.
    - lineheight : line height multiplier applied to the font size in the case of multi-line text.
    - linetype : type of the line. Accept codes or names (0 = 'blank', 1 = 'solid', 2 = 'dashed', 3 = 'dotted', 4 = 'dotdash', 5 = 'longdash', 6 = 'twodash'), a hex string (up to 8 digits for dash-gap lengths), or a list pattern [offset, [dash, gap, ...]] / [dash, gap, ...]. For more info see `Line Types <https://lets-plot.org/python/pages/aesthetics.html#line-types>`__.
    - segment_color : color of the bracket line (the segments forming the bracket).
    - segment_size : width of the bracket line (the segments forming the bracket).
    - segment_alpha : transparency level of the bracket line. Accept values between 0 and 1.
    - tip_length_start : length of the tip at the bracket start (at ``xmin`` for horizontal brackets, or ``ymin`` for vertical).
    - tip_length_end : length of the tip at the bracket end (at ``xmax`` for horizontal brackets, or ``ymax`` for vertical).

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 21

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 50
        np.random.seed(42)
        box_data = {
            'x': ['a'] * n + ['b'] * n + ['c'] * n,
            'y': np.concatenate([np.random.normal(size=n, loc=0),
                                 np.random.normal(size=n, loc=.1),
                                 np.random.normal(size=n, loc=.5)]),
        }
        bracket_data = {
            'xmin': ['a', 'a', 'b'],
            'xmax': ['b', 'c', 'c'],
            'y': [3.6, 4.2, 4.8],
            'label': ['*', '**', 'ns'],
        }
        ggplot(box_data, aes(x='x', y='y', color='x')) + \\
            geom_boxplot(aes(fill='x'), alpha=.25) + \\
            geom_jitter(height=0, seed=42) + \\
            geom_bracket(aes(xmin='xmin', xmax='xmax', y='y', label='label'), data=bracket_data)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 21-23

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 50
        np.random.seed(42)
        violin_data = {
            'x': np.concatenate([np.random.normal(size=n, loc=0),
                                 np.random.normal(size=n, loc=.1),
                                 np.random.normal(size=n, loc=.5)]),
            'y': ['a'] * n + ['b'] * n + ['c'] * n,
        }
        bracket_data = {
            'x': [-2.9, -3.3, -3.7],
            'ymin': ['a', 'a', 'b'],
            'ymax': ['b', 'c', 'c'],
            'label': ['*', '**', 'ns'],
        }
        ggplot(violin_data, aes(x='x', y='y', color='y')) + \\
            geom_violin(aes(fill='y'), alpha=.25) + \\
            geom_sina(seed=42) + \\
            geom_bracket(aes(x='x', ymin='ymin', ymax='ymax', label='label'), data=bracket_data,
                         tip_length_start=-.1, tip_length_end=-.1, tip_length_unit='identity', vjust=2.2,
                         color='maroon', size=9, segment_size=1.25)

    """
    mapping_dict = {} if mapping is None else mapping.as_dict()
    axis = _resolve_primary_axis(orientation, mapping_dict, other_args)
    if axis is None:
        return _geom('bracket',
                     mapping=mapping,
                     data=data,
                     stat=None,
                     position=position,
                     show_legend=show_legend,
                     inherit_aes=False,
                     manual_key=manual_key,
                     sampling=sampling,
                     tooltips=None,
                     orientation=orientation,
                     label_format=label_format,
                     na_text=na_text,
                     nudge_x=nudge_x,
                     nudge_y=nudge_y,
                     nudge_unit=nudge_unit,
                     size_unit=size_unit,
                     bracket_shorten=bracket_shorten,
                     tip_length_unit=tip_length_unit,
                     color_by=color_by,
                     **other_args)
    new_data = _build_bracket_data(axis, mapping_dict, data,
                                   subgroup1, subgroup2,
                                   dodge_width,
                                   group_order, subgroup_order,
                                   other_args)
    if axis in mapping_dict.keys():
        del mapping_dict[axis]
    else:
        del other_args[axis]
    new_mapping = aes(**{**mapping_dict, **{f"{axis}min": _bracket_min_col, f"{axis}max": _bracket_max_col}})
    return _geom('bracket',
                 mapping=new_mapping,
                 data=new_data,
                 stat=None,
                 position=position,
                 show_legend=show_legend,
                 inherit_aes=False,
                 manual_key=manual_key,
                 sampling=sampling,
                 tooltips=None,
                 orientation=orientation,
                 label_format=label_format,
                 na_text=na_text,
                 nudge_x=nudge_x,
                 nudge_y=nudge_y,
                 nudge_unit=nudge_unit,
                 size_unit=size_unit,
                 bracket_shorten=bracket_shorten,
                 tip_length_unit=tip_length_unit,
                 color_by=color_by,
                 **other_args)