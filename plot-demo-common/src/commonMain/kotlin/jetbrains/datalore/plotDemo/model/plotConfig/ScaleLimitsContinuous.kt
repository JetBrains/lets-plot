/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec

open class ScaleLimitsContinuous {
    fun plotSpecList(): List<MutableMap<String, Any>> {
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

        fun colorbarNoLims(): MutableMap<String, Any> {
            return getSpec("{'c':[-0.3, 0, 0.3], 'x':[0, 1, 2]}", null, "No limits")
        }

        fun colorbarLims(): MutableMap<String, Any> {
            return getSpec("{'c':[-0.3, 0, 0.3], 'x':[0, 1, 2]}", "[-1, 1]", "Limits (fill): [-1, 1]")
        }

        fun colorbarLowerLim(): MutableMap<String, Any> {
            return getSpec("{'c':[-0.3, 0, 0.3], 'x':[0, 1, 2]}", "[-1, null]", "Limits (fill): [-1, null]")
        }

        fun colorbarUpperLim(): MutableMap<String, Any> {
            return getSpec("{'c':[-0.3, 0, 0.3], 'x':[0, 1, 2]}", "[null, 1]", "Limits (fill): [null, 1]")
        }

        fun colorbarZoomInLims(): MutableMap<String, Any> {
            return getSpec("{'c':[-3, 0, 3], 'x':[0, 1, 2]}", "[-1, 1]", "Zoom-in limits (fill): [-1, 1]")
        }

        fun colorbarZoomInLowerLim(): MutableMap<String, Any> {
            return getSpec("{'c':[-3, 0, 3], 'x':[0, 1, 2]}", "[-1, null]", "Zoom-in limits (fill): [-1, null]")
        }

        fun colorbarZoomInUpperLim(): MutableMap<String, Any> {
            return getSpec("{'c':[-3, 0, 3], 'x':[0, 1, 2]}", "[null, 1]", "Zoom-in limits (fill): [null, 1]")
        }

        fun getSpec(data: String, limits: String?, title: String): MutableMap<String, Any> {
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
