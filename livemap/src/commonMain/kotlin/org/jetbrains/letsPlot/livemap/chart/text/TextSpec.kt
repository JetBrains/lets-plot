/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.text

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.explicitVec
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight
import org.jetbrains.letsPlot.core.canvas.TextAlign
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.core.graphics.TextMeasurer
import kotlin.math.abs
import kotlin.math.max

class TextSpec(
    label: String,
    fontStyle: FontStyle,
    fontWeight: FontWeight,
    size: Int,
    family: String,
    degreeAngle: Double,
    val hjust: Double,
    val vjust: Double,
    textMeasurer: TextMeasurer,
    val drawBorder: Boolean,
    // label parameters
    labelPadding: Double,
    val labelRadius: Double,
    val labelSize: Double,
    lineheight: Double
) {
    val lines = label.split('\n').map(String::trim)
    val font = Font(
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontSize = size.toDouble(),
        fontFamily = family
    )
    val dimension: Vec<Client>
    val angle: Double = toRadians(-degreeAngle)
    val lineHeight = lineheight * size
    val textSize = textMeasurer.measure(lines, font, lineHeight)
    val textAlign = when (hjust) {
        0.0 -> TextAlign.START
        1.0 -> TextAlign.END
        else -> TextAlign.CENTER
    }
    val padding = font.fontSize * labelPadding

    init {
        dimension = rotateTextSize(textSize.mul(2.0), angle)
    }

    private fun rotateTextSize(textSize: DoubleVector, angle: Double): Vec<Client> {
        val p1 = DoubleVector(textSize.x / 2, +textSize.y / 2).rotate(angle)
        val p2 = DoubleVector(textSize.x / 2, -textSize.y / 2).rotate(angle)

        val maxX = max(abs(p1.x), abs(p2.x))
        val maxY = max(abs(p1.y), abs(p2.y))
        return explicitVec(maxX * 2, maxY * 2)
    }
}