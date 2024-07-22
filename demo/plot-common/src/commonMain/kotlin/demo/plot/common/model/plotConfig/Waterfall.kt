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
            withGrouping(),
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'cat': ['A', 'B', 'C', 'D', 'E'],
                'val': [100, 200, -400, 500, -200]
              },
              'ggtitle': {
                'text': 'Basic waterfall demo'
              },
              'bistro': {
                'name': 'waterfall',
                'x': 'cat',
                'y': 'val'
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
                'cat': ['A', 'B', 'C', 'D', 'E'],
                'val': [100, 200, -400, 500, -200]
              },
              'ggtitle': {
                'text': 'Try different parameters'
              },
              'bistro': {
                'name': 'waterfall',
                'x': 'cat',
                'y': 'val',
                'color': 'flow_type',
                'fill': 'lightgrey',
                'size': 3,
                'alpha': 0.75,
                'linetype': 'dotted',
                'width': 0.4,
                'show_legend': true,
                'tooltips': {
                  'title': 'Category: @xlabel (#@x)',
                  'tooltip_min_width': 200,
                  'tooltip_anchor': 'top_center',
                  'lines': ['@|@ymax', 'ymin|^ymin'],
                  'formats': [
                    {'field': '@x', 'format': 'd'},
                    {'field': '@ymax', 'format': '.3f'},
                    {'field': 'ymin', 'format': 'd'}
                  ],
                  'disable_splitting': true
                },
                'calc_total': true,
                'total_title': 'result',
                'sorted_value': true,
                'max_values': 3,
                'hline': {
                  'color': 'magenta',
                  'size': 5
                },
                'hline_ontop': false,
                'connector': {
                  'color': 'cyan',
                  'size': 1.5
                },
                'label': {
                  'color': 'flow_type',
                  'family': 'Times',
                  'face': 'bold',
                  'size': 5,
                  'angle': 45
                },
                'label_format': 'd'
              }
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))

    }

    private fun withGrouping(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'cat': ['A', 'B', 'C', 'D', 'A', 'B', 'C', 'D'],
                'val': [1.2, 2.2, -.4, 1.5, -2.0, 1.3, -0.8, 1.0],
                'group': ['p', 'p', 'p', 'p', 'q', 'q', 'q', 'q']
              },
              'ggtitle': {
                'text': 'With grouping'
              },
              'bistro': {
                'name': 'waterfall',
                'x': 'cat',
                'y': 'val',
                'group': 'group'
              }
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))

    }
}