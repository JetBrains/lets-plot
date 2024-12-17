/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.core.spec.Option.Facet

class FacetGridFreeScalesDemo {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            cols(),
            rows(),
            both(),
            bothFlipped(),
        )
    }

    private fun cols(): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "grid",
            "x" to demo.plot.common.data.AutoMpg.cylinders.name,
            Facet.SCALES to Facet.SCALES_FREE_X,
            "x_format" to "{d} cyl"
        )

        plotSpec["ggtitle"] = mapOf("text" to "scales='free_x'")

        return plotSpec
    }

    private fun rows(): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "grid",
            "y" to demo.plot.common.data.AutoMpg.origin.name,
            Facet.SCALES to Facet.SCALES_FREE_Y,
        )

        plotSpec["ggtitle"] = mapOf("text" to "scales='free_y'")
        return plotSpec
    }

    private fun both(): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "grid",
            "x" to demo.plot.common.data.AutoMpg.cylinders.name,
            "y" to demo.plot.common.data.AutoMpg.origin.name,
            Facet.SCALES to Facet.SCALES_FREE,
            "x_format" to "{d} cyl"
        )

        plotSpec["ggtitle"] = mapOf("text" to "scales='free'")
        return plotSpec
    }

    private fun bothFlipped(): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "grid",
            "x" to demo.plot.common.data.AutoMpg.origin.name,
            "y" to demo.plot.common.data.AutoMpg.cylinders.name,
            Facet.SCALES to Facet.SCALES_FREE,
            "y_format" to "{d} cyl"
        )

        plotSpec["ggtitle"] = mapOf("text" to "scales='free' (flipped)")
        return plotSpec
    }

    private fun commonSpecs(): MutableMap<String, Any> {
        val spec = """
            {
                'kind': 'plot',
                'mapping': {
                    'x': "${demo.plot.common.data.AutoMpg.horsepower.name}",
                    'y': "${demo.plot.common.data.AutoMpg.mpg.name}",     
                    'color': "${demo.plot.common.data.AutoMpg.origin.name}"     
                },
                'layers': [
                    {
                        'geom': 'point'
                    }
                ],
                'theme': {'name': 'grey'}
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = demo.plot.common.data.AutoMpg.df
        return plotSpec
    }
}