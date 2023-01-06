/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.guide.Orientation

class PlotLayoutInfo(tiles: List<TileLayoutInfo>, val size: DoubleVector) {
    val tiles: List<TileLayoutInfo> = ArrayList(tiles)

    val hAxisOrientation: Orientation = tiles.firstOrNull()?.hAxisInfo?.orientation ?: Orientation.BOTTOM
    val vAxisOrientation: Orientation = tiles.firstOrNull()?.vAxisInfo?.orientation ?: Orientation.LEFT
}
