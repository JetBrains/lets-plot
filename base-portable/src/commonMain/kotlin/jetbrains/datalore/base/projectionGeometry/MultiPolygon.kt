package jetbrains.datalore.base.projectionGeometry

class MultiPolygon<TypeT>(polygons: List<Polygon<TypeT>>) : AbstractGeometryList<Polygon<TypeT>>(polygons)