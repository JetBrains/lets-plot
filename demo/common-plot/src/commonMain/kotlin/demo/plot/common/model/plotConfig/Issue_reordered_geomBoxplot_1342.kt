/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import kotlin.random.Random

/**
 * https://github.com/JetBrains/lets-plot/issues/634
 */
@Suppress("ClassName")
class Issue_reordered_geomBoxplot_1342 {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            case1(),
//            case2(),
        )
    }

    private fun case1(): MutableMap<String, Any> {
        val rand = Random(0L)

        val data = mapOf(
//            "date" to (0 until 100).map { LocalDate.of(2020, 1, 1).plusDays((it % 10).toLong()).toString() },
            "date" to (0 until 100).map { "2020-01-${(it % 10) + 1}" },
            "count" to (0 until 100).map { rand.nextInt(0, 100) }
        )

        val spec = """
        {
         'mapping': {},
         'data_meta': {'series_annotations': [{'type': 'str', 'column': 'date'},
                     {'type': 'int', 'column': 'count'}]},
         'kind': 'plot',
         'scales': [],
         'layers': [{'geom': 'boxplot',
           'mapping': {'x': 'date', 'y': 'count'},
           'data_meta': {}},
          {'geom': 'point',
           'stat': 'boxplot_outlier',
           'mapping': {'x': 'date', 'y': 'count'},
           'show_legend': false,
           'data_meta': {}}],
         'metainfo_list': []
        }        
 """.trimIndent()

        val map = parsePlotSpec(spec)
        map["data"] = data
        return map
    }
    
}