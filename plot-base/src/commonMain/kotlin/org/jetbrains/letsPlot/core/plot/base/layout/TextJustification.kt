/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import kotlin.math.pow

class TextJustification(val x: Double, val y: Double) {

    companion object {
        enum class TextRotation(val angle: Double) {
            CLOCKWISE(90.0),
            ANTICLOCKWISE(-90.0);
        }

        fun applyJustification(
            boundRect: DoubleRectangle,
            fontSize: Double,
            textSize: DoubleVector,
            firstLineHeight: Double,
            justification: TextJustification,
            rotation: TextRotation? = null
        ): Pair<DoubleVector, Text.HorizontalAnchor> {
            val rect = if (rotation != null) boundRect.flip() else boundRect

            val (x, hAnchor) = xPosition(rect, justification.x)
            val y = yPosition(rect, fontSize, textSize, firstLineHeight, justification.y)

            val position = when (rotation) {
                null -> DoubleVector(x, y)
                TextRotation.CLOCKWISE -> DoubleVector(rect.top + rect.bottom - y, x)
                TextRotation.ANTICLOCKWISE -> DoubleVector(y, rect.left + rect.right - x)
            }
            return position to hAnchor
        }

        fun verticalCorrectionFactor(lineHeight: Double, fontSize: Double): (Double) -> Double {
            return { correctionCoefficient ->
                // It's selected by eye to look good with both normal height lines and double height lines (fractions).
                lineHeight * correctionCoefficient.pow(lineHeight / fontSize)
            }
        }

        private fun xPosition(
            boundRect: DoubleRectangle,
            hjust: Double,
        ): Pair<Double, Text.HorizontalAnchor> {
            // todo:
            //  val textWidth = textSize.x
            //  val anchor = Text.HorizontalAnchor.LEFT
            val textWidth = 0.0
            val anchor = when {
                hjust < 0.5 -> Text.HorizontalAnchor.LEFT
                hjust == 0.5 -> Text.HorizontalAnchor.MIDDLE
                else -> Text.HorizontalAnchor.RIGHT
            }
            val x = boundRect.left + (boundRect.width - textWidth) * hjust
            return x to anchor
        }

        private fun yPosition(
            boundRect: DoubleRectangle,
            fontSize: Double,
            textSize: DoubleVector,
            firstLineHeight: Double,
            vjust: Double,
        ): Double {
            val y = boundRect.bottom - (boundRect.height - textSize.y) * vjust
            // use 0.8 for better alignment: like vertical_anchor = 'top' (dy="0.8em")
            val correction = verticalCorrectionFactor(firstLineHeight, fontSize)
            return y - textSize.y + correction(0.8)
        }
    }
}