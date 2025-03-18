/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demo.plot.common.data.Iris
import demoAndTestShared.parsePlotSpec
import kotlin.collections.set

class Sina {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'target',
                'y': 'sepal length (cm)',
                'fill': 'target'
              },
              'ggtitle': {
                'text': 'Basic demo'
              },
              'layers': [
                {
                  'geom': 'violin'
                },
                {
                  'geom': 'point',
                  'size': 2,
                  'shape': 21,
                  'fill': 'white'
                },
                {
                  'geom': 'sina',
                  'size': 2,
                  'shape': 21,
                  'fill': 'orange'
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }
}