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


fun Int.ipow(e: Int): Double {
    return this.toDouble().pow(e)
}

fun areEqual(a: Double, b: Double, epsilon: Double = 0.00001) = abs(a - b) < epsilon


fun length(x: Double, y: Double): Double {
    return sqrt(length2(x, y))
}

fun length2(x: Double, y: Double): Double {
    return x * x + y * y
}

fun dot(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    return x1 * x2 + y1 * y2
}

fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    return sqrt(distance2(x1, y1, x2, y2))
}

fun distance2(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    val dx = x1 - x2
    val dy = y1 - y2
    return dx * dx + dy * dy
}

fun isOnSegment(x: Double, y: Double, p1x: Double, p1y: Double, p2x: Double, p2y: Double): Boolean {
    val v21x = p2x - p1x
    val v21y = p2y - p1y

    val v1x = x - p1x
    val v1y = y - p1y

    val t = dot(v1x, v1y, v21x, v21y) / length2(v21x, v21y)

    return t in 0.0..1.0
}

fun distance2ToLine(x: Double, y: Double, l1x: Double, l1y: Double, l2x: Double, l2y: Double): Double {
    if (l1x == l2x && l1y == l2y) {
        return distance2(x, y, l1x, l1y)
    }

    val vX = l2x - l1x
    val vY = l2y - l1y

    val vOrtX = -vY
    val vOrtY = vX

    val hx = x - l1x
    val hy = y - l1y
    val dot = dot(hx, hy, vOrtX, vOrtY)
    val len = length2(vOrtX, vOrtY)

    return dot * dot / len
}

fun distance2ToSegment(x: Double, y: Double, l1x: Double, l1y: Double, l2x: Double, l2y: Double): Double {
    if (l1x == l2x && l1y == l2y) {
        return distance2(x, y, l1x, l1y)
    }

    if (isOnSegment(x, y, l1x, l1y, l2x, l2y)) {
        return distance2ToLine(x, y, l1x, l1y, l2x, l2y)
    }

    return min(distance2(x, y, l1x, l1y), distance2(x, y, l2x, l2y))
}

// Pair of line parameters: m and b

/**
 * the Equation for the Line
 * y = m * x + b
 * Where
 * m = (y2 - y1) / (x2 - x1)
 * and b computed by substitution p1 or p2 to the equation of the line
 * Return Infinite if the line is vertical
 * */
fun lineParams(p1x: Double, p1y: Double, p2x: Double, p2y: Double): Pair<Double, Double> {
    val m = (p2y - p1y) / (p2x - p1x)
    val b = p2y - m * (p2x)
    return m to b
}

fun yOnLine(p1x: Double, p1y: Double, p2x: Double, p2y: Double, x: Double): Double? {
    if (p2x == p1x) {
        return null
    }
    val (m, b) = lineParams(p1x, p1y, p2x, p2y)
    return m * x + b
}

fun xOnLine(p1x: Double, p1y: Double, p2x: Double, p2y: Double, y: Double): Double? {
    if (p1x == p2x && p1y == p2y) {
        return null
    }

    if (p1x == p2x) {
        return p1x
    }

    val (m, b) = lineParams(p1x, p1y, p2x, p2y)
    return (y - b) / m
}

/*
 offset is the distance from p1.
 Positive value offsets the point in the direction from p1 to p2.
 Negative value offsets the point in the opposite direction.
 */
fun pointOnLine(p1x: Double, p1y: Double, p2x: Double, p2y: Double, offset: Double): DoubleVector {
    val d = distance(p2x, p2y, p1x, p1y)
    if (d == 0.0) {
        return DoubleVector(p1x, p1y)
    }
    val k = offset / d
    val x = p1x + (p2x - p1x) * k
    val y = p1y + (p2y - p1y) * k

    return DoubleVector(x, y)
}

// vector projection
// See: https://en.wikipedia.org/wiki/Vector_projection
fun projection(px: Double, py: Double, l1x: Double, l1y: Double, l2x: Double, l2y: Double): DoubleVector {
    val vx = px - l1x
    val vy = py - l1y

    val sx = l2x - l1x
    val sy = l2y - l1y

    val c = dot(vx, vy, sx, sy) / dot(sx, sy, sx, sy)

    return DoubleVector(sx * c + l1x, sy * c + l1y)
}

fun distance2ToSegment(p: DoubleVector, l1: DoubleVector, l2: DoubleVector): Double {
    return distance2ToSegment(p.x, p.y, l1.x, l1.y, l2.x, l2.y)
}

fun isOnSegment(p: DoubleVector, l1: DoubleVector, l2: DoubleVector): Boolean {
    return isOnSegment(p.x, p.y, l1.x, l1.y, l2.x, l2.y)
}

fun projection(p: DoubleVector, l1: DoubleVector, l2: DoubleVector): DoubleVector {
    return projection(p.x, p.y, l1.x, l1.y, l2.x, l2.y)
}

fun distance2(p1: DoubleVector, p2: DoubleVector): Double {
    return distance2(p1.x, p1.y, p2.x, p2.y)
}

fun distance(p1: DoubleVector, p2: DoubleVector): Double {
    return distance(p1.x, p1.y, p2.x, p2.y)
}

fun lineSlope(p1: DoubleVector, p2: DoubleVector): Double {
    return lineParams(p1.x, p1.y, p2.x, p2.y).first
}

/*
 offset is the distance from p1.
 Positive value offsets the point in the direction from p1 to p2.
 Negative value offsets the point in the opposite direction.
 */
fun pointOnLine(p1: DoubleVector, p2: DoubleVector, offset: Double): DoubleVector {
    return pointOnLine(p1.x, p1.y, p2.x, p2.y, offset)
}

/*
 * Rotate point (px, py) around center (cx, cy) by angle phi.
 */
fun rotateAround(px: Double, py: Double, cx: Double, cy: Double, phi: Double): DoubleVector {
    val vx = px - cx
    val vy = py - cy

    val x = vx * cos(phi) - vy * sin(phi)
    val y = vx * sin(phi) + vy * cos(phi)

    return DoubleVector(x + cx, y + cy)
}