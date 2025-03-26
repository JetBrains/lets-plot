/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.front.tiles

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.core.commons.data.DataType
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.FormatterUtil
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import org.jetbrains.letsPlot.core.plot.base.theme.ExponentFormat
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.MarginalLayerUtil
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.assemble.GeomLayerBuilder
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotAssembler.Companion.extractExponentFormat
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

        val layerCommonScales = when (layerConfig.isMarginal) {
            true -> MarginalLayerUtil.toMarginalScaleMap(
                commonScales,
                layerConfig.marginalSide,
                flipOrientation = false    // Positional aes are already flipped in the "common scale map".
            )

            false -> commonScales
        }

        val layerAddedScales = createScalesForPositionalStatVariables(
            layerConfig.varBindings,
            layerConfig.isYOrientation,
            commonScales
        ).let { scaleByAes ->
            when (layerConfig.isMarginal) {
                true -> MarginalLayerUtil.toMarginalScaleMap(
                    scaleByAes,
                    layerConfig.marginalSide,
                    flipOrientation = layerConfig.isYOrientation
                )

                false -> scaleByAes
            }
        }

        return layerCommonScales + layerAddedScales
    }

    private fun createScalesForPositionalStatVariables(
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
                true -> Aes.X
                false -> Aes.Y
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
                    .any { layer ->
                        // Assume that a layer has tooltips
                        // if it has mapping and tooltips are not hidden,
                        // or if tooltips are explicitly specified
                        (layer.varBindings.isNotEmpty() && !layer.tooltips.hideTooltips())
                                || !layer.tooltips.tooltipLinePatterns.isNullOrEmpty()
                    }

                GeomInteractionUtil.configGeomTargets(
                    layerConfig,
                    scaleMapByLayer[layerIndex],
                    otherLayerWithTooltips,
                    isLiveMap,
                    coordProvider.isPolar,
                    theme
                )
            }
        }
    }

    private fun createDefaultFormatters(layerConfig: LayerConfig, exponentFormat: ExponentFormat): Map<Any, (Any) -> String> {
        val expFormat = extractExponentFormat(exponentFormat)
        val dataFormatters = layerConfig.dtypes.mapValues { (_, dtype) -> FormatterUtil.byDataType(dtype, expFormat) }
        val statFormatters = Stats.VARS.mapValues { FormatterUtil.byDataType(DataType.FLOATING, expFormat) }
        val varFormatters = dataFormatters + statFormatters

        val aesFormatters = layerConfig.varBindings
            .associate { it.aes to (varFormatters[it.variable.name] ?: FormatterUtil.byDataType(DataType.UNKNOWN, expFormat)) }

        val labelFormat = layerConfig.labelFormat?.let {
            val fmt: (Any) -> String = StringFormat.forOneArg(it)::format
            mapOf(Aes.LABEL to fmt)
        }

        return varFormatters + aesFormatters + (labelFormat ?: emptyMap())
    }

    fun createLayerBuilder(
        layerConfig: LayerConfig,
        fontFamilyRegistry: FontFamilyRegistry,
        geomInteraction: GeomInteraction?,
        theme: Theme
    ): GeomLayerBuilder {
        val geomProvider =
            layerConfig.geomProto.geomProvider(
                layerConfig,
                layerConfig.aopConversion,
                expFormat = theme.exponentFormat
            )

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

        layerBuilder
            .disableLegend(layerConfig.isLegendDisabled)
            .customLegendOptions(layerConfig.customLegendOptions)

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

        layerBuilder.defaultFormatters(
            createDefaultFormatters(layerConfig, theme.exponentFormat)
        )

        return layerBuilder
    }

}