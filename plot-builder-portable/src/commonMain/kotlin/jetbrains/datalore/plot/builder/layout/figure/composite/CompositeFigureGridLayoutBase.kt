/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.figure.composite

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.layout.figure.composite.FigureGridLayoutUtil.toCellOrigin
import kotlin.math.max

abstract class CompositeFigureGridLayoutBase(
    protected val ncols: Int,
    protected val nrows: Int,
    private val hSpace: Double,
    private val vSpace: Double,
    private val colWidths: List<Double>?,
    private val rowHeights: List<Double>?,
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

//        val elementWidth = max(1.0, (size.x - hSpaceSum)) / ncols
//        val elementHeight = max(1.0, (size.y - vSpaceSum)) / nrows

        val elementWidthByCol = elementSizeList(
            totalSize = size.x - hSpaceSum,
            n = ncols,
            colWidths
        )

        val elementHeightByRow = elementSizeList(
            totalSize = size.y - vSpaceSum,
            n = nrows,
            rowHeights
        )

        return elements.mapIndexed { index, buildInfo ->
            val row = FigureGridLayoutUtil.indexToRow(index, ncols)
            val col = FigureGridLayoutUtil.indexToCol(index, ncols)
            val elementWidth = elementWidthByCol[col]
            val elementHeight = elementHeightByRow[row]
            val bounds = DoubleRectangle(
                x = toCellOrigin(col, elementWidthByCol, hSpace),
                y = toCellOrigin(row, elementHeightByRow, vSpace),
                elementWidth,
                elementHeight
            )
            buildInfo?.withBounds(bounds)
        }
    }

    private fun elementSizeList(totalSize: Double, n: Int, sizeList: List<Double>?): List<Double> {
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