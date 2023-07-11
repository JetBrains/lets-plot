/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart.donut

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.livemap.chart.PieSpecComponent
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

internal class Sector(
    val index: Int,
    val radius: Double,
    val holeRadius: Double,
    val fillColor: Color,
    val startAngle: Double,
    val endAngle: Double,
    explode: Double
) {
    private val angle = endAngle - startAngle
    private val direction = startAngle + angle / 2

    val sectorCenter = DoubleVector(explode * cos(direction), explode * sin(direction))

    val outerArcStart = outerArcPoint(startAngle)
    val outerArcEnd = outerArcPoint(endAngle)

    val innerArcStart = innerArcPoint(startAngle)
    val innerArcEnd = innerArcPoint(endAngle)

    fun outerArcPoint(angle: Double) = arcPoint(radius, angle)
    fun innerArcPoint(angle: Double) = arcPoint(holeRadius, angle)

    private fun arcPoint(radius: Double, angle: Double): DoubleVector {
        return sectorCenter.add(DoubleVector(radius * cos(angle), radius * sin(angle)))
    }
}

internal fun computeSectors(pieSpec: PieSpecComponent, scaleFactor: Double): List<Sector> {
    val sum = pieSpec.sliceValues.sum()
    fun angle(slice: Double) = when (sum) {
        0.0 -> 1.0 / pieSpec.sliceValues.size
        else -> abs(slice) / sum
    }.let { PI * 2.0 * it }

    // the first slice goes to the left of 12 o'clock and others go clockwise
    var currentAngle = -PI / 2.0
    currentAngle -= angle(pieSpec.sliceValues.first())

    val radius = pieSpec.radius * scaleFactor
    return pieSpec.sliceValues.indices.map { index ->
        Sector(
            index = pieSpec.indices[index],
            radius = radius,
            holeRadius = radius * pieSpec.holeSize,
            fillColor = pieSpec.colors[index],
            startAngle = currentAngle,
            endAngle = currentAngle + angle(pieSpec.sliceValues[index]),
            explode = pieSpec.explodeValues?.get(index)?.let { radius * it } ?: 0.0,
        ).also { sector -> currentAngle = sector.endAngle }
    }
}
