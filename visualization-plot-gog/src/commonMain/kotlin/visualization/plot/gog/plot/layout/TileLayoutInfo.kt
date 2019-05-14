package jetbrains.datalore.visualization.plot.gog.plot.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

class TileLayoutInfo
/**
 * Params xAxisInfo/yAxisInfo can be NULL in this case any mapping of positional Aes should be dropped (live map plot)
 */
private constructor(// tile origin relative to plot (plot area doesn't include title, axis title, facet labels or legends)
        // ToDo: rename
        val plotOffset: DoubleVector,
        // relative to plot tile
        val bounds: DoubleRectangle      // plotting area + optional elements (axis, axis tick labels)
        ,
        val geomBounds: DoubleRectangle  // actual plotting area
        ,
        val clipBounds: DoubleRectangle  // geom shapes outside this bounds will be cut-off
        ,
        val xAxisInfo: AxisLayoutInfo?, val yAxisInfo: AxisLayoutInfo?,
        xAxisShown: Boolean, yAxisShown: Boolean,
        val facetXLabel: String?, val facetYLabel: String?) {
    val xAxisShown: Boolean = xAxisInfo != null && xAxisShown
    val yAxisShown: Boolean = yAxisInfo != null && yAxisShown

    constructor(
            bounds: DoubleRectangle, geomBounds: DoubleRectangle, clipBounds: DoubleRectangle, xAxisInfo: AxisLayoutInfo?, yAxisInfo: AxisLayoutInfo?) : this(DoubleVector.ZERO, bounds, geomBounds, clipBounds, xAxisInfo, yAxisInfo, true, true, null, null)

    constructor(
            bounds: DoubleRectangle, geomBounds: DoubleRectangle, clipBounds: DoubleRectangle, xAxisInfo: AxisLayoutInfo?,
            yAxisInfo: AxisLayoutInfo?, xAxisShown: Boolean, yAxisShown: Boolean
    ) : this(DoubleVector.ZERO, bounds, geomBounds, clipBounds, xAxisInfo, yAxisInfo, xAxisShown, yAxisShown, null, null)

    fun withOffset(offset: DoubleVector): TileLayoutInfo {
        return TileLayoutInfo(offset,
                this.bounds,
                this.geomBounds,
                this.clipBounds,
                this.xAxisInfo, this.yAxisInfo,
                this.xAxisShown, this.yAxisShown,
                this.facetXLabel, this.facetYLabel
        )
    }

    fun getAbsoluteBounds(tilesOrigin: DoubleVector): DoubleRectangle {
        val offset = tilesOrigin.add(plotOffset)
        return bounds.add(offset)
    }

    fun getAbsoluteGeomBounds(tilesOrigin: DoubleVector): DoubleRectangle {
        val offset = tilesOrigin.add(plotOffset)
        return geomBounds.add(offset)
    }

    fun getAbsoluteClipBounds(tilesOrigin: DoubleVector): DoubleRectangle {
        val offset = tilesOrigin.add(plotOffset)
        return clipBounds.add(offset)
    }

    fun withFacetLabels(xLabel: String, yLabel: String): TileLayoutInfo {
        return TileLayoutInfo(this.plotOffset,
                this.bounds,
                this.geomBounds,
                this.clipBounds,
                this.xAxisInfo, this.yAxisInfo,
                this.xAxisShown, this.yAxisShown,
                xLabel, yLabel
        )
    }
}
