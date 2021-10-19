#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec
from .theme_ import _filter_none

__all__ = [
    'theme_grey',
]


def theme_grey(*,
               line=None,
               rect=None,
               text=None,
               title=None,
               axis=None,
               axis_title=None, axis_title_x=None, axis_title_y=None,
               axis_text=None, axis_text_x=None, axis_text_y=None,
               axis_ticks=None, axis_ticks_x=None, axis_ticks_y=None,
               axis_ticks_length=None, axis_ticks_length_x=None, axis_ticks_length_y=None,
               axis_line=None, axis_line_x=None, axis_line_y=None,
               legend_text=None, legend_title=None,
               legend_position=None, legend_justification=None, legend_direction=None,
               panel_background=None,
               panel_grid=None,
               panel_grid_major=None,
               panel_grid_minor=None,
               panel_grid_major_x=None,
               panel_grid_minor_x=None,
               panel_grid_major_y=None,
               panel_grid_minor_y=None,
               plot_title=None,
               strip_background=None,  # ToDo: x/y
               strip_text=None,  # ToDo: x/y
               axis_tooltip=None, axis_tooltip_x=None, axis_tooltip_y=None
               ):
    """
    """

    filtered = _filter_none(locals())
    return FeatureSpec('theme', name="grey", **filtered)
