/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.write

class AxisPositionFixedBreaksRotated  {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            defaultAxisLabelsLayout(), // without angle settings
            axisWithLabelAngles(xAngle = 30.0, yAngle = 0.0),
            axisWithLabelAngles(xAngle = 90.0, yPosition = "right", yAngle = 270.0),
            axisWithLabelAngles(xPosition = "top", yPosition = "right", xAngle = 120.0, yAngle = 45.0),
            axisWithLabelAngles(xPosition = "both", yPosition = "both", xAngle = 180.0, yAngle = -30.0),
        ).map(::setScaleBreaks)
    }

    companion object {
        private val BREAKS = listOf(-0.5, -0.25, 0.0, 0.25, 0.5)
        private val LABS = listOf("one", "two", "three", "four", "five")

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
                    "                 'size': 5" +
                    "               }" +
                    "           ]" +
                    ""
        }

        private fun setScaleBreaks(plotSpec: MutableMap<String, Any>): MutableMap<String, Any> {
            plotSpec
                .getMaps("scales")!!
                .map { scaleSpec ->
                    scaleSpec.write(Option.Scale.BREAKS) { BREAKS }
                    scaleSpec.write(Option.Scale.LABELS) { LABS }
                }
            return plotSpec
        }

        private fun defaultAxisLabelsLayout(
            xPosition: String? = "bottom",
            yPosition: String? = "left"
        ): MutableMap<String, Any> {
            val spec = """
                {
                    'kind': 'plot',
                    'theme': { 'name' : 'classic' },
                    ${layerMapping()},
                    ${title("Default - no angles specification. x: $xPosition; y: $yPosition")},                    
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