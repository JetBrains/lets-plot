/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.CompositeFigureGridLayoutBase
import org.jetbrains.letsPlot.core.spec.config.OptionsAccessor
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontendUtil

internal object FigureGridScaleShareUtil {
    fun getSharedDomains(
        elementConfigs: List<OptionsAccessor?>,
        gridLayout: CompositeFigureGridLayoutBase
    ): Pair<List<DoubleSpan?>, List<DoubleSpan?>> {

        // Collect all continuous domains in grid.
        val continuousXYDomainByElement = elementConfigs.map {
            when {
                isApplicableElement(it) -> {
                    it as PlotConfigFrontend
                    val plotGeomTiles = PlotConfigFrontendUtil.createPlotGeomTiles(it)
                    // Do not 'expand' domains here.
                    // The required 'expand' will be added later, in PlotAssembler (see PositionalScalesUtil.computePlotXYTransformedDomains() call).
                    // However, for 0-based layers, the 0-value must be included in the domain here, because the domains computed here
                    // become hard-set limits on the corresponding scales (see PlotConfigFrontendUtil.createPlotGeomTiles()).
                    // Once set, those limits suppress the 0-inclusion heuristic in PositionalScalesUtil.RangeUtil.expandRange():
                    // if the lower limit is > 0, 0 will not be re-added to the final domain later (in PlotAssembler).
                    plotGeomTiles.overallXYContinuousDomains(withExpand = false)
                }

                else -> Pair(null, null)
            }
        }

        // Shared X-axis
        val indicesWithSharedXAxis = gridLayout.indicesWithSharedXAxis(elementConfigs.size)
        val domainXByElement = continuousXYDomainByElement.map { it.first }
        val jointDomainXByElement = joinDomains(
            domainByElement = domainXByElement,
            groupIndicesList = indicesWithSharedXAxis,
            isElementApplicable = elementConfigs.map { isApplicableElement(it) }
        )

        // Shared Y-axis
        val indicesWithSharedYAxis = gridLayout.indicesWithSharedYAxis(elementConfigs.size)
        val domainYByElement = continuousXYDomainByElement.map { it.second }
        val jointDomainYByElement = joinDomains(
            domainByElement = domainYByElement,
            groupIndicesList = indicesWithSharedYAxis,
            isElementApplicable = elementConfigs.map { isApplicableElement(it) }
        )

        return Pair(jointDomainXByElement, jointDomainYByElement)
    }

    private fun isApplicableElement(element: OptionsAccessor?): Boolean {
        // Skip facetted plots and live maps.
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

            // Update each domain in the group as long as it is applicable for the element.
            for (i in groupIndices) {
                if (isElementApplicable[i]) {
                    // Only update domains which were not null.
                    // Null means a discrete scale and is excluded from scale sharing.
                    domainByElement[i]?.let {
                        jointDomainByElement[i] = jointDomain
                    }
                }
            }
        }

        return jointDomainByElement
    }
}