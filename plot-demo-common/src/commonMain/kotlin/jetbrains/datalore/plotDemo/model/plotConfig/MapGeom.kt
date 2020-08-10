/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

@Suppress("DuplicatedCode")

class MapGeom : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            rectDemo()
        )
    }

    private fun rectDemo(): Map<String, Any> {
        val spec = """
        {
          "data": null,
          "mapping": {
            "x": null,
            "y": null
          },
          "data_meta": {},
          "kind": "plot",
          "scales": [],
          "layers": [
            {
              "geom": "map",
              "stat": null,
              "data": {
                "geometry": [
                  "{\"type\": \"Polygon\", \"coordinates\": [[[-179.0, -77.0], [179.0, -77.0], [179.0, 77.0], [-179.0, 77.0], [-179.0, -77.0]]]}"
                ]
              },
              "mapping": {
                "x": null,
                "y": null
              },
              "position": null,
              "show_legend": false,
              "tooltips": null,
              "data_meta": {
                "geodataframe": {
                  "geometry": "geometry"
                }
              },
              "sampling": null,
              "map": null,
              "map_join": null
            }
          ]
        }
        """.trimMargin()

        return parsePlotSpec(spec)
    }
}