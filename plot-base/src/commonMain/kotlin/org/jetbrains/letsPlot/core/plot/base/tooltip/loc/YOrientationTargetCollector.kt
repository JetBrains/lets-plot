/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint
import org.jetbrains.letsPlot.core.plot.base.util.YOrientationBaseUtil.flipAesKeys

internal class YOrientationTargetCollector(
    private val targetCollector: GeomTargetCollector
) : GeomTargetCollector {

    override fun addPoint(
        index: Int,
        point: DoubleVector,
        radius: Double,
        tooltipParams: GeomTargetCollector.TooltipParams,
        tooltipPlacement: TooltipHint.Placement
    ) {
        targetCollector.addPoint(
            index,
            point,
            radius,
            afterYOrientation(tooltipParams),
            tooltipPlacement
        )
    }

    override fun addRectangle(
        index: Int,
        rectangle: DoubleRectangle,
        tooltipParams: GeomTargetCollector.TooltipParams,
        tooltipPlacement: TooltipHint.Placement,
        tooltipAnchor: DoubleVector?
    ) {
        targetCollector.addRectangle(
            index,
            rectangle,
            afterYOrientation(tooltipParams),
            tooltipPlacement,
            tooltipAnchor
        )
    }

    override fun addPath(
        points: List<DoubleVector>,
        localToGlobalIndex: (Int) -> Int,
        tooltipParams: GeomTargetCollector.TooltipParams,
        tooltipPlacement: TooltipHint.Placement
    ) {
        targetCollector.addPath(
            points,
            localToGlobalIndex,
            afterYOrientation(tooltipParams),
            tooltipPlacement
        )
    }

    override fun addPolygon(
        points: List<DoubleVector>,
        index: Int,
        tooltipParams: GeomTargetCollector.TooltipParams,
        tooltipPlacement: TooltipHint.Placement
    ) {
        targetCollector.addPolygon(
            points,
            index,
            afterYOrientation(tooltipParams),
            tooltipPlacement
        )
    }

    override fun withFlippedAxis(): GeomTargetCollector {
        check(targetCollector !is FlippedTargetCollector) { "'withFlippedAxis()' is not applicable to FlippedTargetCollector" }
        return FlippedTargetCollector(this)
    }

    override fun withYOrientation(): GeomTargetCollector {
        throw IllegalStateException("'withYOrientation()' is not applicable to YOrientationTargetCollector")
    }

    companion object {
        private fun afterYOrientation(tooltipParams: GeomTargetCollector.TooltipParams): GeomTargetCollector.TooltipParams {
            return GeomTargetCollector.TooltipParams(
                tooltipHints = flipAesKeys(tooltipParams.tooltipHints),
                stemLength = tooltipParams.stemLength,
                fillColorFactory = tooltipParams.fillColorFactory,
                markerColorsFactory = tooltipParams.markerColorsFactory
            )
        }
    }
}