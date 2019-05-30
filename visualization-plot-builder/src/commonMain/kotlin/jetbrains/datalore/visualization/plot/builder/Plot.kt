package jetbrains.datalore.visualization.plot.builder

import jetbrains.datalore.base.event.Event
import jetbrains.datalore.base.gcommon.base.Throwables
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.property.PropertyChangeEvent
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.base.registration.throwableHandlers.ThrowableHandlers
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.canvasFigure.CanvasFigure
import jetbrains.datalore.visualization.base.svg.SvgElement
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgNode
import jetbrains.datalore.visualization.base.svg.SvgRectElement
import jetbrains.datalore.visualization.base.svg.event.SvgEventHandler
import jetbrains.datalore.visualization.base.svg.event.SvgEventSpec
import jetbrains.datalore.visualization.plot.FeatureSwitch
import jetbrains.datalore.visualization.plot.base.CoordinateSystem
import jetbrains.datalore.visualization.plot.base.Scale
import jetbrains.datalore.visualization.plot.base.render.svg.SvgComponent
import jetbrains.datalore.visualization.plot.base.render.svg.TextLabel
import jetbrains.datalore.visualization.plot.base.render.svg.TextLabel.HorizontalAnchor
import jetbrains.datalore.visualization.plot.base.render.svg.TextLabel.VerticalAnchor
import jetbrains.datalore.visualization.plot.builder.coord.CoordProvider
import jetbrains.datalore.visualization.plot.builder.event.MouseEventPeer
import jetbrains.datalore.visualization.plot.builder.guide.Orientation
import jetbrains.datalore.visualization.plot.builder.interact.TooltipSpec
import jetbrains.datalore.visualization.plot.builder.layout.*
import jetbrains.datalore.visualization.plot.builder.presentation.Style
import jetbrains.datalore.visualization.plot.builder.theme.Theme
import mu.KotlinLogging

abstract class Plot(private val theme: Theme) : SvgComponent() {

    private val myPreferredSize = ValueProperty(DEF_PLOT_SIZE)
    private val myLaidOutSize = ValueProperty(DoubleVector.ZERO)
    private val myTooltipHelper = PlotTooltipHelper()
    private val myCanvasFigures = ArrayList<CanvasFigure>()

    internal val mouseEventPeer = MouseEventPeer()

    protected abstract val scaleXProto: Scale<Double>

    protected abstract val scaleYProto: Scale<Double>

    protected abstract val title: String

    protected abstract val axisTitleLeft: String

    protected abstract val axisTitleBottom: String

    protected abstract val coordProvider: CoordProvider

    protected abstract val legendBoxInfos: List<LegendBoxInfo>

    protected abstract val isAxisEnabled: Boolean

    abstract val isInteractionsEnabled: Boolean

    protected abstract val isCanvasEnabled: Boolean

    internal val tileCanvasFigures: List<CanvasFigure>
        get() = myCanvasFigures

    fun preferredSize(): WritableProperty<DoubleVector> {
        return myPreferredSize
    }

    fun laidOutSize(): ReadableProperty<DoubleVector> {
        return myLaidOutSize
    }

    protected abstract fun hasTitle(): Boolean

    protected abstract fun hasAxisTitleLeft(): Boolean

    protected abstract fun hasAxisTitleBottom(): Boolean

    protected abstract fun tileLayers(tileIndex: Int): List<GeomLayer>

    protected abstract fun plotLayout(): PlotLayout

    override fun buildComponent() {
        try {
            buildPlot()
        } catch (e: RuntimeException) {
            LOG.error(e) { "buildPlot" }
            ThrowableHandlers.instance.handle(e)

            val rootCause = Throwables.getRootCause(e)
            val messages = arrayOf("Error building plot: " + rootCause::class.simpleName, if (rootCause.message != null)
                "'" + rootCause.message + "'"
            else
                "<no message>")
            var y = myPreferredSize.get().y / 2 - 8
            for (s in messages) {
                val errorLabel = TextLabel(s)
                errorLabel.setHorizontalAnchor(HorizontalAnchor.MIDDLE)
                errorLabel.setVerticalAnchor(VerticalAnchor.CENTER)
                errorLabel.moveTo(myPreferredSize.get().x / 2, y)
                rootGroup.children().add(errorLabel.rootGroup)
                y += 16.0
            }
        }

    }

