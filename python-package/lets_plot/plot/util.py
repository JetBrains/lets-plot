#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from collections import Iterable
from typing import Any, Tuple


def as_boolean(val, *, default):
    if val is None:
        return default

    return bool(val) and val != 'False'


def as_annotated_data(raw_data: Any) -> Tuple:
    data = raw_data
    data_meta = {}

    if is_data_pub_stream(data):
        data = {}
        for col_name in raw_data.col_names:
            data[col_name] = []

        data_meta = {'data_meta': {'pubsub': {'channel_id': raw_data.channel_id, 'col_names': raw_data.col_names}}}

    elif is_geo_data_frame(data):
        data_meta = {'data_meta': get_geo_data_frame_meta(data)}

    return data, data_meta


def is_data_pub_stream(data: Any) -> bool:
    # try:
    #     from lets_plot.display import DataPubStream
    #     return isinstance(data, DataPubStream)
    # except ImportError:
    #     return False  # no pub-sub in standalone deployment
    return False


def as_annotated_map_data(raw_map: Any) -> dict:
    if is_geo_data_regions(raw_map):
        return {'map_data_meta': {'georeference': {}}}

    if is_geo_data_frame(raw_map):
        return {'map_data_meta': get_geo_data_frame_meta(raw_map)}

    return {}


def is_geo_data_regions(data: Any) -> bool:
    try:
        from lets_plot.geo_data import Regions
        return isinstance(data, Regions)
    except ImportError:
        return False  # no geo_data in standalone deployment


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


def geo_data_frame_to_lon_lat(data):
    if data.crs is not None:
        return data.to_crs(epsg=4326)
    else:
        return data


def is_ndarray(data) -> bool:
    try:
        from numpy import ndarray
        return isinstance(data, ndarray)
    except ImportError:
        return False

def as_pair(data):
    if isinstance(data, str):
        return [data, None]
    elif isinstance(data, Iterable):
        if len(data) == 0:
            return [None, None]
        if len(data) == 1:
            return [data[0], None]
        elif len(data) == 2:
            return [data[0], data[1]]
    else:
        return None
