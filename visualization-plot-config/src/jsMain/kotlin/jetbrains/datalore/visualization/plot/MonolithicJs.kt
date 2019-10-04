package jetbrains.datalore.visualization.plot

import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.dom.DomEventUtil
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.jsObject.dynamicObjectToMap
import jetbrains.datalore.base.observable.event.handler
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.visualization.base.canvas.dom.DomCanvasControl
import jetbrains.datalore.visualization.base.canvas.dom.DomEventPeer
import jetbrains.datalore.visualization.base.svg.SvgNodeContainer
import jetbrains.datalore.visualization.base.svgMapper.dom.SvgRootDocumentMapper
import jetbrains.datalore.visualization.plot.LiveMapUtil.containsLiveMap
import jetbrains.datalore.visualization.plot.builder.Plot
import jetbrains.datalore.visualization.plot.builder.PlotContainer
import jetbrains.datalore.visualization.plot.config.LiveMapConfig
import jetbrains.datalore.visualization.plot.config.PlotConfigClientSide
import jetbrains.datalore.visualization.plot.config.PlotConfigClientSideUtil
import jetbrains.datalore.visualization.plot.config.PlotConfigUtil
import org.w3c.dom.Node
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.svg.SVGSVGElement


object MonolithicJs {
    private var myIsLiveMap: Boolean = false

    @Suppress("unused")
    @JsName("buildPlotFromRawSpecs")
    fun buildPlotFromRawSpecs(plotSpecJs: dynamic, width: Double, height: Double, parentElement: Node) {
        // First apply 'server-side' transforms - stat, sampling etc.
        TODO("Not implemented: 'server-side' transforms")
    }

    /**
     * The entry point to use in JS (see demo like BarPlotBrowser.kt)
     */
    @Suppress("unused")
    @JsName("buildPlotFromProcessedSpecs")
    fun buildPlotFromProcessedSpecs(plotSpecJs: dynamic, width: Double, height: Double, parentElement: Node) {
        val plotSpec = dynamicObjectToMap(plotSpecJs)

        val plotSize = DoubleVector(width, height)
        val svg = buildPlotSvg(plotSpec, plotSize, parentElement)
        parentElement.appendChild(svg)
    }

    private fun buildPlotSvg(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector,
        eventTarget: Node
    ): SVGSVGElement {

        // ToDo: computationMessagesHandler
        val plot = createPlot(plotSpec, null)
        val plotContainer = PlotContainer(plot, ValueProperty(plotSize))

        val peer = DomEventPeer(eventTarget)

        peer.addEventHandler(DomEventPeer.DomEventSpec.MOUSE_MOVED, handler { e: Event ->
            plotContainer.mouseEventPeer.dispatch(
                MouseEventSpec.MOUSE_MOVED,
                DomEventUtil.translateInTargetCoord(e as MouseEvent)
            )
        })
        peer.addEventHandler(DomEventPeer.DomEventSpec.MOUSE_EXITED, handler { e: Event ->
            plotContainer.mouseEventPeer.dispatch(
                MouseEventSpec.MOUSE_LEFT,
                DomEventUtil.translateInTargetCoord(e as MouseEvent)
            )
        })

        peer.addEventHandler(DomEventPeer.DomEventSpec.MOUSE_DRAGGED, handler { e: Event ->
            plotContainer.mouseEventPeer.dispatch(
                MouseEventSpec.MOUSE_DRAGGED,
                DomEventUtil.translateInTargetCoord(e as MouseEvent)
            )
        })

        peer.addEventHandler(DomEventPeer.DomEventSpec.MOUSE_PRESSED, handler { e: Event ->
            plotContainer.mouseEventPeer.dispatch(
                MouseEventSpec.MOUSE_PRESSED,
                DomEventUtil.translateInTargetCoord(e as MouseEvent)
            )
        })

        peer.addEventHandler(DomEventPeer.DomEventSpec.MOUSE_RELEASED, handler { e: Event ->
            plotContainer.mouseEventPeer.dispatch(
                MouseEventSpec.MOUSE_RELEASED,
                DomEventUtil.translateInTargetCoord(e as MouseEvent)
            )
        })

        peer.addEventHandler(DomEventPeer.DomEventSpec.MOUSE_DOUBLE_CLICKED, handler { e: Event ->
            plotContainer.mouseEventPeer.dispatch(
                MouseEventSpec.MOUSE_DOUBLE_CLICKED,
                DomEventUtil.translateInTargetCoord(e as MouseEvent)
            )
        })

        plotContainer.ensureContentBuilt()

        if (myIsLiveMap) {
            val canvasFigures = plotContainer.tileCanvasFigures

            canvasFigures.forEach {
                val canvasControl = DomCanvasControl(it.bounds().get().dimension.toVector())
                it.mapToCanvas(canvasControl)
                eventTarget.appendChild(canvasControl.rootElement)
            }
        }

        val svgRoot = plotContainer.svg
        val mapper = SvgRootDocumentMapper(svgRoot)
        SvgNodeContainer(svgRoot)
        mapper.attachRoot()
        return mapper.target
    }

    private fun DoubleVector.toVector(): Vector {
        return Vector(x.toInt(), y.toInt())
    }


    private fun createPlot(plotSpec: MutableMap<String, Any>, computationMessagesHandler: ((List<String>) -> Unit)?): Plot {
        @Suppress("NAME_SHADOWING")
        var plotSpec = plotSpec
        plotSpec = PlotConfigClientSide.processTransform(plotSpec)
        if (computationMessagesHandler != null) {
            val computationMessages = PlotConfigUtil.findComputationMessages(plotSpec)
            if (computationMessages.isNotEmpty()) {
                computationMessagesHandler(computationMessages)
            }
        }

        val assembler = PlotConfigClientSideUtil.createPlotAssembler(plotSpec)

        val layersByTile = assembler.layersByTile
        val isLiveMap = containsLiveMap(layersByTile)

        if (isLiveMap) {
            myIsLiveMap = true
            val liveMapOpts = LiveMapConfig.getLiveMapOptions(plotSpec)
            val liveMapConfig = LiveMapConfig.create(liveMapOpts)
            LiveMapUtil.initLiveMapProvider(layersByTile, liveMapConfig.createLivemapOptions())
        }

        return assembler.createPlot()
    }
}
