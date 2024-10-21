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
            tooltip()
        )
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
              "mapping": {},
              "data_meta": {
                "series_annotations": [
                  {
                    "type": "str",
                    "column": "text"
                  }
                ]
              },
              "ggtitle": {
                "text": "Notebook with <a href=\"https://google.com\">links</a>",
                "subtitle": "Visit <a href=\"https://lets-plot.org\">lets-plot.org</a> for more examples"
              },
              "caption": {
                "text": "Data provided by <a href=\"https://nasa.com\">NASA</a> and <a href=\"https://eida.com\">EISA</a>."
              },
              "facet": {
                "name": "grid",
                "x": "text",
                "x_order": 1.0,
                "y_order": 1.0
              },
              "kind": "plot",
              "scales": [],
              "layers": [
                {
                  "geom": "label",
                  "mapping": {
                    "label": "text"
                  },
                  "tooltips": {
                    "formats": [],
                    "lines": [
                      "^label"
                    ]
                  },
                  "data_meta": {},
                  "x": 0.0,
                  "y": 0.0
                }
              ],
              "metainfo_list": []
            }
        """.trimIndent()

        return parsePlotSpec(spec)
    }

    private fun titles(): MutableMap<String, Any> {
        val spec = """
            {
              "mapping": {},
              "data_meta": {},
              "ggtitle": {
                "text": "Notebook with <a href=\"https://lets-plot.org\">Lets-Plot</a>",
                "subtitle": "Visit <a href=\"https://github.com/JetBrains/lets-plot\">GitHub</a> for more info"
              },
              "caption": {
                "text": "Data provided by <a href=\"https://nasa.com\">NASA</a> and <a href=\"https://eisa.com\">EISA</a>."
              },
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
                      "Link to the <a href=\"https://lets-plot.org/python/pages/gallery.html\">Lets-Plot gallery</a> inside a tooltip"
                    ]
                  },
                  "data_meta": {},
                  "x": 0.0,
                  "y": 0.0,
                  "label": "Here is a label with a <a href=\"https://lets-plot.org\">Lets-Plot</a>! link"
                }
              ],
              "metainfo_list": []
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