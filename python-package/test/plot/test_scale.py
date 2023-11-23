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


# Use dictionary in scale_xxx(labels)

def test_scale_labels_dict():
    spec = gg.scale_x_discrete(labels=dict(a="A", b="B", c="C"))
    as_dict = spec.as_dict()
    assert as_dict['breaks'] == ['a', 'b', 'c']
    assert as_dict['labels'] == ['A', 'B', 'C']


def test_scale_labels_dict_with_specified_breaks():
    spec = gg.scale_x_discrete(labels=dict(a="A", b="B", c="C"), breaks=['a', 'd', 'c'])
    as_dict = spec.as_dict()
    # use the order as in original 'breaks' + correct 'breaks' (without label - to the end of the list)
    assert as_dict['breaks'] == ['a', 'c', 'd']
    assert as_dict['labels'] == ['A', 'C']


def test_scale_labels_dict_no_matches_with_specified_breaks():
    spec = gg.scale_x_discrete(labels=dict(a="A", b="B", c="C"), breaks=['d', 'e'])
    as_dict = spec.as_dict()
    assert as_dict['breaks'] == ['d', 'e']
    assert as_dict['labels'] == []


def test_scale_breaks_dict():
    spec = gg.scale_x_discrete(breaks=dict(A="a", B="b", C="c"))
    as_dict = spec.as_dict()
    assert as_dict['breaks'] == ['a', 'b', 'c']
    assert as_dict['labels'] == ['A', 'B', 'C']

# Use dictionary in scale_manual(values)

def test_scale_manual_values_dict():
    spec = gg.scale_fill_manual(values=dict(a="A", b="B", c="C"))
    as_dict = spec.as_dict()
    assert as_dict['breaks'] == ['a', 'b', 'c']
    assert as_dict['values'] == ['A', 'B', 'C']


def test_scale_manual_values_dict_with_specified_breaks():
    spec = gg.scale_fill_manual(values=dict(a="A", b="B", c="C"), breaks=['a', 'c'])
    as_dict = spec.as_dict()
    # order as in original 'breaks', missing breaks - to the end of the list
    assert as_dict['breaks'] == ['a', 'c']
    assert as_dict['values'] == ['A', 'C', 'B']


def test_scale_manual_values_dict_with_specified_breaks_and_limits():
    spec = gg.scale_fill_manual(values=dict(a="A", b="B", c="C"), breaks=['a', 'c'], limits=['b', 'c'])
    as_dict = spec.as_dict()
    # priority to 'limits' as a base list to choose the order of values
    assert as_dict['breaks'] == ['a', 'c']
    assert as_dict['limits'] == ['b', 'c']
    assert as_dict['values'] == ['B', 'C', 'A']


def test_scale_manual_values_dict_no_matches_with_specified_breaks():
    spec = gg.scale_fill_manual(values=dict(a="A", b="B", c="C"), breaks=['d', 'e'])
    as_dict = spec.as_dict()
    assert as_dict['breaks'] == ['d', 'e']
    assert 'values' not in as_dict
