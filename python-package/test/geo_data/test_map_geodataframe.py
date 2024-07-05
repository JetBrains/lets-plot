#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import json

from geopandas import GeoDataFrame
from shapely.geometry import MultiPolygon, Polygon, LinearRing, Point, mapping

from lets_plot._type_utils import _standardize_value
from lets_plot.plot import ggplot, geom_polygon

POINT = Point(-12.34, 56.78)

POLYGON = Polygon(
    LinearRing([(1, 1), (1, 9), (9, 9), (9, 1)]),
    [
        LinearRing([(2, 2), (3, 2), (3, 3), (2, 3)]),
        LinearRing([(4, 4), (6, 4), (6, 6), (4, 6)])
    ]
)

MULTIPOLYGON = MultiPolygon([
    POLYGON,
    Polygon(LinearRing([(11, 12), (13, 14), (15, 16)]))
])

names = ['A', 'B', 'C']
geoms = [POINT, POLYGON, MULTIPOLYGON]


def make_geodataframe() -> GeoDataFrame:
    return GeoDataFrame(
        data={
            'name': names,
            'coord': geoms
        },
        geometry='coord'
    )


def test_geodataframe_should_be_mapped():
    expected_value = {
        'name': names,
        'coord': [json.dumps(mapping(geom)) for geom in geoms]
    }

    assert expected_value == _standardize_value(make_geodataframe())


def test_plot_should_has_meta_data_for_geodataframe():
    plot_spec = ggplot() + geom_polygon(data=make_geodataframe())

    assert plot_spec.as_dict()['layers'][0]['data_meta']['series_annotations'] == [
        {'column': 'name', 'type': 'str'},
        {'column': 'coord', 'type': 'unknown(pandas:unknown-array)'}
    ]

    assert plot_spec.as_dict()['layers'][0]['data_meta']['geodataframe'] == {'geometry': 'coord'}


def test_plot_should_has_meta_map_for_geodataframe():
    plot_spec = ggplot() + geom_polygon(map=make_geodataframe())

    # map doesn't define type meta (maybe it should?)
    assert plot_spec.as_dict()['layers'][0]['map_data_meta']['geodataframe'] == {'geometry': 'coord'}


def test_when_both_data_and_map_are_gdf_should_has_geodataframe_meta_only_for_map():
    plot_spec = ggplot() + geom_polygon(data=make_geodataframe(), map=make_geodataframe())

    # map doesn't define type meta (maybe it should?)
    assert plot_spec.as_dict()['layers'][0]['map_data_meta']['geodataframe'] == {'geometry': 'coord'}
    assert plot_spec.as_dict()['layers'][0]['data_meta']['series_annotations'] == [
        {'column': 'name', 'type': 'str'},
        {'column': 'coord', 'type': 'unknown(pandas:unknown-array)'}
    ]
