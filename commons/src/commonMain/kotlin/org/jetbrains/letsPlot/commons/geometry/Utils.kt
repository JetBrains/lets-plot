package org.jetbrains.letsPlot.commons.geometry

import org.jetbrains.letsPlot.commons.intern.math.round
import kotlin.math.ceil
import kotlin.math.floor

fun round(v: DoubleVector) = round(v.x, v.y)

fun DoubleVector.ceilToVector(): Vector {
    return Vector(
        ceil(this.x).toInt(),
        ceil(this.y).toInt()
    )
}

fun DoubleVector.floorToVector(): Vector {
    return Vector(
        floor(this.x).toInt(),
        floor(this.y).toInt()
    )
}

fun DoubleVector.subtract(other: Vector): DoubleVector {
    return DoubleVector(this.x - other.x, this.y - other.y)
}

fun DoubleVector.add(other: Vector): DoubleVector {
    return DoubleVector(this.x + other.x, this.y + other.y)
}

fun DoubleVector.add(x: Number, y: Number): DoubleVector {
    return DoubleVector(this.x + x.toDouble(), this.y + y.toDouble())
}

fun Vector.subtract(other: DoubleVector): DoubleVector {
    return DoubleVector(this.x - other.x, this.y - other.y)
}

fun Vector.mul(d: Double): DoubleVector {
    return DoubleVector(x * d, y * d)
}