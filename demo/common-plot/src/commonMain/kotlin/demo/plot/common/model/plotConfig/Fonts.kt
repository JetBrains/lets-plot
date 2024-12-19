/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class Fonts {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            fonts()
        )
    }

    private fun fonts(): MutableMap<String, Any> {
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
                    "y": [
                      0.0,
                      1.0,
                      2.0,
                      3.0,
                      4.0,
                      5.0,
                      6.0,
                      7.0,
                      8.0,
                      9.0,
                      10.0,
                      11.0,
                      12.0
                    ],
                    "label": [
                      "Arial",
                      "Calibri",
                      "Garamond",
                      "Geneva",
                      "Georgia",
                      "Helvetica",
                      "Lucida Grande",
                      "Rockwell",
                      "Times New Roman",
                      "Verdana",
                      "sans-serif",
                      "serif",
                      "monospace"
                    ],
                    "family": [
                      "Arial",
                      "Calibri",
                      "Garamond",
                      "Geneva",
                      "Georgia",
                      "Helvetica",
                      "Lucida Grande",
                      "Rockwell",
                      "Times New Roman",
                      "Verdana",
                      "sans-serif",
                      "serif",
                      "monospace"
                    ]
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