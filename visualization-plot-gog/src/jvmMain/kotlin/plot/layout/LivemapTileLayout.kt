package jetbrains.datalore.visualization.plot.gog.plot.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

internal class LivemapTileLayout : TileLayoutBase() {

    override fun doLayout(preferredSize: DoubleVector): TileLayoutInfo {
        var geomBounds = geomBounds(0.0, 0.0, preferredSize)
        geomBounds = geomBounds.union(DoubleRectangle(geomBounds.origin, GEOM_MIN_SIZE))
        val geomWithAxisBounds = geomBounds
        return TileLayoutInfo(
                geomWithAxisBounds,
                geomBounds,
                clipBounds(geomBounds),
                null!!, null!!
        )
    }
}
