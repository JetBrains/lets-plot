package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.layout.*
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.plot.common.data.SeriesUtil

class PlotAssembler private constructor(
    layersByTile: List<List<GeomLayer>>,
    private val myCoordProvider: jetbrains.datalore.plot.builder.coord.CoordProvider,
    private val myTheme: Theme) {

    private val myLayersByTile = ArrayList<List<GeomLayer>>()
    private val myContainsLiveMap: Boolean

    var facets: PlotFacets? = null
    private var myTitle: String? = null
    private var myGuideOptionsMap: Map<Aes<*>, GuideOptions> = HashMap()
    private var myAxisEnabled: Boolean = false
    private var myLegendsEnabled = true
    private var myInteractionsEnabled = true

    val layersByTile: List<List<GeomLayer>>
        get() = myLayersByTile

    private val isFacetLayout: Boolean
        get() = hasFacets() && facets!!.isDefined

    init {
        for (plotLayers in layersByTile) {
            myLayersByTile.add(ArrayList(plotLayers))
        }
        myContainsLiveMap = myLayersByTile.flatten().any(GeomLayer::isLiveMap)
        myAxisEnabled = !myContainsLiveMap  // no axis on livemap
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

    fun createPlot(): jetbrains.datalore.plot.builder.Plot {
        checkState(hasLayers(), "No layers in plot")

        val legendsBoxInfos = if (myLegendsEnabled)
            PlotAssemblerUtil.createLegends(
                myLayersByTile,
                myGuideOptionsMap,
                myTheme.legend()
            )
        else
            emptyList<LegendBoxInfo>()

        // share first X/Y scale among all layers
        var xScaleProto = jetbrains.datalore.plot.builder.GeomLayerListUtil.anyBoundXScale(myLayersByTile) as Scale<Double>?
        if (xScaleProto == null) {
            xScaleProto = Scales.continuousDomain("x", Aes.X)
        }
        var yScaleProto = jetbrains.datalore.plot.builder.GeomLayerListUtil.anyBoundYScale(myLayersByTile) as Scale<Double>?
        if (yScaleProto == null) {
            yScaleProto = Scales.continuousDomain("y", Aes.Y)
        }

        if (myContainsLiveMap) {
            // build 'live map' plot:
            //  - skip X/Y scale training
            //  - ignore coord provider
            //  - plot layout without axes
            val plotLayout = PlotAssemblerUtil.createPlotLayout(
                LivemapTileLayout(),
                isFacetLayout,
                facets
            )
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
            isFacetLayout, facets
        )
        if (!myAxisEnabled) {
            plotLayout.setPadding(0.0, 0.0, 0.0, 0.0)
        }

        return createXYPlot(xScaleProto, yScaleProto, plotLayout, legendsBoxInfos)
    }


    private fun createXYPlot(
        xScaleProto: Scale<Double>, yScaleProto: Scale<Double>,
        plotLayout: PlotLayout, legendBoxInfos: List<LegendBoxInfo>): jetbrains.datalore.plot.builder.Plot {

        val plotBuilder = jetbrains.datalore.plot.builder.PlotBuilder(myTheme)
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
        fun singleTile(plotLayers: List<GeomLayer>, coordProvider: jetbrains.datalore.plot.builder.coord.CoordProvider, theme: Theme): PlotAssembler {
            val layersByTile = ArrayList<List<GeomLayer>>()
            layersByTile.add(plotLayers)
            return multiTile(
                layersByTile,
                coordProvider,
                theme
            )
        }

        fun multiTile(layersByTile: List<List<GeomLayer>>, coordProvider: jetbrains.datalore.plot.builder.coord.CoordProvider, theme: Theme): PlotAssembler {
            return PlotAssembler(layersByTile, coordProvider, theme)
        }
    }
}
