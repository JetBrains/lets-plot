/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import kotlin.math.pow

class TransformBreaks {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            getSpec("identity", getData()),
            getSpec("log10", getData()),
            getSpec("log2", getData()),
            getSpec("symlog", getData()),
            getSpec("sqrt", getData()),
            getSpec("reverse", getData()),
        )
    }

    private fun getData(
        x: List<Double> = (-10..10).map(Int::toDouble).toList(),
        f: (Double) -> Double = { 3.0.pow(it) },
    ): Map<String, List<Number>> = mapOf("x" to x, "y" to x.map(f))

    private fun getSpec(transform: String, data: Map<String, List<Number>>): MutableMap<String, Any> {
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