    private fun buildPlot() {
        rootGroup.addClass(Style.PLOT)
        buildPlotComponents()
        reg(myPreferredSize.addHandler(object : EventHandler<PropertyChangeEvent<out DoubleVector>> {
            override fun onEvent(event: PropertyChangeEvent<out DoubleVector>) {
                val newValue = event.newValue
                if (newValue!!.x > 0 && newValue.y > 0) {
                    rebuildPlot()
                }
            }
        }))

        reg(object : Registration() {
            override fun doRemove() {
                myTooltipHelper.removeAllTileInfos()
                myCanvasFigures.clear()
            }
        })
    }

    private fun rebuildPlot() {
        clear()
        buildPlot()
    }


    private fun createTile(
            tilesOrigin: DoubleVector,
            tileInfo: TileLayoutInfo,
            tileLayers: List<GeomLayer>): PlotTile {

        val xScale: Scale<Double>
        val yScale: Scale<Double>
        val coord: CoordinateSystem
        if (tileInfo.xAxisInfo != null && tileInfo.yAxisInfo != null) {
            val xDomain = tileInfo.xAxisInfo.axisDomain!!
            val xAxisLength = tileInfo.xAxisInfo.axisLength

            val yDomain = tileInfo.yAxisInfo.axisDomain!!
            val yAxisLength = tileInfo.yAxisInfo.axisLength

            // set-up scales and coordinate system
            xScale = coordProvider.buildAxisScaleX(scaleXProto, xDomain, xAxisLength, tileInfo.xAxisInfo.axisBreaks!!)
            yScale = coordProvider.buildAxisScaleY(scaleYProto, yDomain, yAxisLength, tileInfo.yAxisInfo.axisBreaks!!)
            coord = coordProvider.createCoordinateSystem(xDomain, xAxisLength, yDomain, yAxisLength)
        } else {
            // bogus scales and coordinate system (live map doesn't need them)
            xScale = BogusScale()
            yScale = BogusScale()
            coord = BogusCoordinateSystem()
        }

        val tile = PlotTile(tileLayers, xScale, yScale, tilesOrigin, tileInfo, coord, theme, mouseEventPeer)
        tile.setShowAxis(isAxisEnabled)
        tile.debugDrawing().set(DEBUG_DRAWING)
        tile.setUseCanvas(isCanvasEnabled)

        return tile
    }

    private fun createAxisTitle(text: String, orientation: Orientation, plotBounds: DoubleRectangle, geomBounds: DoubleRectangle) {
        val horizontalAnchor = HorizontalAnchor.MIDDLE
        val verticalAnchor: VerticalAnchor = when (orientation) {
            Orientation.LEFT, Orientation.RIGHT, Orientation.TOP -> VerticalAnchor.TOP
            Orientation.BOTTOM -> VerticalAnchor.BOTTOM
        }

        val titleLocation: DoubleVector
        var rotation = 0.0
        when (orientation) {
            Orientation.LEFT -> {
                titleLocation = DoubleVector(plotBounds.left + PlotLayoutUtil.AXIS_TITLE_OUTER_MARGIN, geomBounds.center.y)
                rotation = -90.0
            }
            Orientation.RIGHT -> {
                titleLocation = DoubleVector(plotBounds.right - PlotLayoutUtil.AXIS_TITLE_OUTER_MARGIN, geomBounds.center.y)
                rotation = 90.0
            }
            Orientation.TOP -> titleLocation = DoubleVector(geomBounds.center.x, plotBounds.top + PlotLayoutUtil.AXIS_TITLE_OUTER_MARGIN)
            Orientation.BOTTOM -> titleLocation = DoubleVector(geomBounds.center.x, plotBounds.bottom - PlotLayoutUtil.AXIS_TITLE_OUTER_MARGIN)
        }

        val titleLabel = TextLabel(text)
        titleLabel.setHorizontalAnchor(horizontalAnchor)
        titleLabel.setVerticalAnchor(verticalAnchor)
        titleLabel.moveTo(titleLocation)
        titleLabel.rotate(rotation)

        val titleElement = titleLabel.rootGroup
        titleElement.addClass(Style.AXIS_TITLE)

        // hack: we have style: ".axis .title text" and we don't want to break backward-compatibility with 'census' charts
        val parent = SvgGElement()
        parent.addClass(Style.AXIS)

        parent.children().add(titleElement)

        add(parent)
    }

