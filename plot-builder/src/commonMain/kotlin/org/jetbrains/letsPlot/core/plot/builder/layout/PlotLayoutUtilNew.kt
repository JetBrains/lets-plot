/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangles
import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme

object PlotLayoutUtilNew {
    fun subtractLegendsSpace(
        bounds: DoubleRectangle,
        legendBlocks: List<LegendsBlockInfo>,
        theme: LegendTheme,
    ): DoubleRectangle {
        // Only process fixed-position legends
        val fixedPositionBlocks = legendBlocks.filter { it.position.isFixed }

        if (fixedPositionBlocks.isEmpty()) {
            return bounds
        }

        val spacing = theme.boxSpacing()

        var leftSpace = 0.0
        var rightSpace = 0.0
        var topSpace = 0.0
        var bottomSpace = 0.0

        // Multiple blocks at the same position have different justifications,
        // so they can be placed side-by-side - we take the max, not sum
        for (legendBlock in fixedPositionBlocks) {
            val size = legendBlock.size()
            when (legendBlock.position) {
                LegendPosition.LEFT -> {
                    leftSpace = maxOf(leftSpace, size.x + spacing)
                }

                LegendPosition.RIGHT -> {
                    rightSpace = maxOf(rightSpace, size.x + spacing)
                }

                LegendPosition.TOP -> {
                    topSpace = maxOf(topSpace, size.y + spacing)
                }

                LegendPosition.BOTTOM -> {
                    bottomSpace = maxOf(bottomSpace, size.y + spacing)
                }

                else -> {} // Never mind
            }
        }

        var adjustedBounds = bounds
        if (leftSpace > 0) {
            adjustedBounds = DoubleRectangles.extendLeft(adjustedBounds, -leftSpace)
        }
        if (rightSpace > 0) {
            adjustedBounds = DoubleRectangles.extendRight(adjustedBounds, -rightSpace)
        }
        if (topSpace > 0) {
            adjustedBounds = DoubleRectangles.extendUp(adjustedBounds, -topSpace)
        }
        if (bottomSpace > 0) {
            adjustedBounds = DoubleRectangles.extendDown(adjustedBounds, -bottomSpace)
        }

        return adjustedBounds
    }
}