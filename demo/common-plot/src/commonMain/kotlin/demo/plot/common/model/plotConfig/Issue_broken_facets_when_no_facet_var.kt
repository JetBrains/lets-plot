/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

@Suppress("ClassName")
class Issue_broken_facets_when_no_facet_var {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            case0()
        )
    }

    private fun case0(): MutableMap<String, Any> {
        val spec = """
{'data': {'x': ['A', 'B', 'A', 'B', 'A', 'B', 'A', 'B', 'A', 'B', 'A', 'B'],
  'f': ['C', 'D', 'D', 'C', 'C', 'D', 'D', 'C', 'C', 'D', 'D', 'C'],
  'y': [101, 102, 103, 104, 105, 106, 111, 112, 113, 114, 115, 116]},
 'mapping': {'x': 'x', 'y': 'y'},
 'data_meta': {},
 'facet': {'name': 'wrap', 'facets': 'f', 'nrow': 1, 'order': 1, 'dir': 'h'},
 'kind': 'plot',
 'scales': [],
 'layers': [{'geom': 'boxplot', 'mapping': {}, 'data_meta': {}},
  {'geom': 'text',
   'data': {'x': ['B'], 'y': [120], 'l': ['AC']},
   'mapping': {'label': 'l'},
   'data_meta': {},
   'color': 'red'}]
   }        """.trimIndent()

        return parsePlotSpec(spec)
    }
}