import enum
from collections import defaultdict
from typing import List, Optional, NamedTuple, DefaultDict

from pandas import DataFrame

FULL_ANGLE = 360.0
ANTI_MERIDIAN = FULL_ANGLE / 2

__all__ = [
    'split_by_antimeridian'
]


def split_by_antimeridian(df: DataFrame) -> DataFrame:
    x_col_name = set(df.columns.values).intersection(['x', 'lon', 'ln', 'longitude'])
    y_col_name = set(df.columns.values).intersection(['y', 'lat', 'latitude'])

    if len(x_col_name) == 0 or len(y_col_name) == 0:
        return df

    x_col_name = x_col_name.pop()
    y_col_name = y_col_name.pop()

    new_x = []
    new_y = []
    for ring in _rings(df[x_col_name].tolist(), df[y_col_name].tolist()):
        coords = _AntimeridianButcher.cut_polygon(ring)
        new_x.extend(coords.x)
        new_y.extend(coords.y)

    new_df = DataFrame(
        {
            x_col_name: new_x,
            y_col_name: new_y
        }
    )

    return new_df


class Coords(NamedTuple):
    x: List[float]
    y: List[float]

    def append(self, x: float, y: float):
        self.x.append(x)
        self.y.append(y)


class Point(NamedTuple):
    x: float
    y: float


class Segment(NamedTuple):
    p1: Point
    p2: Point

    def contains(self, p: Point) -> bool:
        return self.p1 == p or self.p2 == p

    def other(self, p: Point) -> Point:
        assert self.contains(p)

        return self.p2 if p == self.p1 else self.p1


class Polygon:
    def __init__(self, x: List[float], y: List[float]):
        self._ring_start: int = None
        self.x = x
        self.y = y

    def append(self, x: float, y: float):
        self.x.append(x)
        self.y.append(y)

    def start_ring(self):
        self._ring_start: int = len(self.x)

    def zip(self):
        return zip(self.x, self.y)

    def close_ring(self):
        if self.x[self._ring_start] != self.x[-1]:
            self.x.append(self.x[self._ring_start])
            self.y.append(self.y[self._ring_start])


class Intersection(NamedTuple):
    p: Point
    index: int


class Side(enum.Enum):
    positive = 0
    negative = 1


ANTIMERIDIAN = 180.
ANTIMERIDIAN_SEGMENT = Segment(Point(ANTIMERIDIAN, 90.), Point(ANTIMERIDIAN, -90.))

Index = int


