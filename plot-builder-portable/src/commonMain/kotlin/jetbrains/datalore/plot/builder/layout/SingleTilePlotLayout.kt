/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal class SingleTilePlotLayout(
    private val tileLayout: TileLayout,
    hAxisTheme: AxisTheme,
    vAxisTheme: AxisTheme,
) : PlotLayoutBase() {

    init {
        // ToDo: axis position
        val leftPadding = if (!vAxisTheme.showTitle() && !vAxisTheme.showLabels()) PADDING else 0.0
        val bottomPadding = if(!hAxisTheme.showTitle() && !hAxisTheme.showLabels()) PADDING else 0.0
        setPadding(top = PADDING, right = PADDING, bottomPadding, leftPadding)
    }

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): PlotLayoutInfo {
        val paddingLeftTop = DoubleVector(paddingLeft, paddingTop)
        val paddingRightBottom = DoubleVector(paddingRight, paddingBottom)

        val tilePreferredSize = preferredSize
            .subtract(paddingLeftTop)
            .subtract(paddingRightBottom)

        val tileInfo = tileLayout
            .doLayout(tilePreferredSize, coordProvider)
            .withOffset(paddingLeftTop)

        val plotSize = tileInfo.bounds.dimension
            .add(paddingLeftTop)
            .add(paddingRightBottom)

        return PlotLayoutInfo(listOf(tileInfo), plotSize)
    }

    companion object {
        private const val PADDING = 10.0
    }
}
