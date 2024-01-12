/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.FigureGridLayoutUtil.indexToCol
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.FigureGridLayoutUtil.indexToRow
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.FigureGridLayoutUtil.toCellOrigin
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.ScaleSharePolicy.*
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
    private val scaleShareX: ScaleSharePolicy,
    private val scaleShareY: ScaleSharePolicy,
) {
    protected fun toElelemtsWithInitialBounds(
        bounds: DoubleRectangle,
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
            totalSize = bounds.width - hSpaceSum,
            n = ncols,
            colWidths
        )

        val cellHeightByRow = cellSizeList(
            totalSize = bounds.height - vSpaceSum,
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

            buildInfo?.let {
                val figureBounds = if (fitCellAspectRatio) {
                    cellBounds
                } else {
                    val figureDefaultSize = elementsDefaultSizes[index]!!
                    cellBounds.srinkToAspectRatio(figureDefaultSize)
                }

                it.withBounds(figureBounds.add(bounds.origin))
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

    fun hasSharedAxis(): Boolean = !(scaleShareX == NONE && scaleShareY == NONE)

    fun indicesWithSharedXAxis(elementCount: Int): List<List<Int>> {
        return indicesWithSharedAxis(scaleShareX, elementCount, ncols)
    }

    fun indicesWithSharedYAxis(elementCount: Int): List<List<Int>> {
        return indicesWithSharedAxis(scaleShareY, elementCount, ncols)
    }


    private companion object {
        private fun indicesWithSharedAxis(
            sharePolicy: ScaleSharePolicy,
            elementCount: Int,
            ncols: Int
        ): List<List<Int>> {

            return when (sharePolicy) {
                NONE -> listOf(emptyList())
                ALL -> listOf(List(elementCount) { it })
                ROW -> {
                    val indexByRow = (0 until elementCount).map {
                        indexToRow(it, ncols) to it
                    }

                    groupByFirst(indexByRow)
                }

                COL -> {
                    val indexByCol = (0 until elementCount).map {
                        indexToCol(it, ncols) to it
                    }
                    groupByFirst(indexByCol)
                }
            }
        }

        private fun groupByFirst(pairs: List<Pair<Int, Int>>): List<List<Int>> {
            val numGroups = pairs.distinctBy { it.first }.size
            val groupsList = List(numGroups) { ArrayList<Int>() }
            for ((group, value) in pairs) {
                groupsList[group].add(value)
            }

            return groupsList
        }
    }
}