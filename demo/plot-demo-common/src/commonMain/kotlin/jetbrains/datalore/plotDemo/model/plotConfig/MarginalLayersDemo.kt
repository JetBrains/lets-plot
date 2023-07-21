/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec

class MarginalLayersDemo(
    private val coordFixed: Boolean = false,
    private val boxplot: Boolean = false,
) {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            marginalPlot(listOf("l"), listOf(0.1), boxplot),
            marginalPlot(listOf("r"), listOf(0.2), boxplot),
            marginalPlot(listOf("l", "r"), listOf(0.1, 0.2), boxplot),

            marginalPlot(listOf("t"), listOf(0.1), boxplot),
            marginalPlot(listOf("b"), listOf(0.2), boxplot),
            marginalPlot(listOf("t", "b"), listOf(0.1, 0.2), boxplot),

            marginalPlot(listOf("t", "r"), listOf(0.1, 0.2), boxplot),
            marginalPlot(listOf("b", "l"), listOf(0.2, 0.1), boxplot),
            marginalPlot(listOf("t", "r", "b", "l"), listOf(0.1, 0.2, 0.2, 0.1), boxplot),
        )
    }

    private fun marginalPlot(sides: List<String>, sizes: List<Double>, boxplot: Boolean): MutableMap<String, Any> {
        val spec = plotSpec(sides, sizes, boxplot)
        val map = parsePlotSpec(spec)
        map["data"] = data
        if (coordFixed) {
            map["coord"] = mapOf("name" to "fixed")
        }
        return map
    }


    companion object {
        private val x = listOf(
            -0.85682293, -2.3911234, -2.42744314, -1.94456221, -3.08116168,
            -2.82149096, -2.75909911, -0.5976029, -2.28422114, -2.87574481,
            -2.44764864, 0.62140045, -2.78634844, -0.77798494, -1.84775972,
            -0.68368036, -1.16449909, -2.54485003, -2.2036209, -1.68242049
        )
        private val y = listOf(
            -1.52369374, 0.10800142, -0.04456882, 0.24980294, 0.07656286,
            1.18430493, 0.60070862, -1.3039589, 0.54056665, 0.4756451,
            1.23440038, -2.64721071, 0.86629033, -1.01436946, -0.30687369,
            -1.23137015, -0.41737117, 0.54053481, -0.71151953, -1.37503288
        )

        private val data = mapOf(
            "x" to x.map { it - 5 },
            "y" to y.map { it / 3 }
        )
        private val xLim = Pair(-7.5, -6.0)
        private val yLim = Pair(-0.4, 0.2)


        private fun plotSpec(marginSides: List<String>, marginSizes: List<Double>, boxplot: Boolean): String {
            return """
            {
                'mapping': {'x': 'x', 'y': 'y'},
                'kind': 'plot',
                'layers': [
                    {
                        'geom': 'point',
                        'color': 'black',
                        'alpha': 0.6,
                        'size': 5
                    },
                    ${marginalSpecs(marginSides, marginSizes, boxplot)}
                ],
                'ggtitle': {'text': 'Margins: $marginSides'}
            }
         """.trimIndent()
        }

        private fun marginalSpecs(sides: List<String>, sizes: List<Double>, boxplot: Boolean): String {
            val l = ArrayList<String>()
            for ((i, side) in sides.withIndex()) {
                l.add(marginSpec(side, sizes[i], boxplot))
            }
            return l.joinToString(",", "", "")
        }

        private fun marginSpec(side: String, size: Double, boxplot: Boolean): String {
            return when (boxplot) {
                true -> """
                             ${marginalBoxplot(side, size)}
                        """
                false -> """
                            ${marginalHist(side, size)},
                            ${marginalDensity(side, size)}
                        """
            }.trimIndent()

//            return """
//                ${marginalHist(side, size)},
//                ${marginalDensity(side, size)}
//            """.trimIndent()
//            return """
//                ${marginalBoxplot(side, size)}
//            """.trimIndent()
        }

        private fun marginalHist(side: String, size: Double): String {
            val orientation = when (side) {
                "l", "r" -> "y"
                else -> "x"
            }
            val aesY = when (orientation) {
                "x" -> "y"
                else -> "x"
            }

            return """
                {
                    'geom': 'histogram', 'bins' : 10, 'color' : 'white',
                    'mapping': {'$aesY': '..density..'},
                    'marginal' : true,
                    'margin_side' : '$side',
                    'margin_size' : $size,
                    'orientation' : '$orientation'
            }
            """.trimIndent()
        }

        private fun marginalDensity(side: String, size: Double): String {
            val orientation = when (side) {
                "l", "r" -> "y"
                else -> "x"
            }
            return """
                {
                    'geom': 'density', 'color' : 'red', 'fill' : 'blue', 'alpha' : 0.1,
                    'marginal' : true,
                    'margin_side' : '$side',
                    'margin_size' : $size,
                    'orientation' : '$orientation'
            }
            """.trimIndent()
        }

        private fun marginalBoxplot(side: String, size: Double): String {
            val orientation = when (side) {
                "l", "r" -> "x"
                else -> "y"
            }

            val fixed = when (orientation) {
                "x" -> "x"
                else -> "y"
            }

            return """
                {
                    'geom': 'boxplot', '$fixed' : 0.0,
                    'marginal' : true,
                    'margin_side' : '$side',
                    'margin_size' : $size,
                    'orientation' : '$orientation'
            }
            """.trimIndent()
        }

    }
}