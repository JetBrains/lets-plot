#  Copyright (c) 2024. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
from datetime import datetime
from typing import Union, Dict, Iterable

from lets_plot._type_utils import is_polars_dataframe
from lets_plot.plot.util import is_pandas_data_frame

TYPE_INTEGER = 'int'
TYPE_FLOATING = 'float'
TYPE_STRING = 'str'
TYPE_BOOLEAN = 'bool'
TYPE_DATE_TIME = 'datetime'
TYPE_UNKNOWN = 'unknown'


def infer_type(data: Union[Dict, 'pandas.DataFrame', 'polars.DataFrame']) -> Dict[str, str]:
    type_info = {}

    if is_pandas_data_frame(data):
        import pandas as pd
        import numpy as np  # np is a dependency of pandas, we can import it without checking

        for var_name, var_content in data.items():
            if data.empty:
                type_info[var_name] = TYPE_UNKNOWN
                continue

            inferred_type = pd.api.types.infer_dtype(var_content.values, skipna=True)
            if inferred_type == "categorical":
                dtype = var_content.cat.categories.dtype

                if np.issubdtype(dtype, np.integer):
                    type_info[var_name] = TYPE_INTEGER
                elif np.issubdtype(dtype, np.floating):
                    type_info[var_name] = TYPE_FLOATING
                elif np.issubdtype(dtype, np.object_):
                    # Check if all elements are strings
                    if all(isinstance(x, str) for x in var_content.cat.categories):
                        type_info[var_name] = TYPE_STRING
                    else:
                        type_info[var_name] = TYPE_UNKNOWN
                else:
                    type_info[var_name] = TYPE_UNKNOWN
            else:
                # see https://pandas.pydata.org/docs/reference/api/pandas.api.types.infer_dtype.html
                if inferred_type == 'string':
                    type_info[var_name] = TYPE_STRING
                elif inferred_type == 'floating':
                    type_info[var_name] = TYPE_FLOATING
                elif inferred_type == 'integer':
                    type_info[var_name] = TYPE_INTEGER
                elif inferred_type == 'boolean':
                    type_info[var_name] = TYPE_BOOLEAN
                elif inferred_type == 'datetime64' or inferred_type == 'datetime':
                    type_info[var_name] = TYPE_DATE_TIME
                elif inferred_type == "date":
                    type_info[var_name] = TYPE_DATE_TIME
                elif inferred_type == 'empty':  # for columns with all None values
                    type_info[var_name] = TYPE_UNKNOWN
                else:
                    type_info[var_name] = 'unknown(pandas:' + inferred_type + ')'
    elif is_polars_dataframe(data):
        import polars as pl
        from polars.datatypes.group import INTEGER_DTYPES, FLOAT_DTYPES
        for var_name, var_type in data.schema.items():

            # https://docs.pola.rs/api/python/stable/reference/datatypes.html
            if var_type in FLOAT_DTYPES:
                type_info[var_name] = TYPE_FLOATING
            elif var_type in INTEGER_DTYPES:
                type_info[var_name] = TYPE_INTEGER
            elif var_type == pl.datatypes.String:
                type_info[var_name] = TYPE_STRING
            elif var_type == pl.datatypes.Boolean:
                type_info[var_name] = TYPE_BOOLEAN
            elif var_type == pl.datatypes.Date or var_type == pl.datatypes.Datetime:
                type_info[var_name] = TYPE_DATE_TIME
            else:
                type_info[var_name] = 'unknown(polars:' + str(var_type) + ')'
    elif isinstance(data, dict):
        for var_name, var_content in data.items():
            if isinstance(var_content, Iterable):
                if not any(True for _ in var_content):  # empty
                    type_info[var_name] = TYPE_UNKNOWN
                    continue

                type_set = set(type(val) for val in var_content)
                if None in type_set:
                    type_set.remove(None)

                if len(type_set) > 1:
                    type_info[var_name] = 'unknown(mixed types)'
                    continue

                try:
                    import numpy
                except ImportError:
                    numpy = None

                type_obj = list(type_set)[0]
                if type_obj == bool:
                    type_info[var_name] = TYPE_BOOLEAN
                elif issubclass(type_obj, int):
                    type_info[var_name] = TYPE_INTEGER
                elif issubclass(type_obj, float):
                    type_info[var_name] = TYPE_FLOATING
                elif issubclass(type_obj, str):
                    type_info[var_name] = TYPE_STRING
                elif issubclass(type_obj, datetime):
                    type_info[var_name] = TYPE_DATE_TIME
                elif numpy and issubclass(type_obj, numpy.datetime64):
                    type_info[var_name] = TYPE_DATE_TIME
                elif numpy and issubclass(type_obj, numpy.timedelta64):
                    type_info[var_name] = TYPE_DATE_TIME
                elif numpy and issubclass(type_obj, numpy.integer):
                    type_info[var_name] = TYPE_INTEGER
                elif numpy and issubclass(type_obj, numpy.floating):
                    type_info[var_name] = TYPE_FLOATING
                else:
                    type_info[var_name] = 'unknown(python:' + str(type_obj) + ')'

    return type_info
