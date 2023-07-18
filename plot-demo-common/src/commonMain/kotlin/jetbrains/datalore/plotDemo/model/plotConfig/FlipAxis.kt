/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec

class FlipAxis {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            pointsAndSmooth(false),
            pointsAndSmooth(true),
            pointsAndSmooth(false, yOrientation = true),
        )
    }

    private fun pointsAndSmooth(flip: Boolean, yOrientation: Boolean = false): MutableMap<String, Any> {
        val coordSpec = when {
            flip -> "'coord': {'name': 'flip', 'flip': true},"
            else -> ""
        }

        val mappingSpec = when {
            yOrientation -> "'y': 'x', 'x': 'y'"
            else -> "'x': 'x', 'y': 'y'"
        }

        val spec = """
{
 'mapping': {$mappingSpec},
 'kind': 'plot',
 $coordSpec
 'layers': [
   {
       'geom': 'point',
       'color': 'black',
       'alpha': 0.6,
       'size': 5},
   {'geom': 'smooth' ${if (yOrientation) ", 'orientation': 'y'" else ""}
    }],
   'ggtitle': {'text': 'Flipped: $flip ${if(yOrientation) ", y-orientation" else ""}'}
 }
         """.trimIndent()

        val map = parsePlotSpec(spec)
        map["data"] = data
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


//        private val data = mapOf(
//            "x" to listOf(-7.0),
//            "y" to listOf(0.0)
//        )
        //        private val data = mapOf(
//            "x" to listOf(0.0, 0.0),
//            "y" to listOf(0.0, 0.4)
//        )


        private fun cartesianWithLimits(
            xLim: Pair<Double, Double>?,
            yLim: Pair<Double, Double>?
        ): MutableMap<String, Any> {
            val spec =
                """
            |{
            |   'coord': {
            |       'name': 'cartesian',
            |       'xlim': ${xLim?.let { "[${xLim.first}, ${xLim.second}]" } ?: "null"},
            |       'ylim': ${yLim?.let { "[${yLim.first}, ${yLim.second}]" } ?: "null"}
            |   }
            |}   
        """.trimMargin()
            return parsePlotSpec(spec)
        }
    }
}