#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from typing import Any, Tuple, Sequence, Optional, Dict, List

from lets_plot._type_utils import is_pandas_data_frame
from lets_plot.geo_data_internals.utils import find_geo_names
from lets_plot.mapping import MappingMeta
from lets_plot.plot.core import aes, FeatureSpec
from lets_plot.plot.series_meta import _infer_type, TYPE_UNKNOWN, TYPE_DATE_TIME, _detect_time_zone


def as_boolean(val, *, default):
    if val is None:
        return default

    return bool(val) and val != 'False'


def as_annotated_data(data: Any, mapping_spec: FeatureSpec) -> Tuple:
    data_type_by_var: Dict[str, str] = {}  # VarName to Type
    mapping_meta_by_var: Dict[str, Dict[str, MappingMeta]] = {}  # VarName to Dict[Aes, MappingMeta]
    mappings = {}  # Aes to VarName

    # fill mapping_meta_by_var, mappings and data_type_by_var.
    if mapping_spec is not None:
        for key, spec in mapping_spec.props().items():
            # the key is either an aesthetic name or 'name' (FeatureSpec.name property)
            if key == 'name':  # ignore FeatureSpec.name property
                continue

            if isinstance(spec, MappingMeta):
                mappings[key] = spec.variable
                mapping_meta_by_var.setdefault(spec.variable, {})[key] = spec
                data_type_by_var[spec.variable] = TYPE_UNKNOWN
            else:
                mappings[key] = spec  # spec is a variable name

    data_type_by_var.update(_infer_type(data))

    # Detect the tome zone - one for the entire data set.
    time_zone_by_var_name = {}
    for var_name, data_type in data_type_by_var.items():
        if data_type == TYPE_DATE_TIME:
            time_zone = _detect_time_zone(var_name, data)
            if time_zone is not None:
                time_zone_by_var_name[var_name] = time_zone

    # fill series annotations
    series_annotations = {}  # var to series_annotation
    for var_name, data_type in data_type_by_var.items():
        series_annotation = {}

        if data_type != TYPE_UNKNOWN:
            series_annotation['type'] = data_type

        if var_name in time_zone_by_var_name:
            series_annotation['time_zone'] = time_zone_by_var_name[var_name]

        if is_pandas_data_frame(data) and data[var_name].dtype.name == 'category' and data[var_name].dtype.ordered:
            series_annotation['factor_levels'] = data[var_name].cat.categories.to_list()
        elif var_name in mapping_meta_by_var:
            levels = last_not_none(list(map(lambda mm: mm.levels, mapping_meta_by_var[var_name].values())))
            if levels is not None:
                series_annotation['factor_levels'] = levels

        if 'factor_levels' in series_annotation and var_name in mapping_meta_by_var:
            order = last_not_none(list(map(lambda mm: mm.parameters['order'], mapping_meta_by_var[var_name].values())))
            if order is not None:
                series_annotation['order'] = order

        if len(series_annotation) > 0:
            series_annotation['column'] = var_name
            series_annotations[var_name] = series_annotation

    # fill mapping annotations
    mapping_annotations = []
    for var_name, meta_data in mapping_meta_by_var.items():
        for aesthetic, mapping_meta in meta_data.items():
            if mapping_meta.annotation == 'as_discrete':
                if 'factor_levels' in series_annotations.get(var_name, {}):
                    #  there is a bug - if label is set then levels are not applied
                    continue

                mapping_annotation = {}

                # Note that the label is always set; otherwise, the scale title will appear as 'color.cyl'
                label = mapping_meta.parameters.get('label')
                if label is not None:
                    mapping_annotation.setdefault('parameters', {})['label'] = label

                if mapping_meta.levels is not None:
                    mapping_annotation['levels'] = mapping_meta.levels

                order_by = mapping_meta.parameters.get('order_by')
                if order_by is not None:
                    mapping_annotation.setdefault('parameters', {})['order_by'] = order_by

                order = mapping_meta.parameters.get('order')
                if order is not None:
                    mapping_annotation.setdefault('parameters', {})['order'] = order

                # add mapping meta if custom label is set or if series annotation for var doesn't contain order options
                # otherwise don't add mapping meta - it's redundant, nothing unique compared to series annotation
                if len(mapping_annotation):
                    mapping_annotation['aes'] = aesthetic
                    mapping_annotation['annotation'] = 'as_discrete'
                    mapping_annotations.append(mapping_annotation)

    data_meta = {}

    if len(series_annotations) > 0:
        data_meta.update({'series_annotations': list(series_annotations.values())})

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
        if data.columns.inferred_type == 'integer' or data.columns.inferred_type == 'mixed-integer':
            data.columns = data.columns.astype(str)
        return data

    if isinstance(data, dict):
        return {(str(k) if isinstance(k, int) else k): v for k, v in data.items()}

    return data


def last_not_none(lst: List) -> Optional[Any]:
    for i in reversed(lst):
        if i is not None:
            return i
    return None
