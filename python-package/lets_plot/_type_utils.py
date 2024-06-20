#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import json
import math
from datetime import datetime

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


# Parameter 'value' can also be pandas.DataFrame
def standardize_dict(value: Dict) -> Dict:
    result = {}
    for k, v in value.items():
        result[_standardize_value(k)] = _standardize_value(v)

    return result


def is_polars_dataframe(v):
    return polars and isinstance(v, polars.DataFrame)


def is_dict_or_dataframe(v):
    return isinstance(v, dict) or (pandas and isinstance(v, pandas.DataFrame))


def is_int(v):
    return isinstance(v, int) or (numpy and isinstance(v, numpy.integer))


def is_float(v):
    return isinstance(v, float) or (numpy and isinstance(v, numpy.floating))


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
    if (numpy and isinstance(v, numpy.ndarray)) or (pandas and isinstance(v, pandas.Series)):
        return _standardize_value(v.tolist())
    if isinstance(v, datetime):
        if pandas and v is pandas.NaT:
            return None
        else:
            return v.timestamp() * 1000  # convert from second to millisecond
    if shapely and isinstance(v, shapely.geometry.base.BaseGeometry):
        return json.dumps(shapely.geometry.mapping(v))
    try:
        return repr(v)
    except Exception:
        raise Exception('Unsupported type: {0}({1})'.format(v, type(v)))
