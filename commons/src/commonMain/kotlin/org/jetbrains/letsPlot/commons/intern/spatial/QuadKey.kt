/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.spatial

import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*


data class QuadKey<T>(
    val key: String
) {
    operator fun plus(other: QuadKey<T>): QuadKey<T> {
        return QuadKey(key + other.key)
    }

    val length = key.length
}

fun QuadKey<LonLat>.computeRect(): Rect<LonLat> {
    val origin = this.computeOrigin(EARTH_RECT)
    val dimension = EARTH_RECT.dimension / calulateQuadsCount(length).toDouble()

    val flippedY = EARTH_RECT.scalarBottom - (origin.scalarY + dimension.scalarY - EARTH_RECT.scalarTop)
    return Rect.XYWH(origin.transform(newY = { flippedY }), dimension)
}

fun <T> QuadKey<T>.computeRect(rect: Rect<T>): Rect<T> {
    return projectRect(rect)
}

fun <T, OutT> QuadKey<T>.projectRect(rect: Rect<OutT>): Rect<OutT> = Rect.XYWH(
    origin = projectOrigin(rect),
    dimension = rect.dimension / calulateQuadsCount(length).toDouble()
)

fun QuadKey<LonLat>.zoom() = length

fun <TypeT> QuadKey<TypeT>.computeOrigin(mapRect: Rect<TypeT>): Vec<TypeT> {
    return projectOrigin(mapRect)
}

fun <TypeT, OutT> QuadKey<TypeT>.projectOrigin(mapRect: Rect<OutT>): Vec<OutT> {
    var left = mapRect.scalarLeft
    var top = mapRect.scalarTop
    var width = mapRect.scalarWidth
    var height = mapRect.scalarHeight

    for (quadrant in key) {
        width /= 2.0
        height /= 2.0

        if (quadrant == '1' || quadrant == '3') {
            left += width
        }
        if (quadrant == '2' || quadrant == '3') {
            top += height
        }
    }
    return newVec(left, top)
}
