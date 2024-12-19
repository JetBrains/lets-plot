/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demo.plot.common.data.Iris
import demoAndTestShared.parsePlotSpec

class Density2d {
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
                "             'y': 'sepal width (cm)'," +
                "             'color': 'target'" +
                "           }," +

                "   'layers': [" +
                "               {" +
                "                  'geom': 'density2d'" +
                "               }" +
                "           ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }
}