/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.Iris
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

class TooltipAesList: PlotConfigDemoBase()  {

    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            basic(),
            tooltipAesList(),
            tooltipEmptyList()
        )
    }

    private fun basic(): Map<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'ggtitle': {'text' : 'No tooltip list'}," +
                "   'mapping': {" +
                "             'x': 'sepal length (cm)'," +
                "             'color': 'sepal width (cm)'," +
                "             'fill': 'target'" +
                "           }," +
                "   'layers': [" +
                "               {" +
                "                   'geom': 'area', " +
                "                   'stat': 'density'" +
                "               }" +
                "           ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec
    }

    private fun tooltipAesList(): Map<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'ggtitle': {'text' : 'Tooltip aes list = fill (target)'}," +
                "   'mapping': {" +
                "             'x': 'sepal length (cm)'," +
                "             'color': 'sepal width (cm)'," +
                "             'fill': 'target'" +
                "           }," +

                "   'layers': [" +
                "               {" +
                "                   'geom': { 'name': 'area'," +
                "                             'tooltip': ['fill']" +
                "                           }," +
                "                   'stat': 'density'" +
                "               }" +
                "           ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec
    }

    private fun tooltipEmptyList(): Map<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'ggtitle': {'text' : 'Tooltip list = []'}," +
                "   'mapping': {" +
                "             'x': 'sepal length (cm)'," +
                "             'color': 'sepal width (cm)'," +
                "             'fill': 'target'" +
                "           }," +

                "   'layers': [" +
                "               {" +
                "                   'geom': { 'name': 'area'," +
                "                             'tooltip': []" +
                "                           }," +
                "                   'stat': 'density'" +
                "               }" +
                "           ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec
    }
}