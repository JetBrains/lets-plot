/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec
import jetbrains.datalore.plotDemo.data.Iris

class Density2df {
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
                "             'fill': '..level..'" +
                "           }," +

                "   'layers': [" +
                "               {" +
                "                  'geom': 'density2df'" +
                "               }" +
                "           ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }
}