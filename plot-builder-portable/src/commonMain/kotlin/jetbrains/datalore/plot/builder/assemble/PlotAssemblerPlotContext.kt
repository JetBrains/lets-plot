/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.scale.ScaleUtil
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.common.data.SeriesUtil

internal class PlotAssemblerPlotContext(
    layersByTile: List<List<GeomLayer>>,
    private val scaleMap: Map<Aes<*>, Scale>
) : PlotContext {

    private val stitchedPlotLayers: List<StitchedPlotLayer> = createStitchedLayers(layersByTile)
    private val transformedDomainByAes: MutableMap<Aes<*>, DoubleSpan> = HashMap()
    private val tooltipFormatters: MutableMap<Aes<*>, (Any?) -> String> = HashMap()

    override val layers: List<PlotContext.Layer> = stitchedPlotLayers.map(::ContextPlotLayer)

    override fun getScale(aes: Aes<*>): Scale? {
        checkPositionalAes(aes)
        return scaleMap[aes]
    }

    override fun overallTransformedDomain(aes: Aes<*>): DoubleSpan {
        checkPositionalAes(aes)
        return transformedDomainByAes.getOrPut(aes) {
            computeOverallTransformedDomain(aes, stitchedPlotLayers, scaleMap)
        }
    }

    override fun getTooltipFormatter(aes: Aes<*>, defaultValue: () -> (Any?) -> String): (Any?) -> String {
        checkPositionalAes(aes)
        return tooltipFormatters.getOrPut(aes, defaultValue)
    }


    private companion object {
        fun createStitchedLayers(
            layersByPanel: List<List<GeomLayer>>,
        ): List<StitchedPlotLayer> {
            if (layersByPanel.isEmpty()) return emptyList()

            // stitch together layers from all panels
            val layerCount = layersByPanel[0].size

            val stitchedLayers = ArrayList<StitchedPlotLayer>()
            for (i in 0 until layerCount) {
                val layersOnPlane = ArrayList<GeomLayer>()

                // Collect layer[i] chunks from all panels.
                for (panelLayers in layersByPanel) {
                    layersOnPlane.add(panelLayers[i])
                }

                stitchedLayers.add(StitchedPlotLayer(layersOnPlane))
            }

            return stitchedLayers
        }

        fun computeOverallTransformedDomain(
            aes: Aes<*>,
            stitchedLayers: List<StitchedPlotLayer>,
            scaleMap: Map<Aes<*>, Scale>
        ): DoubleSpan {
            checkPositionalAes(aes)

            fun isMatching(v: DataFrame.Variable, aes: Aes<*>, isYOrientation: Boolean): Boolean {
                val varAes = TransformVar.toAes(v)
                return when {
                    Aes.isPositionalXY(varAes) -> Aes.toAxisAes(
                        varAes,
                        isYOrientation
                    ) == aes // collecting pos variables
                    else -> varAes == aes
                }
            }

            val domainsRaw = ArrayList<DoubleSpan>()
            for (layer in stitchedLayers) {
                val variables = layer.getVariables()
                    .filter { it.isTransform }
                    .filter { isMatching(it, aes, layer.isYOrientation) }

                for (transformVar in variables) {
                    val domain = layer.getDataRange(transformVar)
                    if (domain != null) {
                        domainsRaw.add(domain)
                    }
                }
            }

            val overallTransformedDomain = domainsRaw.reduceOrNull { acc, v -> acc.union(v) }

            val scale = scaleMap.getValue(aes)
            return if (scale.isContinuousDomain) {
                finalizeOverallTransformedDomain(overallTransformedDomain, scale.transform as ContinuousTransform)
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

        fun checkPositionalAes(aes: Aes<*>) {
            // expect only X,Y or not positional
            check(!Aes.isPositionalXY(aes) || aes == Aes.X || aes == Aes.Y) {
                "Positional aesthetic should be either X or Y but was $aes"
            }
        }
    }

    private class ContextPlotLayer(
        private val stitchedPlotLayer: StitchedPlotLayer
    ) : PlotContext.Layer {
        override val isLegendDisabled: Boolean get() = stitchedPlotLayer.isLegendDisabled
        override val aestheticsDefaults: AestheticsDefaults get() = stitchedPlotLayer.aestheticsDefaults
        override val legendKeyElementFactory: LegendKeyElementFactory get() = stitchedPlotLayer.legendKeyElementFactory
        override val colorByAes: Aes<Color> get() = stitchedPlotLayer.colorByAes
        override val fillByAes: Aes<Color> get() = stitchedPlotLayer.fillByAes

        override fun renderedAes(): List<Aes<*>> = stitchedPlotLayer.renderedAes()

        override fun hasBinding(aes: Aes<*>): Boolean = stitchedPlotLayer.hasBinding(aes)

        override fun hasConstant(aes: Aes<*>): Boolean = stitchedPlotLayer.hasConstant(aes)

        override fun <T> getConstant(aes: Aes<T>): T = stitchedPlotLayer.getConstant(aes)
    }
}