/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package tools

import FigureModelJs
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.*
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.res.ToolbarIcons
import org.jetbrains.letsPlot.platf.w3c.dom.css.setFill
import org.jetbrains.letsPlot.platf.w3c.dom.css.setStroke
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicFromAnyQ
import org.jetbrains.letsPlot.platf.w3c.jsObject.dynamicObjectToMap
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.svg.SVGSVGElement

@OptIn(ExperimentalJsExport::class)
@JsName("DefaultToolbar")
@JsExport
class DefaultToolbarJs() {

    //
    // Note:    The expected toolbar height is 35px
    //          See: PlotHtmlExport.buildHtmlFromRawSpecs()
    //

    private val element: HTMLElement = (document.createElement("div") as HTMLElement).apply {
        style.apply {
            display = "flex"
            justifyContent = "center"
//        alignItems = "center"
            padding = "0 0 5px 0"
        }
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
                padding = "2px 5px"
                backgroundColor = "$C_BACKGR"
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
            styleToolButton(this)

            title = toolSpec["label"] as String
            innerHTML = toolSpec["icon"] as String
        }

        updateButtonState(button, selected = false)

        val view = object : ToggleToolView {
            override fun setState(selected: Boolean) {
                updateButtonState(button, selected)
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

    private fun styleToolButton(button: HTMLButtonElement) {
        button.style.apply {
            width = "24px"
            height = "24px"
            margin = "0 3px"
            padding = "3px"
            border = "none"
            borderRadius = "4px"
            cursor = "pointer"
        }

        button.setAttribute(
            "onmouseover", """
            if (!this.classList.contains('$SELECTED')) {
                this.style.backgroundColor = '$C_BACKGR_HOVER';
            }
        """.trimIndent()
        )

        button.setAttribute(
            "onmouseout", """
            if (!this.classList.contains('$SELECTED')) {
                this.style.backgroundColor = '$C_BACKGR';
            }
        """.trimIndent()
        )
    }

    private fun updateButtonState(button: HTMLButtonElement, selected: Boolean) {
        if (selected) {
            button.classList.add(SELECTED)
            button.style.backgroundColor = "$C_BACKGR_SEL"

            button.querySelector("svg")?.apply {
                this as SVGSVGElement
                style.setStroke(C_STROKE_SEL)
                style.setFill(C_STROKE_SEL)
            }
        } else {
            button.classList.remove(SELECTED)
            button.style.backgroundColor = "$C_BACKGR"

            button.querySelector("svg")?.apply {
                this as SVGSVGElement
                style.setStroke(C_STROKE)
                style.setFill(C_STROKE)
            }
        }
    }


    private fun resetButton(): HTMLButtonElement {
        val button = (document.createElement("button") as HTMLButtonElement).apply {
            styleToolButton(this)

            title = "Reset"
            innerHTML = ToolbarIcons.RESET
        }

        updateButtonState(button, selected = false)

        button.addEventListener("click", {
            controller.resetFigure(deactiveTools = true)
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

        override fun showError(msg: String) {
            window.alert(msg)
        }
    }

    companion object {
        private val LOG = PortableLogging.logger("SandboxToolbar")

        private const val SELECTED = "selected"

        private const val C_BACKGR = "rgb(247, 248, 250)"
        private const val C_STROKE = "rgb(110, 110, 110)"
        private const val C_BACKGR_HOVER = "rgb(218, 219, 221)"
        private const val C_BACKGR_SEL = "rgb(69, 114, 232)"
        private const val C_STROKE_SEL = "white"
    }
}