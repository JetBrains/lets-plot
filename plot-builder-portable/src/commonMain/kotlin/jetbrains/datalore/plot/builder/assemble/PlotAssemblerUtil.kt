/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.gcommon.collect.Iterables
import jetbrains.datalore.base.values.Color
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
import jetbrains.datalore.plot.builder.assemble.PlotGuidesAssemblerUtil.guideDataRangeByAes
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

        val dataRangeByAes = HashMap<Aes<*>, ClosedRange<Double>>()
        for (stitchedPlotLayers in stitchedLayersList) {
            val layerDataRangeByAes = guideDataRangeByAes(stitchedPlotLayers, guideOptionsMap)
            for (aes in layerDataRangeByAes.keys) {
                val range = layerDataRangeByAes[aes]
                updateAesRangeMap(
                    aes,
                    range,
                    dataRangeByAes
                )
            }
        }

        return createLegends(
            stitchedLayersList,
            dataRangeByAes,
            guideOptionsMap,
            theme
        )
    }

    private fun createLegends(
        stitchedLayersList: List<StitchedPlotLayers>,
        dataRangeByAes: Map<Aes<*>, ClosedRange<Double>>,
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
                            dataRangeByAes, colorScale, guideOptions, theme
                        )
                    }
                } else if (fitsColorBar(binding.aes, scale)) {
                    colorBar = true
                    @Suppress("UNCHECKED_CAST")
                    val colorScale = scale as Scale<Color>
                    colorBarAssemblerByTitle[scaleName] = createColorBarAssembler(
                        scaleName, binding.aes,
                        dataRangeByAes, colorScale, null, theme
                    )
                }

                if (!colorBar) {
                    if (!layerBindingsByScaleName.containsKey(scaleName)) {
                        layerBindingsByScaleName[scaleName] = ArrayList()
                    }
                    layerBindingsByScaleName[scaleName]!!.add(binding)
                }
            }

            for (scaleName in layerBindingsByScaleName.keys) {
                if (!legendAssemblerByTitle.containsKey(scaleName)) {
                    legendAssemblerByTitle[scaleName] =
                        LegendAssembler(
                            scaleName,
                            guideOptionsMap,
                            theme
                        )
                }

                val varBindings = layerBindingsByScaleName[scaleName]!!
                val legendKeyFactory = stitchedLayers.legendKeyElementFactory
                val aestheticsDefaults = stitchedLayers.aestheticsDefaults
                val legendAssembler = legendAssemblerByTitle[scaleName]!!
                legendAssembler.addLayer(
                    legendKeyFactory,
                    varBindings,
                    layerConstantByAes,
                    aestheticsDefaults,
                    stitchedLayers.getScaleMap(),
                    dataRangeByAes
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


    fun rangeByNumericAes(layersByTile: List<List<GeomLayer>>): Map<Aes<*>, ClosedRange<Double>> {
        val rangeByAes = HashMap<Aes<*>, ClosedRange<Double>>()

        // 'dry run' aesthetics use 'identity' mappers for positional aes (because the plot size is not yet determined)
        val dryRunAestheticsByTileLayer = HashMap<GeomLayer, Aesthetics>()
        for (tileLayers in layersByTile) {
            for (layer in tileLayers) {
                val aesthetics = PlotUtil.createLayerDryRunAesthetics(layer)
                dryRunAestheticsByTileLayer[layer] = aesthetics
            }
        }

        for (layers in layersByTile) {
            for (layer in layers) {
                val numericAes = Aes.numeric(layer.renderedAes())

                // use dry-run aesthetics to estimate ranges
                val aesthetics = dryRunAestheticsByTileLayer[layer]!!
                // adjust X/Y range with 'pos adjustment' and 'expands'
                val xyRanges = computeLayerDryRunXYRanges(layer, aesthetics)

                // flags
                var isXCalculated = false
                var isYCalculated = false

                for (aes in numericAes) {
                    var layerAesRange: ClosedRange<Double>? = null
                    // take in account:
                    // - scales domain if defined
                    // - scales breaks if defined
                    if (layer.hasBinding(aes)) {
                        val scale = layer.scaleMap[aes]
                        if (scale.isContinuousDomain) {
                            layerAesRange =
                                updateRange(
                                    ScaleUtil.transformedDefinedLimits(scale), layerAesRange
                                )
                        }

                        if (scale.hasBreaks()) {
                            layerAesRange =
                                updateRange(
                                    ScaleUtil.breaksTransformed(scale), layerAesRange
                                )
                        }
                    }

                    // numerical range for data
                    val realAes: Aes<Double>
                    if (Aes.isAffectingScaleX(aes)) {
                        if (isXCalculated) {
                            continue
                        }
                        isXCalculated = true
                        realAes = Aes.X
                        val xRange = xyRanges.first
                        layerAesRange =
                            updateRange(
                                xRange,
                                layerAesRange
                            )
                        layerAesRange =
                            PlotUtil.rangeWithExpand(layer, aes, layerAesRange)
                    } else if (Aes.isAffectingScaleY(aes)) {
                        if (isYCalculated) {
                            continue
                        }
                        isYCalculated = true
                        realAes = Aes.Y
                        val yRange = xyRanges.second
                        layerAesRange =
                            updateRange(
                                yRange,
                                layerAesRange
                            )
                        layerAesRange =
                            PlotUtil.rangeWithExpand(layer, aes, layerAesRange)
                    } else {
                        realAes = aes
                        layerAesRange =
                            updateRange(
                                aesthetics.range(aes),
                                layerAesRange
                            )
                    }

                    // include zero if necessary
                    if (layer.rangeIncludesZero(aes)) {
                        layerAesRange =
                            updateRange(
                                ClosedRange.singleton(0.0), layerAesRange
                            )
                    }

                    // update range map
                    updateAesRangeMap(
                        realAes,
                        layerAesRange,
                        rangeByAes
                    )
                }
            }
        }

        // validate XY ranges
        for (aes in listOf(Aes.X, Aes.Y)) {
            rangeByAes[aes] = SeriesUtil.ensureApplicableRange(rangeByAes[aes])
        }

        return rangeByAes
    }
}
