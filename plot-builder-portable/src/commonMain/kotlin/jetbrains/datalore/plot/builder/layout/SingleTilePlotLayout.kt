/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.coord.CoordProvider

internal class SingleTilePlotLayout(private val myTileLayout: TileLayout) : PlotLayoutBase() {

    init {
        setPadding(10.0, 10.0, 0.0, 0.0)
    }

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): PlotLayoutInfo {
        val tilePreferredSize = DoubleVector(
            preferredSize.x - (paddingLeft + paddingRight),
            preferredSize.y - (paddingTop + paddingBottom)
        )

        var tileInfo = myTileLayout.doLayout(tilePreferredSize, coordProvider)
        tileInfo = tileInfo.withOffset(DoubleVector(paddingLeft, paddingTop))

        var plotSize = tileInfo.bounds.dimension
        plotSize = plotSize.add(DoubleVector(paddingRight, paddingBottom))

        return PlotLayoutInfo(listOf(tileInfo), plotSize)
    }
}
