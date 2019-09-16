package jetbrains.datalore.base.projectionGeometry

class MultiLineString<TypeT>(geometry: List<LineString<TypeT>>) : AbstractGeometryList<LineString<TypeT>>(geometry)