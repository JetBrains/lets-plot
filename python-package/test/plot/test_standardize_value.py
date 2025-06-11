#  Copyright (c) 2025. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from datetime import datetime, date, time, timezone, timedelta

import jax.numpy as jnp
import numpy as np

from lets_plot._type_utils import _standardize_value


def test_standardize_value_returns_float_for_numeric_and_temporal():
    # Python numeric types
    assert isinstance(_standardize_value(42), float)
    assert isinstance(_standardize_value(3.14), float)

    # NumPy numeric types
    assert isinstance(_standardize_value(np.int8(8)), float)
    assert isinstance(_standardize_value(np.int16(16)), float)
    assert isinstance(_standardize_value(np.int32(32)), float)
    assert isinstance(_standardize_value(np.int64(64)), float)
    assert isinstance(_standardize_value(np.uint8(8)), float)
    assert isinstance(_standardize_value(np.uint16(16)), float)
    assert isinstance(_standardize_value(np.uint32(32)), float)
    assert isinstance(_standardize_value(np.uint64(64)), float)
    assert isinstance(_standardize_value(np.float16(1.6)), float)
    assert isinstance(_standardize_value(np.float32(3.2)), float)
    assert isinstance(_standardize_value(np.float64(6.4)), float)

    # JAX numeric types
    assert isinstance(_standardize_value(jnp.int8(8)), float)
    assert isinstance(_standardize_value(jnp.int16(16)), float)
    assert isinstance(_standardize_value(jnp.int32(32)), float)
    assert isinstance(_standardize_value(jnp.float16(1.6)), float)
    assert isinstance(_standardize_value(jnp.float32(3.2)), float)

    # Temporal types
    current_dt = datetime(2023, 1, 1, 12, 30, 45)
    assert isinstance(_standardize_value(current_dt), float)

    today = date(2023, 1, 1)
    assert isinstance(_standardize_value(today), float)

    now_time = time(12, 34, 56, 789)
    assert isinstance(_standardize_value(now_time), float)

    # NumPy datetime64
    assert isinstance(_standardize_value(np.datetime64('2023-01-01')), float)
    assert isinstance(_standardize_value(np.datetime64('2023-01-01T12:30:45')), float)


def test_datetime_and_datetime64_consistent_epoch_millis():
    # Test specific date/times
    test_cases = [
        # (ISO date string, expected ms since epoch)
        ("2023-01-01T00:00:00", 1672531200000),
        ("2023-01-01T12:30:45", 1672576245000),
        ("2000-02-29T23:59:59", 951868799000),
        ("1970-01-01T00:00:00", 0),
    ]

    for date_str, expected_ms in test_cases:
        # Create datetime with a UTC timezone
        dt = datetime.strptime(date_str, "%Y-%m-%dT%H:%M:%S").replace(tzinfo=timezone.utc)

        # Create numpy datetime64
        np_dt = np.datetime64(date_str)

        # Convert both using _standardize_value
        dt_ms = _standardize_value(dt)
        np_dt_ms = _standardize_value(np_dt)

        # Assert both conversion matches and are correct
        assert dt_ms == expected_ms
        assert np_dt_ms == expected_ms
        assert dt_ms == np_dt_ms


def test_date_to_epoch_millis():
    # Test cases with dates and expected millisecond values
    test_cases = [
        # (Year, Month, Day, expected ms since epoch)
        (2023, 1, 1, 1672531200000),  # 2023-01-01 00:00:00 UTC
        (2000, 2, 29, 951782400000),  # 2000-02-29 00:00:00 UTC (leap year)
        (1970, 1, 1, 0),  # 1970-01-01 00:00:00 UTC (epoch)
        (2023, 12, 31, 1703980800000),  # 2023-12-31 00:00:00 UTC
        (1969, 12, 31, -86400000)  # 1969-12-31 00:00:00 UTC (before epoch)
    ]

    for year, month, day, expected_ms in test_cases:
        # Create a date object
        d = date(year, month, day)

        # Convert using _standardize_value
        date_ms = _standardize_value(d)

        # Verify the conversion is correct
        assert date_ms == expected_ms

        # Verify conversion matches creating a datetime at midnight UTC
        dt_midnight_utc = datetime(year, month, day, 0, 0, 0, tzinfo=timezone.utc)
        dt_ms = dt_midnight_utc.timestamp() * 1000

        assert date_ms == dt_ms


def test_time_to_millis_since_midnight():
    # Test cases with time objects and expected millisecond values
    test_cases = [
        # (hour, minute, second, microsecond, expected ms since midnight)
        (0, 0, 0, 0, 0),  # 00:00:00.000 - Midnight
        (12, 0, 0, 0, 43200000),  # 12:00:00.000 - Noon
        (23, 59, 59, 999000, 86399999),  # 23:59:59.999 - Just before midnight
        (1, 30, 45, 500000, 5445500),  # 01:30:45.500 - Random time
        (6, 15, 30, 750000, 22530750)  # 06:15:30.750 - Another random time
    ]

    for hour, minute, second, microsecond, expected_ms in test_cases:
        # Create a time object
        t = time(hour, minute, second, microsecond)

        # Convert using _standardize_value
        time_ms = _standardize_value(t)

        # Verify the conversion is correct
        assert time_ms == expected_ms

        # Verify conversion matches manual calculation
        manual_ms = hour * 3600_000 + minute * 60_000 + second * 1000 + microsecond // 1000
        assert time_ms == float(manual_ms)


def test_standardize_value_datetime_consistency():
    # Test that a collection of 'datetime' values is standardized consistently regardless of the collection type.

    start_time = datetime(2025, 3, 29, 12, 0)  # Noon on March 29
    standardized_start_time = _standardize_value(start_time)

    N = 3

    # Python list
    py_list = [start_time + timedelta(minutes=i * 90) for i in range(N)]
    standardized_list = _standardize_value(py_list)
    assert standardized_start_time == standardized_list[0]

    # numpy array
    np_array = np.array([start_time + timedelta(minutes=i * 90) for i in range(N)])
    standardized_array = _standardize_value(np_array)
    assert standardized_start_time == standardized_array[0]

    assert standardized_list == standardized_array


def test_standardize_value_numpy_datetime64_consistency():
    # Test that a collection of numpy 'datetime64' values is standardized consistently regardless of the collection type.

    start_time = np.datetime64('2025-03-29T12:00')  # Noon on March 29
    standardized_start_time = _standardize_value(start_time)

    N = 3

    # Python list
    py_list = [start_time + np.timedelta64(i * 90, 'm') for i in range(N)]
    standardized_list = _standardize_value(py_list)
    assert standardized_start_time == standardized_list[0]

    # numpy array
    np_array = np.array([start_time + np.timedelta64(i * 90, 'm') for i in range(N)])
    standardized_array = _standardize_value(np_array)
    assert standardized_start_time == standardized_array[0]

    assert standardized_list == standardized_array
