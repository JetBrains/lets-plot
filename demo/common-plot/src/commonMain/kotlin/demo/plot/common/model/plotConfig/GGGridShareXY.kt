/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.FIGURES
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.COL_WIDTHS
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.INNER_ALIGNMENT
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.NCOLS
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.NROWS
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.ROW_HEIGHTS
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.SHARE_X_SCALE
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.SHARE_Y_SCALE
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.LAYOUT
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Layout.NAME
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Layout.SUBPLOTS_GRID

open class GGGridShareXY {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            grid(shareX = "col", title = "sharex = col"),
            grid(shareX = "row", title = "sharex = row"),
            grid(shareX = "all", shareY = "all", title = "sharex, sharey = all"),
            grid(shareX = "all", shareY = "all", title = "sharex, sharey = all", withBars = true),
        )
    }

    companion object {

        private fun grid(
            colWidths: List<Double>? = null,
            rowHeights: List<Double>? = null,
            innerAlignment: Boolean = true,
            shareX: String = "none",
            shareY: String = "none",
            withBars: Boolean = false,
            title: String
        ): MutableMap<String, Any> {

            val p00 = scatterPlot(0 to 5)
            p00["ggtitle"] = mapOf("text" to title)

            val p10 = scatterPlot(-5 to 0)  // row 1
            val p01 = scatterPlot(0 to 7)   // col 1
            val p11 = if (withBars) {
                // Note: discrete x-scale is excluded.
                barPlot()
            } else {
                scatterPlot(2 to 7)
            }

            return makeGrid(
                elements = listOf(
                    p00, p01,
                    p10, p11
                ),
                ncols = 2,
                nrows = 2,
                colWidths,
                rowHeights,
                innerAlignment,
                shareX,
                shareY,
            )
        }

        private fun makeGrid(
            elements: List<Any?>,
            ncols: Int,
            nrows: Int,
            colWidths: List<Double>? = null,
            rowHeights: List<Double>? = null,
            innerAlignment: Boolean,
            shareXScale: String,
            shareYScale: String,
        ): MutableMap<String, Any> {
            //  'theme': { 'plot_background': {'fill': 'pink', 'blank': false}
            return mutableMapOf(
                Option.Meta.KIND to Option.Meta.Kind.SUBPLOTS,
                "theme" to mapOf(
                    Option.Theme.PLOT_BKGR_RECT to mapOf(
                        Option.Theme.Elem.COLOR to "orange",
                        Option.Theme.Elem.SIZE to 5,
                        Option.Theme.Elem.BLANK to false
                    ),
                    Option.Theme.PLOT_MARGIN to 10
                ),
                FIGURES to elements,
                LAYOUT to mapOf(
                    NAME to SUBPLOTS_GRID,
                    NCOLS to ncols,
                    NROWS to nrows,
                    COL_WIDTHS to colWidths,
                    ROW_HEIGHTS to rowHeights,
                    INNER_ALIGNMENT to innerAlignment,
                    SHARE_X_SCALE to shareXScale,
                    SHARE_Y_SCALE to shareYScale,
                )
            )
        }

        private fun scatterPlot(lims: Pair<Int, Int>): MutableMap<String, Any> {
            val l = IntProgression.fromClosedRange(lims.first, lims.second, 1).toList()
            val dat = mapOf(
                "x" to l,
                "y" to l
            )

            val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': 'x',
                'y': 'y'
              },
              'theme': {'name': 'bw', 'axis_title': 'blank'},
              'layers': [
                {
                  'geom': 'point'
                }
              ]
            }
        """.trimIndent()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = dat
            return plotSpec
        }

        private fun barPlot(): MutableMap<String, Any> {
            val spec = """
            {
              'kind': 'plot',
              'data': {'x': ['a', 'b', 'c'], 'y': [10, 8, -3]},
              'mapping': {
                'x': 'x',
                'y': 'y'
              },
              'theme': {'name': 'bw', 'axis_title': 'blank'},
              'layers': [
                {
                  'geom': 'bar',
                  'stat': 'identity' 
                }
              ]
            }
        """.trimIndent()

            return HashMap(parsePlotSpec(spec))
        }
    }
}
