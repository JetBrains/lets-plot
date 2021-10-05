#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

# __all__ = ['theme', 'element_blank']

_VAL_ELEMENT_BLANK = "blank"


class ThemeSpec(FeatureSpec):
    """
    ToDo
    """

    def __init__(self, name, **kwargs):
        super().__init__('theme', name, **kwargs)

    def _with_option(self, name: str, value):
        copy = ThemeSpec(self.props().get('name'))
        copy.props().update(self.props())
        copy.props()[name] = value
        return copy

    def axis_blank(self, axis: str = 'both'):
        return (self.axis_title_blank(axis)
                .axis_line_blank(axis)
                .axis_text_blank(axis)
                .axis_ticks_blank(axis)
                .axis_tooltip_blank(axis))

    def axis_title_blank(self, axis: str = 'both'):
        return self._with_option(_to_axis_option('axis_title', axis), _VAL_ELEMENT_BLANK)

    def axis_text_blank(self, axis: str = 'both'):
        return self._with_option(_to_axis_option('axis_text', axis), _VAL_ELEMENT_BLANK)

    def axis_ticks_blank(self, axis: str = 'both'):
        return self._with_option(_to_axis_option('axis_ticks', axis), _VAL_ELEMENT_BLANK)

    def axis_line_blank(self, axis: str = 'both'):
        return self._with_option(_to_axis_option('axis_line', axis), _VAL_ELEMENT_BLANK)

    def axis_tooltip_blank(self, axis: str = 'both'):
        return self._with_option(_to_axis_option('axis_tooltip', axis), _VAL_ELEMENT_BLANK)

    def panel_blank(self):
        return self._with_option('panel_rect', _VAL_ELEMENT_BLANK)

    # def panel_grid_blank(self, axis: str = 'both'):
    #     return self._with_option(_to_axis_option('panel_grid', axis), _VAL_ELEMENT_BLANK)
    #
    # def panel_grid_major_blank(self, axis: str = 'both'):
    #     return self._with_option(_to_axis_option('panel_grid_major', axis), _VAL_ELEMENT_BLANK)
    #
    # def panel_grid_major_blank(self, axis: str = 'both'):
    #     return self._with_option(_to_axis_option('panel_grid_major', axis), _VAL_ELEMENT_BLANK)


def _to_axis_option(base_name: str, axis: str):
    if axis == 'both':
        return base_name
    elif axis in ['x', 'y']:
        return base_name + "_" + axis
    else:
        raise ValueError("Expected 'axis' value: 'x', 'y' or 'both'")


class theme2(ThemeSpec):
    # Replacment for old `theme()` function
    def __init__(self, **kwargs):
        super().__init__(None, **kwargs)


class theme_lp_light(ThemeSpec):
    def __init__(self, **kwargs):
        super().__init__(name='lp_light', **kwargs)


class theme_grey(ThemeSpec):
    def __init__(self, **kwargs):
        super().__init__(name='grey', **kwargs)

class theme_classic(ThemeSpec):
    def __init__(self, **kwargs):
        super().__init__(name='classic', **kwargs)
