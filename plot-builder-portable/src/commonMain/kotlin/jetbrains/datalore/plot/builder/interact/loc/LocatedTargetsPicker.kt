/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupResult

internal class LocatedTargetsPicker {
    private val myPicked = ArrayList<LookupResult>()
    private var myMinDistance = 0.0
    private var myNeedCheckTooltip = false
    private val myTargetCandidates = ArrayList<Pair<LookupResult, Int>>()

    val picked: List<LookupResult>
        get() = myPicked

    fun setNeedCheckTooltips(b: Boolean) {
        myNeedCheckTooltip = b
    }

    fun addLookupResult(lookupResult: LookupResult) {
        val distance = distance(lookupResult)
        if (distance > CUTOFF_DISTANCE) {
            return
        }

        when {
            myPicked.isEmpty() || myMinDistance > distance -> {
                myMinDistance = distance
                addNewCandidate(lookupResult, withSamePriority = false)
            }
            myMinDistance == distance && isSameUnivariateGeom(myPicked[0], lookupResult) -> {
                addNewCandidate(lookupResult, withSamePriority = true)
            }
            myMinDistance == distance -> {
                addNewCandidate(lookupResult, withSamePriority = false)
            }
        }
    }

    private fun addNewCandidate(lookupResult: LookupResult, withSamePriority: Boolean) {
        val maxPriority = myTargetCandidates.map(Pair<LookupResult, Int>::second).maxOrNull() ?: 0
        val newPriority = if (withSamePriority) maxPriority else maxPriority + 1
        myTargetCandidates.add(lookupResult to newPriority)

        //update picked
        myPicked.clear()
        chooseBestResult().forEach { myPicked.add(it) }
    }

    private fun chooseBestResult(): List<LookupResult> {
        if (myNeedCheckTooltip) {
            // try to find the result with general tooltip
            val resultWithGeneralTooltip = myTargetCandidates
                .map(Pair<LookupResult, Int>::first)
                .lastOrNull { lookupResult -> lookupResult.contextualMapping.hasGeneralTooltip() }
            if (resultWithGeneralTooltip != null) {
                return listOf(resultWithGeneralTooltip)
            }
        }

        // get the result with the max priority (the last added)
        val maxPriority = myTargetCandidates.map(Pair<LookupResult, Int>::second).maxOrNull() ?: 0
        return myTargetCandidates.filter { (_, priority) -> priority == maxPriority }.map(Pair<LookupResult, Int>::first)
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

        private fun distance(locatedTargetList: LookupResult): Double {
            val distance = locatedTargetList.distance
            // Special case for geoms like histogram, when mouse inside a rect or only X projection is used (so a distance
            // between cursor is zero). Fake the distance to give a chance for tooltips from other layers.
            return if (distance == 0.0) {
                FAKE_DISTANCE
            } else distance
        }

        private fun isSameUnivariateGeom(lft: LookupResult, rgt: LookupResult): Boolean {
            return lft.geomKind === rgt.geomKind && UNIVARIATE_GEOMS.contains(rgt.geomKind)
        }
    }
}
