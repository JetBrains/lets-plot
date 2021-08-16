/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.gcommon.collect.Iterables
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.ScaleUtil
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.PlotUtil
import jetbrains.datalore.plot.builder.PlotUtil.computeLayerDryRunXYRanges
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.PlotGuidesAssemblerUtil.checkFitsColorBar
import jetbrains.datalore.plot.builder.assemble.PlotGuidesAssemblerUtil.createColorBarAssembler
import jetbrains.datalore.plot.builder.assemble.PlotGuidesAssemblerUtil.fitsColorBar
import jetbrains.datalore.plot.builder.assemble.PlotGuidesAssemblerUtil.guideTransformedDomainByAes
import jetbrains.datalore.plot.builder.assemble.PlotGuidesAssemblerUtil.mappedRenderedAesToCreateGuides
import jetbrains.datalore.plot.builder.layout.*
import jetbrains.datalore.plot.builder.theme.LegendTheme
import jetbrains.datalore.plot.common.data.SeriesUtil

internal object PlotAssemblerUtil {

    private fun updateAesRangeMap(
        aes: Aes<*>,
        range: ClosedRange<Double>?,
        rangeByAes: MutableMap<Aes<*>, ClosedRange<Double>>
    ) {
        @Suppress("NAME_SHADOWING")
        var range = range
        if (range != null) {
            val wasRange = rangeByAes[aes]
            if (wasRange != null) {
                range = wasRange.span(range)
            }
            rangeByAes[aes] = range
        }
    }

    private fun updateRange(range: ClosedRange<Double>?, wasRange: ClosedRange<Double>?): ClosedRange<Double>? {
        @Suppress("NAME_SHADOWING")
        var range = range
        if (range != null) {
            if (wasRange != null) {
                range = wasRange.span(range)
            }
            return range
        }
        return wasRange
    }

    private fun updateRange(values: Iterable<Double>, wasRange: ClosedRange<Double>?): ClosedRange<Double>? {
        if (!Iterables.isEmpty(values)) {
            var newRange = ClosedRange.encloseAll(values)
            if (wasRange != null) {
                newRange = wasRange.span(newRange)
            }
            return newRange
        }
        return wasRange
    }

    fun createLegends(
        layersByPanel: List<List<GeomLayer>>,
        guideOptionsMap: Map<Aes<*>, GuideOptions>,
        theme: LegendTheme
    ): List<LegendBoxInfo> {

        // stitch together layers from all panels
        var planeCount = 0
        if (layersByPanel.isNotEmpty()) {
            planeCount = layersByPanel[0].size
        }

        val stitchedLayersList = ArrayList<StitchedPlotLayers>()
        for (i in 0 until planeCount) {
            val layersOnPlane = ArrayList<GeomLayer>()

            // collect layer[i] chunks from all panels
            for (panelLayers in layersByPanel) {
                layersOnPlane.add(panelLayers[i])
            }

            stitchedLayersList.add(
                StitchedPlotLayers(
                    layersOnPlane
                )
            )
        }

        val transformedDomainByAes = HashMap<Aes<*>, ClosedRange<Double>>()
        for (stitchedPlotLayers in stitchedLayersList) {
            val layerTransformedDomainByAes = guideTransformedDomainByAes(stitchedPlotLayers, guideOptionsMap)
            for ((aes, transformedDomain) in layerTransformedDomainByAes) {
                updateAesRangeMap(
                    aes,
                    transformedDomain,
                    transformedDomainByAes
                )
            }
        }

        return createLegends(
            stitchedLayersList,
            transformedDomainByAes,
            guideOptionsMap,
            theme
        )
    }

