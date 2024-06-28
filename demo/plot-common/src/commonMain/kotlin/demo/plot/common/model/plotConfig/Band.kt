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
            verticalAndHorizontal(),
            polar(),
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'xmin': [-3, 1],
                'xmax': [-1, 3]
              },
              'mapping': {
                'xmin': 'xmin',
                'xmax': 'xmax'
              },
              'ggtitle': {
                'text': 'Band demo'
              },
              'layers': [
                {
                  'geom': 'band'
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }

    private fun verticalAndHorizontal(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'xmin': [-1],
                'xmax': [1],
                'ymin': [-1],
                'ymax': [1]
              },
              'mapping': {
                'xmin': 'xmin',
                'xmax': 'xmax',
                'ymin': 'ymin',
                'ymax': 'ymax'
              },
              'ggtitle': {
                'text': 'Vertical and horizontal bands together'
              },
              'layers': [
                {
                  'geom': 'band'
                }
              ],
              'coord': {
                'name': 'cartesian',
                'xlim': [-3, 3],
                'ylim': [-3, 3]
              }
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
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
              'mapping': {
                'xmin': 'xmin',
                'xmax': 'xmax',
                'ymin': 'ymin',
                'ymax': 'ymax'
              },
              'ggtitle': {
                'text': 'Polar coordinates'
              },
              'layers': [
                {
                  'geom': 'band'
                }
              ],
              'coord': {'name': 'polar', 'xlim': [-4.5, 4.5], 'ylim': [-4.5, 4.5]}
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }
}