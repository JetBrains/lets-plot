/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.filterNotNullValues
import org.jetbrains.letsPlot.commons.intern.removeDuplicates
import org.jetbrains.letsPlot.commons.values.Color.Companion.WHITE
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind.*
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.tooltip.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Placement.X_AXIS
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Placement.Y_AXIS
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LineSpec.DataPoint
import kotlin.math.abs

internal class LocatedTargetsPicker(
    private val flippedAxis: Boolean,
    private val cursorCoord: DoubleVector,
    private val axisOrigin: DoubleVector,
    private val xAxisTheme: AxisTheme,
    private val yAxisTheme: AxisTheme,
    private val ctx: PlotContext
) {
    private val allLookupResults = ArrayList<LookupResult>()

    fun addLookupResult(result: LookupResult) {
        // for bar - if the number of targets exceeds the restriction value => use the closest one
        val lookupResult = if (result.geomKind in setOf(BAR, HISTOGRAM) && result.targets.size > BAR_TARGETS_MAX_COUNT
            || result.targets.size > EXPECTED_TARGETS_MAX_COUNT // perf: when LP is used by vis tools with raw data
        ) {
            val closestTarget = result.targets
                .minBy { target -> cursorCoord.distanceTo(target.tooltipHint.coord) }

            result.copy(targets = listOf(closestTarget))
        } else if (result.lookupSpec.lookupSpace.isUnivariate() && result.hitShapeKind == HitShape.Kind.PATH) {
            // Univariate hover on grouped path-like geoms can return several hits from the same layer
            // because the cursor only has to match the lookup axis range.
            // Keep only the hits aligned with the cursor on that axis and drop duplicate hits for the same datum.
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

    fun chooseBestResult(): List<TooltipModel> {
        val bestLookupResults = chooseBestLookupResults()

        val tooltipModels = bestLookupResults.associateWith { chooseTooltipModels(it) }

        // Filter axis tooltips
        val xAxisTooltips = tooltipModels
            .mapValues { (_, tooltips) -> tooltips.singleOrNull { it.tooltipHint.placement == X_AXIS } }
            .filterNotNullValues()

        val yAxisTooltips = tooltipModels
            .mapValues { (_, tooltips) -> tooltips.singleOrNull { it.tooltipHint.placement == Y_AXIS } }
            .filterNotNullValues()

        val closestXAxisTooltip = xAxisTooltips.minByOrNull { (lookupResult, _) -> lookupResult.ownerDistance }
        val closestYAxisTooltip = yAxisTooltips.minByOrNull { (lookupResult, _) -> lookupResult.ownerDistance }

        val finalTooltips = tooltipModels.values.flatten() - xAxisTooltips.values - yAxisTooltips.values +
                closestXAxisTooltip?.value +
                closestYAxisTooltip?.value

        return finalTooltips.filterNotNull()
    }


    internal fun chooseBestLookupResults(): List<LookupResult> {
        val withDistances = allLookupResults
            .map { lookupResult -> lookupResult to distance(cursorCoord, lookupResult) }
            .filter { (lookupResult, distance) ->
                when {
                    lookupResult.isCrosshairEnabled -> true                          // crosshair always snaps to nearest
                    lookupResult.lookupSpec.lookupSpace.isUnivariate() -> true       // X-range containment is the trigger — 2D distance is used for ordering only, not for cutoff
                    else -> distance <= CUTOFF_DISTANCE                              // drop XY targets that are too far
                }
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
        val sortedResults = withDistances
            .sortedByDescending { (_, distance) -> distance }
            .map { (lookupResult, _) -> lookupResult }

        val picked = when {
            candidates.any { it.hasGeneralTooltip && hasAxisTooltip(it) } -> candidates
            allConsideredResults.none { it.hasGeneralTooltip } -> candidates
            allConsideredResults.any { it.hasGeneralTooltip && hasAxisTooltip(it) } -> {
                listOf(
                    sortedResults.last { it.hasGeneralTooltip && hasAxisTooltip(it) }
                )
            }

            else -> {
                listOfNotNull(
                    sortedResults.lastOrNull { it.hasGeneralTooltip },
                    sortedResults.lastOrNull(::hasAxisTooltip)
                )
            }
        }

        return expandWithGroupTooltips(picked)
    }

    private fun chooseTooltipModels(lookupResult: LookupResult): List<TooltipModel> {
        val tooltipModels = chooseTooltipModels(lookupResult.targets, lookupResult.contextualMapping)
            .filter { it.lines.isNotEmpty() }

        return tooltipModels
            .removeDuplicates { it.tooltipHint.placement == X_AXIS }
            .removeDuplicates { it.tooltipHint.placement == Y_AXIS }
    }

    internal fun chooseTooltipModels(
        geomTargets: List<GeomTarget>,
        contextualMapping: ContextualMapping
    ): List<TooltipModel> {
        val tooltipModels = ArrayList<TooltipModel>()
        geomTargets.forEach { geomTarget ->
            val dataPoints = contextualMapping.getDataPoints(geomTarget.hitIndex, ctx)
            tooltipModels += axisTooltipModels(geomTarget, dataPoints)
            tooltipModels += sideTooltipModels(geomTarget, dataPoints)
            tooltipModels += generalTooltipModels(geomTarget, contextualMapping, dataPoints)
        }
        return tooltipModels
    }

    private fun sideTooltipModels(
        geomTarget: GeomTarget,
        dataPoints: List<DataPoint>
    ): List<TooltipModel> {
        val tooltipModels = ArrayList<TooltipModel>()
        val sideDataPoints = sideDataPoints(dataPoints)

        geomTarget.aesTooltipHint.forEach { (aes, hint) ->
            val linesForAes = sideDataPoints
                .filter { aes == it.aes }
                .map(DataPoint::value)
                .map(TooltipModel.Line.Companion::withValue)
            if (linesForAes.isNotEmpty()) {
                tooltipModels.add(
                    TooltipModel(
                        tooltipHint = hint,
                        title = null,
                        lines = linesForAes,
                        fill = hint.fillColor ?: geomTarget.tooltipHint.fillColor
                            ?: geomTarget.tooltipHint.markerColors.firstOrNull() ?: WHITE,
                        markerColors = emptyList(),
                        isSide = true
                    )
                )
            }
        }

        return tooltipModels
    }

    private fun axisTooltipModels(
        geomTarget: GeomTarget,
        dataPoints: List<DataPoint>
    ): List<TooltipModel> {
        val tooltipModels = ArrayList<TooltipModel>()
        val axis = mapOf(
            Aes.X to axisDataPoints(dataPoints).filter { Aes.X == it.aes }
                .map(DataPoint::value)
                .map(TooltipModel.Line.Companion::withValue),
            Aes.Y to axisDataPoints(dataPoints).filter { Aes.Y == it.aes }
                .map(DataPoint::value)
                .map(TooltipModel.Line.Companion::withValue)
        )

        axis.forEach { (aes, lines) ->
            if (lines.isNotEmpty()) {
                val hint = createHintForAxis(aes, geomTarget.tooltipHint)
                tooltipModels.add(
                    TooltipModel(
                        tooltipHint = hint,
                        title = null,
                        lines = lines,
                        fill = hint.fillColor!!,
                        markerColors = emptyList(),
                        isSide = true
                    )
                )
            }
        }

        return tooltipModels
    }

    private fun generalTooltipModels(
        geomTarget: GeomTarget,
        contextualMapping: ContextualMapping,
        dataPoints: List<DataPoint>
    ): List<TooltipModel> {
        val generalLines = generalDataPoints(dataPoints).map {
            TooltipModel.Line.withLabelAndValue(it.label, it.value)
        }

        return if (generalLines.isNotEmpty()) {
            listOf(
                TooltipModel(
                    tooltipHint = geomTarget.tooltipHint,
                    title = contextualMapping.getTitle(geomTarget.hitIndex, ctx),
                    lines = generalLines,
                    fill = null,
                    markerColors = geomTarget.tooltipHint.markerColors,
                    isSide = false,
                    anchor = contextualMapping.tooltipAnchor,
                    minWidth = contextualMapping.tooltipMinWidth,
                    isCrosshairEnabled = contextualMapping.isCrosshairEnabled
                )
            )
        } else {
            emptyList()
        }
    }

    private fun sideDataPoints(dataPoints: List<DataPoint>) = dataPoints.filter { it.isSide && !it.isAxis }

    private fun axisDataPoints(dataPoints: List<DataPoint>) = dataPoints.filter(DataPoint::isAxis)

    private fun generalDataPoints(dataPoints: List<DataPoint>): List<DataPoint> {
        val nonSideDataPoints = dataPoints.filterNot(DataPoint::isSide)
        val sideAes = sideDataPoints(dataPoints).mapNotNull(DataPoint::aes)
        val generalAesList = nonSideDataPoints.mapNotNull(DataPoint::aes) - sideAes
        return nonSideDataPoints.filter { dataPoint ->
            when (dataPoint.aes) {
                null -> true
                in generalAesList -> true
                else -> false
            }
        }
    }

    private fun createHintForAxis(aes: Aes<*>, tooltipHint: TooltipHint): TooltipHint {
        val axis = when {
            flippedAxis && aes == Aes.X -> Aes.Y
            flippedAxis && aes == Aes.Y -> Aes.X
            else -> aes
        }

        return when (axis) {
            Aes.X -> TooltipHint.xAxisTooltip(
                coord = DoubleVector(tooltipHint.coord.x, axisOrigin.y),
                axisRadius = xAxisTheme.lineWidth() / 2,
                fillColor = xAxisTheme.tooltipFill()
            )

            Aes.Y -> TooltipHint.yAxisTooltip(
                coord = DoubleVector(axisOrigin.x, tooltipHint.coord.y),
                axisRadius = yAxisTheme.lineWidth() / 2,
                fillColor = yAxisTheme.tooltipFill()
            )

            else -> error("Not an axis aes: $axis")
        }
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

        private fun distance(cursor: DoubleVector, lookupResult: LookupResult): Double {
            // Special case for geoms like histogram, when mouse inside a rect or only X projection is used (so a distance
            // between cursor is zero). Fake the distance to give a chance for tooltips from other layers.
            return when {
                // HOVER over polygon/boxplot/histogram - give chance for points or lines to show tooltips
                (lookupResult.hitShapeKind == HitShape.Kind.POLYGON ||
                lookupResult.hitShapeKind == HitShape.Kind.RECT) &&
                        lookupResult.lookupDistance == 0.0 -> FAKE_DISTANCE
                lookupResult.isCrosshairEnabled -> {
                    lookupResult.targets
                        .minOfOrNull { target -> cursor.distanceTo(target.tooltipHint.coord) }
                        ?: FAKE_DISTANCE
                }
                else -> lookupResult.lookupDistance // fake distance to give a chance for tooltips from other layers
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
            val offset = target.tooltipHint.coord.subtract(coord)
            return offset.flipIf(flippedAxis).x
        }
    }
}

fun createTooltipModels(
    geomTarget: GeomTarget,
    contextualMapping: ContextualMapping,
    axisOrigin: DoubleVector,
    flippedAxis: Boolean,
    xAxisTheme: AxisTheme,
    yAxisTheme: AxisTheme,
    ctx: PlotContext
): List<TooltipModel> {
    return LocatedTargetsPicker(
        flippedAxis = flippedAxis,
        cursorCoord = DoubleVector.ZERO,
        axisOrigin = axisOrigin,
        xAxisTheme = xAxisTheme,
        yAxisTheme = yAxisTheme,
        ctx = ctx
    )
        .chooseTooltipModels(listOf(geomTarget), contextualMapping)
}
