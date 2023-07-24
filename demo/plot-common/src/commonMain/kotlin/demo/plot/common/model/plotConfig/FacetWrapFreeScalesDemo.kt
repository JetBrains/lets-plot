/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import org.jetbrains.letsPlot.core.plot.builder.assemble.facet.FacetScales
import org.jetbrains.letsPlot.core.spec.Option
import demoAndTestShared.parsePlotSpec
import demo.plot.common.data.AutoMpg

class FacetWrapFreeScalesDemo {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            oneFacetDef(FacetScales.FREE_X),
            oneFacetDef(FacetScales.FREE_Y),
            oneFacetDef(FacetScales.FREE),
            twoFacets(FacetScales.FREE_X),
            twoFacets(FacetScales.FREE_Y),
            twoFacets(FacetScales.FREE),
        )
    }

    private fun oneFacetDef(scales: FacetScales): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to demo.plot.common.data.AutoMpg.cylinders.name,
            Option.Facet.SCALES to scales,
            "format" to "{d} cyl"
        )

        plotSpec["ggtitle"] = mapOf("text" to "scales='${scales.toString().lowercase()}'")
        return plotSpec
    }

    private fun twoFacets(scales: FacetScales): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to listOf(
                demo.plot.common.data.AutoMpg.origin.name,
                demo.plot.common.data.AutoMpg.cylinders.name,
            ),
            "ncol" to 5,
            Option.Facet.SCALES to scales,
            "format" to listOf(null, "{d} cyl")
        )
        plotSpec["ggtitle"] = mapOf("text" to "scales='${scales.toString().lowercase()}'")
        return plotSpec
    }


    @Suppress("DuplicatedCode")
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