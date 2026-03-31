/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

package demo.app.wasmjs

import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.plotson.*
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy.Companion.fitContainerSize
import org.jetbrains.letsPlot.platf.w3c.canvas.DomCanvasView
import org.jetbrains.letsPlot.raster.view.PlotCanvasDrawable
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import kotlin.math.*
import kotlin.random.Random

fun main() {
    RedrawDemo().start()
}

private class RedrawDemo {
    private val controls = document.getElementById("controls") as HTMLElement
    private val root = document.getElementById("demo-root") as HTMLElement
    private val toggleButton = document.getElementById("toggle-button") as HTMLButtonElement
    private val resetButton = document.getElementById("reset-button") as HTMLButtonElement
    private val distributionInputs = document.querySelectorAll("input[name='distribution']")
    private val limitsCheckbox = document.getElementById("limits-checkbox") as HTMLInputElement

    private val model = DensityPlotModel().apply { step(n = SAMPLE_CAPACITY) }

    private val canvasHost = (document.createElement("div") as HTMLDivElement).apply {
        setAttribute(
            "style",
            "width:100%; min-width:${MIN_PLOT_WIDTH}px; min-height:${MIN_PLOT_HEIGHT}px; height:640px; resize:both; overflow:hidden;"
        )
    }
    private val canvasView = DomCanvasView().apply {
        setSize(MIN_PLOT_WIDTH, MIN_PLOT_HEIGHT)
        attachTo(canvasHost)
    }
    private val plotDrawable = PlotCanvasDrawable().also {
        canvasView.content = it
    }

    private var previousFrameTimeMs: Double? = null
    private var paused = true
    private var lastWidth = 0
    private var lastHeight = 0

    fun start() {
        controls.hidden = false
        root.innerHTML = ""
        root.appendChild(canvasHost)

        bindControls()
        resizePlot()
        ResizeObserver(::resizePlot).observe(canvasHost)
        redraw()
        window.requestAnimationFrame(::onFrame)
    }

    private fun bindControls() {
        toggleButton.onclick = {
            paused = !paused
            previousFrameTimeMs = null
            renderControls()
        }
        resetButton.onclick = {
            model.reset()
            redraw()
        }
        limitsCheckbox.onchange = {
            model.useFixedLimits = limitsCheckbox.checked
            redraw()
        }

        repeat(distributionInputs.length) { index ->
            val input = distributionInputs.item(index) as? HTMLInputElement ?: return@repeat
            input.onchange = {
                if (input.checked) {
                    model.distribution = Distribution.fromId(input.value)
                    redraw()
                }
            }
        }

        limitsCheckbox.checked = model.useFixedLimits
        repeat(distributionInputs.length) { index ->
            val input = distributionInputs.item(index) as? HTMLInputElement ?: return@repeat
            input.checked = input.value == model.distribution.id
        }
    }

    private fun redraw() {
        val processedSpec = MonolithicCommon.processRawSpecs(model.buildPlotSpec())

        plotDrawable.update(processedSpec, fitContainerSize(preserveAspectRatio = false)) { }
        renderControls()
    }

    private fun resizePlot() {
        val width = canvasHost.clientWidth.coerceAtLeast(MIN_PLOT_WIDTH)
        val height = canvasHost.clientHeight.coerceAtLeast(MIN_PLOT_HEIGHT)
        if (width == lastWidth && height == lastHeight) return

        lastWidth = width
        lastHeight = height
        canvasView.setSize(width, height)
    }

    private fun onFrame(frameTimeMs: Double) {
        if (!paused) {
            val previousTime = previousFrameTimeMs
            if (previousTime == null) {
                previousFrameTimeMs = frameTimeMs
            } else {
                val steps = floor((frameTimeMs - previousTime) / FRAME_PAUSE_MS).toInt()
                if (steps > 0) {
                    model.step(n = steps)
                    previousFrameTimeMs = previousTime + steps * FRAME_PAUSE_MS
                    redraw()
                }
            }
        }

        window.requestAnimationFrame(::onFrame)
    }

    private fun renderControls() {
        toggleButton.textContent = if (paused) "Run" else "Pause"
    }
}


internal class DensityPlotModel(
    private val maxPoints: Int = 600,
    private val seed: Int = 12,
) {
    var distribution: Distribution = Distribution.NORMAL
    var useFixedLimits: Boolean = true
    private val mySamples = mutableListOf<Double>()
    private var random = Random(seed)

    fun step(n: Int = 1) {
        repeat(n) {
            mySamples.add(distribution.nextValue(random))
            if (mySamples.size > maxPoints) {
                mySamples.removeFirst()
            }
        }
    }

    fun reset() {
        mySamples.clear()
        random = Random(seed)
    }

    fun buildPlotSpec(): MutableMap<String, Any> {
        return plot {
            layerOptions += layer {
                geom = GeomKind.DENSITY
                data = mapOf("x" to mySamples)
                mapping = Mapping(Aes.X to "x")
                color = "black"
                size = 1.2
            }

            if (useFixedLimits) {
                scaleOptions += scale { aes = Aes.X; limits = listOf(-3.0, 3.0) }
                scaleOptions += scale { aes = Aes.Y; limits = listOf(0.0, 0.7) }
            }


        }.toJson()
    }
}

internal enum class Distribution(
    val id: String,
    val nextValue: (Random) -> Double
) {
    NORMAL(id = "normal", nextValue = { random -> normal(random) }),
    SHIFTED(id = "shifted", nextValue = { random -> normal(random, mean = 1.2, stdDev = 0.65) }),
    LAPLACE(id = "laplace", nextValue = { random -> laplace(random) });

    companion object {
        fun fromId(id: String): Distribution {
            return entries.firstOrNull { it.id == id } ?: NORMAL
        }
    }
}

private fun normal(random: Random, mean: Double = 0.0, stdDev: Double = 1.0): Double {
    val u1 = random.nextDouble()
    val u2 = random.nextDouble()
    val z0 = sqrt(-2.0 * ln(u1)) * cos(2.0 * PI * u2)
    return mean + stdDev * z0
}

private fun laplace(random: Random, mean: Double = 0.0, scale: Double = 0.7): Double {
    val u = random.nextDouble() - 0.5
    return mean - scale * if (u < 0) ln(1 + 2 * u) else -ln(1 - 2 * u)
}

private const val FRAME_PAUSE_MS = 16.0
private const val MIN_PLOT_WIDTH = 320
private const val MIN_PLOT_HEIGHT = 240
private const val SAMPLE_CAPACITY = 600

private external class ResizeObserver(callback: () -> Unit) {
    fun observe(target: HTMLDivElement)
}
