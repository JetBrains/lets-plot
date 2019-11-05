/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

internal object XYPlotLayoutUtil {
    private const val GEOM_MARGIN = 10.0          // min space around geom area

    fun maxTickLabelsBounds(axisOrientation: jetbrains.datalore.plot.builder.guide.Orientation, stretch: Double, geomBounds: DoubleRectangle, plotSize: DoubleVector): DoubleRectangle {
        val maxGeomBounds = DoubleRectangle(GEOM_MARGIN, GEOM_MARGIN, plotSize.x - 2 * GEOM_MARGIN, plotSize.y - 2 * GEOM_MARGIN)
        when (axisOrientation) {
            jetbrains.datalore.plot.builder.guide.Orientation.TOP, jetbrains.datalore.plot.builder.guide.Orientation.BOTTOM -> {
                val leftSpace = geomBounds.left - maxGeomBounds.left + stretch
                val rightSpace = maxGeomBounds.right - geomBounds.right + stretch

                val height = Double.MAX_VALUE / 2   // just very large number
                val top = if (axisOrientation === jetbrains.datalore.plot.builder.guide.Orientation.TOP)
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
