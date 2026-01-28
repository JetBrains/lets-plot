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
            grouped(),
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

    private fun grouped(): MutableMap<String, Any> {
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
                'text': 'Grouping demo'
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
                    'label': 'p',
                    'color': 'g'
                  },
                  'position': {'name': 'dodgev'},
                  'data': {
                    'min': [6, 6, 6, 6, 6, 6],
                    'max': [4, 3, 4, 3, 4, 3],
                    'y': [53, 61, 53, 61, 53, 61],
                    'p': [0.01, 0.04, 0.02, 0.05, 0.03, 0.06],
                    'g': ['US', 'US', 'Asia', 'Asia', 'Europe', 'Europe']
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