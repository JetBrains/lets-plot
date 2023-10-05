/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class TransformBreaks {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            getSpec("identity"),
            getSpec("log10"),
            getSpec("log2"),
            getSpec("symlog"),
            getSpec("sqrt"),
            getSpec("reverse"),
        )
    }

    private fun getSpec(transform: String): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': [-6, -5, -4, -3, -2, -1, 1, 2, 3, 4, 5, 6],
                'y': [0.001, 0.004, 0.012, 0.037, 0.111, 0.333, 3, 9, 27, 81, 243, 729]
              },
              'ggtitle': {
                'text': '$transform transform'
              },
              'layers': [
                {
                  'geom': 'point'
                }
              ],
              'scales': [
                {
                  'aesthetic': 'y',
                  'trans': '$transform'
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }
}