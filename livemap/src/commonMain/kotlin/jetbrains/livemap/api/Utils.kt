/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.livemap.projections.World
import jetbrains.livemap.projections.WorldPoint
import jetbrains.livemap.projections.WorldRectangle
import kotlin.math.PI
import kotlin.math.abs

private const val ONE_HUNDRED_PERCENTS = 1.0
private const val MIN_PERCENT = 0.05

fun transformValues2Percents(values: List<Double>, maxAbsValue: Double): List<Double> {
    return values.map { calculatePercent(it, maxAbsValue) }
}

fun transformValues2Angles(values: List<Double>): List<Double> {
    val sum = values.map { abs(it) }.sum()

    return if (sum == 0.0) {
        MutableList(values.size) { 2 * PI / values.size }
    } else {
        values.map { 2 * PI * abs(it) / sum }
    }
}

private fun calculatePercent(value: Double, maxAbsValue: Double): Double {
    val percent = if (maxAbsValue == 0.0) 0.0 else ONE_HUNDRED_PERCENTS * value / maxAbsValue

    if (abs(percent) >= MIN_PERCENT) {
        return percent
    }
    return if (percent >= 0) MIN_PERCENT else -MIN_PERCENT
}

fun createLineGeometry(point: WorldPoint, horizontal: Boolean, mapRect: WorldRectangle): MultiPolygon<World> {
    return if (horizontal) {
        listOf(
            point.transform(
                newX = { mapRect.scalarLeft }
            ),
            point.transform(
                newX = { mapRect.scalarRight }
            )

        )
    } else {
        listOf(
            point.transform(
                newY = { mapRect.scalarTop }
            ),
            point.transform(
                newY = { mapRect.scalarBottom }
            )
        )
    }
        .run { listOf(Ring(this)) }
        .run { listOf(Polygon(this)) }
        .run { MultiPolygon(this) }
}

fun createLineBBox(
    point: WorldPoint,
    strokeWidth: Double,
    horizontal: Boolean,
    mapRect: WorldRectangle
): WorldRectangle {
    return if (horizontal) {
        WorldRectangle(
            explicitVec(mapRect.left, point.y - strokeWidth / 2),
            explicitVec(mapRect.width, strokeWidth)
        )
    } else {
        WorldRectangle(
            explicitVec(point.x - strokeWidth / 2, mapRect.top),
            explicitVec(strokeWidth, mapRect.height)
        )
    }
}