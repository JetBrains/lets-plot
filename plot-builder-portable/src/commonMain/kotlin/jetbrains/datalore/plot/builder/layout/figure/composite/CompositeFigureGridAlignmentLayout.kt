/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.figure.composite

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.layout.figure.CompositeFigureLayout
import jetbrains.datalore.plot.builder.layout.figure.composite.FigureGridLayoutUtil.colElements
import jetbrains.datalore.plot.builder.layout.figure.composite.FigureGridLayoutUtil.indexToCol
import jetbrains.datalore.plot.builder.layout.figure.composite.FigureGridLayoutUtil.indexToRow
import jetbrains.datalore.plot.builder.layout.figure.composite.FigureGridLayoutUtil.rowElements

class CompositeFigureGridAlignmentLayout(
    ncols: Int,
    nrows: Int,
    hSpace: Double,
    vSpace: Double,
) : CompositeFigureGridLayoutBase(
    ncols = ncols,
    nrows = nrows,
    hSpace = hSpace,
    vSpace = vSpace,
), CompositeFigureLayout {
    override fun doLayout(size: DoubleVector, elements: List<FigureBuildInfo?>): List<FigureBuildInfo?> {
        val elementsWithBounds = toElelemtsWithInitialBounds(size, elements)

        val elementsLayoutedByBounds = elementsWithBounds.map {
            it?.layoutedByOuterSize()
        }

        // Compute "inner" size for each row and colunm
        val vSpanByRow = ArrayList<DoubleSpan?>()
        for (row in 0 until nrows) {
            val rowElements = rowElements(row, elementsLayoutedByBounds, ncols)
            val vSpan = rowElements.filterNotNull().map {
                it.layoutInfo.geomAreaBounds.yRange()
            }.reduceOrNull { acc, span ->
                acc.intersection(span)
            }
            vSpanByRow.add(vSpan)
        }

        val hSpanByCol = ArrayList<DoubleSpan?>()
        for (col in 0 until ncols) {
            val colElements = colElements(col, elementsLayoutedByBounds, ncols)
            val hSpan = colElements.filterNotNull().map {
                it.layoutInfo.geomAreaBounds.xRange()
            }.reduceOrNull { acc, span ->
                acc.intersection(span)
            }
            hSpanByCol.add(hSpan)
        }

        val elementsLayoutedByInnerBounds = elementsWithBounds.mapIndexed { index, buildInfo ->
            if (buildInfo == null) {
                null
            } else {
                val row = indexToRow(index, ncols)
                val col = indexToCol(index, ncols)
                val bounds = DoubleRectangle.hvRange(
                    hSpanByCol[col]!!,
                    vSpanByRow[row]!!
                )
                buildInfo.layoutedByGeomBounds(bounds)
            }
        }

        return elementsLayoutedByInnerBounds
    }
}