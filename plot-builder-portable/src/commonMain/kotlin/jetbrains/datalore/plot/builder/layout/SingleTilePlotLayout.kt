/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.coord.CoordProvider

internal class SingleTilePlotLayout(
    private val tileLayout: TileLayout
) : PlotLayoutBase() {

    init {
        setPadding(10.0, 10.0, 0.0, 0.0)
    }

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): PlotLayoutInfo {
        val paddingLeftTop = DoubleVector(paddingLeft, paddingTop)
        val paddingRightBottom = DoubleVector(paddingRight, paddingBottom)

        val tilePreferredSize = preferredSize
            .subtract(paddingLeftTop)
            .subtract(paddingRightBottom)

        val tileInfo = tileLayout.doLayout(tilePreferredSize, coordProvider)
            .withOffset(paddingLeftTop)

        val plotSize = tileInfo.bounds.dimension
            .add(paddingLeftTop)
            .add(paddingRightBottom)

        return PlotLayoutInfo(listOf(tileInfo), plotSize)
    }
}
