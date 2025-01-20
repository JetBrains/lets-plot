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
        val data = mapOf<String, List<*>>(
            "x" to listOf(-.5, .5, 0),
            "y" to listOf(0, 0, sqrt(3.0) / 2.0),
            "g" to listOf("a", "b", "c")
        )
        val spec = """
            {
              'kind': 'plot',
              'ggtitle': {
                'text': 'Basic hex plot\nstat = identity'
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

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = data
        return plotSpec
    }

    private fun basicWithStat(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'ggtitle': {
                'text': 'Basic hex plot\nstat = default'
              },
              'data': {
                'x': [-1, -1, 1, 1, 1, 0.000, 1.00, -0.5],
                'y': [-1, 1, -1, 1, 1, 0.395, 0.32, 0]
              },
              'mapping': {
                'x': 'x',
                'y': 'y'
              },
              'layers': [
                {
                  'geom': 'hex'
                },
                {
                  'geom': 'point',
                  'shape': 21,
                  'size': 4,
                  'color': 'black',
                  'fill': 'orange',
                  'alpha': 0.5
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }
}