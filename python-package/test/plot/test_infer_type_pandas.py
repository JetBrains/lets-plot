#  Copyright (c) 2025. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from datetime import date, time

import numpy as np
import pandas as pd

from lets_plot.plot.series_meta import TYPE_DATE, TYPE_TIME, TYPE_UNKNOWN
from lets_plot.plot.series_meta import _infer_type, TYPE_INTEGER, TYPE_FLOATING, TYPE_STRING, TYPE_BOOLEAN, \
    TYPE_DATE_TIME


def test_infer_type_pandas_dataframe():
    # Create a test DataFrame with various types
    df = pd.DataFrame({
        'int_col': [1, 2, 3, 4, 5],
        'float_col': [1.1, 2.2, 3.3, 4.4, 5.5],
        'str_col': ['a', 'b', 'c', 'd', 'e'],
        'bool_col': [True, False, True, False, True],
        'datetime_col': pd.to_datetime(['2023-01-01', '2023-01-02', '2023-01-03', '2023-01-04', '2023-01-05']),
        'date_col': [date(2023, 1, 1), date(2023, 1, 2), date(2023, 1, 3), date(2023, 1, 4), date(2023, 1, 5)],
        'time_col': [time(12, 0), time(12, 30), time(13, 0), time(13, 30), time(14, 0)],
        'cat_int_col': pd.Categorical([1, 2, 3, 1, 2]),
        'cat_str_col': pd.Categorical(['x', 'y', 'z', 'x', 'y']),
        'empty_col': [None, None, None, None, None],
        'mixed_col': [1, 'a', 3.0, True, None]
    })

    # Get type info
    type_info = _infer_type(df)

    # Check inferred types
    assert type_info['int_col'] == TYPE_INTEGER
    assert type_info['float_col'] == TYPE_FLOATING
    assert type_info['str_col'] == TYPE_STRING
    assert type_info['bool_col'] == TYPE_BOOLEAN
    assert type_info['datetime_col'] == TYPE_DATE_TIME
    assert type_info['date_col'] == TYPE_DATE
    assert type_info['time_col'] == TYPE_TIME
    assert type_info['cat_int_col'] == TYPE_INTEGER
    assert type_info['cat_str_col'] == TYPE_STRING
    assert type_info['empty_col'] == TYPE_UNKNOWN

    # For mixed types, the behavior depends on pandas' infer_dtype, but it should return some value
    assert 'mixed_col' in type_info

    # Test empty dataframe
    empty_df = pd.DataFrame({})
    assert _infer_type(empty_df) == {}

    # Test a single-column empty dataframe
    empty_col_df = pd.DataFrame({'empty': []})
    assert _infer_type(empty_col_df)['empty'] == TYPE_UNKNOWN


def test_infer_type_pandas_with_numpy_types():
    # Create DataFrame with explicit NumPy types
    df = pd.DataFrame({
        'np_int8': pd.Series([1, 2, 3], dtype=np.int8),
        'np_int16': pd.Series([1, 2, 3], dtype=np.int16),
        'np_int32': pd.Series([1, 2, 3], dtype=np.int32),
        'np_int64': pd.Series([1, 2, 3], dtype=np.int64),
        'np_uint8': pd.Series([1, 2, 3], dtype=np.uint8),
        'np_float16': pd.Series([1.1, 2.2, 3.3], dtype=np.float16),
        'np_float32': pd.Series([1.1, 2.2, 3.3], dtype=np.float32),
        'np_float64': pd.Series([1.1, 2.2, 3.3], dtype=np.float64),
        'np_bool': pd.Series([True, False, True], dtype=np.bool_),
        'np_datetime64': pd.Series(np.array(['2023-01-01', '2023-01-02', '2023-01-03'], dtype='datetime64[ns]')),
    })

    type_info = _infer_type(df)

    # Verify correct type inference for NumPy types
    assert type_info['np_int8'] == TYPE_INTEGER
    assert type_info['np_int16'] == TYPE_INTEGER
    assert type_info['np_int32'] == TYPE_INTEGER
    assert type_info['np_int64'] == TYPE_INTEGER
    assert type_info['np_uint8'] == TYPE_INTEGER
    assert type_info['np_float16'] == TYPE_FLOATING
    assert type_info['np_float32'] == TYPE_FLOATING
    assert type_info['np_float64'] == TYPE_FLOATING
    assert type_info['np_bool'] == TYPE_BOOLEAN
    assert type_info['np_datetime64'] == TYPE_DATE_TIME
