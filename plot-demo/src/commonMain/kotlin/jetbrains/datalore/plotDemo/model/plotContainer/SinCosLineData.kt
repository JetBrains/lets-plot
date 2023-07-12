/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotContainer

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import kotlin.math.cos
import kotlin.math.sin

internal class SinCosLineData(xMapper: (Int) -> Any, size: Int) {
    val dataFrame: DataFrame
    val varX = DataFrame.Variable("X")
    val varY = DataFrame.Variable("Y")
    val varCat = DataFrame.Variable("cat")

    init {
        val sinValues = ArrayList<Double>()
        val cosValues = ArrayList<Double>()
        val line = ArrayList<Double>()
        val x = ArrayList<Any>()

        for (i in 0 until size) {
            x.add(xMapper(i))
            val rawX = i.toDouble()
            sinValues.add(sin(rawX))
            cosValues.add(0.5 * cos(rawX))
            line.add(rawX / size)
        }

        // to 'long' form
        val xValues = ArrayList<Any>()
        val yValues = ArrayList<Double>()
        val catValues = ArrayList<String>()
        for (i in 0 until size) {
            catValues.add("sin")
            xValues.add(x[i])
            yValues.add(sinValues[i])

            catValues.add("cos")
            xValues.add(x[i])
            yValues.add(cosValues[i])

            catValues.add("line")
            xValues.add(x[i])
            yValues.add(line[i])
        }

        dataFrame = DataFrame.Builder()
            .put(varX, xValues)
            .putNumeric(varY, yValues)
            .put(varCat, catValues)
            .build()
    }

    fun distinctXValues(): Collection<Any> {
        return dataFrame.distinctValues(varX)
    }
}
