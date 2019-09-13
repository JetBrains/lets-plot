package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.projectionGeometry.*


interface World
interface Client


typealias LonLatPoint = Typed.Point<LonLat>
typealias LonLatRing = Typed.Ring<LonLat>
typealias LonLatPolygon = Typed.Polygon<LonLat>
typealias LonLatMultiPolygon = Typed.MultiPolygon<LonLat>

typealias ClientPoint = Typed.Point<Client>
typealias ClientRectangle = Typed.Rectangle<Client>

typealias WorldPoint = Typed.Point<World>
typealias WorldRectangle = Typed.Rectangle<World>



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

fun GeoRectangle.toLonLatRectangle(): LonLatRectangle {
    return LonLatRectangle(
        LonLatPoint(this.minLongitude(), this.minLatitude()),
        LonLatPoint(this.maxLongitude(), maxLongitude())
    )
}

fun Vector.toClientPoint() = ClientPoint(x, y)
fun DoubleVector.toLonLatPoint() = LonLatPoint(x, y)
fun DoubleVector.toWorldPoint() = WorldPoint(x, y)
fun DoubleVector.toClientPoint() = ClientPoint(x, y)
