/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.GeomKind.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTarget
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupResult
import org.jetbrains.letsPlot.core.plot.builder.tooltip.MathUtil
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

        // TODO: take into account LookupSpace and LookupStrategy, i.e. first check XY target to fall into CUTOFF_DISTANCE
        // then check distance. This will allow to use bar-alike geoms to use their X lookup strategy and to not win
        // every distance checks as the distance between them and the cursor is an order of magnitude smaller than for XY
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

        private const val BAR_TARGETS_MAX_COUNT = 5 // allowed number of visible tooltips
        private val BAR_GEOMS = setOf(BAR, HISTOGRAM)

        // more than 10 targets per layer is too much.
        // Seems like Lets-Plot was used by vis tools, not by humans. Limit tooltips count to 1.
        // User won't get much info from it anyway.
        private const val EXPECTED_TARGETS_MAX_COUNT = 10

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
                        .minOfOrNull { target -> MathUtil.distance(coord, target.tipLayoutHint.coord!!) }
                        ?: FAKE_DISTANCE
                }
            } else {
                distance
            }
        }

        private fun stackableResults(lft: LookupResult, rgt: LookupResult): Boolean {
            return lft.geomKind === rgt.geomKind && STACKABLE_GEOMS.contains(rgt.geomKind)
        }

        private fun LookupResult.withTargets(newTargets: List<GeomTarget>) = LookupResult(
            targets = newTargets,
            distance = distance,
            geomKind = geomKind,
            contextualMapping = contextualMapping,
            isCrosshairEnabled = isCrosshairEnabled
        )

        private fun filterResults(
            lookupResult: LookupResult,
            coord: DoubleVector?,
            flippedAxis: Boolean
        ): LookupResult {
            if (coord == null) return lookupResult

            val geomTargets = lookupResult.targets.filter { it.tipLayoutHint.coord != null }

            // for bar - if the number of targets exceeds the restriction value => use the closest one
            if (lookupResult.geomKind in BAR_GEOMS && geomTargets.size > BAR_TARGETS_MAX_COUNT
                || geomTargets.size > EXPECTED_TARGETS_MAX_COUNT // perf: when LP is used by vis tools with raw data
            ) {
                val closestTarget = geomTargets.minBy { target ->
                    MathUtil.distance(coord, target.tipLayoutHint.coord!!)
                }
                return lookupResult.withTargets(listOf(closestTarget))
            }

            if (lookupResult.geomKind !in setOf(DENSITY, HISTOGRAM, FREQPOLY, LINE, AREA, SEGMENT, CURVE, SPOKE, RIBBON)) {
                return lookupResult
            }

            fun xDistanceToCoord(target: GeomTarget): Double {
                val distance = target.tipLayoutHint.coord!!.subtract(coord)
                return when (flippedAxis) {
                    true -> distance.y
                    false -> distance.x
                }
            }

            // Get the closest targets and remove duplicates

            val minXDistanceToTarget = geomTargets
                .map(::xDistanceToCoord)
                .minByOrNull(::abs)

            val newTargets = geomTargets
                .filter { target -> xDistanceToCoord(target) == minXDistanceToTarget }
                .distinctBy(GeomTarget::hitIndex)

            return lookupResult.withTargets(newTargets)
        }
    }
}
