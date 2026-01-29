/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalNativeApi::class)

package org.jetbrains.letsPlot.pythonExtension.interop

import demoAndTestShared.parsePlotSpec
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil
import org.jetbrains.letsPlot.visualtesting.RasterTileServer
import kotlin.experimental.ExperimentalNativeApi
import kotlin.random.Random
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail
import kotlin.time.measureTime


/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

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

        val plotSpec = parsePlotSpec(spec)
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

        val plotSpec = parsePlotSpec(spec)

        assertPlot("plot_italic_from_theme_test.png", plotSpec)
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

        val plotSpec = parsePlotSpec(spec)

        assertPlot("plot_markdown_test.png", plotSpec)
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
    fun `latex sup and sub`() {
        val spec = """
            {
              "theme": { "name": "classic", "line": "blank", "axis": "blank" },
              "mapping": {},
              "data_meta": {},
              "kind": "plot",
              "scales": [
                {
                  "aesthetic": "y",
                  "limits": [
                    -0.25,
                    7.25
                  ],
                  "trans": "reverse"
                }
              ],
              "layers": [
                {
                  "geom": "text",
                  "mapping": {
                    "y": [
                      0.0,
                      1.0,
                      2.0,
                      3.0,
                      4.0,
                      5.0,
                      6.0,
                      7.0
                    ],
                    "label": [
                      "\\( a^b \\)",
                      "\\( a^{bc} \\)",
                      "\\( a_b \\)",
                      "\\( a_{bc} \\)",
                      "\\( a^{b^c} \\)",
                      "\\( a_{i_1} \\)",
                      "\\( a^{b_i} \\)",
                      "\\( a_{I^n} \\)"
                    ]
                  },
                  "data_meta": {},
                  "x": 0.0,
                  "family": "Noto Sans",
                  "fontface": "italic",
                  "size": 12.0
                }
              ],
              "metainfo_list": []
            }
        """.trimIndent()

        val plotSpec = parsePlotSpec(spec)

        assertPlot("plot_sup_sub_test.png", plotSpec)
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
            |      "theme": { "exponent_format": "pow", "text": { "family": "Noto Sans" }, "axis_title_y": { "blank": true } },
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
            |      "theme": { "exponent_format": [ "pow", -3.0, 3.0 ], "text": { "family": "Noto Sans" }, "axis_title_y": { "blank": true } },
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

        val plotSpec = parsePlotSpec(spec)

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

        val plotSpec = parsePlotSpec(spec)

        assertPlot("plot_bold_italic_geom_bar_label_test.png", plotSpec)
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

    @Test
    fun `issue1423 - drawing primitives and clip-path at the same level`() {
        // With 3_000 points test will hang or take a very long time to render if issue #1423 is present.
        // Don't use time measurement because it may vary depending on the machine performance and give false negatives.
        val n = 1_000
        val rnd = Random(42)
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

        val plottingDuration = measureTime {
            assertPlot("issue1423_test.png", plotSpec, scale = 2)
        }

        if (plottingDuration.inWholeSeconds > 20) {
            fail("Plotting took ${plottingDuration.inWholeSeconds} seconds, possible presence of issue #1423")
        }
    }

    @Test
    fun perf() {
        val rnd = Random(42)
        val n = 10_000
        val xs = List(n) { rnd.nextDouble() * 1000 }
        val ys = List(n) { rnd.nextDouble() * 1000 }

        val spec = """
            |{
            |  "kind": "plot",
            |  "data": { "x": [${xs.joinToString(", ")}], "y": [${ys.joinToString(", ")}] },
            |  "mapping": { "x": "x", "y": "y", "fill": "x" },
            |  "layers": [ { "geom": "point", "size": 4, "color": "black", "shape": 21 } ]
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        val times = mutableListOf<Long>()
        var mvg: String? = null
        repeat(1) {
            times += measureTime {
                mvg = PlotReprGenerator.generateMvg(plotSpec)
            }.inWholeMilliseconds
        }
        //println(mvg)
        println("Time for plotting $n points: ${times.joinToString()} ms")
        //assertPlot("perf.png", plotSpec)

    }

    @Test
    fun `plot layout scheme example`() {
        /* Removed rect that caused glitch on linus and mismatching visual tests
            |    { "geom": "text", "x": 1023.0, "y": 173.5, "label": "plot_inset", "angle": 0.0, "hjust": 1.0, "vjust": 0.0, "color": "black" },
            |    { "geom": "rect", "xmin": 187.0, "xmax": 1001.0, "ymin": 33.0, "ymax": 78.0, "size": 8.0, "fill": "light_blue", "color": "light_blue", "alpha": 0.3 }, <-- REMOVED
            |    { "geom": "text", "x": 594.0, "y": 55.5, "label": "TITLE", "angle": 0.0, "color": "darkgray" },
         */
        val spec = """
            |{
            |  "data": {
            |    "x": [ 210.0, 307.0, 404.0, 501.0, 598.0, 695.0, 792.0, 889.0, 986.0 ],
            |    "y": [ 240.0, 210.0, 270.0, 330.0, 240.0, 360.0, 570.0, 480.0, 480.0  ]
            |  },
            |  "mapping": { "x": "x", "y": "y" },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "int", "column": "x" },
            |      { "type": "int", "column": "y" }
            |    ]
            |  },
            |  "theme": { "name": "classic", "line": "blank", "axis": "blank" },
            |  "ggsize": { "width": 1240.0, "height": 840.0 },
            |  "kind": "plot",
            |  "scales": [ { "aesthetic": "y", "trans": "reverse" } ],
            |  "layers": [
            |    { "geom": "rect", "xmin": 11.0, "xmax": 1229.0, "ymin": 11.0, "ymax": 829.0, "size": 8.0, "fill": "white", "color": "lemon_chiffon", "alpha": 0.9 },
            |    { "geom": "text", "x": 620.0, "y": 420.0, "label": "", "angle": 0.0, "color": "darkgray" },
            |    { "geom": "text", "x": 1229.0, "y": 15.5, "label": "plot_margin", "angle": 0.0, "hjust": 1.0, "vjust": 0.0, "color": "black" },
            |    { "geom": "rect", "xmin": 1.0, "xmax": 1239.0, "ymin": 1.0, "ymax": 839.0, "size": -2.0, "fill": "white", "color": "gold", "alpha": 0.0 },
            |    { "geom": "text", "x": 620.0, "y": 420.0, "label": "", "angle": 0.0, "color": "darkgray" },
            |    { "geom": "text", "x": 1239.0, "y": 0.5, "label": "", "angle": 0.0, "hjust": 1.0, "vjust": 0.0, "color": "black" },
            |    { "geom": "rect", "xmin": 100.0, "xmax": 1023.0, "ymin": 169.0, "ymax": 681.0, "size": 8.0, "fill": "white", "color": "plum", "alpha": 0.5 },
            |    { "geom": "text", "x": 561.5, "y": 425.0, "label": "", "angle": 0.0, "color": "darkgray" },
            |    { "geom": "text", "x": 1023.0, "y": 173.5, "label": "plot_inset", "angle": 0.0, "hjust": 1.0, "vjust": 0.0, "color": "black" },
            |    { "geom": "text", "x": 594.0, "y": 55.5, "label": "TITLE", "angle": 0.0, "color": "darkgray" },
            |    { "geom": "text", "x": 1001.0, "y": 37.5, "label": "plot_title: margin", "angle": 0.0, "hjust": 1.0, "vjust": 0.0, "color": "black" },
            |    { "geom": "rect", "xmin": 187.0, "xmax": 1001.0, "ymin": 99.0, "ymax": 146.0, "size": 8.0, "fill": "light_green", "color": "light_green", "alpha": 0.3 },
            |    { "geom": "text", "x": 594.0, "y": 122.5, "label": "SUBTITLE", "angle": 0.0, "color": "darkgray" },
            |    { "geom": "text", "x": 1001.0, "y": 103.5, "label": "plot_subtitle: margin", "angle": 0.0, "hjust": 1.0, "vjust": 0.0, "color": "black" },
            |    { "geom": "rect", "xmin": 33.0, "xmax": 78.0, "ymin": 191.0, "ymax": 597.0, "size": 8.0, "fill": "peach_puff", "color": "peach_puff", "alpha": 0.2 },
            |    { "geom": "text", "x": 55.5, "y": 394.0, "label": "Y-AXIS TITLE", "angle": 90.0, "color": "darkgray" },
            |    { "geom": "text", "x": 22.5, "y": 191.0, "label": "axis_title_y: margin", "angle": 90.0, "hjust": 1.0, "vjust": 1.0, "color": "black" },
            |    { "geom": "rect", "xmin": 112.0, "xmax": 144.0, "ymin": 196.0, "ymax": 588.0, "size": -3.0, "fill": "pink", "color": "light_pink", "alpha": 0.3 },
            |    { "geom": "text", "x": 128.0, "y": 392.0, "label": "y-axis labels", "angle": 90.0, "color": "darkgray" },
            |    { "geom": "text", "x": 107.0, "y": 196.0, "label": "", "angle": 90.0, "hjust": 1.0, "vjust": 1.0, "color": "black" },
            |    { "geom": "rect", "xmin": 155.0, "xmax": 157.0, "ymin": 207.0, "ymax": 577.0, "size": 8.0, "fill": "pink", "color": "light_pink", "alpha": 0.3 },
            |    { "geom": "text", "x": 156.0, "y": 392.0, "label": "", "angle": 90.0, "color": "darkgray" },
            |    { "geom": "text", "x": 144.5, "y": 207.0, "label": "axis_text_spacing_y", "angle": 90.0, "hjust": 1.0, "vjust": 1.0, "color": "black" },
            |    { "geom": "rect", "xmin": 187.0, "xmax": 1001.0, "ymin": 704.0, "ymax": 745.0, "size": 8.0, "fill": "peach_puff", "color": "peach_puff", "alpha": 0.2 },
            |    { "geom": "text", "x": 594.0, "y": 724.5, "label": "X-AXIS TITLE", "angle": 0.0, "color": "darkgray" },
            |    { "geom": "text", "x": 1001.0, "y": 708.5, "label": "axis_title_x: margin", "angle": 0.0, "hjust": 1.0, "vjust": 0.0, "color": "black" },
            |    { "geom": "rect", "xmin": 196.0, "xmax": 996.0, "ymin": 640.0, "ymax": 668.0, "size": -3.0, "fill": "pink", "color": "light_pink", "alpha": 0.3 },
            |    { "geom": "text", "x": 596.0, "y": 654.0, "label": "x-axis labels", "angle": 0.0, "color": "darkgray" },
            |    { "geom": "text", "x": 996.0, "y": 639.0, "label": "", "angle": 0.0, "hjust": 1.0, "vjust": 0.0, "color": "black" },
            |    { "geom": "rect", "xmin": 207.0, "xmax": 985.0, "ymin": 627.0, "ymax": 627.0, "size": 8.0, "fill": "pink", "color": "light_pink", "alpha": 0.3 },
            |    { "geom": "text", "x": 596.0, "y": 627.0, "label": "", "angle": 0.0, "color": "darkgray" },
            |    { "geom": "text", "x": 985.0, "y": 631.5, "label": "axis_text_spacing_x", "angle": 0.0, "hjust": 1.0, "vjust": 0.0, "color": "black" },
            |    { "geom": "rect", "xmin": 187.0, "xmax": 1001.0, "ymin": 191.0, "ymax": 597.0, "size": 8.0, "fill": "gray", "color": "light_gray", "alpha": 0.5 },
            |    { "geom": "text", "x": 594.0, "y": 394.0, "label": "PLOT\nPANEL", "angle": 0.0, "color": "darkgray" },
            |    { "geom": "text", "x": 1001.0, "y": 195.5, "label": "panel_inset", "angle": 0.0, "hjust": 1.0, "vjust": 0.0, "color": "black" },
            |    { "geom": "rect", "xmin": 187.0, "xmax": 1001.0, "ymin": 767.0, "ymax": 809.0, "size": 8.0, "fill": "sky_blue", "color": "sky_blue", "alpha": 0.3 },
            |    { "geom": "text", "x": 594.0, "y": 788.0, "label": "CAPTION", "angle": 0.0, "color": "darkgray" },
            |    { "geom": "text", "x": 1001.0, "y": 771.5, "label": "plot_caption: margin", "angle": 0.0, "hjust": 1.0, "vjust": 0.0, "color": "black" },
            |    { "geom": "rect", "xmin": 1047.0, "xmax": 1053.0, "ymin": 285.0, "ymax": 497.0, "size": 8.0, "fill": "gray93", "color": "gray93" },
            |    { "geom": "text", "x": 1050.0, "y": 391.0, "label": "", "angle": 90.0, "color": "darkgray" },
            |    { "geom": "text", "x": 1036.5, "y": 285.0, "label": "legend_box_spacing", "angle": 90.0, "hjust": 1.0, "vjust": 1.0, "color": "black" },
            |    { "geom": "rect", "xmin": 1065.0, "xmax": 1219.0, "ymin": 275.0, "ymax": 507.0, "size": -2.0, "fill": "chocolate", "color": "chocolate", "alpha": 0.1 },
            |    { "geom": "text", "x": 1142.0, "y": 391.0, "label": "LEGEND", "angle": 0.0, "color": "darkgray" },
            |    { "geom": "text", "x": 1219.0, "y": 274.5, "label": "", "angle": 0.0, "hjust": 1.0, "vjust": 0.0, "color": "black" },
            |    { "geom": "segment",
            |      "arrow": { "name": "arrow", "angle": 20.0, "length": 8.0, "ends": "last", "type": "open" },
            |      "x": 1058.0,
            |      "y": 644.0,
            |      "xend": 1000.0,
            |      "yend": 612.0
            |    },
            |    { "geom": "text", "x": 1042.0, "y": 652.0, "label": "axis tick area", "hjust": 0.0, "color": "black" },
            |    { "geom": "segment",
            |      "arrow": { "name": "arrow", "angle": 20.0, "length": 8.0, "ends": "last", "type": "open" },
            |      "x": 150.0,
            |      "y": 148.0,
            |      "xend": 172.0,
            |      "yend": 194.0
            |    },
            |    { "geom": "text", "x": 174.0, "y": 138.0, "label": "axis tick area", "hjust": 1.0, "color": "black" },
            |    { "geom": "point", "color": "dodgerblue", "alpha": 1.0 },
            |    { "geom": "line", "color": "dodgerblue", "alpha": 1.0 },
            |    { "geom": "segment",
            |      "data": {
            |        "x": [ 210.0, 307.0, 404.0, 501.0, 598.0, 695.0, 792.0, 889.0, 986.0 ],
            |        "xend": [ 210.0, 307.0, 404.0, 501.0, 598.0, 695.0, 792.0, 889.0, 986.0 ],
            |        "y": [ 608.0, 608.0, 608.0, 608.0, 608.0, 608.0, 608.0, 608.0, 608.0 ],
            |        "yend": [ 616.0, 616.0, 616.0, 616.0, 616.0, 616.0, 616.0, 616.0, 616.0 ]
            |      },
            |      "mapping": { "x": "x", "y": "y", "xend": "xend", "yend": "yend" },
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "int", "column": "x" },
            |          { "type": "int", "column": "xend" },
            |          { "type": "int","column": "y" },
            |          { "type": "int", "column": "yend" }
            |        ]
            |      },
            |      "color": "gray50",
            |      "size": 1.0
            |    },
            |    { "geom": "segment",
            |      "data": {
            |        "y": [ 208.0, 281.2, 354.4, 427.6, 500.8, 574.0 ],
            |        "yend": [ 208.0, 281.2, 354.4, 427.6, 500.8, 574.0 ],
            |        "x": [ 168.0, 168.0, 168.0, 168.0, 168.0, 168.0 ],
            |        "xend": [ 176.0, 176.0, 176.0, 176.0, 176.0, 176.0 ]
            |      },
            |      "mapping": { "x": "x", "y": "y", "xend": "xend", "yend": "yend" },
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "float", "column": "y" },
            |          { "type": "float","column": "yend"},
            |          { "type": "int", "column": "x" },
            |          { "type": "int", "column": "xend"}
            |        ]
            |      },
            |      "color": "gray50",
            |      "size": 1.0
            |    }
            |  ]
            |}            
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)

        try {
            assertPlot("plot_layout_scheme_example_test.png", plotSpec, antialiasing = false)
        } catch (e: Throwable) {
            println("!!! LiveMap export failed: ${e.message}")
        }
    }

    // blocked by https://youtrack.jetbrains.com/issue/KTOR-9267 - on Linux CURl is the only available client engine
    @Ignore
    @Test
    fun `minard with default tiles`() {
        // only ktor-client-darwin and ktor-client-curl are able to work with wss://
        // But due to critical issue https://youtrack.jetbrains.com/issue/KTOR-9267 we can't use ktor-client-curl
        if (Platform.osFamily == OsFamily.MACOSX) {
            val spec = """
            |{
            |    "data": {
            |        "long": [24.0, 24.5, 25.5, 26.0, 27.0, 28.0, 28.5, 29.0, 30.0, 30.3, 32.0, 33.2, 34.4, 35.5, 36.0, 37.6, 37.7, 37.5, 37.0, 36.8, 35.4, 34.3, 33.3, 32.0, 30.4, 29.2, 28.5, 28.3, 27.5, 26.8, 26.4, 25.0, 24.4, 24.2, 24.1, 24.0, 24.5, 25.5, 26.6, 27.4, 28.7, 28.7, 29.2, 28.5, 28.3, 24.0, 24.5, 24.6, 24.6, 24.2, 24.1 ], 
            |        "lat": [54.9, 55.0, 54.5, 54.7, 54.8, 54.9, 55.0, 55.1, 55.2, 55.3, 54.8, 54.9, 55.5, 55.4, 55.5, 55.8, 55.7, 55.7, 55.0, 55.0, 55.3, 55.2, 54.8, 54.6, 54.4, 54.3, 54.2, 54.3, 54.5, 54.3, 54.4, 54.4, 54.4, 54.4, 54.4, 55.1, 55.2, 54.7, 55.7, 55.6, 55.5, 55.5, 54.2, 54.1, 54.2, 55.2, 55.3, 55.8, 55.8, 54.4, 54.4 ], 
            |        "survivors": [340000.0, 340000.0, 340000.0, 320000.0, 300000.0, 280000.0, 240000.0, 210000.0, 180000.0, 175000.0, 145000.0, 140000.0, 127100.0, 100000.0, 100000.0, 100000.0, 100000.0, 98000.0, 97000.0, 96000.0, 87000.0, 55000.0, 37000.0, 24000.0, 20000.0, 20000.0, 20000.0, 20000.0, 20000.0, 12000.0, 14000.0, 8000.0, 4000.0, 4000.0, 4000.0, 60000.0, 60000.0, 60000.0, 40000.0, 33000.0, 33000.0, 33000.0, 30000.0, 30000.0, 28000.0, 22000.0, 22000.0, 6000.0, 6000.0, 6000.0, 6000.0 ], 
            |        "direction": ["A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "A", "A", "A", "A", "A", "A", "R", "R", "R", "R", "A", "A", "A", "R", "R", "R" ], 
            |        "group": [1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0 ]
            |    }, 
            |    "data_meta": {
            |        "series_annotations": [
            |            { "type": "float", "column": "long" }, 
            |            { "type": "float", "column": "lat" }, 
            |            { "type": "int", "column": "survivors" }, 
            |            { "type": "str", "column": "direction" }, 
            |            { "type": "int", "column": "group" }
            |        ]
            |    }, 
            |    "guides": { "color": "none" }, 
            |    "kind": "plot", 
            |    "scales": [
            |        { "aesthetic": "size", "range": [ 1.0, 20.0 ] }, 
            |        { "aesthetic": "color", "values": [ "#E1CBAE", "#232021" ] }
            |    ], 
            |    "layers": [
            |        {
            |            "geom": "livemap", 
            |            "tiles": { "kind": "vector_lets_plot", "url": "wss://tiles.datalore.jetbrains.com", "theme": "color", "attribution": "<a href=\"https://lets-plot.org\">© Lets-Plot</a>, map data: <a href=\"https://www.openstreetmap.org/copyright\">© OpenStreetMap contributors</a>." }, 
            |            "geocoding": { "url": "https://geo2.datalore.jetbrains.com/map_data/geocoding" },
            |            "dev_params": { "microtask_executor": "ui_thread", "computation_projection_quant": 100000, "computation_frame_time": 1000 }
            |        }, 
            |        {
            |            "geom": "path", 
            |            "mapping": { "x": "long", "y": "lat", "size": "survivors", "group": "group", "color": "direction" }, 
            |            "data_meta": { }
            |        }
            |    ]
            |}
        """.trimMargin()

            val plotSpec = parsePlotSpec(spec)

            try {
                assertPlot("geom_livemap_minard.png", plotSpec)
            } catch (e: Exception) {
                println("!!! LiveMap export failed: ${e.message}")
            }
        }
    }

    @Test
    @Ignore
    fun minimal_livemap() {
        // only ktor-client-darwin and ktor-client-curl are able to work with wss://
        // But due to critical issue https://youtrack.jetbrains.com/issue/KTOR-9267 we can't use ktor-client-curl
        if (Platform.osFamily == OsFamily.MACOSX) {
            val spec = """
            |{
            |  "ggsize": {
            |    "width": 300.0,
            |    "height": 300.0
            |  },
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "livemap",
            |      "tiles": {
            |        "kind": "vector_lets_plot",
            |        "url": "wss://tiles.datalore.jetbrains.com",
            |        "theme": "color",
            |        "attribution": "<a href=\"https://lets-plot.org\">\u00a9 Lets-Plot</a>, map data: <a href=\"https://www.openstreetmap.org/copyright\">\u00a9 OpenStreetMap contributors</a>."
            |      },
            |      "geocoding": {
            |        "url": "https://geo2.datalore.jetbrains.com/map_data/geocoding"
            |      }
            |    },
            |    {
            |      "geom": "point",
            |      "map": { 
            |        "city": [ "New York", "Boston", "san Francisco" ],
            |        "found name": [ "New York", "Boston", "San Francisco" ],
            |        "geometry": [
            |          "{\"type\": \"Point\", \"coordinates\": [-73.8673749469137, 40.6847005337477]}",
            |          "{\"type\": \"Point\", \"coordinates\": [-71.0884755326693, 42.3110405355692]}",
            |          "{\"type\": \"Point\", \"coordinates\": [-122.447027491874, 37.7586284279823]}"
            |        ]
            |      },
            |      "map_data_meta": {
            |        "geodataframe": {
            |          "geometry": "geometry"
            |        }
            |      }
            |    }
            |  ]
            |}            
        """.trimMargin()

            val plotSpec = parsePlotSpec(spec)
            assertPlot("geom_livemap_minimal.png", plotSpec)
        }
    }

    @Test
    //@Ignore
    fun `geom_livemap nasa tiles`() {
        val spec = parsePlotSpec("""
            |{
            |  "ggsize": { "width": 300.0, "height": 300.0 },
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "livemap",
            |      "tiles": {
            |        "kind": "raster_zxy",
            |        "url": "http://localhost:8080/{z}/{y}/{x}.png",
            |        "attribution": "<a href=\"https://lets-plot.org\">\u00a9 Lets-Plot</a>",
            |      },
            |      "geocoding": { "url": "https://geo2.datalore.jetbrains.com/map_data/geocoding" }
            |    }
            |  ]
            |}            
        """.trimMargin())

        val plotSpec = spec
        assertPlot("geom_livemap_nasa_tiles.png", plotSpec)
    }

    @Test
    fun testServerReturns404ForMissingTile() {
        val server = RasterTileServer("test-data", okio.FileSystem.SYSTEM)
        server.start(8000)

        val client = HttpClient()
        runBlocking {
            val response = client.get("http://0.0.0.0:8000/99/99/99.png")
            assertEquals(HttpStatusCode.NotFound, response.status)
        }

        client.close()
        server.stop()
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
        scale: Number? = 1,
        antialiasing: Boolean = true
    ) {
        val (bitmap, _) = PlotReprGenerator.exportBitmap(
            plotSpec = plotSpec,
            plotSize = plotSize,
            sizeUnit = unit,
            dpi = dpi,
            scale = scale,
            fontManager = embeddedFontsManager,
            //fontManager = MagickFontManager.default() // For manual testing
            antialiasing = antialiasing
        )

        imageComparer.assertBitmapEquals(expectedFileName, bitmap)
    }
}