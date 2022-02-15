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
            basic(),
            comparisonWithHistogram(),
            coordFlip(),
            statIdentity(),
            withNan(),
            withNanIdentity(),
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'x': 'sepal length (cm)'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Default dotplot'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'dotplot'" +
                "               }" +
                "             ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }

    private fun comparisonWithHistogram(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'x': 'sepal length (cm)'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Comparison with histogram'" +
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
                "                 'method': 'histodot'," +
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

    private fun coordFlip(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'x': 'sepal length (cm)'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'coord_flip()'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'dotplot'," +
                "                 'binwidth': 0.2" +
                "               }" +
                "             ]," +
                "   'coord': {" +
                "              'name': 'flip'," +
                "              'flip': true" +
                "            }" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }

    private fun statIdentity(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [0.5, 1.5]," +
                "             'count': [2, 1]," +
                "             'binwidth': [1, 1]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'stacksize': 'count'," +
                "                'binwidth': 'binwidth'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'stat=identity'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'dotplot'," +
                "                 'stat': 'identity'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun withNan(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [0.0, 1.0, 3.0, null]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'NaNs in data'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'dotplot'," +
                "                 'binwidth': 2.0" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun withNanIdentity(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [0.5, 1.5, 2.5, null]," +
                "             'count': [0, 2, null, 0]," +
                "             'binwidth': [1, 1, 1, 1]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'stacksize': 'count'," +
                "                'binwidth': 'binwidth'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'NaNs in data, stat=identity'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'dotplot'," +
                "                 'stat': 'identity'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }
}