    private fun createLegends(
        stitchedLayersList: List<StitchedPlotLayers>,
        transformedDomainByAes: Map<Aes<*>, ClosedRange<Double>>,
        guideOptionsMap: Map<Aes<*>, GuideOptions>,
        theme: LegendTheme
    ): List<LegendBoxInfo> {

        val legendAssemblerByTitle = LinkedHashMap<String, LegendAssembler>()
        val colorBarAssemblerByTitle = LinkedHashMap<String, ColorBarAssembler>()

        for (stitchedLayers in stitchedLayersList) {
            val layerConstantByAes = HashMap<Aes<*>, Any>()
            for (aes in stitchedLayers.renderedAes()) {
                if (stitchedLayers.hasConstant(aes)) {
                    layerConstantByAes[aes] = stitchedLayers.getConstant(aes)!!
                }
            }

            val layerBindingsByScaleName = LinkedHashMap<String, MutableList<VarBinding>>()
            val aesList = mappedRenderedAesToCreateGuides(stitchedLayers, guideOptionsMap)
            for (aes in aesList) {
                var colorBar = false
                val binding = stitchedLayers.getBinding(aes)
                val scale = stitchedLayers.getScale(aes)
                val scaleName = scale.name
                if (guideOptionsMap.containsKey(aes)) {
                    val guideOptions = guideOptionsMap[aes]
                    if (guideOptions is ColorBarOptions) {
                        checkFitsColorBar(binding.aes, scale)
                        colorBar = true
                        @Suppress("UNCHECKED_CAST")
                        val colorScale = scale as Scale<Color>
                        colorBarAssemblerByTitle[scaleName] = createColorBarAssembler(
                            scaleName, binding.aes,
                            transformedDomainByAes, colorScale, guideOptions, theme
                        )
                    }
                } else if (fitsColorBar(binding.aes, scale)) {
                    colorBar = true
                    @Suppress("UNCHECKED_CAST")
                    val colorScale = scale as Scale<Color>
                    colorBarAssemblerByTitle[scaleName] = createColorBarAssembler(
                        scaleName, binding.aes,
                        transformedDomainByAes, colorScale, null, theme
                    )
                }

                if (!colorBar) {
                    layerBindingsByScaleName.getOrPut(scaleName) { ArrayList() }.add(binding)
                }
            }

            for (scaleName in layerBindingsByScaleName.keys) {
                val legendAssembler = legendAssemblerByTitle.getOrPut(scaleName) {
                    LegendAssembler(
                        scaleName,
                        guideOptionsMap,
                        theme
                    )
                }

                val varBindings = layerBindingsByScaleName[scaleName]!!
                val legendKeyFactory = stitchedLayers.legendKeyElementFactory
                val aestheticsDefaults = stitchedLayers.aestheticsDefaults
                legendAssembler.addLayer(
                    legendKeyFactory,
                    varBindings,
                    layerConstantByAes,
                    aestheticsDefaults,
                    stitchedLayers.getScaleMap(),
                    transformedDomainByAes
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

    fun createPlotLayout(tileLayout: TileLayout, facets: PlotFacets): PlotLayout {
        if (!facets.isDefined) {
            return SingleTilePlotLayout(tileLayout)
        }

        return FacetGridPlotLayout(
            facets,
            tileLayout
        )
    }


    fun computePlotDryRunXYRanges(layersByTile: List<List<GeomLayer>>): Pair<ClosedRange<Double>, ClosedRange<Double>> {
        // 'dry run' aesthetics use 'identity' mappers for positional aes (because the plot size is not yet determined)
        val dryRunAestheticsByTileLayer = HashMap<GeomLayer, Aesthetics>()
        for (tileLayers in layersByTile) {
            for (layer in tileLayers) {
                val aesthetics = PlotUtil.createLayerDryRunAesthetics(layer)
                dryRunAestheticsByTileLayer[layer] = aesthetics
            }
        }

        // the "scale map" is shared by all layers.
        val layers0 = layersByTile[0]
        val scaleMap = layers0[0].scaleMap
        val xScale = scaleMap[Aes.X]
        val yScale = scaleMap[Aes.Y]
        var xInitialRange: ClosedRange<Double>? = RangeUtil.initialRange(xScale)
        var yInitialRange: ClosedRange<Double>? = RangeUtil.initialRange(yScale)

        var xRangeOverall: ClosedRange<Double>? = null
        var yRangeOverall: ClosedRange<Double>? = null
        for (tileLayers in layersByTile) {
            for (layer in tileLayers) {
                // use dry-run aesthetics to estimate ranges
                val aesthetics = dryRunAestheticsByTileLayer.getValue(layer)
                // adjust X/Y range with 'pos adjustment' and 'expands'
                val xyRanges = computeLayerDryRunXYRanges(layer, aesthetics)

                val xRangeLayer = updateRange(xInitialRange, xyRanges.first)
                val yRangeLayer = updateRange(yInitialRange, xyRanges.second)

                xRangeOverall = updateRange(xRangeLayer, xRangeOverall)
                yRangeOverall = updateRange(yRangeLayer, yRangeOverall)
            }
        }

        // 'expand' ranges and include '0' if necessary
        xRangeOverall = RangeUtil.expandRange(xRangeOverall, Aes.X, xScale, layers0)
        yRangeOverall = RangeUtil.expandRange(yRangeOverall, Aes.Y, yScale, layers0)

        // validate XY ranges
        xRangeOverall = SeriesUtil.ensureApplicableRange(xRangeOverall)
        yRangeOverall = SeriesUtil.ensureApplicableRange(yRangeOverall)
        return Pair(
            xRangeOverall,
            yRangeOverall
        )
    }

    private object RangeUtil {
        fun initialRange(scale: Scale<Double>): ClosedRange<Double>? {
            var initialRange: ClosedRange<Double>? = null

            // Take in account:
            // - scales domain if defined
            // - scales breaks if defined
            if (scale.isContinuousDomain) {
                initialRange = updateRange(
                    ScaleUtil.transformedDefinedLimits(scale).toList().filter { it.isFinite() },
                    initialRange
                )
            }

            if (scale.hasBreaks()) {
                val scaleBreaks = scale.getScaleBreaks()
                initialRange = updateRange(
//                    ScaleUtil.breaksTransformed(scale),
                    scaleBreaks.transformedValues,
                    initialRange
                )
            }
            return initialRange
        }

        fun expandRange(
            range: ClosedRange<Double>?,
            aes: Aes<Double>,
            scale: Scale<*>,
            layers: List<GeomLayer>
        ): ClosedRange<Double>? {
            val includeZero = layers.any { it.rangeIncludesZero(aes) }

            @Suppress("NAME_SHADOWING")
            val range = when (includeZero) {
                true -> updateRange(ClosedRange.singleton(0.0), range)
                false -> range
            }

            return PlotUtil.rangeWithExpand(range, scale, includeZero)
        }
    }
}
