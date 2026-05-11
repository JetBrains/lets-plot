/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.buildinfo.FigureBuildInfo

class CompositeFigureGridLayout(
    ncols: Int,
    nrows: Int,
    hSpace: Double,
    vSpace: Double,
    colWidths: List<Double>?,
    rowHeights: List<Double>?,
    fitCellAspectRatio: Boolean,
    elementsDefaultSizes: List<DoubleVector?>,
    scaleShareX: ScaleSharePolicy,
    scaleShareY: ScaleSharePolicy,
) : CompositeFigureGridLayoutBase(
    ncols = ncols,
    nrows = nrows,
    hSpace = hSpace,
    vSpace = vSpace,
    colWidths = colWidths,
    rowHeights = rowHeights,
    fitCellAspectRatio = fitCellAspectRatio,
    elementsDefaultSizes = elementsDefaultSizes,
    scaleShareX = scaleShareX,
    scaleShareY = scaleShareY,
) {
    override fun doLayout(
        elementsSpace: DoubleRectangle,
        elements: List<FigureBuildInfo?>,
        outerBounds: DoubleRectangle
    ): List<FigureBuildInfo?> {
        val elementsWithBounds = toElementsWithInitialBounds(elementsSpace, elements)

        return elementsWithBounds.map {
            it?.layoutedByOuterSize()
        }
    }
}