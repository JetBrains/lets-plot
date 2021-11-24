/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleVector

class PlotLayoutInfo(tiles: List<TileLayoutInfo>, val size: DoubleVector) {
    val tiles: List<TileLayoutInfo> = ArrayList(tiles)
}
