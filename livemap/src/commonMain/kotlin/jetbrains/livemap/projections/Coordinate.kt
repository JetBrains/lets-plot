package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.projectionGeometry.Typed


interface LonLat
interface World
interface Client


typealias LonLatPoint = Typed.Coordinate<LonLat>
typealias WorldPoint = Typed.Coordinate<World>
typealias ClientPoint = Typed.Coordinate<Client>

class Coordinates {
    companion object {
        val ZERO_LONLAT_POINT = LonLatPoint(0.0, 0.0)
        val ZERO_WORLD_POINT = WorldPoint(0.0, 0.0)
        val ZERO_CLIENT_POINT = ClientPoint(0.0, 0.0)
    }

}

fun newDoubleRectangle(origin: Typed.Coordinate<*>, dimension: Typed.Coordinate<*>): DoubleRectangle {
    return DoubleRectangle(origin.x, origin.y, dimension.x, dimension.y)
}

fun Vector.toClientPoint() = ClientPoint(x, y)
fun DoubleVector.toLonLatPoint() = LonLatPoint(x, y)
fun DoubleVector.toWorldPoint() = WorldPoint(x, y)
fun DoubleVector.toClientPoint() = ClientPoint(x, y)
