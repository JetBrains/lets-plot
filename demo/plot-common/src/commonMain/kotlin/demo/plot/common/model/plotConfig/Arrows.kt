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

            withTargetSizes(),
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
                    'type': 'closed'
                  },
                  'x': 0.0, 'y': 0.0,
                  'xend': 1.0, 'yend': 0.0,
                  'size': 4.0,
                  'alpha': 0.3
                }
            ]
        }""".trimIndent()
        return parsePlotSpec(spec)
    }


    private fun withTargetSizes(): MutableMap<String, Any> {
        val spec = """{
            'data': {
                'x': [-1.0, 0.0, 1.0],
                'y': [-1.0, 1.0, -1.0],
                'shape': [1.0, 16.0, 21.0],
                'size': [1.0, 2.0, 3.0],
                'stroke': [1.0, 0.0, 2.0],
                'x_end': [0.0, 1.0, -1.0],
                'y_end': [1.0, -1.0, -1.0],
                'size_end': [2.0, 3.0, 1.0],
                'stroke_end': [0.0, 2.0, 1.0]
            },
            'mapping': { 'x': 'x', 'y': 'y' },
            'theme': { 'name': 'classic' },
            'kind': 'plot',
            'scales': [
                {
                    'aesthetic': 'size',
                    'guide': 'none',
                    'range': [20.0, 30.0]
                },
                {
                    'aesthetic': 'size_start',
                    'guide': 'none',
                    'range': [20.0, 30.0]
                },
                {
                    'aesthetic': 'size_end',
                    'guide': 'none',
                    'range': [20.0, 30.0]
                },
                {
                    'aesthetic': 'stroke',
                    'guide': 'none',
                    'range': [0.0, 10.0]
                },
                {
                    'aesthetic': 'stroke_start',
                    'guide': 'none',
                    'range': [0.0, 10.0]
                },
                {
                    'aesthetic': 'stroke_end',
                    'guide': 'none',
                    'range': [0.0, 10.0]                    
                },
                {
                    'aesthetic': 'shape',
                    'guide': 'none',
                    'scale_mapper_kind': 'identity',
                    'discrete': true
                },
                {
                    'aesthetic': 'x',
                    'limits': [-1.5, 1.5]
                },
                {
                    'aesthetic': 'y',
                    'limits': [-1.5, 1.5]
                }
            ],
            'layers': [
                {
                    'geom': 'point',
                    'mapping': {
                        'size': 'size',
                        'shape': 'shape',
                        'stroke': 'stroke'
                    },
                    'color': '#4575b4',
                    'fill': '#abd9e9'
                },
                {
                    'geom': 'segment',
                    'mapping': {
                        'xend': 'x_end',
                        'yend': 'y_end',
                        'size_start': 'size',
                        'size_end': 'size_end',
                        'stroke_start': 'stroke',
                        'stroke_end': 'stroke_end'
                    },
                    'arrow': {
                        'name': 'arrow',
                        'angle': 15.0,
                        'length': 15.0,
                        'ends': 'both',
                        'type': 'open'
                    }
                }
            ]
        }""".trimIndent()
        return parsePlotSpec(spec)
    }
}