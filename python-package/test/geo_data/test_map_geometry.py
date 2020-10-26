#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import math
from collections import deque
from typing import List, Iterable, Deque, Union

from pandas import DataFrame

from lets_plot.geo_data.map_geometry import Point, Coords, split_by_antimeridian


def _ring(*points) -> Coords:
    x = []
    y = []
    for p in points:
        x.append(p.x)
        y.append(p.y)

    return Coords(x, y)


def _point(x: float, y: float) -> Point:
    return Point(x, y)


RECT_INTERSECTING_MERIDIAN = _ring(
    _point(10, 0),
    _point(10, 10),
    _point(-10, 10),
    _point(-10, 0),
    _point(10, 0),
)

RECT_WITHOUT_INTERSECTIONS = _ring(
    _point(170, 0),
    _point(170, 10),
    _point(180, 10),
    _point(180, 0),
    _point(170, 0),
)

RECT_INTERSECTING_ANTIMERIDIAN = _ring(
    _point(170, 0),
    _point(170, 10),
    _point(-170, 10),
    _point(-170, 0),
    _point(170, 0),
)
EXPECTED_RECT_INTERSECTING_FIRST_PART = _ring(
    _point(170, 0),
    _point(170, 10),
    _point(180, 10),
    _point(180, 0),
    _point(170, 0),
)
EXPECTED_RECT_INTERSECTING_SECOND_PART = _ring(
    _point(-180, 0),
    _point(-180, 10),
    _point(-170, 10),
    _point(-170, 0),
    _point(-180, 0),
)

FULL_ANGLE = 360.0
ANTI_MERIDIAN = FULL_ANGLE / 2


def test_one_point_on_antimeridian():
    p = _poly(
        _ring(
            _point(170, 0),
            _point(180, 0),
            _point(170, 10),
            _point(170, 0)
        )
    )

    assert_poly(
        p,
        _split_poly(p)
    )


def test_two_point_on_antimeridian():
    p = _poly(
        _ring(
            _point(170, 0),
            _point(180, 0),
            _point(175, 10),
            _point(180, 10),
            _point(170, 20),
            _point(170, 0)
        )
    )

    assert_poly(
        p,
        _split_poly(p)
    )


def test_not_intersect_antimeridian():
    p = _poly(
        RECT_WITHOUT_INTERSECTIONS
    )

    assert_poly(
        p,
        _split_poly(p)
    )


def test_rect_on_antimeridian():
    p = _poly(
        RECT_INTERSECTING_ANTIMERIDIAN
    )

    assert_poly(
        _poly(
            EXPECTED_RECT_INTERSECTING_FIRST_PART,
            EXPECTED_RECT_INTERSECTING_SECOND_PART
        ),
        _split_poly(p)
    )


def test_not_closed_polygon():
    p = _poly(
        _ring(
            _point(170, 0),
            _point(-170, 0),
            _point(-170, 10),
            _point(170, 10)
        )
    )

    assert_poly(
        _poly(),
        _split_poly(p)
    )


def test_poly_with_rings():
    fig1 = Coords(
        [140, -170, -140, -150, 130, 135, -140, -150, 90, 140],
        [-30, 0, 30, 50, 60, 70, 75, 80, 75, -30]
    )

    fig1_r1 = _ring(
        _point(140.0, -30),
        _point(180.0, -6.0),
        _point(180.0, 53.75),
        _point(130.0, 60),
        _point(135.0, 70),
        _point(180.0, 72.647059),
        _point(180.0, 78.75),
        _point(90.0, 75),
        _point(140.0, -30),
    )

    fig1_r2: Coords = _ring(
        _point(-170.0, 0),
        _point(-140.0, 30),
        _point(-150.0, 50),
        _point(-180.0, 53.75),
        _point(-180.0, -6.0),
        _point(-170.0, 0),
    )

    fig1_r3: Coords = _ring(
        _point(-140.0, 75),
        _point(-150.0, 80),
        _point(-180.0, 78.75),
        _point(-180.0, 72.647059),
        _point(-140.0, 75),
    )

    fig2 = Coords(
        [130, 140, -170, -140, 130],
        [-35, -74, -70, -61, -35]
    )

    fig2_r1 = _ring(
        _point(130.0, -35),
        _point(140.0, -74),
        _point(180.0, -70.8),
        _point(180.0, -49.444444),
        _point(130.0, -35)
    )

    fig2_r2 = _ring(
        _point(-170.0, -70),
        _point(-140.0, -61),
        _point(-180.0, -49.444444),
        _point(-180.0, -70.8),
        _point(-170.0, -70),
    )

    lon = fig1.x + fig2.x
    lat = fig1.y + fig2.y

    assert_poly(
        _poly(
            fig1_r1,
            fig1_r2,
            fig1_r3,
            fig2_r1,
            fig2_r2
        ),
        _split_columns(lon, lat)
    )


