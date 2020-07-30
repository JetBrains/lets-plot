/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase
import jetbrains.datalore.plotDemo.model.SharedPieces

open class ABLine : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            lineDefaultAlone(),
            lineDefault(),
            negativeSlope(),
            zeroSlope(),
            variableInterceptAndSlope()
        )
    }


    companion object {
        private fun lineDefaultAlone(): Map<String, Any> {
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

        private fun lineDefault(): Map<String, Any> {
            val abLine = "               {" +
                    "             'geom': 'abline'," +
                    "             'size': 3" +
                    "           }"

            return SharedPieces.samplePolyAndPointsPlotWith(abLine, emptyMap())
        }

        private fun negativeSlope(): Map<String, Any> {
            val abLine = "               {" +
                    "             'geom': 'abline'," +
                    "             'slope': '-2'," +
                    "             'size': 3" +
                    "           }"

            return SharedPieces.samplePolyAndPointsPlotWith(abLine, emptyMap())
        }

        private fun zeroSlope(): Map<String, Any> {
            val abLine = "               {" +
                    "             'geom': 'abline'," +
                    "             'intercept': '1'," +
                    "             'slope': '0'," +
                    "             'size': 3" +
                    "           }"

            return SharedPieces.samplePolyAndPointsPlotWith(abLine, emptyMap())
        }

        private fun variableInterceptAndSlope(): Map<String, Any> {
            val intercept = ArrayList<Double>()
            val slope = ArrayList<Double>()
            for (i in 0..9) {
                intercept.add(i * 0.1)
                slope.add(i * 0.2)
            }

            val abLine = "               {" +
                    "             'geom': 'abline'," +
                    "             'size': 2," +
                    "             'mapping': {" +
                    "                          'intercept': 'intercept'," +
                    "                          'slope': 'slope'," +
                    "                          'color': 'intercept'" +
                    "                        }" +
                    "           }"

            return SharedPieces.samplePolyAndPointsPlotWith(abLine, mapOf(
                    "intercept" to intercept,
                    "slope" to slope
            ))
        }
    }
}
