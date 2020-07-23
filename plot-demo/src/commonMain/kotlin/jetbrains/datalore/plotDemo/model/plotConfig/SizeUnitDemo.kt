/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig
import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

@Suppress("DuplicatedCode")

class SizeUnitDemo : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            corrDemo()
        )
    }

    private fun corrDemo(): Map<String, Any> {
        val spec = """
            {
              "data": {
                "var1": [
                  "Unnamed: 0",
                  "displ",
                  "year",
                  "cyl",
                  "cty",
                  "hwy",
                  "Unnamed: 0",
                  "displ",
                  "year",
                  "cyl",
                  "cty",
                  "hwy",
                  "Unnamed: 0",
                  "displ",
                  "year",
                  "cyl",
                  "cty",
                  "hwy",
                  "Unnamed: 0",
                  "displ",
                  "year",
                  "cyl",
                  "cty",
                  "hwy",
                  "Unnamed: 0",
                  "displ",
                  "year",
                  "cyl",
                  "cty",
                  "hwy",
                  "Unnamed: 0",
                  "displ",
                  "year",
                  "cyl",
                  "cty",
                  "hwy"
                ],
                "var2": [
                  "Unnamed: 0",
                  "Unnamed: 0",
                  "Unnamed: 0",
                  "Unnamed: 0",
                  "Unnamed: 0",
                  "Unnamed: 0",
                  "displ",
                  "displ",
                  "displ",
                  "displ",
                  "displ",
                  "displ",
                  "year",
                  "year",
                  "year",
                  "year",
                  "year",
                  "year",
                  "cyl",
                  "cyl",
                  "cyl",
                  "cyl",
                  "cyl",
                  "cyl",
                  "cty",
                  "cty",
                  "cty",
                  "cty",
                  "cty",
                  "cty",
                  "hwy",
                  "hwy",
                  "hwy",
                  "hwy",
                  "hwy",
                  "hwy"
                ],
                "corr": [
                  1.0,
                  -0.2653648488982064,
                  -0.060203669314255394,
                  -0.3044667933088768,
                  0.26999614479673645,
                  0.2182329322811829,
                  -0.2653648488982064,
                  1.0,
                  0.1270849550817556,
                  0.8341449801410408,
                  -0.7210828200928553,
                  -0.6536973747897007,
                  -0.060203669314255394,
                  0.1270849550817556,
                  1.0,
                  0.11192138404786521,
                  -0.0058104587951095885,
                  0.0341352865520212,
                  -0.3044667933088768,
                  0.8341449801410408,
                  0.11192138404786521,
                  1.0,
                  -0.7558617978537244,
                  -0.6734869331413247,
                  0.26999614479673645,
                  -0.7210828200928553,
                  -0.0058104587951095885,
                  -0.7558617978537244,
                  1.0,
                  0.8628045055204074,
                  0.2182329322811829,
                  -0.6536973747897007,
                  0.0341352865520212,
                  -0.6734869331413247,
                  0.8628045055204074,
                  1.0
                ],
                "abs_val": [
                  1.0,
                  0.2653648488982064,
                  0.060203669314255394,
                  0.3044667933088768,
                  0.26999614479673645,
                  0.2182329322811829,
                  0.2653648488982064,
                  1.0,
                  0.1270849550817556,
                  0.8341449801410408,
                  0.7210828200928553,
                  0.6536973747897007,
                  0.060203669314255394,
                  0.1270849550817556,
                  1.0,
                  0.11192138404786521,
                  0.0058104587951095885,
                  0.0341352865520212,
                  0.3044667933088768,
                  0.8341449801410408,
                  0.11192138404786521,
                  1.0,
                  0.7558617978537244,
                  0.6734869331413247,
                  0.26999614479673645,
                  0.7210828200928553,
                  0.0058104587951095885,
                  0.7558617978537244,
                  1.0,
                  0.8628045055204074,
                  0.2182329322811829,
                  0.6536973747897007,
                  0.0341352865520212,
                  0.6734869331413247,
                  0.8628045055204074,
                  1.0
                ]
              },
              "mapping": {
                "x": null,
                "y": null
              },
              "data_meta": {},
              "coord": {
                "name": "fixed",
                "ratio": 1.0,
                "xlim": null,
                "ylim": null
              },
              "kind": "plot",
              "scales": [
                {
                  "name": "Correlation",
                  "aesthetic": "color",
                  "breaks": null,
                  "labels": null,
                  "limits": null,
                  "expand": null,
                  "na_value": null,
                  "guide": null,
                  "trans": null,
                  "low": "dark_blue",
                  "high": "red",
                  "scale_mapper_kind": "color_gradient"
                }
              ],
              "layers": [
                {
                  "geom": "point",
                  "stat": null,
                  "data": null,
                  "mapping": {
                    "x": "var1",
                    "y": "var2",
                    "color": "corr",
                    "size": "abs_val"
                  },
                  "position": null,
                  "show_legend": null,
                  "tooltips": null,
                  "data_meta": {},
                  "sampling": null,
                  "map": null,
                  "map_join": null,
                  "animation": null,
                  "shape": 19,
                  "size_unit": "x"
                }
              ]
            }
        """.trimMargin()

        return parsePlotSpec(spec)
    }
}