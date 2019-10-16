package jetbrains.datalore.plot

import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.dom.DomEventUtil
import jetbrains.datalore.base.gcommon.base.Throwables
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.js.dom.DomEventType
import jetbrains.datalore.base.jsObject.dynamicObjectToMap
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.plot.builder.Plot
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.config.LiveMapOptionsParser.Companion.parseFromPlotOptions
import jetbrains.datalore.plot.config.OptionsAccessor
import jetbrains.datalore.plot.config.PlotConfigClientSide
import jetbrains.datalore.plot.config.PlotConfigClientSideUtil
import jetbrains.datalore.plot.config.PlotConfigUtil
import jetbrains.datalore.vis.canvas.dom.DomCanvasControl
import jetbrains.datalore.vis.svg.SvgNodeContainer
import jetbrains.datalore.vis.svgMapper.dom.SvgRootDocumentMapper
import mu.KotlinLogging
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLParagraphElement
import org.w3c.dom.Node
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.svg.SVGSVGElement


@Suppress("unused")
object MonolithicJs {
    private val LOG = KotlinLogging.logger {}

    private val DEF_PLOT_SIZE = DoubleVector(500.0, 400.0)
    private val DEF_LIVE_MAP_SIZE = DoubleVector(800.0, 600.0)

    /**
     * The entry point to call in JS
     */
    @Suppress("unused")
    @JsName("buildPlotFromRawSpecs")
    fun buildPlotFromRawSpecs(plotSpecJs: dynamic, width: Double, height: Double, parentElement: Node) {
        // First apply 'server-side' transforms - stat, sampling etc.
        TODO("Not implemented: 'server-side' transforms")
    }

    /**
     * The entry point to call in JS
     */
    @Suppress("unused")
    @JsName("buildPlotFromProcessedSpecs")
    fun buildPlotFromProcessedSpecs(plotSpecJs: dynamic, width: Double, height: Double, parentElement: HTMLElement) {
        try {
            buildPlotFromProcessedSpecsIntern(plotSpecJs, width, height, parentElement)
        } catch (e: RuntimeException) {
            handleException(e, parentElement)
        }
    }

    private fun buildPlotFromProcessedSpecsIntern(
        plotSpecJs: dynamic,
        width: Double,
        height: Double,
        parentElement: HTMLElement
    ) {
        // test errors
//        throw RuntimeException()
//        throw RuntimeException("My sudden crush")
//        throw IllegalArgumentException("User configuration error")
//        throw IllegalStateException("User configuration error")
//        throw IllegalStateException()   // Huh?

        val plotSpec = dynamicObjectToMap(plotSpecJs)
        // ToDo: computationMessagesHandler
        val assembler = createPlotAssembler(plotSpec, null)

        // Figure out plot size
        val plotSize = if (width > 0 && height > 0) {
            DoubleVector(width, height)
        } else {
            val maxWidth = if (width > 0) width else parentElement.offsetWidth.toDouble()
            val defaultSize = defaultPlotSize(plotSpec, assembler)
            if (defaultSize.x > maxWidth) {
                val scaler = maxWidth / defaultSize.x
                DoubleVector(maxWidth, defaultSize.y * scaler)
            } else {
                defaultSize
            }
        }

        // Inject LiveMap
        parseFromPlotOptions(OptionsAccessor(plotSpec))
            ?.let { jetbrains.livemap.geom.LiveMapUtil.injectLiveMapProvider(assembler.layersByTile, it) } // LIVEMAP_SWITCH

        val plot = assembler.createPlot()
        val svg = buildPlotSvg(plot, plotSize, parentElement)
        parentElement.appendChild(svg)
    }

    private fun createPlotAssembler(
        plotSpec: MutableMap<String, Any>,
        computationMessagesHandler: ((List<String>) -> Unit)?
    ): PlotAssembler {
        @Suppress("NAME_SHADOWING")
        var plotSpec = plotSpec
        plotSpec = PlotConfigClientSide.processTransform(plotSpec)
        if (computationMessagesHandler != null) {
            val computationMessages = PlotConfigUtil.findComputationMessages(plotSpec)
            if (computationMessages.isNotEmpty()) {
                computationMessagesHandler(computationMessages)
            }
        }

        return PlotConfigClientSideUtil.createPlotAssembler(plotSpec)
    }

    private fun defaultPlotSize(plotSpec: Map<String, Any>, assembler: PlotAssembler): DoubleVector {
        var plotSize = PlotConfigClientSideUtil.getPlotSizeOrNull(plotSpec)
        if (plotSize == null) {
            plotSize = DEF_PLOT_SIZE
            val facets = assembler.facets
            if (facets.isDefined) {
                val xLevels = facets.xLevels!!
                val yLevels = facets.yLevels!!
                val columns = if (xLevels.isEmpty()) 1 else xLevels.size
                val rows = if (yLevels.isEmpty()) 1 else yLevels.size
                val panelWidth = DEF_PLOT_SIZE.x * (0.5 + 0.5 / columns)
                val panelHeight = DEF_PLOT_SIZE.y * (0.5 + 0.5 / rows)
                plotSize = DoubleVector(panelWidth * columns, panelHeight * rows)
            } else if (assembler.containsLiveMap) {
                plotSize = DEF_LIVE_MAP_SIZE
            }
        }

        return plotSize
    }

    private fun buildPlotSvg(
        plot: Plot,
        plotSize: DoubleVector,
        eventTarget: Node
    ): SVGSVGElement {

        // ToDo: computationMessagesHandler
        val plotContainer = PlotContainer(plot, ValueProperty(plotSize))

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

    private fun handleException(e: RuntimeException, parentElement: HTMLElement) {
        @Suppress("NAME_SHADOWING")
        val e = Throwables.getRootCause(e)
        val errorMessage: String
        if (!e.message.isNullOrBlank() && (
                    e is IllegalStateException ||
                            e is IllegalArgumentException)
        ) {
            // Not a bug - likely user configuration error like `No layers in plot`
            errorMessage = e.message!!
        } else {
            errorMessage = "Internal error occurred in datalore plot: ${e::class.js} : ${e.message}"
            LOG.error(e) {}
        }

        val paragraphElement = parentElement.ownerDocument!!.createElement("p") as HTMLParagraphElement
        paragraphElement.setAttribute("style", "color:darkred;")
        paragraphElement.textContent = errorMessage
        parentElement.appendChild(paragraphElement)
    }
}
