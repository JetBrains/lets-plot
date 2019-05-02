package jetbrains.datalore.visualization.plot.gog.plot

import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.gcommon.base.Strings
import jetbrains.datalore.base.observable.collections.Collections.unmodifiableList
import jetbrains.datalore.visualization.plot.gog.core.scale.Scale2
import jetbrains.datalore.visualization.plot.gog.plot.coord.CoordProvider
import jetbrains.datalore.visualization.plot.gog.plot.layout.LegendBoxInfo
import jetbrains.datalore.visualization.plot.gog.plot.layout.PlotLayout
import jetbrains.datalore.visualization.plot.gog.plot.theme.Theme

class PlotBuilder(private val myTheme: Theme) {
    private val myLayersByTile = ArrayList<List<GeomLayer>>()
    private var myTitle: String? = null
    private var myCoordProvider: CoordProvider? = null
    private var myLayout: PlotLayout? = null
    private var myAxisTitleLeft: String? = null
    private var myAxisTitleBottom: String? = null
    private val myLegendBoxInfos = ArrayList<LegendBoxInfo>()
    private var myScaleXProto: Scale2<Double>? = null
    private var myScaleYProto: Scale2<Double>? = null
    private var myAxisEnabled = true
    private var myInteractionsEnabled = true
    private var myCanvasEnabled = false

    fun setTitle(title: String?) {
        myTitle = title
    }

    fun setAxisTitleLeft(v: String) {
        myAxisTitleLeft = v
    }

    fun setAxisTitleBottom(v: String) {
        myAxisTitleBottom = v
    }

    fun setCoordProvider(coordProvider: CoordProvider): PlotBuilder {
        myCoordProvider = coordProvider
        return this
    }

    fun addTileLayers(tileLayers: List<GeomLayer>): PlotBuilder {
        myLayersByTile.add(tileLayers)
        return this
    }

    fun setPlotLayout(layout: PlotLayout): PlotBuilder {
        myLayout = layout
        return this
    }

    fun addLegendBoxInfo(v: LegendBoxInfo): PlotBuilder {
        myLegendBoxInfos.add(v)
        return this
    }

    fun scaleXProto(scaleXProto: Scale2<Double>): PlotBuilder {
        myScaleXProto = scaleXProto
        return this
    }

    fun scaleYProto(scaleYProto: Scale2<Double>): PlotBuilder {
        myScaleYProto = scaleYProto
        return this
    }

    fun axisEnabled(b: Boolean): PlotBuilder {
        myAxisEnabled = b
        return this
    }

    fun interactionsEnabled(b: Boolean): PlotBuilder {
        myInteractionsEnabled = b
        return this
    }

    fun canvasEnabled(b: Boolean): PlotBuilder {
        myCanvasEnabled = b
        return this
    }

    fun build(): Plot {
        return MyPlot(this)
    }


    private class MyPlot internal constructor(b: PlotBuilder) : Plot(b.myTheme) {
        override val scaleXProto: Scale2<Double>?
        override val scaleYProto: Scale2<Double>?

        private val myTitle: String?
        private val myAxisTitleLeft: String?
        private val myAxisTitleBottom: String?
        private val myAxisXTitleEnabled: Boolean
        private val myAxisYTitleEnabled: Boolean
        override val coordProvider: CoordProvider

        private val myLayersByTile: List<List<GeomLayer>>
        private val myLayout: PlotLayout?
        private val myLegendBoxInfos: List<LegendBoxInfo>

        override val isAxisEnabled: Boolean
        override val isInteractionsEnabled: Boolean
        override val isCanvasEnabled: Boolean

        override val title: String
            get() {
                Preconditions.checkArgument(hasTitle(), "No title")
                return myTitle!!
            }

        override val axisTitleLeft: String
            get() {
                Preconditions.checkArgument(hasAxisTitleLeft(), "No left axis title")
                return myAxisTitleLeft!!
            }

        override val axisTitleBottom: String
            get() {
                Preconditions.checkArgument(hasAxisTitleBottom(), "No bottom axis title")
                return myAxisTitleBottom!!
            }

        override val legendBoxInfos: List<LegendBoxInfo>
            get() = myLegendBoxInfos

        init {
            checkState(b.myScaleXProto != null)
            checkState(b.myScaleYProto != null)

            scaleXProto = b.myScaleXProto
            scaleYProto = b.myScaleYProto

            myTitle = b.myTitle
            myAxisTitleLeft = b.myAxisTitleLeft
            myAxisTitleBottom = b.myAxisTitleBottom
            myAxisXTitleEnabled = b.myTheme.axisX().showTitle()
            myAxisYTitleEnabled = b.myTheme.axisY().showTitle()
            coordProvider = b.myCoordProvider!!
            myLayersByTile = unmodifiableList(b.myLayersByTile)
            myLayout = b.myLayout
            myLegendBoxInfos = unmodifiableList(b.myLegendBoxInfos)

            isAxisEnabled = b.myAxisEnabled
            isInteractionsEnabled = b.myInteractionsEnabled
            isCanvasEnabled = b.myCanvasEnabled

            if (isInteractionsEnabled) {
                for (layers in myLayersByTile) {
                    for (layer in layers) {
                        if (layer.locatorLookupSpec == null) {
                            throw IllegalStateException("Plot interactions enabled but layer's lookup spec is not defined")
                        }
                        if (layer.tooltipAesSpec == null) {
                            throw IllegalStateException("Plot interactions enabled but layer's tooltip aesthetics spec is not defined")
                        }
                    }
                }
            }
        }

        override fun hasTitle(): Boolean {
            return !Strings.isNullOrEmpty(myTitle)
        }

        override fun hasAxisTitleLeft(): Boolean {
            return myAxisYTitleEnabled && !Strings.isNullOrEmpty(myAxisTitleLeft)
        }

        override fun hasAxisTitleBottom(): Boolean {
            return myAxisXTitleEnabled && !Strings.isNullOrEmpty(myAxisTitleBottom)
        }

        override fun tileLayers(tileIndex: Int): List<GeomLayer> {
            return myLayersByTile[tileIndex]
        }

        override fun plotLayout(): PlotLayout {
            return myLayout!!
        }
    }
}
