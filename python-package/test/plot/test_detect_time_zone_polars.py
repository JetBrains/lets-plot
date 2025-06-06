#  Copyright (c) 2025. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from datetime import datetime, date, time, timezone, timedelta
from zoneinfo import ZoneInfo

import polars as pl

from lets_plot.plot.series_meta import _detect_time_zone


def test_detect_time_zone_polars_datetime():
    utc_tz = timezone.utc
    est_tz = ZoneInfo("US/Eastern")
    offset_tz_unsupported = timezone(timedelta(hours=5), name='+05:00')
    # For UTC+5, use Etc/GMT-5 (note the sign is inverted in this notation)
    offset_tz = ZoneInfo("Etc/GMT-5")

    df = pl.DataFrame({
        'utc_datetime': pl.Series([
            datetime(2023, 1, 1, tzinfo=utc_tz),
            datetime(2023, 1, 2, tzinfo=utc_tz),
            datetime(2023, 1, 3, tzinfo=utc_tz)
        ]),
        'est_datetime': pl.Series([
            datetime(2023, 1, 1, tzinfo=est_tz),
            datetime(2023, 1, 2, tzinfo=est_tz),
            datetime(2023, 1, 3, tzinfo=est_tz)
        ]),
        'offset_datetime_unsupported': pl.Series([
            datetime(2023, 1, 1, tzinfo=offset_tz_unsupported),
            datetime(2023, 1, 2, tzinfo=offset_tz_unsupported),
            datetime(2023, 1, 3, tzinfo=offset_tz_unsupported)
        ]),
        'offset_datetime': pl.Series([
            datetime(2023, 1, 1, tzinfo=offset_tz),
            datetime(2023, 1, 2, tzinfo=offset_tz),
            datetime(2023, 1, 3, tzinfo=offset_tz)
        ]),
        'naive_datetime': pl.Series([
            datetime(2023, 1, 1),
            datetime(2023, 1, 2),
            datetime(2023, 1, 3)]),
        'string_col': pl.Series(['a', 'b', 'c'])
    })

    # Test timezone detection
    assert _detect_time_zone('utc_datetime', df) == 'UTC'
    assert _detect_time_zone('est_datetime', df) == 'US/Eastern'
    assert _detect_time_zone('offset_datetime_unsupported', df) == 'UTC'
    assert _detect_time_zone('offset_datetime', df) == 'Etc/GMT-5'
    assert _detect_time_zone('naive_datetime', df) is None
    assert _detect_time_zone('string_col', df) is None
    assert _detect_time_zone('nonexistent', df) is None


def test_detect_time_zone_polars_date():
    df = pl.DataFrame({
        'date_col': pl.Series([
            date(2023, 1, 1),
            date(2023, 1, 2),
            date(2023, 1, 3)]),
    })

    # Date columns don't have timezone info
    assert _detect_time_zone('date_col', df) is None


def test_detect_time_zone_polars_time():
    df = pl.DataFrame({
        'time_col': pl.Series([
            time(10, 30),
            time(11, 30),
            time(12, 30)]),
    })

    # Time columns don't have timezone info
    assert _detect_time_zone('time_col', df) is None
