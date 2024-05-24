/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.tools

import org.jetbrains.letsPlot.awt.plot.FigureModel
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_INTERACTION_ORIGIN
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_NAME
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_ACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolInteractionSpec
import javax.swing.JButton
import javax.swing.JPanel

internal class SandboxToolbar(
    private val figureModel: FigureModel
) : JPanel() {

    private val toolButtons: List<Pair<Tool, JButton>>

    init {
        toolButtons = listOf(
            toolButton(PAN_TOOL_SPEC),
            toolButton(BOX_ZOOM_TOOL_SPEC),
            toolButton(WHEEL_ZOOM_TOOL_SPEC),
            toolButton(WHEEL_BOX_ZOOM_TOOL_SPEC)
        )

        toolButtons.forEach {
            this.add(it.second)
        }
        this.add(resetButton())

        figureModel.onToolEvent { event ->
            println("Tool event: $event")
            val activated = event[EVENT_NAME] == INTERACTION_ACTIVATED
            val toolButtonName = event[EVENT_INTERACTION_ORIGIN] as String
            toolButtons.find { it.first.name == toolButtonName }?.let {
                it.first.active = activated
                it.second.text = "${it.first.label} ${if (activated) "on" else "off"}"
            }
        }
    }

    private fun toolButton(toolSpec: Map<String, Any>): Pair<Tool, JButton> {
        val tool = Tool(toolSpec)
        val button = JButton("${tool.label} off")
        button.addActionListener {
            when (tool.active) {
                true -> deactivateTool(tool)
                false -> activateTool(tool)
            }
        }
        return Pair(tool, button)
    }

    private fun resetButton(): JButton {
        val button = JButton("Reset")
        button.addActionListener {
            figureModel.updateView()
        }
        return button
    }

    private fun activateTool(tool: Tool) {
        if (!tool.active) {
            figureModel.activateInteractions(
                origin = tool.name,
                interactionSpecList = tool.interactionSpecList
            )
        }
    }

    private fun deactivateTool(tool: Tool) {
        if (tool.active) {
            figureModel.deactivateInteractions(tool.name)
        }
    }


    private class Tool(
        val spec: Map<String, Any>
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