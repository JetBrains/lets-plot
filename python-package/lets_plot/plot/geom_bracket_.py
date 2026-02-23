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

_bracket_start_col, _bracket_end_col = "..bracket_start..", "..bracket_end.."


def _compute_dodged_position(axis_value_id, dodge_group_id, n_dodge_groups, width):
    median = (n_dodge_groups - 1) / 2
    offset = (dodge_group_id - median) * width
    scaler = 1.0 / n_dodge_groups
    return axis_value_id + offset * scaler


def _resolve_primary_axis(orientation, mapping_dict, other_args):
    if orientation is not None:
        return orientation
    if "xmin" in mapping_dict.keys() or "xmin" in other_args.keys() or \
       "xmax" in mapping_dict.keys() or "xmax" in other_args.keys():
        return "x"
    if "ymin" in mapping_dict.keys() or "ymin" in other_args.keys() or \
       "ymax" in mapping_dict.keys() or "ymax" in other_args.keys():
        return "y"
    return "x"


def _get_values_list(aes_name, mapping_dict, other_args, data):
    if aes_name in other_args.keys():
        return [other_args[aes_name]]
    if aes_name not in mapping_dict.keys():
        raise ValueError(f"'{aes_name}' must be provided in dodged mode.")
    # aes_name in mapping_dict
    mapped_aes = mapping_dict[aes_name]
    if isinstance(mapped_aes, str) and data is not None:
        return data[mapped_aes]
    elif isinstance(mapped_aes, str) and data is None:
        raise ValueError(f"Cannot resolve '{aes_name}' from column name '{mapped_aes}' because data is None.")
    elif hasattr(mapped_aes, '__iter__'):
        return mapped_aes
    else:
        raise TypeError(f"Invalid mapping for '{aes_name}': "
                        f"expected a column name (str) or a sequence of values, got {type(mapped_aes).__name__}: {mapped_aes!r}.")


def _attach_bracket_columns(data, bracket_start_pos, bracket_end_pos):
    if data is None:
        return {_bracket_start_col: bracket_start_pos, _bracket_end_col: bracket_end_pos}
    else:
        if isinstance(data, dict):
            return {**data, **{_bracket_start_col: bracket_start_pos, _bracket_end_col: bracket_end_pos}}
        elif pd is not None and isinstance(data, pd.DataFrame):
            return data.assign(**{_bracket_start_col: bracket_start_pos, _bracket_end_col: bracket_end_pos})
        elif pl is not None and isinstance(data, pl.DataFrame):
            return data.with_columns([pl.Series(_bracket_start_col, bracket_start_pos), pl.Series(_bracket_end_col, bracket_end_pos)])
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
        if next((v for v in distinct_values if v not in ordered_categories), None) is not None:
            raise ValueError(f"Values in the {ordered_categories} should be exhaustive.")
        return ordered_categories


def _data_dimension(data, axis_values, dodge_group1, dodge_group2):
    dim = max(len(axis_values), len(dodge_group1), len(dodge_group2))
    if data is None:
        return dim
    else:
        if isinstance(data, dict) and len(data.values()) > 0:
            return max(dim, len(list(data.values())[0]))
        elif pd is not None and isinstance(data, pd.DataFrame):
            return max(dim, data.shape[0])
        elif pl is not None and isinstance(data, pl.DataFrame):
            return max(dim, data.shape[0])
        else:
            raise TypeError(f"Unsupported data type: {type(data).__name__}. "
                            f"Expected dict, pandas.DataFrame, or polars.DataFrame.")


def _build_bracket_data(axis, mapping_dict, data,
                        dodge_width,
                        axis_order, dodge_order,
                        other_args):
    axis_values = _get_values_list(axis, mapping_dict, other_args, data)
    axis_level_to_index = {g: i for i, g in enumerate(_resolve_category_order(axis_values, axis_order))}
    dodge_group1 = _get_values_list(f"{axis}min", mapping_dict, other_args, data)
    dodge_group2 = _get_values_list(f"{axis}max", mapping_dict, other_args, data)
    dodge_level_to_index = {s: i for i, s in enumerate(_resolve_category_order(list(dodge_group1) + list(dodge_group2), dodge_order))}
    n_dodge_groups = len(dodge_level_to_index.keys())
    dodge_width = _DEF_DODGE_WIDTH if dodge_width is None else dodge_width
    dim = _data_dimension(data, axis_values, dodge_group1, dodge_group2)
    if len(axis_values) == 1 and len(axis_values) < dim:
        axis_values = axis_values * dim
    if len(dodge_group1) == 1 and len(dodge_group1) < dim:
        dodge_group1 = dodge_group1 * dim
    if len(dodge_group2) == 1 and len(dodge_group2) < dim:
        dodge_group2 = dodge_group2 * dim
    bracket_start_pos = [_compute_dodged_position(axis_level_to_index[axis_value], dodge_level_to_index[dodge_group], n_dodge_groups, dodge_width)
                         for (axis_value, dodge_group) in zip(axis_values, dodge_group1)]
    bracket_end_pos = [_compute_dodged_position(axis_level_to_index[axis_value], dodge_level_to_index[dodge_group], n_dodge_groups, dodge_width)
                       for (axis_value, dodge_group) in zip(axis_values, dodge_group2)]
    return _attach_bracket_columns(data, bracket_start_pos, bracket_end_pos)


