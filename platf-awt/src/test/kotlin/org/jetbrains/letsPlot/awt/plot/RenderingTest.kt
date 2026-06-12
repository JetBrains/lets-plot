/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.awt.NotoFontManager
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.visualtesting.AwtBitmapIO
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.jetbrains.letsPlot.visualtesting.plot.PlotVisualTestBase
import org.junit.Rule
import org.junit.rules.TestName
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.measureTime

class RenderingTest : PlotVisualTestBase() {
    @get:Rule
    var currentTest = TestName()

    override val canvasPeer: CanvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
    override val imageComparer: ImageComparer = ImageComparer(canvasPeer, AwtBitmapIO(subdir = "rendering"))

    override fun currentTestName(): String? = currentTest.methodName

    @Test
    fun plot_rendering_raceCondition() {
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

        val plotCanvasDrawable = createPlotFromSpec(parsePlotSpec(spec))

        assertBitmap(plotCanvasDrawable)
    }

    @Test
    fun plot_rendering_issue1423ClipPath() {
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
            val plotCanvasDrawable = createPlotFromSpec(plotSpec)

        assertBitmap(plotCanvasDrawable)
        }

        assertTrue(time.inWholeMilliseconds < 1000, "Plotting took too long: ${time.inWholeMilliseconds} ms")
    }
}
