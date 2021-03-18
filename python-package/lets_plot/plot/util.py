#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from typing import Any, Tuple, Sequence

from lets_plot.mapping import MappingMeta
from lets_plot.plot.core import aes


def as_boolean(val, *, default):
    if val is None:
        return default

    return bool(val) and val != 'False'


def as_annotated_data(raw_data: Any, raw_mapping: dict) -> Tuple:
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

    if raw_mapping is not None:
        for aesthetic, variable in raw_mapping.as_dict().items():
            if aesthetic == 'name': # ignore FeatureSpec.name property
                continue

            if isinstance(variable, MappingMeta):
                mapping[aesthetic] = variable.variable
                mapping_meta.append({
                    'aes': aesthetic,
                    'annotation': variable.annotation,
                    'parameters': variable.parameters
                })
            else:
                mapping[aesthetic] = variable

            if len(mapping_meta) > 0:
                data_meta.update({ 'mapping_annotations': mapping_meta })

    return data, aes(**mapping), {'data_meta': data_meta }


def is_data_pub_stream(data: Any) -> bool:
    # try:
    #     from lets_plot.display import DataPubStream
    #     return isinstance(data, DataPubStream)
    # except ImportError:
    #     return False  # no pub-sub in standalone deployment
    return False


def as_annotated_map_data(raw_map: Any) -> dict:
    if raw_map is None:
        return {}

    if is_geocoder(raw_map):
        return {'map_data_meta': {'georeference': {}}}

    if is_geo_data_frame(raw_map):
        return {'map_data_meta': get_geo_data_frame_meta(raw_map)}

    raise ValueError('Unsupported map parameter type: ' + str(type(raw_map)) + '. Should be a GeoDataFrame.')


def is_geocoder(data: Any) -> bool:
    # do not import Geocoder directly to suppress OSM attribution from geo_data package
    if data is None:
        return False

    return any(base.__name__ == 'Geocoder' for base in type(data).mro())


def auto_join_geocoder(map_join: Any, geocoder):
    if map_join is None:
        return None

    # import geo_data prints OSM attribution - minimize scope
    from lets_plot.geo_data import Geocodes

    if isinstance(map_join, str):
        data_names = [map_join]
        map_names = Geocodes.find_name_columns(geocoder.get_geocodes())
    elif isinstance(map_join, Sequence):
        if len(map_join) == 2 and all(isinstance(v, Sequence) and not isinstance(v, str) for v in map_join):
            data_names = map_join[0]
            map_names = map_join[1]
        else:
            data_names = map_join
            map_names = Geocodes.find_name_columns(geocoder.get_geocodes())
            if any(not isinstance(v, str) for v in data_names):
                raise ValueError("'map_join' with `Geocoder` must be a str, list[str] or pair of list[str]")
    else:
        raise ValueError("'map_join' with `Geocoder` must be a str, list[str] or pair of list[str], but was{}".format(repr(type(map_join))))

    if len(data_names) != len(map_names):
        raise ValueError("`map_join` expected to have ({}) items, but was({})".format(len(map_names), len(data_names)))

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

def as_map_join(map_join):
    if map_join is None:
        return None

    if isinstance(map_join, str):
        data_join_on, map_join_on = map_join, map_join
    elif isinstance(map_join, Sequence):
        if len(map_join) == 0:
            data_join_on, map_join_on = None, None
        if len(map_join) == 1:
            data_join_on, map_join_on = map_join[0], map_join[0]
        elif len(map_join) == 2:
            data_join_on, map_join_on = map_join[0], map_join[1]

    if data_join_on is None and map_join_on is None:
        return None

    if data_join_on is None or map_join_on is None:
        raise ValueError('map_join should not contain None')

    if isinstance(data_join_on, str):
        data_join_on = [data_join_on]

    if isinstance(map_join_on, str):
        map_join_on = [map_join_on]

    return [data_join_on, map_join_on]


def geo_data_frame_to_wgs84(data):
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

