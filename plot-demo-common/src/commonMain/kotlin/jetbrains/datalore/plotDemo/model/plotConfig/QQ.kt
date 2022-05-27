/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.Iris

class QQ {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            onlySampleValues(),
            grouping(),
            withNan(),
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'sample': 'sepal length (cm)'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Basic demo'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'qq'," +
                "                 'distribution': 'norm'" +
                "               }," +
                "               {" +
                "                 'geom': 'qq_line'," +
                "                 'distribution': 'norm'" +
                "               }" +
                "             ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }

    private fun onlySampleValues(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'x': 'sepal width (cm)'," +
                "                'y': 'sepal length (cm)'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Only sample values'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'qq2'" +
                "               }," +
                "               {" +
                "                 'geom': 'qq2_line'" +
                "               }" +
                "             ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }

    private fun grouping(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'y': [-3, -1, 0, 1, 3, -2, 0, 1, 2, 4]," +
                "             'g': ['A', 'A', 'A', 'A', 'A', 'B', 'B', 'B', 'B', 'B']" +
                "            }," +
                "   'mapping': {" +
                "                'sample': 'y'," +
                "                'color': 'g'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Grouping'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'qq'" +
                "               }," +
                "               {" +
                "                 'geom': 'qq_line'," +
                "                 'quantiles': [0.1, 0.9]" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun withNan(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [5, 4, 3, 2, 1]," +
                "             'y': [null, 2, 1, 4, 3]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'With NaN values'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'qq2'" +
                "               }," +
                "               {" +
                "                 'geom': 'qq2_line'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }
}