/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class LineTypes {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            namedLineTypes(),
            arrayLineTypes(),
            hexLineTypes()
        )
    }

    private fun namedLineTypes(): MutableMap<String, Any> {
        val linetypes = listOf(
            "solid",
            "dashed",
            "dotted",
            "dotdash",
            "longdash",
            "twodash"
        )
        return plotSpec(linetypes)
    }

    private fun arrayLineTypes(): MutableMap<String, Any> {
        val linetypes = listOf(
            listOf(1, 1),
            listOf(5, 5),
            listOf(10, 5),
            listOf(5, listOf(10, 5)),
            listOf(5, 10, 1, 10),
            listOf(10, 5, 1, 5, 1, 5)
        )
        return plotSpec(linetypes)
    }

    private fun hexLineTypes(): MutableMap<String, Any> {
        val linetypes = listOf(
            "11",
            "55",
            "A5",
            "5A1A"
        )
        return plotSpec(linetypes)
    }

    companion object {
        private fun plotSpec(linetypes: List<Any>): MutableMap<String, Any> {
            val n = linetypes.size
            val data = mapOf(
                "x" to List(n) { 0 },
                "xend" to List(n) { 1 },
                "y" to linetypes,
                "yend" to linetypes,
                "linetype" to linetypes
            )

            val spec = """{
              "kind": "plot",
              "scales": [
                {
                  "aesthetic": "linetype",
                  "guide": "none",
                  "scale_mapper_kind": "identity",
                  "discrete": true
                }
              ],
              "layers": [
                {
                  "geom": "segment", "size":6,
                  "mapping": {
                    "x": "x",
                    "y": "y",
                    "xend": "xend",
                    "yend": "yend",
                    "linetype": "linetype"
                  },
                  "tooltips": "none"
                }
              ]
            }""".trimIndent()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data
            return plotSpec
        }
    }
}