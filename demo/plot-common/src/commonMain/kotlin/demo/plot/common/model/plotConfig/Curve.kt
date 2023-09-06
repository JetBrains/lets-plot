/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class Curve {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            grid(curvature = 0.9, angle = 45.0),
            grid(curvature = 1.3, angle = 135.0),
            example(curvature = 0.9, angle = -45.0),
        )
    }
    companion object {
        private fun example(curvature: Double, angle: Double): MutableMap<String, Any> {
            val spec = """{
                'ggtitle': { 'text': 'curvature = $curvature, angle = $angle' },
                'kind': 'plot',
                'layers': [
                  {
                    'data': {'x': [-3,3], 'y': [-3,3]},
                    'mapping': { 'x': 'x', 'y': 'y'},
                    'geom': 'point'
                  },
                  {
                    'geom': 'curve',
                    'x': -2, 'y':  1, 'xend': 1, 'yend': -1, 
                    'angle': $angle,
                    'curvature': $curvature,
                    'ncp': 5,
                    'color': 'red', 'tooltips': { 'lines': ['Tooltip'] }, 
                    'size': 1
                  },
                  {
                    'geom': 'segment', 'x': -2, 'y':  1, 'xend': 1, 'yend': -1,
                    'color': 'blue', 'alpha': 0.3,
                    'size': 0.5
                  }
                ]
            }""".trimIndent()

            return HashMap(parsePlotSpec(spec))
        }

        private fun grid(curvature: Double, angle: Double): MutableMap<String, Any> {
            fun plot(curvature: Double, angle: Double) = """{
                "data": {
                    "x": [-30.0, 30.0],
                    "y": [-3.0,  3.0]
                },
                "mapping": {
                    "x": "x",
                    "y": "y"
                },
                "ggtitle": { "text": "curvature = $curvature, angle=$angle" },
                "kind": "plot",
                "layers": [
                    {
                        "geom": "point"
                    },
                    {
                        "geom": "curve",
                        "x": -20.0,
                        "y": 1.0,
                        "xend": 10.0,
                        "yend": -1.0,
                        "curvature": $curvature,
                        "angle": $angle,
                        "ncp": 5.0
                    }
                ]
            }""".trimIndent()

            val spec = """{
                "kind": "subplots",
                "layout": {
                    "ncol": 2.0,
                    "nrow": 2.0,
                    "name": "grid"
                },
                "figures": [
                    ${plot(curvature = curvature, angle = angle)},
                    ${plot(curvature = curvature, angle = -angle)},
                    ${plot(curvature = -curvature, angle = angle)},
                    ${plot(curvature = -curvature, angle = -angle)}
                ]
           }""".trimIndent()

            return parsePlotSpec(spec)
        }
    }
}