#  Copyright (c) 2022. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
from typing import Any

import pytest

from lets_plot._type_utils import LazyModule

numpy = LazyModule("numpy")
geopandas = LazyModule("geopandas")
shapely = LazyModule("shapely")

from lets_plot.plot import ggplot, geom_point, geom_map, geom_imshow, geom_livemap

ITALY_UTM_33N = 'EPSG:32633'
WGS84 = 'epsg:4326'
TOLERANCE = 0.0001
INVALID_CRS_USAGE = "All geoms with map parameter should either use same `use_crs` or not use it at all"


def _rome_coord_wgs84() -> Any:
    return shapely.geometry.Point((12.47156, 41.87993))


def _rome_coord_utm_33n() -> Any:
    return shapely.geometry.Point((290199.90107, 4639536.51707))


def _rome_gdf():
    return geopandas.GeoDataFrame({'city': ['Rome']}, geometry=[_rome_coord_wgs84()], crs=WGS84)


@pytest.mark.skipif(not geopandas, reason="Requires geopandas")
def test_no_crs__geom_map():
    spec = ggplot() + geom_map(map=(_rome_gdf()))
    assert _rome_coord_wgs84().equals_exact(spec.as_dict()['layers'][0]['map'].geometry[0], TOLERANCE)


@pytest.mark.skipif(not geopandas, reason="Requires geopandas")
def test_no_crs__geom_map__geom_point():
    spec = ggplot() + geom_map(map=(_rome_gdf())) + geom_point(map=(_rome_gdf()))

    assert _rome_coord_wgs84().equals_exact(spec.as_dict()['layers'][0]['map'].geometry[0], TOLERANCE)
    assert _rome_coord_wgs84().equals_exact(spec.as_dict()['layers'][1]['map'].geometry[0], TOLERANCE)


@pytest.mark.skipif(not geopandas, reason="Requires geopandas")
def test_map_gdf_ggplot__geom_map():
    spec = ggplot() + geom_map(map=(_rome_gdf()), use_crs=ITALY_UTM_33N)
    assert _rome_coord_utm_33n().equals_exact(spec.as_dict()['layers'][0]['map'].geometry[0], TOLERANCE)


@pytest.mark.skipif(not geopandas, reason="Requires geopandas")
def test_data_gdf_ggplot__geom_map():
    spec = ggplot() + geom_map(data=(_rome_gdf()), use_crs=ITALY_UTM_33N)
    assert _rome_coord_utm_33n().equals_exact(spec.as_dict()['layers'][0]['data'].geometry[0], TOLERANCE)


@pytest.mark.skipif(not geopandas, reason="Requires geopandas")
def test_map_gdf_ggplot__geom_map__geom_point():
    spec = ggplot() + geom_map(map=(_rome_gdf()), use_crs=ITALY_UTM_33N) + geom_point(map=(_rome_gdf()),
                                                                                      use_crs=ITALY_UTM_33N)

    assert _rome_coord_utm_33n().equals_exact(spec.as_dict()['layers'][0]['map'].geometry[0], TOLERANCE)
    assert _rome_coord_utm_33n().equals_exact(spec.as_dict()['layers'][1]['map'].geometry[0], TOLERANCE)


@pytest.mark.skipif(not numpy or not geopandas, reason="Requires numpy and geopandas")
def test_map_gdf_ggplot__geom_map__geom_point__geom_imshow():
    spec = ggplot() \
           + geom_map(map=(_rome_gdf()), use_crs=ITALY_UTM_33N) \
           + geom_point(map=(_rome_gdf()), use_crs=ITALY_UTM_33N) \
           + geom_imshow(numpy.array([[290199.90107], [4639536.51707]], dtype=numpy.float64))

    assert _rome_coord_utm_33n().equals_exact(spec.as_dict()['layers'][0]['map'].geometry[0], TOLERANCE)
    assert _rome_coord_utm_33n().equals_exact(spec.as_dict()['layers'][1]['map'].geometry[0], TOLERANCE)


@pytest.mark.skipif(not geopandas, reason="Requires geopandas")
def test_livemap_with_use_crs_should_fail():
    should_fail(
        lambda: ggplot() + geom_livemap() + geom_map(map=_rome_gdf(), use_crs=ITALY_UTM_33N),
        msg="livemap doesn't support `use_crs`"
    )

    should_fail(
        lambda: ggplot() + geom_livemap() + geom_map(data=_rome_gdf(), use_crs=ITALY_UTM_33N),
        msg="livemap doesn't support `use_crs`"
    )


@pytest.mark.skipif(not geopandas, reason="Requires geopandas")
def test_livemap_without_use_crs_should_work():
    ggplot() + geom_livemap() + geom_map(map=(_rome_gdf()))


@pytest.mark.skipif(not geopandas, reason="Requires geopandas")
def test_mixed_use_crs_should_fail():
    should_fail(
        lambda: ggplot() + geom_map(map=_rome_gdf()) + geom_point(map=_rome_gdf(), use_crs=ITALY_UTM_33N),
        msg=INVALID_CRS_USAGE
    )

    should_fail(
        lambda: ggplot() + geom_map(data=_rome_gdf()) + geom_point(map=_rome_gdf(), use_crs=ITALY_UTM_33N),
        msg=INVALID_CRS_USAGE
    )

    should_fail(
        lambda: ggplot() + geom_map(map=_rome_gdf()) + geom_point(data=_rome_gdf(), use_crs=ITALY_UTM_33N),
        msg=INVALID_CRS_USAGE
    )

    should_fail(
        lambda: ggplot() + geom_map(data=_rome_gdf()) + geom_point(data=_rome_gdf(), use_crs=ITALY_UTM_33N),
        msg=INVALID_CRS_USAGE
    )

    should_fail(
        lambda: ggplot() + geom_map(map=_rome_gdf(), use_crs=ITALY_UTM_33N) + geom_point(map=_rome_gdf()),
        msg=INVALID_CRS_USAGE
    )

    should_fail(
        lambda: ggplot() + geom_map(data=_rome_gdf(), use_crs=ITALY_UTM_33N) + geom_point(map=_rome_gdf()),
        msg=INVALID_CRS_USAGE
    )

    should_fail(
        lambda: ggplot() + geom_map(map=_rome_gdf(), use_crs=ITALY_UTM_33N) + geom_point(data=_rome_gdf()),
        msg=INVALID_CRS_USAGE
    )

    should_fail(
        lambda: ggplot() + geom_map(data=_rome_gdf(), use_crs=ITALY_UTM_33N) + geom_point(data=_rome_gdf()),
        msg=INVALID_CRS_USAGE
    )


def should_fail(f, msg):
    with pytest.raises(Exception, match=msg):
        f()
