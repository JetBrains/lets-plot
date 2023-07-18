/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec
import jetbrains.datalore.plotDemo.data.AutoMpg

class FacetWrapDirVDemo {
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
            "facets" to AutoMpg.cylinders.name,
            "format" to "{d} cyl",
            "dir" to "V",
        )
        return plotSpec
    }

    private fun oneFacet3cols(): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to listOf(AutoMpg.cylinders.name),     // one facet variant
            "ncol" to 3,
            "format" to "{d} cyl",
            "dir" to "V"
        )
        return plotSpec
    }

    private fun oneFacet4rows(): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to AutoMpg.cylinders.name,
            "nrow" to 4,
            "format" to "{d} cyl",
            "dir" to "V"
        )
        return plotSpec
    }

    private fun twoFacets(): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to listOf(
                AutoMpg.origin.name,
                AutoMpg.cylinders.name,
            ),
            "nrow" to 5,
            "format" to listOf(null, "{d} cyl"),
            "dir" to "V"
        )
        return plotSpec
    }

    @Suppress("FunctionName")
    private fun twoFacets_CylindersOrderDesc(): MutableMap<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to listOf(
                AutoMpg.origin.name,
                AutoMpg.cylinders.name,
            ),
            "nrow" to 5,
            "order" to listOf(null, -1),
            "format" to listOf(null, "{d} cyl"),
            "dir" to "V"
        )
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