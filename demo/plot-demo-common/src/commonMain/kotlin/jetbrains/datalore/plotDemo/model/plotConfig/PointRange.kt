/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec

@Suppress("DuplicatedCode")
class PointRange {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            drink(),
            basic(),
            adjustMidpointSize(),
            adjustMidpointAndFill()
        )
    }

    private fun drink(): MutableMap<String, Any> {
        val spec = """
            |{'kind': 'plot',
            |'data' : {'drink': ['coffee','tea','water', 'milk'],
            |           'mean': [3, 4, 6, 2],
            |           'upper': [1, 5, 7, 4],
            |           'lower': [6, 3, 2, 1]
            |         },
            | 'layers': [
            |             {
            |                'geom': 'pointrange',
            |                'mapping': {'x': 'drink', 'y': 'mean', 'ymin': 'lower', 'ymax': 'upper', 'fill': 'mean'},
            |                'fatten': 10
            |             }
            |         ]
            |}
            """.trimMargin()
        return parsePlotSpec(spec)
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = """
            |{'kind': 'plot',
            | $DATA,
            | 'mapping': {'x': 'dose', 'color': 'supp'},
            | 'layers': [
            |             {
            |                 'geom': 'pointrange',
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

    private fun adjustMidpointSize(): MutableMap<String, Any> {
        val spec = """
            |{'kind': 'plot',
            | $DATA,
            | 'mapping': {'x': 'dose', 'color': 'supp'},
            | 'layers': [
            |             {
            |                 'geom': 'pointrange',
            |                 'mapping': {
            |                              'ymin': 'min',
            |                              'ymax': 'max',
            |                              'y': 'len'
            |                            },
            |                 'fatten': 15,
            |                 'position': {'name': 'dodge', 'width': 0.33}
            |             }
            |         ]
            |}
            """.trimMargin()
        return parsePlotSpec(spec)
    }

    private fun adjustMidpointAndFill(): MutableMap<String, Any> {
        val spec = """
            |{'kind': 'plot',
            | $DATA,
            | 'mapping': {'x': 'dose', 'fill': 'supp'},
            | 'layers': [
            |             {
            |                 'geom': 'pointrange',
            |                 'mapping': {
            |                              'ymin': 'min',
            |                              'ymax': 'max',
            |                              'y': 'len'
            |                            },
            |                 'position': {'name': 'dodge', 'width': 0.33},
            |                 'size': 5,
            |                 'linewidth': 5,
            |                 'color': 'rgb(240,240,240)',
            |                 'shape': 21,
            |                 'fatten': 0.8
            |             }
            |         ]
            |}
            """.trimMargin()
        return parsePlotSpec(spec)
    }

    companion object {
        private val DATA = """
            | 'data': {'supp': ['OJ', 'OJ', 'OJ', 'VC', 'VC', 'VC'],
            |           'dose': [0.5, 1.0, 1.5, 0.5, 1.0, 1.5],
            |           'len': [13.23, 21.7, 27.06, 7.00, 17.77, 25.14],
            |           'min': [10.00, 20.8, 24.0, 5.24, 15.26, 24.35],
            |           'max': [15.00, 24.6, 28.11, 10.72, 18.28, 27.93]
            |         }
        """.trimMargin().trimIndent()
    }
}