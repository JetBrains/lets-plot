/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

import org.jetbrains.letsPlot.commons.registration.Registration

/**
 * This class manages the business logic of toolbar functionality
 *
 * Platform-specific toolbar implementations can use this class and provide
 * concrete implementations for creating and adding UI components.
 */
abstract class FigureToolbarSupport {
    private val tools = mutableListOf<Pair<ToggleTool, ToggleToolView>>()
    private var resetButton: ActionToolView? = null
    private var toolEventCallbackRegistrations: Registration? = null

    fun initializeUI() {
        TOOL_SPECS.forEach { toolSpec ->
            val tool = ToggleTool(toolSpec)
            val toolButton = addToggleTool(tool)
            tools.add(tool to toolButton)
        }

        resetButton = addResetButton()
    }

    fun attach(figureModel: FigureModel) {
        check(toolEventCallbackRegistrations == null) { "Toolbar is already attached." }

        val controller = DefaultFigureToolsController(
            figure = figureModel,
            errorMessageHandler = ::errorMessageHandler
        )

        // Register tools and 'reset' button with the controller
        tools.forEach { (tool, toolButton) ->
            controller.registerTool(tool, toolButton)
        }

        resetButton?.onAction {
            controller.resetFigure(deactiveTools = true)
        }

        // Listen to tool events from the figure model
        toolEventCallbackRegistrations = figureModel.addToolEventCallback({ event: Map<String, Any> ->
            controller.handleToolFeedback(event)
        })
    }

    fun detach() {
        toolEventCallbackRegistrations?.remove()
        toolEventCallbackRegistrations = null
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