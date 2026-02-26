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


def is_ndarray(data) -> bool:
    return (numpy and isinstance(data, numpy.ndarray)) or (jnp and isinstance(data, jnp.ndarray))


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
        if pandas and isinstance(v, pandas.DataFrame):
            return standardize_dict(v)
        if pandas and isinstance(v, (pandas.Series, pandas.api.extensions.ExtensionArray)):
            return _standardize_value(v.tolist())

        # numpy
        if numpy and isinstance(v, numpy.floating):
            return float(v) if math.isfinite(v) else None
        if numpy and isinstance(v, numpy.integer):
            return float(v)
        if numpy and isinstance(v, numpy.datetime64):
            try:
                # numpy.datetime64: to milliseconds since epoch (Unix time)
                return float(v.astype('datetime64[ms]').astype(numpy.int64))
            except:
                return None
        if numpy and isinstance(v, numpy.ndarray):
            # Process each array element individually.
            # Don't use '.tolist()' because this will implicitly
            # convert 'datetime64' values to unpredictable 'datetime' objects.
            return [_standardize_value(x) for x in v]

        # polars
        if polars and isinstance(v, polars.DataFrame):
            return standardize_dict(v.to_dict(as_series=False))

        # jax
        if jnp and isinstance(v, jnp.floating):
            return float(v) if math.isfinite(v) else None
        if jnp and isinstance(v, jnp.integer):
            return float(v)
        if jnp and isinstance(v, jnp.ndarray):
            return _standardize_value(v.tolist())

        # shapely
        if shapely and isinstance(v, shapely.geometry.base.BaseGeometry):
            return json.dumps(shapely.geometry.mapping(v))

        # Universal NaT/NaN check
        if pandas and pandas.isna(v):
            return None

        return repr(v)
    except Exception as e:
        raise Exception('Failed to standardize type {0} ({1})'.format(type(v), str(v)[:100])) from e
