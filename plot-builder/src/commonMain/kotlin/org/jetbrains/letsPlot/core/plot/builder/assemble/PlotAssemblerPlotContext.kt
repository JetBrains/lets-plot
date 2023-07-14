/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsDefaults
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleUtil
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer

internal class PlotAssemblerPlotContext(
    layersByTile: List<List<GeomLayer>>,
    private val scaleMap: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Scale>
) : PlotContext {

    private val stitchedPlotLayers: List<StitchedPlotLayer> = createStitchedLayers(layersByTile)
    private val transformedDomainByAes: MutableMap<org.jetbrains.letsPlot.core.plot.base.Aes<*>, DoubleSpan> = HashMap()
    private val tooltipFormatters: MutableMap<org.jetbrains.letsPlot.core.plot.base.Aes<*>, (Any?) -> String> =
        HashMap()

    override val layers: List<PlotContext.Layer> = stitchedPlotLayers.map(::ContextPlotLayer)

    override fun hasScale(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>) = scaleMap.containsKey(aes)

    override fun getScale(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Scale {
        checkPositionalAes(aes)
        return scaleMap.getValue(aes)
    }

    override fun overallTransformedDomain(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): DoubleSpan {
        checkPositionalAes(aes)
        return transformedDomainByAes.getOrPut(aes) {
            computeOverallTransformedDomain(aes, stitchedPlotLayers, scaleMap)
        }
    }

    override fun getTooltipFormatter(
        aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>,
        defaultValue: () -> (Any?) -> String
    ): (Any?) -> String {
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
            aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>,
            stitchedLayers: List<StitchedPlotLayer>,
            scaleMap: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Scale>
        ): DoubleSpan {
            checkPositionalAes(aes)

            fun isMatching(
                v: DataFrame.Variable,
                aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>,
                isYOrientation: Boolean
            ): Boolean {
                val varAes = TransformVar.toAes(v)
                return when {
                    org.jetbrains.letsPlot.core.plot.base.Aes.isPositionalXY(varAes) -> org.jetbrains.letsPlot.core.plot.base.Aes.toAxisAes(
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

        fun checkPositionalAes(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>) {
            // expect only X,Y or not positional
            check(!org.jetbrains.letsPlot.core.plot.base.Aes.isPositionalXY(aes) || aes == org.jetbrains.letsPlot.core.plot.base.Aes.X || aes == org.jetbrains.letsPlot.core.plot.base.Aes.Y) {
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
        override val colorByAes: org.jetbrains.letsPlot.core.plot.base.Aes<Color> get() = stitchedPlotLayer.colorByAes
        override val fillByAes: org.jetbrains.letsPlot.core.plot.base.Aes<Color> get() = stitchedPlotLayer.fillByAes

        override fun renderedAes(): List<org.jetbrains.letsPlot.core.plot.base.Aes<*>> = stitchedPlotLayer.renderedAes()

        override fun hasBinding(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean =
            stitchedPlotLayer.hasBinding(aes)

        override fun hasConstant(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean =
            stitchedPlotLayer.hasConstant(aes)

        override fun <T> getConstant(aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>): T =
            stitchedPlotLayer.getConstant(aes)
    }
}