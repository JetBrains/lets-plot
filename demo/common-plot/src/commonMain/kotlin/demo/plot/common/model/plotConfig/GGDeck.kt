/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

open class GGDeck {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            deckOfTwo(),
        )
    }

    companion object {
        private fun randomData(n: Int): Map<String, List<*>> {
            val random = kotlin.random.Random(42)
            val x = (0 until n).toList()

            var cumSum = 0.0
            val y1 = List(n) {
                // Box-Muller transform for normal distribution
                val u1 = random.nextDouble()
                val u2 = random.nextDouble()
                val z0 = kotlin.math.sqrt(-2.0 * kotlin.math.ln(u1)) * kotlin.math.cos(2.0 * kotlin.math.PI * u2)

                cumSum += z0
                cumSum
            }

            val y2 = List(n) { random.nextDouble(100.0, 200.0) }

            return mapOf(
                "x" to x,
                "y1" to y1,
                "y2" to y2,
            )
        }
    }

    private fun deckOfTwo(): MutableMap<String, Any> {
        val data = randomData(50)

//        'theme': {'axis_line_x': {'color': 'transparent'}},
        val lineSpecRaw = """
            {
               'mapping': {'x': 'x', 'y': 'y1'},
               'guides': {'y': {'title': 'Line (left)'}},
               'kind': 'plot',
               'layers': [
                    {
                        'geom': 'line',
                        'color': 'blue'
                    }
               ]
           }            
        """.trimIndent()

        val linePlotSpec = HashMap(parsePlotSpec(lineSpecRaw))
        linePlotSpec["data"] = data

        val pointsSpecRaw = """
            {   
               'mapping': {'x': 'x', 'y': 'y2'},
               'guides': {'y': {'title': 'Points (right)'}},
               'theme': {
                    'axis_text_y': {'color': 'blue'},
                    'axis_ticks_y': {'color': 'blue'},
                    'axis_line_y': {'color': 'blue'}
                },  
               'kind': 'plot',
               'scales': [{'aesthetic': 'y', 'position': 'right'}],
               'layers': [
                    {
                        'geom': 'point',
                        'color': 'red'
                    }
               ]
           }            
        """.trimIndent()

        val pointsPlotSpec = HashMap(parsePlotSpec(pointsSpecRaw))
        pointsPlotSpec["data"] = data

//        'theme': {'name': 'classic', 'axis_title_x': 'blank'},

        val deckSpecRaw = """
            {
                'theme': {'name': 'classic'},
                'ggtoolbar': {},
                'kind': 'subplots',
                'layout': {'name': 'deck'}
            }    
        """.trimIndent()

        val deckSpec = HashMap(parsePlotSpec(deckSpecRaw))
        deckSpec["figures"] = listOf(
            linePlotSpec,
            pointsPlotSpec,
        )

        return deckSpec
    }
}
