package jetbrains.datalore.visualization.plot.gog.plot.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.plot.guide.Orientation

internal object XYPlotLayoutUtil {
    private val GEOM_MARGIN = 10.0          // min space around geom area

    fun maxTickLabelsBounds(axisOrientation: Orientation, stretch: Double, geomBounds: DoubleRectangle, plotSize: DoubleVector): DoubleRectangle {
        val maxGeomBounds = DoubleRectangle(GEOM_MARGIN, GEOM_MARGIN, plotSize.x - 2 * GEOM_MARGIN, plotSize.y - 2 * GEOM_MARGIN)
        when (axisOrientation) {
            Orientation.TOP, Orientation.BOTTOM -> {
                val leftSpace = geomBounds.left - maxGeomBounds.left + stretch
                val rightSpace = maxGeomBounds.right - geomBounds.right + stretch

                val height = java.lang.Double.MAX_VALUE / 2   // just very large number
                val top = if (axisOrientation === Orientation.TOP)
                    -height
                else
                    0.0

                val left = -leftSpace
                val width = leftSpace + rightSpace + geomBounds.width
                return DoubleRectangle(left, top, width, height)
            }

            else -> throw IllegalArgumentException("Orientation not supported: $axisOrientation")
        }
    }
}
