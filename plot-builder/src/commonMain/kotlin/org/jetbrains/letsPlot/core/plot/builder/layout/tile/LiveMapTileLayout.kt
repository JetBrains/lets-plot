/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.tile

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.AxisLayoutInfoQuad
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.tile.TileLayoutUtil.liveMapGeomBounds

internal class LiveMapTileLayout : TileLayout {
    override val insideOut: Boolean = false

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): TileLayoutInfo {
        val geomBounds = liveMapGeomBounds(preferredSize)
        return TileLayoutInfo(
            offset = DoubleVector.ZERO,
            geomWithAxisBounds = geomBounds,
            geomOuterBounds = geomBounds,
            geomInnerBounds = geomBounds,
            geomContentBounds = geomBounds,
            axisInfos = AxisLayoutInfoQuad.EMPTY,
            hAxisShown = false,
            vAxisShown = false,
            trueIndex = 0
        )
    }
}
