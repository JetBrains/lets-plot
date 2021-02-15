/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

class TileLayoutInfo private constructor(
    // 'plot' means : geom area + axis (but not titles, facet labels or legends)
    val plotOrigin: DoubleVector,     // tile 'plot' origin relative to overall 'plot' origin

    // relative to plot tile
    val bounds: DoubleRectangle,      // plotting area + optional elements (axis, axis tick labels)
    val geomBounds: DoubleRectangle,  // actual plotting area
    val clipBounds: DoubleRectangle,  // geom shapes outside this bounds will be cut-off

    // Params xAxisInfo/yAxisInfo can be NULL.
    // In this case any mapping of positional Aes should be dropped (live map plot).
    val xAxisInfo: AxisLayoutInfo?,
    val yAxisInfo: AxisLayoutInfo?,

    xAxisShown: Boolean,
    yAxisShown: Boolean,

    val facetXLabels: List<String>,
    val facetYLabel: String?,

    val trueIndex: Int     // tile index before re-ordering (in facet wrap)
) {
    val xAxisShown: Boolean = xAxisInfo != null && xAxisShown
    val yAxisShown: Boolean = yAxisInfo != null && yAxisShown

    constructor(
        bounds: DoubleRectangle,
        geomBounds: DoubleRectangle,
        clipBounds: DoubleRectangle,
        xAxisInfo: AxisLayoutInfo?,
        yAxisInfo: AxisLayoutInfo?,
        xAxisShown: Boolean = true,
        yAxisShown: Boolean = true,
        trueIndex: Int
    ) : this(
        DoubleVector.ZERO,
        bounds,
        geomBounds,
        clipBounds,
        xAxisInfo,
        yAxisInfo,
        xAxisShown = xAxisShown,
        yAxisShown = yAxisShown,
        facetXLabels = emptyList(),
        facetYLabel = null,
        trueIndex
    )

    fun withOffset(offset: DoubleVector): TileLayoutInfo {
        return TileLayoutInfo(
            offset,
            bounds,
            geomBounds,
            clipBounds,
            xAxisInfo, yAxisInfo,
            xAxisShown, yAxisShown,
            facetXLabels, facetYLabel,
            trueIndex
        )
    }

    fun getAbsoluteBounds(tilesOrigin: DoubleVector): DoubleRectangle {
        val offset = tilesOrigin.add(plotOrigin)
        return bounds.add(offset)
    }

    fun getAbsoluteGeomBounds(tilesOrigin: DoubleVector): DoubleRectangle {
        val offset = tilesOrigin.add(plotOrigin)
        return geomBounds.add(offset)
    }

    fun withFacetLabels(xLabels: List<String>, yLabel: String?): TileLayoutInfo {
        return TileLayoutInfo(
            this.plotOrigin,
            this.bounds,
            this.geomBounds,
            this.clipBounds,
            this.xAxisInfo, this.yAxisInfo,
            this.xAxisShown, this.yAxisShown,
            xLabels, yLabel,
            trueIndex
        )
    }
}
