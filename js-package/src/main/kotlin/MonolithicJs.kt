/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

/* root package */

import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.dom.DomEventUtil
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.js.css.*
import jetbrains.datalore.base.js.css.enumerables.CssCursor
import jetbrains.datalore.base.js.css.enumerables.CssPosition
import jetbrains.datalore.base.js.dom.DomEventListener
import jetbrains.datalore.base.js.dom.DomEventType
import jetbrains.datalore.base.jsObject.dynamicObjectToMap
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.plot.MonolithicCommon.PlotBuildInfo
import jetbrains.datalore.plot.MonolithicCommon.PlotsBuildResult.Error
import jetbrains.datalore.plot.MonolithicCommon.PlotsBuildResult.Success
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.event.MouseEventPeer
import jetbrains.datalore.plot.builder.presentation.Defaults
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plot.config.FailureHandler
import jetbrains.datalore.plot.config.LiveMapOptionsParser
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.config.PlotConfigClientSide
import jetbrains.datalore.plot.livemap.CursorServiceConfig
import jetbrains.datalore.plot.livemap.LiveMapUtil
import jetbrains.datalore.plot.server.config.PlotConfigServerSide
import jetbrains.datalore.vis.canvas.dom.DomCanvasControl
import jetbrains.datalore.vis.canvasFigure.CanvasFigure
import jetbrains.datalore.vis.svg.SvgNodeContainer
import jetbrains.datalore.vis.svgMapper.dom.SvgRootDocumentMapper
import kotlinx.browser.document
import kotlinx.dom.createElement
import mu.KotlinLogging
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLParagraphElement
import org.w3c.dom.Node
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.svg.SVGSVGElement

private val LOG = KotlinLogging.logger {}

/**
 * The entry point to call in JS
 * `raw specs` are plot specs not processed by datalore plot backend
 */
@Suppress("unused")
@JsName("buildPlotFromRawSpecs")
fun buildPlotFromRawSpecs(plotSpecJs: dynamic, width: Double, height: Double, parentElement: HTMLElement) {
    try {
        val plotSpec = dynamicObjectToMap(plotSpecJs)
        PlotConfig.assertPlotSpecOrErrorMessage(plotSpec)
        val processedSpec = processSpecs(plotSpec, frontendOnly = false)
        buildPlotFromProcessedSpecsIntern(processedSpec, width, height, parentElement)
    } catch (e: RuntimeException) {
        handleException(e, parentElement)
    }
}

/**
 * The entry point to call in JS
 * `processed specs` are plot specs processed by datalore plot backend
 */
@Suppress("unused")
@JsName("buildPlotFromProcessedSpecs")
fun buildPlotFromProcessedSpecs(plotSpecJs: dynamic, width: Double, height: Double, parentElement: HTMLElement) {
    try {
        val plotSpec = dynamicObjectToMap(plotSpecJs)
        // "processed" here means "processed on backend" -> apply "frontend" transforms to have truly processed specs.
        val processedSpec = processSpecs(plotSpec, frontendOnly = true)
        buildPlotFromProcessedSpecsIntern(processedSpec, width, height, parentElement)
    } catch (e: RuntimeException) {
        handleException(e, parentElement)
    }
}

private fun buildPlotFromProcessedSpecsIntern(
    plotSpec: MutableMap<String, Any>,
    width: Double,
    height: Double,
    parentElement: HTMLElement
) {
    val plotSize = if (width > 0 && height > 0) DoubleVector(width, height) else null
    val maxWidth = parentElement.clientWidth.toDouble()
    val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(plotSpec, plotSize, maxWidth)
    if (buildResult.isError) {
        val errorMessage = (buildResult as Error).error
        showError(errorMessage, parentElement)
        return
    }

    val success = buildResult as Success
    val computationMessages = success.buildInfos.flatMap { it.computationMessages }
    computationMessages.forEach {
        showInfo(it, parentElement)
    }

    if (success.buildInfos.size == 1) {
        // a single plot
        buildSinglePlotComponent(success.buildInfos[0], parentElement)
    } else {
        // a bunch
        buildGGBunchComponent(success.buildInfos, parentElement)
    }
}

fun buildGGBunchComponent(plotInfos: List<PlotBuildInfo>, parentElement: HTMLElement) {
    for (plotInfo in plotInfos) {
        val itemElement = parentElement.ownerDocument!!.createElement("div") {
            setAttribute(
                "style",
                "position: absolute; left: ${plotInfo.origin.x}px; top: ${plotInfo.origin.y}px;"
            )
        } as HTMLElement

        parentElement.appendChild(itemElement)
        buildSinglePlotComponent(plotInfo, itemElement)
    }

    val bunchBounds = plotInfos.map { it.bounds() }
        .fold(DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)) { acc, bounds ->
            acc.union(bounds)
        }

    var style = "position: relative; width: ${bunchBounds.width}px; height: ${bunchBounds.height}px;"

    // 'background-color' makes livemap disappear - set only if no livemaps in the bunch.
    if (!plotInfos.any { it.plotAssembler.containsLiveMap }) {
        style = "$style background-color: ${Defaults.BACKDROP_COLOR};"
    }
    parentElement.setAttribute("style", style)
}

