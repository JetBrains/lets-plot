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
import org.w3c.dom.HTMLElement
import tools.DefaultToolbarJs

internal class LiveDemo {
    fun start() {
        val controls = document.getElementById(CONTROLS_ID) as HTMLElement
        val demoRoot = document.getElementById(DEMO_ROOT_ID) as HTMLElement
        controls.style.display = "block"
        demoRoot.innerHTML = ""

        val toolbarHost = (document.createElement("div") as HTMLDivElement).apply {
            demoRoot.appendChild(this)
        }
        val canvasHost = (document.createElement("div") as HTMLDivElement).apply {
            setAttribute(
                "style",
                "width: 100%; min-width: ${MIN_PLOT_WIDTH}px; min-height: ${MIN_PLOT_HEIGHT}px; height: 640px; resize: both; overflow: hidden;"
            )
            demoRoot.appendChild(this)
        }
        val sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio = false)

        val domCanvasView = DomCanvasView().apply {
            setSize(MIN_PLOT_WIDTH, MIN_PLOT_HEIGHT)
        }
        domCanvasView.attachTo(canvasHost)

        val densityPlotModel = DensityPlotModel()
        densityPlotModel.step(n = 600)

        val initialPlotSpec = densityPlotModel.buildPlotSpec().apply {
            put(Option.Meta.Kind.GG_TOOLBAR, emptyMap<String, Any>())
        }
        val initialProcessedSpec = MonolithicCommon.processRawSpecs(initialPlotSpec)

        val plotDrawable = PlotCanvasDrawable()
        plotDrawable.update(
            processedSpec = initialProcessedSpec,
            sizingPolicy = sizingPolicy
        ) { }
        domCanvasView.content = plotDrawable

        var lastWidth = 0
        var lastHeight = 0
        var figureModel: PlotCanvasFigureModel? = null
        var toolbar: DefaultToolbarJs? = null

        fun updateToolbar(processedSpec: Map<String, Any>) {
            figureModel?.dispose()
            figureModel = null

            toolbarHost.innerHTML = ""
            toolbar = null

            if (!processedSpec.containsKey(Option.Meta.Kind.GG_TOOLBAR)) {
                return
            }

            val nextFigureModel = PlotCanvasFigureModel(
                plotDrawable = plotDrawable,
                processedSpec = processedSpec,
                sizingPolicyProvider = { sizingPolicy }
            )
            val nextToolbar = DefaultToolbarJs().also {
                toolbarHost.appendChild(it.getElement())
                it.bind(nextFigureModel)
            }

            figureModel = nextFigureModel
            toolbar = nextToolbar
        }

        fun renderPlot(rawSpec: MutableMap<String, Any>) {
            val processedSpec = MonolithicCommon.processRawSpecs(rawSpec)
            plotDrawable.update(
                processedSpec = processedSpec,
                sizingPolicy = sizingPolicy
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

        LiveDataController(densityPlotModel, ::renderPlot).initControls()
    }

    companion object {
        internal const val MIN_PLOT_WIDTH = 320
        internal const val MIN_PLOT_HEIGHT = 240
    }
}

internal external class ResizeObserver(callback: () -> Unit) {
    fun observe(target: HTMLDivElement)
}
