/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec

class PositionNudge {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            pointAndText(nudgeX = -0.2),
            pointAndText(nudgeX = 0.2),
            pointAndText(nudgeX = 0.2, nudgeY = -0.2),
            pointAndText(nudgeY = -0.2),
            pointAndText(nudgeY = 0.2),
        )
    }

    private fun pointAndText(nudgeX: Double? = null, nudgeY: Double? = null): MutableMap<String, Any> {
        val nudgeXSpec = nudgeX?.let { "'x':$nudgeX" }
        val nudgeYSpec = nudgeY?.let { "'y':$nudgeY" }
        val nudgeXY = listOfNotNull(nudgeXSpec, nudgeYSpec).joinToString()
        val posSpec = if (nudgeX != null || nudgeY != null) {
            "'position': {'name': 'nudge', ${nudgeXY} },"
        } else {
            ""
        }
        val posDescr = if (nudgeX != null || nudgeY != null) {
            "'nudge': ${nudgeXY}"
        } else {
            "def"
        }
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "             'x': 'x'," +
                "             'y': 'y'," +
                "             'label': 'y'" +
                "           }," +

                "   'layers': [" +
                "               {" +
                "                  'geom': 'point'," +
                "                  'size': 4" +
                "               }," +
                "               { $posSpec" +
                "                  'geom': 'text'" +
                "               }" +
                "           ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = mapOf(
            "x" to listOf(1, 1.5, 2, 2.5),
            "y" to listOf("a", "b", "c", "d"),
        )
        plotSpec["ggtitle"] = mapOf("text" to "Position $posDescr")
        return plotSpec
    }
}