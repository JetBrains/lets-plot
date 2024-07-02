/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demo.plot.common.data.Iris
import demoAndTestShared.parsePlotSpec

class LineAnnotations {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            example()
        )
    }

    private fun example(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'sepal length (cm)',
                'color': 'target'
              },
              'layers': [
                {
                  'geom': 'line',
                  'stat': 'density',
                  'labels': { 
                      'lines': [ '^color' ], 
                      'annotation_size': 18
                  } 
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec
    }
}