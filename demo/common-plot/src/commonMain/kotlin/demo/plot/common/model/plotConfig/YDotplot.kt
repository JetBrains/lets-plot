/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demo.plot.common.data.Iris
import demoAndTestShared.parsePlotSpec

class YDotplot {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            histodot(),
            coordFlip(),
            //groupingWithStackgroups(),
            groupingWithoutStackgroups(),
            ydotplotParams(),
            statIdentity(),
            //checkStackCapacity(),
            //checkHints(),
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'x': 'target'," +
                "                'y': 'sepal length (cm)'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Default ydotplot'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'ydotplot'" +
                "               }" +
                "             ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }

    private fun histodot(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'x': 'target'," +
                "                'y': 'sepal length (cm)'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'method=histodot'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'violin'" +
                "               }," +
                "               {" +
                "                 'geom': 'ydotplot'," +
                "                 'method': 'histodot'" +
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
                "                'x': 'target'," +
                "                'y': 'sepal length (cm)'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'coord_flip()'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'ydotplot'," +
                "                 'binwidth': 0.2," +
                "                 'dotsize': 0.2" +
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

    private fun groupingWithoutStackgroups(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'class': ['A', 'A', 'A', 'A', 'A', 'A', 'A', 'B', 'B', 'B', 'B', 'B', 'B']," +
                "             'group': ['x', 'x', 'x', 'x', 'y', 'y', 'y', 'x', 'x', 'x', 'x', 'y', 'y']," +
                "             'value': [0, 0, 0, 1, 1, 1, 2, 1, 2, 2, 3, 2, 3]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'class'," +
                "                'y': 'value'," +
                "                'fill': 'group'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Grouping without stackgroups'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'ydotplot'," +
                "                 'method': 'histodot'," +
                "                 'binwidth': 0.25" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun groupingWithStackgroups(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'class': ['A', 'A', 'A', 'A', 'A', 'A', 'A', 'B', 'B', 'B', 'B', 'B', 'B']," +
                "             'group': ['x', 'x', 'x', 'x', 'y', 'y', 'y', 'x', 'x', 'x', 'x', 'y', 'y']," +
                "             'value': [0, 0, 0, 1, 1, 1, 2, 1, 2, 2, 3, 2, 3]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'class'," +
                "                'y': 'value'," +
                "                'fill': 'group'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Grouping with stackgroups'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'ydotplot'," +
                "                 'method': 'histodot'," +
                "                 'binwidth': 0.25," +
                "                 'stackgroups': true" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun ydotplotParams(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'x': 'target'," +
                "                'y': 'sepal length (cm)'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Custom ydotplot params'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'ydotplot'," +
                "                 'stackdir': 'right'," +
                "                 'stackratio': 0.5," +
                "                 'dotsize': 2," +
                "                 'stroke': 2," +
                "                 'color': '#f03b20'," +
                "                 'fill': '#ffeda0'" +
                "               }" +
                "             ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }

    private fun statIdentity(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [null, 'C', 'C', 'C', 'A', 'A', 'B', 'B']," +
                "             'y': [0, null, 2, 3, 0, 1, 1, 2]," +
                "             'count': [2, 1, null, 3, 3, 1, 2, 0]," +
                "             'binwidth': [0.25, 0.25, 0.25, null, 0.25, 0.25, 0.25, 0.25]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'," +
                "                'stacksize': 'count'," +
                "                'binwidth': 'binwidth'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'NaNs in data, stat=identity'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'ydotplot'," +
                "                 'stat': 'identity'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun checkStackCapacity(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'y': [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]" +
                "            }," +
                "   'mapping': {" +
                "                'y': 'y'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Check stackCapacity'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'ydotplot'," +
                "                 'binwidth': 1.0," +
                "                 'dotsize': 0.3" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun checkHints(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'class': ['A', 'A', 'A', 'A', 'A', 'A', 'A', 'B', 'B', 'B', 'B', 'B', 'B']," +
                "             'group': ['x', 'x', 'x', 'x', 'y', 'y', 'y', 'x', 'x', 'x', 'x', 'y', 'y']," +
                "             'value': [0, 0, 0, 1, 1, 1, 2, 1, 2, 2, 3, 2, 3]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'class'," +
                "                'y': 'value'," +
                "                'fill': 'group'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Check hints'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'ydotplot'," +
                "                 'binwidth': 0.25," +
                "                 'stackratio': 0.75," +
                "                 'stackdir': 'left'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }
}