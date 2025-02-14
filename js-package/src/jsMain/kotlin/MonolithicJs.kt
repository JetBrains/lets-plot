/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

/* root package */

import kotlinx.browser.document
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.FeatureSwitch.PLOT_VIEW_TOOLBOX_HTML
import org.jetbrains.letsPlot.core.spec.FailureHandler
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.MonolithicCommon.PlotsBuildResult.Error
import org.jetbrains.letsPlot.core.util.MonolithicCommon.PlotsBuildResult.Success
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicObjectToMap
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLParagraphElement
import tools.DefaultToolbarJs
import tools.DefaultToolbarJs.Companion.EXPECTED_TOOLBAR_HEIGHT

private val LOG = PortableLogging.logger("MonolithicJs")

// Key for the data attibute <body data-lets-plot-preferred-width='700'>
// Used in Datalore reports to control size of the plot.
// See generated HTML (PlotHtmlHelper.kt)
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
    parentElement: HTMLElement,
    sizingJs: dynamic,
    optionsJs: dynamic = null
): FigureModelJs? {
    return try {
        val plotSpec = dynamicObjectToMap(plotSpecJs)
        PlotConfig.assertFigSpecOrErrorMessage(plotSpec)
        val processedSpec = MonolithicCommon.processRawSpecs(plotSpec, frontendOnly = false)

        @Suppress("DuplicatedCode")
        val sizingOptions: Map<String, Any> = dynamicObjectToMap(sizingJs)
        val options: Map<String, Any> = if (optionsJs != null) {
            dynamicObjectToMap(optionsJs)
        } else {
            emptyMap()
        }

        buildPlotFromProcessedSpecsPrivate(
            processedSpec,
            parentElement,
            sizingOptions,
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
 * @param parentElement DOM element to add the plot to.
 *      The plot size will be determined using `clientWidth` of the parent element.
 * @param optionsJs miscellaneous settings.
 *      For example, set max width to 500px:
 *                          optionsJs = {
 *                              sizing: {
 *                                  width_mode: "min",
 *                                  height_mode: "scaled",
 *                                  width: 500
 *                              }
 *                          };
 */
@OptIn(ExperimentalJsExport::class)
@Suppress("unused")
@JsName("buildPlotFromProcessedSpecs")
@JsExport
fun buildPlotFromProcessedSpecs(
    plotSpecJs: dynamic,
    parentElement: HTMLElement,
    sizingJs: dynamic,
    optionsJs: dynamic = null
): FigureModelJs? {
    return try {
        val plotSpec = dynamicObjectToMap(plotSpecJs)
        // Though the "plotSpec" might contain already "processed" specs,
        // we apply "frontend" transforms anyway, just to be sure that
        // we are going to use a truly processed specs.
        val processedSpec = MonolithicCommon.processRawSpecs(plotSpec, frontendOnly = true)
        val sizingOptions: Map<String, Any> = dynamicObjectToMap(sizingJs)
        val options: Map<String, Any> = if (optionsJs != null) {
            dynamicObjectToMap(optionsJs)
        } else {
            emptyMap()
        }

        buildPlotFromProcessedSpecsPrivate(
            processedSpec,
            parentElement,
            sizingOptions,
            options
        )
    } catch (e: RuntimeException) {
        handleException(e, MessageHandler(parentElement))
        null
    }
}

private fun buildPlotFromProcessedSpecsPrivate(
    processedSpec: Map<String, Any>,
    containerDiv: HTMLElement,
    sizingOptions: Map<String, Any>,
    options: Map<String, Any>
): FigureModelJs? {

    val showToolbar = PLOT_VIEW_TOOLBOX_HTML || processedSpec.containsKey(Option.Meta.Kind.GG_TOOLBAR)
    var (plotContainer: HTMLElement, toolbar: DefaultToolbarJs?) = if (showToolbar) {
        // Wrapper for toolbar and chart
        var outputDiv = document.createElement("div")
        outputDiv.setAttribute("style", "display: inline-block;");
        containerDiv.appendChild(outputDiv);

        // Toolbar
        var toolbar = DefaultToolbarJs();
        outputDiv.appendChild(toolbar.getElement());

        // Plot
        var plotContainer = document.createElement("div") as HTMLElement;
        outputDiv.appendChild(plotContainer);
        Pair(plotContainer, toolbar)
    } else {
        Pair(containerDiv, null)
    }

    // Plot wrapper:
    // - will get `width` and `height` style attributes according to the plot dimensions
    //      (computed later, see: FigureToHtml.eval())
    // - will serve as an 'event target' for interactive tools
    // - will persist through the figure rebuilds via `FigureModel.updateView()`
    val wrapperDiv = document.createElement("div") as HTMLDivElement
    plotContainer.appendChild(wrapperDiv)

    // Messages div
    // ToDo: messages should not affect size of 'container'.
    val messagesDiv = document.createElement("div") as HTMLDivElement
    plotContainer.appendChild(messagesDiv)
    val messageHandler = MessageHandler(messagesDiv)

    // Sizing policy

    // ---
    // The "letsPlotPreferredWidth" attribute is now processed in the generated HTML.
    // See: PlotHtmlHelper.kt
    // ---
//    // Datalore specific option - not compatible with reactive sizing.
//    val datalorePreferredWidth: Double? =
//        plotContainer.ownerDocument?.body?.dataset?.get(DATALORE_PREFERRED_WIDTH)?.toDouble()
//
//    val sizingPolicy = if (datalorePreferredWidth != null) {
//        SizingPolicy.dataloreReportCell(datalorePreferredWidth)
//    } else when (val o = options[SizingOption.KEY]) {
//        is Map<*, *> -> SizingPolicy.create(o)
//        else -> SizingPolicy.notebookCell()   // default to 'notebook mode'.
//    }

//    val sizingPolicy = when (val o = options[SizingOption.KEY]) {
//        is Map<*, *> -> SizingPolicy.create(o)
//        else -> SizingPolicy.notebookCell()   // default to 'notebook mode'.
//    }

    val sizingPolicy = SizingPolicy.create(sizingOptions)

    val containerSize: () -> DoubleVector = {
        val height = if (showToolbar) {
            maxOf(0.0, (containerDiv.clientHeight - EXPECTED_TOOLBAR_HEIGHT).toDouble())
        } else {
            containerDiv.clientHeight.toDouble()
        }
        DoubleVector(
            containerDiv.clientWidth.toDouble(),
            height
        )
    }

    val figureModelJs = buildPlotFromProcessedSpecsIntern(
        processedSpec,
        wrapperDiv,
        containerSize,
        sizingPolicy,
        messageHandler
    )

    if (toolbar != null && figureModelJs != null) {
        toolbar.bind(figureModelJs);
    }

    return figureModelJs
}

/**
 * Also used in FigureModelJs.updateView()
 */
internal fun buildPlotFromProcessedSpecsIntern(
    plotSpec: Map<String, Any>,
    wrapperElement: HTMLElement,
    containerSize: () -> DoubleVector,
    sizingPolicy: SizingPolicy,
    messageHandler: MessageHandler
): FigureModelJs? {

    val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(
        plotSpec,
        containerSize.invoke(),
        sizingPolicy
    )
    if (buildResult.isError) {
        val errorMessage = (buildResult as Error).error
        messageHandler.showError(errorMessage)
        return null
    }

    val success = buildResult as Success
    val computationMessages = success.buildInfo.computationMessages
    messageHandler.showComputationMessages(computationMessages)

    val result = FigureToHtml(success.buildInfo, wrapperElement).eval(isRoot = true)
    return FigureModelJs(
        plotSpec,
        wrapperElement,
        containerSize,
        sizingPolicy,
        messageHandler.toMute(),
        result.toolEventDispatcher,
        result.figureRegistration
    )
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

    fun showComputationMessages(messages: List<String>) {
        messages.forEach {
            showText(it, "lets-plot-message-info", "color:darkblue;")
        }
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
