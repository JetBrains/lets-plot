/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

open class Path {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            colored(),
            polar()
        )
    }

    companion object {

        fun polar(): MutableMap<String, Any> {
            val spec = """
                    |{
                    |  "kind": "subplots",
                    |  "layout": { "ncol": 2.0, "nrow": 1.0, "name": "grid" },
                    |  "figures": [
                    |    {
                    |      "data": {
                    |        "x": [ 1.0, 1.0, 1.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 ],
                    |        "y": [ 1.0, 5.0, 9.0, 10.0, 0.0, 9.0, 9.0, 9.0, 9.0 ],
                    |        "g": [ 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0 ],
                    |        "c": [ 1.0, 17.0, 4.0, 8.0, 3.0, 15.0, 15.0, 2.0, 9.0 ],
                    |        "s": [ 10.0, 10.0, 10.0, 8.0, 3.0, 9.0, 15.0, 12.0, 9.0 ]
                    |      },
                    |      "ggtitle": { "text": "geom_path(aes(x=x, y=y, size=s, color=c, group=g))" },
                    |      "kind": "plot",
                    |      "layers": [
                    |        {
                    |          "geom": "path",
                    |          "mapping": { "x": "x", "y": "y", "size": "s", "color": "c", "group": "g" }
                    |        }
                    |      ]
                    |    },
                    |    {
                    |      "data": {
                    |        "x": [ 1.0, 1.0, 1.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 ],
                    |        "y": [ 1.0, 5.0, 9.0, 10.0, 0.0, 9.0, 9.0, 9.0, 9.0 ],
                    |        "g": [ 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0 ],
                    |        "c": [ 1.0, 17.0, 4.0, 8.0, 3.0, 15.0, 15.0, 2.0, 9.0 ],
                    |        "s": [ 10.0, 10.0, 10.0, 8.0, 3.0, 9.0, 15.0, 12.0, 9.0 ]
                    |      },
                    |      "ggtitle": { "text": "geom_path(aes(x=x, y=y, size=s, color=c, group=g))" },
                    |      "coord": { "name": "polar" },
                    |      "kind": "plot",
                    |      "layers": [
                    |        {
                    |          "geom": "path",
                    |          "mapping": { "x": "x", "y": "y", "size": "s", "color": "c", "group": "g" }
                    |        }
                    |      ]
                    |    }
                    |  ]
                    |}              
                """.trimMargin()

            return parsePlotSpec(spec)
        }

        fun colored(): MutableMap<String, Any> {
            val spec = """
                |{
                |  "kind": "subplots",
                |  "layout": { "ncol": 5.0, "nrow": 1.0, "name": "grid" },
                |  "figures": [
                |    {
                |      "data": {
                |        "x": [ 0.0, 5.0, 10.0 ],
                |        "y": [ 0.0, 5.0, 0.0 ],
                |        "g": [ 0.0, 0.0, 0.0 ],
                |        "v": [ 1.0, 4.0, 8.0 ]
                |      },
                |      "kind": "plot",
                |      "layers": [
                |        {
                |          "geom": "path",
                |          "mapping": { "x": "x", "y": "y", "size": "v" }
                |        }
                |      ]
                |    },
                |    {
                |      "data": {
                |        "x": [ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 ],
                |        "y": [ 0.0, 5.0, 0.0, 10.0, 0.0, 5.0, 0.0, 5.0, 0.0 ],
                |        "g": [ 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0 ],
                |        "c": [ 1.0, 17.0, 4.0, 8.0, 3.0, 15.0, 15.0, 2.0, 9.0 ],
                |        "s": [ 10.0, 10.0, 10.0, 8.0, 3.0, 9.0, 15.0, 12.0, 9.0 ]
                |      },
                |      "ggtitle": { "text": "geom_path(aes(x=x, y=y)" },
                |      "kind": "plot",
                |      "layers": [
                |        {
                |          "geom": "path",
                |          "mapping": { "x": "x", "y": "y" },
                |          "size": 15.0
                |        }
                |      ]
                |    },
                |    {
                |      "data": {
                |        "x": [ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 ],
                |        "y": [ 0.0, 5.0, 0.0, 10.0, 0.0, 5.0, 0.0, 5.0, 0.0 ],
                |        "g": [ 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0 ],
                |        "c": [ 1.0, 17.0, 4.0, 8.0, 3.0, 15.0, 15.0, 2.0, 9.0 ],
                |        "s": [ 10.0, 10.0, 10.0, 8.0, 3.0, 9.0, 15.0, 12.0, 9.0 ]
                |      },
                |      "ggtitle": { "text": "geom_path(aes(x=x, y=y, color=c))" },
                |      "kind": "plot",
                |      "layers": [
                |        {
                |          "geom": "path",
                |          "mapping": { "x": "x", "y": "y", "color": "c" },
                |          "size": 15.0
                |        }
                |      ]
                |    },
                |    {
                |      "data": {
                |        "x": [ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 ],
                |        "y": [ 0.0, 5.0, 0.0, 10.0, 0.0, 5.0, 0.0, 5.0, 0.0 ],
                |        "g": [ 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0 ],
                |        "c": [ 1.0, 17.0, 4.0, 8.0, 3.0, 15.0, 15.0, 2.0, 9.0 ],
                |        "s": [ 10.0, 10.0, 10.0, 8.0, 3.0, 9.0, 15.0, 12.0, 9.0 ]
                |      },
                |      "ggtitle": { "text": "geom_path(aes(x=x, y=y, size=v))" },
                |      "kind": "plot",
                |      "scales": [
                |        {
                |          "aesthetic": "size",
                |          "guide": "none",
                |          "scale_mapper_kind": "identity"
                |        }
                |      ],
                |      "layers": [
                |        {
                |          "geom": "path",
                |          "mapping": { "x": "x", "y": "y", "size": "s" }
                |        }
                |      ]
                |    },
                |    {
                |      "data": {
                |        "x": [ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 ],
                |        "y": [ 0.0, 5.0, 0.0, 10.0, 0.0, 5.0, 0.0, 5.0, 0.0 ],
                |        "g": [ 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0 ],
                |        "c": [ 1.0, 17.0, 4.0, 8.0, 3.0, 15.0, 15.0, 2.0, 9.0 ],
                |        "s": [ 10.0, 10.0, 10.0, 8.0, 3.0, 9.0, 15.0, 12.0, 9.0 ]
                |      },
                |      "ggtitle": { "text": "geom_path(aes(x=x, y=y, size=s, color=c))" },
                |      "kind": "plot",
                |      "layers": [
                |        {
                |          "geom": "path",
                |          "mapping": { "x": "x", "y": "y", "size": "s", "color": "c" }
                |        }
                |      ]
                |    }
                |  ]
                |}
                """.trimMargin()
            return parsePlotSpec(spec)
        }
    }
}
