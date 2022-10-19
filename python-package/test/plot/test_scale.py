#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import pytest

import lets_plot as gg
from lets_plot.plot.core import FeatureSpec
from lets_plot.plot.core import FeatureSpecArray
from lets_plot.plot.scale import _scale


def gen_scale_args():
    positional_args = ['a']  # aesthetic
    positional_args_as_dict = {'aesthetic': 'a'}
    kwargs = {'name':'n', 'other1': 1, 'other2': 2}

    expected = positional_args_as_dict.copy()
    expected.update(kwargs)
    return positional_args, kwargs, expected


@pytest.mark.parametrize('args_list,args_dict,expected', [
    gen_scale_args(),
])
def test_scale(args_list, args_dict, expected):
    spec = _scale(*args_list, **args_dict)
    assert spec.as_dict() == expected


def test_labs_empty():
    spec = gg.labs()
    assert spec.as_dict()['feature-list'] == []


def test_plot_title():
    spec = gg.labs(title='New plot title')
    as_dict = spec.as_dict()
    assert as_dict['text'] == 'New plot title'


def test_aes_label():
    spec = gg.labs(x='New X label')
    as_dict = spec.as_dict()
    assert as_dict['aesthetic'] == 'x'
    assert as_dict['name'] == 'New X label'


def test_plot_title_and_aes_label():
    spec_list = gg.labs(title='New plot title', x='New X label')
    assert isinstance(spec_list, FeatureSpecArray)
    for spec in spec_list.elements():
        assert isinstance(spec, FeatureSpec)
        assert spec.kind in ('scale', 'ggtitle')
        if spec.kind == 'ggtitle':
            assert spec.props()['text'] == 'New plot title'
        else:
            assert spec.props()['aesthetic'] == 'x'
            assert spec.props()['name'] == 'New X label'


def test_scale_x_discrete():
    spec = gg.scale_x_discrete(name="N", breaks=[1, 2])
    as_dict = spec.as_dict()
    assert as_dict['aesthetic'] == 'x'
    assert as_dict['name'] == 'N'
    assert as_dict['discrete']
