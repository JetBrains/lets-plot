/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("OPT_IN_USAGE")

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.InteractionSpec
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelHelper
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.TARGET_ID
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.SpecOverrideState
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
    private val wrapperElement: HTMLElement,
    private val containerSize: () -> DoubleVector,
    private var sizingPolicy: SizingPolicy,
    private val messageHandler: MessageHandler,
    private var toolEventDispatcher: ToolEventDispatcher,
    private var figureRegistration: Registration?,
) {
    private var toolEventCallback: ((dynamic) -> Unit)? = null
    private var currSpecOverrideList: List<Map<String, Any>> = emptyList()
    private var currSpecOverrideState: SpecOverrideState = SpecOverrideState(emptyList(), null)

    fun onToolEvent(callback: (dynamic) -> Unit) {
        toolEventCallback = callback
        toolEventDispatcher.initToolEventCallback { event -> handleToolEvent(event) }

        // Make snsure that 'implicit' interaction activated.
        deactivateInteractions(origin = ToolEventDispatcher.ORIGIN_FIGURE_IMPLICIT)
        activateInteractions(
            origin = ToolEventDispatcher.ORIGIN_FIGURE_IMPLICIT,
            interactionSpecListJs = dynamicFromAnyQ(FIGURE_IMPLICIT_INTERACTIONS)
        )
    }

    internal fun updateSpecOverride(specOverride: Map<String, Any>?) {
        currSpecOverrideList = FigureModelHelper.updateSpecOverrideList(
            specOverrideList = currSpecOverrideList,
            newSpecOverride = specOverride
        )
        val activeTargetId = specOverride?.get(TARGET_ID) as? String
        currSpecOverrideState = SpecOverrideState(ArrayList(currSpecOverrideList), activeTargetId)
    }

    private fun rebuildView() {
        val state = currSpecOverrideState

        val currentInteractions = toolEventDispatcher.deactivateAllSilently()

        figureRegistration?.dispose()
        figureRegistration = null

        val plotSpec = SpecOverrideUtil.applySpecOverride(processedPlotSpec, state)

        // Read back expanded overrides (non-empty only when expansion occurred).
        if (state.expandedOverrides.isNotEmpty()) {
            currSpecOverrideList = state.expandedOverrides
        }

//        LOG.info { "New sizing policy: $sizingPolicy" }
        val newFigureModel = buildPlotFromProcessedSpecsIntern(
            plotSpec,
            wrapperElement,
            containerSize,
            sizingPolicy,
            messageHandler,
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

    fun updateView(optionsJs: dynamic = null) {

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

        rebuildView()
    }

    fun activateInteractions(origin: String, interactionSpecListJs: dynamic) {
        val interactionSpecListRaw = dynamicToAnyQ(interactionSpecListJs)
        require(interactionSpecListRaw is List<*>) { "Interaction spec list expected but was: $interactionSpecListJs" }

        val interactionSpecList = interactionSpecListRaw.map { spec ->
            require(spec is Map<*, *>) { "Interaction spec (Map) expected but was: $spec" }
            InteractionSpec.fromMap(spec)
        }
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

        private val FIGURE_IMPLICIT_INTERACTIONS = listOf(
            mapOf(
                InteractionSpec.Name.PROPERTY_NAME to InteractionSpec.Name.ROLLBACK_ALL_CHANGES.value
            )
        )
    }
}