private fun buildSinglePlotComponent(
    plotBuildInfo: PlotBuildInfo,
    parentElement: HTMLElement
) {

    val assembler = plotBuildInfo.plotAssembler
    val cursorServiceConfig = CursorServiceConfig()
    injectLivemapProvider(assembler, plotBuildInfo.processedPlotSpec, cursorServiceConfig)

    val plot = assembler.createPlot()
    val plotContainer = PlotContainer(plot, plotBuildInfo.size)
    val svg = buildPlotSvg(plotContainer, parentElement)

    cursorServiceConfig.defaultSetter { svg.style.setCursor(CssCursor.CROSSHAIR) }
    cursorServiceConfig.pointerSetter { svg.style.setCursor(CssCursor.POINTER) }

    parentElement.appendChild(svg)
}

private fun injectLivemapProvider(
    plotAssembler: PlotAssembler,
    processedPlotSpec: MutableMap<String, Any>,
    cursorServiceConfig: CursorServiceConfig
) {
    LiveMapOptionsParser.parseFromPlotSpec(processedPlotSpec)
        ?.let {
            LiveMapUtil.injectLiveMapProvider(
                plotAssembler.layersByTile,
                it,
                cursorServiceConfig
            )
        }
}

private fun buildPlotSvg(
    plotContainer: PlotContainer,
    eventTarget: Element
): SVGSVGElement {
    listOf(
        DomEventType.Companion.BLUR,
        DomEventType.Companion.CHANGE,
        DomEventType.Companion.INPUT,
        DomEventType.Companion.PASTE,
        DomEventType.Companion.RESIZE,
        DomEventType.Companion.CLICK,
        DomEventType.Companion.CONTEXT_MENU,
        DomEventType.Companion.DOUBLE_CLICK,
        DomEventType.Companion.DRAG,
        DomEventType.Companion.DRAG_END,
        DomEventType.Companion.DRAG_ENTER,
        DomEventType.Companion.DRAG_LEAVE,
        DomEventType.Companion.DRAG_OVER,
        DomEventType.Companion.DRAG_START,
        DomEventType.Companion.DROP,
        DomEventType.Companion.FOCUS,
        DomEventType.Companion.FOCUS_IN,
        DomEventType.Companion.FOCUS_OUT,
        DomEventType.Companion.KEY_DOWN,
        DomEventType.Companion.KEY_PRESS,
        DomEventType.Companion.KEY_UP,
        DomEventType.Companion.LOAD,
        DomEventType.Companion.MOUSE_ENTER,
        DomEventType.Companion.MOUSE_LEAVE,
        DomEventType.Companion.MOUSE_DOWN,
        DomEventType.Companion.MOUSE_MOVE,
        DomEventType.Companion.MOUSE_OUT,
        DomEventType.Companion.MOUSE_OVER,
        DomEventType.Companion.MOUSE_UP,
        DomEventType.Companion.MOUSE_WHEEL,
        DomEventType.Companion.SCROLL,
        DomEventType.Companion.TOUCH_CANCEL,
        DomEventType.Companion.TOUCH_END,
        DomEventType.Companion.TOUCH_MOVE,
        DomEventType.Companion.TOUCH_START,
        DomEventType.Companion.COMPOSITION_START,
        DomEventType.Companion.COMPOSITION_END,
        DomEventType.Companion.COMPOSITION_UPDATE,
        DomEventType.Companion.MESSAGE,

        DomEventType.Companion.XHR_PROGRESS,
        DomEventType.Companion.XHR_LOAD,
        DomEventType.Companion.XHR_LOAD_START,
        DomEventType.Companion.XHR_LOAD_END,
        DomEventType.Companion.XHR_ABORT,
        DomEventType.Companion.XHR_ERROR,
    ).forEach {
        eventTarget.addEventListener(it.name, { e: Event ->
            //println(it.name)
        })
    }


    plotContainer.ensureContentBuilt()

    val svg = plotContainer.svg

    val mapper = SvgRootDocumentMapper(svg)
    SvgNodeContainer(svg)
    mapper.attachRoot()

    if (plotContainer.isLiveMap) {
        // Plot - transparent for live-map base layer to be visible.
        svg.addClass(Style.PLOT_TRANSPARENT)

        mapper.target.style.run {
            setPosition(CssPosition.RELATIVE)
        }
    }

    DomEventMapper(plotContainer.mouseEventPeer, eventTarget)

    plotContainer.liveMapFigures.forEach { liveMapFigure ->
        val bounds = (liveMapFigure as CanvasFigure).bounds().get()
        val liveMapDiv = document.createElement("div") as HTMLElement

        liveMapDiv.style.run {
            setLeft(bounds.origin.x.toDouble())
            setTop(bounds.origin.y.toDouble())
            setWidth(bounds.dimension.x)
            setPosition(CssPosition.RELATIVE)
        }

        val canvasControl = DomCanvasControl(
            liveMapDiv,
            bounds.dimension,
            DomCanvasControl.DomEventPeer(mapper.target, bounds)
        )

        liveMapFigure.mapToCanvas(canvasControl)
        eventTarget.appendChild(liveMapDiv)
    }

    return mapper.target
}

