/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.builder.layout.PlotLayoutUtil.textDimensions
import jetbrains.datalore.plot.builder.presentation.Defaults
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.vis.StyleSheet

abstract class LegendBoxLayout(
    private val title: String,
    legendDirection: LegendDirection,
    protected val styleSheet: StyleSheet
) {
    // legend keys/colorbar + labels.
    abstract val graphSize: DoubleVector

    val isHorizontal = legendDirection === LegendDirection.HORIZONTAL
    val titleHorizontalAnchor = Text.HorizontalAnchor.LEFT

    private val myTitleLabelSpec: PlotLabelSpec
        get() = PlotLabelSpec.legendTitle(styleSheet)

    val titleBounds: DoubleRectangle
        get() {
            var origin = DoubleVector.ZERO
            if (isHorizontal) {
                val titleHeight = titleSize(title, myTitleLabelSpec).y
                val y = if (graphSize.y > titleHeight) (graphSize.y - titleHeight) / 2 else origin.y
                origin = DoubleVector(origin.x, y)
            }
            return DoubleRectangle(origin, titleSize(title, myTitleLabelSpec))
        }

    val graphOrigin: DoubleVector
        get() = when {
            isHorizontal -> {
                val titleSize = titleSize(title, myTitleLabelSpec)
                val y = if (titleSize.y > graphSize.y) (titleSize.y - graphSize.y) / 2 else 0.0
                DoubleVector(titleSize.x, y)
            }
            else -> {
                // make some space between title and the rest of the content.
                val y = titleSize(title, myTitleLabelSpec).y + LEGEND_TITLE_HEIGHT / 2
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
        private const val LEGEND_TITLE_HEIGHT = Defaults.Common.Legend.TITLE_FONT_SIZE.toDouble()
        internal const val LEGEND_ITEM_HEIGHT = Defaults.Common.Legend.ITEM_FONT_SIZE.toDouble()

        private fun titleSize(s: String, titleLabelSpec: PlotLabelSpec): DoubleVector {
            return when {
                s.isBlank() -> DoubleVector.ZERO
                else -> textDimensions(s.split('\n'), titleLabelSpec)
            }
        }
    }
}
