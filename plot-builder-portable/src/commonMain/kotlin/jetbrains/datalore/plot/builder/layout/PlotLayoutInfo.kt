/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.guide.Orientation

/**
 * Only "geom" area + axes.
 */
class PlotLayoutInfo constructor(
    val tiles: List<TileLayoutInfo>,
    val size: DoubleVector
) {
    val hasTopAxisTitle: Boolean = tiles.firstOrNull()?.axisInfos?.hAxisTitleOrientation == Orientation.TOP
    val hasLeftAxisTitle: Boolean = tiles.firstOrNull()?.axisInfos?.vAxisTitleOrientation == Orientation.LEFT

    val hasBottomAxis: Boolean = tiles.firstOrNull()?.axisInfos?.bottom != null
    val hasLeftAxis: Boolean = tiles.firstOrNull()?.axisInfos?.left != null

    val geomInnerBounds: DoubleRectangle
        get() {
            return tiles.map { it.geomInnerBounds }.reduce { acc, el -> acc.union(el) }
        }

    val geomOuterBounds: DoubleRectangle
        get() {
            return tiles.map { it.geomOuterBounds }.reduce { acc, el -> acc.union(el) }
        }

    val geomWithAxisBounds: DoubleRectangle
        get() {
            return tiles.map { it.geomWithAxisBounds }.reduce { acc, el -> acc.union(el) }
        }
}
