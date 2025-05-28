#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import json
import math
from datetime import datetime, date, time, timezone

from typing import Dict

try:
    import numpy
except ImportError:
    numpy = None

try:
    import pandas
except ImportError:
    pandas = None

try:
    import polars
except ImportError:
    polars = None

try:
    import shapely
    import shapely.geometry
except ImportError:
    shapely = None

try:
    import jax.numpy as jnp
except ImportError:
    jnp = None


# Parameter 'value' can also be pandas.DataFrame
def standardize_dict(value: Dict) -> Dict:
    result = {}
    for k, v in value.items():
        result[_standardize_value(k)] = _standardize_value(v)

    return result


def is_pandas_data_frame(v) -> bool:
    return pandas and isinstance(v, pandas.DataFrame)


def is_polars_dataframe(v):
    return polars and isinstance(v, polars.DataFrame)


def is_dict_or_dataframe(v):
    return isinstance(v, dict) or (pandas and isinstance(v, pandas.DataFrame))


def is_int(v):
    return isinstance(v, int) or (numpy and isinstance(v, numpy.integer)) or (jnp and isinstance(v, jnp.integer))


def is_float(v):
    return isinstance(v, float) or (numpy and isinstance(v, numpy.floating)) or (jnp and isinstance(v, jnp.floating))


def is_ndarray(data) -> bool:
    return (numpy and isinstance(data, numpy.ndarray)) or (jnp and isinstance(data, jnp.ndarray))


def is_number(v):
    return is_int(v) or is_float(v)


def _standardize_value(v):
    if v is None:
        return v
    if isinstance(v, bool):
        return bool(v)
    if isinstance(v, str):
        return str(v)
    if is_float(v):
        if math.isfinite(v):
            return float(v)
        # None for special values like 'nan' etc. because
        # some json parsers (like com.google.gson.Gson) do not handle them well.
        return None
    if is_int(v):
        return float(v)
    if is_dict_or_dataframe(v):
        return standardize_dict(v)
    if is_polars_dataframe(v):
        return standardize_dict(v.to_dict(as_series=False))
    if isinstance(v, list):
        return [_standardize_value(elem) for elem in v]
    if isinstance(v, tuple):
        return tuple(_standardize_value(elem) for elem in v)
    if (numpy and isinstance(v, numpy.ndarray)) or (pandas and isinstance(v, pandas.Series)) or (
            jnp and isinstance(v, jnp.ndarray)):
        return _standardize_value(v.tolist())

    # Universal NaT/NaN check
    if pandas and pandas.isna(v):
        return None

    if isinstance(v, datetime):
        # Datetime: to milliseconds since epoch (time zone aware)
        return v.timestamp() * 1000
    if isinstance(v, date) and not isinstance(v, datetime):
        # Local date: to milliseconds since epoch (midnight UTC)
        return datetime.combine(v, time.min, tzinfo=timezone.utc).timestamp() * 1000
    if isinstance(v, time):
        # Local time: to milliseconds since midnight
        return v.hour * 3600_000 + v.minute * 60_000 + v.second * 1000 + v.microsecond // 1000
    if numpy and isinstance(v, numpy.datetime64):
        try:
            # numpy.datetime64: to milliseconds since epoch (Unix time)
            return v.astype('datetime64[ms]').astype(numpy.int64)
        except:
            return None

    if shapely and isinstance(v, shapely.geometry.base.BaseGeometry):
        return json.dumps(shapely.geometry.mapping(v))
    try:
        return repr(v)
    except Exception:
        raise Exception('Unsupported type: {0}({1})'.format(v, type(v)))
