/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.tools

import demoAndTestShared.parsePlotSpec
import kotlin.math.PI
import kotlin.math.sin

object ScatterToolsBatik {
    @JvmStatic
    fun main(args: Array<String>) {
        with(ScatterModel()) {
            SandboxViewerWithToolsBatik(
                "Scatter plot tools",
                null,
                plotSpec(),
//                    Dimension(900, 700),
            ).open()
        }
    }

    @Suppress("DuplicatedCode")
    private class ScatterModel {
        fun plotSpec(): MutableMap<String, Any> {
            val n = 50
            val step = 4 * PI / n
            val x = List(n) { it * step }
            val y = List(n) { sin(it * step) }

            val spec = """
            {
              'kind': 'plot',
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
}
