/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class Tile {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            customSize(0.5, 4.0, "res", "identity")
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'x': [-4, -4, 4, 4],
                'y': [-4, 4, -4, 4]
              },
              'mapping': {
                'x': 'x',
                'y': 'y'
              },
              'ggtitle': {
                'text': 'Tile basic demo'
              },
              'layers': [
                {
                  'geom': 'tile'
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
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'x': [-4, -4, 4, 4],
                'y': [-4, 4, -4, 4]
              },
              'mapping': {
                'x': 'x',
                'y': 'y'
              },
              'ggtitle': {
                'text': 'Tiles with custom size:\nwidth=$width, width_unit=\"$widthUnit\"\nheight=$height, height_unit=\"$heightUnit\"'
              },
              'layers': [
                {
                  'geom': 'tile',
                  'width': $width,
                  'height': $height,
                  'width_unit': '$widthUnit',
                  'height_unit': '$heightUnit'
                }
              ]
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }
}