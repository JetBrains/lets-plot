/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.figure.composite

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
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
    colWidths: List<Double>?,
    rowHeights: List<Double>?,
    fitCellAspectRatio: Boolean,
    elementsDefaultSizes: List<DoubleVector?>,
) : CompositeFigureGridLayoutBase(
    ncols = ncols,
    nrows = nrows,
    hSpace = hSpace,
    vSpace = vSpace,
    colWidths = colWidths,
    rowHeights = rowHeights,
    fitCellAspectRatio = fitCellAspectRatio,
    elementsDefaultSizes = elementsDefaultSizes,
), CompositeFigureLayout {
    override fun doLayout(size: DoubleVector, elements: List<FigureBuildInfo?>): List<FigureBuildInfo?> {
        val elementsWithBounds = toElelemtsWithInitialBounds(size, elements)

        val elementsLayoutedByBounds = elementsWithBounds.map {
            it?.layoutedByOuterSize()
        }

        // Compute "inner" size for each row and colunm
        val vGeomSpanByRow = ArrayList<DoubleSpan?>()
        for (row in 0 until nrows) {
            val rowElements = rowElements(row, elementsLayoutedByBounds, ncols, inclideComposite = false)
            val vSpan = rowElements
                .filterNotNull()
                .map {
                    it.layoutInfo.geomAreaBounds.yRange()
                }.reduceOrNull { acc, span ->
                    acc.intersection(span)
                }
            vGeomSpanByRow.add(vSpan)
        }

        val hGeomSpanByCol = ArrayList<DoubleSpan?>()
        for (col in 0 until ncols) {
            val colElements = colElements(col, elementsLayoutedByBounds, ncols, inclideComposite = false)
            val hSpan = colElements
                .filterNotNull()
                .map {
                    it.layoutInfo.geomAreaBounds.xRange()
                }.reduceOrNull { acc, span ->
                    acc.intersection(span)
                }
            hGeomSpanByCol.add(hSpan)
        }

        val elementsLayoutedByInnerBounds = elementsLayoutedByBounds.mapIndexed { index, buildInfo ->
            if (buildInfo == null) {
                null
            } else if (buildInfo.isComposite) {
                // Do not layoute composite figure by "geom bounds".
                buildInfo
            } else {
                val row = indexToRow(index, ncols)
                val col = indexToCol(index, ncols)
                val bounds = DoubleRectangle.hvRange(
                    hGeomSpanByCol[col]!!,
                    vGeomSpanByRow[row]!!
                )
                buildInfo.layoutedByGeomBounds(bounds)
            }
        }

        return elementsLayoutedByInnerBounds
    }
}