/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

class TileLayoutInfo constructor(
    val offset: DoubleVector,  // A value to take in account when translating relative tile bounds to absolute ones.

    // Relative bounds.
    val geomWithAxisBounds: DoubleRectangle,    // Tile geom area, axis, axis ticks/labels.
    val geomOuterBounds: DoubleRectangle,  // Tile geom area including margins.
    val geomInnerBounds: DoubleRectangle,  // Tile main geom area.
    val geomContentBounds: DoubleRectangle,  // panel area excluding padding.

    val axisInfos: AxisLayoutInfoQuad,

    hAxisShown: Boolean,
    vAxisShown: Boolean,

    // labels and tab height for them
    val facetXLabels: List<Pair<String, Double>> = emptyList(),
    val facetYLabel: Pair<String, Double>? = null,

    val trueIndex: Int,     // tile index before re-ordering (in facet wrap)
) {
    val hAxisShown: Boolean = (axisInfos.top != null || axisInfos.bottom != null) && hAxisShown
    val vAxisShown: Boolean = (axisInfos.left != null || axisInfos.right != null) && vAxisShown

    fun getAbsoluteBounds(tilesOrigin: DoubleVector): DoubleRectangle {
        val offset = tilesOrigin.add(this.offset)
        return geomWithAxisBounds.add(offset)
    }

    fun getAbsoluteOuterGeomBounds(tilesOrigin: DoubleVector): DoubleRectangle {
        val offset = tilesOrigin.add(this.offset)
        return geomOuterBounds.add(offset)
    }

    fun axisThicknessX(): Double {
        return geomWithAxisBounds.bottom - geomOuterBounds.bottom
    }

    fun axisThicknessY(): Double {
        return geomOuterBounds.left - geomWithAxisBounds.left
    }

    fun geomOuterWidth(): Double {
        return geomOuterBounds.width
    }

    fun geomOuterHeight(): Double {
        return geomOuterBounds.height
    }

    fun withOffset(offset: DoubleVector): TileLayoutInfo {
        return TileLayoutInfo(
            offset = offset,
            this.geomWithAxisBounds,
            this.geomOuterBounds,
            this.geomInnerBounds,
            this.geomContentBounds,
            this.axisInfos,
            this.hAxisShown, this.vAxisShown,
            this.facetXLabels, this.facetYLabel,
            this.trueIndex
        )
    }

    fun withFacetLabels(xLabels: List<Pair<String, Double>>, yLabel: Pair<String, Double>?): TileLayoutInfo {
        return TileLayoutInfo(
            this.offset,
            this.geomWithAxisBounds,
            this.geomOuterBounds,
            this.geomInnerBounds,
            this.geomContentBounds,
            this.axisInfos,
            this.hAxisShown, this.vAxisShown,
            xLabels, yLabel,
            this.trueIndex
        )
    }

    fun withAxisShown(hAxisShown: Boolean, vAxisShown: Boolean): TileLayoutInfo {
        return TileLayoutInfo(
            this.offset,
            this.geomWithAxisBounds,
            this.geomOuterBounds,
            this.geomInnerBounds,
            this.geomContentBounds,
            this.axisInfos,
            hAxisShown, vAxisShown,
            this.facetXLabels, this.facetYLabel,
            this.trueIndex
        )
    }

    fun withNormalizedOrigin(): TileLayoutInfo {
        val geomWithAxisOrigin = geomWithAxisBounds.origin

        val geomWithAxisBounds = geomWithAxisBounds.subtract(geomWithAxisOrigin)
        val geomOuterBounds = geomOuterBounds.subtract(geomWithAxisOrigin)
        val geomInnerBounds = geomInnerBounds.subtract(geomWithAxisOrigin)
        val geomContentBounds = geomContentBounds.subtract(geomWithAxisOrigin)

        return TileLayoutInfo(
            this.offset,
            geomWithAxisBounds,
            geomOuterBounds,
            geomInnerBounds,
            geomContentBounds,
            this.axisInfos,
            this.hAxisShown, this.vAxisShown,
            this.facetXLabels, this.facetYLabel,
            this.trueIndex
        )
    }
}
