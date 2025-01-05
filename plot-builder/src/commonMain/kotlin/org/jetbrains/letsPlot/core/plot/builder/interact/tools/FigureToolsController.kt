/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_INTERACTION_ORIGIN
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_INTERACTION_TARGET
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_NAME
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_RESULT_DATA_BOUNDS
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_RESULT_ERROR_MSG
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_ACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_DEACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_UNSUPPORTED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.ROLLBACK_ALL_CHANGES
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.SELECTION_CHANGED
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.COORD_XLIM_TRANSFORMED
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.COORD_YLIM_TRANSFORMED
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.TARGET_ID

abstract class FigureToolsController {
    private val tools: MutableList<ToolAndView> = ArrayList()

    fun registerTool(tool: ToggleTool, view: ToggleToolView) {
        tools.add(ToolAndView(tool, view))

        view.onAction {
            when (tool.active) {
                true -> deactivateFigureTool(tool)
                false -> activateFigureTool(tool)
            }
        }
    }

    fun deactivateAllTools() {
        tools.filter { it.tool.active }.forEach {
            deactivateFigureTool(it.tool)
        }
    }

    fun resetFigure(deactiveTools: Boolean) {
        if (deactiveTools) {
            tools.filter { it.tool.active }.forEach {
                deactivateFigureTool(it.tool)
            }
        }
        updateFigureView()
    }

    fun handleToolFeedback(event: Map<String, Any>) {
        when (event[EVENT_NAME]) {
            INTERACTION_ACTIVATED, INTERACTION_DEACTIVATED -> {
                val toolName = event[EVENT_INTERACTION_ORIGIN] as String
                val activated = event[EVENT_NAME] == INTERACTION_ACTIVATED
                tools.find { it.tool.name == toolName }?.let {
                    it.tool.active = activated
                    it.view.setState(activated)
                }
            }

            SELECTION_CHANGED -> {
                event[EVENT_RESULT_DATA_BOUNDS]?.let { bounds ->
                    @Suppress("UNCHECKED_CAST")
                    bounds as List<Double?>
                    val specOverride = HashMap<String, Any>().also { map ->
                        map[COORD_XLIM_TRANSFORMED] = listOf(bounds[0], bounds[2])
                        map[COORD_YLIM_TRANSFORMED] = listOf(bounds[1], bounds[3])
                        event[EVENT_INTERACTION_TARGET]?.let { targetId ->
                            map[TARGET_ID] = targetId
                        }
                    }
                    updateFigureView(specOverride)
                }
            }

            ROLLBACK_ALL_CHANGES -> {
                val targetId = event[EVENT_INTERACTION_TARGET]
                val specOverride = targetId?.let {
                    mapOf(TARGET_ID to targetId)
                }
                updateFigureView(specOverride)
            }

            INTERACTION_UNSUPPORTED -> {
                showFigureError(
                    (event[EVENT_RESULT_ERROR_MSG] as? String) ?: "Unspecified error."
                )
            }

            else -> {}
        }
    }

    abstract fun activateFigureTool(tool: ToggleTool)
    abstract fun deactivateFigureTool(tool: ToggleTool)
    abstract fun updateFigureView(specOverride: Map<String, Any>? = null)
    abstract fun showFigureError(msg: String)

    private data class ToolAndView(
        val tool: ToggleTool,
        val view: ToggleToolView
    )
}