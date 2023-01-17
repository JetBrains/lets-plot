/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.guide.Orientation

class PlotLayoutInfo(tiles: List<TileLayoutInfo>, val size: DoubleVector) {
    val tiles: List<TileLayoutInfo> = ArrayList(tiles)

    val hasTopAxisTitle: Boolean = tiles.firstOrNull()?.axisInfos?.hAxisTitleOrientation == Orientation.TOP
    val hasLeftAxisTitle: Boolean = tiles.firstOrNull()?.axisInfos?.vAxisTitleOrientation == Orientation.LEFT
}
