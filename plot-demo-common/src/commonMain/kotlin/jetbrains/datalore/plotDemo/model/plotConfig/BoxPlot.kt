/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase
import jetbrains.datalore.plotDemo.model.util.DemoUtil.fill
import jetbrains.datalore.plotDemo.model.util.DemoUtil.gauss
import jetbrains.datalore.plotDemo.model.util.DemoUtil.zip

/**
 * See 'Plotting distributions'
 * www.cookbook-r.com/Graphs/Plotting_distributions_(ggplot2)/
 */
open class BoxPlot : PlotConfigDemoBase() {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            withVarWidth(),
            withCondColored(),
            withOutlierOverride(),
            withGrouping(),
            withGroupingAndVarWidth(),
            withMiddlePoint()
        )
    }


    companion object {
        private val DATA =
            data()  // make it stable between calls

        private fun data(): Map<String, List<*>> {
            val count1 = 50
            val count2 = 100

            val ratingA = gauss(count1, 12, 0.0, 1.0)
            val ratingB = gauss(count2, 24, 0.0, 1.0)
            val rating = zip(ratingA, ratingB)
            val cond = zip(fill("a", count1), fill("b", count2))
//            val group = ArrayList(fill("G1", count1))
//            group.addAll(fill("G2", count2))

            val map = HashMap<String, List<*>>()
            map["cond"] = cond
            map["rating"] = rating
//            map["group"] = group
            return map
        }


        //===========================


        fun basic(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'cond'," +
                    "             'y': 'rating'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'boxplot'" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withVarWidth(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'cond'," +
                    "             'y': 'rating'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'boxplot'," +
                    "                  'varwidth': true" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withCondColored(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'cond'," +
                    "             'y': 'rating'," +
                    "             'fill': 'cond'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'boxplot'" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withOutlierOverride(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'cond'," +
                    "             'y': 'rating'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'boxplot'," +
                    "                  'outlier_color': 'red'," +
                    "                  'outlier_shape': 1," +
                    "                  'outlier_size': 15" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withGrouping(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'cond'," +
                    "             'y': 'rating'," +
                    "             'color': 'cond'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'boxplot'" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withGroupingAndVarWidth(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'cond'," +
                    "             'y': 'rating'," +
                    "             'color': 'cond'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'boxplot'," +
                    "                  'varwidth': true" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withMiddlePoint(): MutableMap<String, Any> {
            val spec = """
                |   {
                |      'kind': 'plot',
                |      'mapping': {
                |                'x': 'cond',
                |                'y': 'rating'
                |              },
                |      'layers': [
                |                  {
                |                     'geom': 'point',
                |                     'stat': 'boxplot',
                |                     'mapping': {'y': '..middle..'},
                |                     'size': 7,
                |                     'color': 'red'
                |                  }
                |              ]
                |   }
                    """.trimMargin()

//                |                  {
//                |                     'geom': 'boxplot'
//                |                  },

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }
    }
}
