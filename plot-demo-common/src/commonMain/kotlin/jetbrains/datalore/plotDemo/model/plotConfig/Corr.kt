/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.AutoMpg
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

class Corr : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            complexMpg()
        )
    }

    private fun complexMpg(): Map<String, Any> {
        val spec = """
            {
              "mapping": {
                "x": null,
                "y": null
              },
              "data_meta": {},
              "coord": {
                "name": "cartesian",
                "xlim": null,
                "ylim": null
              },
              "theme": {
                "axis_title": {
                  "name": "blank"
                },
                "axis_title_x": null,
                "axis_title_y": null,
                "axis_text": null,
                "axis_text_x": null,
                "axis_text_y": null,
                "axis_ticks": null,
                "axis_ticks_x": null,
                "axis_ticks_y": null,
                "axis_line": null,
                "axis_line_x": {
                  "name": "blank"
                },
                "axis_line_y": {
                  "name": "blank"
                },
                "legend_position": null,
                "legend_justification": null,
                "legend_direction": null,
                "axis_tooltip": null,
                "axis_tooltip_x": null,
                "axis_tooltip_y": null,
                "legend_title": {
                  "name": "blank"
                }
              },
              "ggsize": {
                "width": 400,
                "height": 300.0
              },
              "ggtitle": {
                "text": "Tiles, points and labels"
              },
              "kind": "plot",
              "scales": [
                {
                  "name": "",
                  "aesthetic": "size",
                  "breaks": null,
                  "labels": null,
                  "limits": null,
                  "expand": null,
                  "na_value": 0,
                  "guide": "none",
                  "trans": null,
                  "scale_mapper_kind": "identity"
                },
                {
                  "name": "Corr",
                  "aesthetic": "color",
                  "breaks": [
                    -1.0,
                    -0.5,
                    0.0,
                    0.5,
                    1.0
                  ],
                  "labels": null,
                  "limits": [
                    -1.0,
                    1.0
                  ],
                  "expand": null,
                  "na_value": "rgba(0,0,0,0)",
                  "guide": null,
                  "trans": null,
                  "low": "red",
                  "mid": "light_gray",
                  "high": "blue",
                  "midpoint": 0,
                  "scale_mapper_kind": "color_gradient2"
                },
                {
                  "name": "Corr",
                  "aesthetic": "fill",
                  "breaks": [
                    -1.0,
                    -0.5,
                    0.0,
                    0.5,
                    1.0
                  ],
                  "labels": null,
                  "limits": [
                    -1.0,
                    1.0
                  ],
                  "expand": null,
                  "na_value": "rgba(0,0,0,0)",
                  "guide": null,
                  "trans": null,
                  "low": "red",
                  "mid": "light_gray",
                  "high": "blue",
                  "midpoint": 0,
                  "scale_mapper_kind": "color_gradient2"
                },
                {
                  "aesthetic": "x",
                  "breaks": null,
                  "labels": null,
                  "limits": null,
                  "expand": [
                    0,
                    0.1
                  ],
                  "na_value": null,
                  "guide": null,
                  "trans": null,
                  "discrete": true,
                  "reverse": false
                },
                {
                  "aesthetic": "y",
                  "breaks": null,
                  "labels": null,
                  "limits": null,
                  "expand": [
                    0,
                    0.1
                  ],
                  "na_value": null,
                  "guide": null,
                  "trans": null,
                  "discrete": true,
                  "reverse": true
                }
              ],
              "layers": [
                {
                  "geom": "tile",
                  "stat": "corr",
                  "data": null,
                  "mapping": {
                    "x": null,
                    "y": null
                  },
                  "position": null,
                  "show_legend": true,
                  "sampling": "none",
                  "tooltips": {
                    "tooltip_formats": [
                      {
                        "field": "@..corr..",
                        "format": ".2f"
                      }
                    ],
                    "tooltip_lines": [
                      "@..corr.."
                    ],
                    "tooltip_anchor": null,
                    "tooltip_min_width": null
                  },
                  "data_meta": {},
                  "size": 0.0,
                  "width": 1.002,
                  "height": 1.002,
                  "type": "upper",
                  "diag": false,
                  "threshold": null
                },
                {
                  "geom": "point",
                  "stat": "corr",
                  "data": null,
                  "mapping": {
                    "x": null,
                    "y": null,
                    "size": "..corr_abs.."
                  },
                  "position": null,
                  "show_legend": true,
                  "sampling": "none",
                  "tooltips": {
                    "tooltip_formats": [
                      {
                        "field": "@..corr..",
                        "format": ".2f"
                      }
                    ],
                    "tooltip_lines": [
                      "@..corr.."
                    ],
                    "tooltip_anchor": null,
                    "tooltip_min_width": null
                  },
                  "data_meta": {},
                  "map": null,
                  "map_join": null,
                  "size_unit": "x",
                  "type": "lower",
                  "diag": false,
                  "threshold": null
                },
                {
                  "geom": "text",
                  "stat": "corr",
                  "data": null,
                  "mapping": {
                    "x": null,
                    "y": null,
                    "size": "..corr_abs.."
                  },
                  "position": null,
                  "show_legend": true,
                  "sampling": "none",
                  "tooltips": {
                    "tooltip_formats": [
                      {
                        "field": "@..corr..",
                        "format": ".2f"
                      }
                    ],
                    "tooltip_lines": [
                      "@..corr.."
                    ],
                    "tooltip_anchor": null,
                    "tooltip_min_width": null
                  },
                  "data_meta": {},
                  "map": null,
                  "map_join": null,
                  "label_format": ".2f",
                  "na_text": "",
                  "size_unit": "x",
                  "type": "lower",
                  "diag": false,
                  "color": "white",
                  "threshold": null
                }
              ]
            }
        """.trimIndent()

        return parsePlotSpec(spec).apply { put("data", AutoMpg.df) }
    }
}
