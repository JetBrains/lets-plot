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
            basic()
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val data = mapOf<String, List<*>>(
            "x" to listOf(-.5, .5, 0),
            "y" to listOf(0, 0, sqrt(3.0) / 2.0),
            "g" to listOf("a", "b", "c")
        )
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'x',
                'y': 'y',
                'fill': 'g'
              },
              'layers': [
                {
                  'geom': 'hex'
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = data
        return plotSpec
    }
}