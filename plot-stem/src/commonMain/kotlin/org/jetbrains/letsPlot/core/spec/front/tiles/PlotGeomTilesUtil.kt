/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.front.tiles

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.MarginalLayerUtil
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.assemble.GeomLayerBuilder
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.tooltip.conf.GeomInteraction
import org.jetbrains.letsPlot.core.spec.config.GeoConfig
import org.jetbrains.letsPlot.core.spec.config.LayerConfig
import org.jetbrains.letsPlot.core.spec.front.GeomInteractionUtil

internal object PlotGeomTilesUtil {

    fun buildLayerScaleMap(
        layerConfig: LayerConfig,
        commonScales: Map<Aes<*>, Scale>,
    ): Map<Aes<*>, Scale> {

        val layerRenamedScales = namePositionalScalesAfterStatVariables(
            layerConfig.varBindings,
            layerConfig.isYOrientation,
            commonScales
        )

        val layerScales = commonScales + layerRenamedScales
        return when (layerConfig.isMarginal) {
            true -> MarginalLayerUtil.toMarginalScaleMap(
                layerScales,
                layerConfig.marginalSide,
                flipOrientation = false    // Positional aes are already flipped in the "common scale map".
            )

            false -> layerScales
        }
    }

    private fun namePositionalScalesAfterStatVariables(
        layerVarBindings: List<VarBinding>,
        isYOrientation: Boolean,
        commonScaleMap: Map<Aes<*>, Scale>,
    ): Map<Aes<*>, Scale> {
        val statPositionalBindings =
            layerVarBindings.filter { it.variable.isStat }
                .filterNot { it.aes == Aes.X || it.aes == Aes.Y }
                .filter { Aes.isPositionalXY(it.aes) }

        return statPositionalBindings.associate { binding ->
            val positionalAes = when (isYOrientation) {
                true -> if (Aes.isPositionalX(binding.aes)) Aes.Y else Aes.X
                false -> if (Aes.isPositionalX(binding.aes)) Aes.X else Aes.Y
            }
            val scaleProto = commonScaleMap.getValue(positionalAes)
            val aesScale = scaleProto.with().name(binding.variable.label).build()
            binding.aes to aesScale
        }
    }

    fun geomInteractionByLayer(
        layerConfigs: List<LayerConfig>,
        scaleMapByLayer: List<Map<Aes<*>, Scale>>,
        coordProvider: CoordProvider,
        theme: Theme,
        isLiveMap: Boolean
    ): List<GeomInteraction?> {
        return layerConfigs.mapIndexed { layerIndex, layerConfig ->
            if (layerConfig.isMarginal) {
                // marginal layer doesn't have interactions
                null
            } else {
                val otherLayerWithTooltips = layerConfigs
                    .filterIndexed { index, _ -> index != layerIndex }
                    .any { !it.tooltips.hideTooltips() }

                GeomInteractionUtil.configGeomTargets(
                    layerConfig,
                    scaleMapByLayer[layerIndex],
                    otherLayerWithTooltips,
                    isLiveMap,
                    coordProvider.isLinear,
                    theme
                )
            }
        }
    }

    fun createLayerBuilder(
        layerConfig: LayerConfig,
        fontFamilyRegistry: FontFamilyRegistry,
        geomInteraction: GeomInteraction?,
        theme: Theme
    ): GeomLayerBuilder {
        val geomProvider =
            layerConfig.geomProto.geomProvider(layerConfig, layerConfig.aopConversion, theme.exponentFormat.superscript)

        val stat = layerConfig.stat
        val layerBuilder = GeomLayerBuilder(
            geomProvider = geomProvider,
            stat = stat,
            posProvider = layerConfig.posProvider,
            fontFamilyRegistry = fontFamilyRegistry
        )
            .yOrientation(layerConfig.isYOrientation)
            .marginal(layerConfig.isMarginal, layerConfig.marginalSide, layerConfig.marginalSize)

        // Color aesthetics
        layerBuilder
            .colorByAes(layerConfig.colorByAes)
            .fillByAes(layerConfig.fillByAes)

        // geomTheme
        layerBuilder.geomTheme(theme.geometries(layerConfig.geomProto.geomKind))

        layerBuilder.superscriptExponent(theme.exponentFormat.superscript)

        val constantAesMap = layerConfig.constantsMap
        for (aes in constantAesMap.keys) {
            @Suppress("UNCHECKED_CAST", "MapGetWithNotNullAssertionOperator")
            layerBuilder.addConstantAes(aes as Aes<Any>, constantAesMap[aes]!!)
        }

        if (layerConfig.hasExplicitGrouping()) {
            layerBuilder.groupingVarName(layerConfig.explicitGroupingVarName!!)
        }

        // no map_join, data=gdf or map=gdf - group values and geometries by GEO_ID
        DataFrameUtil.variables(layerConfig.combinedData)[GeoConfig.GEO_ID]?.let {
            layerBuilder.pathIdVarName(GeoConfig.GEO_ID)
        }

        // variable bindings
        val bindings = layerConfig.varBindings
        for (binding in bindings) {
            layerBuilder.addBinding(binding)
        }

        layerBuilder.disableLegend(layerConfig.isLegendDisabled)

        geomInteraction?.let {
            layerBuilder
                .locatorLookupSpec(it.createLookupSpec())
                .contextualMappingProvider(it)
        }
        // annotations
        layerBuilder.annotationSpecification(
            layerConfig.annotations,
            theme.annotations().textStyle(),
            theme.annotations().useCustomColor()
        )

        return layerBuilder
    }

}