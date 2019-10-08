package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

internal abstract class TileLayoutBase : TileLayout {
    companion object {
        val GEOM_MARGIN = 0.0          // min space around geom area
        protected val CLIP_EXTEND = 5.0
        val GEOM_MIN_SIZE = DoubleVector(50.0, 50.0)

        fun geomBounds(xAxisThickness: Double, yAxisThickness: Double, plotSize: DoubleVector): DoubleRectangle {
            val marginLeftTop = DoubleVector(yAxisThickness, GEOM_MARGIN)
            val marginRightBottom = DoubleVector(GEOM_MARGIN, xAxisThickness)
            var geomSize = plotSize
                    .subtract(marginLeftTop)
                    .subtract(marginRightBottom)

            if (geomSize.x < GEOM_MIN_SIZE.x) {
                geomSize = DoubleVector(GEOM_MIN_SIZE.x, geomSize.y)
            }
            if (geomSize.y < GEOM_MIN_SIZE.y) {
                geomSize = DoubleVector(geomSize.x, GEOM_MIN_SIZE.y)
            }
            return DoubleRectangle(marginLeftTop, geomSize)
        }

        fun clipBounds(geomBounds: DoubleRectangle): DoubleRectangle {
            return DoubleRectangle(
                    geomBounds.origin.subtract(DoubleVector(CLIP_EXTEND, CLIP_EXTEND)),
                    DoubleVector(geomBounds.dimension.x + 2 * CLIP_EXTEND, geomBounds.dimension.y + 2 * CLIP_EXTEND))
        }
    }
}
