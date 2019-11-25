#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import pytest

from lets_plot.plot.core import FeatureSpecArray, DummySpec
from lets_plot.plot.scale_convenience import *


@pytest.mark.parametrize('scale_spec, expected', [
    (xlim(), {'dummy-feature': True}),
    (ylim(), {'dummy-feature': True}),
])
def test_scale_spec_dummy(scale_spec, expected):
    _check(expected, scale_spec.as_dict())


@pytest.mark.parametrize('scale_spec, expected', [
    (xlim(-1, 1), {
        'aesthetic': 'x',
        'limits': [-1, 1],
    }),
    (xlim(None, 1), {
        'aesthetic': 'x',
        'limits': [None, 1],
    }),
    (xlim(-1, None), {
        'aesthetic': 'x',
        'limits': [-1, None],
    }),
    (xlim(-1), {
        'aesthetic': 'x',
        'limits': [-1],
    }),
])
def test_scale_spec_continuous_x(scale_spec, expected):
    # print('---------')
    # print(expected)
    # print(scale_spec.as_dict())
    _check(expected, scale_spec.as_dict())


@pytest.mark.parametrize('scale_spec, expected', [
    (ylim(-1, 1), {
        'aesthetic': 'y',
        'limits': [-1, 1],
    }),
    (ylim(None, 1), {
        'aesthetic': 'y',
        'limits': [None, 1],
    }),
    (ylim(-1, None), {
        'aesthetic': 'y',
        'limits': [-1, None],
    }),
    (ylim(-1), {
        'aesthetic': 'y',
        'limits': [-1],
    }),
])
def test_scale_spec_continuous_y(scale_spec, expected):
    # print('---------')
    # print(expected)
    # print(scale_spec.as_dict())
    _check(expected, scale_spec.as_dict())


@pytest.mark.parametrize('scale_spec, expected', [
    (xlim('a', 'b'), {
        'aesthetic': 'x',
        'limits': ['a', 'b'],
        'discrete': True
    }),
    (xlim(1, 'b'), {
        'aesthetic': 'x',
        'limits': [1, 'b'],
        'discrete': True
    }),
])
def test_scale_spec_discrete_x(scale_spec, expected):
    # print('---------')
    # print(expected)
    # print(scale_spec.as_dict())
    _check(expected, scale_spec.as_dict())


@pytest.mark.parametrize('scale_spec, expected', [
    (ylim('a', 'b'), {
        'aesthetic': 'y',
        'limits': ['a', 'b'],
        'discrete': True
    }),
    (ylim(1, 'b'), {
        'aesthetic': 'y',
        'limits': [1, 'b'],
        'discrete': True
    }),
])
def test_scale_spec_discrete_y(scale_spec, expected):
    # print('---------')
    # print(expected)
    # print(scale_spec.as_dict())
    _check(expected, scale_spec.as_dict())


@pytest.mark.parametrize('spec, expected_x, expected_y', [
    (lims([-1, 1], [-2, 2]), {'limits': [-1, 1]}, {'limits': [-2, 2]}),
    (lims((-1, 1), (-2, 2)), {'limits': [-1, 1]}, {'limits': [-2, 2]}),
    (lims(None, [-2, 2]), None, {'limits': [-2, 2]}),
    (lims([-1, 1], None), {'limits': [-1, 1]}, None),
    (lims(None, None), None, None),
    (lims(['a', 'b', 'c'], ['d', 'e', 'f']), {'limits': ['a', 'b', 'c'], 'discrete': True}, {'limits': ['d', 'e', 'f'], 'discrete': True}),
])
def test_scale_spec_lims_all(spec, expected_x, expected_y):
    # print('---------')
    # print(expected_x)
    # print(expected_y)
    # print(spec.as_dict())

    if expected_x is None and expected_y is None:
        assert isinstance(spec, DummySpec)
    elif expected_x is None:
        _check({'aesthetic': 'y', **expected_y}, spec.as_dict())
    elif expected_y is None:
        _check({'aesthetic': 'x', **expected_x}, spec.as_dict())
    else:
        assert isinstance(spec, FeatureSpecArray)
        assert len(spec.elements()) == 2

        scale_x = spec.elements()[0]
        _check({'aesthetic': 'x', **expected_x}, scale_x.as_dict())

        scale_y = spec.elements()[1]
        _check({'aesthetic': 'y', **expected_y}, scale_y.as_dict())


def _check(expected, actual):
    for key in set(expected) | set(actual):
        if key in expected:
            assert actual[key] == expected[key]
        else:
            assert actual[key] is None
