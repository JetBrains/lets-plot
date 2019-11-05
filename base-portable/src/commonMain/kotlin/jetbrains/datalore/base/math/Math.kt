/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.math

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.geometry.Vector
import kotlin.math.floor
import kotlin.math.sqrt

fun round(x: Double, y: Double): Vector {
    return Vector(kotlin.math.round(x).toInt(), kotlin.math.round(y).toInt())
}

fun round(v: DoubleVector): Vector {
    return round(v.x, v.y)
}

fun ceil(x: Double, y: Double): Vector {
    return Vector(
        kotlin.math.ceil(x).toInt(),
        kotlin.math.ceil(y).toInt()
    )
}

fun ceil(v: DoubleVector): Vector {
    return ceil(v.x, v.y)
}

fun containingRectangle(rect: DoubleRectangle): Rectangle {
    return containingRectangle(rect.origin, rect.dimension)
}

fun containingRectangle(origin: DoubleVector, dimension: DoubleVector): Rectangle {
    val left = floor(origin.x).toInt()
    val top = floor(origin.y).toInt()
    val right = kotlin.math.ceil(origin.x + dimension.x).toInt()
    val bottom = kotlin.math.ceil(origin.y + dimension.y).toInt()
    return Rectangle(left, top, right - left, bottom - top)
}

fun distance(vector: Vector, doubleVector: DoubleVector): Double {
    val dx = doubleVector.x - vector.x
    val dy = doubleVector.y - vector.y
    return sqrt(dx * dx + dy * dy)
}

