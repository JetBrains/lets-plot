/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble.tiles

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleUtil
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotGeomTiles
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider

abstract class PlotGeomTilesBase(
    override val scalesBeforeFacets: Map<Aes<*>, Scale>,
    override val coordProvider: CoordProvider,
    override val containsLiveMap: Boolean
) : PlotGeomTiles {

    private val transformedDomainByAes: MutableMap<Aes<*>, DoubleSpan> = HashMap()

    override val xyContinuousTransforms: Pair<Transform?, Transform?>
        get() = Pair(
            scalesBeforeFacets.getValue(Aes.X).transform.let { if (it is ContinuousTransform) it else null },
            scalesBeforeFacets.getValue(Aes.Y).transform.let { if (it is ContinuousTransform) it else null }
        )

    override fun coreLayersByTile(): List<List<GeomLayer>> {
        return layersByTile().map { layers ->
            layers.filterNot { it.isMarginal }
        }
    }

    override fun marginalLayersByTile(): List<List<GeomLayer>> {
        return layersByTile().map { layers ->
            layers.filter { it.isMarginal }.filterNot { it.isLiveMap }
        }
    }

    override fun overallTransformedDomain(aes: Aes<*>): DoubleSpan {
        val layersByTile = if (Aes.isPositionalXY(aes)) {
            // expect only X,Y or not positional
            check(aes == Aes.X || aes == Aes.Y) {
                "Positional aesthetic should be either X or Y but was $aes"
            }
            // exclude marginal layers
            coreLayersByTile()
        } else {
            layersByTile()
        }
        return transformedDomainByAes.getOrPut(aes) {
            computeOverallTransformedDomain(aes, layersByTile, scalesBeforeFacets)
        }
    }

//    override fun coreLayerInfos(): List<GeomLayerInfo> {
//        return coreLayersByTile()[0].map(::GeomLayerInfo)
//    }

    override fun layerInfos(): List<GeomLayerInfo> {
        return layersByTile()[0].map(::GeomLayerInfo)
    }

    companion object {

        private fun computeOverallTransformedDomain(
            aes: Aes<*>,
            layersByTile: List<List<GeomLayer>>,
            scaleMap: Map<Aes<*>, Scale>
        ): DoubleSpan {

            fun isMatching(v: DataFrame.Variable, aes: Aes<*>): Boolean {
                val varAes = TransformVar.toAes(v)
                return when {
                    Aes.isPositionalXY(varAes) -> Aes.toAxisAes(varAes) == aes // collecting pos variables
                    else -> varAes == aes
                }
            }

            val domainsRaw = ArrayList<DoubleSpan>()
            for (layer in layersByTile.flatten()) {
                val variables = layer.dataFrame.variables()
                    .filter { it.isTransform }
                    .filter { isMatching(it, aes) }

                for (transformVar in variables) {
                    layer.dataFrame.range(transformVar)?.let {
                        domainsRaw.add(it)
                    }
                }
            }

            val overallTransformedDomain = domainsRaw.reduceOrNull { acc, v -> acc.union(v) }

            val scale = scaleMap.getValue(aes)
            return if (scale.isContinuousDomain) {
                finalizeOverallTransformedDomain(
                    overallTransformedDomain,
                    scale.transform as ContinuousTransform
                )
            } else {
                // Discrete domain
                overallTransformedDomain ?: DoubleSpan.singleton(0.0)
            }
        }

        private fun finalizeOverallTransformedDomain(
            transformedDomain: DoubleSpan?,
            transform: ContinuousTransform
        ): DoubleSpan {
            val (dataLower, dataUpper) = when (transformedDomain) {
                null -> Pair(Double.NaN, Double.NaN)
                else -> Pair(transformedDomain.lowerEnd, transformedDomain.upperEnd)
            }
            val (scaleLower, scaleUpper) = ScaleUtil.transformedDefinedLimits(transform)

            val lowerEnd = if (scaleLower.isFinite()) scaleLower else dataLower
            val upperEnd = if (scaleUpper.isFinite()) scaleUpper else dataUpper

            val newRange = when {
                lowerEnd.isFinite() && upperEnd.isFinite() -> DoubleSpan(lowerEnd, upperEnd)
                lowerEnd.isFinite() -> DoubleSpan(lowerEnd, lowerEnd)
                upperEnd.isFinite() -> DoubleSpan(upperEnd, upperEnd)
                else -> null
            }

            return SeriesUtil.ensureApplicableRange(newRange)
        }
    }
}