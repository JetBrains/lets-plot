/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class CustomLegend {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            customLegend(),
            appendToLegend()
        )
    }

    private fun customLegend(): MutableMap<String, Any> {
        //geom_point(legend_item=dict(legend="custom_key", label="Red zone"), shape=21, color="red" ) +
        //geom_line(legend_item=dict(legend="custom_key", label="Blue zone"), linetype="dotted", color="blue" ) +
        val spec = """
            {
              'kind': 'plot',
              'layers': [
                {
                    'geom': 'line', 
                    'data': {
                       'x': [0, 10], 
                       'y': [1, 1]
                    },
                    'mapping': {
                       'x': 'x', 
                       'y': 'y'
                    },
                    'color': 'blue',
                    'size': 1.2,
                    'linetype': 'dotted',
                    'legend_item': {
                        'label': 'Blue zone',
                        'index': 2
                    }
                },                
                {
                    'geom': 'point',
                    'x': 5,
                    'y': 0,
                    'color': 'red',
                    'size': 5,
                    'legend_item': {
                        'label': 'Red zone',
                        'index': 0
                    }
                },                
                {
                    'geom': 'rect',
                    'xmin': 2, 'xmax': 8, 'ymin': 0.2, 'ymax': 0.8,
                    'alpha': 0.2,
                    'fill': 'green',
                    'legend_item': {
                        'label': 'Green zone',
                        'index': 1
                    }
                }               
              ]
            }
        """.trimIndent()
        //  + settings:
        //              'scales': [ {'name': 'Zones', 'key': 'custom_key' } ],
        //              'guides': {'custom_key': {'name': 'legend', 'ncol': 2}}
        return parsePlotSpec(spec)
    }

    private fun appendToLegend(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'layers': [
                {
                    'geom': 'line', 
                    'data': {
                        'x': [10, 15, 10, 15], 
                        'y': [42, 48, 50, 50], 
                        'g': ['a', 'a', 'b', 'b']
                    },
                    'mapping': {
                        'x': 'x', 
                        'y': 'y', 
                        'color': 'g'
                    },
                    'legend_item': {
                        'legend': 'color',
                        'label': 'NA'
                    }
                },                
                {
                    'geom': 'point',
                    'x': 15,
                    'y': 44,
                    'color': 'yellow',
                    'size': 5,
                    'legend_item': {
                        'legend': 'color',
                        'label': 'point'
                    }
                }
              ],
              'scales': [ {'name': 'Zones', 'aesthetic': 'color' } ],
              'guides': {'color': {'name': 'legend', 'ncol': 2}}
            }
        """.trimIndent()
        return parsePlotSpec(spec)
    }
}