/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.PlotContext
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.builder.assemble.PlotGuidesAssemblerUtil.checkFitsColorBar
import jetbrains.datalore.plot.builder.assemble.PlotGuidesAssemblerUtil.createColorBarAssembler
import jetbrains.datalore.plot.builder.assemble.PlotGuidesAssemblerUtil.fitsColorBar
import jetbrains.datalore.plot.builder.assemble.PlotGuidesAssemblerUtil.mappedRenderedAesToCreateGuides
import jetbrains.datalore.plot.builder.layout.*
import jetbrains.datalore.plot.builder.scale.AxisPosition
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.FacetsTheme
import jetbrains.datalore.plot.builder.theme.LegendTheme

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
        scaleMappersNP: Map<Aes<*>, ScaleMapper<*>>,
        guideOptionsMap: Map<Aes<*>, GuideOptions>,
        theme: LegendTheme
    ): List<LegendBoxInfo> {

        val legendAssemblerByTitle = LinkedHashMap<String, LegendAssembler>()
        val colorBarAssemblerByTitle = LinkedHashMap<String, ColorBarAssembler>()

        for (contextLayer in ctx.layers) {
            val layerConstantByAes = HashMap<Aes<*>, Any>()
            for (aes in contextLayer.renderedAes()) {
                if (contextLayer.hasConstant(aes)) {
                    layerConstantByAes[aes] = contextLayer.getConstant(aes)!!
                }
            }

            val aesListByScaleName = LinkedHashMap<String, MutableList<Aes<*>>>()
            val aesList = mappedRenderedAesToCreateGuides(contextLayer, guideOptionsMap)
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
                val legendKeyFactory = contextLayer.legendKeyElementFactory
                val aestheticsDefaults = contextLayer.aestheticsDefaults
                legendAssembler.addLayer(
                    legendKeyFactory,
                    aesListForScaleName,
                    layerConstantByAes,
                    aestheticsDefaults,
                    ctx
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
                vAxisTheme
            )
        }
    }
}
