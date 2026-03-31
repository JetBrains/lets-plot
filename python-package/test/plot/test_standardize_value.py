#  Copyright (c) 2025. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import decimal
import math
from datetime import datetime, date, time, timezone, timedelta

import jax.numpy as jnp
import numpy as np
import pandas as pd
import polars as pl
import pytest

from lets_plot._type_utils import _standardize_value, is_ndarray, LazyModule


def test_standardize_value_types():
    # Test that common types are standardized correctly
    assert _standardize_value(None) is None
    assert _standardize_value(42) == 42.0
    assert _standardize_value(3.14) == 3.14
    assert _standardize_value(decimal.Decimal('3.14')) == 3.14
    assert _standardize_value(True) is True
    assert _standardize_value(False) is False
    assert _standardize_value("hello") == "hello"


def test_standardize_temporal_types():
    assert _standardize_value(datetime(2023, 1, 1, 12, 30, 45)) == 1672572645000.0
    assert _standardize_value(date(2023, 1, 1)) == 1672531200000.0
    assert _standardize_value(time(12, 34, 56, 789)) == 45296000.0
    assert _standardize_value(timedelta(days=1, hours=2, minutes=30)) == 95400000.0


def test_standardize_containers():
    # Test that common containers are standardized correctly
    assert _standardize_value([1, 2, 3]) == [1.0, 2.0, 3.0]
    assert _standardize_value((4, 5)) == [4.0, 5.0]
    assert _standardize_value({6, 7}) == [6.0, 7.0]
    assert _standardize_value({'a': 8, 'b': 9}) == {'a': 8.0, 'b': 9.0}


def test_standardize_external_numeric_types():
    # NumPy numeric types
    assert _standardize_value([np.int8(8)]) == [8.0]
    assert _standardize_value([np.int16(16)]) == [16.0]
    assert _standardize_value([np.int32(32)]) == [32.0]
    assert _standardize_value([np.int64(64)]) == [64.0]
    assert _standardize_value([np.uint8(8)]) == [8.0]
    assert _standardize_value([np.uint16(16)]) == [16.0]
    assert _standardize_value([np.uint32(32)]) == [32.0]
    assert _standardize_value([np.uint64(64)]) == [64.0]
    assert _standardize_value([np.float16(1.6)]) == [1.599609375]
    assert _standardize_value([np.float32(3.2)]) == [3.200000047683716]
    assert _standardize_value([np.float64(6.4)]) == [6.4]

    # JAX numeric types
    assert _standardize_value([jnp.int8(8)]) == [8.0]
    assert _standardize_value([jnp.int16(16)]) == [16.0]
    assert _standardize_value([jnp.int32(32)]) == [32.0]
    assert _standardize_value([jnp.float16(1.6)]) == [1.599609375]
    assert _standardize_value([jnp.float32(3.2)]) == [3.200000047683716]


def test_standardize_external_temporal_types():
    # NumPy datetime64
    assert _standardize_value([np.datetime64('2023-01-01')]) == [1672531200000.0]
    assert _standardize_value([np.datetime64('2023-01-01T12:30:45')]) == [1672576245000.0]
    assert _standardize_value(np.array(np.datetime64('2023-01-01T12:30:45'))) == 1672576245000.0  # 0D array

    # NumPy timedelta64
    assert _standardize_value([np.timedelta64(1, 'D')]) == [86400000.0]
    assert _standardize_value([np.timedelta64(2, 'h')]) == [7200000.0]
    assert _standardize_value([np.timedelta64(30, 'm')]) == [1800000.0]
    assert _standardize_value([np.timedelta64(45, 's')]) == [45000.0]
    assert _standardize_value([np.timedelta64(500, 'ms')]) == [500.0]
    assert _standardize_value([np.timedelta64('NaT')]) == [None]

    # Pandas Timestamp and Timedelta
    assert _standardize_value([pd.Timestamp('2023-01-01')]) == [1672531200000.0]
    assert _standardize_value([pd.Timestamp('2023-01-01T12:30:45')]) == [1672576245000.0]
    assert _standardize_value([pd.Timedelta(days=1)]) == [86400000.0]
    assert _standardize_value([pd.Timedelta(hours=2)]) == [7200000.0]
    assert _standardize_value([pd.Timedelta(minutes=30)]) == [1800000.0]
    assert _standardize_value([pd.Timedelta(seconds=45)]) == [45000.0]
    assert _standardize_value([pd.Timedelta(milliseconds=500)]) == [500.0]
    assert _standardize_value([pd.NaT]) == [None]


