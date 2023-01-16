#  Copyright (c) 2022. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
import numpy as np
from geopandas import GeoDataFrame
from shapely.geometry import Point

from lets_plot.plot import ggplot, geom_point, geom_map, geom_imshow, geom_livemap

ITALY_UTM_33N = 'EPSG:32633'
WGS84 = 'epsg:4326'
ROME_COORD_UTM_33N = Point(290199.90107, 4639536.51707)
ROME_COORD_WGS84 = Point(12.47156, 41.87993)

ROME_GDF = GeoDataFrame({'city': ['Rome']}, geometry=[ROME_COORD_WGS84], crs=WGS84)
INVALID_CRS_USAGE = "'All geoms with map parameter should either use same `use_crs` or not use it at all'"


def test_no_crs__geom_map():
    spec = ggplot() + geom_map(map=ROME_GDF)
    assert ROME_COORD_WGS84.almost_equals(spec.as_dict()['layers'][0]['map'].geometry[0], decimal=4)


def test_no_crs__geom_map__geom_point():
    spec = ggplot() + geom_map(map=ROME_GDF) + geom_point(map=ROME_GDF)

    assert ROME_COORD_WGS84.almost_equals(spec.as_dict()['layers'][0]['map'].geometry[0], decimal=4)
    assert ROME_COORD_WGS84.almost_equals(spec.as_dict()['layers'][1]['map'].geometry[0], decimal=4)


def test_map_gdf_ggplot__geom_map():
    spec = ggplot() + geom_map(map=ROME_GDF, use_crs=ITALY_UTM_33N)
    assert ROME_COORD_UTM_33N.almost_equals(spec.as_dict()['layers'][0]['map'].geometry[0], decimal=4)


def test_data_gdf_ggplot__geom_map():
    spec = ggplot() + geom_map(data=ROME_GDF, use_crs=ITALY_UTM_33N)
    assert ROME_COORD_UTM_33N.almost_equals(spec.as_dict()['layers'][0]['data'].geometry[0], decimal=4)


def test_map_gdf_ggplot__geom_map__geom_point():
    spec = ggplot() + geom_map(map=ROME_GDF, use_crs=ITALY_UTM_33N) + geom_point(map=ROME_GDF, use_crs=ITALY_UTM_33N)

    assert ROME_COORD_UTM_33N.almost_equals(spec.as_dict()['layers'][0]['map'].geometry[0], decimal=4)  # geom_map
    assert ROME_COORD_UTM_33N.almost_equals(spec.as_dict()['layers'][1]['map'].geometry[0], decimal=4)  # geom_point


def test_map_gdf_ggplot__geom_map__geom_point__geom_imshow():
    spec = ggplot() \
           + geom_map(map=ROME_GDF, use_crs=ITALY_UTM_33N) \
           + geom_point(map=ROME_GDF, use_crs=ITALY_UTM_33N) \
           + geom_imshow(np.array([[290199.90107], [4639536.51707]], dtype=np.float64))

    assert ROME_COORD_UTM_33N.almost_equals(spec.as_dict()['layers'][0]['map'].geometry[0], decimal=4)  # geom_map
    assert ROME_COORD_UTM_33N.almost_equals(spec.as_dict()['layers'][1]['map'].geometry[0], decimal=4)  # geom_point


def test_livemap_with_use_crs_should_fail():
    should_fail(
        lambda: ggplot() + geom_livemap() + geom_map(map=ROME_GDF, use_crs=ITALY_UTM_33N),
        msg="livemap doesn't support `use_crs`"
    )

    should_fail(
        lambda: ggplot() + geom_livemap() + geom_map(data=ROME_GDF, use_crs=ITALY_UTM_33N),
        msg="livemap doesn't support `use_crs`"
    )


def test_livemap_without_use_crs_should_work():
    spec = ggplot() + geom_livemap() + geom_map(map=ROME_GDF)


def test_mixed_use_crs_should_fail():
    should_fail(
        lambda: ggplot() + geom_map(map=ROME_GDF) + geom_point(map=ROME_GDF, use_crs=ITALY_UTM_33N),
        msg=INVALID_CRS_USAGE
    )

    should_fail(
        lambda: ggplot() + geom_map(data=ROME_GDF) + geom_point(map=ROME_GDF, use_crs=ITALY_UTM_33N),
        msg=INVALID_CRS_USAGE
    )

    should_fail(
        lambda: ggplot() + geom_map(map=ROME_GDF) + geom_point(data=ROME_GDF, use_crs=ITALY_UTM_33N),
        msg=INVALID_CRS_USAGE
    )

    should_fail(
        lambda: ggplot() + geom_map(data=ROME_GDF) + geom_point(data=ROME_GDF, use_crs=ITALY_UTM_33N),
        msg=INVALID_CRS_USAGE
    )

    should_fail(
        lambda: ggplot() + geom_map(map=ROME_GDF, use_crs=ITALY_UTM_33N) + geom_point(map=ROME_GDF),
        msg=INVALID_CRS_USAGE
    )

    should_fail(
        lambda: ggplot() + geom_map(data=ROME_GDF, use_crs=ITALY_UTM_33N) + geom_point(map=ROME_GDF),
        msg=INVALID_CRS_USAGE
    )

    should_fail(
        lambda: ggplot() + geom_map(map=ROME_GDF, use_crs=ITALY_UTM_33N) + geom_point(data=ROME_GDF),
        msg=INVALID_CRS_USAGE
    )

    should_fail(
        lambda: ggplot() + geom_map(data=ROME_GDF, use_crs=ITALY_UTM_33N) + geom_point(data=ROME_GDF),
        msg=INVALID_CRS_USAGE
    )


def should_fail(f, msg):
    ex = None
    try:
        f()
    except Exception as e:
        ex = e

    if ex is None:
        raise ValueError("Expected failure didn't happen")

    return ex
