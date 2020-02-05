/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.GeomLayerListUtil
import jetbrains.datalore.plot.builder.Plot
import jetbrains.datalore.plot.builder.PlotBuilder
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.layout.*
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.plot.common.data.SeriesUtil

class PlotAssembler private constructor(
    layersByTile: List<List<GeomLayer>>,
    private val myCoordProvider: CoordProvider,
    private val myTheme: Theme
) {

    private val myLayersByTile = ArrayList<List<GeomLayer>>()
    val containsLiveMap: Boolean

    var facets: PlotFacets = PlotFacets.undefined()
    private var myTitle: String? = null
    private var myGuideOptionsMap: Map<Aes<*>, GuideOptions> = HashMap()
    private var myAxisEnabled: Boolean
    private var myLegendsEnabled = true
    private var myInteractionsEnabled = true

    val layersByTile: List<List<GeomLayer>>
        get() = myLayersByTile

    private val isFacetLayout: Boolean
        get() = hasFacets()

    init {
        for (plotLayers in layersByTile) {
            myLayersByTile.add(ArrayList(plotLayers))
        }
        containsLiveMap = myLayersByTile.flatten().any(GeomLayer::isLiveMap)
        myAxisEnabled = !containsLiveMap  // no axis on livemap
    }

    fun setTitle(title: String?) {
        myTitle = title
    }

    private fun hasFacets(): Boolean {
        return facets.isDefined
    }

    private fun hasLayers(): Boolean {
        for (tileLayers in myLayersByTile) {
            if (tileLayers.isNotEmpty()) {
                return true
            }
        }
        return false
    }

    fun createPlot(): Plot {
        checkState(hasLayers(), "No layers in plot")

        val legendsBoxInfos = if (myLegendsEnabled)
            PlotAssemblerUtil.createLegends(
                myLayersByTile,
                myGuideOptionsMap,
                myTheme.legend()
            )
        else
            emptyList()

        // share first X/Y scale among all layers
        @Suppress("UNCHECKED_CAST")
        var xScaleProto = GeomLayerListUtil.anyBoundXScale(myLayersByTile) as Scale<Double>?
        if (xScaleProto == null) {
            xScaleProto = Scales.continuousDomain("x", Aes.X)
        }
        @Suppress("UNCHECKED_CAST")
        var yScaleProto = GeomLayerListUtil.anyBoundYScale(myLayersByTile) as Scale<Double>?
        if (yScaleProto == null) {
            yScaleProto = Scales.continuousDomain("y", Aes.Y)
        }

        if (containsLiveMap) {
            // build 'live map' plot:
            //  - skip X/Y scale training
            //  - ignore coord provider
            //  - plot layout without axes
            val plotLayout = PlotAssemblerUtil.createPlotLayout(
                LiveMapTileLayout(),
                isFacetLayout,
                facets
            )
            return createXYPlot(xScaleProto, yScaleProto, plotLayout, legendsBoxInfos, hasLiveMap = true)
        }

        // train scales
        val rangeByAes = PlotAssemblerUtil.rangeByNumericAes(myLayersByTile)

        val xDomain = rangeByAes.get(Aes.X)
        val yDomain = rangeByAes[Aes.Y]
        checkState(xDomain != null, "X domain not defined")
        checkState(yDomain != null, "Y domain not defined")
        checkState(SeriesUtil.isFinite(xDomain!!.lowerEndpoint()), "X domain lower end: " + xDomain.lowerEndpoint())
        checkState(SeriesUtil.isFinite(xDomain.upperEndpoint()), "X domain upper end: " + xDomain.upperEndpoint())
        checkState(SeriesUtil.isFinite(yDomain!!.lowerEndpoint()), "Y domain lower end: " + yDomain.lowerEndpoint())
        checkState(SeriesUtil.isFinite(yDomain.upperEndpoint()), "Y domain upper end: " + yDomain.upperEndpoint())

        val xAxisLayout: AxisLayout
        val yAxisLayout: AxisLayout
        if (myAxisEnabled) {
            xAxisLayout = PlotAxisLayout.bottom(xScaleProto, xDomain, yDomain, myCoordProvider, myTheme.axisX())
            yAxisLayout = PlotAxisLayout.left(yScaleProto, xDomain, yDomain, myCoordProvider, myTheme.axisY())
        } else {
            xAxisLayout = EmptyAxisLayout.bottom(xDomain, yDomain)
            yAxisLayout = EmptyAxisLayout.left(xDomain, yDomain)
        }

        val plotLayout = PlotAssemblerUtil.createPlotLayout(
            XYPlotTileLayout(xAxisLayout, yAxisLayout),
            isFacetLayout, facets
        )
        if (!myAxisEnabled) {
            plotLayout.setPadding(0.0, 0.0, 0.0, 0.0)
        }

        return createXYPlot(xScaleProto, yScaleProto, plotLayout, legendsBoxInfos)
    }


    private fun createXYPlot(
        xScaleProto: Scale<Double>,
        yScaleProto: Scale<Double>,
        plotLayout: PlotLayout,
        legendBoxInfos: List<LegendBoxInfo>,
        hasLiveMap: Boolean = false
    ): Plot {

        val plotBuilder = PlotBuilder(myTheme)
        plotBuilder.setTitle(myTitle)
        plotBuilder.scaleXProto(xScaleProto)
        plotBuilder.scaleYProto(yScaleProto)
        plotBuilder.setAxisTitleBottom(xScaleProto.name)
        plotBuilder.setAxisTitleLeft(yScaleProto.name)
        plotBuilder.setCoordProvider(myCoordProvider)
        for (legendBoxInfo in legendBoxInfos) {
            plotBuilder.addLegendBoxInfo(legendBoxInfo)
        }
        for (panelLayers in myLayersByTile) {
            plotBuilder.addTileLayers(panelLayers)
        }

        plotBuilder.setPlotLayout(plotLayout)
        plotBuilder.axisEnabled(myAxisEnabled)
        plotBuilder.interactionsEnabled(myInteractionsEnabled)
        plotBuilder.setLiveMap(hasLiveMap)
        return plotBuilder.build()
    }

    fun setGuideOptionsMap(guideOptionsMap: Map<Aes<*>, GuideOptions>) {
        myGuideOptionsMap = guideOptionsMap
    }

    fun disableAxis() {
        myAxisEnabled = false
    }

    fun disableLegends() {
        myLegendsEnabled = false
    }

    fun disableInteractions() {
        myInteractionsEnabled = false
    }

    companion object {
        fun singleTile(
            plotLayers: List<GeomLayer>,
            coordProvider: CoordProvider,
            theme: Theme
        ): PlotAssembler {
            val layersByTile = ArrayList<List<GeomLayer>>()
            layersByTile.add(plotLayers)
            return multiTile(
                layersByTile,
                coordProvider,
                theme
            )
        }

        fun multiTile(
            layersByTile: List<List<GeomLayer>>,
            coordProvider: CoordProvider,
            theme: Theme
        ): PlotAssembler {
            return PlotAssembler(layersByTile, coordProvider, theme)
        }
    }
}
