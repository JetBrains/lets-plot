/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.base.gcommon.base.Strings.isNullOrEmpty
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import kotlin.math.max

object PlotLayoutUtil {
    val AXIS_TITLE_OUTER_MARGIN = 4.0
    val AXIS_TITLE_INNER_MARGIN = 4.0
    private val TITLE_V_MARGIN = 4.0
    private val LIVE_MAP_PLOT_PADDING = DoubleVector(10.0, 0.0)
    private val LIVE_MAP_PLOT_MARGIN = DoubleVector(10.0, 10.0)

    fun titleDimensions(text: String): DoubleVector {
        if (isNullOrEmpty(text)) {
            return DoubleVector.ZERO
        }

        val labelSpec = PlotLabelSpec.PLOT_TITLE
        return DoubleVector(
                labelSpec.width(text.length),
                labelSpec.height() + 2 * TITLE_V_MARGIN
        )
    }

    fun titleBounds(titleSize: DoubleVector, containerSize: DoubleVector): DoubleRectangle {
        val titleBoxLeft = max(0.0, (containerSize.x - titleSize.x) / 2)
        val titleBoxTop = 0.0
        return DoubleRectangle(titleBoxLeft, titleBoxTop, titleSize.x, titleSize.y)
    }

    fun axisTitleDimensions(text: String): DoubleVector {
        if (isNullOrEmpty(text)) {
            return DoubleVector.ZERO
        }

        val labelSpec = PlotLabelSpec.AXIS_TITLE
        return DoubleVector(
                labelSpec.width(text.length),
                labelSpec.height())
    }

    fun absoluteGeomBounds(origin: DoubleVector, plotLayoutInfo: PlotLayoutInfo): DoubleRectangle {
        Preconditions.checkArgument(!plotLayoutInfo.tiles.isEmpty(), "Plot is empty")

        var result: DoubleRectangle? = null
        for (tile in plotLayoutInfo.tiles) {
            val geomBounds = tile.getAbsoluteGeomBounds(origin)
            result = result?.union(geomBounds) ?: geomBounds
        }
        return result!!
    }

    fun liveMapBounds(plotOrigin: DoubleVector, plotDimension: DoubleVector): DoubleRectangle {
        return DoubleRectangle(
            plotOrigin.add(LIVE_MAP_PLOT_PADDING),
            plotDimension.subtract(LIVE_MAP_PLOT_MARGIN)
        )
    }
}
