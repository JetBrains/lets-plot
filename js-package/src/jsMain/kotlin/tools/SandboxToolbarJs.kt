/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package tools

import FigureModelJs
import kotlinx.browser.document
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_INTERACTION_ORIGIN
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_NAME
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_ACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolInteractionSpec
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicObjectFromMap
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicObjectToMap
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement

@OptIn(ExperimentalJsExport::class)
@JsName("SandboxToolbar")
@JsExport
class SandboxToolbarJs() {
    private val element: HTMLElement = document.createElement("div") as HTMLElement
    private val toolButtons: List<Pair<Tool, HTMLButtonElement>>
    private var figure: FigureModelJs? = null

    init {
        element.style.display = "flex"
        element.style.justifyContent = "center"
        element.style.alignItems = "center"
        element.style.padding = "10px"
        element.style.backgroundColor = "#f0f0f0"

        toolButtons = listOf(
            toolButton(PAN_TOOL_SPEC),
            toolButton(BOX_ZOOM_TOOL_SPEC),
            toolButton(WHEEL_ZOOM_TOOL_SPEC),
        )

        toolButtons.forEach {
            element.appendChild(it.second)
        }

        element.appendChild(resetButton())
    }

    fun getElement(): HTMLElement {
        return element
    }

    fun bind(figure: FigureModelJs) {
        check(this.figure == null) { "Tollbar is already bound to another figure." }
        this.figure = figure
        figure.onToolEvent { e: dynamic ->
            val event = dynamicObjectToMap(e)
            println("Tool event: $event")
            val activated = event[EVENT_NAME] == INTERACTION_ACTIVATED
            val toolButtonName = event[EVENT_INTERACTION_ORIGIN] as String
            toolButtons.find { it.first.name == toolButtonName }?.let {
                it.first.active = activated
                it.second.textContent = "${it.first.label} ${if (activated) "on" else "off"}"
            }

        }
    }

    private fun toolButton(toolSpec: Map<String, Any>): Pair<Tool, HTMLButtonElement> {
        val tool = Tool(toolSpec)
        val button = document.createElement("button") as HTMLButtonElement
        button.textContent = "${tool.label} off"
        button.style.margin = "0 5px"
        button.addEventListener("click", {
            when (tool.active) {
                true -> deactivateTool(tool)
                false -> activateTool(tool)
            }
        })
        return Pair(tool, button)
    }

    private fun resetButton(): HTMLButtonElement {
        val button = document.createElement("button") as HTMLButtonElement
        button.textContent = "Reset"
        button.style.margin = "0 5px"
        button.addEventListener("click", {
            figure?.updateView()
        })
        return button
    }

    private fun activateTool(tool: Tool) {
        if (!tool.active) {
            figure?.activateInteraction(
                origin = tool.name,
                interactionSpecJs = dynamicObjectFromMap(tool.interactionSpec)
            ) ?: LOG.info { "The toolbar is unbound." }
        }
    }

    private fun deactivateTool(tool: Tool) {
        if (tool.active) {
            figure?.deactivateInteractions(tool.name)
                ?: LOG.info { "The toolbar is unbound." }
        }
    }

    @Suppress("NON_EXPORTABLE_TYPE")
    private class Tool(
        val spec: Map<String, Any>   // <-- NON_EXPORTABLE_TYPE
    ) {
        val name = spec.getValue("name") as String
        val label = spec.getValue("label") as String

        @Suppress("UNCHECKED_CAST")
        val interactionSpec = spec.getValue("interaction") as Map<String, Any>
        var active: Boolean = false
    }

    companion object {
        private val LOG = PortableLogging.logger("SandboxToolbar")

        val PAN_TOOL_SPEC = mapOf(
            "name" to "my-pan",
            "label" to "Pan",
            "interaction" to mapOf(
                ToolInteractionSpec.NAME to ToolInteractionSpec.DRAG_PAN
            )
        )
        val BOX_ZOOM_TOOL_SPEC = mapOf(
            "name" to "my-zoom-box",
            "label" to "Zoom Box",
            "interaction" to mapOf(
                ToolInteractionSpec.NAME to ToolInteractionSpec.BOX_ZOOM
            )
        )
        val WHEEL_ZOOM_TOOL_SPEC = mapOf(
            "name" to "my-zoom-wheel",
            "label" to "Zoom Wheel",
            "interaction" to mapOf(
                ToolInteractionSpec.NAME to ToolInteractionSpec.WHEEL_ZOOM
            )
        )
    }
}