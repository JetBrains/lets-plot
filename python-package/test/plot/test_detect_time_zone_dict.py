#  Copyright (c) 2025. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from datetime import datetime, date, time, timezone, timedelta
from zoneinfo import ZoneInfo

import numpy as np

from lets_plot.plot.series_meta import _detect_time_zone


def test_detect_time_zone_dict_datetime():
    utc_tz = timezone.utc
    est_tz = ZoneInfo("US/Eastern")
    offset_tz = timezone(timedelta(hours=5), name='+05:00')

    data_dict = {
        'utc_datetime': [
            datetime(2023, 1, 1, tzinfo=utc_tz),
            datetime(2023, 1, 2, tzinfo=utc_tz),
            datetime(2023, 1, 3, tzinfo=utc_tz)
        ],
        'est_datetime': [
            datetime(2023, 1, 1, tzinfo=est_tz),
            datetime(2023, 1, 2, tzinfo=est_tz),
            datetime(2023, 1, 3, tzinfo=est_tz)
        ],
        'offset_datetime': [
            datetime(2023, 1, 1, tzinfo=offset_tz),
            datetime(2023, 1, 2, tzinfo=offset_tz),
            datetime(2023, 1, 3, tzinfo=offset_tz)
        ],
        'naive_datetime': [
            datetime(2023, 1, 1),
            datetime(2023, 1, 2),
            datetime(2023, 1, 3)
        ],
        'mixed_datetime': [
            datetime(2023, 1, 1, tzinfo=utc_tz),
            datetime(2023, 1, 2, tzinfo=est_tz),
            datetime(2023, 1, 3, tzinfo=offset_tz)
        ],
        'mixed_with_naive': [
            datetime(2023, 1, 1),
            datetime(2023, 1, 2, tzinfo=est_tz),
            datetime(2023, 1, 3, tzinfo=utc_tz),
        ],
        'empty_col': [],
        'string_col': ['a', 'b', 'c']
    }

    assert _detect_time_zone('utc_datetime', data_dict) == 'UTC'
    assert _detect_time_zone('est_datetime', data_dict) == 'US/Eastern'
    assert _detect_time_zone('offset_datetime', data_dict) == '+05:00'
    assert _detect_time_zone('naive_datetime', data_dict) is None
    assert _detect_time_zone('mixed_datetime', data_dict) == 'UTC'  # First value's timezone
    assert _detect_time_zone('mixed_with_naive', data_dict) == 'US/Eastern'  # First value's timezone
    assert _detect_time_zone('empty_col', data_dict) is None
    assert _detect_time_zone('string_col', data_dict) is None
    assert _detect_time_zone('nonexistent', data_dict) is None


def test_detect_time_zone_dict_date():
    data_dict = {
        'date_col': [
            date(2023, 1, 1),
            date(2023, 1, 2),
            date(2023, 1, 3)],
    }

    # Date objects don't have timezone info
    assert _detect_time_zone('date_col', data_dict) is None


def test_detect_time_zone_dict_time():
    utc_tz = timezone.utc
    est_tz = ZoneInfo("US/Eastern")
    offset_tz = timezone(timedelta(hours=5), name='+05:00')

    # Dictionary with time objects
    data_dict = {
        'time_col': [
            time(10, 30),
            time(11, 30),
            time(12, 30)],
        'time_with_tz': [
            time(10, 30, tzinfo=utc_tz),
            time(11, 30, tzinfo=utc_tz),
            time(12, 30, tzinfo=utc_tz)
        ],
        'mixed_time_with_tz': [
            time(10, 30, tzinfo=utc_tz),
            time(11, 30, tzinfo=est_tz),
            time(12, 30, tzinfo=offset_tz)
        ],
    }

    # We don't extract timezone from 'time' objects.
    assert _detect_time_zone('time_col', data_dict) is None
    assert _detect_time_zone('time_with_tz', data_dict) is None
    assert _detect_time_zone('mixed_time_with_tz', data_dict) is None


def test_detect_time_zone_dict_numpy():
    data_dict = {
        'np_datetime': [
            np.datetime64('2023-01-01'),
            np.datetime64('2023-01-02'),
            np.datetime64('2023-01-03')
        ],
        'np_datetime_with_h_m': [
            np.datetime64('2025-03-29T12:00'),
            np.datetime64('2025-03-29T12:30'),
            np.datetime64('2025-03-29T13:00'),
        ]
    }

    # NumPy datetime64 objects don't store timezone information
    assert _detect_time_zone('np_datetime', data_dict) is None
    assert _detect_time_zone('np_datetime_with_h_m', data_dict) is None
