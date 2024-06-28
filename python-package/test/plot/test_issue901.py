#  Copyright (c) 2024. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.


import numpy as np
import pandas as pd

import lets_plot as gg
from lets_plot._type_utils import _standardize_value


def test_issue_901():
    df = pd.DataFrame(np.random.rand(2, 2))
    assert_column_values(df)


def test_int_keys_in_dict():
    df = dict([(0, [1, 2]), (1, [3, 4])])
    assert_column_values(df)


def test_pd_int():
    assert_column_values(create_df(pd.array([0, 1], dtype=pd.Int8Dtype())))
    assert_column_values(create_df(pd.array([0, 1], dtype=pd.Int16Dtype())))
    assert_column_values(create_df(pd.array([0, 1], dtype=pd.Int32Dtype())))
    assert_column_values(create_df(pd.array([0, 1], dtype=pd.Int64Dtype())))


def test_pd_uint():
    assert_column_values(create_df(pd.array([0, 1], dtype=pd.UInt8Dtype())))
    assert_column_values(create_df(pd.array([0, 1], dtype=pd.UInt16Dtype())))
    assert_column_values(create_df(pd.array([0, 1], dtype=pd.UInt32Dtype())))
    assert_column_values(create_df(pd.array([0, 1], dtype=pd.UInt64Dtype())))


def test_np_object():
    assert_column_values(create_df(np.array([0, 1], dtype='object')))


def test_np_str_int():
    assert_column_values(create_df(np.array([0, 1], dtype='int8')))
    assert_column_values(create_df(np.array([0, 1], dtype='int16')))
    assert_column_values(create_df(np.array([0, 1], dtype='int32')))
    assert_column_values(create_df(np.array([0, 1], dtype='int64')))


def test_np_int():
    assert_column_values(create_df(np.array([0, 1], dtype=np.int8)))
    assert_column_values(create_df(np.array([0, 1], dtype=np.int16)))
    assert_column_values(create_df(np.array([0, 1], dtype=np.int32)))
    assert_column_values(create_df(np.array([0, 1], dtype=np.int64)))


def test_np_uint():
    assert_column_values(create_df(np.array([0, 1], dtype=np.uint8)))
    assert_column_values(create_df(np.array([0, 1], dtype=np.uint16)))
    assert_column_values(create_df(np.array([0, 1], dtype=np.uint32)))
    assert_column_values(create_df(np.array([0, 1], dtype=np.uint64)))


def create_df(columns):
    return pd.DataFrame(
        columns=columns,
        data=[[1, 2], [3, 4]]
    )


def assert_column_values(data):
    p = gg.ggplot(data=data) + gg.geom_point(gg.aes(x="0", y="1"))
    st = _standardize_value(p.as_dict())
    assert list(st['data'].keys()) == ['0', '1']

    p = gg.ggplot() + gg.geom_point(data=data, mapping=gg.aes(x="0", y="1"))
    st = _standardize_value(p.as_dict())
    assert list(st['layers'][0]['data'].keys()) == ['0', '1']
