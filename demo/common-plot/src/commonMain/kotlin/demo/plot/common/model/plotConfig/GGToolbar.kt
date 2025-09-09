/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demo.plot.common.data.Iris
import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.FIGURES
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.NCOLS
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.NROWS
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.LAYOUT
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Layout.NAME
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Layout.SUBPLOTS_GRID

class GGToolbar {
    fun plotSpecList(dark: Boolean): List<MutableMap<String, Any>> {
        return listOf(
            irisScatter(dark),
            irisPair(dark),
        )
    }

    fun irisScatter(
        dark: Boolean
    ): MutableMap<String, Any> {

        val spec = """
            {
              'ggtoolbar': {},
              'kind': 'plot',
              'mapping': {
                'x': '${Iris.sepalLength.name}',
                'y': '${Iris.sepalWidth.name}'
              },
              'theme': {'name': 'bw'},
              'layers': [
                {
                  'geom': 'point',
                  'size': 5,
                  'alpha': 0.4
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        if (dark) {
            plotSpec["theme"] = mapOf("name" to "bw", "flavor" to "high_contrast_dark")
        }
        plotSpec["data"] = Iris.df
        return plotSpec
    }

    fun irisDensity(
        dark: Boolean
    ): MutableMap<String, Any> {

        val spec = """
            {
              'kind': 'plot',
              'mapping': {
                'x': '${Iris.sepalLength.name}'
              },
              'layers': [
                {
                  'geom': 'density',
                  'size': 1.5,
                  'alpha': 0.1
                }
              ],
              'scales': [
                  {'aesthetic': 'y', 'position': 'right'}
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        if (dark) {
            plotSpec["theme"] = mapOf("name" to "bw", "flavor" to "high_contrast_dark")
        }
        plotSpec["data"] = Iris.df
        return plotSpec
    }

    fun irisPair(
        dark: Boolean
    ): MutableMap<String, Any> {
        val scatterSpec = irisScatter(dark)
        val densitySpec = irisDensity(dark)
        return mutableMapOf(
            "ggtoolbar" to emptyMap<String, Any>(),
            Option.Meta.KIND to Option.Meta.Kind.SUBPLOTS,
            FIGURES to listOf(
                scatterSpec, densitySpec,
            ),
            LAYOUT to mapOf(
                NAME to SUBPLOTS_GRID,
                NCOLS to 2,
                NROWS to 1,
            )
        ).also {
            if (dark) {
                it["theme"] = mapOf("name" to "bw", "flavor" to "high_contrast_dark")
            }
        }
    }
}