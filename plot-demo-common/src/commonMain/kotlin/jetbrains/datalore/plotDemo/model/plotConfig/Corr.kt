/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.AutoMpg
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

class Corr : PlotConfigDemoBase() {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            complexMpg()
        )
    }

    private fun complexMpg(): MutableMap<String, Any> {
        val spec = """
            {
              "coord": { "name": "cartesian" },
              "theme": {
                "axis_title": { "name": "blank" },
                "axis_line_x": { "name": "blank" },
                "axis_line_y": { "name": "blank" },
                "legend_title": { "name": "blank" }
              },
              "ggsize": { "width": 400, "height": 300.0 },
              "ggtitle": { "text": "Tiles, points and labels" },
              "kind": "plot",
              "scales": [
                {
                  "name": "",
                  "aesthetic": "size",
                  "na_value": 0,
                  "guide": "none",
                  "scale_mapper_kind": "identity"
                },
                {
                  "name": "Corr",
                  "aesthetic": "color",
                  "breaks": [-1.0, -0.5, 0.0, 0.5, 1.0],
                  "limits": [-1.0, 1.0],
                  "na_value": "rgba(0,0,0,0)",
                  "low": "red",
                  "mid": "light_gray",
                  "high": "blue",
                  "midpoint": 0,
                  "scale_mapper_kind": "color_gradient2"
                },
                {
                  "name": "Corr",
                  "aesthetic": "fill",
                  "breaks": [-1.0, -0.5, 0.0, 0.5, 1.0],
                  "limits": [-1.0, 1.0],
                  "na_value": "rgba(0,0,0,0)",
                  "low": "red",
                  "mid": "light_gray",
                  "high": "blue",
                  "midpoint": 0,
                  "scale_mapper_kind": "color_gradient2"
                },
                {
                  "aesthetic": "x",
                  "expand": [0, 0.1],
                  "discrete": true,
                  "reverse": false
                },
                {
                  "aesthetic": "y",
                  "expand": [0, 0.1],
                  "discrete": true,
                  "reverse": true
                }
              ],
              "layers": [
                {
                  "geom": "tile",
                  "stat": "corr",
                  "show_legend": true,
                  "sampling": "none",
                  "tooltips": {
                    "tooltip_formats": [
                      {
                        "field": "@..corr..",
                        "format": ".2f"
                      }
                    ],
                    "tooltip_lines": ["@..corr.."]
                  },
                  "data_meta": {},
                  "size": 0.0,
                  "width": 1.002,
                  "height": 1.002,
                  "type": "upper",
                  "diag": false
                },
                {
                  "geom": "point",
                  "stat": "corr",
                  "mapping": { "size": "..corr_abs.." },
                  "show_legend": true,
                  "sampling": "none",
                  "tooltips": {
                    "tooltip_formats": [
                      {
                        "field": "@..corr..",
                        "format": ".2f"
                      }
                    ],
                    "tooltip_lines": ["@..corr.."]
                  },
                  "data_meta": {},
                  "size_unit": "x",
                  "type": "lower",
                  "diag": false
                },
                {
                  "geom": "text",
                  "stat": "corr",
                  "mapping": { "size": "..corr_abs.." },
                  "show_legend": true,
                  "sampling": "none",
                  "tooltips": {
                    "tooltip_formats": [
                      {
                        "field": "@..corr..",
                        "format": ".2f"
                      }
                    ],
                    "tooltip_lines": ["@..corr.."]
                  },
                  "data_meta": {},
                  "label_format": ".2f",
                  "na_text": "",
                  "size_unit": "x",
                  "type": "lower",
                  "diag": false,
                  "color": "white"
                }
              ]
            }
        """.trimIndent()

        return parsePlotSpec(spec).apply { put("data", AutoMpg.df) }
    }
}
