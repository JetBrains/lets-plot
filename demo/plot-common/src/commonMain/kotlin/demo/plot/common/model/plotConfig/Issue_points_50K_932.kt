/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import kotlin.math.PI
import kotlin.math.sin

@Suppress("ClassName")
class Issue_points_50K_932 {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            n(50_000),
        )
    }

    private fun n(n: Int): MutableMap<String, Any> {
        val step = 4 * PI / n
        val x = MutableList(n) { "Some average text $it" }.joinToString(prefix = "[", transform = { "\"$it\"" }, separator = ", ", postfix = "]")
        val y = MutableList(n) { sin(it * step) }.joinToString(prefix = "[", separator = ", ", postfix = "]")

        val spec = """
            {
              'kind': 'plot',
              'data': {
                'x': $x,
                'y': $y
              },
              'mapping': {
                'x': 'x',
                'y': 'y'
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
