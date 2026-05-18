/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.awt.NotoFontManager
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.visualtesting.AwtBitmapIO
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.jetbrains.letsPlot.visualtesting.plot.PlotVisualTestBase
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class PointGeomTest : PlotVisualTestBase() {
    @get:Rule
    var currentTest = TestName()

    override val canvasPeer: CanvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
    override val imageComparer: ImageComparer = ImageComparer(canvasPeer, AwtBitmapIO(subdir = "geoms"))

    override fun currentTestName(): String? = currentTest.methodName

    @Test
    fun plot_geomPoint_sizeUnit() {
        val spec = """
            |{
            |  "kind": "subplots",
            |  "layout": { "ncol": 3.0, "nrow": 1.0, "name": "grid" },
            |  "ggsize": { "width": 700.0, "height": 300.0 },
            |  "figures": [
            |    {
            |      "kind": "plot",
            |      "data": { "x": [ 0.0 ], "y": [ 0.0 ], "s": [ 2.0 ] },
            |      "mapping": { "x": "x", "y": "y", "size": "s" },
            |      "layers": [ { "geom": "point" } ],
            |      "scales": [
            |        { "aesthetic": "size", "guide": "none", "scale_mapper_kind": "identity" },
            |        { "aesthetic": "x", "limits": [ -1.5, 1.5 ] },
            |        { "aesthetic": "y", "limits": [ -3.0, 3.0 ] }
            |      ]
            |    },
            |    {
            |      "kind": "plot",
            |      "data": { "x": [ 0.0 ], "y": [ 0.0 ], "s": [ 2.0 ] },
            |      "mapping": { "x": "x", "y": "y", "size": "s" },
            |      "layers": [ { "geom": "point", "size_unit": "x" } ],
            |      "scales": [
            |        { "aesthetic": "size", "guide": "none", "scale_mapper_kind": "identity" },
            |        { "aesthetic": "x", "limits": [ -1.5, 1.5 ] },
            |        { "aesthetic": "y", "limits": [ -3.0, 3.0 ] }
            |      ]
            |    },
            |    {
            |      "kind": "plot",
            |      "data": { "x": [0.0], "y": [0.0], "s": [2.0] },
            |      "mapping": { "x": "x", "y": "y", "size": "s" },
            |      "layers": [ { "geom": "point", "size_unit": "y" } ],
            |      "scales": [
            |        { "aesthetic": "size", "guide": "none", "scale_mapper_kind": "identity" },
            |        { "aesthetic": "x", "limits": [ -1.5, 1.5 ] },
            |        { "aesthetic": "y", "limits": [-3.0, 3.0] }
            |      ]
            |    }
            |  ]
            |}            
        """.trimMargin()

        val plotCanvasDrawable = createPlot(parseJson(spec))

        assertBitmap(plotCanvasDrawable)
    }

    @Test
    fun plot_geomPoint_defaultScaleSize() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "ggsize": { "width": 360.0, "height": 280.0 },
            |  "data": {
            |    "x_val": [ 1.0, 2.0, 3.0, 4.0, 5.0, 1.0, 2.0, 3.0, 4.0, 5.0 ],
            |    "y_val": [ 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0 ],
            |    "magnitude": [ 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0 ]
            |  },
            |  "mapping": { "x": "x_val", "y": "y_val" },
            |  "layers": [ { "geom": "point", "mapping": { "size": "magnitude"} } ],
            |  "scales": [
            |    { "aesthetic": "size", "breaks": [ 5.0, 10.0, 20.0, 30.0, 40.0, 50.0, 50.0, 70.0, 80.0, 90.0, 100.0 ] },
            |    { "aesthetic": "y", "limits": [ -1.0, 2.0 ] }
            |  ],
            |  "theme": { "text": { "blank": true } }
            |}            
        """.trimMargin()

        val plotCanvasDrawable = createPlot(parseJson(spec))

        assertBitmap(plotCanvasDrawable)
    }

    @Test
    fun plot_geomPoint_stroke() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": {
            |    "x": [ 1.0, 2.0, 3.0, 4.0, 5.0 ],
            |    "y": [ 5.0, 3.0, 4.0, 2.0, 1.0 ]
            |  },
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "mapping": { "x": "x", "y": "y" },
            |      "size": 20.0,
            |      "stroke": 8.0,
            |      "color": "blue",
            |      "fill": "red",
            |      "shape": 21
            |    }
            |  ]
            |}
        """.trimMargin()

        val plotCanvasDrawable = createPlotFromSpec(parsePlotSpec(spec))

        assertBitmap(plotCanvasDrawable)
    }

    @Test
    fun plot_geomPoint_shapeRotation90() {
        val spec = parsePlotSpec(
            """
            |{
            |  "kind": "plot",
            |  "data": {
            |    "x": [ 1.0 ],
            |    "y": [ 1.0 ],
            |    "angle": [ -30.0 ]
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "int", "column": "x" },
            |      { "type": "int", "column": "y" },
            |      { "type": "int", "column": "angle" }
            |    ]
            |  },
            |  "layers": [
            |    { "geom": "point", "mapping": { "x": "x", "y": "y", "angle": "angle" }, "size": 20.0, "shape": 2.0 },
            |    { "geom": "point", "x": 5.0, "y": 1.0, "angle": 90.0, "size": 20.0, "shape": 2.0, "color": "red" },
            |    { "geom": "blank", "mapping": { "x": [0.0, 6.0], "y": [null, null] }, "inherit_aes": false, "tooltips": "none" }
            |  ],
            |  "theme": { "name": "classic", "line": "blank", "axis": "blank" },
            |  "ggsize": { "width": 200.0, "height": 200.0 }
            |}
        """.trimMargin()
        )

        val plotCanvasDrawable = createPlotFromSpec(spec)

        assertBitmap(plotCanvasDrawable)
    }
}
