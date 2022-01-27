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
            data132Violin(),
            data132ViolinIdentity(),
            data132Boxplot(),
            data123Violin(),
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

    private fun data132Violin(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [1, 3, 2]," +
                "             'y': [2, 0, 1]" +
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

    private fun data132ViolinIdentity(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [1, 1, 1, 3, 3, 3, 2, 2, 2]," +
                "             'y': [4, 3, 2, 5, 4, 3, 3, 2, 1]," +
                "             'vw': [0, 1, 0, 0, 1, 0, 0, 1, 0]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'violin'," +
                "                 'stat': 'identity'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun data132Boxplot(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [1, 3, 2]," +
                "             'y': [2, 0, 1]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'boxplot'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun data123Violin(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [1, 2, 3]," +
                "             'y': [2, 1, 0]" +
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