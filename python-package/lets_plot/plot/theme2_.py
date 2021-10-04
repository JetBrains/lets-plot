#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

__all__ = ['theme', 'element_blank']

_VAL_ELEMENT_BLANK = "blank"


class theme_lp_light(ThemeSpec):
    def __init__(self):
        super().__init__('theme', name='lp_light')


class theme_grey(ThemeSpec):
    def __init__(self):
        super().__init__('theme', name='grey')


class ThemeSpec(FeatureSpec):
    """
    ToDo
    """

    def __init__(self, name):
        super().__init__('theme', name=name)

    def _with_option(self, name: str, value) -> ThemeSpec:
        copy = ThemeSpec(self.props().get('name'))
        copy.props().update(self.props())
        copy.props()[name] = value
        return copy

    def _to_axis_option(self, base_name: str, axis: str):
        if axis == 'both':
            return base_name
        elif axis in ['x', 'y']:
            return base_name + "_" + axis
        else:
            raise ValueError("Expected 'axis' value: 'x', 'y' or 'both'")

    def axis_blank(self, axis: str = 'both') -> ThemeSpec:
        return (self.axis_title_blank(axis)
                .axis_text_blank(axis)
                .axis_ticks_blank(axis)
                .axis_tooltip_blank(axis)
                .axis_tooltip_blank(axis))

    def axis_title_blank(self, axis: str = 'both') -> ThemeSpec:
        return self._with_option(_to_axis_option('axis_title', axis), _VAL_ELEMENT_BLANK)

    def axis_text_blank(self, axis: str = 'both') -> ThemeSpec:
        return self._with_option(_to_axis_option('axis_text', axis), _VALELEMENT_BLANK)

    def axis_ticks_blank(self, axis: str = 'both') -> ThemeSpec:
        return self._with_option(_to_axis_option('axis_ticks', axis), _VALELEMENT_BLANK)

    def axis_line_blank(self, axis: str = 'both') -> ThemeSpec:
        return self._with_option(_to_axis_option('axis_line', axis), _VALELEMENT_BLANK)

    def axis_tooltip_blank(self, axis: str = 'both') -> ThemeSpec:
        return self._with_option(_to_axis_option('axis_tooltip', axis), _VALELEMENT_BLANK)

    def panel_background(self, color: str) -> ThemeSpec:
        return self._with_option('panel_background', color)

    def panel_border_blank(self) -> ThemeSpec:
        return self._with_option('panel_border', _VALELEMENT_BLANK)

    def panel_grid_blank(self, axis: str = 'both') -> ThemeSpec:
        return self._with_option(_to_axis_option('panel_grid', axis), _VALELEMENT_BLANK)

    def panel_grid_major_blank(self, axis: str = 'both') -> ThemeSpec:
        return self._with_option(_to_axis_option('panel_grid_major', axis), _VALELEMENT_BLANK)

    def panel_grid_major_blank(self, axis: str = 'both') -> ThemeSpec:
        return self._with_option(_to_axis_option('panel_grid_major', axis), _VALELEMENT_BLANK)
