/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.CompositeFigureLayout
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.DEF_LARGE_PLOT_SIZE

class CompositeFigureFreeLayout(
    regions: List<DoubleRectangle>,
    elementsCount: Int
) : CompositeFigureLayout {
    private val regions: List<DoubleRectangle>

    init {
        val autoRegionsCount = elementsCount - regions.size
        this.regions = regions + calculateAutoRegions(autoRegionsCount)
    }

    override fun defaultSize(): DoubleVector {
        return DEF_LARGE_PLOT_SIZE
    }

    override fun doLayout(bounds: DoubleRectangle, elements: List<FigureBuildInfo?>): List<FigureBuildInfo?> {
        val elementBoundsList = regions.map {
            // scale
            DoubleRectangle.XYWH(
                it.origin.x * bounds.width,
                it.origin.y * bounds.height,
                it.width * bounds.width,
                it.height * bounds.height,
            ).add(bounds.origin)
        }

        val elementsWithBounds = elements.mapIndexed { index, buildInfo ->
            buildInfo?.withBounds(elementBoundsList[index])
        }
        return elementsWithBounds.map {
            it?.layoutedByOuterSize()
        }
    }

    private fun calculateAutoRegions(n: Int): List<DoubleRectangle> {
        if (n <= 0) return emptyList()

        // Start and end points of diagonal
        val startPos = 0.3 to 0.3
        val endPos = 0.7 to 0.7

        val width = 0.3
        val height = 0.3

        // Available space for movement along diagonal
        val dx = endPos.first - width - startPos.first
        val dy = endPos.second - height - startPos.second

        val (stepX, stepY) = if (n > 1) {
            (dx / (n - 1)) to (dy / (n - 1))
        } else {
            0.0 to 0.0
        }

        return List(n) { i ->
            val x = startPos.first + (stepX * i)
            val y = startPos.second + (stepY * i)
            DoubleRectangle(x, y, width, height)
        }
    }
}