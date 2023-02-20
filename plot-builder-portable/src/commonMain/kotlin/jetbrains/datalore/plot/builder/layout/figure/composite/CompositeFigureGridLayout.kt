/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.figure.composite

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.layout.figure.CompositeFigureLayout
import jetbrains.datalore.plot.builder.layout.figure.composite.FigureGridLayoutUtil.indexToCol
import jetbrains.datalore.plot.builder.layout.figure.composite.FigureGridLayoutUtil.indexToRow

class CompositeFigureGridLayout(
    private val ncols: Int,
    private val nrows: Int,
) : CompositeFigureLayout {
    override fun doLayout(size: DoubleVector, elements: List<FigureBuildInfo?>): List<FigureBuildInfo> {
        check(elements.size == nrows * ncols) {
            "Grid size mismatch: ${elements.size} elements in a $ncols X $nrows grid."
        }

        val cellWidth = size.x / ncols
        val cellHeight = size.y / nrows

        val elementsWithBounds = elements.mapIndexed { index, buildInfo ->
            val row = indexToRow(index, ncols)
            val col = indexToCol(index, ncols)
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