/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

/* root package */

import kotlinx.browser.document
import messages.OverlayMessageHandler
import messages.SimpleMessageHandler
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.FeatureSwitch.PLOT_VIEW_TOOLBOX_HTML
import org.jetbrains.letsPlot.core.spec.FailureHandler
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.MonolithicCommon.PlotsBuildResult.Error
import org.jetbrains.letsPlot.core.util.MonolithicCommon.PlotsBuildResult.Success
import org.jetbrains.letsPlot.core.util.sizing.SizingMode.*
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicObjectToMap
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import tools.DefaultToolbarJs
import tools.DefaultToolbarJs.Companion.EXPECTED_TOOLBAR_HEIGHT

private val LOG = PortableLogging.logger("MonolithicJs")

// Key for the data attibute <body data-lets-plot-preferred-width='700'>
// Used in Datalore reports to control size of the plot.
// See generated HTML (PlotHtmlHelper.kt)
private const val DATALORE_PREFERRED_WIDTH = "letsPlotPreferredWidth"

/**
 * Main entry point for creating plots from the JavaScript environment.
 *
 * Takes "raw" plot specifications (not processed by plot backend)
 * and constructs the plot with the specified sizing configuration.
 *
 * The `sizingJs` parameter is a JavaScript object with the structure:
 * {
 *   width_mode: String    // "fixed", "min", "fit", or "scaled" (case-insensitive)
 *   height_mode: String   // "fixed", "min", "fit", or "scaled" (case-insensitive)
 *   width: Number         // optional
 *   height: Number        // optional
 * }
 *
 * Sizing modes:
 *
 * 1. FIXED mode:
 *    - Uses the explicitly provided width/height values
 *    - Falls back to the default figure size if no values provided
 *    - Not responsive to container size
 *
 * 2. MIN mode:
 *    Applies the smallest dimension among:
 *    - The default figure size
 *    - The specified width/height (if provided)
 *    - The container size (if available)
 *
 * 3. FIT mode:
 *    Uses either:
 *    - The specified width/height if provided
 *    - Otherwise uses container size if available
 *    - Falls back to default figure size if neither is available
 *
 * 4. SCALED mode:
 *    - Always preserves the figure's aspect ratio
 *    - Typical usage: one dimension (usually width) uses FIXED/MIN/FIT mode
 *      and SCALED height adjusts to maintain aspect ratio
 *    - Special case: when both width and height are SCALED:
 *      * Requires container size to be available
 *      * Fits figure within container while preserving aspect ratio
 *
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
        val processedSpec = MonolithicCommon.processRawSpecs(plotSpec)

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
        handleException(e, SimpleMessageHandler(parentElement))
        null
    }
}

/**
 * Main entry point for creating plots from the JavaScript environment.
 *
 * Takes "processed" plot specifications (processed by plot backend)
 * and constructs the plot with the specified sizing configuration.
 *
 * The `sizingJs` parameter is a JavaScript object with the structure:
 * {
 *   width_mode: String    // "fixed", "min", "fit", or "scaled" (case-insensitive)
 *   height_mode: String   // "fixed", "min", "fit", or "scaled" (case-insensitive)
 *   width: Number         // optional
 *   height: Number        // optional
 * }
 *
 * Sizing modes:
 *
 * 1. FIXED mode:
 *    - Uses the explicitly provided width/height values
 *    - Falls back to the default figure size if no values provided
 *    - Not responsive to container size
 *
 * 2. MIN mode:
 *    Applies the smallest dimension among:
 *    - The default figure size
 *    - The specified width/height (if provided)
 *    - The container size (if available)
 *
 * 3. FIT mode:
 *    Uses either:
 *    - The specified width/height if provided
 *    - Otherwise uses container size if available
 *    - Falls back to default figure size if neither is available
 *
 * 4. SCALED mode:
 *    - Always preserves the figure's aspect ratio
 *    - Typical usage: one dimension (usually width) uses FIXED/MIN/FIT mode
 *      and SCALED height adjusts to maintain aspect ratio
 *    - Special case: when both width and height are SCALED:
 *      * Requires container size to be available
 *      * Fits figure within container while preserving aspect ratio
 *
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
        handleException(e, SimpleMessageHandler(parentElement))
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
        var outputDiv = document.createElement("div") as HTMLDivElement
        outputDiv.style.display = "inline-block"
        containerDiv.appendChild(outputDiv);

        // Toolbar
        var toolbar = DefaultToolbarJs();
        outputDiv.appendChild(toolbar.getElement());

        // Plot
        var plotContainer = document.createElement("div") as HTMLElement;
        plotContainer.style.position = "relative"
        outputDiv.appendChild(plotContainer);
        Pair(plotContainer, toolbar)
    } else {
        // We may want to use absolute child positioning later (see OverlayMessageHandler).
        containerDiv.style.position = "relative"
        Pair(containerDiv, null)
    }

    // Plot wrapper:
    // - will get `width` and `height` style attributes according to the plot dimensions
    //      (computed later, see: FigureToHtml.eval())
    // - will serve as an 'event target' for interactive tools
    // - will persist through the figure rebuilds via `FigureModel.updateView()`
    val wrapperDiv = document.createElement("div") as HTMLDivElement
    plotContainer.appendChild(wrapperDiv)

    // Sizing policy
    val sizingPolicy = SizingPolicy.create(sizingOptions)

    val useContainerHeight = sizingPolicy.run {
        heightMode in listOf(FIT, MIN) ||
                widthMode == SCALED && heightMode == SCALED
    }
    if (useContainerHeight && containerDiv.clientHeight <= 0) {
        containerDiv.style.height = "100%"
    }

    // Computation messages handling
    val isHeightLimited = useContainerHeight || sizingPolicy.heightMode == FIXED
    val messageHandler = createMessageHandler(
        plotContainer,
        isOverlay = isHeightLimited
    )

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

private fun createMessageHandler(plotContainer: HTMLElement, isOverlay: Boolean): MessageHandler {
    return if (isOverlay) {
        OverlayMessageHandler(plotContainer)
    } else {
        val messagesDiv = document.createElement("div") as HTMLDivElement
        plotContainer.appendChild(messagesDiv)
        SimpleMessageHandler(messagesDiv)
    }
}
