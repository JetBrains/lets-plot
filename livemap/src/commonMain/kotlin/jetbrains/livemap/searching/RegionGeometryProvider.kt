package jetbrains.livemap.searching

import jetbrains.datalore.base.projectionGeometry.MultiPolygon

interface RegionGeometryProvider {
    fun getGeometry(regionId: String): MultiPolygon
}