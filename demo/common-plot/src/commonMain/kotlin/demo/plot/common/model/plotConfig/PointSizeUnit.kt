/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class PointSizeUnit {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            sizeUnitX(),
        )
    }

    private fun sizeUnitX(): MutableMap<String, Any> {
        val spec = """
            {
              "kind": "plot",
              "layers": [
                {
                  "geom": "point",
                  "x": 0,
                  "size_unit": "x"
                }
              ],
              'coord': {"name": "fixed", "ratio": 1.0, "flip": "False"},
              'scales': [
                    {'aesthetic': 'x', 'limits': [-2, 2]},
                    {'aesthetic': 'y', 'limits': [-2, 2]}
                    ]
            }
        """.trimIndent()

        return parsePlotSpec(spec)
    }
}