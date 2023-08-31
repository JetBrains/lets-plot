/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.donut

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.livemap.chart.PieSpecComponent
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

internal class Sector(
    val index: Int,
    val radius: Double,
    val holeRadius: Double,
    val startAngle: Double,
    val endAngle: Double,
    val fillColor: Color?,
    val strokeColor: Color?,
    val strokeWidth: Double,
    val drawInnerArc: Boolean,
    val drawOuterArc: Boolean,
    val spacerColor: Color?,
    val spacerWidth: Double,
    val drawSpacerAtStart: Boolean,
    val drawSpacerAtEnd: Boolean,
    explode: Double
) {
    private val angle = endAngle - startAngle
    private val direction = startAngle + angle / 2

    val sectorCenter = DoubleVector(explode * cos(direction), explode * sin(direction))

    val radiusWithStroke = when (drawOuterArc) {
        true -> radius + strokeWidth / 2
        false -> radius
    }
    val holeRadiusWithStroke = when (drawInnerArc) {
        true -> holeRadius - strokeWidth / 2
        false -> holeRadius
    }

    val outerArcStart = outerArcPoint(startAngle)
    val outerArcEnd = outerArcPoint(endAngle)

    val innerArcStart = innerArcPoint(startAngle)
    val innerArcEnd = innerArcPoint(endAngle)

    private fun outerArcPoint(angle: Double) = arcPoint(radiusWithStroke, angle)
    private fun innerArcPoint(angle: Double) = arcPoint(holeRadiusWithStroke, angle)

    private fun arcPoint(radius: Double, angle: Double): DoubleVector {
        return sectorCenter.add(DoubleVector(radius * cos(angle), radius * sin(angle)))
    }
}

 enum class StrokeSide {
    OUTER, INNER, BOTH;

     val hasOuter: Boolean
         get() = this == OUTER || this == BOTH

     val hasInner: Boolean
         get() = this == INNER || this == BOTH
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

    val pieIndices = pieSpec.sliceValues.indices
    val explodedSectors = pieIndices.mapNotNull { index ->
        val explode = pieSpec.explodeValues?.get(index)
        index.takeIf { explode != null && explode != 0.0  }
    }
    fun needAddAtStart(index: Int) = when (index) {
        in explodedSectors -> false
        0 -> pieIndices.last !in explodedSectors
        else -> index - 1 !in explodedSectors
    }
    fun needAddAtEnd(index: Int) = when (index) {
        in explodedSectors -> false
        pieIndices.last -> 0 !in explodedSectors
        else -> index + 1 !in explodedSectors
    }

    val hasInnerArc = pieSpec.strokeSide?.hasInner ?: false
    val hasOuterArc = pieSpec.strokeSide?.hasOuter ?: false

    return pieIndices.map { index ->
        val strokeColor = pieSpec.strokeColors.getOrNull(index)
        val fillColor = pieSpec.fillColors.getOrNull(index)
        val strokeWidth = pieSpec.strokeWidths.getOrElse(index) { 0.0 }

        val hasVisibleStroke = strokeWidth > 0.0 && strokeColor != null && strokeColor != Color.TRANSPARENT
        val holeRadius = radius * pieSpec.holeSize

        Sector(
            index = pieSpec.indices[index],
            radius = radius,
            holeRadius =  holeRadius,
            startAngle = currentAngle,
            endAngle = currentAngle + angle(pieSpec.sliceValues[index]),
            explode = pieSpec.explodeValues?.get(index)?.let { radius * it } ?: 0.0,
            fillColor = fillColor,
            strokeColor = strokeColor,
            strokeWidth = strokeWidth,
            drawInnerArc = hasInnerArc && hasVisibleStroke && holeRadius > 0,
            drawOuterArc = hasOuterArc && hasVisibleStroke,
            spacerColor = pieSpec.spacerColor,
            spacerWidth = pieSpec.spacerWidth,
            drawSpacerAtStart = needAddAtStart(index),
            drawSpacerAtEnd = needAddAtEnd(index)
        ).also { sector -> currentAngle = sector.endAngle }
    }
}
