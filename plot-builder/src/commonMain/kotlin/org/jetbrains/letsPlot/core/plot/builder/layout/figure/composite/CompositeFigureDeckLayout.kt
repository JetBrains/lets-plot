/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.builder.buildinfo.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.buildinfo.PlotFigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.CompositeFigureLayout
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.DEF_PLOT_SIZE

/**
 * Layout for `ggdeck()`: overlays all figures in the same bounds
 * with aligned geometry areas.
 *
 * Unlike a grid layout, all elements share the same rectangular space.
 */
class CompositeFigureDeckLayout(
    val shareX: Boolean,
    val shareY: Boolean,
) : CompositeFigureLayout {

    fun hasSharedAxis(): Boolean = shareX || shareY

    fun indicesWithSharedXAxis(elementCount: Int): List<List<Int>> {
        return sharedGroups(shareX, elementCount)
    }

    fun indicesWithSharedYAxis(elementCount: Int): List<List<Int>> {
        return sharedGroups(shareY, elementCount)
    }

    override fun defaultSize(): DoubleVector = DEF_PLOT_SIZE

    override fun doLayout(bounds: DoubleRectangle, elements: List<FigureBuildInfo?>): List<FigureBuildInfo?> {
        // Initial layout: all elements get the same outer bounds.
        val layouted = elements.map { element ->
            element?.withBounds(bounds)?.layoutedByOuterSize()
        }

        // Compute the common geom content area as the intersection of all plot geom content bounds.
        val plotElements = layouted.filterIsInstance<PlotFigureBuildInfo>()
        val commonGeomContentBounds = plotElements
            .map { it.layoutInfo.geomContentBounds }
            .reduceOrNull { acc, r ->
                DoubleRectangle.hvRange(
                    acc.xRange().intersection(r.xRange()),
                    acc.yRange().intersection(r.yRange()),
                )
            } ?: return layouted


        // Collect axis thickness on each side for non-shared axes.
        val axisMaskByPlot = plotElements.map { plotInfo ->
            val axisInfos = plotInfo.layoutInfo.plotLayoutInfo.tiles[0].axisInfos
            Thickness(
                left = if (shareY || axisInfos.left == null) 0.0 else 1.0,
                right = if (shareY || axisInfos.right == null) 0.0 else 1.0,
                top = if (shareX || axisInfos.top == null) 0.0 else 1.0,
                bottom = if (shareX || axisInfos.bottom == null) 0.0 else 1.0,
            )
        }

        val axisThicknessByPlot = plotElements.mapIndexed { index, plotInfo ->
            val geomContentBounds = plotInfo.layoutInfo.geomContentBounds
            // inflated geom bounds = geom bounds + panel insets + axis + axis titles
            val geomContentBoundsInflated = plotInfo.layoutInfo.figureBoundsWithoutTitleAndCaption
            val layoutInsets = Thickness.diff(from = geomContentBoundsInflated, to = geomContentBounds)
            val axisMask = axisMaskByPlot[index]
            Thickness(
                left = layoutInsets.left * axisMask.left,
                right = layoutInsets.right * axisMask.right,
                top = layoutInsets.top * axisMask.top,
                bottom = layoutInsets.bottom * axisMask.bottom,
            )
        }

        val cumulativeAxisThicknessByPlot = mutableListOf(axisThicknessByPlot[0])
        for (i in 1 until axisThicknessByPlot.size) {
            cumulativeAxisThicknessByPlot.add(
                cumulativeAxisThicknessByPlot[i - 1] + axisThicknessByPlot[i]
            )
        }

        val axisSpacerByPlot = List(cumulativeAxisThicknessByPlot.size) { index ->
            if (index == 0) {
                Thickness.ZERO
            } else {
                val cumulativeAxisThickness = cumulativeAxisThicknessByPlot[index - 1]
                val axisMask = axisMaskByPlot[index]
                Thickness(
                    left = cumulativeAxisThickness.left * axisMask.left,
                    right = cumulativeAxisThickness.right * axisMask.right,
                    top = cumulativeAxisThickness.top * axisMask.top,
                    bottom = cumulativeAxisThickness.bottom * axisMask.bottom,
                )
            }
        }

        // Shrink the common geom bounds by the maximum spacer on each side.
        val totalSpacer = axisSpacerByPlot.reduce { acc, thickness ->
            Thickness(
                left = maxOf(acc.left, thickness.left),
                right = maxOf(acc.right, thickness.right),
                top = maxOf(acc.top, thickness.top),
                bottom = maxOf(acc.bottom, thickness.bottom),
            )
        }
        val adjustedGeomContentBounds = totalSpacer.shrinkRect(commonGeomContentBounds)

        // Re-layout each plot element so its content area matches the adjusted common rect.
        var plotIdx = 0
        return layouted.map { buildInfo ->
            when (buildInfo) {
                null -> null
                is PlotFigureBuildInfo -> buildInfo.layoutedByGeomBounds(
                    geomBounds = adjustedGeomContentBounds,
                    axisSpacer = axisSpacerByPlot[plotIdx++]
                )

                else -> buildInfo
            }
        }
    }

    companion object {
        private fun sharedGroups(share: Boolean, elementCount: Int): List<List<Int>> {
            return if (share) {
                listOf((0 until elementCount).toList())
            } else {
                (0 until elementCount).map { listOf(it) }
            }
        }
    }
}
