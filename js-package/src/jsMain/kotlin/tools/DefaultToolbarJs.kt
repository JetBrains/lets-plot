/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package tools

import FigureModelJs
import kotlinx.browser.document
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.*
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.res.ToolbarIcons
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicFromAnyQ
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicObjectToMap
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

@OptIn(ExperimentalJsExport::class)
@JsName("DefaultToolbar")
@JsExport
class DefaultToolbarJs() {
    private val element: HTMLElement = (document.createElement("div") as HTMLElement).apply {
        style.display = "flex"
        style.justifyContent = "center"
//        style.alignItems = "center"
//        style.padding = "10px"
        style.padding = "0 0 5px 0"
//        style.backgroundColor = "#f0f0f0"
    }

    private var figureModel: FigureModelJs? = null

    private val controller = DefaultToolbarController(
        figure = FigureModelAdapterJs()
    )

    init {
        val toolbar = (document.createElement("div") as HTMLDivElement).apply {
            style.apply {
                position = "relative"
                display = "inline-flex"
                justifyContent = "center"
                padding = "5px"
                backgroundColor = "rgb(240, 240, 240)"
                border = "1px solid rgb(200, 200, 200)"
                borderRadius = "8px"
            }
        }
        element.appendChild(toolbar)

        listOf(
            ToolSpecs.PAN_TOOL_SPEC,
            ToolSpecs.BBOX_ZOOM_TOOL_SPEC,
            ToolSpecs.CBOX_ZOOM_TOOL_SPEC,
        ).forEach {
            val button = createToolButton(it)
            toolbar.appendChild(button)
        }

        toolbar.appendChild(resetButton())
    }

    fun getElement(): HTMLElement {
        return element
    }

    fun bind(figure: FigureModelJs) {
        check(this.figureModel == null) { "Toolbar is already bound to another figure." }
        this.figureModel = figure
        figure.onToolEvent { e: dynamic ->
            val event = dynamicObjectToMap(e)
            println("Tool event: $event")
            controller.handleToolFeedback(event)
        }
    }

    private fun createToolButton(toolSpec: Map<String, Any>): HTMLButtonElement {
        val tool = ToggleTool(toolSpec)
        val button = (document.createElement("button") as HTMLButtonElement).apply {
            style.width = "24px"
            style.height = "24px"
            style.margin = "0 3px"
            style.padding = "3px"
//            style.backgroundColor = "transparent"
            style.border = "none"
            style.borderRadius = "4px"
            style.cursor = "pointer"

            title = toolSpec["label"] as String
            innerHTML = toolSpec["icon"] as String
        }

        updateToggleButtonState(button, on = false)

        val view = object : ToggleToolView {
            override fun setState(on: Boolean) {
                updateToggleButtonState(button, on)
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

    private fun updateToggleButtonState(button: HTMLButtonElement, on: Boolean) {
        if (on) {
            button.style.backgroundColor = "rgb(91, 145, 202)"
            button.setAttribute("onmouseover", "this.style.backgroundColor='rgb(110, 175, 241)'")
            button.setAttribute("onmouseout", "this.style.backgroundColor='rgb(91, 145, 202)'")
        } else {
            button.style.backgroundColor = "transparent"
            button.setAttribute("onmouseover", "this.style.backgroundColor='rgb(220, 220, 220)'")
            button.setAttribute("onmouseout", "this.style.backgroundColor='transparent'")
        }
    }


    private fun resetButton(): HTMLButtonElement {
        val button = (document.createElement("button") as HTMLButtonElement).apply {
            style.width = "24px"
            style.height = "24px"
            style.margin = "0 3px"
            style.padding = "3px"
            style.backgroundColor = "transparent"
            style.border = "none"
            style.borderRadius = "4px"
            style.cursor = "pointer"

            title = "Reset"
            innerHTML = ToolbarIcons.RESET
        }

        button.setAttribute("onmouseover", "this.style.backgroundColor='rgb(220, 220, 220)'")
        button.setAttribute("onmouseout", "this.style.backgroundColor='transparent'")

        button.addEventListener("click", {
            controller.reset()
        })
        return button
    }

    inner class FigureModelAdapterJs : FigureModelAdapter {
        override fun activateTool(@Suppress("NON_EXPORTABLE_TYPE") tool: ToggleTool) {
            if (!tool.active) {
                figureModel?.activateInteractions(
                    origin = tool.name,
                    interactionSpecListJs = dynamicFromAnyQ(tool.interactionSpecList)
                ) ?: LOG.info { "The toolbar is unbound." }
            }
        }

        override fun deactivateTool(@Suppress("NON_EXPORTABLE_TYPE") tool: ToggleTool) {
            if (tool.active) {
                figureModel?.deactivateInteractions(tool.name)
                    ?: LOG.info { "The toolbar is unbound." }
            }
        }

        override fun updateView(specOverride: Map<String, Any>?) {
            figureModel?.updateView(dynamicFromAnyQ(specOverride))
        }
    }


    companion object {
        private val LOG = PortableLogging.logger("SandboxToolbar")
    }
}