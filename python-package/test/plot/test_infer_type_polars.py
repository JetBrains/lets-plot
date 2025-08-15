#  Copyright (c) 2025. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from datetime import datetime, date, time

import polars as pl

from lets_plot.plot.series_meta import _infer_type, TYPE_INTEGER, TYPE_FLOATING, TYPE_STRING, TYPE_BOOLEAN, \
    TYPE_DATE_TIME, TYPE_DATE, TYPE_TIME


def test_infer_type_polars_dataframe():
    # Create a test Polars DataFrame with various types
    df = pl.DataFrame({
        'int_col': [1, 2, 3, 4, 5],
        'float_col': [1.1, 2.2, 3.3, 4.4, 5.5],
        'str_col': ['a', 'b', 'c', 'd', 'e'],
        'bool_col': [True, False, True, False, True],
        'datetime_col': pl.Series('datetime_col', [datetime(2023, 1, i, 0, 0) for i in range(1, 6)]),
        'date_col': pl.Series('date_col', [date(2023, 1, i) for i in range(1, 6)]),
        'time_col': pl.Series('time_col', [time(i, 0) for i in range(10, 15)]),
        # Cast columns to specific types
        'int8_col': pl.Series('int8_col', [1, 2, 3, 4, 5], dtype=pl.Int8),
        'int16_col': pl.Series('int16_col', [1, 2, 3, 4, 5], dtype=pl.Int16),
        'int32_col': pl.Series('int32_col', [1, 2, 3, 4, 5], dtype=pl.Int32),
        'int64_col': pl.Series('int64_col', [1, 2, 3, 4, 5], dtype=pl.Int64),
        'uint8_col': pl.Series('uint8_col', [1, 2, 3, 4, 5], dtype=pl.UInt8),
        'float32_col': pl.Series('float32_col', [1.1, 2.2, 3.3, 4.4, 5.5], dtype=pl.Float32),
        'float64_col': pl.Series('float64_col', [1.1, 2.2, 3.3, 4.4, 5.5], dtype=pl.Float64),

        'enum_col': pl.Series('enum_col', ['a', 'b', 'c', 'a', 'b'], dtype=pl.Enum(['a', 'c', 'b'])),
        'categorical_col': pl.Series('categorical_col', ['a', 'b', 'c', 'a', 'b'],
                                     dtype=pl.Categorical(['a', 'c', 'b'])),
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

    # Check specific numeric types
    assert type_info['int8_col'] == TYPE_INTEGER
    assert type_info['int16_col'] == TYPE_INTEGER
    assert type_info['int32_col'] == TYPE_INTEGER
    assert type_info['int64_col'] == TYPE_INTEGER
    assert type_info['uint8_col'] == TYPE_INTEGER
    assert type_info['float32_col'] == TYPE_FLOATING
    assert type_info['float64_col'] == TYPE_FLOATING

    assert type_info['enum_col'] == TYPE_STRING
    assert type_info['categorical_col'] == TYPE_STRING

    # Test empty dataframe
    empty_df = pl.DataFrame({})
    assert _infer_type(empty_df) == {}

    # Test with null values
    null_df = pl.DataFrame({
        'null_col': pl.Series('null_col', [None, None, None])
    })
    # The behavior with nulls depends on how polars handles them in schema
    assert 'null_col' in _infer_type(null_df)
