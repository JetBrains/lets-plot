/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec

open class AxisPosition {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
//            defaultAxis(),
            yAxis_Right()
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
                    "                 'size': 5" +
                    "               }" +
                    "           ]" +
                    ""
        }


        fun defaultAxis(): MutableMap<String, Any> {
            val spec = """
                {
                    'kind': 'plot',
                    ${layerMapping()},
                    ${title("Default")}
                }
            """.trimIndent()
            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        @Suppress("FunctionName")
        fun yAxis_Right(): MutableMap<String, Any> {
            val spec = """
                {
                    'kind': 'plot',
                    ${layerMapping()},
                    ${title("Default")},
                    'scales': [{'aesthetic': 'y', 'name': 'right', 'position': 'right'}]
                }
            """.trimIndent()
            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }
    }
}
