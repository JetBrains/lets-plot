/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class RibbonTooltips {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(grid())
    }

    fun grid(): MutableMap<String, Any> {
        val plotSpec = """
            {
              "kind": "subplots",
              "layout": { "ncol": 2.0, "nrow": 4.0, "name": "grid" },
              "figures": [
                {
                  "data": {
                    "g": [ "a", "a", "a", "a" ],
                    "t": [ 300.0, 310.0, 315.0, 310.0 ],
                    "b": [ 250.0, 230.0, 210.0, 250.0 ],
                    "x": [ 100.0, 105.0, 110.0, 115.0 ]
                  },
                  "kind": "plot",
                  "layers": [
                    {
                      "geom": "ribbon",
                      "mapping": { "x": "x", "ymin": "b", "ymax": "t" }
                    }
                  ]
                },
                {
                  "data": {
                    "g": [ "b", "b", "b", "b" ],
                    "t": [ 405.0, 425.0, 400.0, 300.0 ],
                    "b": [ 100.0, 300.0, 150.0, 200.0 ],
                    "x": [ 100.0, 105.0, 110.0, 115.0 ]
                  },
                  "kind": "plot",
                  "layers": [
                    {
                      "geom": "ribbon",
                      "mapping": { "x": "x", "ymin": "b", "ymax": "t" }
                    }
                  ]
                },
                {
                  "data": {
                    "g": [ "a", "a", "a", "a" ],
                    "t": [ 300.0, 310.0, 315.0, 310.0 ],
                    "b": [ 250.0, 230.0, 210.0, 250.0 ],
                    "x": [ 100.0, 105.0, 110.0, 115.0 ]
                  },
                  "kind": "plot",
                  "layers": [
                    {
                      "geom": "ribbon",
                      "mapping": { "x": "x", "ymin": "b", "ymax": "t" }
                    },
                    {
                      "geom": "point",
                      "data": {
                        "x": [ 100.0, 105.0, 110.0, 115.0 ],
                        "y": [ 543.0, 553.0, 558.0, 553.0 ],
                        "v": [ "B", "B", "B", "B" ]
                      },
                      "mapping": { "x": "x", "y": "y" }
                    }
                  ]
                },
                {
                  "data": {
                    "g": [ "b", "b", "b", "b" ],
                    "t": [ 405.0, 425.0, 400.0, 300.0 ],
                    "b": [ 100.0, 300.0, 150.0, 200.0 ],
                    "x": [ 100.0, 105.0, 110.0, 115.0 ]
                  },
                  "kind": "plot",
                  "layers": [
                    {
                      "geom": "ribbon",
                      "mapping": { "x": "x", "ymin": "b", "ymax": "t" }
                    },
                    {
                      "geom": "point",
                      "data": {
                        "x": [ 100.0, 105.0, 110.0, 115.0 ],
                        "y": [ 502.0, 522.0, 497.0, 397.0 ],
                        "v": [ "B", "B", "B", "B" ]
                      },
                      "mapping": { "x": "x", "y": "y" }
                    }
                  ]
                },
                {
                  "kind": "plot",
                  "layers": [
                    {
                      "geom": "ribbon",
                      "data": {
                        "g": [ "a", "a", "a", "a" ],
                        "t": [ 300.0, 310.0, 315.0, 310.0 ],
                        "b": [ 250.0, 230.0, 210.0, 250.0 ],
                        "x": [ 100.0, 105.0, 110.0, 115.0 ]
                      },
                      "mapping": { "x": "x", "ymin": "b", "ymax": "t" }
                    },
                    {
                      "geom": "ribbon",
                      "data": {
                        "g": [ "b", "b", "b", "b" ],
                        "t": [ 405.0, 425.0, 400.0, 300.0 ],
                        "b": [ 100.0, 300.0, 150.0, 200.0 ],
                        "x": [ 100.0, 105.0, 110.0, 115.0 ]
                      },
                      "mapping": { "x": "x", "ymin": "b", "ymax": "t" }
                    }
                  ]
                },
                {
                  "kind": "plot",
                  "layers": [
                    {
                      "geom": "ribbon",
                      "data": {
                        "g": [ "a", "a", "a", "a", "b", "b", "b", "b" ],
                        "t": [ 300.0, 310.0, 315.0, 310.0, 405.0, 425.0, 400.0, 300.0 ],
                        "b": [ 250.0, 230.0, 210.0, 250.0, 100.0, 300.0, 150.0, 200.0 ],
                        "x": [ 100.0, 105.0, 110.0, 115.0, 100.0, 105.0, 110.0, 115.0 ]
                      },
                      "mapping": { "x": "x", "ymin": "b", "ymax": "t", "color": "g" }
                    }
                  ]
                },
                {
                  "kind": "plot",
                  "layers": [
                    {
                      "geom": "ribbon",
                      "data": {
                        "g": [ "a", "a", "a", "a" ],
                        "t": [ 300.0, 310.0, 315.0, 310.0 ],
                        "b": [ 250.0, 230.0, 210.0, 250.0 ],
                        "x": [ 100.0, 105.0, 110.0, 115.0 ]
                      },
                      "mapping": { "x": "x", "ymin": "b", "ymax": "t" }
                    },
                    {
                      "geom": "ribbon",
                      "data": {
                        "g": [ "b", "b", "b", "b" ],
                        "t": [ 405.0, 425.0, 400.0, 300.0 ],
                        "b": [ 100.0, 300.0, 150.0, 200.0 ],
                        "x": [ 100.0, 105.0, 110.0, 115.0 ]
                      },
                      "mapping": { "x": "x", "ymin": "b", "ymax": "t" }
                    },
                    {
                      "geom": "point",
                      "data": {
                        "x": [ 100.0, 105.0, 110.0, 115.0 ],
                        "y": [ 543.0, 553.0, 558.0, 553.0 ],
                        "v": [ "B", "B", "B", "B" ]
                      },
                      "mapping": { "x": "x", "y": "y"
                      }
                    },
                    {
                      "geom": "point",
                      "data": {
                        "x": [ 100.0, 105.0, 110.0, 115.0 ],
                        "y": [ 502.0, 522.0, 497.0, 397.0 ],
                        "v": [ "B", "B", "B", "B" ]
                      },
                      "mapping": { "x": "x", "y": "y" }
                    }
                  ]
                },
                {
                  "kind": "plot",
                  "layers": [
                    {
                      "geom": "ribbon",
                      "data": {
                        "g": [ "a", "a", "a", "a", "b", "b", "b", "b" ],
                        "t": [ 300.0, 310.0, 315.0, 310.0, 405.0, 425.0, 400.0, 300.0 ],
                        "b": [ 250.0, 230.0, 210.0, 250.0, 100.0, 300.0, 150.0, 200.0 ],
                        "x": [ 100.0, 105.0, 110.0, 115.0, 100.0, 105.0, 110.0, 115.0 ]
                      },
                      "mapping": { "x": "x", "ymin": "b", "ymax": "t", "color": "g" }
                    },
                    {
                      "geom": "point",
                      "data": {
                        "g": [ "a", "a", "a", "a", "b", "b", "b", "b" ],
                        "y": [ 543.0, 553.0, 558.0, 553.0, 502.0, 522.0, 497.0, 397.0 ],
                        "x": [ 100.0, 105.0, 110.0, 115.0, 100.0, 105.0, 110.0, 115.0 ],
                        "v": [ "B", "B", "B", "B", "B", "B", "B", "B" ]
                      },
                      "mapping": { "x": "x", "y": "y", "color": "g" }
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        return parsePlotSpec(plotSpec)
    }
}