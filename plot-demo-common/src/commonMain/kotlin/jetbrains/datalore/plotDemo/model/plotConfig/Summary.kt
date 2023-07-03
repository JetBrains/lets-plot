/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.Iris

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
                  'geom': 'crossbar',
                  'mapping': {'middle': '..median..'},
                  'stat': 'summary',
                  'fun_min': 'lq',
                  'quantiles': [0.45, 0.5, 0.55]
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }
}