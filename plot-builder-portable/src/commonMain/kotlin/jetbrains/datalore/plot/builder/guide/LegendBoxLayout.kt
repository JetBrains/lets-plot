/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.builder.layout.PlotLayoutUtil.textDimensions
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec

abstract class LegendBoxLayout(
    private val title: String,
    legendDirection: LegendDirection
) {
    // legend keys/colorbar + labels.
    abstract val graphSize: DoubleVector

    val isHorizontal = legendDirection === LegendDirection.HORIZONTAL
    val titleHorizontalAnchor = Text.HorizontalAnchor.LEFT

    val titleBounds: DoubleRectangle
        get() {
            var origin = DoubleVector.ZERO
            if (isHorizontal) {
                val titleHeight = titleSize(title).y
                val y = if (graphSize.y > titleHeight) (graphSize.y - titleHeight) / 2 else origin.y
                origin = DoubleVector(origin.x, y)
            }
            return DoubleRectangle(origin, titleSize(title))
        }

    val graphOrigin: DoubleVector
        get() = when {
            isHorizontal -> {
                val titleSize = titleSize(title)
                val y = if (titleSize.y > graphSize.y) (titleSize.y - graphSize.y) / 2 else 0.0
                DoubleVector(titleSize.x, y)
            }
            else -> {
                // make some space between title and the rest of the content.
                val y = titleSize(title).y + TITLE_SPEC.height() / 2
                DoubleVector(0.0, y)
            }
        }

    val size: DoubleVector
        get() {
            val graphBounds = DoubleRectangle(graphOrigin, graphSize)
            val titleAndContent = DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
                .union(titleBounds)
                .union(graphBounds)
            return titleAndContent.dimension
        }

    companion object {
        private val TITLE_SPEC = PlotLabelSpec.LEGEND_TITLE
        internal val LABEL_SPEC = PlotLabelSpec.LEGEND_ITEM

        private fun titleSize(s: String): DoubleVector {
            return when {
                s.isBlank() -> DoubleVector.ZERO
                else -> textDimensions(s.split('\n'), TITLE_SPEC)
            }
        }
    }
}
