/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demo.plot.common.data.Iris
import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.Plot.THEME
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.FIGURES
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.COL_WIDTHS
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.INNER_ALIGNMENT
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.NCOLS
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.NROWS
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.ROW_HEIGHTS
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.LAYOUT
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Layout.NAME
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Layout.SUBPLOTS_GRID

open class GGGridTheme {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            irisTriple(firstElemTheme = theme("bw", "solarized_light")),
            irisTriple(theme = theme("grey", "darcula", plotMargin = 40)),
            irisTriple(theme = theme("grey", "darcula"), firstElemTheme = theme("bw", "solarized_light")),
            irisTriple_compositeCell(),
            irisTriple_compositeCell(theme = theme("bw", "darcula")),
            irisTriple_compositeCell(
                theme = theme("bw", "darcula"),
                innerCompositTheme = theme("grey", "solarized_light")
            ),
        )
    }

    companion object {
        private fun data(): Map<String, List<*>> {
            val map = HashMap<String, List<*>>()
            map["x"] = listOf(0.0)
            map["y"] = listOf(0.0)
            return map
        }

        private fun theme(name: String?, flavor: String?, plotMargin: Int? = null): Map<String, Any> {
            return HashMap<String, Any>().also { m ->
                name?.let { m["name"] = name }
                flavor?.let { m["flavor"] = flavor }
                plotMargin?.let {
                    m["plot_margin"] = mapOf(
                        "t" to plotMargin,
                        "r" to plotMargin,
                        "b" to plotMargin,
                        "l" to plotMargin,
                    )
                }
            }
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


        //============================

        private fun irisTriple(
            colWidths: List<Double>? = null,
            rowHeights: List<Double>? = null,
            innerAlignment: Boolean = false,
            theme: Map<*, *>? = null,
            firstElemTheme: Map<*, *>? = null,
        ): MutableMap<String, Any> {

            val densitySpec = irisDensityPlot()
            val scatterSpec = irisScatterPlot()

            val firstElem = firstElemTheme?.let {
                densitySpec.mapValues { it.value }.toMutableMap().also { it["theme"] = firstElemTheme }
            } ?: densitySpec

            return subplotsGrid(
                elements = listOf(
                    firstElem, simplePie(),
                    scatterSpec, densitySpec
                ),
                ncols = 2,
                nrows = 2,
                colWidths,
                rowHeights,
                innerAlignment,
                theme = theme
            )
        }

        @Suppress("FunctionName")
        private fun irisTriple_compositeCell(
            colWidths: List<Double>? = null,
            rowHeights: List<Double>? = null,
            innerAlignment: Boolean = false,
            theme: Map<*, *>? = null,
            innerCompositTheme: Map<*, *>? = null,
        ): MutableMap<String, Any> {
            val scatterSpec = irisScatterPlot()
            val densitySpec = irisDensityPlot()

            val innerSubplots = subplotsGrid(
                elements = listOf(scatterSpec, densitySpec),
                ncols = 2,
                nrows = 1,
                innerAlignment = false,
                theme = innerCompositTheme
            )

            return subplotsGrid(
                elements = listOf(
                    densitySpec, innerSubplots,
                ),
                ncols = 1,
                nrows = 2,
                colWidths,
                rowHeights,
                innerAlignment,
                theme = theme
            )
        }

        private fun subplotsGrid(
            elements: List<Any?>,
            ncols: Int,
            nrows: Int,
            colWidths: List<Double>? = null,
            rowHeights: List<Double>? = null,
            innerAlignment: Boolean,
            theme: Map<*, *>?
        ): MutableMap<String, Any> {
            return mutableMapOf(
                Option.Meta.KIND to Option.Meta.Kind.SUBPLOTS,
                FIGURES to elements,
                LAYOUT to mapOf(
                    NAME to SUBPLOTS_GRID,
                    NCOLS to ncols,
                    NROWS to nrows,
                    COL_WIDTHS to colWidths,
                    ROW_HEIGHTS to rowHeights,
                    INNER_ALIGNMENT to innerAlignment,
                ),
            ).also { map ->
                theme?.let {
                    map[THEME] = it
                }
            }
        }

        private fun irisScatterPlot(): MutableMap<String, Any> {
//            'theme': {'name': 'bw'},
            val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': '${Iris.sepalLength.name}',
                'y': '${Iris.sepalWidth.name}'
              },
              ${title("Bottom-Left")},
              'layers': [
                {
                  'geom': 'point',
                  'size': 5
                }
              ]
            }
        """.trimIndent()
//            'sampling': {'name': 'random', 'n': 10},

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = Iris.df
            return plotSpec
        }

        private fun irisDensityPlot(): MutableMap<String, Any> {
//            'theme': {'name': 'bw'},

            val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': '${Iris.sepalLength.name}'
              },
              'layers': [
                {
                  'geom': 'density'
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

        private fun simplePie(): MutableMap<String, Any> {
            val spec = """
                {
                 'data': {'name': ['rock', 'paper', 'scissors'], 'slice': [1, 3, 3]},
                 'kind': 'plot',
                 'layers': [{'geom': 'pie',
                   'stat': 'identity',
                   'mapping': {'fill': 'name', 'slice': 'slice'},
                   'size_unit': 'x',
                   'size': 0.5}]
                 }
             """.trimIndent()
            return parsePlotSpec(spec)
        }
    }
}
