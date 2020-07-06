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
            tiles()
        )
    }


    companion object {

        fun tiles(): Map<String, Any> {
            val spec = """
{
    'mapping':{}, 
    'data':{'x':['a', 'b', 'c', 'd', 'e']}, 
    'kind':'plot', 
    'scales':
        [
            {
                'aesthetic':'x', 
                'limits':['b', 'c', 'e']
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
