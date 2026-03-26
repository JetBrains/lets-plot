/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.app.wasmjs

import kotlinx.browser.document
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.platf.w3c.canvas.DomCanvasView
import org.jetbrains.letsPlot.raster.view.PlotCanvasDrawable
import org.w3c.dom.HTMLDivElement

fun main() {
    val densityPlotModel = DensityPlotModel()
    densityPlotModel.step(n = 600)

    val initialPlotSpec = densityPlotModel.buildPlotSpec()

    val plotDrawable = PlotCanvasDrawable()
    plotDrawable.update(
        processedSpec = MonolithicCommon.processRawSpecs(initialPlotSpec),
        sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio = false)
    ) { }

    val domCanvasView = DomCanvasView().apply {
        content = plotDrawable
        setSize(MIN_PLOT_WIDTH, MIN_PLOT_HEIGHT)
    }

    val canvasHost = document.getElementById(PLOT_HOST_ID) as HTMLDivElement
    domCanvasView.attachTo(canvasHost)

    var lastWidth = 0
    var lastHeight = 0

    fun renderPlot(rawSpec: MutableMap<String, Any>) {
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec)
        plotDrawable.update(
            processedSpec = processedSpec,
            sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio = false)
        ) { }
    }

    fun resizePlot() {
        val width = canvasHost.clientWidth.coerceAtLeast(MIN_PLOT_WIDTH)
        val height = canvasHost.clientHeight.coerceAtLeast(MIN_PLOT_HEIGHT)
        if (width == lastWidth && height == lastHeight) return

        lastWidth = width
        lastHeight = height
        domCanvasView.setSize(width, height)
    }

    resizePlot()
    ResizeObserver(::resizePlot).observe(canvasHost)

    val animationController = AnimationController(densityPlotModel, ::renderPlot)
    animationController.initControls()
}

private external class ResizeObserver(callback: () -> Unit) {
    fun observe(target: HTMLDivElement)
}

private const val MIN_PLOT_WIDTH = 320
private const val MIN_PLOT_HEIGHT = 240
private const val PLOT_HOST_ID = "plot-host"
