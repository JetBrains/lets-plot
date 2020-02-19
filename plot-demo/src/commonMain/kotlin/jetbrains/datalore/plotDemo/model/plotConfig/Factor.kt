/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

class Factor : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            dataInMapping()
            ,fillFactor()
            ,fillAndColorFactor()
            ,fillFactorWithScaleColor()
        )
    }

    private val data = """{"x": [0, 5, 10], "y": [0, 5, 10], "a": [1, 2, 4], "b": [5, 6, 7]}"""
    private fun dataInMapping(): Map<String, Any> {
        val spec = """
{
    "ggtitle": { "text": "geom_point(aes(x=[0, 5, 10], y=[0, 5, 10], color=factor([1, 2, 4]))"}, 
    "kind": "plot", 
    "layers": [{
        "geom": "point", 
        "mapping": {"x": [0, 5, 10], "y": [0, 5, 10], "color": [1, 2, 4]}, 
        "data_meta": {"series_annotation": [{"variable": [1, 2, 4], "category": "discrete"}]}, 
        "shape": 21, 
        "size": 9
    }]
}        """.trimIndent()
        return parsePlotSpec(spec)
    }

    private fun fillFactor(): Map<String, Any> {

        val spec = """
{
    "ggtitle": { "text": "... fill=factor('a') ..."}, 
    "data": $data, 
    "mapping": {"x": "x", "y": "y"}, 
    "kind": "plot", 
    "layers": [{
        "geom": "point", 
        "mapping": {"fill": "a", "color": "b"}, 
        "data_meta": {"series_annotation": [{"variable": "a", "category": "discrete"}]},
        "shape": 21, 
        "size": 9
    }]
}        """.trimIndent()
        return parsePlotSpec(spec)
    }


    private fun fillFactorWithScaleColor(): Map<String, Any> {

        val spec = """
{
    "ggtitle": { "text": "... fill=factor('a'), scale_color_discrete() ..."}, 
    "data": $data, 
    "mapping": {"x": "x", "y": "y"}, 
    "kind": "plot", 
    "scales": [{"aesthetic": "color", "scale_mapper_kind": "color_hue", "discrete": True}],
    "layers": [{
        "geom": "point", 
        "mapping": {"fill": "a", "color": "b"}, 
        "data_meta": {"series_annotation": [{"variable": "a", "category": "discrete"}]},
        "shape": 21, 
        "size": 9
    }]
}        """.trimIndent()
        return parsePlotSpec(spec)
    }


    private fun fillAndColorFactor(): Map<String, Any> {

        val spec = """
{
    "ggtitle": { "text": "... fill=factor('a'), color=factor('b') ..."}, 
    "data": $data, 
    "mapping": {"x": "x", "y": "y"}, 
    "kind": "plot", 
    "layers": [{
        "geom": "point", 
        "mapping": {"fill": "a", "color": "b"}, 
        "data_meta": {"series_annotation": [
            {"variable": "a", "category": "discrete"}, 
            {"variable": "b", "category": "discrete"}
        ]},
        "shape": 21, 
        "size": 9
    }]
}        """.trimIndent()
        return parsePlotSpec(spec)
    }

}