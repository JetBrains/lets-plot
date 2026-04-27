/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import demoAndTestShared.parsePlotSpec
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.measureTime

class PlotRenderingTest : VisualPlotTestBase(expectedImagesSubdir = "rendering") {
    @Test
    fun `with a long rendering time the race condition should not occur`() {
        val dim = sqrt(40_000.0).roundToInt()
        val rand = Random(12)
        val xs = mutableListOf<String>()
        val ys = mutableListOf<String>()
        val cs = mutableListOf<String>()

        repeat((0..dim).count()) {
            repeat((0..dim).count()) {
                xs.add(rand.nextDouble().toString())
                ys.add(rand.nextDouble().toString())
                cs.add(rand.nextDouble().toString())
            }
        }

        val spec = """
            |{
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "mapping": { "x": "x", "y": "y", "color": "col" },
            |      "size": 8.0,
            |      "alpha": 0.3,
            |      "sampling": "none",
            |      "data": {
            |        "x": [${xs.joinToString()}],
            |        "y": [${ys.joinToString()}],
            |        "col": [${cs.joinToString()}]
            |      }
            |    }
            |  ]
            |}
        """.trimMargin()

        assertPlot("plot_race_condition_test.png", parsePlotSpec(spec))
    }

    @Test
    fun `issue1423 - drawing primitives and clip-path at the same level`() {
        val rnd = Random(42)
        val n = 100
        val xs = List(n) { rnd.nextDouble() * 1000 }
        val ys = List(n) { rnd.nextDouble() * 1000 }

        val spec = """
            |{
            |  "data": {
            |    "x": [ ${xs.joinToString(", ")} ],
            |    "y": [ ${ys.joinToString(", ")} ]
            |  },
            |  "mapping": {
            |    "x": "x",
            |    "y": "y",
            |    "fill": "x"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "float", "column": "x" },
            |      { "type": "float", "column": "y" },
            |      { "type": "float", "column": "v" }
            |    ]
            |  },
            |    "theme": {
            |    "name": "classic",
            |    "line": "blank",
            |    "axis": "blank"
            |  },
            |  "kind": "plot",
            |  "layers": [ { "geom": "point", "size": 30, "show_legend": false } ],
            |  "metainfo_list": []
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        val time = measureTime {
            assertPlot("issue1423_test.png", plotSpec)
        }

        assertTrue(time.inWholeMilliseconds < 1000, "Plotting took too long: ${time.inWholeMilliseconds} ms")
    }
}
