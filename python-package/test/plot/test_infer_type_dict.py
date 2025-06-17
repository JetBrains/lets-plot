#  Copyright (c) 2025. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from datetime import datetime, date, time

import numpy as np

from lets_plot.plot.series_meta import _infer_type, TYPE_INTEGER, TYPE_FLOATING, TYPE_STRING, TYPE_BOOLEAN, \
    TYPE_DATE_TIME, TYPE_DATE, TYPE_TIME, TYPE_UNKNOWN


def test_infer_type_dict():
    # Create a test dictionary with various types
    data_dict = {
        'int_col': [1, 2, 3, 4, 5],
        'float_col': [1.1, 2.2, 3.3, 4.4, 5.5],
        'str_col': ['a', 'b', 'c', 'd', 'e'],
        'bool_col': [True, False, True, False, True],
        'datetime_col': [datetime(2023, 1, i, 0, 0) for i in range(1, 6)],
        'date_col': [date(2023, 1, i) for i in range(1, 6)],
        'time_col': [time(i, 0) for i in range(10, 15)],
        'np_int_col': [np.int32(i) for i in range(1, 6)],
        'np_float_col': [np.float32(i + 0.5) for i in range(1, 6)],
        'np_datetime_col': [np.datetime64(f'2023-01-0{i}') for i in range(1, 6)],
        'empty_col': [],
        'none_col': [None, None, None],
        'mixed_numeric_col': [1, 2.5, 3, 4.5, 5],
        'mixed_types_col': [1, 'a', True, datetime(2023, 1, 1)]
    }

    # Get type info
    type_info = _infer_type(data_dict)

    # Check inferred types
    assert type_info['int_col'] == TYPE_INTEGER
    assert type_info['float_col'] == TYPE_FLOATING
    assert type_info['str_col'] == TYPE_STRING
    assert type_info['bool_col'] == TYPE_BOOLEAN
    assert type_info['datetime_col'] == TYPE_DATE_TIME
    assert type_info['date_col'] == TYPE_DATE
    assert type_info['time_col'] == TYPE_TIME

    # Check numpy types
    assert type_info['np_int_col'] == TYPE_INTEGER
    assert type_info['np_float_col'] == TYPE_FLOATING
    assert type_info['np_datetime_col'] == TYPE_DATE_TIME

    # Check special cases
    assert type_info['empty_col'] == TYPE_UNKNOWN
    assert type_info['none_col'] == TYPE_UNKNOWN
    assert type_info['mixed_numeric_col'] == TYPE_FLOATING
    assert 'mixed types' in type_info['mixed_types_col']

    # Test empty dictionary
    empty_dict = {}
    assert _infer_type(empty_dict) == {}
