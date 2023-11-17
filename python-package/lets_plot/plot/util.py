#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from collections.abc import Iterable
from datetime import datetime
from typing import Any, Tuple, Sequence, Optional

from lets_plot._type_utils import is_dict_or_dataframe
from lets_plot.geo_data_internals.utils import find_geo_names
from lets_plot.mapping import MappingMeta
from lets_plot.plot.core import aes


def as_boolean(val, *, default):
    if val is None:
        return default

    return bool(val) and val != 'False'


def as_annotated_data(raw_data: Any, raw_mapping: Any) -> Tuple:
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

    if raw_mapping is not None:
        for aesthetic, variable in raw_mapping.as_dict().items():
            if aesthetic == 'name':  # ignore FeatureSpec.name property
                continue

            if isinstance(variable, MappingMeta):
                if variable.levels is not None:
                    series_meta.append({
                        'column': variable.variable,
                        'factor_levels': variable.levels
                    })

                mapping[aesthetic] = variable.variable
                mapping_meta.append({
                    'aes': aesthetic,
                    'annotation': variable.annotation,
                    'parameters': variable.parameters
                })
            else:
                mapping[aesthetic] = variable

            if len(mapping_meta) > 0:
                data_meta.update({'mapping_annotations': mapping_meta})

    if is_dict_or_dataframe(data):
        for column_name, values in data.items():
            if isinstance(values, Iterable):
                not_empty_series = any(True for _ in values)
                if not_empty_series and all(isinstance(val, datetime) for val in values):
                    series_meta.append({
                        'column': column_name,
                        'type': 'datetime'
                    })

    if len(series_meta) > 0:
        data_meta.update({'series_annotations': series_meta})

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
                raise ValueError("map_join of type list[str] expected to have 1 or 2 items, but was {}".format(len(map_join)))
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


def is_data_frame(data: Any) -> bool:
    try:
        from pandas import DataFrame
        return isinstance(data, DataFrame)
    except ImportError:
        return False
