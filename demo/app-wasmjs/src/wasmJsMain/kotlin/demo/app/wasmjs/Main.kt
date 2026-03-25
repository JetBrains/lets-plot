/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.app.wasmjs

import demo.plot.common.model.plotConfig.TooltipConfig
import kotlinx.browser.document
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.platf.w3c.canvas.DomCanvasElement
import org.jetbrains.letsPlot.raster.view.PlotCanvasDrawable
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLHeadingElement
import org.w3c.dom.HTMLParagraphElement
import org.w3c.dom.HTMLPreElement

private const val MIN_PLOT_WIDTH = 320
private const val MIN_PLOT_HEIGHT = 240

private external class ResizeObserver(callback: () -> Unit) {
    fun observe(target: HTMLDivElement)
}

fun main() {
    renderDemo()
}

private fun renderDemo() {
    val body = document.body ?: return
    body.innerHTML = ""
    body.style.margin = "0"
    body.style.fontFamily = "system-ui, sans-serif"
    body.style.backgroundColor = "#f4f4f4"

    val page = (document.createElement("div") as HTMLDivElement).apply {
        style.padding = "24px"
        style.minHeight = "100vh"
        style.boxSizing = "border-box"
    }
    body.appendChild(page)

    page.appendChild((document.createElement("h2") as HTMLHeadingElement).apply {
        textContent = "app-wasmjs"
        style.margin = "0 0 8px"
    })
    page.appendChild((document.createElement("p") as HTMLParagraphElement).apply {
        textContent = "DomCanvasElement + PlotCanvasDrawable"
        style.margin = "0 0 16px"
        style.color = "#555"
    })

    val messages = (document.createElement("pre") as HTMLPreElement).apply {
        style.display = "none"
        style.margin = "16px 0 0"
        style.padding = "12px"
        style.backgroundColor = "#fff7e6"
        style.border = "1px solid #f0d9a7"
        style.whiteSpace = "pre-wrap"
    }

    val canvasHost = (document.createElement("div") as HTMLDivElement).apply {
        style.display = "block"
        style.backgroundColor = "white"
        style.border = "1px solid #d0d0d0"
        style.boxShadow = "0 4px 18px rgba(0, 0, 0, 0.08)"
        style.width = "min(960px, calc(100vw - 48px))"
        style.height = "min(70vh, 640px)"
        style.minWidth = "${MIN_PLOT_WIDTH}px"
        style.minHeight = "${MIN_PLOT_HEIGHT}px"
        style.boxSizing = "border-box"
        style.resize = "both"
        style.overflowX = "hidden"
        style.overflowY = "hidden"
    }

    page.appendChild(canvasHost)
    page.appendChild(messages)

    val rawSpec = TooltipConfig().plotSpecList()[1]

    println(rawSpec)

    val processedSpec = MonolithicCommon.processRawSpecs(rawSpec)

    val plotDrawable = PlotCanvasDrawable().apply {
        update(
            processedSpec = processedSpec,
            sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio = false)
        ) { computationMessages ->
            if (computationMessages.isEmpty()) {
                messages.style.display = "none"
                messages.textContent = ""
            } else {
                messages.style.display = "block"
                messages.textContent = computationMessages.joinToString("\n")
            }
        }
    }

    val canvas = DomCanvasElement(plotDrawable).apply {
        setSize(MIN_PLOT_WIDTH, MIN_PLOT_HEIGHT)
    }
    canvas.canvasElement.style.display = "block"
    canvasHost.appendChild(canvas.canvasElement)

    var lastWidth = 0
    var lastHeight = 0

    fun resizePlot() {
        val width = canvasHost.clientWidth.coerceAtLeast(MIN_PLOT_WIDTH)
        val height = canvasHost.clientHeight.coerceAtLeast(MIN_PLOT_HEIGHT)
        if (width == lastWidth && height == lastHeight) return

        lastWidth = width
        lastHeight = height
        canvas.setSize(width, height)
    }

    resizePlot()
    ResizeObserver(::resizePlot).observe(canvasHost)
}
