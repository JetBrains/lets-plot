/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleInsets
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation

/**
 * Only "geom" area and axes.
 */
class PlotLayoutInfo constructor(
    val tiles: List<TileLayoutInfo>,
    private val insets: DoubleInsets,
) {
    val hasTopAxisTitle: Boolean = tiles.firstOrNull()?.axisInfos?.hAxisTitleOrientation == Orientation.TOP
    val hasLeftAxisTitle: Boolean = tiles.firstOrNull()?.axisInfos?.vAxisTitleOrientation == Orientation.LEFT

    val hasBottomAxis: Boolean = tiles.firstOrNull()?.axisInfos?.bottom != null
    val hasLeftAxis: Boolean = tiles.firstOrNull()?.axisInfos?.left != null

    val geomWithAxisBounds: DoubleRectangle
        get() {
            // Tile geom area, axis, axis ticks/labels.
            return tiles.map { it.geomWithAxisBounds }.reduce { acc, el -> acc.union(el) }
        }

//    val geomOuterBounds: DoubleRectangle
//        get() {
//            // geomWithAxisBounds excluding axis
//            return tiles.map { it.geomOuterBounds }.reduce { acc, el -> acc.union(el) }
//        }
//
//    val geomInnerBounds: DoubleRectangle
//        get() {
//            // geomOuterBounds excluding marginal layers (if any)
//            return tiles.map { it.geomInnerBounds }.reduce { acc, el -> acc.union(el) }
//        }
//
//    val geomContentBounds: DoubleRectangle
//        get() {
//            // actual plotting area: geomInnerBounds excluding plot panel insets
//            return tiles.map { it.geomContentBounds }.reduce { acc, el -> acc.union(el) }
//        }

    val size: DoubleVector = geomWithAxisBounds.dimension
        .add(insets.leftTop)
        .add(insets.rightBottom)
}
