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
abstract class FigureToolbarSupport {

    private var figureModel: FigureModel? = null
    private var controller: DefaultFigureToolsController? = null
    private val tools = mutableListOf<Pair<ToggleTool, ToggleToolView>>()
    private var resetButton: ActionToolView? = null

    fun initializeUI() {
        TOOL_SPECS.forEach { toolSpec ->
            val tool = ToggleTool(toolSpec)
            val toolButton = addToggleTool(tool)
            tools.add(tool to toolButton)
        }

        resetButton = addResetButton()
    }

    fun attach(figureModel: FigureModel) {
        check(this.figureModel == null) { "Toolbar is already attached to a figure model" }

        this.figureModel = figureModel
        this.controller = DefaultFigureToolsController(
            figure = figureModel,
            errorMessageHandler = ::errorMessageHandler
        )

        registerWithController()
    }

    fun detach() {
        this.figureModel = null
        this.controller = null
    }

    private fun registerWithController() {
        val controller = this.controller ?: return
        val figureModel = this.figureModel ?: return

        tools.forEach { (tool, toolButton) ->
            controller.registerTool(tool, toolButton)
        }

        resetButton?.onAction {
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