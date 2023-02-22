/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.figure.composite

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.layout.figure.CompositeFigureLayout

class CompositeFigureGridLayout(
    ncols: Int,
    nrows: Int,
    hSpace: Double,
    vSpace: Double,
    colWidths: List<Double>?,
    rowHeights: List<Double>?,
) : CompositeFigureGridLayoutBase(
    ncols = ncols,
    nrows = nrows,
    hSpace = hSpace,
    vSpace = vSpace,
    colWidths = colWidths,
    rowHeights = rowHeights,
), CompositeFigureLayout {
    override fun doLayout(size: DoubleVector, elements: List<FigureBuildInfo?>): List<FigureBuildInfo?> {
        val elementsWithBounds = toElelemtsWithInitialBounds(size, elements)

        return elementsWithBounds.map {
            it?.layoutedByOuterSize()
        }
    }
}