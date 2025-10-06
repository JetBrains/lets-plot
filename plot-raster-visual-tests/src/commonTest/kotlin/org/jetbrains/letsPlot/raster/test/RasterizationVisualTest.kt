package org.jetbrains.letsPlot.raster.test

import demoAndTestShared.parsePlotSpec
import kotlin.test.Test

class RasterizationVisualTest : VisualTestBase() {
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
            |{
            |  "kind": "plot",
            |  "theme": { "name": "classic", "line": "blank", "axis": "blank" },
            |  "scales": [
            |    { "aesthetic": "y", "limits": [ -0.25, 7.25 ], "trans": "reverse" }
            |  ],
            |  "layers": [
            |    {
            |      "geom": "text",
            |      "mapping": {
            |        "y": [ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0 ],
            |        "label": [ "\\( a^b \\)", "\\( a^{bc} \\)", "\\( a_b \\)", "\\( a_{bc} \\)", "\\( a^{b^c} \\)", "\\( a_{i_1} \\)", "\\( a^{b_i} \\)", "\\( a_{I^n} \\)" ]
            |      },
            |      "x": 0.0,
            |      "family": "Noto Sans",
            |      "fontface": "italic",
            |      "size": 12.0
            |    }
            |  ]
            |}
        """.trimMargin()

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
            |      "\\( x^{a \\cdot b^{c - d}}}}+1 \\)",
            |      "\\( x^{a \\cdot b}+1 \\)",
            |      "\\( x^{a \\cdot b}+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^{a \\cdot b^c}+1 \\)",
            |      "\\( x^{a \\cdot b}+1 \\)",
            |      "\\( x^{a \\cdot b}+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^{a \\cdot b^{c - d}}}}+1 \\)",
            |      "\\( x^{a \\cdot b^c}+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^{a \\cdot b}+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^{a \\cdot b}+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^{a \\cdot b^c}+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^{a \\cdot b}+1 \\)",
            |      "\\( x^{a \\cdot b}+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^{a \\cdot b}+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^{a \\cdot b^c}+1 \\)",
            |      "\\( x^{a \\cdot b^{c - d}}}}+1 \\)",
            |      "\\( x^{a \\cdot b^c}+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^{a \\cdot b}+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^{a \\cdot b^c}+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^{a \\cdot b}+1 \\)",
            |      "\\( x^a+1 \\)",
            |      "\\( x^{a \\cdot b}+1 \\)",
            |      "\\( x^{a \\cdot b}+1 \\)",
            |      "\\( x^a+1 \\)"
            |    ]
            |  },
            |  "mapping": { "x": "x" },
            |  "data_meta": {
            |    "series_annotations": [ { "type": "str", "column": "x" } ],
            |    "mapping_annotations": [
            |      {
            |        "parameters": {
            |          "label": "x",
            |          "order_by": "..count.."
            |        },
            |        "aes": "x",
            |        "annotation": "as_discrete"
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "layers": [ { "geom": "bar" } ]
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
            |}
            |""".trimMargin()

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()
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

        val plotSpec = parsePlotSpec(spec).themeTextNotoSans()
        assertPlot("plot_polar_test.png", plotSpec)
    }

    @Test
    fun geom_raster() {
        val spec = parsePlotSpec(
            """
            |{
            |  "data": {
            |    "x": [ -1.0, 1.0, -1.0, 1.0 ],
            |    "y": [ -1.0, -1.0, 1.0, 1.0 ],
            |    "z": [ 0.024, 0.094, 0.094, 0.024 ]
            |  },
            |  "kind": "plot",
            |  "scales": [
            |    {
            |      "aesthetic": "fill",
            |      "low": "#54278f",
            |      "high": "#f2f0f7",
            |      "scale_mapper_kind": "color_gradient",
            |      "guide": "none"
            |    }
            |  ],
            |  "layers": [
            |    {
            |      "geom": "raster",
            |      "mapping": { "x": "x", "y": "y", "fill": "z" }
            |    }
            |  ]
            |}
        """.trimMargin()
        )
            .themeTextNotoSans()

        assertPlot("geom_raster_test.png", spec)
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
    fun `geom_imshow`() {
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
            .themeTextNotoSans()

        assertPlot("geom_imshow_export_test.png", spec)
    }

    @Test
    fun `shape with 90 degree rotation`() {
        // Was a bug caused by multiplying stroke by the transform.sx (which is 0.0 for 90-degree rotation)
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

        // stroke size should remain the same (3 pixels) at any scaling factor
        assertPlot("plot_constant_stroke_size_test.png", spec, scale = 1.0)
    }

    @Test
    fun `path with none`() {
        val spec = parsePlotSpec(
            """
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
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "line",
            |      "mapping": {},
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("path_with_none.png", plotSpec)
    }

    @Test
    fun `path with none coord polar`() {
        val spec = parsePlotSpec(
            """
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
            |      "geom": "line",
            |      "mapping": {},
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin()
        )

        val plotSpec = spec.themeTextNotoSans()

        assertPlot("path_with_none_coord_polar.png", plotSpec)
    }
}