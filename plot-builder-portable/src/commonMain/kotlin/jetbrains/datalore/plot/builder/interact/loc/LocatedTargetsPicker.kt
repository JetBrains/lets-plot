/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.GeomKind.*
import org.jetbrains.letsPlot.core.plot.base.interact.GeomTarget
import org.jetbrains.letsPlot.core.plot.base.interact.GeomTargetLocator.LookupResult
import jetbrains.datalore.plot.builder.interact.MathUtil
import kotlin.math.abs

class LocatedTargetsPicker(
    val flippedAxis: Boolean,
    private val myCursorCoord: DoubleVector? = null
) {
    private val myAllLookupResults = ArrayList<LookupResult>()

    val picked: List<LookupResult>
        get() = chooseBestResult()

    fun addLookupResult(result: LookupResult) {
        val lookupResult = filterResults(result, myCursorCoord, flippedAxis)
        myAllLookupResults.add(lookupResult)
    }

    private fun chooseBestResult(): List<LookupResult> {
        fun hasGeneralTooltip(lookupResult: LookupResult) = lookupResult.contextualMapping.hasGeneralTooltip
        fun hasAxisTooltip(lookupResult: LookupResult): Boolean {
            return lookupResult.contextualMapping.hasAxisTooltip ||
                    // actually hline/vline have axis info in the general tooltip
                    lookupResult.geomKind in listOf(V_LINE, H_LINE)
        }

        val withDistances = myAllLookupResults
            .map { lookupResult -> lookupResult to distance(lookupResult, myCursorCoord) }
            .filter { (lookupResult, distance) ->
                lookupResult.isCrosshairEnabled || distance <= CUTOFF_DISTANCE
            }

        val minDistance = withDistances.minByOrNull { (_, distance) -> distance }?.second ?: 0.0

        var picked = listOf<LookupResult>()
        withDistances
            .filter { (_, distance) -> distance == minDistance }
            .map { (lookupResult, _) -> lookupResult }
            .forEach { lookupResult ->
                picked = when {
                    picked.isNotEmpty() && lookupResult.geomKind in listOf(TEXT, LABEL) -> {
                        // TEXT tooltips are considered only when no other tooltips are present.
                        // Otherwise, TEXT layer is used as decoration, e.g. values of bars, histograms, corrplot,
                        // and we actually want to see ancestors geom tooltip.
                        picked
                    }
                    picked.isNotEmpty() && stackableResults(picked[0], lookupResult) -> {
                        picked + lookupResult
                    }
                    else -> {
                        listOf(lookupResult)
                    }
                }
            }

        val allConsideredResults = withDistances.map { (lookupResult, _) -> lookupResult }

        return when {
            picked.any { hasGeneralTooltip(it) && hasAxisTooltip(it) } -> picked
            allConsideredResults.none(::hasGeneralTooltip) -> picked
            allConsideredResults.any { hasGeneralTooltip(it) && hasAxisTooltip(it) } -> {
                listOf(
                    withDistances
                        .sortedByDescending { (_, distance) -> distance }
                        .map { (lookupResult, _) -> lookupResult }
                        .last { hasGeneralTooltip(it) && hasAxisTooltip(it) }
                )
            }
            else -> {
                with(
                    withDistances
                        .sortedByDescending { (_, distance) -> distance }
                        .map { (lookupResult, _) -> lookupResult }
                ) {
                    listOfNotNull(
                        lastOrNull(::hasGeneralTooltip),
                        lastOrNull(::hasAxisTooltip)
                    )
                }
            }
        }
    }

    companion object {
        internal const val CUTOFF_DISTANCE = 30.0
        internal const val FAKE_DISTANCE = 15.0

        // Consider layers with the same geom as a single layer to join their tooltips
        private val STACKABLE_GEOMS = setOf(
            DENSITY,
            FREQPOLY,
            BOX_PLOT,
            HISTOGRAM,
            LINE,
            AREA,
            BAR,
            ERROR_BAR,
            CROSS_BAR,
            LINE_RANGE,
            POINT_RANGE
        )

        private fun distance(locatedTargetList: LookupResult, coord: DoubleVector?): Double {
            val distance = locatedTargetList.distance
            // Special case for geoms like histogram, when mouse inside a rect or only X projection is used (so a distance
            // between cursor is zero). Fake the distance to give a chance for tooltips from other layers.
            return if (distance == 0.0) {
                if (!locatedTargetList.isCrosshairEnabled || coord == null) {
                    FAKE_DISTANCE
                } else {
                    // use XY distance for tooltips with crosshair to avoid giving them priority
                    locatedTargetList.targets
                        .filter { it.tipLayoutHint.coord != null }
                        .map { target -> MathUtil.distance(coord, target.tipLayoutHint.coord!!) }
                        .minOrNull()
                        ?: FAKE_DISTANCE
                }
            } else {
                distance
            }
        }

        private fun stackableResults(lft: LookupResult, rgt: LookupResult): Boolean {
            return lft.geomKind === rgt.geomKind && STACKABLE_GEOMS.contains(rgt.geomKind)
        }

        private fun filterResults(lookupResult: LookupResult, coord: DoubleVector?, flippedAxis: Boolean): LookupResult {
            if (coord == null || lookupResult.geomKind !in setOf(DENSITY, HISTOGRAM, FREQPOLY, LINE, AREA, SEGMENT)) {
                return lookupResult
            }

            fun xDistanceToCoord(target: GeomTarget): Double {
                val distance = target.tipLayoutHint.coord!!.subtract(coord)
                return when (flippedAxis) {
                    true -> distance.y
                    false -> distance.x
                }
            }

            // Get closest targets and remove duplicates
            val geomTargets = lookupResult.targets.filter { it.tipLayoutHint.coord != null }

            val minXDistanceToTarget = geomTargets
                .map(::xDistanceToCoord)
                .minByOrNull(::abs)

            val newTargets = geomTargets
                .filter { target -> xDistanceToCoord(target) == minXDistanceToTarget }
                .distinctBy(GeomTarget::hitIndex)

            return LookupResult(
                targets = newTargets,
                distance = lookupResult.distance,
                geomKind = lookupResult.geomKind,
                contextualMapping = lookupResult.contextualMapping,
                isCrosshairEnabled = lookupResult.isCrosshairEnabled
            )
        }
    }
}
