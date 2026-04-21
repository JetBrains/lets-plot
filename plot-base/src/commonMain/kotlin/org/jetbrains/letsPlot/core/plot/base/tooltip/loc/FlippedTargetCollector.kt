/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint

internal class FlippedTargetCollector(
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
            point.flip(),
            radius,
            tooltipParams,
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
            rectangle.flip(),
            tooltipParams,
            tooltipPlacement,
            tooltipAnchor?.flip()
        )
    }

    override fun addPath(
        points: List<DoubleVector>,
        localToGlobalIndex: (Int) -> Int,
        tooltipParams: GeomTargetCollector.TooltipParams,
        tooltipPlacement: TooltipHint.Placement
    ) {
        val pointsWithIndex = points.map(DoubleVector::flip).withIndex().reversed()
        val indices = pointsWithIndex.map {
            localToGlobalIndex(it.index)
        }
        targetCollector.addPath(
            pointsWithIndex.map(IndexedValue<DoubleVector>::value),
            { indices[it] },
            tooltipParams,
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
            points.map(DoubleVector::flip),
            index,
            tooltipParams,
            tooltipPlacement
        )
    }

    override fun withFlippedAxis(): GeomTargetCollector {
        throw IllegalStateException("'withFlippedAxis()' is not applicable to FlippedTargetCollector")
    }

    override fun withYOrientation(): GeomTargetCollector {
        check(targetCollector !is YOrientationTargetCollector) { "'withYOrientation()' is not applicable to YOrientationTargetCollector" }
        return YOrientationTargetCollector(this)
    }
}