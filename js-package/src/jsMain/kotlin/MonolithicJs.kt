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
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicObjectToMap
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLParagraphElement
import org.w3c.dom.get
import sizing.SizingOption
import sizing.SizingOption.HEIGHT
import sizing.SizingOption.WIDTH
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

        buildPlotFromProcessedSpecsPrivate(
            processedSpec,
            width, height,
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

        buildPlotFromProcessedSpecsPrivate(
            processedSpec,
            width, height,
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
    width: Double,
    height: Double,
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

    val sizingPolicy = createSizingPolicy(
        width, height,
        parentElement,
        options[SizingOption.KEY] as? Map<*, *>
    )

    return buildPlotFromProcessedSpecsIntern(
        processedSpec,
        width, height,
        wrapperDiv,
        sizingPolicy,
        MessageHandler(messagesDiv),
    )
}

private fun createSizingPolicy(
    width: Double,    // "not-specified" if set to value <= 0
    height: Double,   // same
    parentElement: HTMLElement,
    sizingOptions: Map<*, *>?
): SizingPolicy {

    // Fixed plot size (not compatible with reactive sizing).
    val plotSizeProvided = if (width > 0 && height > 0) DoubleVector(width, height) else null

    return if (plotSizeProvided != null) {
        // Ignore sizing options even if provided
        SizingPolicy.fixedBoth(plotSizeProvided)
    } else {
        val parentWidth = when (val w = parentElement.clientWidth) {
            0 -> null  // parent element wasn't yet layouted
            else -> w
        }?.toDouble()
        val parentHeight = when (val h = parentElement.clientHeight) {
            0 -> null  // parent element wasn't yet layouted
            else -> h
        }?.toDouble()

        when (sizingOptions) {
            is Map<*, *> -> {
                // The width/height should be in 'sizingOptions'.
                // Take the 'parentElement' dimensions as a fallback option.
                val fallbackSizingOptions = mapOf(
                    WIDTH to parentWidth,
                    HEIGHT to parentHeight,
                )
                SizingPolicy.create(fallbackSizingOptions + sizingOptions)
            }

            else -> {
                // No sizing options given - default to 'notebook mode'.
                SizingPolicy.notebookCell(parentWidth, parentHeight)
            }
        }
    }
}

internal fun buildPlotFromProcessedSpecsIntern(
    plotSpec: Map<String, Any>,
    width: Double,
    height: Double,
    wrapperElement: HTMLElement,
    sizingPolicy: SizingPolicy,
    messageHandler: MessageHandler
): FigureModelJs? {

    // Datalore specific option - not compatible with reactive sizing.
    val datalorePreferredWidth: Double? =
        wrapperElement.ownerDocument?.body?.dataset?.get(DATALORE_PREFERRED_WIDTH)?.toDouble()

    val (plotSize, plotMaxWidth) = if (datalorePreferredWidth != null) {
        SizingPolicyAdapter.SizeAndMaxWidth(null, null)
    } else {
        val sizingPolicyAdapter = SizingPolicyAdapter(sizingPolicy)
        sizingPolicyAdapter.toMonolithicSizingParameters()
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
                width,
                height,
                wrapperElement,
                sizingPolicy,
                messageHandler.toMute(),
            ),
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
