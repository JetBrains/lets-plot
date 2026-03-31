/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleInsets
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme

internal object PlotLegendsLayoutUtil {

    internal fun legendsSpaceLeftTopDelta(
        legendBlocks: List<LegendsBlockInfo>,
        theme: LegendTheme
    ): DoubleVector {
        val insets = computeLegendsSpace(legendBlocks, theme)
        return insets.leftTop
    }

    internal fun legendsSpaceTotalDelta(
        legendBlocks: List<LegendsBlockInfo>,
        theme: LegendTheme
    ): DoubleVector {
        val insets = computeLegendsSpace(legendBlocks, theme)
        return DoubleVector(
            x = insets.left + insets.right,
            y = insets.top + insets.bottom
        )
    }

    internal fun subtractLegendsSpace(
        bounds: DoubleRectangle,
        legendBlocks: List<LegendsBlockInfo>,
        theme: LegendTheme,
    ): DoubleRectangle {
        val insets = computeLegendsSpace(legendBlocks, theme)
        return insets.subtractFrom(bounds)
    }

    private fun computeLegendsSpace(
        legendBlocks: List<LegendsBlockInfo>,
        theme: LegendTheme,
    ): DoubleInsets {
        // Only process fixed-position legends
        val fixedPositionBlocks = legendBlocks.filter { it.position.isFixed }
        if (fixedPositionBlocks.isEmpty()) {
            return DoubleInsets.ZERO
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

        return DoubleInsets(
            left = leftSpace,
            top = topSpace,
            right = rightSpace,
            bottom = bottomSpace
        )
    }
}