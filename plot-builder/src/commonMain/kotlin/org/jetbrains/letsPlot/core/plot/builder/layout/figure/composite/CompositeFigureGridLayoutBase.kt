/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.FigureGridLayoutUtil.toCellOrigin
import kotlin.math.max

abstract class CompositeFigureGridLayoutBase(
    protected val ncols: Int,
    protected val nrows: Int,
    private val hSpace: Double,
    private val vSpace: Double,
    private val colWidths: List<Double>?,
    private val rowHeights: List<Double>?,
    private val fitCellAspectRatio: Boolean,
    private val elementsDefaultSizes: List<DoubleVector?>,
) {
    protected fun toElelemtsWithInitialBounds(
        size: DoubleVector,
        elements: List<FigureBuildInfo?>
    ): List<FigureBuildInfo?> {
        check(ncols > 0)
        check(nrows > 0)
        check(elements.size == nrows * ncols) {
            "Grid size mismatch: ${elements.size} elements in a $ncols X $nrows grid."
        }

        val hSpaceSum = hSpace * (ncols - 1)
        val vSpaceSum = vSpace * (nrows - 1)

        val cellWidthByCol = cellSizeList(
            totalSize = size.x - hSpaceSum,
            n = ncols,
            colWidths
        )

        val cellHeightByRow = cellSizeList(
            totalSize = size.y - vSpaceSum,
            n = nrows,
            rowHeights
        )

        return elements.mapIndexed { index, buildInfo ->
            val row = FigureGridLayoutUtil.indexToRow(index, ncols)
            val col = FigureGridLayoutUtil.indexToCol(index, ncols)
            val cellBounds = DoubleRectangle(
                x = toCellOrigin(col, cellWidthByCol, hSpace),
                y = toCellOrigin(row, cellHeightByRow, vSpace),
                w = cellWidthByCol[col],
                h = cellHeightByRow[row]
            )


//            buildInfo?.withBounds(cellBounds)

            buildInfo?.let {
                val figureBounds = if (fitCellAspectRatio) {
                    cellBounds
                } else {
                    val figureDefaultSize = elementsDefaultSizes[index]!!
                    cellBounds.srinkToAspectRatio(figureDefaultSize)
                }

//                it.withBounds(cellBounds)
                it.withBounds(figureBounds)
            }
        }
    }

    private fun cellSizeList(totalSize: Double, n: Int, sizeList: List<Double>?): List<Double> {
        @Suppress("NAME_SHADOWING")
        val sizeList = if (sizeList.isNullOrEmpty()) {
            List(n) { 1.0 }
        } else {
            (sizeList + List(n) { sizeList.last() }).take(n)
        }

        val sizeSum = sizeList.sum()
        val sizeListNorm = sizeList.map { it / sizeSum }
        return sizeListNorm
            .map { it * totalSize }
            .map { max(it, 1.0) }
    }
}