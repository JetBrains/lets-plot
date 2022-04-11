/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec

class YOrientation {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'bar'," +
                "                 'orientation': 'y'," +
                "                 'fill': 'white', 'color': 'gray'" +
                "               }," +
                "               {" +
                "                 'geom': 'point'," +
                "                 'size': 5" +
                "               }" +
                "             ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = mapOf<String, Any>(
            "x" to listOf(1, 2, 3),
            "y" to listOf('a', 'a', 'b')
        )
        return plotSpec
    }
}