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
            coordFlip(),
            statIdentity(),
            withNan(),
        )
    }

    private fun basic(): MutableMap<String, Any> {
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
                "             'count': [2, 1]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'count'" +
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
                "   'data' : {'x': [0.5, 1.5, 2.5, null]," +
                "             'count': [0, 2, null, 0]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'count'" +
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
}