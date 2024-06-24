#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from collections.abc import Iterable
from datetime import datetime
from typing import Any, Tuple, Sequence, Optional, Dict, Union

from lets_plot._type_utils import is_polars_dataframe
from lets_plot.geo_data_internals.utils import find_geo_names
from lets_plot.mapping import MappingMeta
from lets_plot.plot.core import aes, FeatureSpec


def as_boolean(val, *, default):
    if val is None:
        return default

    return bool(val) and val != 'False'


def as_annotated_data(raw_data: Any, raw_mapping: FeatureSpec) -> Tuple:
    data_meta = {}

    # data
    data = raw_data

    if is_data_pub_stream(data):
        data = {}
        for col_name in raw_data.col_names:
            data[col_name] = []

        data_meta.update({'pubsub': {'channel_id': raw_data.channel_id, 'col_names': raw_data.col_names}})

    # mapping
    mapping = {}
    mapping_meta = []
    # series annotations
    series_meta = []

    class VariableMeta:
        def __init__(self):
            self.levels = None
            self.aesthetics = []
            self.order = None
            self.type = None

    variables_meta: Dict[str, VariableMeta] = {}
    for var_name, var_type in infer_type(data).items():
        var_meta = VariableMeta()
        variables_meta[var_name] = var_meta
        var_meta.type = var_type

    if is_pandas_data_frame(data):
        for var_name, var_content in data.items():
            dtype = var_content.dtype
            if dtype.name == 'category' and dtype.ordered:
                variables_meta[var_name].levels = dtype.categories.to_list()

    if raw_mapping is not None:
        for aesthetic, variable in raw_mapping.as_dict().items():
            if aesthetic == 'name':  # ignore FeatureSpec.name property
                continue

            if isinstance(variable, MappingMeta):
                mapping[aesthetic] = variable.variable

                if variable.variable in variables_meta:
                    var_meta = variables_meta[variable.variable]
                else:
                    var_meta = VariableMeta()
                    variables_meta[variable.variable] = var_meta

                var_meta.aesthetics.append(aesthetic)
                if variable.levels is not None:
                    var_meta.levels = variable.levels
                order = variable.parameters.get('order')
                if order is not None:
                    var_meta.order = order
            else:
                mapping[aesthetic] = variable

    for var_name, var_meta in variables_meta.items():
        meta_dict = {
            'column': var_name,
            'type': var_meta.type
        }

        if var_meta.type != 'unknown':
            series_meta.append(meta_dict)

        if var_meta.levels is not None:
            # series annotations
            meta_dict['factor_levels'] = var_meta.levels
            meta_dict['order'] = var_meta.order
        else:
            # mapping annotations
            for aesthetic in var_meta.aesthetics:
                value = raw_mapping.as_dict().get(aesthetic)
                if value is not None and isinstance(value, MappingMeta):
                    mapping_meta.append({
                        'aes': aesthetic,
                        'annotation': value.annotation,
                        'parameters': value.parameters
                    })

    if len(series_meta) > 0:
        data_meta.update({'series_annotations': series_meta})

    if len(mapping_meta) > 0:
        data_meta.update({'mapping_annotations': mapping_meta})

    return data, aes(**mapping), {'data_meta': data_meta}


def is_data_pub_stream(data: Any) -> bool:
    # try:
    #     from lets_plot.display import DataPubStream
    #     return isinstance(data, DataPubStream)
    # except ImportError:
    #     return False  # no pub-sub in standalone deployment
    return False


def normalize_map_join(map_join):
    if map_join is None:
        return None

    def invalid_map_join_format():
        return ValueError("map_join must be a str, list[str] or pair of list[str]")

    if isinstance(map_join, str):  # 'foo' -> [['foo'], None]
        data_names = [map_join]
        map_names = None
    elif isinstance(map_join, Sequence):
        if all(isinstance(v, str) for v in map_join):  # all items are strings
            if len(map_join) == 1:  # ['foo'] -> [['foo'], None]
                data_names = map_join
                map_names = None
            elif len(map_join) == 2:  # ['foo', 'bar'] -> [['foo'], ['bar']]
                data_names = [map_join[0]]
                map_names = [map_join[1]]
            elif len(map_join) > 2:  # ['foo', 'bar', 'baz'] -> error
                raise ValueError(
                    "map_join of type list[str] expected to have 1 or 2 items, but was {}".format(len(map_join)))
            else:
                raise invalid_map_join_format()
        elif all(isinstance(v, Sequence) and not isinstance(v, str) for v in map_join):  # all items are lists
            if len(map_join) == 1:  # [['foo', 'bar']] -> [['foo', 'bar'], None]
                data_names = map_join[0]
                map_names = None
            elif len(map_join) == 2:  # [['foo', 'bar'], ['baz', 'qux']] -> [['foo', 'bar'], ['baz', 'qux']]
                data_names = map_join[0]
                map_names = map_join[1]
            else:
                raise invalid_map_join_format()
        else:
            raise invalid_map_join_format()

    else:
        raise invalid_map_join_format()

    return [data_names, map_names]


