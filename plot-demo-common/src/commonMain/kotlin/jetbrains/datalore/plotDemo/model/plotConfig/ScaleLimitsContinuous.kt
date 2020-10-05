/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

open class ScaleLimitsContinuous : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            colorbarNoLims(),
            colorbarLims(),
            colorbarLowerLim(),
            colorbarUpperLim(),
            colorbarZoomInLims(),
            colorbarZoomInLowerLim(),
            colorbarZoomInUpperLim()
        )
    }


    companion object {

        fun colorbarNoLims(): Map<String, Any> {
            return getSpec("{'c':[-0.3, 0, 0.3], 'x':[0, 1, 2]}", null,"No limits")
        }

        fun colorbarLims(): Map<String, Any> {
            return getSpec("{'c':[-0.3, 0, 0.3], 'x':[0, 1, 2]}", "[-1, 1]", "Limits (fill): [-1, 1]")
        }

        fun colorbarLowerLim(): Map<String, Any> {
            return getSpec("{'c':[-0.3, 0, 0.3], 'x':[0, 1, 2]}", "[-1, null]", "Limits (fill): [-1, null]")
        }

        fun colorbarUpperLim(): Map<String, Any> {
            return getSpec("{'c':[-0.3, 0, 0.3], 'x':[0, 1, 2]}", "[null, 1]", "Limits (fill): [null, 1]")
        }

        fun colorbarZoomInLims(): Map<String, Any> {
            return getSpec("{'c':[-3, 0, 3], 'x':[0, 1, 2]}", "[-1, 1]", "Zoom-in limits (fill): [-1, 1]")
        }

        fun colorbarZoomInLowerLim(): Map<String, Any> {
            return getSpec("{'c':[-3, 0, 3], 'x':[0, 1, 2]}", "[-1, null]", "Zoom-in limits (fill): [-1, null]")
        }

        fun colorbarZoomInUpperLim(): Map<String, Any> {
            return getSpec("{'c':[-3, 0, 3], 'x':[0, 1, 2]}", "[null, 1]", "Zoom-in limits (fill): [null, 1]")
        }

        fun getSpec(data: String, limits: String?, title: String): Map<String, Any> {
            val spec = """
{
    'mapping':{}, 
    'data':$data, 
    'kind':'plot', 
    'scales':
        [
            {
                'aesthetic':'fill'
                ${if (limits != null) ", 'limits':$limits" else ""} 
            }
        ], 
    'layers':
        [
            {
                'mapping':{'x':'x', 'fill':'c'},
                'geom':'tile'
            }
        ],
    'ggtitle': {'text': '$title'}
}                
            """.trimIndent()

            return parsePlotSpec(spec)
        }
    }
}
