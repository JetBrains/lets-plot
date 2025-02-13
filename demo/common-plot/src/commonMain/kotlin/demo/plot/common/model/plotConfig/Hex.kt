/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import kotlin.math.sqrt

class Hex {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basicIdentity(),
            basicWithStat(),
            pointOnBorder(0),
            customSize(40.0, 40.0, "size", "size"),
        )
    }

    private fun basicIdentity(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'ggtitle': {
                'text': 'Basic hex plot\nstat = identity'
              },
              'data': {
                'x': [-0.5, 0.5, 0],
                'y': [0, 0, ${1.0 / HEX_HEIGHT}],
                'g': ['a', 'b', 'c']
              },
              'mapping': {
                'x': 'x',
                'y': 'y',
                'fill': 'g'
              },
              'layers': [
                {
                  'geom': 'hex',
                  'stat': 'identity'
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }

    private fun basicWithStat(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'ggtitle': {
                'text': 'Basic hex plot\nstat = default'
              },
              'data': {
                'x': [-1, -1, 1, 0.95, 1.05, 0.000, 1.00, -0.5],
                'y': [${listOf(-1.0, 1.0, -1.0, 0.95, 1.05, 1.0/3.0, 1.0/3.0, 0.0).map { it / HEX_HEIGHT }.joinToString(", ")}]
              },
              'mapping': {
                'x': 'x',
                'y': 'y'
              },
              'layers': [
                {
                  'geom': 'hex',
                  'binwidth': [1, 1]
                },
                {
                  'geom': 'point',
                  'shape': 21,
                  'size': 4,
                  'color': 'black',
                  'fill': 'orange'
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }

    private fun pointOnBorder(pointId: Int): MutableMap<String, Any> {
        val halfHexHeight = HEX_HEIGHT / 2.0
        val coordinates = listOf(
            DoubleVector(0.0, halfHexHeight),
            DoubleVector(0.25, 0.75 * halfHexHeight),
            DoubleVector(0.5, 0.5 * halfHexHeight),
            DoubleVector(0.5, 0.0),
            DoubleVector(0.5, -0.5 * halfHexHeight),
            DoubleVector(0.25, -0.75 * halfHexHeight),
            DoubleVector(0.0, -halfHexHeight),
            DoubleVector(-0.25, -0.75 * halfHexHeight),
            DoubleVector(-0.5, -0.5 * halfHexHeight),
            DoubleVector(-0.5, 0.0),
            DoubleVector(-0.5, 0.5 * halfHexHeight),
            DoubleVector(-0.25, 0.75 * halfHexHeight),
        )
        val x = coordinates[pointId % coordinates.size].x
        val y = coordinates[pointId % coordinates.size].y
        val spec = """
            {
              'kind': 'plot',
              'ggtitle': {
                'text': 'Point on border'
              },
              'data': {
                'x': [-1, 0, -1.5, -0.5, 0.5, -1, 0, $x],
                'y': [${listOf(-1.0, -1.0, 0.0, 0.0, 0.0, 1.0, 1.0).map { it / HEX_HEIGHT }.joinToString(", ")}, $y],
                'g': ['a', 'a', 'a', 'a', 'a', 'a', 'a', 'b']
              },
              'mapping': {
                'x': 'x',
                'y': 'y'
              },
              'layers': [
                {
                  'mapping': {
                    'paint_a': '..count..'
                  },
                  'geom': 'hex',
                  'binwidth': [1, 1],
                  'size': 0.5,
                  'color': 'black',
                  'fill_by': 'paint_a'
                },
                {
                  'mapping': {
                    'paint_b': 'g'
                  },
                  'geom': 'point',
                  'shape': 21,
                  'size': 4,
                  'color': 'black',
                  'fill_by': 'paint_b'
                }
              ],
              'scales': [
                {
                  'aesthetic': 'paint_a',
                  'scale_mapper_kind' : 'color_gradient',
                  'low': '#253494',
                  'high': '#ffffcc',
                  'guide': 'none'
                },
                {
                  'aesthetic': 'paint_b',
                  'values': ['white', 'red'],
                  'guide': 'none'
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }

    private fun customSize(
        width: Double,
        height: Double,
        widthUnit: String,
        heightUnit: String
    ): MutableMap<String, Any> {
        val pointSize = when {
            widthUnit == "size" -> width
            heightUnit == "size" -> height
            else -> 4.0
        }
        val spec = """
            {
              'kind': 'plot',
              'ggtitle': {
                'text': 'Custom size hex plot\nwidth=$width, width_unit=\"$widthUnit\"\nheight=$height, height_unit=\"$heightUnit\"'
              },
              'data': {
                'x': [-2, 2, 0],
                'y': [0, 0, ${4.0 / HEX_HEIGHT}],
                'g': ['a', 'b', 'c']
              },
              'mapping': {
                'x': 'x',
                'y': 'y',
                'fill': 'g'
              },
              'layers': [
                {
                  'geom': 'hex',
                  'stat': 'identity',
                  'width': $width,
                  'height': $height,
                  'width_unit': '$widthUnit',
                  'height_unit': '$heightUnit'
                },
                {
                  'geom': 'point',
                  'size': $pointSize,
                  'color': 'orange'
                }
              ],
              'coord': {
                'name': 'fixed',
                'ratio': 1,
                'flip': false,
                'xlim': [-6, 6],
                'ylim': [-${4.0 / HEX_HEIGHT}, ${8.0 / HEX_HEIGHT}]
              }
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }

    companion object {
        val HEX_HEIGHT = 2.0 / sqrt(3.0) // height of right hexagon with width = 1
    }
}