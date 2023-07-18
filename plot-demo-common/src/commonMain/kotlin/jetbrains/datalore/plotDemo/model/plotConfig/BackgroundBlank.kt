/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec
import jetbrains.datalore.plotDemo.data.Iris

class BackgroundBlank {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            sepalLength()
        )
    }

    private fun sepalLength(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "             'x': 'sepal length (cm)'," +
                "             'group': 'target'," +
                "             'color': 'sepal width (cm)'," +
                "             'fill': 'target'" +
                "           }," +

                "   'layers': [" +
                "               {" +
                "                  'geom': 'area'," +
                "                   'stat': 'density'," +
                "                   'position' : 'identity'," +
                "                   'alpha': 0.7" +
                "               }" +
                "           ]" +
                "           ," +
                "   'theme': { 'plot_background': 'blank', 'legend_background': 'blank' }," +
                "   'ggtitle': { 'text': 'background - blank'}" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }
}