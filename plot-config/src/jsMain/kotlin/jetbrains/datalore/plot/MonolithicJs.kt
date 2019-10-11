package jetbrains.datalore.plot

import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.dom.DomEventUtil
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.js.dom.DomEventType
import jetbrains.datalore.base.jsObject.dynamicObjectToMap
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.plot.config.LiveMapOptionsParser.Companion.parseFromPlotOptions
import jetbrains.datalore.plot.config.OptionsAccessor
import jetbrains.datalore.plot.config.PlotConfigClientSide
import jetbrains.datalore.plot.config.PlotConfigClientSideUtil
import jetbrains.datalore.plot.config.PlotConfigUtil
import jetbrains.datalore.vis.canvas.dom.DomCanvasControl
import jetbrains.datalore.vis.svg.SvgNodeContainer
import jetbrains.datalore.vis.svgMapper.dom.SvgRootDocumentMapper
import org.w3c.dom.Node
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.svg.SVGSVGElement


object MonolithicJs {
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
        val plotContainer = jetbrains.datalore.plot.builder.PlotContainer(plot, ValueProperty(plotSize))

        eventTarget.addEventListener(DomEventType.MOUSE_MOVE.name, { e: Event ->
            plotContainer.mouseEventPeer.dispatch(
                MouseEventSpec.MOUSE_MOVED,
                DomEventUtil.translateInTargetCoord(e as MouseEvent)
            )
        })

        eventTarget.addEventListener(DomEventType.MOUSE_LEAVE.name, { e: Event ->
            plotContainer.mouseEventPeer.dispatch(
                MouseEventSpec.MOUSE_LEFT,
                DomEventUtil.translateInTargetCoord(e as MouseEvent)
            )
        })

        plotContainer.ensureContentBuilt()

        plotContainer.liveMapFigures.forEach { liveMapFigure ->
            val canvasControl =
                DomCanvasControl(liveMapFigure.dimension().get().toVector())
            liveMapFigure.mapToCanvas(canvasControl)
            eventTarget.appendChild(canvasControl.rootElement)
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


    private fun createPlot(
        intPlotSpec: MutableMap<String, Any>,
        computationMessagesHandler: ((List<String>) -> Unit)?
    ): jetbrains.datalore.plot.builder.Plot {

        var plotSpec = intPlotSpec
        plotSpec = PlotConfigClientSide.processTransform(plotSpec)
        if (computationMessagesHandler != null) {
            val computationMessages = PlotConfigUtil.findComputationMessages(plotSpec)
            if (computationMessages.isNotEmpty()) {
                computationMessagesHandler(computationMessages)
            }
        }

        val assembler = PlotConfigClientSideUtil.createPlotAssembler(plotSpec)

        parseFromPlotOptions(OptionsAccessor(plotSpec))
            ?.let { jetbrains.livemap.geom.LiveMapUtil.injectLiveMapProvider(assembler.layersByTile, it) }

        return assembler.createPlot()
    }
}
