/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class Band {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            oriented("x"),
            oriented("y"),
            regular(),
            polar(),
            // TODO: coord_flip
            // TODO: coord_flip+horizontal
            // TODO: alpha+fill+stroke+color
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'ggtitle': {
                'text': 'Band demo'
              },
              'layers': [
                {
                  'geom': 'band',
                  'xmin': -1,
                  'xmax': 1
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }

    private fun oriented(orientation: String): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                '${orientation}min': [-3, 1],
                '${orientation}max': [-1, 3]
              },
              'mapping': {
                '${orientation}min': '${orientation}min',
                '${orientation}max': '${orientation}max'
              },
              'ggtitle': {
                'text': 'Band demo ($orientation-oriented)'
              },
              'layers': [
                {
                  'geom': 'band'
                }
              ]
            }
        """.trimIndent()

        return parsePlotSpec(spec)
    }

    private fun regular(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'xmin': [-4, -1, 2],
                'xmax': [-2, 1, 4],
                'ymin': [-4, -1, 2],
                'ymax': [-2, 1, 4]
              },
              'ggtitle': {
                'text': 'Polar coordinates'
              },
              'layers': [
                {
                  'geom': 'band',
                  'mapping': {
                    'ymin': 'ymin',
                    'ymax': 'ymax'
                  },
                  'size': 0,
                  'fill': 'yellow',
                  'alpha': 0.5
                },
                {
                  'geom': 'band',
                  'mapping': {
                    'xmin': 'xmin',
                    'xmax': 'xmax'
                  },
                  'size': 0,
                  'fill': 'black',
                  'alpha': 0.5
                }
              ],
              'coord': {'name': 'cartesian', 'xlim': [-4.5, 4.5], 'ylim': [-4.5, 4.5]}
            }
        """.trimIndent()

        return parsePlotSpec(spec)
    }
    private fun polar(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'xmin': [-4, -1, 2],
                'xmax': [-2, 1, 4],
                'ymin': [-4, -1, 2],
                'ymax': [-2, 1, 4]
              },
              'ggtitle': {
                'text': 'Polar coordinates'
              },
              'layers': [
                {
                  'geom': 'band',
                  'mapping': {
                    'ymin': 'ymin',
                    'ymax': 'ymax'
                  },
                  'size': 0,
                  'fill': 'yellow',
                  'alpha': 0.5
                },
                {
                  'geom': 'band',
                  'mapping': {
                    'xmin': 'xmin',
                    'xmax': 'xmax'
                  },
                  'size': 0,
                  'fill': 'black',
                  'alpha': 0.5
                }
              ],
              'coord': {'name': 'polar', 'xlim': [-4.5, 4.5], 'ylim': [-4.5, 4.5]}
            }
        """.trimIndent()

        return parsePlotSpec(spec)
    }
}