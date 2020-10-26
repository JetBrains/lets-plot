/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

open class ScaleLimitsDiscrete : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            tiles(),
            tilesReversed()
        )
    }


    companion object {

        fun tiles(): Map<String, Any> {
            return getSpec("{'x':['a', 'b', 'c', 'd', 'e']}", "['b', 'c', 'e']")
        }

        fun tilesReversed(): Map<String, Any> {
            return getSpec("{'x':['a', 'b', 'c', 'd', 'e']}", "['e', 'c', 'b']")
        }


        fun getSpec(data: String, limits: String): Map<String, Any> {
            val spec = """
{
    'mapping':{}, 
    'data':$data, 
    'kind':'plot', 
    'scales':
        [
            {
                'aesthetic':'x', 
                'limits':$limits
            }
        ], 
    'layers':
        [
            {
                'mapping':{'x':'x', 'fill':'x'},
                'geom':'tile'
            }
        ]
}                
            """.trimIndent()

            return parsePlotSpec(spec)
        }
    }
}