def test_standardize_value_returns_na_for_missing_values():
    # Standard Python missing values
    assert _standardize_value([None]) == [None]
    assert _standardize_value([float('nan')]) == [None]
    assert _standardize_value([math.nan]) == [None]

    # Pandas missing values
    assert _standardize_value([pd.NaT]) == [None]
    assert _standardize_value([pd.NA]) == [None]

    # NumPy floating NaNs
    assert _standardize_value([np.nan]) == [None]
    assert _standardize_value([np.float16('nan')]) == [None]
    assert _standardize_value([np.float32('nan')]) == [None]
    assert _standardize_value([np.float64('nan')]) == [None]

    # NumPy time/date NaNs
    assert _standardize_value([np.datetime64('NaT')]) == [None]
    assert _standardize_value([np.timedelta64('NaT')]) == [None]

    # Python standard library NaNs
    assert _standardize_value([decimal.Decimal('NaN')]) == [None]
    assert _standardize_value([decimal.Decimal('sNaN')]) == [None]
    assert _standardize_value([decimal.Decimal('Infinity')]) == [None]
    assert _standardize_value([decimal.Decimal('-Infinity')]) == [None]


def test_collection_of_strings():
    assert _standardize_value(['foo']) == ['foo']

    # NumPy array of strings
    assert _standardize_value(np.array(['foo'])) == ['foo']
    assert _standardize_value(np.array(['foo'])) == ['foo']

    # Pandas Series and array of strings
    assert _standardize_value(pd.Series(['foo'])) == ['foo']
    assert _standardize_value(pd.array(['foo'])) == ['foo']

    # JAX array of strings does not support string data types
    # TypeError: Value 'foo' with dtype <U3 is not a valid JAX array type. Only arrays of numeric types are supported by JAX.
    #assert _standardize_value(jnp.array(['foo'])) == ['foo']


def test_collection_of_integers():
    # Standard Python integers
    assert _standardize_value([1, 2, 3]) == [1.0, 2.0, 3.0]

    # NumPy array of integers
    assert _standardize_value(np.array([1, 2, 3], dtype=np.int32)) == [1.0, 2.0, 3.0]

    # Pandas Series and array of integers
    assert _standardize_value(pd.Series([1, 2, 3], dtype=pd.Int64Dtype())) == [1.0, 2.0, 3.0]
    assert _standardize_value(pd.array([1, 2, 3], dtype=pd.Int64Dtype())) == [1.0, 2.0, 3.0]

    # JAX array of integers
    assert _standardize_value(jnp.array([1, 2, 3], dtype=jnp.int32)) == [1.0, 2.0, 3.0]


def test_collection_of_floats():
    # Standard Python floats
    assert _standardize_value([1.5, 2.5, 3.5]) == [1.5, 2.5, 3.5]

    # NumPy array of floats
    assert _standardize_value(np.array([1.5, 2.5, 3.5], dtype=np.float32)) == [1.5, 2.5, 3.5]

    # Pandas Series and array of floats
    assert _standardize_value(pd.Series([1.5, 2.5, 3.5], dtype=pd.Float64Dtype())) == [1.5, 2.5, 3.5]
    assert _standardize_value(pd.array([1.5, 2.5, 3.5], dtype=pd.Float64Dtype())) == [1.5, 2.5, 3.5]

    # JAX array of floats
    assert _standardize_value(jnp.array([1.5, 2.5, 3.5])) == [1.5, 2.5, 3.5]


def test_collection_of_nans():
    # Test that collections containing NaN values are standardized to None
    assert _standardize_value([float('nan'), math.nan, None]) == [None, None, None]

    # NumPy array and Pandas Series of NaN values
    assert _standardize_value(np.array([np.nan], dtype=np.float32)) == [None]
    assert _standardize_value(np.array([np.nan, np.nan], dtype=np.float32)) == [None, None]
    assert _standardize_value(np.array(np.nan, dtype=np.float32)) == None  # 0D array to list to match pandas behavior
    assert _standardize_value(np.array(np.datetime64('NaT'))) == None  # 0D array to list to match pandas behavior

    # Pandas Series of NaN values
    assert _standardize_value(pd.Series(pd.NA)) == [None]
    assert _standardize_value(pd.array([pd.NA])) == [None]

    # JAX array of NaN values
    assert _standardize_value(jnp.array(float('nan'))) == None
    assert _standardize_value(jnp.array([float('nan')])) == [None]


def test_collection_of_none():
    # Test that collections containing None values are standardized correctly
    assert _standardize_value([None]) == [None]
    assert _standardize_value((None,)) == [None]
    assert _standardize_value({None}) == [None]
    assert _standardize_value({None: None}) == {None: None}

    # NumPy array and Pandas Series of None values
    assert _standardize_value(np.array([None])) == [None]

    # Pandas Series of None values
    assert _standardize_value(pd.Series([None])) == [None]
    assert _standardize_value(pd.array([None])) == [None]

    # jnp does not allow None values in arrays
    # ValueError: None is not a valid value for jnp.array


