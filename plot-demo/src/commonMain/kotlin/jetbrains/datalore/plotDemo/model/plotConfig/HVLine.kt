/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.Iris
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

class HVLine : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            hline(),
            vline()
        )
    }

    fun hline(): Map<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'ggtitle': {'text': 'HLine'}," +
                "   'layers': [" +
                "               { " +
                "                   'geom': 'point', " +
                "                   'mapping': {" +
                "                                 'x': 'sepal length (cm)'," +
                "                                 'y': 'sepal width (cm)'" +
                "                              }" +
                "               }," +
                "               { " +
                "                   'geom': { " +
                "                              'name' : 'hline'," +
                "                              'data': { 'hl': [3.0] }" +
                "                           }," +
                "                   'mapping': {'yintercept': 'hl'}, " +
                "                   'color': 'red'" +
                "               }" +
                "           ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec
    }

    fun vline(): Map<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'ggtitle': {'text': 'VLine'}," +
                "   'layers': [" +
                "               { " +
                "                   'geom': 'point', " +
                "                   'mapping': {" +
                "                                 'x': 'sepal length (cm)'," +
                "                                 'y': 'sepal width (cm)'" +
                "                              }" +
                "               }," +
                "               { " +
                "                   'geom': { " +
                "                              'name' : 'vline'," +
                "                              'data': { 'vl': [5.0, 7.0] }" +
                "                           }," +
                "                   'mapping': {'xintercept': 'vl'}, " +
                "                   'color': 'red'" +
                "               }" +
                "           ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec
    }
}