/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class Spoke {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'x': [0, 1, 1, 0],
                'y': [0, 0, 1, 1],
                'angle': [4.7124, 0, 1.5708, 3.1416],
                'radius': [0.2, 0.4, 0.6, 0.8]
              },
              'mapping': {
                'x': 'x',
                'y': 'y',
                'angle': 'angle',
                'radius': 'radius'
              },
              'coord': {
                'name': 'fixed',
                'ratio': 1.0,
                'flip': false
              },
              'layers': [
                {
                  'geom': 'spoke',
                  'linewidth': 5,
                  'color': 'blue',
                  'alpha': 0.5
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }
}