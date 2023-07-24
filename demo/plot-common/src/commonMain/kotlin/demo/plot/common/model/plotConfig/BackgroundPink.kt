/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import demo.plot.common.data.Iris

class BackgroundPink {
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
                "   'theme': { 'plot_background': {'fill': 'pink', 'blank': false}, 'legend_background': {'fill': 'pink', 'blank': false}}," +
//                "   'theme': { 'axis_title_x': 'blank'}," +
                "   'ggtitle': { 'text': 'background - pink'}" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }
}