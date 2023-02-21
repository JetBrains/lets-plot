/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.figure.composite

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.FigureBuildInfo
import kotlin.math.max

abstract class CompositeFigureGridLayoutBase(
    protected val ncols: Int,
    protected val nrows: Int,
    private val hSpace: Double,
    private val vSpace: Double,
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

        val figureWidth = max(1.0, (size.x - hSpaceSum)) / ncols
        val figureHeight = max(1.0, (size.y - vSpaceSum)) / nrows

        return elements.mapIndexed { index, buildInfo ->
            val row = FigureGridLayoutUtil.indexToRow(index, ncols)
            val col = FigureGridLayoutUtil.indexToCol(index, ncols)
            val bounds = DoubleRectangle(
                x = col * (figureWidth + hSpace),
                y = row * (figureHeight + vSpace),
                figureWidth,
                figureHeight
            )
            buildInfo?.withBounds(bounds)
        }
    }
}