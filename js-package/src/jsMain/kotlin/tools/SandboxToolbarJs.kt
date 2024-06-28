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
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_RESULT_DATA_BOUNDS
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_ACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_COMPLETED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_DEACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolInteractionSpec
import org.jetbrains.letsPlot.core.spec.Option.SpecOverride
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicFromAnyQ
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicObjectToMap
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement

@OptIn(ExperimentalJsExport::class)
@JsName("SandboxToolbar")
@JsExport
class SandboxToolbarJs() {
    private val element: HTMLElement = document.createElement("div") as HTMLElement
    private val toolButtons: List<Pair<Tool, HTMLButtonElement>>
    private var figureModel: FigureModelJs? = null

    init {
        element.style.display = "flex"
        element.style.justifyContent = "right"
//        element.style.alignItems = "center"
        element.style.padding = "10px"
        element.style.backgroundColor = "#f0f0f0"

        toolButtons = listOf(
            toolButton(PAN_TOOL_SPEC),
            toolButton(BOX_ZOOM_TOOL_SPEC),
            toolButton(WHEEL_ZOOM_TOOL_SPEC),
            toolButton(WHEEL_BOX_ZOOM_TOOL_SPEC),
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
        check(this.figureModel == null) { "Tollbar is already bound to another figure." }
        this.figureModel = figure
        figure.onToolEvent { e: dynamic ->
            val event = dynamicObjectToMap(e)
            println("Tool event: $event")
            when (event[EVENT_NAME]) {
                INTERACTION_ACTIVATED, INTERACTION_DEACTIVATED -> {
                    val toolButtonName = event[EVENT_INTERACTION_ORIGIN] as String
                    val activated = event[EVENT_NAME] == INTERACTION_ACTIVATED
                    toolButtons.find { it.first.name == toolButtonName }?.let {
                        it.first.active = activated
                        it.second.textContent = "${it.first.label} ${if (activated) "on" else "off"}"
                    }
                }

                INTERACTION_COMPLETED -> {
                    event[EVENT_RESULT_DATA_BOUNDS]?.let { bounds ->
                        @Suppress("UNCHECKED_CAST")
                        bounds as List<Double?>
                        LOG.info { "bounds: $bounds" }
                        val specOverride = HashMap<String, Any>().also { map ->
                            val xlim = listOf(bounds[0], bounds[2])
                            if (xlim.filterNotNull().isNotEmpty()) {
                                map[SpecOverride.COORD_XLIM_TRANSFORMED] = xlim
                            }
                            val ylim = listOf(bounds[1], bounds[3])
                            if (ylim.filterNotNull().isNotEmpty()) {
                                map[SpecOverride.COORD_YLIM_TRANSFORMED] = ylim
                            }
                        }
                        LOG.info { "specOverride: $specOverride" }
                        val specOverrideJs = dynamicFromAnyQ(specOverride)
                        figureModel?.updateView(specOverrideJs)
                    }
                }

                else -> {}
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
            figureModel?.updateView()
        })
        return button
    }

    private fun activateTool(tool: Tool) {
        if (!tool.active) {
            figureModel?.activateInteraction(
                origin = tool.name,
                interactionSpecListJs = dynamicFromAnyQ(tool.interactionSpecList)
            ) ?: LOG.info { "The toolbar is unbound." }
        }
    }

    private fun deactivateTool(tool: Tool) {
        if (tool.active) {
            figureModel?.deactivateInteractions(tool.name)
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
        val interactionSpecList = spec.getValue("interactions") as List<Map<String, Any>>
        var active: Boolean = false
    }

    companion object {
        private val LOG = PortableLogging.logger("SandboxToolbar")

        val PAN_TOOL_SPEC = mapOf(
            "name" to "my-pan",
            "label" to "Pan",
            "interactions" to listOf(
                mapOf(
                    ToolInteractionSpec.NAME to ToolInteractionSpec.DRAG_PAN
                )
            )
        )
        val BOX_ZOOM_TOOL_SPEC = mapOf(
            "name" to "my-zoom-box",
            "label" to "Zoom Box",
            "interactions" to listOf(
                mapOf(
                    ToolInteractionSpec.NAME to ToolInteractionSpec.BOX_ZOOM
                )
            )
        )
        val WHEEL_ZOOM_TOOL_SPEC = mapOf(
            "name" to "my-zoom-wheel",
            "label" to "Zoom Wheel",
            "interactions" to listOf(
                mapOf(
                    ToolInteractionSpec.NAME to ToolInteractionSpec.WHEEL_ZOOM
                )
            )
        )
        val WHEEL_BOX_ZOOM_TOOL_SPEC = mapOf(
            "name" to "my-zoom-wheel-box",
            "label" to "Zoom Wheel/Box",
            "interactions" to listOf(
                mapOf(
                    ToolInteractionSpec.NAME to ToolInteractionSpec.BOX_ZOOM
                ),
                mapOf(
                    ToolInteractionSpec.NAME to ToolInteractionSpec.WHEEL_ZOOM
                )
            )
        )

    }
}