/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.core.spec.Option.Facet

class MarginalLayersFacetsDemo {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            grid(),
            grid_coord_fixed(),
            grid_freeY(),

            wrap(),
            wrap_coord_fixed(),
            wrap_freeX(),
        )
    }

    private fun grid(): MutableMap<String, Any> {
        val plotSpec = commonSpecs("Grid")
        plotSpec["facet"] = mapOf(
            "name" to "grid",
            "y" to demo.plot.common.data.AutoMpg.origin.name,
        )
        return plotSpec
    }

    @Suppress("FunctionName")
    private fun grid_coord_fixed(): MutableMap<String, Any> {
        val plotSpec = commonSpecs("Grid, coord=fixed")
        plotSpec["facet"] = mapOf(
            "name" to "grid",
            "y" to demo.plot.common.data.AutoMpg.origin.name,
        )
        plotSpec["coord"] = mapOf(
            "name" to "fixed",
//            "xlim" to listOf(100, 150),
//            "ylim" to listOf(0, 50),
        )
        return plotSpec
    }

    @Suppress("FunctionName")
    private fun grid_freeY(): MutableMap<String, Any> {
        val plotSpec = commonSpecs("Grid, scales=free_y")
        plotSpec["facet"] = mapOf(
            "name" to "grid",
            "y" to demo.plot.common.data.AutoMpg.origin.name,
            Facet.SCALES to Facet.SCALES_FREE_Y,
        )
        return plotSpec
    }

    private fun wrap(): MutableMap<String, Any> {
        val plotSpec = commonSpecs("Wrap")
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to demo.plot.common.data.AutoMpg.cylinders.name,
            "format" to "{d} cyl"
        )
        return plotSpec
    }

    @Suppress("FunctionName")
    private fun wrap_coord_fixed(): MutableMap<String, Any> {
        val plotSpec = commonSpecs("Wrap, coord=fixed")
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to demo.plot.common.data.AutoMpg.cylinders.name,
            "format" to "{d} cyl"
        )

        plotSpec["coord"] = mapOf(
            "name" to "fixed",
////            "xlim" to listOf(100, 150),
//            "xlim" to listOf(50, 150),
            "ylim" to listOf(0, 100),
        )
        return plotSpec
    }

    @Suppress("FunctionName")
    private fun wrap_freeX(): MutableMap<String, Any> {
        val plotSpec = commonSpecs("Wrap, scales=free_x")
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to demo.plot.common.data.AutoMpg.cylinders.name,
            "format" to "{d} cyl",
            Facet.SCALES to Facet.SCALES_FREE_X,
        )

        return plotSpec
    }


    companion object {
        private fun commonSpecs(title: String): MutableMap<String, Any> {
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
                    },
                    ${marginalSpecs(listOf("l", "t", "r", "b"), listOf(0.1, 0.1, 0.2, 0.2))}
                ],
                'ggtitle': {'text': '$title'},
                'theme': {'name': 'grey'}
            }
        """.trimIndent()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = demo.plot.common.data.AutoMpg.df
            return plotSpec
        }

        private fun marginalSpecs(sides: List<String>, sizes: List<Double>): String {
            val l = ArrayList<String>()
            for ((i, side) in sides.withIndex()) {
//                l.add(marginalPoints(side, sizes[i]))
                l.add(marginalHist(side, sizes[i]))
                l.add(marginalDensity(side, sizes[i]))
            }
            return l.joinToString(",", "", "")
        }

        private fun marginalPoints(side: String, size: Double): String {
            return """
                {
                    'geom': 'point',
                    'marginal' : true,
                    'margin_side' : '$side',
                    'margin_size' : $size
                }
            """.trimIndent()
        }

        private fun marginalHist(side: String, size: Double): String {
            val orientation = when (side) {
                "l", "r" -> "y"
                else -> "x"
            }
            val aesY = when (orientation) {
                "x" -> "y"
                else -> "x"
            }

            return """
                {
                    'geom': 'histogram', 'bins' : 10, 'color' : 'white',
                    'mapping': {'$aesY': '..density..', 'fill': '${demo.plot.common.data.AutoMpg.origin.name}'},
                    'marginal' : true,
                    'margin_side' : '$side',
                    'margin_size' : $size,
                    'orientation' : '$orientation'
            }
            """.trimIndent()
        }

        private fun marginalDensity(side: String, size: Double): String {
            val orientation = when (side) {
                "l", "r" -> "y"
                else -> "x"
            }
            return """
                {
                    'geom': 'density', 'color' : 'red', 'fill' : 'blue', 'alpha' : 0.1,
                    'marginal' : true,
                    'margin_side' : '$side',
                    'margin_size' : $size,
                    'orientation' : '$orientation'
            }
            """.trimIndent()
        }

    }
}