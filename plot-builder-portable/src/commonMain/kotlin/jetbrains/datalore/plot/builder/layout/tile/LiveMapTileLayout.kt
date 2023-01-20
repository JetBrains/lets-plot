/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.tile

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.layout.AxisLayoutInfoQuad
import jetbrains.datalore.plot.builder.layout.TileLayout
import jetbrains.datalore.plot.builder.layout.TileLayoutInfo
import jetbrains.datalore.plot.builder.layout.tile.TileLayoutUtil.liveMapGeomBounds

internal class LiveMapTileLayout : TileLayout {

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): TileLayoutInfo {
        val geomBounds = liveMapGeomBounds(preferredSize)
        return TileLayoutInfo(
            offset = DoubleVector.ZERO,
            bounds = geomBounds,
            geomOuterBounds = geomBounds,
            geomInnerBounds = geomBounds,
            axisInfos = AxisLayoutInfoQuad.EMPTY,
            hAxisShown = false,
            vAxisShown = false,
            trueIndex = 0
        )
    }
}
