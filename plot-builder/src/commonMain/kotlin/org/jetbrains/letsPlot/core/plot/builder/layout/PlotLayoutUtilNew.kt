/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangles
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Rectangles
import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme

object PlotLayoutUtilNew {
    fun subtractLegendsSpace(
        bounds: DoubleRectangle,
        legendBlocks: List<CompositeLegendBlockInfo>,
        theme: LegendTheme,
    ): DoubleRectangle {
        var adjustedBounds = bounds
        for (legendBlock in legendBlocks) {
            adjustedBounds = subtractLegendSpace(adjustedBounds, legendBlock, theme)
        }
        return adjustedBounds
    }

    /**
     * Calculate space needed for collected legends and subtract from bounds.
     * Similar to legendBlockDelta in PlotLayoutUtil.kt:247
     */
    private fun subtractLegendSpace(
        bounds: DoubleRectangle,
        legendBlock: CompositeLegendBlockInfo,
        theme: LegendTheme,
    ): DoubleRectangle {
        val legendsInfo = legendBlock.legendsBlockInfo
        val position = legendBlock.position

        val size = legendsInfo.size()
        val spacing = theme.boxSpacing()

        return when (position) {
            LegendPosition.LEFT -> {
                val delta = size.x + spacing
                DoubleRectangles.extendLeft(bounds, -delta)
            }

            LegendPosition.RIGHT -> {
                val delta = size.x + spacing
                DoubleRectangles.extendRight(bounds, -delta)
            }

            LegendPosition.TOP -> {
                val delta = size.y + spacing
                DoubleRectangles.extendUp(bounds, -delta)
            }

            LegendPosition.BOTTOM -> {
                val delta = size.y + spacing
                DoubleRectangles.extendDown(bounds, -delta)
            }

            else -> bounds // Overlay or hidden positions don't affect bounds
        }
    }

}