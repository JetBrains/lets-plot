/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.distance
import org.jetbrains.letsPlot.core.plot.base.GeomKind.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTarget
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupResult
import org.jetbrains.letsPlot.core.plot.base.tooltip.HitShape
import kotlin.math.abs

class LocatedTargetsPicker(
    private val flippedAxis: Boolean,
    private val cursorCoord: DoubleVector
) {
    private val allLookupResults = ArrayList<LookupResult>()

    fun addLookupResult(result: LookupResult) {
        // for bar - if the number of targets exceeds the restriction value => use the closest one
        val lookupResult = if (result.geomKind in setOf(BAR, HISTOGRAM) && result.targets.size > BAR_TARGETS_MAX_COUNT
            || result.targets.size > EXPECTED_TARGETS_MAX_COUNT // perf: when LP is used by vis tools with raw data
        ) {
            val closestTarget = result.targets
                .minBy { target -> cursorCoord.distanceTo(target.tipLayoutHint.coord) }

            result.copy(targets = listOf(closestTarget))
        } else if (result.geomKind in setOf(DENSITY, HISTOGRAM, FREQPOLY, LINE, AREA, SEGMENT, SPOKE, RIBBON)) {
            // Get the closest targets and remove duplicates
            val minXDistanceToTarget = result.targets
                .map { target -> xDistanceToCoord(target, cursorCoord, flippedAxis) }
                .minByOrNull(::abs)

            val newTargets = result.targets
                .filter { target -> xDistanceToCoord(target, cursorCoord, flippedAxis) == minXDistanceToTarget }
                .distinctBy(GeomTarget::hitIndex)

            result.copy(targets = newTargets)
        } else {
            result
        }

        allLookupResults.add(lookupResult)
    }

    fun chooseBestResult(): List<LookupResult> {
        // TODO: take into account LookupSpace and LookupStrategy, i.e. first check XY target to fall into CUTOFF_DISTANCE
        // then check distance. This will allow to use bar-alike geoms to use their X lookup strategy and to not win
        // every distance checks as the distance between them and the cursor is an order of magnitude smaller than for XY
        val withDistances = allLookupResults
            .map { lookupResult -> lookupResult to distance(lookupResult, cursorCoord) }
            .filter { (lookupResult, distance) ->
                lookupResult.isCrosshairEnabled || distance <= CUTOFF_DISTANCE
            }

        val minDistance = withDistances.minByOrNull { (_, distance) -> distance }?.second ?: 0.0

        var candidates = listOf<LookupResult>()
        withDistances
            .filter { (_, distance) -> distance == minDistance }
            .map { (lookupResult, _) -> lookupResult }
            .forEach { lookupResult ->
                candidates = when {
                    // TEXT tooltips are considered only when no other tooltips are present.
                    // Otherwise, TEXT layer is used as decoration, e.g. values of bars, histograms, corrplot,
                    // and we actually want to see ancestors geom tooltip.
                    candidates.isNotEmpty() && lookupResult.geomKind in listOf(TEXT, LABEL) -> candidates

                    candidates.isNotEmpty() && stackableResults(candidates[0], lookupResult) -> candidates + lookupResult

                    else -> listOf(lookupResult)
                }
            }

        val allConsideredResults = withDistances.map { (lookupResult, _) -> lookupResult }

        val picked = when {
            candidates.any { it.hasGeneralTooltip && hasAxisTooltip(it) } -> candidates
            allConsideredResults.none { it.hasGeneralTooltip } -> candidates
            allConsideredResults.any { it.hasGeneralTooltip && hasAxisTooltip(it) } -> {
                listOf(
                    withDistances
                        .sortedByDescending { (_, distance) -> distance }
                        .map { (lookupResult, _) -> lookupResult }
                        .last { it.hasGeneralTooltip && hasAxisTooltip(it) }
                )
            }

            else -> {
                with(
                    withDistances
                        .sortedByDescending { (_, distance) -> distance }
                        .map { (lookupResult, _) -> lookupResult }
                ) {
                    listOfNotNull(
                        lastOrNull { it.hasGeneralTooltip },
                        lastOrNull(::hasAxisTooltip)
                    )
                }
            }
        }

        return expandWithGroupTooltips(picked)
    }

    private fun expandWithGroupTooltips(picked: List<LookupResult>): List<LookupResult> {
        val groupIds = picked.mapNotNull { it.tooltipGroup }.toSet()

        return allLookupResults.filter { result ->
            result in picked || result.tooltipGroup in groupIds
        }
    }

    companion object {
        internal const val CUTOFF_DISTANCE = 30.0
        internal const val FAKE_DISTANCE = 15.0

        private const val BAR_TARGETS_MAX_COUNT = 5 // allowed number of visible tooltips

        // more than 10 targets per layer is too much.
        // Seems like Lets-Plot was used by vis tools, not by humans. Limit tooltips count to 1.
        // User won't get much info from it anyway.
        private const val EXPECTED_TARGETS_MAX_COUNT = 10

        private fun distance(lookupResult: LookupResult, coord: DoubleVector): Double {
            // Special case for geoms like histogram, when mouse inside a rect or only X projection is used (so a distance
            // between cursor is zero). Fake the distance to give a chance for tooltips from other layers.
            return when {
                lookupResult.distance != 0.0 -> lookupResult.distance
                lookupResult.isCrosshairEnabled -> {
                    // use XY distance for tooltips with crosshair to avoid giving them priority
                    lookupResult.targets
                        .minOfOrNull { target -> distance(coord, target.tipLayoutHint.coord) }
                        ?: FAKE_DISTANCE
                }

                lookupResult.hitShapeKind == HitShape.Kind.POINT -> 0.0 // Points are small; on hovering over them, we don't want to give priority to other tooltips by faking distance.
                else -> FAKE_DISTANCE // fake distance to give a chance for tooltips from other layers
            }
        }

        private fun stackableResults(lft: LookupResult, rgt: LookupResult): Boolean {
            return lft.tooltipGroup != null && lft.tooltipGroup == rgt.tooltipGroup
        }

        fun hasAxisTooltip(lookupResult: LookupResult): Boolean {
            return lookupResult.hasAxisTooltip ||
                    // actually hline/vline have axis info in the general tooltip
                    lookupResult.geomKind in listOf(V_LINE, H_LINE)
        }

        fun xDistanceToCoord(target: GeomTarget, coord: DoubleVector, flippedAxis: Boolean): Double {
            val distance = target.tipLayoutHint.coord.subtract(coord)
            return when (flippedAxis) {
                true -> distance.y
                false -> distance.x
            }
        }
    }
}
