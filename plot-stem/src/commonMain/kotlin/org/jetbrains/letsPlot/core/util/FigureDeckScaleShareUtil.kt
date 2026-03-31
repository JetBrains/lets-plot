/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.CompositeFigureDeckLayout
import org.jetbrains.letsPlot.core.spec.config.OptionsAccessor
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontendUtil

internal object FigureDeckScaleShareUtil {
    fun getSharedDomains(
        elementConfigs: List<OptionsAccessor?>,
        deckLayout: CompositeFigureDeckLayout
    ): Pair<List<DoubleSpan?>, List<DoubleSpan?>> {

        val continuousXYDomainByElement = elementConfigs.map {
            when {
                isApplicableElement(it) -> {
                    it as PlotConfigFrontend
                    val plotGeomTiles = PlotConfigFrontendUtil.createPlotGeomTiles(it)
                    plotGeomTiles.overallXYContinuousDomains()
                }

                else -> Pair(null, null)
            }
        }

        val isApplicable = elementConfigs.map { isApplicableElement(it) }

        val indicesWithSharedXAxis = deckLayout.indicesWithSharedXAxis(elementConfigs.size)
        val domainXByElement = continuousXYDomainByElement.map { it.first }
        val jointDomainXByElement = joinDomains(domainXByElement, indicesWithSharedXAxis, isApplicable)

        val indicesWithSharedYAxis = deckLayout.indicesWithSharedYAxis(elementConfigs.size)
        val domainYByElement = continuousXYDomainByElement.map { it.second }
        val jointDomainYByElement = joinDomains(domainYByElement, indicesWithSharedYAxis, isApplicable)

        return Pair(jointDomainXByElement, jointDomainYByElement)
    }

    private fun isApplicableElement(element: OptionsAccessor?): Boolean {
        return element?.let {
            it is PlotConfigFrontend &&
                    !(it.facets.isDefined || it.containsLiveMap)
        } ?: false
    }

    private fun joinDomains(
        domainByElement: List<DoubleSpan?>,
        groupIndicesList: List<List<Int>>,
        isElementApplicable: List<Boolean>,
    ): List<DoubleSpan?> {

        val jointDomainByElement = MutableList<DoubleSpan?>(domainByElement.size) { null }

        for (groupIndices in groupIndicesList) {
            val jointDomain = domainByElement
                .slice(groupIndices)
                .filterNotNull()
                .reduceOrNull { span0, span1 -> span0.union(span1) }

            for (i in groupIndices) {
                if (isElementApplicable[i]) {
                    domainByElement[i]?.let {
                        jointDomainByElement[i] = jointDomain
                    }
                }
            }
        }

        return jointDomainByElement
    }
}
