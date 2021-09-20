/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.FeatureSwitch.FLIP_AXIS
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupResult
import jetbrains.datalore.plot.builder.interact.MathUtil
import kotlin.math.abs

internal class LocatedTargetsPicker {
    private val myPicked = ArrayList<LookupResult>()
    private var myMinDistance = 0.0
    private val myAllLookupResults = ArrayList<LookupResult>()

    val picked: List<LookupResult>
        get() = chooseBestResult()

    fun addLookupResult(result: LookupResult, coord: DoubleVector? = null) {
        val lookupResult = filterResults(result, coord)

        val distance = distance(lookupResult, coord)
        if (!lookupResult.isCrosshairEnabled && distance > CUTOFF_DISTANCE) {
            return
        }

        when {
            myPicked.isEmpty() || myMinDistance > distance -> {
                myPicked.clear()
                myPicked.add(lookupResult)
                myMinDistance = distance
            }
            myMinDistance == distance && isSameUnivariateGeom(myPicked[0], lookupResult) -> {
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
                    lookupResult.geomKind in listOf(GeomKind.V_LINE, GeomKind.H_LINE)
        }

        return when {
            myPicked.any { hasGeneralTooltip(it) && hasAxisTooltip(it) } -> myPicked
            myAllLookupResults.none { hasGeneralTooltip(it) } -> myPicked
            myAllLookupResults.any { hasGeneralTooltip(it) && hasAxisTooltip(it) } -> {
                listOf(myAllLookupResults.last { hasGeneralTooltip(it) && hasAxisTooltip(it) })
            }
            else -> {
                val withGeneralTooltip = myAllLookupResults.lastOrNull { hasGeneralTooltip(it) }
                val withAxisTooltip = myAllLookupResults.lastOrNull { hasAxisTooltip(it) }
                listOfNotNull(withGeneralTooltip, withAxisTooltip)
            }
        }
    }

    companion object {
        internal const val CUTOFF_DISTANCE = 30.0
        internal const val FAKE_DISTANCE = 15.0
        private val UNIVARIATE_GEOMS = listOf(
            GeomKind.DENSITY,
            GeomKind.FREQPOLY,
            GeomKind.BOX_PLOT,
            GeomKind.HISTOGRAM,
            GeomKind.LINE,
            GeomKind.AREA,
            GeomKind.BAR,
            GeomKind.ERROR_BAR,
            GeomKind.CROSS_BAR,
            GeomKind.LINE_RANGE,
            GeomKind.POINT_RANGE
        )

        private val UNIVARIATE_LINES = listOf(
            GeomKind.DENSITY,
            GeomKind.FREQPOLY,
            GeomKind.LINE,
            GeomKind.AREA,
            GeomKind.SEGMENT
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

        private fun isSameUnivariateGeom(lft: LookupResult, rgt: LookupResult): Boolean {
            return lft.geomKind === rgt.geomKind && UNIVARIATE_GEOMS.contains(rgt.geomKind)
        }

        private fun filterResults(lookupResult: LookupResult, coord: DoubleVector?): LookupResult {
            if (coord == null || lookupResult.geomKind !in UNIVARIATE_LINES) {
                return lookupResult
            }

            val getCoord = if (FLIP_AXIS) {
                { point: DoubleVector -> point.y }
            } else {
                { point: DoubleVector -> point.x }
            }

            // Get closest targets and remove duplicates
            val geomTargets = lookupResult.targets.filter { it.tipLayoutHint.coord != null }

            val minXToTarget = geomTargets
                .map { target -> getCoord(target.tipLayoutHint.coord!!.subtract(coord)) }
                .minByOrNull { abs(it) }

            val newTargets = geomTargets
                .filter { target ->
                    getCoord(target.tipLayoutHint.coord!!.subtract(coord)) == minXToTarget
                }
                .distinctBy { it.hitIndex }

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
