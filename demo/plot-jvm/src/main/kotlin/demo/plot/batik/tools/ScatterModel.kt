/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.tools

import demoAndTestShared.parsePlotSpec
import kotlin.math.PI
import kotlin.math.sin

internal class ScatterModel {
    fun plotSpec(): MutableMap<String, Any> {
        val n = 50
        val step = 4 * PI / n
        val x = List(n) { it * step }
        val y = List(n) { sin(it * step) }

        val spec = """
            {
              'kind': 'plot',
              'ggsize': { 'width': 800, 'height': 500 },
              'data': {
                'x': $x,
                'y': $y
              },
              'mapping': {
                'x': 'x',
                'y': 'y',
                'color': 'y'
              },
              'layers': [
                {
                  'geom': 'point',
                  'sampling': 'none'
                }
              ]
            }
        """.trimIndent()

        return parsePlotSpec(spec)
    }
}
