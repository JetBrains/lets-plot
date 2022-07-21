/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

class TextJustification(val x: Double, val y: Double) {
    companion object {
        fun MultilineLabel.applyJustification(
            boundRect: DoubleRectangle,
            textSize: DoubleVector,
            lineHeight: Double,
            justification: TextJustification,
        ) {
            val (x, hAnchor) = xPosition(boundRect, textSize, justification.x)
            val y = yPosition(boundRect, textSize, lineHeight, justification.y)

            val position = DoubleVector(x, y)
            setLineHeight(lineHeight)
            setHorizontalAnchor(hAnchor)
            moveTo(position)
        }

        private fun xPosition(
            boundRect: DoubleRectangle,
            textSize: DoubleVector,
            hjust: Double,
        ): Pair<Double, Text.HorizontalAnchor> {
            val textWidth = 0.0  // todo textWidth = textSize.x
            val x = boundRect.left + (boundRect.width - textWidth) * hjust
            // todo: anchor = LEFT
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
            return y - textSize.y + lineHeight
        }
    }
}