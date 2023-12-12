/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class PowerExponentFormat {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            powerExponentFormat()
        )
    }

    private fun powerExponentFormat(): MutableMap<String, Any> {
        val spec = """
            {
              "data": {
                "x": [ 2.718281828459e-09, 3.718281828459e-09, 4.718281828459e-09 ],
                "y": [ 2.718281828459e-09, 3.718281828459e-09, 4.718281828459e-09 ]
              },
              "mapping": { "x": "x", "y": "y" },
              "ggsize": { "width": 600.0, "height": 600.0 },
              "kind": "plot",
              "layers": [
                {
                  "geom": "point",
                  "tooltips": {
                    "formats": [
                      {
                        "field": "@x",
                        "format": ".3e"
                      },
                      {
                        "field": "@y",
                        "format": "e"
                      }
                    ],
                    "lines": [
                      "@|@x",
                      "@|@y",
                      "Two formulas in one line|(@x, @y)"
                    ],
                    "title": "^x"
                  }
                }
              ]
            }
        """.trimIndent()
        return parsePlotSpec(spec)
    }
}