/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import demo.plot.common.data.Iris

class Dotplot {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            histodot(),
            coordFlip(),
            //facets(),
            withGroups(),
            dotplotParams(),
            statIdentity(),
            //emptyData(),
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

    private fun histodot(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'x': 'sepal length (cm)'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'method=histodot'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'dotplot'," +
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

    private fun facets(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'x': 'sepal length (cm)'," +
                "                'fill': 'target'" +
                "              }," +
                "   'facet': {'name': 'grid'," +
                "             'x': 'target'," +
                "             'x_order': 1," +
                "             'y_order': 1}," +
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

    private fun withGroups(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'x': 'sepal length (cm)'," +
                "                'fill': 'target'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Dotplot with groups'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'dotplot'," +
                "                 'method': 'histodot'," +
                "                 'bins': 20," +
                "                 'stackgroups': true," +
                "                 'color': 'black'" +
                "               }" +
                "             ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }

    private fun dotplotParams(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'x': 'sepal length (cm)'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Custom dotplot params'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'dotplot'," +
                "                 'stackdir': 'centerwhole'," +
                "                 'stackratio': 0.5," +
                "                 'dotsize': 2" +
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
                "                 'stat': 'identity'," +
                "                 'color': 'black'," +
                "                 'stroke': 5" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun emptyData(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': []}," +
                "   'mapping': {" +
                "                'x': 'x'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Empty dataset'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'dotplot'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }
}