#  Copyright (c) 2021. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import lets_plot as gg
from lets_plot.plot import theme
from lets_plot.plot.geom import _geom


def test_theme_merge_opts():
    geom = _geom('geom')
    theme_opts1 = theme(axis_line='blank', axis_title='blank')
    theme_opts2 = theme(axis_text='blank', axis_tooltip='blank')
    expected_theme = {
        'axis_title': 'blank',
        'axis_title_x': None,
        'axis_title_y': None,
        'axis_text': 'blank',
        'axis_text_x': None,
        'axis_text_y': None,
        'axis_ticks': None,
        'axis_ticks_x': None,
        'axis_ticks_y': None,
        'axis_line': 'blank',
        'axis_line_x': None,
        'axis_line_y': None,
        'legend_position': None,
        'legend_justification': None,
        'legend_direction': None,
        'axis_tooltip': 'blank',
        'axis_tooltip_x': None,
        'axis_tooltip_y': None
    }

    assert (gg.ggplot() + theme_opts1 + geom + theme_opts2).as_dict()['theme'] == expected_theme


def test_theme_override_opts():
    geom = _geom('geom')
    theme_opts = theme(axis_tooltip='blank', legend_position='none')
    overridden_opts = theme(legend_position='bottom')
    expected_theme = {
        'axis_title': None,
        'axis_title_x': None,
        'axis_title_y': None,
        'axis_text': None,
        'axis_text_x': None,
        'axis_text_y': None,
        'axis_ticks': None,
        'axis_ticks_x': None,
        'axis_ticks_y': None,
        'axis_line': None,
        'axis_line_x': None,
        'axis_line_y': None,
        'legend_position': 'bottom',
        'legend_justification': None,
        'legend_direction': None,
        'axis_tooltip': 'blank',
        'axis_tooltip_x': None,
        'axis_tooltip_y': None
    }

    assert (gg.ggplot() + theme_opts + geom + overridden_opts).as_dict()['theme'] == expected_theme
