/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import demo.plot.common.data.AutoMpg

class FacetWrapDemo {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            oneFacetDef(),
            oneFacet3cols(),
            oneFacet4rows(),
            twoFacets(),
            twoFacets_CylindersOrderDesc(),
        )
    }

    private fun oneFacetDef(): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to demo.plot.common.data.AutoMpg.cylinders.name,
            "format" to "{d} cyl"
        )
        return plotSpec
    }

    private fun oneFacet3cols(): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to listOf(demo.plot.common.data.AutoMpg.cylinders.name),     // one facet variant
            "ncol" to 3,
            "format" to "{d} cyl"
        )
        return plotSpec
    }

    private fun oneFacet4rows(): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to demo.plot.common.data.AutoMpg.cylinders.name,
            "nrow" to 4,
            "format" to "{d} cyl",
            "dir" to "v"
        )
        return plotSpec
    }

    private fun twoFacets(): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to listOf(
                demo.plot.common.data.AutoMpg.origin.name,
                demo.plot.common.data.AutoMpg.cylinders.name,
            ),
            "ncol" to 5,
            "format" to listOf(null, "{d} cyl")
        )
        return plotSpec
    }

    @Suppress("FunctionName")
    private fun twoFacets_CylindersOrderDesc(): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to listOf(
                demo.plot.common.data.AutoMpg.origin.name,
                demo.plot.common.data.AutoMpg.cylinders.name,
            ),
            "ncol" to 5,
            "order" to listOf(null, -1),
            "format" to listOf(null, "{d} cyl")
        )
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