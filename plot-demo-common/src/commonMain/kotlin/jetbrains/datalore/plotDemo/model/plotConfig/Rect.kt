/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec

class Rect {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            poly()
        )
    }

    companion object {
        val p = """{\"type\": \"Point\", \"coordinates\": [-5.0, 17.0]}"""
        val poly1 =
            """{\"type\": \"MultiPolygon\", \"coordinates\": [[[[0.0, 0.0], [20.0, 0.0], [20.0, 20.0], [0.0, 0.0]]]]}"""
        val poly2 =
            """{\"type\": \"MultiPolygon\", \"coordinates\": [[[[2.0, 2.0], [15.0, 2.0], [18.0, 25.0], [2.0, 2.0]]]]}"""

        fun basic(): MutableMap<String, Any> {
            val spec = """
                |{
                |    "kind": "plot",
                |    "layers": [{
                |        "geom": "rect",
                |        "tooltips": { "lines": [ "@id"] },
                |        "mapping": {"fill": "id"},
                |        "data_meta": {"geodataframe": {"geometry": "coord"}},
                |        "data": {
                |            "id": ["AAA", "BB", "C"],
                |            "coord": [
                |                "$p",
                |                "$poly1",
                |                "$poly2"
                |            ]
                |        }
                |    }]
                |}
            """.trimMargin()

            return parsePlotSpec(spec)
        }

        fun poly(): MutableMap<String, Any> {
            val spec = """
                |{
                |    "kind": "plot",
                |    "layers": [{
                |        "geom": "polygon",
                |        "tooltips": { "lines": [ "@id"] },
                |        "mapping": {"fill": "id"},
                |        "data_meta": {"geodataframe": {"geometry": "coord"}},
                |        "data": {
                |            "id": ["AAA", "BB", "C"],
                |            "coord": [
                |                "$p",
                |                "$poly1",
                |                "$poly2"
                |            ]
                |        }
                |    }]
                |}
            """.trimMargin()

            return parsePlotSpec(spec)
        }
    }
}