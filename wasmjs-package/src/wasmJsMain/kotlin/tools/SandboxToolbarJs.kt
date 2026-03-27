/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

package tools

import FigureModelJs
import kotlinx.browser.document
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModel
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToggleTool
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToggleToolModel
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToolSpecs
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement

@OptIn(ExperimentalJsExport::class)
@JsName("SandboxToolbar")
//@JsExport
class SandboxToolbarJs() {
    private val element: HTMLElement = document.createElement("div") as HTMLElement

    private var figure: FigureModel? = null
    private val controller = FigureToolsControllerJs { figure }

    init {
        element.style.display = "flex"
        element.style.justifyContent = "center"
        element.style.padding = "10px"
        element.style.backgroundColor = "#f0f0f0"

        listOf(
            ToolSpecs.PAN_TOOL_SPEC,
            ToolSpecs.BBOX_ZOOM_TOOL_SPEC,
            ToolSpecs.CBOX_ZOOM_TOOL_SPEC,
        ).forEach {
            val button = createToolButton(it)
            element.appendChild(button)
        }

        element.appendChild(resetButton())
    }

    fun getElement(): HTMLElement {
        return element
    }

    fun bind(figure: FigureModelJs) {
        bind(figure.asFigureModel())
    }

    @Suppress("DuplicatedCode")
    fun bind(figure: FigureModel) {
        check(this.figure == null) { "Toolbar is already bound to another figure." }
        this.figure = figure
        figure.addToolEventCallback { event ->
            controller.handleToolFeedback(event)
        }
    }

    private fun createToolButton(toolSpec: Map<String, Any>): HTMLButtonElement {
        val tool = ToggleTool(toolSpec)
        val button = document.createElement("button") as HTMLButtonElement
        button.textContent = "${tool.label} off"
        button.style.margin = "0 5px"

        val toolModel = object : ToggleToolModel() {
            override fun setState(selected: Boolean) {
                button.textContent = "${tool.label} ${if (selected) "on" else "off"}"
            }
        }

        button.addEventListener("click", {
            toolModel.action()
        })

        controller.registerTool(tool, toolModel)
        return button
    }

    private fun resetButton(): HTMLButtonElement {
        val button = document.createElement("button") as HTMLButtonElement
        button.textContent = "Reset"
        button.style.margin = "0 5px"
        button.addEventListener("click", {
            controller.resetFigure(deactiveTools = true)
        })
        return button
    }
}
