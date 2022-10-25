from typing import List

import shapely
from geopandas import GeoDataFrame
from pandas import DataFrame
from shapely.geometry import box

from lets_plot.geo_data import PlacesDataFrameBuilder, abstractmethod
from lets_plot.geo_data.geocodes import _zip_answers
from lets_plot.geo_data.gis.request import RegionQuery, LevelKind
from lets_plot.geo_data.gis.response import Answer, GeocodedFeature, GeoRect, Boundary, Multipolygon, Polygon, GeoPoint

ShapelyPoint = shapely.geometry.Point
ShapelyLinearRing = shapely.geometry.LinearRing
ShapelyPolygon = shapely.geometry.Polygon
ShapelyMultiPolygon = shapely.geometry.MultiPolygon


def _create_geo_data_frame(data, geometry) -> DataFrame:
    return GeoDataFrame(
        data,
        crs='EPSG:4326',
        geometry=geometry
    )


class RectGeoDataFrame:
    def __init__(self):
        super().__init__()
        self._lonmin: List[float] = []
        self._latmin: List[float] = []
        self._lonmax: List[float] = []
        self._latmax: List[float] = []

    def to_data_frame(self, answers: List[Answer], queries: List[RegionQuery], level_kind: LevelKind) -> DataFrame:
        assert len(answers) == len(queries)
        places = PlacesDataFrameBuilder(level_kind)

        for query, answer in _zip_answers(queries, answers):
            for feature in answer.features:
                rects: List[GeoRect] = self._read_rect(feature)
                for rect in rects:
                    places.append_row(query, feature)
                    self._lonmin.append(rect.start_lon)
                    self._latmin.append(rect.min_lat)
                    self._lonmax.append(rect.end_lon)
                    self._latmax.append(rect.max_lat)

        geometry = [
            box(lmt[0], lmt[1], lmt[2], lmt[3]) for lmt in zip(self._lonmin, self._latmin, self._lonmax, self._latmax)
        ]
        return _create_geo_data_frame(places.build_dict(), geometry=geometry)

    def _read_rect(self, feature: GeocodedFeature) -> List[GeoRect]:
        rect: GeoRect = self._select_rect(feature)
        if rect.crosses_antimeridian():
            return [
                GeoRect(start_lon=rect.start_lon, end_lon=180., min_lat=rect.min_lat, max_lat=rect.max_lat),
                GeoRect(start_lon=-180., end_lon=rect.end_lon, min_lat=rect.min_lat, max_lat=rect.max_lat)
            ]
        else:
            return [rect]

    @abstractmethod
    def _select_rect(self, feature: GeocodedFeature) -> GeoRect:
        pass


class CentroidsGeoDataFrame:
    def __init__(self):
        super().__init__()
        self._lons: List[float] = []
        self._lats: List[float] = []

    def to_data_frame(self, answers: List[Answer], queries: List[RegionQuery], level_kind: LevelKind) -> DataFrame:
        places = PlacesDataFrameBuilder(level_kind)

        for query, answer in _zip_answers(queries, answers):
            for feature in answer.features:
                places.append_row(query, feature)
                self._lons.append(feature.centroid.lon)
                self._lats.append(feature.centroid.lat)

        geometry = [ShapelyPoint(pnt[0], pnt[1]) for pnt in zip(self._lons, self._lats)]
        return _create_geo_data_frame(places.build_dict(), geometry)


class BoundariesGeoDataFrame:
    def __init__(self):
        super().__init__()

    def to_data_frame(self, answers: List[Answer], queries: List[RegionQuery], level_kind: LevelKind) -> DataFrame:
        places = PlacesDataFrameBuilder(level_kind)

        geometry = []
        for query, answer in _zip_answers(queries, answers):
            for feature in answer.features:
                places.append_row(query, feature)
                geometry.append(self._geo_parse_geometry(feature.boundary))

        return _create_geo_data_frame(places.build_dict(), geometry=geometry)

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
        geo_rings: List[ShapelyLinearRing] = [
            ShapelyLinearRing([(p.lon, p.lat) for p in ring.points]) for ring in polygon.rings
        ]
        return ShapelyPolygon(shell=geo_rings[0], holes=geo_rings[1:])

    def _geo_parse_point(self, geometry_data: GeoPoint) -> ShapelyPoint:
        return ShapelyPoint((geometry_data.lon, geometry_data.lat))


class LimitsGeoDataFrame(RectGeoDataFrame):
    def _select_rect(self, feature: GeocodedFeature) -> GeoRect:
        return feature.limit


class PositionsGeoDataFrame(RectGeoDataFrame):
    def _select_rect(self, feature: GeocodedFeature) -> GeoRect:
        return feature.position
