/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class CustomLegend {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            shortForm(),
            parameterizedForm(),
            appendToLegend()
        )
    }

    private fun shortForm(): MutableMap<String, Any> {
        //    geom_point(..., manual_key="Red zone") + \
        //    geom_line(..., manual_key="Blue zone") + \
        //    geom_rect(..., manual_key="Green zone")
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
                    'manual_key': 'Blue zone'
                },
                {
                    'geom': 'point',
                    'x': 5,
                    'y': 0,
                    'color': 'red',
                    'size': 5,
                    'manual_key': 'Red zone'
                },
                {
                    'geom': 'rect',
                    'xmin': 2, 'xmax': 8, 'ymin': 0.2, 'ymax': 0.8,
                    'alpha': 0.2,
                    'fill': 'green',
                    'manual_key': 'Green zone'
                },
                {
                    'geom': 'label',
                    'label': 'Text',
                    'x': 8,
                    'y': 0,
                    'fill': 'orange',
                    'color': 'white',
                    'size': 8,
                    'manual_key': 'Orange zone'
                }
              ]
            }
        """.trimIndent()
        return parsePlotSpec(spec)
    }

    /// manual_key=layer_key(label="Red zone", group="Group 1", index=0, size=3)
    private fun parameterizedForm(): MutableMap<String, Any> {
        // geom_point(..., color='red', shape=21,
        //            manual_key=layer_key("Red zone", index=0, size=3)) + \
        //    geom_line(..., color='blue', linetype=2,
        //              manual_key=layer_key("Blue zone", index=2)) + \
        //    geom_rect(..., fill='green', alpha=0.2,
        //              manual_key=layer_key("Green zone", index=1, alpha=1))

        val spec = """
            {
              'kind': 'plot',
              'guides': {
                'manual': {'name': 'legend', 'ncol': 2, 'title': 'Zones' }
               },           
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
                    'manual_key': {
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
                    'manual_key': {
                        'label': 'Red zone',
                        'index': 0,
                        'size': 7,
                        'shape': 21
                    }
                },
                {
                    'geom': 'rect',
                    'xmin': 2, 'xmax': 8, 'ymin': 0.2, 'ymax': 0.8,
                    'alpha': 0.2,
                    'fill': 'green',
                    'manual_key': {
                        'label': 'Green zone',
                        'index': 1,
                        'alpha': 0.8
                    }
                },
                {
                    'geom': 'label',
                    'label': 'Text',
                    'x': 8,
                    'y': 0,
                    'fill': 'orange',
                    'color': 'white',
                    'size': 8,
                    'manual_key': {
                        'label': 'Orange zone',
                        'colour': 'black'
                    }
                }
              ]
            }
        """.trimIndent()
        return parsePlotSpec(spec)
    }

    private fun appendToLegend(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'layers': [
                {
                    'geom': 'line', 
                    'linetype': 'dotted',                    
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
                    'manual_key': {
                        'group': 'g',
                        'label': 'NA'
                    }
                },                
                {
                    'geom': 'point',
                    'x': 15,
                    'y': 44,
                    'color': 'yellow',
                    'size': 5,
                    'manual_key': {
                        'group': 'g',
                        'label': 'point'
                    }
                }
              ]
            }
        """.trimIndent()
        return parsePlotSpec(spec)
    }
}