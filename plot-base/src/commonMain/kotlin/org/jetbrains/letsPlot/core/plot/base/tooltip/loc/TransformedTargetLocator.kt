/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTarget
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.LookupResult
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint

abstract class TransformedTargetLocator(
    private val targetLocator: GeomTargetLocator
) : GeomTargetLocator {

    override fun search(coord: DoubleVector): LookupResult? {
        val targetCoord = convertToTargetCoord(coord)
        val result = targetLocator.search(targetCoord) ?: return null

        return result.copy(
            targets = convertGeomTargets(result.targets),
            lookupDistance = convertToPlotDistance(result.lookupDistance),
            ownerDistance = convertToPlotDistance(result.ownerDistance)
        )
    }

    private fun convertGeomTargets(geomTargets: List<GeomTarget>): List<GeomTarget> {
        return geomTargets.map { geomTarget ->
            GeomTarget(
                geomTarget.hitIndex,
                convertTooltipHint(geomTarget.tooltipHint),
                convertTooltipHints(geomTarget.aesTooltipHint)
            )
        }
    }

    private fun convertTooltipHint(hint: TooltipHint): TooltipHint {
        return TooltipHint(
            hint.placement,
            safeConvertToPlotCoord(hint.coord),
            convertToPlotDistance(hint.objectRadius),
            hint.stemLength,
            hint.fillColor,
            hint.markerColors
        )
    }

    private fun convertTooltipHints(tooltipHint: Map<Aes<*>, TooltipHint>): Map<Aes<*>, TooltipHint> {
        val result = HashMap<Aes<*>, TooltipHint>()
        tooltipHint.forEach { (aes, hint) -> result[aes] = convertTooltipHint(hint) }
        return result
    }

    private fun safeConvertToPlotCoord(coord: DoubleVector): DoubleVector {
        return convertToPlotCoord(coord)
    }

    protected abstract fun convertToTargetCoord(coord: DoubleVector): DoubleVector

    protected abstract fun convertToPlotCoord(coord: DoubleVector): DoubleVector

    protected abstract fun convertToPlotDistance(distance: Double): Double
}
