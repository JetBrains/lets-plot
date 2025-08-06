#  Copyright (c) 2024. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
from datetime import datetime, date, time
from typing import Union, Dict, Iterable, Optional

from lets_plot._type_utils import is_polars_dataframe
from lets_plot.plot.util import is_pandas_data_frame

try:
    import numpy
except ImportError:
    numpy = None

try:
    import pandas
except ImportError:
    pass

try:
    import polars as pl
    from polars.datatypes.group import INTEGER_DTYPES as PL_INTEGER_DTYPES
    from polars.datatypes.group import FLOAT_DTYPES as PL_FLOAT_DTYPES
except ImportError:
    pass

TYPE_INTEGER = 'int'
TYPE_FLOATING = 'float'
TYPE_STRING = 'str'
TYPE_BOOLEAN = 'bool'
TYPE_DATE_TIME = 'datetime'
TYPE_DATE = 'date'  # Local date (no time zone)
TYPE_TIME = 'time'  # Local time (we ignore time zone even if it is present)
TYPE_UNKNOWN = 'unknown'


def _infer_type(data: Union[Dict, 'pandas.DataFrame', 'polars.DataFrame']) -> Dict[str, str]:
    type_info = {}

    if is_pandas_data_frame(data):
        for var_name, var_content in data.items():
            type_info[var_name] = _infer_type_pandas_dataframe(var_name, var_content)
    elif is_polars_dataframe(data):
        for var_name, var_type in data.schema.items():
            type_info[var_name] = _infer_type_polars_dataframe(var_name, var_type)
    elif isinstance(data, dict):
        for var_name, var_content in data.items():
            type_info[var_name] = _infer_type_dict(var_name, var_content)

    return type_info


def _infer_type_pandas_dataframe(var_name: str, var_content) -> str:
    if var_content.empty:
        return TYPE_UNKNOWN
    elif var_content.isna().all():
        return TYPE_UNKNOWN

    lp_dtype = TYPE_UNKNOWN
    time_zone = None
    pandas_dtype = pandas.api.types.infer_dtype(var_content.values, skipna=True)

    if pandas_dtype == "categorical":
        dtype = var_content.cat.categories.dtype

        if numpy.issubdtype(dtype, numpy.integer):
            lp_dtype = TYPE_INTEGER
        elif numpy.issubdtype(dtype, numpy.floating):
            lp_dtype = TYPE_FLOATING
        elif numpy.issubdtype(dtype, numpy.object_):
            # Check if all elements are strings
            if all(isinstance(x, str) for x in var_content.cat.categories):
                lp_dtype = TYPE_STRING
    else:
        # see https://pandas.pydata.org/docs/reference/api/pandas.api.types.infer_dtype.html
        if pandas_dtype == 'string':
            lp_dtype = TYPE_STRING
        elif pandas_dtype == 'floating':
            lp_dtype = TYPE_FLOATING
        elif pandas_dtype == 'integer':
            lp_dtype = TYPE_INTEGER
        elif pandas_dtype == 'boolean':
            lp_dtype = TYPE_BOOLEAN

        elif pandas_dtype == 'datetime64' or pandas_dtype == 'datetime':
            lp_dtype = TYPE_DATE_TIME
        elif pandas_dtype == "date":
            lp_dtype = TYPE_DATE
        elif pandas_dtype == "time":
            lp_dtype = TYPE_TIME

        elif pandas_dtype == 'empty':  # for columns with all None values
            lp_dtype = TYPE_UNKNOWN
        else:
            lp_dtype = 'unknown(pandas:' + pandas_dtype + ')'

    return lp_dtype


