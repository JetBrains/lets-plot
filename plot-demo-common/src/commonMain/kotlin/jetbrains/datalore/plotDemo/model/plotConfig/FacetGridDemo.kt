/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.AutoMpg
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

class FacetGridDemo : PlotConfigDemoBase() {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            cols(),
            rows(),
            both(),
            bothFlipped(),
            both_YOrderingDesc(),
        )
    }

    private fun cols(): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "grid",
            "x" to AutoMpg.cylinders.name,
            "x_format" to "{d} cyl"
        )
        return plotSpec
    }

    private fun rows(): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "grid",
            "y" to AutoMpg.origin.name
        )
        return plotSpec
    }

    private fun both(): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "grid",
            "x" to AutoMpg.cylinders.name,
            "y" to AutoMpg.origin.name,
            "x_format" to "{d} cyl"
        )
        return plotSpec
    }

    private fun bothFlipped(): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "grid",
            "x" to AutoMpg.origin.name,
            "y" to AutoMpg.cylinders.name,
            "y_format" to "{d} cyl"
        )
        return plotSpec
    }

    @Suppress("FunctionName")
    private fun both_YOrderingDesc(): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "grid",
            "x" to AutoMpg.cylinders.name,
            "y" to AutoMpg.origin.name,
            "y_order" to -1,
            "x_format" to "{d} cyl"
        )
        return plotSpec
    }

    private fun commonSpecs(): MutableMap<String, Any> {
        val spec = """
            {
                'kind': 'plot',
                'mapping': {
                    'x': "${AutoMpg.horsepower.name}",
                    'y': "${AutoMpg.mpg.name}",     
                    'color': "${AutoMpg.origin.name}"     
                },
                'layers': [
                    {
                        'geom': 'point'
                    }
                ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = AutoMpg.df
        return plotSpec
    }
}