/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import demo.plot.common.data.Iris

class QQ {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            onlySampleValues(),
            grouping(),
            bistroBasic(),
            bistroOnlySampleValues(),
            bistroGrouping(),
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
                "   'data' : {'y': [-3, -1, 0, 1, 3, null, -4, -2, 0, 1, 2, 4]," +
                "             'g': ['A', 'A', 'A', 'A', 'A', 'A', null, 'B', 'B', 'B', 'B', 'B']" +
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

    private fun bistroBasic(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'ggtitle': {" +
                "                'text': 'Bistro basic demo'" +
                "              }," +
                "   'bistro': {" +
                "               'name': 'qqplot'," +
                "               'sample': 'sepal length (cm)'" +
                "             }" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }

    private fun bistroOnlySampleValues(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'ggtitle': {" +
                "                'text': 'Bistro only sample values'" +
                "              }," +
                "   'bistro': {" +
                "               'name': 'qqplot'," +
                "               'x': 'sepal width (cm)'," +
                "               'y': 'sepal length (cm)'" +
                "             }" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }

    private fun bistroGrouping(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'y': [-3, -1, 0, 1, 3, null, -4, -2, 0, 1, 2, 4]," +
                "             'g': ['A', 'A', 'A', 'A', 'A', 'A', null, 'B', 'B', 'B', 'B', 'B']" +
                "            }," +
                "   'ggtitle': {" +
                "                'text': 'Bistro grouping'" +
                "              }," +
                "   'bistro': {" +
                "               'name': 'qqplot'," +
                "               'sample': 'y'," +
                "               'group': 'g'," +
                "               'alpha': 0.8," +
                "               'color': 'black'," +
                "               'shape': 21" +
                "             }," +
                "   'scales': [" +
                "               {" +
                "                 'aesthetic': 'x'," +
                "                 'name': 'Normal distribution quantiles'" +
                "               }," +
                "               {" +
                "                 'aesthetic': 'color'," +
                "                 'type': 'qual'," +
                "                 'palette': 'Set1'," +
                "                 'scale_mapper_kind': 'color_brewer'" +
                "               }," +
                "               {" +
                "                 'aesthetic': 'fill'," +
                "                 'type': 'qual'," +
                "                 'palette': 'Set1'," +
                "                 'scale_mapper_kind': 'color_brewer'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }
}