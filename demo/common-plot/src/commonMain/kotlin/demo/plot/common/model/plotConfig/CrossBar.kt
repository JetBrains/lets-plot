/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

@Suppress("DuplicatedCode")
class CrossBar {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            adjustWidth(),
            adjustMidlineAndFill(),
            undefinedY()
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = """
            |{'kind': 'plot',
            | 'data': {'supp': ['OJ', 'OJ', 'OJ', 'VC', 'VC', 'VC'],
            |           'dose': [0.5, 1.0, 1.5, 0.5, 1.0, 1.5],
            |           'len': [13.23, 21.7, 27.06, 7.00, 17.77, 25.14],
            |           'min': [10.00, 20.8, 24.0, 5.24, 15.26, 24.35],
            |           'max': [15.00, 24.6, 28.11, 10.72, 18.28, 27.93]
            |         },
            | 'mapping': {'x': 'dose', 'color': 'supp'},
            | 'layers': [
            |             {
            |                 'geom': 'crossbar',
            |                 'mapping': {
            |                              'ymin': 'min',
            |                              'ymax': 'max',
            |                              'y': 'len'
            |                            }
            |             }
            |         ]
            |}
            """.trimMargin()
        return parsePlotSpec(spec)
    }

    private fun adjustWidth(): MutableMap<String, Any> {
        val spec = """
            |{'kind': 'plot',
            | 'data': {'supp': ['OJ', 'OJ', 'OJ', 'VC', 'VC', 'VC'],
            |           'dose': [0.5, 1.0, 1.5, 0.5, 1.0, 1.5],
            |           'len': [13.23, 21.7, 27.06, 7.00, 17.77, 25.14],
            |           'min': [10.00, 20.8, 24.0, 5.24, 15.26, 24.35],
            |           'max': [15.00, 24.6, 28.11, 10.72, 18.28, 27.93]
            |         },
            | 'mapping': {'x': 'dose', 'color': 'supp'},
            | 'layers': [
            |             {
            |                 'geom': 'crossbar',
            |                 'mapping': {
            |                              'ymin': 'min',
            |                              'ymax': 'max',
            |                              'y': 'len'
            |                            },
            |                 'width': 0.3,
            |                 'position': {'name': 'dodge', 'width': 0.33}
            |             }
            |         ]
            |}
            """.trimMargin()
        return parsePlotSpec(spec)
    }

    private fun adjustMidlineAndFill(): MutableMap<String, Any> {
        val spec = """
            |{'kind': 'plot',
            | 'data': {'supp': ['OJ', 'OJ', 'OJ', 'VC', 'VC', 'VC'],
            |           'dose': [0.5, 1.0, 1.5, 0.5, 1.0, 1.5],
            |           'len': [13.23, 21.7, 27.06, 7.00, 17.77, 25.14],
            |           'min': [10.00, 20.8, 24.0, 5.24, 15.26, 24.35],
            |           'max': [15.00, 24.6, 28.11, 10.72, 18.28, 27.93]
            |         },
            | 'mapping': {'x': 'dose', 'fill': 'supp'},
            | 'layers': [
            |             {
            |                 'geom': 'crossbar',
            |                 'mapping': {
            |                              'ymin': 'min',
            |                              'ymax': 'max',
            |                              'y': 'len'
            |                            },
            |                 'width': 0.3,
            |                 'position': {'name': 'dodge', 'width': 0.33},
            |                 'size': 2,
            |                 'alpha': 0.5,
            |                 'fatten': 1.0
            |             }
            |         ]
            |}
            """.trimMargin()
        return parsePlotSpec(spec)
    }

    private fun undefinedY(): MutableMap<String, Any> {
        val spec = """{
            'data': {
                'animal': ['cow', 'mouse', 'horse'],
                'weight_l': [20, 5, 30],
                'weight_h': [27, 10, 39],
                'weight_m': [25, 7, 32]
            },
            'kind': 'plot',
            'layers': [
                {   
                    'geom': 'crossbar',
                    'tooltips': {'lines': ['Tooltip']},
                    'mapping': {
                        'x': 'animal', 'ymin': 'weight_l', 'ymax': 'weight_h'
                    }
                }
            ]
        }""".trimIndent()
        return parsePlotSpec(spec)
    }
}