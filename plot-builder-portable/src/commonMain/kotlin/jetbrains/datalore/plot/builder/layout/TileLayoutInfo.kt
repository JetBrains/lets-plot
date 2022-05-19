/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

class TileLayoutInfo constructor(
    val offset: DoubleVector,  // A value to take in account when translating relative tile bounds to absolute ones.

    // Relative bounds.
    val bounds: DoubleRectangle,      // Tile geom area, axis, axis ticks/labels.
    val geomBounds: DoubleRectangle,  // Tile geom area.
    private val clipBounds: DoubleRectangle,  // geom shapes outside these bounds will be cut-off

    // Params xAxisInfo/yAxisInfo can be NULL.
    // In this case any mapping of positional Aes should be dropped (live map plot).
    val hAxisInfo: AxisLayoutInfo?,
    val vAxisInfo: AxisLayoutInfo?,

    hAxisShown: Boolean,
    vAxisShown: Boolean,

    val facetXLabels: List<String> = emptyList(),
    val facetYLabel: String? = null,

    val trueIndex: Int     // tile index before re-ordering (in facet wrap)
) {
    val hAxisShown: Boolean = hAxisInfo != null && hAxisShown
    val vAxisShown: Boolean = vAxisInfo != null && vAxisShown

    fun withOffset(offset: DoubleVector): TileLayoutInfo {
        return TileLayoutInfo(
            offset = offset,
            this.bounds,
            this.geomBounds,
            this.clipBounds,
            this.hAxisInfo, this.vAxisInfo,
            this.hAxisShown, this.vAxisShown,
            this.facetXLabels, this.facetYLabel,
            this.trueIndex
        )
    }

    fun withFacetLabels(xLabels: List<String>, yLabel: String?): TileLayoutInfo {
        return TileLayoutInfo(
            this.offset,
            this.bounds,
            this.geomBounds,
            this.clipBounds,
            this.hAxisInfo, this.vAxisInfo,
            this.hAxisShown, this.vAxisShown,
            xLabels, yLabel,
            this.trueIndex
        )
    }

    fun withAxisShown(hAxisShown: Boolean, vAxisShown: Boolean): TileLayoutInfo {
        return TileLayoutInfo(
            this.offset,
            this.bounds,
            this.geomBounds,
            this.clipBounds,
            this.hAxisInfo, this.vAxisInfo,
            hAxisShown, vAxisShown,
            this.facetXLabels, this.facetYLabel,
            this.trueIndex
        )
    }

    fun getAbsoluteBounds(tilesOrigin: DoubleVector): DoubleRectangle {
        val offset = tilesOrigin.add(this.offset)
        return bounds.add(offset)
    }

    fun getAbsoluteGeomBounds(tilesOrigin: DoubleVector): DoubleRectangle {
        val offset = tilesOrigin.add(this.offset)
        return geomBounds.add(offset)
    }

    fun axisThicknessX(): Double {
        return bounds.bottom - geomBounds.bottom
    }

    fun axisThicknessY(): Double {
        return geomBounds.left - bounds.left
    }

    fun geomWidth(): Double {
        return geomBounds.width
    }

    fun geomHeight(): Double {
        return geomBounds.height
    }
}