    private fun onMouseMove(e: SvgElement, message: String) {
        e.addEventHandler(SvgEventSpec.MOUSE_MOVE, object : SvgEventHandler<Event> {
            override fun handle(node: SvgNode, e: Event) {
                println(message)
            }
        })
    }

    private fun buildPlotComponents() {
        val preferredSize = myPreferredSize.get()

        // compute geom bounds
        val entirePlot = DoubleRectangle(DoubleVector.ZERO, preferredSize)

        @Suppress("ConstantConditionIf")
        if (DEBUG_DRAWING) {
            val rect = SvgRectElement(entirePlot)
            rect.strokeColor().set(Color.MAGENTA)
            rect.strokeWidth().set(1.0)
            rect.fillOpacity().set(0.0)
            onMouseMove(rect, "MAGENTA: preferred size: $entirePlot")
            add(rect)
        }

        // subtract title size
        var withoutTitle = entirePlot
        if (hasTitle()) {
            val titleSize = PlotLayoutUtil.titleDimensions(title)
            val origin = DoubleVector(0.0, titleSize.y)
            withoutTitle = DoubleRectangle(origin, preferredSize.subtract(origin))

            val titleLabel = TextLabel(title)
            titleLabel.addClassName(Style.PLOT_TITLE)
            titleLabel.setHorizontalAnchor(HorizontalAnchor.MIDDLE)
            titleLabel.setVerticalAnchor(VerticalAnchor.CENTER)

            val titleBounds = PlotLayoutUtil.titleBounds(titleSize, preferredSize)
            titleLabel.moveTo(titleBounds.center)
            add(titleLabel)
        }

        // adjust for legend boxes
        var boxesLayoutResult: LegendBoxesLayout.Result? = null
        val legendTheme = theme.legend()
        var withoutTitleAndLegends = withoutTitle
        if (legendTheme.position().isFixed) {
            val legendBoxesLayout = LegendBoxesLayout(withoutTitle, legendTheme)
            boxesLayoutResult = legendBoxesLayout.doLayout(legendBoxInfos)
            withoutTitleAndLegends = boxesLayoutResult.plotInnerBoundsWithoutLegendBoxes
        }

        @Suppress("ConstantConditionIf")
        if (DEBUG_DRAWING) {
            val rect = SvgRectElement(withoutTitleAndLegends)
            rect.strokeColor().set(Color.BLUE)
            rect.strokeWidth().set(1.0)
            rect.fillOpacity().set(0.0)
            onMouseMove(rect, "BLUE: plot without title and legends: $withoutTitleAndLegends")
            add(rect)
        }

        // subtract left axis title width
        var geomAndAxis = withoutTitleAndLegends
        if (isAxisEnabled) {
            if (hasAxisTitleLeft()) {
                val titleSize = PlotLayoutUtil.axisTitleDimensions(axisTitleLeft)
                val thickness = titleSize.y + PlotLayoutUtil.AXIS_TITLE_OUTER_MARGIN + PlotLayoutUtil.AXIS_TITLE_INNER_MARGIN
                geomAndAxis = DoubleRectangle(
                        geomAndAxis.left + thickness, geomAndAxis.top,
                        geomAndAxis.width - thickness, geomAndAxis.height)
            }

            // subtract bottom axis title height
            if (hasAxisTitleBottom()) {
                val titleSize = PlotLayoutUtil.axisTitleDimensions(axisTitleBottom)
                val thickness = titleSize.y + PlotLayoutUtil.AXIS_TITLE_OUTER_MARGIN + PlotLayoutUtil.AXIS_TITLE_INNER_MARGIN
                geomAndAxis = DoubleRectangle(
                        geomAndAxis.left, geomAndAxis.top,
                        geomAndAxis.width, geomAndAxis.height - thickness)
            }
        }

        // Layout plot inners
        val plotLayout = plotLayout()
        val plotInfo = plotLayout.doLayout(geomAndAxis.dimension)
        this.myLaidOutSize.set(preferredSize)

        if (plotInfo.tiles.isEmpty()) {
            return
        }

        val geomAreaBounds = PlotLayoutUtil.absoluteGeomBounds(geomAndAxis.origin, plotInfo)
        if (legendTheme.position().isOverlay) {
            // put 'overlay' in 'geom' bounds
            val legendBoxesLayout = LegendBoxesLayout(geomAreaBounds, legendTheme)
            boxesLayoutResult = legendBoxesLayout.doLayout(legendBoxInfos)
        }

        // build tiles

        val tilesOrigin = geomAndAxis.origin
        for (i in 0 until plotInfo.tiles.size) {
            val tileInfo = plotInfo.tiles[i]

            //GWT.log("plot offset: " + tileInfo.plotOffset);
            //GWT.log("     bounds: " + tileInfo.bounds);
            //GWT.log("geom bounds: " + tileInfo.geomBounds);
            //GWT.log("clip bounds: " + tileInfo.clipBounds);
            val tile = createTile(tilesOrigin, tileInfo, tileLayers(i))

            tile.moveTo(tilesOrigin.add(tileInfo.plotOffset))

            add(tile)

            myCanvasFigures.addAll(tile.canvasFigures)

            val realGeomBounds = tileInfo.geomBounds.add(tilesOrigin.add(tileInfo.plotOffset))
            myTooltipHelper.addTileInfo(realGeomBounds, tile.targetLocators)
        }

        /*
    DoubleRectangle plotBounds = new DoubleRectangle(DoubleVector.ZERO, plotActualSize);
    if (DEBUG_DRAWING) {
      SvgRectElement rect = new SvgRectElement(plotBounds);
      rect.strokeColor().set(Color.BLUE);
      rect.strokeWidth().set(2.);
      rect.fillOpacity().set(0.);
      onMouseMove(rect, "BLUE: Plot Bounds: " + plotBounds);
      add(rect);
    }
    */

        @Suppress("ConstantConditionIf")
        if (DEBUG_DRAWING) {
            val rect = SvgRectElement(geomAreaBounds)
            rect.strokeColor().set(Color.RED)
            rect.strokeWidth().set(1.0)
            rect.fillOpacity().set(0.0)
            add(rect)
        }

        // add axis titles

        if (isAxisEnabled) {
            if (hasAxisTitleLeft()) {
                createAxisTitle(axisTitleLeft, Orientation.LEFT, withoutTitleAndLegends, geomAreaBounds)
            }
            if (hasAxisTitleBottom()) {
                createAxisTitle(axisTitleBottom, Orientation.BOTTOM, withoutTitleAndLegends, geomAreaBounds)
            }
        }

        // add legends
        if (boxesLayoutResult != null) {
            for (boxWithLocation in boxesLayoutResult.boxWithLocationList) {
                val legendBox = boxWithLocation.legendBox.createLegendBox()
                legendBox.moveTo(boxWithLocation.location)
                add(legendBox)
            }
        }
    }

    internal fun createTooltipSpecs(plotCoord: DoubleVector): List<TooltipSpec> {
        return myTooltipHelper.createTooltipSpecs(plotCoord)
    }

    companion object {
        private val LOG = KotlinLogging.logger {}

        private val DEF_PLOT_SIZE = DoubleVector(600.0, 400.0)
        private const val DEBUG_DRAWING = FeatureSwitch.PLOT_DEBUG_DRAWING
    }
}
