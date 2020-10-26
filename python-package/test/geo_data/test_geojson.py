#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from typing import List

from lets_plot.geo_data.gis.geometry import Ring, Polygon, Multipolygon
from lets_plot.geo_data.gis.json_response import GeoJson
from .geo_data import multipolygon, polygon, ring, point, format_multipolygon, GJPoint, format_polygon


def test_boundaries_polygon_response():
    geometry: Polygon = GeoJson.parse_geometry(
        format_polygon(
            polygon(
                ring(
                    point(1, 2), point(3, 4), point(5, 6)
                ),
                ring(
                    point(11, 11), point(11, 13), point(13, 13)
                )
            )
        )
    )

    assert_ring(geometry.rings[0], [point(1, 2), point(3, 4), point(5, 6)])
    assert_ring(geometry.rings[1], [point(11, 11), point(11, 13), point(13, 13)])


def test_boundaries_multipolygon_response():
    r0 = [point(1, 2), point(3, 4), point(5, 6), point(1, 2)]
    r1 = [point(7, 8), point(9, 10), point(11, 12), point(7, 8)]
    r2 = [point(13, 14), point(15, 16), point(17, 18), point(13, 14)]
    r3 = [point(19, 20), point(21, 22), point(23, 24), point(19, 20)]

    boundary: Multipolygon = GeoJson.parse_geometry(
        format_multipolygon(
            multipolygon(
                polygon(
                    ring(*r0)
                ),
                polygon(
                    ring(*r1)
                ),
                polygon(
                    ring(*r2),
                    ring(*r3)
                )
            )
        )
    )

    assert_polygon(boundary.polygons[0], polygon(ring(*r0)))
    assert_polygon(boundary.polygons[1], polygon(ring(*r1)))
    assert_polygon(boundary.polygons[2], polygon(ring(*r2), ring(*r3)))


def assert_ring(r: Ring, expected: List[GJPoint]):
    assert [[p.lon, p.lat] for p in r.points] == expected


def assert_polygon(p: Polygon, expected: List[List[GJPoint]]):
    for r, e in zip(p.rings, expected):
        assert_ring(r, e)
