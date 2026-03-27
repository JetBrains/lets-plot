/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.app.wasmjs

import MonolithicWasmJs
import kotlinx.browser.document
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLParagraphElement
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

internal class SimpleApiDemo {
    fun start() {
        val status = document.getElementById(STATUS_ID) as HTMLParagraphElement
        val controls = document.getElementById(CONTROLS_ID) as HTMLElement
        val root = document.getElementById(DEMO_ROOT_ID) as HTMLElement

        controls.style.display = "none"
        root.innerHTML = ""
        status.textContent = "MonolithicWasmJs: one function returns a ready HTML element with a standalone density plot and toolbar."
        root.appendChild(
            MonolithicWasmJs.buildPlotFromRawSpecs(
                plotSpec = createSimpleDemoSpec(withToolbar = true),
                sizingPolicy = SizingPolicy.keepFigureDefaultSize()
            )
        )
    }

    fun createSimpleDemoSpec(withToolbar: Boolean): MutableMap<String, Any> {
        val random = Random(12)
        val cond = List(200) { "A" } + List(200) { "B" }
        val rating = List(200) { normal(random, mean = 0.0, stdDev = 1.0) } +
                List(200) { normal(random, mean = 1.0, stdDev = 1.5) }

        return mutableMapOf(
            "kind" to "plot",
            "data" to mapOf(
                "cond" to cond,
                "rating" to rating
            ),
            "mapping" to mapOf(
                "x" to "rating",
                "fill" to "cond"
            ),
            "ggsize" to mapOf(
                "width" to 700,
                "height" to 300
            ),
            "layers" to listOf(
                mapOf(
                    "geom" to "density",
                    "color" to "dark_green",
                    "alpha" to 0.7
                )
            ),
            "scales" to listOf(
                mapOf(
                    "aesthetic" to "fill",
                    "scale_mapper_kind" to "color_brewer",
                    "type" to "seq"
                )
            ),
            "theme" to mapOf("panel_grid_major_x" to "blank")
        ).withToolbarIfNeeded(withToolbar)
    }

    private fun MutableMap<String, Any>.withToolbarIfNeeded(withToolbar: Boolean): MutableMap<String, Any> {
        if (withToolbar) {
            put(Option.Meta.Kind.GG_TOOLBAR, emptyMap<String, Any>())
        }
        return this
    }

}

private fun normal(random: Random, mean: Double, stdDev: Double): Double {
    val u1 = random.nextDouble()
    val u2 = random.nextDouble()
    val z0 = sqrt(-2.0 * ln(u1)) * cos(2.0 * PI * u2)
    return mean + stdDev * z0
}
