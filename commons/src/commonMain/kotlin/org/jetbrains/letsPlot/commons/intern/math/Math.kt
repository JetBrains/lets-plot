/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.math

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import kotlin.math.*

fun toRadians(degrees: Double): Double = degrees * PI / 180.0
fun toDegrees(radians: Double): Double = radians * 180.0 / PI

fun round(v: DoubleVector) = round(v.x, v.y)
fun ceil(v: DoubleVector) = ceil(v.x, v.y)

fun round(x: Double, y: Double): Vector {
    return Vector(
        round(x).toInt(),
        round(y).toInt()
    )
}


fun ceil(x: Double, y: Double): Vector {
    return Vector(
        ceil(x).toInt(),
        ceil(y).toInt()
    )
}


fun distance(vector: Vector, doubleVector: DoubleVector): Double {
    val dx = doubleVector.x - vector.x
    val dy = doubleVector.y - vector.y
    return sqrt(dx * dx + dy * dy)
}


fun Int.ipow(e: Int): Double {
    return this.toDouble().pow(e)
}

fun areEqual(a: Double, b: Double, epsilon: Double = 0.00001) = abs(a - b) < epsilon

fun pointToLineSqrDistance(x: Double, y: Double, l1x: Double, l1y: Double, l2x: Double, l2y: Double): Double {
    return if (l1x == l2x && l1y == l2y) {
        pointSqrDistance(x, y, l1x, l1y)
    } else {
        val ortX = l2x - l1x
        val ortY = -(l2y - l1y)

        val dot = (x - l1x) * ortY + (y - l1y) * ortX
        val len = ortY * ortY + ortX * ortX

        dot * dot / len
    }
}

fun pointSqrDistance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    val dx = x1 - x2
    val dy = y1 - y2
    return dx * dx + dy * dy
}

fun yOnLine(p1x: Double, p1y: Double, p2x: Double, p2y: Double, x: Double): Double {
    // the Equation for the Line
    // y = m * x + b
    // Where
    // m = (y2 - y1) / (x2 - x1)
    // and b computed by substitution p1 or p2 to the equation of the line

    val m = (p2y - p1y) / ((p2x) - p1x)
    val b = p2y - m * (p2x)

    // Result
    return m * x + b
}