def test_empty_collections():
    # Test that empty collections are standardized correctly
    assert _standardize_value([]) == []
    assert _standardize_value(()) == []
    assert _standardize_value(set()) == []
    assert _standardize_value({}) == {}

    # NumPy array and Pandas Series of empty collections
    assert _standardize_value(np.array([])) == []

    # Pandas Series of empty collections
    assert _standardize_value(pd.Series([])) == []
    assert _standardize_value(pd.array([])) == []

    # JAX array of empty collections
    assert _standardize_value(jnp.array([])) == []


@pytest.mark.timeout(10)
def test_perf_float_numpy_array():
    huge_arr = np.full((3_000 * 3_000), 42.0, dtype=np.float32)

    huge_arr[0] = np.nan
    huge_arr[1] = np.inf
    huge_arr[2] = -np.inf

    result = _standardize_value(pd.DataFrame({'data': huge_arr}))['data']

    assert len(result) == 9_000_000

    assert result[0] is None      # NaN converted to None
    assert result[1] is None      # +Inf converted to None
    assert result[2] is None      # -Inf converted to None
    assert result[3] == 42.0      # Normal constant untouched
    assert result[-1] == 42.0    # Normal constant untouched


@pytest.mark.timeout(10)
def test_perf_float_pandas_array():
    large_array = pd.array([42.0] * (3_000 * 3_000))
    large_array[0] = np.nan
    large_array[1] = np.inf
    large_array[2] = -np.inf

    standardized_large_array = _standardize_value(pd.DataFrame({'data': large_array}))['data']

    assert len(standardized_large_array) == 9000000
    assert standardized_large_array[0] is None      # NaN converted to None
    assert standardized_large_array[1] is None      # +Inf converted to None
    assert standardized_large_array[2] is None      # -Inf converted to None
    assert standardized_large_array[3] == 42.0      # Normal constant untouched
    assert standardized_large_array[-1] == 42.0     # Normal constant untouched


@pytest.mark.timeout(10)
def test_perf_float_pandas_series():
    large_series = pd.Series([42.0] * (3_000 * 3_000))
    large_series[0] = np.nan
    large_series[1] = np.inf
    large_series[2] = -np.inf

    standardized_large_series = _standardize_value(pd.DataFrame({'data': large_series}))['data']

    assert len(standardized_large_series) == 9000000
    assert standardized_large_series[0] is None      # NaN converted to None
    assert standardized_large_series[1] is None      # +Inf converted to None
    assert standardized_large_series[2] is None      # -Inf converted to None
    assert standardized_large_series[3] == 42.0      # Normal constant untouched
    assert standardized_large_series[-1] == 42.0     # Normal constant untouched


@pytest.mark.timeout(10)
def test_perf_float_jax_array():
    huge_arr = jnp.full((3_000 * 3_000), 42.0, dtype=jnp.float32)

    huge_arr = huge_arr.at[0].set(jnp.nan)
    huge_arr = huge_arr.at[1].set(jnp.inf)
    huge_arr = huge_arr.at[2].set(-jnp.inf)

    result = _standardize_value(pd.DataFrame({'data': huge_arr}))['data']

    assert len(result) == 9_000_000

    assert result[0] is None      # NaN converted to None
    assert result[1] is None      # +Inf converted to None
    assert result[2] is None      # -Inf converted to None
    assert result[3] == 42.0      # Normal constant untouched
    assert result[-1] == 42.0    # Normal constant untouched


@pytest.mark.timeout(10)
def test_perf_float_polars_series():
    large_series = pl.Series([42.0] * (3_000 * 3_000))
    large_series[0] = np.nan
    large_series[1] = np.inf
    large_series[2] = -np.inf

    standardized_large_series = _standardize_value(pl.DataFrame({'data': large_series}))['data']

    assert len(standardized_large_series) == 9000000
    assert standardized_large_series[0] is None      # NaN converted to None
    assert standardized_large_series[1] is None      # +Inf converted to None
    assert standardized_large_series[2] is None      # -Inf converted to None
    assert standardized_large_series[3] == 42.0      # Normal constant untouched
    assert standardized_large_series[-1] == 42.0     # Normal constant untouched


@pytest.mark.timeout(10)
def test_perf_datetime_numpy_array():
    huge_arr = np.array([np.datetime64('2023-01-01T12:30:45')] * (3_000 * 3_000))

    huge_arr[0] = np.datetime64('NaT')
    huge_arr[1] = np.datetime64('NaT')
    huge_arr[2] = np.datetime64('NaT')

    result = _standardize_value(pd.DataFrame({'data': huge_arr}))['data']

    assert len(result) == 9_000_000

    assert result[0] is None      # NaT converted to None
    assert result[1] is None      # NaT converted to None
    assert result[2] is None      # NaT converted to None
    assert result[3] == 1672576245000.0      # Normal datetime converted to epoch millis
    assert result[-1] == 1672576245000.0    # Normal datetime converted to epoch millis


