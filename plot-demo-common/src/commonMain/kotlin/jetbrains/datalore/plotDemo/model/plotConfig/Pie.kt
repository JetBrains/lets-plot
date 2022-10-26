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
            pieStatCount()
        )
    }

    private val data = mapOf(
        "name" to ('A'..'H').toList() + 'B',
        "value" to listOf(160, 90, 34, 44, 21, 86, 15, 100, 20)
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
                "                  'geom': 'pie', " +
                "                  'stat': 'identity', " +
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

    private fun pieStatCount(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'fill': 'name'," +
                "                'weight': 'value'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                  'geom': 'pie', " +
                "                  'x':0, 'y':0, " +
                "                  'size': 40" +
                "               }" +
                "             ]," +
                "   'data_meta': {" +
                "      'mapping_annotations': [" +
                "          {" +
                "               'aes': 'fill'," +
                "               'annotation': 'as_discrete'," +
                "               'parameters': {" +
                "                   'label':'name', 'order_by': '..count..', 'order': -1 " +
                "               }" +
                "          }" +
                "       ]" +
                "   }" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = data
        return plotSpec
    }
}