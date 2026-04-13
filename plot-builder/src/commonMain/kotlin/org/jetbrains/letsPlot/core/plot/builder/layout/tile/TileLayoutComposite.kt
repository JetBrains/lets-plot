/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.tile

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutInfo

internal class TileLayoutComposite(
    private val topDownTileLayout: TopDownTileLayout,
    private val insideOutTileLayout: InsideOutTileLayout,
) : TileLayout {
    override fun doTopDownLayout(
        geomWithAxisSize: DoubleVector,
        coordProvider: CoordProvider
    ): TileLayoutInfo {
        return topDownTileLayout.doLayout(geomWithAxisSize, coordProvider)
    }

    override fun doInsideOutLayout(
        geomContentSize: DoubleVector,
        coordProvider: CoordProvider
    ): TileLayoutInfo {
        return insideOutTileLayout.doLayout(geomContentSize, coordProvider)
    }
}