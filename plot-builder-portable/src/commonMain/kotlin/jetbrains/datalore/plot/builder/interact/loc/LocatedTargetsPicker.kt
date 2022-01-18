/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.GeomKind.*
import jetbrains.datalore.plot.base.interact.GeomTarget
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupResult
import jetbrains.datalore.plot.builder.interact.MathUtil
import kotlin.math.abs

class LocatedTargetsPicker(
    val flippedAxis: Boolean
) {
    private val myPicked = ArrayList<LookupResult>()
    private var myMinDistance = 0.0
    private val myAllLookupResults = ArrayList<LookupResult>()

    val picked: List<LookupResult>
        get() = chooseBestResult()

    fun addLookupResult(result: LookupResult, coord: DoubleVector? = null) {
        val lookupResult = filterResults(result, coord, flippedAxis)

        val distance = distance(lookupResult, coord)
        if (!lookupResult.isCrosshairEnabled && distance > CUTOFF_DISTANCE) {
            return
        }

        when {
            myPicked.isNotEmpty() && lookupResult.geomKind == TEXT -> {
                // TEXT tooltips are considered only when no other tooltips are present.
                // Otherwise, TEXT layer is used as decoration, e.g. values of bars, histograms, corrplot,
                // and we actually want to see acnestors geom tooltip.
            }
            myPicked.isEmpty() || myMinDistance > distance -> {
                myPicked.clear()
                myPicked.add(lookupResult)
                myMinDistance = distance
            }
            myMinDistance == distance && stackableResults(myPicked[0], lookupResult) -> {
                myPicked.add(lookupResult)
            }
            myMinDistance == distance -> {
                myPicked.clear()
                myPicked.add(lookupResult)
            }
        }
        myAllLookupResults.add(lookupResult)
    }

    private fun chooseBestResult(): List<LookupResult> {
        fun hasGeneralTooltip(lookupResult: LookupResult) = lookupResult.contextualMapping.hasGeneralTooltip
        fun hasAxisTooltip(lookupResult: LookupResult): Boolean {
            return lookupResult.contextualMapping.hasAxisTooltip ||
                    // actually hline/vline have axis info in the general tooltip
                    lookupResult.geomKind in listOf(V_LINE, H_LINE)
        }

        return when {
            myPicked.any { hasGeneralTooltip(it) && hasAxisTooltip(it) } -> myPicked
            myAllLookupResults.none(::hasGeneralTooltip) -> myPicked
            myAllLookupResults.any { hasGeneralTooltip(it) && hasAxisTooltip(it) } -> {
                listOf(myAllLookupResults.last { hasGeneralTooltip(it) && hasAxisTooltip(it) })
            }
            else -> {
                val withGeneralTooltip = myAllLookupResults.lastOrNull(::hasGeneralTooltip)
                val withAxisTooltip = myAllLookupResults.lastOrNull(::hasAxisTooltip)
                listOfNotNull(withGeneralTooltip, withAxisTooltip)
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
