/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTarget
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint

abstract class TransformedTargetLocator(private val targetLocator: GeomTargetLocator) :
    GeomTargetLocator {

    override fun search(coord: DoubleVector): GeomTargetLocator.LookupResult? {
        val targetCoord = convertToTargetCoord(coord)
        val result = targetLocator.search(targetCoord) ?: return null
        return convertLookupResult(result)
    }

    private fun convertLookupResult(lookupResult: GeomTargetLocator.LookupResult): GeomTargetLocator.LookupResult {
        return GeomTargetLocator.LookupResult(
            convertGeomTargets(lookupResult.targets),
            convertToPlotDistance(lookupResult.distance),
            lookupResult.geomKind,
            lookupResult.contextualMapping,
            lookupResult.contextualMapping.isCrosshairEnabled
        )
    }

    private fun convertGeomTargets(geomTargets: List<GeomTarget>): List<GeomTarget> {
        return geomTargets.map { geomTarget ->
            GeomTarget(
                geomTarget.hitIndex,
                convertTipLayoutHint(geomTarget.tipLayoutHint),
                convertTipLayoutHints(geomTarget.aesTipLayoutHints)
            )
        }
    }

    private fun convertTipLayoutHint(hint: TipLayoutHint): TipLayoutHint {
        return TipLayoutHint(
            hint.kind,
            safeConvertToPlotCoord(hint.coord)!!,
            convertToPlotDistance(hint.objectRadius),
            hint.stemLength,
            hint.fillColor,
            hint.markerColors
        )
    }

    private fun convertTipLayoutHints(tipLayoutHints: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, TipLayoutHint>): Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, TipLayoutHint> {
        val result = HashMap<org.jetbrains.letsPlot.core.plot.base.Aes<*>, TipLayoutHint>()
        tipLayoutHints.forEach { (aes, hint) -> result[aes] = convertTipLayoutHint(hint) }
        return result
    }

    private fun safeConvertToPlotCoord(coord: DoubleVector?): DoubleVector? {
        return if (coord == null) null else convertToPlotCoord(coord)
    }

    protected abstract fun convertToTargetCoord(coord: DoubleVector): DoubleVector

    protected abstract fun convertToPlotCoord(coord: DoubleVector): DoubleVector

    protected abstract fun convertToPlotDistance(distance: Double): Double
}
