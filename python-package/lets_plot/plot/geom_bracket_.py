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

_min_col_name, _max_col_name = "_min", "_max"


def _dodged_coord(group_id, subgroup_id, subgroup_count, width):
    median = (subgroup_count - 1) / 2
    offset = (subgroup_id - median) * width
    scaler = 1.0 / subgroup_count
    return group_id + offset * scaler


def _get_x_aes_name(orientation, mapping_dict, other_args):
    x_aes_name = orientation
    if x_aes_name is None and \
        ("x" in mapping_dict or "x" in other_args.keys()) and \
        "ymin" not in mapping_dict and "ymax" not in mapping_dict and \
        "ymin" not in other_args.keys() and "ymax" not in other_args.keys():
        x_aes_name = "x"
    return x_aes_name


def _get_xs(x_aes_name, mapping_dict, other_args, data):
    if x_aes_name in other_args.keys():
        return [other_args[x_aes_name]]
    # x_aes_name in mapping_dict
    x_aes = mapping_dict[x_aes_name]
    if isinstance(x_aes, str) and data is not None:
        return data[x_aes]
    elif hasattr(x_aes, '__iter__'):
        return x_aes
    else:
        raise Exception(f"Unknown column name {x_aes} in dataset")


def _get_subgroups(subgroup, data, xs):
    if isinstance(subgroup, str) and data is not None:
        subgroups = data[subgroup]
        assert len(subgroups) == len(xs), f"Wrong size of subgroup: {len(xs)} != {len(subgroups)}"
        return subgroups
    elif hasattr(subgroup, '__iter__'):
        assert len(subgroup) == len(xs), f"Wrong size of subgroup: {len(xs)} != {len(subgroup)}"
        return subgroup
    else:
        return [subgroup] * len(xs)


def _construct_data(data, min_col, max_col):
    if data is None:
        return {_min_col_name: min_col, _max_col_name: max_col}
    else:
        if isinstance(data, dict):
            return {**data, **{_min_col_name: min_col, _max_col_name: max_col}}
        elif pd is not None and isinstance(data, pd.DataFrame):
            return data.assign(**{_min_col_name: min_col, _max_col_name: max_col})
        elif pl is not None and isinstance(data, pl.DataFrame):
            return data.with_columns(**{_min_col_name: pl.Series(values=min_col), _max_col_name: pl.Series(values=max_col)})
        else:
            raise Exception("Unsupported type of data: {0}".format(data))


def _get_new_data(x_aes_name, mapping_dict, data,
                  subgroup1, subgroup2,
                  dodge_width,
                  ordered_groups, ordered_subgroups,
                  other_args):
    xs = _get_xs(x_aes_name, mapping_dict, other_args, data)
    # dict.fromkeys() takes unique values and preserves an order
    group_names = dict.fromkeys(xs) if ordered_groups is None else ordered_groups
    groups = {v: k for k, v in enumerate(group_names)}
    subgroups1 = _get_subgroups(subgroup1, data, xs)
    subgroups2 = _get_subgroups(subgroup2, data, xs)
    subgroup_names = dict.fromkeys(list(subgroups1) + list(subgroups2)) if ordered_subgroups is None else ordered_subgroups
    subgroups = {v: k for k, v in enumerate(subgroup_names)}
    subgroup_count = len(subgroups.keys())
    dodge_width = dodge_width or _DEF_DODGE_WIDTH
    min_col = [_dodged_coord(groups[group], subgroups[subgroup], subgroup_count, dodge_width)
               for (group, subgroup) in zip(xs, subgroups1)]
    max_col = [_dodged_coord(groups[group], subgroups[subgroup], subgroup_count, dodge_width)
               for (group, subgroup) in zip(xs, subgroups2)]
    return _construct_data(data, min_col, max_col)


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
                 ordered_groups=None, ordered_subgroups=None,
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
    x_aes_name = _get_x_aes_name(orientation, mapping_dict, other_args)
    if x_aes_name is None:
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
    new_data = _get_new_data(x_aes_name, mapping_dict, data,
                             subgroup1, subgroup2,
                             dodge_width,
                             ordered_groups, ordered_subgroups,
                             other_args)
    if x_aes_name in mapping_dict.keys():
        del mapping_dict[x_aes_name]
    else:
        del other_args[x_aes_name]
    new_mapping = aes(**{**mapping_dict, **{f"{x_aes_name}min": _min_col_name, f"{x_aes_name}max": _max_col_name}})
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