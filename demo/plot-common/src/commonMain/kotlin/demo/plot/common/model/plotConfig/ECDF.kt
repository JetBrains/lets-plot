/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demo.plot.common.data.Iris
import demoAndTestShared.parsePlotSpec

class ECDF {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            withInterpolation(),
            withGrouping()
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'sepal length (cm)'
              },
              'ggtitle': {
                'text': 'Basic demo'
              },
              'layers': [
                {
                  'geom': 'step',
                  'stat': 'ecdf',
                  'pad': true
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }

    private fun withInterpolation(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'sepal length (cm)'
              },
              'ggtitle': {
                'text': 'Interpolation'
              },
              'layers': [
                {
                  'geom': 'step',
                  'stat': 'ecdf',
                  'n': 10,
                  'pad': true,
                  'direction': 'vh'
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }

    private fun withGrouping(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'sepal length (cm)',
                'color': 'target'
              },
              'ggtitle': {
                'text': 'With additional grouping'
              },
              'layers': [
                {
                  'geom': 'step',
                  'stat': 'ecdf',
                  'pad': true
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }
}