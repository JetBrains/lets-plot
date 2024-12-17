/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

/**
 * https://github.com/JetBrains/lets-plot/issues/679
 */
@Suppress("ClassName")
class Issue_facet_ordering_groups_679 {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            bars_no_as_discrete(),
            bars_fill_as_discrete_order(),
            bars_no_as_discrete_orientation_y()
        )
    }

    @Suppress("FunctionName")
    private fun bars_no_as_discrete(): MutableMap<String, Any> {
        val spec = """
            {'data': {'x': [0, 0, 1, 1, 0, 0, 1, 1],
              'f': ['A', 'B', 'B', 'A', 'B', 'A', 'A', 'B'],
              'g': ['G0', 'G0', 'G0', 'G0', 'G1', 'G1', 'G1', 'G1']},
             'mapping': {},
             'data_meta': {},
             'facet': {'name': 'grid', 'x': 'g', 'x_order': 1, 'y_order': 1},
             'kind': 'plot',
             'scales': [],
             'layers': [{'geom': 'bar',
               'mapping': {'x': 'x', 'fill': 'f'},
               'data_meta': {}}],
             'metainfo_list': []}
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }

    @Suppress("FunctionName")
    private fun bars_fill_as_discrete_order(): MutableMap<String, Any> {
        val spec = """
            {'data': {'x': [0, 0, 1, 1, 0, 0, 1, 1],
              'f': ['A', 'B', 'B', 'A', 'B', 'A', 'A', 'B'],
              'g': ['G0', 'G0', 'G0', 'G0', 'G1', 'G1', 'G1', 'G1']},
             'mapping': {},
             'data_meta': {},
             'facet': {'name': 'grid', 'x': 'g', 'x_order': 1, 'y_order': 1},
             'kind': 'plot',
             'scales': [],
             'layers': [{'geom': 'bar',
               'mapping': {'x': 'x', 'fill': 'f'},
               'data_meta': {'mapping_annotations': [{'aes': 'fill',
                  'annotation': 'as_discrete',
                  'parameters': {'label': 'f', 'order': 1}}]}}],
             'metainfo_list': []}         
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }

    @Suppress("FunctionName")
    private fun bars_no_as_discrete_orientation_y(): MutableMap<String, Any> {
//              'x': [0, 0, 1, 1, 0, 0, 1, 1],
        val spec = """
            {'data': {
              'x': ['L', 'L', 'R', 'R', 'L', 'L', 'R', 'R'],
              'f': ['A', 'B', 'B', 'A', 'B', 'A', 'A', 'B'],
              'g': ['G0', 'G0', 'G0', 'G0', 'G1', 'G1', 'G1', 'G1']},
             'mapping': {},
             'data_meta': {},
             'facet': {'name': 'grid', 'x': 'g', 'x_order': 1, 'y_order': 1},
             'kind': 'plot',
             'scales': [],
             'layers': [{'geom': 'bar',
               'mapping': {'y': 'x', 'fill': 'f'},
               'orientation': 'y',
               'data_meta': {}
               }],
             'metainfo_list': []}
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }

}