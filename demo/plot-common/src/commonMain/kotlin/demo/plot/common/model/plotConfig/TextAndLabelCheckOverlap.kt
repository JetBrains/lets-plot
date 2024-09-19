/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class TextAndLabelCheckOverlap {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            checkOverlapsForLabel(false),
            checkOverlapsForLabel(true),
            checkOverlapsForText(false),
            checkOverlapsForText(true)
        )
    }

    private fun checkOverlapsForLabel(checkOverlap: Boolean): MutableMap<String, Any> {
        val spec = """{
            'kind': 'plot',
            'mapping': {
                'x': 'vehicle weight (lbs.)',
                'y':  'engine horsepower',
                'label': 'vehicle name'
            },
           'layers': [
                {
                    'geom': 'point',
                    'size': 2
                },
                {
                    'geom': 'label',
                    'hjust': 0.0,
                    'vjust': 0.0,
                    'check_overlap': $checkOverlap
                }
           ]
        }
        """.trimIndent()
        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = demo.plot.common.data.AutoMpg.df
        return plotSpec
    }

    private fun checkOverlapsForText(checkOverlap: Boolean): MutableMap<String, Any> {
        val spec = """{
            'kind': 'plot',
            'mapping': {
                'x': 'vehicle weight (lbs.)',
                'y':  'engine horsepower',
                'label': 'vehicle name'
            },
           'layers': [
                {
                    'geom': 'point',
                    'size': 2,
                    'alpha': 0.3,
                    'color': 'red'
                },
                {
                    'geom': 'text',
                    'hjust': 0,
                    'vjust': 0.5,
                    'check_overlap': $checkOverlap
                }
           ]
        }
        """.trimIndent()
        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = demo.plot.common.data.AutoMpg.df
        return plotSpec
    }
}
