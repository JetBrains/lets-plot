package jetbrains.datalore.visualization.plot.gog.plot.assemble

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.visualization.plot.gog.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.scale.Scale2
import jetbrains.datalore.visualization.plot.gog.core.scale.Scales
import jetbrains.datalore.visualization.plot.gog.plot.*
import jetbrains.datalore.visualization.plot.gog.plot.coord.CoordProvider
import jetbrains.datalore.visualization.plot.gog.plot.layout.*
import jetbrains.datalore.visualization.plot.gog.plot.theme.Theme

class PlotAssembler private constructor(layersByTile: List<List<GeomLayer>>, private val myCoordProvider: CoordProvider, private val myTheme: Theme) {

    private val myLayersByTile = ArrayList<List<GeomLayer>>()
    private val myContainsLivemap: Boolean

    var facets: PlotFacets? = null
    private var myTitle: String? = null
    private var myGuideOptionsMap: Map<Aes<*>, GuideOptions> = HashMap()
    private var myAxisEnabled: Boolean = false
    private var myLegendsEnabled = true
    private var myInteractionsEnabled = true
    private var myCanvasEnabled = false

    val layersByTile: List<List<GeomLayer>>
        get() = myLayersByTile

    private val isFacetLayout: Boolean
        get() = hasFacets() && facets!!.isDefined

    init {
        for (plotLayers in layersByTile) {
            myLayersByTile.add(ArrayList(plotLayers))
        }
        myContainsLivemap = GeomLayerListUtil.containsLivemapLayer(myLayersByTile)
        myAxisEnabled = !myContainsLivemap  // no axis on livemap
    }

    fun setTitle(title: String?) {
        myTitle = title
    }

    private fun hasFacets(): Boolean {
        return facets != null
    }

    private fun hasLayers(): Boolean {
        for (tileLayers in myLayersByTile) {
            if (!tileLayers.isEmpty()) {
                return true
            }
        }
        return false
    }

    fun createPlot(): Plot {
        checkState(hasLayers(), "No layers in plot")

        val legendsBoxInfos = if (myLegendsEnabled)
            PlotAssemblerUtil.createLegends(myLayersByTile, myGuideOptionsMap, myTheme.legend())
        else
            emptyList<LegendBoxInfo>()

        // share first X/Y scale among all layers
        var xScaleProto = GeomLayerListUtil.anyBoundXScale(myLayersByTile) as Scale2<Double>?
        if (xScaleProto == null) {
            xScaleProto = Scales.continuousDomain("x", Aes.X)
        }
        var yScaleProto = GeomLayerListUtil.anyBoundYScale(myLayersByTile) as Scale2<Double>?
        if (yScaleProto == null) {
            yScaleProto = Scales.continuousDomain("y", Aes.Y)
        }

        if (myContainsLivemap) {
            // build 'live map' plot:
            //  - skip X/Y scale training
            //  - ignore coord provider
            //  - plot layout without axes
            val plotLayout = PlotAssemblerUtil.createPlotLayout(LivemapTileLayout(), isFacetLayout, facets)
            return createXYPlot(xScaleProto, yScaleProto, plotLayout, legendsBoxInfos)
        }

        // train scales
        val rangeByAes = PlotAssemblerUtil.rangeByNumericAes(myLayersByTile)

        val xDomain = rangeByAes.get(Aes.X)
        val yDomain = rangeByAes.get(Aes.Y)
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
                isFacetLayout, facets)
        if (!myAxisEnabled) {
            plotLayout.setPadding(0.0, 0.0, 0.0, 0.0)
        }

        return createXYPlot(xScaleProto, yScaleProto, plotLayout, legendsBoxInfos)
    }


    private fun createXYPlot(
            xScaleProto: Scale2<Double>, yScaleProto: Scale2<Double>,
            plotLayout: PlotLayout, legendBoxInfos: List<LegendBoxInfo>): Plot {

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
        plotBuilder.canvasEnabled(myCanvasEnabled)
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

    fun enableCanvas() {
        myCanvasEnabled = true
    }

    companion object {
        fun singleTile(plotLayers: List<GeomLayer>, coordProvider: CoordProvider, theme: Theme): PlotAssembler {
            val layersByTile = ArrayList<List<GeomLayer>>()
            layersByTile.add(plotLayers)
            return multiTile(layersByTile, coordProvider, theme)
        }

        fun multiTile(layersByTile: List<List<GeomLayer>>, coordProvider: CoordProvider, theme: Theme): PlotAssembler {
            return PlotAssembler(layersByTile, coordProvider, theme)
        }
    }
}
