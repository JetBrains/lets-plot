package jetbrains.livemap.tiles

import jetbrains.datalore.base.projectionGeometry.GeoUtils.getTileCount
import jetbrains.datalore.base.projectionGeometry.GeoUtils.getTileOrigin
import jetbrains.datalore.base.projectionGeometry.Typed

fun <ProjT> getTileRect(mapRect: Typed.Rectangle<ProjT>, tileKey: String): Typed.Rectangle<ProjT> {
    val origin = getTileOrigin(mapRect, tileKey)
    val dimension = mapRect.dimension.mul(1.0 / getTileCount(tileKey.length))

    return Typed.Rectangle(origin, dimension)
}
