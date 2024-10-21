/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class LinkLabel {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            simple(),
            twoLinks(),
            titles(),
            facets(),
            tooltip(),
            polar()
        )
    }

    private fun polar(): MutableMap<String, Any> {
        val spec = """
            |{
            |  "data": {
            |    "name": [
            |      "Lets-Plot\nMultiplatform",
            |      "Lets-Plot\nfor Python",
            |      "Lets-Plot\nfor Kotlin",
            |      "Lets-Plot\nCompose Multiplatform",
            |      "Geocoding"
            |    ],
            |    "documentationUrl": [
            |      "https://lets-plot.org",
            |      "https://lets-plot.org/kotlin/get-started.html",
            |      "https://lets-plot.org/kotlin/get-started.html",
            |      "https://github.com/JetBrains/lets-plot-skia",
            |      "https://lets-plot.org/python/pages/geocoding.html"
            |    ],
            |    "sourcesUrl": [
            |      "https://github.com/JetBrains/lets-plot",
            |      "https://github.com/JetBrains/lets-plot-kotlin",
            |      "https://github.com/JetBrains/lets-plot-kotlin",
            |      "https://github.com/JetBrains/lets-plot-skia",
            |      "https://github.com/JetBrains/lets-plot"
            |    ],
            |    "x": [ 0.0, 130.0, 200.0, 80.0, 70.0 ],
            |    "y": [ 0.0, 150.0, 200.0, 250.0, 320.0 ],
            |    "size": [ 14.0, 9.0, 7.0, 7.0, 7.0 ],
            |    "shape": [ 16.0, 15.0, 15.0, 15.0, 17.0 ],
            |    "angle": [ 0.0, 15.0, -15.0, 30.0, 0.0 ]
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "str", "column": "name" },
            |      { "type": "str", "column": "documentationUrl" },
            |      { "type": "str", "column": "sourcesUrl" },
            |      { "type": "int", "column": "x" },
            |      { "type": "int", "column": "y" },
            |      { "type": "int", "column": "size" },
            |      { "type": "int", "column": "shape" },
            |      { "type": "int", "column": "angle" }
            |    ]
            |  },
            |  "coord": { "name": "polar" },
            |  "ggsize": { "width": 800.0, "height": 800.0 },
            |  "ggtitle": {
            |    "text": "The <a href=\"https://lets-plot.org/python/pages/gallery.html\">Observable</a> LP-verse",
            |    "subtitle": "Latest <a href=\"https://github.com/JetBrains/lets-plot/releases/latest\">news</a>."
            |  },
            |  "caption": {
            |    "text": "User <a href=\"https://github.com/JetBrains/lets-plot/issues\">stories</a>."
            |  },
            |  "theme": {
            |    "axis_title": "blank",
            |    "axis_text": "blank",
            |    "axis_ticks": "blank",
            |    "panel_grid": "blank",
            |    "panel_grid_major_y": { "size": 2.0, "linetype": "dotted", "blank": false },
            |    "plot_title": { "face": "bold", "size": 25.0, "hjust": 0.5, "blank": false },
            |    "plot_subtitle": { "hjust": 0.5, "blank": false },
            |    "plot_margin": [ 40.0, 0.0, 0.0 ],
            |    "flavor": "high_contrast_dark"
            |  },
            |  "kind": "plot",
            |  "scales": [
            |    { "aesthetic": "shape", "guide": "none", "scale_mapper_kind": "identity", "discrete": true },
            |    { "aesthetic": "size", "guide": "none", "scale_mapper_kind": "identity" },
            |    { "aesthetic": "y", "breaks": [ 150.0, 200.0, 250.0, 320.0 ] },
            |    { "aesthetic": "y", "limits": [ 0.0, 400.0 ] }
            |  ],
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "data": {
            |        "x": [ 109, 143, 120, 108, 84, 129, 87, 178, 192, 76 ],
            |        "y": [ 337, 258, 270, 377, 121, 126, 106, 349, 333, 361],
            |        "size": [ 1.9, 1.6, 0.9, 1.5, 0.3, 1.3, 0.3, 1.8, 1.0, 0.8 ]
            |      },
            |      "mapping": { "x": "x", "y": "y", "size": "size" },
            |      "tooltips": "none",
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "float", "column": "x" },
            |          { "type": "float", "column": "y" },
            |          { "type": "float", "column": "size" }
            |        ]
            |      }
            |    },
            |    {
            |      "geom": "point",
            |      "mapping": {
            |        "x": "x",
            |        "y": "y",
            |        "size": "size",
            |        "shape": "shape",
            |        "angle": "angle",
            |        "color": "name"
            |      },
            |      "show_legend": false,
            |      "tooltips": {
            |        "lines": [
            |          "Links: <a href=\"@{documentationUrl}\">docs</a>, <a href=\"@{sourcesUrl}\">sources</a>"
            |        ],
            |        "title": "@name"
            |      }
            |    },
            |    {
            |      "geom": "text",
            |      "x": 50.0,
            |      "y": 250.0,
            |      "size": 12.0,
            |      "label": "Hover, then click\nto <a href=\"https://www.merriam-webster.com/dictionary/freeze\">freeze</a> the tooltip.\nClick links\nto navigate."
            |    },
            |    {
            |      "geom": "segment",
            |      "arrow": { "name": "arrow", "angle": 40.0, "type": "open" },
            |      "x": 70.0,
            |      "y": 250.0,
            |      "xend": 0.0,
            |      "yend": 0.0,
            |      "size_start": 150.0,
            |      "size_end": 20.0
            |    }
            |  ]
            |}            
        """.trimMargin()
        return parsePlotSpec(spec)
    }

    private fun facets(): MutableMap<String, Any> {
        val spec = """
            {
              "theme": { "flavor": "darcula" },
              "data": {
                "text": [
                  "Here is a label with a <a href=\"https://lets-plot.org\">Lets-Plot</a>! link",
                  "Here is a label with a <a href=\"https://github.com/JetBrains/lets-plot\">GitHub</a>! link"
                ]
              },
              "data_meta": {
                "series_annotations": [
                  { "type": "str", "column": "text" }
                ]
              },
              "ggtitle": {
                "text": "Notebook with <a href=\"https://google.com\">links</a>",
                "subtitle": "Visit <a href=\"https://lets-plot.org\">lets-plot.org</a> for more examples"
              },
              "caption": {
                "text": "Data provided by <a href=\"https://nasa.com\">NASA</a> and <a href=\"https://eida.com\">EISA</a>."
              },
              "facet": { "name": "grid", "x": "text", "x_order": 1.0, "y_order": 1.0 },
              "kind": "plot",
              "layers": [
                {
                  "geom": "label",
                  "mapping": { "label": "text" },
                  "tooltips": { "lines": [ "^label" ] },
                  "x": 0.0,
                  "y": 0.0
                }
              ]
            }
        """.trimIndent()

        return parsePlotSpec(spec)
    }

    private fun titles(): MutableMap<String, Any> {
        val spec = """
            {
              "ggtitle": {
                "text": "Notebook with <a href=\"https://lets-plot.org\">Lets-Plot</a>",
                "subtitle": "Visit <a href=\"https://github.com/JetBrains/lets-plot\">GitHub</a> for more info"
              },
              "caption": {
                "text": "Data provided by <a href=\"https://nasa.com\">NASA</a> and <a href=\"https://eisa.com\">EISA</a>."
              },
              "kind": "plot",
              "layers": [
                {
                    "geom": "rect",
                    "xmin": -5,
                    "xmax": 5,
                    "ymin": -5,
                    "ymax": 5,
                    "fill": "red",
                    "alpha": 0.5
                },
                {
                  "geom": "label",
                  "tooltips": {
                    "lines": [
                      "Link to the <a href=\"https://lets-plot.org/python/pages/gallery.html\">Lets-Plot gallery</a> inside a tooltip"
                    ]
                  },
                  "x": 0.0,
                  "y": 0.0,
                  "label": "Here is a label with a <a href=\"https://lets-plot.org\">Lets-Plot</a>! link"
                }
              ]
            }
        """.trimIndent()

        return parsePlotSpec(spec)
    }

    private fun twoLinks(): MutableMap<String, Any> {
        val spec = """
            {
              "mapping": {},
              "data_meta": {},
              "kind": "plot",
              "scales": [],
              "layers": [
                {
                  "geom": "label",
                  "mapping": {
                    "y": [0.0],
                    "label": ["Visit <a href=\"https://lets-plot.org\">Lets-Plot</a> and <a href=\"https://lets-plot.org/python/pages/gallery.html\">gallery</a>!"]
                  },
                  "data_meta": {},
                  "label_padding": 0.0,
                  "label_r": 0.0,
                  "size": 10.0
                }
              ],
              "metainfo_list": []
            }
        """.trimIndent()

        return parsePlotSpec(spec)
    }

    private fun simple(): MutableMap<String, Any> {
        val spec = """
            {
              "mapping": {},
              "data_meta": {},
              "kind": "plot",
              "scales": [],
              "layers": [
                {
                  "geom": "label",
                  "mapping": {
                    "y": [0.0],
                    "label": ["This is a <a href=\"https://lets-plot.org/\">Lets-Plot</a>!\nSay HI!"]
                  },
                  "data_meta": {},
                  "label_padding": 0.0,
                  "label_r": 0.0,
                  "size": 10.0
                }
              ],
              "metainfo_list": []
            }
        """.trimIndent()

        return parsePlotSpec(spec)
    }

    private fun tooltip(): MutableMap<String, Any> {
        val spec = """
            {
              "mapping": {},
              "data_meta": {},
              "kind": "plot",
              "scales": [],
              "layers": [
                {
                    "geom": "rect",
                    "xmin": -5,
                    "xmax": 5,
                    "ymin": -5,
                    "ymax": 5,
                    "fill": "red",
                    "alpha": 0.5
                },
                {
                  "geom": "label",
                  "mapping": {},
                  "tooltips": {
                    "formats": [],
                    "lines": [
                      "First line\nSecond Line\nThird Line\nLink to the <a href=\"https://lets-plot.org/python/pages/gallery.html\">Lets-Plot gallery</a> inside a tooltip"
                    ]
                  },
                  "data_meta": {},
                  "x": 0.0,
                  "y": 0.0,
                  "label": "Hey"
                }
              ],
              "metainfo_list": []
            }
        """.trimIndent()

        return parsePlotSpec(spec)
    }

}