/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec

object PlotLayoutUtil {
    const val AXIS_TITLE_OUTER_MARGIN = 4.0
    const val AXIS_TITLE_INNER_MARGIN = 4.0
    private const val TITLE_V_MARGIN = 4.0
    private val LIVE_MAP_PLOT_PADDING = DoubleVector(10.0, 0.0)
    private val LIVE_MAP_PLOT_MARGIN = DoubleVector(10.0, 10.0)

    fun titleDimensions(text: String): DoubleVector {
        if (text.isEmpty()) {
            return DoubleVector.ZERO
        }

        val labelSpec = PlotLabelSpec.PLOT_TITLE
        return DoubleVector(
            labelSpec.width(text.length),
            labelSpec.height() + 2 * TITLE_V_MARGIN
        )
    }

    fun axisTitleDimensions(text: String): DoubleVector {
        if (text.isEmpty()) {
            return DoubleVector.ZERO
        }

        val labelSpec = PlotLabelSpec.AXIS_TITLE
        return DoubleVector(
            labelSpec.width(text.length),
            labelSpec.height()
        )
    }

    fun absoluteGeomBounds(origin: DoubleVector, plotLayoutInfo: PlotLayoutInfo): DoubleRectangle {
        require(plotLayoutInfo.tiles.isNotEmpty()) { "Plot is empty" }

        var result: DoubleRectangle? = null
        for (tile in plotLayoutInfo.tiles) {
            val geomBounds = tile.getAbsoluteGeomBounds(origin)
            result = result?.union(geomBounds) ?: geomBounds
        }
        return result!!
    }

    fun liveMapBounds(container: DoubleRectangle): DoubleRectangle {
        return DoubleRectangle(
            container.origin.add(LIVE_MAP_PLOT_PADDING),
            container.dimension.subtract(LIVE_MAP_PLOT_MARGIN)
        )
    }
}
