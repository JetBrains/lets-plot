/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot

import demoAndTestShared.parsePlotSpec
import kotlinx.cinterop.ExperimentalForeignApi
import savePlot

object BarPlotMagickCanvasDemo {
    @OptIn(ExperimentalForeignApi::class)
    fun main() {
        savePlot(basic(), "bar_plot_basic.bmp")
    }

    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            fancy(),
            fancyWithWidth(0.5),
            fancyWithWidth(5.0),
        )
    }


    private const val OUR_DATA = "   {" +
            "      'time': ['Lunch','Lunch', 'Dinner', 'Dinner', 'Dinner']" +
            "   }"


    fun basic(): MutableMap<String, Any> {
        val spec = "{" +
//                    "'scales': [{'aesthetic': 'y', 'trans': 'reverse'}]," +
                "   'kind': 'plot'," +
                "   'data': " + OUR_DATA +
                "           ," +
                "   'mapping': {" +
                "             'x': 'time'," +
                "             'color': 'time'," +
                "             'fill': 'time'" +
                "           }," +
                "   'layers': [" +
                "               {" +
                "                  'geom': 'bar'," +
                "                  'alpha': '0.5'" +
                "               }" +
                "           ]" +
                "}"

        return parsePlotSpec(spec)
    }

    fun fancy(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data': " + OUR_DATA +
                "           ," +
                "   'mapping': {" +
                "             'x': 'time'," +
                "             'y': '..count..'," +
                "             'fill': '..count..'" +
                "           }," +
                "   'layers': [" +
                "               {" +
                "                  'geom': 'bar'" +
                "               }" +
                "           ]" +

                "   ," +
                "   'scales': [" +
                "               {" +
                "                  'aesthetic': 'fill'," +
                "                  'discrete': true," +
                "                  'scale_mapper_kind': 'color_hue'" +
                "               }" +
                "           ]" +
                "}"

        return parsePlotSpec(spec)
    }

    fun fancyWithWidth(w: Double): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data': " + OUR_DATA +
                "           ," +
                "   'mapping': {" +
                "             'x': 'time'," +
                "             'y': '..count..'," +
                "             'fill': '..count..'," +
                "             'width': 'width_val'" +
                "           }," +
                "   'layers': [" +
                "               {" +
                "                  'geom': 'bar'," +
                "                  'width': $w" +
                "               }" +
                "           ]" +

                "   ," +
                "   'scales': [" +
                "               {" +
                "                  'aesthetic': 'fill'," +
                "                  'discrete': true," +
                "                  'scale_mapper_kind': 'color_hue'" +
                "               }" +
                "           ]," +
                "   'ggtitle': { 'text': 'width = $w' }" +
                "}"

        return parsePlotSpec(spec)
    }

}