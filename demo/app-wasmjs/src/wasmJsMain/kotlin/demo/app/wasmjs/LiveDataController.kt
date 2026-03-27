/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

package demo.app.wasmjs

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLParagraphElement
import kotlin.math.floor

internal class LiveDataController(
    private val densityPlotModel: DensityPlotModel,
    private val renderPlot: (MutableMap<String, Any>) -> Unit,
) {
    private val status = document.getElementById(STATUS_ID) as HTMLParagraphElement
    private val toggleButton = document.getElementById("toggle-button") as HTMLButtonElement
    private val resetButton = document.getElementById("reset-button") as HTMLButtonElement
    private val distributionInputs = document.querySelectorAll("input[name='distribution']")
    private val limitsCheckbox = document.getElementById("limits-checkbox") as HTMLInputElement

    private var isPaused = true
    private var frameRequestHandle: Int? = null
    private var previousFrameTimeMs: Double? = null
    private var statusVersion = -1

    fun initControls() {
        bindControls()
        syncDistributionSelection()
        limitsCheckbox.checked = densityPlotModel.useFixedLimits
        scheduleNextFrame()
        renderStatus()
    }

    private fun bindControls() {
        toggleButton.onclick = {
            isPaused = !isPaused
            previousFrameTimeMs = null
            renderStatus()
        }

        resetButton.onclick = {
            densityPlotModel.reset()
            renderCurrentPlot()
            renderStatus()
        }

        repeat(distributionInputs.length) { index ->
            val input = distributionInputs.item(index) as? HTMLInputElement ?: return@repeat
            input.onchange = {
                if (input.checked) {
                    densityPlotModel.distribution = Distribution.fromId(input.value)
                    renderCurrentPlot()
                    renderStatus()
                }
            }
        }

        limitsCheckbox.onchange = {
            densityPlotModel.useFixedLimits = limitsCheckbox.checked
            renderCurrentPlot()
            renderStatus()
        }
    }

    private fun renderCurrentPlot() {
        renderPlot(densityPlotModel.buildPlotSpec())
    }

    private fun scheduleNextFrame() {
        frameRequestHandle = window.requestAnimationFrame(::onFrame)
    }

    private fun onFrame(frameTimeMs: Double) {
        frameRequestHandle = null

        if (!isPaused) {
            val previousTime = previousFrameTimeMs
            if (previousTime == null) {
                previousFrameTimeMs = frameTimeMs
            } else {
                val elapsedMs = frameTimeMs - previousTime
                val steps = floor(elapsedMs / FRAME_PAUSE_MS).toInt()
                if (steps > 0) {
                    densityPlotModel.step(n = steps)
                    previousFrameTimeMs = previousTime + steps * FRAME_PAUSE_MS
                    renderCurrentPlot()
                    renderStatus()
                }
            }
        }

        scheduleNextFrame()
    }

    private fun syncDistributionSelection() {
        repeat(distributionInputs.length) { index ->
            val input = distributionInputs.item(index) as? HTMLInputElement ?: return@repeat
            input.checked = input.value == densityPlotModel.distribution.id
        }
    }

    private fun renderStatus() {
        val currentVersion = statusStateVersion()
        if (currentVersion == statusVersion) return
        statusVersion = currentVersion

        val mode = if (isPaused) "Paused" else "Running"
        val limits = if (densityPlotModel.useFixedLimits) "fixed limits" else "auto limits"
        status.textContent =
            "Live demo: $mode, ${densityPlotModel.samples.size} samples, ${densityPlotModel.distribution.label}, $limits."
        toggleButton.textContent = if (isPaused) "Run" else "Pause"
    }

    private fun statusStateVersion(): Int {
        var result = densityPlotModel.samples.size
        result = 31 * result + densityPlotModel.distribution.ordinal
        result = 31 * result + if (densityPlotModel.useFixedLimits) 1 else 0
        result = 31 * result + if (isPaused) 1 else 0
        return result
    }
}

private const val FRAME_PAUSE_MS = 16.0
