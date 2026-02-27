#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import functools
import importlib
import json
import math
from datetime import datetime, date, time, timezone
from typing import Dict

_UNHANDLED = object()


# Parameter 'value' can also be pandas.DataFrame
def standardize_dict(value: Dict) -> Dict:
    result = {}
    for k, v in value.items():
        result[_standardize_value(k)] = _standardize_value(v)

    return result


def is_pandas_data_frame(v) -> bool:
    pandas = _get_pandas()
    return pandas is not None and isinstance(v, pandas.DataFrame)


def is_polars_dataframe(v):
    polars = _get_polars()
    return polars is not None and isinstance(v, polars.DataFrame)


def is_ndarray(data) -> bool:
    numpy = _get_numpy()
    if numpy is not None and isinstance(data, numpy.ndarray):
        return True

    jax = _get_jax()
    if jax is not None and isinstance(data, jax.numpy.ndarray):
        return True

    return False


def _standardize_value(v):
    # check for common types first to avoid unnecessary imports of heavy libraries that cause delays
    try:
        # python types
        if v is None:
            return v
        if isinstance(v, int):
            return float(v)
        if isinstance(v, float):
            return float(v) if math.isfinite(v) else None  # nan/inf to None (Gson does not handle them well)
        if isinstance(v, bool):
            return bool(v)
        if isinstance(v, str):
            return str(v)
        if isinstance(v, datetime):
            # Datetime: to milliseconds since epoch (time zone aware)
            return v.timestamp() * 1000
        if isinstance(v, date):
            # Local date: to milliseconds since epoch (midnight UTC)
            return datetime.combine(v, time.min, tzinfo=timezone.utc).timestamp() * 1000
        if isinstance(v, time):
            # Local time: to milliseconds since midnight
            return float(v.hour * 3600_000 + v.minute * 60_000 + v.second * 1000 + v.microsecond // 1000)

        # python containers
        if isinstance(v, list):
            return [_standardize_value(elem) for elem in v]
        if isinstance(v, tuple):
            return tuple(_standardize_value(elem) for elem in v)
        if isinstance(v, set):
            return {_standardize_value(elem) for elem in v}
        if isinstance(v, dict):
            return standardize_dict(v)

        # pandas
        value = _standardize_pandas_value(v)
        if value is not _UNHANDLED:
            return value

        # numpy
        value = _standardize_numpy_value(v)
        if value is not _UNHANDLED:
            return value

        # polars
        value = _standardize_polars_value(v)
        if value is not _UNHANDLED:
            return value

        # jax
        value = _standardize_jax_value(v)
        if value is not _UNHANDLED:
            return value

        # geopandas
        value = _standardize_geopandas_value(v)
        if value is not _UNHANDLED:
            return value

        # shapely
        value = _standardize_shapely_value(v)
        if value is not _UNHANDLED:
            return value

        return repr(v)
    except Exception as e:
        raise Exception('Failed to standardize type {0} ({1})'.format(type(v), str(v)[:100])) from e


def _standardize_pandas_value(v):
    if not _is_from_module(v, "pandas"):
        return _UNHANDLED

    pandas = _get_pandas()
    if pandas is not None:
        # don't use is_dict_like - Series is also dict-like and we want to process it as list-like
        if isinstance(v, pandas.DataFrame):
            return standardize_dict(v)
        if pandas.api.types.is_list_like(v):
            return [_standardize_value(element) for element in v]
        if pandas.api.types.is_scalar(v) and pandas.isna(v):
            return None
    return _UNHANDLED


def _standardize_numpy_value(v):
    if not _is_from_module(v, "numpy"):
        return _UNHANDLED

    numpy = _get_numpy()
    if numpy is not None:
        if isinstance(v, numpy.floating):
            return float(v) if math.isfinite(v) else None
        if isinstance(v, numpy.integer):
            return float(v)
        if isinstance(v, numpy.bool_):
            return bool(v)
        if isinstance(v, numpy.str_):
            return str(v)
        if isinstance(v, numpy.datetime64):
            try:
                # numpy.datetime64: to milliseconds since epoch (Unix time)
                return float(v.astype('datetime64[ms]').astype(numpy.int64))
            except:
                return None
        if isinstance(v, numpy.ndarray):
            # Process each array element individually.
            # Don't use '.tolist()' because this will implicitly
            # convert 'datetime64' values to unpredictable 'datetime' objects.
            return [_standardize_value(x) for x in v]
    return _UNHANDLED


def _standardize_polars_value(v):
    if not _is_from_module(v, "polars"):
        return _UNHANDLED

    polars = _get_polars()
    if polars is not None:
        if isinstance(v, polars.DataFrame):
            return standardize_dict(v.to_dict(as_series=False))
        if isinstance(v, polars.Series):
            return _standardize_value(v.to_list())
    return _UNHANDLED


def _standardize_jax_value(v):
    if not _is_from_module(v, "jax"):
        return _UNHANDLED

    jax = _get_jax()
    if jax is not None:
        if isinstance(v, jax.numpy.floating):
            return float(v) if math.isfinite(v) else None
        if isinstance(v, jax.numpy.integer):
            return float(v)
        if isinstance(v, jax.numpy.ndarray):
            return _standardize_value(v.tolist())
    return _UNHANDLED


def _standardize_geopandas_value(v):
    if not _is_from_module(v, "geopandas"):
        return _UNHANDLED

    geopandas = _get_geopandas()
    if geopandas is not None:
        if isinstance(v, geopandas.GeoDataFrame):
            return standardize_dict(v)
        if isinstance(v, geopandas.GeoSeries):
            return [_standardize_value(element) for element in v]
    return _UNHANDLED


def _standardize_shapely_value(v):
    if not _is_from_module(v, "shapely"):
        return _UNHANDLED

    shapely = _get_shapely()
    if shapely is not None:
        if isinstance(v, shapely.geometry.base.BaseGeometry):
            return json.dumps(shapely.geometry.mapping(v))
    return _UNHANDLED


@functools.cache
def _get_lib(name: str):
    try:
        return importlib.import_module(name)
    except (ImportError, AttributeError):
        return None


def _get_pandas():
    return _get_lib("pandas")


def _get_geopandas():
    return _get_lib("geopandas")


def _get_numpy():
    return _get_lib("numpy")


def _get_jax():
    return _get_lib("jax")


def _get_polars():
    return _get_lib("polars")


def _get_shapely():
    return _get_lib("shapely")


def _is_from_module(v, module_name: str) -> bool:
    # return hasattr(v, '__module__') and module_name in v.__module__
    return type(v).__module__.startswith(module_name)
