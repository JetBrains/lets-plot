/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec
import jetbrains.datalore.plotDemo.data.Iris

class PositionStackFill {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            densityPlot(),
            densityPlot("stack"),
            densityPlot("fill"),
            histPlot(),
            histPlot("identity"),
            histPlot("fill"),
            withLabel("stack", vjust = 0.5),
            withLabel("fill", vjust = 0.5),
        )
    }

    private fun densityPlot(position: String? = null): MutableMap<String, Any> {
        val posSpec = position?.let { "                  'position' : '$it'," } ?: ""
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "             'x': 'sepal length (cm)'," +
                "             'fill': 'target'" +
                "           }," +

                "   'layers': [" +
                "               { $posSpec" +
                "                  'geom': 'density'," +
                "                  'color': 'black'," +
                "                  'alpha': 0.7" +
                "               }" +
                "           ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        plotSpec["ggtitle"] = mapOf("text" to "Position: ${position?.let { "$it" } ?: "def"}")
        return plotSpec
    }

    private fun histPlot(position: String? = null): MutableMap<String, Any> {
        val posSpec = position?.let { "                  'position' : '$it'," } ?: ""
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "             'x': 'sepal length (cm)'," +
                "             'fill': 'target'" +
                "           }," +

                "   'layers': [" +
                "               { $posSpec" +
                "                  'geom': 'histogram'," +
                "                  'color': 'black'," +
                "                  'alpha': 0.7" +
                "               }" +
                "           ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        plotSpec["ggtitle"] = mapOf("text" to "Position: ${position?.let { "$it" } ?: "def"}")
        return plotSpec
    }

    private fun withLabel(position: String, vjust: Double?): MutableMap<String, Any> {
        val spec = "{" +
                "   'ggtitle': {'text': 'Position: $position with vjust = $vjust'}," +
                "   'data': {" +
                "       'x': [1, 1, 2, 2, 1, 2, 2]," +
                "       'y': [1, 3, 2, 1, 2, -3, 2], " +
                "       'grp': ['a', 'b', 'a', 'b', 'c', 'c','d']" +
                "    }," +
                "   'mapping': {'x': 'x', 'y': 'y', 'group': 'grp'}," +
                "   'kind': 'plot'," +
                "   'layers': [" +
                "           {" +
                "               'geom': 'bar'," +
                "               'stat': 'identity'," +
                "               'mapping': {'fill': 'grp'}," +
                "               'position':{ 'name': '$position' }" +
                "           }," +
                "           {" +
                "               'geom': 'label'," +
                "               'mapping': {'label': 'y'}," +
                "               'position':{'name': '$position', 'vjust':  $vjust }" +
                "           }" +
                "   ]" +
                "}"

        return parsePlotSpec(spec)
    }
}