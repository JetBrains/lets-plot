/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import demo.plot.common.data.Iris

class Area {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            sepalLength(),
            sepalLengthCoordFixed(),
            withQuantileAes(),
            withQuantileLines(),
        )
    }

    private fun sepalLength(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'sepal length (cm)',
                'group': 'target',
                'color': 'sepal width (cm)',
                'fill': 'target'
              },
              'layers': [
                {
                  'geom': 'area',
                  'stat': 'density',
                  'position' : 'identity',
                  'alpha': 0.7
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }

    private fun sepalLengthCoordFixed(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'sepal length (cm)',
                'group': 'target',
                'color': 'sepal width (cm)',
                'fill': 'target'
              },
              'layers': [
                {
                  'geom': 'area',
                  'stat': 'density',
                  'position' : 'identity',
                  'alpha': 0.7,
                  'trim': true
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["coord"] = mapOf("name" to "fixed")
        plotSpec["data"] = Iris.df
        return plotSpec
    }

    private fun withQuantileAes(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'hwy': [29, 29, 31, 30, 26, 26, 27, 26, 25, 28, 27, 25, 25, 25, 25, 24, 25, 23, 20, 15, 20, 17, 17, 26, 23, 26, 25, 24, 19, 14, 15, 17, 27, 30, 26, 29, 26, 24, 24, 22, 22, 24, 24, 17, 22, 21, 23, 23, 19, 18, 17, 17, 19, 19, 12, 17, 15, 17, 17, 12, 17, 16, 18, 15, 16, 12, 17, 17, 16, 12, 15, 16, 17, 15, 17, 17, 18, 17, 19, 17, 19, 19, 17, 17, 17, 16, 16, 17, 15, 17, 26, 25, 26, 24, 21, 22, 23, 22, 20, 33, 32, 32, 29, 32, 34, 36, 36, 29, 26, 27, 30, 31, 26, 26, 28, 26, 29, 28, 27, 24, 24, 24, 22, 19, 20, 17, 12, 19, 18, 14, 15, 18, 18, 15, 17, 16, 18, 17, 19, 19, 17, 29, 27, 31, 32, 27, 26, 26, 25, 25, 17, 17, 20, 18, 26, 26, 27, 28, 25, 25, 24, 27, 25, 26, 23, 26, 26, 26, 26, 25, 27, 25, 27, 20, 20, 19, 17, 20, 17, 29, 27, 31, 31, 26, 26, 28, 27, 29, 31, 31, 26, 26, 27, 30, 33, 35, 37, 35, 15, 18, 20, 20, 22, 17, 19, 18, 20, 29, 26, 29, 29, 24, 44, 29, 26, 29, 29, 29, 29, 23, 24, 44, 41, 29, 26, 28, 29, 29, 29, 28, 29, 26, 26, 26],
                'drv': ['f', 'f', 'f', 'f', 'f', 'f', 'f', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', 'r', 'r', 'r', 'r', 'r', 'r', 'r', 'r', 'r', 'r', '4', '4', '4', '4', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', 'r', 'r', 'r', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', 'r', 'r', 'r', 'r', 'r', 'r', 'r', 'r', 'r', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', 'r', 'r', 'r', '4', '4', '4', '4', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', '4', '4', '4', '4', 'f', 'f', 'f', 'f', 'f', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', '4', '4', '4', '4', '4', '4', '4', '4', '4', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f']
              },
              'mapping': {
                'x': 'hwy',
                'group': 'drv'
              },
              'layers': [
                {
                  'geom': 'area',
                  'stat': 'density',
                  'position': 'identity',
                  'size': 2,
                  'alpha': 0,
                  'mapping': {
                    'color': '..quantile..'
                  }
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))

    }

    private fun withQuantileLines(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'sepal length (cm)',
                'group': 'target'
              },
              'layers': [
                {
                  'geom': 'density',
                  'color': 'black',
                  'quantiles': [0, 0.02, 0.1, 0.5, 0.9, 0.98, 1],
                  'quantile_lines': true,
                  'mapping': {
                    'fill': '..quantile..'
                  }
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }
}