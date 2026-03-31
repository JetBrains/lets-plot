/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demo.plot.common.data.Iris
import demoAndTestShared.parsePlotSpec
import kotlin.collections.set

class PointDensity {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            basic("kde2d"),
        )
    }

    private fun basic(method: String = "neighbours"): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'sepal width (cm)',
                'y': 'sepal length (cm)'
              },
              'ggtitle': {
                'text': 'Basic pointdensity\nmethod: $method'
              },
              'layers': [
                {
                  'geom': 'pointdensity',
                  'method': '$method',
                  'tooltips': {
                    'lines': [
                      '@target',
                      'count|@..count..',
                      'density|@..density..',
                      'scaled|@..scaled..'
                    ]
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