/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase
import jetbrains.datalore.plotDemo.model.util.DemoUtil

/**
 * See 'Plotting distributions'
 * www.cookbook-r.com/Graphs/Plotting_distributions_(ggplot2)/
 */
open class Histogram : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            basic(),
            withWeights(),
            densityMapping()
        )
    }


    companion object {

        private val DATA =
            data()  // make it stable between calls

        private fun data(): Map<String, List<*>> {
            val count = 100

            val xs = DemoUtil.gauss(count, 12, 0.0, 5.0)
            val weights = ArrayList<Double>()
            for (x in xs) {
                weights.add(if (x < 0.0) 2.0 else 0.5);
//                weights.add(2.0);
//                weights.add(abs(x))
            }
            val map = HashMap<String, List<*>>()
            map["x"] = xs
            map["weight"] = weights
            return map
        }


        //===========================


        fun basic(): Map<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'x'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'histogram'" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withWeights(): Map<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'x'," +
                    "             'weight': 'weight'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'histogram'" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun densityMapping(): Map<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'x'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'histogram'," +
                    "                  'mapping': {" +
                    "                            'y': '..density..'" +
                    "                          }," +
                    "                  'fill': 'orange'" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec1 = HashMap(parsePlotSpec(spec))
            plotSpec1["data"] = DATA
            return plotSpec1
        }

    }
}
