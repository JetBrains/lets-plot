/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase
import kotlin.random.Random

open class ScaleBrewerWithContinuousData : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            tiles()
        )
    }


    companion object {
        fun data(n: Int): Map<String, Any> {
            val rand = Random(37)
            return mapOf(
                "x" to (0..n).toList(),
                "v" to (0..n).toList().map { rand.nextDouble(-1.0, 1.0) }
            )
        }

        private fun getSpec(): Map<String, Any> {
            val spec = """
{
    'mapping':{'x': 'x', 'fill': 'v'}, 
    'kind':'plot', 
    'scales':
        [
            {
                'aesthetic':'fill', 
                'breaks' : [-1, -0.5, 0, 0.5, 1],
                'limits': [-1, 1],
                'palette': 'PRGn',
                'scale_mapper_kind': 'color_brewer'
            }
        ], 
    'layers':
        [
            {
                'mapping':{},
                'geom':'tile',
                'tooltips': {'tooltip_formats': [], 'tooltip_lines': ['^fill']}
            }
        ]
}                
            """.trimIndent()

            return parsePlotSpec(spec)
        }

//        fun text() {
//            """
//{'scales': [{'aesthetic': 'fill', 'breaks': [-1, -0.5, 0, 0.5, 1], 'labels': None, 'limits': [-1, 1], 'expand': None, 'na_value': None, 'guide': None, 'trans': None, 'type': None, 'palette': 'PRGn', 'direction': None, 'scale_mapper_kind': 'color_brewer'}], 'layers': [{'geom': 'tile', 'stat': None, 'data': None, 'mapping': {'x': None, 'y': None}, 'position': None, 'show_legend': None, 'tooltips': {'tooltip_formats': [], 'tooltip_lines': ['^fill']}, 'data_meta': {}, 'sampling': None}]}
//
//            """.trimIndent()
//        }

        fun tiles(): Map<String, Any> {
            val spec = getSpec()
            val specWithData = HashMap<String, Any>(spec)
            specWithData["data"] = data(10)
            return specWithData
        }
    }
}
