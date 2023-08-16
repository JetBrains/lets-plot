/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import demo.plot.common.data.Iris

class Summary {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            vsBoxplot(),
            crossbarGeom(),
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'target',
                'y': 'sepal length (cm)'
              },
              'ggtitle': {
                'text': 'Basic demo'
              },
              'layers': [
                {
                  'geom': 'pointrange',
                  'stat': 'summary'
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }

    private fun vsBoxplot(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'target',
                'y': 'sepal length (cm)'
              },
              'ggtitle': {
                'text': 'Summary vs. Boxplot'
              },
              'layers': [
                {
                  'geom': 'boxplot'
                },
                {
                  'geom': 'pointrange',
                  'stat': 'summary',
                  'fun': 'median',
                  'fun_min': 'lq',
                  'fun_max': 'uq',
                  'color': 'red'
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }

    private fun crossbarGeom(): MutableMap<String, Any> {
        // Failed with error (Internal error: IllegalStateException : Unsupported stat variable: '..y..') before
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'target',
                'y': 'sepal length (cm)'
              },
              'ggtitle': {
                'text': 'Crossbar demo'
              },
              'layers': [
                {
                  'mapping': {'fill': '..y..'},
                  'geom': 'crossbar',
                  'stat': 'summary',
                  'fun': 'median',
                  'fun_min': 'lq',
                  'quantiles': [0.45, 0.5, 0.55],
                  'tooltips': {
                    'lines': [
                      'max|^ymax',
                      'median|^y',
                      'min|^ymin'
                    ]
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