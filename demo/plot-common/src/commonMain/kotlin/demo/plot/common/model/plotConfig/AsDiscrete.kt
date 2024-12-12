/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import kotlin.random.Random

class AsDiscrete {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            plotData_DiscreteGroup(),
            fillFactor(),
            fillAndColorFactor(),
            fillFactorWithScaleColor(),
            layerData_DiscreteGroup(),
            smoothStatAsDiscrete(),
            smoothStatWithGroup(),
            factorLevels(),
            scatterplotWith_199_999_Groups(),
        )
    }

    @Suppress("FunctionName")
    private fun scatterplotWith_199_999_Groups(): MutableMap<String, Any> {
        val n = 199_999
        val rand = Random(12)

        val data = """{
            "x": [${List(n) { rand.nextDouble() }.joinToString()}], 
            "y": [${List(n) { rand.nextDouble() }.joinToString()}],
            "g": [${List(n) { it }.joinToString()}]
        }"""
        val spec = """
                |{
                |  "kind": "plot",
                |  "data": $data,
                |  "mapping": {
                |    "x": "x",
                |    "y": "y",
                |    "color": "g"
                |  },
                |  "data_meta": {
                |    "mapping_annotations": [
                |      {
                |        "aes": "color",
                |        "annotation": "as_discrete",
                |        "parameters": {"label": "clr"}
                |      }
                |    ]
                |  },
                |  "layers": [ { "geom": "point", "size": 8, "sampling": "none" } ]
                |}
        """.trimMargin()
        return parsePlotSpec(spec)
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
        |    "d": ["0", "0", "0", "1", "1", "1", "2", "2", "2"]
        |}
    """.trimMargin()


    private fun plotData_DiscreteGroup(): MutableMap<String, Any> {
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
                "mapping_annotations": [
                  {
                    "aes": "color",
                    "annotation": "as_discrete",
                    "parameters": {"label": "clr"}
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

    private fun layerData_DiscreteGroup(): MutableMap<String, Any> {
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
                    "mapping_annotations": [
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

    private fun smoothStatAsDiscrete(): MutableMap<String, Any> {
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
                    "mapping_annotations": [
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

    private fun smoothStatWithGroup(): MutableMap<String, Any> {
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
                    "mapping_annotations": [
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


    private fun fillFactor(): MutableMap<String, Any> {

        val spec = """
{
    "ggtitle": { "text": "... fill=as_discrete(a) ..."}, 
    "kind": "plot", 
    "layers": [{
        "geom": "point", 
        "data": $data,
        "mapping": {"x": "x", "y": "y", "fill": "a", "color": "b"}, 
        "data_meta": {"mapping_annotations": [{"aes": "fill", "annotation": "as_discrete"}]},
        "shape": 21, 
        "size": 9
    }]
}        """.trimIndent()
        return parsePlotSpec(spec)
    }


    private fun fillFactorWithScaleColor(): MutableMap<String, Any> {

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
        "data_meta": {"mapping_annotations": [{"aes": "fill", "annotation": "as_discrete"}]},
        "shape": 21, 
        "size": 9
    }]
}        """.trimIndent()
        return parsePlotSpec(spec)
    }


    private fun fillAndColorFactor(): MutableMap<String, Any> {

        val spec = """
{
    "ggtitle": { "text": "... fill=as_discrete(a), color=as_discrete(b) ..."}, 
    "data": $data, 
    "mapping": {"x": "x", "y": "y"}, 
    "kind": "plot", 
    "layers": [{
        "geom": "point", 
        "mapping": {"fill": "a", "color": "b"}, 
        "data_meta": {"mapping_annotations": [
            {"aes": "fill", "annotation": "as_discrete"}, 
            {"aes": "color", "annotation": "as_discrete"}
        ]},
        "shape": 21, 
        "size": 9
    }]
}        """.trimIndent()
        return parsePlotSpec(spec)
    }

    private fun factorLevels(): MutableMap<String, Any> {
        val spec = """{
            "data": { 
                "name" : ["c", "c", "a", "a", "d", "b", "b", "a"],
                "value": [1,   2,   3,    2,   2,   1,   4,  1]    
            },
            "kind": "plot",
            "layers": [
                { 
                    "geom": "bar",
                    "stat": "identity",
                    "mapping": {"x": "name", "y": "value", "fill": "value"},
                    "data_meta": {
                        "series_annotations": [
                            {   
                                "column": "name",
                                "factor_levels": ["a","c","b"]
                            },
                            {   
                                "column": "value",
                                "factor_levels": [1,2,3],
                                "order": -1
                            }                            
                        ]
                    }        
                }
            ]
        }""".trimIndent()
        return parsePlotSpec(spec)
    }
}