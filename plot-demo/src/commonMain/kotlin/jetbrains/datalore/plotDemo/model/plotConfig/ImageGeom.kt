/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase
import jetbrains.datalore.plotDemo.model.SharedPieces.sampleImageDataUrl3x3

@Suppress("DuplicatedCode")
class ImageGeom : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            image_3x3()
        )
    }

    @Suppress("FunctionName")
    private fun image_3x3(): Map<String, Any> {
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
}