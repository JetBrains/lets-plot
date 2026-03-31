/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

/* root package */

@file:OptIn(ExperimentalWasmJsInterop::class)

import kotlinx.browser.document
import messages.OverlayMessageHandler
import messages.SimpleMessageHandler
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.spec.FailureHandler
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.MonolithicCommon.PlotsBuildResult.Error
import org.jetbrains.letsPlot.core.util.MonolithicCommon.PlotsBuildResult.Success
import org.jetbrains.letsPlot.core.util.sizing.SizingMode
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import tools.DefaultToolbarJs
import tools.DefaultToolbarJs.Companion.EXPECTED_TOOLBAR_HEIGHT

private val MONOLITHIC_WASM_LOG = PortableLogging.logger("MonolithicWasmJs")

object MonolithicWasmJs {
    fun buildPlotFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        containerSize: DoubleVector? = null,
        sizingPolicy: SizingPolicy = SizingPolicy.keepFigureDefaultSize(),
        errorMessageComponentFactory: (String) -> HTMLElement = ::createErrorMessageComponent,
        computationMessagesHandler: (List<String>) -> Unit = {}
    ): HTMLElement {
        return try {
            val processedPlotSpec = MonolithicCommon.processRawSpecs(plotSpec)
            buildPlotFromProcessedSpecs(
                processedPlotSpec = processedPlotSpec,
                containerSize = containerSize,
                sizingPolicy = sizingPolicy,
                errorMessageComponentFactory = errorMessageComponentFactory,
                computationMessagesHandler = computationMessagesHandler
            )
        } catch (e: RuntimeException) {
            handleException(e, errorMessageComponentFactory)
        }
    }

    fun buildPlotFromProcessedSpecs(
        processedPlotSpec: MutableMap<String, Any>,
        containerSize: DoubleVector? = null,
        sizingPolicy: SizingPolicy = SizingPolicy.keepFigureDefaultSize(),
        errorMessageComponentFactory: (String) -> HTMLElement = ::createErrorMessageComponent,
        computationMessagesHandler: (List<String>) -> Unit = {}
    ): HTMLElement {
        return try {
            buildPlotComponent(
                processedPlotSpec = processedPlotSpec,
                initialContainerSize = containerSize,
                sizingPolicy = sizingPolicy,
                errorMessageComponentFactory = errorMessageComponentFactory,
                computationMessagesHandler = computationMessagesHandler
            )
        } catch (e: RuntimeException) {
            handleException(e, errorMessageComponentFactory)
        }
    }
}

private fun buildPlotComponent(
    processedPlotSpec: Map<String, Any>,
    initialContainerSize: DoubleVector?,
    sizingPolicy: SizingPolicy,
    errorMessageComponentFactory: (String) -> HTMLElement,
    computationMessagesHandler: (List<String>) -> Unit
): HTMLElement {
    val outputDiv = document.createElement("div") as HTMLDivElement
    outputDiv.style.display = "inline-block"

    val showToolbar = processedPlotSpec.containsKey(Option.Meta.Kind.GG_TOOLBAR)
    val (plotContainer, toolbar) = if (showToolbar) {
        val toolbar = DefaultToolbarJs()
        outputDiv.appendChild(toolbar.getElement())

        val plotContainer = document.createElement("div") as HTMLDivElement
        plotContainer.style.position = "relative"
        outputDiv.appendChild(plotContainer)
        plotContainer to toolbar
    } else {
        outputDiv.style.position = "relative"
        outputDiv to null
    }

    val wrapperDiv = document.createElement("div") as HTMLDivElement
    plotContainer.appendChild(wrapperDiv)

    val useContainerHeight = initialContainerSize != null || sizingPolicy.run {
        heightMode in listOf(SizingMode.FIT, SizingMode.MIN) ||
                widthMode == SizingMode.SCALED && heightMode == SizingMode.SCALED
    }
    val messageHandler = createMessageHandler(
        plotContainer = plotContainer,
        isOverlay = useContainerHeight || sizingPolicy.heightMode == SizingMode.FIXED
    )

    val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(
        processedPlotSpec,
        initialContainerSize,
        sizingPolicy,
    )
    if (buildResult.isError) {
        val errorMessage = (buildResult as Error).error
        return errorMessageComponentFactory(errorMessage)
    }

    val success = buildResult as Success
    val result = FigureToHtml(success.buildInfo, wrapperDiv).eval(isRoot = true)
    computationMessagesHandler(success.buildInfo.computationMessages)
    messageHandler.showComputationMessages(success.buildInfo.computationMessages)

    val containerSizeProvider: () -> DoubleVector = {
        val height = if (showToolbar) {
            maxOf(0.0, (outputDiv.clientHeight - EXPECTED_TOOLBAR_HEIGHT).toDouble())
        } else {
            outputDiv.clientHeight.toDouble()
        }
        DoubleVector(
            outputDiv.clientWidth.toDouble(),
            height
        )
    }

    val figureModel = FigureModelJs(
        processedPlotSpec = processedPlotSpec,
        wrapperElement = wrapperDiv,
        containerSize = containerSizeProvider,
        sizingPolicy = sizingPolicy,
        messageHandler = messageHandler.toMute(),
        toolEventDispatcher = result.toolEventDispatcher,
        figureRegistration = result.figureRegistration
    )
    toolbar?.bind(figureModel)

    return outputDiv
}

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
        sizingPolicy,
    )
    if (buildResult.isError) {
        val errorMessage = (buildResult as Error).error
        messageHandler.showError(errorMessage)
        return null
    }

    val success = buildResult as Success
    val result = FigureToHtml(success.buildInfo, wrapperElement).eval(isRoot = true)

    val computationMessages = success.buildInfo.computationMessages
    messageHandler.showComputationMessages(computationMessages)

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

private fun createErrorMessageComponent(message: String): HTMLElement {
    return (document.createElement("div") as HTMLDivElement).apply {
        textContent = message
    }
}

private fun handleException(
    e: RuntimeException,
    errorMessageComponentFactory: (String) -> HTMLElement
): HTMLElement {
    val failureInfo = FailureHandler.failureInfo(e)
    if (failureInfo.isInternalError) {
        MONOLITHIC_WASM_LOG.error(e) { "Unexpected situation in 'MonolithicWasmJs'" }
    }
    return errorMessageComponentFactory(failureInfo.message)
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
