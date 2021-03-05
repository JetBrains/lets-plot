#  Copyright (c) 2021. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import lets_plot as gg
from lets_plot import element_blank
from lets_plot.plot import theme
from lets_plot.plot.geom import _geom


def test_theme_options_should_be_merged():
    spec = gg.ggplot() + _geom('foo') + \
           theme(axis_line='blank', axis_title='blank') + \
           theme(axis_text='blank', axis_tooltip='blank')
    expected_theme = {
        'axis_title': 'blank',
        'axis_text': 'blank',
        'axis_line': 'blank',
        'axis_tooltip': 'blank'
    }
    assert expected_theme == spec.props()['theme']


def test_override_theme_option_with_none():
    spec = gg.ggplot() + _geom('foo') + theme(legend_position='bottom') + theme(legend_position='none')
    assert 'none' == spec.as_dict()['theme']['legend_position']


def test_override_theme_none_option_with_value():
    spec = gg.ggplot() + _geom('foo') + theme(legend_position='none') + theme(legend_position='bottom')
    assert 'bottom' == spec.as_dict()['theme']['legend_position']


def test_element_blank_in_theme_option():
    spec = gg.ggplot() + _geom('foo') + theme(axis_tooltip=element_blank())
    assert 'blank' == spec.as_dict()['theme']['axis_tooltip']['name']
    assert isinstance(spec.props()['theme']['axis_tooltip'], type(element_blank()))


def test_blank_values_in_theme_options():
    spec = gg.ggplot() + _geom('foo') + theme(axis_tooltip=element_blank()) + theme(axis_title='blank')
    assert 'blank' == spec.as_dict()['theme']['axis_tooltip']['name']
    assert 'blank' == spec.as_dict()['theme']['axis_title']


def test_override_theme_option_with_element_blank():
    spec = gg.ggplot() + _geom('foo') + theme(axis_tooltip='bar') + theme(axis_tooltip=element_blank())
    assert 'blank' == spec.as_dict()['theme']['axis_tooltip']['name']


def test_override_theme_blank_option_with_value():
    spec = gg.ggplot() + _geom('foo') + theme(axis_tooltip=element_blank()) + theme(axis_tooltip='bar')
    assert 'bar' == spec.as_dict()['theme']['axis_tooltip']


def test_unexpected_FeatureSpec_in_theme_option_wont_break_anything():
    geom = _geom('foo')
    spec = gg.ggplot() + geom + theme(legend_position=geom)
    assert geom == spec.props()['theme']['legend_position']
    assert isinstance(spec.props()['theme']['legend_position'], type(geom))
