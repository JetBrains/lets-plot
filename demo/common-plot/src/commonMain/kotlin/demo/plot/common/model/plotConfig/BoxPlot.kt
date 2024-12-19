/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demo.plot.common.model.util.DemoUtil.interlace
import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.commons.intern.random.RandomGaussian.Companion.normal

/**
 * See 'Plotting distributions'
 * www.cookbook-r.com/Graphs/Plotting_distributions_(ggplot2)/
 */
open class BoxPlot {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            withVarWidth(),
            withCondColored(),
            withOutlierOverride(),
            withGrouping(),
            withGroupingAndVarWidth(),
            withMiddlePoint(),
            oneBox(),
            oneBox(5.0)
        )
    }

    companion object {
        private val DATA = data()

        fun basic(): MutableMap<String, Any> {
            val spec = """
                {
                  'kind': 'plot',
                  'mapping': {
                    'x': 'cond',
                    'y': 'rating'
                  },
                  'ggtitle': {
                    'text': 'Basic demo'
                  },
                  'layers': [
                    {
                      'geom': 'boxplot'
                    },
                    {
                      'geom': 'point',
                      'stat': 'boxplot_outlier'
                    }
                  ]
                }
            """.trimIndent()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withVarWidth(): MutableMap<String, Any> {
            val spec = """
                {
                  'kind': 'plot',
                  'mapping': {
                    'x': 'cond',
                    'y': 'rating'
                  },
                  'ggtitle': {
                    'text': 'With varwidth'
                  },
                  'layers': [
                    {
                      'geom': 'boxplot',
                      'varwidth': true
                    },
                    {
                      'geom': 'point',
                      'stat': 'boxplot_outlier'
                    }
                  ]
                }
            """.trimIndent()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withCondColored(): MutableMap<String, Any> {
            val spec = """
                {
                  'kind': 'plot',
                  'mapping': {
                    'x': 'cond',
                    'y': 'rating',
                    'fill': 'cond'
                  },
                  'ggtitle': {
                    'text': 'With fill aes'
                  },
                  'layers': [
                    {
                      'geom': 'boxplot',
                      'whisker_width': 0.5
                    },
                    {
                      'geom': 'point',
                      'stat': 'boxplot_outlier'
                    }
                  ]
                }
            """.trimIndent()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withOutlierOverride(): MutableMap<String, Any> {
            val spec = """
                {
                  'kind': 'plot',
                  'mapping': {
                    'x': 'cond',
                    'y': 'rating'
                  },
                  'ggtitle': {
                    'text': 'With specified outlier options'
                  },
                  'layers': [
                    {
                      'geom': 'boxplot'
                    },
                    {
                      'geom': 'point',
                      'stat': 'boxplot_outlier',
                      'color': 'red',
                      'shape': 1,
                      'size': 15
                    }
                  ]
                }
            """.trimIndent()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withGrouping(): MutableMap<String, Any> {
            val spec = """
                {
                  'kind': 'plot',
                  'mapping': {
                    'x': 'cond',
                    'y': 'rating',
                    'color': 'cond'
                  },
                  'ggtitle': {
                    'text': 'With grouping'
                  },
                  'layers': [
                    {
                      'geom': 'boxplot'
                    },
                    {
                      'geom': 'point',
                      'stat': 'boxplot_outlier'
                    }
                  ]
                }
            """.trimIndent()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withGroupingAndVarWidth(): MutableMap<String, Any> {
            val spec = """
                {
                  'kind': 'plot',
                  'mapping': {
                    'x': 'cond',
                    'y': 'rating',
                    'color': 'cond'
                  },
                  'ggtitle': {
                    'text': 'With grouping and varwidth'
                  },
                  'layers': [
                    {
                      'geom': 'boxplot',
                      'varwidth': true
                    },
                    {
                      'geom': 'point',
                      'stat': 'boxplot_outlier'
                    }
                  ]
                }
            """.trimIndent()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withMiddlePoint(): MutableMap<String, Any> {
            // This one is not working.
            val spec = """
                {
                  'kind': 'plot',
                  'mapping': {
                    'x': 'cond',
                    'y': 'rating'
                  },
                  'ggtitle': {
                    'text': 'Point geometry'
                  },
                  'layers': [
                    {
                      'geom': 'point',
                      'stat': 'boxplot',
                      'mapping': {
                        'y': '..middle..'
                      },
                      'size': 7,
                      'color': 'red'
                    },
                    {
                      'geom': 'point',
                      'stat': 'boxplot_outlier'
                    }
                  ]
                }
            """.trimMargin()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun oneBox(x: Double? = null): MutableMap<String, Any> {
            // Undefined x-aesthetics.
            // see this issue: https://github.com/JetBrains/lets-plot/issues/325

            val layerSpec = if (x == null) {
                "{'geom': 'boxplot'}, {'geom': 'point', 'stat': 'boxplot_outlier'}"
            } else {
                "{'geom': 'boxplot', 'x': '$x'}, {'geom': 'point', 'stat': 'boxplot_outlier', 'x': '$x'}"
            }

            val spec = """
                {
                  'kind': 'plot',
                  'mapping': {
                    'y': 'rating'
                  },
                  'ggtitle': {
                    'text': 'One box, x = $x'
                  },
                  'layers': [$layerSpec]
                }
            """.trimIndent()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        private fun data(): Map<String, List<*>> {
            val count1 = 50
            val count2 = 100

            val ratingA = normal(count1, 0.0, 1.0, 12)
            val ratingB = normal(count2, 0.0, 1.0, 24)
            val rating = interlace(ratingA, ratingB)
            val cond = interlace(List(count1) { "a" }, List(count2) { "b" })

            val map = HashMap<String, List<*>>()
            map["cond"] = cond
            map["rating"] = rating

            return map
        }
    }
}
