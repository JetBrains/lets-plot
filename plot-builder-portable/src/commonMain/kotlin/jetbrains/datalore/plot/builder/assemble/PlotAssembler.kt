/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.plot.FeatureSwitch.FLIP_AXIS
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.builder.*
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.layout.LegendBoxInfo
import jetbrains.datalore.plot.builder.layout.LiveMapTileLayout
import jetbrains.datalore.plot.builder.layout.PlotLayout
import jetbrains.datalore.plot.builder.theme.Theme

class PlotAssembler private constructor(
    private val scaleByAes: TypedScaleMap,
    val layersByTile: List<List<GeomLayer>>,
    private val myCoordProvider: CoordProvider,
    private val myTheme: Theme
) {

    val containsLiveMap: Boolean

    var facets: PlotFacets = PlotFacets.undefined()
    private var myTitle: String? = null
    private var myGuideOptionsMap: Map<Aes<*>, GuideOptions> = HashMap()
    private var myAxisEnabled: Boolean
    private var myLegendsEnabled = true
    private var myInteractionsEnabled = true

    init {
        containsLiveMap = layersByTile.flatten().any(GeomLayer::isLiveMap)
        myAxisEnabled = !containsLiveMap  // no axis on livemap
    }

    fun setTitle(title: String?) {
        myTitle = title
    }

    private fun hasLayers(): Boolean {
        for (tileLayers in layersByTile) {
            if (tileLayers.isNotEmpty()) {
                return true
            }
        }
        return false
    }

    fun createPlot(): Plot {
        require(hasLayers()) { "No layers in plot" }

        val legendsBoxInfos = when {
            myLegendsEnabled -> PlotAssemblerUtil.createLegends(
                layersByTile,
                myGuideOptionsMap,
                myTheme.legend()
            )
            else -> emptyList()
        }

        if (containsLiveMap) {
            // build 'live map' plot:
            //  - skip X/Y scale training
            //  - ignore coord provider
            //  - plot layout without axes
            val plotLayout = PlotAssemblerUtil.createPlotLayout(
                LiveMapTileLayout(),
                facets
            )

            val fOrProvider = BogusFrameOfReferenceProvider()
            return createPlot(fOrProvider, plotLayout, legendsBoxInfos, hasLiveMap = true)
        }

        // train X/Y scales
        val (xAesRange, yAesRange) = PlotAssemblerUtil.computePlotDryRunXYRanges(layersByTile)

        val fOrProvider = SquareFrameOfReferenceProvider(
            scaleByAes[Aes.X],
            scaleByAes[Aes.Y],
            xAesRange,
            yAesRange,
            myCoordProvider,
            myTheme,
            FLIP_AXIS
        )
        val plotLayout = PlotAssemblerUtil.createPlotLayout(
            fOrProvider.createTileLayout(),
            facets
        )


        if (!myAxisEnabled) {
            // ToDo: we never arrive here
            plotLayout.setPadding(0.0, 0.0, 0.0, 0.0)
        }

        return createPlot(fOrProvider, plotLayout, legendsBoxInfos)
    }

    private fun createPlot(
        fOrProvider: TileFrameOfReferenceProvider,
        plotLayout: PlotLayout,
        legendBoxInfos: List<LegendBoxInfo>,
        hasLiveMap: Boolean = false
    ): Plot {

        val plotBuilder = PlotBuilder(myTheme)
        plotBuilder.setTitle(myTitle)
        plotBuilder.tileFrameOfReferenceProvider(fOrProvider)

        for (legendBoxInfo in legendBoxInfos) {
            plotBuilder.addLegendBoxInfo(legendBoxInfo)
        }
        for (panelLayers in layersByTile) {
            plotBuilder.addTileLayers(panelLayers)
        }

        plotBuilder.plotLayout(plotLayout)
        plotBuilder.axisEnabled(myAxisEnabled)
        plotBuilder.interactionsEnabled(myInteractionsEnabled)
        plotBuilder.setLiveMap(hasLiveMap)
        return plotBuilder.build()
    }

    fun setGuideOptionsMap(guideOptionsMap: Map<Aes<*>, GuideOptions>) {
        myGuideOptionsMap = guideOptionsMap
    }

    fun disableLegends() {
        myLegendsEnabled = false
    }

    fun disableInteractions() {
        myInteractionsEnabled = false
    }

    companion object {
        fun singleTile(
            scaleByAes: TypedScaleMap,
            plotLayers: List<GeomLayer>,
            coordProvider: CoordProvider,
            theme: Theme
        ): PlotAssembler {
            val layersByTile = ArrayList<List<GeomLayer>>()
            layersByTile.add(plotLayers)
            return multiTile(
                scaleByAes,
                layersByTile,
                coordProvider,
                theme
            )
        }

        fun multiTile(
            scaleByAes: TypedScaleMap,
            layersByTile: List<List<GeomLayer>>,
            coordProvider: CoordProvider,
            theme: Theme
        ): PlotAssembler {
            @Suppress("NAME_SHADOWING")
            val theme = if (layersByTile.size > 1) {
                theme.multiTile()
            } else {
                theme
            }

            return PlotAssembler(
                scaleByAes,
                layersByTile,
                coordProvider,
                theme
            )
        }
    }
}
