/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.awt.NotoFontManager
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit.CM
import org.jetbrains.letsPlot.visualtesting.AwtBitmapIO
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.jetbrains.letsPlot.visualtesting.plot.PlotVisualTestBase
import org.junit.Rule
import org.junit.rules.TestName
import kotlin.test.Test

class ImageExportVisualTest : PlotVisualTestBase() {
    @get:Rule
    var currentTest = TestName()

    override val canvasPeer: CanvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
    override val imageComparer: ImageComparer = ImageComparer(canvasPeer, AwtBitmapIO(subdir = "export"))

    override fun currentTestName(): String? = currentTest.methodName

    @Test
    fun plot_export_implicitSize() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3], "y": [4, 5, 6] },
            |  "mapping": { "x": "x", "y": "y" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 200, "height": 200 }
            |}
        """.trimMargin()

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec))
    }

    @Test
    fun plot_export_explicitSize() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3], "y": [4, 5, 6] },
            |  "mapping": { "x": "x", "y": "y" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 200, "height": 200 }
            |}
        """.trimMargin()

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec), width = 3, height = 3)
    }

    @Test
    fun plot_export_implicitSize2Xscale() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3], "y": [4, 5, 6] },
            |  "mapping": { "x": "x", "y": "y" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 200, "height": 200 }
            |}
        """.trimMargin()

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec), scale = 2.0)
    }

    @Test
    fun plot_export_explicitSize2Xscale() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3], "y": [4, 5, 6] },
            |  "mapping": { "x": "x", "y": "y" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 200, "height": 200 }
            |}
        """.trimMargin()

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec), width = 3, height = 3, scale = 2.0)
    }

    @Test
    fun plot_export_5x2cm96dpi() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ]
            |}
        """.trimMargin()

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec), width = 5, height = 2, unit = CM, dpi = 96)
    }

    @Test
    fun plot_export_5x2cm300dpi() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ]
            |}
        """.trimMargin()

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec), width = 5, height = 2, unit = CM, dpi = 300)
    }

    @Test
    fun plot_export_5x2cm300dpi2Xscale() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ]
            |}
        """.trimMargin()

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec), width = 5, height = 2, unit = CM, dpi = 300, scale = 2)
    }

    @Test
    fun plot_export_12x4cm96dpi() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ]
            |}
        """.trimMargin()

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec), width = 12, height = 4, unit = CM, dpi = 96)
    }

    @Test
    fun plot_export_12x4cm300dpi() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ]
            |}
        """.trimMargin()

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec), width = 12, height = 4, unit = CM, dpi = 300)
    }

    @Test
    fun plot_export_400x200px150dpi() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 400, "height": 200 }
            |}
        """.trimMargin()

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec), dpi = 150)
    }

    @Test
    fun plot_export_400x200px() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 400, "height": 200 }
            |}
        """.trimMargin()

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec))
    }

    @Test
    fun plot_export_400x200px2Xscale() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 400, "height": 200 }
            |}
        """.trimMargin()

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec), scale = 2)
    }

    @Test
    fun plot_export_dpiNaN() {
        val spec = parsePlotSpec(
            """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3], "y": [4, 5, 6] },
            |  "mapping": { "x": "x", "y": "y" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 200, "height": 200 }
            |}
            """.trimMargin()
        )

        assertExportedPlot(currentTestName() + ".png", spec, dpi = Double.NaN)
    }
}
