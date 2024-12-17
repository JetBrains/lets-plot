/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

/**
 * See issue:
 * https://github.com/JetBrains/lets-plot/issues/955
 */
class FacetWrapFreeDiscreteScale {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            twoFacetsFreeX(),
            fourFacetsFreeX(),
            fourFacetsFreeAll(),

            // Y-orientation
            twoFacetsFreeX(orientationY = true),
            fourFacetsFreeX(orientationY = true),
            fourFacetsFreeAll(orientationY = true),
        )
    }

    private fun twoFacetsFreeX(orientationY: Boolean = false): MutableMap<String, Any> {
        val mapping = if (!orientationY) {
            "{'x': 'animal', 'y': 'weight'}"
        } else {
            "{'y': 'animal', 'x': 'weight'}"
        }
        val facet = if (!orientationY) {
            "{'name': 'wrap', 'facets': 'animal_type', 'scales': 'free_x' }"
        } else {
            "{'name': 'wrap', 'facets': 'animal_type', 'scales': 'free_y' }"
        }

        val spec = """
            {
                'kind': 'plot',
                'mapping':  $mapping,
                'layers':   [{'geom': 'bar',
                               'stat': 'identity',
                               ${if (orientationY) "'orientation': 'y'," else ""}
                               'size': 0.5,
                               'color': 'black'}],
                'facet': $facet,
                 'theme': {'name': 'bw', 'panel_grid_minor': {'blank': true}},
                 'ggtitle': {'text': '${if (orientationY) "free_y" else "free_x"}'}                                      
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = DATA
        return plotSpec
    }

    private fun fourFacetsFreeX(orientationY: Boolean = false): MutableMap<String, Any> {
        val mapping = if (!orientationY) {
            "{'x': 'animal', 'y': 'weight' }"
        } else {
            "{'y': 'animal', 'x': 'weight' }"
        }
        val facet = if (!orientationY) {
            "{'name': 'wrap', 'facets': ['animal_type', 'diet'], 'ncol' : 2, 'scales': 'free_x' }"
        } else {
            "{'name': 'wrap', 'facets': ['animal_type', 'diet'], 'ncol' : 2, 'scales': 'free_y' }"
        }

        val spec = """
            {
                'kind': 'plot',
                'mapping':  $mapping,
                'layers':   [{'geom': 'bar',
                               'stat': 'identity',
                               ${if (orientationY) "'orientation': 'y'," else ""}
                               'size': 0.5,
                               'color': 'black'}],
                'facet': $facet,
                 'theme': {'name': 'bw', 'panel_grid_minor': {'blank': true}},
                 'ggtitle': {'text': '${if (orientationY) "free_y" else "free_x"}'}                                      
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = DATA
        return plotSpec
    }

    private fun fourFacetsFreeAll(orientationY: Boolean = false): MutableMap<String, Any> {
        val mapping = if (!orientationY) {
            "{'x': 'animal', 'y': 'weight' }"
        } else {
            "{'y': 'animal', 'x': 'weight' }"
        }

        val facet = if (!orientationY) {
            "{'name': 'wrap', 'facets': ['animal_type', 'diet'], 'ncol' : 2, 'scales': 'free' }"
        } else {
            "{'name': 'wrap', 'facets': ['animal_type', 'diet'], 'ncol' : 2, 'scales': 'free' }"
        }

        val spec = """
            {
                'kind': 'plot',
                'mapping': $mapping,
                'layers':   [{'geom': 'bar',
                               'stat': 'identity',
                                ${if (orientationY) "'orientation': 'y'," else ""}
                               'size': 0.5,
                               'color': 'black'}],
                'facet': $facet,
                 'theme': {'name': 'bw', 'panel_grid_minor': {'blank': true}},
                 'ggtitle': {'text': 'free all'}                                      
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = DATA
        return plotSpec
    }

    private companion object {
        //            animal_type   animal  weight  diet
//                    pet      cat       5  carnivore
//                    pet      dog      10  carnivore
//                    pet   rabbit       2  herbivore
//                    pet  hamster       1  herbivore
//            farm_animal      cow     500  herbivore
//            farm_animal      pig     100  carnivore
//            farm_animal    horse     700, herbivore
        val DATA = mapOf(
            "animal_type" to listOf(
                "pet",
                "pet",
                "pet",
                "pet",
                "farm_animal",
                "farm_animal",
                "farm_animal",
            ),
            "animal" to listOf(
                "cat",
                "dog",
                "rabbit",
                "hamster",
                "cow",
                "pig",
                "horse",
            ),
            "diet" to listOf(
                "carnivore",
                "carnivore",
                "herbivore",
                "herbivore",
                "herbivore",
                "carnivore",
                "herbivore",
            ),
            "weight" to listOf(
                5,
                10,
                2,
                1,
                500,
                100,
                700,
            )
        )
    }
}