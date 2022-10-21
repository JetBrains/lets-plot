/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec

class Pie {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
             pie(hole = 0.0),
             pie(hole = 0.2),
        )
    }

    private val data = mapOf(
        "name" to ('A'..'H').toList(),
        "value" to  listOf(160, 90, 34, 44, 21, 86, 15, 100)
    )
    private fun pie(hole: Double): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'fill': 'name'," +
                "                'slice': 'value'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'pie', " +
                "                  'x':0, 'y':0, " +
                "                  'hole': $hole," +
                "                  'size': 40" +
                "               }" +
                "             ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = data
        return plotSpec
    }
}