/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.layout.XYPlotLayoutUtil.clipBounds
import jetbrains.datalore.plot.builder.layout.XYPlotLayoutUtil.liveMapGeomBounds

internal class LiveMapTileLayout : TileLayout {

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): TileLayoutInfo {
        val geomBounds = liveMapGeomBounds(preferredSize)
        return TileLayoutInfo(
            geomBounds,
            geomBounds,
            clipBounds(geomBounds),
            null, null,
            trueIndex = 0
        )
    }
}
