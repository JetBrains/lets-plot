/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import org.jetbrains.letsPlot.commons.intern.random.RandomGaussian.Companion.normal
import demoAndTestShared.parsePlotSpec
import jetbrains.datalore.plotDemo.data.Iris
import jetbrains.datalore.plotDemo.model.util.DemoUtil

open class TooltipAnchor {
    fun plotSpecList(): List<MutableMap<String, Any>> {
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
            val ratingA = normal(count1, 12, 0.0, 1.0)
            val ratingB = normal(count2, 24, 0.0, 1.0)
            val rating = DemoUtil.interlace(ratingA, ratingB)
            val cond = DemoUtil.interlace(List(count1) { "a" }, List(count2) { "b" })
            val map = HashMap<String, List<*>>()
            map["cond"] = cond
            map["rating"] = rating
            return map
        }

        private fun withTooltipAnchor(anchor: String): MutableMap<String, Any> {
            val allPositionals = "^Y"
            val aesYMin = "^ymin"
            val aesYMax = "^ymax"
            val aesMiddle = "^middle"
            val aesLower = "^lower"
            val aesUpper = "^upper"
            val spec = """{
                    'kind': 'plot',
                    'ggtitle': {'text': '$anchor'},
                    'mapping': {
                        'x': 'cond',
                        'y': 'rating',
                        'fill': 'cond'
                    },
                    'layers':  [
                        {
                             'geom': 'boxplot',
                             'tooltips' : {
                                 'formats': [
                                    { 'field' : '$allPositionals', 'format' : '.0f' },
                                    { 'field' : '$aesMiddle', 'format' : '.2f' }
                                 ],
                                 'lines': [
                                    'min/max|$aesYMin/$aesYMax',
                                    'lower/upper|$aesLower/$aesUpper',
                                    '@|$aesMiddle'
                                 ],
                                'tooltip_anchor': '$anchor'
                             }
                        }
                    ]
            }"""

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = data()
            return plotSpec
        }

        private fun middle_right(): MutableMap<String, Any> = withTooltipAnchor("middle_right")

        private fun middle_center(): MutableMap<String, Any> = withTooltipAnchor("middle_center")

        private fun middle_left(): MutableMap<String, Any> = withTooltipAnchor("middle_left")

        private fun top_right(): MutableMap<String, Any> = withTooltipAnchor("top_right")

        private fun top_left(): MutableMap<String, Any> = withTooltipAnchor("top_left")

        private fun top_center(): MutableMap<String, Any> = withTooltipAnchor("top_center")

        private fun bottom_right(): MutableMap<String, Any> = withTooltipAnchor("bottom_right")

        private fun bottom_left(): MutableMap<String, Any> = withTooltipAnchor("bottom_left")

        private fun bottom_center(): MutableMap<String, Any> = withTooltipAnchor("bottom_center")

        private fun overCursor(): MutableMap<String, Any> {
            val spec = """{
                       'kind': 'plot',
                       'mapping': {
                                 'x': 'sepal length (cm)',
                                 'group': 'target',
                                 'color': 'sepal width (cm)',
                                 'fill': 'target'
                               },
                       'layers': [
                                   {
                                      'geom': 'area',
                                       'stat': 'density',
                                       'position' : 'identity',
                                       'alpha': 0.7,
                                       'tooltips' : { 'tooltip_anchor': 'top_right' }
                                   }
                               ]
                    }"""

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = Iris.df
            return plotSpec
        }
    }
}
