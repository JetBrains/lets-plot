/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import org.jetbrains.letsPlot.commons.intern.random.RandomGaussian.Companion.normal
import demoAndTestShared.parsePlotSpec

/**
 * See 'Plotting distributions'
 * www.cookbook-r.com/Graphs/Plotting_distributions_(ggplot2)/
 */
open class Histogram {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            basicWithVLine(),
            withWeights(),
            densityMapping()
        )
    }


    companion object {

        private val DATA =
            data()  // make it stable between calls

        fun data(): Map<String, List<*>> {
            val count = 100
//            val count = 500000

            val xs = normal(count, 0.0, 5.0, 12)
            val weights = ArrayList<Double>()
            for (x in xs) {
                weights.add(if (x < 0.0) 2.0 else 0.5);
            }
            val map = HashMap<String, List<*>>()
            map["x"] = xs
            map["weight"] = weights
            return map
        }


        //===========================


        fun basic(): MutableMap<String, Any> {
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

        fun basicWithVLine(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'x'" +
                    "           }," +

                    "   'layers': [" +
                    "                  {'geom': 'histogram'}," +
                    "                  {'geom': { " +
                    "                              'name' : 'vline'," +
                    "                              'data': { 'vl': [2.0, 5.0] }" +
                    "                           }," +
                    "                          'mapping': {'xintercept': 'vl'}, " +
                    "                          'color': 'red'" +
                    "                  }" +
                    "              ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        fun withWeights(): MutableMap<String, Any> {
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

        fun densityMapping(): MutableMap<String, Any> {
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
