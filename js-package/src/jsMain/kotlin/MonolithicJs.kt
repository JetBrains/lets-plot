/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

/* root package */

import kotlinx.browser.document
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.platf.dom.DomMouseEventMapper
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.spec.FailureHandler
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.MonolithicCommon.PlotsBuildResult.Error
import org.jetbrains.letsPlot.core.util.MonolithicCommon.PlotsBuildResult.Success
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicObjectToMap
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLParagraphElement
import org.w3c.dom.get
import sizing.SizingOption
import sizing.SizingPolicy

private val LOG = PortableLogging.logger("MonolithicJs")

// key for data attibute <body data-lets-plot-preferred-width='700'>
private const val DATALORE_PREFERRED_WIDTH = "letsPlotPreferredWidth"

/**
 * The entry point to call in JS
 * `raw specs` are plot specs not processed by datalore plot backend
 */
@OptIn(ExperimentalJsExport::class)
@Suppress("unused")
@JsName("buildPlotFromRawSpecs")
@JsExport
fun buildPlotFromRawSpecs(
    plotSpecJs: dynamic,
    width: Double,
    height: Double,
    parentElement: HTMLElement,
    optionsJs: dynamic = null
): FigureModelJs? {
    return try {
        val plotSpec = dynamicObjectToMap(plotSpecJs)
        PlotConfig.assertFigSpecOrErrorMessage(plotSpec)
        val processedSpec = MonolithicCommon.processRawSpecs(plotSpec, frontendOnly = false)
        val options: Map<String, Any> = if (optionsJs != null) {
            dynamicObjectToMap(optionsJs)
        } else {
            emptyMap()
        }

        val figureContainer = document.createElement("div") as HTMLDivElement
        val mouseEventSource = DomMouseEventMapper(figureContainer)

        parentElement.appendChild(figureContainer)
        buildPlotFromProcessedSpecsIntern(processedSpec, width, height, figureContainer, mouseEventSource, options)
    } catch (e: RuntimeException) {
        handleException(e, parentElement)
        null
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
fun buildPlotFromProcessedSpecs(
    plotSpecJs: dynamic,
    width: Double,
    height: Double,
    parentElement: HTMLElement,
    optionsJs: dynamic = null
): FigureModelJs? {
    return try {
        val plotSpec = dynamicObjectToMap(plotSpecJs)
        // Though the "plotSpec" might contain already "processed" specs,
        // we apply "frontend" transforms anyway, just to be sure that
        // we are going to use a truly processed specs.
        val processedSpec = MonolithicCommon.processRawSpecs(plotSpec, frontendOnly = true)
        val options: Map<String, Any> = if (optionsJs != null) {
            dynamicObjectToMap(optionsJs)
        } else {
            emptyMap()
        }

        val figureContainer = document.createElement("div") as HTMLDivElement
        parentElement.appendChild(figureContainer)
        val mouseEventSource = DomMouseEventMapper(figureContainer)

        buildPlotFromProcessedSpecsIntern(processedSpec, width, height, figureContainer, mouseEventSource, options)
    } catch (e: RuntimeException) {
        handleException(e, parentElement)
        null
    }
}

internal fun buildPlotFromProcessedSpecsIntern(
    plotSpec: Map<String, Any>,
    width: Double,
    height: Double,
    parentElement: HTMLElement,
    mouseEventSource: MouseEventSource,
    options: Map<String, Any>
): FigureModelJs? {

    // Fixed plot size (not compatible with reactive sizing).
    val plotSizeProvided = if (width > 0 && height > 0) DoubleVector(width, height) else null

    // Datalore specific option - not compatible with reactive sizing.
    val datalorePreferredWidth: Double? =
        parentElement.ownerDocument?.body?.dataset?.get(DATALORE_PREFERRED_WIDTH)?.toDouble()

    val sizingPolicy = if (plotSizeProvided != null) {
        // Ignore sizing options even if provided
        SizingPolicy.fixedBoth(plotSizeProvided)
    } else when (val sizingOptions = options[SizingOption.KEY]) {
        is Map<*, *> -> SizingPolicy.create(sizingOptions)
        else -> SizingPolicy.DEFAULT
    }

    val (plotSize, plotMaxWidth) = /*if (plotSizeProvided != null) {
        SizingPolicyAdapter.SizeAndMaxWidth(plotSizeProvided, null)
    } else */if (datalorePreferredWidth != null) {
        SizingPolicyAdapter.SizeAndMaxWidth(null, null)
    } else {

        val sizingPolicyAdapter = SizingPolicyAdapter(sizingPolicy)
        sizingPolicyAdapter.monolithicSizingParameters(plotSizeProvided, parentElement)
    }

//    LOG.error { "plotSize=$plotSize, preferredWidth=$preferredWidth, maxWidth=$maxWidth " }
    val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(
        plotSpec,
        plotSize,
        plotMaxWidth,
        datalorePreferredWidth,
    )
    if (buildResult.isError) {
        val errorMessage = (buildResult as Error).error
        showError(errorMessage, parentElement)
        return null
    }

    val success = buildResult as Success
    val computationMessages = success.buildInfos.flatMap { it.computationMessages }
    computationMessages.forEach {
        showInfo(it, parentElement)
    }

    val figureModel = if (success.buildInfos.size == 1) {
        // a single figure
        val buildInfo = success.buildInfos[0]
        val result = FigureToHtml(buildInfo, parentElement, mouseEventSource).eval()
        FigureModelJs(
            plotSpec,
            MonolithicParameters(width, height, parentElement, mouseEventSource, options),
            result.toolEventDispatcher,
            result.figureRegistration
        )
    } else {
        // a bunch
        buildGGBunchComponent(success.buildInfos, parentElement, mouseEventSource)
        null
    }

    return figureModel
}

fun buildGGBunchComponent(
    plotInfos: List<FigureBuildInfo>,
    parentElement: HTMLElement,
    mouseEventSource: MouseEventSource
) {
    val bunchBounds = plotInfos.map { it.bounds }
        .fold(DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)) { acc, bounds ->
            acc.union(bounds)
        }

    FigureToHtml.setupRootHTMLElement(
        parentElement,
        bunchBounds.dimension
    )

    for (plotInfo in plotInfos) {
        val origin = plotInfo.bounds.origin
        val itemContainerElement = FigureToHtml.createContainerElement(origin)
        parentElement.appendChild(itemContainerElement)

        FigureToHtml(
            buildInfo = plotInfo,
            containerElement = itemContainerElement,
            mouseEventSource = mouseEventSource
        ).eval()

    }
}

private fun handleException(e: RuntimeException, parentElement: HTMLElement) {
    val failureInfo = FailureHandler.failureInfo(e)
    showError(failureInfo.message, parentElement)
    if (failureInfo.isInternalError) {
        LOG.error(e) { "Unexpected situation in 'MonolithicJs'" }
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
