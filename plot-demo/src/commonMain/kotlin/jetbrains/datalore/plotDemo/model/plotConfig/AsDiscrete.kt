/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

class AsDiscrete : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            plotData_DiscreteGroup(),
            fillFactor(),
            fillAndColorFactor(),
            fillFactorWithScaleColor(),
            layerData_DiscreteGroup(),
            smoothStatAsDiscrete(),
            smoothStatWithGroup()
        )
    }

    private val data = """{
        |"x": [0, 5, 10, 15], 
        |"y": [0, 5, 10, 15], 
        |"a": [1, 2, 4, 6], 
        |"b": [10, 11, 12, 13], 
        |"c": ["a", "a", "b", "b"], 
        |"g": [0, 0, 1, 1]
|}""".trimMargin()

    private val smoothData = """
        |{
        |    "x": [0, 2, 5, 8, 9, 12, 16, 20, 40],
        |    "y": [3, 1, 2, 7, 8, 9, 10, 10, 10],
        |    "g": [0, 0, 0, 1, 1, 1, 2, 2, 2],
        |    "d": ['0', '0', '0', '1', '1', '1', '2', '2', '2']
        |}
    """.trimMargin()


    private fun plotData_DiscreteGroup(): Map<String, Any> {
        val spec = """
            {
              "kind": "plot",
              "data": $data,
              "mapping": {
                "x": "x",
                "y": "y",
                "color": "g"
              },
              "data_meta": {
                "mapping_annotation": [
                  {
                    "aes": "color",
                    "annotation": "as_discrete"
                  }
                ]
              },
              "layers": [
                {
                  "geom": "line",
                  "mapping": {
                  },
                  "size": 3
                }
              ]
            }
        """.trimIndent()
        return parsePlotSpec(spec)
    }

    private fun layerData_DiscreteGroup(): Map<String, Any> {
        val spec = """
            {
              "kind": "plot",
              "layers": [
                {
                  "data": $data,
                  "geom": "line",
                  "mapping": {
                    "x": "x",
                    "y": "y",
                    "color": "g"
                  },
                  "data_meta": {
                    "mapping_annotation": [
                      {
                        "aes": "color",
                        "annotation": "as_discrete"
                      }
                    ]
                  },
                  "size": 3
                }
              ]
            }
        """.trimIndent()
        return parsePlotSpec(spec)
    }

    private fun smoothStatAsDiscrete(): Map<String, Any> {
        val spec = """
            {
              "mapping": {
                "x": "x",
                "y": "y"
              },
              "kind": "plot",
              "layers": [
                {
                  "data": $smoothData,
                  "geom": "smooth",
                  "mapping": {
                    "color": "g"
                  },
                  "data_meta": {
                    "mapping_annotation": [
                      {
                        "aes": "color",
                        "annotation": "as_discrete"
                      }
                    ]
                  },
                  "se": false
                }
              ]
            }
        """.trimIndent()
        return parsePlotSpec(spec)
    }
    private fun smoothStatWithGroup(): Map<String, Any> {
        val spec = """
            {
              "data": $smoothData,
              "mapping": {
                "x": "x",
                "y": "y"
              },
              "kind": "plot",
              "layers": [
                {
                  "geom": "smooth",
                  "mapping": {
                    "color": "g",
                    "group": "g"
                  },
                  "data_meta": {
                    "mapping_annotation": [
                      {
                        "aes": "color",
                        "annotation": "as_discrete"
                      }
                    ]
                  },
                  "se": false
                }
              ]
            }
        """.trimIndent()
        return parsePlotSpec(spec)
    }


    private fun fillFactor(): Map<String, Any> {

        val spec = """
{
    "ggtitle": { "text": "... fill=as_discrete(a) ..."}, 
    "kind": "plot", 
    "layers": [{
        "geom": "point", 
        "data": $data,
        "mapping": {"x": "x", "y": "y", "fill": "a", "color": "b"}, 
        "data_meta": {"mapping_annotation": [{"aes": "fill", "annotation": "as_discrete"}]},
        "shape": 21, 
        "size": 9
    }]
}        """.trimIndent()
        return parsePlotSpec(spec)
    }


    private fun fillFactorWithScaleColor(): Map<String, Any> {

        val spec = """
{
    "ggtitle": { "text": "... fill=as_discrete(a), scale_color_discrete() ..."}, 
    "data": $data, 
    "mapping": {"x": "x", "y": "y"}, 
    "kind": "plot", 
    "scales": [{"aesthetic": "color", "discrete": true}],
    "layers": [{
        "geom": "point", 
        "mapping": {"fill": "a", "color": "b"}, 
        "data_meta": {"mapping_annotation": [{"aes": "fill", "annotation": "as_discrete"}]},
        "shape": 21, 
        "size": 9
    }]
}        """.trimIndent()
        return parsePlotSpec(spec)
    }


    private fun fillAndColorFactor(): Map<String, Any> {

        val spec = """
{
    "ggtitle": { "text": "... fill=as_discrete(a), color=as_discrete(b) ..."}, 
    "data": $data, 
    "mapping": {"x": "x", "y": "y"}, 
    "kind": "plot", 
    "layers": [{
        "geom": "point", 
        "mapping": {"fill": "a", "color": "b"}, 
        "data_meta": {"mapping_annotation": [
            {"aes": "fill", "annotation": "as_discrete"}, 
            {"aes": "color", "annotation": "as_discrete"}
        ]},
        "shape": 21, 
        "size": 9
    }]
}        """.trimIndent()
        return parsePlotSpec(spec)
    }

}