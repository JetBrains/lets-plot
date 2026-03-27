/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.app.wasmjs

import kotlinx.browser.document
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ActionToolModel
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureToolbarSupport
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToggleTool
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToggleToolModel
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.res.ToolbarIcons
import org.jetbrains.letsPlot.platf.w3c.dom.css.setFill
import org.jetbrains.letsPlot.platf.w3c.dom.css.setStroke
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.svg.SVGSVGElement

internal class DomFigureToolbar(
    private val host: HTMLDivElement
) : FigureToolbarSupport() {
    fun render() {
        host.innerHTML = ""
        host.style.display = "flex"
        host.style.justifyContent = "center"
        host.style.alignItems = "center"
        host.style.margin = "0 0 5px 0"
        initializeUI()
    }

    fun clear() {
        super.detach()
        host.innerHTML = ""
    }

    override fun addToggleTool(tool: ToggleTool): ToggleToolModel {
        val button = createButton(
            title = tool.label,
            iconHtml = tool.spec.getValue("icon") as String
        )
        host.appendChild(button)
        return object : ToggleToolModel() {
            override fun setState(selected: Boolean) {
                updateButtonState(button, selected)
            }

            init {
                button.onclick = {
                    action()
                    null
                }
            }
        }
    }

    override fun addResetButton(): ActionToolModel {
        val button = createButton(
            title = "Reset",
            iconHtml = ToolbarIcons.RESET
        )
        host.appendChild(button)
        return ActionToolModel().also { model ->
            button.onclick = {
                model.action()
                null
            }
        }
    }

    override fun errorMessageHandler(message: String) {
        println(message)
    }

    private fun createButton(title: String, iconHtml: String): HTMLButtonElement {
        return (document.createElement("button") as HTMLButtonElement).apply {
            type = "button"
            this.title = title
            style.apply {
                width = "22px"
                height = "22px"
                margin = "0 3px"
                padding = "0"
                display = "flex"
                justifyContent = "center"
                alignItems = "center"
                border = "none"
                borderRadius = "4px"
                cursor = "pointer"
            }
            innerHTML = iconHtml
            updateButtonState(this, selected = false)
            setAttribute(
                "onmouseover", """
                if (!this.classList.contains('$SELECTED')) {
                    this.style.backgroundColor = '$C_BACKGR_HOVER';
                }
            """.trimIndent()
            )
            setAttribute(
                "onmouseout", """
                if (!this.classList.contains('$SELECTED')) {
                    this.style.backgroundColor = 'transparent';
                }
            """.trimIndent()
            )
        }
    }

    private fun updateButtonState(button: HTMLButtonElement, selected: Boolean) {
        if (selected) {
            button.classList.add(SELECTED)
            button.style.backgroundColor = C_BACKGR_SEL
            button.setAttribute("aria-pressed", "true")
            button.querySelector("svg")?.let { svg ->
                (svg as SVGSVGElement).style.apply {
                    setStroke(C_STROKE_SEL)
                    setFill(C_STROKE_SEL)
                }
            }
        } else {
            button.classList.remove(SELECTED)
            button.style.backgroundColor = "transparent"
            button.setAttribute("aria-pressed", "false")
            button.querySelector("svg")?.let { svg ->
                (svg as SVGSVGElement).style.apply {
                    setStroke(C_STROKE)
                    setFill(C_STROKE)
                }
            }
        }
    }

    companion object {
        private const val SELECTED = "selected"

        private const val C_STROKE = "rgb(110, 110, 110)"
        private const val C_BACKGR_HOVER = "rgb(218, 219, 221)"
        private const val C_BACKGR_SEL = "rgb(69, 114, 232)"
        private const val C_STROKE_SEL = "white"
    }
}
