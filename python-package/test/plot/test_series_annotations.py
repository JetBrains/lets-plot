#  Copyright (c) 2022. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from datetime import datetime
from pandas import DataFrame, Categorical
from lets_plot import aes
from lets_plot.mapping import as_discrete
from lets_plot.plot.util import as_annotated_data

dt_value = datetime(2020, 1, 1)
data_dict = {
    'v1': [dt_value],
    'v2': [0.0],
    'v3': [dt_value],
    'v4': ['foo']
}
expected_series_annotations = [
    {'column': 'v1', 'type': 'datetime'},
    {'column': 'v3', 'type': 'datetime'}
]


def test_as_annotated_data_dict():
    assert_series_annotations(data_dict, expected_series_annotations)


def test_as_annotated_data_dataframe():
    df = DataFrame(data_dict)
    assert_series_annotations(df, expected_series_annotations)


def test_as_annotated_data_polars_dataframe():
    from polars import DataFrame as plDataFrame
    df = plDataFrame(data_dict)
    assert_series_annotations(df, expected_series_annotations)


def test_as_annotated_data_list():
    data = [dt_value]
    assert {} == get_data_meta(data)


def test_as_annotated_data_string():
    data = {'x': 'foo', 'y': 'bar'}
    assert {} == get_data_meta(data)


def test_dict_with_empty_series():
    data = {'x': [], 'y': []}
    assert {} == get_data_meta(data)


def test_df_with_empty_series():
    data = DataFrame({'x': [], 'y': []})
    assert {} == get_data_meta(data)


def get_data_meta(data, mapping=None):
    _, _, data_meta = as_annotated_data(data, mapping)
    return data_meta['data_meta']


def assert_series_annotations(data, expected):
    data_meta = get_data_meta(data, None)
    assert expected == data_meta['series_annotations']


# factor levels

data_dict2 = {
    'v1': ['foo', 'bar'],
    'v2': [1, 2],
    # add pandas Categorical
}


def test_factor_levels():
    mapping = aes(as_discrete('v1', levels=['foo', 'bar']))
    data_meta = get_data_meta(data_dict2, mapping)
    expected_factor_levels = [
        {'column': 'v1', 'factor_levels': ['foo', 'bar'], 'order': None}
    ]
    assert expected_factor_levels == data_meta['series_annotations']


def test_factor_levels_with_ordering():
    mapping = aes(
        as_discrete('v1', order=-1),
        'v2',
        a=as_discrete('v1', levels=['foo', 'bar']),
        b=as_discrete('v2', levels=[2, 1])
    )
    data_meta = get_data_meta(data_dict2, mapping)
    expected_factor_levels = [
        {'column': 'v1', 'factor_levels': ['foo', 'bar'], 'order': -1},
        {'column': 'v2', 'factor_levels': [2, 1], 'order': None}
    ]
    assert expected_factor_levels == data_meta['series_annotations']
    assert 'mapping_annotations' not in data_meta


def test_with_mapping_annotations():
    mapping = aes(
        x=as_discrete('v1', order_by='v2'),
        y=as_discrete('v2', order_by='v1'),
        a=as_discrete('v1', order=-1),
        b=as_discrete('v2', levels=[2, 1])
    )
    data_meta = get_data_meta(data_dict2, mapping)
    expected_mapping_annotations = [
        {'aes': 'x', 'annotation': 'as_discrete', 'parameters': {'label': 'v1', 'order_by': 'v2', 'order': None}},
        {'aes': 'a', 'annotation': 'as_discrete', 'parameters': {'label': 'v1', 'order_by': None, 'order': -1}}
    ]
    expected_factor_levels = [
        {'column': 'v2', 'factor_levels': [2, 1], 'order': None}
    ]
    assert expected_factor_levels == data_meta['series_annotations']
    assert expected_mapping_annotations == data_meta['mapping_annotations']


def test_pd_categorical_variable():
    x_order = ['ch5', 'ch4', 'ch2', 'ch1']
    x = ['ch4', 'ch5', 'ch1', 'ch2']
    df = DataFrame({'v': x})
    df['v'] = Categorical(df.v, categories=x_order, ordered=True)
    data_meta = get_data_meta(df)
    expected_factor_levels = [
        {'column': 'v', 'factor_levels': ['ch5', 'ch4', 'ch2', 'ch1'], 'order': None}
    ]
    assert expected_factor_levels == data_meta['series_annotations']


def test_pd_categorical_variable_with_order_from_mapping():
    x_order = ['ch5', 'ch4', 'ch2', 'ch1']
    x = ['ch4', 'ch5', 'ch1', 'ch2']
    df = DataFrame({'v': x})
    df['v'] = Categorical(df.v, categories=x_order, ordered=True)
    mapping = aes(
        x=as_discrete('v', order=-1)
    )
    data_meta = get_data_meta(df, mapping)

    expected_factor_levels = [
        {'column': 'v', 'factor_levels': ['ch5', 'ch4', 'ch2', 'ch1'], 'order': -1}
    ]
    assert expected_factor_levels == data_meta['series_annotations']
