/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class Step {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            withDirection(),
            flipped(),
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'x': [1, 2, 3, 4],
                'y': [-4, -3, 3, 4]
              },
              'mapping': {
                'x': 'x',
                'y': 'y'
              },
              'ggtitle': {
                'text': 'Basic demo'
              },
              'layers': [
                {
                  'geom': 'step'
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))

    }

    private fun withDirection(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'x': [1, 2, 3, 4],
                'y': [-4, -3, 3, 4]
              },
              'mapping': {
                'x': 'x',
                'y': 'y'
              },
              'ggtitle': {
                'text': 'Changed direction'
              },
              'layers': [
                {
                  'geom': 'step',
                  'direction': 'vh'
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))

    }

    private fun flipped(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'x': [1, 2, 3, 4],
                'y': [-4, -3, 3, 4]
              },
              'mapping': {
                'x': 'x',
                'y': 'y'
              },
              'ggtitle': {
                'text': 'Flipped'
              },
              'layers': [
                {
                  'geom': 'step'
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
}