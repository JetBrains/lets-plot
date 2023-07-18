/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec

/**
 * see: www.cookbook-r.com/Graphs/Bar_and_line_graphs_(ggplot2)/
 */
open class BarAndLine {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            defaultBarDiscreteX(),
            barDiscreteXFill(),
            barDiscreteXFillMappedInGeom(),
            barDiscreteXFillAndBlackOutline(),
            barDiscreteXTitleAxisLabelsNarrowWidth()
        )
    }

    companion object {
        private const val OUR_DATA = "   {" +
                "      'time': ['Lunch', 'Dinner']," +
                "      'total_bill': [14.89, 17.23]" +
                "   }"


        //===========================

        fun defaultBarDiscreteX(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'data': " + OUR_DATA +
                    "           ," +
                    "   'mapping': {" +
                    "             'x': 'time'," +
                    "             'y': 'total_bill'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                 'geom': 'bar'," +
                    "                 'stat': 'identity'" +
                    "               }" +
                    "           ]" +
                    "}"

            return parsePlotSpec(spec)
        }

        fun barDiscreteXFill(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'data': " + OUR_DATA +
                    "           ," +
                    "   'mapping': {" +
                    "             'x': 'time'," +
                    "             'y': 'total_bill'," +
                    "             'fill': 'time'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                 'geom': 'bar'," +
                    "                 'stat': 'identity'" +
                    "               }" +
                    "           ]" +
                    "}"

            return parsePlotSpec(spec)
        }

        fun barDiscreteXFillMappedInGeom(): MutableMap<String, Any> {
            // Must be same result as in the method above
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'data': " + OUR_DATA +
                    "           ," +
                    "   'mapping': {" +
                    "             'x': 'time'," +
                    "             'y': 'total_bill'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                 'geom': 'bar'," +
                    "                 'stat': 'identity'," +
                    "                 'mapping': {" +
                    "                              'fill': 'time'" +
                    "                            }" +
                    "               }" +
                    "           ]" +
                    "}"

            return parsePlotSpec(spec)
        }

        fun barDiscreteXFillAndBlackOutline(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'data': " + OUR_DATA +
                    "           ," +
                    "   'mapping': {" +
                    "             'x': 'time'," +
                    "             'y': 'total_bill'," +
                    "             'fill': 'time'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                 'geom': 'bar'," +
                    "                 'stat': 'identity'," +
                    "                 'colour': 'black'" +
                    "               }" +
                    "           ]" +
                    "}"

            return parsePlotSpec(spec)
        }

        fun barDiscreteXTitleAxisLabelsNarrowWidth(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'data': " + OUR_DATA +
                    "           ," +
                    "   'mapping': {" +
                    "             'x': 'time'," +
                    "             'y': 'total_bill'," +
                    "             'fill': 'time'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                 'geom': 'bar'," +
                    "                 'stat': 'identity'," +
                    "                 'colour': 'black'," +
                    "                 'fill': '#DD8888'," +
                    "                 'width': 0.8" +
                    "               }" +
                    "           ]," +
                    "   'scales': [" +
                    "               {" +
                    "                  'aesthetic': 'x'," +
                    "                  'name': 'Time of day'" +
                    "               }," +
                    "               {" +
                    "                  'aesthetic': 'y'," +
                    "                  'name': 'Total bill'" +
                    "               }" +
                    "           ]," +
                    "   'ggtitle': {" +
                    "                 'text': 'Average bill for 2 people'" +
                    "               }" +
                    "}"

            return parsePlotSpec(spec)
        }
    }
}
