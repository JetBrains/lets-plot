/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import kotlin.math.pow

class TransformBreaks {
    private val xValues = (-10..10).map(Int::toDouble).toList()
    private val yValues = xValues.map { it * 10.0.pow(-5) }
    private val data = mapOf("x" to xValues, "y" to yValues)

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
                'x': 'x',
                'y': 'y'
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

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = data
        return plotSpec
    }
}