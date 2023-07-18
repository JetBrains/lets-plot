/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec

open class Density {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            withWeights()
        )
    }

    companion object {

        private val DATA =
            Histogram.data()  // make it stable between calls

        //===========================


        fun basic(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'mapping': {" +
                    "             'x': 'x'" +
                    "           }," +

                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'density'" +
                    "               }" +
                    "           ]" +
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
                    "                  'geom': 'density'" +
                    "               }" +
                    "           ]" +
                    "}"

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }
    }
}
