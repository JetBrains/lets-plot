#  Copyright (c) 2025. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from datetime import datetime, date, time

import pytest

from lets_plot._type_utils import LazyModule
from lets_plot.plot import series_meta

polars = LazyModule("polars")


@pytest.mark.skipif(not polars, reason="Requires polars")
def test_infer_type_polars_dataframe():
    # Create a test Polars DataFrame with various types
    df = polars.DataFrame({
        'int_col': [1, 2, 3, 4, 5],
        'float_col': [1.1, 2.2, 3.3, 4.4, 5.5],
        'str_col': ['a', 'b', 'c', 'd', 'e'],
        'bool_col': [True, False, True, False, True],
        'datetime_col': polars.Series('datetime_col', [datetime(2023, 1, i, 0, 0) for i in range(1, 6)]),
        'date_col': polars.Series('date_col', [date(2023, 1, i) for i in range(1, 6)]),
        'time_col': polars.Series('time_col', [time(i, 0) for i in range(10, 15)]),
        # Cast columns to specific types
        'int8_col': polars.Series('int8_col', [1, 2, 3, 4, 5], dtype=polars.Int8),
        'int16_col': polars.Series('int16_col', [1, 2, 3, 4, 5], dtype=polars.Int16),
        'int32_col': polars.Series('int32_col', [1, 2, 3, 4, 5], dtype=polars.Int32),
        'int64_col': polars.Series('int64_col', [1, 2, 3, 4, 5], dtype=polars.Int64),
        'uint8_col': polars.Series('uint8_col', [1, 2, 3, 4, 5], dtype=polars.UInt8),
        'float32_col': polars.Series('float32_col', [1.1, 2.2, 3.3, 4.4, 5.5], dtype=polars.Float32),
        'float64_col': polars.Series('float64_col', [1.1, 2.2, 3.3, 4.4, 5.5], dtype=polars.Float64),

        'enum_col': polars.Series('enum_col', ['a', 'b', 'c', 'a', 'b'], dtype=polars.Enum(['a', 'c', 'b'])),
        'categorical_col': polars.Series('categorical_col', ['a', 'b', 'c', 'a', 'b'],
                                         dtype=polars.Categorical),
    })

    # Get type info
    type_info = series_meta._infer_type(df)

    # Check inferred types
    assert type_info['int_col'] == series_meta.TYPE_INTEGER
    assert type_info['float_col'] == series_meta.TYPE_FLOATING
    assert type_info['str_col'] == series_meta.TYPE_STRING
    assert type_info['bool_col'] == series_meta.TYPE_BOOLEAN
    assert type_info['datetime_col'] == series_meta.TYPE_DATE_TIME
    assert type_info['date_col'] == series_meta.TYPE_DATE
    assert type_info['time_col'] == series_meta.TYPE_TIME

    # Check specific numeric types
    assert type_info['int8_col'] == series_meta.TYPE_INTEGER
    assert type_info['int16_col'] == series_meta.TYPE_INTEGER
    assert type_info['int32_col'] == series_meta.TYPE_INTEGER
    assert type_info['int64_col'] == series_meta.TYPE_INTEGER
    assert type_info['uint8_col'] == series_meta.TYPE_INTEGER
    assert type_info['float32_col'] == series_meta.TYPE_FLOATING
    assert type_info['float64_col'] == series_meta.TYPE_FLOATING

    assert type_info['enum_col'] == series_meta.TYPE_STRING
    assert type_info['categorical_col'] == series_meta.TYPE_STRING

    # Test empty dataframe
    empty_df = polars.DataFrame({})
    assert series_meta._infer_type(empty_df) == {}

    # Test with null values
    null_df = polars.DataFrame({
        'null_col': polars.Series('null_col', [None, None, None])
    })
    # The behavior with nulls depends on how polars handles them in schema
    assert 'null_col' in series_meta._infer_type(null_df)
