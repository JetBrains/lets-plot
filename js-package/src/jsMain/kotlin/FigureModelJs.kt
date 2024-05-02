/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_INTERACTION_NAME
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_INTERACTION_ORIGIN
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_NAME
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_ACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_DEACTIVATED
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
    private val activeInteractionsByOrigin: MutableMap<String, MutableList<String>> = HashMap()

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
        val response: List<Map<String, Any>> = toolEventDispatcher.activateInteraction(origin, interactionSpec)
        response.forEach {
            processToolEvent(it)
        }
    }

    fun deactivateInteractions(origin: String) {
        val originAndInteractionList = activeInteractionsByOrigin.flatMap { (origin, interactionList) ->
            interactionList.map {
                Pair(origin, it)
            }
        }

        originAndInteractionList.forEach { (origin, interaction) ->
            val response: Map<String, Any> = toolEventDispatcher.deactivateInteraction(origin, interaction)
            processToolEvent(response)
        }
    }

    private fun processToolEvent(event: Map<String, Any>) {
        val origin = event.getValue(EVENT_INTERACTION_ORIGIN) as String
        val interactionName = event.getValue(EVENT_INTERACTION_NAME) as String
        when (event.getValue(EVENT_NAME) as String) {
            INTERACTION_ACTIVATED -> {
                activeInteractionsByOrigin.getOrPut(origin) { ArrayList<String>() }.add(interactionName)
            }

            INTERACTION_DEACTIVATED -> {
                activeInteractionsByOrigin[origin]?.remove(interactionName)
            }

            else -> {
                throw IllegalStateException("Unexpected tool event: $event")
            }
        }
        toolEventHandler?.invoke(dynamicObjectFromMap(event))
    }


    companion object {
        private val LOG = PortableLogging.logger("FigureModelJs")
    }
}