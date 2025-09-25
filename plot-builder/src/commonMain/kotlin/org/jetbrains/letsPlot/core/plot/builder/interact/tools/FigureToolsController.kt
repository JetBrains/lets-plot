/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_INTERACTION_ORIGIN
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_INTERACTION_TARGET
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_NAME
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_RESULT_DATA_BOUNDS
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_RESULT_ERROR_MSG
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_RESULT_SCALE_FACTOR
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_ACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_DEACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_UNSUPPORTED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.ROLLBACK_ALL_CHANGES
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.SELECTION_CHANGED
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.COORD_XLIM_TRANSFORMED
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.COORD_YLIM_TRANSFORMED
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.SCALE_RATIO
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.TARGET_ID

abstract class FigureToolsController {
    private val tools: MutableList<ToolAndModel> = ArrayList()

    fun registerTool(tool: ToggleTool, toolModel: ToggleToolModel): Registration {
        val toolEntry = ToolAndModel(tool, toolModel)
        tools.add(toolEntry)

        toolModel.onAction {
            when (tool.active) {
                true -> deactivateFigureTool(tool)
                false -> activateFigureTool(tool)
            }
        }

        return object : Registration() {
            override fun doRemove() {
                toolEntry.model.onAction {} // no-op
                tools.remove(toolEntry)
            }
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
                    it.model.setState(activated)
                }
            }

            SELECTION_CHANGED -> {
                val specOverride = HashMap<String, Any>()

                event[EVENT_RESULT_DATA_BOUNDS]?.let { bounds ->
                    @Suppress("UNCHECKED_CAST")
                    bounds as List<Double?>

                    specOverride[COORD_XLIM_TRANSFORMED] = listOf(bounds[0], bounds[2])
                    specOverride[COORD_YLIM_TRANSFORMED] = listOf(bounds[1], bounds[3])
                    event[EVENT_INTERACTION_TARGET]?.let { targetId ->
                        specOverride[TARGET_ID] = targetId
                    }
                }

                specOverride[SCALE_RATIO] = event[EVENT_RESULT_SCALE_FACTOR]?.let { factor ->
                    @Suppress("UNCHECKED_CAST")
                    factor as List<Double>
                } ?: listOf(1.0, 1.0)

                updateFigureView(specOverride)
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

    protected abstract fun activateFigureTool(tool: ToggleTool)
    protected abstract fun deactivateFigureTool(tool: ToggleTool)
    protected abstract fun updateFigureView(specOverride: Map<String, Any>? = null)
    protected abstract fun showFigureError(msg: String)

    private data class ToolAndModel(
        val tool: ToggleTool,
        val model: ToggleToolModel
    )
}