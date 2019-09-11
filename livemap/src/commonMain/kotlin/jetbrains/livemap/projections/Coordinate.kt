package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector

data class Coordinate<T>(val x: Double, val y: Double) {
    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())

    fun add(p: Coordinate<T>) = Coordinate<T>(x + p.x, y + p.y)
    fun subtract(p: Coordinate<T>) = Coordinate<T>(x - p.x, y - p.y)
    fun mul(d: Double) = Coordinate<T>(x * d, y * d)

    companion object {}
}


interface LonLat
interface World
interface Client


typealias LonLatPoint = Coordinate<LonLat>
typealias WorldPoint = Coordinate<World>
typealias ClientPoint = Coordinate<Client>

class Coordinates {
    companion object {
        val ZERO_LONLAT_POINT = LonLatPoint(0.0, 0.0)
        val ZERO_WORLD_POINT = WorldPoint(0.0, 0.0)
        val ZERO_CLIENT_POINT = ClientPoint(0.0, 0.0)
    }

}

fun <T> newDoubleRectangle(origin: Coordinate<T>, dimension: Coordinate<T>): DoubleRectangle {
    return DoubleRectangle(origin.x, origin.y, dimension.x, dimension.y)
}

fun Vector.toClientPoint() = ClientPoint(x, y)
fun DoubleVector.toLonLatPoint() = LonLatPoint(x, y)
fun DoubleVector.toWorldPoint() = WorldPoint(x, y)
fun DoubleVector.toClientPoint() = ClientPoint(x, y)
