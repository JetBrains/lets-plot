package jetbrains.livemap.tiles

import jetbrains.datalore.base.projectionGeometry.GeoUtils.getTileCount
import jetbrains.datalore.base.projectionGeometry.GeoUtils.getTileOrigin
import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.datalore.base.projectionGeometry.times

fun <TypeT> getTileRect(mapRect: Rect<TypeT>, tileKey: String): Rect<TypeT> {
    val origin = getTileOrigin(mapRect, tileKey)
    val dimension = mapRect.dimension * (1.0 / getTileCount(tileKey.length))

    return Rect(origin, dimension)
}
