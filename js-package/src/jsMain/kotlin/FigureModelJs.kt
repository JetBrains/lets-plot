/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicObjectFromMap
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicObjectToMap
import org.w3c.dom.HTMLElement
import sizing.SizingPolicy

@OptIn(ExperimentalJsExport::class)
@JsName("FigureModel")
@JsExport
class FigureModelJs internal constructor(
    private val parentElement: HTMLElement,
    private val sizingPolicy: SizingPolicy,
    private val buildInfo: FigureBuildInfo,
    private var toolEventDispatcher: ToolEventDispatcher,
    private var figureRegistration: Registration?,
) {
    private var toolEventCallback: ((dynamic) -> Unit)? = null

    init {
        toolEventDispatcher.initToolEventCallback { event -> handleToolEvent(event) }
    }

    fun onToolEvent(callback: (dynamic) -> Unit) {
        toolEventCallback = callback
    }

    fun updateView() {
        val currentInteractions = toolEventDispatcher.deactivateAllSilently()

        figureRegistration?.dispose()
        figureRegistration = null

        val figureSize = buildInfo.bounds.dimension
        val figureNewSize = sizingPolicy.resize(figureSize, parentElement)
        val newBuildInfo = buildInfo.withPreferredSize(figureNewSize)

        val result = FigureToHtml(newBuildInfo, parentElement).eval()
        figureRegistration = result.figureRegistration
        toolEventDispatcher = result.toolEventDispatcher
        toolEventDispatcher.initToolEventCallback { event -> handleToolEvent(event) }

        // Re-activate interactions
        currentInteractions.forEach { (origin, interactionSpec) ->
            toolEventDispatcher.activateInteractions(origin, interactionSpec)
        }
    }

    /**
     * ToDo: a tool can activate several interactions at once.
     */
    fun activateInteraction(origin: String, interactionSpecJs: dynamic) {
        val interactionSpec = dynamicObjectToMap(interactionSpecJs)
        toolEventDispatcher.activateInteractions(origin, listOf(interactionSpec))
    }

    fun deactivateInteractions(origin: String) {
        toolEventDispatcher.deactivateInteractions(origin)
    }

    private fun handleToolEvent(event: Map<String, Any>) {
        toolEventCallback?.invoke(dynamicObjectFromMap(event))
    }


    companion object {
        private val LOG = PortableLogging.logger("FigureModelJs")
    }
}