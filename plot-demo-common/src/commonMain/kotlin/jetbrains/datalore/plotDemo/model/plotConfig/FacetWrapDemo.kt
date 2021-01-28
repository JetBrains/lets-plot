/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.AutoMpg
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

class FacetWrapDemo : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            oneFacetDef(),
            oneFacet3cols(),
            oneFacet4rows()
//            rows(),
//            both()
        )
    }

    private fun oneFacetDef(): Map<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to AutoMpg.cylinders.name
        )
        return plotSpec
    }

    private fun oneFacet3cols(): Map<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to listOf(AutoMpg.cylinders.name),     // one facet variant
            "ncol" to 3
        )
        return plotSpec
    }

    private fun oneFacet4rows(): Map<String, Any> {
        val plotSpec = commonSpecs()
        plotSpec["facet"] = mapOf(
            "name" to "wrap",
            "facets" to AutoMpg.cylinders.name,
            "nrow" to 4
        )
        return plotSpec
    }

//    private fun rows(): Map<String, Any> {
//        val plotSpec = commonSpecs()
//        plotSpec["facet"] = mapOf(
//            "name" to "grid",
//            "y" to AutoMpg.origin.name
//        )
//        return plotSpec
//    }

//    private fun both(): Map<String, Any> {
//        val plotSpec = commonSpecs()
//        plotSpec["facet"] = mapOf(
//            "name" to "grid",
//            "x" to AutoMpg.cylinders.name,
//            "y" to AutoMpg.origin.name
//        )
//        return plotSpec
//    }

    private fun commonSpecs(): MutableMap<String, Any> {
        val spec = """
            {
                'kind': 'plot',
                'mapping': {
                    'x': "${AutoMpg.horsepower.name}",
                    'y': "${AutoMpg.mpg.name}"     
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