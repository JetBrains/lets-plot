/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.base.gcommon.collect.Lists
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.GeomTarget
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.TipLayoutHint

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
                lookupResult.contextualMapping
        )
    }

    private fun convertGeomTargets(geomTargets: List<GeomTarget>): List<GeomTarget> {
        return ArrayList(Lists.transform(geomTargets) { geomTarget ->
            GeomTarget(
                geomTarget.hitIndex,
                convertTipLayoutHint(geomTarget.tipLayoutHint),
                convertTipLayoutHints(geomTarget.aesTipLayoutHints)
            )
        })
    }

    private fun convertTipLayoutHint(hint: TipLayoutHint): TipLayoutHint {
        return TipLayoutHint(
            hint.kind,
            safeConvertToPlotCoord(hint.coord)!!,
            convertToPlotDistance(hint.objectRadius),
            hint.color,
            hint.stemLength
        )
    }

    private fun convertTipLayoutHints(tipLayoutHints: Map<Aes<*>, TipLayoutHint>): Map<Aes<*>, TipLayoutHint> {
        val result = HashMap<Aes<*>, TipLayoutHint>()
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
