/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.tools

import demoAndTestShared.parsePlotSpec
import kotlin.math.PI
import kotlin.math.sin

object ScatterToolsBrowser {
    @JvmStatic
    fun main(args: Array<String>) {
        with(ScatterModel()) {
            (PlotToolsBrowserDemoUtil.show(
                "Scatter plot tools",
                plotSpec(),
            ))
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
                'color': $y
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
