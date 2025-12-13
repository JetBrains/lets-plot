package org.jetbrains.letsPlot.awt.plot

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit.CM
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.measureTime


class PlotImageExportVisualTest: VisualPlotTestBase() {

    @Test
    fun `with a long rendering time the race condition should not occur`() {
        // Test potential race condition in image export
        // Could be caused by unexpected use of EDT in the render process.
        val dim = sqrt(40_000.0).roundToInt()
        val rand = Random(12)
        val xs = mutableListOf<String>()
        val ys = mutableListOf<String>()
        val cs = mutableListOf<String>()

        (0..dim).map {
            (0..dim).map {
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

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        assertPlot("plot_race_condition_test.png", plotSpec)
    }

    @Test
    fun `geom_point with stroke`() {
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

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()
        assertPlot("plot_geom_point_with_stroke_test.png", plotSpec)
    }

    @Test
    fun `italic from theme`() {
        // See the issue https://github.com/JetBrains/lets-plot/issues/1391
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": {
            |    "x": [ "foo", "bar", "baz" ],
            |    "y": [ 1.0, 2.0, 3.0 ]
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "str", "column": "x" },
            |      { "type": "int", "column": "y" }
            |    ]
            |  },
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "mapping": { "x": "x", "y": "y" }
            |    }
            |  ],
            |  "theme": {
            |    "axis_title_x": { "face": "italic", "blank": false },
            |    "axis_title_y": { "face": "italic", "blank": false },
            |    "axis_text_x": { "face": "bold_italic", "blank": false },
            |    "axis_text_y": { "face": "bold_italic", "blank": false }
            |  }
            |}            
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()
        assertPlot("plot_italic_from_theme_test.png", plotSpec)
    }

    @Test
    fun `latex formula`() {
        val spec = """
            |{
            |  "theme": { "name": "classic", "line": "blank", "axis": "blank" },
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "text",
            |      "x": 0.0,
            |      "label": "\\( e^{i \\cdot \\pi} = -1 \\)",
            |      "size": 70.0,
            |      "family": "Noto Sans",
            |      "fontface": "italic"
            |    }
            |  ]
            |}            
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)

        assertPlot("plot_latex_formula_test.png", plotSpec)
    }

    @Test
    fun superscript() {
        val spec = """
            |{
            |  "kind": "subplots",
            |  "layout": { "ncol": 2.0, "nrow": 1.0, "name": "grid" },
            |  "figures": [
            |    {
            |      "kind": "plot",
            |      "ggtitle": { "text": "Default limits" },
            |      "theme": { "name": "classic", "exponent_format": "pow", "text": { "family": "Noto Sans" }, "axis_title_y": { "blank": true } },
            |      "scales": [ { "aesthetic": "y", "limits": [ 1e-08, 10000000.0 ], "trans": "log10" } ],
            |      "layers": [
            |        {
            |          "geom": "text",
            |          "mapping": {
            |            "y": [ 1e-07, 1e-06, 1e-05, 0.0001, 0.001, 0.01, 0.1, 1.0, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0 ],
            |            "label": [ 1e-07, 1e-06, 1e-05, 0.0001, 0.001, 0.01, 0.1, 1.0, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0 ]
            |          },
            |          "family": "Noto Sans",
            |          "size": 10.0
            |        }
            |      ]
            |    },
            |    {
            |      "kind": "plot",
            |      "ggtitle": { "text": "Scientific notation for \\( x \\leq 10^{-3} \\) and \\( x \\geq 10^3 \\)" },
            |      "theme": { "name": "classic", "exponent_format": [ "pow", -3.0, 3.0 ], "text": { "family": "Noto Sans" }, "axis_title_y": { "blank": true } },
            |      "scales": [ { "aesthetic": "y", "limits": [ 1e-08, 10000000.0 ], "trans": "log10" } ],
            |      "layers": [
            |        {
            |          "geom": "text",
            |          "mapping": {
            |            "y": [ 1e-07, 1e-06, 1e-05, 0.0001, 0.001, 0.01, 0.1, 1.0, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0 ],
            |            "label": [ 1e-07, 1e-06, 1e-05, 0.0001, 0.001, 0.01, 0.1, 1.0, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0 ]
            |          },
            |          "family": "Noto Sans",
            |          "size": 10.0
            |        }
            |      ]
            |    }
            |  ]
            |}
            |""".trimMargin()

        val plotSpec = parsePlotSpec(spec)

        assertPlot("plot_superscript_test.png", plotSpec)
    }

    @Test
    fun `multi-level latex formula`() {
        val spec = """
            |{
            |  "data": {
            |    "x": [
            |      "\\( x^a+1 \\)",
            |      "\\( x^{a \\cdot b}+1 \\)",
            |      "\\( x^{a \\cdot b^c}+1 \\)",
            |      "\\( x^{a \\cdot b^{c - d}}+1 \\)"
            |    ],
            |    "y": [ 28, 13, 6, 3]
            |  },
            |  "mapping": { "x": "x", "y": "y" },
            |  "kind": "plot",
            |  "layers": [ { "geom": "bar", "stat": "identity" } ]
            |}            
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        assertPlot("plot_multi_level_latex_formula_test.png", plotSpec)
    }

    @Test
    fun `bold_italic geom_bar label`() {
        val spec = """
            |{
            |  "theme": {
            |    "label_text": {
            |      "face": "bold_italic",
            |      "size": 16.0,
            |      "blank": false
            |    }
            |  },
            |  "kind": "plot",
            |  "data": {
            |    "x": [ 0.0, 1.0, 2.0 ],
            |    "y": [ 4.0, 5.0, 3.0 ]
            |  },
            |  "data_meta": {
            |    "series_annotations": [ 
            |      { "type": "int", "column": "x" },
            |      { "type": "int", "column": "y" } 
            |    ]
            |  },
            |  "scales": [ { "aesthetic": "fill", "discrete": true } ],
            |  "layers": [
            |    {
            |      "geom": "bar",
            |      "stat": "identity",
            |      "mapping": { "x": "x", "y": "y", "fill": "x" },
            |      "show_legend": false,
            |      "labels": { "formats": [], "lines": [ "Value: @y" ] }
            |    }
            |  ]
            |}""".trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        assertPlot("plot_bold_italic_geom_bar_label_test.png", plotSpec)
    }

    @Test
    fun labels() {
        val spec = """
            {
              "kind": "plot",
              "theme": {
                "axis_title_y": { "blank": true }
              },
              "layers": [
                { "geom": "text", "x": 0.0, "y": 0.0, "label": "QWE", "family": "Noto Sans" },
                { "geom": "text", "x": 0.0, "y": 0.0, "label": "___", "family": "Noto Sans", "color": "red" }
              ],
              "ggsize": { "width": 200.0, "height": 200.0 }
            }
        """.trimIndent()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        assertPlot("plot_labels_test.png", plotSpec)
    }

    @Test
    fun markdown2Xscale() {
        val spec = """
            |{
            |  "theme": {
            |    "title": { "markdown": true, "blank": false },
            |    "plot_title": { "family": "Noto Sans Regular", "size": 30.0, "hjust": 0.5, "blank": false },
            |    "plot_subtitle": { "family": "Noto Sans Regular", "hjust": 0.5, "blank": false }
            |  },
            |  "ggtitle": {
            |    "text": "<span style=\"color:#66c2a5\">**Forward**</span>, <span style=\"color:#8da0cb\">**Rear**</span> and <span style=\"color:#fc8d62\">**4WD**</span> Drivetrain",
            |    "subtitle": "**City milage** *vs* **displacement**"
            |  },
            |  "caption": {
            |    "text": "<span style='color:grey'>Powered by <a href='https://lets-plot.org'>Lets-Plot</a>.  \nVisit the <a href='https://github.com/jetbrains/lets-plot/issues'>issue tracker</a> for feedback.</span>"
            |  },
            |  "guides": {
            |    "x": { "title": "Displacement (***inches***)" },
            |    "y": { "title": "Miles per gallon (***cty***)" }
            |  },
            |  "kind": "plot",
            |  "scales": [
            |    {
            |      "aesthetic": "color",
            |      "guide": "none",
            |      "values": [ "#66c2a5", "#fc8d62", "#8da0cb" ]
            |    }
            |  ],
            |  "layers": [
            |    {
            |      "geom": "blank",
            |      "inherit_aes": false,
            |      "tooltips": "none"
            |    }
            |  ]
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        assertPlot("plot_markdown2Xscale_test.png", plotSpec, scale = 2)
    }

    @Test
    fun markdown() {
        val spec = """
            |{
            |  "theme": {
            |    "title": { "markdown": true, "blank": false },
            |    "plot_title": { "family": "Noto Sans Regular", "size": 30.0, "hjust": 0.5, "blank": false },
            |    "plot_subtitle": { "family": "Noto Sans Regular", "hjust": 0.5, "blank": false }
            |  },
            |  "ggtitle": {
            |    "text": "<span style=\"color:#66c2a5\">**Forward**</span>, <span style=\"color:#8da0cb\">**Rear**</span> and <span style=\"color:#fc8d62\">**4WD**</span> Drivetrain",
            |    "subtitle": "**City milage** *vs* **displacement**"
            |  },
            |  "caption": {
            |    "text": "<span style='color:grey'>Powered by <a href='https://lets-plot.org'>Lets-Plot</a>.  \nVisit the <a href='https://github.com/jetbrains/lets-plot/issues'>issue tracker</a> for feedback.</span>"
            |  },
            |  "guides": {
            |    "x": { "title": "Displacement (***inches***)" },
            |    "y": { "title": "Miles per gallon (***cty***)" }
            |  },
            |  "kind": "plot",
            |  "scales": [
            |    {
            |      "aesthetic": "color",
            |      "guide": "none",
            |      "values": [ "#66c2a5", "#fc8d62", "#8da0cb" ]
            |    }
            |  ],
            |  "layers": [
            |    {
            |      "geom": "blank",
            |      "inherit_aes": false,
            |      "tooltips": "none"
            |    }
            |  ]
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        assertPlot("plot_markdown_test.png", plotSpec)
    }

    @Test
    fun plotFauxObliqueBoldFontStyle2Xscale() {
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
            |    "plot_title": { "markdown": true },
            |    "text": { "family": "Noto Serif Regular" }
            |  }
            |}            
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)

        assertPlot("plot_markdown_faux_oblique_bold_font_style2Xscale_test.png", plotSpec, scale = 2)
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

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 200x200 from ggsize is the size in pixels, scale = 1.0 means the bitmap will be 200x200 pixels
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

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 3x3 inches with 300 DPI means the bitmap will be 900x900 pixels (3 * 300 = 900).
        assertPlot("plot_explicit_size_test.png", plotSpec, width = 3, height = 3)
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

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

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

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 3x3 inches with 300 DPI and scale = 2.0 means the bitmap will be 1800x1800 pixels (3 * 300 * 2 = 1800).
        assertPlot("plot_explicit_size_scaled_test.png", plotSpec, width = 3, height = 3, scale = 2.0)
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

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 5x2cm is the size in centimeters, dpi = 96 means the bitmap will be 189x76 pixels (5 * 96 / 2.54 = 189, 2 * 96 / 2.54 = 76).
        assertPlot("plot_${w}x${h}cm${dpi}dpi_test.png", plotSpec, width = w, height = h, unit = CM, dpi = dpi)
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

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 5x2cm is the size in centimeters, dpi = 300 means the bitmap will be 591x238 pixels (5 * 300 / 2.54 = 591, 2 * 300 / 2.54 = 236).
        assertPlot("plot_${w}x${h}cm${dpi}dpi_test.png", plotSpec, width = w, height = h, unit = CM, dpi = dpi)
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

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 5x2cm is the size in centimeters, dpi = 300 and scale = 2 means the bitmap will be 1181x475 pixels (5 * 300 / 2.54 * 2 = 1182, 2 * 300 / 2.54 * 2 = 472).
        assertPlot("plot_${w}x${h}cm${dpi}dpi2Xscale_test.png", plotSpec, width = w, height = h, unit = CM, dpi = dpi, scale=2)
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

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 12x4cm is the size in centimeters, dpi = 96 means the bitmap will be 452x152 pixels (12 * 96 / 2.54 = 454, 4 * 96 / 2.54 = 152).
        assertPlot("plot_${w}x${h}cm${dpi}dpi_test.png", plotSpec, width = w, height = h, unit = CM, dpi = dpi)
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

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 12x4cm is the size in centimeters, dpi = 300.
        // Taking into account rounding errors while transforming cm -> logical size -> pixels,
        // the bitmap will be 1419x475 pixels (the exact size is 12 * 300 / 2.54 = 1417, 4 * 300 / 2.54 = 472).
        assertPlot("plot_${w}x${h}cm${dpi}dpi_test.png", plotSpec, width = w, height = h, unit = CM, dpi = dpi)
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

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        // 400x200 is the size in pixels, scale = 1.0 means the bitmap will be 400x200 pixels
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

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

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

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()

        assertPlot("plot_400pxx200px2Xscale_test.png", plotSpec, scale=2)
    }



    @Test
    fun `geom_raster() should not fail on image export`() {
        val spec = parsePlotSpec("""
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
        """.trimIndent())
            .themeTextNotoSans()

        assertPlot("geom_raster_export_test.png", spec)
    }

    @Test
    fun `geom_imshow() should not fail on image export`() {
        val spec = parsePlotSpec("""
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
        """.trimMargin())
            .themeTextNotoSans()

        assertPlot("geom_imshow_export_test.png", spec)
    }

    @Test
    fun `with dpi=NaN`() {
        val spec = parsePlotSpec("""
            |{
            |  "kind": "plot",
            |  "data": { "x": [1, 2, 3], "y": [4, 5, 6] },
            |  "mapping": { "x": "x", "y": "y" },
            |  "layers": [ { "geom": "point" } ],
            |  "ggsize": { "width": 200, "height": 200 }
            |}
        """.trimMargin())

        val plotSpec = spec.themeTextNotoSans()

        // dpi is NaN, so the bitmap will be exported with the default scaling factor of 1.0
        assertPlot("plot_dpi_nan_test.png", plotSpec, dpi = Double.NaN)
    }

    @Test
    fun `shape with 90 degree rotation`() {
        // Was a bug caused by multiplying stroke by the transform.sx (which is 0.0 for 90-degree rotation)
        val spec = parsePlotSpec("""
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
        """.trimMargin())

        // stroke size should remain the same (3 pixels) at any scaling factor
        assertPlot("plot_constant_stroke_size_test.png", spec, scale = 1.0)
    }

    @Test
    fun `path with none`() {
        val spec = parsePlotSpec("""
            |{
            |  "kind": "subplots",
            |  "layout": {
            |    "ncol": 3.0,
            |    "nrow": 1.0,
            |    "name": "grid"
            |  },
            |  "figures": [
            |    {
            |      "data": {
            |        "x": [ 0.0, 4.0, 1.0, 5.0, 2.0, 6.0, 3.0, 7.0 ],
            |        "y": [ 4.0, 4.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0 ],                    
            |        "c": [ "a", "a", "a", "a", "b", "b", "b", "b" ]
            |      },
            |      "mapping": {
            |        "x": "x",
            |        "y": "y",
            |        "color": "c"
            |      },
            |      "data_meta": {
            |        "series_annotations": [
            |          {
            |            "type": "float",
            |            "column": "x"
            |          },
            |          {
            |            "type": "float",
            |            "column": "y"
            |          },
            |          {
            |            "type": "float",
            |            "column": "x_na"
            |          },
            |          {
            |            "type": "float",
            |            "column": "y_na"
            |          },
            |          {
            |            "type": "str",
            |            "column": "c"
            |          }
            |        ]
            |      },
            |      "ggtitle": {
            |        "text": "geom_path"
            |      },
            |      "kind": "plot",
            |      "scales": [],
            |      "layers": [
            |        {
            |          "geom": "path",
            |          "mapping": {},
            |          "data_meta": {},
            |          "linewidth": 1.0
            |        }
            |      ],
            |      "metainfo_list": []
            |    },
            |    {
            |      "data": {                    
            |        "y": [ 4.0, 4.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0 ],
            |        "x_na": [ null, 4.0, 1.0, 5.0, 2.0, null, 3.0, 7.0 ],
            |        "c": [ "a", "a", "a", "a", "b", "b", "b", "b" ]
            |      },
            |      "mapping": {
            |        "x": "x_na",
            |        "y": "y",
            |        "color": "c"
            |      },
            |      "data_meta": {
            |        "series_annotations": [
            |          {
            |            "type": "float",
            |            "column": "x"
            |          },
            |          {
            |            "type": "float",
            |            "column": "y"
            |          },
            |          {
            |            "type": "float",
            |            "column": "x_na"
            |          },
            |          {
            |            "type": "float",
            |            "column": "y_na"
            |          },
            |          {
            |            "type": "str",
            |            "column": "c"
            |          }
            |        ]
            |      },
            |      "ggtitle": {
            |        "text": "geom_path NA in x"
            |      },
            |      "kind": "plot",
            |      "scales": [],
            |      "layers": [
            |        {
            |          "geom": "path",
            |          "mapping": {},
            |          "data_meta": {},
            |          "linewidth": 1.0
            |        }
            |      ],
            |      "metainfo_list": []
            |    },
            |    {
            |      "data": {
            |        "x": [ 0.0, 4.0, 1.0, 5.0, 2.0, 6.0, 3.0, 7.0 ],
            |        "y_na": [ null, 4.0, 3.0, 3.0, 2.0, null, 1.0, 1.0 ],
            |        "c": [ "a", "a", "a", "a", "b", "b", "b", "b" ]
            |      },
            |      "mapping": {
            |        "x": "x",
            |        "y": "y_na",
            |        "color": "c"
            |      },
            |      "data_meta": {
            |        "series_annotations": [
            |          {
            |            "type": "float",
            |            "column": "x"
            |          },
            |          {
            |            "type": "float",
            |            "column": "y"
            |          },
            |          {
            |            "type": "float",
            |            "column": "x_na"
            |          },
            |          {
            |            "type": "float",
            |            "column": "y_na"
            |          },
            |          {
            |            "type": "str",
            |            "column": "c"
            |          }
            |        ]
            |      },
            |      "ggtitle": {
            |        "text": "geom_path NA in y"
            |      },
            |      "kind": "plot",
            |      "scales": [],
            |      "layers": [
            |        {
            |          "geom": "path",
            |          "mapping": {},
            |          "data_meta": {},
            |          "linewidth": 1.0
            |        }
            |      ],
            |      "metainfo_list": []
            |    }
            |  ]
            |}
        """.trimMargin())

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("path_with_none.png", plotSpec)
    }

    @Test
    fun `path with none coord polar`() {
        val spec = parsePlotSpec("""
            |{
            |  "data": {
            |    "x": [ null, null, 0.0, null, 1.0, 2.0, null, 4.0, 5.0, 6.0 ],
            |    "y": [ null, 0.0, 0.5, 0.0, 0.0, 1.0, null, null, 0.5, 1.0 ],
            |    "c": [ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0 ]
            |  },
            |  "mapping": {
            |    "x": "x",
            |    "y": "y"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "float",
            |        "column": "x"
            |      },
            |      {
            |        "type": "float",
            |        "column": "y"
            |      },
            |      {
            |        "type": "int",
            |        "column": "c"
            |      }
            |    ]
            |  },
            |  "coord": {
            |    "name": "polar"
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "path",
            |      "mapping": {},
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin())

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("path_with_none_coord_polar.png", plotSpec)
    }

    @Test
    fun `variadic path with none`() {
        val spec = parsePlotSpec("""
            |{
            | "data": {
            |    "x": [ null, null, 0.0, null, 1.0, 2.0, null, 4.0, 5.0, 6.0 ],
            |    "y": [ null, 0.0, 0.5, 0.0, 0.0, 1.0, null, null, 0.5, 1.0 ],
            |    "c": [ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0 ]
            |  },
            |  "mapping": {
            |    "x": "x",
            |    "y": "y",
            |    "color": "c"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "float",
            |        "column": "x"
            |      },
            |      {
            |        "type": "float",
            |        "column": "y"
            |      },
            |      {
            |        "type": "int",
            |        "column": "c"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "path",
            |      "mapping": {},
            |      "data_meta": {},
            |      "size": 3.0
            |    },
            |    {
            |      "geom": "point",
            |      "mapping": {},
            |      "data_meta": {},
            |      "color": "red"
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin())

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("variadic_path_with_none.png", plotSpec)
    }

    @Test
    fun `line with none`() {
        val spec = parsePlotSpec("""
            |{
            |  "kind": "subplots",
            |  "layout": {
            |    "ncol": 3.0,
            |    "nrow": 1.0,
            |    "name": "grid"
            |  },
            |  "figures": [
            |    {
            |      "data": {
            |        "x": [ 0.0, 4.0, 1.0, 5.0, 2.0, 6.0, 3.0, 7.0 ],
            |        "y": [ 4.0, 4.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0 ],                    
            |        "c": [ "a", "a", "a", "a", "b", "b", "b", "b" ]
            |      },
            |      "mapping": {
            |        "x": "x",
            |        "y": "y",
            |        "color": "c"
            |      },
            |      "data_meta": {
            |        "series_annotations": [
            |          {
            |            "type": "float",
            |            "column": "x"
            |          },
            |          {
            |            "type": "float",
            |            "column": "y"
            |          },
            |          {
            |            "type": "float",
            |            "column": "x_na"
            |          },
            |          {
            |            "type": "float",
            |            "column": "y_na"
            |          },
            |          {
            |            "type": "str",
            |            "column": "c"
            |          }
            |        ]
            |      },
            |      "ggtitle": {
            |        "text": "geom_line"
            |      },
            |      "kind": "plot",
            |      "scales": [],
            |      "layers": [
            |        {
            |          "geom": "line",
            |          "mapping": {},
            |          "data_meta": {},
            |          "linewidth": 1.0
            |        }
            |      ],
            |      "metainfo_list": []
            |    },
            |    {
            |      "data": {                    
            |        "y": [ 4.0, 4.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0 ],
            |        "x_na": [ null, 4.0, 1.0, 5.0, 2.0, null, 3.0, 7.0 ],
            |        "c": [ "a", "a", "a", "a", "b", "b", "b", "b" ]
            |      },
            |      "mapping": {
            |        "x": "x_na",
            |        "y": "y",
            |        "color": "c"
            |      },
            |      "data_meta": {
            |        "series_annotations": [
            |          {
            |            "type": "float",
            |            "column": "x"
            |          },
            |          {
            |            "type": "float",
            |            "column": "y"
            |          },
            |          {
            |            "type": "float",
            |            "column": "x_na"
            |          },
            |          {
            |            "type": "float",
            |            "column": "y_na"
            |          },
            |          {
            |            "type": "str",
            |            "column": "c"
            |          }
            |        ]
            |      },
            |      "ggtitle": {
            |        "text": "geom_line NA in x"
            |      },
            |      "kind": "plot",
            |      "scales": [],
            |      "layers": [
            |        {
            |          "geom": "line",
            |          "mapping": {},
            |          "data_meta": {},
            |          "linewidth": 1.0
            |        }
            |      ],
            |      "metainfo_list": []
            |    },
            |    {
            |      "data": {
            |        "x": [ 0.0, 4.0, 1.0, 5.0, 2.0, 6.0, 3.0, 7.0 ],
            |        "y_na": [ null, 4.0, 3.0, 3.0, 2.0, null, 1.0, 1.0 ],
            |        "c": [ "a", "a", "a", "a", "b", "b", "b", "b" ]
            |      },
            |      "mapping": {
            |        "x": "x",
            |        "y": "y_na",
            |        "color": "c"
            |      },
            |      "data_meta": {
            |        "series_annotations": [
            |          {
            |            "type": "float",
            |            "column": "x"
            |          },
            |          {
            |            "type": "float",
            |            "column": "y"
            |          },
            |          {
            |            "type": "float",
            |            "column": "x_na"
            |          },
            |          {
            |            "type": "float",
            |            "column": "y_na"
            |          },
            |          {
            |            "type": "str",
            |            "column": "c"
            |          }
            |        ]
            |      },
            |      "ggtitle": {
            |        "text": "geom_line NA in y"
            |      },
            |      "kind": "plot",
            |      "scales": [],
            |      "layers": [
            |        {
            |          "geom": "line",
            |          "mapping": {},
            |          "data_meta": {},
            |          "linewidth": 1.0
            |        }
            |      ],
            |      "metainfo_list": []
            |    }
            |  ]
            |}
        """.trimMargin())

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("line_with_none.png", plotSpec)
    }

    @Test
    fun `area ridges with none`() {
        val spec = parsePlotSpec("""
            |{
            |  "data": {
            |    "x": [ null, -1.0, -0.5, 0.0, 0.5, 1.0, null, -1.0, -0.5, 0.0, null, 0.5, 1.0, 2.0 ],
            |    "g": [ "A", "A", "A", "A", "A", "A", "A", "B", "B", "B", "B", "B", "B", "B" ],
            |    "h": [ null, 0.0, 0.6, 1.0, 0.6, 0.0, null, 0.0, 0.5, 0.8, null, 0.4, 0.0, 0.0 ]
            |  },
            |  "mapping": {
            |    "x": "x",
            |    "y": "g",
            |    "height": "h",
            |    "group": "g"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "float",
            |        "column": "x"
            |      },
            |      {
            |        "type": "str",
            |        "column": "g"
            |      },
            |      {
            |        "type": "float",
            |        "column": "h"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "area_ridges",
            |      "stat": "identity",
            |      "mapping": {},
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin())

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("area_ridges_with_none.png", plotSpec)
    }

    @Test
    fun `smooth with none`() {
        val spec = parsePlotSpec("""
            |{
            |  "data": {
            |    "x": [ null, -2.0, -1.0, 0.0, 1.0, 2.0, null, 3.0, 4.0, 5.0, null, 6.0 ],
            |    "y": [ null, 1.5, 0.5, 0.0, 0.6, 1.0, null, 1.2, 1.0, 0.8, null, 0.7 ]
            |  },
            |  "mapping": {
            |    "x": "x",
            |    "y": "y"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "int",
            |        "column": "x"
            |      },
            |      {
            |        "type": "float",
            |        "column": "y"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "smooth",
            |      "mapping": {},
            |      "data_meta": {},
            |      "method": "loess"
            |    },
            |    {
            |      "geom": "point",
            |      "mapping": {},
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin())

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("smooth_with_none.png", plotSpec)
    }

    @Test
    fun `violin with none`() {
        val spec = parsePlotSpec("""
            |{
            |  "data": {
            |    "grp": [ null, "A", "A", "A", "A", "B", "B", "B", "C", "C", "C", "D" ],
            |    "y": [ 0.3, null, 0.2, 0.5, 0.8, null, 0.4, 0.6, 0.1, null, 0.9, 0.7 ]
            |  },
            |  "mapping": {
            |    "y": "y"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "str",
            |        "column": "grp"
            |      },
            |      {
            |        "type": "float",
            |        "column": "y"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "violin",
            |      "mapping": {},
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin())

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("violin_with_none.png", plotSpec)
    }

    @Test
    fun `step with none`() {
        val spec = parsePlotSpec("""
            |{
            |  "kind": "subplots",
            |  "layout": {
            |    "ncol": 3.0,
            |    "nrow": 1.0,
            |    "name": "grid"
            |  },
            |  "figures": [
            |    {
            |      "data": {
            |        "x": [ 0.0, 4.0, 1.0, 5.0, 2.0, 6.0, 3.0, 7.0 ],
            |        "y": [ 4.0, 4.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0 ],                    
            |        "c": [ "a", "a", "a", "a", "b", "b", "b", "b" ]
            |      },
            |      "mapping": {
            |        "x": "x",
            |        "y": "y",
            |        "color": "c"
            |      },
            |      "data_meta": {
            |        "series_annotations": [
            |          {
            |            "type": "float",
            |            "column": "x"
            |          },
            |          {
            |            "type": "float",
            |            "column": "y"
            |          },
            |          {
            |            "type": "float",
            |            "column": "x_na"
            |          },
            |          {
            |            "type": "float",
            |            "column": "y_na"
            |          },
            |          {
            |            "type": "str",
            |            "column": "c"
            |          }
            |        ]
            |      },
            |      "ggtitle": {
            |        "text": "geom_step"
            |      },
            |      "kind": "plot",
            |      "scales": [],
            |      "layers": [
            |        {
            |          "geom": "step",
            |          "mapping": {},
            |          "data_meta": {},
            |          "linewidth": 1.0
            |        }
            |      ],
            |      "metainfo_list": []
            |    },
            |    {
            |      "data": {                    
            |        "y": [ 4.0, 4.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0 ],
            |        "x_na": [ null, 4.0, 1.0, 5.0, 2.0, null, 3.0, 7.0 ],
            |        "c": [ "a", "a", "a", "a", "b", "b", "b", "b" ]
            |      },
            |      "mapping": {
            |        "x": "x_na",
            |        "y": "y",
            |        "color": "c"
            |      },
            |      "data_meta": {
            |        "series_annotations": [
            |          {
            |            "type": "float",
            |            "column": "x"
            |          },
            |          {
            |            "type": "float",
            |            "column": "y"
            |          },
            |          {
            |            "type": "float",
            |            "column": "x_na"
            |          },
            |          {
            |            "type": "float",
            |            "column": "y_na"
            |          },
            |          {
            |            "type": "str",
            |            "column": "c"
            |          }
            |        ]
            |      },
            |      "ggtitle": {
            |        "text": "geom_step NA in x"
            |      },
            |      "kind": "plot",
            |      "scales": [],
            |      "layers": [
            |        {
            |          "geom": "step",
            |          "mapping": {},
            |          "data_meta": {},
            |          "linewidth": 1.0
            |        }
            |      ],
            |      "metainfo_list": []
            |    },
            |    {
            |      "data": {
            |        "x": [ 0.0, 4.0, 1.0, 5.0, 2.0, 6.0, 3.0, 7.0 ],
            |        "y_na": [ null, 4.0, 3.0, 3.0, 2.0, null, 1.0, 1.0 ],
            |        "c": [ "a", "a", "a", "a", "b", "b", "b", "b" ]
            |      },
            |      "mapping": {
            |        "x": "x",
            |        "y": "y_na",
            |        "color": "c"
            |      },
            |      "data_meta": {
            |        "series_annotations": [
            |          {
            |            "type": "float",
            |            "column": "x"
            |          },
            |          {
            |            "type": "float",
            |            "column": "y"
            |          },
            |          {
            |            "type": "float",
            |            "column": "x_na"
            |          },
            |          {
            |            "type": "float",
            |            "column": "y_na"
            |          },
            |          {
            |            "type": "str",
            |            "column": "c"
            |          }
            |        ]
            |      },
            |      "ggtitle": {
            |        "text": "geom_step NA in y"
            |      },
            |      "kind": "plot",
            |      "scales": [],
            |      "layers": [
            |        {
            |          "geom": "step",
            |          "mapping": {},
            |          "data_meta": {},
            |          "linewidth": 1.0
            |        }
            |      ],
            |      "metainfo_list": []
            |    }
            |  ]
            |}
        """.trimMargin())

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("step_with_none.png", plotSpec)
    }

    @Test
    fun `polygon with none`() {
        val spec = parsePlotSpec("""
            |{
            |  "data": {
            |    "x": [ null, 0.0, 1.0, 1.0, 0.0, 2.0, 3.0, null, null, 4.0, 5.0, null, 5.0, 4.0 ],
            |    "y": [ null, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.5, null, 1.0, 1.0 ],
            |    "id": [ "A", "A", "A", "A", "A", "B", "B", "B", "B", "C", "C", "C", "C", "C" ]
            |  },
            |  "mapping": {
            |    "x": "x",
            |    "y": "y",
            |    "group": "id",
            |    "color": "id",
            |    "fill": "id"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "int",
            |        "column": "x"
            |      },
            |      {
            |        "type": "float",
            |        "column": "y"
            |      },
            |      {
            |        "type": "str",
            |        "column": "id"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [
            |    {
            |      "aesthetic": "fill",
            |      "scale_mapper_kind": "color_hue"
            |    }
            |  ],
            |  "layers": [
            |    {
            |      "geom": "polygon",
            |      "mapping": {},
            |      "data_meta": {},
            |      "size": 5.0
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin())

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("polygon_with_none.png", plotSpec)
    }

    @Test
    fun `density with none`() {
        val spec = parsePlotSpec("""
            |{
            |  "data": {
            |    "x": [ null, -3.0, -2.9, -2.8, null, -1.0, -1.0, -0.8, null, 0.5, 0.6, 0.7, null, 3.0, 10.0, null ],
            |    "y": [ null, 2.0, 3.0, 2.0, null, 3.0, null, 4.0, 5.0, 0.0, 1.0, 2.0, 3.0, 4.0, 1.0, 5.0 ]
            |  },
            |  "mapping": {
            |    "x": "x"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "float",
            |        "column": "x"
            |      },
            |      {
            |        "type": "int",
            |        "column": "y"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "density",
            |      "mapping": {},
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin())

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("density_with_none.png", plotSpec)
    }

    @Test
    fun `density identity with none`() {
        val spec = parsePlotSpec("""
            |{
            |  "data": {
            |    "x": [ null, -3.0, -2.9, -2.8, null, -1.0, -1.0, -0.8, null, 0.5, 0.6, 0.7, null, 3.0, 10.0, null ],
            |    "y": [ null, 2.0, 3.0, 2.0, null, 3.0, null, 4.0, 5.0, 0.0, 1.0, 2.0, 3.0, 4.0, 1.0, 5.0 ]
            |  },
            |  "mapping": {
            |    "x": "x",
            |    "y": "y"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "float",
            |        "column": "x"
            |      },
            |      {
            |        "type": "int",
            |        "column": "y"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "density",
            |      "stat": "identity",
            |      "mapping": {},
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin())

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("density_identity_with_none.png", plotSpec)
    }

    @Test
    fun `map with none`() {
        val spec = parsePlotSpec("""
            |{
            |  "data": {
            |    "x": [ null, 0.0, 1.0, 1.0, 0.0, 2.0, 3.0, null, null, 4.0, 5.0, null, 5.0, 4.0 ],
            |    "y": [ null, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.5, null, 1.0, 1.0 ],
            |    "id": [ "A", "A", "A", "A", "A", "B", "B", "B", "B", "C", "C", "C", "C", "C" ]
            |  },
            |  "mapping": {
            |    "x": "x",
            |    "y": "y",
            |    "group": "id",
            |    "color": "id",
            |    "fill": "id"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "int",
            |        "column": "x"
            |      },
            |      {
            |        "type": "float",
            |        "column": "y"
            |      },
            |      {
            |        "type": "str",
            |        "column": "id"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [
            |    {
            |      "aesthetic": "fill",
            |      "scale_mapper_kind": "color_hue"
            |    }
            |  ],
            |  "layers": [
            |    {
            |      "geom": "map",
            |      "mapping": {},
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin())

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("map_with_none.png", plotSpec)
    }

    @Test
    fun `contour with none`() {
        val spec = parsePlotSpec("""
            |{
            |    "data": {
            |    "x": [ 0.0, 1.0, 2.0, 3.0, 4.0, 0.0, 1.0, 2.0, 3.0, 4.0, 0.0, 1.0, 2.0, 3.0, 4.0, 0.0, 1.0, 2.0, 3.0, 4.0, 0.0, 1.0, 2.0, 3.0, 4.0 ],
            |    "y": [ 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0, 3.0, 3.0, 4.0, 4.0, 4.0, 4.0, 4.0 ],
            |    "z": [ 0.0, 1.0, 4.0, 9.0, 16.0, 1.0, 2.0, 5.0, 10.0, 17.0, 4.0, 5.0, 8.0, 13.0, 20.0, 9.0, 10.0, 13.0, 18.0, 25.0, 16.0, 17.0, 20.0, 25.0, 32.0 ]
            |    },
            |    "mapping": {
            |    "x": "x",
            |    "y": "y",
            |    "z": "z"
            |    },
            |    "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "int",
            |        "column": "x"
            |      },
            |      {
            |        "type": "int",
            |        "column": "y"
            |      },
            |      {
            |        "type": "int",
            |        "column": "z"
            |      }
            |    ]
            |    },
            |    "kind": "plot",
            |    "scales": [],
            |    "layers": [
            |    {
            |      "geom": "contour",
            |      "mapping": {},
            |      "data_meta": {},
            |      "bins": 6.0
            |    }
            |    ],
            |    "metainfo_list": []
            |}
        """.trimMargin())

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("contour_with_none.png", plotSpec)
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
        println("Plotting time: ${time.inWholeMilliseconds} ms")

        assertTrue(time.inWholeMilliseconds < 1000, "Plotting took too long: ${time.inWholeMilliseconds} ms")
    }

}