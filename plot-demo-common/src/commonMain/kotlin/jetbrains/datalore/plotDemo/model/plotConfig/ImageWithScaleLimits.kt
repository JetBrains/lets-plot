/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.SharedPieces.sampleImageDataUrl3x3

@Suppress("DuplicatedCode")
class ImageWithScaleLimits {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
//            image_3x3_bbox_via_mapping(),  // "via mapping" is no longer supported.
            image_3x3("Default"),
            image_3x3("xlim [-1, 3]", "'scales': [{'aesthetic': 'x', 'limits': [-1, 3]}]"),
            image_3x3("xlim [1, 3]", "'scales': [{'aesthetic': 'x', 'limits': [1, 3]}]"),
            image_3x3(
                "xlim [0, 2] ylim [0.5, 1.5]",
                "'scales': [{'aesthetic': 'x', 'limits': [0, 2]},{'aesthetic': 'y', 'limits': [0.5, 1.5]}]"
            ),
        )
    }

    @Suppress("FunctionName")
    private fun image_3x3_bbox_via_mapping(): MutableMap<String, Any> {
        val spec = """
            |{"kind": "plot",
            | "data": {
            |           "xmin": [-0.5],
            |           "ymin": [-0.5],
            |           "xmax": [2.5],
            |           "ymax": [1.5]
            |         },
            | "layers": [
            |             {
            |                 "geom": "image",
            |                 "mapping": {
            |                              "xmin": "xmin",
            |                              "ymin": "xmin",
            |                              "xmax": "xmax",
            |                              "ymax": "ymax"
            |                            },
            |                 "href": "${sampleImageDataUrl3x3()}"
            |             }
            |         ]
            |}
            """.trimMargin()
        return parsePlotSpec(spec)
    }

    @Suppress("FunctionName")
    private fun image_3x3(title: String, moreOptions: String? = null): MutableMap<String, Any> {
        val spec = """
            |{"kind": "plot",
            | "layers": [
            |             {
            |                 "geom": "image",
            |           "xmin": -0.5,
            |           "ymin": -0.5,
            |           "xmax": 2.5,
            |           "ymax": 2.5,
            |                 "href": "${sampleImageDataUrl3x3()}"
            |             }
            |         ],
            | 'ggtitle': {'text': '$title'}
            | ${moreOptions?.let { ", $moreOptions" } ?: ""}
            |}
            """.trimMargin()
        return parsePlotSpec(spec)
    }

    private fun tmp(): MutableMap<String, Any> {
        val spec = """
{'data': {'cond': ['A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'B', 'B', 'B',
         'B', 'B', 'B', 'B', 'B', 'B', 'B'],
  'xvar': [-0.5350085359533401,
   1.2789999594224606,
   4.156327539675369,
   1.7424594690351034,
   3.6405449467440674,
   5.540040138951763,
   4.278424618044973,
   7.514239812545039,
   3.9414548625396355,
   5.142504575034982,
   10.730842434982156,
   9.42003347366922,
   14.844275708809093,
   15.63041263944261,
   21.80442426196177,
   14.784786115494253,
   15.332976215681029,
   18.598127471275177,
   18.98032073590405,
   17.621361072598052],
  'yvar': [-4.042638873614538,
   1.5672108979893835,
   0.24501640086155763,
   3.6689792284460543,
   3.011327027748805,
   5.5959316368787775,
   11.829862436875551,
   7.872664513168294,
   7.898693442511667,
   13.557878125329824,
   7.669991107988421,
   9.064824912382921,
   9.925195629109382,
   12.540164589557316,
   10.509635889212861,
   11.266172669437841,
   13.683161222668426,
   18.940618247021398,
   15.837483581685929,
   17.523892877672083],
  'xrnd': [0,
   0,
   5,
   0,
   5,
   5,
   5,
   10,
   5,
   5,
   10,
   10,
   15,
   15,
   20,
   15,
   15,
   20,
   20,
   20],
  'yrnd': [-5,
   0,
   0,
   5,
   5,
   5,
   10,
   10,
   10,
   15,
   10,
   10,
   10,
   15,
   10,
   10,
   15,
   20,
   15,
   20]},
 'mapping': {'x': 'xvar', 'y': 'yvar'},
 'kind': 'plot',
 'scales': [{'aesthetic': 'x', 'limits': [2, 16]}],
 'layers': [{'geom': 'point', 'mapping': {}, 'data_meta': {}, 'shape': 1}]
 }
      """.trimIndent()

        return parsePlotSpec(spec)
    }
}