def test_complex_polygon_case():
    p = _poly(
        _ring(
            _point(170, 0),
            _point(170, 15),
            _point(-170, 15),
            _point(-170, 10),
            _point(175, 10),
            _point(175, 5),
            _point(-170, 5),
            _point(-170, 0),
            _point(170, 0),
        )
    )

    assert_poly(
        _poly(
            _ring(
                _point(170, 0),
                _point(170, 15),
                _point(180, 15),
                _point(180, 10),
                _point(175, 10),
                _point(175, 5),
                _point(180, 5),
                _point(180, 0),
                _point(170, 0),
            ),
            _ring(
                _point(-180, 15),
                _point(-170, 15),
                _point(-170, 10),
                _point(-180, 10),
                _point(-180, 15),
            ),
            _ring(
                _point(-180, 5),
                _point(-170, 5),
                _point(-170, 0),
                _point(-180, 0),
                _point(-180, 5),
            )
        ),
        _split_poly(p)
    )


def test_rect_should_intersect_meridian():
    p = _poly(
        RECT_INTERSECTING_MERIDIAN
    )

    assert_poly(
        _poly(RECT_INTERSECTING_MERIDIAN),
        _split_poly(p)
    )


def test_with_non_intersecting_rects():
    lon = list(RECT_INTERSECTING_MERIDIAN.x) + RECT_WITHOUT_INTERSECTIONS.x
    lat = list(RECT_INTERSECTING_MERIDIAN.y) + RECT_WITHOUT_INTERSECTIONS.y

    assert_poly(
        _poly(
            RECT_INTERSECTING_MERIDIAN,
            RECT_WITHOUT_INTERSECTIONS
        ),
        _split_columns(lon, lat)
    )


def test_with_mixed_rects():
    lon = list(RECT_INTERSECTING_ANTIMERIDIAN.x) + RECT_WITHOUT_INTERSECTIONS.x
    lat = list(RECT_INTERSECTING_ANTIMERIDIAN.y) + RECT_WITHOUT_INTERSECTIONS.y

    assert_poly(
        _poly(
            EXPECTED_RECT_INTERSECTING_FIRST_PART,
            EXPECTED_RECT_INTERSECTING_SECOND_PART,
            RECT_WITHOUT_INTERSECTIONS
        ),
        _split_columns(lon, lat)
    )


def _compare_double(expected: Deque[Point], actual: Deque[Point]) -> bool:
    if len(expected) != len(actual):
        return False

    for e, a in zip(expected, actual):
        if not math.isclose(e.x, a.x, rel_tol=1e-6) or \
                not math.isclose(e.y, a.y, rel_tol=1e-6):
            return False
        return True


def _compare_rings(expected: Deque[Point], actual: Deque[Point]) -> bool:
    if len(expected) != len(actual):
        return False

    for n in range(1, len(actual) + 1):
        if _compare_double(actual, expected):
            return True
        else:
            actual.rotate(n)
            n += 1


def assert_poly(expected: Coords, actual: Union[Coords, DataFrame]):
    if isinstance(actual, DataFrame):
        actual = Coords(actual['lon'].tolist(), actual['lat'].tolist())

    for expected_ring in _rings(expected):
        matched_ring = list(filter(lambda r: _compare_rings(expected_ring, r), _rings(actual)))
        if not matched_ring:
            print('Expected ring not found: ' + str(expected_ring))
            assert False


def _points(point_coords: Coords) -> Iterable[Point]:
    for i in range(len(point_coords.x)):
        x = point_coords.x[i]
        y = point_coords.y[i]
        yield Point(x, y)


def _poly(*rings) -> Coords:
    x = []
    y = []
    for r in rings:
        x.extend(r.x)
        y.extend(r.y)

    return Coords(x, y)


def _rings(poly: Coords) -> Iterable[Deque[Point]]:
    ring: Deque[Point] = deque()
    start: Point = None
    for p in _points(poly):
        if start is None:
            start = p
            ring = deque()
            ring.append(p)
        else:
            if p != start:
                ring.append(p)
            else:
                start = None
                yield ring


def _split_poly(poly: Coords) -> DataFrame:
    df = DataFrame({
        'lon': poly.x,
        'lat': poly.y
    })
    return split_by_antimeridian(df)


def _split_columns(x: List[float], y: List[float]) -> DataFrame:
    df = DataFrame({
        'lon': x,
        'lat': y
    })
    return split_by_antimeridian(df)
