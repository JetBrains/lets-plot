/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.util.Insets

/**
 * Only "geom" area + axes.
 */
class PlotLayoutInfo constructor(
    val tiles: List<TileLayoutInfo>,
    private val insets: Insets,
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

    val size: DoubleVector = geomWithAxisBounds.dimension
        .add(insets.leftTop)
        .add(insets.rightBottom)
}
