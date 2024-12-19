/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demo.plot.common.data.Iris
import demoAndTestShared.parsePlotSpec

class AreaRidges {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            withGroups(),
            flipCoord(),
            withNegativeHeight(),
            withStat(),
            quantiles(),
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data' : {
                'x': [null, 0, 1, 2, 3, 4, 1, 2, 3, 4],
                'y': [0, 0, 0, 0, 0, null, 1, 1, 1, 1],
                'h': [0.5, 0.3, 1.0, 0.4, 0.6, 0.5, 0.3, 0.4, 0.2, 0.3]
              },
              'mapping': {
                'x': 'x',
                'y': 'y',
                'height': 'h'
              },
              'ggtitle': {
                'text': 'Basic demo'
              },
              'layers': [
                {
                  'geom': 'area_ridges',
                  'stat': 'identity'
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))

    }

    private fun withGroups(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data' : {
                'x': [0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3],
                'y': [0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2],
                'h': [0.1, 0.2, 0.1, 0.1, 0.1, 0.1, 0.2, 0.1, 0.1, 0.1, 0.2, 0.1],
                'g': ['A', 'A', 'B', 'B', 'C', 'C', 'A', 'A', 'B', 'B', 'C', 'C']
              },
              'mapping': {
                'x': 'x',
                'y': 'y',
                'height': 'h',
                'fill': 'g'
              },
              'ggtitle': {
                'text': 'Additional grouping'
              },
              'layers': [
                {
                  'geom': 'area_ridges',
                  'stat': 'identity',
                  'color': 'black'
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))

    }

    private fun flipCoord(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data' : {
                'x': [0, 1, 2, 3, 1, 2, 3, 4],
                'y': [0, 0, 0, 0, 0.5, 0.5, 0.5, 0.5],
                'h': [0.6, 2.0, 0.8, 1.2, 0.6, 0.8, 0.4, 0.6]
              },
              'mapping': {
                'x': 'x',
                'y': 'y',
                'height': 'h'
              },
              'ggtitle': {
                'text': 'Flip coordinates'
              },
              'layers': [
                {
                  'geom': 'area_ridges',
                  'stat': 'identity',
                  'scale': 0.5
                }
              ],
              'coord': {
                'name': 'flip',
                'flip': true
              }
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))

    }

    private fun withNegativeHeight(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data' : {
                'x': [0, 1, 2, 3, 4, 0, 1, 2, 3, 4],
                'y': [-1.0, -1.0, -1.0, -1.0, -1.0, -0.5, -0.5, -0.5, -0.5, -0.5],
                'h': [1.0, -0.5, 0.5, -1.0, 1.0, 1.0, -1.0, 0.5, -0.5, 1.0]
              },
              'mapping': {
                'x': 'x',
                'y': 'y',
                'height': 'h'
              },
              'ggtitle': {
                'text': 'Negative height'
              },
              'layers': [
                {
                  'geom': 'area_ridges',
                  'min_height': -0.9,
                  'stat': 'identity',
                  'color': 'black',
                  'fill': 'red'
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))

    }

    private fun withStat(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data' : {
                'x': [1, 2, 0, 1, 2, 3],
                'y': [1.2, 1.2, 1.1, 1.1, 1.4, 1.4]
              },
              'mapping': {
                'x': 'x',
                'y': 'y'
              },
              'ggtitle': {
                'text': 'With density ridges stat'
              },
              'layers': [
                {
                  'geom': 'area_ridges',
                  'scale': 2,
                  'tails_cutoff': 3
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))

    }

    private fun quantiles(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'sepal length (cm)',
                'y': 'target',
                'fill': '..quantile..'
              },
              'ggtitle': {
                'text': 'Quantiles'
              },
              'layers': [
                {
                  'geom': 'area_ridges',
                  'quantiles': [0, 0.1, 0.25, 0.5, 0.75, 0.9, 1],
                  'quantile_lines': true,
                  'color': 'black'
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }
}