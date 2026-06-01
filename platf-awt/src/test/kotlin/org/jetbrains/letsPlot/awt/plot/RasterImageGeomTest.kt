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
import kotlin.test.Test

class RasterImageGeomTest : PlotVisualTestBase() {
    @get:Rule
    var currentTest = TestName()

    override val canvasPeer: CanvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
    override val imageComparer: ImageComparer = ImageComparer(canvasPeer, AwtBitmapIO(subdir = "geoms"))

    override fun currentTestName(): String? = currentTest.methodName

    @Test
    fun plot_geomRaster_export() {
        val spec = parsePlotSpec(
            """
            {
              "data": {
                "x": [ -1.0, 1.0, -1.0, 1.0 ],
                "y": [ -1.0, -1.0, 1.0, 1.0 ],
                "z": [ 0.024, 0.094, 0.094, 0.024 ]
              },
              "kind": "plot",
              "scales": [
                {
                  "aesthetic": "fill",
                  "low": "#54278f",
                  "high": "#f2f0f7",
                  "scale_mapper_kind": "color_gradient",
                  "guide": "none"
                }
              ],
              "layers": [
                {
                  "geom": "raster",
                  "mapping": { "x": "x", "y": "y", "fill": "z" }
                }
              ]
            }
        """.trimIndent()
        )

        val plotCanvasDrawable = createPlotFromSpec(spec)

        assertBitmap(plotCanvasDrawable)
    }

    @Test
    fun plot_geomImshow_export() {
        val spec = parsePlotSpec(
            """
            |{
            |    "kind": "plot",
            |    "layers": [
            |        {
            |            "geom": "image",
            |            "xmin": 0.0,
            |            "xmax": 60.0,
            |            "ymin": 0.0,
            |            "ymax": 20.0,
            |            "href": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAABCAYAAAD0In+KAAAAEUlEQVR42mNgYGBo+P//fwMADAAD/kv6htYAAAAASUVORK5CYII="
            |        }
            |    ]
            |}
        """.trimMargin()
        )

        val plotCanvasDrawable = createPlotFromSpec(spec)

        assertBitmap(plotCanvasDrawable)
    }
}
