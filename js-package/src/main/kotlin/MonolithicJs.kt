/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

/* root package */

import jetbrains.datalore.base.event.dom.DomEventMapper
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.js.css.*
import jetbrains.datalore.base.js.css.enumerables.CssCursor
import jetbrains.datalore.base.js.css.enumerables.CssPosition
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
import org.w3c.dom.svg.SVGSVGElement

private val LOG = KotlinLogging.logger {}

/**
 * The entry point to call in JS
 * `raw specs` are plot specs not processed by datalore plot backend
 */
@OptIn(ExperimentalJsExport::class)
@Suppress("unused")
@JsName("buildPlotFromRawSpecs")
@JsExport
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
@OptIn(ExperimentalJsExport::class)
@Suppress("unused")
@JsName("buildPlotFromProcessedSpecs")
@JsExport
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
    parentElement: Element
): SVGSVGElement {
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

    DomEventMapper(mapper.target) { eventSpec, mouseEvent ->
        plotContainer.mouseEventPeer.dispatch(eventSpec, mouseEvent)
    }

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
        parentElement.appendChild(liveMapDiv)
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
