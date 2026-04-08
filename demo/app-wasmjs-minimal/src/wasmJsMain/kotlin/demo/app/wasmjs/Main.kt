/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.app.wasmjs

import MonolithicWasmJs
import kotlinx.browser.document
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.Mapping.toOption
import org.jetbrains.letsPlot.core.spec.plotson.PlotOptions
import org.jetbrains.letsPlot.core.spec.plotson.layer
import org.jetbrains.letsPlot.core.spec.plotson.plot
import org.jetbrains.letsPlot.core.spec.plotson.toJson
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.w3c.dom.HTMLElement
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

fun main() {
    val root = document.getElementById(DEMO_ROOT_ID) as HTMLElement

    root.innerHTML = ""
    root.appendChild(
        MonolithicWasmJs.buildPlotFromRawSpecs(
            plotSpec = createPlotSpec(),
            sizingPolicy = SizingPolicy.keepFigureDefaultSize()
        )
    )
}

private fun createPlotSpec(): MutableMap<String, Any> {
    val random = Random(12)
    val cond = List(200) { "A" } + List(200) { "B" }
    val rating = List(200) { normal(random, mean = 0.0, stdDev = 1.0) } +
            List(200) { normal(random, mean = 1.0, stdDev = 1.5) }

    return plot {
        data = mapOf(
            "cond" to cond,
            "rating" to rating
        )
        mappings = mapOf(
            Aes.X to "rating",
            Aes.FILL to "cond"
        )
        size = PlotOptions.size {
            width = 700
            height = 300
        }
        layerOptions += layer {
            geom = GeomKind.DENSITY
            color = "dark_green"
            alpha = 0.7
        }

        properties[Option.Plot.SCALES] = listOf(
            mapOf(
                Option.Scale.AES to toOption(Aes.FILL),
                Option.Scale.PALETTE_TYPE to "seq",
                Option.Scale.SCALE_MAPPER_KIND to Option.Scale.MapperKind.COLOR_BREWER
            )
        )
    }.toJson().apply {
        put(Option.Meta.Kind.GG_TOOLBAR, emptyMap<String, Any>())
    }
}

private fun normal(random: Random, mean: Double, stdDev: Double): Double {
    val u1 = random.nextDouble()
    val u2 = random.nextDouble()
    val z0 = sqrt(-2.0 * ln(u1)) * cos(2.0 * PI * u2)
    return mean + stdDev * z0
}

internal const val DEMO_ROOT_ID = "demo-root"
