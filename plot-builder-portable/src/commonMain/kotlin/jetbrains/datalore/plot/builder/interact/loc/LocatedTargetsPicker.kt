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
    private val myAllLookupResults = ArrayList<LookupResult>()

    fun picked(withGeneralTooltip: Boolean): List<LookupResult> = chooseBestResult(withGeneralTooltip)

    fun addLookupResult(lookupResult: LookupResult) {
        val distance = distance(lookupResult)
        if (distance > CUTOFF_DISTANCE) {
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

    private fun chooseBestResult(withGeneralTooltip: Boolean): List<LookupResult> {
        return when {
            !withGeneralTooltip -> myPicked
            myPicked.any { it.contextualMapping.hasGeneralTooltip } -> myPicked
            myAllLookupResults.none { it.contextualMapping.hasGeneralTooltip } -> myPicked
            else -> {
                listOf(myAllLookupResults.last { it.contextualMapping.hasGeneralTooltip })
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
