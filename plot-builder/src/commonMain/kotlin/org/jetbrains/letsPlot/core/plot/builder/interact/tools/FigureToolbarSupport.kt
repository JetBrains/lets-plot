/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration

/**
 * This class manages the business logic of toolbar functionality
 *
 * Platform-specific toolbar implementations can use this class and provide
 * concrete implementations for creating and adding UI components.
 */
abstract class FigureToolbarSupport {
    private val tools = mutableListOf<Pair<ToggleTool, ToggleToolModel>>()
    private var resetButton: ActionToolModel? = null
    private var toolEventCallbackRegistration: Registration? = null
    private var toolRegistrations = CompositeRegistration()

    fun initializeUI() {
        TOOL_SPECS.forEach { toolSpec ->
            val tool = ToggleTool(toolSpec)
            val toolButton = addToggleTool(tool)
            tools.add(tool to toolButton)
        }

        resetButton = addResetButton()
    }

    fun attach(figureModel: FigureModel) {
        check(toolEventCallbackRegistration == null) { "Toolbar is already attached." }

        val controller = DefaultFigureToolsController(
            figure = figureModel,
            errorMessageHandler = ::errorMessageHandler
        )

        // Register tools and 'reset' button with the controller
        tools.forEach { (tool, toolButton) ->
            val toolReg = controller.registerTool(tool, toolButton)
            toolRegistrations.add(toolReg)
        }

        resetButton?.onAction {
            controller.resetFigure(deactiveTools = true)
        }

        // Listen to tool events from the figure model
        toolEventCallbackRegistration = figureModel.addToolEventCallback({ event: Map<String, Any> ->
            controller.handleToolFeedback(event)
        })

        // Detauch automatically when the figure model is disposed
        // (e.g., when the plot panel is removed from the UI)
        figureModel.addDisposible(object : Disposable {
            override fun dispose() {
                detach()
            }
        })
    }

    fun detach() {
        resetButton?.onAction {} // no-op
        toolRegistrations.remove()
        toolEventCallbackRegistration?.remove()
        toolRegistrations = CompositeRegistration()
        toolEventCallbackRegistration = null
    }

    /**
     * Create a platform-specific button for the given tool.
     */
    protected abstract fun addToggleTool(tool: ToggleTool): ToggleToolModel

    /**
     * Create a platform-specific reset button.
     */
    protected abstract fun addResetButton(): ActionToolModel

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