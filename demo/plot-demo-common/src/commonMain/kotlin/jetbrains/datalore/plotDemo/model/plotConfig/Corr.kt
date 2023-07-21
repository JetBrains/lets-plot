/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec
import jetbrains.datalore.plotDemo.data.AutoMpg

class Corr {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            corrPlotWithCoef(),
            simple(),
            withThreshold(),
            complexMpg()
        )
    }

    private fun corrPlotWithCoef(): MutableMap<String, Any> {
        val spec = """
            {
              "kind": "plot",
              "ggtitle": { "text": "Precomputed coefficients" },
              "data": {
                "a": [1.0, null, 0.8717537758865838, 0.8179411262715758],
                "b": [null, 1.0, -0.42844010433053864,-0.3661259325364377],
                "c": [0.8717537758865838, -0.42844010433053864, 1.0, 0.962865431402796],
                "d": [0.8179411262715758, -0.3661259325364377, 0.962865431402796, 1.0]
              },
              "bistro": {
                "name": "corr",
                "coefficients": true,
                "point_params": {
                  "type": null,
                  "diag": null
                },
                "label_params": {
                  "type": null,
                  "diag": null
                }
              }
            }
        """.trimIndent()

        return parsePlotSpec(spec)
    }

    private fun simple(): MutableMap<String, Any> {
        val spec = """
{
  "kind": "plot",
  "ggtitle": { "text": "Simple" },
  "data": {
    "a": [ 0.4967, -0.138, 0.6476, 1.5230, -0.234, -0.234, 1.5792, 0.7674, -0.469, 0.5425],
    "b": [ -0.4634, -0.4657, 0.24196, -1.9132, -1.7249, -0.5622, -1.0128, 0.31424, -0.9080, -1.4123],
    "c": [ 1.4656, -0.225, 0.0675, -1.424, -0.544, 0.1109, -1.150, 0.3756, -0.600, -0.291],
    "d": [ -0.601, 1.8522, -0.013, -1.057, 0.8225, -1.220, 0.2088, -1.959, -1.328, 0.1968],
    "e": [ 0.738, 0.171, -0.11, -0.30, -1.47, -0.71, -0.46, 1.057, 0.343, -1.76],
    "f": [ 0.324, -0.38, -0.67, 0.611, 1.030, 0.931, -0.83, -0.30, 0.331, 0.975]
  },
  "bistro": {
      "name": "corr",
      "show_legend": true,
      "flip": false,
      "point_params": {"type": "upper", "diag": false},
      "tile_params": {"type": "lower", "diag": false},
      "label_params": {"color": "black", "map_size": true, "diag": false},
      "labels_map_size": null
  }  
}            
        """.trimIndent()

        return parsePlotSpec(spec)
    }

    private fun withThreshold(): MutableMap<String, Any> {
        val spec = """
{
  "kind": "plot",
  "ggtitle": { "text": "Thershold = 0.63" },
  "data": {
    "a": [ 0.4967, -0.138, 0.6476, 1.5230, -0.234, -0.234, 1.5792, 0.7674, -0.469, 0.5425],
    "b": [ -0.4634, -0.4657, 0.24196, -1.9132, -1.7249, -0.5622, -1.0128, 0.31424, -0.9080, -1.4123],
    "c": [ 1.4656, -0.225, 0.0675, -1.424, -0.544, 0.1109, -1.150, 0.3756, -0.600, -0.291],
    "d": [ -0.601, 1.8522, -0.013, -1.057, 0.8225, -1.220, 0.2088, -1.959, -1.328, 0.1968],
    "e": [ 0.738, 0.171, -0.11, -0.30, -1.47, -0.71, -0.46, 1.057, 0.343, -1.76],
    "f": [ 0.324, -0.38, -0.67, 0.611, 1.030, 0.931, -0.83, -0.30, 0.331, 0.975]
  },
  "bistro": {
      "name": "corr",
      "show_legend": true,
      "flip": false,
      "threshold": 0.63,
      "tile_params": {
        "type": null,
        "diag": false
      }
  }  
}            
        """.trimIndent()

        return parsePlotSpec(spec)
    }

    private fun complexMpg(): MutableMap<String, Any> {
        val spec = """
            {
              "ggsize": { "width": 400, "height": 300.0 },
              "ggtitle": { "text": "Tiles, points and labels" },
              "kind": "plot",
              "bistro": {
                  "name": "corr",
                  "show_legend": false,
                  "flip": false,
                  "point_params": {"type": "upper", "diag": false},
                  "tile_params": {"type": "lower", "diag": false},
                  "label_params": {"color": "black", "map_size": true, "diag": false},
                  "labels_map_size": null
              }
            }
        """.trimIndent()

        return parsePlotSpec(spec).apply { put("data", AutoMpg.df) }
    }
}
