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
            titles()
        )
    }

    private fun titles(): MutableMap<String, Any> {
        val spec = """
            {
              "mapping": {},
              "data_meta": {},
              "ggtitle": {
                "text": "Notebook with <a href=\"https://google.com\">links</a>",
                "subtitle": "Visit <a href=\"https://lets-plot.org\">lets-plot.org</a> for more examples"
              },
              "caption": {
                "text": "Data provided by <a href=\"https://nasa.com\">NASA</a> and <a href=\"https://eida.com\">EISA</a>."
              },
              "kind": "plot",
              "scales": [],
              "layers": [
                {
                  "geom": "label",
                  "mapping": {},
                  "tooltips": {
                    "formats": [],
                    "lines": [
                      "Link to a <a href=\"https://google.com\">google</a> inside a tooltip"
                    ]
                  },
                  "data_meta": {},
                  "x": 0.0,
                  "y": 0.0,
                  "label": "Here is a label with a <a href=\"https://google.com\">google</a>! link"
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
                    "label": ["This is a <a href=\"https://opentopomap.org/\">first link</a> and <a href=\"https://opentopomap.org/\">second link</a>!"]
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
                    "label": ["This is a <a href=\"https://opentopomap.org/\">link</a>!"]
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
}