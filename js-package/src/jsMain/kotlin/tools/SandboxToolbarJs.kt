/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package tools

import FigureModelJs
import kotlinx.browser.document
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToggleTool
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToggleToolView
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToolSpecs
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicObjectToMap
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement

@OptIn(ExperimentalJsExport::class)
@JsName("SandboxToolbar")
@JsExport
class SandboxToolbarJs() {
    private val element: HTMLElement = document.createElement("div") as HTMLElement

    private var figure: FigureModelJs? = null
    private val controller = FigureToolsControllerJs { figure }

    init {
        element.style.display = "flex"
        element.style.justifyContent = "center"
//        element.style.alignItems = "center"
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

    @Suppress("DuplicatedCode")
    fun bind(figure: FigureModelJs) {
        check(this.figure == null) { "Toolbar is already bound to another figure." }
        this.figure = figure
        figure.onToolEvent { e: dynamic ->
            val event = dynamicObjectToMap(e)
            controller.handleToolFeedback(event)
        }
    }

    private fun createToolButton(toolSpec: Map<String, Any>): HTMLButtonElement {
        val tool = ToggleTool(toolSpec)
        val button = document.createElement("button") as HTMLButtonElement
        button.textContent = "${tool.label} off"
        button.style.margin = "0 5px"

        val view = object : ToggleToolView {
            override fun setState(selected: Boolean) {
                button.textContent = "${tool.label} ${if (selected) "on" else "off"}"
            }

            override fun onAction(handler: () -> Unit) {
                button.addEventListener("click", {
                    handler()
                })
            }
        }
        controller.registerTool(tool, view)
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