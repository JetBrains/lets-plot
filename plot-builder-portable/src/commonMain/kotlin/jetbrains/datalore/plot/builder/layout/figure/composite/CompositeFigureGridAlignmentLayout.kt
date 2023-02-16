/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.figure.composite

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.builder.config.FigureBuildInfo
import jetbrains.datalore.plot.builder.layout.figure.CompositeFigureLayout
import jetbrains.datalore.plot.builder.layout.figure.composite.FigureGridLayoutUtil.colElements
import jetbrains.datalore.plot.builder.layout.figure.composite.FigureGridLayoutUtil.indexToCol
import jetbrains.datalore.plot.builder.layout.figure.composite.FigureGridLayoutUtil.indexToRow
import jetbrains.datalore.plot.builder.layout.figure.composite.FigureGridLayoutUtil.rowElements

class CompositeFigureGridAlignmentLayout(
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

        val elementsLayoutedByBounds = elementsWithBounds.map {
            it?.layoutedByOuterSize()
        }

        // Compute "inner" size for each row and colunm
        val vSpanByRow = ArrayList<DoubleSpan?>()
        for (row in 0 until nrow) {
            val rowElements = rowElements(row, elementsLayoutedByBounds, ncol)
            val vSpan = rowElements.filterNotNull().map {
                it.layoutInfo.geomBounds.yRange()
            }.reduceOrNull { acc, span ->
                acc.intersection(span)
            }
            vSpanByRow.add(vSpan)
        }

        val hSpanByCol = ArrayList<DoubleSpan?>()
        for (col in 0 until ncol) {
            val colElements = colElements(col, elementsLayoutedByBounds, ncol)
            val hSpan = colElements.filterNotNull().map {
                it.layoutInfo.geomBounds.xRange()
            }.reduceOrNull { acc, span ->
                acc.intersection(span)
            }
            hSpanByCol.add(hSpan)
        }

        val elementsLayoutedByInnerBounds = elementsWithBounds.mapIndexed { index, buildInfo ->
            if (buildInfo == null) {
                null
            } else {
                val row = indexToRow(index, ncol)
                val col = indexToCol(index, ncol)
                val bounds = DoubleRectangle.hvRange(
                    hSpanByCol[col]!!,
                    vSpanByRow[row]!!
                )
                buildInfo.layoutedByGeomBounds(bounds)
            }
        }

        return elementsLayoutedByInnerBounds.filterNotNull()
    }
}