#  Copyright (c) 2022. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from datetime import datetime
from pandas import DataFrame
from plot.util import as_annotated_data

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


def test_as_annotated_data_list():
    data_list = [dt_value]
    _, _, data_meta = as_annotated_data(data_list, None)
    assert {} == data_meta['data_meta']


def assert_series_annotations(data, expected):
    _, _, data_meta = as_annotated_data(data, None)
    assert expected == data_meta['data_meta']['series_annotations']