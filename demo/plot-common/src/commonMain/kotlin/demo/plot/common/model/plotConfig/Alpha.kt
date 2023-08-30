/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class Alpha {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            areaDefault(),
            areaAlphaParam(),
            areaFillRGBA(),
            areaFillAndAlpha(),

            rectDefault(),
            rectFillAndAlpha()
        )
    }

    private fun areaDefault(): MutableMap<String, Any> {
        val spec = """{
            'data': {'x': [1, 2, 2, 1]},
            'ggtitle': {'text' : 'geom_area()'},            
            'mapping': {'x': 'x'},
            'kind': 'plot',
            'layers': [
                { 'geom': 'area',
                  'stat': 'density'
                }
            ]
        }""".trimIndent()
        return parsePlotSpec(spec)
    }

    private fun areaAlphaParam(): MutableMap<String, Any> {
        val spec = """{
            'data': {'x': [1, 2, 2, 1]},
            'ggtitle': {'text' : 'geom_area(alpha=1)'},            
            'mapping': {'x': 'x'},
            'kind': 'plot',
            'layers': [
                { 'geom': 'area',
                  'stat': 'density',
                  'alpha': 1
                }
            ]
        }""".trimIndent()
        return parsePlotSpec(spec)
    }

    private fun areaFillRGBA(): MutableMap<String, Any> {
        val spec = """{
            'data': {'x': [1, 2, 2, 1]},
            'ggtitle': {'text' : 'geom_area(fill=rgba(255,0,0,1))'},            
            'mapping': {'x': 'x'},
            'kind': 'plot',
            'layers': [
                { 'geom': 'area',
                  'stat': 'density',
                  'fill': 'rgba(255,0,0,1)'
                }
            ]
        }""".trimIndent()
        return parsePlotSpec(spec)
    }


    private fun areaFillAndAlpha(): MutableMap<String, Any> {
        val spec = """{
            'data': {'x': [1, 2, 2, 1]},
            'ggtitle': {'text' : 'geom_area(alpha=0, fill=rgba(255,0,0,1))'},            
            'mapping': {'x': 'x'},
            'kind': 'plot',
            'layers': [
                { 'geom': 'area',
                  'stat': 'density',
                  'alpha': 0,
                  'fill': 'rgba(255,0,0,1)'
                }
            ]
        }""".trimIndent()
        return parsePlotSpec(spec)
    }

    private fun rectDefault(): MutableMap<String, Any> {
        val spec = """{
            'ggtitle': {'text' : 'geom_rect()'},            
            'kind': 'plot',
            'layers': [
                { 'geom': 'rect',
                  'xmin': -1, 'xmax': 1, 'ymin': -1, 'ymax': 1
                }
            ]
        }""".trimIndent()
        return parsePlotSpec(spec)
    }

    private fun rectFillAndAlpha(): MutableMap<String, Any> {
        val spec = """{
            'ggtitle': {'text' : 'geom_rect(alpha=0.2, fill=rgba(255,0,0,0.8))'},            
            'kind': 'plot',
            'layers': [
                { 'geom': 'rect',
                  'xmin': -1, 'xmax': 1, 'ymin': -1, 'ymax': 1,
                  'alpha': 0.2,
                  'fill': 'rgba(255,0,0,0.8)'
                }
            ]
        }""".trimIndent()
        return parsePlotSpec(spec)
    }
}