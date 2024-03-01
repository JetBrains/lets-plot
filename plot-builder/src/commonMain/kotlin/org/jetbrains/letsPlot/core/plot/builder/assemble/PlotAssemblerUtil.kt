/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.FacetsTheme
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme
import org.jetbrains.letsPlot.core.plot.base.theme.PlotTheme
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotGuidesAssemblerUtil.checkFitsColorBar
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotGuidesAssemblerUtil.createColorBarAssembler
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotGuidesAssemblerUtil.fitsColorBar
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotGuidesAssemblerUtil.mappedRenderedAesToCreateGuides
import org.jetbrains.letsPlot.core.plot.builder.layout.*
import org.jetbrains.letsPlot.core.plot.builder.scale.AxisPosition

internal object PlotAssemblerUtil {

    private fun updateAesRangeMap(
        aes: Aes<*>,
        range: DoubleSpan?,
        rangeByAes: MutableMap<Aes<*>, DoubleSpan>
    ) {
        @Suppress("NAME_SHADOWING")
        var range = range
        if (range != null) {
            val wasRange = rangeByAes[aes]
            if (wasRange != null) {
                range = wasRange.union(range)
            }
            rangeByAes[aes] = range
        }
    }

    fun createLegends(
        ctx: PlotContext,
        geomTiles: PlotGeomTiles,
        scaleMappersNP: Map<Aes<*>, ScaleMapper<*>>,
        guideOptionsMap: Map<Aes<*>, GuideOptions>,
        theme: LegendTheme
    ): List<LegendBoxInfo> {

        val legendAssemblerByTitle = LinkedHashMap<String, LegendAssembler>()
        val colorBarAssemblerByTitle = LinkedHashMap<String, ColorBarAssembler>()

        for (layerInfo in geomTiles.coreLayerInfos()) {
            val layerConstantByAes = HashMap<Aes<*>, Any>()
            for (aes in layerInfo.renderedAes()) {
                if (layerInfo.hasConstant(aes)) {
                    layerConstantByAes[aes] = layerInfo.getConstant(aes)!!
                }
            }

            val aesListByScaleName = LinkedHashMap<String, MutableList<Aes<*>>>()
            val aesList = mappedRenderedAesToCreateGuides(layerInfo, guideOptionsMap)
            for (aes in aesList) {
                var colorBar = false
                val scale = ctx.getScale(aes)
                val scaleName = scale.name
                if (guideOptionsMap.containsKey(aes)) {
                    val guideOptions = guideOptionsMap[aes]
                    if (guideOptions is ColorBarOptions) {
                        checkFitsColorBar(aes, scale)
                        colorBar = true
                        @Suppress("UNCHECKED_CAST")
                        colorBarAssemblerByTitle[scaleName] = createColorBarAssembler(
                            scaleName,
                            ctx.overallTransformedDomain(aes),
                            scale,
                            scaleMappersNP.getValue(aes) as ScaleMapper<Color>,
                            guideOptions,
                            theme
                        )
                    }
                } else if (fitsColorBar(aes, scale)) {
                    colorBar = true
                    @Suppress("UNCHECKED_CAST")
                    colorBarAssemblerByTitle[scaleName] = createColorBarAssembler(
                        scaleName,
                        ctx.overallTransformedDomain(aes),
                        scale,
                        scaleMappersNP.getValue(aes) as ScaleMapper<Color>,
                        null,
                        theme
                    )
                }

                if (!colorBar) {
                    aesListByScaleName.getOrPut(scaleName) { ArrayList() }.add(aes)
                }
            }

            for (scaleName in aesListByScaleName.keys) {
                val legendAssembler = legendAssemblerByTitle.getOrPut(scaleName) {
                    LegendAssembler(
                        scaleName,
                        guideOptionsMap,
                        scaleMappersNP,
                        theme
                    )
                }

                val aesListForScaleName = aesListByScaleName.getValue(scaleName)
                val legendKeyFactory = layerInfo.legendKeyElementFactory
                val aestheticsDefaults = layerInfo.aestheticsDefaults
                legendAssembler.addLayer(
                    legendKeyFactory,
                    aesListForScaleName,
                    layerConstantByAes,
                    aestheticsDefaults,
                    ctx,
                    layerInfo.colorByAes,
                    layerInfo.fillByAes
                )
            }
        }

        val legendBoxInfos = ArrayList<LegendBoxInfo>()
        for (legendTitle in colorBarAssemblerByTitle.keys) {
            val boxInfo = colorBarAssemblerByTitle[legendTitle]!!.createColorBar()
            if (!boxInfo.isEmpty) {
                legendBoxInfos.add(boxInfo)
            }
        }

        for (legendTitle in legendAssemblerByTitle.keys) {
            val boxInfo = legendAssemblerByTitle[legendTitle]!!.createLegend()
            if (!boxInfo.isEmpty) {
                legendBoxInfos.add(boxInfo)
            }
        }
        return legendBoxInfos
    }

    fun createPlotLayout(
        layoutProviderByTile: List<TileLayoutProvider>,
        insideOut: Boolean,
        facets: PlotFacets,
        facetsTheme: FacetsTheme,
        hAxisPosition: AxisPosition,
        vAxisPosition: AxisPosition,
        hAxisTheme: AxisTheme,
        vAxisTheme: AxisTheme,
        plotTheme: PlotTheme,
    ): PlotLayout {
        return if (facets.isDefined) {
            FacetedPlotLayout(
                facets,
                layoutProviderByTile,
                facetsTheme.showStrip(),
                hAxisPosition,
                vAxisPosition,
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
                hAxisPosition,
                vAxisPosition,
                hAxisTheme,
                vAxisTheme,
                plotTheme
            )
        }
    }
}
