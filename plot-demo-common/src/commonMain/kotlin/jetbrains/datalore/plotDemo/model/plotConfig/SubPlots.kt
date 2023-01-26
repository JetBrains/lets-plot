/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.parsePlotSpec

open class SubPlots {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
//            simple(),
            simpleGGBunch(),
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

        private fun simplePlot(): MutableMap<String, Any> {
            val plot = """
                {
                    'kind': 'plot',
                    ${layerMapping()},
                    ${title("Default")}
                }
            """.trimIndent()


            val plotSpec = HashMap(parsePlotSpec(plot))
            plotSpec["data"] = data()
            return plotSpec
        }


        //============================

        fun simple(): MutableMap<String, Any> {
            val plotSpec = simplePlot()

            // Sub-plots
            val subPlots = mutableMapOf(
                Option.Meta.KIND to Option.Meta.Kind.SUBPLOTS,
                "figures" to listOf(
                    listOf(plotSpec, plotSpec)    // 1 row, 2 col
                )
            )
            return subPlots
        }

        fun simpleGGBunch(): MutableMap<String, Any> {
            val plotSpec = simplePlot()

            // GGBunch
            val ggBunch = mutableMapOf(
                Option.Meta.KIND to Option.Meta.Kind.GG_BUNCH,
                "items" to listOf(
                    mapOf(
                        "x" to 0, "y" to 0, "width" to 150, "height" to 150,
                        "feature_spec" to plotSpec
                    ),
                    mapOf(
                        "x" to 160, "y" to 0, "width" to 150, "height" to 150,
                        "feature_spec" to plotSpec
                    ),
                )
            )
            return ggBunch
        }
    }
}
