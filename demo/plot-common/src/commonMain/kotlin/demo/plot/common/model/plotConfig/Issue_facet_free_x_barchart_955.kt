/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

/**
 * https://github.com/JetBrains/lets-plot/issues/955
 */
@Suppress("ClassName")
class Issue_facet_free_x_barchart_955 {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            case0(),  // free_x
            case1(),  // fixed scales
            case2(),  // free scales
        )
    }

    private fun case0(): MutableMap<String, Any> {
        val spec = """
            {
                'kind': 'plot',
                'mapping':  {'x': 'animal', 'y': 'weight'},
                'layers':   [{'geom': 'bar',
                               'stat': 'identity',
                               'size': 0.5,
                               'color': 'black'}],
                'facet':{'name': 'grid',
                          'x': 'animal_type', 
                          'y': 'diet',
                          'scales': 'free_x',
                          'x_order': 1,
                          'y_order': 1},
                 'theme': {'name': 'bw', 'panel_grid_minor': {'blank': true}},
                 'ggtitle': {'text': 'free_x'}                                      
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = DATA
        return plotSpec
    }

    private fun case1(): MutableMap<String, Any> {
        val spec = """
            {
                'kind': 'plot',
                'mapping':  {'x': 'animal', 'y': 'weight'},
                'layers':   [{'geom': 'bar',
                               'stat': 'identity',
                               'size': 0.5,
                               'color': 'black'}],
                'facet':{'name': 'grid',
                          'x': 'animal_type',
                          'y': 'diet',
                          'scales': 'fixed',
                          'x_order': 1,
                          'y_order': 1},
                 'theme': {'name': 'bw', 'panel_grid_minor': {'blank': true}},
                 'ggtitle': {'text': 'fixed'}                                      
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = DATA
        return plotSpec
    }

    private fun case2(): MutableMap<String, Any> {
        val spec = """
            {
                'kind': 'plot',
                'mapping':  {'x': 'animal', 'y': 'weight'},
                'layers':   [{'geom': 'bar',
                               'stat': 'identity',
                               'size': 0.5,
                               'color': 'black'}],
                'facet':{'name': 'grid',
                          'x': 'animal_type',
                          'y': 'diet',
                          'scales': 'free',
                          'x_order': 1,
                          'y_order': 1},
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