/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec

open class AxisPositionFlexBreaksRotated {

    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            axisWithLabelAngles(xAngle = 10.0, yAngle = 0.0),
            axisWithLabelAngles(xAngle = 90.0, yAngle = 0.0)
        )
    }


    companion object {
        private fun data(): Map<String, List<*>> {
            val map = HashMap<String, List<*>>()
            map["x"] = listOf(0.0)
            map["y"] = listOf(0.0)
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

        private fun axisWithLabelAngles(
            xPosition: String? = "bottom",
            xAngle: Double,
            yPosition: String? = "left",
            yAngle: Double
        ): MutableMap<String, Any> {
            val spec = """
                {
                    'kind': 'plot',
                    'theme': {
                       'name': 'classic',
                       'axis_text_x': { 'angle' : $xAngle, 'blank': false },
                       'axis_text_y': { 'angle' : $yAngle, 'blank': false }
                    },
                    ${title("x: $xPosition, $xAngle°; y: $yPosition, $yAngle°")},
                    ${layerMapping()},                    
                    'scales': [
                          {'aesthetic': 'x', 'position': '$xPosition'},
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