class _AntimeridianButcher:
    @staticmethod
    def cut_polygon(ring: Coords) -> Coords:
        return _AntimeridianButcher(ring.x, ring.y)._cut_polygon()

    def __init__(self, x: List[float], y: List[float]):
        self._visited_points: DefaultDict[int, int] = defaultdict(lambda: 0)
        self._x: List[float] = list(map(lambda x: _normalize(x), x))
        self._y: List[float] = y
        self._anti_intersections: List[Intersection] = []
        self._anti_segments: List[Segment] = []
        self._new_polygon: Polygon = Polygon([], [])
        self._ring_side: Side = None

        new_x = list(self._x)
        new_y = list(self._y)

        insertions_counter = 0
        # detect intersections with antimeridian
        for i in range(0, self._size() - 1):
            s = self._segment(i)
            if _should_split(s):
                inter = Intersection(_antimeridian_intersection(s), i + 1 + insertions_counter)
                insertions_counter = insertions_counter + 1
                self._anti_intersections.append(inter)
                new_x.insert(inter.index, inter.p.x)
                new_y.insert(inter.index, inter.p.y)

        # build new segments
        self._anti_intersections = sorted(self._anti_intersections, key=lambda inter: inter.p.y, reverse=True)
        assert len(self._anti_intersections) % 2 == 0
        points = [iter(map(lambda intersec: intersec.p, self._anti_intersections))] * 2
        for p1, p2 in zip(*points):
            self._anti_segments.append(Segment(p1, p2))

        # update points
        self._x = new_x
        self._y = new_y

    def _cut_polygon(self) -> Coords:
        # traverse points to split polygon
        # collect new ring
        start_point: Index = self._select_start_point()
        while start_point is not None:
            self._new_polygon.start_ring()
            self._ring_side = self._get_side(start_point)
            self._append_point(start_point)

            i: Index = self._next_point(start_point)
            while i != start_point:
                self._append_point(i)
                i = self._next_point(i)

            self._new_polygon.close_ring()
            start_point = self._select_start_point()

        return Coords(self._new_polygon.x, self._new_polygon.y)

    def _append_point(self, index: Index):
        if not self._is_anti_intersection(index):
            x = self._x[index]
            y = self._y[index]
        else:
            x = ANTI_MERIDIAN if self._ring_side == Side.positive else -ANTI_MERIDIAN
            y = self._point(index).y

        self._new_polygon.append(x, y)
        self._visited_points[index] += 1

    def _select_start_point(self) -> Optional[Index]:
        for i in range(0, self._size()):
            if self._visited_points[i] == 0:
                return i
        return None

    def _is_anti_intersection(self, index: Index) -> Optional[Intersection]:
        for inter in self._anti_intersections:
            if index == inter.index:
                return inter
        return None

    def _get_side(self, i: int) -> Side:
        return Side.positive if 0 < self._x[i] < ANTIMERIDIAN else Side.negative

    def _next_point(self, current: Index) -> Index:
        if self._is_anti_intersection(current) is None:
            return self._next_index(current)

        if self._get_side(self._next_index(current)) == self._ring_side:
            return self._next_index(current)

        point = self._point(current)
        segment = next(filter(lambda anti_segment: anti_segment.contains(point), self._anti_segments))
        assert segment is not None

        next_index = self._index_of(segment.other(point))

        if self._visited_points[next_index] > 2:
            raise ValueError('Invalid geometry')
        return next_index

    def _next_index(self, index: Index) -> Index:
        return 0 if index == self._size() - 1 else index + 1

    def _size(self) -> int:
        return len(self._x) if self._x is not None else 0

    def _segment(self, start_point: Index) -> Segment:
        end_point = self._next_index(start_point)
        return Segment(self._point(start_point), self._point(end_point))

    def _point(self, index: Index) -> Point:
        return Point(self._x[index], self._y[index])

    def _index_of(self, p: Point) -> Optional[Index]:
        for i in range(0, self._size()):
            if self._x[i] == p.x and self._y[i] == p.y:
                return i
        return None


def _antimeridian_intersection(segment: Segment) -> Optional[Point]:
    def make_positive(p: Point):
        assert p.x < FULL_ANGLE
        if p.x >= 0.:
            return p
        return Point(p.x + FULL_ANGLE, p.y)

    segment = Segment(make_positive(segment.p1), make_positive(segment.p2))

    xdiff = (ANTIMERIDIAN_SEGMENT.p1.x - ANTIMERIDIAN_SEGMENT.p2.x, segment.p1.x - segment.p2.x)
    ydiff = (ANTIMERIDIAN_SEGMENT.p1.y - ANTIMERIDIAN_SEGMENT.p2.y, segment.p1.y - segment.p2.y)

    def det(a, b):
        return a[0] * b[1] - a[1] * b[0]

    div = det(xdiff, ydiff)
    if div == 0:
        return None

    d = (det(*ANTIMERIDIAN_SEGMENT), det(*segment))
    x = det(d, xdiff) / div
    y = det(d, ydiff) / div
    return Point(_normalize(x), y)


def _should_split(s: Segment) -> bool:
    x1 = s.p1.x
    x2 = s.p2.x

    if x1 >= 0 and x2 >= 0:
        return False

    if x1 <= 0 and x2 <= 0:
        return False

    direct_dist = abs(x2 - x1)
    cross_dist = FULL_ANGLE - (abs(x1) + abs(x2))

    return True if cross_dist < direct_dist else False


def _normalize(x: float) -> float:
    result = x % FULL_ANGLE

    if result > ANTI_MERIDIAN:
        result -= FULL_ANGLE

    if result < -ANTI_MERIDIAN:
        result += FULL_ANGLE

    return result


def _rings(xs: List[float], ys: List[float]) -> Coords:
    start: Point = None
    for x, y in zip(xs, ys):
        if start is None:
            start = Point(x, y)
            ring = Coords([], [])
            ring.append(x, y)
        else:
            ring.append(x, y)
            if start.x == x and start.y == y:
                start = None
                yield ring
