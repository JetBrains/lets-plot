/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
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

        // Re-layout each plot element so its content area matches the common rect.
        return layouted.map { buildInfo ->
            when (buildInfo) {
                null -> null
                is PlotFigureBuildInfo -> buildInfo.layoutedByGeomBounds(commonGeomContentBounds)
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
