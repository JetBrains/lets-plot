/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demo.plot.common.model.SharedPieces
import demoAndTestShared.parsePlotSpec

open class ABLine {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            lineDefaultAlone(),
            lineDefault(),
            negativeSlope(),
            zeroSlope(),
            variableInterceptAndSlope()
        )
    }


    companion object {
        private fun lineDefaultAlone(): MutableMap<String, Any> {
            val spec = "    {" +
                    "   'kind': 'plot'," +
                    "   'layers': [" +
                    "           {" +
                    "             'geom': 'abline'," +
                    "             'size': 3" +
                    "           }" +
                    "         ]" +
                    "}"

            return HashMap(parsePlotSpec(spec))
        }

        private fun lineDefault(): MutableMap<String, Any> {
            val abLine = "               {" +
                    "             'geom': 'abline'," +
                    "             'size': 3" +
                    "           }"

            return SharedPieces.samplePolyAndPointsPlotWith(abLine, emptyMap())
        }

        private fun negativeSlope(): MutableMap<String, Any> {
            val abLine = "               {" +
                    "             'geom': 'abline'," +
                    "             'slope': '-2'," +
                    "             'size': 3" +
                    "           }"

            return SharedPieces.samplePolyAndPointsPlotWith(abLine, emptyMap())
        }

        private fun zeroSlope(): MutableMap<String, Any> {
            val abLine = "               {" +
                    "             'geom': 'abline'," +
                    "             'intercept': '1'," +
                    "             'slope': '0'," +
                    "             'size': 3" +
                    "           }"

            return SharedPieces.samplePolyAndPointsPlotWith(abLine, emptyMap())
        }

        private fun variableInterceptAndSlope(): MutableMap<String, Any> {
            val intercept = ArrayList<Any>()
            val slope = ArrayList<Any>()
            for (i in 0..9) {
                intercept.add(i * 0.1)
                slope.add(i * 0.2)
            }
//            intercept.add(0)
//            intercept.add(1)
//            slope.add(0.5)
//            slope.add(0.5)

            val abLine = "               {" +
                    "             'geom': 'abline'," +
                    "             'size': 2," +
                    "             'mapping': {" +
                    "                          'intercept': 'intercept'," +
                    "                          'slope': 'slope'," +
                    "                          'color': 'intercept'" +
                    "                        }" +
                    "           }"

            return SharedPieces.samplePolyAndPointsPlotWith(
                abLine,
                mutableMapOf(
                    "intercept" to intercept,
                    "slope" to slope
                )
            )
        }
    }
}
