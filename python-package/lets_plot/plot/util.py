#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from typing import Any, Tuple, Sequence, Optional, Dict

from lets_plot._type_utils import is_pandas_data_frame
from lets_plot.geo_data_internals.utils import find_geo_names
from lets_plot.mapping import MappingMeta
from lets_plot.plot.core import aes, FeatureSpec
from lets_plot.plot.series_meta import infer_type, TYPE_UNKNOWN


def as_boolean(val, *, default):
    if val is None:
        return default

    return bool(val) and val != 'False'


def as_annotated_data(data: Any, mapping_spec: FeatureSpec) -> Tuple:
    data_meta = {}

    #if is_data_pub_stream(data):
    #    data = {}
    #    for col_name in raw_data.col_names:
    #        data[col_name] = []
    #
    #    data_meta.update({'pubsub': {'channel_id': raw_data.channel_id, 'col_names': raw_data.col_names}})

    # mapping
    mappings = {}

    class VariableMeta:
        def __init__(self, type: str = TYPE_UNKNOWN):
            self.levels = None
            self.aesthetics = []
            self.order = None
            self.type: str = type

    variables_meta: Dict[str, VariableMeta] = {}

    # init variables_meta
    # take all variables from layer data
    for var_name, var_type in infer_type(data).items():
        variables_meta[var_name] = VariableMeta(var_type)

    # and append with variables from mapping

    if is_pandas_data_frame(data):
        for var_name, var_content in data.items():
            dtype = var_content.dtype
            if dtype.name == 'category' and dtype.ordered:
                variables_meta[var_name].levels = dtype.categories.to_list()

    if mapping_spec is not None:
        for aesthetic in mapping_spec.props().keys():
            if aesthetic == 'name':  # ignore FeatureSpec.name property
                continue

            if isinstance(mapping_spec.props()[aesthetic], MappingMeta):
                mapping_meta: MappingMeta = mapping_spec.props()[aesthetic]
                mappings[aesthetic] = mapping_meta.variable

                if mapping_meta.variable not in variables_meta:  # as_discrete for the variable stored in ggplot data
                    variables_meta[mapping_meta.variable] = VariableMeta()

                var_meta = variables_meta[mapping_meta.variable]
                var_meta.aesthetics.append(aesthetic)
                if mapping_meta.levels is not None:
                    var_meta.levels = mapping_meta.levels

                if mapping_meta.parameters['order'] is not None:
                    var_meta.order = mapping_meta.parameters['order']
            else:
                mappings[aesthetic] = mapping_spec.props()[aesthetic]  # variable name

    series_annotation = []
    mapping_annotations = []

    for var_name, var_meta in variables_meta.items():
        series_meta = {}

        if var_meta.type != TYPE_UNKNOWN:
            series_meta['type'] = var_meta.type

        if var_meta.levels is not None:
            # series annotations
            series_meta['factor_levels'] = var_meta.levels

            if var_meta.order is not None:
                series_meta['order'] = var_meta.order
        else:
            # mapping annotations
            for aesthetic in var_meta.aesthetics:
                mapping = mapping_spec.props().get(aesthetic)
                if isinstance(mapping, MappingMeta):
                    mapping_annotations.append({
                        'aes': aesthetic,
                        'annotation': mapping.annotation,
                        'parameters': mapping.parameters
                    })

        if len(series_meta) > 0:
            series_meta['column'] = var_name
            series_annotation.append(series_meta)

    if len(series_annotation) > 0:
        data_meta.update({'series_annotations': series_annotation})

    if len(mapping_annotations) > 0:
        data_meta.update({'mapping_annotations': mapping_annotations})

    return data, aes(**mappings), {'data_meta': data_meta}


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


def key_int2str(data):
    if is_pandas_data_frame(data):
        if data.columns.inferred_type == 'integer':
            data.columns = data.columns.astype(str)
        return data

    if isinstance(data, dict):
        return {(str(k) if isinstance(k, int) else k): v for k, v in data.items()}

    return data
