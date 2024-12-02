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
            withMeasure(),
            withDataMeta(),
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
                'relative_tooltips': {
                  'title': 'Category: @..xlabel.. (#@..x..)',
                  'tooltip_min_width': 200,
                  'tooltip_anchor': 'top_center',
                  'lines': ['ymax|@..ymax..', 'ymin|^ymin'],
                  'formats': [
                    {'field': '@..x..', 'format': 'd'},
                    {'field': '@..ymax..', 'format': '.3f'},
                    {'field': 'ymin', 'format': 'd'}
                  ],
                  'disable_splitting': true
                },
                'calc_total': false,
                'total_title': 'result',
                'sorted_value': true,
                'max_values': 3,
                'base': -100,
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
                'cat': ['A', 'B', 'C', 'A', 'B', 'C'],
                'val': [2, 3, -1, 1, -2, 4],
                'g': ['one', 'one', 'one', 'two', 'two', 'two']
              },
              'ggtitle': {
                'text': 'With grouping'
              },
              'bistro': {
                'name': 'waterfall',
                'x': 'cat',
                'y': 'val',
                'group': 'g'
              },
              'facet': {
                'name': 'wrap',
                'facets': 'g',
                'scales': 'free_x'
              }
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))

    }

    private fun withMeasure(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'cat': ['A', 'B', 'C', 'D', 'T1', 'A', 'B', 'C', null, 'E', 'T2'],
                'val': [1.2, 2.2, -0.4, 1.5, null, -2.0, 1.3, -0.8, 1.0, 1.0, 0.0],
                'm': ['absolute', 'relative', 'relative', 'relative', 'total', 'relative', 'relative', 'relative', 'relative', null, 'total']
              },
              'ggtitle': {
                'text': 'With measure'
              },
              'bistro': {
                'name': 'waterfall',
                'x': 'cat',
                'y': 'val',
                'measure': 'm',
                'show_legend': true
              }
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))

    }

    private fun withDataMeta(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'cat': ['A', 'B', 'C', 'D', 'E'],
                'val': [100000, 200000, -400000, 500000, -200000]
              },
              'data_meta': {
                'series_annotations': [
                  {'type': 'str', 'column': 'cat'},
                  {'type': 'float', 'column': 'val'}
                ]
              },
              'ggtitle': {
                'text': 'With specified data_meta'
              },
              'bistro': {
                'name': 'waterfall',
                'x': 'cat',
                'y': 'val'
              },
              'scales': [
                {
                  'aesthetic': 'y',
                  'breaks': [-100000.0, 150000.0, 400000.0]
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))

    }
}