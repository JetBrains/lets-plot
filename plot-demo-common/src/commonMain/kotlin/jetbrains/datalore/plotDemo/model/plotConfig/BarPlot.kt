/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

open class BarPlot : PlotConfigDemoBase() {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            fancy()
        )
    }


    companion object {
        private const val OUR_DATA = "   {" +
                "      'time': ['Lunch','Lunch', 'Dinner', 'Dinner', 'Dinner']" +
                "   }"


        fun basic(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'data': " + OUR_DATA +
                    "           ," +
                    "   'mapping': {" +
                    "             'x': 'time'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'bar'" +
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
    }
}
