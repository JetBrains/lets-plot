/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class VerticalGeoms {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            boxplotIdentityStat(),
        )
    }

    private fun boxplotIdentityStat(): MutableMap<String, Any> {
        val spec = """
            {
              "data": {
                "cat": ["a", "b"],
                "min": [-4, -2],
                "lower": [-2, -1],
                "middle": [0, 0],
                "upper": [2, 1],
                "max": [4, 2]
              },
              "ggtitle": {
                "text": "Horizontal boxplot\n'identity' stat"
              },
              "data_meta": {
                "series_annotations": [
                  {"type": "str", "column": "cat"},
                  {"type": "int", "column": "min"},
                  {"type": "int", "column": "lower"},
                  {"type": "int", "column": "middle"},
                  {"type": "int", "column": "upper"},
                  {"type": "int", "column": "max"}
                ]
              },
              "kind": "plot",
              "layers": [
                {
                  "geom": "boxplot",
                  "stat": "identity",
                  "mapping": {
                    "y": "cat",
                    "xmin": "min",
                    "xlower": "lower",
                    "xmiddle": "middle",
                    "xupper": "upper",
                    "xmax": "max"
                  }
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }
}