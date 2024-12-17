/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demo.plot.common.data.Iris
import demoAndTestShared.parsePlotSpec

open class MultiLineTooltip {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            oneLine(),
            oneLineDigit(),
            twoLines(),
            threeLines()
        )
    }

    companion object {
        private const val OUR_DATA = "{'name': ['Aa','HH', 'Jj', 'hj', 'jp', 'aq', 'aa']}"

        fun oneLine(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'data': " + OUR_DATA +
                    "           ," +
                    "   'mapping': {" +
                    "             'x' : 'name', 'color' : 'name'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'point'" +
                    "               }" +
                    "           ]" +
                    "}"

            return parsePlotSpec(spec)
        }

        fun oneLineDigit(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'data': " + OUR_DATA +
                    "           ," +
                    "   'mapping': {" +
                    "             'x': 'name'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'bar'" +
                    "               }" +
                    "           ]" +
                    "}"

            return parsePlotSpec(spec)
        }

        fun twoLines(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'data': " + OUR_DATA +
                    "           ," +
                    "   'mapping': {" +
                    "             'x': 'name'," +
                    "              'fill' : 'name'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'bar'" +
                    "               }" +
                    "           ]" +
                    "}"

            return parsePlotSpec(spec)
        }

        fun threeLines(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'sepal length (cm)'," +
                    "             'group': 'target'," +
                    "             'color': 'sepal width (cm)'," +
                    "             'fill': 'target'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                   'geom': 'area'," +
                    "                   'stat': 'density'," +
                    "                   'position' : 'identity'," +
                    "                   'alpha': 0.7" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = Iris.df
            return plotSpec

        }
    }
}