/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.AutoMpg

class Corr {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            //bistro(),
            bistroThreshold(),
            //complexMpg()
        )
    }

    private fun bistro(): MutableMap<String, Any> {
        val spec = """
{
  "kind": "plot",
  "data": {
    "a": [ 0.4967, -0.138, 0.6476, 1.5230, -0.234, -0.234, 1.5792, 0.7674, -0.469, 0.5425],
    "b": [ -0.4634, -0.4657, 0.24196, -1.9132, -1.7249, -0.5622, -1.0128, 0.31424, -0.9080, -1.4123],
    "c": [ 1.4656, -0.225, 0.0675, -1.424, -0.544, 0.1109, -1.150, 0.3756, -0.600, -0.291],
    "d": [ -0.601, 1.8522, -0.013, -1.057, 0.8225, -1.220, 0.2088, -1.959, -1.328, 0.1968],
    "e": [ 0.738, 0.171, -0.11, -0.30, -1.47, -0.71, -0.46, 1.057, 0.343, -1.76],
    "f": [ 0.324, -0.38, -0.67, 0.611, 1.030, 0.931, -0.83, -0.30, 0.331, 0.975]
  },
  "layers": [],
  "scales": [],
  "bistro": {
      "name": "corr",
      "show_legend": true,
      "flip": false,
      "point_params": null,
      "tile_params": {
        "type": null,
        "diag": null
      },
      "label_params": null,
      "labels_map_size": null
  }  
}            
        """.trimIndent()

        return parsePlotSpec(spec)
    }

    private fun bistroThreshold(): MutableMap<String, Any> {
        val spec = """
{
  "kind": "plot",
  "data": {
    "a": [ 0.4967, -0.138, 0.6476, 1.5230, -0.234, -0.234, 1.5792, 0.7674, -0.469, 0.5425],
    "b": [ -0.4634, -0.4657, 0.24196, -1.9132, -1.7249, -0.5622, -1.0128, 0.31424, -0.9080, -1.4123],
    "c": [ 1.4656, -0.225, 0.0675, -1.424, -0.544, 0.1109, -1.150, 0.3756, -0.600, -0.291],
    "d": [ -0.601, 1.8522, -0.013, -1.057, 0.8225, -1.220, 0.2088, -1.959, -1.328, 0.1968],
    "e": [ 0.738, 0.171, -0.11, -0.30, -1.47, -0.71, -0.46, 1.057, 0.343, -1.76],
    "f": [ 0.324, -0.38, -0.67, 0.611, 1.030, 0.931, -0.83, -0.30, 0.331, 0.975]
  },
  "layers": [],
  "scales": [],
  "bistro": {
      "name": "corr",
      "show_legend": true,
      "flip": false,
      "threshold": 0.63,
      "point_params": null,
      "tile_params": {
        "type": null,
        "diag": false
      },
      "label_params": null,
      "labels_map_size": null
  }  
}            
        """.trimIndent()

        return parsePlotSpec(spec)
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
