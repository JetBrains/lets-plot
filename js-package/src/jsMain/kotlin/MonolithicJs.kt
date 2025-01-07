/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

/* root package */

import kotlinx.browser.document
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.spec.FailureHandler
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.MonolithicCommon.PlotsBuildResult.Error
import org.jetbrains.letsPlot.core.util.MonolithicCommon.PlotsBuildResult.Success
import org.jetbrains.letsPlot.core.util.sizing.SizingOption
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicObjectToMap
import org.w3c.dom.HTMLDivElement
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
@OptIn(ExperimentalJsExport::class)
@Suppress("unused")
@JsName("buildPlotFromRawSpecs")
@JsExport
fun buildPlotFromRawSpecs(
    plotSpecJs: dynamic,
    width: Double,              // deprecated - do not use!!!
    height: Double,             // deprecated - do not use!!!
    parentElement: HTMLElement,
    optionsJs: dynamic = null
): FigureModelJs? {
    return try {
        check(width < 0) { "Do not use 'width' parameter: deprecated." }
        check(height < 0) { "Do not use 'height' parameter: deprecated." }

        val plotSpec = dynamicObjectToMap(plotSpecJs)
        PlotConfig.assertFigSpecOrErrorMessage(plotSpec)
        val processedSpec = MonolithicCommon.processRawSpecs(plotSpec, frontendOnly = false)
        val options: Map<String, Any> = if (optionsJs != null) {
            dynamicObjectToMap(optionsJs)
        } else {
            emptyMap()
        }

        buildPlotFromProcessedSpecsPrivate(
            processedSpec,
            parentElement,
            options
        )

    } catch (e: RuntimeException) {
        handleException(e, MessageHandler(parentElement))
        null
    }
}

/**
 * The entry point to call in JS
 * `processed specs` are plot specs processed by datalore plot backend.
 *
 * @param plotSpecJs plot specifications (a dictionary)
 * @param width number, if > 0, plot will assume given fixed width in px.
 * @param height number, if > 0, plot will assume given fixed height in px.
 * @param parentElement DOM element to add the plot to.
 *      If fixed `width/height` aren't provided, the plot size will be determined using `clientWidth` of the parent element.
 * @param optionsJs miscellaneous settings.
 *      For example, set max width to 500px:
 *                          optionsJs = {
 *                              sizing: {
 *                                  width_mode: "min",
 *                                  height_mode: "scaled",
 *                                  width: 500
 *                              }
 *                          };
 *
 */
@OptIn(ExperimentalJsExport::class)
@Suppress("unused")
@JsName("buildPlotFromProcessedSpecs")
@JsExport
fun buildPlotFromProcessedSpecs(
    plotSpecJs: dynamic,
    width: Double,              // deprecated - do not use!!!
    height: Double,             // deprecated - do not use!!!
    parentElement: HTMLElement,
    optionsJs: dynamic = null
): FigureModelJs? {
    return try {
        check(width < 0) { "Do not use 'width' parameter: deprecated." }
        check(height < 0) { "Do not use 'height' parameter: deprecated." }

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

        buildPlotFromProcessedSpecsPrivate(
            processedSpec,
            parentElement,
            options
        )
    } catch (e: RuntimeException) {
        handleException(e, MessageHandler(parentElement))
        null
    }
}

private fun buildPlotFromProcessedSpecsPrivate(
    processedSpec: Map<String, Any>,
    parentElement: HTMLElement,
    options: Map<String, Any>
): FigureModelJs? {
    // Plot wrapper:
    // - will get `width` and `height` style attributes according to the plot dimensions
    //      (computed later, see: FigureToHtml.eval())
    // - will serve as an 'event target' for interactive tools
    // - will persist through the figure rebuilds via `FigureModel.updateView()`
    val wrapperDiv = document.createElement("div") as HTMLDivElement
    parentElement.appendChild(wrapperDiv)

    // Messages div
    val messagesDiv = document.createElement("div") as HTMLDivElement
    parentElement.appendChild(messagesDiv)

    val sizingPolicy = when (val o = options[SizingOption.KEY]) {
        is Map<*, *> -> SizingPolicy.create(o)
        else -> SizingPolicy.notebookCell()   // default to 'notebook mode'.
    }

    // Datalore specific option - not compatible with reactive sizing.
    val datalorePreferredWidth: Double? =
        parentElement.ownerDocument?.body?.dataset?.get(DATALORE_PREFERRED_WIDTH)?.toDouble()

    return buildPlotFromProcessedSpecsIntern(
        processedSpec,
        wrapperDiv,
        sizingPolicy,
        datalorePreferredWidth,
        MessageHandler(messagesDiv),
    )
}

/**
 * Also used in FigureModelJs.updateView()
 */
internal fun buildPlotFromProcessedSpecsIntern(
    plotSpec: Map<String, Any>,
    wrapperElement: HTMLElement,
    sizingPolicy: SizingPolicy,
    datalorePreferredWidth: Double?,
    messageHandler: MessageHandler
): FigureModelJs? {

    @Suppress("NAME_SHADOWING")
    val sizingPolicy = if (datalorePreferredWidth != null) {
        sizingPolicy.withFixedWidth(datalorePreferredWidth)
    } else {
        sizingPolicy
    }

    val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(
        plotSpec,
        sizingPolicy
    )
    if (buildResult.isError) {
        val errorMessage = (buildResult as Error).error
        messageHandler.showError(errorMessage)
        return null
    }

    val success = buildResult as Success
    val computationMessages = success.buildInfos.flatMap { it.computationMessages }
    computationMessages.forEach {
        messageHandler.showInfo(it)
    }

    val figureModel = if (success.buildInfos.size == 1) {
        // a single figure
        val buildInfo = success.buildInfos[0]
        val result = FigureToHtml(buildInfo, wrapperElement).eval(isRoot = true)
        FigureModelJs(
            plotSpec,
            MonolithicParameters(
                wrapperElement,
                datalorePreferredWidth,
                messageHandler.toMute(),
            ),
            sizingPolicy,
            result.toolEventDispatcher,
            result.figureRegistration
        )
    } else {
        // a bunch
        buildGGBunchComponent(success.buildInfos, wrapperElement)
        null
    }

    return figureModel
}

private fun buildGGBunchComponent(
    plotInfos: List<FigureBuildInfo>,
    parentElement: HTMLElement,
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
            parentElement = itemContainerElement,
        ).eval(isRoot = false)

    }
}

private fun handleException(e: RuntimeException, messageHandler: MessageHandler) {
    val failureInfo = FailureHandler.failureInfo(e)
    messageHandler.showError(failureInfo.message)
    if (failureInfo.isInternalError) {
        LOG.error(e) { "Unexpected situation in 'MonolithicJs'" }
    }
}

internal class MessageHandler(
    private val messagesDiv: HTMLElement,
) {

    private var mute: Boolean = false

    fun showError(message: String) {
        showText(message, "lets-plot-message-error", "color:darkred;")
    }

    fun showInfo(message: String) {
        showText(message, "lets-plot-message-info", "color:darkblue;")
    }

    private fun showText(message: String, className: String, style: String) {
        if (mute) return

        val paragraphElement = messagesDiv.ownerDocument!!.createElement("p") as HTMLParagraphElement

        if (style.isNotBlank()) {
            paragraphElement.setAttribute("style", style)
        }
        paragraphElement.textContent = message
        paragraphElement.className = className
        messagesDiv.appendChild(paragraphElement)
    }

    fun toMute(): MessageHandler {
        return MessageHandler(messagesDiv).also { it.mute = true }
    }
}
