/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

package demo.app.wasmjs

import kotlinx.browser.document
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.platf.w3c.canvas.DomCanvasView
import org.jetbrains.letsPlot.raster.view.PlotCanvasDrawable
import org.w3c.dom.HTMLDivElement
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

private const val MIN_PLOT_WIDTH = 320
private const val MIN_PLOT_HEIGHT = 240
private const val PLOT_HOST_ID = "plot-host"

fun main() {
    renderDemo()
}

private fun renderDemo() {
    var lastWidth = 0
    var lastHeight = 0

    val rawSpec = buildPlotSpec()
    val processedSpec = MonolithicCommon.processRawSpecs(rawSpec)

    val plotDrawable = PlotCanvasDrawable().apply {
        update(
            processedSpec = processedSpec,
            sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio = false)
        ) { }
    }

    val domCanvasView = DomCanvasView(plotDrawable).apply {
        setSize(MIN_PLOT_WIDTH, MIN_PLOT_HEIGHT)
    }

    val canvasHost = document.getElementById(PLOT_HOST_ID) as? HTMLDivElement ?: return
    canvasHost.innerHTML = ""

    domCanvasView.attachTo(canvasHost)

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
}

private fun buildPlotSpec(): MutableMap<String, Any> {
    fun normal(random: Random, mean: Double, stdDev: Double): Double {
        val u1 = random.nextDouble()
        val u2 = random.nextDouble()
        val z0 = sqrt(-2.0 * ln(u1)) * cos(2.0 * PI * u2)
        return mean + stdDev * z0
    }

    val random = Random(12)

    val cond = List(200) { "A" } + List(200) { "B" }

    val rating =
        List(200) { normal(random, 0.0, 1.0) } +
                List(200) { normal(random, 1.0, 1.5) }

    val data = mutableMapOf<String, List<Any?>>(
        "cond" to cond,
        "rating" to rating
    )

    val plotSpec = JsonSupport.parseJson("""
            |{
            |  "kind": "plot",
            |  "layers": [ 
            |    { 
            |      "geom": "density",
            |       "mapping": {
            |         "x": "rating",
            |         "fill": "cond"
            |       },
            |      "color": "dark_green", 
            |      "alpha": 0.7 
            |    } 
            |  ],
            |  "scales": [
            |    {
            |      "aesthetic": "fill",
            |      "type": "seq",
            |      "scale_mapper_kind": "color_brewer"
            |    }
            |  ],
            |  "theme": { "panel_grid_major_x": "blank" }
            |}
    """.trimMargin())

    plotSpec["data"] = data

    @Suppress("UNCHECKED_CAST")
    return plotSpec as MutableMap<String, Any>
}

private external class ResizeObserver(callback: () -> Unit) {
    fun observe(target: HTMLDivElement)
}

