/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import kotlin.math.sqrt

class Hex {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basicIdentity(),
            basicWithStat()
        )
    }

    private fun basicIdentity(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'ggtitle': {
                'text': 'Basic hex plot\nstat = identity'
              },
              'data': {
                'x': [-0.5, 0.5, 0],
                'y': [0, 0, ${sqrt(3.0) / 2.0}],
                'g': ['a', 'b', 'c']
              },
              'mapping': {
                'x': 'x',
                'y': 'y',
                'fill': 'g'
              },
              'layers': [
                {
                  'geom': 'hex',
                  'stat': 'identity'
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }

    private fun basicWithStat(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'ggtitle': {
                'text': 'Basic hex plot\nstat = default'
              },
              'data': {
                'x': [-1, -1, 1, 0.95, 1.05, 0.000, 1.00, -0.5],
                'y': [-1, 1, -1, 0.95, 1.05, 0.395, 0.32, 0]
              },
              'mapping': {
                'x': 'x',
                'y': 'y'
              },
              'layers': [
                {
                  'geom': 'hex',
                  'binwidth': [1, ${sqrt(3.0) * 2.0 / 3.0}]
                },
                {
                  'geom': 'point',
                  'shape': 21,
                  'size': 4,
                  'color': 'black',
                  'fill': 'orange'
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }
}