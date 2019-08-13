package jetbrains.livemap

import jetbrains.datalore.base.projectionGeometry.MultiPolygon

interface RegionGeometryConsumer {
    fun updateGeometryMap(geometryMapChanges: Map<String, MultiPolygon>)
}