def _infer_type_polars_dataframe(var_name: str, var_type) -> str:
    lp_dtype = TYPE_UNKNOWN

    # https://docs.pola.rs/api/python/stable/reference/datatypes.html
    if isinstance(var_type, pl.datatypes.Enum):
        # In the current version of Polars, Enum is always a string
        # https://docs.pola.rs/api/python/stable/reference/datatypes.html#string
        return TYPE_STRING
    elif isinstance(var_type, pl.datatypes.Categorical):
        return TYPE_STRING
    elif var_type in PL_FLOAT_DTYPES:
        lp_dtype = TYPE_FLOATING
    elif var_type in PL_INTEGER_DTYPES:
        lp_dtype = TYPE_INTEGER
    elif var_type == pl.datatypes.String:
        lp_dtype = TYPE_STRING
    elif var_type == pl.datatypes.Boolean:
        lp_dtype = TYPE_BOOLEAN

    elif var_type == pl.datatypes.Datetime:
        lp_dtype = TYPE_DATE_TIME
    elif var_type == pl.datatypes.Date:
        lp_dtype = TYPE_DATE
    elif var_type == pl.datatypes.Time:
        lp_dtype = TYPE_TIME

    else:
        lp_dtype = 'unknown(polars:' + str(var_type) + ')'

    return lp_dtype


def _infer_type_dict(var_name: str, var_content) -> str:
    if isinstance(var_content, Iterable):
        if not any(True for _ in var_content):  # empty
            return TYPE_UNKNOWN
    else:
        return TYPE_UNKNOWN

    type_set = set(type(val) for val in var_content)
    if type(None) in type_set:
        type_set.remove(type(None))

    if len(type_set) == 0:
        return TYPE_UNKNOWN

    if len(type_set) > 1:
        if all(issubclass(type_obj, int) or issubclass(type_obj, float) for type_obj in type_set):
            return TYPE_FLOATING
        else:
            return 'unknown(mixed types)'

    lp_dtype = TYPE_UNKNOWN
    type_obj = list(type_set)[0]
    if type_obj == bool:
        lp_dtype = TYPE_BOOLEAN
    elif issubclass(type_obj, int):
        lp_dtype = TYPE_INTEGER
    elif issubclass(type_obj, float):
        lp_dtype = TYPE_FLOATING
    elif issubclass(type_obj, str):
        lp_dtype = TYPE_STRING

    elif issubclass(type_obj, datetime):
        lp_dtype = TYPE_DATE_TIME
    elif issubclass(type_obj, date) and not issubclass(type_obj, datetime):
        lp_dtype = TYPE_DATE
    elif issubclass(type_obj, time):
        lp_dtype = TYPE_TIME

    elif numpy and issubclass(type_obj, numpy.datetime64):
        lp_dtype = TYPE_DATE_TIME
    elif numpy and issubclass(type_obj, numpy.timedelta64):
        # ToDo: time delta?
        # lp_dtype = TYPE_DATE_TIME
        lp_dtype = 'unknown(python:' + str(type_obj) + ')'

    elif numpy and issubclass(type_obj, numpy.integer):
        lp_dtype = TYPE_INTEGER
    elif numpy and issubclass(type_obj, numpy.floating):
        lp_dtype = TYPE_FLOATING
    else:
        lp_dtype = 'unknown(python:' + str(type_obj) + ')'

    return lp_dtype


def _detect_time_zone(var_name: str, data: Union[Dict, 'pandas.DataFrame', 'polars.DataFrame']) -> Optional[str]:
    if is_pandas_data_frame(data):
        if var_name in data:
            var_content = data[var_name]
            if hasattr(var_content, 'dt') and hasattr(var_content.dt, 'tz') and var_content.dt.tz is not None:
                return str(var_content.dt.tz)
    elif is_polars_dataframe(data):
        if var_name in data.columns:
            col_dtype = data[var_name].dtype
            if hasattr(col_dtype, 'time_zone'):
                if col_dtype.time_zone is not None:
                    return str(col_dtype.time_zone)
    elif isinstance(data, dict):
        if var_name in data:
            var_content = data[var_name]
            if isinstance(var_content, Iterable):
                for val in var_content:
                    if isinstance(val, datetime) and val.tzinfo is not None:
                        return str(val.tzinfo)

                    # NumPy datetime64 objects don't store timezone information,
                    # so we can't extract it from them.

    return None
