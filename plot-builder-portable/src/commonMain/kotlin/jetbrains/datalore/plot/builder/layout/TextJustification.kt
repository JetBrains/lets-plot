/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.Text

class TextJustification(val x: Double, val y: Double) {
    companion object {
        fun applyJustification(
            boundRect: DoubleRectangle,
            textSize: DoubleVector,
            lineHeight: Double,
            justification: TextJustification,
            angle: Double = 0.0,
        ): Pair<DoubleVector, Text.HorizontalAnchor> {
            require(angle in listOf(0.0, 90.0, -90.0))

            val rect = if (angle != 0.0) boundRect.flip() else boundRect

            val (x, hAnchor) = xPosition(rect, textSize, justification.x)
            val y = yPosition(rect, textSize, lineHeight, justification.y)

            val position = when {
                angle == 0.0 -> DoubleVector(x, y)
                angle < 0.0 -> DoubleVector(y, rect.left + rect.right - x)
                else -> DoubleVector(rect.top + rect.bottom - y, x)
            }
            return position to hAnchor
        }

        private fun xPosition(
            boundRect: DoubleRectangle,
            textSize: DoubleVector,
            hjust: Double,
        ): Pair<Double, Text.HorizontalAnchor> {
            val textWidth = 0.0  // todo val textWidth = textSize.x
            val x = boundRect.left + (boundRect.width - textWidth) * hjust
            // todo: val anchor = Text.HorizontalAnchor.LEFT
            val anchor = when {
                hjust < 0.5 -> Text.HorizontalAnchor.LEFT
                hjust == 0.5 -> Text.HorizontalAnchor.MIDDLE
                else -> Text.HorizontalAnchor.RIGHT
            }
            return x to anchor
        }

        private fun yPosition(
            boundRect: DoubleRectangle,
            textSize: DoubleVector,
            lineHeight: Double,
            vjust: Double,
        ): Double {
            val y = boundRect.bottom - (boundRect.height - textSize.y) * vjust
            // use 0.7 for better alignment: like vertical_anchor = 'top' (dy="0.7em")
            return y - textSize.y + lineHeight * 0.7
        }
    }
}