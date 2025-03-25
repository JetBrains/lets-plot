/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class VerticalGeoms {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            horizontalBoxplot(),
            horizontalBoxplotWithIdentityStat(),
        )
    }

    private fun horizontalBoxplot(): MutableMap<String, Any> {
        val spec = """
            {
              "data": {
                "cat": ["a", "a", "a", "a", "b", "b", "b", "b"],
                "val": [-5, -1, -1, 3, -1, 1, 1, 3]
              },
              "ggtitle": {
                "text": "Horizontal boxplot\ndefault stat"
              },
              "kind": "plot",
              "layers": [
                {
                  "geom": "boxplot",
                  "mapping": {
                    "x": "val",
                    "y": "cat"
                  }
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }

    private fun horizontalBoxplotWithIdentityStat(): MutableMap<String, Any> {
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