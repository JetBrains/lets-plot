#  Copyright (c) 2021. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import lets_plot as gg
from lets_plot import element_blank
from lets_plot import element_rect
from lets_plot.plot import theme
from lets_plot.plot.geom import _geom
from lets_plot.plot.theme_set import theme_classic


def test_theme_options_should_be_merged():
    spec = gg.ggplot() + _geom('foo') + theme(axis_tooltip='blank') + theme(legend_position='bottom')
    expected_theme = {
        'axis_tooltip': 'blank',
        'legend_position': 'bottom'
    }
    assert expected_theme == spec.as_dict()['theme']


def test_override_theme_option():
    spec = gg.ggplot() + _geom('foo') + theme(legend_position='bottom') + theme(legend_position='none')
    assert 'none' == spec.as_dict()['theme']['legend_position']


def test_override_theme_None_option_with_value():
    spec = gg.ggplot() + _geom('foo') + theme(legend_position=None) + theme(legend_position='bottom')
    assert 'bottom' == spec.as_dict()['theme']['legend_position']


def test_None_theme_option_not_override_the_previous():
    spec = gg.ggplot() + _geom('foo') + theme(legend_position='bottom') + theme(legend_position=None)
    assert 'bottom' == spec.as_dict()['theme']['legend_position']


def test_element_blank_in_theme_option():
    spec = gg.ggplot() + _geom('foo') + theme(axis_tooltip=element_blank())
    assert spec.as_dict()['theme']['axis_tooltip']['blank']
    assert isinstance(spec.props()['theme']['axis_tooltip'], dict)


def test_named_theme_overrides_all():
    spec = gg.ggplot() + _geom('foo') + theme(legend_position='bottom') + theme_classic()
    assert 'legend_position' not in spec.as_dict()['theme']


def test_named_theme_is_modified_by_theme():
    spec = gg.ggplot() + _geom('foo') + theme_classic() + theme(legend_position='bottom')
    assert 'bottom' == spec.as_dict()['theme']['legend_position']


def test_element_values_merged():
    spec = (gg.ggplot() + _geom('foo')
            + theme(panel_background=element_rect(color='a', fill='b'))
            + theme(panel_background=element_rect(color='c', size=1)))

    rect = spec.props()['theme']['panel_background']
    assert isinstance(rect, dict)
    assert 'c' == rect['color']
    assert 'b' == rect['fill']
    assert 1 == rect['size']
