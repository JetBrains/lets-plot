/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.theme.*
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotGuidesAssemblerUtil.checkFitsColorBar
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotGuidesAssemblerUtil.createColorBarAssembler
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotGuidesAssemblerUtil.fitsColorBar
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotGuidesAssemblerUtil.mappedRenderedAesToCreateGuides
import org.jetbrains.letsPlot.core.plot.builder.layout.*

internal object PlotAssemblerUtil {

    fun createLegends(
        ctx: PlotContext,
        geomTiles: PlotGeomTiles,
        scaleMappersNP: Map<Aes<*>, ScaleMapper<*>>,
        guideOptionsMap: Map<GuideKey, GuideOptionsList>,
        legendTheme: LegendTheme
    ): List<LegendBoxInfo> {

        val legendAssemblerByTitle = LinkedHashMap<String, LegendAssembler>()
        val colorBarAssemblerByTitle = LinkedHashMap<String, ColorBarAssembler>()

        for ((layerIndex, layerInfo) in geomTiles.layerInfos().withIndex()) {
            val layerConstantByAes = HashMap<Aes<*>, Any>()
            for (aes in layerInfo.renderedAes()) {
                if (layerInfo.hasConstant(aes)) {
                    layerConstantByAes[aes] = layerInfo.getConstant(aes)!!
                }
            }

            val aesListByScaleName = LinkedHashMap<String, MutableList<Aes<*>>>()
            val aesList = mappedRenderedAesToCreateGuides(layerInfo, guideOptionsMap)
            for (aes in aesList) {
                val scale = ctx.getScale(aes)

                val colorBarOptions: ColorBarOptions? = guideOptionsMap[GuideKey.fromAes(aes)]
                    ?.getColorBarOptions()
                    ?.also { checkFitsColorBar(aes, scale) }

                if (colorBarOptions != null || fitsColorBar(aes, scale)) {
                    // Colorbar
                    @Suppress("UNCHECKED_CAST")
                    val colorBarAssembler = createColorBarAssembler(
                        scale.name,
                        ctx.overallTransformedDomain(aes),
                        scale,
                        scaleMappersNP.getValue(aes) as ScaleMapper<Color>,
                        colorBarOptions,
                        legendTheme
                    )

                    val colorbarName = colorBarAssemblerByTitle[scale.name]?.let { existingAssembler ->
                        if (colorBarAssembler.equalScalesAndOptions(existingAssembler)) {
                            scale.name
                        } else {
                            // Don't just replace an existing colorbar (see LP-760: ggmarginal(): broken coloring)
                            // Add under another key
                            "${scale.name} (${aes.name})"
                        }
                    } ?: scale.name

                    colorBarAssemblerByTitle[colorbarName] = colorBarAssembler.withTitle(colorbarName)

                } else {
                    // Legend
                    aesListByScaleName.getOrPut(scale.name) { ArrayList() }.add(aes)
                }
            }

            for ((scaleName, aesListForScaleName) in aesListByScaleName) {
                val legendAssembler = legendAssemblerByTitle.getOrPut(scaleName) {
                    LegendAssembler(
                        scaleName,
                        guideOptionsMap,
                        scaleMappersNP,
                        legendTheme
                    )
                }

                val guideKeysForScaleName = aesListForScaleName.map(GuideKey.Companion::fromAes)
                val allOverrideAesValues = guideOptionsMap
                    .filterKeys { it in guideKeysForScaleName }
                    .values
                    .mapNotNull { it.getLegendOptions()?.overrideAesValues }
                    .flatMap { it.entries }
                    .associate { it.key to it.value }

                legendAssembler.addLayer(
                    keyFactory = layerInfo.legendKeyElementFactory,
                    aesList = aesListForScaleName,
                    overrideAesValues = allOverrideAesValues,
                    constantByAes = layerConstantByAes,
                    aestheticsDefaults = layerInfo.aestheticsDefaults,
                    colorByAes = layerInfo.colorByAes,
                    fillByAes = layerInfo.fillByAes,
                    isMarginal = layerInfo.isMarginal,
                    ctx = ctx
                )
            }

            // custom legend
            layerInfo.customLegendOptions?.let { legendOptions ->
                val guideKey = GuideKey.fromName(legendOptions.group)
                if (guideOptionsMap[guideKey]?.hasNone() == true) return@let

                val legendTitle = guideOptionsMap[guideKey]?.getTitle() ?: legendOptions.group

                val customLegendAssembler = legendAssemblerByTitle.getOrPut(legendTitle) {
                    LegendAssembler(
                        legendTitle,
                        guideOptionsMap,
                        scaleMappersNP,
                        legendTheme
                    )
                }
                val allOverrideAesValues = processOverrideAesValues(
                    guideOptionsMap[guideKey]?.getLegendOptions()?.overrideAesValues,
                    legendOptions.index ?: layerIndex
                )

                customLegendAssembler.addCustomLayer(
                    customLegendOptions = legendOptions,
                    keyFactory = layerInfo.legendKeyElementFactory,
                    overrideAesValues = allOverrideAesValues,
                    constantByAes = layerConstantByAes,
                    aestheticsDefaults = layerInfo.aestheticsDefaults,
                    colorByAes = layerInfo.colorByAes,
                    fillByAes = layerInfo.fillByAes,
                    isMarginal = layerInfo.isMarginal
                )
            }
        }

        val legendBoxInfos = ArrayList<LegendBoxInfo>()
        for (legendTitle in colorBarAssemblerByTitle.keys) {
            val boxInfo = colorBarAssemblerByTitle.getValue(legendTitle).createColorBar()
            if (!boxInfo.isEmpty) {
                legendBoxInfos.add(boxInfo)
            }
        }

        for (legendTitle in legendAssemblerByTitle.keys) {
            val boxInfo = legendAssemblerByTitle.getValue(legendTitle).createLegend()
            if (!boxInfo.isEmpty) {
                legendBoxInfos.add(boxInfo)
            }
        }
        return legendBoxInfos
    }

    private fun processOverrideAesValues(overrideAesValues: Map<Aes<*>, Any>?, index: Int): Map<Aes<*>, Any> {
        if (overrideAesValues == null) {
            return emptyMap()
        }

        return overrideAesValues.mapNotNull { (key, value) ->
            val processedValue = if (value is List<*>) {
                if (index in value.indices) value[index] else value.lastOrNull()
            } else {
                value
            }
            if (processedValue != null) key to processedValue else null
        }.toMap()
    }

    fun createPlotLayout(
        layoutProviderByTile: List<TileLayoutProvider>,
        insideOut: Boolean,
        facets: PlotFacets,
        facetsTheme: FacetsTheme,
        hAxisTheme: AxisTheme,
        vAxisTheme: AxisTheme,
        plotTheme: PlotTheme,
    ): PlotLayout {
        return if (facets.isDefined) {
            FacetedPlotLayout(
                facets,
                layoutProviderByTile,
                facetsTheme.showStrip(),
                hAxisTheme,
                vAxisTheme,
                plotTheme,
                facetsTheme
            )
        } else {
            val tileLayout = if (insideOut) {
                layoutProviderByTile[0].createInsideOutTileLayout()
            } else {
                layoutProviderByTile[0].createTopDownTileLayout()
            }

            SingleTilePlotLayout(
                tileLayout,
                plotTheme
            )
        }
    }
}
