package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.base.gcommon.base.Strings
import jetbrains.datalore.plot.builder.layout.LegendBoxInfo
import jetbrains.datalore.plot.builder.layout.PlotLayout
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.visualization.plot.base.Scale

class PlotBuilder(private val myTheme: Theme) {
    private val myLayersByTile = ArrayList<List<jetbrains.datalore.plot.builder.GeomLayer>>()
    private var myTitle: String? = null
    private lateinit var myCoordProvider: jetbrains.datalore.plot.builder.coord.CoordProvider
    private var myLayout: PlotLayout? = null
    private var myAxisTitleLeft: String? = null
    private var myAxisTitleBottom: String? = null
    private val myLegendBoxInfos = ArrayList<LegendBoxInfo>()
    private lateinit var myScaleXProto: Scale<Double>
    private lateinit var myScaleYProto: Scale<Double>
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

    fun setCoordProvider(coordProvider: jetbrains.datalore.plot.builder.coord.CoordProvider): jetbrains.datalore.plot.builder.PlotBuilder {
        myCoordProvider = coordProvider
        return this
    }

    fun addTileLayers(tileLayers: List<jetbrains.datalore.plot.builder.GeomLayer>): jetbrains.datalore.plot.builder.PlotBuilder {
        myLayersByTile.add(ArrayList(tileLayers))
        return this
    }

    fun setPlotLayout(layout: PlotLayout): jetbrains.datalore.plot.builder.PlotBuilder {
        myLayout = layout
        return this
    }

    fun addLegendBoxInfo(v: LegendBoxInfo): jetbrains.datalore.plot.builder.PlotBuilder {
        myLegendBoxInfos.add(v)
        return this
    }

    fun scaleXProto(scaleXProto: Scale<Double>): jetbrains.datalore.plot.builder.PlotBuilder {
        myScaleXProto = scaleXProto
        return this
    }

    fun scaleYProto(scaleYProto: Scale<Double>): jetbrains.datalore.plot.builder.PlotBuilder {
        myScaleYProto = scaleYProto
        return this
    }

    fun axisEnabled(b: Boolean): jetbrains.datalore.plot.builder.PlotBuilder {
        myAxisEnabled = b
        return this
    }

    fun interactionsEnabled(b: Boolean): jetbrains.datalore.plot.builder.PlotBuilder {
        myInteractionsEnabled = b
        return this
    }

    fun canvasEnabled(b: Boolean): jetbrains.datalore.plot.builder.PlotBuilder {
        myCanvasEnabled = b
        return this
    }

    fun build(): jetbrains.datalore.plot.builder.Plot {
        return jetbrains.datalore.plot.builder.PlotBuilder.MyPlot(this)
    }


    private class MyPlot(b: jetbrains.datalore.plot.builder.PlotBuilder) : jetbrains.datalore.plot.builder.Plot(b.myTheme) {
        override val scaleXProto: Scale<Double> = b.myScaleXProto
        override val scaleYProto: Scale<Double> = b.myScaleYProto

        private val myTitle: String? = b.myTitle
        private val myAxisTitleLeft: String? = b.myAxisTitleLeft
        private val myAxisTitleBottom: String? = b.myAxisTitleBottom
        private val myAxisXTitleEnabled: Boolean = b.myTheme.axisX().showTitle()
        private val myAxisYTitleEnabled: Boolean = b.myTheme.axisY().showTitle()

        override val coordProvider: jetbrains.datalore.plot.builder.coord.CoordProvider = b.myCoordProvider

        private val myLayersByTile: List<List<jetbrains.datalore.plot.builder.GeomLayer>>
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
            myLayersByTile = ArrayList(b.myLayersByTile)
            myLayout = b.myLayout
            myLegendBoxInfos = ArrayList(b.myLegendBoxInfos)

            isAxisEnabled = b.myAxisEnabled
            isInteractionsEnabled = b.myInteractionsEnabled
            isCanvasEnabled = b.myCanvasEnabled
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

        override fun tileLayers(tileIndex: Int): List<jetbrains.datalore.plot.builder.GeomLayer> {
            return myLayersByTile[tileIndex]
        }

        override fun plotLayout(): PlotLayout {
            return myLayout!!
        }
    }
}
