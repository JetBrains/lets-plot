/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demo.plot.common.data.AutoMpg
import demoAndTestShared.parsePlotSpec

class Bracket {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            betweenGroups(),
            negativeTips(),
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'origin of car',
                'y': 'miles per gallon'
              },
              'ggtitle': {
                'text': 'Basic demo'
              },
              'layers': [
                {
                  'geom': 'boxplot'
                },
                {
                  'geom': 'bracket',
                  'mapping': {
                    'xmin': 'min',
                    'xmax': 'max',
                    'y': 'y',
                    'label': 'p'
                  },
                  'inherit_aes': false,
                  'data': {
                    'min': ['US', 'US'],
                    'max': ['Asia', 'Europe'],
                    'y': [48, 51],
                    'p': [0.01, 0.02]
                  }
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = AutoMpg.df
        return plotSpec

    }

    private fun betweenGroups(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'number of cylinders',
                'y': 'miles per gallon',
                'fill': 'origin of car'
              },
              'data_meta': {
                'mapping_annotations': [
                  {
                    'aes': 'x',
                    'annotation': 'as_discrete'
                  }
                ]
              },
              'ggtitle': {
                'text': 'Brackets between groups'
              },
              'layers': [
                {
                  'geom': 'boxplot'
                },
                {
                  'geom': 'bracket',
                  'mapping': {
                    'x': 'number of cylinders',
                    'gstart': 'gstart',
                    'gend': 'gend',
                    'y': 'y',
                    'label': 'p'
                  },
                  'inherit_aes': false,
                  'data': {
                    'number of cylinders': [6, 6, 6, 4, 4, 4],
                    'gstart': [0, 0, 1, 0, 0, 1],
                    'gend': [1, 2, 2, 1, 2, 2],
                    'y': [53, 61, 69, 53, 61, 69],
                    'p': [0.01, 0.04, 0.02, 0.05, 0.03, 0.06]
                  }
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = AutoMpg.df
        return plotSpec

    }

    private fun negativeTips(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'origin of car',
                'y': 'miles per gallon'
              },
              'ggtitle': {
                'text': 'Negative tips'
              },
              'layers': [
                {
                  'geom': 'boxplot'
                },
                {
                  'geom': 'bracket',
                  'mapping': {
                    'xmin': 'min',
                    'xmax': 'max',
                    'y': 'y',
                    'label': 'p'
                  },
                  'inherit_aes': false,
                  'tiplength_start': -0.5,
                  'tiplength_end': -0.5,
                  'tiplength_unit': 'res',
                  'vjust': 2,
                  'data': {
                    'min': ['US', 'US'],
                    'max': ['Asia', 'Europe'],
                    'y': [5, 2],
                    'p': [0.01, 0.02]
                  }
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = AutoMpg.df
        return plotSpec

    }
}