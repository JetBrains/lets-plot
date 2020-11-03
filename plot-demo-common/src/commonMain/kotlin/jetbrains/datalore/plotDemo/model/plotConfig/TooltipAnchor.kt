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
            top_center(),
            bottom_right(),
            bottom_left(),
            bottom_center(),
            middle_right(),
            middle_left(),
            middle_center(),
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
            val map = HashMap<String, List<*>>()
            map["cond"] = cond
            map["rating"] = rating
            return map
        }

        private fun withTooltipAnchor(anchor: String): Map<String, Any> {
            val spec = """{
                    'kind': 'plot',
                    'ggtitle': {'text': '$anchor'},
                    'theme':   {'tooltip_anchor': '$anchor'},
                    'mapping': {
                        'x': 'cond',
                        'y': 'rating',
                        'fill': 'cond'
                    },
                    'layers':  [
                        {
                             'geom': 'boxplot',
                             'tooltips' : {
                                 'tooltip_formats': [
                                    { 'field' : '${'$'}Y', 'format' : '.0f' },
                                    { 'field' : '${'$'}middle', 'format' : '.2f' }
                                 ],
                                 'tooltip_lines': [
                                    'min/max|${'$'}ymin/${'$'}ymax',
                                    'lower/upper|${'$'}lower/${'$'}upper',
                                    '@|${'$'}middle'
                                 ]
                             }
                        }
                    ]
            }"""

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        private fun middle_right(): Map<String, Any> = withTooltipAnchor("middle_right")

        private fun middle_center(): Map<String, Any> = withTooltipAnchor("middle_center")

        private fun middle_left(): Map<String, Any> = withTooltipAnchor("middle_left")

        private fun top_right(): Map<String, Any> = withTooltipAnchor("top_right")

        private fun top_left(): Map<String, Any> = withTooltipAnchor("top_left")

        private fun top_center(): Map<String, Any> = withTooltipAnchor("top_center")

        private fun bottom_right(): Map<String, Any> = withTooltipAnchor("bottom_right")

        private fun bottom_left(): Map<String, Any> = withTooltipAnchor("bottom_left")

        private fun bottom_center(): Map<String, Any> = withTooltipAnchor("bottom_center")

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