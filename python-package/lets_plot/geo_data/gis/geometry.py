from typing import List

from ..type_assertion import assert_list_type


class GeometryBase:
    pass


class GeoPoint(GeometryBase):
    def __init__(self, lon: float, lat: float):
        self.lon: float = lon
        self.lat: float = lat

    def __eq__(self, other):
        return isinstance(other, GeoPoint) \
               and self.lon == other.lon \
               and self.lat == other.lat

    def __ne__(self, other):
        return not self == other


class GeoRect(GeometryBase):
    def __init__(self, min_lon: float, min_lat: float, max_lon: float, max_lat: float):
        self.min_lon: float = min_lon
        self.min_lat: float = min_lat
        self.max_lon: float = max_lon
        self.max_lat: float = max_lat

    def __eq__(self, other):
        return isinstance(other, GeoRect) \
               and self.min_lon == other.min_lon \
               and self.min_lat == other.min_lat \
               and self.max_lon == other.max_lon \
               and self.max_lat == other.max_lat

    def __ne__(self, other):
        return not self == other


class Ring(GeometryBase):
    def __init__(self, points: List[GeoPoint]):
        assert_list_type(points, GeoPoint)
        self.points: List[GeoPoint] = points

    def __eq__(self, other):
        return isinstance(other, Ring) \
               and self.points == other.points

    def __ne__(self, other):
        return not self == other


class Polygon(GeometryBase):
    def __init__(self, rings: List[Ring]):
        assert_list_type(rings, Ring)
        self.rings: List[Ring] = rings

    def __eq__(self, other):
        if not isinstance(other, Polygon):
            return False

        for r, o in zip(self.rings, other.rings):
            if r != o:
                return False
        return True

    def __ne__(self, other):
        return not self == other


class Multipolygon(GeometryBase):
    def __init__(self, polygons: List[Polygon]):
        assert_list_type(polygons, Polygon)
        self.polygons: List[Polygon] = polygons

    def __eq__(self, other):
        if not isinstance(other, Multipolygon):
            return False

        for p, o in zip(self.polygons, other.polygons):
            if p != o:
                return False
        return True

    def __ne__(self, other):
        return not self == other
