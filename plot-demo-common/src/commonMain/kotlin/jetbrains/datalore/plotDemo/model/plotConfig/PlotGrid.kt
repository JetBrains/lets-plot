/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.Option.SubPlots.FIGURES
import jetbrains.datalore.plot.config.Option.SubPlots.Figure.BLANK
import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.Iris

open class PlotGrid {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
//            simple(),
            irisTriple()
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

        //============================

        private fun irisTriple(): MutableMap<String, Any> {
            val scatterSpec = irisScatterPlot()
            val densitySpec = irisDensityPlot()

            // Plot grid
            return mutableMapOf(
                Option.Meta.KIND to Option.Meta.Kind.SUBPLOTS,
                FIGURES to listOf(              // 2 row, 2 col
                    listOf(densitySpec, BLANK),
                    listOf(scatterSpec, densitySpec)
                )
            )
        }

        private fun irisScatterPlot(): MutableMap<String, Any> {

            val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': '${Iris.sepalLength.name}',
                'y': '${Iris.sepalWidth.name}'
              },
              'theme': {'name': 'bw'},
              ${title("Bottom-Left")},
              'layers': [
                {
                  'geom': 'point',
                  'size': 5,
                  'color': 'black',
                  'alpha': 0.4
                }
              ]
            }
        """.trimIndent()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = Iris.df
            return plotSpec
        }

        private fun irisDensityPlot(): MutableMap<String, Any> {

            val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': '${Iris.sepalLength.name}'
              },
              'theme': {'name': 'bw'},
              'layers': [
                {
                  'geom': 'density',
                  'size': 1.5,
                  'color': 'black',
                  'fill': 'black',
                  'alpha': 0.1
                }
              ],
              'scales': [
                  {'aesthetic': 'y', 'position': 'right'}
              ]
            }
        """.trimIndent()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = Iris.df
            return plotSpec
        }
    }
}
