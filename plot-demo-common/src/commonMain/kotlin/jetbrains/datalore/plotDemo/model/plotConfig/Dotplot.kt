/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.Iris

class Dotplot {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic()
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'x': 'sepal length (cm)'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'histogram'," +
                "                 'binwidth': 0.2," +
                "                 'color': 'black'," +
                "                 'fill': '#08519c'" +
                "               }," +
                "               {" +
                "                 'geom': 'dotplot'," +
                "                 'binwidth': 0.2," +
                "                 'color': 'black'," +
                "                 'fill': '#de2d26'" +
                "               }" +
                "             ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }
}