def _bracket_mapping(axis, mapping_dict):
    if axis in mapping_dict.keys():
        del mapping_dict[axis]
    if f"{axis}min" in mapping_dict.keys():
        del mapping_dict[f"{axis}min"]
    if f"{axis}max" in mapping_dict.keys():
        del mapping_dict[f"{axis}max"]
    return aes(**{**mapping_dict, **{f"{axis}min": _bracket_start_col, f"{axis}max": _bracket_end_col}})


def _bracket_other_args(axis, other_args):
    if axis in other_args.keys():
        del other_args[axis]
    if f"{axis}min" in other_args.keys():
        del other_args[f"{axis}min"]
    if f"{axis}max" in other_args.keys():
        del other_args[f"{axis}max"]
    return other_args


def geom_bracket(mapping=None, *, data=None,
                 position=None, show_legend=None,
                 manual_key=None,
                 sampling=None,
                 orientation=None,
                 label_format=None, na_text=None,
                 nudge_x=None, nudge_y=None, nudge_unit=None,
                 size_unit=None,
                 bracket_shorten=None, tip_length_unit=None,
                 dodged=False, dodge_width=None,
                 axis_order=None, dodge_order=None,
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
    bracket_shorten : float, default=0
        Symmetrically shorten the bracket by shifting both ends toward the center.
        Expect values between 0 and 1, where 0 corresponds to no shortening and 1 to a fully collapsed bracket.
    tip_length_unit : {'res', 'identity', 'size', 'px'}, default='size'
        Unit for ``tip_length_start`` and ``tip_length_end`` aesthetics.
        Possible values:

        - 'res': the unit equals the smallest distance between data points along the corresponding axis;
        - 'identity': a unit of 1 corresponds to a difference of 1 in data space;
        - 'size': a unit of 1 corresponds to the diameter of a point with ``size=1``;
        - 'px': the unit is measured in screen pixels.

    dodged : bool, default=False
        If True, interpret ``xmin``/``xmax`` (or ``ymin``/``ymax`` for ``orientation='y'``)
        as dodged group ids and compute bracket positions accordingly.
    dodge_width : float, default=0.95
        Width used to compute bracket positions in ``dodged=True`` mode.
        Expected to match the dodge width used by other layers for proper alignment.
    axis_order : list of Any
        Order of primary axis categories used to map ``x`` (or ``y`` for ``orientation='y'``)
        to discrete positions in ``dodged=True`` mode.
        If None, inferred from this layer's data.
    dodge_order : list of Any
        Order of dodged group levels used to map ``xmin``/``xmax`` (or ``ymin``/``ymax``)
        to dodge offsets in ``dodged=True`` mode.
        If None, inferred from this layer's data.
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
    - y or x : bracket level (the height/position at which the bracket is drawn) for horizontal or vertical brackets, respectively.
    - x or y : primary axis category for horizontal or vertical brackets, respectively; used only in ``dodged=True`` mode.
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

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 27

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 50
        np.random.seed(42)
        box_data = {
            'x': ['a'] * 2 * n + ['b'] * 2 * n + ['c'] * 2 * n,
            'y': np.concatenate([np.random.normal(size=n, loc=0),
                                 np.random.normal(size=n, loc=.5),
                                 np.random.normal(size=n, loc=0),
                                 np.random.normal(size=n, loc=-.5),
                                 np.random.normal(size=n, loc=0),
                                 np.random.normal(size=n, loc=.25)]),
            'g': (['x'] * n + ['y'] * n) * 3,
        }
        bracket_data = {
            'x': ['a', 'b', 'c'],
            'gstart': ['x', 'x', 'x'],
            'gend': ['y', 'y', 'y'],
            'y': [2.6, 3, 4.4],
            'label': ['***', '*', 'ns'],
        }
        ggplot(box_data, aes(x='x', y='y', color='g')) + \\
            geom_boxplot(aes(fill='g'), alpha=.25) + \\
            geom_point(position=position_jitterdodge(jitter_width=.2, jitter_height=0, seed=42),
                       shape=1, size=2, alpha=.25, show_legend=False) + \\
            geom_bracket(aes(x='x', y='y', xmin='gstart', xmax='gend', label='label'), data=bracket_data, dodged=True)

    """
    if not dodged:
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
    mapping_dict = {} if mapping is None else mapping.as_dict()
    axis = _resolve_primary_axis(orientation, mapping_dict, other_args)
    new_data = _build_bracket_data(axis, mapping_dict, data,
                                   dodge_width,
                                   axis_order, dodge_order,
                                   other_args)
    return _geom('bracket',
                 mapping=_bracket_mapping(axis, mapping_dict),
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
                 **_bracket_other_args(axis, other_args.copy()))