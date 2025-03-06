/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

open class ScaleLabelJustification {

    fun plotSpecList(): List<MutableMap<String, Any>> {
        val angle = 0.0
        val justifications = listOf(1.0, 0.5, 0.0)
        val axisPosition = "bottom"

        return justifications.flatMap { hJust ->
            justifications.map { vJust ->
                specWithAxis(position = axisPosition, angle = angle, hJust = hJust, vJust = vJust)
            }
        }
    }

    companion object {
        private fun data(): Map<String, List<*>> {
            val map = HashMap<String, List<*>>()
            map["xh"] = listOf("Label number\none", "2", "\n\nLower label", "A\nshort\none", "Fifth label 5")
            map["yh"] = listOf("1", "20", "300", "4000", "50000")
            map["xv"] = listOf("1", "2", "3", "4", "5")
            map["yv"] = listOf("label 1", "label\nnumber\n2", "long label", "4", "label 5")
            return map
        }

        private fun title(s: String): String {
            return "   'ggtitle': {" +
                    "                 'text': '" + s + "'" +
                    "              }" +
                    ""
        }

        private fun layerMapping(position: String): String {
            val mappingVariant = if (position == "top" || position == "bottom") "h" else "v"
            return "   'mapping': {" +
                    "             'x': 'x$mappingVariant'," +
                    "             'y': 'y$mappingVariant'" +
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
