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
            //dataInMapping()
//            fillFactor()
//            ,fillAndColorFactor()
//            ,fillFactorWithScaleColor(),
            discreteGroup()
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

    private fun discreteGroup(): Map<String, Any> {
        val spec = """
            {
              "data": $data,
              "mapping": {
                "x": "x",
                "y": "y"
              },
              "kind": "plot",
              "layers": [
                {
                  "geom": "line",
                  "mapping": {
                    "color": "g"
                  },
                  "data_meta": {
                    "series_annotation": [
                      {
                        "variable": "g",
                        "annotation": "discrete"
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

    private fun dataInMapping(): Map<String, Any> {
        val spec = """
{
    "ggtitle": { "text": "geom_point(aes(x=[0, 5, 10], y=[0, 5, 10], color=as_discrete([1, 2, 4]))"}, 
    "kind": "plot", 
    "layers": [{
        "geom": "point", 
        "mapping": {"x": [0, 5, 10], "y": [0, 5, 10], "color": [1, 2, 4]}, 
        "data_meta": {"series_annotation": [{"variable": [1, 2, 4], "annotation": "discrete"}]}, 
        "shape": 21, 
        "size": 9
    }]
}        """.trimIndent()
        return parsePlotSpec(spec)
    }

    private fun fillFactor(): Map<String, Any> {

        val spec = """
{
    "ggtitle": { "text": "... fill=as_discrete(a) ..."}, 
    "data": $data, 
    "mapping": {"x": "x", "y": "y"}, 
    "kind": "plot", 
    "layers": [{
        "geom": "point", 
        "mapping": {"fill": "a", "color": "b"}, 
        "data_meta": {"series_annotation": [{"variable": "a", "annotation": "discrete"}]},
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
        "data_meta": {"series_annotation": [{"variable": "a", "annotation": "discrete"}]},
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
        "data_meta": {"series_annotation": [
            {"variable": "a", "annotation": "discrete"}, 
            {"variable": "b", "annotation": "discrete"}
        ]},
        "shape": 21, 
        "size": 9
    }]
}        """.trimIndent()
        return parsePlotSpec(spec)
    }

}