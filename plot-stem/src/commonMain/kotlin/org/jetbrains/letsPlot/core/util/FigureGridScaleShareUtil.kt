/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.Transform
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
        val domainXYByElement = elementConfigs.map {
            when {
                isApplicableElement(it) -> {
                    it as PlotConfigFrontend
                    val plotAssembler = PlotConfigFrontendUtil.createPlotAssembler(it)
                    val transformedDomainsByTile = plotAssembler.rawXYTransformedDomainsByTile
                    val transformesByTile = plotAssembler.xyTransformByTile

                    transformedDomainsByTile?.let {
                        // ToDo: tiles could have different X/Y scales / transforms
                        // Assume just 1 tile (no facets)
                        val transformedDomainX = transformedDomainsByTile[0].first
                        val transformedDomainY = transformedDomainsByTile[0].second
                        val scaleXTransform = transformesByTile!![0].first
                        val scaleYTransform = transformesByTile[0].second

                        val domainX = inverseTransformIfContinuousOrNull(transformedDomainX, scaleXTransform)
                        val domainY = inverseTransformIfContinuousOrNull(transformedDomainY, scaleYTransform)
                        Pair(domainX, domainY)
                    }
                }

                else -> null
            }
        }

        // Shared X-axis
        val indicesWithSharedXAxis = gridLayout.indicesWithSharedXAxis(elementConfigs.size)
        val domainXByElement = domainXYByElement.map { it?.first }
        val jointDomainXByElement = joinDomains(
            domainByElement = domainXByElement,
            groupIndicesList = indicesWithSharedXAxis,
            isElementApplicable = elementConfigs.map { isApplicableElement(it) }
        )

        // Shared Y-axis
        val indicesWithSharedYAxis = gridLayout.indicesWithSharedYAxis(elementConfigs.size)
        val domainYByElement = domainXYByElement.map { it?.second }
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

    private fun inverseTransformIfContinuousOrNull(
        span: DoubleSpan,
        transform: Transform,
    ): DoubleSpan? {
        val doubleSpan: DoubleSpan? = if (transform is ContinuousTransform) {
            val v0 = transform.applyInverse(span.lowerEnd)
            val v1 = transform.applyInverse(span.upperEnd)
            if (SeriesUtil.allFinite(v0, v1)) {
                DoubleSpan(v0!!, v1!!)
            } else {
                null
            }
        } else {
            null
        }
        return doubleSpan
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