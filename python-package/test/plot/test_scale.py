#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import pytest

import lets_plot as gg
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


# scale continuous

def test_scale_color_continuous():
    # identical:
    spec1 = gg.scale_continuous('color', low='red', high='blue')
    spec2 = gg.scale_color_continuous(low='red', high='blue')
    spec3 = gg.scale_color_gradient(low='red', high='blue')

    as_dict = spec1.as_dict()
    assert as_dict == spec2.as_dict()
    assert as_dict == spec3.as_dict()
    assert as_dict['aesthetic'] == 'color'
    assert as_dict['scale_mapper_kind'] == 'color_gradient'
    assert as_dict['low'] == 'red'
    assert as_dict['high'] == 'blue'


def test_scale_color_continuous_with_mapper_kind():
    # identical:
    spec1 = gg.scale_continuous('color', scale_mapper_kind='color_gradient2', low='red', mid='green', high='blue')
    spec2 = gg.scale_color_continuous(scale_mapper_kind='color_gradient2', low='red', mid='green', high='blue')

    as_dict = spec1.as_dict()
    assert as_dict == spec2.as_dict()
    assert as_dict['aesthetic'] == 'color'
    assert as_dict['scale_mapper_kind'] == 'color_gradient2'
    assert as_dict['low'] == 'red'
    assert as_dict['mid'] == 'green'
    assert as_dict['high'] == 'blue'


def test_scale_continuous_with_non_color_aesthetic():
    spec = gg.scale_continuous('size', scale_mapper_kind='size_area')
    as_dict = spec.as_dict()
    assert as_dict['aesthetic'] == 'size'
    assert as_dict['scale_mapper_kind'] == 'size_area'


def test_scale_color_discrete():
    spec1 = gg.scale_discrete('color', scale_mapper_kind='brewer', palette='Set1')
    spec2 = gg.scale_color_discrete(scale_mapper_kind='brewer', palette='Set1')
    as_dict = spec1.as_dict()
    assert as_dict == spec2.as_dict()
    assert as_dict['aesthetic'] == 'color'
    assert as_dict['scale_mapper_kind'] == 'brewer'
    assert as_dict['palette'] == 'Set1'
    assert as_dict['discrete'] is True


def test_scale_continuous():
    spec = gg.ggplot() + gg.scale_continuous(['x', 'y'])

    assert spec.as_dict()['scales'] == [
        { 'aesthetic': 'x' },
        { 'aesthetic': 'y' }
    ]


def test_scale_continuous_with_expand():
    spec = gg.ggplot() + gg.scale_continuous(['x', 'y'], expand=[0, 0])

    assert spec.as_dict()['scales'] == [
        { 'aesthetic': 'x', 'expand': [0, 0] },
        { 'aesthetic': 'y', 'expand': [0, 0] }
    ]


def test_scale_continuous_with_single_aes():
    spec = gg.ggplot() + gg.scale_continuous('x')

    assert spec.as_dict()['scales'] == [
        { 'aesthetic': 'x' }
    ]


