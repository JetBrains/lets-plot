/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.pythonExtension.interop

import demoAndTestShared.ImageComparer
import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasProvider
import kotlin.test.Test


/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class PlotTest {
    private val imageComparer = ImageComparer(
        suffix = getOSName(),
        expectedDir = getCurrentDir() + "/src/nativeImagickTest/resources/expected/",
        outDir = getCurrentDir() + "/build/reports/",
        canvasProvider = MagickCanvasProvider,
        bitmapIO = MagickBitmapIO,
        tol = 1
    )

    @Test
    fun barPlot() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "time": ["Lunch", "Lunch", "Dinner", "Dinner", "Dinner"] },
            |  "theme": { "text": {"blank": false } },
            |  "mapping": {
            |    "x": "time",
            |    "color": "time",
            |    "fill": "time"
            |  },
            |  "layers": [
            |    {
            |      "geom": "bar",
            |      "alpha": "0.5"
            |    }
            |  ]
            |}""".trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("plot_bar_test.bmp", plotSpec)
    }

    @Test
    fun polarPlot() {
            val spec = """
                |{
                |  "data": { "foo": [ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0 ] },
                |  "coord": { "name": "polar", "theta": "x" },
                |  "ggtitle": { "text": "position=dodge, coord_polar(theta=x)" },
                |  "theme": { "text": { "blank": false } },
                |  "kind": "plot",
                |  "scales": [
                |    { "aesthetic": "x", "expand": [ 0.0, 0.0 ] },
                |    { "aesthetic": "y", "expand": [ 0.0, 0.0 ] }
                |  ],
                |  "layers": [
                |    {
                |      "geom": "bar",
                |      "size": 0.0,
                |      "mapping": { "fill": "foo" },
                |      "position": "dodge",
                |      "data_meta": {
                |        "mapping_annotations": [
                |          {
                |            "aes": "fill",
                |            "annotation": "as_discrete",
                |            "parameters": { "label": "foo", "order": 1.0 }
                |          }
                |        ]
                |      }
                |    }
                |  ]
                |}               
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("plot_polar_test.bmp", plotSpec)
    }

    @Test
    fun rasterPlot() {
        val spec = """
            |{
            |  "data": {
            |    "x": [ -1.0, -0.5, 0.0, 0.5, 1.0, -1.0, -0.5, 0.0, 0.5, 1.0, -1.0, -0.5, 0.0, 0.5, 1.0, -1.0, -0.5, 0.0, 0.5, 1.0, -1.0, -0.5, 0.0, 0.5, 1.0 ],
            |    "y": [ -1.0, -1.0, -1.0, -1.0, -1.0, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5, 0.5, 0.5, 0.5, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0 ],
            |    "z": [ 0.024871417406145683, 0.057228531823873385, 0.09435389770895924, 0.11146595955293902, 0.09435389770895924, 0.057228531823873385, 0.11146595955293902, 0.15556327812622517, 0.15556327812622517, 0.11146595955293902, 0.09435389770895924, 0.15556327812622517, 0.1837762984739307, 0.15556327812622517, 0.09435389770895924, 0.11146595955293902, 0.15556327812622517, 0.15556327812622517, 0.11146595955293902, 0.057228531823873385, 0.09435389770895924, 0.11146595955293902, 0.09435389770895924, 0.057228531823873385, 0.024871417406145683 ]
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "float", "column": "x" },
            |      { "type": "float", "column": "y" },
            |      { "type": "float", "column": "z" }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [
            |    {
            |      "aesthetic": "fill",
            |      "low": "#54278f",
            |      "high": "#f2f0f7",
            |      "scale_mapper_kind": "color_gradient"
            |    }
            |  ],
            |  "layers": [
            |    {
            |      "geom": "raster",
            |      "mapping": {
            |        "x": "x",
            |        "y": "y",
            |        "fill": "z"
            |      }
            |    }
            |  ]
            |}            
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("plot_raster_test.bmp", plotSpec)
    }

    @Test
    fun plotExportImplicitSize() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3], "y": [4, 5, 6] },
            |  "mapping": { "x": "x", "y": "y" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 200, "height": 200 }
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("plot_implicit_size_test.bmp", plotSpec)
    }


    @Test
    fun plotExportExplicitSize() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3], "y": [4, 5, 6] },
            |  "mapping": { "x": "x", "y": "y" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 200, "height": 200 }
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("plot_explicit_size_test.bmp", plotSpec, width = 300, height = 300)
    }

    @Test
    fun plotExportImplicitSizeScaled() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3], "y": [4, 5, 6] },
            |  "mapping": { "x": "x", "y": "y" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 200, "height": 200 }
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)

        // 200x200 is the size in pixels, scale = 2.0 means the bitmap will be 400x400 pixels
        assertPlot("plot_implicit_size_scaled_test.bmp", plotSpec, scale = 2.0)
    }

    @Test
    fun plotExportExplicitSizeScaled() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3], "y": [4, 5, 6] },
            |  "mapping": { "x": "x", "y": "y" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 200, "height": 200 }
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)

        // 300x300 is the size in pixels, scale = 2.0 means the bitmap will be 600x600 pixels
        assertPlot("plot_explicit_size_scaled_test.bmp", plotSpec, width = 300, height = 300, scale = 2.0)
    }

    @Test
    fun plotMarkdownFontStyle() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "layers": [ { "geom": "blank" } ],
            |  "ggtitle": { "text": "Foo *Bar* **Baz** ***FooBarBaz***" },
            |  "ggsize": { "width": 190.0, "height": 30.0 },
            |  "theme": {
            |    "name": "classic",
            |    "line": "blank",
            |    "axis": "blank",
            |    "plot_title": { "markdown": true, "blank": false }
            |  }
            |}            
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)

        assertPlot("plot_markdown_font_style_test.bmp", plotSpec)
    }

    private fun assertPlot(
        expectedFileName: String,
        plotSpec: MutableMap<String, Any>,
        width: Int = -1,
        height: Int = -1,
        scale: Double = 1.0
    ) {
        val bitmap = PlotReprGenerator.exportBitmap(plotSpec, width, height, scale)
            ?: error("Failed to export bitmap from plot spec")
        imageComparer.assertBitmapEquals(expectedFileName, bitmap)
    }
}