def auto_join_geo_names(map_join: Any, gdf):
    if map_join is None:
        return None

    data_names = map_join[0]
    map_names = map_join[1]

    if map_names is None:
        map_names = find_geo_names(gdf)
        if len(map_names) == 0:
            raise ValueError(
                "Can't deduce joining keys.\n"
                "Define both data and map key columns in map_join "
                "explicitly: map_join=[['data_column'], ['map_column']]."
            )

        if len(data_names) > len(map_names):
            raise ValueError(
                "Data key columns count exceeds map key columns count: {} > {}".format(len(data_names), len(map_names))
            )

        map_names = map_names[:len(data_names)]  # use same number of key columns

    return [data_names, map_names]


def is_geo_data_frame(data: Any) -> bool:
    try:
        from geopandas import GeoDataFrame
        return isinstance(data, GeoDataFrame)
    except ImportError:
        return False


def get_geo_data_frame_meta(geo_data_frame) -> dict:
    return {
        'geodataframe': {
            'geometry': geo_data_frame.geometry.name
        }
    }


def geo_data_frame_to_crs(gdf: 'GeoDataFrame', use_crs: Optional[str]):
    if gdf.crs is None:
        return gdf

    return gdf.to_crs('EPSG:4326' if use_crs is None else use_crs)


def is_ndarray(data) -> bool:
    try:
        from numpy import ndarray
        return isinstance(data, ndarray)
    except ImportError:
        return False


def is_pandas_data_frame(data: Any) -> bool:
    try:
        from pandas import DataFrame
        return isinstance(data, DataFrame)
    except ImportError:
        return False


def infer_categorical_type(column: 'pandas.Series') -> str:
    import numpy as np
    dtype = column.cat.categories.dtype

    if np.issubdtype(dtype, np.integer):
        return 'integer'
    elif np.issubdtype(dtype, np.floating):
        return 'float'
    elif np.issubdtype(dtype, np.object_):
        # Check if all elements are strings
        if all(isinstance(x, str) for x in column.cat.categories):
            return 'string'
        else:
            return 'mixed'
    else:
        return 'unknown'


def infer_type(data: Union[Dict, 'pandas.DataFrame']) -> Dict[str, str]:
    type_info = {}

    if is_pandas_data_frame(data):
        import pandas as pd

        for var_name, var_content in data.items():
            if data.empty:
                type_info[var_name] = 'unknown'
                continue

            inferred_type = pd.api.types.infer_dtype(var_content.values, skipna=True)
            if inferred_type == "categorical":
                inferred_type = infer_categorical_type(var_content)

            # see https://pandas.pydata.org/docs/reference/api/pandas.api.types.infer_dtype.html
            if inferred_type == 'string':
                type_info[var_name] = 'str'
            elif inferred_type == 'floating':
                type_info[var_name] = 'float'
            elif inferred_type == 'integer':
                type_info[var_name] = 'int'
            elif inferred_type == 'boolean':
                type_info[var_name] = 'bool'
            elif inferred_type == 'datetime64' or inferred_type == 'datetime':
                type_info[var_name] = 'datetime'
            elif inferred_type == "date":
                type_info[var_name] = 'date'
            elif inferred_type == 'empty':  # for columns with all None values
                type_info[var_name] = 'unknown'
            else:
                type_info[var_name] = 'unknown(pandas:' + inferred_type + ')'
    elif is_polars_dataframe(data):
        import polars as pl
        for var_name, var_type in data.schema.items():

            # https://docs.pola.rs/api/python/stable/reference/datatypes.html
            if var_type in pl.FLOAT_DTYPES:
                type_info[var_name] = 'float'
            elif var_type in pl.INTEGER_DTYPES:
                type_info[var_name] = 'int'
            elif var_type == pl.datatypes.Utf8:
                type_info[var_name] = 'str'
            elif var_type == pl.datatypes.Boolean:
                type_info[var_name] = 'bool'
            elif var_type in pl.datatypes.DATETIME_DTYPES:
                type_info[var_name] = 'datetime'
            else:
                type_info[var_name] = 'unknown(polars:' + str(var_type) + ')'
    elif isinstance(data, dict):
        for var_name, var_content in data.items():
            if isinstance(var_content, Iterable):
                if not any(True for _ in var_content):  # empty
                    type_info[var_name] = 'unknown'
                    continue

                type_set = set(type(val) for val in var_content)
                if None in type_set:
                    type_set.remove(None)

                if len(type_set) > 1:
                    type_info[var_name] = 'unknown(mixed types)'
                    continue

                type_obj = list(type_set)[0]
                if type_obj == int:
                    type_info[var_name] = 'int'
                elif type_obj == float:
                    type_info[var_name] = 'float'
                elif type_obj == bool:
                    type_info[var_name] = 'bool'
                elif type_obj == str:
                    type_info[var_name] = 'str'
                elif type_obj == datetime:
                    type_info[var_name] = 'datetime'
                else:
                    type_info[var_name] = 'unknown(python:' + str(type_obj) + ')'

    return type_info


def key_int2str(data):
    if is_pandas_data_frame(data):
        if data.columns.inferred_type == 'integer':
            data.columns = data.columns.astype(str)
        return data

    if isinstance(data, dict):
        return {(str(k) if isinstance(k, int) else k): v for k, v in data.items()}

    return data
