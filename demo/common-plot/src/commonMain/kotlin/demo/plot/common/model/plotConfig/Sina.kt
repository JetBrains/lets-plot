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
            withParameters()
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
                  'geom': 'sina'
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }

    private fun withParameters(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'x': ['a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b'],
                'y': [-2, -1, -1, 0, 0, 0, 1, 1, 2, -2, -1, -1, 0, 0, 0, 1, 1, 2]
              },
              'mapping': {
                'x': 'x',
                'y': 'y'
              },
              'ggtitle': {
                'text': 'With parameters'
              },
              'layers': [
                {
                  'geom': 'violin'
                },
                {
                  'geom': 'point',
                  'size': 2,
                  'shape': 21,
                  'fill': 'orange'
                },
                {
                  'geom': 'sina',
                  'mapping': {
                    'fill': '..quantile..'
                  },
                  'size': 2,
                  'shape': 21,
                  'quantiles': [0.5],
                  'seed': 42
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))

    }
}