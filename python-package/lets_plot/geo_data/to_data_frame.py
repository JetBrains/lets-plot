from abc import abstractmethod
from typing import List, Tuple

from pandas import DataFrame

from .gis.geometry import GeoRect, Ring, Polygon, Multipolygon
from .gis.response import GeocodedFeature, Boundary
from .regions import DF_REQUEST, DF_FOUND_NAME, DF_LON, DF_LAT, DF_LONMIN, DF_LATMIN, DF_LONMAX, DF_LATMAX, \
    DataFrameProvider


class BoundariesDataFrame(DataFrameProvider):
    def __init__(self):
        super().__init__()
        self._lons: List[float] = []
        self._lats: List[float] = []

    def to_data_frame(self, features: List[GeocodedFeature]) -> DataFrame:
        for feature in features:
            lons = []
            lats = []
            self._get_lons_lats(feature.boundary, lons, lats)
            size = len(lons)
            self._lons.extend(lons)
            self._lats.extend(lats)
            self._extend_names(feature.query, feature.name, size)

        data = {
            DF_REQUEST: self._request,
            DF_FOUND_NAME: self._found_name,
            DF_LON: self._lons,
            DF_LAT: self._lats,
        }
        return DataFrame(data, columns=[DF_REQUEST, DF_LON, DF_LAT, DF_FOUND_NAME])

    def _get_lons_lats(self, boundary: Boundary, lons: List[float], lats: List[float]) -> Tuple[List[float], List[float]]:
        if isinstance(boundary.geometry, Polygon):
            return self._get_polygon_lons_lats(boundary.geometry.rings, lons, lats)

        if isinstance(boundary.geometry, Multipolygon):
            for polygon in boundary.geometry.polygons:
                self._get_polygon_lons_lats(polygon.rings, lons, lats)

    def _get_polygon_lons_lats(self, rings: List[Ring], lons: List[float], lats: List[float]):
        for ring in rings:
            for point in ring.points:
                lons.append(point.lon)
                lats.append(point.lat)

        return lons, lats


class CentroidsDataFrame(DataFrameProvider):
    def __init__(self):
        super().__init__()
        self._lons: List[float] = []
        self._lats: List[float] = []

    def to_data_frame(self, features: List[GeocodedFeature]) -> DataFrame:
        for feature in features:
            self._lons.append(feature.centroid.lon)
            self._lats.append(feature.centroid.lat)
            self._extend_names(feature.query, feature.name, 1)

        data = {
            DF_REQUEST: self._request,
            DF_FOUND_NAME: self._found_name,
            DF_LON: self._lons,
            DF_LAT: self._lats,
        }
        return DataFrame(data, columns=[DF_REQUEST, DF_LON, DF_LAT, DF_FOUND_NAME])


class RectDataFrame(DataFrameProvider):

    @staticmethod
    def intersected_by_antimeridian(lonmin: float, lonmax: float):
        return lonmin > lonmax

    def __init__(self):
        super().__init__()
        self._lonmin: List[float] = []
        self._latmin: List[float] = []
        self._lonmax: List[float] = []
        self._latmax: List[float] = []

    def to_data_frame(self, features: List[GeocodedFeature]) -> DataFrame:
        data = self._calc_common_data(features)
        data[DF_LONMIN] = self._lonmin
        data[DF_LATMIN] = self._latmin
        data[DF_LONMAX] = self._lonmax
        data[DF_LATMAX] = self._latmax
        return DataFrame(data, columns=[DF_REQUEST, DF_LONMIN, DF_LATMIN, DF_LONMAX, DF_LATMAX, DF_FOUND_NAME])

    def _calc_common_data(self, features: List[GeocodedFeature]) -> dict:
        for feature in features:
            rows_count: int = self._read_rect(feature)
            self._request.extend([(self._get_request(feature))] * rows_count)
            self._found_name.extend([self._get_found_name(feature)] * rows_count)

        return {
            DF_REQUEST: self._request,
            DF_FOUND_NAME: self._found_name
        }

    def _read_rect(self, feature: GeocodedFeature) -> int:
        rect: GeoRect = self._select_rect(feature)
        if RectDataFrame.intersected_by_antimeridian(rect.min_lon, rect.max_lon):
            self._append(rect.min_lon, 180., rect.min_lat, rect.max_lat)
            self._append(-180., rect.max_lon, rect.min_lat, rect.max_lat)
            return 2
        else:
            self._append(rect.min_lon, rect.max_lon, rect.min_lat, rect.max_lat)
            return 1

    def _append(self, lonmin, lonmax, latmin, latmax):
        self._lonmin.append(lonmin)
        self._latmin.append(latmin)
        self._lonmax.append(lonmax)
        self._latmax.append(latmax)

    @abstractmethod
    def _select_rect(self, feature: GeocodedFeature) -> GeoRect:
        pass


class LimitsDataFrame(RectDataFrame):
    def _select_rect(self, feature: GeocodedFeature) -> GeoRect:
        return feature.limit


class PositionsDataFrame(RectDataFrame):
    def _select_rect(self, feature: GeocodedFeature) -> GeoRect:
        return feature.position


