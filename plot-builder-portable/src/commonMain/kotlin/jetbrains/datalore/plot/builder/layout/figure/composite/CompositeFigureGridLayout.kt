/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.figure.composite

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.config.FigureBuildInfo
import jetbrains.datalore.plot.builder.layout.figure.CompositeFigureLayout
import jetbrains.datalore.plot.builder.layout.figure.composite.FigureGridLayoutUtil.indexToCol
import jetbrains.datalore.plot.builder.layout.figure.composite.FigureGridLayoutUtil.indexToRow

class CompositeFigureGridLayout(
    private val ncol: Int,
    private val nrow: Int,
) : CompositeFigureLayout {
    override fun doLayout(size: DoubleVector, elements: List<FigureBuildInfo?>): List<FigureBuildInfo> {
        check(elements.size == nrow * ncol) {
            "Grid size mismatch: ${elements.size} elements in a $ncol X $nrow grid."
        }

        val cellWidth = size.x / ncol
        val cellHeight = size.y / nrow

        val elementsWithBounds = elements.mapIndexed { index, buildInfo ->
            val row = indexToRow(index, ncol)
            val col = indexToCol(index, ncol)
            val bounds = DoubleRectangle(
                x = col * cellWidth,
                y = row * cellHeight,
                cellWidth,
                cellHeight
            )
            buildInfo?.withBounds(bounds)
        }

        return elementsWithBounds.map {
            it?.layoutedByOuterSize()
        }.filterNotNull()
    }
}