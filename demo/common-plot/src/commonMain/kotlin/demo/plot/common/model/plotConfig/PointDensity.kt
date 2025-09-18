/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demo.plot.common.data.Iris
import demoAndTestShared.parsePlotSpec
import kotlin.collections.set

class PointDensity {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            withParameters(method = "kde2d"),
            withParameters(adjust = 10.0),
            withParameters(adjust = 0.1),
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'sepal width (cm)',
                'y': 'sepal length (cm)'
              },
              'ggtitle': {
                'text': 'Basic demo'
              },
              'layers': [
                {
                  'geom': 'pointdensity'
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }

    private fun withParameters(
        adjust: Double = 1.0,
        method: String = "neighbours"
    ): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'sepal width (cm)',
                'y': 'sepal length (cm)'
              },
              'ggtitle': {
                'text': 'With parameters:\nadjust = $adjust\nmethod = $method'
              },
              'layers': [
                {
                  'geom': 'pointdensity',
                  'adjust': $adjust,
                  'method': '$method'
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }
}