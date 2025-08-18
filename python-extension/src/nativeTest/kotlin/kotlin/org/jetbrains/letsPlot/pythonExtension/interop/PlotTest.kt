/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.pythonExtension.interop

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.fail


/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@Ignore
class PlotTest {
    companion object {
        private val embeddedFontsManager by lazy { newEmbeddedFontsManager() }
        private val imageComparer by lazy { createImageComparer(embeddedFontsManager) }
    }

    @Test
    fun barPlotShouldNotLeakMemory() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "time": ["Lunch", "Lunch", "Dinner", "Dinner", "Dinner"] },
            |  "theme": { "text": {"blank": false } },
            |  "mapping": { "x": "time", "color": "time", "fill": "time" },
            |  "layers": [ { "geom": "bar", "alpha": "0.5" } ]
            |}""".trimMargin()

        val plotSpec = parsePlotSpec(spec)

        assertMemoryLeakFree(plotSpec)
    }

    @Test
    fun rasterPlotShouldNotLeakMemory() {
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
            |  "layers": [ { "geom": "raster", "mapping": { "x": "x", "y": "y", "fill": "z" } } ]
            |}            
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)

        assertMemoryLeakFree(plotSpec)
    }

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
        assertPlot("plot_bar_test.png", plotSpec)
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
        assertPlot("plot_polar_test.png", plotSpec)
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
        assertPlot("plot_raster_test.png", plotSpec)
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
        assertPlot("plot_implicit_size_test.png", plotSpec)
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
        assertPlot("plot_explicit_size_test.png", plotSpec, DoubleVector(300, 300))
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
        assertPlot("plot_implicit_size_scaled_test.png", plotSpec, scale = 2.0)
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
        assertPlot("plot_explicit_size_scaled_test.png", plotSpec, DoubleVector(300, 300), scale = 2.0)
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

        assertPlot("plot_markdown_font_style_test.png", plotSpec)
    }

    @Test
    fun plotMarkdownMonospaceFontStyle() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "layers": [ { "geom": "blank" } ],
            |  "ggtitle": { "text": "Foo *Bar* **Baz** ***FooBarBaz***" },
            |  "ggsize": { "width": 220.0, "height": 30.0 },
            |  "theme": {
            |    "name": "classic",
            |    "line": "blank",
            |    "axis": "blank",
            |    "plot_title": { "markdown": true, "blank": false, "family": "mono" }
            |  }
            |}            
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)

        assertPlot("plot_markdown_monospace_font_style_test.png", plotSpec)
    }

    @Test
    fun plotMarkdownRegularMonospaceFontStyle() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "layers": [ { "geom": "blank" } ],
            |  "ggtitle": { "text": "Foo *Bar* **Baz** ***FooBarBaz***" },
            |  "ggsize": { "width": 220.0, "height": 30.0 },
            |  "theme": {
            |    "name": "classic",
            |    "line": "blank",
            |    "axis": "blank",
            |    "plot_title": { "markdown": true, "blank": false, "family": "regular_mono" }
            |  }
            |}            
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)

        assertPlot("plot_markdown_regular_monospace_font_style_test.png", plotSpec)
    }

    @Test
    fun plotMarkdownObliqueFontStyle() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "layers": [ { "geom": "blank" } ],
            |  "ggtitle": { "text": "Foo *Bar* **Baz** ***FooBarBaz***" },
            |  "ggsize": { "width": 220.0, "height": 30.0 },
            |  "theme": {
            |    "name": "classic",
            |    "line": "blank",
            |    "axis": "blank",
            |    "plot_title": { "markdown": true, "blank": false, "family": "oblique" }
            |  }
            |}            
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)

        assertPlot("plot_markdown_oblique_font_style_test.png", plotSpec)
    }

    @Test
    fun plotMarkdownObliqueBoldFontStyle() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "layers": [ { "geom": "blank" } ],
            |  "ggtitle": { "text": "Foo *Bar* **Baz** ***FooBarBaz***" },
            |  "ggsize": { "width": 220.0, "height": 30.0 },
            |  "theme": {
            |    "name": "classic",
            |    "line": "blank",
            |    "axis": "blank",
            |    "plot_title": { "markdown": true, "blank": false, "family": "oblique_bold" }
            |  }
            |}            
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)

        assertPlot("plot_markdown_oblique_bold_font_style_test.png", plotSpec)
    }

    @Test
    fun plotMarkdownObliqueBoldFontStyleScale2() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "layers": [ { "geom": "blank" } ],
            |  "ggtitle": { "text": "Foo *Bar* **Baz** ***FooBarBaz***" },
            |  "ggsize": { "width": 220.0, "height": 30.0 },
            |  "theme": {
            |    "name": "classic",
            |    "line": "blank",
            |    "axis": "blank",
            |    "plot_title": { "markdown": true, "blank": false, "family": "oblique_bold" }
            |  }
            |}            
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)

        assertPlot("plot_markdown_oblique_bold_font_style_2Xscale_test.png", plotSpec, scale = 2)
    }

    @Test
    fun plot5x2cm96dpi() {
        val (w, h, dpi) = Triple(5, 2, 96)
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ]
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("plot_${w}x${h}cm${dpi}dpi_test.png", plotSpec, DoubleVector(w, h), unit = SizeUnit.CM, dpi = dpi)
    }

    @Test
    fun plot5x2cm300dpi() {
        val (w, h, dpi) = Triple(5, 2, 300)
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ]
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("plot_${w}x${h}cm${dpi}dpi_test.png", plotSpec, DoubleVector(w, h), unit = SizeUnit.CM, dpi = dpi)
    }

    @Test
    fun plot5x2cm300dpi2Xscale() {
        val (w, h, dpi) = Triple(5, 2, 300)
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ]
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot(
            "plot_${w}x${h}cm${dpi}dpi2Xscale_test.png",
            plotSpec,
            DoubleVector(w, h),
            unit = SizeUnit.CM,
            dpi = dpi,
            scale = 2
        )
    }

    @Test
    fun plot12x4cm96dpi() {
        val (w, h, dpi) = Triple(12, 4, 96)
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ]
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("plot_${w}x${h}cm${dpi}dpi_test.png", plotSpec, DoubleVector(w, h), unit = SizeUnit.CM, dpi = dpi)
    }

    @Test
    fun plot12x4cm300dpi() {
        val (w, h, dpi) = Triple(12, 4, 300)
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ]
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("plot_${w}x${h}cm${dpi}dpi_test.png", plotSpec, DoubleVector(w, h), unit = SizeUnit.CM, dpi = dpi)
    }

    @Test
    fun plot400pxx200Dpx() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 400, "height": 200 }
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)

        assertPlot("plot_400pxx200px_test.png", plotSpec)
    }

    @Test
    fun plot400pxx200Dpx150dpi() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 400, "height": 200 }
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)

        // In this case 400x200 is the size in pixels with 96 DPI.
        // Passing only DPI is useful for scaling the plot for printing but keeping plot size and layout intact.
        // For 150 DPI, the bitmap will be scaled to 625x313 pixels (400 * 150 / 96 = 625, 200 * 150 / 96 = 313).
        assertPlot("plot_400pxx200px150dpi_test.png", plotSpec, dpi = 150)
    }

    @Test
    fun plot400pxx200Dpx2Xscale() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3, 4] },
            |  "mapping": { "x": "x" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 400, "height": 200 }
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)

        assertPlot("plot_400pxx200px2Xscale_test.png", plotSpec, scale = 2)
    }

    private fun assertMemoryLeakFree(plotSpec: MutableMap<String, Any>) {
        MagickUtil.startCountAllocations()

        PlotReprGenerator.exportBitmap(plotSpec, embeddedFontsManager)

        val (refCounter, log) = MagickUtil.stopCountAllocations()

        if (refCounter.isNotEmpty()) {
            println("refCounter:")
            println(refCounter.entries.joinToString(separator = "\n"))
            println("\nLog:")
            println(log.joinToString(separator = "\n"))
            fail("Memory leak detected: $refCounter")
        }
    }

    private fun assertPlot(
        expectedFileName: String,
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector? = null,
        unit: SizeUnit? = SizeUnit.PX,
        dpi: Number? = null,
        scale: Number? = 1
    ) {
        val bitmap = PlotReprGenerator.exportBitmap(
            plotSpec = plotSpec,
            plotSize = plotSize,
            sizeUnit = unit,
            dpi = dpi,
            scale = scale,
            fontManager = embeddedFontsManager
            //fontManager = MagickFontManager.default() // For manual testing
        ) ?: error("Failed to export bitmap from plot spec")

        imageComparer.assertBitmapEquals(expectedFileName, bitmap)
    }
}