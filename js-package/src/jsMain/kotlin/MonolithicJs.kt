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
import jetbrains.datalore.base.js.dom.DomEventType
import jetbrains.datalore.base.jsObject.dynamicObjectToMap
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.plot.MonolithicCommon.PlotBuildInfo
import jetbrains.datalore.plot.MonolithicCommon.PlotsBuildResult.Error
import jetbrains.datalore.plot.MonolithicCommon.PlotsBuildResult.Success
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.config.FailureHandler
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.server.config.PlotConfigServerSide
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
        val processedSpec = if (PlotConfig.isFailure(plotSpec)) {
            plotSpec
        } else {
            PlotConfigServerSide.processTransform(plotSpec)
        }
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
        buildPlotFromProcessedSpecsIntern(plotSpec, width, height, parentElement)
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
        buildSinglePlotComponent(success.buildInfos[0].plotContainer, parentElement)
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
        buildSinglePlotComponent(plotInfo.plotContainer, itemElement)
    }

    val bunchBounds = plotInfos.map { it.bounds() }
        .fold(DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)) { acc, bounds ->
            acc.union(bounds)
        }

    parentElement.setAttribute(
        "style",
        "position: relative; width: ${bunchBounds.width}px; height: ${bunchBounds.height}px;"
    )
}

private fun buildSinglePlotComponent(
    plotContainer: PlotContainer,
    parentElement: HTMLElement
) {
    val svg = buildPlotSvg(plotContainer, parentElement)
    parentElement.appendChild(svg)
}

private fun buildPlotSvg(
    plotContainer: PlotContainer,
    eventTarget: Node
): SVGSVGElement {

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
        val canvasControl = DomCanvasControl(liveMapFigure.bounds().get().dimension.toVector())
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

