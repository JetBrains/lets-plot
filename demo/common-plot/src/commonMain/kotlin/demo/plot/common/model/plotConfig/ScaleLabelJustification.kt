/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

open class ScaleLabelJustification {

    fun plotSpecList(): List<MutableMap<String, Any>> {
        val angle = 30.0
        val justifications = listOf(1.0, 0.5, 0.0)
        val axis_position = "bottom"

        return justifications.flatMap { hJust ->
            justifications.map { vJust ->
                if (axis_position == "left" || axis_position == "right")
                    axisWithVLabelAngles(yPosition = axis_position, yAngle = angle, yHJust = hJust, yVJust = vJust)
                else
                    axisWithHLabelAngles(xPosition = axis_position, xAngle = angle, xHJust = hJust, xVJust = vJust)
            }
        }
    }

    companion object {
        private fun data(): Map<String, List<*>> {
            val map = HashMap<String, List<*>>()
            map["x"] = listOf("OXXXXXXX", "OOOOO", "It's lXXX", "XOOOOXXX")
            map["y"] = listOf(500, 1000, 500, 0)
            return map
        }

        private fun title(s: String): String {
            return "   'ggtitle': {" +
                    "                 'text': '" + s + "'" +
                    "              }" +
                    ""
        }

        private fun layerMapping(): String {
            return "   'mapping': {" +
                    "             'x': 'x'," +
                    "             'y': 'y'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                 'geom': 'point'," +
                    "                 'size': 5,     " +
                    "                  'tooltips': {'lines': ['text']}" +
                    "               }" +
                    "           ]" +
                    ""
        }

        private fun axisWithHLabelAngles(
            xPosition: String,
            xAngle: Double,
            xHJust: Double,
            xVJust: Double,
        ): MutableMap<String, Any> {
            val spec = """
                {
                    'kind': 'plot',
                    'theme': {
                       'name': 'classic',
                       'axis_text_x': { 'angle' : $xAngle, 'hjust' : $xHJust, 'vjust' : $xVJust, 'blank': false }
                    },
                    ${title("x: $xAngle°, h$xHJust, v$xVJust")},
                    'ggsize': {'width': 400, 'height': 320},
                    ${layerMapping()},                    
                    'scales': [
                          {'aesthetic': 'x', 'position': '$xPosition'}
                    ]
                }
            """.trimIndent()
            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        private fun axisWithVLabelAngles(
            yPosition: String,
            yAngle: Double,
            yHJust: Double,
            yVJust: Double,
        ): MutableMap<String, Any> {
            val spec = """
                {
                    'kind': 'plot',
                    'theme': {
                       'name': 'classic',
                       'axis_text_y': { 'angle' : $yAngle, 'hjust' : $yHJust, 'vjust' : $yVJust, 'blank': false }
                    },
                    ${title("y: $yAngle°, h$yHJust, v$yVJust")},
                    'ggsize': {'width': 400, 'height': 320},
                    ${layerMapping()},                    
                    'scales': [
                          {'aesthetic': 'y', 'position': '$yPosition'}
                    ]
                }
            """.trimIndent()
            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }
    }
}
