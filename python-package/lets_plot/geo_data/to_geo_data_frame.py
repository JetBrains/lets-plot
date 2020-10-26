from typing import List

import shapely
from geopandas import GeoDataFrame
from pandas import DataFrame
from shapely.geometry import box

from lets_plot.geo_data import DataFrameProvider, select_not_empty_name, DF_REQUEST, DF_FOUND_NAME, abstractmethod
from lets_plot.geo_data.gis.response import GeocodedFeature, GeoRect, Boundary, Multipolygon, Polygon, GeoPoint

ShapelyPoint = shapely.geometry.Point
ShapelyLinearRing = shapely.geometry.LinearRing
ShapelyPolygon = shapely.geometry.Polygon
ShapelyMultiPolygon = shapely.geometry.MultiPolygon


def _create_geo_data_frame(data, geometry) -> DataFrame:
    return GeoDataFrame(
        data,
        #crs={'init': 'epsg:4326'}, # causes warning in Jupyter. Everything looks fine w/o. Related issue: https://github.com/geopandas/geopandas/issues/1245
        geometry=geometry
    )


class RectGeoDataFrame(DataFrameProvider):

    @staticmethod
    def intersected_by_antimeridian(lonmin: float, lonmax: float):
        return lonmin > lonmax

    @staticmethod
    def limit2geometry(lonmin: float, latmin: float, lonmax: float, latmax: float):
        return box(lonmin, latmin, lonmax, latmax)

    def __init__(self):
        super().__init__()
        self._lonmin: List[float] = []
        self._latmin: List[float] = []
        self._lonmax: List[float] = []
        self._latmax: List[float] = []

    def to_data_frame(self, features: List[GeocodedFeature]) -> DataFrame:
        data = self._calc_common_data(features)
        geometry = [RectGeoDataFrame.limit2geometry(lmt[0], lmt[1], lmt[2], lmt[3]) for lmt in
                    zip(self._lonmin, self._latmin, self._lonmax, self._latmax)]
        return _create_geo_data_frame(data, geometry=geometry)

    def _calc_common_data(self, features: List[GeocodedFeature]) -> dict:
        for feature in features:
            rects: GeoRect = self._read_rect(feature)
            for rect in rects:
                self._lonmin.append(rect.min_lon)
                self._latmin.append(rect.min_lat)
                self._lonmax.append(rect.max_lon)
                self._latmax.append(rect.max_lat)
                self._request.append(select_not_empty_name(feature))
                self._found_name.append(feature.name)

        return {
            DF_REQUEST: self._request,
            DF_FOUND_NAME: self._found_name
        }

    def _read_rect(self, feature: GeocodedFeature) -> List[GeoRect]:
        rect: GeoRect = self._select_rect(feature)
        if RectGeoDataFrame.intersected_by_antimeridian(rect.min_lon, rect.max_lon):
            return [
                GeoRect(min_lon=rect.min_lon, max_lon=180., min_lat=rect.min_lat, max_lat=rect.max_lat),
                GeoRect(min_lon=-180., max_lon=rect.max_lon, min_lat=rect.min_lat, max_lat=rect.max_lat)
            ]
        else:
            return [rect]

    @abstractmethod
    def _select_rect(self, feature: GeocodedFeature) -> GeoRect:
        pass


class CentroidsGeoDataFrame(DataFrameProvider):
    def __init__(self):
        super().__init__()
        self._lons: List[float] = []
        self._lats: List[float] = []

    def to_data_frame(self, features: List[GeocodedFeature]) -> DataFrame:
        for feature in features:
            self._lons.append(feature.centroid.lon)
            self._lats.append(feature.centroid.lat)
            self._request.append(select_not_empty_name(feature))
            self._found_name.append(feature.name)

        data = {
            DF_REQUEST: self._request,
            DF_FOUND_NAME: self._found_name,
        }
        geometry = [ShapelyPoint(pnt[0], pnt[1]) for pnt in zip(self._lons, self._lats)]
        return _create_geo_data_frame(data, geometry)


class BoundariesGeoDataFrame(DataFrameProvider):
    def __init__(self):
        super().__init__()

    def to_data_frame(self, features: List[GeocodedFeature]) -> DataFrame:
        geometry = []
        for feature in features:
            self._request.append(select_not_empty_name(feature))
            self._found_name.append(feature.name)
            geometry.append(self._geo_parse_geometry(feature.boundary))

        df = {
            DF_REQUEST: self._request,
            DF_FOUND_NAME: self._found_name
        }
        return _create_geo_data_frame(df, geometry=geometry)

    def _geo_parse_geometry(self, boundary: Boundary):

        geometry = boundary.geometry
        if isinstance(geometry, GeoPoint):
            return self._geo_parse_point(geometry)

        if isinstance(geometry, Polygon):
            return self._geo_parse_polygon(geometry)

        if isinstance(geometry, Multipolygon):
            return self._geo_parse_multipolygon(geometry)

        raise ValueError('Invalid geometry type')

    def _geo_parse_multipolygon(self, geometry: Multipolygon) -> ShapelyMultiPolygon:
        geo_polygons: List[ShapelyPolygon] = [self._geo_parse_polygon(polygon) for polygon in geometry.polygons]
        return ShapelyMultiPolygon(geo_polygons)

    def _geo_parse_polygon(self, polygon: Polygon) -> ShapelyPolygon:
        geo_rings: List[ShapelyLinearRing] = [ShapelyLinearRing([(p.lon, p.lat) for p in ring.points]) for ring in polygon.rings]
        return ShapelyPolygon(shell=geo_rings[0], holes=geo_rings[1:])

    def _geo_parse_point(self, geometry_data: GeoPoint) -> ShapelyPoint:
        return ShapelyPoint((geometry_data.lon, geometry_data.lat))


class LimitsGeoDataFrame(RectGeoDataFrame):
    def _select_rect(self, feature: GeocodedFeature) -> GeoRect:
        return feature.limit


class PositionsGeoDataFrame(RectGeoDataFrame):
    def _select_rect(self, feature: GeocodedFeature) -> GeoRect:
        return feature.position
