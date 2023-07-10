/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.Text

class TextJustification(val x: Double, val y: Double) {

    companion object {
        enum class TextRotation(val angle: Double) {
            CLOCKWISE(90.0),
            ANTICLOCKWISE(-90.0);
        }

        fun applyJustification(
            boundRect: DoubleRectangle,
            textSize: DoubleVector,
            lineHeight: Double,
            justification: TextJustification,
            rotation: TextRotation? = null
        ): Pair<DoubleVector, Text.HorizontalAnchor> {
            val rect = if (rotation != null) boundRect.flip() else boundRect

            val (x, hAnchor) = xPosition(rect, justification.x)
            val y = yPosition(rect, textSize, lineHeight, justification.y)

            val position = when (rotation) {
                null -> DoubleVector(x, y)
                TextRotation.CLOCKWISE -> DoubleVector(rect.top + rect.bottom - y, x)
                TextRotation.ANTICLOCKWISE -> DoubleVector(y, rect.left + rect.right - x)
            }
            return position to hAnchor
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