/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.AutoMpg
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

class FacetGridDemo : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            cols(),
            rows(),
            both(),
            bothFlipped(),
            bothOrderingDesc(),
        )
    }

    private fun cols(): Map<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "grid",
            "x" to AutoMpg.cylinders.name
        )
        return plotSpec
    }

    private fun rows(): Map<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "grid",
            "y" to AutoMpg.origin.name
        )
        return plotSpec
    }

    private fun both(): Map<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "grid",
            "x" to AutoMpg.cylinders.name,
            "y" to AutoMpg.origin.name
        )
        return plotSpec
    }

    private fun bothFlipped(): Map<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "grid",
            "x" to AutoMpg.origin.name,
            "y" to AutoMpg.cylinders.name
        )
        return plotSpec
    }

    private fun bothOrderingDesc(): Map<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "grid",
            "x" to AutoMpg.cylinders.name,
            "y" to AutoMpg.origin.name,
            "order" to listOf(null, "desc")
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