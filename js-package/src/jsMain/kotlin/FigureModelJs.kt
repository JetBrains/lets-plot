/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.spec.Option.Plot.SPEC_OVERRIDE
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
    private var toolEventDispatcher: ToolEventDispatcher,
    private var figureRegistration: Registration?,
) {
    private var toolEventCallback: ((dynamic) -> Unit)? = null

    fun onToolEvent(callback: (dynamic) -> Unit) {
        toolEventCallback = callback
        toolEventDispatcher.initToolEventCallback { event -> handleToolEvent(event) }
    }

    fun updateView(specOverrideJs: dynamic = null) {

        val specOverride: Map<String, Any>? = if (specOverrideJs != null) {
            dynamicObjectToMap(specOverrideJs)
        } else {
            null
        }

        val currentInteractions = toolEventDispatcher.deactivateAllSilently()

        figureRegistration?.dispose()
        figureRegistration = null

        val newPlotSpec = specOverride?.let {
            processedPlotSpec + mapOf(SPEC_OVERRIDE to specOverride)
        } ?: processedPlotSpec
        val newFigureModel = buildPlotFromProcessedSpecsIntern(
            newPlotSpec,
            monolithicParameters.width,
            monolithicParameters.height,
            monolithicParameters.parentElement,
            monolithicParameters.options
        )

        if (newFigureModel == null) return  // something went wrong.

        figureRegistration = newFigureModel.figureRegistration
        toolEventDispatcher = newFigureModel.toolEventDispatcher
        toolEventDispatcher.initToolEventCallback { event -> handleToolEvent(event) }

        // Re-activate interactions
        currentInteractions.forEach { (origin, interactionSpec) ->
            toolEventDispatcher.activateInteractions(origin, interactionSpec)
        }
    }

    fun activateInteraction(origin: String, interactionSpecListJs: dynamic) {
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
    val width: Double,
    val height: Double,
    val parentElement: HTMLElement,
    val options: Map<String, Any>
)