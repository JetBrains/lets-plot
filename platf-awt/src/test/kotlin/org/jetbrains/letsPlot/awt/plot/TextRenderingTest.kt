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

class TextRenderingTest : PlotVisualTestBase() {
    @get:Rule
    var currentTest = TestName()

    override val canvasPeer: CanvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
    override val imageComparer: ImageComparer = ImageComparer(canvasPeer, AwtBitmapIO(subdir = "rendering"))

    override fun currentTestName(): String? = currentTest.methodName

    @Test
    fun plot_text_italicTheme() {
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

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec))
    }

    @Test
    fun plot_text_latexFormula() {
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
            |      "fontface": "italic"
            |    }
            |  ]
            |}
        """.trimMargin()

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec))
    }

    @Test
    fun plot_text_superscript() {
        val spec = """
            |{
            |  "kind": "subplots",
            |  "layout": { "ncol": 2.0, "nrow": 1.0, "name": "grid" },
            |  "figures": [
            |    {
            |      "kind": "plot",
            |      "ggtitle": { "text": "Default limits" },
            |      "theme": { "name": "classic", "exponent_format": "pow", "axis_title_y": { "blank": true } },
            |      "scales": [ { "aesthetic": "y", "limits": [ 1e-08, 10000000.0 ], "trans": "log10" } ],
            |      "layers": [
            |        {
            |          "geom": "text",
            |          "mapping": {
            |            "y": [ 1e-07, 1e-06, 1e-05, 0.0001, 0.001, 0.01, 0.1, 1.0, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0 ],
            |            "label": [ 1e-07, 1e-06, 1e-05, 0.0001, 0.001, 0.01, 0.1, 1.0, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0 ]
            |          },
            |          "size": 10.0
            |        }
            |      ]
            |    },
            |    {
            |      "kind": "plot",
            |      "ggtitle": { "text": "Scientific notation for \\( x \\leq 10^{-3} \\) and \\( x \\geq 10^3 \\)" },
            |      "theme": { "name": "classic", "exponent_format": [ "pow", -3.0, 3.0 ], "axis_title_y": { "blank": true } },
            |      "scales": [ { "aesthetic": "y", "limits": [ 1e-08, 10000000.0 ], "trans": "log10" } ],
            |      "layers": [
            |        {
            |          "geom": "text",
            |          "mapping": {
            |            "y": [ 1e-07, 1e-06, 1e-05, 0.0001, 0.001, 0.01, 0.1, 1.0, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0 ],
            |            "label": [ 1e-07, 1e-06, 1e-05, 0.0001, 0.001, 0.01, 0.1, 1.0, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0 ]
            |          },
            |          "size": 10.0
            |        }
            |      ]
            |    }
            |  ]
            |}
            |""".trimMargin()

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec))
    }

    @Test
    fun plot_text_multilevelLatexFormula() {
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

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec))
    }

    @Test
    fun plot_text_boldItalicBarLabel() {
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
            |}
            |""".trimMargin()

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec))
    }

    @Test
    fun plot_text_labels() {
        val spec = """
            {
              "kind": "plot",
              "theme": {
                "axis_title_y": { "blank": true }
              },
              "layers": [
                { "geom": "text", "x": 0.0, "y": 0.0, "label": "QWE" },
                { "geom": "text", "x": 0.0, "y": 0.0, "label": "___", "color": "red" }
              ],
              "ggsize": { "width": 200.0, "height": 200.0 }
            }
        """.trimIndent()

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec))
    }

    @Test
    fun plot_text_markdown2Xscale() {
        val spec = """
            |{
            |  "theme": {
            |    "title": { "markdown": true, "blank": false },
            |    "plot_title": { "size": 30.0, "hjust": 0.5, "blank": false },
            |    "plot_subtitle": { "hjust": 0.5, "blank": false }
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

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec), scale = 2)
    }

    @Test
    fun plot_text_markdown() {
        val spec = """
            |{
            |  "theme": {
            |    "title": { "markdown": true, "blank": false },
            |    "plot_title": { "size": 30.0, "hjust": 0.5, "blank": false },
            |    "plot_subtitle": { "hjust": 0.5, "blank": false }
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

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec))
    }

    @Test
    fun plot_text_markdownFauxObliqueBold2Xscale() {
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
            |    "text": { "family": "Noto Sans Regular" }
            |  }
            |}
        """.trimMargin()

        assertExportedPlot(currentTestName() + ".png", parsePlotSpec(spec), scale = 2)
    }
}
