package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.projectionGeometry.*

/**
 * Coordinates in [0.0.. 256.0]
 */
interface World

/**
 * Coordinates used by Context2d
 */
interface Client


typealias LonLatPoint = Vec<LonLat>
typealias LonLatRing = Ring<LonLat>
typealias LonLatPolygon = Polygon<LonLat>
typealias LonLatMultiPolygon = MultiPolygon<LonLat>

typealias ClientPoint = Vec<Client>
typealias ClientRectangle = Rect<Client>

typealias WorldPoint = Vec<World>
typealias WorldRectangle = Rect<World>


class Coordinates {
    companion object {
        val ZERO_LONLAT_POINT = LonLatPoint(0.0, 0.0)
        val ZERO_WORLD_POINT = WorldPoint(0.0, 0.0)
        val ZERO_CLIENT_POINT = ClientPoint(0.0, 0.0)
    }

}

fun newDoubleRectangle(origin: AnyPoint, dimension: AnyPoint): DoubleRectangle {
    return DoubleRectangle(origin.x, origin.y, dimension.x, dimension.y)
}

fun Vector.toClientPoint() = ClientPoint(x, y)
fun DoubleVector.toClientPoint() = ClientPoint(x, y)
