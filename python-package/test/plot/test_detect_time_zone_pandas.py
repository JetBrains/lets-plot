#  Copyright (c) 2025. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from datetime import datetime, date, time, timezone, timedelta

import numpy as np
import pandas as pd

from lets_plot.plot.series_meta import _detect_time_zone


def test_detect_time_zone_pandas_datetime():
    df = pd.DataFrame({
        'utc_datetime': pd.Series(pd.date_range('2023-01-01', periods=3, tz='UTC')),
        'est_datetime': pd.Series(pd.date_range('2023-01-01', periods=3, tz='US/Eastern')),
        'offset_datetime': pd.Series(pd.date_range('2023-01-01', periods=3, tz=timezone(timedelta(hours=5)))),
        'naive_datetime': pd.Series(pd.date_range('2023-01-01', periods=3)),
        'mixed_datetime': pd.Series([
            datetime(2023, 1, 1, tzinfo=timezone.utc),
            datetime(2023, 1, 2, tzinfo=timezone(timedelta(hours=-5))),
            datetime(2023, 1, 3, tzinfo=timezone(timedelta(hours=1)))
        ]),
        'empty_col': pd.Series([], dtype='object'),
        'string_col': ['a', 'b', 'c']
    })

    assert _detect_time_zone('utc_datetime', df) == 'UTC'
    assert _detect_time_zone('est_datetime', df) == 'US/Eastern'
    tz_string = _detect_time_zone('offset_datetime', df)
    assert tz_string == 'UTC+05:00' or tz_string == '+05:00'  # Handle different pandas versions
    assert _detect_time_zone('naive_datetime', df) is None
    assert _detect_time_zone('mixed_datetime', df) is None
    assert _detect_time_zone('empty_col', df) is None
    assert _detect_time_zone('string_col', df) is None
    assert _detect_time_zone('nonexistent', df) is None


def test_detect_time_zone_pandas_date():
    df = pd.DataFrame({
        'date_col': pd.Series([
            date(2023, 1, 1),
            date(2023, 1, 2),
            date(2023, 1, 3)]),
        'date_as_object': pd.Series([
            date(2023, 1, 1),
            date(2023, 1, 2),
            date(2023, 1, 3)],
            dtype='object'),
    })

    # Date columns don't have timezone info
    assert _detect_time_zone('date_col', df) is None
    assert _detect_time_zone('date_as_object', df) is None


def test_detect_time_zone_pandas_time():
    df = pd.DataFrame({
        'time_col': pd.Series([
            time(10, 30),
            time(11, 30),
            time(12, 30)]),
        'time_with_tz': pd.Series([
            time(10, 30, tzinfo=timezone.utc),
            time(11, 30, tzinfo=timezone.utc),
            time(12, 30, tzinfo=timezone.utc)
        ]),
        'mixed_time_with_tz': pd.Series([
            time(10, 30, tzinfo=timezone.utc),
            time(11, 30, tzinfo=timezone(timedelta(hours=-5))),
            time(12, 30, tzinfo=timezone(timedelta(hours=1)))
        ]),
    })

    assert _detect_time_zone('time_col', df) is None

    # Pandas doesn't expose timezone through dt accessor for `time` objects
    assert _detect_time_zone('time_with_tz', df) is None

    assert _detect_time_zone('mixed_time_with_tz', df) is None


def test_detect_time_zone_pandas_numpy():
    # NumPy datetime columns
    df = pd.DataFrame({
        'np_datetime': pd.Series(
            np.array(['2023-01-01', '2023-01-02', '2023-01-03'], dtype='datetime64[ns]')
        ),
        'np_datetime_utc': pd.Series(
            np.array(['2023-01-01', '2023-01-02', '2023-01-03'], dtype='datetime64[ns]')
        ).dt.tz_localize('UTC'),
        'np_datetime_est': pd.Series(
            np.array(['2023-01-01', '2023-01-02', '2023-01-03'], dtype='datetime64[ns]')
        ).dt.tz_localize('US/Eastern'),
        'empty_np_datetime': pd.Series([], dtype='datetime64[ns]')
    })

    assert _detect_time_zone('np_datetime', df) is None

    # NumPy datetime64 converted to pandas datetime with timezone
    assert _detect_time_zone('np_datetime_utc', df) == 'UTC'
    assert _detect_time_zone('np_datetime_est', df) == 'US/Eastern'

    assert _detect_time_zone('empty_np_datetime', df) is None
