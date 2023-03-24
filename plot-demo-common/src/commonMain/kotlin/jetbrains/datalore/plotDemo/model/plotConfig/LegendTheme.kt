/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.AutoMpg

class LegendTheme {

    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            defaultPosition(),
            topRightHoriz(),
            multiLineLegendItems()
        )
    }

    private fun defaultPosition(): MutableMap<String, Any> {
        val plotSpec = plotSpec("Legend default")
        return plotSpec
    }

    private fun topRightHoriz(): MutableMap<String, Any> {
        val plotSpec = plotSpec("Top-right, horizontal")
        plotSpec["theme"] = themeTopRightHoriz()
        return plotSpec
    }

    private fun multiLineLegendItems(): MutableMap<String, Any> {
        val plotSpec = plotSpec("Multiline legend items")
        plotSpec["scales"] = listOf(colorScaleLabels(), shapeScaleLabels())
        val legendTextSize = """{ 'legend_text': { 'size': 15, 'blank':  false } }""".trimIndent()
        plotSpec["theme"] = parsePlotSpec(legendTextSize)
        // plotSpec["theme"] = themeTopRightHoriz()
        return plotSpec
    }


    companion object {
        private fun plotSpec(title: String): MutableMap<String, Any> {
            val spec = """
        {
           'kind': 'plot',
           'ggtitle': {'text' : '$title'},
           'mapping': {
                         'x': 'engine displacement (cu. inches)',
                         'y':  'engine horsepower',
                         'color': 'miles per gallon',
                         'shape': 'origin of car'
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

        private fun themeTopRightHoriz(): Map<String, Any> {
            val spec = """
    {  'legend_position': [1, 1],
                'legend_justification': [1, 1],
                'legend_direction': 'horizontal'}                
            """.trimIndent()

            return parsePlotSpec(spec)
        }

        private fun colorScaleLabels(): Map<String, Any> {
            val spec = """
            {
                'name': 'The origin \nof car',
                'aesthetic': 'shape',
                'labels': [
                    'a)\nUS',
                    'b)\nAsia\n(Japan, Korea...)',
                    'c) Europe'
                ],
                'discrete': true
            }
            """.trimIndent()
            return parsePlotSpec(spec)
        }
        private fun shapeScaleLabels(): Map<String, Any> {
            val spec = """
            {
                'name': 'Miles per gallon',
                'aesthetic': 'color',
                'breaks': [40,30,20,10],
                'labels': [
                    '40\n\n(mpg)',
                    '30 (mpg)',
                    '20\n(mpg)',
                    '10 (mpg)' 
                ]
            }
            """.trimIndent()
            return parsePlotSpec(spec)
        }
    }
}
