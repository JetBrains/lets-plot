/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class Arrows {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            onSegment(10.0),
            onSegment(30.0),
            onSegment(145.0),
            onSegment(315.0),
        )
    }

    private fun onSegment(angle: Double): MutableMap<String, Any> {
        val spec = """{
            'ggtitle': { 'text': 'angle=$angle' },
            'ggsize': {
                'width': 600.0,
                'height': 250.0
            },
            'kind': 'plot',
            'layers': [
                {
                  'geom': 'vline',
                  'xintercept': 0.0
                },
                {
                  'geom': 'vline',
                  'xintercept': 1.0
                },
                {
                  'geom': 'segment',
                  'arrow': {
                    'name': 'arrow',
                    'angle': $angle,
                    'length': 75.0,
                    'ends': 'both',
                    'type': 'open'
                  },
                  'x': 0.0, 'y': 0.0,
                  'xend': 1.0, 'yend': 0.0,
                  'size': 5.0,
                  'alpha': 0.3
                }
            ]
        }""".trimIndent()
        return parsePlotSpec(spec)
    }
}