/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec

/**
 * https://github.com/JetBrains/lets-plot/issues/634
 */
@Suppress("ClassName")
class Issue_gradientn_634 {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            case1(),
            case2(),
        )
    }

    private fun case1(): MutableMap<String, Any> {
        // The "fill" shouldn't be black

        val spec = """
            {
                'mapping' : {}, 
                'data' : {
                    "x" : ["first", "first", "second", "third", "third"], 
                    "y" : [18, 21, 21, 18, 21], 
                    "z" : [3.5264934876244764E-7, 0.0974772170888232, 0.1116666955828674, 2.58687631451427E-5, 0.07896070396295593]}, 
                    "kind" : "plot", 
                    "scales" : [
                        {
                            "aesthetic" : "fill", 
                            "scale_mapper_kind" : "color_gradientn", 
                            "na_value" : "#000000", 
                            "colors" : [
                                "#DFFADC", "#C9F5D3", "#B3F2CF", "#9AEBCD", "#80DCCC", 
                                "#6DC8D2", "#61B7DB", "#5C97DB", "#5A7CD6", "#6060C7", 
                                "#674BB3", "#693799", "#6A277B", "#671D60", "#611347"]
                        }], 
                    "layers" : [
                        {
                            "mapping" : {"x" : "x", "y" : "y", "fill" : "z"}, 
                            "geom" : "tile"
                        }]
            }            
        """.trimIndent()

        return parsePlotSpec(spec)
    }
    
    private fun case2(): MutableMap<String, Any> {
        // The "fill" shouldn't be black

        val spec = """
            {
                'mapping' : {}, 
                'data' : {
                    'x' : ['first', 'first', 'second', 'third', 'third'], 
                    'y' : [18, 21, 21, 18, 21], 
                    'z' : [ 3.5264934876244764E-7,
                            0.0974772170888232,
                            0.1116666955828674,
                            2.58687631451427E-5,
                            0.5]}, 
                    'kind' : 'plot', 
                    'scales' : [
                        {
                            'aesthetic' : 'fill', 
                            'scale_mapper_kind' : 'color_gradientn', 
                            'na_value' : '#000000', 
                            'trans' : 'log10',
                            'colors' : [
                                '#DFFADC', '#C9F5D3', '#B3F2CF', '#9AEBCD', '#80DCCC', 
                                '#6DC8D2', '#61B7DB', '#5C97DB', '#5A7CD6', '#6060C7', 
                                '#674BB3', '#693799', '#6A277B', '#671D60', '#611347']
                        }], 
                    'layers' : [
                        {
                            'mapping' : {'x' : 'x', 'y' : 'y', 'fill' : 'z'}, 
                            'geom' : 'tile'
                        }]
            }            
        """.trimIndent()

        return parsePlotSpec(spec)
    }
}