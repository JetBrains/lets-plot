/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

/* root package */

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.logging.PortableLogging
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.plot.MonolithicCommon.PlotsBuildResult.Error
import jetbrains.datalore.plot.MonolithicCommon.PlotsBuildResult.Success
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.config.FailureHandler
import jetbrains.datalore.plot.config.PlotConfig
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicObjectToMap
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLParagraphElement
import org.w3c.dom.get

private val LOG = PortableLogging.logger("MonolithicJs")

// key for data attibute <body data-lets-plot-preferred-width='700'>
private const val DATALORE_PREFERRED_WIDTH = "letsPlotPreferredWidth"

/**
 * The entry point to call in JS
 * `raw specs` are plot specs not processed by datalore plot backend
 */
@Suppress("unused")
@JsName("buildPlotFromRawSpecs")
fun buildPlotFromRawSpecs(plotSpecJs: dynamic, width: Double, height: Double, parentElement: HTMLElement) {
    try {
        val plotSpec = dynamicObjectToMap(plotSpecJs)
        PlotConfig.assertFigSpecOrErrorMessage(plotSpec)
        val processedSpec = MonolithicCommon.processRawSpecs(plotSpec, frontendOnly = false)
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
        // Though the "plotSpec" might contain already "processed" specs,
        // we apply "frontend" transforms anyway, just to be sure that
        // we are going to use a truly processed specs.
        val processedSpec = MonolithicCommon.processRawSpecs(plotSpec, frontendOnly = true)
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
    val preferredWidth: Double? = parentElement.ownerDocument?.body?.dataset?.get(DATALORE_PREFERRED_WIDTH)?.toDouble()
    val parentWidth = when (val w = parentElement.clientWidth) {
        0 -> null  // parent element wasn't yet layouted
        else -> w
    }
    val maxWidth = if (preferredWidth == null) parentWidth?.toDouble() else null
//    LOG.error { "plotSize=$plotSize, preferredWidth=$preferredWidth, maxWidth=$maxWidth " }
    val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(
        plotSpec,
        plotSize,
        maxWidth,
        preferredWidth,
    )
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
        // a single figure
        val buildInfo = success.buildInfos[0]
        FigureToHtml(buildInfo, parentElement).eval()
    } else {
        // a bunch
        buildGGBunchComponent(success.buildInfos, parentElement)
    }
}

fun buildGGBunchComponent(plotInfos: List<FigureBuildInfo>, parentElement: HTMLElement) {
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
            containerElement = itemContainerElement
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
