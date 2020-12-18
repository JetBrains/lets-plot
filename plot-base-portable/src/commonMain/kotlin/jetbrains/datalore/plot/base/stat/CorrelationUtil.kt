/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.DataFrameUtil.isNumeric
import jetbrains.datalore.plot.base.stat.regression.allFinite
import kotlin.math.abs
import kotlin.Pair as Pair

object CorrelationUtil {

    fun correlation(
        lxs: List<Double?>, lys: List<Double?>,
        correlationFunction: (DoubleArray, DoubleArray) -> Double
    ): Double {
        val (xs, ys) = allFinite(lxs, lys)
        return correlationFunction(xs, ys)
    }

    private fun createComparator(vars: List<DataFrame.Variable>): Comparator<String> {
        val indexMap = vars.withIndex().map { it.value.label to it.index }.toMap()

        return Comparator { a: String, b: String ->
            val lhsWeight = indexMap[a] ?: error("Unknown variable label ${a}.")
            val rhsWeight = indexMap[b] ?: error("Unknown variable label ${b}.")
            return@Comparator lhsWeight - rhsWeight
        }
    }

    fun correlationMatrix(
        data: DataFrame,
        type: CorrelationStat.Type,
        fillDiagonal: Boolean,
        correlationFunction: (DoubleArray, DoubleArray) -> Double,
        threshold: Double = CorrelationStat.DEF_THRESHOLD
    ): DataFrame {
        val numerics = data.variables().filter { isNumeric(data, it.name) }
        val knownVars = mutableSetOf<String>()
        val corrData = mutableMapOf<Pair<String, String>, Double>()

        fun addCorrelation(varX: String, varY: String, v: Double) {
            if (abs(v) >= threshold) {
                knownVars.add(varX)
                knownVars.add(varY)
                corrData[varX to varY] = v
            }
        }

        for ((i, vx) in numerics.withIndex()) {
            val xs = data.getNumeric(vx)

            if (fillDiagonal) {    // values on main diagonal does not require calculations
                addCorrelation(vx.label, vx.label, 1.0)
            }

            for (j in 0 until i) {
                val vy = numerics[j]
                val ys = data.getNumeric(vy)
                val c = correlation(xs, ys, correlationFunction)

                if (type == CorrelationStat.Type.FULL || type == CorrelationStat.Type.LOWER) {
                    addCorrelation(vx.label, vy.label, c)
                }

                if (type == CorrelationStat.Type.FULL || type == CorrelationStat.Type.UPPER) {
                    addCorrelation(vy.label, vx.label, c)
                }
            }
        }

        val var1 = arrayListOf<String>()
        val var2 = arrayListOf<String>()
        val corr = arrayListOf<Double?>()

        // put all correlation matrix values (including nulls)
        // to result dataframe in proper order, to keep matrix shape.
        val sortedVars = knownVars.sortedWith(createComparator(numerics))

        for (x in sortedVars) {
            for (y in sortedVars) {
                var1.add(x)
                var2.add(y)
                corr.add(corrData[x to y])
            }
        }

        return DataFrame.Builder()
            .putDiscrete(Stats.X, var1)
            .putDiscrete(Stats.Y, var2)
            .putNumeric(Stats.CORR, corr)
            .build()
    }
}