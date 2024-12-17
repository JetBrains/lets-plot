/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.core.plot.builder.assemble.facet.FacetScales
import org.jetbrains.letsPlot.core.spec.Option

class FacetWrapDirVFreeScalesDemo {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            oneFacetDef(FacetScales.FREE_X),
            oneFacetDef(FacetScales.FREE_Y),
            oneFacetDef(FacetScales.FREE),
//            twoFacets(),
            twoFacets_CylindersOrderDesc(FacetScales.FREE_Y),
        )
    }

    private fun oneFacetDef(scales: FacetScales): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to demo.plot.common.data.AutoMpg.cylinders.name,
            "format" to "{d} cyl",
            "dir" to "V",
            Option.Facet.SCALES to scales,
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
            "nrow" to 5,
            "format" to listOf(null, "{d} cyl"),
            "dir" to "V",
            Option.Facet.SCALES to scales,
        )
        plotSpec["ggtitle"] = mapOf("text" to "scales='${scales.toString().lowercase()}'")
        return plotSpec
    }

    @Suppress("FunctionName")
    private fun twoFacets_CylindersOrderDesc(scales: FacetScales): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to listOf(
                demo.plot.common.data.AutoMpg.origin.name,
                demo.plot.common.data.AutoMpg.cylinders.name,
            ),
            "nrow" to 5,
            "order" to listOf(null, -1),
            "format" to listOf(null, "{d} cyl"),
            "dir" to "V",
            Option.Facet.SCALES to scales,

        )
        plotSpec["ggtitle"] = mapOf("text" to "cyl order=desc, scales='${scales.toString().lowercase()}'")
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