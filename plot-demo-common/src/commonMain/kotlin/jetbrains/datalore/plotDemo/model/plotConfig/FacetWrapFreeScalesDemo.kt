/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import org.jetbrains.letsPlot.core.plot.builder.assemble.facet.FacetScales
import jetbrains.datalore.plot.config.Option
import demoAndTestShared.parsePlotSpec
import jetbrains.datalore.plotDemo.data.AutoMpg

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
            "facets" to AutoMpg.cylinders.name,
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
                AutoMpg.origin.name,
                AutoMpg.cylinders.name,
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
                    'x': "${AutoMpg.horsepower.name}",
                    'y': "${AutoMpg.mpg.name}",     
                    'color': "${AutoMpg.origin.name}"     
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
        plotSpec["data"] = AutoMpg.df
        return plotSpec
    }
}