/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.Iris
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase
import jetbrains.datalore.plotDemo.model.util.DemoUtil

open class TooltipAnchor : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            top_right(),
            top_left(),
            bottom_right(),
            bottom_left(),
            overCursor()
        )
    }
    companion object {
        private fun data(): Map<String, List<*>> {
            val count1 = 20
            val count2 = 50
            val ratingA = DemoUtil.gauss(count1, 12, 0.0, 1.0)
            val ratingB = DemoUtil.gauss(count2, 24, 0.0, 1.0)
            val rating = DemoUtil.zip(ratingA, ratingB)
            val cond = DemoUtil.zip(DemoUtil.fill("a", count1), DemoUtil.fill("b", count2))
            val fill = DemoUtil.zip(ratingB, ratingA)
            val color = DemoUtil.zip(DemoUtil.fill("red", count1), DemoUtil.fill("blue", count2))
            val map = HashMap<String, List<*>>()
            map["cond"] = cond
            map["rating"] = rating
            map["fill"] = fill
            map["color"] = color
            return map
        }

        private const val OUR_DATA =
            "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'cond'," +
                    "             'y': 'rating'," +
                    "             'fill': 'fill'," +
                    "             'color': 'color'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'boxplot'" +
                    "               }" +
                    "           ]"

        fun top_right(): Map<String, Any> {
            val spec =
                "{" + OUR_DATA + "," +
                        "   'ggtitle': {'text': 'top_right'}," +
                        "   'theme':   {'tooltip_anchor': 'top_right'}" +
                        "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        fun top_left(): Map<String, Any> {
            val spec =
                "{" + OUR_DATA + "," +
                        "   'ggtitle': {'text': 'top_left'}," +
                        "   'theme':   {'tooltip_anchor': 'top_left'}" +
                        "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        fun bottom_right(): Map<String, Any> {
            val spec =
                "{" + OUR_DATA + "," +
                        "   'ggtitle': {'text': 'bottom_right'}," +
                        "   'theme':   {'tooltip_anchor': 'bottom_right'}" +
                        "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        fun bottom_left(): Map<String, Any> {
            val spec =
                "{" + OUR_DATA + "," +
                        "   'ggtitle': {'text': 'bottom_left'}," +
                        "   'theme':   {'tooltip_anchor': 'bottom_left'}" +
                        "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        private fun overCursor(): Map<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'sepal length (cm)'," +
                    "             'group': 'target'," +
                    "             'color': 'sepal width (cm)'," +
                    "             'fill': 'target'" +
                    "           }," +
                    "    'theme': {" +
                    "       'tooltip_anchor': 'top_right'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'area'," +
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