@pytest.mark.timeout(10)
def test_perf_datetime_pandas_array():
    large_array = pd.array([pd.Timestamp('2023-01-01T12:30:45')] * (3_000 * 3_000))
    large_array[0] = pd.NaT
    large_array[1] = pd.NaT
    large_array[2] = pd.NaT

    standardized_large_array = _standardize_value(pd.DataFrame({'data': large_array}))['data']

    assert len(standardized_large_array) == 9000000
    assert standardized_large_array[0] is None      # NaT converted to None
    assert standardized_large_array[1] is None      # NaT converted to None
    assert standardized_large_array[2] is None      # NaT converted to None
    assert standardized_large_array[3] == 1672576245000.0      # Normal datetime converted to epoch millis
    assert standardized_large_array[-1] == 1672576245000.0     # Normal datetime converted to epoch millis


@pytest.mark.timeout(10)
def test_perf_datetime_pandas_series():
    large_series = pd.Series([pd.Timestamp('2023-01-01T12:30:45')] * (3_000 * 3_000))
    large_series[0] = pd.NaT
    large_series[1] = pd.NaT
    large_series[2] = pd.NaT

    standardized_large_series = _standardize_value(pd.DataFrame({'data': large_series}))['data']

    assert len(standardized_large_series) == 9000000
    assert standardized_large_series[0] is None      # NaT converted to None
    assert standardized_large_series[1] is None      # NaT converted to None
    assert standardized_large_series[2] is None      # NaT converted to None
    assert standardized_large_series[3] == 1672576245000.0      # Normal datetime converted to epoch millis
    assert standardized_large_series[-1] == 1672576245000.0     # Normal datetime converted to epoch millis


def test_is_ndarray():
    assert is_ndarray(np.array([1, 2, 3])) == True
    assert is_ndarray(jnp.array([4, 5, 6])) == True


def test_lazy_is_instance():
    lazy_numpy = LazyModule('numpy')
    assert lazy_numpy.lazy_is_instance(np.array([1, 2, 3]), 'ndarray') == True

    lazy_jax = LazyModule('jax')
    assert lazy_jax.lazy_is_instance(jnp.array([1, 2, 3]), 'numpy.ndarray') == True


def test_shapely_geometry():
    from shapely.geometry import Point, Polygon

    # Test with a Point geometry
    point = Point(1, 2)
    standardized_point = _standardize_value(point)
    assert standardized_point == '{"type": "Point", "coordinates": [1.0, 2.0]}'

    # Test with a Polygon geometry
    polygon = Polygon([(0, 0), (1, 0), (1, 1), (0, 1)])
    standardized_polygon = _standardize_value(polygon)
    assert standardized_polygon == '{"type": "Polygon", "coordinates": [[[0.0, 0.0], [1.0, 0.0], [1.0, 1.0], [0.0, 1.0], [0.0, 0.0]]]}'


def test_geodataframe_with_shapely_geometry():
    import geopandas as gpd
    from shapely.geometry import Point

    # Create a GeoDataFrame with a Point geometry
    gdf = gpd.GeoDataFrame({'geometry': [Point(1, 2)]})

    standardized_gdf = _standardize_value(gdf)

    assert standardized_gdf['geometry'][0] == '{"type": "Point", "coordinates": [1.0, 2.0]}'


def test_standardize_value_polars_enum_and_categorical():
    import polars as pl

    # Create a Polars DataFrame with Enum and Categorical columns
    df = pl.DataFrame({
        'enum_col': pl.Series('enum_col', ['a', 'b', 'c', 'a', 'b'],
                              dtype=pl.Enum(['a', 'c', 'b'])),
        'categorical_col': pl.Series('categorical_col', ['x', 'y', 'z', 'x', 'y'],
                                     dtype=pl.Categorical)
    })

    # Standardize the DataFrame
    standardized_df = _standardize_value(df)

    # Check that the values in the standardized dataframe are strings
    assert all(isinstance(v, str) for v in standardized_df['enum_col'])
    assert all(isinstance(v, str) for v in standardized_df['categorical_col'])

    # Verify specific values
    assert standardized_df['enum_col'] == ['a', 'b', 'c', 'a', 'b']
    assert standardized_df['categorical_col'] == ['x', 'y', 'z', 'x', 'y']

    assert all(isinstance(v, str) for v in standardized_df['enum_col'])
    assert all(isinstance(v, str) for v in standardized_df['categorical_col'])


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


