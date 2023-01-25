/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec

class PositionStack {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            pointPlot(listOf("A", "B", "C", "A", "C", "B", "C", "A", "A")),
            pointPlot(listOf(1.0, 2.0, 3.0, 1.0, 3.0, 2.0, 3.0, 1.0, 1.0)),
            pointPlot(listOf(1.0, 2.0, 3.0, 1.0, 3.0, 2.0, 3.0, 1.0, 1.0), true),
        )
    }

    private fun pointPlot(groups: List<Any>, mapGroups: Boolean = false): MutableMap<String, Any> {
        val groupsData = "[${groups.joinToString(", ") { group ->
            if (group::class.simpleName == "String") "\"${group}\"" else group.toString()
        }}]"
        val mapping = if (mapGroups) "{'color': 'g', 'group': 'g'}" else "{'color': 'g'}"
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data': {" +
                "             'x': [0, 0, 0, 1, 1, 1, 2, 2, 2]," +
                "             'y': [3, 2, 1, 3, 1, 2, 1, 3, 2]," +
                "             'g': $groupsData" +
                "           }," +
                "   'mapping': {" +
                "             'x': 'x'," +
                "             'y': 'y'" +
                "           }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'point'," +
                "                 'size': 10," +
                "                 'position': {'name': 'stack'}," +
                "                 'mapping': $mapping" +
                "               }" +
                "           ]" +
                "}"

        return HashMap(parsePlotSpec(spec))
    }
}