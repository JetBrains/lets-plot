/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.Iris

class Violin {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            mpgCylHwy456(),
            mpgCylHwy46(),
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'x': 'target'," +
                "                'y': 'sepal length (cm)'," +
                "                'fill': 'target'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'violin'," +
                "                 'alpha': 0.7," +
                "                 'draw_quantiles': [0.1, 0.5, 0.9]" +
                "               }" +
                "             ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }

    private fun withNan(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'class': [0, 0, 0, null, 1, 1, 1, 1]," +
                "             'value': [0, 0, 2, 2, 1, 1, 3, null]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'class'," +
                "                'y': 'value'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'violin'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun withGroups(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'class': ['A', 'A', 'A', 'A', 'A', 'A', 'B', 'B', 'B', 'B', 'B', 'B']," +
                "             'group': ['x', 'x', 'x', 'y', 'y', 'y', 'x', 'x', 'x', 'x', 'y', 'y']," +
                "             'value': [0, 0, 2, 1, 1, 3, 1, 3, 3, 5, 2, 4]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'class'," +
                "                'y': 'value'," +
                "                'fill': 'group'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'violin'," +
                "                 'draw_quantiles': [0.25, 0.5, 0.75]" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun mpgCylHwy456(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [4, 4, 4, 4, 6, 6, 6, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 4, 4, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 6, 6, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 4, 4, 4, 4, 6, 6, 6, 4, 4, 4, 4, 6, 6, 6, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 6, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 5, 5, 6, 6, 4, 4, 4, 4, 5, 5, 4, 4, 4, 4, 6, 6, 6]," +
                "             'y': [29, 29, 31, 30, 26, 26, 27, 26, 25, 28, 27, 25, 25, 25, 25, 24, 25, 27, 30, 26, 29, 26, 24, 24, 22, 22, 24, 24, 17, 22, 21, 23, 23, 19, 18, 17, 17, 17, 17, 19, 17, 19, 17, 17, 26, 25, 26, 24, 33, 32, 32, 29, 32, 34, 36, 36, 29, 26, 27, 30, 31, 26, 26, 28, 26, 29, 28, 27, 24, 24, 24, 22, 19, 20, 17, 19, 29, 27, 31, 32, 27, 26, 26, 25, 25, 17, 17, 20, 26, 26, 27, 28, 25, 24, 27, 25, 26, 23, 26, 26, 26, 26, 25, 27, 25, 27, 20, 20, 19, 17, 20, 29, 27, 31, 31, 26, 26, 28, 27, 29, 31, 31, 26, 26, 27, 30, 33, 35, 37, 35, 20, 20, 22, 17, 19, 18, 20, 29, 26, 29, 29, 24, 44, 29, 26, 29, 29, 29, 29, 23, 24, 44, 41, 29, 26, 28, 29, 29, 29, 28, 29, 26, 26, 26]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'violin'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun mpgCylHwy46(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [4, 4, 4, 4, 6, 6, 6, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 4, 4, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 6, 6, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 4, 4, 4, 4, 6, 6, 6, 4, 4, 4, 4, 6, 6, 6, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 6, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 6, 6, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6]," +
                "             'y': [29, 29, 31, 30, 26, 26, 27, 26, 25, 28, 27, 25, 25, 25, 25, 24, 25, 27, 30, 26, 29, 26, 24, 24, 22, 22, 24, 24, 17, 22, 21, 23, 23, 19, 18, 17, 17, 17, 17, 19, 17, 19, 17, 17, 26, 25, 26, 24, 33, 32, 32, 29, 32, 34, 36, 36, 29, 26, 27, 30, 31, 26, 26, 28, 26, 29, 28, 27, 24, 24, 24, 22, 19, 20, 17, 19, 29, 27, 31, 32, 27, 26, 26, 25, 25, 17, 17, 20, 26, 26, 27, 28, 25, 24, 27, 25, 26, 23, 26, 26, 26, 26, 25, 27, 25, 27, 20, 20, 19, 17, 20, 29, 27, 31, 31, 26, 26, 28, 27, 29, 31, 31, 26, 26, 27, 30, 33, 35, 37, 35, 20, 20, 22, 17, 19, 18, 20, 29, 26, 29, 29, 24, 44, 29, 26, 29, 29, 23, 24, 44, 41, 29, 26, 29, 29, 28, 29, 26, 26, 26]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'violin'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }
}