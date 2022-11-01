/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec

class Pie {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            pie(hole = 0.0, useCountStat = false),
            pie(hole = 0.2),
            pie(hole = 0.5, withOrdering = true),
        )
    }

    private val data = mapOf(
        "name" to ('A'..'H').toList() + 'B',
        "value" to listOf(160, 90, 34, 44, 21, 86, 15, 100, 20)
    )

    private fun pie(hole: Double, useCountStat: Boolean = true, withOrdering: Boolean = false): MutableMap<String, Any> {
        val stat = if (useCountStat) "count2d" else "identity"
        val mapping = if (useCountStat) {
            "'fill': 'name', 'weight': 'value'"
        } else {
            "'fill': 'name', 'slice': 'value'"
        }
        val tooltipContent = if (useCountStat) {
            "'tooltip_lines': [ '@|^fill', 'count|@{..count..} (@{..prop..})', 'total|@{..sum..}' ]," +
            "'tooltip_formats': [{'field': '@{..prop..}', 'format': '.0%'}]"
        } else ""
        val ordering = if (useCountStat && withOrdering) {
                ",   'data_meta': {" +
                        "      'mapping_annotations': [" +
                        "          {" +
                        "               'aes': 'fill'," +
                        "               'annotation': 'as_discrete'," +
                        "               'parameters': {" +
                        "                   'label':'name', 'order_by': '..count..', 'order': -1 " +
                        "               }" +
                        "          }" +
                        "       ]" +
                        "   }"
        } else ""
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'ggsize': {'width': 400, 'height': 300}," +
                "   'ggtitle': {'text' : 'stat=$stat ${if (withOrdering) "with ordering" else ""}; hole=$hole'}," +
                "   'theme': { 'line': 'blank', 'axis': 'blank' }," +
                "   'mapping': { $mapping }," +
                "   'layers': [" +
                "               {" +
                "                  'geom': 'pie', " +
                "                  'stat': '$stat', " +
                "                  'hole': $hole," +
                "                  'tooltips': { $tooltipContent }" +
                "               }" +
                "             ]" +
                "    $ordering" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = data
        return plotSpec
    }

}