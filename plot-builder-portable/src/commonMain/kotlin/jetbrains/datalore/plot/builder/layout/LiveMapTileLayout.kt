/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

internal class LiveMapTileLayout : TileLayoutBase() {

    override fun doLayout(preferredSize: DoubleVector): TileLayoutInfo {
        var geomBounds = geomBounds(
            0.0,
            0.0,
            preferredSize
        )
        geomBounds = geomBounds.union(DoubleRectangle(geomBounds.origin,
            GEOM_MIN_SIZE
        ))
        val geomWithAxisBounds = geomBounds
        return TileLayoutInfo(
            geomWithAxisBounds,
            geomBounds,
            clipBounds(geomBounds),
            null, null
        )
    }
}
