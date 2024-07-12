/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class Waterfall {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            checkParameters(),
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'x': ['A', 'B', 'C', 'D', 'E'],
                'y': [100, 200, -400, 500, -200]
              },
              'ggtitle': {
                'text': 'Basic waterfall demo'
              },
              'bistro': {
                'name': 'waterfall',
                'x': 'x',
                'y': 'y'
              }
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))

    }

    private fun checkParameters(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'x': ['A', 'B', 'C', 'D', 'E'],
                'y': [100, 200, -400, 500, -200]
              },
              'ggtitle': {
                'text': 'Basic waterfall demo'
              },
              'bistro': {
                'name': 'waterfall',
                'x': 'x',
                'y': 'y',
                'color': 'grey',
                'fill': 'flow_type',
                'size': 2,
                'alpha': 0.75,
                'linetype': 'longdash',
                'width': 0.35,
                'calc_total': false,
                'sorted_value': true,
                'max_values': 3
              }
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))

    }
}