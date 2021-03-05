#  Copyright (c) 2021. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import lets_plot as gg
from lets_plot.plot import theme
from lets_plot.plot.geom import _geom


def test_theme_merge_opts():
    geom = _geom('geom')
    theme_opts1 = theme(axis_line='blank', axis_title='blank')
    theme_opts2 = theme(axis_text='blank', axis_tooltip='blank')

    spec = gg.ggplot() + theme_opts1 + geom + theme_opts2
    expected_theme = {
        'axis_title': 'blank',
        'axis_text': 'blank',
        'axis_line': 'blank',
        'axis_tooltip': 'blank'
    }
    assert_theme(spec, expected_theme)


def test_theme_override_opts():
    geom = _geom('geom')
    theme_opts = theme(axis_tooltip='blank', legend_position='none')
    overridden_opts = theme(legend_position='bottom')

    spec = gg.ggplot() + theme_opts + geom + overridden_opts
    assert_theme(spec, {'axis_tooltip': 'blank', 'legend_position': 'bottom'})


def test_theme_geom_in_opts():
    geom = _geom('geom')
    spec = gg.ggplot() + geom + theme(legend_position=geom)
    assert_theme(spec, {'legend_position': geom})


def assert_theme(spec, expected):
    assert spec.props()['theme'] == expected
