/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.app.wasmjs

import kotlinx.browser.document
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.platf.w3c.canvas.DomCanvasView
import org.jetbrains.letsPlot.raster.view.PlotCanvasDrawable
import org.jetbrains.letsPlot.raster.view.PlotCanvasFigureModel
import org.w3c.dom.HTMLDivElement

fun main() {
    val densityPlotModel = DensityPlotModel()
    densityPlotModel.step(n = 600)

    val initialPlotSpec = densityPlotModel.buildPlotSpec().apply {
        put(Option.Meta.Kind.GG_TOOLBAR, emptyMap<String, Any>())
    }
    val initialProcessedSpec = MonolithicCommon.processRawSpecs(initialPlotSpec)

    val plotDrawable = PlotCanvasDrawable()
    plotDrawable.update(
        processedSpec = initialProcessedSpec,
        sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio = false)
    ) { }

    val domCanvasView = DomCanvasView().apply {
        content = plotDrawable
        setSize(MIN_PLOT_WIDTH, MIN_PLOT_HEIGHT)
    }

    val canvasHost = document.getElementById(PLOT_HOST_ID) as HTMLDivElement
    val toolbarHost = document.getElementById(TOOLBAR_HOST_ID) as HTMLDivElement
    domCanvasView.attachTo(canvasHost)

    var lastWidth = 0
    var lastHeight = 0
    var figureModel: PlotCanvasFigureModel? = null
    var toolbar: DomFigureToolbar? = null

    fun sizingPolicy(): SizingPolicy {
        return SizingPolicy.fitContainerSize(preserveAspectRatio = false)
    }

    fun updateToolbar(processedSpec: Map<String, Any>) {
        figureModel?.dispose()
        figureModel = null

        toolbar?.clear()
        toolbar = null

        if (!processedSpec.containsKey(Option.Meta.Kind.GG_TOOLBAR)) {
            return
        }

        val nextFigureModel = PlotCanvasFigureModel(
            plotDrawable = plotDrawable,
            processedSpec = processedSpec,
            sizingPolicyProvider = ::sizingPolicy
        )
        val nextToolbar = DomFigureToolbar(toolbarHost).apply {
            render()
            attach(nextFigureModel)
        }

        figureModel = nextFigureModel
        toolbar = nextToolbar
    }

    fun renderPlot(rawSpec: MutableMap<String, Any>) {
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec)
        plotDrawable.update(
            processedSpec = processedSpec,
            sizingPolicy = sizingPolicy()
        ) { }
        updateToolbar(processedSpec)
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
    updateToolbar(initialProcessedSpec)

    val animationController = AnimationController(densityPlotModel, ::renderPlot)
    animationController.initControls()
}

private external class ResizeObserver(callback: () -> Unit) {
    fun observe(target: HTMLDivElement)
}

private const val MIN_PLOT_WIDTH = 320
private const val MIN_PLOT_HEIGHT = 240
private const val TOOLBAR_HOST_ID = "plot-toolbar"
private const val PLOT_HOST_ID = "plot-host"
