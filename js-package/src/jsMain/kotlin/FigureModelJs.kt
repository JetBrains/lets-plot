/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.plot.builder.interact.FigureImplicitInteractionSpecs
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelHelper
import org.jetbrains.letsPlot.core.spec.front.SpecOverrideUtil
import org.jetbrains.letsPlot.core.util.sizing.SizingOption
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicFromAnyQ
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicObjectToMap
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicToAnyQ
import org.w3c.dom.HTMLElement

@OptIn(ExperimentalJsExport::class)
@JsName("FigureModel")
@JsExport
class FigureModelJs internal constructor(
    private val processedPlotSpec: Map<String, Any>,
    private val monolithicParameters: MonolithicParameters,
    private var sizingPolicy: SizingPolicy,
    private var toolEventDispatcher: ToolEventDispatcher,
    private var figureRegistration: Registration?,
) {
    private var toolEventCallback: ((dynamic) -> Unit)? = null
    private var currSpecOverrideList: List<Map<String, Any>> = emptyList()

    fun onToolEvent(callback: (dynamic) -> Unit) {
        toolEventCallback = callback
        toolEventDispatcher.initToolEventCallback { event -> handleToolEvent(event) }

        // Make snsure that 'implicit' interaction activated.
        deactivateInteractions(origin = ToolEventDispatcher.ORIGIN_FIGURE_IMPLICIT)
        activateInteractions(
            origin = ToolEventDispatcher.ORIGIN_FIGURE_IMPLICIT,
            interactionSpecListJs = dynamicFromAnyQ(FigureImplicitInteractionSpecs.LIST)
        )
    }

    fun updateView(specOverrideJs: dynamic = null, optionsJs: dynamic = null) {

        // view options update (just 'sizing' at the moment).
        val options: Map<String, Any>? = if (optionsJs != null) {
            dynamicObjectToMap(optionsJs)
        } else {
            null
        }

        val sizingOptionsUpdate = options?.get(SizingOption.KEY)
        if (sizingOptionsUpdate is Map<*, *>) {
            sizingPolicy = sizingPolicy.withUpdate(sizingOptionsUpdate)
        }

        // plot specs update.
        val specOverride: Map<String, Any>? = if (specOverrideJs != null) {
            dynamicObjectToMap(specOverrideJs)
        } else {
            null
        }

        currSpecOverrideList = FigureModelHelper.updateSpecOverrideList(
            specOverrideList = currSpecOverrideList,
            newSpecOverride = specOverride
        )

        val currentInteractions = toolEventDispatcher.deactivateAllSilently()

        figureRegistration?.dispose()
        figureRegistration = null

        val plotSpec = SpecOverrideUtil.applySpecOverride(processedPlotSpec, currSpecOverrideList)

//        LOG.info { "New sizing policy: $sizingPolicy" }
        val newFigureModel = buildPlotFromProcessedSpecsIntern(
            plotSpec,
            monolithicParameters.wrapperElement,
            sizingPolicy,
            monolithicParameters.messageHandler,
        )

        if (newFigureModel == null) return  // something went wrong.

        // Grab properties and discard just created another figure model
        figureRegistration = newFigureModel.figureRegistration
        toolEventDispatcher = newFigureModel.toolEventDispatcher
        toolEventDispatcher.initToolEventCallback { event -> handleToolEvent(event) }

        // Re-activate interactions
        currentInteractions.forEach { (origin, interactionSpec) ->
            toolEventDispatcher.activateInteractions(origin, interactionSpec)
        }
    }

    fun activateInteractions(origin: String, interactionSpecListJs: dynamic) {
        val interactionSpecList = dynamicToAnyQ(interactionSpecListJs)
        require(interactionSpecList is List<*>) { "Interaction spec list expected but was: $interactionSpecListJs" }
        @Suppress("UNCHECKED_CAST")
        interactionSpecList as List<Map<String, Any>>
        toolEventDispatcher.activateInteractions(origin, interactionSpecList)
    }

    fun deactivateInteractions(origin: String) {
        toolEventDispatcher.deactivateInteractions(origin)
    }

    private fun handleToolEvent(event: Map<String, Any>) {
        toolEventCallback?.invoke(dynamicFromAnyQ(event))
    }

    companion object {
        private val LOG = PortableLogging.logger("FigureModelJs")
    }
}

internal class MonolithicParameters(
    val wrapperElement: HTMLElement,
    val messageHandler: MessageHandler,
)