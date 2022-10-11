/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec

class TextAndLabel {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            textWithAngle("text"),
            textWithAngle("text", angle = 45.0),
            textWithAngle("text", angle = 90.0),

            textWithAngle("label"),
            textWithAngle("label", angle = 45.0),
            textWithAngle("label", angle = 90.0),
        )
    }

    private fun textWithAngle(geom: String, angle: Double = 0.0): MutableMap<String, Any> {
        val xList = listOf(
            0.0, 0.0, 0.0,
            0.5, 0.5, 0.5,
            1.0, 1.0, 1.0
        )
        val yList = listOf(
            0.0, 0.5, 1.0,
            0.0, 0.5, 1.0,
            0.0, 0.5, 1.0
        )
        val hjustMap = mapOf(0.0 to "left", 0.5 to "center", 1.0 to "right")
        val vjustMap = mapOf(0.0 to "bottom", 0.5 to "middle", 1.0 to "top")
        val hjustNames = xList.map { hjustMap[it] }
        val vjustNames = yList.map { vjustMap[it] }

        val data = mapOf(
            "x" to xList,
            "y" to yList,
            "hjust" to hjustNames,
            "vjust" to vjustNames,
            "label" to hjustNames.zip(vjustNames).map { "${it.first}\n${it.second}" } //multiline
        )

        val spec = """{
            "mapping": {
                "x": "x",
                "y": "y"
            },
            "theme": { "name": "classic" },
            "kind": "plot",
            "scales": [
                {
                    "aesthetic": "x",
                    "breaks": [0,0.5,1], "expand": [0.2]
                },
                {
                    "aesthetic": "y",
                    "breaks": [0,0.5,1], "expand": [0.0, 0.5]
                }
            ],
            "layers": [
                { 
                    "geom" : "point",
                    "size" : 3
                },
                {
                    "geom": "$geom",
                    "size": 10,
                    "fill": "light_green",
                    "family":"Arial",
                    "angle": $angle,
                    "mapping": {
                       "hjust": "hjust",
                       "vjust": "vjust",
                       "label": "label"
                    }
                }
            ]        
        }""".trimIndent()
        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = data
        return plotSpec
    }
}