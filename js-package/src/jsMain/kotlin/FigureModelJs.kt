/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_NAME
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_ACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolInteractionSpec
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
    private var toolEventHandler: ((dynamic) -> Unit)? = null
    private val activeInteractionsByOrigin: MutableMap<String, MutableList<Map<String, Any>>> = HashMap()

    fun updateView() {
        figureRegistration?.dispose()
        figureRegistration = null

        val figureSize = buildInfo.bounds.dimension
        val figureNewSize = sizingPolicy.resize(figureSize, parentElement)
        val newBuildInfo = buildInfo.withPreferredSize(figureNewSize)

        val result = FigureToHtml(newBuildInfo, parentElement).eval()
        toolEventDispatcher = result.toolEventDispatcher
        figureRegistration = result.figureRegistration
    }

    fun onToolEvent(callback: (dynamic) -> Unit) {
        toolEventHandler = callback
    }

    fun activateInteraction(origin: String, interactionSpecJs: dynamic) {
        val interactionSpec = dynamicObjectToMap(interactionSpecJs)
        val responce: Map<String, Any> = toolEventDispatcher.activateInteraction(origin, interactionSpec)
        if (responce.getValue(EVENT_NAME) == INTERACTION_ACTIVATED) {
            activeInteractionsByOrigin.getOrPut(origin) { ArrayList<Map<String, Any>>() }.add(interactionSpec)
        }
        toolEventHandler?.invoke(dynamicObjectFromMap(responce))
    }

    fun deactivateInteractions(origin: String) {
        activeInteractionsByOrigin.remove(origin)?.forEach { interactionSpec ->
            val interactionName = interactionSpec.getValue(ToolInteractionSpec.NAME) as String
            val responce: Map<String, Any> = toolEventDispatcher.deactivateInteraction(origin, interactionName)
            toolEventHandler?.invoke(dynamicObjectFromMap(responce))
        }
    }

    companion object {
        private val LOG = PortableLogging.logger("FigureModelJs")
    }
}