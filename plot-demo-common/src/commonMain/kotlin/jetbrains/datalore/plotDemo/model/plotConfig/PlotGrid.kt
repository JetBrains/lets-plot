/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.Option.SubPlots.FIGURES
import jetbrains.datalore.plot.config.Option.SubPlots.Figure.BLANK
import jetbrains.datalore.plot.config.Option.SubPlots.Grid.INNER_ALIGNMENT
import jetbrains.datalore.plot.config.Option.SubPlots.Grid.NCOLS
import jetbrains.datalore.plot.config.Option.SubPlots.Grid.NROWS
import jetbrains.datalore.plot.config.Option.SubPlots.LAYOUT
import jetbrains.datalore.plot.config.Option.SubPlots.Layout.NAME
import jetbrains.datalore.plot.config.Option.SubPlots.Layout.SUBPLOTS_GRID
import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.Iris

open class PlotGrid {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
//            simple(),
//            irisTriple(innerAlignment = false),
//            irisTriple(innerAlignment = true),
            irisTriple_compositeCell(innerAlignment = false),
//            irisTriple_compositeCell(innerAlignment = true),
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
            return subplotsGrid(
                elements = listOf(plotSpec, plotSpec),
                ncols = 2,
                nrows = 1,
                innerAlignment = false
            )
        }

        //============================

        private fun irisTriple(innerAlignment: Boolean): MutableMap<String, Any> {
            val scatterSpec = irisScatterPlot()
            val densitySpec = irisDensityPlot()
            return subplotsGrid(
                elements = listOf(
                    densitySpec, BLANK,
                    scatterSpec, densitySpec
                ),
                ncols = 2,
                nrows = 2,
                innerAlignment
            )
        }

        @Suppress("FunctionName")
        private fun irisTriple_compositeCell(innerAlignment: Boolean): MutableMap<String, Any> {
            val scatterSpec = irisScatterPlot()
            val densitySpec = irisDensityPlot()

            val innerSubplots = subplotsGrid(
                elements = listOf(scatterSpec, densitySpec),
                ncols = 2,
                nrows = 1,
                innerAlignment = false
            )

            return subplotsGrid(
                elements = listOf(
                    densitySpec, innerSubplots,
                ),
                ncols = 1,
                nrows = 2,
                innerAlignment
            )
        }

        private fun subplotsGrid(
            elements: List<Any?>,
            ncols: Int,
            nrows: Int,
            innerAlignment: Boolean
        ): MutableMap<String, Any> {
            return mutableMapOf(
                Option.Meta.KIND to Option.Meta.Kind.SUBPLOTS,
                FIGURES to elements,
                LAYOUT to mapOf(
                    NAME to SUBPLOTS_GRID,
                    NCOLS to ncols,
                    NROWS to nrows,
                    INNER_ALIGNMENT to innerAlignment,
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
