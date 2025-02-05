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
                specWithAxis(position = axis_position, angle = angle, hJust = hJust, vJust = vJust)
            }
        }
    }

    companion object {
        private fun data(): Map<String, List<*>> {
            val map = HashMap<String, List<*>>()
            map["x"] = listOf("OXXXXXXX", "OOOOO", "It's lXXX", "XOOOOXXX")
            map["xa"] = listOf("1", "2", "3", "4") // do not overcrowd the x-axis when testing y-axis
            map["y"] = listOf(500, 1000, 500, 0)
            return map
        }

        private fun title(s: String): String {
            return "   'ggtitle': {" +
                    "                 'text': '" + s + "'" +
                    "              }" +
                    ""
        }

        private fun layerMapping(postition: String): String {
            val xMapping = if (postition == "top" || postition == "bottom") "x" else "xa"
            return "   'mapping': {" +
                    "             'x': '$xMapping'," +
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

        private fun specWithAxis(
            position: String,
            angle: Double,
            hJust: Double,
            vJust: Double
        ): MutableMap<String, Any> {
            val axis = when (position) {
                "left", "right" -> "y"
                "top", "bottom" -> "x"
                else -> throw IllegalArgumentException("Axis must be 'x' or 'y'")
            }

            val axisTextKey = "axis_text_$axis"
            val spec = """
            {
                'kind': 'plot',
                'theme': {
                   'name': 'classic',
                   'axis_title': { 'blank': true },
                   '$axisTextKey': { 'angle': $angle, 'hjust': $hJust, 'vjust': $vJust, 'blank': false }
                },
                ${title("$axis: $angle°, h$hJust, v$vJust")},
                'ggsize': {'width': 360, 'height': 300},
                ${layerMapping(position)},                    
                'scales': [
                      {'aesthetic': '$axis', 'position': '$position'}
                ]
            }
            """.trimIndent()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }
    }
}