private fun handleException(e: RuntimeException, parentElement: HTMLElement) {
    val failureInfo = FailureHandler.failureInfo(e)
    showError(failureInfo.message, parentElement)
    if (failureInfo.isInternalError) {
        LOG.error(e) {}
    }
}

private fun showError(message: String, parentElement: HTMLElement) {
    showText(message, "lets-plot-message-error", "color:darkred;", parentElement)
}

private fun showInfo(message: String, parentElement: HTMLElement) {
    showText(message, "lets-plot-message-info", "color:darkblue;", parentElement)
}

private fun showText(message: String, className: String, style: String, parentElement: HTMLElement) {
    val paragraphElement = parentElement.ownerDocument!!.createElement("p") as HTMLParagraphElement
    if (style.isNotBlank()) {
        paragraphElement.setAttribute("style", style)
    }
    paragraphElement.textContent = message
    paragraphElement.className = className
    parentElement.appendChild(paragraphElement)
}

@Suppress("DuplicatedCode")
private fun processSpecs(plotSpec: MutableMap<String, Any>, frontendOnly: Boolean): MutableMap<String, Any> {
    PlotConfig.assertPlotSpecOrErrorMessage(plotSpec)
    if (PlotConfig.isFailure(plotSpec)) {
        return plotSpec
    }

    // Backend transforms
    @Suppress("NAME_SHADOWING")
    val plotSpec =
        if (frontendOnly) {
            plotSpec
        } else {
            PlotConfigServerSide.processTransform(plotSpec)
        }

    if (PlotConfig.isFailure(plotSpec)) {
        return plotSpec
    }

    // Frontend transforms
    return PlotConfigClientSide.processTransform(plotSpec)
}

class DomEventMapper(
    private val destMouseEventPeer: MouseEventPeer,
    private val myEventTarget: Element
) {
    private var myButtonPressed = false
    private var myDragging = false
    private var myButtonPressCoord: Vector? = null
    private val myDragThreshold = 3.0

    init {
        handle(DomEventType.MOUSE_ENTER) {
            dispatch(MouseEventSpec.MOUSE_ENTERED, it)
        }

        handle(DomEventType.MOUSE_LEAVE) {
            dispatch(MouseEventSpec.MOUSE_LEFT, it)
        }

        handle(DomEventType.CLICK) {
            if (!myDragging) {
                dispatch(MouseEventSpec.MOUSE_CLICKED, it)
            }
            myDragging = false
        }

        handle(DomEventType.DOUBLE_CLICK) {
            dispatch(MouseEventSpec.MOUSE_DOUBLE_CLICKED, it)
        }

        handle(DomEventType.MOUSE_DOWN) {
            myButtonPressed = true
            myButtonPressCoord = Vector(it.x.toInt(), it.y.toInt())
            dispatch(MouseEventSpec.MOUSE_PRESSED, it)
        }

        handle(DomEventType.MOUSE_UP) {
            myButtonPressed = false
            myButtonPressCoord = null
            myDragging = false
            dispatch(MouseEventSpec.MOUSE_RELEASED, it)
        }

        handle(DomEventType.MOUSE_MOVE) {
            if (myDragging) {
                dispatch(MouseEventSpec.MOUSE_DRAGGED, it)
            }
            else if (myButtonPressed && !myDragging) {
                val distance = myButtonPressCoord?.sub(Vector(it.x.toInt(), it.y.toInt()))?.length() ?: 0.0
                if (distance > myDragThreshold) {
                    myDragging = true
                    dispatch(MouseEventSpec.MOUSE_DRAGGED, it)
                } else {
                    // Just in case do not generate move event. Can be changed if needed.
                }
            } else if (!myButtonPressed && !myDragging) {
                dispatch(MouseEventSpec.MOUSE_MOVED, it)
            }
        }
    }

    private fun dispatch(eventSpec: MouseEventSpec, mouseEvent: MouseEvent) {
        val translatedEvent = DomEventUtil.translateInTargetCoord(mouseEvent, myEventTarget)
        destMouseEventPeer.dispatch(eventSpec, translatedEvent)
    }

    private fun handle(eventSpec: DomEventType<MouseEvent>, handler: (MouseEvent) -> Unit) {
        targetNode(eventSpec).addEventListener(eventSpec.name, DomEventListener<MouseEvent> {
            handler(it)
            false
        })
    }

    private fun targetNode(eventSpec: DomEventType<MouseEvent>): Node = when (eventSpec) {
        DomEventType.MOUSE_MOVE, DomEventType.MOUSE_UP -> myEventTarget
        else -> myEventTarget
    }
}
