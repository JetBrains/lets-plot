#  Copyright (c) 2022. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from datetime import datetime

from pandas import DataFrame, Categorical

from lets_plot import aes, ggplot, geom_point
from lets_plot.mapping import as_discrete

data_dict = {
    'v1': [(datetime(2020, 1, 1))],
    'v2': [0.0],
    'v3': [0],
    'v4': ['foo']
}
expected_series_annotations = [
    {'column': 'v1', 'type': 'datetime'},
    {'column': 'v2', 'type': 'float'},
    {'column': 'v3', 'type': 'int'},
    {'column': 'v4', 'type': 'str'}
]


def test_values_list_in_aes_doest_not_produce_series_annotations():
    # values list in layer
    p = ggplot() + geom_point(aes(x=[0, 0, 0], y=[1, 2, 3]))
    assert p.as_dict()['data_meta'] == {}

    # values list in ggplot
    p = ggplot(mapping=aes(x=[0, 0, 0], y=[1, 2, 3])) + geom_point()
    assert p.as_dict()['data_meta'] == {}


def test_as_annotated_data_dict():
    p = ggplot(data_dict) + geom_point()
    assert p.as_dict()['data_meta']['series_annotations'] == expected_series_annotations


def test_as_annotated_data_dataframe():
    df = DataFrame(data_dict)
    p = ggplot(df) + geom_point()
    assert p.as_dict()['data_meta']['series_annotations'] == expected_series_annotations


def test_as_annotated_data_polars_dataframe():
    from polars import DataFrame as plDataFrame
    df = plDataFrame(data_dict)
    p = ggplot(df) + geom_point()
    assert p.as_dict()['data_meta']['series_annotations'] == expected_series_annotations


def test_as_annotated_data_list():
    data = [datetime(2020, 1, 1)]
    p = ggplot(data) + geom_point()
    assert p.as_dict()['data_meta'] == {}


def test_as_annotated_data_string():
    data = {'x': 'foo', 'y': 'bar'}
    p = ggplot(data) + geom_point()

    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'x', 'type': 'str'},
        {'column': 'y', 'type': 'str'}
    ]


def test_dict_with_empty_series():
    data = {'x': [], 'y': []}
    p = ggplot(data) + geom_point()
    assert p.as_dict()['data_meta'] == {}


def test_df_with_empty_series():
    data = DataFrame({'x': [], 'y': []})
    p = ggplot(data) + geom_point()
    assert p.as_dict()['data_meta'] == {}


def test_df_with_none_series():
    data = DataFrame({'x': [None], 'y': [None]})
    p = ggplot(data) + geom_point()
    assert p.as_dict()['data_meta'] == {}


def test_factor_levels():
    data = {
        'v1': ['foo', 'bar'],
        'v2': [1, 2],
    }

    mapping = aes(as_discrete('v1', levels=['foo', 'bar']))
    p = ggplot(data, mapping) + geom_point()

    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v1', 'type': 'str', 'factor_levels': ['foo', 'bar']},
        {'column': 'v2', 'type': 'int'}
    ]


def test_factor_levels_with_ordering():
    data = {
        'v1': ['foo', 'bar'],
        'v2': [1, 2],
    }

    mapping = aes(
        x=as_discrete('v1', order=-1),
        y='v2',
        a=as_discrete('v1', levels=['foo', 'bar']),
        b=as_discrete('v2', levels=[2, 1], label='V2')  # do not lose the label in mapping annotations
    )

    p = ggplot(data, mapping) + geom_point()

    assert p.as_dict()['data_meta']['mapping_annotations'] == [
        {'aes': 'b', 'annotation': 'as_discrete', 'parameters': {'label': 'V2'}},
    ]
    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v1', 'type': 'str', 'factor_levels': ['foo', 'bar'], 'order': -1},
        {'column': 'v2', 'type': 'int', 'factor_levels': [2, 1]},
    ]


def test_with_mapping_annotations():
    data = {
        'v1': ['foo', 'bar'],
        'v2': [1, 2],
    }

    mapping = aes(
        x=as_discrete('v1', order_by='v2'),
        y=as_discrete('v2', order_by='v1'),
        a=as_discrete('v1', order=-1),
        b=as_discrete('v2', label='V2', levels=[2, 1])
    )
    p = ggplot(data, mapping) + geom_point()

    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v1', 'type': 'str'},
        {'column': 'v2', 'type': 'int', 'factor_levels': [2, 1]},
    ]
    assert p.as_dict()['data_meta']['mapping_annotations'] == [
        {'aes': 'x', 'annotation': 'as_discrete', 'parameters': {'order_by': 'v2'}},
        {'aes': 'a', 'annotation': 'as_discrete', 'parameters': {'order': -1}},
        {'aes': 'b', 'annotation': 'as_discrete', 'parameters': {'label': 'V2'}},
    ]


def test_pd_categorical_variable():
    df = DataFrame({
        'v': Categorical(['ch4', 'ch5', 'ch1', 'ch2'], categories=['ch5', 'ch4', 'ch2', 'ch1'], ordered=True)
    })

    p = ggplot(df) + geom_point()

    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'type': 'str', 'factor_levels': ['ch5', 'ch4', 'ch2', 'ch1']},
    ]


def test_pd_int_categorical_variable():
    df = DataFrame({
        'v': Categorical([4, 5, 2, 1], categories=[5, 4, 2, 1], ordered=True)
    })

    p = ggplot(df) + geom_point()

    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'type': 'int', 'factor_levels': [5, 4, 2, 1]},
    ]


def test_pd_int_categorical_variable_from_ggplot_dataframe():
    df = DataFrame({
        'v': Categorical([4, 5, 2, 1], categories=[5, 4, 2, 1], ordered=True)
    })

    p = ggplot(df) + geom_point()

    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'type': 'int', 'factor_levels': [5, 4, 2, 1]},
    ]


def test_pd_categorical_variable_with_order_from_mapping():
    df = DataFrame({
        'v': Categorical(values=['ch4', 'ch5', 'ch1', 'ch2'], categories=['ch5', 'ch4', 'ch2', 'ch1'], ordered=True)
    })

    p = ggplot(df, aes(x=as_discrete('v', order=-1))) + geom_point()

    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'type': 'str', 'factor_levels': ['ch5', 'ch4', 'ch2', 'ch1'], 'order': -1},
    ]


def test_as_discrete():
    d = {
        'x': [1, 2, 3, 4, 5],
        'y': [0, 0, 0, 0, 0],
        'c': [1, 2, 3, 1, 2]
    }
    p = ggplot(d) + geom_point(aes(x='x', y='y', color=as_discrete('c')))

    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'x', 'type': 'int'},
        {'column': 'y', 'type': 'int'},
        {'column': 'c', 'type': 'int'}
    ]

    # no meta for 'c' in the layer -> no series_annotations
    assert 'series_annotations' not in p.as_dict()['layers'][0]['data_meta']


def test_as_discrete_with_levels():
    d = {
        'x': [1, 2, 3, 4, 5],
        'y': [0, 0, 0, 0, 0],
        'c': [1, 2, 3, 1, 2]
    }
    p = ggplot(d) + geom_point(aes(x='x', y='y', color=as_discrete('c', levels=[3, 2, 1])))

    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'x', 'type': 'int'},
        {'column': 'y', 'type': 'int'},
        {'column': 'c', 'type': 'int'}
    ]

    assert p.as_dict()['layers'][0]['data_meta']['series_annotations'] == [
        {'column': 'c', 'factor_levels': [3, 2, 1]}
    ]
