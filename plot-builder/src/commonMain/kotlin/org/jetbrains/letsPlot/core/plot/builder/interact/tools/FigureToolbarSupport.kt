/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

/**
 * This class manages the business logic of toolbar functionality
 *
 * Platform-specific toolbar implementations can use this class and provide
 * concrete implementations for creating and adding UI components.
 */
abstract class FigureToolbarSupport(
    protected val figureModel: FigureModel
) {

    private val controller = DefaultFigureToolsController(
        figure = figureModel,
        errorMessageHandler = ::errorMessageHandler
    )

    fun initialize() {
        TOOL_SPECS.forEach { toolSpec ->
            val tool = ToggleTool(toolSpec)
            val toolButton = addToggleTool(tool)
            controller.registerTool(tool, toolButton)
        }

        val resetButton = addResetButton()
        resetButton.onAction {
            controller.resetFigure(deactiveTools = true)
        }

        figureModel.onToolEvent { event ->
            controller.handleToolFeedback(event)
        }
    }

    /**
     * Create a platform-specific button for the given tool.
     */
    protected abstract fun addToggleTool(tool: ToggleTool): ToggleToolView

    /**
     * Create a platform-specific reset button.
     */
    protected abstract fun addResetButton(): ActionToolView

    /**
     * Handle error messages from tools.
     */
    protected abstract fun errorMessageHandler(message: String)

    companion object {
        private val TOOL_SPECS = listOf(
            ToolSpecs.PAN_TOOL_SPEC,
            ToolSpecs.BBOX_ZOOM_TOOL_SPEC,
            ToolSpecs.CBOX_ZOOM_TOOL_SPEC,
        )
    }
}