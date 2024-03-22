/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class SegmentAndCurve {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
           // grid(curvature = 0.9, angle = 45.0),
           // grid(curvature = 1.3, angle = 135.0),
           // example(curvature = 0.9, angle = -45.0),
            withArrow(),
            withTooltips(),
        )
    }

    companion object {
        private fun example(curvature: Double, angle: Double): MutableMap<String, Any> {
            val spec = """{
                'ggtitle': { 'text': 'curvature = $curvature, angle = $angle' },
                'kind': 'plot',
                'theme': { 'name' : 'grey' },
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

        private fun plot(curvature: Double, angle: Double, ncp: Int = 5, withTooltips: Boolean = false): String {
            val tooltips = if (withTooltips) ",\n\"tooltips\": { \"lines\": [\"Tooltip\"]}" else ""
            return """{
                "data": {
                    "x": [-30.0, 30.0],
                    "y": [-3.0,  3.0]
                },
                "mapping": {
                    "x": "x",
                    "y": "y"
                },
                "ggtitle": { "text": "curvature = $curvature, angle=$angle, ncp=$ncp" },
                "kind": "plot",
                'theme': { 'name' : 'grey' },
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
                        "ncp": $ncp,
                        "arrow": {"name": "arrow", "ends": "both", "type": "open"}
                        $tooltips
                    }
                ]
            }""".trimIndent()
        }

        private fun grid(curvature: Double, angle: Double): MutableMap<String, Any> {
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

        private fun withArrow(): MutableMap<String, Any> {
            val spec = """{
                "data": {
                    "x": [-1.0, 0.0, 1.0],
                    "y": [-1.0, 1.0, -1.0],
                    "shape": [1.0, 16.0, 21.0],
                    "size": [1.0, 2.0, 3.0],
                    "stroke": [1.0, 0.0, 2.0],
                    "x_end": [0.0, 1.0, -1.0],
                    "y_end": [1.0, -1.0, -1.0],
                    "size_end": [2.0, 3.0, 1.0],
                    "stroke_end": [0.0, 2.0, 1.0]
                },
                "mapping": {
                    "x": "x",
                    "y": "y"
                },
                "theme": {"name": "classic"},
                "kind": "plot",
                "scales": [
                    {
                        "aesthetic": "size",
                        "guide": "none",
                        "range": [20.0, 30.0]
                    },
                    {
                        "aesthetic": "stroke",
                        "guide": "none",
                        "range": [0.0, 10.0]
                    },
                    {
                        "aesthetic": "shape",
                        "guide": "none",
                        "scale_mapper_kind": "identity",
                        "discrete": true
                    },
                    {
                        "aesthetic": "x",
                        "limits": [-1.5, 1.5]
                    },
                    {
                        "aesthetic": "y",
                        "limits": [-1.5, 1.5]
                    }
                ],
                "layers": [
                    {
                        "geom": "point",
                        "mapping": {
                            "size": "size",
                            "shape": "shape",
                            "stroke": "stroke"
                        },
                        "color": "#4575b4",
                        "fill": "#abd9e9"
                    },
                    {
                        "geom": "segment",
                        "mapping": {
                            "xend": "x_end",
                            "yend": "y_end",
                            "size_start": "size",
                            "size_end": "size_end",
                            "stroke_start": "stroke",
                            "stroke_end": "stroke_end"
                        },
                        "size": 1.2,
                        "alpha": 0.8,
                        "spacer": 0.0,
                        "arrow": {
                            "name": "arrow",
                            "angle": 15.0,
                            "length": 24.0,
                            "ends": "both",
                            "type": "open"
                        },
                        "tooltips": "none"
                    },
                    {
                        "geom": "curve",
                        "mapping": {
                            "xend": "x_end",
                            "yend": "y_end",
                            "size_start": "size",
                            "size_end": "size_end",
                            "stroke_start": "stroke",
                            "stroke_end": "stroke_end"
                        },
                        "size": 1.2,
                        "alpha": 0.8,
                        "spacer": 0.0,
                        "arrow": {
                            "name": "arrow",
                            "angle": 15.0,
                            "length": 24.0,
                            "ends": "both",
                            "type": "open"
                        },
                        "curvature": -0.2,
                        "angle": 90
                    }
                ]
            }""".trimIndent()

            return parsePlotSpec(spec)
        }

        private fun withTooltips(): MutableMap<String, Any> {
            val spec = """{
                "kind": "subplots",
                "layout": {
                    "ncol": 2.0,
                    "nrow": 2.0,
                    "name": "grid"
                },
                "figures": [
                    ${plot(0.9, 45.0, ncp = 1, withTooltips = true)},
                    ${plot(0.9, 45.0, ncp = 2, withTooltips = true)},
                    ${plot(0.9, 45.0, ncp = 5, withTooltips = true)},
                    ${plot(0.9, 45.0, ncp = 7, withTooltips = true)}
                ]
           }""".trimIndent()

            return parsePlotSpec(spec)
        }
    }
}