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
    private val regions: List<DoubleRectangle>
) : CompositeFigureLayout {
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
}