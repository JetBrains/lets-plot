/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import demo.plot.common.model.SharedPieces.sampleImageDataUrl3x3

@Suppress("DuplicatedCode")
class ImageWithScaleLimits {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
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
}