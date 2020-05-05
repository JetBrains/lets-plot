/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

/* root package */

import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.dom.DomEventUtil
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.js.css.*
import jetbrains.datalore.base.js.css.enumerables.CssPosition
import jetbrains.datalore.base.js.dom.DomEventType
import jetbrains.datalore.base.jsObject.dynamicObjectToMap
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.plot.MonolithicCommon.PlotBuildInfo
import jetbrains.datalore.plot.MonolithicCommon.PlotsBuildResult.Error
import jetbrains.datalore.plot.MonolithicCommon.PlotsBuildResult.Success
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.presentation.Defaults
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plot.config.FailureHandler
import jetbrains.datalore.plot.config.LiveMapOptionsParser
import jetbrains.datalore.plot.config.OptionsAccessor
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.livemap.LiveMapUtil
import jetbrains.datalore.plot.server.config.PlotConfigClientSideJvmJs
import jetbrains.datalore.plot.server.config.PlotConfigServerSide
import jetbrains.datalore.vis.canvas.dom.DomCanvasControl
import jetbrains.datalore.vis.canvasFigure.CanvasFigure
import jetbrains.datalore.vis.svg.SvgNodeContainer
import jetbrains.datalore.vis.svgMapper.dom.SvgRootDocumentMapper
import mu.KotlinLogging
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLParagraphElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.svg.SVGSVGElement
import kotlin.browser.document
import kotlin.dom.createElement

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
    val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(plotSpec, plotSize)
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

    parentElement.setAttribute(
        "style",
        "position: relative; width: ${bunchBounds.width}px; height: ${bunchBounds.height}px; background-color: ${Defaults.BACKDROP_COLOR};"
    )
}

private fun buildSinglePlotComponent(
    plotBuildInfo: PlotBuildInfo,
    parentElement: HTMLElement
) {

    val assembler = plotBuildInfo.plotAssembler
    injectLivemapProvider(assembler, plotBuildInfo.processedPlotSpec)

    val plot = assembler.createPlot()
    val plotContainer = PlotContainer(plot, plotBuildInfo.size)
    val svg = buildPlotSvg(plotContainer, parentElement)
    parentElement.appendChild(svg)
}

private fun injectLivemapProvider(
    plotAssembler: PlotAssembler,
    processedPlotSpec: MutableMap<String, Any>
) {
    LiveMapOptionsParser.parseFromPlotSpec(processedPlotSpec)
        ?.let {
            LiveMapUtil.injectLiveMapProvider(
                plotAssembler.layersByTile,
                it
            )
        }
}

private fun buildPlotSvg(
    plotContainer: PlotContainer,
    eventTarget: Element
): SVGSVGElement {

    eventTarget.addEventListener(DomEventType.MOUSE_DOWN.name, { e: Event ->
        e.preventDefault()
    })

    eventTarget.addEventListener(DomEventType.MOUSE_MOVE.name, { e: Event ->
        plotContainer.mouseEventPeer.dispatch(
            MouseEventSpec.MOUSE_MOVED,
            DomEventUtil.translateInTargetCoord(e as MouseEvent, eventTarget)
        )
    })

    eventTarget.addEventListener(DomEventType.MOUSE_LEAVE.name, { e: Event ->
        plotContainer.mouseEventPeer.dispatch(
            MouseEventSpec.MOUSE_LEFT,
            DomEventUtil.translateInTargetCoord(e as MouseEvent, eventTarget)
        )
    })

    plotContainer.ensureContentBuilt()

    plotContainer.liveMapFigures.forEach { liveMapFigure ->
        val bounds = (liveMapFigure as CanvasFigure).bounds().get()
        val liveMapDiv = document.createElement("div") as HTMLElement

        liveMapDiv.style.run {
            setLeft(bounds.origin.x.toDouble())
            setTop(bounds.origin.y.toDouble())
            setWidth(bounds.dimension.x)
            setPosition(CssPosition.RELATIVE)
            setZIndex(-1)
        }

        val canvasControl = DomCanvasControl(
            liveMapDiv,
            bounds.dimension,
            DomCanvasControl.DomEventPeer(eventTarget, bounds)
        )

        liveMapFigure.mapToCanvas(canvasControl)
        eventTarget.appendChild(liveMapDiv)
    }

    val svg = plotContainer.svg
    if (plotContainer.isLiveMap) {
        // Plot - transparent for live-map base layer to be visible.
        svg.addClass(Style.PLOT_TRANSPARENT)
    }

    val mapper = SvgRootDocumentMapper(svg)
    SvgNodeContainer(svg)
    mapper.attachRoot()
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
    showText(message, "color:darkred;", parentElement)
}

private fun showInfo(message: String, parentElement: HTMLElement) {
    showText(message, "color:darkblue;", parentElement)
}

private fun showText(message: String, style: String, parentElement: HTMLElement) {
    val paragraphElement = parentElement.ownerDocument!!.createElement("p") as HTMLParagraphElement
    if (style.isNotBlank()) {
        paragraphElement.setAttribute("style", style)
    }
    paragraphElement.textContent = message
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
    return PlotConfigClientSideJvmJs.processTransform(plotSpec)
}

